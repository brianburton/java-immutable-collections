package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.common.IndexedArray;

import javax.annotation.Nonnull;

public class LazyMultiCursor<T>
    extends AbstractStartedCursor<T>
{
    @Nonnull
    private final Cursor<Cursorable<T>> source;
    @Nonnull
    private final Cursor<T> cursor;

    private LazyMultiCursor(@Nonnull Cursor<Cursorable<T>> source,
                            @Nonnull Cursor<T> cursor)
    {
        this.source = source;
        this.cursor = cursor;
    }

    public static <T> Builder<T> builder(int size)
    {
        return new Builder<T>(size);
    }

    public static <T> Cursor<T> cursor(@Nonnull final Cursor<Cursorable<T>> sources)
    {
        return new AbstractStartCursor<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> next()
            {
                return advance(sources);
            }
        };
    }

    public static <T, C extends Cursorable<T>> Cursor<T> cursor(@Nonnull final Indexed<C> sources)
    {
        return cursor(CursorableCursors.of(sources).cursor());
    }

    public static <T> Cursorable<T> cursorable(@Nonnull final Cursor<Cursorable<T>> sources)
    {
        return new Cursorable<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> cursor()
            {
                return LazyMultiCursor.cursor(sources);
            }
        };
    }

    public static <T, C extends Cursorable<T>> Cursorable<T> cursorable(@Nonnull final Indexed<C> sources)
    {
        return new Cursorable<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> cursor()
            {
                return LazyMultiCursor.cursor(sources);
            }
        };
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        if (cursor.hasValue()) {
            Cursor<T> nextCursor = cursor.next();
            if (nextCursor.hasValue()) {
                return new LazyMultiCursor<T>(source, nextCursor);
            }
        }
        return advance(source);
    }

    @Override
    public boolean hasValue()
    {
        return cursor.hasValue();
    }

    @Override
    public T getValue()
    {
        return cursor.getValue();
    }

    @Nonnull
    private static <T> Cursor<T> advance(@Nonnull Cursor<Cursorable<T>> source)
    {
        Cursor<Cursorable<T>> nextSource = source.next();
        while (nextSource.hasValue()) {
            Cursor<T> nextCursor = nextSource.getValue().cursor().start();
            if (nextCursor.hasValue()) {
                return new LazyMultiCursor<T>(nextSource, nextCursor);
            }
            nextSource = nextSource.next();
        }
        return EmptyStartedCursor.of();
    }

    public static class Builder<T>
    {
        private final Cursorable<T>[] sources;
        private int nextIndex = 0;

        @SuppressWarnings("unchecked")
        private Builder(int size)
        {
            sources = (Cursorable<T>[])new Cursorable[size];
            this.nextIndex = 0;
        }

        @Nonnull
        public Builder<T> insert(@Nonnull Cursorable<T> source)
        {
            sources[nextIndex++] = source;
            return this;
        }

        @Nonnull
        public <C extends Cursorable<T>> Builder<T> insert(@Nonnull Indexed<C> c)
        {
            sources[nextIndex++] = LazyMultiCursor.cursorable(c);
            return this;
        }

        @Nonnull
        public Cursor<T> cursor()
        {
            fillArray();
            return LazyMultiCursor.cursor(IndexedArray.retained(sources));
        }

        @Nonnull
        public Cursorable<T> cursorable()
        {
            fillArray();
            return LazyMultiCursor.cursorable(IndexedArray.retained(sources));
        }

        private void fillArray()
        {
            while (nextIndex < sources.length) {
                insert(StandardCursor.<T>emptyCursorable());
            }
        }
    }
}
