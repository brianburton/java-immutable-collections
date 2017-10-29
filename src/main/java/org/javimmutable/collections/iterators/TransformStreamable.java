package org.javimmutable.collections.iterators;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

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

    @Nonnull
    @Override
    public Spliterator<T> spliterator()
    {
        return new SpliteratorImpl(source.spliterator());
    }

    @Nonnull
    @Override
    public Stream<T> stream()
    {
        return source.stream().map(transforminator);
    }

    @Nonnull
    @Override
    public Stream<T> parallelStream()
    {
        return source.parallelStream().map(transforminator);
    }

    private class SpliteratorImpl
        implements Spliterator<T>
    {
        private final Spliterator<S> source;

        private SpliteratorImpl(Spliterator<S> source)
        {
            this.source = source;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action)
        {
            return source.tryAdvance(value -> action.accept(transforminator.apply(value)));
        }

        @Override
        public Spliterator<T> trySplit()
        {
            final Spliterator<S> split = source.trySplit();
            return split == null ? null : new SpliteratorImpl(split);
        }

        @Override
        public long estimateSize()
        {
            return source.estimateSize();
        }

        @Override
        public int characteristics()
        {
            return source.characteristics();
        }
    }
}
