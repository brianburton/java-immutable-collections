package org.javimmutable.collections.iterators;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public class TransformIterator<S, T>
    extends AbstractSplitableIterator<T>
{
    private final Function<S, T> transforminator;
    private final SplitableIterator<S> source;

    private TransformIterator(@Nonnull SplitableIterator<S> source,
                              @Nonnull Function<S, T> transforminator)
    {
        this.transforminator = transforminator;
        this.source = source;
    }

    public static <S, T> TransformIterator<S, T> of(@Nonnull SplitableIterator<S> source,
                                                    @Nonnull Function<S, T> transforminator)
    {
        return new TransformIterator<>(source, transforminator);
    }

    public static <K, V> SplitableIterator<K> ofKeys(@Nonnull SplitableIterator<JImmutableMap.Entry<K, V>> source)
    {
        return of(source, e -> e.getKey());
    }

    public static <K, V> SplitableIterator<V> ofValues(@Nonnull SplitableIterator<JImmutableMap.Entry<K, V>> source)
    {
        return of(source, e -> e.getValue());
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

    @Override
    public boolean isSplitAllowed()
    {
        return source.isSplitAllowed();
    }

    @Nonnull
    @Override
    public SplitIterator<T> splitIterator()
    {
        final SplitIterator<S> split = source.splitIterator();
        return new SplitIterator<>(new TransformIterator<>(split.getLeft(), transforminator),
                                   new TransformIterator<>(split.getRight(), transforminator));
    }
}