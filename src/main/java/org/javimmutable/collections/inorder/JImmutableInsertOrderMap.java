package org.javimmutable.collections.inorder;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.cursors.TransformCursor;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.tree.JImmutableTreeMap;

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
        extends AbstractJImmutableMap<K, V>
{
    @SuppressWarnings("unchecked")
    public static final JImmutableInsertOrderMap EMPTY = new JImmutableInsertOrderMap(JImmutableTreeMap.<Integer, Object>of(), JImmutableHashMap.of(), 1);

    private final JImmutableTreeMap<Integer, K> sortedKeys;
    private final JImmutableHashMap<K, Node<K, V>> values;
    private final int nextIndex;

    private JImmutableInsertOrderMap(JImmutableTreeMap<Integer, K> sortedKeys,
                                     JImmutableHashMap<K, Node<K, V>> values,
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
            final JImmutableTreeMap<Integer, K> newSortedKeys = sortedKeys.assign(nextIndex, key);
            final JImmutableHashMap<K, Node<K, V>> newValues = values.assign(key, new Node<K, V>(key, value, nextIndex));
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
    public JImmutableInsertOrderMap<K, V> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return TransformCursor.of(sortedKeys.cursor(), new Func1<Entry<Integer, K>, Entry<K, V>>()
        {
            @Override
            public Entry<K, V> apply(Entry<Integer, K> entry)
            {
                return values.get(entry.getValue());
            }
        });
    }

    private static class Node<K, V>
            implements Entry<K, V>,
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

        @Override
        public String toString()
        {
            return MapEntry.makeToString(this);
        }

        private Node<K, V> withValue(V value)
        {
            return new Node<K, V>(key, value, index);
        }
    }
}
