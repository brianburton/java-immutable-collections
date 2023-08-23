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

import org.javimmutable.collections.hash.HashSet;
import org.javimmutable.collections.inorder.OrderedSet;
import org.javimmutable.collections.tree.TreeSet;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Iterator;

public final class ISets
{
    private ISets()
    {
    }

    /**
     * Constructs an unsorted set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> hashed()
    {
        return HashSet.of();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    @SafeVarargs
    public static <T> ISet<T> hashed(T... source)
    {
        return HashSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> hashed(@Nonnull Iterable<? extends T> source)
    {
        return HashSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> hashed(@Nonnull Iterator<? extends T> source)
    {
        return HashSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an empty set that sorts values in their natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISet<T> sorted()
    {
        return TreeSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    @SafeVarargs
    public static <T extends Comparable<T>> ISet<T> sorted(T... source)
    {
        return TreeSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISet<T> sorted(@Nonnull Iterable<? extends T> source)
    {
        return TreeSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISet<T> sorted(@Nonnull Iterator<? extends T> source)
    {
        return TreeSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an empty set that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator)
    {
        return TreeSet.of(comparator);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    @SafeVarargs
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator,
                                     T... source)
    {
        return TreeSet.builder(comparator).add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator,
                                     @Nonnull Iterable<? extends T> source)
    {
        return TreeSet.builder(comparator).add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator,
                                     @Nonnull Iterator<? extends T> source)
    {
        return TreeSet.builder(comparator).add(source).build();
    }

    /**
     * Constructs an empty set that sorts values based on the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> ordered()
    {
        return OrderedSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    @SafeVarargs
    public static <T> ISet<T> ordered(T... source)
    {
        return OrderedSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> ordered(@Nonnull Iterable<? extends T> source)
    {
        return OrderedSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> ordered(@Nonnull Iterator<? extends T> source)
    {
        return OrderedSet.<T>builder().add(source).build();
    }

    /**
     * Constructs Builder object to produce unsorted sets.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISetBuilder<T> hashedBuilder()
    {
        return HashSet.builder();
    }

    /**
     * Constructs a Builder object to produce sets that sort values in their natural
     * sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISetBuilder<T> sortedBuilder()
    {
        return TreeSet.builder();
    }

    /**
     * Constructs a Builder object to produce sets that sort values using specified Comparator.
     */
    @Nonnull
    public static <T> ISetBuilder<T> sortedBuilder(@Nonnull Comparator<T> comparator)
    {
        return TreeSet.builder(comparator);
    }

    /**
     * Constructs Builder object to produce sets that sort values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISetBuilder<T> orderedBuilder()
    {
        return OrderedSet.builder();
    }
}
