package org.javimmutable.collections.iterators;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

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
    default Spliterator<T> spliterator(int characteristics)
    {
        return Spliterators.<T>spliterator(this, Long.MAX_VALUE, characteristics);
    }
}
