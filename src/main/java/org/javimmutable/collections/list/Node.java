package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursorable;

interface Node<T>
        extends Cursorable<T>
{
    boolean isEmpty();

    boolean isFull();

    int size();

    int getDepth();

    Node<T> deleteFirst();

    Node<T> deleteLast();

    Node<T> insertFirst(T value);

    Node<T> insertLast(T value);

    boolean containsIndex(int index);

    T get(int index);

    Node<T> assign(int index,
                   T value);

    void checkInvariants();
}
