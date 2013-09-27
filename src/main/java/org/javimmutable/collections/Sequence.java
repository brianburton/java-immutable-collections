package org.javimmutable.collections;

public interface Sequence<T>
{
    /**
     * Determines if this is the end of the Sequence.  When try the Sequence is empty and
     * getHead() cannot be called.  getTail() can still be called but will always return
     * an empty Sequence once isEmpty() returns true.
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Accesses the first value in the Sequence.
     *
     * @return
     */
    T getHead();

    /**
     * Accesses the rest of the Sequence.
     *
     * @return
     */
    Sequence<T> getTail();
}
