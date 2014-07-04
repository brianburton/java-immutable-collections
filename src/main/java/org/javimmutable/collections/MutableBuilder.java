package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for mutable objects used to produce collections by adding objects to the builder
 * and then calling a build() method.  MutableBuilders are required to support unlimited
 * calls to build().
 */
public interface MutableBuilder<T, C>
{
    /**
     * Adds the specified value to the values included in the collection when build() is called.
     *
     * @param value
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    MutableBuilder<T, C> add(T value);

    /**
     * Builds and returns a collection containing all of the added values.  Usually build() can
     * only be called once for a single MutableBuilder instance although implementing classes
     * are not required to enforce this restriction.
     *
     * @return the collection
     */
    @Nonnull
    C build();

    /**
     * Adds all values in the Cursor to the values included in the collection when build() is called.
     *
     * @param source Cursor containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    MutableBuilder<T, C> add(Cursor<? extends T> source);

    /**
     * Adds all values in the Iterator to the values included in the collection when build() is called.
     *
     * @param source Iterator containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    MutableBuilder<T, C> add(Iterator<? extends T> source);

    /**
     * Adds all values in the Collection to the values included in the collection when build() is called.
     *
     * @param source Collection containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    MutableBuilder<T, C> add(Collection<? extends T> source);

    /**
     * Adds all values in the array to the values included in the collection when build() is called.
     *
     * @param source array containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    <K extends T> MutableBuilder<T, C> add(K... source);

    /**
     * Adds all values in the specified range of Indexed to the values included in the collection when build() is called.
     *
     * @param source Indexed containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    MutableBuilder<T, C> add(Indexed<? extends T> source,
                             int offset,
                             int limit);

    /**
     * Adds all values in the Indexed to the values included in the collection when build() is called.
     *
     * @param source Indexed containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    MutableBuilder<T, C> add(Indexed<? extends T> source);
}
