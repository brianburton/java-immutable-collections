package org.javimmutable.collections.array.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyNode<T>
        implements Node<T>
{
    private static final EmptyNode INSTANCE = new EmptyNode();

    @SuppressWarnings("unchecked")
    public static <T> EmptyNode<T> of()
    {
        return (EmptyNode<T>)INSTANCE;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean isFull()
    {
        return false;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public int getDepth()
    {
        return 1;
    }

    @Override
    public TakeValueResult<T> takeFirstValue()
    {
        throw new IllegalStateException();
    }

    @Override
    public TakeValueResult<T> takeLastValue()
    {
        throw new IllegalStateException();
    }

    @Override
    public Node<T> insertFirstValue(T value)
    {
        return new LeafNode<T>(value);
    }

    @Override
    public Node<T> insertLastValue(T value)
    {
        return new LeafNode<T>(value);
    }

    @Override
    public boolean containsIndex(int index)
    {
        return false;
    }

    @Override
    public T get(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Node<T> assign(int index,
                          T value)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of();
    }
}
