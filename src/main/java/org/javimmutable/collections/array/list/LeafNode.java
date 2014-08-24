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
    @Nonnull
    private final T[] values;

    private LeafNode(@Nonnull T[] values)
    {
        assert values.length > 0;
        assert values.length <= 32;
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
    public Node<T> deleteFirst()
    {
        if (values.length == 1) {
            return EmptyNode.of();
        }
        T[] newValues = ListHelper.allocateValues(values.length - 1);
        System.arraycopy(values, 1, newValues, 0, newValues.length);
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> deleteLast()
    {
        if (values.length == 1) {
            return EmptyNode.of();
        }
        T[] newValues = ListHelper.allocateValues(values.length - 1);
        System.arraycopy(values, 0, newValues, 0, newValues.length);
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> insertFirst(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(value, this);
        }
        T[] newValues = ListHelper.allocateValues(values.length + 1);
        System.arraycopy(values, 0, newValues, 1, values.length);
        newValues[0] = value;
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> insertLast(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(this, value);
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

    @Override
    public void checkInvariants()
    {
        if ((values.length == 0) || (values.length > 32)) {
            throw new IllegalStateException();
        }
    }
}
