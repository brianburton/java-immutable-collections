///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.deque;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;

final class DequeHelper
{
    private static final Object[] EMPTY_VALUES = new Object[0];
    private static final Node[] EMPTY_NODES = new Node[0];

    @SuppressWarnings("unchecked")
    static <T> Node<T>[] allocateNodes(int size)
    {
        return (Node<T>[])((size == 0) ? EMPTY_NODES : new Node[size]);
    }

    static <T> Node<T>[] allocateNodes(@Nonnull Indexed<Node<T>> source,
                                       int offset,
                                       int limit)
    {
        assert source.size() >= (limit - offset);

        final int size = limit - offset;
        final Node<T>[] nodes = allocateNodes(size);
        for (int i = 0; i < size; ++i) {
            nodes[i] = source.get(offset + i);
        }
        return nodes;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] allocateValues(int size)
    {
        return (T[])((size == 0) ? EMPTY_VALUES : new Object[size]);
    }

    static <T> Node<T>[] allocateSingleNode(Node<T> node)
    {
        Node<T>[] answer = allocateNodes(1);
        answer[0] = node;
        return answer;
    }

    static <T> Node<T>[] assign(Node<T>[] nodes,
                                int index,
                                Node<T> node)
    {
        Node<T>[] newNodes = nodes.clone();
        newNodes[index] = node;
        return newNodes;
    }

    static <T> Node<T>[] insertFirst(Node<T>[] nodes,
                                     Node<T> node)
    {
        Node<T>[] newNodes = allocateNodes(nodes.length + 1);
        System.arraycopy(nodes, 0, newNodes, 1, nodes.length);
        newNodes[0] = node;
        return newNodes;
    }

    static <T> Node<T>[] insertLast(Node<T>[] nodes,
                                    Node<T> node)
    {
        Node<T>[] newNodes = allocateNodes(nodes.length + 1);
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        newNodes[nodes.length] = node;
        return newNodes;
    }

    static <T> Node<T>[] deleteFirst(Node<T>[] nodes)
    {
        Node<T>[] newNodes = allocateNodes(nodes.length - 1);
        System.arraycopy(nodes, 1, newNodes, 0, newNodes.length);
        return newNodes;
    }

    static <T> Node<T>[] deleteLast(Node<T>[] nodes)
    {
        Node<T>[] newNodes = allocateNodes(nodes.length - 1);
        System.arraycopy(nodes, 0, newNodes, 0, newNodes.length);
        return newNodes;
    }

    static int sizeForDepth(int depth)
    {
        return 1 << (5 * depth);
    }

    static <T> boolean allNodesFull(int depth,
                                    @Nonnull Indexed<Node<T>> nodes,
                                    int offset,
                                    int limit)
    {
        for (int i = offset; i < limit; ++i) {
            Node<T> node = nodes.get(i);
            if (node.getDepth() != depth - 1) {
                return false;
            }
            if (!node.isFull()) {
                return false;
            }
        }
        return true;
    }
}
