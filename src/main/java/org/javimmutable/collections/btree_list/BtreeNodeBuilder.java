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

package org.javimmutable.collections.btree_list;

import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Mutable builder object for constructing well formed btree lists without
 * requiring all values to be collected into java.util.List first.  The
 * build() method can be called multiple times safely, for example to build
 * multiple root nodes successively.
 */
class BtreeNodeBuilder<T>
{
    private final LeafBuilder<T> leafBuilder = new LeafBuilder<>();
    private int size;

    synchronized int size()
    {
        return size;
    }

    synchronized void add(T value)
    {
        leafBuilder.add(value);
        size += 1;
    }

    /**
     * Clears any existing data in this builder and then populates the builder with
     * nodes from the provided tree.  At each level of the tree it pushes all children
     * except the last to its parent's depth.  The last child at each level is then
     * split in the same way to populate the next lower level.  Once the leaf level is
     * reached all of the values in the last leaf are added to the builder normally.
     */
    synchronized void rebuild(@Nonnull BtreeNode<T> root)
    {
        leafBuilder.clear();
        size = 0;
        Iterator<BtreeNode<T>> nodes = root.depth() > 1 ? root.childIterator() : SingleValueIterator.of(root);
        while (nodes.hasNext()) {
            final BtreeNode<T> node = nodes.next();
            if (nodes.hasNext()) {
                leafBuilder.addNode(node);
                size += node.valueCount();
            } else if (node.depth() > 1) {
                nodes = node.childIterator();
            } else {
                for (T value : node) {
                    leafBuilder.add(value);
                }
                size += node.valueCount();
            }
        }
        assert size == root.valueCount();
    }

    synchronized BtreeNode<T> build()
    {
        if (size == 0) {
            return BtreeEmptyNode.of();
        } else {
            return leafBuilder.build().compress();
        }
    }

    synchronized void checkInvariants()
    {
        if (size != leafBuilder.computeSize()) {
            throw new IllegalStateException("size mismatch");
        }
        leafBuilder.checkInvariants();
    }

    private static class LeafBuilder<T>
    {
        private final T[] buffer;
        private int count;
        private BranchBuilder<T> parent;

        @SuppressWarnings("unchecked")
        private LeafBuilder()
        {
            buffer = (T[])new Object[BtreeNode.MAX_CHILDREN];
        }

        private void add(@Nonnull T value)
        {
            assert count < BtreeNode.MAX_CHILDREN;
            buffer[count] = value;
            count += 1;
            if (count == BtreeNode.MAX_CHILDREN) {
                push(BtreeNode.MIN_CHILDREN);
            }
        }

        private void addNode(@Nonnull BtreeNode<T> node)
        {
            assert count == 0;
            if (parent == null) {
                parent = new BranchBuilder<>(1);
            }
            if (node.depth() == 1) {
                parent.add(node);
            } else {
                parent.addNode(node);
            }
        }

        private BtreeNode<T> build()
        {
            assert count > 0;
            if (parent == null) {
                return BtreeLeafNode.of(IndexedArray.retained(buffer), 0, count);
            } else {
                assert count >= BtreeNode.MIN_CHILDREN;
                return parent.build(BtreeLeafNode.of(IndexedArray.retained(buffer), 0, count));
            }
        }

        private void clear()
        {
            count = 0;
            parent = null;
        }

        private void push(int howMany)
        {
            assert howMany <= count;
            assert howMany >= BtreeNode.MIN_CHILDREN;
            if (parent == null) {
                parent = new BranchBuilder<>(1);
            }
            parent.add(BtreeLeafNode.of(IndexedArray.retained(buffer), 0, howMany));
            System.arraycopy(buffer, howMany, buffer, 0, count - howMany);
            count -= howMany;
        }

        private int computeSize()
        {
            int answer = count;
            if (parent != null) {
                answer += parent.computeSize();
            }
            return answer;
        }

        private void checkInvariants()
        {
            if (parent != null) {
                if (count < BtreeNode.MIN_CHILDREN) {
                    throw new IllegalStateException("count < MIN_CHILDREN");
                }
                parent.checkInvariants();
            }
            if (count >= BtreeNode.MAX_CHILDREN) {
                throw new IllegalStateException("count >= MAX_CHILDREN");
            }
        }
    }

    private static class BranchBuilder<T>
    {
        private final int depth;
        private final BtreeNode<T>[] buffer;
        private int count;
        private BranchBuilder<T> parent;

        @SuppressWarnings("unchecked")
        private BranchBuilder(int depth)
        {
            this.depth = depth;
            this.buffer = new BtreeNode[BtreeNode.MAX_CHILDREN];
        }

        private void add(@Nonnull BtreeNode<T> node)
        {
            assert node.depth() == depth;
            assert count < BtreeNode.MAX_CHILDREN;
            buffer[count] = node;
            count += 1;
            if (count == BtreeNode.MAX_CHILDREN) {
                push(BtreeNode.MIN_CHILDREN);
            }
        }

        private void addNode(@Nonnull BtreeNode<T> node)
        {
            if (node.depth() == depth) {
                add(node);
            } else {
                assert count == 0;
                if (parent == null) {
                    parent = new BranchBuilder<>(depth + 1);
                }
                parent.addNode(node);
            }
        }

        private BtreeNode<T> build(@Nonnull BtreeNode<T> extraNode)
        {
            assert count < BtreeNode.MAX_CHILDREN;
            buffer[count] = extraNode;
            final BtreeNode<T> node = BtreeBranchNode.of(IndexedArray.retained(buffer), 0, count + 1);
            if (parent == null) {
                return node;
            } else {
                return parent.build(node);
            }
        }

        private void push(int howMany)
        {
            assert howMany <= count;
            assert howMany >= BtreeNode.MIN_CHILDREN;
            if (parent == null) {
                parent = new BranchBuilder<>(depth + 1);
            }
            parent.add(BtreeBranchNode.of(IndexedArray.retained(buffer), 0, howMany));
            System.arraycopy(buffer, howMany, buffer, 0, count - howMany);
            count -= howMany;
        }

        private int computeSize()
        {
            int answer = 0;
            for (int i = 0; i < count; ++i) {
                answer += buffer[i].valueCount();
            }
            if (parent != null) {
                answer += parent.computeSize();
            }
            return answer;
        }

        private void checkInvariants()
        {
            if (parent == null) {
                if (count < 1) {
                    throw new IllegalStateException("count < 1");
                }
            } else {
                if (count < (BtreeNode.MIN_CHILDREN - 1)) {
                    throw new IllegalStateException("count < MIN_CHILDREN");
                }
            }
            if (count >= BtreeNode.MAX_CHILDREN) {
                throw new IllegalStateException("count >= MAX_CHILDREN");
            }
            for (int i = 0; i < count; ++i) {
                if (buffer[i].depth() != depth) {
                    throw new IllegalStateException("wrong node depth");
                }
            }
            if (parent != null) {
                parent.checkInvariants();
            }
        }
    }
}
