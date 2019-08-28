///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.LazyMultiIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class HamtBranchNode<K, V>
    implements ArrayHelper.Allocator<HamtNode<K, V>>,
               HamtNode<K, V>
{
    private static final HamtBranchNode[] EMPTY_NODES = new HamtBranchNode[0];

    static final int SHIFT = 5;
    static final int MASK = 0x1f;

    private final int bitmask;
    @Nonnull
    private final CollisionMap<K, V> value;
    @Nonnull
    private final HamtNode<K, V>[] children;
    private final int size;

    private HamtBranchNode(int bitmask,
                           @Nonnull CollisionMap<K, V> value,
                           @Nonnull HamtNode<K, V>[] children,
                           int size)
    {
        this.bitmask = bitmask;
        this.value = value;
        this.children = children;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    static <K, V> HamtNode<K, V> forLeafExpansion(@Nonnull CollisionMap<K, V> emptyMap,
                                                  int hashCode,
                                                  @Nonnull CollisionMap<K, V> value)
    {
        if (hashCode == 0) {
            return new HamtBranchNode<>(0, value, EMPTY_NODES, value.size());
        } else {
            final int index = hashCode & MASK;
            final int remainder = hashCode >>> SHIFT;
            final int bit = 1 << index;
            final HamtNode<K, V>[] children = new HamtNode[1];
            children[0] = forLeafExpansion(emptyMap, remainder, value);
            return new HamtBranchNode<>(bit, emptyMap, children, value.size());
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public Holder<V> find(int hashCode,
                          @Nonnull K hashKey)
    {
        if (hashCode == 0) {
            return value.findValue(hashKey);
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].find(remainder, hashKey);
        }
    }

    @Override
    public V getValueOr(int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        if (hashCode == 0) {
            return value.getValueOr(hashKey, defaultValue);
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].getValueOr(remainder, hashKey, defaultValue);
        }
    }

    @Override
    @Nonnull
    public HamtNode<K, V> assign(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nullable V value)
    {
        final HamtNode<K, V>[] children = this.children;
        final int bitmask = this.bitmask;
        final CollisionMap<K, V> thisValue = this.value;
        if (hashCode == 0) {
            final CollisionMap<K, V> newValue = thisValue.update(hashKey, value);
            if (thisValue == newValue) {
                return this;
            } else {
                return new HamtBranchNode<>(bitmask, newValue, children, size - thisValue.size() + newValue.size());
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final HamtNode<K, V> newChild = new HamtLeafNode<>(remainder, emptyMap.update(hashKey, value));
            final HamtNode<K, V>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new HamtBranchNode<>(bitmask | bit, thisValue, newChildren, size + 1);
        } else {
            final HamtNode<K, V> child = children[childIndex];
            final HamtNode<K, V> newChild = child.assign(emptyMap, remainder, hashKey, value);
            if (newChild == child) {
                return this;
            } else {
                final HamtNode<K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new HamtBranchNode<>(bitmask, thisValue, newChildren, size - child.size() + newChild.size());
            }
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> update(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nonnull Func1<Holder<V>, V> generator)
    {
        final HamtNode<K, V>[] children = this.children;
        final int bitmask = this.bitmask;
        final CollisionMap<K, V> thisValue = this.value;
        if (hashCode == 0) {
            final CollisionMap<K, V> newValue = thisValue.update(hashKey, generator);
            if (thisValue == newValue) {
                return this;
            } else {
                return new HamtBranchNode<>(bitmask, newValue, children, size - thisValue.size() + newValue.size());
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final HamtNode<K, V> newChild = new HamtLeafNode<>(remainder, emptyMap.update(hashKey, generator));
            final HamtNode<K, V>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new HamtBranchNode<>(bitmask | bit, thisValue, newChildren, size + 1);
        } else {
            final HamtNode<K, V> child = children[childIndex];
            final HamtNode<K, V> newChild = child.update(emptyMap, remainder, hashKey, generator);
            if (newChild == child) {
                return this;
            } else {
                final HamtNode<K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new HamtBranchNode<>(bitmask, thisValue, newChildren, size - child.size() + newChild.size());
            }
        }
    }

    @Override
    @Nonnull
    public HamtNode<K, V> delete(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey)
    {
        final int bitmask = this.bitmask;
        final HamtNode<K, V>[] children = this.children;
        final CollisionMap<K, V> value = this.value;
        if (hashCode == 0) {
            final CollisionMap<K, V> newValue = value.delete(hashKey);
            final int newSize = this.size - value.size() + newValue.size();
            if (newValue == value) {
                return this;
            } else if (newValue.size() == 0) {
                if (bitmask == 0) {
                    return HamtEmptyNode.of();
                } else {
                    return createForDelete(bitmask, newValue, children, newSize);
                }
            } else {
                return new HamtBranchNode<>(bitmask, newValue, children, newSize);
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final HamtNode<K, V> child = children[childIndex];
            final HamtNode<K, V> newChild = child.delete(emptyMap, remainder, hashKey);
            final int newSize = size - child.size() + newChild.size();
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty()) {
                if (children.length == 1) {
                    if (value.size() == 0) {
                        return HamtEmptyNode.of();
                    } else {
                        return new HamtLeafNode<>(0, value);
                    }
                } else {
                    final HamtNode<K, V>[] newChildren = ArrayHelper.delete(this, children, childIndex);
                    return createForDelete(bitmask & ~bit, value, newChildren, newSize);
                }
            } else {
                final HamtNode<K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return createForDelete(bitmask, value, newChildren, newSize);
            }
        }
    }

    private HamtNode<K, V> createForDelete(int bitmask,
                                           CollisionMap<K, V> value,
                                           @Nonnull HamtNode<K, V>[] children,
                                           int newSize)
    {
        if (value.size() == 0 && children.length == 1) {
            final HamtNode<K, V> child = children[0];
            if (child instanceof HamtLeafNode) {
                final HamtLeafNode<K, V> leaf = (HamtLeafNode<K, V>)child;
                assert newSize == leaf.size();
                return leaf.liftNode(Integer.numberOfTrailingZeros(bitmask));
            }
            if (child instanceof HamtBranchNode) {
                final HamtBranchNode<K, V> branch = (HamtBranchNode<K, V>)child;
                if (branch.value.size() > 0 && branch.children.length == 0) {
                    assert newSize == branch.value.size();
                    return new HamtLeafNode<>(Integer.numberOfTrailingZeros(bitmask), branch.value);
                }
            }
        }
        return new HamtBranchNode<>(bitmask, value, children, newSize);
    }

    @Override
    public boolean isEmpty()
    {
        return bitmask == 0 && value.size() == 0;
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public HamtNode<K, V>[] allocate(int size)
    {
        return new HamtNode[size];
    }

    @Override
    @Nonnull
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return LazyMultiIterator.iterator(indexedForIterator());
    }

    @Override
    public String toString()
    {
        return "(" + value + ",0x" + Integer.toHexString(bitmask) + "," + children.length + ")";
    }

    private int computeSize()
    {
        int answer = value.size();
        for (HamtNode<K, V> child : children) {
            answer += child.size();
        }
        return answer;
    }

    @Override
    public void checkInvariants()
    {
        if (size != computeSize()) {
            throw new IllegalStateException(String.format("incorrect size: expected=%d actual=%d", computeSize(), size));
        }
        if (value.size() == 0 && children.length == 1) {
            if (children[0] instanceof HamtLeafNode) {
                // we should have replaced ourselves with a leaf
                throw new IllegalStateException();
            }
        }
        for (HamtNode<K, V> child : children) {
            child.checkInvariants();
        }
    }

    private Indexed<SplitableIterable<JImmutableMap.Entry<K, V>>> indexedForIterator()
    {
        return new Indexed<SplitableIterable<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public SplitableIterable<JImmutableMap.Entry<K, V>> get(int index)
            {
                if (index == 0) {
                    return value;
                } else {
                    return children[index - 1];
                }
            }

            @Override
            public int size()
            {
                return children.length + 1;
            }
        };
    }
}
