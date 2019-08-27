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
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;
import java.util.Objects;

@Immutable
public class LeafNode<K, V>
    implements Node<K, V>,
               JImmutableMap.Entry<K, V>,
               Holders.Filled<V>
{
    private final K key;
    private final V value;

    public LeafNode(@Nonnull K key,
                    V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public K baseKey()
    {
        return key;
    }

    @Override
    public int childCount()
    {
        return 1;
    }

    @Override
    public int valueCount()
    {
        return 1;
    }

    @Override
    public V getValueOr(@Nonnull Comparator<K> comparator,
                        @Nonnull K key,
                        V defaultValue)
    {
        return comparator.compare(this.key, key) == 0 ? value : defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull Comparator<K> comparator,
                          @Nonnull K key)
    {
        return comparator.compare(this.key, key) == 0 ? this : Holders.of();
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Comparator<K> comparator,
                                                       @Nonnull K key)
    {
        return comparator.compare(this.key, key) == 0 ? Holders.of(this) : Holders.of();
    }

    @Nonnull
    @Override
    public UpdateResult<K, V> assign(@Nonnull Comparator<K> comparator,
                                     @Nonnull K key,
                                     V value)
    {
        final LeafNode<K, V> newLeaf = new LeafNode<>(key, value);
        final int diff = comparator.compare(this.key, key);
        if (diff == 0) {
            return (this.value == value) ? UpdateResult.createUnchanged() : UpdateResult.createInPlace(newLeaf, 0);
        } else {
            return (diff < 0) ? UpdateResult.createSplit(this, newLeaf, 1) : UpdateResult.createSplit(newLeaf, this, 1);
        }
    }

    @Nonnull
    @Override
    public UpdateResult<K, V> update(@Nonnull Comparator<K> comparator,
                                     @Nonnull K key,
                                     @Nonnull Func1<Holder<V>, V> generator)
    {
        final int diff = comparator.compare(this.key, key);
        if (diff == 0) {
            final V value = generator.apply(this);
            final LeafNode<K, V> newLeaf = new LeafNode<>(key, value);
            return (this.value == value) ? UpdateResult.createUnchanged() : UpdateResult.createInPlace(newLeaf, 0);
        } else {
            final LeafNode<K, V> newLeaf = new LeafNode<>(key, generator.apply(Holders.of()));
            return (diff < 0) ? UpdateResult.createSplit(this, newLeaf, 1) : UpdateResult.createSplit(newLeaf, this, 1);
        }
    }

    @Nonnull
    @Override
    public Node<K, V> delete(@Nonnull Comparator<K> comparator,
                             @Nonnull K key)
    {
        final int diff = comparator.compare(this.key, key);
        return (diff == 0) ? EmptyNode.of() : this;
    }

    @Nonnull
    @Override
    public Node<K, V> mergeChildren(@Nonnull Node<K, V> sibling)
    {
        return new BranchNode<>(this, sibling);
    }

    @Nonnull
    @Override
    public Tuple2<Node<K, V>, Node<K, V>> distributeChildren(@Nonnull Node<K, V> sibling)
    {
        return Tuple2.of(this, sibling);
    }

    @Nonnull
    @Override
    public Node<K, V> compress()
    {
        return this;
    }

    @Override
    public int depth()
    {
        return 0;
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return SingleValueIterator.of(this);
    }

    @Override
    public void checkInvariants(@Nonnull Comparator<K> comparator)
    {
    }

    @Nonnull
    @Override
    public K getKey()
    {
        return key;
    }

    @Override
    public V getValue()
    {
        return value;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "<" + key + "," + value + ">";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LeafNode<?, ?> leafNode = (LeafNode<?, ?>)o;
        return Objects.equals(key, leafNode.key) &&
               Objects.equals(value, leafNode.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, value);
    }
}
