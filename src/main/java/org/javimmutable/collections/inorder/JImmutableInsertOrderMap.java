///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

import org.javimmutable.collections.GenericCollector;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.InfiniteKey;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.iterators.TransformIterator;
import org.javimmutable.collections.serialization.JImmutableInsertOrderMapProxy;
import org.javimmutable.collections.tree.JImmutableTreeMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.stream.Collector;

import static org.javimmutable.collections.MapEntry.entry;
import static org.javimmutable.collections.common.StreamConstants.SPLITERATOR_ORDERED;

/**
 * JImmutableMap implementation that allows iteration over members in the order in which they
 * were inserted into the map.  Maintains two parallel data structures, one for sorting and
 * the other for storing entries.  Gets are approximately as fast as hash map gets but updates
 * are significantly slower.   Iteration is comparable to sorted map iteration.
 * <p>
 * Use a hash or tree map whenever possible but this class performs well enough for most cases
 * where insertion order is important to an algorithm.
 */
@Immutable
public class JImmutableInsertOrderMap<K, V>
    extends AbstractJImmutableMap<K, V>
    implements Serializable
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final JImmutableInsertOrderMap EMPTY = new JImmutableInsertOrderMap(JImmutableTreeMap.of(), JImmutableHashMap.of(), InfiniteKey.first());
    private static final long serialVersionUID = -121805;

    private final JImmutableMap<InfiniteKey, K> keys;
    private final JImmutableMap<K, Node<V>> values;
    private final InfiniteKey nextIndex;

    private JImmutableInsertOrderMap(@Nonnull JImmutableMap<InfiniteKey, K> keys,
                                     @Nonnull JImmutableMap<K, Node<V>> values,
                                     @Nonnull InfiniteKey nextIndex)
    {
        assert keys.size() == values.size();
        this.keys = keys;
        this.values = values;
        this.nextIndex = nextIndex;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableInsertOrderMap<K, V> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <K, V> Builder<K, V> builder()
    {
        return new Builder<K, V>()
        {
            private JImmutableInsertOrderMap<K, V> map = of();

            @Nonnull
            @Override
            public synchronized JImmutableMap<K, V> build()
            {
                return map;
            }

            @Nonnull
            @Override
            public synchronized Builder<K, V> clear()
            {
                map = of();
                return this;
            }

            @Nonnull
            @Override
            public synchronized Builder<K, V> add(@Nonnull K key,
                                                  V value)
            {
                map = map.assign(key, value);
                return this;
            }

            @Override
            public synchronized int size()
            {
                return map.size();
            }
        };
    }

    @Nonnull
    @Override
    public Builder<K, V> mapBuilder()
    {
        return builder();
    }

    @Nonnull
    public static <K, V> Collector<Entry<K, V>, ?, JImmutableMap<K, V>> createMapCollector()
    {
        final JImmutableMap<K, V> empty = of();
        return GenericCollector.ordered(empty, empty, a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Nonnull
    @Override
    public Collector<Entry<K, V>, ?, JImmutableMap<K, V>> mapCollector()
    {
        return GenericCollector.ordered(this, of(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        final Node<V> current = values.get(key);
        return (current != null) ? current.value : defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        final Node<V> current = values.get(key);
        return (current != null) ? Holders.of(current.value) : Holders.of();
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull K key)
    {
        final Node<V> current = values.get(key);
        return (current != null) ? Holders.of(entry(key, current.value)) : Holders.of();
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> assign(@Nonnull K key,
                                                 V value)
    {
        final Node<V> current = values.get(key);
        if (current == null) {
            final Node<V> newNode = new Node<>(nextIndex, value);
            return new JImmutableInsertOrderMap<>(keys.assign(newNode.index, key),
                                                  values.assign(key, newNode),
                                                  nextIndex.next());
        } else if (current.value == value) {
            return this;
        } else {
            final Node<V> newNode = new Node<>(current.index, value);
            return new JImmutableInsertOrderMap<>(keys,
                                                  values.assign(key, newNode),
                                                  nextIndex);
        }
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> delete(@Nonnull K key)
    {
        final Node<V> current = values.get(key);
        if (current != null) {
            return new JImmutableInsertOrderMap<>(keys.delete(current.index),
                                                  values.delete(key),
                                                  nextIndex);
        } else {
            return this;
        }
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return TransformIterator.of(keys.values().iterator(), this::entryForKey);
    }

    private JImmutableMap.Entry<K, V> entryForKey(K key)
    {
        final Node<V> node = values.get(key);
        assert node != null;
        return entry(key, node.value);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return SPLITERATOR_ORDERED;
    }

    @Override
    public void checkInvariants()
    {
        if (keys.size() != values.size()) {
            throw new IllegalStateException(String.format("size mismatch: sorted=%s hashed=%s", keys.size(), values.size()));
        }
        for (Entry<InfiniteKey, K> e : keys) {
            final Node<V> node = values.get(e.getValue());
            if (node == null) {
                throw new IllegalStateException(String.format("node missing: index=%s key=%s", e.getKey(), e.getValue()));
            }
            if (e.getKey() != node.index) {
                throw new IllegalStateException(String.format("node mismatch: sorted=%s hashed=%s", e.getValue(), node));
            }
        }
    }

    private Object writeReplace()
    {
        return new JImmutableInsertOrderMapProxy(this);
    }

    /**
     * An Entry implementation that also stores the sortedKeys index corresponding to this node's key.
     */
    @Immutable
    private static class Node<V>
    {
        private final InfiniteKey index;
        private final V value;

        private Node(InfiniteKey index,
                     V value)
        {
            this.index = index;
            this.value = value;
        }
    }
}
