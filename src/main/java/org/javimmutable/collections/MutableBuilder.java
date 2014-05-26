package org.javimmutable.collections;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for mutable objects used to produce other objects by adding objects to the builder
 * and then calling a build() method.  MutableBuilders are only required to support a single
 * call to build().  They may throw IllegalStateException if build is called more than once.
 */
public interface MutableBuilder<T, C>
{
    MutableBuilder<T, C> add(T value);

    C build();

    MutableBuilder<T, C> add(Cursor<? extends T> source);

    MutableBuilder<T, C> add(Iterator<? extends T> source);

    MutableBuilder<T, C> add(Collection<? extends T> source);

    <K extends T> MutableBuilder<T, C> add(K... source);
}
