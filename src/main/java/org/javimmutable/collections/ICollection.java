package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Iterator;

public interface ICollection<T>
    extends IStreamable<T>,
            Serializable
{
    /**
     * Add value to the container in some manner appropriate to the implementation.
     */
    @Nonnull
    ICollection<T> insert(T value);

    /**
     * Add all values to the container in some manner appropriate to the implementation.
     */
    @Nonnull
    default ICollection<T> insertAll(@Nonnull Iterator<? extends T> iterator)
    {
        ICollection<T> container = this;
        while (iterator.hasNext()) {
            container = container.insert(iterator.next());
        }
        return container;
    }

    /**
     * Add all values to the container in some manner appropriate to the implementation.
     */
    @Nonnull
    default ICollection<T> insertAll(@Nonnull Iterable<? extends T> iterable)
    {
        return insertAll(iterable.iterator());
    }

    /**
     * @return number of values in the collection
     */
    int size();

    /**
     * @return true only if collection contains no values
     */
    default boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * @return false only if collection contains no values
     */
    default boolean isNonEmpty()
    {
        return !isEmpty();
    }

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    ICollection<T> deleteAll();
}
