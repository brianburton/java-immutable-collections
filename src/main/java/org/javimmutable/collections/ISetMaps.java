package org.javimmutable.collections;

import java.util.Comparator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.setmap.JImmutableHashSetMap;
import org.javimmutable.collections.setmap.JImmutableInsertOrderSetMap;
import org.javimmutable.collections.setmap.JImmutableSetMapFactory;
import org.javimmutable.collections.setmap.JImmutableTemplateSetMap;
import org.javimmutable.collections.setmap.JImmutableTreeSetMap;

public final class ISetMaps
{
    private ISetMaps()
    {
    }

    /**
     * Creates a set map with higher performance but no specific ordering of keys.
     * Sets for each key are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> hashed()
    {
        return JImmutableHashSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by order they are inserted.
     * Sets for each value are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> ordered()
    {
        return JImmutableInsertOrderSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by their natural ordering.
     * Sets for each key are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K extends Comparable<K>, V> ISetMap<K, V> sorted()
    {
        return JImmutableTreeSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     * Sets for each value are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> sorted(@Nonnull Comparator<K> comparator)
    {
        return JImmutableTreeSetMap.of(comparator);
    }

    /**
     * Creates a set map using the provided templates for map and set.  The templates do not have to be
     * empty.  The set map will always use empty versions of them internally.  This factory method
     * provided complete flexibility in the choice of map and set types by caller.
     *
     * @param templateMap instance of the type of map to use
     * @param templateSet instance of the type of set to use
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> templated(@Nonnull IMap<K, ISet<V>> templateMap,
                                                 @Nonnull ISet<V> templateSet)
    {
        return JImmutableTemplateSetMap.of(templateMap, templateSet);
    }

    /**
     * Creates a builder to build a custom JImmutableSetMap configuration from a
     * base map and set type.
     */
    @Nonnull
    public static <K, V> JImmutableSetMapFactory<K, V> factory()
    {
        return new JImmutableSetMapFactory<>();
    }

    /**
     * Creates a builder to build a custom JImmutableSetMap configuration from a
     * base map and set type.   The provided classes are used to tell the java
     * type system what the target times are.  Sometimes this can be more
     * convenient than angle brackets.
     */
    @Nonnull
    public static <K, V> JImmutableSetMapFactory<K, V> factory(@Nonnull Class<K> keyClass,
                                                               @Nonnull Class<V> valueClass)
    {
        return new JImmutableSetMapFactory<>();
    }
}
