package org.javimmutable.collections.array.nodes;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nullable;

public class ArrayEmptyNode<T>
    extends ArrayNode<T>
{
    @SuppressWarnings("rawtypes")
    private static final ArrayEmptyNode INSTANCE = new ArrayEmptyNode();

    private ArrayEmptyNode()
    {
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayNode<T> of()
    {
        return (ArrayNode<T>)INSTANCE;
    }

    @Override
    public int iterableSize()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public T getValueOr(int shiftCount,
                        int index,
                        T defaultValue)
    {
        return defaultValue;
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        return Holders.of();
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        assert shiftCount > LEAF_SHIFTS;
        return ArrayBranchNode.forValue(entryBaseIndex, shiftCount, index, value);
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        return this;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        return parent;
    }
}
