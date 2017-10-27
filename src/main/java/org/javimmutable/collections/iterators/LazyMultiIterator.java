package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;

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
