///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.TransformCursor;

/**
 * Full HashTrieNode implementation for up to 32 subbranches and 32 values.  Uses Bit32Arrays for branches
 * and values to minimize memory consumption.
 *
 * @param <K>
 * @param <V>
 */
public class HashInteriorNode<K, V>
        extends AbstractHashTrieNode<K, V>
{
    private final Bit32Array<HashTrieNode<K, V>> branches;
    private final Bit32Array<HashTrieValue<K, V>> values;

    public HashInteriorNode()
    {
        this(Bit32Array.<HashTrieNode<K, V>>of(), Bit32Array.<HashTrieValue<K, V>>of());
    }

    public HashInteriorNode(Bit32Array<HashTrieValue<K, V>> values)
    {
        this(Bit32Array.<HashTrieNode<K, V>>of(), values);
    }

    public HashInteriorNode(int branchIndex,
                            int valueIndex,
                            HashTrieValue<K, V> value)
    {
        if (branchIndex == 0) {
            this.branches = Bit32Array.of();
            this.values = Bit32Array.<HashTrieValue<K, V>>of().assign(valueIndex, value);
        } else {
            this.branches = Bit32Array.<HashTrieNode<K, V>>of().assign(branchIndex & 0x1f, new HashQuickNode<K, V>(branchIndex >>> 5, valueIndex, value));
            this.values = Bit32Array.of();
        }
    }

    public HashInteriorNode(Bit32Array<HashTrieNode<K, V>> branches,
                            Bit32Array<HashTrieValue<K, V>> values)
    {
        this.branches = branches;
        this.values = values;
    }

    @Override
    public Holder<V> get(int branchIndex,
                         int valueIndex,
                         K key)
    {
        HashTrieValue<K, V> value = getTrieValue(branchIndex, valueIndex);
        return value != null ? value.getValueForKey(key) : Holders.<V>of();
    }

    @Override
    public JImmutableMap.Entry<K, V> getEntry(int branchIndex,
                                              int valueIndex,
                                              K key)
    {
        HashTrieValue<K, V> value = getTrieValue(branchIndex, valueIndex);
        return value != null ? value.getEntryForKey(key) : null;
    }

    @Override
    public HashTrieValue<K, V> getTrieValue(int branchIndex,
                                            int valueIndex)
    {
        if (branchIndex == 0) {
            return values.get(valueIndex).getValueOrNull();
        } else {
            HashTrieNode<K, V> branch = branches.get(branchIndex & 0x1f).getValueOrNull();
            return (branch != null) ? branch.getTrieValue(branchIndex >>> 5, valueIndex) : null;
        }
    }

    @Override
    public HashTrieNode<K, V> assign(int branchIndex,
                                     int valueIndex,
                                     K key,
                                     V value,
                                     MutableDelta sizeDelta)
    {
        final Bit32Array<HashTrieNode<K, V>> branches = this.branches;
        final Bit32Array<HashTrieValue<K, V>> values = this.values;
        if (branchIndex == 0) {
            HashTrieValue<K, V> valueNode = values.get(valueIndex).getValueOrNull();
            if (valueNode != null) {
                HashTrieValue<K, V> newValueNode = valueNode.setValueForKey(key, value, sizeDelta);
                return (newValueNode == valueNode) ? this : new HashInteriorNode<K, V>(branches, values.assign(valueIndex, newValueNode));
            } else {
                sizeDelta.add(1);
                return new HashInteriorNode<K, V>(branches, values.assign(valueIndex, new HashTrieSingleValue<K, V>(key, value)));
            }
        } else {
            final int childIndex = branchIndex & 0x1f;
            HashTrieNode<K, V> child = branches.get(childIndex).getValueOrNull();
            HashTrieNode<K, V> newChild;
            if (child == null) {
                sizeDelta.add(1);
                newChild = new HashQuickNode<K, V>(branchIndex >>> 5, valueIndex, new HashTrieSingleValue<K, V>(key, value));
            } else {
                newChild = child.assign(branchIndex >>> 5, valueIndex, key, value, sizeDelta);
            }
            return (newChild == child) ? this : new HashInteriorNode<K, V>(branches.assign(childIndex, newChild), values);
        }
    }

    @Override
    public HashTrieNode<K, V> delete(int branchIndex,
                                     int valueIndex,
                                     K key,
                                     MutableDelta sizeDelta)
    {
        final Bit32Array<HashTrieNode<K, V>> branches = this.branches;
        final Bit32Array<HashTrieValue<K, V>> values = this.values;
        if (branchIndex == 0) {
            final HashTrieValue<K, V> currentValue = values.get(valueIndex).getValueOrNull();
            if (currentValue == null) {
                return this;
            } else {
                HashTrieValue<K, V> newValue = currentValue.deleteValueForKey(key, sizeDelta);
                if (newValue == currentValue) {
                    return this;
                } else if (newValue == null) {
                    return deleteConsolidationImpl(branches, values.delete(valueIndex));
                } else {
                    return new HashInteriorNode<K, V>(branches, values.assign(valueIndex, newValue));
                }
            }
        } else {
            final int nodeIndex = branchIndex & 0x1f;
            HashTrieNode<K, V> currentNode = branches.get(nodeIndex).getValueOrNull();
            if (currentNode == null) {
                return this;
            } else {
                final HashTrieNode<K, V> newNode = currentNode.delete(branchIndex >>> 5, valueIndex, key, sizeDelta);
                if (newNode == currentNode) {
                    return this;
                } else if (newNode.shallowSize() == 0) {
                    return deleteConsolidationImpl(branches.delete(nodeIndex), values);
                } else {
                    return new HashInteriorNode<K, V>(branches.assign(nodeIndex, newNode), values);
                }
            }
        }
    }

    @Override
    public Cursor<HashTrieValue<K, V>> cursor()
    {
        final Cursor<HashTrieValue<K, V>> valuesCursor = TransformCursor.ofValues(LazyCursor.of(values));
        final Cursor<HashTrieValue<K, V>> branchesCursor = MultiTransformCursor.of(TransformCursor.ofValues(LazyCursor.of(branches)), HashTrieNodeToValueCursorFunc.<K, V>of());
        return MultiCursor.of(valuesCursor, branchesCursor);
    }

    @Override
    public int shallowSize()
    {
        return values.size() + branches.size();
    }

    @Override
    public int deepSize()
    {
        int total = 0;
        for (JImmutableMap.Entry<Integer, HashTrieNode<K, V>> branch : branches) {
            total += branch.getValue().deepSize();
        }
        for (JImmutableMap.Entry<Integer, HashTrieValue<K, V>> value : values) {
            total += value.getValue().size();
        }
        return total;
    }

    @Override
    public JImmutableMap<Class, Integer> getNodeTypeCounts(JImmutableMap<Class, Integer> map)
    {
        map = super.getNodeTypeCounts(map);
        for (JImmutableMap.Entry<Integer, HashTrieNode<K, V>> branch : branches) {
            map = branch.getValue().getNodeTypeCounts(map);
        }
        return map;
    }

    private HashTrieNode<K, V> deleteConsolidationImpl(Bit32Array<HashTrieNode<K, V>> branches,
                                                       Bit32Array<HashTrieValue<K, V>> values)
    {
        if (branches.size() == 0) {
            switch (values.size()) {
            case 0:
                return HashEmptyNode.of();
            case 1:
                final int remainingIndex = values.firstIndex();
                return new HashQuickNode<K, V>(0, remainingIndex, values.get(remainingIndex).getValue());
            }
        }
        return new HashInteriorNode<K, V>(branches, values);
    }
}
