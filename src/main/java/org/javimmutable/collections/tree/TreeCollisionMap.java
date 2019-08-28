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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Transforms implementation that stores values in Node objects (balanced trees).
 * Usable with keys that implement Comparable.  Will fail with any other
 * type of key.
 */
@Immutable
public class TreeCollisionMap<K, V>
    implements CollisionMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final TreeCollisionMap EMPTY = new TreeCollisionMap(ComparableComparator.of(), FringeNode.instance());

    private final Comparator<K> comparator;
    private final AbstractNode<K, V> root;

    private TreeCollisionMap(@Nonnull Comparator<K> comparator,
                             @Nonnull AbstractNode<K, V> root)
    {
        this.comparator = comparator;
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> TreeCollisionMap<K, V> empty()
    {
        return (TreeCollisionMap<K, V>)EMPTY;
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Nonnull
    @Override
    public TreeCollisionMap<K, V> update(@Nonnull K key,
                                         V value)
    {
        return resultForUpdate(root.assign(comparator, key, value));
    }

    @Nonnull
    @Override
    public TreeCollisionMap<K, V> update(@Nonnull K key,
                                         @Nonnull Func1<Holder<V>, V> generator)
    {
        return resultForUpdate(root.update(comparator, key, generator));
    }

    @Nonnull
    @Override
    public TreeCollisionMap<K, V> delete(@Nonnull K key)
    {
        return resultForDelete(root.delete(comparator, key));
    }

    @Override
    public V getValueOr(@Nonnull K key,
                        V defaultValue)
    {
        return root.get(comparator, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> findValue(@Nonnull K key)
    {
        return root.find(comparator, key);
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull K key)
    {
        return root.findEntry(comparator, key);
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return root.iterator();
    }

    AbstractNode<K, V> root()
    {
        return root;
    }

    @Nonnull
    private TreeCollisionMap<K, V> resultForUpdate(AbstractNode<K, V> root)
    {
        if (root == this.root) {
            return this;
        } else {
            return new TreeCollisionMap<>(comparator, root);
        }
    }

    @Nonnull
    private TreeCollisionMap<K, V> resultForDelete(AbstractNode<K, V> root)
    {
        if (root == this.root) {
            return this;
        } else if (root.isEmpty()) {
            return empty();
        } else {
            return new TreeCollisionMap<>(comparator, root);
        }
    }
}
