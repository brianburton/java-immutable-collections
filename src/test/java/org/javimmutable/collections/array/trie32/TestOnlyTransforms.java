package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.IterableCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Transforms implementation intended solely for unit tests because it uses a mutable
 * collection to store its values.
 */
class TestOnlyTransforms<K, V>
        implements Transforms<Map<K, V>, K, V>
{
    @Override
    public Map<K, V> update(Holder<Map<K, V>> leaf,
                            K key,
                            V value,
                            MutableDelta delta)
    {
        if (leaf.isEmpty()) {
            delta.add(1);
            Map<K, V> map = new TreeMap<K, V>();
            map.put(key, value);
            return map;
        } else {
            Map<K, V> map = leaf.getValue();
            if (!map.containsKey(key)) {
                delta.add(1);
            }
            map.put(key, value);
            return map;
        }
    }

    @Override
    public Holder<Map<K, V>> delete(Map<K, V> leaf,
                                    K key,
                                    MutableDelta delta)
    {
        if (leaf.containsKey(key)) {
            delta.subtract(1);
            leaf.remove(key);
        }
        if (leaf.isEmpty()) {
            return Holders.of();
        } else {
            return Holders.of(leaf);
        }
    }

    @Override
    public Holder<V> findValue(Map<K, V> leaf,
                               K key)
    {
        return Holders.fromNullable(leaf.get(key));
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Map<K, V> leaf,
                                                       K key)
    {
        V value = leaf.get(key);
        if (value == null) {
            return Holders.of();
        } else {
            return Holders.<JImmutableMap.Entry<K, V>>of(MapEntry.<K, V>of(key, value));
        }
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(Map<K, V> leaf)
    {
        List<JImmutableMap.Entry<K, V>> entries = new ArrayList<JImmutableMap.Entry<K, V>>();
        for (Map.Entry<K, V> entry : leaf.entrySet()) {
            entries.add(MapEntry.of(entry));
        }
        return IterableCursor.of(entries);
    }
}
