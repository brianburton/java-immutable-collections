package org.javimmutable.collections.array.nodes;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nullable;

import static org.javimmutable.collections.MapEntry.entry;
import static org.javimmutable.collections.common.HamtLongMath.baseIndexFromHashCode;

public class ArraySingleLeafNode<T>
    extends ArrayNode<T>
{
    private final int iteratorIndex;
    private final int index;
    private final T value;

    ArraySingleLeafNode(int iteratorIndex,
                        int index,
                        T value)
    {
        this.iteratorIndex = iteratorIndex;
        this.index = index;
        this.value = value;
    }

    public static <T> ArrayNode<T> forValue(int entryBaseIndex,
                                            int index,
                                            T value)
    {
        return new ArraySingleLeafNode<>(entryBaseIndex + index, index, value);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public T getValueOr(int shiftCount,
                        int index,
                        T defaultValue)
    {
        if (index == this.index) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        if (index == this.index) {
            return Holders.of(value);
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
        if (index == this.index) {
            return new ArraySingleLeafNode<>(iteratorIndex, index, value);
        } else if (baseIndexFromHashCode(index) == baseIndexFromHashCode(this.index)) {
            return ArrayLeafNode.forValues(entryBaseIndex, this.index, this.value, index, value);
        } else {
            final ArrayNode<T> leaf = forValue(entryBaseIndex, index, value);
            return ArrayBranchNode.forChildren(this.index, this, index, leaf);
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        if (index == this.index) {
            return ArrayEmptyNode.of();
        }
        return this;
    }

    @Override
    public void checkInvariants()
    {
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        return GenericIterator.valueState(parent, entry(iteratorIndex, value));
    }

    @Override
    public int iterableSize()
    {
        return 1;
    }

    @Override
    boolean isLeaf()
    {
        return true;
    }
}
