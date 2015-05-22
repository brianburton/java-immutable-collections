///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Node implementation containing other nodes.  Prefix and suffix nodes can contain nodes
 * of any depth and size (including empty) while body nodes array contains only full nodes
 * of exactly depth - 1.  This allows fast access to the body nodes by a computed offset
 * based on size but also quick adds and deletes from either end using the prefix and suffix
 * nodes.  Once a BranchNode reaches its theoretical limit based on depth any insert triggers
 * the creation of a new higher depth node containing the branch so that the new parent's prefix
 * or suffix can contain the new value.
 *
 * @param <T>
 */
@Deprecated
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
        assert size <= ListHelper.sizeForDepth(depth);
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
             new LeafNode<T>(prefixValue),
             ListHelper.allocateSingleNode(node),
             EmptyNode.<T>of());
        assert node.isFull();
    }

    BranchNode(Node<T> node,
               T suffixValue)
    {
        this(node.getDepth() + 1,
             node.size() + 1,
             EmptyNode.<T>of(),
             ListHelper.allocateSingleNode(node),
             new LeafNode<T>(suffixValue));
        assert node.isFull();
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

        List<Node<T>> nodes = new ArrayList<Node<T>>();
        int offset = 0;
        while (offset < nodeCount) {
            nodes.add(LeafNode.fromList(leaves, offset, Math.min(offset + 32, nodeCount)));
            offset += 32;
        }
        nodeCount = nodes.size();

        // loop invariant - all nodes except last one are always full
        // last one is possibly full
        int depth = 2;
        while (nodeCount > 1) {
            int dstOffset = 0;
            int srcOffset = 0;
            // fill all full nodes
            while (nodeCount > 32) {
                Node<T>[] newNodes = ListHelper.allocateNodes(32);
                for (int i = 0; i < 32; ++i) {
                    newNodes[i] = nodes.get(srcOffset++);
                }
                nodes.set(dstOffset++, new BranchNode<T>(depth, ListHelper.sizeForDepth(depth), EmptyNode.<T>of(), newNodes, EmptyNode.<T>of()));
                nodeCount -= 32;
            }
            // collect remaining nodes
            Node<T> lastNode = nodes.get(srcOffset + (nodeCount - 1));
            if ((lastNode.getDepth() == (depth - 1)) && lastNode.isFull()) {
                // all remaining nodes are full
                Node<T>[] newNodes = ListHelper.allocateNodes(nodeCount);
                for (int i = 0; i < newNodes.length; ++i) {
                    newNodes[i] = nodes.get(srcOffset++);
                }
                nodes.set(dstOffset++, new BranchNode<T>(depth, ListHelper.sizeForDepth(depth - 1) * newNodes.length, EmptyNode.<T>of(), newNodes, EmptyNode.<T>of()));
            } else {
                // all but last remaining nodes are full
                Node<T>[] newNodes = ListHelper.allocateNodes(nodeCount - 1);
                for (int i = 0; i < newNodes.length; ++i) {
                    newNodes[i] = nodes.get(srcOffset++);
                }
                nodes.set(dstOffset++, new BranchNode<T>(depth, (ListHelper.sizeForDepth(depth - 1) * newNodes.length) + lastNode.size(), EmptyNode.<T>of(), newNodes, lastNode));
            }
            nodeCount = dstOffset;
            depth += 1;
        }
        assert nodeCount == 1;
        return nodes.get(0);
    }

    static <T> Builder<T> builder()
    {
        return new Builder<T>();
    }

    static <T> BranchNode<T> forTesting(int depth,
                                        Node<T> prefix,
                                        Node<T>[] nodes,
                                        Node<T> suffix)
    {
        return new BranchNode<T>(depth,
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
        return size == ListHelper.sizeForDepth(depth);
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
                return new BranchNode<T>(depth, size, prefix, nodes, suffix);
            }
        } else if ((nodes.length == 1) && prefix.isEmpty() && suffix.isEmpty()) {
            return nodes[0];
        } else {
            int depth = 1 + nodes[0].getDepth();
            return new BranchNode<T>(depth, size, prefix, nodes, suffix);
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
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length - 1);
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
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length - 1);
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
            return new BranchNode<T>(value, this);
        }
        if (prefix.getDepth() < (depth - 1)) {
            return new BranchNode<T>(depth, size + 1, prefix.insertFirst(value), nodes, suffix);
        }
        assert prefix.getDepth() == (depth - 1);
        assert !prefix.isFull();
        Node<T>[] newNodes;
        Node<T> newPrefix = prefix.insertFirst(value);
        if (newPrefix.isFull()) {
            newNodes = ListHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 1, nodes.length);
            newNodes[0] = newPrefix;
            newPrefix = EmptyNode.of();
        } else {
            newNodes = nodes;
        }
        return new BranchNode<T>(depth, size + 1, newPrefix, newNodes, suffix);
    }

    @Override
    public Node<T> insertLast(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(this, value);
        }
        if (suffix.getDepth() < (depth - 1)) {
            return new BranchNode<T>(depth, size + 1, prefix, nodes, suffix.insertLast(value));
        }
        assert suffix.getDepth() == (depth - 1);
        assert !suffix.isFull();
        Node<T>[] newNodes;
        Node<T> newSuffix = suffix.insertLast(value);
        if (newSuffix.isFull()) {
            newNodes = ListHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            newNodes[nodes.length] = newSuffix;
            newSuffix = EmptyNode.of();
        } else {
            newNodes = nodes;
        }
        return new BranchNode<T>(depth, size + 1, prefix, newNodes, newSuffix);
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
        final int fullNodeSize = ListHelper.sizeForDepth(depth - 1);
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

    @Override
    public Node<T> assign(int index,
                          T value)
    {
        if (prefix.containsIndex(index)) {
            return new BranchNode<T>(depth, size, prefix.assign(index, value), nodes, suffix);
        }
        index -= prefix.size();
        final int fullNodeSize = ListHelper.sizeForDepth(depth - 1);
        int arrayIndex = index / fullNodeSize;
        if (arrayIndex < nodes.length) {
            Node<T>[] newNodes = nodes.clone();
            newNodes[arrayIndex] = nodes[arrayIndex].assign(index - (arrayIndex * fullNodeSize), value);
            return new BranchNode<T>(depth, size, prefix, newNodes, suffix);
        }
        index -= nodes.length * fullNodeSize;
        if (suffix.containsIndex(index)) {
            return new BranchNode<T>(depth, size, prefix, nodes, suffix.assign(index, value));
        }
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        MultiCursor.Builder<T> builder = MultiCursor.builder();
        builder = builder.add(LazyCursor.of(prefix));
        for (Node<T> node : nodes) {
            builder = builder.add(LazyCursor.of(node));
        }
        builder = builder.add(LazyCursor.of(suffix));
        return builder.build();
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
        int computedSize = prefix.size() + suffix.size();
        for (Node<T> node : nodes) {
            computedSize += node.size();
        }
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

    static class Builder<T>
            implements MutableBuilder<T, Node<T>>
    {
        private final List<T> leaves = new ArrayList<T>();

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            leaves.add(value);
            return this;
        }

        @Nonnull
        @Override
        public Node<T> build()
        {
            return of(IndexedList.retained(leaves));
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
    }
}
