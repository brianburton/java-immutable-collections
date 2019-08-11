package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;

@ThreadSafe
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

    synchronized int size()
    {
        return size;
    }

    @Nonnull
    synchronized TreeBuilder<T> add(T value)
    {
        if (count == LeafNode.MAX_SIZE) {
            final AbstractNode<T> leaf = new LeafNode<>(buffer, count);
            if (parent == null) {
                parent = new BranchBuilder<>(leaf);
            } else {
                parent.add(leaf);
            }
            buffer[0] = value;
            count = 1;
        } else {
            buffer[count] = value;
            count += 1;
        }
        size += 1;
        return this;
    }

    @Nonnull
    synchronized AbstractNode<T> build()
    {
        if (count == 0) {
            return EmptyNode.instance();
        } else {
            AbstractNode<T> root = new LeafNode<>(buffer, count);
            if (parent != null) {
                root = parent.build(root);
            }
            return root;
        }
    }

    synchronized void checkInvariants()
    {
        if (size != computeSize()) {
            throw new IllegalStateException("size mismatch");
        }
        if (parent != null) {
            parent.checkInvariants();
        }
    }

    @Nonnull
    synchronized TreeBuilder<T> add(Cursor<? extends T> source)
    {
        for (source = source.start(); source.hasValue(); source = source.next()) {
            add(source.getValue());
        }
        return this;
    }

    @Nonnull
    synchronized TreeBuilder<T> add(Iterator<? extends T> source)
    {
        while (source.hasNext()) {
            add(source.next());
        }
        return this;
    }

    @Nonnull
    synchronized TreeBuilder<T> add(Iterable<? extends T> source)
    {
        return add(source.iterator());
    }

    @Nonnull
    synchronized <K extends T> TreeBuilder<T> add(K... source)
    {
        for (K k : source) {
            add(k);
        }
        return this;
    }

    @Nonnull
    synchronized TreeBuilder<T> add(Indexed<? extends T> source,
                                    int offset,
                                    int limit)
    {
        for (int i = 0; i < limit; ++i) {
            add(source.get(i));
        }
        return this;
    }

    @Nonnull
    synchronized TreeBuilder<T> add(Indexed<? extends T> source)
    {
        return add(source, 0, source.size());
    }

    /**
     * Clears any existing data in this builder and then populates the builder with
     * nodes from the provided tree.  At each level of the tree it creates a parent
     * branch using the left node and proceeds further using the right node.
     * At the leaf all values are copied into the buffer.
     */
    synchronized void rebuild(@Nonnull AbstractNode<T> node)
    {
        count = 0;
        parent = null;
        while (node.depth() > 0) {
            parent = new BranchBuilder<>(parent, node.left());
            node = node.right();
        }
        for (T t : node) {
            buffer[count++] = t;
        }
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIndexed(@Nonnull Indexed<? extends T> values)
    {
        return new TreeBuilder<T>().add(values).build();
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIterator(@Nonnull Iterator<? extends T> values)
    {
        return new TreeBuilder<T>().add(values).build();
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromCursor(@Nonnull Cursor<? extends T> values)
    {
        return new TreeBuilder<T>().add(values).build();
    }

    private int computeSize()
    {
        int answer = count;
        if (parent != null) {
            answer += parent.computeSize();
        }
        return answer;
    }

    @ThreadSafe
    private static class BranchBuilder<T>
    {
        private BranchBuilder<T> parent;
        private AbstractNode<T> left;
        private AbstractNode<T> right;

        private BranchBuilder(@Nonnull BranchBuilder<T> parent,
                              @Nonnull AbstractNode<T> left)
        {
            this.parent = parent;
            this.left = left;
        }

        private BranchBuilder(@Nonnull AbstractNode<T> left)
        {
            this.left = left;
        }

        private void add(@Nonnull AbstractNode<T> node)
        {
            if (right == null) {
                right = node;
            } else {
                final AbstractNode<T> branch = new BranchNode<>(left, right);
                if (parent == null) {
                    parent = new BranchBuilder<>(branch);
                } else {
                    parent.add(branch);
                }
                left = node;
                right = null;
            }
        }

        @Nonnull
        private AbstractNode<T> build(@Nonnull AbstractNode<T> extra)
        {
            AbstractNode<T> answer;
            if (right == null) {
                answer = left;
            } else {
                answer = new BranchNode<>(left, right);
            }
            if (parent != null) {
                answer = parent.build(answer);
            }
            answer = answer.append(extra);
            return answer;
        }

        private int computeSize()
        {
            int answer = left.size();
            if (right != null) {
                answer += right.size();
            }
            if (parent != null) {
                answer += parent.computeSize();
            }
            return answer;
        }

        private void checkInvariants()
        {
            if (left == null) {
                throw new IllegalStateException("left is null");
            }
            if (parent != null) {
                parent.checkInvariants();
            }
        }
    }
}
