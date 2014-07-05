package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;

import javax.annotation.Nonnull;

/**
 * Cursorable implementation that creates an IterableCursor.
 * Intended for use in unit tests only.
 */
public class IterableCursorable<T>
        implements Cursorable<T>
{
    private final Iterable<T> iterable;

    private IterableCursorable(Iterable<T> iterable)
    {
        this.iterable = iterable;
    }

    public static <T> IterableCursorable<T> of(Iterable<T> iterable)
    {
        return new IterableCursorable<T>(iterable);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return IterableCursor.of(iterable);
    }
}
