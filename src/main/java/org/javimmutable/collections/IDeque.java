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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * Interface for containers that store items in list form with individual items available
 * for get() and assign() using their indexes.  Items inserted into the list are always
 * added at either the front or the end of the list and indexes of items are always in
 * the range 0 through size() - 1.
 */
@Immutable
public interface IDeque<T>
    extends ICollection<T>,
            Indexed<T>,
            InvariantCheckable,
            Serializable
{
    /**
     * Retrieves the value at the specified index (which must be within the bounds
     * of the list).
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Override
    T get(int index);

    /**
     * Replaces the value at the specified index (which must be within current
     * bounds of the list) with the new value.
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Nonnull
    IDeque<T> assign(int index,
                     @Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     */
    @Nonnull
    @Override
    IDeque<T> insert(@Nullable T value);

    /**
     * Adds a value to the front of the list.  May be invoked on an empty list.
     * Synonym for insert()
     */
    @Nonnull
    IDeque<T> insertFirst(@Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     * Synonym for insert().
     */
    @Nonnull
    IDeque<T> insertLast(@Nullable T value);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IDeque<T> insertAll(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    IDeque<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    IDeque<T> insertAllFirst(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    IDeque<T> insertAllFirst(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    IDeque<T> insertAllLast(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    IDeque<T> insertAllLast(@Nonnull Iterator<? extends T> values);

    /**
     * Removes the first value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new {@link IList} without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    IDeque<T> deleteFirst();

    /**
     * Removes the last value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new {@link IList} without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    IDeque<T> deleteLast();

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    @Override
    IDeque<T> deleteAll();

    /**
     * Returns an unmodifiable List implementation backed by this list.
     */
    @Nonnull
    List<T> getList();

    /**
     * Returns a list containing the same elements as this list but with their order reversed
     * so that first in this list is last in returned list etc.
     */
    @Nonnull
    IDeque<T> reverse();

    /**
     * Returns a list of the same type as this containing only those elements for which
     * predicate returns true.  Implementations are optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns true
     */
    @Nonnull
    IDeque<T> select(@Nonnull Predicate<T> predicate);

    /**
     * Returns a list of the same type as this containing all those elements for which
     * predicate returns false.  Implementations can be optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns false
     */
    @Nonnull
    IDeque<T> reject(@Nonnull Predicate<T> predicate);

    /**
     * Returns a Collector that creates a list of the same type as this containing all
     * of the collected values inserted after whatever starting values this already contained.
     */
    @Nonnull
    default Collector<T, ?, IDeque<T>> dequeCollector()
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
    <A> IDeque<A> transform(@Nonnull Func1<T, A> transform);

    /**
     * Apply the transform function to all elements in iterator order and add the contents of
     * non-empty Holders to a new collection of this type.
     *
     * @param transform transformation applied to each element
     * @return the collection after all elements have been processed
     */
    <A> IDeque<A> transformSome(@Nonnull Func1<T, Maybe<A>> transform);

    /**
     * Returns a Holder containing a value if this list contains only a single value and that value is non-null.
     * Otherwise returns and empty Holder.  i.e. empty unless size() == 1 and get(0) returns a non-null value.
     *
     * @return Holder possibly containing the single non-null value in this list
     */
    default Maybe<T> single()
    {
        if (size() == 1) {
            T value = get(0);
            return Maybe.of(value);
        } else {
            return Maybe.empty();
        }
    }
}
