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
    private final long bitmask;
    private final T[] values;

    private ArrayLeafNode(int iteratorBaseIndex,
                          long bitmask,
                          T[] values)
    {
        assert bitCount(bitmask) == values.length;
        this.iteratorBaseIndex = iteratorBaseIndex;
        this.bitmask = bitmask;
        this.values = values;
    }

    static <T> ArrayNode<T> forValue(int entryBaseIndex,
                                     int index,
                                     T value)
    {
        final int arrayIndex = indexFromHashCode(index);
        final long bitmask = bitFromIndex(arrayIndex);
        final int iteratorBaseIndex = entryBaseIndex + (index - arrayIndex);
        final T[] values = ArrayHelper.newArray(value);
        return new ArrayLeafNode<>(iteratorBaseIndex, bitmask, values);
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
        assert shiftCount == LEAF_SHIFTS;
        assert indexAtShift(shiftCount, index) == indexFromHashCode(index);
        final int valueIndex = indexFromHashCode(index);
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
        assert shiftCount == LEAF_SHIFTS;
        assert indexAtShift(shiftCount, index) == indexFromHashCode(index);
        final int valueIndex = indexFromHashCode(index);
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
        assert shiftCount == LEAF_SHIFTS;
        assert indexAtShift(shiftCount, index) == indexFromHashCode(index);
        final int valueIndex = indexFromHashCode(index);
        final long bit = bitFromIndex(valueIndex);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        if (bitIsPresent(bitmask, bit)) {
            final T[] newValues = ArrayHelper.assign(values, arrayIndex, value);
            return new ArrayLeafNode<>(iteratorBaseIndex, bitmask, newValues);
        } else {
            final T[] newValues = ArrayHelper.insert(values, arrayIndex, value);
            return new ArrayLeafNode<>(iteratorBaseIndex, addBit(bitmask, bit), newValues);
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        assert shiftCount == LEAF_SHIFTS;
        assert indexAtShift(shiftCount, index) == indexFromHashCode(index);
        final int valueIndex = indexFromHashCode(index);
        final long bit = bitFromIndex(valueIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return new ArrayLeafNode<>(iteratorBaseIndex, removeBit(bitmask, bit), ArrayHelper.delete(values, arrayIndex));
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
}
