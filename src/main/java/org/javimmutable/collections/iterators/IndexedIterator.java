package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class IndexedIterator<T>
    extends AbstractSplitableIterator<T>
{
    @Nonnull
    private final Indexed<T> values;
    private final int limit;
    private int index;

    private IndexedIterator(@Nonnull Indexed<T> values,
                            int index,
                            int limit)
    {
        this.values = values;
        this.limit = limit;
        this.index = index;
    }

    public static <T> IndexedIterator<T> iterator(@Nonnull Indexed<T> values)
    {
        return new IndexedIterator<>(values, -1, values.size() - 1);
    }

    @Override
    public boolean hasNext()
    {
        return index < limit;
    }

    @Override
    public T next()
    {
        if (index >= limit) {
            throw new NoSuchElementException();
        }
        index += 1;
        return values.get(index);
    }

    @Override
    public boolean isSplitAllowed()
    {
        return (limit - index) > 1;
    }

    @Nonnull
    @Override
    public SplitIterator<T> splitIterator()
    {
        final int splitIndex = index + (limit - index) / 2;
        if (splitIndex == index) {
            throw new UnsupportedOperationException();
        }
        return new SplitIterator<>(new IndexedIterator<>(values, index, splitIndex),
                                   new IndexedIterator<>(values, splitIndex, limit));
    }
}
