package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.JImmutableRandomAccessList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class JImmutableTreeListBuilder<T>
    implements JImmutableRandomAccessList.Builder<T>
{
    private final T[] buffer;
    private int count;
    private int size;
    private BranchBuilder<T> parent;

    @SuppressWarnings("unchecked")
    public JImmutableTreeListBuilder()
    {
        buffer = (T[])new Object[LeafNode.MAX_SIZE];
    }

    @Override
    public synchronized int size()
    {
        return size;
    }

    @Nonnull
    @Override
    public synchronized JImmutableTreeListBuilder<T> add(T value)
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
    @Override
    public synchronized JImmutableTreeList<T> build()
    {
        return JImmutableTreeList.create(buildRoot());
    }

    @Nonnull
    public synchronized AbstractNode<T> buildRoot()
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

    public synchronized void checkInvariants()
    {
        if (size != computeSize()) {
            throw new IllegalStateException("size mismatch");
        }
        if (parent != null) {
            parent.checkInvariants();
        }
    }

    /**
     * Clears any existing data in this builder and then populates the builder with
     * nodes from the provided tree.  At each level of the tree it creates a parent
     * branch using the left node and proceeds further using the right node.
     * At the leaf all values are copied into the buffer.
     */
    public synchronized void rebuild(@Nonnull AbstractNode<T> node)
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
    public JImmutableTreeListBuilder<T> combineWith(@Nonnull JImmutableTreeListBuilder<T> other)
    {
        final AbstractNode<T> a = buildRoot();
        final AbstractNode<T> b = other.buildRoot();
        final AbstractNode<T> ab = a.append(b);
        rebuild(ab);
        return this;
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
