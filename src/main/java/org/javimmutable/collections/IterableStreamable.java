package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.Spliterator;
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
    /**
     * Overridden here to require implementations to return a SplitableIterator rather than
     * a basic Iterator.  This is necessary to allow composition of new objects from methods
     * like keys() and values().
     */
    @Nonnull
    @Override
    SplitableIterator<T> iterator();

    /**
     * Overridden here to require implementations to create a proper spliterator() rather
     * than allowing the default, non-optimized implementation to be inherited from Iterable.
     */
    @Nonnull
    @Override
    Spliterator<T> spliterator();

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
