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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiTransformCursor;

public class JImmutableHashMap<K, V>
        extends AbstractJImmutableMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableHashMap EMPTY = new JImmutableHashMap();

    private final HashTrieNode<K, V> nodes;
    private final int size;

    @SuppressWarnings("unchecked")
    public JImmutableHashMap()
    {
        this(new HashInteriorNode<K, V>(), 0);
    }

    private JImmutableHashMap(HashTrieNode<K, V> nodes,
                              int size)
    {
        this.nodes = nodes;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashMap<K, V> of()
    {
        return (JImmutableHashMap<K, V>)EMPTY;
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
    public JImmutableHashMap<K, V> assign(final K key,
                                          final V value)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        final HashTrieNode<K, V> nodes = this.nodes;
        final MutableDelta sizeDelta = new MutableDelta();
        final int hashCode = key.hashCode();
        HashTrieNode<K, V> newNodes = nodes.assign(hashCode >>> 5, hashCode & 0x1f, key, value, sizeDelta);
        return (newNodes == nodes) ? this : new JImmutableHashMap<K, V>(newNodes, sizeDelta.apply(size));
    }

    @Override
    public JImmutableHashMap<K, V> delete(final K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        final HashTrieNode<K, V> nodes = this.nodes;
        final MutableDelta sizeDelta = new MutableDelta();
        final int hashCode = key.hashCode();
        HashTrieNode<K, V> newNodes = nodes.delete(hashCode >>> 5, hashCode & 0x1f, key, sizeDelta);
        return (newNodes == nodes) ? this : new JImmutableHashMap<K, V>(newNodes, sizeDelta.apply(size));
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public JImmutableMap<K, V> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return MultiTransformCursor.of(LazyCursor.of(nodes), new Func1<HashTrieValue<K, V>, Cursor<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<K, V>> apply(HashTrieValue<K, V> node)
            {
                return node.cursor();
            }
        });
    }

    public JImmutableMap<Class, Integer> getNodeTypeCounts(JImmutableMap<Class, Integer> map)
    {
        return map;
    }
}
