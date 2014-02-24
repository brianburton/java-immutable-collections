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
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.trie32.TransformedTrie32Array;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.MutableDelta;

public class JImmutableHashMap<K, V>
        extends AbstractJImmutableMap<K, V>
{
    private static final JImmutableHashMap EMPTY = new JImmutableHashMap();

    private final TransformedTrie32Array<K, V> values;

    private JImmutableHashMap(TransformedTrie32Array<K, V> values)
    {
        this.values = values;
    }

    private JImmutableHashMap()
    {
        this(TransformedTrie32Array.of(new TransformedTrie32Array.Transforms<K, V>(JImmutableHashMap.<K, V>createUpdater(),
                                                                                   JImmutableHashMap.<K, V>createDeleter(),
                                                                                   JImmutableHashMap.<K, V>createValueGetter(),
                                                                                   JImmutableHashMap.<K, V>createEntryGetter(),
                                                                                   JImmutableHashMap.<K, V>createCursorGetter())));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashMap<K, V> of()
    {
        return (JImmutableHashMap<K, V>)EMPTY;
    }

    @Override
    public Holder<V> find(K key)
    {
        return values.findValue(key.hashCode(), key);
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        return values.findEntry(key.hashCode(), key);
    }

    @Override
    public JImmutableHashMap<K, V> assign(K key,
                                          V value)
    {
        final TransformedTrie32Array<K, V> newValues = values.assign(key.hashCode(), key, value);
        return (newValues == values) ? this : new JImmutableHashMap<K, V>(newValues);
    }

    @Override
    public JImmutableHashMap<K, V> delete(K key)
    {
        final TransformedTrie32Array<K, V> newValues = values.delete(key.hashCode(), key);
        return (newValues == values) ? this : new JImmutableHashMap<K, V>(newValues);
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Override
    public JImmutableHashMap<K, V> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return values.cursor();
    }

    private static <K, V> Func4<Holder<Object>, K, V, MutableDelta, Object> createUpdater()
    {
        return new Func4<Holder<Object>, K, V, MutableDelta, Object>()
        {
            @Override
            public Object apply(Holder<Object> oldLeaf,
                                K key,
                                V value,
                                MutableDelta delta)
            {
                if (oldLeaf.isEmpty()) {
                    delta.add(1);
                    return new SingleValueLeafNode<K, V>(key, value);
                }

                @SuppressWarnings("unchecked") final LeafNode<K, V> oldNode = (LeafNode<K, V>)oldLeaf.getValue();
                return oldNode.setValueForKey(key, value, delta);
            }
        };
    }

    private static <K, V> Func3<Object, K, MutableDelta, Holder<Object>> createDeleter()
    {
        return new Func3<Object, K, MutableDelta, Holder<Object>>()
        {
            @Override
            public Holder<Object> apply(Object oldLeaf,
                                        K key,
                                        MutableDelta delta)
            {
                @SuppressWarnings("unchecked") final LeafNode<K, V> oldNode = (LeafNode<K, V>)oldLeaf;
                final LeafNode<K, V> newNode = oldNode.deleteValueForKey(key, delta);
                return (newNode == null || newNode.size() == 0) ? Holders.of() : Holders.<Object>of(newNode);
            }
        };
    }

    private static <K, V> Func2<Object, K, Holder<V>> createValueGetter()
    {
        return new Func2<Object, K, Holder<V>>()
        {
            @Override
            public Holder<V> apply(Object oldLeaf,
                                   K key)
            {
                @SuppressWarnings("unchecked") final LeafNode<K, V> oldNode = (LeafNode<K, V>)oldLeaf;
                return oldNode.getValueForKey(key);
            }
        };
    }

    private static <K, V> Func2<Object, K, Holder<JImmutableMap.Entry<K, V>>> createEntryGetter()
    {
        return new Func2<Object, K, Holder<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public Holder<JImmutableMap.Entry<K, V>> apply(Object oldLeaf,
                                                           K key)
            {
                @SuppressWarnings("unchecked") final LeafNode<K, V> oldNode = (LeafNode<K, V>)oldLeaf;
                return Holders.fromNullable(oldNode.getEntryForKey(key));
            }
        };
    }

    private static <K, V> Func1<Object, Cursor<Entry<K, V>>> createCursorGetter()
    {
        return new Func1<Object, Cursor<Entry<K, V>>>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public Cursor<Entry<K, V>> apply(Object leaf)
            {
                return ((LeafNode<K, V>)leaf).cursor();
            }
        };
    }
}
