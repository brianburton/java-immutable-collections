///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
            final Node<K, V> node = current.getValue();
            return node.getValue() == value ? this : new JImmutableInsertOrderMap<K, V>(sortedKeys, values.assign(key, node.withValue(value)), nextIndex);
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
