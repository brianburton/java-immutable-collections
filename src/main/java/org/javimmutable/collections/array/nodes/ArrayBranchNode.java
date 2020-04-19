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

import static org.javimmutable.collections.common.HamtIntMath.*;

public class ArrayBranchNode<T>
    extends ArrayNode<T>
    implements ArrayHelper.Allocator<ArrayNode<T>>
{
    @SuppressWarnings("rawtypes")
    private static final ArrayBranchNode EMPTY = new ArrayBranchNode();

    private final int bitmask;
    private final ArrayNode<T>[] children;
    private final int size;

    private ArrayBranchNode()
    {
        bitmask = 0;
        children = allocate(0);
        size = 0;
    }

    private ArrayBranchNode(int bitmask,
                            ArrayNode<T>[] children,
                            int size)
    {
        assert bitCount(bitmask) == children.length;
        this.bitmask = bitmask;
        this.children = children;
        this.size = size;
        assert computeSize() == size;
    }

    private int computeSize()
    {
        int total = 0;
        for (ArrayNode<T> child : children) {
            total += child.valueCount();
        }
        return total;
    }

    @SuppressWarnings("unchecked")
    static <T> ArrayNode<T> forValue(int entryBaseIndex,
                                     int shiftCount,
                                     int index,
                                     T value)
    {
        final ArrayNode<T> empty = (ArrayNode<T>)EMPTY;
        return empty.assign(entryBaseIndex, shiftCount, index, value);
    }

    @Override
    public int valueCount()
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
        assert shiftCount > LEAF_SHIFTS;
        final int childIndex = indexAtShift(shiftCount, index);
        final int bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].getValueOr(shiftCount - 1, index, defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        assert shiftCount > LEAF_SHIFTS;
        final int childIndex = indexAtShift(shiftCount, index);
        final int bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].find(shiftCount - 1, index);
        } else {
            return Holders.of();
        }
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        assert shiftCount > LEAF_SHIFTS;
        final int childIndex = indexAtShift(shiftCount, index);
        final int bit = bitFromIndex(childIndex);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        if (bitIsPresent(bitmask, bit)) {
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.assign(entryBaseIndex, shiftCount - 1, index, value);
            if (newChild != child) {
                final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
                final int newSize = size - child.valueCount() + newChild.valueCount();
                return new ArrayBranchNode<>(bitmask, newChildren, newSize);
            } else {
                return this;
            }
        } else {
            final ArrayNode<T> newChild;
            if (shiftCount == PARENT_SHIFTS) {
                newChild = ArrayLeafNode.forValue(entryBaseIndex, index, value);
            } else {
                newChild = new ArrayBranchNode<T>().assign(entryBaseIndex, shiftCount - 1, index, value);
            }
            final ArrayNode<T>[] newChildren = ArrayHelper.insert(this, children, arrayIndex, newChild);
            return new ArrayBranchNode<>(addBit(bitmask, bit), newChildren, size + 1);
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        assert shiftCount > LEAF_SHIFTS;
        final int childIndex = indexAtShift(shiftCount, index);
        final int bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.delete(shiftCount - 1, index);
            if (newChild != child) {
                final int newSize = size - child.valueCount() + newChild.valueCount();
                if (newSize == 0) {
                    return ArrayEmptyNode.of();
                } else if (newChild.isEmpty()) {
                    final ArrayNode<T>[] newChildren = ArrayHelper.delete(this, children, arrayIndex);
                    return new ArrayBranchNode<>(removeBit(bitmask, bit), newChildren, newSize);
                } else {
                    final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
                    return new ArrayBranchNode<>(bitmask, newChildren, newSize);
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

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ArrayNode<T>[] allocate(int size)
    {
        return (ArrayNode<T>[])new ArrayNode[size];
    }
}
