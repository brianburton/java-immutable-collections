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
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECINDIRECINCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACSTRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
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
    private final CollisionMap<K, V> value;

    HamtLeafNode(int hashCode,
                 @Nonnull CollisionMap<K, V> value)
    {
        this.hashCode = hashCode;
        this.value = value;
    }

    @Override
    public int size()
    {
        return value.size();
    }

    @Override
    public Holder<V> find(int hashCode,
                          @Nonnull K hashKey)
    {
        if (hashCode == this.hashCode) {
            return value.findValue(hashKey);
        } else {
            return Holders.of();
        }
    }

    @Override
    public V getValueOr(int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        if (hashCode == this.hashCode) {
            return value.getValueOr(hashKey, defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> assign(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nullable V value)
    {
        final int thisHashCode = this.hashCode;
        final CollisionMap<K, V> thisValue = this.value;
        if (hashCode == thisHashCode) {
            final CollisionMap<K, V> newValue = thisValue.update(hashKey, value);
            if (newValue == thisValue) {
                return this;
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else if (Integer.numberOfLeadingZeros(thisHashCode) < Integer.numberOfLeadingZeros(hashCode)) {
            // our path is longer so expand using new value then add our values to tree
            HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(emptyMap, hashCode, emptyMap.update(hashKey, value));
            for (JImmutableMap.Entry<K, V> entry : thisValue) {
                expanded = expanded.assign(emptyMap, thisHashCode, entry.getKey(), entry.getValue());
            }
            return expanded;
        } else {
            // our path is shorter so expand using our hashcode then add new value to tree
            final HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(emptyMap, thisHashCode, thisValue);
            return expanded.assign(emptyMap, hashCode, hashKey, value);
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> update(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nonnull Func1<Holder<V>, V> generator)
    {
        final int thisHashCode = this.hashCode;
        final CollisionMap<K, V> thisValue = this.value;
        if (hashCode == thisHashCode) {
            final CollisionMap<K, V> newValue = value.update(hashKey, generator);
            if (newValue == thisValue) {
                return this;
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else if (Integer.numberOfLeadingZeros(thisHashCode) < Integer.numberOfLeadingZeros(hashCode)) {
            // our path is longer so expand using new value then add our values to tree
            HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(emptyMap, hashCode, emptyMap.update(hashKey, generator));
            for (JImmutableMap.Entry<K, V> entry : thisValue) {
                expanded = expanded.assign(emptyMap, thisHashCode, entry.getKey(), entry.getValue());
            }
            return expanded;
        } else {
            // our path is shorter so expand using our hashcode then add new value to tree
            final HamtNode<K, V> expanded = HamtBranchNode.forLeafExpansion(emptyMap, thisHashCode, thisValue);
            return expanded.update(emptyMap, hashCode, hashKey, generator);
        }
    }

    @Nonnull
    @Override
    public HamtNode<K, V> delete(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey)
    {
        final int thisHashCode = this.hashCode;
        final CollisionMap<K, V> thisValue = this.value;
        if (hashCode == thisHashCode) {
            final CollisionMap<K, V> newValue = thisValue.delete(hashKey);
            if (newValue == thisValue) {
                return this;
            } else if (newValue.size() == 0) {
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
    public boolean isEmpty()
    {
        return false;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        return value.iterateOverRange(parent, offset, limit);
    }
}
