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

package org.javimmutable.collections.list;

import org.javimmutable.collections.indexed.IndexedArray;

import javax.annotation.Nonnull;

class TreeBuilder<T>
{
    private final LeafBuilder<T> leafBuilder;

    TreeBuilder(boolean forwardOrder)
    {
        leafBuilder = new LeafBuilder<>(forwardOrder);
    }

    synchronized void add(T value)
    {
        leafBuilder.add(value);
    }

    synchronized int size()
    {
        return leafBuilder.size;
    }

    @Nonnull
    synchronized Node<T> build()
    {
        return leafBuilder.build();
    }

    private static class LeafBuilder<T>
    {
        private final boolean forwardOrder;
        private final T[] values;
        private BranchBuilder<T> next;
        private int offset;
        private int remaining;
        private int size;

        private LeafBuilder(boolean forwardOrder)
        {
            this.forwardOrder = forwardOrder;
            values = ListHelper.allocateValues(32);
            offset = forwardOrder ? 0 : 32;
            remaining = 32;
        }

        private void add(T value)
        {
            assert remaining >= 1;

            if (forwardOrder) {
                values[offset++] = value;
            } else {
                values[--offset] = value;
            }
            if (remaining == 1) {
                if (next == null) {
                    next = new BranchBuilder<>(1, forwardOrder);
                }
                next.add(createNodeForNext());
                offset = forwardOrder ? 0 : 32;
                remaining = 32;
            } else {
                remaining -= 1;
            }
            size += 1;
        }

        @Nonnull
        private Node<T> createNodeForNext()
        {
            if (forwardOrder) {
                return LeafNode.fromList(IndexedArray.retained(values), 0, offset);
            } else {
                return LeafNode.fromList(IndexedArray.retained(values), offset, 32);
            }
        }

        @Nonnull
        private Node<T> build()
        {
            final Node<T> myNode = (remaining == 32) ? EmptyNode.of() : createNodeForNext();
            return (next == null) ? myNode : next.build(myNode);
        }
    }

    private static class BranchBuilder<T>
    {
        private final int depth;
        private final boolean forwardOrder;
        private final Node<T>[] nodes;
        private BranchBuilder<T> next;
        private int offset;
        private int remaining;
        private int size;

        private BranchBuilder(int depth,
                              boolean forwardOrder)
        {
            this.depth = depth;
            this.forwardOrder = forwardOrder;
            nodes = ListHelper.allocateNodes(32);
            next = null;
            offset = forwardOrder ? 0 : 32;
            remaining = 32;
            size = 0;
        }

        private void add(@Nonnull Node<T> node)
        {
            assert node.isFull();
            assert node.getDepth() == depth;

            if (forwardOrder) {
                nodes[offset++] = node;
            } else {
                nodes[--offset] = node;
            }
            size += node.size();
            if (remaining == 1) {
                if (next == null) {
                    next = new BranchBuilder<>(depth + 1, forwardOrder);
                }
                next.add(createNodeForNext(EmptyNode.of()));
                offset = forwardOrder ? 0 : 32;
                remaining = 32;
                size = 0;
            } else {
                remaining -= 1;
            }
        }

        private Node<T> build(@Nonnull Node<T> extra)
        {
            Node<T> node;
            if (remaining == 32) {
                node = extra;
            } else if (remaining == 31 && next == null && extra.isEmpty()) {
                final int nodeOffset = forwardOrder ? offset - 1 : offset;
                return nodes[nodeOffset];
            } else {
                node = createNodeForNext(extra);
            }
            if (next != null) {
                node = next.build(node);
            }
            return node;
        }

        @Nonnull
        private Node<T> createNodeForNext(@Nonnull Node<T> extra)
        {
            final int nodeSize = size + extra.size();
            assert nodeSize <= ListHelper.sizeForDepth(depth + 1);
            if (forwardOrder) {
                return BranchNode.forNodeBuilder(depth + 1, nodeSize, EmptyNode.of(), IndexedArray.retained(nodes), 0, offset, extra);
            } else {
                return BranchNode.forNodeBuilder(depth + 1, nodeSize, extra, IndexedArray.retained(nodes), offset, 32, EmptyNode.of());
            }
        }
    }
}
