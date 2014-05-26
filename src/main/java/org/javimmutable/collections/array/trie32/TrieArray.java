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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.common.AbstractJImmutableArray;
import org.javimmutable.collections.common.MutableDelta;

import java.util.Collection;
import java.util.Iterator;

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
        return new Builder<T>();
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
     * @param source
     * @param offset
     * @param limit
     * @param <T>
     * @return
     * @deprecated use builder() instead
     */
    public static <T> JImmutableArray<T> of(Indexed<? extends T> source,
                                            int offset,
                                            int limit)
    {
        return TrieArray.<T>builder().add(source, offset, limit).build();
    }

    // made obsolete by Builder but retained for use in unit test
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
            return new TrieArray<T>(TrieNode.fromSource(0, source, offset, limit), size);
        }

        // first construct an array containing a single level of arrays of leaves
        final int numBranches = Math.min(32, (limit - offset + 31) / 32);
        @SuppressWarnings("unchecked") final TrieNode<T>[] branchArray = (TrieNode<T>[])new TrieNode[numBranches];
        int index = 0;
        for (int b = 0; b < numBranches; ++b) {
            int branchSize = Math.min(32, limit - offset);
            branchArray[b] = TrieNode.fromSource(index, source, offset, limit);
            offset += branchSize;
            index += branchSize;
        }

        // then add any extras left over above that size
        JImmutableArray<T> array = new TrieArray<T>(MultiBranchTrieNode.forEntries(5, branchArray), index);
        while (offset < limit) {
            array = array.assign(index++, source.get(offset++));
        }
        return array;
    }

    @Override
    public T getValueOr(int index,
                        T defaultValue)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return defaultValue;
        } else {
            return root.getValueOr(root.getShift(), index, defaultValue);
        }
    }

    @Override
    public Holder<T> find(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return Holders.of();
        } else {
            return root.find(root.getShift(), index);
        }
    }

    @Override
    public JImmutableArray<T> assign(int index,
                                     T value)
    {
        MutableDelta sizeDelta = new MutableDelta();
        TrieNode<T> newRoot = root.paddedToMinimumDepthForShift(TrieNode.shiftForIndex(index));
        newRoot = newRoot.assign(newRoot.getShift(), index, value, sizeDelta);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
    }

    @Override
    public JImmutableArray<T> delete(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return this;
        } else {
            MutableDelta sizeDelta = new MutableDelta();
            final TrieNode<T> newRoot = root.delete(root.getShift(), index, sizeDelta).trimmedToMinimumDepth();
            return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return root.signedOrderEntryCursor();
    }

    public static class Builder<T>
            implements MutableBuilder<T, TrieArray<T>>
    {
        private BuilderImpl<T> builderImpl = new LeafBuilderImpl<T>();
        private boolean built;

        @Override
        public Builder<T> add(T value)
        {
            builderImpl = builderImpl.add(value);
            return this;
        }

        @Override
        public TrieArray<T> build()
        {
            if (built) {
                throw new IllegalStateException();
            }
            built = true;

            builderImpl = builderImpl.finish();
            return builderImpl.build();
        }

        @Override
        public Builder<T> add(Cursor<? extends T> source)
        {
            for (Cursor<? extends T> cursor = source.start(); cursor.hasValue(); cursor = cursor.next()) {
                add(cursor.getValue());
            }
            return this;
        }

        @Override
        public Builder<T> add(Iterator<? extends T> source)
        {
            while (source.hasNext()) {
                add(source.next());
            }
            return this;
        }

        @Override
        public Builder<T> add(Collection<? extends T> source)
        {
            add(source.iterator());
            return this;
        }

        @Override
        public <K extends T> Builder<T> add(K... source)
        {
            for (T value : source) {
                add(value);
            }
            return this;
        }

        @Override
        public Builder<T> add(Indexed<? extends T> source)
        {
            return add(source, 0, source.size());
        }

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
    }

    private static abstract class BuilderImpl<T>
    {
        protected BranchBuilderImpl<T> parentBuilder;
        protected TrieNode<T>[] nodes;
        protected int nodeCount;
        final int shift;

        protected BuilderImpl(int shift)
        {
            this.nodes = allocate(32);
            this.nodeCount = 0;
            this.shift = shift;
        }

        abstract BuilderImpl<T> add(T value);

        abstract TrieArray<T> build();

        abstract int size();

        abstract BuilderImpl<T> finish();

        protected BuilderImpl<T> getRoot()
        {
            return parentBuilder != null ? parentBuilder.getRoot() : this;
        }

        protected TrieNode<T> createBranch(int shift,
                                           TrieNode<T>[] nodes,
                                           int nodeCount)
        {
            switch (nodeCount) {
            case 1:
                return SingleBranchTrieNode.forBranchIndex(shift, 0, nodes[0]);

            case 32:
                return new FullBranchTrieNode<T>(shift, nodes.clone());

            default:
                TrieNode<T>[] branchArray = allocate(nodeCount);
                System.arraycopy(nodes, 0, branchArray, 0, nodeCount);
                return MultiBranchTrieNode.forEntries(shift, branchArray);
            }
        }

        protected void addNode(TrieNode<T> node)
        {
            nodes[nodeCount] = node;
            nodeCount += 1;
            if (nodeCount == 32) {
                if (parentBuilder == null) {
                    parentBuilder = new BranchBuilderImpl<T>(shift + 5, this);
                }
                parentBuilder.acceptChildNode(nodes, nodeCount);
                nodeCount = 0;
            }
        }

        @SuppressWarnings("unchecked")
        private static <T> TrieNode<T>[] allocate(int size)
        {
            return new TrieNode[size];
        }
    }

    private static class BranchBuilderImpl<T>
            extends BuilderImpl<T>
    {
        private final BuilderImpl<T> childBuilder;

        private BranchBuilderImpl(int shift,
                                  BuilderImpl<T> childBuilder)
        {
            super(shift);
            this.childBuilder = childBuilder;
        }

        @Override
        BuilderImpl<T> add(T value)
        {
            return childBuilder.add(value);
        }

        @Override
        TrieArray<T> build()
        {
            assert parentBuilder == null;
            assert nodeCount > 0;
            return new TrieArray<T>(createBranch(shift, nodes, nodeCount).trimmedToMinimumDepth(), size());
        }

        void acceptChildNode(TrieNode<T>[] childNodes,
                             int childNodeCount)
        {
            addNode(createBranch(shift - 5, childNodes, childNodeCount));
        }

        @Override
        int size()
        {
            return childBuilder.size();
        }

        @Override
        BuilderImpl<T> finish()
        {
            childBuilder.finish();
            if (parentBuilder != null && nodeCount > 0) {
                parentBuilder.acceptChildNode(nodes, nodeCount);
            }
            return getRoot();
        }
    }

    private static class LeafBuilderImpl<T>
            extends BuilderImpl<T>
    {
        private int size;

        private LeafBuilderImpl()
        {
            super(0);
        }

        @Override
        BuilderImpl<T> add(T value)
        {
            addNode(LeafTrieNode.of(size, value));
            size += 1;
            return getRoot();
        }

        @Override
        TrieArray<T> build()
        {
            assert parentBuilder == null;
            assert nodeCount == size;
            switch (size) {
            case 0:
                return TrieArray.of();
            case 1:
                return new TrieArray<T>(nodes[0], 1);
            default:
                return new TrieArray<T>(createBranch(0, nodes, nodeCount), size);
            }
        }

        @Override
        int size()
        {
            return size;
        }

        @Override
        BuilderImpl<T> finish()
        {
            if (parentBuilder != null && nodeCount > 0) {
                parentBuilder.acceptChildNode(nodes, nodeCount);
            }
            return getRoot();
        }
    }
}
