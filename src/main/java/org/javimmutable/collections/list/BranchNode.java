package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.LazyMultiIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

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

    private BranchNode(@Nonnull AbstractNode<T> left,
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

    @Nonnull
    static <T> AbstractNode<T> join(@Nonnull AbstractNode<T> left,
                                    @Nonnull AbstractNode<T> right)
    {
        final int size = left.size() + right.size();
        if (size <= LeafNode.MAX_SIZE) {
            return new LeafNode<>(left, right, size);
        } else {
            return new BranchNode<>(left, right, size);
        }
    }

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
        return false;
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

    @Override
    T first()
    {
        return left.first();
    }

    @Override
    T last()
    {
        return right.last();
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
    AbstractNode<T> set(int index,
                        T value)
    {
        final int leftSize = left.size();
        if (index < leftSize) {
            return new BranchNode<>(left.set(index, value), right);
        } else {
            return new BranchNode<>(left, right.set(index - leftSize, value));
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
    AbstractNode<T> head(int limit)
    {
        final int leftSize = left.size();
        if (limit < leftSize) {
            return left.head(limit);
        } else {
            return left.append(right.head(limit - leftSize));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> tail(int offset)
    {
        final int leftSize = left.size();
        if (offset < leftSize) {
            return left.tail(offset).append(right);
        } else {
            return right.tail(offset - leftSize);
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
        return LazyMultiIterator.iterator(IndexedHelper.indexed(left, right));
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
}
