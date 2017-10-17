package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitCursor;

import javax.annotation.Nonnull;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CursorSpliterator<T>
    implements Spliterator<T>
{
    private final int characteristics;
    private Cursor<T> cursor;

    public CursorSpliterator(int characteristics,
                             @Nonnull Cursor<T> cursor)
    {
        this.characteristics = characteristics;
        this.cursor = cursor.start();
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action)
    {
        if (action == null) {
            throw new NullPointerException();
        }
        if (cursor.hasValue()) {
            action.accept(cursor.getValue());
            cursor = cursor.next();
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit()
    {
        if (cursor.isSplitAllowed()) {
            SplitCursor<T> split = cursor.splitCursor();
            cursor = split.getRight().start();
            return new CursorSpliterator<>(characteristics, split.getLeft());
        }
        return null;
    }

    @Override
    public long estimateSize()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics()
    {
        return characteristics;
    }
}
