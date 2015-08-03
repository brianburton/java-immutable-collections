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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Keeps a set of distinct values, as well as the count corresponding to each value. Can return
 * a list with the correct number of occurrences per value.
 *
 * @param <T>
 */
@Immutable
public interface JImmutableMultiset<T>
        extends JImmutableSet<T>
{

    /**
     * Adds one occurrence of value to the multiset.
     *
     * @param value value to add
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> insert(@Nonnull T value);

    /**
     * Adds count number of occurrences of value to the multiset.
     * Count must be greater than or equal to zero.
     *
     * @param value value to add
     * @param count number of occurrences added
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> insert(@Nonnull T value,
                                 int count);

    /**
     * Determines if the multiset contains at least one occurrence of the specified value
     *
     * @param value value to check for
     * @return true if this multiset contains the value
     */
    @Override
    boolean contains(@Nullable T value);

    /**
     * Determines if the multiset contains at least count occurrences of value.
     *
     * @param value value to check for
     * @param count number of occurrences checked for
     * @return true if thsi multiset contains at least count occurrences of value
     */
    boolean containsAtLeast(@Nullable T value,
                            int count);

    /**
     * Determines if the multiset contains every value (but not necessarily the same number of
     * occurrences of each value) in other.
     * Synonymous to calling contains() on each element of other.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all values
     */
    @Override
    boolean containsAll(@Nonnull Cursorable<? extends T> other);

    /**
     * Determines if the multiset contains every value (but not necessarily the same number of
     * occurrences of each value) in other.
     * Synonymous to calling contains() on each element of other.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all values
     */
    @Override
    boolean containsAll(@Nonnull Collection<? extends T> other);

    /**
     * Determines if the multiset contains every value (but not necessarily the same number of
     * occurrences of each value) in other.
     * Synonymous to calling contains() on each element of other.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all values
     */
    @Override
    boolean containsAll(@Nonnull Cursor<? extends T> other);

    /**
     * Determines if the multiset contains every value (but not necessarily the same number of
     * occurrences of each value) in other.
     * Synonymous to calling contains() on each element of other.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all values
     */
    @Override
    boolean containsAll(@Nonnull Iterator<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Cursorable<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Collection<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Cursor<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Iterator<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull JImmutableMultiset<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull JImmutableSet<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Set<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains a value
     */
    @Override
    boolean containsAny(@Nonnull Cursorable<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains a value
     */
    @Override
    boolean containsAny(@Nonnull Collection<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains a value
     */
    @Override
    boolean containsAny(@Nonnull Cursor<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains a value
     */
    @Override
    boolean containsAny(@Nonnull Iterator<? extends T> other);

    /**
     * Removes every occurrence of value from the multiset.
     *
     * @param value value to remove
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> delete(@Nonnull T value);

    /**
     * Removes one occurrence of value from the multiset.
     *
     * @param value value to remove
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteOccurrence(@Nonnull T value);


    /**
     * Removes count number of occurrences of value from the multiset. If there are fewer than
     * count occurrences in the multimap, then all the occurrences that do exist are deleted.
     * Count must be greater than or equal to zero.
     *
     * @param value value to remove
     * @param count number of occurrences to remove
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteOccurrence(@Nonnull T value,
                                           int count);

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    JImmutableMultiset<T> deleteAll();

    /**
     * Removes all occurrences of each value in other from the multiset.
     * Synonymous to calling delete() on each element of other.
     *
     * @param other contains values to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Cursorable<? extends T> other);

    /**
     * Removes all occurrences of each value in other from the multiset.
     * Synonymous to calling delete() on each element of other.
     *
     * @param other contains values to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Collection<? extends T> other);

    /**
     * Removes all occurrences of each value in other from the multiset.
     * Synonymous to calling delete() on each element of other.
     *
     * @param other contains values to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Cursor<? extends T> other);

    /**
     * Removes all occurrences of each value in other from the multiset.
     * Synonymous to calling delete() on each element of other.
     *
     * @param other contains values to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAll(@Nonnull Iterator<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursorable<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Collection<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursor<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Iterator<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> deleteAllOccurrences(@Nonnull JImmutableMultiset<? extends T> other);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Cursorable<? extends T> values);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Collection<? extends T> values);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Cursor<? extends T> values);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> insertAll(@Nonnull JImmutableMultiset<? extends T> values);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Cursorable<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Collection<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Cursor<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Iterator<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull JImmutableMultiset<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull JImmutableSet<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     * Slow operation.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Nonnull
    JImmutableMultiset<T> union(@Nonnull Set<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Cursorable<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Collection<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Cursor<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Iterator<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull JImmutableMultiset<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull JImmutableSet<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     * Slow operation.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    JImmutableMultiset<T> intersection(@Nonnull Set<? extends T> other);

    /**
     * Cursor iterates through each occurrence in the multiset the correct number of times.
     *
     * @return Cursor that behaves as if multiset was a list
     */
    @Nonnull
    Cursor<T> occurrenceCursor();

    /**
     * Cursor iterates through each unique value in the multiset once.
     *
     * @return Cursor that behaves as if multiset was a JImmutableSet
     */
    @Nonnull
    Cursor<T> cursor();

    /**
     * Cursor iterates through each Entry, that contains a unique value and the count of occurrences.
     *
     * @return Cursor of JImmutableMap.Entries
     */
    @Nonnull
    Cursor<JImmutableMap.Entry<T, Integer>> entryCursor();

    /**
     * Returns the number of occurrences associated with the specified value. If the value is not
     * contained in the mulitset, 0 is returned.
     *
     * @param value value to check for
     * @return number of occurrences
     */
    int count(@Nonnull T value);

    /**
     * Manually sets the number of occurrences associated with the specified value.
     * Count must be greater than or equal to zero. If count == 0, is synonymous to delete(value).
     * If value is not currently in the multiset, is equivalent to insert(value, count).
     *
     * @param value value to add
     * @param count new number of occurrences for value
     * @return new multiset reflecting the change
     */
    @Nonnull
    JImmutableMultiset<T> setCount(@Nonnull T value,
                                   int count);

    /**
     * @return true only if the multiset contains no values
     */
    boolean isEmpty();

    /**
     * @return total number of unique values in the multiset
     */
    int size();

    /**
     * @return the total number of occurrences in the multiset
     */
    int valueCount();
}