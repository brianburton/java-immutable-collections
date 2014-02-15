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
 * were inserted into the map.  Maintains two parallel data structures, one for sorting and
 * the other for storing entries.  Gets are approximately as fast as hash map gets but updates
 * are significantly slower.   Iteration is comparable to sorted map iteration.
 * <p/>
 * Use a hash or tree map whenever possible but this class performs well enough for most cases
 * where insertion order is important to an algorithm.
 *
 * @param <K>
 * @param <V>
 */
public class JImmutableInsertOrderMap<K, V>
        extends AbstractJImmutableMap<K, V>
{
    @SuppressWarnings("unchecked")
    public static final JImmutableInsertOrderMap EMPTY = new JImmutableInsertOrderMap(JImmutableTreeMap.<Integer, Object>of(), JImmutableHashMap.of(), 1);

    private final JImmutableTreeMap<Integer, Node<K, V>> sortedNodes;
    private final JImmutableHashMap<K, Node<K, V>> hashedNodes;
    private final int nextIndex;

    private JImmutableInsertOrderMap(JImmutableTreeMap<Integer, Node<K, V>> sortedNodes,
                                     JImmutableHashMap<K, Node<K, V>> hashedNodes,
                                     int nextIndex)
    {
        assert sortedNodes.size() == hashedNodes.size();
        this.sortedNodes = sortedNodes;
        this.hashedNodes = hashedNodes;
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
        final Node<K, V> current = hashedNodes.get(key);
        return (current != null) ? current : Holders.<V>of();
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        final Node<K, V> current = hashedNodes.get(key);
        return (current != null) ? Holders.<Entry<K, V>>of(current) : Holders.<Entry<K, V>>of();
    }

    @Override
    public JImmutableInsertOrderMap<K, V> assign(K key,
                                                 V value)
    {
        final Node<K, V> current = hashedNodes.get(key);
        if (current == null) {
            final Node<K, V> newNode = new Node<K, V>(key, value, nextIndex);
            return new JImmutableInsertOrderMap<K, V>(sortedNodes.assign(newNode.index, newNode),
                                                      hashedNodes.assign(key, newNode),
                                                      nextIndex + 1);
        } else if (current.getValue() == value) {
            return this;
        } else {
            final Node<K, V> newNode = current.withValue(value);
            return new JImmutableInsertOrderMap<K, V>(sortedNodes.assign(newNode.index, newNode),
                                                      hashedNodes.assign(key, newNode),
                                                      nextIndex);
        }
    }

    @Override
    public JImmutableInsertOrderMap<K, V> delete(K key)
    {
        final Node<K, V> current = hashedNodes.get(key);
        if (current != null) {
            return new JImmutableInsertOrderMap<K, V>(sortedNodes.delete(current.index),
                                                      hashedNodes.delete(key),
                                                      nextIndex);
        } else {
            return this;
        }
    }

    @Override
    public int size()
    {
        return hashedNodes.size();
    }

    @Override
    public JImmutableInsertOrderMap<K, V> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return TransformCursor.of(sortedNodes.valuesCursor(), new Func1<Node<K, V>, Entry<K, V>>()
        {
            @Override
            public Entry<K, V> apply(Node<K, V> node)
            {
                return node;
            }
        });
    }

    /**
     * An Entry implementation that also stores the sortedKeys index corresponding to this node's key.
     *
     * @param <K>
     * @param <V>
     */
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