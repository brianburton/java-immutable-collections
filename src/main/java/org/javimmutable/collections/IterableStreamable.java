package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface for classes that can produce java.util.Streams and are also Iterable.   The default stream
 * creation implementations use spliterator().
 */
public interface IterableStreamable<T>
    extends Iterable<T>,
            Streamable<T>
{
    @Nonnull
    @Override
    SplitableIterator<T> iterator();

    @Nonnull
    default Stream<T> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    @Nonnull
    default Stream<T> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }
}
