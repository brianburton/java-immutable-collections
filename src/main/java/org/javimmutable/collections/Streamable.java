package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Interface for objects that can produce a Stream on demand.  This interface only requires implementation
 * of a stream() method so it can easily be used with lambdas.
 * @param <T>
 */
public interface Streamable<T>
{
    /**
     * Produce a Stream supporting non-parallel computations.
     */
    @Nonnull
    Stream<T> stream();

    /**
     * Produce a Stream for use in parallel computations if possible.   Might return a non-parallel stream
     * if the underlying class does not support splitting the stream.
     */
    @Nonnull
    default Stream<T> parallelStream()
    {
        return stream();
    }
}
