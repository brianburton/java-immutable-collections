package org.javimmutable.collections.array.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;

import javax.annotation.Nonnull;

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
        this.depth = depth;
        this.size = size;
        this.prefix = prefix;
        this.nodes = nodes;
        this.suffix = suffix;
    }

    BranchNode(Node<T> node)
    {
        if (!node.isFull()) {
            throw new IllegalStateException();
        }
        depth = node.getDepth() + 1;
        size = node.size();
        prefix = new LeafNode<T>();
        nodes = ListHelper.allocateNodes(1);
        nodes[0] = node;
        suffix = new LeafNode<T>();
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
    public TakeValueResult<T> takeFirstValue()
    {
        if (!prefix.isEmpty()) {
            TakeValueResult<T> rc = prefix.takeFirstValue();
            return new TakeValueResult<T>(rc.getValue(), new BranchNode<T>(depth, size - 1, rc.getRoot(), nodes, suffix));
        }
        if (nodes.length > 0) {
            Node<T> newPrefix = nodes[0];
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length - 1);
            System.arraycopy(nodes, 1, newNodes, 0, newNodes.length);
            TakeValueResult<T> rc = newPrefix.takeFirstValue();
            return new TakeValueResult<T>(rc.getValue(), new BranchNode<T>(depth, size - 1, rc.getRoot(), newNodes, suffix));
        }
        if (!suffix.isEmpty()) {
            TakeValueResult<T> rc = suffix.takeFirstValue();
            return new TakeValueResult<T>(rc.getValue(), new BranchNode<T>(depth, size - 1, prefix, nodes, rc.getRoot()));
        }
        throw new IllegalStateException();
    }

    @Override
    public TakeValueResult<T> takeLastValue()
    {
        if (!suffix.isEmpty()) {
            TakeValueResult<T> rc = suffix.takeLastValue();
            return new TakeValueResult<T>(rc.getValue(), new BranchNode<T>(depth, size - 1, prefix, nodes, rc.getRoot()));
        }
        if (nodes.length > 0) {
            Node<T> newSuffix = nodes[nodes.length - 1];
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length - 1);
            System.arraycopy(nodes, 0, newNodes, 0, newNodes.length);
            TakeValueResult<T> rc = newSuffix.takeLastValue();
            return new TakeValueResult<T>(rc.getValue(), new BranchNode<T>(depth, size - 1, prefix, newNodes, rc.getRoot()));
        }
        if (!prefix.isEmpty()) {
            TakeValueResult<T> rc = prefix.takeLastValue();
            return new TakeValueResult<T>(rc.getValue(), new BranchNode<T>(depth, size - 1, rc.getRoot(), nodes, suffix));
        }
        throw new IllegalStateException();
    }

    @Override
    public Node<T> insertFirstValue(T value)
    {
        if (isFull()) {
            // create a new parent containing us as a node and tell it to insert the value
            return new BranchNode<T>(this).insertFirstValue(value);
        }
        if (prefix.isFull()) {
            if (prefix.getDepth() < (depth - 1)) {
                return new BranchNode<T>(depth, size + 1, prefix.insertFirstValue(value), nodes, suffix);
            }
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 1, nodes.length);
            newNodes[0] = prefix;
            return new BranchNode<T>(depth, size + 1, new LeafNode<T>(value), newNodes, suffix);
        }
        return new BranchNode<T>(depth, size + 1, prefix.insertFirstValue(value), nodes, suffix);
    }

    @Override
    public Node<T> insertLastValue(T value)
    {
        if (isFull()) {
            // create a new parent containing us as a node and tell it to insert the value
            return new BranchNode<T>(this).insertLastValue(value);
        }
        if (suffix.isFull()) {
            if (suffix.getDepth() < (depth - 1)) {
                return new BranchNode<T>(depth, size + 1, prefix, nodes, suffix.insertLastValue(value));
            }
            Node<T>[] newNodes = ListHelper.allocateNodes(nodes.length + 1);
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            newNodes[nodes.length] = suffix;
            return new BranchNode<T>(depth, size + 1, prefix, newNodes, new LeafNode<T>(value));
        }
        return new BranchNode<T>(depth, size + 1, prefix, nodes, suffix.insertLastValue(value));
    }


    @Override
    public boolean containsIndex(int index)
    {
        return (index >= 0) && (index < size);
    }

    @Override
    public T getValue(int index)
    {
        if (prefix.containsIndex(index)) {
            return prefix.getValue(index);
        }
        index -= prefix.size();
        final int fullNodeSize = ListHelper.sizeForDepth(depth - 1);
        int arrayIndex = index / fullNodeSize;
        if (arrayIndex < nodes.length) {
            return nodes[arrayIndex].getValue(index - (arrayIndex * fullNodeSize));
        }
        index -= nodes.length * fullNodeSize;
        if (suffix.containsIndex(index)) {
            return suffix.getValue(index);
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
}
