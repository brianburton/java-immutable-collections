package org.javimmutable.collections.array.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Node that forms the bottom of the tree and contains up to 32 values.
 *
 * @param <T>
 */
@Immutable
public class LeafNode<T>
        implements Node<T>
{
    private final T[] values;

    private LeafNode(T[] values)
    {
        this.values = values;
    }

    public LeafNode(T value)
    {
        values = ListHelper.allocateValues(1);
        values[0] = value;
    }

    @Override
    public boolean isEmpty()
    {
        return values.length == 0;
    }

    @Override
    public boolean isFull()
    {
        return values.length == 32;
    }

    @Override
    public int size()
    {
        return values.length;
    }

    @Override
    public int getDepth()
    {
        return 1;
    }

    @Override
    public TakeValueResult<T> takeFirstValue()
    {
        if (values.length == 0) {
            throw new IllegalStateException();
        }
        if (values.length == 1) {
            return new TakeValueResult<T>(values[0], EmptyNode.<T>of());
        }
        T[] newValues = ListHelper.allocateValues(values.length - 1);
        System.arraycopy(values, 1, newValues, 0, newValues.length);
        return new TakeValueResult<T>(values[0], new LeafNode<T>(newValues));
    }

    @Override
    public TakeValueResult<T> takeLastValue()
    {
        if (values.length == 0) {
            throw new IllegalStateException();
        }
        if (values.length == 1) {
            return new TakeValueResult<T>(values[0], EmptyNode.<T>of());
        }
        T[] newValues = ListHelper.allocateValues(values.length - 1);
        System.arraycopy(values, 0, newValues, 0, newValues.length);
        return new TakeValueResult<T>(values[values.length - 1], new LeafNode<T>(newValues));
    }

    @Override
    public Node<T> insertFirstValue(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(this).insertFirstValue(value);
        }
        T[] newValues = ListHelper.allocateValues(values.length + 1);
        System.arraycopy(values, 0, newValues, 1, values.length);
        newValues[0] = value;
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> insertLastValue(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(this).insertLastValue(value);
        }
        T[] newValues = ListHelper.allocateValues(values.length + 1);
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[values.length] = value;
        return new LeafNode<T>(newValues);
    }

    @Override
    public boolean containsIndex(int index)
    {
        return (index >= 0) && (index < values.length);
    }

    @Override
    public T get(int index)
    {
        return values[index];
    }

    @Override
    public Node<T> assign(int index,
                          T value)
    {
        T[] newValues = values.clone();
        newValues[index] = value;
        return new LeafNode<T>(newValues);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of(IndexedArray.retained(values));
    }
}
