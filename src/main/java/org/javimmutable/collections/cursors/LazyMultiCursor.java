package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.common.IndexedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    public static <T> Builder<T> builder()
    {
        return new Builder<T>();
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
        private final List<Cursorable<T>> sources = new ArrayList<Cursorable<T>>();

        @Nonnull
        public Builder<T> with(@Nullable Cursorable<T> source)
        {
            if (source != null) {
                sources.add(source);
            }
            return this;
        }

        @Nonnull
        public <C extends Cursorable<T>> Builder<T> with(Indexed<C> c)
        {
            sources.add(LazyMultiCursor.cursorable(c));
            return this;
        }

        @Nonnull
        public Cursor<T> cursor()
        {
            return LazyMultiCursor.cursor(IndexedList.copied(sources));
        }

        @Nonnull
        public Cursorable<T> cursorable()
        {
            return LazyMultiCursor.cursorable(IndexedList.copied(sources));
        }
    }
}
