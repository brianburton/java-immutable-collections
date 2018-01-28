///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface for classes that can produce java.util.Streams and are also Iterable.   The default stream
 * creation implementations use spliterator().
 */
public interface IterableStreamable<T>
    extends SplitableIterable<T>,
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

    /**
     * Default implementation that creates a Spliterator from a newly created Iterator.
     */
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

    /**
     * Count the total number of elements in iterator.
     *
     * @return total number of elements in iterator.
     */
    default int count()
    {
        int answer = 0;
        for (T ignored : this) {
            answer += 1;
        }
        return answer;
    }

    /**
     * Apply the predicate to every element in iterator order and return number of times
     * predicate returned true.
     *
     * @param predicate test applied to each element
     * @return number of times predicate returned true
     */
    default int count(@Nonnull Predicate<T> predicate)
    {
        int answer = 0;
        for (T value : this) {
            if (predicate.test(value)) {
                answer += 1;
            }
        }
        return answer;
    }

    /**
     * Apply the predicate to every element in iterator order and return true if the
     * predicate returns true for all elements.  Iteration of elements stops immediately
     * if predicate returns false.  Returns true if there are no elements.
     *
     * @param predicate test to apply to each element
     * @return true if no elements to process or predicate returned true for all elements, otherwise false
     */
    default boolean allMatch(@Nonnull Predicate<T> predicate)
    {
        for (T value : this) {
            if (!predicate.test(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply the predicate to every element in iterator order and return true if the
     * predicate returns true for any element.  Iteration of elements stops immediately
     * if predicate returns true.  Returns false if there are no elements.
     *
     * @param predicate test to apply to each element
     * @return false if no elements to process or predicate returned false for all elements, otherwise true
     */
    default boolean anyMatch(@Nonnull Predicate<T> predicate)
    {
        for (T value : this) {
            if (predicate.test(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Apply the predicate to every element in iterator order until the predicate returns true.
     *
     * @param predicate test to apply to each element
     * @return empty if predicate always returns false otherwise Holder containing value for which predicate returned true
     */
    default Holder<T> first(@Nonnull Predicate<T> predicate)
    {
        for (T value : this) {
            if (predicate.test(value)) {
                return Holders.of(value);
            }
        }
        return Holders.of();
    }

    /**
     * Add all elements to the collection.
     *
     * @param collection collection to accumulate the values
     * @return the collection after all elements have been processed
     */
    default <C extends Insertable<T, C>> C collect(@Nonnull C collection)
    {
        for (T value : this) {
            collection = collection.insert(value);
        }
        return collection;
    }

    /**
     * Add the first maxToCollect elements to the collection
     *
     * @param collection collection to accumulate the values
     * @return the collection after all elements have been processed
     */
    default <C extends Insertable<T, C>> C collect(int maxToCollect,
                                                   @Nonnull C collection)
    {
        final Iterator<T> iterator = iterator();
        for (int i = 0; i < maxToCollect && iterator.hasNext(); ++i) {
            collection = collection.insert(iterator.next());
        }
        return collection;
    }

    /**
     * Apply the predicate to every element in iterator order and add any elements for which
     * predicate returns true to the collection.
     *
     * @param collection collection to accumulate the values
     * @param predicate  predicate applied to each element
     * @return the collection after all elements have been processed
     */
    default <C extends Insertable<T, C>> C collect(@Nonnull C collection,
                                                   @Nonnull Predicate<T> predicate)
    {
        for (T value : this) {
            if (predicate.test(value)) {
                collection = collection.insert(value);
            }
        }
        return collection;
    }

    /**
     * Apply the predicate to every element in iterator order and add any elements for which
     * predicate returns true to the collection.  Iteration stops if maxToCollect values have
     * been added to collection.
     *
     * @param collection collection to accumulate the values
     * @param predicate  predicate applied to each element
     * @return the collection after all elements have been processed
     */
    default <C extends Insertable<T, C>> C collect(int maxToCollect,
                                                   @Nonnull C collection,
                                                   @Nonnull Predicate<T> predicate)
    {
        final Iterator<T> iterator = iterator();
        while (maxToCollect > 0 && iterator.hasNext()) {
            final T value = iterator.next();
            if (predicate.test(value)) {
                collection = collection.insert(value);
                maxToCollect -= 1;
            }
        }
        return collection;
    }

    /**
     * Apply the transform function to all elements in iterator order and add each transformed
     * value to the specified collection.
     *
     * @param collection collection to accumulate the transformed values
     * @param transform  transformation applied to each element
     * @return the collection after all elements have been processed
     */
    default <A, C extends Insertable<A, C>> C transform(@Nonnull C collection,
                                                        @Nonnull Func1<T, A> transform)
    {
        for (T value : this) {
            collection = collection.insert(transform.apply(value));
        }
        return collection;
    }

    /**
     * Apply the transform function to all elements in iterator order and add the contents of
     * non-empty Holders to the specified collection.  Stops iteration and returns immediately
     * if maxToCollect values have been added to the collection.
     *
     * @param maxToCollect maximum number of values to add to collection
     * @param collection   collection to accumulate the transformed values
     * @param transform    transformation applied to each element
     * @return the collection after all elements have been processed
     */
    default <A, C extends Insertable<A, C>> C transform(int maxToCollect,
                                                        @Nonnull C collection,
                                                        @Nonnull Func1<T, A> transform)
    {
        final Iterator<T> iterator = iterator();
        while (maxToCollect > 0 && iterator.hasNext()) {
            final T value = iterator.next();
            collection = collection.insert(transform.apply(value));
            maxToCollect -= 1;
        }
        return collection;
    }

    /**
     * Apply the transform function to all elements in iterator order and add the contents of
     * non-empty Holders to the specified collection.
     *
     * @param collection collection to accumulate the transformed values
     * @param transform  transformation applied to each element
     * @return the collection after all elements have been processed
     */
    default <A, C extends Insertable<A, C>> C transformSome(@Nonnull C collection,
                                                            @Nonnull Func1<T, Holder<A>> transform)
    {
        for (T value : this) {
            Holder<A> transformed = transform.apply(value);
            if (transformed.isFilled()) {
                collection = collection.insert(transformed.getValue());
            }
        }
        return collection;
    }

    /**
     * Apply the transform function to all elements in iterator order and add the contents of
     * non-empty Holders to the specified collection.  Stops iteration and returns immediately
     * if maxToCollect values have been added to the collection.
     *
     * @param maxToCollect maximum number of values to add to collection
     * @param collection   collection to accumulate the transformed values
     * @param transform    transformation applied to each element
     * @return the collection after all elements have been processed
     */
    default <A, C extends Insertable<A, C>> C transformSome(int maxToCollect,
                                                            @Nonnull C collection,
                                                            @Nonnull Func1<T, Holder<A>> transform)
    {
        final Iterator<T> iterator = iterator();
        while (maxToCollect > 0 && iterator.hasNext()) {
            final T value = iterator.next();
            Holder<A> transformed = transform.apply(value);
            if (transformed.isFilled()) {
                collection = collection.insert(transformed.getValue());
                maxToCollect -= 1;
            }
        }
        return collection;
    }

    /**
     * Apply the predicate to all elements in iterator order and add the elements to
     * the specified collections based on predicate return value.  All elements for which predicate
     * returns true are added to matched.  All others are added to unmatched.  Resulting
     * collections are packaged into Partitions object and returned.
     *
     * @param matched   collection to accumulate the matched elements
     * @param unmatched collection to accumulate the unmatched elements
     * @param predicate predicate applied to each element
     * @return Partitions containing (matched,unmatched)
     */
    default <C extends Insertable<T, C>> Partitions<C> partition(@Nonnull C matched,
                                                                 @Nonnull C unmatched,
                                                                 @Nonnull Predicate<T> predicate)
    {
        for (T value : this) {
            if (predicate.test(value)) {
                matched = matched.insert(value);
            } else {
                unmatched = unmatched.insert(value);
            }
        }
        return new Partitions<>(matched, unmatched);
    }

    /**
     * Apply the specified accumulator to all elements in iterator order calling the accumulator function
     * for each element.  The first call to accumulator is passed the first element in the sequence.
     * All remaining calls to accumulator are passed the result from the previous call.
     *
     * @param accumulator method called to compute result
     * @return empty Holder if the sequence is empty, otherwise Holder containing result from last call to accumulator
     */
    default Holder<T> reduce(Func2<T, T, T> accumulator)
    {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return Holders.of();
        }
        T answer = iterator.next();
        while (iterator.hasNext()) {
            answer = accumulator.apply(answer, iterator.next());
        }
        return Holders.of(answer);
    }

    class Partitions<T>
    {
        private final T matched;
        private final T unmatched;

        public Partitions(T matched,
                          T unmatched)
        {
            this.matched = matched;
            this.unmatched = unmatched;
        }

        public T getMatched()
        {
            return matched;
        }

        public T getUnmatched()
        {
            return unmatched;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Partitions<?> that = (Partitions<?>)o;
            return Objects.equals(matched, that.matched) &&
                   Objects.equals(unmatched, that.unmatched);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(matched, unmatched);
        }
    }
}
