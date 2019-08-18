package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NotThreadSafe
class TreeBuilder<T>
{
    private final T[] buffer;
    private int count;
    private int size;
    private BranchBuilder<T> parent;

    @SuppressWarnings("unchecked")
    TreeBuilder()
    {
        buffer = (T[])new Object[LeafNode.MAX_SIZE];
    }

    @Nonnull
    AbstractNode<T> build()
    {
        AbstractNode<T> answer;
        if (count > 0) {
            answer = new LeafNode<>(buffer, count);
        } else {
            answer = EmptyNode.instance();
        }
        if (parent != null) {
            answer = parent.build(answer);
        }
        return answer;
    }

    int size()
    {
        return size;
    }

    void combineWith(@Nonnull TreeBuilder<T> other)
    {
        final AbstractNode<T> a = build();
        final AbstractNode<T> b = other.build();
        final AbstractNode<T> ab = a.append(b);
        rebuild(ab);
    }

    /**
     * Clears any existing data in this builder and then populates the builder with
     * nodes from the provided tree.  At each level of the tree it creates a parent
     * branch using the left node and proceeds further using the right node.
     * At the leaf all values are copied into the buffer.
     */
    void rebuild(@Nonnull AbstractNode<T> node)
    {
        count = 0;
        size = node.size();
        parent = null;
        while (node.depth() > 0) {
            parent = new BranchBuilder<>(parent, node.left());
            node = node.right();
        }
        for (T t : node) {
            buffer[count++] = t;
        }
    }

    void add(T value)
    {
        buffer[count++] = value;
        if (count == LeafNode.MAX_SIZE) {
            final AbstractNode<T> leaf = new LeafNode<>(buffer, count);
            if (parent == null) {
                parent = new BranchBuilder<>(leaf);
            } else {
                parent.add(leaf);
            }
            count = 0;
        }
        size += 1;
    }

    void add(Cursor<? extends T> source)
    {
        for (source = source.start(); source.hasValue(); source = source.next()) {
            add(source.getValue());
        }
    }

    void add(Iterator<? extends T> source)
    {
        while (source.hasNext()) {
            add(source.next());
        }
    }

    void add(Iterable<? extends T> source)
    {
        add(source.iterator());
    }

    @SafeVarargs
    final <K extends T> void add(K... source)
    {
        for (K k : source) {
            add(k);
        }
    }

    void add(Indexed<? extends T> source,
             int offset,
             int limit)
    {
        for (int i = offset; i < limit; ++i) {
            add(source.get(i));
        }
    }

    void add(Indexed<? extends T> source)
    {
        add(source, 0, source.size());
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIndexed(@Nonnull Indexed<? extends T> source)
    {
        return nodeFromIndexed(source, 0, source.size());
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIndexed(@Nonnull Indexed<? extends T> source,
                                               int offset,
                                               int limit)
    {
        final int sourceSize = limit - offset;
        if (sourceSize == 0) {
            return EmptyNode.instance();
        }

        final List<AbstractNode<T>> nodes = new ArrayList<>(1 + sourceSize / LeafNode.MAX_SIZE);
        int o = offset;
        while (o < limit) {
            final int nodeSize = Math.min(LeafNode.MAX_SIZE, limit - o);
            nodes.add(new LeafNode<>(source.subArray(o, o + nodeSize), nodeSize));
            o += nodeSize;
        }
        int nodeCount = nodes.size();
        while (nodeCount > 1) {
            int writeIndex = 0;
            int readIndex = 0;
            int remaining = nodeCount;
            while (remaining > 0) {
                if (remaining > 1) {
                    nodes.set(writeIndex, BranchNode.balance(nodes.get(readIndex), nodes.get(readIndex + 1)));
                    readIndex += 2;
                    writeIndex += 1;
                    remaining -= 2;
                } else {
                    nodes.set(writeIndex - 1, nodes.get(writeIndex - 1).append(nodes.get(readIndex)));
                    readIndex += 1;
                    remaining -= 1;
                }
            }
            nodeCount = writeIndex;
        }
        return nodes.get(0);
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIterator(@Nonnull Iterator<? extends T> values)
    {
        TreeBuilder<T> builder = new TreeBuilder<>();
        builder.add(values);
        return builder.build();
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromCursor(@Nonnull Cursor<? extends T> values)
    {
        TreeBuilder<T> builder = new TreeBuilder<>();
        builder.add(values);
        return builder.build();
    }

    void checkInvariants()
    {
        if (size != computeSize()) {
            throw new IllegalStateException("size mismatch");
        }
        if (parent != null) {
            parent.checkInvariants();
        }
    }

    private int computeSize()
    {
        int answer = count;
        if (parent != null) {
            answer += parent.computeSize();
        }
        return answer;
    }

    private static class BranchBuilder<T>
    {
        private BranchBuilder<T> parent;
        private AbstractNode<T> buffer;

        private BranchBuilder(@Nonnull BranchBuilder<T> parent,
                              @Nonnull AbstractNode<T> node)
        {
            this.parent = parent;
            buffer = node;
        }

        private BranchBuilder(@Nonnull AbstractNode<T> node)
        {
            buffer = node;
        }

        private void add(@Nonnull AbstractNode<T> node)
        {
//            assert node.size() == (1 << node.depth()) * LeafNode.MAX_SIZE;
            if (buffer == null) {
                buffer = node;
            } else {
                final AbstractNode<T> branch = new BranchNode<>(buffer, node);
                if (parent == null) {
                    parent = new BranchBuilder<>(branch);
                } else {
                    parent.add(branch);
                }
                buffer = null;
            }
        }

        @Nonnull
        private AbstractNode<T> build(@Nonnull AbstractNode<T> extra)
        {
            AbstractNode<T> answer;
            if (buffer == null) {
                answer = extra;
            } else {
                answer = buffer.append(extra);
            }
            if (parent != null) {
                answer = parent.build(answer);
            }
            return answer;
        }

        private int computeSize()
        {
            int answer = 0;
            if (buffer != null) {
                answer += buffer.size();
            }
            if (parent != null) {
                answer += parent.computeSize();
            }
            return answer;
        }

        private void checkInvariants()
        {
            if (buffer == null && parent == null) {
                throw new IllegalStateException("buffer is null");
            }
            if (parent != null) {
                parent.checkInvariants();
            }
        }
    }
}
