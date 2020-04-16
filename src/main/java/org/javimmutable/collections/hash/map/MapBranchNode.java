///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.common.ToStringHelper;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Objects;

@Immutable
public class MapBranchNode<K, V>
    implements ArrayHelper.Allocator<MapNode<K, V>>,
               MapNode<K, V>
{
    private static final MapBranchNode[] EMPTY_NODES = new MapBranchNode[0];

    static final int SHIFT = 6;
    static final int MASK = 0x3f;

    private final long bitmask;
    @Nonnull
    private final CollisionMap.Node value;
    @Nonnull
    private final MapNode<K, V>[] children;
    private final int size;

    MapBranchNode(long bitmask,
                  @Nonnull CollisionMap.Node value,
                  @Nonnull MapNode<K, V>[] children,
                  int size)
    {
        this.bitmask = bitmask;
        this.value = value;
        this.children = children;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    static <K, V> MapNode<K, V> forLeafExpansion(@Nonnull CollisionMap<K, V> collisionMap,
                                                 int hashCode,
                                                 @Nonnull K key,
                                                 @Nullable V value)
    {
        if (hashCode == 0) {
            return new MapBranchNode<K, V>(0, collisionMap.single(key, value), EMPTY_NODES, 1);
        } else {
            final int index = hashCode & MASK;
            final int remainder = hashCode >>> SHIFT;
            final long bit = 1L << index;
            final MapNode<K, V>[] children = new MapNode[1];
            children[0] = new MapSingleKeyLeafNode<>(remainder, key, value);
            return new MapBranchNode<>(bit, collisionMap.empty(), children, 1);
        }
    }

    @SuppressWarnings("unchecked")
    static <K, V> MapNode<K, V> forLeafExpansion(@Nonnull CollisionMap<K, V> collisionMap,
                                                 int hashCode,
                                                 @Nonnull CollisionMap.Node value)
    {
        if (hashCode == 0) {
            return new MapBranchNode<K, V>(0, value, EMPTY_NODES, collisionMap.size(value));
        } else {
            final int index = hashCode & MASK;
            final int remainder = hashCode >>> SHIFT;
            final long bit = 1L << index;
            final MapNode<K, V>[] children = new MapNode[1];
            children[0] = MapMultiKeyLeafNode.createLeaf(collisionMap, remainder, value);
            return new MapBranchNode<>(bit, collisionMap.empty(), children, collisionMap.size(value));
        }
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return size;
    }

    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        if (hashCode == 0) {
            return collisionMap.findValue(value, hashKey);
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final long bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].find(collisionMap, remainder, hashKey);
        }
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                        int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        if (hashCode == 0) {
            return collisionMap.getValueOr(value, hashKey, defaultValue);
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final long bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].getValueOr(collisionMap, remainder, hashKey, defaultValue);
        }
    }

    @Override
    @Nonnull
    public MapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey,
                                @Nullable V value)
    {
        final MapNode<K, V>[] children = this.children;
        final long bitmask = this.bitmask;
        final CollisionMap.Node thisValue = this.value;
        if (hashCode == 0) {
            final CollisionMap.Node newValue = collisionMap.update(thisValue, hashKey, value);
            if (thisValue == newValue) {
                return this;
            } else {
                return new MapBranchNode<>(bitmask, newValue, children, size - collisionMap.size(thisValue) + collisionMap.size(newValue));
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final MapNode<K, V> newChild = new MapSingleKeyLeafNode<>(remainder, hashKey, value);
            final MapNode<K, V>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new MapBranchNode<>(bitmask | bit, thisValue, newChildren, size + 1);
        } else {
            final MapNode<K, V> child = children[childIndex];
            final MapNode<K, V> newChild = child.assign(collisionMap, remainder, hashKey, value);
            if (newChild == child) {
                return this;
            } else {
                final MapNode<K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new MapBranchNode<>(bitmask, thisValue, newChildren, size - child.size(collisionMap) + newChild.size(collisionMap));
            }
        }
    }

    @Nonnull
    @Override
    public MapNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey,
                                @Nonnull Func1<Holder<V>, V> generator)
    {
        final MapNode<K, V>[] children = this.children;
        final long bitmask = this.bitmask;
        final CollisionMap.Node thisValue = this.value;
        if (hashCode == 0) {
            final CollisionMap.Node newValue = collisionMap.update(thisValue, hashKey, generator);
            if (thisValue == newValue) {
                return this;
            } else {
                return new MapBranchNode<>(bitmask, newValue, children, size - collisionMap.size(thisValue) + collisionMap.size(newValue));
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final MapNode<K, V> newChild = new MapSingleKeyLeafNode<>(remainder, hashKey, generator.apply(Holders.of()));
            final MapNode<K, V>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new MapBranchNode<>(bitmask | bit, thisValue, newChildren, size + 1);
        } else {
            final MapNode<K, V> child = children[childIndex];
            final MapNode<K, V> newChild = child.update(collisionMap, remainder, hashKey, generator);
            if (newChild == child) {
                return this;
            } else {
                final MapNode<K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new MapBranchNode<>(bitmask, thisValue, newChildren, size - child.size(collisionMap) + newChild.size(collisionMap));
            }
        }
    }

    @Override
    @Nonnull
    public MapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey)
    {
        final long bitmask = this.bitmask;
        final MapNode<K, V>[] children = this.children;
        final CollisionMap.Node value = this.value;
        if (hashCode == 0) {
            final CollisionMap.Node newValue = collisionMap.delete(value, hashKey);
            final int newSize = this.size - collisionMap.size(value) + collisionMap.size(newValue);
            if (newValue == value) {
                return this;
            } else if (collisionMap.size(newValue) == 0) {
                if (bitmask == 0) {
                    return MapEmptyNode.of();
                } else {
                    return createForDelete(collisionMap, bitmask, newValue, children, newSize);
                }
            } else {
                return new MapBranchNode<>(bitmask, newValue, children, newSize);
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final MapNode<K, V> child = children[childIndex];
            final MapNode<K, V> newChild = child.delete(collisionMap, remainder, hashKey);
            final int newSize = size - child.size(collisionMap) + newChild.size(collisionMap);
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty(collisionMap)) {
                if (children.length == 1) {
                    if (collisionMap.size(value) == 0) {
                        return MapEmptyNode.of();
                    } else {
                        return MapMultiKeyLeafNode.createLeaf(collisionMap, 0, value);
                    }
                } else {
                    final MapNode<K, V>[] newChildren = ArrayHelper.delete(this, children, childIndex);
                    return createForDelete(collisionMap, bitmask & ~bit, value, newChildren, newSize);
                }
            } else {
                final MapNode<K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return createForDelete(collisionMap, bitmask, value, newChildren, newSize);
            }
        }
    }

    private MapNode<K, V> createForDelete(@Nonnull CollisionMap<K, V> collisionMap,
                                          long bitmask,
                                          CollisionMap.Node value,
                                          @Nonnull MapNode<K, V>[] children,
                                          int newSize)
    {
        if (collisionMap.size(value) == 0 && children.length == 1) {
            final MapNode<K, V> child = children[0];
            if (child.isLeaf()) {
                assert newSize == child.size(collisionMap);
                return child.liftNode(Long.numberOfTrailingZeros(bitmask));
            }
            if (child instanceof MapBranchNode) {
                final MapBranchNode<K, V> branch = (MapBranchNode<K, V>)child;
                if (collisionMap.size(branch.value) > 0 && branch.children.length == 0) {
                    assert newSize == collisionMap.size(branch.value);
                    return MapMultiKeyLeafNode.createLeaf(collisionMap, Long.numberOfTrailingZeros(bitmask), branch.value);
                }
            }
        }
        return new MapBranchNode<>(bitmask, value, children, newSize);
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return bitmask == 0 && collisionMap.size(value) == 0;
    }

    @Nonnull
    @Override
    public MapNode<K, V> liftNode(int index)
    {
        throw new UnsupportedOperationException();
    }

    private static int realIndex(long bitmask,
                                 long bit)
    {
        return Long.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public MapNode<K, V>[] allocate(int size)
    {
        return new MapNode[size];
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nonnull CollisionMap<K, V> collisionMap,
                                                                             @Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        assert offset >= 0 && offset <= limit && limit <= size;
        return GenericIterator.indexedState(parent, indexedForIterator(collisionMap), offset, limit);
    }

    @Override
    public void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull Proc2<K, V> proc)
    {
        collisionMap.forEach(value, proc);
        for (MapNode<K, V> child : children) {
            child.forEach(collisionMap, proc);
        }
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        collisionMap.forEachThrows(value, proc);
        for (MapNode<K, V> child : children) {
            child.forEachThrows(collisionMap, proc);
        }
    }

    @Override
    public <R> R reduce(@Nonnull CollisionMap<K, V> collisionMap,
                        R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        sum = collisionMap.reduce(value, sum, proc);
        for (MapNode<K, V> child : children) {
            sum = child.reduce(collisionMap, sum, proc);
        }
        return sum;
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                   R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        sum = collisionMap.reduceThrows(value, sum, proc);
        for (MapNode<K, V> child : children) {
            sum = child.reduceThrows(collisionMap, sum, proc);
        }
        return sum;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapBranchNode<?, ?> that = (MapBranchNode<?, ?>)o;
        return bitmask == that.bitmask &&
               size == that.size &&
               value.equals(that.value) &&
               Arrays.equals(children, that.children);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(bitmask, value, size);
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }

    @Override
    public String toString()
    {
        return "(" + size + ",0x" + Long.toHexString(bitmask) + "," + children.length + "," + value + "," + ToStringHelper.arrayToString(children) + ")";
    }

    private int computeSize(@Nonnull CollisionMap<K, V> collisionMap)
    {
        int answer = collisionMap.size(value);
        for (MapNode<K, V> child : children) {
            answer += child.size(collisionMap);
        }
        return answer;
    }

    @Override
    public void checkInvariants(@Nonnull CollisionMap<K, V> collisionMap)
    {
        if (size != computeSize(collisionMap)) {
            throw new IllegalStateException(String.format("incorrect size: expected=%d actual=%d", computeSize(collisionMap), size));
        }
        if (collisionMap.size(value) == 0 && children.length == 1) {
            if (children[0] instanceof MapMultiKeyLeafNode || children[0] instanceof MapSingleKeyLeafNode) {
                // we should have replaced ourselves with a leaf
                throw new IllegalStateException(String.format("expected leaf but was %s", children[0].getClass().getName()));
            }
        }
        for (MapNode<K, V> child : children) {
            child.checkInvariants(collisionMap);
        }
    }

    @Nonnull
    private Indexed<GenericIterator.Iterable<JImmutableMap.Entry<K, V>>> indexedForIterator(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return new Indexed<GenericIterator.Iterable<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public GenericIterator.Iterable<JImmutableMap.Entry<K, V>> get(int index)
            {
                if (index == 0) {
                    return collisionMap.genericIterable(value);
                } else {
                    return children[index - 1].genericIterable(collisionMap);
                }
            }

            @Override
            public int size()
            {
                return 1 + children.length;
            }
        };
    }
}
