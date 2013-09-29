///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.common.AbstractPersistentMap;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.MapAdaptor;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiTransformCursor;

import java.util.Iterator;
import java.util.Map;

public class PersistentHashMap<K, V>
        extends AbstractPersistentMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final PersistentHashMap EMPTY = new PersistentHashMap();

    private final HashTrieNode<K, V> nodes;
    private final int size;

    @SuppressWarnings("unchecked")
    public PersistentHashMap()
    {
        this(new HashInteriorNode<K, V>(), 0);
    }

    private PersistentHashMap(HashTrieNode<K, V> nodes,
                              int size)
    {
        this.nodes = nodes;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> PersistentHashMap<K, V> of()
    {
        return (PersistentHashMap<K, V>)EMPTY;
    }

    @Override
    public V get(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        return find(key).getValueOrNull();
    }

    @Override
    public Holder<V> find(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        final int hashCode = key.hashCode();
        return nodes.get(hashCode >>> 5, hashCode & 0x1f, key);
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        final int hashCode = key.hashCode();
        return Holders.fromNullable(nodes.getEntry(hashCode >>> 5, hashCode & 0x1f, key));
    }

    @Override
    public PersistentHashMap<K, V> assign(final K key,
                                          final V value)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        final HashTrieNode<K, V> nodes = this.nodes;
        final MutableDelta sizeDelta = new MutableDelta();
        final int hashCode = key.hashCode();
        HashTrieNode<K, V> newNodes = nodes.set(hashCode >>> 5, hashCode & 0x1f, key, value, sizeDelta);
        return (newNodes == nodes) ? this : new PersistentHashMap<K, V>(newNodes, sizeDelta.apply(size));
    }

    @Override
    public PersistentHashMap<K, V> delete(final K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        final HashTrieNode<K, V> nodes = this.nodes;
        final MutableDelta sizeDelta = new MutableDelta();
        final int hashCode = key.hashCode();
        HashTrieNode<K, V> newNodes = nodes.delete(hashCode >>> 5, hashCode & 0x1f, key, sizeDelta);
        return (newNodes == nodes) ? this : new PersistentHashMap<K, V>(newNodes, sizeDelta.apply(size));
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public Map<K, V> asMap()
    {
        return MapAdaptor.of(this);
    }

    /**
     * Adds the key/value pair to this map.  Any value already existing for the specified key
     * is replaced with the new value.
     *
     * @param e
     * @return
     */
    @Override
    public Insertable<Entry<K, V>> insert(Entry<K, V> e)
    {
        return assign(e.getKey(), e.getValue());
    }

    @Override
    public Iterator<Entry<K, V>> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return MultiTransformCursor.of(LazyCursor.of(nodes), new Func1<HashTrieValue<K, V>, Cursor<PersistentMap.Entry<K, V>>>()
        {
            @Override
            public Cursor<PersistentMap.Entry<K, V>> apply(HashTrieValue<K, V> node)
            {
                return node.cursor();
            }
        });
    }

    public PersistentMap<Class, Integer> getNodeTypeCounts(PersistentMap<Class, Integer> map)
    {
        return map;
    }
}
