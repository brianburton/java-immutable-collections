///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.GenericCollector;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collector;

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
    public static <T> Collector<T, ?, JImmutableList<T>> toList()
    {
        return JImmutables.<T>list().listCollector();
    }

    /**
     * Collects values into a JImmutableRandomAccessList.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, JImmutableRandomAccessList<T>> toRalist()
    {
        return JImmutables.<T>ralist().ralistCollector();
    }

    /**
     * Collects values into a hashed JImmutableSet.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, JImmutableSet<T>> toSet()
    {
        return JImmutables.<T>set().setCollector();
    }

    /**
     * Collects values into a sorted JImmutableSet using natural sort order of elements.
     */
    @Deprecated
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, JImmutableSet<T>> toSortedSet()
    {
        return JImmutables.<T>sortedSet().setCollector();
    }

    /**
     * Collects values into a sorted JImmutableSet using specified Comparator.
     */
    @Deprecated
    @Nonnull
    public static <T> Collector<T, ?, JImmutableSet<T>> toSortedSet(@Nonnull Comparator<T> comparator)
    {
        return JImmutables.sortedSet(comparator).setCollector();
    }

    /**
     * Collects values into a hashed JImmutableListMap using the specified classifier function
     * to generate keys from the encountered elements.
     */
    @Nonnull
    public static <T, K> Collector<T, ?, JImmutableListMap<K, T>> groupingBy(@Nonnull Function<? super T, ? extends K> classifier)
    {
        return GenericCollector.unordered(JImmutables.listMap(),
                                          JImmutables.listMap(),
                                          (a, v) -> a.insert(classifier.apply(v), v),
                                          (a, b) -> a.insertAll(b.entries()));
    }
}
