///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Interface for immutable sets.
 */
@SuppressWarnings("ClassWithTooManyMethods")
@Immutable
public interface ISet<T>
    extends Insertable<T, ISet<T>>,
            IterableStreamable<T>,
            Mapped<T, T>,
            InvariantCheckable,
            Serializable
{
    /**
     * Adds the single value to the Set.
     *
     * @return instance of set containing the value
     */
    @Nonnull
    @Override
    ISet<T> insert(@Nonnull T value);

    /**
     * Adds all of the elements of the specified collection to the set.
     *
     * @return instance of set containing the collection
     */
    @Nonnull
    @Override
    ISet<T> insertAll(@Nonnull Iterable<? extends T> values);

    /**
     * Adds all of the elements of the specified collection to the set.
     *
     * @return instance of set containing the collection
     */
    @Nonnull
    @Override
    ISet<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Determines if the Set contains the specified value.
     *
     * @return true if the Set contains the value
     */
    boolean contains(@Nullable T value);

    /**
     * Determines if the Set contains all values in the specified collection.
     *
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull Iterable<? extends T> values);

    /**
     * Determines if the Set contains all values in the specified collection.
     *
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull Iterator<? extends T> values);

    /**
     * Determines if the Set contains any values in the specified collection.
     *
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull Iterable<? extends T> values);

    /**
     * Determines if the Set contains any values in the specified collection.
     *
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull Iterator<? extends T> values);

    /**
     * Removes the value from the Set.  Has no effect if the value is not in the Set.
     *
     * @return instance of set without the value
     */
    @Nonnull
    ISet<T> delete(T value);

    /**
     * Removes all values of other from the Set.  Has no effect if none of the values are in the Set
     *
     * @return instance of set without the values
     */
    @Nonnull
    ISet<T> deleteAll(@Nonnull Iterable<? extends T> other);

    /**
     * Removes all values of other from the Set.  Has no effect if none of the values are in the Set
     *
     * @return instance of set without the values
     */
    @Nonnull
    ISet<T> deleteAll(@Nonnull Iterator<? extends T> other);

    /**
     * Adds all values from other to the Set.
     *
     * @param other source of values to add
     * @return instance of set containing the values
     */
    @Nonnull
    ISet<T> union(@Nonnull Iterable<? extends T> other);

    /**
     * Adds all values from other to the Set.
     *
     * @param values source of values to add
     * @return instance of set containing the values
     */
    @Nonnull
    ISet<T> union(@Nonnull Iterator<? extends T> values);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    ISet<T> intersection(@Nonnull Iterable<? extends T> other);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    ISet<T> intersection(@Nonnull Iterator<? extends T> values);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    ISet<T> intersection(@Nonnull ISet<? extends T> other);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    ISet<T> intersection(@Nonnull Set<? extends T> other);

    /**
     * Determines the number of values in the Set.
     *
     * @return number of values in the Set
     */
    int size();

    /**
     * @return true only if set contains no values
     */
    boolean isEmpty();

    /**
     * @return true only if set contains values
     */
    default boolean isNonEmpty()
    {
        return !isEmpty();
    }

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    ISet<T> deleteAll();

    /**
     * @return an unmodifiable Set implementation backed by this set.
     */
    @Nonnull
    Set<T> getSet();

    /**
     * Returns the specified key if its contained in the set.  Otherwise returns null.
     *
     * @param key identifies the value to retrieve
     * @return the key or null
     */
    @Nullable
    @Override
    default T get(T key)
    {
        return contains(key) ? key : null;
    }

    /**
     * Returns the specified key if its contained in the set.  Otherwise returns defaultValue.
     *
     * @param key identifies the value to retrieve
     * @return the key or defaultValue
     */
    @Override
    default T getValueOr(T key,
                         T defaultValue)
    {
        return contains(key) ? key : defaultValue;
    }

    /**
     * Returns a Holder containing the specified key if its contained in the set.
     * Otherwise returns an empty Holder.
     *
     * @param key identifies the value to retrieve
     * @return possibly empty Holder
     */
    @Nonnull
    @Override
    default Holder<T> find(T key)
    {
        return contains(key) ? Holders.nullable(key) : Holder.none();
    }

    /**
     * Apply the transform function to all elements in iterator order and add each transformed
     * value to build a new collection the same type as this.
     *
     * @param transform transformation applied to each element
     * @return the new collection after all elements have been processed
     */
    @SuppressWarnings("unchecked")
    default <A> ISet<A> transform(@Nonnull Func1<T, A> transform)
    {
        return transform((ISet)deleteAll(), transform);
    }

    /**
     * Returns a set of the same type as this containing only those elements for which
     * predicate returns true.  Implementations are optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return set of same type as this containing only those elements for which predicate returns true
     */
    @Nonnull
    default ISet<T> select(@Nonnull Predicate<T> predicate)
    {
        ISet<T> answer = deleteAll();
        for (T value : this) {
            if (predicate.test(value)) {
                answer = answer.insert(value);
            }
        }
        return answer.size() == size() ? this : answer;
    }

    /**
     * Returns a set of the same type as this containing all those elements for which
     * predicate returns false.  Implementations are optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return set of same type as this containing only those elements for which predicate returns false
     */
    @Nonnull
    default ISet<T> reject(@Nonnull Predicate<T> predicate)
    {
        ISet<T> answer = this;
        for (T value : this) {
            if (predicate.test(value)) {
                answer = answer.delete(value);
            }
        }
        return answer.size() == size() ? this : answer;
    }

    /**
     * Returns a Collector that creates a set of the same type as this containing all
     * of the collected values inserted over whatever starting values this already contained.
     */
    @Nonnull
    default Collector<T, ?, ISet<T>> setCollector()
    {
        return GenericCollector.unordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }
}
