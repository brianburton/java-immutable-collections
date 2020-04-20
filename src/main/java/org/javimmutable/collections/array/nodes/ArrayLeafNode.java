package org.javimmutable.collections.array.nodes;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.HamtLongMath;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static org.javimmutable.collections.common.HamtLongMath.*;

@Immutable
public class ArrayLeafNode<T>
    extends ArrayNode<T>
{
    private final int iteratorBaseIndex;
    private final int baseIndex;
    private final long bitmask;
    private final T[] values;

    private ArrayLeafNode(int iteratorBaseIndex,
                          int baseIndex,
                          long bitmask,
                          T[] values)
    {
        assert bitCount(bitmask) == values.length;
        assert values.length >= 2;
        this.iteratorBaseIndex = iteratorBaseIndex;
        this.baseIndex = baseIndex;
        this.bitmask = bitmask;
        this.values = values;
    }

    public static <T> ArrayNode<T> forValues(int entryBaseIndex,
                                             int index1,
                                             T value1,
                                             int index2,
                                             T value2)
    {
        assert baseIndexFromHashCode(index1) == baseIndexFromHashCode(index2);
        final int arrayIndex1 = indexFromHashCode(index1);
        final int arrayIndex2 = indexFromHashCode(index2);
        final long bitmask = bitFromIndex(arrayIndex1) | bitFromIndex(arrayIndex2);
        final int baseIndex = baseIndexFromHashCode(index1);
        final int iteratorBaseIndex = entryBaseIndex + baseIndex;
        assert baseIndexFromHashCode(index2) == baseIndex;
        final T[] values;
        if (index1 < index2) {
            values = ArrayHelper.newArray(value1, value2);
        } else {
            values = ArrayHelper.newArray(value2, value1);
        }
        return new ArrayLeafNode<>(iteratorBaseIndex, baseIndex, bitmask, values);
    }

    @Override
    public int iterableSize()
    {
        return values.length;
    }

    @Override
    public boolean isEmpty()
    {
        return bitmask == 0;
    }

    @Override
    public T getValueOr(int shiftCount,
                        int index,
                        T defaultValue)
    {
        final int valueIndex = indexFromHashCode(index);
        if (baseIndex + valueIndex != index) {
            return defaultValue;
        }
        final long bit = bitFromIndex(valueIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return values[arrayIndex];
        } else {
            return defaultValue;
        }
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        final int valueIndex = indexFromHashCode(index);
        if (baseIndex + valueIndex != index) {
            return Holders.of();
        }
        final long bit = bitFromIndex(valueIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return Holders.of(values[arrayIndex]);
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
        if (shiftCount > LEAF_SHIFTS && baseIndexFromHashCode(index) != baseIndex) {
            final ArrayNode<T> leaf = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
            return ArrayBranchNode.forChildren(baseIndex, this, index, leaf);
        }
        final int valueIndex = indexFromHashCode(index);
        final long bit = bitFromIndex(valueIndex);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        if (bitIsPresent(bitmask, bit)) {
            final T[] newValues = ArrayHelper.assign(values, arrayIndex, value);
            return new ArrayLeafNode<>(iteratorBaseIndex, baseIndex, bitmask, newValues);
        } else {
            final T[] newValues = ArrayHelper.insert(values, arrayIndex, value);
            return new ArrayLeafNode<>(iteratorBaseIndex, baseIndex, addBit(bitmask, bit), newValues);
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        final int valueIndex = indexFromHashCode(index);
        if (baseIndex + valueIndex != index) {
            return this;
        }
        final long bit = bitFromIndex(valueIndex);
        if (bitIsPresent(bitmask, bit)) {
            final long newBitmask = removeBit(bitmask, bit);
            if (values.length == 2) {
                assert bitCount(newBitmask) == 1;
                final int keepValueIndex = indexForBit(newBitmask);
                final int keepArrayIndex = arrayIndexForBit(bitmask, newBitmask);
                return new ArraySingleLeafNode<>(iteratorBaseIndex + keepValueIndex, baseIndex + keepValueIndex, values[keepArrayIndex]);
            } else {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                final T[] newValues = ArrayHelper.delete(values, arrayIndex);
                return new ArrayLeafNode<>(iteratorBaseIndex, baseIndex, newBitmask, newValues);
            }
        }
        return this;
    }

    @Nonnull
    private JImmutableMap.Entry<Integer, T> valueEntry(int valueIndex)
    {
        final long bit = bitFromIndex(valueIndex);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        final T value = values[arrayIndex];
        return MapEntry.entry(iteratorBaseIndex + valueIndex, value);
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        final Indexed<Integer> indices = HamtLongMath.indices(bitmask);
        final Indexed<JImmutableMap.Entry<Integer, T>> entries = IndexedHelper.transformed(indices, this::valueEntry);
        return GenericIterator.multiValueState(parent, entries, offset, limit);
    }

    @Override
    public void checkInvariants()
    {
        if (bitCount(bitmask) != values.length) {
            throw new IllegalStateException(String.format("invalid bitmask for array: bitmask=%s length=%d", Long.toBinaryString(bitmask), values.length));
        }
    }

    @Override
    boolean isLeaf()
    {
        return true;
    }
}
