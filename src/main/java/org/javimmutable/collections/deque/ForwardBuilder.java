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

import org.javimmutable.collections.IDeque;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A builder for efficiently building {@link IDeque}s.  Also supports efficiently
 * appending values to an existing {@link IDeque}.  When constructed with an existing
 * deque it retains nearly all of its structure and just constructs new nodes needed
 * to add new values to the end of the deque.
 */
@NotThreadSafe
class ForwardBuilder<T>
    implements InvariantCheckable
{
    private final Leaf<T> leaf;  // first node (depth 1 leaf) in the chain

    private ForwardBuilder(@Nonnull Leaf<T> leaf)
    {
        this.leaf = leaf;
    }

    void add(T value)
    {
        leaf.add(value);
    }

    void addAll(Indexed<? extends T> values)
    {
        for (int i = 0; i < values.size(); ++i) {
            add(values.get(i));
        }
    }

    void addAll(Iterator<? extends T> values)
    {
        while (values.hasNext()) {
            add(values.next());
        }
    }

    Node<T> build()
    {
        return leaf.toNode().prune();
    }

    int size()
    {
        return leaf.totalSize();
    }

    void clear()
    {
        leaf.clear();
    }

    @Override
    public void checkInvariants()
    {
        leaf.checkInvariants();
    }

    /**
     * Deconstructs the base node to construct a list of builders that add new elements
     * to the end of the list.
     *
     * @param root the node the builder will prepend data to
     * @return the builder
     */
    static <T> ForwardBuilder<T> insertAtEnd(Node<T> root)
    {
        if (root.getDepth() == 1) {
            if (root.isFull()) {
                Branch<T> branch = new Branch<>(2, IndexedHelper.indexed(root), EmptyNode.of(), null);
                Leaf<T> leaf = new Leaf<>(IndexedHelper.empty(), branch);
                return new ForwardBuilder<>(leaf);
            } else {
                LeafNode<T> leafNode = root.castAsLeaf();
                Indexed<T> values = leafNode != null ? leafNode.values() : IndexedHelper.empty();
                Leaf<T> leaf = new Leaf<>(values, null);
                return new ForwardBuilder<>(leaf);
            }
        }

        BranchNode<T> branchNode = root.castAsBranch();
        assert branchNode != null;
        if (branchNode.isFull()) {
            Branch<T> branch = new Branch<>(branchNode.getDepth() + 1, IndexedHelper.indexed(branchNode), EmptyNode.of(), null);
            Leaf<T> leaf = new Leaf<>(IndexedHelper.empty(), addMissingBranches(1, branch));
            return new ForwardBuilder<>(leaf);
        }

        Branch<T> next = new Branch<>(branchNode.getDepth(), branchNode.filledNodes(), branchNode.prefix(), null);
        Node<T> suffix = branchNode.suffix();
        while (suffix.getDepth() > 1) {
            next = addMissingBranches(suffix.getDepth(), next);
            branchNode = suffix.castAsBranch();
            assert branchNode != null;
            if (suffix.isFull()) {
                next.add(suffix);
            } else {
                next = new Branch<>(branchNode.getDepth(),
                                    branchNode.filledNodes(),
                                    branchNode.prefix(),
                                    next);
            }
            suffix = branchNode.suffix();
        }

        next = addMissingBranches(1, next);
        if (suffix.isFull()) {
            next.add(suffix);
            suffix = EmptyNode.of();
        }
        LeafNode<T> leafNode = suffix.castAsLeaf();
        Indexed<T> values = leafNode != null ? leafNode.values() : IndexedHelper.empty();
        return new ForwardBuilder<>(new Leaf<>(values, next));
    }

    private static <T> Branch<T> addMissingBranches(int nodeDepth,
                                                    Branch<T> next)
    {
        final int requiredDepth = nodeDepth + 1;
        while (next.depth > requiredDepth) {
            next = new Branch<>(next.depth - 1, next);
        }
        return next;
    }

    private static class Leaf<T>
    {
        private final T[] values;
        private Branch<T> next;
        private int length;
        private int capacity;

        private Leaf(Indexed<T> values,
                     @Nullable Branch<T> next)
        {
            assert next == null || next.depth == 2;

            this.values = DequeHelper.allocateValues(32);
            length = values.size();
            capacity = (next != null)
                       ? Math.min(32, next.capacity) - length
                       : 32 - length;
            for (int i = 0; i < length; ++i) {
                this.values[i] = values.get(i);
            }
            this.next = next;

            assert capacity > 0;
        }

        private void add(T value)
        {
            assert capacity > 0;
            values[length++] = value;
            capacity -= 1;
            if (capacity == 0) {
                push();
            }
            assert capacity > 0;
        }

        private int totalSize()
        {
            int answer = length;
            if (next != null) {
                answer += next.totalSize();
            }
            return answer;
        }

        private void clear()
        {
            Arrays.fill(values, null);
            next = null;
            length = 0;
            capacity = 32;
        }

        private void push()
        {
            Node<T> newNode = LeafNode.fromList(IndexedArray.retained(values), 0, length);
            if (next == null) {
                next = new Branch<>(2, null);
            }
            next.add(newNode);

            length = 0;
            capacity = Math.min(next.capacity, 32);
            Arrays.fill(values, null);
        }

        private Node<T> toNode()
        {
            Node<T> leafNode = length == 0 ? EmptyNode.of() : LeafNode.fromList(IndexedArray.retained(values), 0, length);
            return next == null ? leafNode : next.toNode(leafNode);
        }

        private void checkInvariants()
        {
            if (next != null) {
                next.checkInvariants();
                if (next.depth != 2) {
                    throw new IllegalStateException();
                }
            }

            for (int i = length; i < values.length; ++i) {
                if (values[i] != null) {
                    throw new IllegalStateException();
                }
            }
        }
    }

    private static class Branch<T>
    {
        private final Node<T>[] nodes;
        private final int depth;
        private Branch<T> next;
        private Node<T> prefix;
        private int length;
        private int capacity;
        private int size;

        private Branch(int depth,
                       @Nullable Branch<T> next)
        {
            assert next == null || next.depth == depth + 1;

            this.nodes = DequeHelper.allocateNodes(32);
            this.depth = depth;
            this.next = next;
            prefix = EmptyNode.of();
            capacity = (next != null)
                       ? Math.min(next.capacity, DequeHelper.sizeForDepth(depth))
                       : DequeHelper.sizeForDepth(depth);
            assert capacity >= 0;
        }

        private Branch(int depth,
                       @Nonnull Indexed<Node<T>> startNodes,
                       @Nonnull Node<T> prefix,
                       @Nullable Branch<T> next)
        {
            assert startNodes.size() == 0 || startNodes.get(0).getDepth() < depth;
            assert next == null || next.depth == depth + 1;

            this.nodes = DequeHelper.allocateNodes(32);
            this.depth = depth;
            this.next = next;
            this.prefix = prefix;
            capacity = (next != null)
                       ? Math.min(next.capacity, DequeHelper.sizeForDepth(depth))
                       : DequeHelper.sizeForDepth(depth);

            length = startNodes.size();
            size = prefix.size();
            for (int i = 0; i < length; ++i) {
                Node<T> node = startNodes.get(i);
                nodes[i] = node;
                size += node.size();
            }
            capacity -= size;
            assert capacity >= 0;
        }

        private void add(Node<T> node)
        {
            assert capacity > 0;
            assert length < nodes.length;
            assert node.getDepth() < depth;
            assert node.size() <= capacity;
            assert node.isFull() || node.size() == capacity;

            Node<T> suffix = EmptyNode.of();
            if (node.isFull()) {
                nodes[length++] = node;
            } else if (node.size() == capacity) {
                suffix = node;
            } else {
                throw new AssertionError("node.isFull() || node.size() == capacity");
            }
            capacity -= node.size();
            size += node.size();
            if (capacity == 0) {
                push(node, suffix);
            }
            assert capacity > 0;
        }

        private void push(Node<T> node,
                          Node<T> suffix)
        {
            Node<T> newNode =
                size == node.size()
                ? node
                : BranchNode.forNodeBuilder(depth, size, prefix,
                                            IndexedArray.retained(nodes),
                                            0, length, suffix);
            if (next == null) {
                next = new Branch<>(depth + 1, null);
            }
            next.add(newNode);
            prefix = EmptyNode.of();
            length = 0;
            size = 0;
            capacity = Math.min(next.capacity, DequeHelper.sizeForDepth(depth));
            Arrays.fill(nodes, null);
        }

        private int totalSize()
        {
            int answer = size;
            if (next != null) {
                answer += next.totalSize();
            }
            return answer;
        }

        private Node<T> toNode(Node<T> suffix)
        {
            assert suffix.size() < capacity;

            if (size > 0) {
                if (prefix.isEmpty() && suffix.isEmpty() && length == 1) {
                    suffix = nodes[0];
                } else {
                    suffix = BranchNode.forNodeBuilder(depth,
                                                       size + suffix.size(),
                                                       prefix,
                                                       IndexedArray.retained(nodes), 0, length,
                                                       suffix);
                }
            }
            if (next == null) {
                return suffix;
            } else {
                return next.toNode(suffix);
            }
        }

        private void checkInvariants()
        {
            if (next != null) {
                next.checkInvariants();
                if (next.depth != depth + 1) {
                    throw new IllegalStateException();
                }
            }

            if (capacity <= 0) {
                throw new IllegalStateException();
            }

            if (prefix == null) {
                throw new IllegalStateException();
            }

            // we should always maintain a proper size and length
            int computedSize = prefix.size();
            for (int i = 0; i < length; ++i) {
                Node<T> node = nodes[i];
                node.checkInvariants();

                if (node.getDepth() != depth - 1) {
                    throw new IllegalStateException();
                }
                computedSize += node.size();
            }
            if (computedSize != size) {
                throw new IllegalStateException();
            }

            for (int i = length; i < nodes.length; ++i) {
                if (nodes[i] != null) {
                    throw new IllegalStateException();
                }
            }

            int computedCapacity = DequeHelper.sizeForDepth(depth) - computedSize;
            if (next != null) {
                computedCapacity = Math.min(computedCapacity, next.capacity);
            }
            if (computedCapacity != capacity) {
                throw new IllegalStateException();
            }
        }
    }
}
