package org.javimmutable.collections;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nonnull;

public interface ISetBuilder<T>
{
    /**
     * Builds and returns a collection containing all of the added values.  May be called
     * as often as desired and is safe to call and then continue adding more elements to build
     * another collection with those additional elements.
     *
     * @return the collection
     */
    @Nonnull
    ISet<T> build();

    /**
     * Determines how many values will be in the collection if build() is called now.
     */
    int size();

    /**
     * Adds the specified value to the values included in the collection when build() is called.
     *
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    ISetBuilder<T> add(T value);

    /**
     * Adds all values in the Iterator to the values included in the collection when build() is called.
     *
     * @param source Iterator containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default ISetBuilder<T> add(Iterator<? extends T> source)
    {
        while (source.hasNext()) {
            add(source.next());
        }
        return this;
    }

    /**
     * Adds all values in the Collection to the values included in the collection when build() is called.
     *
     * @param source Collection containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default ISetBuilder<T> add(Iterable<? extends T> source)
    {
        return add(source.iterator());
    }

    /**
     * Adds all values in the array to the values included in the collection when build() is called.
     *
     * @param source array containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default <K extends T> ISetBuilder<T> add(K... source)
    {
        return add(Arrays.asList(source));
    }

    /**
     * Adds all values in the specified range of Indexed to the values included in the collection when build() is called.
     *
     * @param source Indexed containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default ISetBuilder<T> add(Indexed<? extends T> source,
                               int offset,
                               int limit)
    {
        for (int i = offset; i < limit; ++i) {
            add(source.get(i));
        }
        return this;
    }

    /**
     * Adds all values in the Indexed to the values included in the collection when build() is called.
     *
     * @param source Indexed containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default ISetBuilder<T> add(Indexed<? extends T> source)
    {
        return add(source, 0, source.size());
    }

    /**
     * Removes all objects and resets the builder to it's initial post-build state.
     *
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    ISetBuilder<T> clear();
}
