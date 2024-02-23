///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

import org.javimmutable.collections.hash.HashMultiset;
import org.javimmutable.collections.inorder.OrderedMultiset;
import org.javimmutable.collections.tree.TreeMultiset;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public final class IMultisets
{
    private IMultisets()
    {
    }

    /**
     * Constructs an unsorted multiset.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value assigned to the multiset.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> IMultiset<T> hashed()
    {
        return HashMultiset.of();
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IMultiset<T> hashed(T... source)
    {
        return HashMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> IMultiset<T> hashed(@Nonnull Iterable<? extends T> source)
    {
        return HashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> IMultiset<T> hashed(@Nonnull Iterator<? extends T> source)
    {
        return HashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty set that sorts values in their natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> IMultiset<T> sorted()
    {
        return TreeMultiset.of();
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    @SafeVarargs
    public static <T extends Comparable<T>> IMultiset<T> sorted(T... source)
    {
        return TreeMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> IMultiset<T> sorted(@Nonnull Iterable<? extends T> source)
    {
        return TreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> IMultiset<T> sorted(@Nonnull Iterator<? extends T> source)
    {
        return TreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty multiset that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator)
    {
        return TreeMultiset.of(comparator);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator,
                                          T... source)
    {
        return TreeMultiset.of(comparator).insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator,
                                          @Nonnull Iterable<? extends T> source)
    {
        return TreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator,
                                          @Nonnull Iterator<? extends T> source)
    {
        return TreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    public static <T> IMultiset<T> ordered()
    {
        return OrderedMultiset.of();
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IMultiset<T> ordered(T... source)
    {
        return OrderedMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    public static <T> IMultiset<T> ordered(@Nonnull Iterable<? extends T> source)
    {
        return OrderedMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    public static <T> IMultiset<T> ordered(@Nonnull Iterator<? extends T> source)
    {
        return OrderedMultiset.<T>of().insertAll(source);
    }
}
