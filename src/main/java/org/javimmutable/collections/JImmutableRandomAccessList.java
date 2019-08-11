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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * Extension of JImmutableList that allows insertion and deletion at arbitrary
 * indexes within the list.
 */
@Immutable
public interface JImmutableRandomAccessList<T>
    extends JImmutableList<T>
{
    interface Builder<T>
        extends MutableBuilder<T, JImmutableRandomAccessList<T>>
    {
    }

    /**
     * Replaces the value at the specified index (which must be within current
     * bounds of the list) with the new value.
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> assign(int index,
                                         @Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insert(@Nullable T value);

    @Nonnull
    @Override
    default JImmutableRandomAccessList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        return insertAll(values);
    }

    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insert(int index,
                                         @Nullable T value);

    /**
     * Adds a value to the front of the list.  May be invoked on an empty list.
     * Synonym for insert()
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertFirst(@Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     * Synonym for insert().
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertLast(@Nullable T value);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAll(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAll(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAll(@Nonnull Iterator<? extends T> values);

    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Iterable<? extends T> values);

    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Cursor<? extends T> values);

    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Iterable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @return instance of list containing the collection
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Iterator<? extends T> values);

    @Nonnull
    @Override
    JImmutableRandomAccessList<T> delete(int index);

    /**
     * Removes the first value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new JImmutableList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> deleteFirst();

    /**
     * Removes the last value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new JImmutableList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> deleteLast();

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    @Override
    JImmutableRandomAccessList<T> deleteAll();

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
    default JImmutableRandomAccessList<T> select(@Nonnull Predicate<T> predicate)
    {
        JImmutableRandomAccessList<T> answer = deleteAll();
        for (T value : this) {
            if (predicate.test(value)) {
                answer = answer.insert(value);
            }
        }
        return answer.size() == size() ? this : answer;
    }

    /**
     * Returns a list of the same type as this containing all those elements for which
     * predicate returns false.  Implementation optimized assuming predicate will
     * return false more often than true.
     *
     * @param predicate decides whether to include an element
     * @return list of same type as this containing only those elements for which predicate returns false
     */
    @Nonnull
    @Override
    default JImmutableRandomAccessList<T> reject(@Nonnull Predicate<T> predicate)
    {
        JImmutableRandomAccessList<T> answer = this;
        int index = 0;
        for (T value : this) {
            assert value == answer.get(index);
            if (predicate.test(value)) {
                answer = answer.delete(index);
            } else {
                index += 1;
            }
        }
        return answer.size() == size() ? this : answer;
    }

    /**
     * Returns a Collector that creates a list of the same type as this containing all
     * of the collected values inserted after whatever starting values this already contained.
     */
    @Nonnull
    default Collector<T, ?, JImmutableRandomAccessList<T>> ralistCollector()
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
    <A> JImmutableRandomAccessList<A> transform(@Nonnull Func1<T, A> transform);

    /**
     * Apply the transform function to all elements in iterator order and add the contents of
     * non-empty Holders to a new collection of this type.
     *
     * @param transform transformation applied to each element
     * @return the collection after all elements have been processed
     */
    @Override
    <A> JImmutableRandomAccessList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform);
}
