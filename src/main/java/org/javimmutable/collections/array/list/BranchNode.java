package org.javimmutable.collections.array.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BranchNode<T>
        implements Node<T>
{
    private final int depth;
    private final int size;
    private final Node<T> prefix;
    private final Node<T>[] nodes; // all of these are full and have depth - 1
    private final Node<T> suffix;

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

    BranchNode(Node<T> node)
    {
        assert node.isFull();
        depth = node.getDepth() + 1;
        size = node.size();
        prefix = EmptyNode.of();
        nodes = ListHelper.allocateNodes(1);
        nodes[0] = node;
        suffix = EmptyNode.of();
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

    @Override
    public Node<T> deleteFirst()
    {
        if (!prefix.isEmpty()) {
            return new BranchNode<T>(depth, size - 1, prefix.deleteFirst(), nodes, suffix);
        }
        if (nodes.length > 0) {
            Node<T> newPrefix = nodes[0];
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length - 1);
            System.arraycopy(nodes, 1, newNodes, 0, newNodes.length);
            return new BranchNode<T>(depth, size - 1, newPrefix.deleteFirst(), newNodes, suffix);
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
            return new BranchNode<T>(depth, size - 1, prefix, nodes, suffix.deleteLast());
        }
        if (nodes.length > 0) {
            Node<T> newSuffix = nodes[nodes.length - 1];
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length - 1);
            System.arraycopy(nodes, 0, newNodes, 0, newNodes.length);
            return new BranchNode<T>(depth, size - 1, prefix, newNodes, newSuffix.deleteLast());
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
            // create a new parent containing us as a node and tell it to insert the value
            return new BranchNode<T>(this).insertFirst(value);
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
            // create a new parent containing us as a node and tell it to insert the value
            return new BranchNode<T>(this).insertLast(value);
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
    }
}
