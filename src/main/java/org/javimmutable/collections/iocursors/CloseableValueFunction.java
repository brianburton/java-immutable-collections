package org.javimmutable.collections.iocursors;

import org.javimmutable.collections.cursors.ValueFunction;

/**
 * Extension of ValueFunction that adds a close() method.
 *
 * @param <T>
 */
public interface CloseableValueFunction<T>
        extends ValueFunction<T>
{
    public void close();
}
