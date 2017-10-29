package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class LazyMultiIterator<T>
    extends AbstractSplitableIterator<T>
{
    @Nonnull
    private final SplitableIterator<SplitableIterable<T>> source;
    @Nullable
    private SplitableIterator<T> iterator;
    private boolean advanced;
    private boolean hasNext;
    private T nextValue;

    private LazyMultiIterator(@Nonnull SplitableIterator<SplitableIterable<T>> source,
                              @Nullable SplitableIterator<T> iterator,
                              boolean advanced,
                              boolean hasNext,
                              T nextValue)
    {
        this.source = source;
        this.iterator = iterator;
        this.advanced = advanced;
        this.hasNext = hasNext;
        this.nextValue = nextValue;
    }

    @Nonnull
    public static <T> LazyMultiIterator<T> iterator(@Nonnull Indexed<SplitableIterable<T>> source)
    {
        return iterator(IndexedIterator.iterator(source));
    }

    @Nonnull
    public static <T> LazyMultiIterator<T> iterator(@Nonnull SplitableIterator<SplitableIterable<T>> source)
    {
        return new LazyMultiIterator<>(source, null, false, false, null);
    }

    /**
     * Constructs an iterator that visits all of the values reachable from all of the
     * SplitableIterables visited by source.  All values are transformed using the provided method.
     */
    public static <S, T> LazyMultiIterator<T> transformed(SplitableIterator<S> source,
                                                          Function<S, SplitableIterable<T>> transforminator)
    {
        return iterator(TransformIterator.of(source, transforminator));
    }

    /**
     * Constructs an iterator that visits all of the values reachable from all of the
     * SplitableIterables visited by source.  All values are transformed using the provided method.
     */
    public static <S, T> LazyMultiIterator<T> transformed(Indexed<S> source,
                                                          Function<S, SplitableIterable<T>> transforminator)
    {
        return iterator(TransformIterator.of(IndexedIterator.iterator(source), transforminator));
    }

    @Override
    public boolean hasNext()
    {
        advance();
        return hasNext;
    }

    @Override
    public T next()
    {
        advance();
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        advanced = false;
        return nextValue;
    }

    private void advance()
    {
        if (!advanced) {
            advanceImpl();
            advanced = true;
        }
    }

    private void advanceImpl()
    {
        if (iterator != null) {
            if (iterator.hasNext()) {
                hasNext = true;
                nextValue = iterator.next();
                return;
            }
        }

        assert (iterator == null) || !iterator.hasNext();

        while (source.hasNext()) {
            final SplitableIterable<T> nextIterable = source.next();
            final SplitableIterator<T> nextIterator = nextIterable.iterator();
            if (nextIterator.hasNext()) {
                iterator = nextIterator;
                hasNext = true;
                nextValue = iterator.next();
                return;
            }
        }

        hasNext = false;
    }
}
