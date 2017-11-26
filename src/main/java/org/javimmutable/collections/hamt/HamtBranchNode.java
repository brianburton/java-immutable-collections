///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.LazyMultiIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class HamtBranchNode<T, K, V>
    implements ArrayHelper.Allocator<HamtNode<T, K, V>>,
               HamtNode<T, K, V>
{
    private static final HamtBranchNode[] EMPTY_NODES = new HamtBranchNode[0];

    static final int SHIFT = 5;
    static final int MASK = 0x1f;

    private final int bitmask;
    @Nullable
    private final T value;
    @Nonnull
    private final HamtNode<T, K, V>[] children;

    private HamtBranchNode(int bitmask,
                           @Nullable T value,
                           @Nonnull HamtNode<T, K, V>[] children)
    {
        this.bitmask = bitmask;
        this.value = value;
        this.children = children;
    }

    @SuppressWarnings("unchecked")
    static <T, K, V> HamtNode<T, K, V> forLeafExpansion(int hashCode,
                                                        @Nonnull T value)
    {
        if (hashCode == 0) {
            return new HamtBranchNode<>(0, value, EMPTY_NODES);
        } else {
            final int index = hashCode & MASK;
            final int remainder = hashCode >>> SHIFT;
            final int bit = 1 << index;
            final HamtNode<T, K, V>[] children = new HamtNode[1];
            children[0] = forLeafExpansion(remainder, value);
            return new HamtBranchNode<>(bit, null, children);
        }
    }

    @Override
    public Holder<V> find(@Nonnull Transforms<T, K, V> transforms,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        if (hashCode == 0) {
            if (value != null) {
                return transforms.findValue(value, hashKey);
            } else {
                return Holders.of();
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].find(transforms, remainder, hashKey);
        }
    }

    @Override
    public V getValueOr(@Nonnull Transforms<T, K, V> transforms,
                        int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        if (hashCode == 0) {
            if (value != null) {
                return transforms.getValueOr(value, hashKey, defaultValue);
            } else {
                return defaultValue;
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].getValueOr(transforms, remainder, hashKey, defaultValue);
        }
    }

    @Override
    @Nonnull
    public HamtNode<T, K, V> assign(@Nonnull Transforms<T, K, V> transforms,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nullable V value,
                                    @Nonnull MutableDelta sizeDelta)
    {
        final HamtNode<T, K, V>[] children = this.children;
        final int bitmask = this.bitmask;
        final T thisValue = this.value;
        if (hashCode == 0) {
            final T newValue = transforms.update(thisValue, hashKey, value, sizeDelta);
            if (thisValue == newValue) {
                return this;
            } else {
                return new HamtBranchNode<>(bitmask, newValue, children);
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final HamtNode<T, K, V> newChild = new HamtLeafNode<>(remainder, transforms.update(null, hashKey, value, sizeDelta));
            final HamtNode<T, K, V>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new HamtBranchNode<>(bitmask | bit, thisValue, newChildren);
        } else {
            final HamtNode<T, K, V> child = children[childIndex];
            final HamtNode<T, K, V> newChild = child.assign(transforms, remainder, hashKey, value, sizeDelta);
            if (newChild == child) {
                return this;
            } else {
                final HamtNode<T, K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new HamtBranchNode<>(bitmask, thisValue, newChildren);
            }
        }
    }

    @Override
    @Nonnull
    public HamtNode<T, K, V> delete(@Nonnull Transforms<T, K, V> transforms,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nonnull MutableDelta sizeDelta)
    {
        final int bitmask = this.bitmask;
        final HamtNode<T, K, V>[] children = this.children;
        final T value = this.value;
        if (hashCode == 0) {
            if (value != null) {
                final T newValue = transforms.delete(value, hashKey, sizeDelta);
                if (newValue == value) {
                    return this;
                } else if (newValue == null) {
                    if (bitmask == 0) {
                        return HamtEmptyNode.of();
                    } else {
                        return createForDelete(bitmask, null, children);
                    }
                } else {
                    return new HamtBranchNode<>(bitmask, newValue, children);
                }
            } else {
                return this;
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final HamtNode<T, K, V> child = children[childIndex];
            final HamtNode<T, K, V> newChild = child.delete(transforms, remainder, hashKey, sizeDelta);
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty()) {
                if (children.length == 1) {
                    if (value == null) {
                        return HamtEmptyNode.of();
                    } else {
                        return new HamtLeafNode<>(hashCode, value);
                    }
                } else {
                    final HamtNode<T, K, V>[] newChildren = ArrayHelper.delete(this, children, childIndex);
                    return createForDelete(bitmask & ~bit, value, newChildren);
                }
            } else {
                final HamtNode<T, K, V>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return createForDelete(bitmask, value, newChildren);
            }
        }
    }

    private HamtNode<T, K, V> createForDelete(int bitmask,
                                              T value,
                                              @Nonnull HamtNode<T, K, V>[] children)
    {
        if ((value == null) && (children.length == 1)) {
            final HamtNode<T, K, V> child = children[0];
            if (child instanceof HamtLeafNode) {
                final HamtLeafNode<T, K, V> leaf = (HamtLeafNode<T, K, V>)child;
                return leaf.liftNode(Integer.numberOfTrailingZeros(bitmask));
            }
            if (child instanceof HamtBranchNode) {
                final HamtBranchNode<T, K, V> branch = (HamtBranchNode<T, K, V>)child;
                if (branch.value != null && branch.children.length == 0) {
                    return new HamtLeafNode<>(Integer.numberOfTrailingZeros(bitmask), branch.value);
                }
            }
        }
        return new HamtBranchNode<>(bitmask, value, children);
    }

    @Override
    public boolean isEmpty()
    {
        return bitmask == 0 && value == null;
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public HamtNode<T, K, V>[] allocate(int size)
    {
        return new HamtNode[size];
    }

    @Override
    @Nonnull
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(Transforms<T, K, V> transforms)
    {
        return LazyMultiIterator.transformed(indexedForIterator(), node -> () -> iteratorHelper(node.iterator(), transforms));
    }

    @Nonnull
    private SplitableIterator<JImmutableMap.Entry<K, V>> iteratorHelper(SplitableIterator<T> value,
                                                                        Transforms<T, K, V> transforms)
    {
        return LazyMultiIterator.transformed(value, t -> () -> transforms.iterator(t));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return LazyMultiIterator.iterator(indexedForIterator());
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<K, V>> cursor(Transforms<T, K, V> transforms)
    {
        return LazyMultiCursor.transformed(indexedForCursor(), node -> () -> cursorHelper(node.cursor(), transforms));
    }

    @Nonnull
    private Cursor<JImmutableMap.Entry<K, V>> cursorHelper(Cursor<T> value,
                                                           Transforms<T, K, V> transforms)
    {
        return LazyMultiCursor.transformed(value, t -> () -> transforms.cursor(t));
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return LazyMultiCursor.cursor(indexedForCursor());
    }

    @Override
    public String toString()
    {
        return "(" + value + ",0x" + Integer.toHexString(bitmask) + "," + children.length + ")";
    }

    @Override
    public void checkInvariants()
    {
        if ((value == null) && (children.length == 1)) {
            if (children[0] instanceof HamtLeafNode) {
                // we should have replaced ourselves with a leaf
                throw new IllegalStateException();
            }
        }
    }

    private Indexed<SplitableIterable<T>> indexedForIterator()
    {
        return new Indexed<SplitableIterable<T>>()
        {
            @Override
            public SplitableIterable<T> get(int index)
            {
                if (index == 0) {
                    if (value != null) {
                        return () -> SingleValueIterator.of(value);
                    } else {
                        return () -> EmptyIterator.of();
                    }
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

    private Indexed<Cursorable<T>> indexedForCursor()
    {
        return new Indexed<Cursorable<T>>()
        {
            @Override
            public Cursorable<T> get(int index)
            {
                if (index == 0) {
                    if (value != null) {
                        return () -> SingleValueCursor.of(value);
                    } else {
                        return () -> StandardCursor.of();
                    }
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
