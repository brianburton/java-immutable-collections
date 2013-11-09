package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

/**
 * Base class for started (i.e. actively traversing the collection) Cursors.
 * Derived classes usually only need to override next() and getValue().
 * Once the end of the traversal is reached returning super.next() will
 * end the traversal.
 *
 * @param <T>
 */
public abstract class AbstractStartedCursor<T>
        implements Cursor<T>
{
    @Override
    public Cursor<T> next()
    {
        return EmptyStartedCursor.of();
    }

    @Override
    public boolean hasValue()
    {
        return true;
    }
}
