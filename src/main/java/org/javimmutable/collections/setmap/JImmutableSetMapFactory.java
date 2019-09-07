package org.javimmutable.collections.setmap;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashSet;

import javax.annotation.Nonnull;
import java.util.stream.Collector;

public class JImmutableSetMapFactory<K, V>
{
    private JImmutableMap<K, JImmutableSet<V>> map = JImmutableHashMap.of();
    private JImmutableSet<V> set = JImmutableHashSet.of();

    public JImmutableSetMap<K, V> create()
    {
        return JImmutableTemplateSetMap.of(map, set);
    }

    public Collector<JImmutableMap.Entry<K, V>, ?, JImmutableSetMap<K, V>> collector()
    {
        return create().setMapCollector();
    }

    public JImmutableSetMapFactory<K, V> withMap(@Nonnull JImmutableMap<K, JImmutableSet<V>> map)
    {
        this.map = map.deleteAll();
        return this;
    }

    public JImmutableSetMapFactory<K, V> withSet(@Nonnull JImmutableSet<V> set)
    {
        this.set = set.deleteAll();
        return this;
    }
}
