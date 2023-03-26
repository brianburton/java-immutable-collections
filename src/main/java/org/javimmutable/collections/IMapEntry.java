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
    @Nonnull
     static <K, V> IMapEntry<K, V> of(@Nonnull Map.Entry<K, V> entry)
    {
        return new MapEntry<K, V>(entry);
    }

    @Nonnull
     static <K, V> IMapEntry<K, V> of(@Nonnull IMapEntry<K, V> entry)
    {
        return new MapEntry<K, V>(entry);
    }

    @Nonnull
     static <K, V> IMapEntry<K, V> of(@Nonnull K key,
                                           V value)
    {
        return new MapEntry<K, V>(key, value);
    }

    @Nonnull
    K getKey();

    V getValue();
}
