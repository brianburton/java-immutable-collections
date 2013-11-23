package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

/**
 * Indexed implementation backed by a java array.
 */
public class IndexedArray<T>
        implements Indexed<T>
{
    private final T[] values;

    /**
     * Produces an instance using a clone of the specified array to ensure that changes to the array
     * will not influence the values returned by the instance's methods.  This is generally preferred
     * to the unsafe() constructor.
     *
     * @param values
     * @param <T>
     * @return
     */
    public static <T> IndexedArray<T> copied(T[] values)
    {
        return new IndexedArray<T>(values.clone());
    }

    /**
     * Produces an instance using the provided array.  This makes the instance unsafe for sharing since
     * changes to the array will cause changes to this instance's values.  However this can be useful
     * when performance is important and the instance will not be shared or retained beyond a single
     * method scope.
     *
     * @param values
     * @param <T>
     * @return
     */
    public static <T> IndexedArray<T> retained(T[] values)
    {
        return new IndexedArray<T>(values);
    }

    private IndexedArray(T[] values)
    {
        this.values = values;
    }

    @Override
    public T get(int index)
    {
        return values[index];
    }

    @Override
    public int size()
    {
        return values.length;
    }
}
