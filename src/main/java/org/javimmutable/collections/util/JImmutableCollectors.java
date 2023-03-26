///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collector;
import javax.annotation.Nonnull;
import org.javimmutable.collections.GenericCollector;
import org.javimmutable.collections.IArray;
import org.javimmutable.collections.ICollectors;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.ISet;

/**
 * Utility class providing static methods for collecting various immutable collections using streams.
 */
public final class JImmutableCollectors
{
    private JImmutableCollectors()
    {
    }

    /**
     * Collects values into a JImmutableList.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, IList<T>> toList()
    {
        return ICollectors.toList();
    }

    /**
     * Collects values into a JImmutableArray.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, IArray<T>> toArray()
    {
        return ICollectors.toArray();
    }

    /**
     * Collects values into a hashed JImmutableSet.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, ISet<T>> toSet()
    {
        return ICollectors.toSet();
    }

    /**
     * Collects values into a sorted JImmutableSet using natural sort order of elements.
     */
    @Deprecated
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, ISet<T>> toSortedSet()
    {
        return ICollectors.toSortedSet();
    }

    /**
     * Collects values into a sorted JImmutableSet using specified Comparator.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, ISet<T>> toSortedSet(@Nonnull Comparator<T> comparator)
    {
        return ICollectors.toSortedSet(comparator);
    }

    /**
     * Collects values into a hashed JImmutableListMap using the specified classifier function
     * to generate keys from the encountered elements.
     */
    @Nonnull
    public static <T, K> Collector<T, ?, IListMap<K, T>> groupingBy(@Nonnull Function<? super T, ? extends K> classifier)
    {
        return GenericCollector.ordered(IListMap.listMap(),
                                        IListMap.listMap(),
                                        IListMap::isEmpty,
                                        (a, v) -> a.insert(classifier.apply(v), v),
                                        (a, b) -> a.insertAll(b.entries()));
    }
}
