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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.tree.BranchNode;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.LeafNode;
import org.javimmutable.collections.tree.Node;
import org.javimmutable.collections.tree.UpdateResult;

import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Transforms implementation that stores values in Node objects (b-trees trees).
 * Usable with keys that implement Comparable.  Will fail with any other
 * type of key.
 */
@Immutable
class HashValueTreeTransforms<K extends Comparable<K>, V>
    implements Transforms<Node<K, V>, K, V>
{
    private final Comparator<K> comparator = ComparableComparator.of();

    @Override
    public Node<K, V> update(Holder<Node<K, V>> leaf,
                             K key,
                             V value,
                             MutableDelta delta)
    {
        if (leaf.isEmpty()) {
            delta.add(1);
            return new LeafNode<>(key, value);
        } else {
            final Node<K, V> oldLeaf = leaf.getValue();
            final UpdateResult<K, V> result = oldLeaf.assign(comparator, key, value);
            switch (result.type) {
            case UNCHANGED:
                return oldLeaf;
            case INPLACE:
                delta.add(result.sizeDelta);
                return result.newNode;
            case SPLIT:
                delta.add(result.sizeDelta);
                return new BranchNode<>(result.newNode, result.extraNode);
            default:
                throw new IllegalStateException("unknown UpdateResult.Type value");
            }
        }
    }

    @Override
    public Holder<Node<K, V>> delete(Node<K, V> leaf,
                                     K key,
                                     MutableDelta delta)
    {
        final Node<K, V> newLeaf = leaf.delete(comparator, key);
        if (newLeaf == leaf) {
            return Holders.of(leaf);
        } else {
            delta.add(-1);
            return newLeaf.isEmpty() ? Holders.of() : Holders.of(newLeaf.compress());
        }
    }

    @Override
    public Holder<V> findValue(Node<K, V> leaf,
                               K key)
    {
        return leaf.find(comparator, key);
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Node<K, V> leaf,
                                                       K key)
    {
        return leaf.findEntry(comparator, key);
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(Node<K, V> leaf)
    {
        return leaf.cursor();
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(Node<K, V> leaf)
    {
        return leaf.iterator();
    }
}
