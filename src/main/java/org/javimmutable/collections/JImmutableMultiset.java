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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Keeps a set of distinct values, as well as the count corresponding to each value. Can return
 * a list with the correct number of elements per value.
 *
 * @param <T>
 */
@Immutable
public interface JImmutableMultiset<T>
        extends JImmutableSet<T>
{

    /**
     * Adds one occurance of value to the multiset.
     *
     * @param value
     * @return
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> insert(@Nonnull T value);

    /**
     * Adds count number of occurances of value to the multiset.
     *
     * @param value
     * @param count
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> insert(@Nonnull T value,
                                 int count);

    /**
     * Determines if the multiset contains the specified value
     *
     * @param value
     * @return
     */
    @Override
    boolean contains(@Nonnull T value);

    /**
     * Determines if the multiset contains at least count occurances of value
     *
     * @param value
     * @param count
     * @return
     */
    boolean contains(@Nonnull T value,
                     int count);

    /**
     * Determines if the multiset contains at least as many occurances of
     * each value as other.
     * Slow operation.
     *
     * @param other
     * @return
     */
    boolean containsAllOccurances(@Nonnull Cursorable<? extends T> other);

    boolean containsAllOccurances(@Nonnull Collection<? extends T> other);

    boolean containsAllOccurances(@Nonnull Cursor<? extends T> other);

    boolean containsAllOccurances(@Nonnull Iterator<? extends T> other);

    /**
     * Determines if the multiset contains every value in other. If containsAll(other)
     * is true, this must be true as well.
     *
     * @param other
     * @return
     */
    @Override
    boolean containsAll(@Nonnull Cursorable<? extends T> other);

    @Override
    boolean containsAll(@Nonnull Collection<? extends T> other);

    @Override
    boolean containsAll(@Nonnull Cursor<? extends T> other);

    @Override
    boolean containsAll(@Nonnull Iterator<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     *
     * @param other
     * @return
     */
    @Override
    boolean containsAny(@Nonnull Cursorable<? extends T> other);

    @Override
    boolean containsAny(@Nonnull Collection<? extends T> other);

    @Override
    boolean containsAny(@Nonnull Cursor<? extends T> other);

    @Override
    boolean containsAny(@Nonnull Iterator<? extends T> other);

    /**
     * Removes one occurance of value from the multiset.
     *
     * @param value
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> deleteOccurance(@Nonnull T value);

    /**
     * Removes count number of occurances of value from the multiset.
     *
     * @param value
     * @param count
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> deleteOccurance(@Nonnull T value,
                                          int count);

    /**
     * Removes every occurance of value from the multiset.
     *
     * @param value
     * @return
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> delete(@Nonnull T value);

    /**
     * Removes each element in other from the multiset.
     * Synonymous to calling delete() on each element of values.
     *
     * @param other
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> deleteAllOccurances(@Nonnull Cursorable<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> deleteAllOccurances(@Nonnull Collection<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> deleteAllOccurances(@Nonnull Cursor<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> deleteAllOccurances(@Nonnull Iterator<? extends T> other);

    /**
     * For each element in other, removes all occurances of that value from the multiset.
     * Synonymous to calling deleteValue() on each element of other.
     *
     * @param other
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Cursorable<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Collection<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Cursor<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Iterator<? extends T> other);

    /**
     * Adds each element in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Cursorable<? extends T> values);

    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Collection<? extends T> values);

    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Cursor<? extends T> values);

    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Iterator<? extends T> values);


    /**
     * Combines all occurances in other and the multiset. If only the multiset or
     * other contains a value, that value's count is used. If both contain a value,
     * the greater count is used.
     * Slow operation.
     *
     * @param other
     * @return
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Cursorable<? extends T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Collection<? extends T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Cursor<? extends T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Iterator<? extends T> other);


    /**
     * Removes all occurances from the multiset that are not contained in other.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other
     * @return
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Cursorable<? extends T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Collection<? extends T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Cursor<? extends T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Iterator<? extends T> other);

    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull JImmutableMultiset<T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull JImmutableSet<T> other);

    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Set<? extends T> other);


    /**
     * Cursor iterates through each occurance in the multiset the correct number of times.
     * Synonymous to a cursor from getList()
     *
     * @return
     */
    @Nonnull
    Cursor<T> occuranceCursor();

    /**
     * Cursor iterates through each uniqe value in the multiset once.
     * Synonymous to a cursor from getSet()
     *
     * @return
     */
    @Nonnull
    Cursor<T> cursor();

    /**
     * Cursor iterates through each Entry, that contains a unique value and the count of occurances.
     * Synonymous to a cursor from getEntrySet()
     *
     * @return
     */
    @Nonnull
    Cursor<JImmutableMap.Entry<T, Integer>> entryCursor();


    /**
     * Returns a JImmutableSet of the unique values in the multiset
     *
     * @return
     */
    @Override
    @Nonnull
    Set<T> getSet();


    /**
     * Returns the number of occurances associated with the specified value.
     *
     * @param value
     * @return
     */
    int count(@Nonnull T value);

    /**
     * Manually sets the number of occurances associated with the specified value.
     * Count must be greater than or equal to zero.
     *
     * @param value
     * @param count
     * @return
     */
    @Nonnull
    JImmutableMultiset<T> setCount(@Nonnull T value,
                                   int count);

    /**
     * Determines if the multiset contains any values.
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Determines the total number of occurances in the multiset.
     *
     * @return
     */
    int size();

    /**
     * Determines the total number of values in the multiset.
     *
     * @return
     */
    int valueCount();
}
