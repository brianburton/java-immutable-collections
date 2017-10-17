package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

import javax.annotation.Nonnull;

public class ArrayCursor<T>
    extends AbstractStartedCursor<T>
{
    private final T[] values;
    private final int index;

    private ArrayCursor(T[] values,
                        int index)
    {
        this.values = values;
        this.index = index;
    }

    @Nonnull
    public static <T> Cursor<T> cursor(@Nonnull T[] source)
    {
        if (source.length == 0) {
            return StandardCursor.of();
        } else {
            return AbstractStartCursor.cursor(() -> new ArrayCursor<>(source, 0));
        }
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        final int nextIndex = index + 1;
        return (nextIndex < values.length) ? new ArrayCursor<T>(values, nextIndex) : super.next();
    }

    @Override
    public T getValue()
    {
        return values[index];
    }
}
