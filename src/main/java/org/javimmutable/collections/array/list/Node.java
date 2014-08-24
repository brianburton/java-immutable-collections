package org.javimmutable.collections.array.list;

import org.javimmutable.collections.Cursorable;

public interface Node<T>
        extends Cursorable<T>
{
    int LEAF_DEPTH = 1;

    boolean isEmpty();

    boolean isFull();

    int size();

    int getDepth();

    TakeValueResult<T> takeFirstValue();

    TakeValueResult<T> takeLastValue();

    Node<T> insertFirstValue(T value);

    Node<T> insertLastValue(T value);

    boolean containsIndex(int index);

    T get(int index);

    Node<T> assign(int index,
                   T value);
}
