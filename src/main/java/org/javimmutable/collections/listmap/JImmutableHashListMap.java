package org.javimmutable.collections.listmap;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.hash.JImmutableHashMap;

/**
 * JImmutableListMap using a hash map for fast lookup.
 *
 * @param <K>
 * @param <V>
 */
public class JImmutableHashListMap<K, V>
        extends AbstractJImmutableListMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableHashListMap EMPTY = new JImmutableHashListMap(JImmutableHashMap.of());

    private JImmutableHashListMap(JImmutableMap<K, JImmutableList<V>> contents)
    {
        super(contents);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashListMap<K, V> of()
    {
        return (JImmutableHashListMap<K, V>)EMPTY;
    }

    @Override
    protected JImmutableListMap<K, V> create(JImmutableMap<K, JImmutableList<V>> map)
    {
        return new JImmutableHashListMap<K, V>(map);
    }
}
