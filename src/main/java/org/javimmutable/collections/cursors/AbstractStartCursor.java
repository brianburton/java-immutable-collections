package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

/**
 * Base implementation for unstarted Cursor classes.
 * Derived classes generally override the next() method to start the traversal.
 * Once the end of the traversal is reached returning super.next() will
 * end the traversal.
 *
 * @param <T>
 */
public abstract class AbstractStartCursor<T>
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
        throw new NotStartedException();
    }

    @Override
    public T getValue()
    {
        throw new NotStartedException();
    }
}
