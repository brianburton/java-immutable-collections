package org.javimmutable.collections.iterators;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class TransformStreamable<S, T>
    implements IterableStreamable<T>
{
    private final IterableStreamable<S> source;
    private final Function<S, T> transforminator;

    private TransformStreamable(IterableStreamable<S> source,
                                Function<S, T> transforminator)
    {
        this.source = source;
        this.transforminator = transforminator;
    }

    public static <S, T> IterableStreamable<T> of(@Nonnull IterableStreamable<S> source,
                                                  @Nonnull Function<S, T> transforminator)
    {
        return new TransformStreamable<>(source, transforminator);
    }

    public static <K, V> IterableStreamable<K> ofKeys(@Nonnull IterableStreamable<JImmutableMap.Entry<K, V>> source)
    {
        return of(source, JImmutableMap.Entry::getKey);
    }

    public static <K, V> IterableStreamable<V> ofValues(@Nonnull IterableStreamable<JImmutableMap.Entry<K, V>> source)
    {
        return of(source, JImmutableMap.Entry::getValue);
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return TransformIterator.of(source.iterator(), transforminator);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return source.getSpliteratorCharacteristics();
    }
}
