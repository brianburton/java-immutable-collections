package org.javimmutable.collections.array.list;

class ListHelper
{
    @SuppressWarnings("unchecked")
    static <T> Node<T>[] allocateNodes(int size)
    {
        return (Node<T>[])new Node[size];
    }

    @SuppressWarnings("unchecked")
    static <T> T[] allocateValues(int size)
    {
        return (T[])new Object[size];
    }

    static int sizeForDepth(int depth)
    {
        return 1 << (5 * depth);
    }
}
