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

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.hash.collision_map.CollisionMap;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * HamtNode that stores only one value.  Any assign that would progress down the tree
 * below this node replaces it with a normal node instead.  These exist to shorten the
 * height of the overall tree structure when hashCodes are dispersed.
 */
public class HamtLeafNode<T, K, V>
    implements HamtNode<T, K, V>
{
    private final int hashCode;
    @Nonnull
    private final T value;

    HamtLeafNode(int hashCode,
                 @Nonnull T value)
    {
        this.hashCode = hashCode;
        this.value = value;
    }

    @Override
    public Holder<V> find(@Nonnull CollisionMap<T, K, V> collisionMap,
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
    public V getValueOr(@Nonnull CollisionMap<T, K, V> collisionMap,
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
    public HamtNode<T, K, V> assign(@Nonnull CollisionMap<T, K, V> collisionMap,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nullable V value,
                                    @Nonnull MutableDelta sizeDelta)
    {
        final int thisHashCode = this.hashCode;
        final T thisValue = this.value;
        if (hashCode == thisHashCode) {
            final T newValue = collisionMap.update(thisValue, hashKey, value, sizeDelta);
            if (newValue == thisValue) {
                return this;
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else if (Integer.numberOfLeadingZeros(thisHashCode) < Integer.numberOfLeadingZeros(hashCode)) {
            // our path is longer so expand using new value then add our values to tree
            final MutableDelta ignored = new MutableDelta();
            final Iterator<JImmutableMap.Entry<K, V>> entries = collisionMap.iterator(thisValue);
            HamtNode<T, K, V> expanded = HamtBranchNode.forLeafExpansion(hashCode, collisionMap.update(null, hashKey, value, sizeDelta));
            while (entries.hasNext()) {
                JImmutableMap.Entry<K, V> entry = entries.next();
                expanded = expanded.assign(collisionMap, thisHashCode, entry.getKey(), entry.getValue(), ignored);
            }
            return expanded;
        } else {
            // our path is shorter so expand using our hashcode then add new value to tree
            final HamtNode<T, K, V> expanded = HamtBranchNode.forLeafExpansion(thisHashCode, thisValue);
            return expanded.assign(collisionMap, hashCode, hashKey, value, sizeDelta);
        }
    }

    @Nonnull
    @Override
    public HamtNode<T, K, V> delete(@Nonnull CollisionMap<T, K, V> collisionMap,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nonnull MutableDelta sizeDelta)
    {
        final int thisHashCode = this.hashCode;
        final T thisValue = this.value;
        if (hashCode == this.hashCode) {
            final T newValue = collisionMap.delete(thisValue, hashKey, sizeDelta);
            if (newValue == thisValue) {
                return this;
            } else if (newValue == null) {
                return HamtEmptyNode.of();
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else {
            return this;
        }
    }

    public HamtNode<T, K, V> liftNode(int index)
    {
        return new HamtLeafNode<>(hashCode << HamtBranchNode.SHIFT | index, value);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(CollisionMap<T, K, V> collisionMap)
    {
        return collisionMap.iterator(value);
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(CollisionMap<T, K, V> collisionMap)
    {
        return collisionMap.cursor(value);
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return SingleValueIterator.of(value);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return SingleValueCursor.of(value);
    }
}
