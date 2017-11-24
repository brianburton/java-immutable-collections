///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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
import org.javimmutable.collections.GenericCollector;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.TrieArray;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.cursors.TransformCursor;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.iterators.TransformIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import java.util.stream.Collector;

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
{
    @SuppressWarnings("unchecked")
    public static final JImmutableInsertOrderMap EMPTY = new JImmutableInsertOrderMap(TrieArray.of(), JImmutableHashMap.of(), 0);

    private final TrieArray<Node<K, V>> sortedNodes;
    private final JImmutableMap<K, Node<K, V>> hashedNodes;
    private final int nextIndex;

    private JImmutableInsertOrderMap(TrieArray<Node<K, V>> sortedNodes,
                                     JImmutableMap<K, Node<K, V>> hashedNodes,
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
    public V getValueOr(K key,
                        V defaultValue)
    {
        final Node<K, V> current = hashedNodes.get(key);
        return (current != null) ? current.getValue() : defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        final Node<K, V> current = hashedNodes.get(key);
        return (current != null) ? current : Holders.of();
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull K key)
    {
        final Node<K, V> current = hashedNodes.get(key);
        return (current != null) ? Holders.of(current) : Holders.of();
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> assign(@Nonnull K key,
                                                 V value)
    {
        final Node<K, V> current = hashedNodes.get(key);
        if (current == null) {
            final Node<K, V> newNode = new Node<>(key, value, nextIndex);
            return new JImmutableInsertOrderMap<>(sortedNodes.assign(newNode.index, newNode),
                                                  hashedNodes.assign(key, newNode),
                                                  nextIndex + 1);
        } else if (current.getValue() == value) {
            return this;
        } else {
            final Node<K, V> newNode = current.withValue(value);
            return new JImmutableInsertOrderMap<>(sortedNodes.assign(newNode.index, newNode),
                                                  hashedNodes.assign(key, newNode),
                                                  nextIndex);
        }
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> delete(@Nonnull K key)
    {
        final Node<K, V> current = hashedNodes.get(key);
        if (current != null) {
            return new JImmutableInsertOrderMap<>(sortedNodes.delete(current.index),
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

    @Nonnull
    @Override
    public JImmutableInsertOrderMap<K, V> deleteAll()
    {
        return of();
    }

    @Override
    @Nonnull
    public Cursor<Entry<K, V>> cursor()
    {
        return TransformCursor.of(sortedNodes.cursor(), e -> e.getValue());
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return TransformIterator.of(sortedNodes.iterator(), e -> e.getValue());
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return SPLITERATOR_ORDERED;
    }

    @Nonnull
    @Override
    public Collector<Entry<K, V>, ?, JImmutableMap<K, V>> mapCollector()
    {
        return GenericCollector.ordered(this, deleteAll(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Override
    public void checkInvariants()
    {
        //TODO: fix empty checkInvariants()
    }

    /**
     * An Entry implementation that also stores the sortedKeys index corresponding to this node's key.
     */
    @Immutable
    private static class Node<K, V>
        extends MapEntry<K, V>
        implements Holder<V>
    {
        private final int index;

        private Node(K key,
                     V value,
                     int index)
        {
            super(key, value);
            this.index = index;
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

        private Node<K, V> withValue(V value)
        {
            return new Node<>(key, value, index);
        }
    }
}
