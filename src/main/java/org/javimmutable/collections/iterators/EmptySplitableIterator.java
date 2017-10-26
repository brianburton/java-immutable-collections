package org.javimmutable.collections.iterators;

import java.util.NoSuchElementException;

public class EmptySplitableIterator<T>
    implements SplitableIterator<T>
{
    private static final SplitableIterator INSTANCE = new EmptySplitableIterator();

    public static <T> SplitableIterator<T> of()
    {
        return (SplitableIterator<T>)INSTANCE;
    }

    @Override
    public boolean hasNext()
    {
        return false;
    }

    @Override
    public T next()
    {
        throw new NoSuchElementException();
    }
}
