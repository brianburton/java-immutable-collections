///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.StandardCursor;

public class MultiBranchTrieNode<T>
        extends TrieNode<T>
{
    // used by SignedOrderCursorSource to determine which index to use next
    private static final IndexList SIGNED_INDEX_LIST = new IndexList(2, new IndexList(3, new IndexList(0, new IndexList(1, null))));

    private final int shift;
    private final int bitmask;
    private final TrieNode<T>[] entries;

    private MultiBranchTrieNode(int shift,
                                int bitmask,
                                TrieNode<T>[] entries)
    {
        assert shift >= 0;
        this.shift = shift;
        this.bitmask = bitmask;
        this.entries = entries;
    }

    static <T> MultiBranchTrieNode<T> forTesting(int shift)
    {
        TrieNode<T>[] entries = allocate(0);
        return new MultiBranchTrieNode<T>(shift, 0, entries);
    }

    static <T> MultiBranchTrieNode<T> forIndex(int shift,
                                               int index,
                                               TrieNode<T> child)
    {
        int branchIndex = ((index >>> shift) & 0x1f);
        return forBranchIndex(shift, branchIndex, child);
    }

    static <T> MultiBranchTrieNode<T> forBranchIndex(int shift,
                                                     int branchIndex,
                                                     TrieNode<T> child)
    {
        assert branchIndex >= 0 && branchIndex < 32;
        TrieNode<T>[] entries = allocate(1);
        entries[0] = child;
        return new MultiBranchTrieNode<T>(shift, 1 << branchIndex, entries);
    }

    static <T> MultiBranchTrieNode<T> forEntries(int shift,
                                                 TrieNode<T>[] entries)
    {
        final int length = entries.length;
        final int bitmask = (length == 32) ? -1 : ((1 << length) - 1);
        return new MultiBranchTrieNode<T>(shift, bitmask, entries.clone());
    }

    static <T> MultiBranchTrieNode<T> forSource(int index,
                                                int size,
                                                Indexed<T> source,
                                                int offset)
    {
        final TrieNode<T>[] entries = allocate(size);
        for (int i = 0; i < size; ++i) {
            entries[i] = LeafTrieNode.of(index++, source.get(offset++));
        }
        final int bitmask = (size == 32) ? -1 : ((1 << size) - 1);
        return new MultiBranchTrieNode<T>(0, bitmask, entries);
    }

    static <T> MultiBranchTrieNode<T> fullWithout(int shift,
                                                  TrieNode<T>[] entries,
                                                  int withoutIndex)
    {
        assert entries.length == 32;
        final TrieNode<T>[] newEntries = allocate(31);
        System.arraycopy(entries, 0, newEntries, 0, withoutIndex);
        System.arraycopy(entries, withoutIndex + 1, newEntries, withoutIndex, 31 - withoutIndex);
        final int newMask = ~(1 << withoutIndex);
        return new MultiBranchTrieNode<T>(shift, newMask, newEntries);
    }

    @Override
    public boolean isEmpty()
    {
        return entries.length == 0;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].getValueOr(shift - 5, index, defaultValue);
        }
    }

    @Override
    public <K, V> V getValueOr(int shift,
                               int index,
                               K key,
                               Transforms<T, K, V> transforms,
                               V defaultValue)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].getValueOr(shift - 5, index, key, transforms, defaultValue);
        }
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].find(shift - 5, index);
        }
    }

    @Override
    public <K, V> Holder<V> find(int shift,
                                 int index,
                                 K key,
                                 Transforms<T, K, V> transforms)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].find(shift - 5, index, key, transforms);
        }
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final int childIndex = realIndex(bitmask, bit);
        final TrieNode<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            final int oldLength = entries.length;
            final TrieNode<T>[] newEntries = allocate(oldLength + 1);
            if (bitmask != 0) {
                System.arraycopy(entries, 0, newEntries, 0, childIndex);
                System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
            }
            newEntries[childIndex] = LeafTrieNode.of(index, value);
            sizeDelta.add(1);
            if (newEntries.length == 32) {
                return new FullBranchTrieNode<T>(shift, newEntries);
            } else {
                return new MultiBranchTrieNode<T>(shift, bitmask | bit, newEntries);
            }
        } else {
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.assign(shift - 5, index, value, sizeDelta);
            if (newChild == child) {
                return this;
            } else {
                assert newChild.isLeaf() || (newChild.getShift() == shift - 5);
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public <K, V> TrieNode<T> assign(int shift,
                                     int index,
                                     K key,
                                     V value,
                                     Transforms<T, K, V> transforms,
                                     MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final int childIndex = realIndex(bitmask, bit);
        final TrieNode<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            final int oldLength = entries.length;
            final TrieNode<T>[] newEntries = allocate(oldLength + 1);
            if (bitmask != 0) {
                System.arraycopy(entries, 0, newEntries, 0, childIndex);
                System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
            }
            newEntries[childIndex] = LeafTrieNode.of(index, transforms.update(Holders.<T>of(), key, value, sizeDelta));
            if (newEntries.length == 32) {
                return new FullBranchTrieNode<T>(shift, newEntries);
            } else {
                return new MultiBranchTrieNode<T>(shift, bitmask | bit, newEntries);
            }
        } else {
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.assign(shift - 5, index, key, value, transforms, sizeDelta);
            if (newChild == child) {
                return this;
            } else {
                assert newChild.isLeaf() || (newChild.getShift() == shift - 5);
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final TrieNode<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.delete(shift - 5, index, sizeDelta);
            if (newChild.isEmpty()) {
                switch (entries.length) {
                case 1:
                    return of();
                case 2: {
                    final int newBitmask = bitmask & ~bit;
                    final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                    final TrieNode<T> remainingChild = entries[realIndex(bitmask, 1 << remainingIndex)];
                    if (remainingChild.isLeaf()) {
                        return remainingChild;
                    } else {
                        return SingleBranchTrieNode.forBranchIndex(shift, remainingIndex, remainingChild);
                    }
                }
                default: {
                    final int newLength = entries.length - 1;
                    final TrieNode<T>[] newArray = allocate(newLength);
                    System.arraycopy(entries, 0, newArray, 0, childIndex);
                    System.arraycopy(entries, childIndex + 1, newArray, childIndex, newLength - childIndex);
                    return new MultiBranchTrieNode<T>(shift, bitmask & ~bit, newArray);
                }
                }
            } else if (newChild == child) {
                return this;
            } else {
                assert newChild.isLeaf() || (newChild.getShift() == shift - 5);
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public <K, V> TrieNode<T> delete(int shift,
                                     int index,
                                     K key,
                                     Transforms<T, K, V> transforms,
                                     MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final TrieNode<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            final TrieNode<T> child = entries[childIndex];
            TrieNode<T> newChild = child.delete(shift - 5, index, key, transforms, sizeDelta);
            if (newChild.isEmpty()) {
                switch (entries.length) {
                case 1:
                    return of();
                case 2: {
                    final int newBitmask = bitmask & ~bit;
                    final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                    final TrieNode<T> remainingChild = entries[realIndex(bitmask, 1 << remainingIndex)];
                    if (remainingChild.isLeaf()) {
                        return remainingChild;
                    } else {
                        return SingleBranchTrieNode.forBranchIndex(shift, remainingIndex, remainingChild);
                    }
                }
                default: {
                    final int newLength = entries.length - 1;
                    final TrieNode<T>[] newArray = allocate(newLength);
                    System.arraycopy(entries, 0, newArray, 0, childIndex);
                    System.arraycopy(entries, childIndex + 1, newArray, childIndex, newLength - childIndex);
                    return new MultiBranchTrieNode<T>(shift, bitmask & ~bit, newArray);
                }
                }
            } else if (newChild == child) {
                return this;
            } else {
                assert newChild.isLeaf() || (newChild.getShift() == shift - 5);
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public int getShift()
    {
        return shift;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public TrieNode<T> trimmedToMinimumDepth()
    {
        return bitmask == 1 ? entries[0].trimmedToMinimumDepth() : this;
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor()
    {
        return entryCursor(new AnyOrderCursorSource());
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> signedOrderEntryCursor()
    {
        if (shift != ROOT_SHIFT) {
            return anyOrderEntryCursor();
        } else {
            return entryCursor(new SignedOrderCursorSource());
        }
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> anyOrderEntryCursor(final Transforms<T, K, V> transforms)
    {
        return entryCursor(new AnyOrderCursorSource(), transforms);
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> signedOrderEntryCursor(Transforms<T, K, V> transforms)
    {
        if (shift != ROOT_SHIFT) {
            return anyOrderEntryCursor(transforms);
        } else {
            return entryCursor(new SignedOrderCursorSource(), transforms);
        }
    }

    @Override
    public Cursor<T> anyOrderValueCursor()
    {
        return valueCursor(new AnyOrderCursorSource());
    }

    @Override
    public Cursor<T> signedOrderValueCursor()
    {
        if (shift != ROOT_SHIFT) {
            return anyOrderValueCursor();
        } else {
            return valueCursor(new SignedOrderCursorSource());
        }
    }

    // for use by unit tests
    int getBitmask()
    {
        return bitmask;
    }

    // for use by unit tests
    TrieNode<T>[] getEntries()
    {
        return entries.clone();
    }

    private Cursor<JImmutableMap.Entry<Integer, T>> entryCursor(StandardCursor.Source<TrieNode<T>> source)
    {
        return MultiTransformCursor.of(StandardCursor.of(source), new Func1<TrieNode<T>, Cursor<JImmutableMap.Entry<Integer, T>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<Integer, T>> apply(TrieNode<T> node)
            {
                return node.anyOrderEntryCursor();
            }
        });
    }

    private <K, V> Cursor<JImmutableMap.Entry<K, V>> entryCursor(StandardCursor.Source<TrieNode<T>> source,
                                                                 final Transforms<T, K, V> transforms)
    {
        return MultiTransformCursor.of(StandardCursor.of(source), new Func1<TrieNode<T>, Cursor<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<K, V>> apply(TrieNode<T> node)
            {
                return node.anyOrderEntryCursor(transforms);
            }
        });
    }

    private Cursor<T> valueCursor(StandardCursor.Source<TrieNode<T>> source)
    {
        return MultiTransformCursor.of(StandardCursor.of(source), new Func1<TrieNode<T>, Cursor<T>>()
        {
            @Override
            public Cursor<T> apply(TrieNode<T> node)
            {
                return node.anyOrderValueCursor();
            }
        });
    }

    private class AnyOrderCursorSource
            implements StandardCursor.Source<TrieNode<T>>
    {
        private final int index;

        private AnyOrderCursorSource()
        {
            this(0);
        }

        private AnyOrderCursorSource(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index >= entries.length;
        }

        @Override
        public TrieNode<T> currentValue()
        {
            return entries[index];
        }

        @Override
        public StandardCursor.Source<TrieNode<T>> advance()
        {
            return new AnyOrderCursorSource(index + 1);
        }
    }

    private class SignedOrderCursorSource
            implements StandardCursor.Source<TrieNode<T>>
    {
        private final IndexList indexList;

        private SignedOrderCursorSource()
        {
            this(SIGNED_INDEX_LIST);
        }

        private SignedOrderCursorSource(IndexList indexList)
        {
            assert shift == TrieNode.ROOT_SHIFT;
            this.indexList = findFirstIndex(indexList);
        }

        @Override
        public boolean atEnd()
        {
            return indexList == null;
        }

        @Override
        public TrieNode<T> currentValue()
        {
            return entries[realIndex(bitmask, indexList.bit)];
        }

        @Override
        public StandardCursor.Source<TrieNode<T>> advance()
        {
            if (indexList == null) {
                return this;
            } else {
                return new SignedOrderCursorSource(findFirstIndex(indexList.next));
            }
        }
    }

    private IndexList findFirstIndex(IndexList next)
    {
        while (next != null && (bitmask & next.bit) == 0) {
            next = next.next;
        }
        return next;
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    static <T> TrieNode<T>[] allocate(int size)
    {
        return (TrieNode<T>[])new TrieNode[size];
    }

    private static class IndexList
    {
        private final int bit;
        private final IndexList next;

        private IndexList(int index,
                          IndexList next)
        {
            this.bit = 1 << index;
            this.next = next;
        }
    }
}
