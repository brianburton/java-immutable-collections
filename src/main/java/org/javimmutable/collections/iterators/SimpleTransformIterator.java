package org.javimmutable.collections.iterators;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.Function;

public class SimpleTransformIterator<S, T>
    implements Iterator<T>
{
    private final Iterator<S> source;
    private final Function<S, T> transforminator;

    public SimpleTransformIterator(@Nonnull Iterator<S> source,
                                   @Nonnull Function<S, T> transforminator)
    {
        this.source = source;
        this.transforminator = transforminator;
    }

    public static <S, T> Iterator<T> of(@Nonnull Iterator<S> source,
                                        @Nonnull Function<S, T> transforminator)
    {
        return new SimpleTransformIterator<>(source, transforminator);
    }

    @Override
    public boolean hasNext()
    {
        return source.hasNext();
    }

    @Override
    public T next()
    {
        return transforminator.apply(source.next());
    }
}
