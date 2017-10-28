package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Streamable;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Spliterator;

public class SimpleStreamable<T>
    implements Streamable<T>
{
    private final int characteristics;
    private final Func0<SplitableIterator<T>> factory;

    private SimpleStreamable(int characteristics,
                             Func0<SplitableIterator<T>> factory)
    {
        this.characteristics = characteristics;
        this.factory = factory;
    }

    public static <T> Streamable<T> of(int characteristics,
                                       Func0<SplitableIterator<T>> factory)
    {
        return new SimpleStreamable<>(characteristics, factory);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator()
    {
        return factory.apply();
    }

    @Override
    public Spliterator<T> spliterator()
    {
        return factory.apply().spliterator(characteristics);
    }
}
