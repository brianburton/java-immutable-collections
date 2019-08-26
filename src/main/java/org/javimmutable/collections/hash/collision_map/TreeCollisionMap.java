///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.hash.collision_map;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Transforms implementation that stores values in Node objects (balanced trees).
 * Usable with keys that implement Comparable.  Will fail with any other
 * type of key.
 */
@Immutable
public class TreeCollisionMap<K extends Comparable<K>, V>
    implements CollisionMap<Node<K, V>, K, V>
{
    private final Comparator<K> comparator = ComparableComparator.of();

    @Nonnull
    @Override
    public Node<K, V> update(Node<K, V> leaf,
                             @Nonnull K key,
                             V value,
                             @Nonnull MutableDelta delta)
    {
        if (leaf == null) {
            delta.add(1);
            return Node.single(key, value);
        } else {
            return resultForUpdate(leaf, delta, leaf.assign(comparator, key, value));
        }
    }

    @Nonnull
    @Override
    public Node<K, V> update(@Nullable Node<K, V> leaf,
                             @Nonnull K key,
                             @Nonnull Func1<Holder<V>, V> generator,
                             @Nonnull MutableDelta delta)
    {
        if (leaf == null) {
            delta.add(1);
            return Node.single(key, generator.apply(Holders.of()));
        } else {
            return resultForUpdate(leaf, delta, leaf.update(comparator, key, generator));
        }
    }

    @Override
    public Node<K, V> delete(@Nonnull Node<K, V> leaf,
                             @Nonnull K key,
                             @Nonnull MutableDelta delta)
    {
        final Node<K, V> newLeaf = leaf.delete(comparator, key);
        if (newLeaf == leaf) {
            return leaf;
        } else {
            delta.add(-1);
            return newLeaf.isEmpty() ? null : newLeaf;
        }
    }

    @Override
    public V getValueOr(@Nonnull Node<K, V> leaf,
                        @Nonnull K key,
                        V defaultValue)
    {
        return leaf.getOr(comparator, key, defaultValue);
    }

    @Override
    public Holder<V> findValue(@Nonnull Node<K, V> leaf,
                               @Nonnull K key)
    {
        return leaf.find(comparator, key);
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Node<K, V> leaf,
                                                       @Nonnull K key)
    {
        return leaf.findEntry(comparator, key);
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(@Nonnull Node<K, V> leaf)
    {
        return leaf.cursor();
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(@Nonnull Node<K, V> leaf)
    {
        return leaf.iterator();
    }

    private Node<K, V> resultForUpdate(Node<K, V> leaf,
                                       @Nonnull MutableDelta delta,
                                       Node<K, V> result)
    {
        delta.add(result.size() - leaf.size());
        return result;
    }
}
