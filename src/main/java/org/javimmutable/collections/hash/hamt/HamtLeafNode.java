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

package org.javimmutable.collections.hash.hamt;

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

/**
 * HamtNode that stores only one value.  Any assign that would progress down the tree
 * below this node replaces it with a normal node instead.  These exist to shorten the
 * height of the overall tree structure when hashCodes are dispersed.
 */
public class HamtLeafNode<K, V>
    implements HamtNode<K, V>
{
    private final int hashCode;
    @Nonnull
    private final CollisionMap.Node value;

    HamtLeafNode(int hashCode,
                 @Nonnull CollisionMap.Node value)
    {
        this.hashCode = hashCode;
        this.value = value;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return collisionMap.size(value);
    }

    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        if (hashCode == this.hashCode) {
            return collisionMap.findValue(value, hashKey);
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
        if (hashCode == this.hashCode) {
            return collisionMap.getValueOr(value, hashKey, defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nullable V value)
    {
        final int thisHashCode = this.hashCode;
        final CollisionMap.Node thisValue = this.value;
        if (hashCode == thisHashCode) {
            final CollisionMap.Node newValue = collisionMap.update(thisValue, hashKey, value);
            if (newValue == thisValue) {
                return this;
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else if (Integer.numberOfLeadingZeros(thisHashCode) < Integer.numberOfLeadingZeros(hashCode)) {
            // our path is longer so expand using new value then add our values to tree
            HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(collisionMap, hashCode, collisionMap.update(collisionMap.emptyNode(), hashKey, value));
            for (JImmutableMap.Entry<K, V> entry : collisionMap.iterable(thisValue)) {
                expanded = expanded.assign(collisionMap, thisHashCode, entry.getKey(), entry.getValue());
            }
            return expanded;
        } else {
            // our path is shorter so expand using our hashcode then add new value to tree
            final HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(collisionMap, thisHashCode, thisValue);
            return expanded.assign(collisionMap, hashCode, hashKey, value);
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nonnull Func1<Holder<V>, V> generator)
    {
        final int thisHashCode = this.hashCode;
        final CollisionMap.Node thisValue = this.value;
        if (hashCode == thisHashCode) {
            final CollisionMap.Node newValue = collisionMap.update(value, hashKey, generator);
            if (newValue == thisValue) {
                return this;
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else if (Integer.numberOfLeadingZeros(thisHashCode) < Integer.numberOfLeadingZeros(hashCode)) {
            // our path is longer so expand using new value then add our values to tree
            HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(collisionMap, hashCode, collisionMap.update(collisionMap.emptyNode(), hashKey, generator));
            for (JImmutableMap.Entry<K, V> entry : collisionMap.iterable(thisValue)) {
                expanded = expanded.assign(collisionMap, thisHashCode, entry.getKey(), entry.getValue());
            }
            return expanded;
        } else {
            // our path is shorter so expand using our hashcode then add new value to tree
            final HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(collisionMap, thisHashCode, thisValue);
            return expanded.update(collisionMap, hashCode, hashKey, generator);
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                 int hashCode,
                                 @Nonnull K hashKey)
    {
        final int thisHashCode = this.hashCode;
        final CollisionMap.Node thisValue = this.value;
        if (hashCode == thisHashCode) {
            final CollisionMap.Node newValue = collisionMap.delete(thisValue, hashKey);
            if (newValue == thisValue) {
                return this;
            } else if (collisionMap.size(newValue) == 0) {
                return HamtEmptyNode.of();
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else {
            return this;
        }
    }

    public HamtNode<K, V> liftNode(int index)
    {
        return new HamtLeafNode<>(hashCode << HamtBranchNode.SHIFT | index, value);
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return collisionMap.size(value) == 0;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nonnull CollisionMap<K, V> collisionMap,
                                                                             @Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        return collisionMap.iterateOverRange(value, parent, offset, limit);
    }

    @Override
    public void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull Proc2<K, V> proc)
    {
        collisionMap.forEach(value, proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        collisionMap.forEachThrows(value, proc);
    }

    @Override
    public <R> R reduce(@Nonnull CollisionMap<K, V> collisionMap,
                        R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return collisionMap.reduce(value, sum, proc);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                   R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return collisionMap.reduceThrows(value, sum, proc);
    }
}
