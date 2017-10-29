package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Streamable;

import javax.annotation.Nonnull;
import java.util.Spliterator;

/**
 * Streamable implementation that creates Streams using Cursors provided by a factory method.
 */
public class CursorStreamable<T>
    implements Streamable<T>
{
    private final int characteristics;
    private final Func0<Cursor<T>> cursorFactory;

    public CursorStreamable(int characteristics,
                            Func0<Cursor<T>> cursorFactory)
    {
        this.characteristics = characteristics;
        this.cursorFactory = cursorFactory;
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return IteratorAdaptor.of(cursorFactory.apply());
    }

    @Override
    public Spliterator<T> spliterator()
    {
        return new CursorSpliterator<>(characteristics, cursorFactory.apply());
    }
}
