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
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.int_trie.Transforms;
import org.javimmutable.collections.common.MutableDelta;

class HashValueListTransforms<K, V>
        implements Transforms<HashValueListNode<K, V>, K, V>
{
    @Override
    public HashValueListNode<K, V> update(Holder<HashValueListNode<K, V>> oldLeaf,
                                          K key,
                                          V value,
                                          MutableDelta delta)
    {
        if (oldLeaf.isEmpty()) {
            delta.add(1);
            return SingleHashValueListNode.of(key, value);
        } else {
            return oldLeaf.getValue().setValueForKey(key, value, delta);
        }
    }

    @Override
    public Holder<HashValueListNode<K, V>> delete(HashValueListNode<K, V> oldLeaf,
                                                  K key,
                                                  MutableDelta delta)
    {
        return Holders.fromNullable(oldLeaf.deleteValueForKey(key, delta));
    }

    @Override
    public Holder<V> findValue(HashValueListNode<K, V> oldLeaf,
                               K key)
    {
        return oldLeaf.getValueForKey(key);
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(HashValueListNode<K, V> oldLeaf,
                                                       K key)
    {
        return Holders.fromNullable(oldLeaf.getEntryForKey(key));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(HashValueListNode<K, V> leaf)
    {
        return leaf.cursor();
    }
}
