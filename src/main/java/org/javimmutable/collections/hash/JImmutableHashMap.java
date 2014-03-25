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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.trie32.Trie32HashTable;
import org.javimmutable.collections.common.AbstractJImmutableMap;

public class JImmutableHashMap<K, V>
        extends AbstractJImmutableMap<K, V>
{
    // we only new one instance of the transformations object
    static final HashValueListTransforms TRANSFORMS = new HashValueListTransforms();

    // we only new one instance of the transformations object
    static final HashValueTreeTransforms COMPARABLE_TRANSFORMS = new HashValueTreeTransforms();

    // this is safe since the transformations object works for any possible K and V
    @SuppressWarnings("unchecked")
    static final JImmutableHashMap EMPTY = new JImmutableHashMap(Trie32HashTable.of(TRANSFORMS));

    // this is safe since the transformations object works for any possible K and V
    @SuppressWarnings("unchecked")
    static final JImmutableHashMap COMPARABLE_EMPTY = new JImmutableHashMap(Trie32HashTable.of(COMPARABLE_TRANSFORMS));

    private final Trie32HashTable<K, V> values;

    private JImmutableHashMap(Trie32HashTable<K, V> values)
    {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashMap<K, V> of()
    {
        return (JImmutableHashMap<K, V>)EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> JImmutableHashMap<K, V> comparableOf()
    {
        return (JImmutableHashMap<K, V>)COMPARABLE_EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashMap<K, V> of(Class<K> klass)
    {
        return klass.isAssignableFrom(Comparable.class) ? COMPARABLE_EMPTY : EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashMap<K, V> forKey(K key)
    {
        return (key instanceof Comparable) ? COMPARABLE_EMPTY : EMPTY;
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        return values.getValueOr(key.hashCode(), key, defaultValue);
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
        final Trie32HashTable<K, V> newValues = values.assign(key.hashCode(), key, value);
        return (newValues == values) ? this : new JImmutableHashMap<K, V>(newValues);
    }

    @Override
    public JImmutableMap<K, V> delete(K key)
    {
        final Trie32HashTable<K, V> newValues = values.delete(key.hashCode(), key);
        return (newValues == values) ? this : ((newValues.size() == 0) ? EmptyHashMap.<K, V>of() : new JImmutableHashMap<K, V>(newValues));
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Override
    public JImmutableMap<K, V> deleteAll()
    {
        return EmptyHashMap.of();
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return values.cursor();
    }

    // for unit test to verify proper transforms selected
    Trie32HashTable.Transforms getTransforms()
    {
        return values.getTransforms();
    }

}
