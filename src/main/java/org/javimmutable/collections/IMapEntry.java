package org.javimmutable.collections;

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
    K getKey();

    V getValue();
}
