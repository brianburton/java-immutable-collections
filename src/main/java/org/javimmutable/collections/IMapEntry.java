package org.javimmutable.collections;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * An immutable entry in the map.  Contains the key and value for that entry.
 * key must not be null but value can be null.
 */
@Immutable
public
interface IMapEntry<K, V>
{
    /**
     * Convenience function to create a JImmutableMap.Entry.
     */
    @Nonnull
    static <K, V, K1 extends K, V1 extends V> IMapEntry<K, V> entry(K1 key,
                                                                    V1 value)
    {
        return MapEntry.of(key, value);
    }

    /**
     * Convenience function to create a JImmutableMap.Entry.
     */
    @Nonnull
    static <K, V> IMapEntry<K, V> entry(@Nonnull IMapEntry<? extends K, ? extends V> e)
    {
        return MapEntry.of(e.getKey(), e.getValue());
    }

    /**
     * Convenience function to create a Map.Entry.
     */
    @Nonnull
    static <K, V> IMapEntry<K, V> entry(@Nonnull Map.Entry<? extends K, ? extends V> e)
    {
        return MapEntry.of(e.getKey(), e.getValue());
    }

    @Nonnull
    K getKey();

    V getValue();
}
