package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface for classes that can produce java.util.Streams.   Extends Iterable so that the default implementations
 * can use spliterator() method defined there.
 */
public interface Streamable<T>
    extends Iterable<T>
{
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
