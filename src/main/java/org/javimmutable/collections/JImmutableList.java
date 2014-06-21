///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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
import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Interface for containers that store items in list form with individual items available
 * for get() and assign() using their indexes.  Items inserted into the list are always
 * added at either the front or the end of the list and indexes of items are always in
 * the range 0 through size() - 1.
 *
 * @param <T>
 */
@Immutable
public interface JImmutableList<T>
        extends Insertable<T>,
                Indexed<T>,
                Cursorable<T>,
                Iterable<T>
{
    /**
     * @return number of values in the list
     */
    int size();

    /**
     * Retrieves the value at the specified index (which must be within the bounds
     * of the list).
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    T get(int index);

    /**
     * Replaces the value at the specified index (which must be within current
     * bounds of the list) with the new value.
     *
     * @param index
     * @param value
     * @return
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    JImmutableList<T> assign(int index,
                             T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     *
     * @param value
     * @return
     */
    @Nonnull
    JImmutableList<T> insert(T value);

    /**
     * Adds a value to the front of the list.  May be invoked on an empty list.
     * Synonym for insert()
     *
     * @param value
     * @return
     */
    JImmutableList<T> insertFirst(T value);

    /**
     * Adds a value to the end of the list.  May be invoked on an empty list.
     * Synonym for insert().
     *
     * @param value
     * @return
     */
    JImmutableList<T> insertLast(T value);

    /**
     * Removes the first value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new PersistentList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    JImmutableList<T> deleteFirst();

    /**
     * Removes the last value from the list and reduces size by 1.  size() must be greater than zero
     *
     * @return new PersistentList without last value
     * @throws IndexOutOfBoundsException if list is already empty
     */
    JImmutableList<T> deleteLast();

    /**
     * @return true only if list contains no values
     */
    boolean isEmpty();

    /**
     * @return an equivalent collection with no values
     */
    JImmutableList<T> deleteAll();

    /**
     * Returns an unmodifiable List implementation backed by this list.
     *
     * @return
     */
    public List<T> getList();
}
