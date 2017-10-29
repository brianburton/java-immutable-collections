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
     * @return characteristics value used when creating Spliterators
     */
    int getSpliteratorCharacteristics();

    @Nonnull
    @Override
    default Spliterator<T> spliterator()
    {
        return iterator().spliterator(getSpliteratorCharacteristics());
    }

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
