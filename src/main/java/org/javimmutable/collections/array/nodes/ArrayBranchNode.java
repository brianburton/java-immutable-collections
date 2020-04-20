package org.javimmutable.collections.array.nodes;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.javimmutable.collections.common.HamtLongMath.*;

public class ArrayBranchNode<T>
    extends ArrayNode<T>
{
    private final int shiftCount;
    private final int baseIndex;
    private final long bitmask;
    private final ArrayNode<T>[] children;
    private final int size;

    private ArrayBranchNode(int shiftCount,
                            int baseIndex,
                            long bitmask,
                            ArrayNode<T>[] children,
                            int size)
    {
        assert bitCount(bitmask) == children.length;
        assert children.length >= 2;
        this.shiftCount = shiftCount;
        this.baseIndex = baseIndex;
        this.bitmask = bitmask;
        this.children = children;
        this.size = size;
        assert checkChildShifts();
        assert computeSize() == size;
    }

    static <T> ArrayNode<T> forChildren(int index1,
                                        ArrayNode<T> child1,
                                        int index2,
                                        ArrayNode<T> child2)
    {
        final int shiftCount = findMaxCommonShift(ROOT_SHIFTS, index1, index2);
        assert shiftCount > LEAF_SHIFTS;
        assert baseIndexAtShift(shiftCount, index1) == baseIndexAtShift(shiftCount, index2);
        final int baseIndex = baseIndexAtShift(shiftCount, index1);
        final int childIndex1 = indexAtShift(shiftCount, index1);
        final int childIndex2 = indexAtShift(shiftCount, index2);
        final long bitmask = addBit(bitFromIndex(childIndex1), bitFromIndex(childIndex2));
        final int size = child1.iterableSize() + child2.iterableSize();
        final ArrayNode<T>[] children = allocate(2);
        if (childIndex1 < childIndex2) {
            children[0] = child1;
            children[1] = child2;
        } else {
            children[0] = child2;
            children[1] = child1;
        }
        return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, children, size);
    }

    @Override
    public int iterableSize()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public T getValueOr(int shiftCount,
                        int index,
                        T defaultValue)
    {
        assert shiftCount >= this.shiftCount;
        if (shiftCount != this.shiftCount) {
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return defaultValue;
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].getValueOr(shiftCount - 1, index, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        if (shiftCount != this.shiftCount) {
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return Holders.of();
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].find(shiftCount - 1, index);
        }
        return Holders.of();
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        assert shiftCount >= this.shiftCount;
        if (shiftCount != this.shiftCount) {
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                final ArrayNode<T> leaf = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
                return ArrayBranchNode.forChildren(baseIndex, this, index, leaf);
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        if (bitIsPresent(bitmask, bit)) {
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.assign(entryBaseIndex, shiftCount - 1, index, value);
            if (newChild != child) {
                final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
                if (newChildren.length == 1) {
                    return newChild;
                } else {
                    final int newSize = size - child.iterableSize() + newChild.iterableSize();
                    return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, newChildren, newSize);
                }
            } else {
                return this;
            }
        } else {
            final ArrayNode<T> newChild = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
            final ArrayNode<T>[] newChildren = ArrayHelper.insert(ArrayBranchNode::allocate, children, arrayIndex, newChild);
            assert newChildren.length > 1;
            return new ArrayBranchNode<>(shiftCount, baseIndex, addBit(bitmask, bit), newChildren, size + 1);
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        assert shiftCount >= this.shiftCount;
        if (shiftCount != this.shiftCount) {
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return this;
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.delete(shiftCount - 1, index);
            if (newChild != child) {
                final int newSize = size - child.iterableSize() + newChild.iterableSize();
                if (newSize == 0) {
                    return ArrayEmptyNode.of();
                } else if (newChild.isEmpty()) {
                    final ArrayNode<T>[] newChildren = ArrayHelper.delete(ArrayBranchNode::allocate, children, arrayIndex);
                    if (newChildren.length == 1) {
                        return newChildren[0];
                    } else {
                        return new ArrayBranchNode<>(shiftCount, baseIndex, removeBit(bitmask, bit), newChildren, newSize);
                    }
                } else {
                    final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
                    return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, newChildren, newSize);
                }

            }
        }
        return this;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        final Indexed<ArrayNode<T>> source = IndexedArray.retained(children);
        return GenericIterator.indexedState(parent, source, offset, limit);
    }

    @Override
    public void checkInvariants()
    {
        if (bitCount(bitmask) != children.length) {
            throw new IllegalStateException(String.format("invalid bitmask for array: bitmask=%s length=%d", Long.toBinaryString(bitmask), children.length));
        }
        if (children.length < 2) {
            throw new IllegalStateException(String.format("fewer than 2 children in branch: length=%d", children.length));
        }
        if (!checkChildShifts()) {
            throw new IllegalStateException("one or more children invalid for this branch");
        }
        if (computeSize() != size) {
            throw new IllegalStateException(String.format("size mismatch: size=%d computed=%d", size, computeSize()));
        }
    }

    @Override
    boolean isLeaf()
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> ArrayNode<T>[] allocate(int size)
    {
        return (ArrayNode<T>[])new ArrayNode[size];
    }

    private boolean checkChildShifts()
    {
        if (shiftCount == PARENT_SHIFTS) {
            for (ArrayNode<T> child : children) {
                if (!child.isLeaf()) {
                    return false;
                }
            }
        } else {
            for (ArrayNode<T> child : children) {
                if (child instanceof ArrayBranchNode) {
                    if (shiftCount <= ((ArrayBranchNode<?>)child).shiftCount) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int computeSize()
    {
        int total = 0;
        for (ArrayNode<T> child : children) {
            total += child.iterableSize();
        }
        return total;
    }
}
