///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

import static org.javimmutable.collections.MapEntry.entry;
import static org.javimmutable.collections.common.HamtLongMath.liftedHashCode;

/**
 * HamtNode that stores only one key/value pair.  Any assign that would progress down the tree
 * below this node replaces it with a branch node instead.  These exist to shorten the
 * height of the overall tree structure when hashCodes are dispersed.
 */
@Immutable
public class MapSingleKeyLeafNode<K, V>
    implements MapNode<K, V>
{
    private final int hashCode;
    @Nonnull
    private final K key;
    private final V value;

    MapSingleKeyLeafNode(int hashCode,
                         @Nonnull K key,
                         @Nullable V value)
    {
        this.hashCode = hashCode;
        this.key = key;
        this.value = value;
    }

    MapSingleKeyLeafNode(@Nonnull CollisionMap<K, V> collisionMap,
                         int hashCode,
                         @Nonnull CollisionMap.Node node)
    {
        assert collisionMap.size(node) == 1;
        final JImmutableMap.Entry<K, V> entry = collisionMap.iterator(node).next();
        this.hashCode = hashCode;
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return 1;
    }

    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        if (this.hashCode == hashCode && key.equals(hashKey)) {
            return Holders.of(value);
        } else {
            return Holders.of();
        }
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                        int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        if (this.hashCode == hashCode && key.equals(hashKey)) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public MapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey,
                                @Nullable V newValue)
    {
        final int thisHashCode = this.hashCode;
        final K thisKey = this.key;
        final V thisValue = this.value;
        if (thisHashCode == hashCode) {
            if (thisKey.equals(hashKey)) {
                if (newValue == thisValue) {
                    return this;
                } else {
                    return new MapSingleKeyLeafNode<>(hashCode, thisKey, newValue);
                }
            } else {
                final CollisionMap.Node thisNode = collisionMap.single(thisKey, thisValue);
                return new MapMultiKeyLeafNode<>(hashCode, collisionMap.update(thisNode, hashKey, newValue));
            }
        } else {
            final MapNode<K, V> expanded = MapBranchNode.forLeafExpansion(collisionMap, thisHashCode, thisKey, thisValue);
            return expanded.assign(collisionMap, hashCode, hashKey, newValue);
        }
    }

    @Nonnull
    @Override
    public MapNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey,
                                @Nonnull Func1<Holder<V>, V> generator)
    {
        final int thisHashCode = this.hashCode;
        final K thisKey = this.key;
        final V thisValue = this.value;
        if (thisHashCode == hashCode) {
            if (thisKey.equals(hashKey)) {
                final V newValue = generator.apply(Holders.of(thisValue));
                if (newValue == thisValue) {
                    return this;
                } else {
                    return new MapSingleKeyLeafNode<>(hashCode, thisKey, newValue);
                }
            } else {
                final V newValue = generator.apply(Holders.of());
                final CollisionMap.Node thisNode = collisionMap.single(thisKey, thisValue);
                return new MapMultiKeyLeafNode<>(hashCode, collisionMap.update(thisNode, hashKey, newValue));
            }
        } else {
            final MapNode<K, V> expanded = MapBranchNode.forLeafExpansion(collisionMap, thisHashCode, thisKey, thisValue);
            return expanded.update(collisionMap, hashCode, hashKey, generator);
        }
    }

    @Nonnull
    @Override
    public MapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey)
    {
        if (this.hashCode == hashCode && key.equals(hashKey)) {
            return MapEmptyNode.of();
        } else {
            return this;
        }
    }

    @Nonnull
    public MapNode<K, V> liftNode(int index)
    {
        return new MapSingleKeyLeafNode<>(liftedHashCode(hashCode, index), key, value);
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return false;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nonnull CollisionMap<K, V> collisionMap,
                                                                             @Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        return GenericIterator.valueState(parent, entry(key, value));
    }

    @Override
    public void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull Proc2<K, V> proc)
    {
        proc.apply(key, value);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        proc.apply(key, value);
    }

    @Override
    public <R> R reduce(@Nonnull CollisionMap<K, V> collisionMap,
                        R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return proc.apply(sum, key, value);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                   R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return proc.apply(sum, key, value);
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
        MapSingleKeyLeafNode<?, ?> that = (MapSingleKeyLeafNode<?, ?>)o;
        return hashCode == that.hashCode &&
               key.equals(that.key) &&
               Objects.equals(value, that.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hashCode, key, value);
    }

    @Override
    public String toString()
    {
        return "(0x" + Integer.toHexString(hashCode) + "," + key + "," + value + ")";
    }
}
