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

package org.javimmutable.collections.array;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
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
public class FullBranchTrieNode<T>
    extends TrieNode<T>
{
    private final int shift;
    private final TrieNode<T>[] entries;

    FullBranchTrieNode(int shift,
                       TrieNode<T>[] entries)
    {
        assert shift != ROOT_SHIFT;
        this.shift = shift;
        this.entries = entries;
    }

    static <T> FullBranchTrieNode<T> fromSource(int index,
                                                Indexed<? extends T> source,
                                                int offset)
    {
        assert (source.size() - offset) >= 32;
        TrieNode<T>[] entries = MultiBranchTrieNode.allocate(32);
        for (int i = 0; i < 32; ++i) {
            entries[i] = LeafTrieNode.of(index++, source.get(offset++));
        }
        return new FullBranchTrieNode<>(0, entries);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        return entries[childIndex].getValueOr(shift - 5, index, defaultValue);
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        return entries[childIndex].find(shift - 5, index);
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        final TrieNode<T> child = entries[childIndex];
        final TrieNode<T> newChild = child.assign(shift - 5, index, value, sizeDelta);
        if (newChild == child) {
            return this;
        } else {
            return createUpdatedEntries(shift, childIndex, newChild);
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        final TrieNode<T> child = entries[childIndex];
        final TrieNode<T> newChild = child.delete(shift - 5, index, sizeDelta);
        return createDeleteResultNode(shift, childIndex, child, newChild);
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

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return LazyMultiIterator.iterator(IndexedArray.retained(entries));
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return LazyMultiCursor.cursor(IndexedArray.retained(entries));
    }

    @Override
    public void checkInvariants()
    {
        if (shift < 0 || shift > ROOT_SHIFT) {
            throw new IllegalStateException("illegal shift value: " + shift);
        }
        if (entries.length != 32) {
            throw new IllegalStateException("unexpected entries size: expected=32 actual=" + entries.length);
        }
        for (TrieNode<T> entry : entries) {
            entry.checkInvariants();
        }
    }

    private TrieNode<T> createUpdatedEntries(int shift,
                                             int childIndex,
                                             TrieNode<T> newChild)
    {
        assert newChild.isLeaf() || (newChild.getShift() == (shift - 5));
        TrieNode<T>[] newEntries = entries.clone();
        newEntries[childIndex] = newChild;
        return new FullBranchTrieNode<>(shift, newEntries);
    }

    private TrieNode<T> createDeleteResultNode(int shift,
                                               int childIndex,
                                               TrieNode<T> child,
                                               TrieNode<T> newChild)
    {
        if (newChild == child) {
            return this;
        } else if (newChild.isEmpty()) {
            return MultiBranchTrieNode.fullWithout(shift, entries, childIndex);
        } else {
            return createUpdatedEntries(shift, childIndex, newChild);
        }
    }
}
