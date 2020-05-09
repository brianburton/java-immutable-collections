///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

package org.javimmutable.collections.inorder.token_list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.common.IntArrayMappedTrieMath.*;

/**
 * Implements an array mapped trie using TokenImpl as keys.
 */
@Immutable
class TrieNode<T>
    implements InvariantCheckable
{
    private static final Object[] EMPTY_VALUES = new Object[0];
    @SuppressWarnings({"rawtypes"})
    private static final TrieNode[] EMPTY_NODES = new TrieNode[0];
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TrieNode EMPTY = new TrieNode(0, TrieToken.ZERO, 0L, EMPTY_VALUES, 0L, EMPTY_NODES, 0);

    private final int shift;
    private final TrieToken baseToken;
    private final long valuesBitmask;
    private final T[] values;
    private final long nodesBitmask;
    private final TrieNode<T>[] nodes;
    private final int size;

    TrieNode(int shift,
             TrieToken baseToken,
             long valuesBitmask,
             T[] values,
             long nodesBitmask,
             @Nonnull TrieNode<T>[] nodes,
             int size)
    {
        assert bitCount(valuesBitmask) == values.length;
        assert bitCount(nodesBitmask) == nodes.length;
        this.shift = shift;
        this.baseToken = baseToken;
        this.valuesBitmask = valuesBitmask;
        this.values = values;
        this.nodesBitmask = nodesBitmask;
        this.nodes = nodes;
        this.size = size;
        assert checkChildShifts(shift, nodes);
        assert computeSize(nodes) + values.length == size;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> TrieNode<T> empty()
    {
        return (TrieNode<T>)EMPTY;
    }

    @Nonnull
    static <T> TrieNode<T> create(@Nonnull TrieToken token,
                                  T value)
    {
        return forValue(token.trieDepth(), token, value);
    }

    @Nonnull
    private static <T> TrieNode<T> forValue(int shift,
                                            @Nonnull TrieToken token,
                                            T value)
    {
        assert shift == token.trieDepth();
        final TrieToken baseToken = token.base(shift);
        final long valueBitmask = bitFromIndex(token.indexAt(shift));
        final T[] values = ArrayHelper.newArray(value);
        final long nodeBitmask = 0L;
        final TrieNode<T>[] nodes = emptyNodes();
        return new TrieNode<>(shift, baseToken, valueBitmask, values, nodeBitmask, nodes, 1);
    }

    @Nonnull
    private static <T> TrieNode<T> forNode(int shift,
                                           @Nonnull TrieToken nodeBaseToken,
                                           @Nonnull TrieNode<T> node)
    {
        final TrieToken baseToken = nodeBaseToken.base(shift);
        final long valueBitmask = 0L;
        final T[] values = emptyValues();
        final long nodeBitmask = bitFromIndex(nodeBaseToken.indexAt(shift));
        final TrieNode<T>[] nodes = allocateNodes(1);
        nodes[0] = node;
        return new TrieNode<>(shift, baseToken, valueBitmask, values, nodeBitmask, nodes, node.size);
    }

    int size()
    {
        return size;
    }

    boolean isEmpty()
    {
        return size == 0;
    }

    T getValueOr(@Nonnull TrieToken token,
                 T defaultValue)
    {
        final int shiftForValue = token.trieDepth();
        return getValueOrImpl(shiftForValue, token, defaultValue);
    }

    @Nonnull
    TrieNode<T> assign(@Nonnull TrieToken token,
                       T value)
    {
        final int shiftForValue = token.trieDepth();
        final int maxShift = Math.max(baseToken.maxShift(), shiftForValue) + 1;
        return assignImpl(maxShift, shiftForValue, token, value);
    }

    @Nonnull
    TrieNode<T> delete(@Nonnull TrieToken token)
    {
        final int shiftForValue = token.trieDepth();
        return deleteImpl(shiftForValue, token);
    }

    private T getValueOrImpl(int shiftForValue,
                             @Nonnull TrieToken token,
                             T defaultValue)
    {
        final int shift = this.shift;
        if (shiftForValue > shift) {
            return defaultValue;
        }
        if (!TrieToken.sameBaseAt(baseToken, token, shift)) {
            return defaultValue;
        }
        final int myIndex = token.indexAt(shift);
        final long bit = bitFromIndex(myIndex);
        if (shiftForValue == shift) {
            final long bitmask = this.valuesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return values[arrayIndex];
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return nodes[arrayIndex].getValueOrImpl(shiftForValue, token, defaultValue);
            }
        }
        return defaultValue;
    }

    @Nonnull
    private TrieNode<T> assignImpl(int shift,
                                   int shiftForValue,
                                   @Nonnull TrieToken token,
                                   T value)
    {
        final int thisShift = this.shift;
        final TrieToken baseToken = this.baseToken;
        assert TrieToken.sameBaseAt(baseToken, token, shift);
        assert shift >= thisShift;
        assert shift >= shiftForValue;
        if (shift != thisShift) {
            // We are lower in tree than our parent expects, see if we need to create an ancestor to hold the value.
            // This happens when we've skipped intermediate nodes for efficiency and one of those nodes needs to be
            // inserted now because we are assigning a value that goes down a different branch than this node.
            final int ancestorShiftCount = TrieToken.commonAncestorShift(baseToken.withIndexAt(thisShift, 1), token);
            assert ancestorShiftCount <= shift;
            if (ancestorShiftCount > thisShift) {
                final TrieNode<T> ancestor = forNode(ancestorShiftCount, baseToken, this);
                return ancestor.assignImpl(ancestorShiftCount, shiftForValue, token, value);
            }
            shift = thisShift;
        }
        // If we've gotten here we know the value belongs either in this node or in one of our descendent nodes.
        assert TrieToken.equivalentTo(token.base(shift), baseToken);
        final int myIndex = token.indexAt(shift);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        final long nodesBitmask = this.nodesBitmask;
        if (shift == shiftForValue) {
            // Store the value in this node.
            final T[] values = this.values;
            final long newBitmask = addBit(valuesBitmask, bit);
            final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
            if (bitIsPresent(valuesBitmask, bit)) {
                final T[] newValues = ArrayHelper.assign(values, arrayIndex, value);
                return new TrieNode<>(shift, baseToken, newBitmask, newValues, nodesBitmask, nodes, size);
            } else {
                final T[] newValues = ArrayHelper.insert(TrieNode::allocateValues, values, arrayIndex, value);
                return new TrieNode<>(shift, baseToken, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            // Store the value in a descendent node.
            final int arrayIndex = arrayIndexForBit(nodesBitmask, bit);
            if (bitIsPresent(nodesBitmask, bit)) {
                final TrieNode<T> node = nodes[arrayIndex];
                final TrieNode<T> newNode = node.assignImpl(shift - 1, shiftForValue, token, value);
                final TrieNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                final int newSize = size - node.size + newNode.size;
                return new TrieNode<>(shift, baseToken, valuesBitmask, values, nodesBitmask, newNodes, newSize);
            } else {
                final long newBitmask = addBit(nodesBitmask, bit);
                final TrieNode<T> newNode = forValue(shiftForValue, token, value);
                if (valuesBitmask == 0 && nodesBitmask == 0) {
                    return newNode;
                } else {
                    final TrieNode<T>[] newNodes = ArrayHelper.insert(TrieNode::allocateNodes, nodes, arrayIndex, newNode);
                    return new TrieNode<>(shift, baseToken, valuesBitmask, values, newBitmask, newNodes, size + 1);
                }
            }
        }
    }

    @Nonnull
    private TrieNode<T> deleteImpl(int shiftForValue,
                                   @Nonnull TrieToken token)
    {
        final int shift = this.shift;
        if (shiftForValue > shift) {
            return this;
        }
        if (!TrieToken.sameBaseAt(baseToken, token, shift)) {
            return this;
        }
        final int myIndex = token.indexAt(shift);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        if (shiftForValue == shift) {
            if (bitIsPresent(valuesBitmask, bit)) {
                if (size == 1) {
                    return empty();
                } else {
                    final long newBitmask = removeBit(valuesBitmask, bit);
                    final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                    final T[] newValues = ArrayHelper.delete(TrieNode::allocateValues, values, arrayIndex);
                    return new TrieNode<>(shift, baseToken, newBitmask, newValues, nodesBitmask, nodes, size - 1);
                }
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                final TrieNode<T>[] nodes = this.nodes;
                final TrieNode<T> node = nodes[arrayIndex];
                final TrieNode<T> newNode = node.deleteImpl(shiftForValue, token);
                if (newNode != node) {
                    final int newSize = size - node.size + newNode.size;
                    if (newSize == 0) {
                        return empty();
                    } else if (newNode.isEmpty()) {
                        final long newBitmask = removeBit(bitmask, bit);
                        if (valuesBitmask == 0 && bitCount(newBitmask) == 1) {
                            // return the unaffected single remaining node to minimize height of the tree
                            return nodes[arrayIndexForBit(bitmask, newBitmask)];
                        } else {
                            final TrieNode<T>[] newNodes = ArrayHelper.delete(TrieNode::allocateNodes, nodes, arrayIndex);
                            return new TrieNode<>(shift, baseToken, valuesBitmask, values, newBitmask, newNodes, newSize);
                        }
                    } else {
                        final TrieNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                        return new TrieNode<>(shift, baseToken, valuesBitmask, values, bitmask, newNodes, newSize);
                    }
                }
            }
        }
        return this;
    }

    @Override
    public void checkInvariants()
    {
        if (bitCount(valuesBitmask) != values.length) {
            throw new IllegalStateException(String.format("invalid bitmask for values array: bitmask=%s length=%d", Long.toBinaryString(valuesBitmask), values.length));
        }
        if (bitCount(nodesBitmask) != nodes.length) {
            throw new IllegalStateException(String.format("invalid bitmask for nodes array: bitmask=%s length=%d", Long.toBinaryString(nodesBitmask), nodes.length));
        }
        if (!checkChildShifts(shift, nodes)) {
            throw new IllegalStateException("one or more nodes invalid for this branch");
        }
        final int computedSize = computeSize(nodes) + values.length;
        if (computedSize != size) {
            throw new IllegalStateException(String.format("size mismatch: size=%d computed=%d", size, computedSize));
        }
    }

    @Nonnull
    GenericIterator.Iterable<TokenList.Token> tokens()
    {
        return iterable((valueIndex, arrayIndex) -> baseToken.withIndexAt(shift, valueIndex),
                        nodeIndex -> nodes[nodeIndex].tokens());
    }

    @Nonnull
    GenericIterator.Iterable<T> values()
    {
        return iterable((valueIndex, arrayIndex) -> values[arrayIndex],
                        nodeIndex -> nodes[nodeIndex].values());
    }

    @Nonnull
    GenericIterator.Iterable<TokenList.Entry<T>> entries()
    {
        return iterable((valueIndex, arrayIndex) -> new Entry<>(baseToken.withIndexAt(shift, valueIndex), values[arrayIndex]),
                        nodeIndex -> nodes[nodeIndex].entries());
    }

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(entries().iterator());
    }

    @Nonnull
    private <V> GenericIterator.Iterable<V> iterable(Func2<Integer, Integer, V> valueFunction,
                                                     Func1<Integer, GenericIterator.Iterable<V>> nodeFunction)
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
                        iterables.add(GenericIterator.valueIterable(valueFunction.apply(valueIndex, arrayIndex)));
                    }
                    if (bitIsPresent(nodesBitmask, bit)) {
                        final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                        iterables.add(nodeFunction.apply(nodeIndex));
                    }
                    combinedBitmask = removeBit(combinedBitmask, bit);
                }
                assert iterables.size() == (values.length + nodes.length);
                return GenericIterator.indexedState(parent, IndexedList.retained(iterables), offset, limit);
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
    private static <T> T[] allocateValues(int size)
    {
        return size == 0 ? emptyValues() : (T[])new Object[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> TrieNode<T>[] allocateNodes(int size)
    {
        return size == 0 ? emptyNodes() : (TrieNode<T>[])new TrieNode[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> T[] emptyValues()
    {
        return (T[])EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> TrieNode<T>[] emptyNodes()
    {
        return (TrieNode<T>[])EMPTY_NODES;
    }

    private static <T> boolean checkChildShifts(int shiftCount,
                                                @Nonnull TrieNode<T>[] nodes)
    {
        for (TrieNode<T> node : nodes) {
            if (shiftCount <= node.shift || node.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static <T> int computeSize(@Nonnull TrieNode<T>[] children)
    {
        int total = 0;
        for (TrieNode<T> child : children) {
            total += child.size;
        }
        return total;
    }

    private static class Entry<T>
        implements TokenList.Entry<T>
    {
        private final TrieToken token;
        private final T value;

        private Entry(TrieToken token,
                      T value)
        {
            this.token = token;
            this.value = value;
        }

        @Nonnull
        @Override
        public TokenList.Token token()
        {
            return token;
        }

        @Override
        public T value()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "[" + token + "," + value + "]";
        }
    }
}
