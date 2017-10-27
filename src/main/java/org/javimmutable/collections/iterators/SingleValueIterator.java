package org.javimmutable.collections.iterators;

import java.util.NoSuchElementException;

public class SingleValueIterator<T>
    extends AbstractSplitableIterator<T>
{
    private final T value;
    private boolean started;

    private SingleValueIterator(T value)
    {
        this.value = value;
    }

    public static <T> SingleValueIterator<T> iterator(T value)
    {
        return new SingleValueIterator<>(value);
    }

    @Override
    public boolean hasNext()
    {
        return !started;
    }

    @Override
    public T next()
    {
        if (started) {
            throw new NoSuchElementException();
        }
        started = true;
        return value;
    }
}
