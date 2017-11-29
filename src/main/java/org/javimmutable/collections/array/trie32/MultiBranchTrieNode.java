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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.LazyMultiIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class MultiBranchTrieNode<T>
    extends TrieNode<T>
{
    private final int shift;
    private final int bitmask;
    @Nonnull
    private final TrieNode<T>[] entries;

    private MultiBranchTrieNode(int shift,
                                int bitmask,
                                @Nonnull TrieNode<T>[] entries)
    {
        assert shift >= 0;
        this.shift = shift;
        this.bitmask = bitmask;
        this.entries = entries;
    }

    @SuppressWarnings("SameParameterValue")
    static <T> MultiBranchTrieNode<T> forTesting(int shift)
    {
        TrieNode<T>[] entries = allocate(0);
        return new MultiBranchTrieNode<>(shift, 0, entries);
    }

    static <T> MultiBranchTrieNode<T> forIndex(int shift,
                                               int index,
                                               @Nonnull TrieNode<T> child)
    {
        int branchIndex = ((index >>> shift) & 0x1f);
        return forBranchIndex(shift, branchIndex, child);
    }

    static <T> MultiBranchTrieNode<T> forBranchIndex(int shift,
                                                     int branchIndex,
                                                     @Nonnull TrieNode<T> child)
    {
        assert (branchIndex >= 0) && (branchIndex < 32);
        TrieNode<T>[] entries = allocate(1);
        entries[0] = child;
        return new MultiBranchTrieNode<>(shift, 1 << branchIndex, entries);
    }

    static <T> MultiBranchTrieNode<T> forEntries(int shift,
                                                 @Nonnull TrieNode<T>[] entries)
    {
        final int length = entries.length;
        final int bitmask = (length == 32) ? -1 : ((1 << length) - 1);
        return new MultiBranchTrieNode<>(shift, bitmask, entries.clone());
    }

    static <T> MultiBranchTrieNode<T> forSource(int index,
                                                int size,
                                                @Nonnull Indexed<? extends T> source,
                                                int offset)
    {
        final TrieNode<T>[] entries = allocate(size);
        for (int i = 0; i < size; ++i) {
            entries[i] = LeafTrieNode.of(index++, source.get(offset++));
        }
        final int bitmask = (size == 32) ? -1 : ((1 << size) - 1);
        return new MultiBranchTrieNode<>(0, bitmask, entries);
    }

    static <T> MultiBranchTrieNode<T> fullWithout(int shift,
                                                  @Nonnull TrieNode<T>[] entries,
                                                  int withoutIndex)
    {
        assert entries.length == 32;
        final TrieNode<T>[] newEntries = allocate(31);
        System.arraycopy(entries, 0, newEntries, 0, withoutIndex);
        System.arraycopy(entries, withoutIndex + 1, newEntries, withoutIndex, 31 - withoutIndex);
        final int newMask = ~(1 << withoutIndex);
        return new MultiBranchTrieNode<>(shift, newMask, newEntries);
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
            final TrieNode<T> newChild = LeafTrieNode.of(index, value);
            sizeDelta.add(1);
            return selectNodeForInsertResult(shift, bit, bitmask, childIndex, entries, newChild);
        } else {
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.assign(shift - 5, index, value, sizeDelta);
            return selectNodeForUpdateResult(shift, bitmask, childIndex, entries, child, newChild);
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
            return selectNodeForDeleteResult(shift, bit, bitmask, entries, childIndex, child, newChild);
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
        return (bitmask == 1) ? entries[0].trimmedToMinimumDepth() : this;
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        if (shift != ROOT_SHIFT) {
            return LazyMultiCursor.cursor(IndexedArray.retained(entries));
        } else {
            return LazyMultiCursor.cursor(IndexedArray.retained(entriesForSignedOrderIteration()));
        }
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        if (shift != ROOT_SHIFT) {
            return LazyMultiIterator.iterator(IndexedArray.retained(entries));
        } else {
            return LazyMultiIterator.iterator(IndexedArray.retained(entriesForSignedOrderIteration()));
        }
    }

    @Override
    public void checkInvariants()
    {
        if (shift < 0 || shift > ROOT_SHIFT) {
            throw new IllegalStateException("illegal shift value: " + shift);
        }
        if (entries.length != Integer.bitCount(bitmask)) {
            throw new IllegalStateException("unexpected entries size: expected=" + Integer.bitCount(bitmask) + " actual=" + entries.length);
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

    private TrieNode<T> selectNodeForUpdateResult(int shift,
                                                  int bitmask,
                                                  int childIndex,
                                                  TrieNode<T>[] entries,
                                                  TrieNode<T> child,
                                                  TrieNode<T> newChild)
    {
        if (newChild == child) {
            return this;
        } else {
            assert newChild.isLeaf() || (newChild.getShift() == (shift - 5));
            final TrieNode<T>[] newEntries = entries.clone();
            newEntries[childIndex] = newChild;
            return new MultiBranchTrieNode<>(shift, bitmask, newEntries);
        }
    }

    private TrieNode<T> selectNodeForInsertResult(int shift,
                                                  int bit,
                                                  int bitmask,
                                                  int childIndex,
                                                  TrieNode<T>[] entries,
                                                  TrieNode<T> newChild)
    {
        final int oldLength = entries.length;
        final TrieNode<T>[] newEntries = allocate(oldLength + 1);
        if (bitmask != 0) {
            System.arraycopy(entries, 0, newEntries, 0, childIndex);
            System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
        }
        newEntries[childIndex] = newChild;
        if (newEntries.length == 32) {
            return new FullBranchTrieNode<>(shift, newEntries);
        } else {
            return new MultiBranchTrieNode<>(shift, bitmask | bit, newEntries);
        }
    }

    private TrieNode<T> selectNodeForDeleteResult(int shift,
                                                  int bit,
                                                  int bitmask,
                                                  TrieNode<T>[] entries,
                                                  int childIndex,
                                                  TrieNode<T> child,
                                                  TrieNode<T> newChild)
    {
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
                    return new MultiBranchTrieNode<>(shift, bitmask & ~bit, newArray);
                }
            }
        } else {
            return selectNodeForUpdateResult(shift, bitmask, childIndex, entries, child, newChild);
        }
    }

    private TrieNode<T>[] entriesForSignedOrderIteration()
    {
        final TrieNode<T>[] entries = this.entries;
        final TrieNode<T>[] nodes = allocate(entries.length);
        final int bitmask = this.bitmask;
        int offset = 0;
        if ((bitmask & 0b0100) != 0) {
            nodes[offset++] = entries[realIndex(bitmask, 0b0100)];
        }
        if ((bitmask & 0b1000) != 0) {
            nodes[offset++] = entries[realIndex(bitmask, 0b1000)];
        }
        if ((bitmask & 0b0001) != 0) {
            nodes[offset++] = entries[realIndex(bitmask, 0b0001)];
        }
        if ((bitmask & 0b0010) != 0) {
            nodes[offset++] = entries[realIndex(bitmask, 0b0010)];
        }
        assert offset == nodes.length;
        return nodes;
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
}
