package org.javimmutable.collections.common;

import org.javimmutable.collections.JImmutableList;

import javax.annotation.Nonnull;
import java.util.Spliterator;

public abstract class AbstractJImmutableList<T>
    implements JImmutableList<T>
{
    @Override
    @Nonnull
    public Spliterator<T> spliterator()
    {
        return new CursorSpliterator<>(Spliterator.IMMUTABLE | Spliterator.ORDERED, cursor().start());
    }
}
