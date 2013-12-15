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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.cursors.SingleValueCursor;

import java.util.Collection;
import java.util.Comparator;

public class LeafNode<K, V>
        implements TreeNode<K, V>,
                   JImmutableMap.Entry<K, V>,
                   Holder<V>
{
    private final K nodeKey;
    private final V value;

    public LeafNode(K key,
                    V value)
    {
        this.nodeKey = key;
        this.value = value;
    }

    public K getKey()
    {
        return nodeKey;
    }

    public V getValue()
    {
        return value;
    }

    public boolean isEmpty()
    {
        return false;
    }

    public boolean isFilled()
    {
        return true;
    }

    public V getValueOrNull()
    {
        return value;
    }

    public V getValueOr(V defaultValue)
    {
        return value;
    }

    @Override
    public Holder<V> find(Comparator<K> props,
                          K searchKey)
    {
        return props.compare(searchKey, nodeKey) == 0 ? this : Holders.<V>of();
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Comparator<K> props,
                                                       K searchKey)
    {
        return props.compare(searchKey, nodeKey) == 0 ? Holders.<JImmutableMap.Entry<K, V>>of(this) : Holders.<JImmutableMap.Entry<K, V>>of();
    }

    @Override
    public K getMaxKey()
    {
        return nodeKey;
    }

    @Override
    public UpdateResult<K, V> update(Comparator<K> props,
                                     K key,
                                     V value)
    {
        final int diff = props.compare(key, nodeKey);
        if (diff == 0) {
            if (this.value == value) { // value identity - useful for sets, booleans, etc
                return UpdateResult.createUnchanged();
            } else {
                return UpdateResult.createInPlace(new LeafNode<K, V>(key, value), 0);
            }
        } else if (diff < 0) {
            return UpdateResult.createSplit(new LeafNode<K, V>(key, value), this, 1);
        } else {
            return UpdateResult.createSplit(this, new LeafNode<K, V>(key, value), 1);
        }
    }

    @Override
    public void addEntriesTo(Collection<JImmutableMap.Entry<K, V>> collection)
    {
        collection.add(this);
    }

    @Override
    public int verifyDepthsMatch()
    {
        return 1;
    }

    @Override
    public DeleteResult<K, V> delete(Comparator<K> props,
                                     K key)
    {
        if (props.compare(key, nodeKey) == 0) {
            return DeleteResult.createEliminated();
        } else {
            return DeleteResult.createUnchanged();
        }
    }

    @Override
    public DeleteMergeResult<K, V> leftDeleteMerge(Comparator<K> props,
                                                   TreeNode<K, V> node)
    {
        return new DeleteMergeResult<K, V>(new TwoNode<K, V>(node,
                                                             this,
                                                             node.getMaxKey(),
                                                             nodeKey));
    }

    @Override
    public DeleteMergeResult<K, V> rightDeleteMerge(Comparator<K> props,
                                                    TreeNode<K, V> node)
    {
        return new DeleteMergeResult<K, V>(new TwoNode<K, V>(this,
                                                             node,
                                                             nodeKey,
                                                             node.getMaxKey()));
    }

    @Override
    public String toString()
    {
        return String.format("%s => %s", nodeKey, value);
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return SingleValueCursor.<JImmutableMap.Entry<K, V>>of(this);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LeafNode leafNode = (LeafNode)o;

        if (nodeKey != null ? !nodeKey.equals(leafNode.nodeKey) : leafNode.nodeKey != null) {
            return false;
        }
        if (value != null ? !value.equals(leafNode.value) : leafNode.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = nodeKey != null ? nodeKey.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
