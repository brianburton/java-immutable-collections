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
    public Node<T> deleteFirst()
    {
        throw new IllegalStateException();
    }

    @Override
    public Node<T> deleteLast()
    {
        throw new IllegalStateException();
    }

    @Override
    public Node<T> insertFirst(T value)
    {
        return new LeafNode<T>(value);
    }

    @Override
    public Node<T> insertLast(T value)
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

    @Override
    public void checkInvariants()
    {
    }
}
