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

package org.javimmutable.collections.token_list;

import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.common.IntArrayMappedTrieMath.*;

/**
 * Implements an array mapped trie using TokenImpl as keys.
 */
class TokenTrieNode<T>
    implements InvariantCheckable,
               GenericIterator.Iterable<TokenList.Entry<T>>
{
    private static final Object[] EMPTY_VALUES = new Object[0];
    @SuppressWarnings({"rawtypes"})
    private static final TokenTrieNode[] EMPTY_NODES = new TokenTrieNode[0];
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TokenTrieNode EMPTY = new TokenTrieNode(0, TokenImpl.ZERO, 0L, EMPTY_VALUES, 0L, EMPTY_NODES, 0);

    private final int shift;
    private final TokenImpl baseToken;
    private final long valuesBitmask;
    private final T[] values;
    private final long nodesBitmask;
    private final TokenTrieNode<T>[] nodes;
    private final int size;

    TokenTrieNode(int shift,
                  TokenImpl baseToken,
                  long valuesBitmask,
                  T[] values,
                  long nodesBitmask,
                  @Nonnull TokenTrieNode<T>[] nodes,
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
    static <T> TokenTrieNode<T> empty()
    {
        return (TokenTrieNode<T>)EMPTY;
    }

    @Nonnull
    private static <T> TokenTrieNode<T> forValue(int shift,
                                                 @Nonnull TokenImpl token,
                                                 T value)
    {
        assert shift == token.trieDepth();
        final TokenImpl baseToken = token.base(shift);
        final long valueBitmask = bitFromIndex(token.indexAt(shift));
        final T[] values = ArrayHelper.newArray(value);
        final long nodeBitmask = 0L;
        final TokenTrieNode<T>[] nodes = emptyNodes();
        return new TokenTrieNode<>(shift, baseToken, valueBitmask, values, nodeBitmask, nodes, 1);
    }

    @Nonnull
    private static <T> TokenTrieNode<T> forNode(int shift,
                                                @Nonnull TokenImpl nodeBaseToken,
                                                @Nonnull TokenTrieNode<T> node)
    {
        final TokenImpl baseToken = nodeBaseToken.base(shift);
        final long valueBitmask = 0L;
        final T[] values = emptyValues();
        final long nodeBitmask = bitFromIndex(nodeBaseToken.indexAt(shift));
        final TokenTrieNode<T>[] nodes = allocateNodes(1);
        nodes[0] = node;
        return new TokenTrieNode<>(shift, baseToken, valueBitmask, values, nodeBitmask, nodes, node.iterableSize());
    }

    boolean isEmpty()
    {
        return size == 0;
    }

    T getValueOr(@Nonnull TokenImpl token,
                 T defaultValue)
    {
        final int shiftForValue = token.trieDepth();
        return getValueOrImpl(shiftForValue, token, defaultValue);
    }

    @Nonnull
    TokenTrieNode<T> assign(@Nonnull TokenImpl token,
                            T value)
    {
        final int shiftForValue = token.trieDepth();
        final int maxShift = Math.max(baseToken.maxShift(), shiftForValue) + 1;
        return assignImpl(maxShift, shiftForValue, token, value);
    }

    @Nonnull
    TokenTrieNode<T> delete(@Nonnull TokenImpl token)
    {
        final int shiftForValue = token.trieDepth();
        return deleteImpl(shiftForValue, token);
    }

    private T getValueOrImpl(int shiftForValue,
                             @Nonnull TokenImpl token,
                             T defaultValue)
    {
        final int shift = this.shift;
        if (shiftForValue > shift) {
            return defaultValue;
        }
        if (!TokenImpl.sameBaseAt(shift, baseToken, token)) {
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
    private TokenTrieNode<T> assignImpl(int shift,
                                        int shiftForValue,
                                        @Nonnull TokenImpl token,
                                        T value)
    {
        final int thisShift = this.shift;
        final TokenImpl baseToken = this.baseToken;
        assert TokenImpl.sameBaseAt(shift, baseToken, token);
        assert shift >= thisShift;
        assert shift >= shiftForValue;
        if (shift != thisShift) {
            // We are lower in tree than our parent expects, see if we need to create an ancestor to hold the value.
            // This happens when we've skipped intermediate nodes for efficiency and one of those nodes needs to be
            // inserted now because we are assigning a value that goes down a different branch than this node.
            final TokenImpl ancestorToken = baseToken.withIndexAt(thisShift, 1).commonBaseWith(token);
            final int ancestorShiftCount = Math.max(token.trieDepth(), ancestorToken.trieDepth());
            assert ancestorShiftCount <= shift;
            if (ancestorShiftCount > thisShift) {
                final TokenTrieNode<T> ancestor = forNode(ancestorShiftCount, baseToken, this);
                return ancestor.assignImpl(ancestorShiftCount, shiftForValue, token, value);
            }
            shift = thisShift;
        }
        // If we've gotten here we know the value belongs either in this node or in one of our descendent nodes.
        assert TokenImpl.equivalentTo(token.base(shift), baseToken);
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
                return new TokenTrieNode<>(shift, baseToken, newBitmask, newValues, nodesBitmask, nodes, size);
            } else {
                final T[] newValues = ArrayHelper.insert(TokenTrieNode::allocateValues, values, arrayIndex, value);
                return new TokenTrieNode<>(shift, baseToken, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            // Store the value in a descendent node.
            final int arrayIndex = arrayIndexForBit(nodesBitmask, bit);
            if (bitIsPresent(nodesBitmask, bit)) {
                final TokenTrieNode<T> node = nodes[arrayIndex];
                final TokenTrieNode<T> newNode = node.assignImpl(shift - 1, shiftForValue, token, value);
                final TokenTrieNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                final int newSize = size - node.iterableSize() + newNode.iterableSize();
                return new TokenTrieNode<>(shift, baseToken, valuesBitmask, values, nodesBitmask, newNodes, newSize);
            } else {
                final long newBitmask = addBit(nodesBitmask, bit);
                final TokenTrieNode<T> newNode = forValue(shiftForValue, token, value);
                if (valuesBitmask == 0 && nodesBitmask == 0) {
                    return newNode;
                } else {
                    final TokenTrieNode<T>[] newNodes = ArrayHelper.insert(TokenTrieNode::allocateNodes, nodes, arrayIndex, newNode);
                    return new TokenTrieNode<>(shift, baseToken, valuesBitmask, values, newBitmask, newNodes, size + 1);
                }
            }
        }
    }

    @Nonnull
    private TokenTrieNode<T> deleteImpl(int shiftForValue,
                                        @Nonnull TokenImpl token)
    {
        final int shift = this.shift;
        if (shiftForValue > shift) {
            return this;
        }
        if (!TokenImpl.sameBaseAt(shift, baseToken, token)) {
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
                    final T[] newValues = ArrayHelper.delete(TokenTrieNode::allocateValues, values, arrayIndex);
                    return new TokenTrieNode<>(shift, baseToken, newBitmask, newValues, nodesBitmask, nodes, size - 1);
                }
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                final TokenTrieNode<T>[] nodes = this.nodes;
                final TokenTrieNode<T> node = nodes[arrayIndex];
                final TokenTrieNode<T> newNode = node.deleteImpl(shiftForValue, token);
                if (newNode != node) {
                    final int newSize = size - node.iterableSize() + newNode.iterableSize();
                    if (newSize == 0) {
                        return empty();
                    } else if (newNode.isEmpty()) {
                        final long newBitmask = removeBit(bitmask, bit);
                        if (valuesBitmask == 0 && bitCount(newBitmask) == 1) {
                            // return the unaffected single remaining node to minimize height of the tree
                            return nodes[arrayIndexForBit(bitmask, newBitmask)];
                        } else {
                            final TokenTrieNode<T>[] newNodes = ArrayHelper.delete(TokenTrieNode::allocateNodes, nodes, arrayIndex);
                            return new TokenTrieNode<>(shift, baseToken, valuesBitmask, values, newBitmask, newNodes, newSize);
                        }
                    } else {
                        final TokenTrieNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                        return new TokenTrieNode<>(shift, baseToken, valuesBitmask, values, bitmask, newNodes, newSize);
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

    @Nullable
    @Override
    public GenericIterator.State<TokenList.Entry<T>> iterateOverRange(@Nullable GenericIterator.State<TokenList.Entry<T>> parent,
                                                                      int offset,
                                                                      int limit)
    {
        final List<GenericIterator.Iterable<TokenList.Entry<T>>> iterables = new ArrayList<>(values.length + nodes.length);
        long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
        while (combinedBitmask != 0) {
            final long bit = leastBit(combinedBitmask);
            if (bitIsPresent(valuesBitmask, bit)) {
                final int valueIndex = indexForBit(bit);
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                iterables.add(GenericIterator.valueIterable(new Entry<>(baseToken.withIndexAt(shift, valueIndex), values[arrayIndex])));
            }
            if (bitIsPresent(nodesBitmask, bit)) {
                final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                iterables.add(nodes[nodeIndex]);
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

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(iterator());
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> T[] allocateValues(int size)
    {
        return size == 0 ? emptyValues() : (T[])new Object[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> TokenTrieNode<T>[] allocateNodes(int size)
    {
        return size == 0 ? emptyNodes() : (TokenTrieNode<T>[])new TokenTrieNode[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> T[] emptyValues()
    {
        return (T[])EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> TokenTrieNode<T>[] emptyNodes()
    {
        return (TokenTrieNode<T>[])EMPTY_NODES;
    }

    private static <T> boolean checkChildShifts(int shiftCount,
                                                @Nonnull TokenTrieNode<T>[] nodes)
    {
        for (TokenTrieNode<T> node : nodes) {
            if (shiftCount <= node.shift || node.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static <T> int computeSize(@Nonnull TokenTrieNode<T>[] children)
    {
        int total = 0;
        for (TokenTrieNode<T> child : children) {
            total += child.iterableSize();
        }
        return total;
    }

    private static class Entry<T>
        implements TokenList.Entry<T>
    {
        private final TokenImpl token;
        private final T value;

        private Entry(TokenImpl token,
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
