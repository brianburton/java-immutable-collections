///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collections.array;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IndexedProc1;
import org.javimmutable.collections.IndexedProc1Throws;
import org.javimmutable.collections.IntFunc2;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.IntArrayMappedTrieMath;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import static org.javimmutable.collections.common.BitmaskMath.*;
import static org.javimmutable.collections.common.IntArrayMappedTrieMath.*;

/**
 * Implements an array mapped trie using integers as keys.  When iterating keys
 * are visited in signed-integer order.
 */
public class TrieArrayNode<T>
{
    static final int LEAF_SHIFT_COUNT = 0;
    static final int ROOT_SHIFT_COUNT = IntArrayMappedTrieMath.maxShiftsForBitCount(30);
    private static final int SIGN_BIT = 1 << 31;

    private static final Object[] EMPTY_VALUES = new Object[0];
    @SuppressWarnings({"rawtypes"})
    private static final TrieArrayNode[] EMPTY_NODES = new TrieArrayNode[0];
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TrieArrayNode EMPTY = new TrieArrayNode(ROOT_SHIFT_COUNT, 0, 0L, EMPTY_VALUES, 0L, EMPTY_NODES, 0);

    private final int shiftCount;
    private final int baseIndex;
    private final long valuesBitmask;
    private final T[] values;
    private final long nodesBitmask;
    private final TrieArrayNode<T>[] nodes;
    private final int size;

    TrieArrayNode(int shiftCount,
                  int baseIndex,
                  long valuesBitmask,
                  T[] values,
                  long nodesBitmask,
                  @Nonnull TrieArrayNode<T>[] nodes,
                  int size)
    {
        assert bitCount(valuesBitmask) == values.length;
        assert bitCount(nodesBitmask) == nodes.length;
        this.shiftCount = shiftCount;
        this.baseIndex = baseIndex;
        this.valuesBitmask = valuesBitmask;
        this.values = values;
        this.nodesBitmask = nodesBitmask;
        this.nodes = nodes;
        this.size = size;
        assert checkChildShifts(shiftCount, nodes);
//        assert computeSize(nodes) + values.length == size;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> TrieArrayNode<T> empty()
    {
        return (TrieArrayNode<T>)EMPTY;
    }

    @Nonnull
    private static <T> TrieArrayNode<T> forValue(int shiftCount,
                                                 int index,
                                                 T value)
    {
        assert shiftCount == findShiftForIndex(index);
        final int baseIndex = baseIndexAtShift(shiftCount, index);
        final long valueBitmask = bitFromIndex(indexAtShift(shiftCount, index));
        final T[] values = ArrayHelper.newArray(value);
        final long nodeBitmask = 0L;
        final TrieArrayNode<T>[] nodes = emptyNodes();
        return new TrieArrayNode<>(shiftCount, baseIndex, valueBitmask, values, nodeBitmask, nodes, 1);
    }

    @Nonnull
    private static <T> TrieArrayNode<T> forNode(int shiftCount,
                                                int nodeBaseIndex,
                                                @Nonnull TrieArrayNode<T> node)
    {
        final int baseIndex = baseIndexAtShift(shiftCount, nodeBaseIndex);
        final long valueBitmask = 0L;
        final T[] values = emptyValues();
        final long nodeBitmask = bitFromIndex(indexAtShift(shiftCount, nodeBaseIndex));
        final TrieArrayNode<T>[] nodes = allocateNodes(1);
        nodes[0] = node;
        return new TrieArrayNode<>(shiftCount, baseIndex, valueBitmask, values, nodeBitmask, nodes, node.size());
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public T getValueOr(int index,
                        T defaultValue)
    {
        index = flip(index);
        final int shiftCountForValue = findShiftForIndex(index);
        return getValueOrImpl(shiftCountForValue, index, defaultValue);
    }

    @Nonnull
    public Holder<T> find(int index)
    {
        index = flip(index);
        final int shiftCountForValue = findShiftForIndex(index);
        return findImpl(shiftCountForValue, index, () -> Holder.none(), value -> Holders.nullable(value));
    }

    @Nonnull
    public TrieArrayNode<T> assign(int index,
                                   T value)
    {
        index = flip(index);
        final int shiftCountForValue = findShiftForIndex(index);
        return assignImpl(ROOT_SHIFT_COUNT, shiftCountForValue, index, value);
    }

    @Nonnull
    public TrieArrayNode<T> delete(int index)
    {
        index = flip(index);
        final int shiftCountForValue = findShiftForIndex(index);
        return deleteImpl(shiftCountForValue, index);
    }

    public <K> boolean mappedContains(@Nonnull ArrayContainsMapper<K, T> mapper,
                                      @Nonnull K key)
    {
        final T node = getNodeFofHashKey(key);
        return node != null && mapper.mappedContains(node, key);
    }

    public <K, V> V mappedGetValueOr(@Nonnull ArrayGetMapper<K, V, T> mapper,
                                     @Nonnull K key,
                                     V defaultValue)
    {
        final T node = getNodeFofHashKey(key);
        return node != null ? mapper.mappedGetValueOr(node, key, defaultValue) : defaultValue;
    }

    @Nonnull
    public <K, V> Holder<V> mappedFind(@Nonnull ArrayGetMapper<K, V, T> mapper,
                                       @Nonnull K key)
    {
        final T node = getNodeFofHashKey(key);
        return node != null ? mapper.mappedFind(node, key) : Holder.none();
    }

    @Nonnull
    public <K, V> Holder<IMapEntry<K, V>> mappedFindEntry(@Nonnull ArrayFindEntryMapper<K, V, T> mapper,
                                                          @Nonnull K key)
    {
        final T node = getNodeFofHashKey(key);
        return node != null ? mapper.mappedFindEntry(node, key) : Holder.none();
    }

    @Nonnull
    public <K, V> TrieArrayNode<T> mappedAssign(@Nonnull ArrayAssignMapper<K, V, T> mapper,
                                                @Nonnull K key,
                                                V value)
    {
        final int index = flip(key.hashCode());
        final int shiftCountForValue = findShiftForIndex(index);
        return mappedAssignImpl(ROOT_SHIFT_COUNT, shiftCountForValue, index, mapper, key, value);
    }

    @Nonnull
    public <K, V> TrieArrayNode<T> mappedUpdate(@Nonnull ArrayUpdateMapper<K, V, T> mapper,
                                                @Nonnull K key,
                                                @Nonnull Func1<Holder<V>, V> generator)
    {
        final int index = flip(key.hashCode());
        final int shiftCountForValue = findShiftForIndex(index);
        return mappedUpdateImpl(ROOT_SHIFT_COUNT, shiftCountForValue, index, mapper, key, generator);
    }

    @Nonnull
    public <K> TrieArrayNode<T> mappedDelete(@Nonnull ArrayDeleteMapper<K, T> mapper,
                                             @Nonnull K key)
    {
        final int index = flip(key.hashCode());
        final int shiftCountForValue = findShiftForIndex(index);
        return mappedDeleteImpl(shiftCountForValue, index, mapper, key);
    }

    public void forEach(@Nonnull Proc1<T> proc)
    {
        long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
        while (combinedBitmask != 0) {
            final long bit = leastBit(combinedBitmask);
            if (bitIsPresent(valuesBitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                proc.apply(values[arrayIndex]);
            }
            if (bitIsPresent(nodesBitmask, bit)) {
                final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                nodes[nodeIndex].forEach(proc);
            }
            combinedBitmask = removeBit(combinedBitmask, bit);
        }
    }

    public <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
        while (combinedBitmask != 0) {
            final long bit = leastBit(combinedBitmask);
            if (bitIsPresent(valuesBitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                proc.apply(values[arrayIndex]);
            }
            if (bitIsPresent(nodesBitmask, bit)) {
                final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                nodes[nodeIndex].forEachThrows(proc);
            }
            combinedBitmask = removeBit(combinedBitmask, bit);
        }
    }

    public void forEach(@Nonnull IndexedProc1<T> proc)
    {
        long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
        while (combinedBitmask != 0) {
            final long bit = leastBit(combinedBitmask);
            if (bitIsPresent(valuesBitmask, bit)) {
                final int valueIndex = indexForBit(bit);
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                final int entryIndex = baseIndex + shift(shiftCount, valueIndex);
                proc.apply(flip(entryIndex), values[arrayIndex]);
            }
            if (bitIsPresent(nodesBitmask, bit)) {
                final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                nodes[nodeIndex].forEach(proc);
            }
            combinedBitmask = removeBit(combinedBitmask, bit);
        }
    }

    public <E extends Exception> void forEachThrows(@Nonnull IndexedProc1Throws<T, E> proc)
        throws E
    {
        long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
        while (combinedBitmask != 0) {
            final long bit = leastBit(combinedBitmask);
            if (bitIsPresent(valuesBitmask, bit)) {
                final int valueIndex = indexForBit(bit);
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                final int entryIndex = baseIndex + shift(shiftCount, valueIndex);
                proc.apply(flip(entryIndex), values[arrayIndex]);
            }
            if (bitIsPresent(nodesBitmask, bit)) {
                final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                nodes[nodeIndex].forEachThrows(proc);
            }
            combinedBitmask = removeBit(combinedBitmask, bit);
        }
    }

    @Nullable
    private <K> T getNodeFofHashKey(@Nonnull K key)
    {
        final int index = flip(key.hashCode());
        final int shiftCountForValue = findShiftForIndex(index);
        return getValueOrImpl(shiftCountForValue, index, null);
    }

    private T getValueOrImpl(int shiftCountForValue,
                             int index,
                             T defaultValue)
    {
        final int shiftCount = this.shiftCount;
        if (shiftCountForValue > shiftCount) {
            return defaultValue;
        }
        if (baseIndexAtShift(shiftCount, index) != baseIndex) {
            return defaultValue;
        }
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (shiftCountForValue == shiftCount) {
            final long bitmask = this.valuesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return values[arrayIndex];
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return nodes[arrayIndex].getValueOrImpl(shiftCountForValue, index, defaultValue);
            }
        }
        return defaultValue;
    }

    @Nonnull
    private <C> C findImpl(int shiftCountForValue,
                           int index,
                           Func0<C> notFound,
                           Func1<T, C> found)
    {
        final int shiftCount = this.shiftCount;
        if (shiftCountForValue > shiftCount) {
            return notFound.apply();
        }
        if (baseIndexAtShift(shiftCount, index) != baseIndex) {
            return notFound.apply();
        }
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (shiftCountForValue == shiftCount) {
            final long bitmask = this.valuesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return found.apply(values[arrayIndex]);
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return nodes[arrayIndex].findImpl(shiftCountForValue, index, notFound, found);
            }
        }
        return notFound.apply();
    }

    @Nonnull
    private TrieArrayNode<T> assignImpl(int shiftCount,
                                        int shiftCountForValue,
                                        int index,
                                        T value)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        assert baseIndexAtShift(shiftCount, index) == baseIndexAtShift(shiftCount, baseIndex);
        assert shiftCount >= thisShiftCount;
        assert shiftCount >= shiftCountForValue;
        if (shiftCount != thisShiftCount) {
            // We are lower in tree than our parent expects, see if we need to create an ancestor to hold the value.
            // This happens when we've skipped intermediate nodes for efficiency and one of those nodes needs to be
            // inserted now because we are assigning a value that goes down a different branch than this node.
            final int ancestorShiftCount = findCommonAncestorShift(baseIndex + shift(thisShiftCount, 1), index);
            assert ancestorShiftCount <= shiftCount;
            if (ancestorShiftCount > thisShiftCount) {
                final TrieArrayNode<T> ancestor = forNode(ancestorShiftCount, baseIndex, this);
                return ancestor.assignImpl(ancestorShiftCount, shiftCountForValue, index, value);
            }
            shiftCount = thisShiftCount;
        }
        // If we've gotten here we know the value belongs either in this node or in one of our descendent nodes.
        assert baseIndexAtShift(shiftCount, index) == baseIndex;
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        final long nodesBitmask = this.nodesBitmask;
        if (shiftCount == shiftCountForValue) {
            // Store the value in this node.
            final T[] values = this.values;
            final long newBitmask = addBit(valuesBitmask, bit);
            final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
            if (bitIsPresent(valuesBitmask, bit)) {
                final T[] newValues = ArrayHelper.assign(values, arrayIndex, value);
                return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size);
            } else {
                final T[] newValues = ArrayHelper.insert(TrieArrayNode::allocateValues, values, arrayIndex, value);
                return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            // Store the value in a descendent node.
            final int arrayIndex = arrayIndexForBit(nodesBitmask, bit);
            if (bitIsPresent(nodesBitmask, bit)) {
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.assignImpl(shiftCount - 1, shiftCountForValue, index, value);
                final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                final int newSize = size - node.size() + newNode.size();
                return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, nodesBitmask, newNodes, newSize);
            } else {
                final long newBitmask = addBit(nodesBitmask, bit);
                final TrieArrayNode<T> newNode = forValue(shiftCountForValue, index, value);
                if (valuesBitmask == 0 && nodesBitmask == 0) {
                    return newNode;
                } else {
                    final TrieArrayNode<T>[] newNodes = ArrayHelper.insert(TrieArrayNode::allocateNodes, nodes, arrayIndex, newNode);
                    return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, newBitmask, newNodes, size + 1);
                }
            }
        }
    }

    @Nonnull
    private <K, V> TrieArrayNode<T> mappedAssignImpl(int shiftCount,
                                                     int shiftCountForValue,
                                                     int index,
                                                     @Nonnull ArrayAssignMapper<K, V, T> mapper,
                                                     @Nonnull K key,
                                                     V value)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        assert baseIndexAtShift(shiftCount, index) == baseIndexAtShift(shiftCount, baseIndex);
        assert shiftCount >= thisShiftCount;
        assert shiftCount >= shiftCountForValue;
        if (shiftCount != thisShiftCount) {
            // We are lower in tree than our parent expects, see if we need to create an ancestor to hold the value.
            // This happens when we've skipped intermediate nodes for efficiency and one of those nodes needs to be
            // inserted now because we are assigning a value that goes down a different branch than this node.
            final int ancestorShiftCount = findCommonAncestorShift(baseIndex + shift(thisShiftCount, 1), index);
            assert ancestorShiftCount <= shiftCount;
            if (ancestorShiftCount > thisShiftCount) {
                final TrieArrayNode<T> ancestor = forNode(ancestorShiftCount, baseIndex, this);
                return ancestor.mappedAssignImpl(ancestorShiftCount, shiftCountForValue, index, mapper, key, value);
            }
            shiftCount = thisShiftCount;
        }
        // If we've gotten here we know the value belongs either in this node or in one of our descendent nodes.
        assert baseIndexAtShift(shiftCount, index) == baseIndex;
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        final long nodesBitmask = this.nodesBitmask;
        final T[] values = this.values;
        final TrieArrayNode<T>[] nodes = this.nodes;
        if (shiftCount == shiftCountForValue) {
            // Store the value in this node.
            final long newBitmask = addBit(valuesBitmask, bit);
            final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
            if (bitIsPresent(valuesBitmask, bit)) {
                final T oldValue = values[arrayIndex];
                final T newValue = mapper.mappedAssign(oldValue, key, value);
                if (newValue == oldValue) {
                    return this;
                } else {
                    final int newSize = size - mapper.mappedSize(oldValue) + mapper.mappedSize(newValue);
                    assert (newSize == size) || (newSize == size + 1);
                    final T[] newValues = ArrayHelper.assign(values, arrayIndex, newValue);
                    assert newSize == computeSize(mapper, nodes, newValues);
                    return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, newSize);
                }
            } else {
                final T newValue = mapper.mappedAssign(key, value);
                assert mapper.mappedSize(newValue) == 1;
                final T[] newValues = ArrayHelper.insert(TrieArrayNode::allocateValues, values, arrayIndex, newValue);
                assert (size + 1) == computeSize(mapper, nodes, newValues);
                return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            // Store the value in a descendent node.
            final int arrayIndex = arrayIndexForBit(nodesBitmask, bit);
            if (bitIsPresent(nodesBitmask, bit)) {
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.mappedAssignImpl(shiftCount - 1, shiftCountForValue, index, mapper, key, value);
                if (newNode == node) {
                    return this;
                } else {
                    final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                    final int newSize = size - node.size() + newNode.size();
                    assert newSize == computeSize(mapper, newNodes, values);
                    return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, nodesBitmask, newNodes, newSize);
                }
            } else {
                final long newBitmask = addBit(nodesBitmask, bit);
                final TrieArrayNode<T> newNode = forValue(shiftCountForValue, index, mapper.mappedAssign(key, value));
                if (valuesBitmask == 0 && nodesBitmask == 0) {
                    return newNode;
                } else {
                    final TrieArrayNode<T>[] newNodes = ArrayHelper.insert(TrieArrayNode::allocateNodes, nodes, arrayIndex, newNode);
                    assert (size + 1) == computeSize(mapper, newNodes, values);
                    return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, newBitmask, newNodes, size + 1);
                }
            }
        }
    }

    @Nonnull
    private <K, V> TrieArrayNode<T> mappedUpdateImpl(int shiftCount,
                                                     int shiftCountForValue,
                                                     int index,
                                                     @Nonnull ArrayUpdateMapper<K, V, T> mapper,
                                                     @Nonnull K key,
                                                     @Nonnull Func1<Holder<V>, V> generator)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        assert baseIndexAtShift(shiftCount, index) == baseIndexAtShift(shiftCount, baseIndex);
        assert shiftCount >= thisShiftCount;
        assert shiftCount >= shiftCountForValue;
        if (shiftCount != thisShiftCount) {
            // We are lower in tree than our parent expects, see if we need to create an ancestor to hold the value.
            // This happens when we've skipped intermediate nodes for efficiency and one of those nodes needs to be
            // inserted now because we are assigning a value that goes down a different branch than this node.
            final int ancestorShiftCount = findCommonAncestorShift(baseIndex + shift(thisShiftCount, 1), index);
            assert ancestorShiftCount <= shiftCount;
            if (ancestorShiftCount > thisShiftCount) {
                final TrieArrayNode<T> ancestor = forNode(ancestorShiftCount, baseIndex, this);
                return ancestor.mappedUpdateImpl(ancestorShiftCount, shiftCountForValue, index, mapper, key, generator);
            }
            shiftCount = thisShiftCount;
        }
        // If we've gotten here we know the value belongs either in this node or in one of our descendent nodes.
        assert baseIndexAtShift(shiftCount, index) == baseIndex;
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        final long nodesBitmask = this.nodesBitmask;
        final T[] values = this.values;
        final TrieArrayNode<T>[] nodes = this.nodes;
        if (shiftCount == shiftCountForValue) {
            // Store the value in this node.
            final long newBitmask = addBit(valuesBitmask, bit);
            final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
            if (bitIsPresent(valuesBitmask, bit)) {
                final T oldValue = values[arrayIndex];
                final T newValue = mapper.mappedUpdate(oldValue, key, generator);
                if (newValue == oldValue) {
                    return this;
                } else {
                    final int newSize = size - mapper.mappedSize(oldValue) + mapper.mappedSize(newValue);
                    assert (newSize == size) || (newSize == size + 1);
                    final T[] newValues = ArrayHelper.assign(values, arrayIndex, newValue);
                    assert newSize == computeSize(mapper, nodes, newValues);
                    return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, newSize);
                }
            } else {
                final T newValue = mapper.mappedAssign(key, generator.apply(Holder.none()));
                assert mapper.mappedSize(newValue) == 1;
                final T[] newValues = ArrayHelper.insert(TrieArrayNode::allocateValues, values, arrayIndex, newValue);
                assert (size + 1) == computeSize(mapper, nodes, newValues);
                return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            // Store the value in a descendent node.
            final int arrayIndex = arrayIndexForBit(nodesBitmask, bit);
            if (bitIsPresent(nodesBitmask, bit)) {
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.mappedUpdateImpl(shiftCount - 1, shiftCountForValue, index, mapper, key, generator);
                if (newNode == node) {
                    return this;
                } else {
                    final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                    final int newSize = size - node.size() + newNode.size();
                    assert newSize == computeSize(mapper, newNodes, values);
                    return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, nodesBitmask, newNodes, newSize);
                }
            } else {
                final long newBitmask = addBit(nodesBitmask, bit);
                final V value = generator.apply(Holder.none());
                final TrieArrayNode<T> newNode = forValue(shiftCountForValue, index, mapper.mappedAssign(key, value));
                if (valuesBitmask == 0 && nodesBitmask == 0) {
                    return newNode;
                } else {
                    final TrieArrayNode<T>[] newNodes = ArrayHelper.insert(TrieArrayNode::allocateNodes, nodes, arrayIndex, newNode);
                    assert (size + 1) == computeSize(mapper, newNodes, values);
                    return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, newBitmask, newNodes, size + 1);
                }
            }
        }
    }

    @Nonnull
    private TrieArrayNode<T> deleteImpl(int shiftCountForValue,
                                        int index)
    {
        final int shiftCount = this.shiftCount;
        if (shiftCountForValue > shiftCount) {
            return this;
        }
        if (baseIndexAtShift(shiftCount, index) != baseIndex) {
            return this;
        }
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        final long nodesBitmask = this.nodesBitmask;
        final T[] values = this.values;
        final TrieArrayNode<T>[] nodes = this.nodes;
        if (shiftCountForValue == shiftCount) {
            if (bitIsPresent(valuesBitmask, bit)) {
                if (size == 1) {
                    return empty();
                } else {
                    final long newBitmask = removeBit(valuesBitmask, bit);
                    final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                    final T[] newValues = ArrayHelper.delete(TrieArrayNode::allocateValues, values, arrayIndex);
                    return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size - 1);
                }
            }
        } else {
            if (bitIsPresent(nodesBitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(nodesBitmask, bit);
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.deleteImpl(shiftCountForValue, index);
                if (newNode != node) {
                    final int newSize = size - node.size() + newNode.size();
                    if (newSize == 0) {
                        return empty();
                    } else if (newNode.isEmpty()) {
                        final long newBitmask = removeBit(nodesBitmask, bit);
                        if (valuesBitmask == 0 && bitCount(newBitmask) == 1) {
                            // return the unaffected single remaining node to minimize height of the tree
                            return nodes[arrayIndexForBit(nodesBitmask, newBitmask)];
                        } else {
                            final TrieArrayNode<T>[] newNodes = ArrayHelper.delete(TrieArrayNode::allocateNodes, nodes, arrayIndex);
                            return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, newBitmask, newNodes, newSize);
                        }
                    } else {
                        final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                        return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, nodesBitmask, newNodes, newSize);
                    }
                }
            }
        }
        return this;
    }

    @Nonnull
    private <K> TrieArrayNode<T> mappedDeleteImpl(int shiftCountForValue,
                                                  int index,
                                                  @Nonnull ArrayDeleteMapper<K, T> mapper,
                                                  @Nonnull K key)
    {
        final int shiftCount = this.shiftCount;
        if (shiftCountForValue > shiftCount) {
            return this;
        }
        if (baseIndexAtShift(shiftCount, index) != baseIndex) {
            return this;
        }
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        final T[] values = this.values;
        if (shiftCountForValue == shiftCount) {
            if (bitIsPresent(valuesBitmask, bit)) {
                final long newBitmask;
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                final T mapping = values[arrayIndex];
                final T newMapping = mapper.mappedDelete(mapping, key);
                if (newMapping != mapping) {
                    final T[] newValues;
                    if (newMapping == null) {
                        if (size == 1) {
                            return empty();
                        }
                        newBitmask = removeBit(valuesBitmask, bit);
                        newValues = ArrayHelper.delete(TrieArrayNode::allocateValues, values, arrayIndex);
                    } else {
                        newBitmask = valuesBitmask;
                        newValues = ArrayHelper.assign(values, arrayIndex, newMapping);
                    }
                    assert (size - 1) == computeSize(mapper, nodes, newValues);
                    return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size - 1);
                }
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                final TrieArrayNode<T>[] nodes = this.nodes;
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.mappedDeleteImpl(shiftCountForValue, index, mapper, key);
                if (newNode != node) {
                    final int newSize = size - node.size() + newNode.size();
                    if (newSize == 0) {
                        return empty();
                    } else if (newNode.isEmpty()) {
                        final long newBitmask = removeBit(bitmask, bit);
                        if (valuesBitmask == 0 && bitCount(newBitmask) == 1) {
                            // return the unaffected single remaining node to minimize height of the tree
                            return nodes[arrayIndexForBit(bitmask, newBitmask)];
                        } else {
                            final TrieArrayNode<T>[] newNodes = ArrayHelper.delete(TrieArrayNode::allocateNodes, nodes, arrayIndex);
                            assert newSize == computeSize(mapper, newNodes, values);
                            return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, newBitmask, newNodes, newSize);
                        }
                    } else {
                        final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                        assert newSize == computeSize(mapper, newNodes, values);
                        return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, bitmask, newNodes, newSize);
                    }
                }
            }
        }
        return this;
    }

    public void checkInvariants(@Nullable ArraySizeMapper<T> mapper)
    {
        if (bitCount(valuesBitmask) != values.length) {
            throw new IllegalStateException(String.format("invalid bitmask for values array: bitmask=%s length=%d", Long.toBinaryString(valuesBitmask), values.length));
        }
        if (bitCount(nodesBitmask) != nodes.length) {
            throw new IllegalStateException(String.format("invalid bitmask for nodes array: bitmask=%s length=%d", Long.toBinaryString(nodesBitmask), nodes.length));
        }
        if (!checkChildShifts(shiftCount, nodes)) {
            throw new IllegalStateException("one or more nodes invalid for this branch");
        }
        final int computedSize = mapper != null ? computeSize(mapper, nodes, values) : computeSize(nodes) + values.length;
        if (computedSize != size) {
            throw new IllegalStateException(String.format("size mismatch: size=%d computed=%d", size, computedSize));
        }
    }

    @Nonnull
    public GenericIterator.Iterable<Integer> keys()
    {
        return iterable((valueIndex, arrayIndex) -> computeUserIndexForValue(valueIndex),
                        nodeIndex -> nodes[nodeIndex].keys());
    }

    @Nonnull
    public GenericIterator.Iterable<T> values()
    {
        return iterable((valueIndex, arrayIndex) -> values[arrayIndex],
                        nodeIndex -> nodes[nodeIndex].values());
    }

    @Nonnull
    public GenericIterator.Iterable<IMapEntry<Integer, T>> entries()
    {
        return iterable((valueIndex, arrayIndex) -> MapEntry.entry(computeUserIndexForValue(valueIndex), values[arrayIndex]),
                        nodeIndex -> nodes[nodeIndex].entries());
    }

    @Nonnull
    public <K> GenericIterator.Iterable<K> mappedKeys(@Nonnull ArrayIterationMapper<K, ?, T> mapper)
    {
        return mappedIterable(mapper::mappedKeys);
    }

    @Nonnull
    public <V> GenericIterator.Iterable<V> mappedValues(@Nonnull ArrayIterationMapper<?, V, T> mapper)
    {
        return mappedIterable(mapper::mappedValues);
    }

    @Nonnull
    public <K, V> GenericIterator.Iterable<IMapEntry<K, V>> mappedEntries(@Nonnull ArrayIterationMapper<K, V, T> mapper)
    {
        return mappedIterable(mapper::mappedEntries);
    }

    public int size()
    {
        return size;
    }

    private int computeUserIndexForValue(Integer valueIndex)
    {
        return flip(baseIndex + shift(shiftCount, valueIndex));
    }

    private <V> GenericIterator.Iterable<V> iterable(@Nonnull IntFunc2<V> valueFunction,
                                                     @Nonnull IntFunction<GenericIterator.Iterable<V>> nodeFunction)
    {
        return new GenericIterator.Iterable<V>()
        {
            @Nullable
            @Override
            public GenericIterator.State<V> iterateOverRange(@Nullable GenericIterator.State<V> parent,
                                                             int offset,
                                                             int limit)
            {
                final List<GenericIterator.Iterable<V>> iterables = new ArrayList<>(values.length + nodes.length);
                long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
                while (combinedBitmask != 0) {
                    final long bit = leastBit(combinedBitmask);
                    if (bitIsPresent(valuesBitmask, bit)) {
                        final int valueIndex = indexForBit(bit);
                        final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                        final int entryIndex = baseIndex + shift(shiftCount, valueIndex);
                        iterables.add(GenericIterator.singleValueIterable(valueFunction.apply(valueIndex, arrayIndex)));
                    }
                    if (bitIsPresent(nodesBitmask, bit)) {
                        final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                        iterables.add(nodeFunction.apply(nodeIndex));
                    }
                    combinedBitmask = removeBit(combinedBitmask, bit);
                }
                assert iterables.size() == (values.length + nodes.length);
                return GenericIterator.multiIterableState(parent, IndexedList.retained(iterables), offset, limit);
            }

            @Override
            public int iterableSize()
            {
                return size;
            }
        };
    }

    @Nonnull
    private <V> GenericIterator.Iterable<V> mappedIterable(@Nonnull Func1<T, GenericIterator.Iterable<V>> valueFunction)
    {
        return new GenericIterator.Iterable<V>()
        {
            @Nullable
            @Override
            public GenericIterator.State<V> iterateOverRange(@Nullable GenericIterator.State<V> parent,
                                                             int offset,
                                                             int limit)
            {
                final List<GenericIterator.Iterable<V>> iterables = new ArrayList<>(values.length + nodes.length);
                long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
                while (combinedBitmask != 0) {
                    final long bit = leastBit(combinedBitmask);
                    if (bitIsPresent(valuesBitmask, bit)) {
                        final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                        iterables.add(valueFunction.apply(values[arrayIndex]));
                    }
                    if (bitIsPresent(nodesBitmask, bit)) {
                        final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                        iterables.add(nodes[nodeIndex].mappedIterable(valueFunction));
                    }
                    combinedBitmask = removeBit(combinedBitmask, bit);
                }
                assert iterables.size() == (values.length + nodes.length);
                return GenericIterator.multiIterableState(parent, IndexedList.retained(iterables), offset, limit);
            }

            @Override
            public int iterableSize()
            {
                return size;
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> T[] allocateValues(int size)
    {
        return size == 0 ? emptyValues() : (T[])new Object[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> TrieArrayNode<T>[] allocateNodes(int size)
    {
        return size == 0 ? emptyNodes() : (TrieArrayNode<T>[])new TrieArrayNode[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> T[] emptyValues()
    {
        return (T[])EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> TrieArrayNode<T>[] emptyNodes()
    {
        return (TrieArrayNode<T>[])EMPTY_NODES;
    }

    static int findShiftForIndex(int index)
    {
        return findMinimumShiftForZeroBelowHashCode(index);
    }

    static int findCommonAncestorShift(int index1,
                                       int index2)
    {
        final int shift1 = findShiftForIndex(index1);
        final int shift2 = findShiftForIndex(index2);
        int shiftCount = Math.max(shift1, shift2);
        while (baseIndexAtShift(shiftCount, index1) != baseIndexAtShift(shiftCount, index2)) {
            shiftCount += 1;
        }
        assert shiftCount <= ROOT_SHIFT_COUNT;
        return shiftCount;
    }

    /**
     * Flips the sign bit in the index.  Starting from a userIndex which might be positive or
     * negative it converts it into a corresponding (but different) unsigned integer such that
     * for any two incoming integers a and b: compare(a, b)==compareUnsigned(flip(a),flip(b)).
     * This ensures that iteration order fits the signed order while allowing all of the array
     * mapping to work with indices as unsigned positive numbers.  Operation is reversable (i.e.
     * flip(flip(x))==x for all x.
     */
    static int flip(int index)
    {
        return index ^ SIGN_BIT;
    }

    private static <T> boolean checkChildShifts(int shiftCount,
                                                @Nonnull TrieArrayNode<T>[] nodes)
    {
        for (TrieArrayNode<T> node : nodes) {
            if (shiftCount <= node.shiftCount || node.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static <K, T> int computeSize(@Nonnull ArraySizeMapper<T> mapper,
                                          @Nonnull TrieArrayNode<T>[] children,
                                          @Nonnull T[] values)
    {
        int total = 0;
        for (TrieArrayNode<T> child : children) {
            total += child.size();
        }
        for (T value : values) {
            total += mapper.mappedSize(value);
        }
        return total;
    }

    private static <T> int computeSize(@Nonnull TrieArrayNode<T>[] children)
    {
        int total = 0;
        for (TrieArrayNode<T> child : children) {
            total += child.size();
        }
        return total;
    }
}
