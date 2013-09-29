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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;

/**
 * HashTrieNode implementation for a single value at arbitrary depth.  Used to keep branch depth
 * to one when only a single key/value pair is stored in that branch.
 *
 * @param <K>
 * @param <V>
 */
public class HashQuickNode<K, V>
        extends AbstractHashTrieNode<K, V>
{
    private final int branchIndex;
    private final int valueIndex;
    private final HashTrieValue<K, V> value;

    public HashQuickNode(int branchIndex,
                         int valueIndex,
                         HashTrieValue<K, V> value)
    {
        this.branchIndex = branchIndex;
        this.valueIndex = valueIndex;
        this.value = value;
    }

    @Override
    public Holder<V> get(int branchIndex,
                         int valueIndex,
                         K key)
    {
        if (branchIndex != this.branchIndex || valueIndex != this.valueIndex) {
            return Holders.of();
        } else {
            return value.getValueForKey(key);
        }
    }

    @Override
    public PersistentMap.Entry<K, V> getEntry(int branchIndex,
                                              int valueIndex,
                                              K key)
    {
        if (branchIndex != this.branchIndex || valueIndex != this.valueIndex) {
            return null;
        } else {
            return value.getEntryForKey(key);
        }
    }

    @Override
    public HashTrieValue<K, V> getTrieValue(int branchIndex,
                                            int valueIndex)
    {
        if (branchIndex != this.branchIndex || valueIndex != this.valueIndex) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public HashTrieNode<K, V> assign(int branchIndex,
                                     int valueIndex,
                                     K key,
                                     V value,
                                     MutableDelta sizeDelta)
    {
        final int thisBranchIndex = this.branchIndex;
        final int thisValueIndex = this.valueIndex;
        if (branchIndex != thisBranchIndex || valueIndex != thisValueIndex) {
            return new HashInteriorNode<K, V>(thisBranchIndex, thisValueIndex, this.value).assign(branchIndex, valueIndex, key, value, sizeDelta);
        } else {
            HashTrieValue<K, V> newValue = this.value.setValueForKey(key, value, sizeDelta);
            return (newValue == this.value) ? this : new HashQuickNode<K, V>(branchIndex, valueIndex, newValue);
        }
    }

    @Override
    public HashTrieNode<K, V> delete(int branchIndex,
                                     int valueIndex,
                                     K key,
                                     MutableDelta sizeDelta)
    {
        final int thisBranchIndex = this.branchIndex;
        final int thisValueIndex = this.valueIndex;
        if (branchIndex != thisBranchIndex || valueIndex != thisValueIndex) {
            return this;
        } else {
            HashTrieValue<K, V> newValue = value.deleteValueForKey(key, sizeDelta);
            if (newValue == null) {
                return HashEmptyNode.of();
            } else if (newValue == value) {
                return this;
            } else {
                return new HashQuickNode<K, V>(thisBranchIndex, thisValueIndex, newValue);
            }
        }
    }

    @Override
    public Cursor<HashTrieValue<K, V>> cursor()
    {
        return SingleValueCursor.of(value);
    }

    @Override
    public int shallowSize()
    {
        return 1;
    }

    @Override
    public int deepSize()
    {
        return value.size();
    }
}
