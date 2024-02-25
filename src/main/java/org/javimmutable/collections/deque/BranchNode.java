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
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Node implementation containing other nodes.  Prefix and suffix nodes can contain nodes
 * of any depth and size (including empty) while body nodes array contains only full nodes
 * of exactly depth - 1.  This allows fast access to the body nodes by a computed offset
 * based on size but also quick adds and deletes from either end using the prefix and suffix
 * nodes.  Once a BranchNode reaches its theoretical limit based on depth any insert triggers
 * the creation of a new higher depth node containing the branch so that the new parent's prefix
 * or suffix can contain the new value.
 */
@Immutable
class BranchNode<T>
    implements Node<T>
{
    private final int depth;
    private final int size;
    private final Node<T> prefix;  // possibly empty and can be any depth < this.depth
    private final Node<T>[] nodes; // all of these are full and have depth - 1
    private final Node<T> suffix;  // possibly empty and can be any depth < this.depth

    private BranchNode(int depth,
                       int size,
                       Node<T> prefix,
                       Node<T>[] nodes,
                       Node<T> suffix)
    {
        assert nodes.length <= 32;
        assert size <= DequeHelper.sizeForDepth(depth);
        this.depth = depth;
        this.size = size;
        this.prefix = prefix;
        this.nodes = nodes;
        this.suffix = suffix;
    }

    BranchNode(T prefixValue,
               Node<T> node)
    {
        this(node.getDepth() + 1,
             node.size() + 1,
             new LeafNode<>(prefixValue),
             DequeHelper.allocateSingleNode(node),
             EmptyNode.of());
        assert node.isFull();
    }

    BranchNode(Node<T> node,
               T suffixValue)
    {
        this(node.getDepth() + 1,
             node.size() + 1,
             EmptyNode.of(),
             DequeHelper.allocateSingleNode(node),
             new LeafNode<>(suffixValue));
        assert node.isFull();
    }

    static <T> Node<T> forNodeBuilder(int depth,
                                      int size,
                                      Node<T> prefix,
                                      Indexed<Node<T>> sourceNodes,
                                      int offset,
                                      int limit,
                                      Node<T> suffix)
    {
        assert limit >= offset;
        assert DequeHelper.allNodesFull(depth, sourceNodes, offset, limit);
        final Node<T>[] nodes = DequeHelper.allocateNodes(sourceNodes, offset, limit);
        return new BranchNode<>(depth, size, prefix, nodes, suffix);
    }

    static <T> BranchNode<T> forTesting(Node<T> prefix,
                                        Node<T>[] nodes,
                                        Node<T> suffix)
    {
        return new BranchNode<>(2,
                                prefix.size() + (nodes.length * 32) + suffix.size(),
                                prefix,
                                nodes.clone(),
                                suffix);
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public boolean isFull()
    {
        return size == DequeHelper.sizeForDepth(depth);
    }

    @Override
    public int iterableSize()
    {
        return size;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public int getDepth()
    {
        return depth;
    }

    private static <T> Node<T> forDelete(int size,
                                         Node<T> prefix,
                                         Node<T>[] nodes,
                                         Node<T> suffix)
    {
        if (nodes.length == 0) {
            if (prefix.isEmpty()) {
                return suffix;
            } else if (suffix.isEmpty()) {
                return prefix;
            } else {
                int depth = 1 + Math.max(prefix.getDepth(), suffix.getDepth());
                return new BranchNode<>(depth, size, prefix, nodes, suffix);
            }
        } else if ((nodes.length == 1) && prefix.isEmpty() && suffix.isEmpty()) {
            return nodes[0];
        } else {
            int depth = 1 + nodes[0].getDepth();
            return new BranchNode<>(depth, size, prefix, nodes, suffix);
        }
    }

    @Override
    public Node<T> deleteFirst()
    {
        if (!prefix.isEmpty()) {
            return forDelete(size - 1, prefix.deleteFirst(), nodes, suffix);
        }
        if (nodes.length > 0) {
            Node<T> newPrefix = nodes[0];
            Node<T>[] newNodes = DequeHelper.deleteFirst(nodes);
            return forDelete(size - 1, newPrefix.deleteFirst(), newNodes, suffix);
        }
        if (!suffix.isEmpty()) {
            return suffix.deleteFirst();
        }
        throw new IllegalStateException();
    }

    @Override
    public Node<T> deleteLast()
    {
        if (!suffix.isEmpty()) {
            return forDelete(size - 1, prefix, nodes, suffix.deleteLast());
        }
        if (nodes.length > 0) {
            Node<T> newSuffix = nodes[nodes.length - 1];
            Node<T>[] newNodes = DequeHelper.deleteLast(nodes);
            return forDelete(size - 1, prefix, newNodes, newSuffix.deleteLast());
        }
        if (!prefix.isEmpty()) {
            return prefix.deleteLast();
        }
        throw new IllegalStateException();
    }

    @Override
    public Node<T> insertFirst(T value)
    {
        if (isFull()) {
            return new BranchNode<>(value, this);
        }

        Node<T>[] newNodes;
        Node<T> newPrefix = prefix.insertFirst(value);
        if (newPrefix.getDepth() == (depth - 1) && newPrefix.isFull()) {
            newNodes = DequeHelper.insertFirst(nodes, newPrefix);
            newPrefix = EmptyNode.of();
        } else {
            newNodes = nodes;
        }
        return new BranchNode<>(depth, size + 1, newPrefix, newNodes, suffix);
    }

    @Override
    public Node<T> insertLast(T value)
    {
        if (isFull()) {
            return new BranchNode<>(this, value);
        }

        Node<T>[] newNodes;
        Node<T> newSuffix = suffix.insertLast(value);
        if (newSuffix.getDepth() == (depth - 1) && newSuffix.isFull()) {
            newNodes = DequeHelper.insertLast(nodes, newSuffix);
            newSuffix = EmptyNode.of();
        } else {
            newNodes = nodes;
        }
        return new BranchNode<>(depth, size + 1, prefix, newNodes, newSuffix);
    }

    @Override
    public boolean containsIndex(int index)
    {
        return (index >= 0) && (index < size);
    }

    @Override
    public T get(int index)
    {
        if (prefix.containsIndex(index)) {
            return prefix.get(index);
        }
        index -= prefix.size();
        final int fullNodeSize = DequeHelper.sizeForDepth(depth - 1);
        int arrayIndex = index / fullNodeSize;
        if (arrayIndex < nodes.length) {
            return nodes[arrayIndex].get(index - (arrayIndex * fullNodeSize));
        }
        index -= nodes.length * fullNodeSize;
        if (suffix.containsIndex(index)) {
            return suffix.get(index);
        }
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public Maybe<T> find(int index)
    {
        T value = get(index);
        return Maybe.of(value);
    }

    @Override
    public Node<T> assign(int index,
                          T value)
    {
        if (prefix.containsIndex(index)) {
            return new BranchNode<>(depth, size, prefix.assign(index, value), nodes, suffix);
        }
        index -= prefix.size();
        final int fullNodeSize = DequeHelper.sizeForDepth(depth - 1);
        int arrayIndex = index / fullNodeSize;
        if (arrayIndex < nodes.length) {
            int nodeIndexStart = arrayIndex * fullNodeSize;
            Node<T> newNode = nodes[arrayIndex].assign(index - nodeIndexStart, value);
            Node<T>[] newNodes = DequeHelper.assign(nodes, arrayIndex, newNode);
            return new BranchNode<>(depth, size, prefix, newNodes, suffix);
        }
        index -= nodes.length * fullNodeSize;
        if (suffix.containsIndex(index)) {
            return new BranchNode<>(depth, size, prefix, nodes, suffix.assign(index, value));
        }
        throw new IndexOutOfBoundsException();
    }

    @Nullable
    @Override
    public BranchNode<T> castAsBranch()
    {
        return this;
    }

    @Nonnull
    @Override
    public Node<T> prune()
    {
        if (suffix.isEmpty() && nodes.length == 0) {
            return prefix.prune();
        }
        if (prefix.isEmpty() && nodes.length == 0) {
            return suffix.prune();
        }
        return prefix.isEmpty() && suffix().isEmpty() && nodes.length == 1
               ? nodes[0].prune()
               : this;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return GenericIterator.multiIterableState(parent, indexedForIterator(), offset, limit);
    }

    @Nonnull
    private Indexed<Node<T>> indexedForIterator()
    {
        final int size = nodes.length + 2;
        final int last = size - 1;
        return new Indexed<Node<T>>()
        {
            @Override
            public Node<T> get(int index)
            {
                if (index == 0) {
                    return prefix;
                } else if (index == last) {
                    return suffix;
                } else {
                    return nodes[index - 1];
                }
            }

            @Nonnull
            @Override
            public Maybe<Node<T>> find(int index)
            {
                return Maybe.of(get(index));
            }

            @Override
            public int size()
            {
                return size;
            }
        };
    }

    @Override
    public int computedSize()
    {
        int computedSize = prefix.computedSize() + suffix.computedSize();
        for (Node<T> node : nodes) {
            computedSize += node.computedSize();
        }
        return computedSize;
    }

    @Override
    public void checkInvariants()
    {
        if (nodes.length > 32) {
            throw new IllegalStateException();
        }
        if ((nodes.length == 32) && !(prefix.isEmpty() && suffix.isEmpty())) {
            throw new IllegalStateException();
        }
        for (Node<T> node : nodes) {
            if ((node.getDepth() != (depth - 1)) || !node.isFull()) {
                throw new IllegalStateException();
            }
        }
        int computedSize = computedSize();
        if (computedSize != size) {
            throw new IllegalStateException();
        }
        if (prefix.isFull() && (prefix.getDepth() == (depth - 1))) {
            throw new IllegalStateException();
        }
        if (suffix.isFull() && (suffix.getDepth() == (depth - 1))) {
            throw new IllegalStateException();
        }

        prefix.checkInvariants();
        for (Node<T> node : nodes) {
            node.checkInvariants();
        }
        suffix.checkInvariants();
    }

    @Nonnull
    @Override
    public Node<T> reverse()
    {
        Node<T>[] newNodes = DequeHelper.allocateNodes(nodes.length);
        int to = newNodes.length;
        for (Node<T> node : nodes) {
            newNodes[--to] = node.reverse();
        }
        return new BranchNode<>(depth, size, suffix.reverse(), newNodes, prefix.reverse());
    }

    Node<T> prefix()
    {
        return prefix;
    }

    Indexed<Node<T>> filledNodes()
    {
        return IndexedArray.retained(nodes);
    }

    Node<T> suffix()
    {
        return suffix;
    }

    @Nonnull
    @Override
    public Node<T> prefix(int limit)
    {
        if (limit < 0 || limit > size) {
            throw new IndexOutOfBoundsException();
        }

        if (limit < prefix.size()) {
            return prefix.prefix(limit);
        }

        final int newSize = limit;
        limit -= prefix.size();
        final int fullNodeSize = DequeHelper.sizeForDepth(depth - 1);
        final int arrayIndex = limit / fullNodeSize;
        if (arrayIndex < nodes.length) {
            limit -= arrayIndex * fullNodeSize;
            Node<T>[] newNodes = DequeHelper.allocateNodes(IndexedArray.retained(nodes), 0, arrayIndex);
            Node<T> newSuffix = nodes[arrayIndex].prefix(limit);
            return new BranchNode<>(depth, newSize, prefix, newNodes, newSuffix).prune();
        }

        limit -= nodes.length * fullNodeSize;
        if (suffix.containsIndex(limit)) {
            return new BranchNode<>(depth, newSize, prefix, nodes, suffix.prefix(limit));
        }

        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public Node<T> suffix(int offset)
    {
        if (offset < 0 || offset > size) {
            throw new IndexOutOfBoundsException();
        }

        if (offset == 0) {
            return this;
        }

        if (offset == size) {
            return EmptyNode.of();
        }

        final int newSize = size - offset;
        if (offset <= prefix.size()) {
            return new BranchNode<>(depth, newSize, prefix.suffix(offset), nodes, suffix);
        }

        offset -= prefix.size();
        final int fullNodeSize = DequeHelper.sizeForDepth(depth - 1);
        final int arrayIndex = offset / fullNodeSize;
        if (arrayIndex < nodes.length) {
            offset -= arrayIndex * fullNodeSize;
            Node<T>[] newNodes = DequeHelper.allocateNodes(IndexedArray.retained(nodes), arrayIndex + 1, nodes.length);
            Node<T> newPrefix = nodes[arrayIndex].suffix(offset);
            return new BranchNode<>(depth, newSize, newPrefix, newNodes, suffix).prune();
        }

        offset -= nodes.length * fullNodeSize;
        return suffix.suffix(offset);
    }
}
