package org.javimmutable.collections.array.nodes;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.HamtLongMath;
import org.javimmutable.collections.iterators.GenericIterator;

public abstract class ArrayNode<T>
    implements GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>
{
    public static final int ROOT_SHIFTS = HamtLongMath.maxShiftsForBitCount(30);
    static final int LEAF_SHIFTS = 0;
    static final int PARENT_SHIFTS = 1;

    public abstract int valueCount();

    public abstract boolean isEmpty();

    public abstract T getValueOr(int shiftCount,
                                 int index,
                                 T defaultValue);

    public abstract Holder<T> find(int shiftCount,
                                   int index);

    public abstract ArrayNode<T> assign(int entryBaseIndex,
                                        int shiftCount,
                                        int index,
                                        T value);

    public abstract ArrayNode<T> delete(int shiftCount,
                                        int index);

    @Override
    public int iterableSize()
    {
        return valueCount();
    }
}
