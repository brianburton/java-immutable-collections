package org.javimmutable.collections.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

public class SingleKeyTransforms<K, V>
    implements Transforms<MapEntry<K, V>, K, V>
{
    @Override
    public MapEntry<K, V> update(Holder<MapEntry<K, V>> leafHolder,
                                 K key,
                                 V value,
                                 MutableDelta delta)
    {
        if (leafHolder.isEmpty()) {
            delta.add(1);
            return MapEntry.of(key, value);
        } else {
            final MapEntry<K, V> leaf = leafHolder.getValue();
            if (leaf.getKey().equals(key) && leaf.getValue() == value) {
                return leaf;
            } else {
                return MapEntry.of(key, value);
            }
        }
    }

    @Override
    public Holder<MapEntry<K, V>> delete(MapEntry<K, V> leaf,
                                         K key,
                                         MutableDelta delta)
    {
        if (leaf.getKey().equals(key)) {
            delta.subtract(1);
            return Holders.of();
        } else {
            return Holders.of(leaf);
        }
    }

    @Override
    public Holder<V> findValue(MapEntry<K, V> leaf,
                               K key)
    {
        return leaf.getKey().equals(key) ? Holders.of(leaf.getValue()) : Holders.of();
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(MapEntry<K, V> leaf,
                                                       K key)
    {
        return leaf.getKey().equals(key) ? Holders.of(leaf) : Holders.of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(MapEntry<K, V> leaf)
    {
        return SingleValueCursor.of(leaf);
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(MapEntry<K, V> leaf)
    {
        return SingleValueIterator.of(leaf);
    }
}
