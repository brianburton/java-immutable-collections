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
    implements ArrayHelper.Allocator<T>
{
    private final int baseIndex;
    private final long bitmask;
    private final T[] values;

    private ArrayLeafNode(int baseIndex,
                          long bitmask,
                          T[] values)
    {
        assert bitCount(bitmask) == values.length;
        this.baseIndex = baseIndex;
        this.bitmask = bitmask;
        this.values = values;
    }

    private ArrayLeafNode(int entryBaseIndex,
                          int index,
                          T value)
    {
        final int arrayIndex = indexFromHashCode(index);
        final long bitmask = bitFromIndex(arrayIndex);
        this.baseIndex = entryBaseIndex + (index - arrayIndex);
        this.bitmask = bitmask;
        values = allocate(1);
        values[0] = value;
    }

    static <T> ArrayNode<T> forValue(int entryBaseIndex,
                                     int index,
                                     T value)
    {
        return new ArrayLeafNode<>(entryBaseIndex, index, value);
    }

    @Override
    public int valueCount()
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
            return new ArrayLeafNode<>(baseIndex, bitmask, newValues);
        } else {
            final T[] newValues = ArrayHelper.insert(this, values, arrayIndex, value);
            return new ArrayLeafNode<>(baseIndex, addBit(bitmask, bit), newValues);
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
            return new ArrayLeafNode<>(baseIndex, removeBit(bitmask, bit), ArrayHelper.delete(this, values, arrayIndex));
        } else {
            return this;
        }
    }

    @Nonnull
    private JImmutableMap.Entry<Integer, T> valueEntry(int valueIndex)
    {
        final long bit = bitFromIndex(valueIndex);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        final T value = values[arrayIndex];
        return MapEntry.entry(baseIndex + valueIndex, value);
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

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T[] allocate(int size)
    {
        return (T[])new Object[size];
    }
}
