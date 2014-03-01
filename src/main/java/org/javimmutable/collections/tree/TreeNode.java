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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;

import java.util.Collection;
import java.util.Comparator;

public interface TreeNode<K, V>
        extends Cursorable<JImmutableMap.Entry<K, V>>
{
    public V getValueOr(Comparator<K> props,
                        K key,
                        V defaultValue);

    public Holder<V> find(Comparator<K> props,
                          K key);

    public Holder<JImmutableMap.Entry<K, V>> findEntry(Comparator<K> props,
                                                       K key);

    public K getMaxKey();

    public UpdateResult<K, V> update(Comparator<K> props,
                                     K key,
                                     V value);

    public void addEntriesTo(Collection<JImmutableMap.Entry<K, V>> collection);

    public int verifyDepthsMatch();

    public DeleteResult<K, V> delete(Comparator<K> props,
                                     K key);

    public DeleteMergeResult<K, V> leftDeleteMerge(Comparator<K> props,
                                                   TreeNode<K, V> node);

    public DeleteMergeResult<K, V> rightDeleteMerge(Comparator<K> props,
                                                    TreeNode<K, V> node);

    public Cursor<JImmutableMap.Entry<K, V>> cursor();
}
