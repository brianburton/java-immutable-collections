package org.javimmutable.collections.tree_list;

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
        assert !left.isEmpty();
        assert !right.isEmpty();

        this.left = left;
        this.right = right;
        this.size = left.size() + right.size();
        this.depth = 1 + Math.max(left.depth(), right.depth());
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
            return new BranchNode<>(left, right);
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
        } else {
            return balance(left, right.insert(index - leftSize, value));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        final int leftSize = left.size();
        if (index < leftSize) {
            final AbstractNode<T> newLeft = left.delete(index);
            if (newLeft.isEmpty()) {
                return right;
            } else {
                return balance(newLeft, right);
            }
        } else {
            final AbstractNode<T> newRight = right.delete(index - leftSize);
            if (newRight.isEmpty()) {
                return left;
            } else {
                return balance(left, newRight);
            }
        }
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
            return new BranchNode<>(left, new BranchNode<>(right, parentRight));
        } else {
            return new BranchNode<>(new BranchNode<>(left, right.left()),
                                    new BranchNode<>(right.right(), parentRight));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> rotateLeft(AbstractNode<T> parentLeft)
    {
        if (left.depth() > right.depth()) {
            return new BranchNode<>(new BranchNode<>(parentLeft, left.left()),
                                    new BranchNode<>(left.right(), right));
        } else {
            return new BranchNode<>(new BranchNode<>(parentLeft, this.left), right);
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
}
