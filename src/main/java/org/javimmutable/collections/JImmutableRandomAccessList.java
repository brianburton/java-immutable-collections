///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

/**
 * Extension of PersistentList that allows insertion and deletion at arbitrary
 * indexes within the list.
 *
 * @param <T>
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
     * @param index
     * @param value
     * @return
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Nonnull
    JImmutableRandomAccessList<T> assign(int index,
                                         @Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     *
     * @param value
     * @return
     */
    @Nonnull
    JImmutableRandomAccessList<T> insert(@Nullable T value);

    /**
     * Insert value at index (which must be within 0 to size).
     * Shifts all values at and after index one position to the right and adds 1
     * to size of the list.
     *
     * @param index
     * @param value
     * @return
     */
    @Nonnull
    JImmutableRandomAccessList<T> insert(int index,
                                         @Nullable T value);

    /**
     * Adds a value to the front of the list.  May be invoked on an empty list.
     *
     * @param value
     * @return
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertFirst(@Nullable T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     * Synonym for insert().
     *
     * @param value
     * @return
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertLast(@Nullable T value);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(@Nonnull Collection<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAllLast()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Inserts all elements at index (which must be within 0 to size) in the same
     * order they appear in the Iterable.
     * Shifts all values at and after index x positions to the right and adds x
     * to size of the list, where x is the number of elements being inserted.
     *
     * @param index
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Cursorable<? extends T> values);

    /**
     * Inserts all elements at index (which must be within 0 to size) in the same
     * order they appear in the Iterable.
     * Shifts all values at and after index x positions to the right and adds x
     * to size of the list, where x is the number of elements being inserted.
     *
     * @param index
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Collection<? extends T> values);

    /**
     * Inserts all elements at index (which must be within 0 to size) in the same
     * order they appear in the Iterable.
     * Shifts all values at and after index x positions to the right and adds x
     * to size of the list, where x is the number of elements being inserted.
     *
     * @param index
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Cursor<? extends T> values);

    /**
     * Inserts all elements at index (which must be within 0 to size) in the same
     * order they appear in the Iterable.
     * Shifts all values at and after index x positions to the right and adds x
     * to size of the list, where x is the number of elements being inserted.
     *
     * @param index
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAll(int index,
                                            @Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Collection<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the beginning of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllFirst(@Nonnull Iterator<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Collection<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Cursor<? extends T> values);

    /**
     * Adds the values to the end of the list in the same order they appear in the Iterable.  May be invoked on an empty list.
     * Synonym for insertAll()
     *
     * @param values
     * @return instance of list containing the collection
     */
    @Nonnull
    JImmutableRandomAccessList<T> insertAllLast(@Nonnull Iterator<? extends T> values);

    /**
     * Removes the first value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new PersistentList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    JImmutableRandomAccessList<T> deleteFirst();

    /**
     * Removes the last value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new PersistentList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    @Nonnull
    JImmutableRandomAccessList<T> deleteLast();

    /**
     * Delete value at index (which must be within the current bounds of the list).
     * Shifts all values at and after index one position to the left and subtracts 1
     * from size of the list.
     *
     * @param index
     * @return
     */
    @Nonnull
    JImmutableRandomAccessList<T> delete(int index);

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    JImmutableRandomAccessList<T> deleteAll();
}
