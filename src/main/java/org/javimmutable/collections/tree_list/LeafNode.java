package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.IndexedIterator;

import javax.annotation.Nonnull;

class LeafNode<T>
    extends AbstractNode<T>
    implements ArrayHelper.Allocator<T>
{
    static final int MAX_SIZE = 48;

    private final T[] values;

    LeafNode(T value)
    {
        values = allocate(1);
        values[0] = value;
    }

    LeafNode(T[] values,
             int count)
    {
        assert count > 0;
        this.values = allocate(count);
        System.arraycopy(values, 0, this.values, 0, count);
    }

    private LeafNode(T[] values)
    {
        assert values.length > 0;
        this.values = values;
    }

    @Override
    boolean isEmpty()
    {
        return false;
    }

    @Override
    int size()
    {
        return values.length;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        return values[index];
    }

    @Override
    T first()
    {
        return values[0];
    }

    @Override
    T last()
    {
        return values[values.length - 1];
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        final int size = values.length;
        if (size < MAX_SIZE) {
            return new LeafNode<>(ArrayHelper.append(this, values, value));
        } else {
            final T[] right = allocate(1);
            right[0] = value;
            return new BranchNode<>(this, new LeafNode<>(right));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        } else if (node.depth() > 0) {
            return node.prepend(this);
        } else {
            final LeafNode<T> other = (LeafNode<T>)node;
            final int combinedSize = size() + other.size();
            if (combinedSize < MAX_SIZE) {
                return new LeafNode<>(ArrayHelper.concat(this, values, other.values));
            }
            return new BranchNode<>(this, node);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        final int size = values.length;
        if (size < MAX_SIZE) {
            return new LeafNode<>(ArrayHelper.insert(this, values, 0, value));
        } else {
            final T[] left = allocate(1);
            left[0] = value;
            return new BranchNode<>(new LeafNode<>(left), this);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        } else if (node.depth() > 0) {
            return node.append(this);
        } else {
            final LeafNode<T> other = (LeafNode<T>)node;
            final int combinedSize = size() + other.size();
            if (combinedSize < MAX_SIZE) {
                return new LeafNode<>(ArrayHelper.concat(this, other.values, values));
            }
            return new BranchNode<>(node, this);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> set(int index,
                        T value)
    {
        return new LeafNode<>(ArrayHelper.assign(values, index, value));
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        final int size = values.length;
        if (index == 0) {
            return prepend(value);
        } else if (index == size) {
            return append(value);
        } else if (size < MAX_SIZE) {
            return new LeafNode<>(ArrayHelper.insert(this, values, index, value));
        } else {
            T[] left = ArrayHelper.subArray(this, values, 0, index);
            T[] right = allocate(size - index + 1);
            right[0] = value;
            System.arraycopy(values, index, right, 1, size - index);
            return new BranchNode<>(new LeafNode<>(left), new LeafNode<>(right));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        if (values.length == 1) {
            ArrayHelper.checkBounds(values, index);
            return EmptyNode.instance();
        } else {
            return new LeafNode<>(ArrayHelper.delete(this, values, index));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        return delete(0);
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        return delete(values.length - 1);
    }

    @Nonnull
    @Override
    AbstractNode<T> head(int limit)
    {
        if (limit == 0) {
            return EmptyNode.instance();
        } else if (limit == values.length) {
            return this;
        } else {
            return new LeafNode<>(ArrayHelper.subArray(this, values, 0, limit));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> tail(int offset)
    {
        if (offset == 0) {
            return this;
        } else if (offset == values.length) {
            return EmptyNode.instance();
        } else {
            return new LeafNode<>(ArrayHelper.subArray(this, values, offset, values.length));
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T[] allocate(int size)
    {
        if (size < 1) {
            throw new IllegalArgumentException();
        }
        return (T[])new Object[size];
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of(IndexedArray.retained(values));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return IndexedIterator.iterator(IndexedArray.retained(values));
    }
}
