package org.javimmutable.collections;

/**
 * Interface for containers that allow access to values by an integer index.
 *
 * @param <T>
 */
public interface Indexed<T>
{
    /**
     * Retrieve the value.  The index must be valid for the container's current size (i.e. [0-size)
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    T get(int index);
}
