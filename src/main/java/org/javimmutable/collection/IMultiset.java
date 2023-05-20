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
import java.util.Set;
import java.util.stream.Collector;

/**
 * Keeps a set of distinct values, as well as the count corresponding to each value. Can iterate
 * through the multiset with the correct number of occurrences per value.
 */
@Immutable
public interface IMultiset<T>
    extends ISet<T>
{
    /**
     * Adds one occurrence of value to the multiset.
     *
     * @param value value to add
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    IMultiset<T> insert(@Nonnull T value);

    /**
     * Adds count number of occurrences of value to the multiset.
     * Count must be greater than or equal to zero.
     *
     * @param value value to add
     * @param count number of occurrences added
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> insert(@Nonnull T value,
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
     * occurrences of each value) in other. If other is empty, returns true.
     * Synonymous to calling contains() on each element of other.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all values
     */
    @Override
    boolean containsAll(@Nonnull Iterable<? extends T> other);

    /**
     * Determines if the multiset contains every value (but not necessarily the same number of
     * occurrences of each value) in other. If other is empty, returns true.
     * Synonymous to calling contains() on each element of other.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all values
     */
    @Override
    boolean containsAll(@Nonnull Iterator<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other. If other is empty, returns true.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Iterable<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other. If other is empty, returns true.
     * Slow operation.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull Iterator<? extends T> other);

    /**
     * Determines if the multiset contains every occurrence of all the values
     * in other. If other is empty, returns true.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains all occurrences
     */
    boolean containsAllOccurrences(@Nonnull IMultiset<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     * Returns false if other is empty.
     *
     * @param other contains values to be checked for
     * @return true if this multiset contains a value
     */
    @Override
    boolean containsAny(@Nonnull Iterable<? extends T> other);

    /**
     * Determines if the multiset and other have at least one value in common.
     * Returns false if other is empty.
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
    IMultiset<T> delete(@Nonnull T value);

    /**
     * Removes one occurrence of value from the multiset.
     *
     * @param value value to remove
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> deleteOccurrence(@Nonnull T value);

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
    IMultiset<T> deleteOccurrence(@Nonnull T value,
                                  int count);

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    @Override
    IMultiset<T> deleteAll();

    /**
     * Removes all occurrences of each value in other from the multiset.
     * Synonymous to calling delete() on each element of other.
     *
     * @param other contains values to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    @Override
    IMultiset<T> deleteAll(@Nonnull Iterable<? extends T> other);

    /**
     * Removes all occurrences of each value in other from the multiset.
     * Synonymous to calling delete() on each element of other.
     *
     * @param other contains values to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    @Override
    IMultiset<T> deleteAll(@Nonnull Iterator<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> deleteAllOccurrences(@Nonnull Iterable<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> deleteAllOccurrences(@Nonnull Iterator<? extends T> other);

    /**
     * Removes each occurrence in other from the multiset.
     * Synonymous to calling deleteOccurrence() on each element of other.
     *
     * @param other contains occurrences to delete
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> deleteAllOccurrences(@Nonnull IMultiset<? extends T> other);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    @Override
    IMultiset<T> insertAll(@Nonnull Iterable<? extends T> values);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    @Override
    IMultiset<T> insertAll(@Nonnull Iterator<? extends T> values);

    /**
     * Adds each occurrence in values to the multiset.
     * Synonymous to calling insert on each element of values.
     *
     * @param values contains occurrences to be added
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> insertAll(@Nonnull IMultiset<? extends T> values);

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
    IMultiset<T> union(@Nonnull Iterable<? extends T> other);

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
    IMultiset<T> union(@Nonnull Iterator<? extends T> other);

    /**
     * Combines all occurrences from other and the multiset. If only the multiset or
     * other contains a value, that value's count is preserved in the new multiset.
     * If both contain a value, the greater count is used.
     *
     * @param other contains values to union with this multiset
     * @return new multiset reflecting the changes
     */
    @Nonnull
    IMultiset<T> union(@Nonnull IMultiset<? extends T> other);

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
    IMultiset<T> intersection(@Nonnull Iterable<? extends T> other);

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
    IMultiset<T> intersection(@Nonnull Iterator<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Nonnull
    IMultiset<T> intersection(@Nonnull IMultiset<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    IMultiset<T> intersection(@Nonnull ISet<? extends T> other);

    /**
     * Creates a new multiset with the occurrences that are in both other and this.
     * If neither the multiset nor other contains a value, it is removed. If both
     * contain a value, the lesser count is used.
     *
     * @param other contains values to intersect with this multiset
     * @return new multiset reflecting the change
     */
    @Override
    @Nonnull
    IMultiset<T> intersection(@Nonnull Set<? extends T> other);

    /**
     * Stream iterates through each Entry, that contains a unique value and the count of occurrences.
     *
     * @return IStreamable of {@link IMap}.Entries
     */
    @Nonnull
    IStreamable<IMapEntry<T, Integer>> entries();

    /**
     * IStreamable that iterates through each occurrence in the multiset the correct number of times.
     *
     * @return IStreamable that behaves as if multiset was a list
     */
    @Nonnull
    IStreamable<T> occurrences();

    /**
     * Returns the number of occurrences associated with the specified value. If the value is not
     * contained in the multiset, 0 is returned.
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
    IMultiset<T> setCount(@Nonnull T value,
                          int count);

    /**
     * @return total number of unique values in the multiset. Same as the number of items in iterator() and entries().iterator().
     */
    @Override
    int size();

    /**
     * @return the total number of occurrences in the multiset. Same as the number of items in occurrences.iterator().
     */
    int occurrenceCount();

    /**
     * Returns a Collector that creates a multiset of the same type as this containing all
     * of the collected values inserted over whatever starting values this already contained.
     */
    @Nonnull
    default Collector<T, ?, IMultiset<T>> multisetCollector()
    {
        return GenericCollector.unordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }
}
