package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitCursor;

import javax.annotation.Nonnull;

public class ArrayCursor<T>
    extends AbstractStartedCursor<T>
{
    private final T[] values;
    private final int index;
    private final int limit;

    private ArrayCursor(T[] values,
                        int index,
                        int limit)
    {
        this.values = values;
        this.index = index;
        this.limit = limit;
    }

    @Nonnull
    public static <T> Cursor<T> cursor(@Nonnull T[] source)
    {
        if (source.length == 0) {
            return StandardCursor.of();
        } else {
            return AbstractStartCursor.cursor(() -> new ArrayCursor<>(source, 0, source.length));
        }
    }

    @Nonnull
    private static <T> Cursor<T> cursor(@Nonnull T[] source,
                                        int index,
                                        int limit)
    {
        if (index >= limit) {
            return StandardCursor.of();
        } else {
            return AbstractStartCursor.cursor(() -> new ArrayCursor<>(source, index, limit));
        }
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        final int nextIndex = index + 1;
        return (nextIndex < limit) ? new ArrayCursor<T>(values, nextIndex, limit) : super.next();
    }

    @Override
    public T getValue()
    {
        return values[index];
    }

    @Override
    public boolean isSplitAllowed()
    {
        return (limit - index) > 1;
    }

    @Override
    public SplitCursor<T> splitCursor()
    {
        final int splitIndex = index + (limit - index) / 2;
        if (splitIndex == index) {
            throw new UnsupportedOperationException();
        }
        return new SplitCursor<>(cursor(values, index, splitIndex), cursor(values, splitIndex, limit));
    }
}
