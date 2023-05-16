///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection.inorder;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.GenericCollector;
import org.javimmutable.collection.IMap;
import org.javimmutable.collection.IMapBuilder;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.IStreamable;
import org.javimmutable.collection.Maybe;
import org.javimmutable.collection.SplitableIterator;
import org.javimmutable.collection.common.AbstractMap;
import org.javimmutable.collection.common.StreamConstants;
import org.javimmutable.collection.hash.HashMap;
import org.javimmutable.collection.iterators.AbstractSplitableIterator;
import org.javimmutable.collection.serialization.OrderedMapProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.stream.Collector;

/**
 * JImmutableMap implementation that allows iteration over members in the order in which they
 * were inserted into the map.  Maintains two parallel data structures, one for sorting and
 * the other for storing entries.  Gets are approximately as fast as hash map gets but updates
 * are significantly slower.
 */
@Immutable
public class OrderedMap<K, V>
    extends AbstractMap<K, V>
    implements Serializable
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final OrderedMap EMPTY = new OrderedMap(HashMap.of(), null, null);
    private static final long serialVersionUID = -121805;
    private static final int SPLITERATOR_CHARACTERISTICS = StreamConstants.SPLITERATOR_ORDERED;

    private final @Nonnull IMap<K, Node<K, V>> values;
    private final @Nullable K firstKey;
    private final @Nullable K lastKey;

    private OrderedMap(@Nonnull IMap<K, Node<K, V>> values,
                       @Nullable K firstKey,
                       @Nullable K lastKey)
    {
        this.values = values;
        this.firstKey = firstKey;
        this.lastKey = lastKey;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> OrderedMap<K, V> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <K, V> IMapBuilder<K, V> builder()
    {
        return new IMapBuilder<K, V>()
        {
            private OrderedMap<K, V> map = of();

            @Nonnull
            @Override
            public synchronized IMap<K, V> build()
            {
                return map;
            }

            @Nonnull
            @Override
            public synchronized IMapBuilder<K, V> clear()
            {
                map = of();
                return this;
            }

            @Nonnull
            @Override
            public synchronized IMapBuilder<K, V> add(@Nonnull K key,
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
    public IMapBuilder<K, V> mapBuilder()
    {
        return builder();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> createMapCollector()
    {
        final IMap<K, V> empty = of();
        return GenericCollector.ordered(empty, empty, a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Nonnull
    @Override
    public Collector<IMapEntry<K, V>, ?, IMap<K, V>> mapCollector()
    {
        return GenericCollector.ordered(this, of(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        final Node<K, V> current = values.get(key);
        return (current != null) ? current.value : defaultValue;
    }

    @Nonnull
    @Override
    public Maybe<V> find(@Nonnull K key)
    {
        final Node<K, V> current = values.get(key);
        return current != null ? Maybe.present(current.value) : Maybe.absent();
    }

    @Nonnull
    @Override
    public Maybe<IMapEntry<K, V>> findEntry(@Nonnull K key)
    {
        final Node<K, V> current = values.get(key);
        return current != null ? Maybe.present(IMapEntry.of(key, current.value)) : Maybe.absent();
    }

    @Nonnull
    @Override
    public OrderedMap<K, V> assign(@Nonnull K key,
                                   V value)
    {
        IMap<K, Node<K, V>> newValues = this.values;
        final Node<K, V> oldNode = newValues.get(key);
        if (oldNode == null) {
            final Node<K, V> newNode = new Node<>(lastKey, null, value);
            newValues = newValues.assign(key, newNode);
            if (lastKey != null) {
                final Node<K, V> prevNode = nodeForKey(lastKey);
                newValues = newValues.assign(lastKey, prevNode.withNextKey(key));
            }
            final K newFirstKey = (firstKey == null) ? key : firstKey;
            return new OrderedMap<>(newValues, newFirstKey, key);
        } else if (oldNode.value != value) {
            final Node<K, V> newNode = oldNode.withValue(value);
            newValues = newValues.assign(key, newNode);
            return new OrderedMap<>(newValues, firstKey, lastKey);
        } else {
            return this;
        }
    }

    @Nonnull
    @Override
    public OrderedMap<K, V> delete(@Nonnull K key)
    {
        IMap<K, Node<K, V>> newValues = this.values;
        final Node<K, V> oldNode = newValues.get(key);
        if (oldNode == null) {
            return this;
        } else if (values.size() == 1) {
            return of();
        } else {
            if (oldNode.prevKey != null) {
                final Node<K, V> prevNode = nodeForKey(oldNode.prevKey);
                newValues = newValues.assign(oldNode.prevKey, prevNode.withNextKey(oldNode.nextKey));
            }
            if (oldNode.nextKey != null) {
                final Node<K, V> nextNode = nodeForKey(oldNode.nextKey);
                newValues = newValues.assign(oldNode.nextKey, nextNode.withPrevKey(oldNode.prevKey));
            }
            newValues = newValues.delete(key);
            final K newFirstKey = oldNode.prevKey == null ? oldNode.nextKey : firstKey;
            final K newLastKey = oldNode.nextKey == null ? oldNode.prevKey : lastKey;
            return new OrderedMap<>(newValues, newFirstKey, newLastKey);
        }
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Nonnull
    @Override
    public OrderedMap<K, V> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public SplitableIterator<IMapEntry<K, V>> iterator()
    {
        return new NodeStreamable<>(NodeWalker::nextEntry).iterator();
    }

    @Nonnull
    @Override
    public IStreamable<K> keys()
    {
        return new NodeStreamable<>(NodeWalker::nextKey);
    }

    @Nonnull
    @Override
    public IStreamable<V> values()
    {
        return new NodeStreamable<>(NodeWalker::nextValue);
    }

    @Nonnull
    private Node<K, V> nodeForKey(K key)
    {
        final Node<K, V> node = values.get(key);
        assert node != null;
        return node;
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return SPLITERATOR_CHARACTERISTICS;
    }

    @Override
    public void checkInvariants()
    {
    }

    private Object writeReplace()
    {
        return new OrderedMapProxy(this);
    }

    @Immutable
    private static class Node<K, V>
    {
        @Nullable
        private final K prevKey;
        @Nullable
        private final K nextKey;
        private final V value;

        private Node(@Nullable K prevKey,
                     @Nullable K nextKey,
                     V value)
        {
            this.prevKey = prevKey;
            this.nextKey = nextKey;
            this.value = value;
        }

        private Node<K, V> withPrevKey(K prevKey)
        {
            return new Node<>(prevKey, nextKey, value);
        }

        private Node<K, V> withNextKey(K nextKey)
        {
            return new Node<>(prevKey, nextKey, value);
        }

        private Node<K, V> withValue(V value)
        {
            return (value == this.value) ? this : new Node<>(prevKey, nextKey, value);
        }

        @Override
        public String toString()
        {
            return "(" + value + "," + prevKey + "," + nextKey + ")";
        }
    }

    private class NodeWalker
    {
        @Nullable
        private K key;

        private NodeWalker()
        {
            key = firstKey;
        }

        public boolean hasNext()
        {
            return key != null;
        }

        private Node<K, V> nextNode()
        {
            if (key == null) {
                throw new NoSuchElementException();
            }
            final Node<K, V> node = values.get(key);
            assert node != null;
            key = node.nextKey;
            return node;
        }

        private K nextKey()
        {
            final K answer = key;
            nextNode();
            return answer;
        }

        private V nextValue()
        {
            final Node<K, V> node = nextNode();
            return node.value;
        }

        private IMapEntry<K, V> nextEntry()
        {
            final K entryKey = key;
            final Node<K, V> node = nextNode();
            return IMapEntry.of(entryKey, node.value);
        }
    }

    private class NodeStreamable<T>
        implements IStreamable<T>
    {
        private final Func1<NodeWalker, T> transforminator;

        private NodeStreamable(Func1<NodeWalker, T> transforminator)
        {
            this.transforminator = transforminator;
        }

        @Nonnull
        @Override
        public SplitableIterator<T> iterator()
        {
            return new AbstractSplitableIterator<T>()
            {
                private final NodeWalker nodes = new NodeWalker();

                @Override
                public boolean hasNext()
                {
                    return nodes.hasNext();
                }

                @Override
                public T next()
                {
                    return transforminator.apply(nodes);
                }
            };
        }

        @Override
        public int getSpliteratorCharacteristics()
        {
            return SPLITERATOR_CHARACTERISTICS;
        }
    }
}
