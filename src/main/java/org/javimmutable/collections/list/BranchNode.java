package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.StringJoiner;

@Immutable
class BranchNode<T>
    extends AbstractNode<T>
{
    private final AbstractNode<T> left;
    private final AbstractNode<T> right;
    private final int size;
    private final int depth;

    BranchNode(@Nonnull AbstractNode<T> left,
               @Nonnull AbstractNode<T> right)
    {
        this(left, right, left.size() + right.size());
    }

    BranchNode(@Nonnull AbstractNode<T> left,
               @Nonnull AbstractNode<T> right,
               int size)
    {
        assert !left.isEmpty();
        assert !right.isEmpty();

        this.left = left;
        this.right = right;
        this.size = size;
        this.depth = 1 + Math.max(left.depth(), right.depth());
        assert size > LeafNode.MAX_SIZE;
    }

    /**
     * Low level build a new node from the specified child nodes.
     * Assumes that the two nodes are already in balance.  If the
     * size of the resulting node is small enough a leaf is return.
     * Otherwise a branch is returned.
     */
    @Nonnull
    private static <T> AbstractNode<T> join(@Nonnull AbstractNode<T> left,
                                            @Nonnull AbstractNode<T> right)
    {
        final int size = left.size() + right.size();
        if (size <= LeafNode.MAX_SIZE) {
            return new LeafNode<>(left, right, size);
        } else {
            return new BranchNode<>(left, right, size);
        }
    }

    /**
     * Build a new node from the specified child nodes.  Performs rotations if necessary to ensure the tree
     * remains in balance (depths of two child branches stay within 1 of each other).
     */
    @Nonnull
    static <T> AbstractNode<T> balance(@Nonnull AbstractNode<T> left,
                                       @Nonnull AbstractNode<T> right)
    {
        final int diff = left.depth() - right.depth();
        if (diff > 1) {
            return left.rotateRight(right);
        } else if (diff < -1) {
            return right.rotateLeft(left);
        } else {
            return join(left, right);
        }
    }

    @Override
    boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    int size()
    {
        return size;
    }

    @Override
    int depth()
    {
        return depth;
    }

    @Override
    T get(int index)
    {
        final int leftSize = left.size();
        if (index < leftSize) {
            return left.get(index);
        } else {
            return right.get(index - leftSize);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        return balance(left, right.append(value));
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        }
        final int diff = depth - node.depth();
        if (diff < 0) {
            return node.prepend(this);
        } else if (diff <= 1) {
            return new BranchNode<>(this, node);
        } else {
            return balance(left, right.append(node));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        return balance(left.prepend(value), right);
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        }
        final int diff = depth - node.depth();
        if (diff < 0) {
            return node.append(this);
        } else if (diff <= 1) {
            return new BranchNode<>(node, this);
        } else {
            return balance(left.prepend(node), right);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> assign(int index,
                           T value)
    {
        final int leftSize = left.size();
        if (index < leftSize) {
            return new BranchNode<>(left.assign(index, value), right);
        } else {
            return new BranchNode<>(left, right.assign(index - leftSize, value));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        final int leftSize = left.size();
        if (index < leftSize) {
            return balance(left.insert(index, value), right);
        } else if (index == leftSize && leftSize <= right.size()) {
            return balance(left.insert(index, value), right);
        } else {
            return balance(left, right.insert(index - leftSize, value));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        final int leftSize = left.size();
        final AbstractNode<T> newLeft, newRight;
        if (index < leftSize) {
            newLeft = left.delete(index);
            newRight = right;
            if (newLeft.isEmpty()) {
                return right;
            }
        } else {
            newLeft = left;
            newRight = right.delete(index - leftSize);
            if (newRight.isEmpty()) {
                return left;
            }
        }
        return balance(newLeft, newRight);
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        final AbstractNode<T> newLeft = left.deleteFirst();
        if (newLeft.isEmpty()) {
            return right;
        } else {
            return balance(newLeft, right);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        final AbstractNode<T> newRight = right.deleteLast();
        if (newRight.isEmpty()) {
            return left;
        } else {
            return balance(left, newRight);
        }
    }

    @Override
    void copyTo(T[] array,
                int offset)
    {
        left.copyTo(array, offset);
        right.copyTo(array, offset + left.size());
    }

    @Nonnull
    @Override
    AbstractNode<T> prefix(int limit)
    {
        if (limit == size) {
            return this;
        } else if (limit == 0) {
            return EmptyNode.instance();
        } else {
            final int leftSize = left.size();
            if (limit <= leftSize) {
                return left.prefix(limit);
            } else {
                return left.append(right.prefix(limit - leftSize));
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> suffix(int offset)
    {
        if (offset == 0) {
            return this;
        } else if (offset == size) {
            return EmptyNode.instance();
        } else {
            final int leftSize = left.size();
            if (offset < leftSize) {
                return left.suffix(offset).append(right);
            } else {
                return right.suffix(offset - leftSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> left()
    {
        return left;
    }

    @Nonnull
    @Override
    AbstractNode<T> right()
    {
        return right;
    }

    @Nonnull
    @Override
    AbstractNode<T> rotateRight(AbstractNode<T> parentRight)
    {
        if (left.depth() >= right.depth()) {
            return join(left, join(right, parentRight));
        } else {
            return join(join(left, right.left()), join(right.right(), parentRight));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> rotateLeft(AbstractNode<T> parentLeft)
    {
        if (left.depth() > right.depth()) {
            return join(join(parentLeft, left.left()), join(left.right(), right));
        } else {
            return join(join(parentLeft, this.left), right);
        }
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return LazyMultiCursor.cursor(IndexedHelper.indexed(left, right));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return new GenericIterator<>(this, iterateOverRange(null, 0, size), 0, size);
    }

    @Override
    public void checkInvariants()
    {
        if (depth != Math.max(left.depth(), right.depth()) + 1) {
            throw new RuntimeException(String.format("incorrect depth: depth=%d leftDepth=%d rightDepth=%d", depth, left.depth(), right.depth()));
        }
        if (Math.abs(left.depth() - right.depth()) > 1) {
            throw new RuntimeException(String.format("invalid child depths: leftDepth=%d rightDepth=%d", left.depth(), right.depth()));
        }
        if (size != left.size() + right.size()) {
            throw new RuntimeException(String.format("incorrect size: size=%d leftSize=%d rightSize=%d", size, left.size(), right.size()));
        }
        if (size <= LeafNode.MAX_SIZE) {
            throw new RuntimeException(String.format("invalid size: size=%d leftSize=%d rightSize=%d", size, left.size(), right.size()));
        }
        if (left.isEmpty() || right.isEmpty()) {
            throw new RuntimeException(String.format("branch node has an empty branch: leftSize=%d rightSize=%d", left.size(), right.size()));
        }
        left.checkInvariants();
        right.checkInvariants();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BranchNode<?> that = (BranchNode<?>)o;

        if (size != that.size) {
            return false;
        }
        if (depth != that.depth) {
            return false;
        }
        if (!left.equals(that.left)) {
            return false;
        }
        return right.equals(that.right);
    }

    @Override
    public int hashCode()
    {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        result = 31 * result + size;
        result = 31 * result + depth;
        return result;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", BranchNode.class.getSimpleName() + "[", "]")
            .add("left=" + left)
            .add("right=" + right)
            .add("size=" + size)
            .add("depth=" + depth)
            .toString();
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        if (offset < 0 || limit > size || offset > limit) {
            throw new IndexOutOfBoundsException();
        }
        final int leftSize = left.size();
        if (limit <= leftSize) {
            return left.iterateOverRange(parent, offset, limit);
        } else if (offset >= leftSize) {
            return right.iterateOverRange(parent, offset - leftSize, limit - leftSize);
        } else {
            return left.iterateOverRange(new IteratorState(parent, limit - leftSize), offset, leftSize);
        }
    }

    // state object to resume iteration down right branch from start to limit (limit relative to right branch)
    class IteratorState
        implements GenericIterator.State<T>
    {
        private final GenericIterator.State<T> parent;
        private final int limit;

        private IteratorState(@Nullable GenericIterator.State<T> parent,
                              int limit)
        {
            this.parent = parent;
            this.limit = limit;
        }

        @Override
        public T value()
        {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public GenericIterator.State<T> advance()
        {
            return right.iterateOverRange(parent, 0, limit);
        }
    }
}
