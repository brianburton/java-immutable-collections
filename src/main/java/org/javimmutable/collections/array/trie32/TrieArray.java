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
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.common.AbstractJImmutableArray;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.iterators.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Immutable
public class TrieArray<T>
    extends AbstractJImmutableArray<T>
{
    @SuppressWarnings("unchecked")
    private static final TrieArray EMPTY = new TrieArray(TrieNode.of(), 0);

    private final TrieNode<T> root;
    private final int size;

    private TrieArray(TrieNode<T> root,
                      int size)
    {
        this.root = root;
        this.size = size;
    }

    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> TrieArray<T> of()
    {
        return (TrieArray<T>)EMPTY;
    }

    /**
     * Efficiently constructs a TrieArray containing the objects from source (in the specified range).
     * In the constructed TrieArray objects will have array indexes starting at 0 (i.e. indexes
     * from the source are not carried over) so if offset is 10 then source.get(10) will map to
     * array.get(0).
     *
     * @deprecated use builder() instead
     */
    @Deprecated
    public static <T> JImmutableArray<T> of(Indexed<? extends T> source,
                                            int offset,
                                            int limit)
    {
        return TrieArray.<T>builder().add(source, offset, limit).build();
    }

    // made obsolete by Builder but retained for use in unit test
    @SuppressWarnings("SameParameterValue")
    static <T> JImmutableArray<T> oldof(Indexed<? extends T> source,
                                        int offset,
                                        int limit)
    {
        final int size = limit - offset;
        if (size == 0) {
            return of();
        }

        // small lists can be directly constructed from a single leaf array
        if (size <= 32) {
            return new TrieArray<>(TrieNode.fromSource(0, source, offset, limit), size);
        }

        // first construct an array containing a single level of arrays of leaves
        final int numBranches = Math.min(32, ((limit - offset) + 31) / 32);
        @SuppressWarnings("unchecked") final TrieNode<T>[] branchArray = (TrieNode<T>[])new TrieNode[numBranches];
        int index = 0;
        for (int b = 0; b < numBranches; ++b) {
            int branchSize = Math.min(32, limit - offset);
            branchArray[b] = TrieNode.fromSource(index, source, offset, limit);
            offset += branchSize;
            index += branchSize;
        }

        // then add any extras left over above that size
        JImmutableArray<T> array = new TrieArray<>(MultiBranchTrieNode.forEntries(5, branchArray), index);
        while (offset < limit) {
            array = array.assign(index++, source.get(offset++));
        }
        return array;
    }

    @Override
    @Nullable
    public T getValueOr(int index,
                        @Nullable T defaultValue)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return defaultValue;
        } else {
            return root.getValueOr(root.getShift(), index, defaultValue);
        }
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return Holders.of();
        } else {
            return root.find(root.getShift(), index);
        }
    }

    @Nonnull
    @Override
    public TrieArray<T> assign(int index,
                               @Nullable T value)
    {
        MutableDelta sizeDelta = new MutableDelta();
        TrieNode<T> newRoot = root.paddedToMinimumDepthForShift(TrieNode.shiftForIndex(index));
        newRoot = newRoot.assign(newRoot.getShift(), index, value, sizeDelta);
        return (newRoot == root) ? this : new TrieArray<>(newRoot, size + sizeDelta.getValue());
    }

    @Nonnull
    @Override
    public TrieArray<T> delete(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return this;
        } else {
            MutableDelta sizeDelta = new MutableDelta();
            final TrieNode<T> newRoot = root.delete(root.getShift(), index, sizeDelta).trimmedToMinimumDepth();
            return (newRoot == root) ? this : new TrieArray<>(newRoot, size + sizeDelta.getValue());
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Nonnull
    @Override
    public TrieArray<T> deleteAll()
    {
        return of();
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return root.signedOrderEntryCursor();
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return root.signedOrderEntryIterator();
    }

    @Override
    public void checkInvariants()
    {
        //TODO: fix empty checkInvariants()
    }

    public static class Builder<T>
        implements MutableBuilder<T, TrieArray<T>>
    {
        private final List<TrieNode<T>> leaves = new ArrayList<>();

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            final TrieNode<T> leaf = LeafTrieNode.of(leaves.size(), value);
            leaves.add(leaf);
            return this;
        }

        @Nonnull
        @Override
        public TrieArray<T> build()
        {
            int nodeCount = leaves.size();
            if (nodeCount == 0) {
                return of();
            }

            if (nodeCount == 1) {
                return new TrieArray<>(leaves.get(0), 1);
            }

            List<TrieNode<T>> dst = new ArrayList<>();
            List<TrieNode<T>> src = leaves;
            int shift = 0;
            while (nodeCount > 1) {
                int dstOffset = 0;
                int srcOffset = 0;
                while (srcOffset < nodeCount) {
                    final int count = Math.min(32, nodeCount - srcOffset);
                    TrieNode<T>[] nodes = allocate(count);
                    for (int i = 0; i < count; ++i) {
                        nodes[i] = src.get(srcOffset++);
                    }
                    TrieNode<T> branch;
                    switch (count) {
                    case 1:
                        branch = SingleBranchTrieNode.forBranchIndex(shift, 0, nodes[0]);
                        break;
                    case 32:
                        branch = new FullBranchTrieNode<>(shift, nodes);
                        break;
                    default:
                        branch = MultiBranchTrieNode.forEntries(shift, nodes);
                        break;
                    }
                    set(dst, dstOffset++, branch);
                }
                shift += 5;
                src = dst;
                nodeCount = dstOffset;
            }
            assert nodeCount == 1;
            return new TrieArray<>(dst.get(0), leaves.size());
        }

        @Nonnull
        @Override
        public Builder<T> add(Cursor<? extends T> source)
        {
            for (Cursor<? extends T> cursor = source.start(); cursor.hasValue(); cursor = cursor.next()) {
                add(cursor.getValue());
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Iterator<? extends T> source)
        {
            while (source.hasNext()) {
                add(source.next());
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Collection<? extends T> source)
        {
            add(source.iterator());
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> Builder<T> add(K... source)
        {
            for (T value : source) {
                add(value);
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source)
        {
            return add(source, 0, source.size());
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source,
                              int offset,
                              int limit)
        {
            for (int i = offset; i < limit; ++i) {
                add(source.get(i));
            }
            return this;
        }

        private void set(List<TrieNode<T>> dst,
                         int index,
                         TrieNode<T> node)
        {
            if (index < dst.size()) {
                dst.set(index, node);
            } else {
                assert index == dst.size();
                dst.add(node);
            }
        }

        @SuppressWarnings("unchecked")
        private TrieNode<T>[] allocate(int size)
        {
            return new TrieNode[size];
        }
    }
}
