package org.javimmutable.collections.iocursors;

import org.javimmutable.collections.Cursor;

/**
 * Extension of the Cursor interface to add a close() method.
 *
 * @param <T>
 */
public interface CloseableCursor<T>
        extends Cursor<T>
{
    public void close();
}
