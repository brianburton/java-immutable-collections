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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.cursors.EmptyCursor;
import org.javimmutable.collections.common.MutableDelta;

/**
 * Trivial HashTrieNode implementation for an empty node.  gets and deletes do nothing and sets
 * create HashQuickNode instances.
 *
 * @param <K>
 * @param <V>
 */
public class HashEmptyNode<K, V>
        extends AbstractHashTrieNode<K, V>
{
    private static final HashEmptyNode EMPTY = new HashEmptyNode();

    @SuppressWarnings("unchecked")
    public static <K, V> HashEmptyNode<K, V> of()
    {
        return (HashEmptyNode<K, V>)EMPTY;
    }

    @Override
    public Holder<V> get(int branchIndex,
                         int valueIndex,
                         K key)
    {
        return Holders.of();
    }

    @Override
    public PersistentMap.Entry<K, V> getEntry(int branchIndex,
                                              int valueIndex,
                                              K key)
    {
        return null;
    }

    @Override
    public HashTrieValue<K, V> getTrieValue(int branchIndex,
                                            int valueIndex)
    {
        return null;
    }

    @Override
    public HashTrieNode<K, V> set(int branchIndex,
                                  int valueIndex,
                                  K key,
                                  V value,
                                  MutableDelta sizeDelta)
    {
        sizeDelta.add(1);
        return new HashQuickNode<K, V>(branchIndex, valueIndex, new HashTrieSingleValue<K, V>(key, value));
    }

    @Override
    public HashTrieNode<K, V> delete(int branchIndex,
                                     int valueIndex,
                                     K key,
                                     MutableDelta sizeDelta)
    {
        return this;
    }

    @Override
    public Cursor<HashTrieValue<K, V>> cursor()
    {
        return EmptyCursor.of();
    }

    @Override
    public int shallowSize()
    {
        return 0;
    }

    @Override
    public int deepSize()
    {
        return 0;
    }
}
