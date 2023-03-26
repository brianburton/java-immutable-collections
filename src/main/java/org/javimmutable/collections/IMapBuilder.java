package org.javimmutable.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;

public interface IMapBuilder<K, V>
{
    @Nonnull
    IMap<K, V> build();

    @Nonnull
    IMapBuilder<K, V> add(@Nonnull K key,
                          V value);

    int size();

    @Nonnull
    default IMapBuilder<K, V> add(IMapEntry<? extends K, ? extends V> e)
    {
        return add(e.getKey(), e.getValue());
    }

    /**
     * Adds all values in the Iterator to the values included in the collection when build() is called.
     *
     * @param source Iterator containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Iterator<? extends IMapEntry<? extends K, ? extends V>> source)
    {
        while (source.hasNext()) {
            add(source.next());
        }
        return this;
    }

    /**
     * Adds all values in the Map to the values included in the collection when build() is called.
     *
     * @param source Iterator containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Map<? extends K, ? extends V> source)
    {
        for (Map.Entry<? extends K, ? extends V> e : source.entrySet()) {
            add(e.getKey(), e.getValue());
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
    default IMapBuilder<K, V> add(Iterable<? extends IMapEntry<? extends K, ? extends V>> source)
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
    default <T extends IMapEntry<? extends K, ? extends V>> IMapBuilder<K, V> add(T... source)
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
    default IMapBuilder<K, V> add(Indexed<? extends IMapEntry<? extends K, ? extends V>> source,
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
    default IMapBuilder<K, V> add(Indexed<? extends IMapEntry<? extends K, ? extends V>> source)
    {
        return add(source, 0, source.size());
    }

    @Nonnull
    default IMapBuilder<K, V> add(@Nonnull IMapBuilder<K, V> other)
    {
        add(other.build());
        return this;
    }

    /**
     * Deletes all values.  This is useful to reset to build a new map with the same builder.
     *
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    IMapBuilder<K, V> clear();
}
