package org.javimmutable.collections.inorder;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.MapAdaptor;
import org.javimmutable.collections.cursors.TransformCursor;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.tree.JImmutableTreeMap;

import java.util.Iterator;
import java.util.Map;

/**
 * JImmutableMap implementation that allows iteration over members in the order in which they
 * were inserted into the map.  Gets are as fast as hash map gets but updates are slower.
 * Iteration is slightly slower than a bare map since each entry returned by the cursor
 * is created as needed.
 *
 * @param <K>
 * @param <V>
 */
public class JImmutableInsertOrderMap<K, V>
        implements JImmutableMap<K, V>
{
    @SuppressWarnings("unchecked")
    public static final JImmutableInsertOrderMap EMPTY = new JImmutableInsertOrderMap(JImmutableTreeMap.<Integer, Object>of(), JImmutableHashMap.of(), 1);

    private final JImmutableMap<Integer, K> sortedKeys;
    private final JImmutableMap<K, Node<K, V>> values;
    private final int nextIndex;

    public JImmutableInsertOrderMap(JImmutableMap<Integer, K> sortedKeys,
                                    JImmutableMap<K, Node<K, V>> values,
                                    int nextIndex)
    {
        this.sortedKeys = sortedKeys;
        this.values = values;
        this.nextIndex = nextIndex;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableInsertOrderMap<K, V> of()
    {
        return (JImmutableInsertOrderMap<K, V>)EMPTY;
    }

    @Override
    public Holder<V> find(K key)
    {
        final Holder<Node<K, V>> current = values.find(key);
        return current.isFilled() ? current.getValue() : Holders.<V>of();
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        final Holder<Node<K, V>> current = values.find(key);
        return current.isFilled() ? Holders.<Entry<K, V>>of(current.getValue()) : Holders.<Entry<K, V>>of();
    }

    @Override
    public JImmutableInsertOrderMap<K, V> assign(K key,
                                                 V value)
    {
        final Holder<Node<K, V>> current = values.find(key);
        if (current.isFilled()) {
            return new JImmutableInsertOrderMap<K, V>(sortedKeys, values.assign(key, current.getValue().withValue(value)), nextIndex);
        } else {
            final JImmutableMap<Integer, K> newSortedKeys = sortedKeys.assign(nextIndex, key);
            final JImmutableMap<K, Node<K, V>> newValues = values.assign(key, new Node<K, V>(key, value, nextIndex));
            return new JImmutableInsertOrderMap<K, V>(newSortedKeys, newValues, nextIndex + 1);
        }
    }

    @Override
    public JImmutableInsertOrderMap<K, V> delete(K key)
    {
        final Holder<Node<K, V>> current = values.find(key);
        if (current.isFilled()) {
            return new JImmutableInsertOrderMap<K, V>(sortedKeys.delete(current.getValue().index), values.delete(key), nextIndex);
        } else {
            return this;
        }
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Override
    public boolean isEmpty()
    {
        return values.isEmpty();
    }

    @Override
    public JImmutableInsertOrderMap<K, V> deleteAll()
    {
        return of();
    }

    @Override
    public Map<K, V> getMap()
    {
        return MapAdaptor.of(this);
    }

    @Override
    public Cursor<K> keysCursor()
    {
        return TransformCursor.ofKeys(cursor());
    }

    @Override
    public Cursor<V> valuesCursor()
    {
        return TransformCursor.ofValues(cursor());
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return new InOrderCursor(sortedKeys.cursor());
    }

    @Override
    public Insertable<Entry<K, V>> insert(Entry<K, V> entry)
    {
        return assign(entry.getKey(), entry.getValue());
    }

    @Override
    public Iterator<Entry<K, V>> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public V get(K key)
    {
        return find(key).getValueOrNull();
    }

    private class InOrderCursor
            implements Cursor<Entry<K, V>>
    {
        private Cursor<Entry<Integer, K>> sortedMapCursor;

        private InOrderCursor(Cursor<Entry<Integer, K>> sortedMapCursor)
        {
            this.sortedMapCursor = sortedMapCursor;
        }

        @Override
        public Cursor<Entry<K, V>> next()
        {
            return new InOrderCursor(sortedMapCursor.next());
        }

        @Override
        public boolean hasValue()
        {
            return sortedMapCursor.hasValue();
        }

        @Override
        public Entry<K, V> getValue()
        {
            return values.get(sortedMapCursor.getValue().getValue());
        }
    }

    private static class Node<K, V>
            implements JImmutableMap.Entry<K, V>,
                       Holder<V>
    {
        private final K key;
        private final V value;
        private final int index;

        private Node(K key,
                     V value,
                     int index)
        {
            this.key = key;
            this.value = value;
            this.index = index;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean isFilled()
        {
            return true;
        }

        @Override
        public V getValueOrNull()
        {
            return value;
        }

        @Override
        public V getValueOr(V defaultValue)
        {
            return value;
        }

        @Override
        public int hashCode()
        {
            return (key == null ? 0 : key.hashCode()) ^
                   (value == null ? 0 : value.hashCode());
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof JImmutableMap.Entry) {
                JImmutableMap.Entry jentry = (JImmutableMap.Entry)o;
                return (key == null ?
                        jentry.getKey() == null : key.equals(jentry.getKey())) &&
                       (value == null ?
                        jentry.getValue() == null : value.equals(jentry.getValue()));
            }

            if (!(o instanceof Map.Entry)) {
                return false;
            }

            Map.Entry entry2 = (Map.Entry)o;
            return (key == null ?
                    entry2.getKey() == null : key.equals(entry2.getKey())) &&
                   (value == null ?
                    entry2.getValue() == null : value.equals(entry2.getValue()));
        }

        private Node<K, V> withValue(V value)
        {
            return new Node<K, V>(key, value, index);
        }
    }
}
