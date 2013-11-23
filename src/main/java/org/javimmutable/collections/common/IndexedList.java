package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

import java.util.ArrayList;
import java.util.List;

public class IndexedList<T>
        implements Indexed<T>
{
    private final List<T> values;

    private IndexedList(List<T> values)
    {
        this.values = values;
    }

    /**
     * Produces an instance using a copy of the specified List to ensure that changes to the List
     * will not influence the values returned by the instance's methods.  This is generally preferred
     * to the unsafe() constructor.
     *
     * @param values
     * @param <T>
     * @return
     */
    public static <T> IndexedList<T> copied(List<T> values)
    {
        return new IndexedList<T>(new ArrayList<T>(values));
    }

    /**
     * Produces an instance using the provided List.  This makes the instance unsafe for sharing since
     * changes to the List will cause changes to this instance's values.  However this can be useful
     * when performance is important and the instance will not be shared or retained beyond a single
     * method scope.
     *
     * @param values
     * @param <T>
     * @return
     */
    public static <T> IndexedList<T> retained(List<T> values)
    {
        return new IndexedList<T>(values);
    }

    @Override
    public T get(int index)
    {
        return values.get(index);
    }

    @Override
    public int size()
    {
        return values.size();
    }
}
