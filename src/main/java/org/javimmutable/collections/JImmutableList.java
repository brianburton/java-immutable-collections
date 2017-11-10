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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for containers that store items in list form with individual items available
 * for get() and assign() using their indexes.  Items inserted into the list are always
 * added at either the front or the end of the list and indexes of items are always in
 * the range 0 through size() - 1.
 */
@Immutable
public interface JImmutableList<T>
    extends Insertable<T, JImmutableList<T>>,
            Indexed<T>,
            Cursorable<T>,
            IterableStreamable<T>,
            InvariantCheckable
{
    interface Builder<T>
        extends MutableBuilder<T, JImmutableList<T>>
    {
    }

    /**
     * @return number of values in the list
     */
    int size();

    /**
     * Retrieves the value at the specified index (which must be within the bounds
     * of the list).
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    T get(int index);

    /**
     * Replaces the value at the specified index (which must be within current
     * bounds of the list) with the new value.
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Nonnull
    JImmutableList<T> assign(int index,
                             @Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     */
    @Nonnull
    JImmutableList<T> insert(@Nullable T value);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     */
    @Nonnull
    JImmutableList<T> insert(@Nonnull Iterable<? extends T> values);

    /**
     * Adds a value to the front of the list.  May be invoked on an empty list.
     * Synonym for insert()
     */
    @Nonnull
    JImmutableList<T> insertFirst(@Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     * Synonym for insert().
     */
    @Nonnull
    JImmutableList<T> insertLast(@Nullable T value);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAll(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAll(@Nonnull Collection<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAll(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllFirst(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllFirst(@Nonnull Collection<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllFirst(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllFirst(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllLast(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllLast(@Nonnull Collection<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllLast(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableList<T> insertAllLast(@Nonnull Iterator<? extends T> values);

    /**
     * Removes the first value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new JImmutableList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    JImmutableList<T> deleteFirst();

    /**
     * Removes the last value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new JImmutableList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    JImmutableList<T> deleteLast();

    /**
     * @return true only if list contains no values
     */
    boolean isEmpty();

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    JImmutableList<T> deleteAll();

    /**
     * Returns an unmodifiable List implementation backed by this list.
     */
    @Nonnull
    List<T> getList();

    /**
     * Returns a list of the same type as this containing only those elements for which
     * predicate returns true.  Implementations are optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns true
     */
    @Nonnull
    default JImmutableList<T> select(@Nonnull Predicate<T> predicate)
    {
        JImmutableList<T> answer = deleteAll();
        for (T value : this) {
            if (predicate.test(value)) {
                answer = answer.insert(value);
            }
        }
        return answer.size() == size() ? this : answer;
    }

    /**
     * Returns a list of the same type as this containing all those elements for which
     * predicate returns false.  Implementations can be optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns false
     */
    @Nonnull
    default JImmutableList<T> reject(@Nonnull Predicate<T> predicate)
    {
        return select(predicate.negate());
    }
}
