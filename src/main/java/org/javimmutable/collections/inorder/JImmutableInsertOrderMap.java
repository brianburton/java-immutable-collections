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
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Temp;
import org.javimmutable.collections.array.TrieLongArrayNode;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.iterators.TransformStreamable;
import org.javimmutable.collections.serialization.JImmutableInsertOrderMapProxy;

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
 * are significantly slower.
 */
@Immutable
public class JImmutableInsertOrderMap<K, V>
    extends AbstractJImmutableMap<K, V>
    implements Serializable
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final JImmutableInsertOrderMap EMPTY = new JImmutableInsertOrderMap(TrieLongArrayNode.empty(), JImmutableHashMap.of(), Long.MIN_VALUE);
    private static final long serialVersionUID = -121805;
    private static final int SPLITERATOR_CHARACTERISTICS = StreamConstants.SPLITERATOR_ORDERED;

    private final TrieLongArrayNode<K> keys;
    private final JImmutableMap<K, Node<V>> values;
    private final long nextToken;

    private JImmutableInsertOrderMap(@Nonnull TrieLongArrayNode<K> keys,
                                     @Nonnull JImmutableMap<K, Node<V>> values,
                                     long nextToken)
    {
        assert keys.size() == values.size();
        this.keys = keys;
        this.values = values;
        this.nextToken = nextToken;
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
        final Temp.Var1<Boolean> inserted = new Temp.Var1<>(false);
        final JImmutableMap<K, Node<V>> newValues = values.update(key, hv -> {
            if (hv.isEmpty()) {
                inserted.x = true;
                return new Node<>(nextToken, value);
            } else {
                final Node<V> node = hv.getValue();
                return node.withValue(value);
            }
        });
        if (inserted.x) {
            final TrieLongArrayNode<K> newKeys = keys.assign(nextToken, key);
            return new JImmutableInsertOrderMap<>(newKeys, newValues, nextToken + 1);
        } else if (newValues != values) {
            return new JImmutableInsertOrderMap<>(keys, newValues, nextToken);
        } else {
            return this;
        }
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> delete(@Nonnull K key)
    {
        final Node<V> current = values.get(key);
        if (current == null) {
            return this;
        } else if (values.size() == 1) {
            return of();
        } else {
            return new JImmutableInsertOrderMap<>(keys.delete(current.token), values.delete(key), nextToken);
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
        return TransformStreamable.of(keys(), k -> entry(k, valueForKey(k))).iterator();
    }

    @Nonnull
    @Override
    public IterableStreamable<K> keys()
    {
        return keys.values().streamable(SPLITERATOR_CHARACTERISTICS);
    }

    @Nonnull
    @Override
    public IterableStreamable<V> values()
    {
        return TransformStreamable.of(keys(), this::valueForKey);
    }

    private V valueForKey(K key)
    {
        final Node<V> node = values.get(key);
        assert node != null;
        return node.value;
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
        for (JImmutableMap.Entry<Long, K> e : keys.entries()) {
            final Node<V> node = values.get(e.getValue());
            if (node == null) {
                throw new IllegalStateException(String.format("node missing: token=%s key=%s", e.getKey(), e.getValue()));
            }
            if (!e.getKey().equals(node.token)) {
                throw new IllegalStateException(String.format("node mismatch: sorted=%s hashed=%s", e, node));
            }
        }
    }

    private Object writeReplace()
    {
        return new JImmutableInsertOrderMapProxy(this);
    }

    @Immutable
    private static class Node<V>
    {
        private final long token;
        private final V value;

        private Node(long token,
                     V value)
        {
            this.token = token;
            this.value = value;
        }

        private Node<V> withValue(V value)
        {
            return (value == this.value) ? this : new Node<>(token, value);
        }
    }
}
