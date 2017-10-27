package org.javimmutable.collections.iterators;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;

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

    @Nonnull
    @Override
    public Spliterator<T> spliterator(int characteristics)
    {
        return Spliterators.emptySpliterator();
    }
}
