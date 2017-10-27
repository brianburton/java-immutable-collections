package org.javimmutable.collections.iterators;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Spliterator;

public interface SplitableIterator<T>
    extends Iterator<T>
{
    default boolean isSplitAllowed()
    {
        return false;
    }

    @Nonnull
    default SplitIterator<T> splitIterator()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    Spliterator<T> spliterator(int characteristics);
}
