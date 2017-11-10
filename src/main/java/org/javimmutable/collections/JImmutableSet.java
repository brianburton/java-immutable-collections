///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Interface for immutable sets.
 */
@SuppressWarnings("ClassWithTooManyMethods")
@Immutable
public interface JImmutableSet<T>
    extends Insertable<T, JImmutableSet<T>>,
            Cursorable<T>,
            IterableStreamable<T>,
            InvariantCheckable
{
    /**
     * Adds the single value to the Set.
     *
     * @return instance of set containing the value
     */
    @Nonnull
    @Override
    JImmutableSet<T> insert(@Nonnull T value);

    /**
     * Adds all of the elements of the specified collection to the set.
     *
     * @return instance of set containing the collection
     */
    @Nonnull
    @Override
    JImmutableSet<T> insertAll(@Nonnull Iterable<? extends T> values);

    /**
     * Adds all of the elements of the specified collection to the set.
     *
     * @return instance of set containing the collection
     */
    @Nonnull
    @Override
    JImmutableSet<T> insertAll(@Nonnull Cursor<? extends T> values);

    /**
     * Adds all of the elements of the specified collection to the set.
     *
     * @return instance of set containing the collection
     */
    @Nonnull
    @Override
    JImmutableSet<T> insertAll(@Nonnull Iterator<? extends T> values);

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
    boolean containsAll(@Nonnull Cursor<? extends T> values);

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
    boolean containsAny(@Nonnull Cursor<? extends T> values);

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
    JImmutableSet<T> delete(T value);

    /**
     * Removes all values of other from the Set.  Has no effect if none of the values are in the Set
     *
     * @return instance of set without the values
     */
    @Nonnull
    JImmutableSet<T> deleteAll(@Nonnull Iterable<? extends T> other);

    /**
     * Removes all values of other from the Set.  Has no effect if none of the values are in the Set
     *
     * @return instance of set without the values
     */
    @Nonnull
    JImmutableSet<T> deleteAll(@Nonnull Cursor<? extends T> other);

    /**
     * Removes all values of other from the Set.  Has no effect if none of the values are in the Set
     *
     * @return instance of set without the values
     */
    @Nonnull
    JImmutableSet<T> deleteAll(@Nonnull Iterator<? extends T> other);

    /**
     * Adds all values from other to the Set.
     *
     * @param other source of values to add
     * @return instance of set containing the values
     */
    @Nonnull
    JImmutableSet<T> union(@Nonnull Iterable<? extends T> other);

    /**
     * Adds all values from other to the Set.
     *
     * @param values source of values to add
     * @return instance of set containing the values
     */
    @Nonnull
    JImmutableSet<T> union(@Nonnull Cursor<? extends T> values);

    /**
     * Adds all values from other to the Set.
     *
     * @param values source of values to add
     * @return instance of set containing the values
     */
    @Nonnull
    JImmutableSet<T> union(@Nonnull Iterator<? extends T> values);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    JImmutableSet<T> intersection(@Nonnull Iterable<? extends T> other);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    JImmutableSet<T> intersection(@Nonnull Cursor<? extends T> values);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    JImmutableSet<T> intersection(@Nonnull JImmutableSet<? extends T> other);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @return instance of set with unmatched values removed
     */
    @Nonnull
    JImmutableSet<T> intersection(@Nonnull Set<? extends T> other);

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
     * @return an equivalent collection with no values
     */
    @Nonnull
    JImmutableSet<T> deleteAll();

    /**
     * @return an unmodifiable Set implementation backed by this set.
     */
    @Nonnull
    Set<T> getSet();

    /**
     * Returns a set of the same type as this containing only those elements for which
     * predicate returns true.  Implementations are optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return set of same type as this containing only those elements for which predicate returns true
     */
    @Nonnull
    default JImmutableSet<T> select(@Nonnull Predicate<T> predicate)
    {
        JImmutableSet<T> answer = deleteAll();
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
    default JImmutableSet<T> reject(@Nonnull Predicate<T> predicate)
    {
        JImmutableSet<T> answer = this;
        for (T value : this) {
            if (predicate.test(value)) {
                answer = answer.delete(value);
            }
        }
        return answer.size() == size() ? this : answer;
    }
}
