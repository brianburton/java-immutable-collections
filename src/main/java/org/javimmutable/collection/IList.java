///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * Interface for containers that store items in list form with individual items available
 * for get() and assign() using their indexes.  Items inserted into the list are always
 * added at either the front or the end of the list and indexes of items are always in
 * the range 0 through size() - 1.
 */
@Immutable
public interface IList<T>
    extends IDeque<T>
{
    /**
     * Replaces the value at the specified index (which must be within current
     * bounds of the list) with the new value.
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Nonnull
    @Override
    IList<T> assign(int index,
                    @Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     */
    @Nonnull
    @Override
    IList<T> insert(@Nullable T value);

    /**
     * Insert value at index (which must be within 0 to size).
     * Shifts all values at and after index one position to the right and adds 1
     * to size of the list.
     */
    @Nonnull
    IList<T> insert(int index,
                    @Nullable T value);

    /**
     * Adds a value to the front of the list.  May be invoked on an empty list.
     * Synonym for insert()
     */
    @Nonnull
    @Override
    IList<T> insertFirst(@Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     * Synonym for insert().
     */
    @Nonnull
    @Override
    IList<T> insertLast(@Nullable T value);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IList<T> insertAll(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IList<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Inserts all elements at index (which must be within 0 to size) in the same
     * order they appear in the Iterable.
     * Shifts all values at and after index x positions to the right and adds x
     * to size of the list, where x is the number of elements being inserted.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    IList<T> insertAll(int index,
                       @Nonnull Iterable<? extends T> values);

    /**
     * Inserts all elements at index (which must be within 0 to size) in the same
     * order they appear in the Iterable.
     * Shifts all values at and after index x positions to the right and adds x
     * to size of the list, where x is the number of elements being inserted.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    IList<T> insertAll(int index,
                       @Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IList<T> insertAllFirst(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IList<T> insertAllFirst(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IList<T> insertAllLast(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IList<T> insertAllLast(@Nonnull Iterator<? extends T> values);

    /**
     * Removes the first value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new JImmutableList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    @Override
    IList<T> deleteFirst();

    /**
     * Removes the last value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new JImmutableList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    @Override
    IList<T> deleteLast();

    /**
     * Delete value at index (which must be within the current bounds of the list).
     * Shifts all values at and after index one position to the left and subtracts 1
     * from size of the list.
     */
    @Nonnull
    IList<T> delete(int index);

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    @Override
    IList<T> deleteAll();

    /**
     * Returns a list containing the same elements as this list but with their order reversed
     * so that first in this list is last in returned list etc.
     */
    @Nonnull
    @Override
    IList<T> reverse();

    /**
     * Returns a list of the same type as this containing only those elements for which
     * predicate returns true.  Implementations are optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns true
     */
    @Nonnull
    @Override
    IList<T> select(@Nonnull Predicate<T> predicate);

    /**
     * Returns a list of the same type as this containing all those elements for which
     * predicate returns false.  Implementations can be optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns false
     */
    @Nonnull
    @Override
    IList<T> reject(@Nonnull Predicate<T> predicate);

    /**
     * Returns a Collector that creates a list of the same type as this containing all
     * of the collected values inserted after whatever starting values this already contained.
     */
    @Nonnull
    default Collector<T, ?, IList<T>> listCollector()
    {
        return GenericCollector.ordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    /**
     * Apply the transform function to all elements in iterator order and add each transformed
     * value to a new collection of this type.
     *
     * @param transform transformation applied to each element
     * @return the collection after all elements have been processed
     */
    @Override
    <A> IList<A> transform(@Nonnull Func1<T, A> transform);

    /**
     * Apply the transform function to all elements in iterator order and add the contents of
     * non-empty Holders to a new collection of this type.
     *
     * @param transform transformation applied to each element
     * @return the collection after all elements have been processed
     */
    @Override
    <A> IList<A> transformSome(@Nonnull Func1<T, Maybe<A>> transform);

    /**
     * Return the (possibly empty) list containing the first limit values.
     *
     * @param limit last index (exclusive) of values to include
     * @return a possibly empty list containing the values
     */
    @Nonnull
    IList<T> prefix(int limit);

    /**
     * Return the (possibly empty) list containing the values starting at offset (inclusive)
     * and including all remaining items.
     *
     * @param offset first index (inclusive) of values to include
     * @return a possibly empty list containing the values
     */
    @Nonnull
    IList<T> suffix(int offset);

    /**
     * Return the (possibly empty) list containing the values starting at offset (inclusive)
     * and including all remaining items up to but excluding the value at index limit.
     *
     * @param offset first index (inclusive) of values to include
     * @param limit  last index (exclusive) of values to include
     * @return a possibly empty list containing the values
     */
    @Nonnull
    IList<T> middle(int offset,
                    int limit);

    /**
     * Return the (possibly empty) list containing the values starting at offset (inclusive)
     * and including all remaining items up to but excluding the value at index limit.  Similar
     * to middle() but accepts a more permissive set of values.  Negative values are interpreted
     * relative to the end of the list and are inclusive when used as limit.  This (-5,-1) would
     * return the last 5 elements of the list.  Bounds checking is removed and values past the
     * end of the list are simply set to the end of the list.  So (0,10000) for a list with
     * five elements would be equivalent to (0,5).
     *
     * @param offset first index (inclusive) of values to include
     * @param limit  last index (exclusive) of values to include
     * @return a possibly empty list containing the values
     */
    @Nonnull
    IList<T> slice(int offset,
                   int limit);
}
