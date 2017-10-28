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
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.iterators.SplitableIterator;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.TreeNode;

import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Transforms implementation that stores values in TreeNodes (2-3 trees).
 * Usable with keys that implement Comparable.  Will fail with any other
 * type of key.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
class HashValueTreeTransforms<K extends Comparable<K>, V>
        implements Transforms<TreeNode<K, V>, K, V>
{
    private final Comparator<K> comparator = ComparableComparator.of();

    @Override
    public TreeNode<K, V> update(Holder<TreeNode<K, V>> leaf,
                                 K key,
                                 V value,
                                 MutableDelta delta)
    {
        if (leaf.isEmpty()) {
            return TreeNode.<K, V>of().assign(comparator, key, value, delta);
        } else {
            return leaf.getValue().assign(comparator, key, value, delta);
        }
    }

    @Override
    public Holder<TreeNode<K, V>> delete(TreeNode<K, V> leaf,
                                         K key,
                                         MutableDelta delta)
    {
        final TreeNode<K, V> newTree = leaf.delete(comparator, key, delta);
        return (newTree.isEmpty()) ? Holders.<TreeNode<K, V>>of() : Holders.of(newTree);
    }

    @Override
    public Holder<V> findValue(TreeNode<K, V> leaf,
                               K key)
    {
        return leaf.find(comparator, key);
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(TreeNode<K, V> leaf,
                                                       K key)
    {
        return leaf.findEntry(comparator, key);
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(TreeNode<K, V> leaf)
    {
        return leaf.cursor();
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(TreeNode<K, V> leaf)
    {
        return leaf.iterator();
    }
}
