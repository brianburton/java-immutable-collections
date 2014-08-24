package org.javimmutable.collections.array.list;

class ListHelper
{
    private static final Object[] EMPTY_VALUES = new Object[0];
    private static final Node[] EMPTY_NODES = new Node[0];

    @SuppressWarnings("unchecked")
    static <T> Node<T>[] allocateNodes(int size)
    {
        return (Node<T>[])((size == 0) ? EMPTY_NODES : new Node[size]);
    }

    @SuppressWarnings("unchecked")
    static <T> T[] allocateValues(int size)
    {
        return (T[])((size == 0) ? EMPTY_VALUES : new Object[size]);
    }

    static int sizeForDepth(int depth)
    {
        return 1 << (5 * depth);
    }
}
