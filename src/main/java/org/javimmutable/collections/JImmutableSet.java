///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

import java.util.Set;

public interface JImmutableSet<T>
        extends Insertable<T>,
                Cursorable<T>,
                Iterable<T>
{
    /**
     * Adds the single value to the Set.
     *
     * @param value
     * @return instance of set containing the value
     */
    JImmutableSet<T> insert(T value);

    /**
     * Determines if the Set contains the specified value.
     *
     * @param value
     * @return true if the Set contains the value
     */
    boolean contains(T value);

    /**
     * Determines if the Set contains all values in the specified collection.
     *
     * @param value
     * @return true if the Set contains the values
     */
    boolean containsAll(Cursorable<T> value);

    /**
     * Determines if the Set contains any values in the specified collection.
     *
     * @param value
     * @return true if the Set contains a value
     */
    boolean containsAny(Cursorable<T> value);

    /**
     * Removes the value from the Set.  Has no effect if the value is not in the Set.
     *
     * @param value
     * @return instance of set without the value
     */
    JImmutableSet<T> delete(T value);

    /**
     * Removes all values of other from the Set.  Has no effect if none of the values are in the Set
     *
     * @param other
     * @return instance of set without the values
     */
    JImmutableSet<T> deleteAll(Cursorable<T> other);

    /**
     * Adds all values from other to the Set.
     *
     * @param other source of values to add
     * @return instance of set containing the values
     */
    JImmutableSet<T> union(Cursorable<T> other);

    /**
     * Removes all values from the Set that are not contained in the other Set.
     *
     * @param other
     * @return instance of set with unmatched values removed
     */
    JImmutableSet<T> intersection(JImmutableSet<T> other);

    /**
     * Removes all values from the Set that are not contained in the other collection.
     *
     * @param other
     * @return instance of set with unmatched values removed
     */
    JImmutableSet<T> intersection(Cursorable<T> other);

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
    JImmutableSet<T> deleteAll();

    /**
     * @return an unmodifiable Set implementation backed by this set.
     */
    public Set<T> getSet();
}
