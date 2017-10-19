package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.common.IteratorAdaptor;

import javax.annotation.Nonnull;
import java.util.Iterator;

public abstract class AbstractCursor<T>
    implements Cursor<T>
{
    @Nonnull
    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(this);
    }
}
