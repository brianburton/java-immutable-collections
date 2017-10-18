package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Tuple2;

import javax.annotation.Nonnull;

public class ArrayCursor<T>
{
    private ArrayCursor()
    {
    }

    @Nonnull
    public static <T> Cursor<T> cursor(@Nonnull T[] source)
    {
        return cursor(source, 0, source.length);
    }

    @Nonnull
    private static <T> Cursor<T> cursor(@Nonnull T[] source,
                                        int index,
                                        int limit)
    {
        return StandardCursor.of(new ArraySource<>(source, index, limit));
    }

    private static class ArraySource<T>
        implements StandardCursor.Source<T>
    {
        private final T[] values;
        private final int index;
        private final int limit;

        private ArraySource(T[] values,
                            int index,
                            int limit)
        {
            this.values = values;
            this.index = index;
            this.limit = limit;
        }

        @Override
        public boolean atEnd()
        {
            return index >= limit;
        }

        @Override
        public T currentValue()
        {
            return values[index];
        }

        @Override
        public StandardCursor.Source<T> advance()
        {
            return new ArraySource<>(values, index + 1, limit);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return (limit - index) > 1;
        }

        @Override
        public Tuple2<StandardCursor.Source<T>, StandardCursor.Source<T>> splitSource()
        {
            final int splitIndex = index + (limit - index) / 2;
            if (splitIndex == index) {
                throw new UnsupportedOperationException();
            }
            return Tuple2.of(new ArraySource<>(values, index, splitIndex),
                             new ArraySource<>(values, splitIndex, limit));
        }
    }
}
