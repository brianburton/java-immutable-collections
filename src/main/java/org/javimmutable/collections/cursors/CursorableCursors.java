package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;

public class CursorableCursors<T, C extends Cursorable<T>>
    implements Cursorable<Cursorable<T>>
{
    private final Indexed<C> sources;

    private CursorableCursors(@Nonnull Indexed<C> sources)
    {
        this.sources = sources;
    }

    @Nonnull
    @Override
    public Cursor<Cursorable<T>> cursor()
    {
        return StandardCursor.of(new CursorableSource(0));
    }

    @Nonnull
    public static <T, C extends Cursorable<T>> Cursorable<Cursorable<T>> of(@Nonnull Indexed<C> sources)
    {
        return new CursorableCursors<T, C>(sources);
    }

    private class CursorableSource
        implements StandardCursor.Source<Cursorable<T>>
    {
        private final int index;

        private CursorableSource(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index >= sources.size();
        }

        @Override
        public Cursorable<T> currentValue()
        {
            return sources.get(index);
        }

        @Override
        public StandardCursor.Source<Cursorable<T>> advance()
        {
            return new CursorableSource(index + 1);
        }
    }
}
