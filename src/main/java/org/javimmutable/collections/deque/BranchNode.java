///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;

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
    private final Node<T> prefix;  // possibly empty and can be any depth
    private final Node<T>[] nodes; // all of these are full and have depth - 1
    private final Node<T> suffix;  // possibly empty and can be any depth

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

    BranchNode(Node<T> node)
    {
        this(node.getDepth() + 1,
             node.size(),
             EmptyNode.of(),
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
        assert limit > offset;
        assert DequeHelper.allNodesFull(depth, sourceNodes, offset, limit);
        final Node<T>[] nodes = DequeHelper.allocateNodes(sourceNodes, offset, limit);
        return new BranchNode<>(depth, size, prefix, nodes, suffix);
    }

    static <T> Node<T> of(Indexed<? extends T> leaves)
    {
        int nodeCount = leaves.size();
        if (nodeCount == 0) {
            return EmptyNode.of();
        }

        if (nodeCount <= 32) {
            return LeafNode.fromList(leaves, 0, nodeCount);
        }

        final Node<T>[] nodes = DequeHelper.allocateNodes(1 + (leaves.size() / 32));
        int offset = 0;
        int index = 0;
        while (offset < nodeCount) {
            nodes[index++] = LeafNode.fromList(leaves, offset, Math.min(offset + 32, nodeCount));
            offset += 32;
        }
        nodeCount = index;

        // loop invariant - all nodes except last one are always full
        // last one is possibly full
        int depth = 2;
        while (nodeCount > 1) {
            int dstOffset = 0;
            int srcOffset = 0;
            // fill all full nodes
            while (nodeCount > 32) {
                final Node<T>[] newNodes = DequeHelper.allocateNodes(32);
                System.arraycopy(nodes, srcOffset, newNodes, 0, 32);
                nodes[dstOffset++] = new BranchNode<>(depth, DequeHelper.sizeForDepth(depth), EmptyNode.of(), newNodes, EmptyNode.of());
                srcOffset += 32;
                nodeCount -= 32;
            }
            // collect remaining nodes
            if (nodeCount == 1) {
                nodes[dstOffset++] = nodes[srcOffset];
            } else if (nodeCount > 1) {
                final Node<T> lastNode = nodes[srcOffset + nodeCount - 1];
                if ((lastNode.getDepth() == (depth - 1)) && lastNode.isFull()) {
                    // all remaining nodes are full
                    final Node<T>[] newNodes = DequeHelper.allocateNodes(nodeCount);
                    System.arraycopy(nodes, srcOffset, newNodes, 0, nodeCount);
                    nodes[dstOffset++] = new BranchNode<>(depth, DequeHelper.sizeForDepth(depth - 1) * nodeCount, EmptyNode.of(), newNodes, EmptyNode.of());
                } else {
                    // all but last remaining nodes are full
                    final int newNodesLength = nodeCount - 1;
                    final Node<T>[] newNodes = DequeHelper.allocateNodes(newNodesLength);
                    System.arraycopy(nodes, srcOffset, newNodes, 0, newNodesLength);
                    nodes[dstOffset++] = new BranchNode<>(depth, (DequeHelper.sizeForDepth(depth - 1) * newNodesLength) + lastNode.size(), EmptyNode.of(), newNodes, lastNode);
                }
            }
            nodeCount = dstOffset;
            depth += 1;
        }
        assert nodeCount == 1;
        return nodes[0];
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
            Node<T>[] newNodes = DequeHelper.allocateNodes(nodes.length - 1);
            System.arraycopy(nodes, 1, newNodes, 0, newNodes.length);
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
            Node<T>[] newNodes = DequeHelper.allocateNodes(nodes.length - 1);
            System.arraycopy(nodes, 0, newNodes, 0, newNodes.length);
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
        if (prefix.getDepth() < (depth - 1)) {
            return new BranchNode<>(depth, size + 1, prefix.insertFirst(value), nodes, suffix);
        }
        assert prefix.getDepth() == (depth - 1);
        assert !prefix.isFull();
        Node<T>[] newNodes;
        Node<T> newPrefix = prefix.insertFirst(value);
        if (newPrefix.isFull()) {
            newNodes = DequeHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 1, nodes.length);
            newNodes[0] = newPrefix;
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
        if (suffix.getDepth() < (depth - 1)) {
            return new BranchNode<>(depth, size + 1, prefix, nodes, suffix.insertLast(value));
        }
        assert suffix.getDepth() == (depth - 1);
        assert !suffix.isFull();
        Node<T>[] newNodes;
        Node<T> newSuffix = suffix.insertLast(value);
        if (newSuffix.isFull()) {
            newNodes = DequeHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            newNodes[nodes.length] = newSuffix;
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
    public Holder<T> find(int index)
    {
        return Holders.nullable(get(index));
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
            Node<T>[] newNodes = nodes.clone();
            newNodes[arrayIndex] = nodes[arrayIndex].assign(index - (arrayIndex * fullNodeSize), value);
            return new BranchNode<>(depth, size, prefix, newNodes, suffix);
        }
        index -= nodes.length * fullNodeSize;
        if (suffix.containsIndex(index)) {
            return new BranchNode<>(depth, size, prefix, nodes, suffix.assign(index, value));
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Efficiently add values from an Iterator to those contained in this BranchNode
     * to create a new BranchNode of a given maximum size.  This is done in two stages.
     * First the appropriate (based on insertion order) prefix/suffix is expanded until
     * it is full.  Then a builder is created from the full nodes of this branch and any
     * remaining values from the iterator are added to the builder.  Finally the original
     * prefix/suffix from the unexpanded side is added to the final result.
     */
    @Override
    public Node<T> insertAll(int maxSize,
                             boolean forwardOrder,
                             @Nonnull Iterator<? extends T> values)
    {
        assert maxSize >= size;
        assert DequeHelper.sizeForDepth(depth) >= size;
        if (size >= maxSize || !values.hasNext()) {
            return this;
        }
        final int fullSize = Math.min(DequeHelper.sizeForDepth(depth), maxSize);
        final int growthAllowed = fullSize - size;
        BranchNode<T> newNode;
        if (isFull()) {
            newNode = this;
        } else if (forwardOrder) {
            if (suffix.isEmpty()) {
                newNode = this;
            } else {
                final int maxSuffixSize = Math.min(DequeHelper.sizeForDepth(depth - 1), suffix.size() + growthAllowed);
                final Node<T> newSuffix = suffix.insertAll(maxSuffixSize, true, values);
                newNode = withSuffix(newSuffix);
            }
            if (values.hasNext() && newNode.size() < maxSize && !newNode.isFull()) {
                assert newNode.suffix.isEmpty();
                // expand on the filled nodes and then add our unused opposite prefix/suffix to the result
                newNode = TreeBuilder.expandBranchNode(fullSize - prefix.size(), true, newNode, values).withPrefix(prefix);
            }
        } else {
            if (prefix.isEmpty()) {
                newNode = this;
            } else {
                final int maxPrefixSize = Math.min(DequeHelper.sizeForDepth(depth - 1), prefix.size() + growthAllowed);
                final Node<T> newPrefix = prefix.insertAll(maxPrefixSize, false, values);
                newNode = withPrefix(newPrefix);
            }
            if (values.hasNext() && newNode.size() < maxSize && !newNode.isFull()) {
                assert newNode.prefix.isEmpty();
                // expand on the filled nodes and then add our unused opposite prefix/suffix to the result
                newNode = TreeBuilder.expandBranchNode(fullSize - suffix.size(), false, newNode, values).withSuffix(suffix);
            }
        }
        assert newNode.isFull() || newNode.size == maxSize || !values.hasNext();
        if (newNode.size() < maxSize && values.hasNext()) {
            // since we are already full we need to create a parent and expand that
            newNode = TreeBuilder.expandBranchNode(maxSize, forwardOrder, new BranchNode<>(newNode), values);
        }
        assert newNode.size() == maxSize || !values.hasNext();
        return newNode;
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
            public Holder<Node<T>> find(int index)
            {
                return Holders.nullable(get(index));
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

    private BranchNode<T> withPrefix(@Nonnull Node<T> newPrefix)
    {
        assert newPrefix.getDepth() < depth;
        final int baseSize = size - prefix.size();
        if (newPrefix.size() == DequeHelper.sizeForDepth(depth - 1)) {
            final Node<T>[] newNodes = DequeHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 1, nodes.length);
            newNodes[0] = newPrefix;
            return new BranchNode<T>(depth, baseSize + newPrefix.size(), EmptyNode.of(), newNodes, suffix);
        } else {
            return new BranchNode<T>(depth, baseSize + newPrefix.size(), newPrefix, nodes, suffix);
        }
    }

    private BranchNode<T> withSuffix(@Nonnull Node<T> newSuffix)
    {
        assert newSuffix.getDepth() < depth;
        final int baseSize = size - suffix.size();
        if (newSuffix.size() == DequeHelper.sizeForDepth(depth - 1)) {
            final Node<T>[] newNodes = DequeHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            newNodes[nodes.length] = newSuffix;
            return new BranchNode<T>(depth, baseSize + newSuffix.size(), prefix, newNodes, EmptyNode.of());
        } else {
            return new BranchNode<T>(depth, baseSize + newSuffix.size(), prefix, nodes, newSuffix);
        }
    }
}
