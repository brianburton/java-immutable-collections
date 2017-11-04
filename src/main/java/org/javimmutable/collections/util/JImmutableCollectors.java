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

import org.javimmutable.collections.Func2;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

/**
 * Utility class providing static methods for collecting various immutable collections using streams.
 */
public class JImmutableCollectors
{
    static final Set<Characteristics> ORDERED = JImmutables.set(Characteristics.CONCURRENT).getSet();
    static final Set<Characteristics> UNORDERED = JImmutables.set(Characteristics.UNORDERED, Characteristics.CONCURRENT).getSet();

    private JImmutableCollectors()
    {
    }

    /**
     * Collects values into a JImmutableList.
     */
    @Nonnull
    public static <T> Collector<T, ?, JImmutableList<T>> toList()
    {
        return new CollectorImpl<>(ORDERED,
                                   JImmutables.list(),
                                   (a, v) -> a.insert(v),
                                   (a, b) -> a.insertAll(b));
    }

    /**
     * Collects values into a JImmutableRandomAccessList.
     */
    @Nonnull
    public static <T> Collector<T, ?, JImmutableRandomAccessList<T>> toRalist()
    {
        return new CollectorImpl<>(ORDERED,
                                   JImmutables.ralist(),
                                   (a, v) -> a.insert(v),
                                   (a, b) -> a.insertAll(b));
    }

    /**
     * Collects values into a hashed JImmutableSet.
     */
    @Nonnull
    public static <T> Collector<T, ?, JImmutableSet<T>> toSet()
    {
        return new CollectorImpl<>(UNORDERED,
                                   JImmutables.set(),
                                   (a, v) -> a.insert(v),
                                   (a, b) -> a.insertAll(b));
    }

    /**
     * Collects values into a sorted JImmutableSet using natural sort order of elements.
     */
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, JImmutableSet<T>> toSortedSet()
    {
        return new CollectorImpl<>(UNORDERED,
                                   JImmutables.<T>sortedSet(),
                                   (a, v) -> a.insert(v),
                                   (a, b) -> a.insertAll(b));
    }

    /**
     * Collects values into a sorted JImmutableSet using specified Comparator.
     */
    @Nonnull
    public static <T> Collector<T, ?, JImmutableSet<T>> toSortedSet(@Nonnull Comparator<T> comparator)
    {
        return new CollectorImpl<>(UNORDERED,
                                   JImmutables.sortedSet(comparator),
                                   (a, v) -> a.insert(v),
                                   (a, b) -> a.insertAll(b));
    }

    /**
     * Collects values into a hashed JImmutableListMap using the specified classifier function
     * to generate keys from the encountered elements.
     */
    @Nonnull
    public static <T, K> Collector<T, ?, JImmutableListMap<K, T>> groupingBy(@Nonnull Function<? super T, ? extends K> classifier)
    {
        return new CollectorImpl<>(ORDERED,
                                   JImmutables.listMap(),
                                   (a, v) -> a.insert(classifier.apply(v), v),
                                   (a, b) -> combine(a, b));
    }

    private static <K, T> JImmutableListMap<K, T> combine(@Nonnull JImmutableListMap<K, T> a,
                                                          @Nonnull JImmutableListMap<K, T> b)
    {
        for (JImmutableMap.Entry<K, JImmutableList<T>> e : b) {
            a = a.assign(e.getKey(), a.getList(e.getKey()).insertAll(e.getValue()));
        }
        return a;
    }

    private static class Accumulator<T, C>
    {
        private Func2<C, T, C> adder;
        private Func2<C, C, C> combiner;
        private C list;

        private Accumulator(@Nonnull Func2<C, T, C> adder,
                            @Nonnull Func2<C, C, C> combiner,
                            @Nonnull C list)
        {
            this.adder = adder;
            this.combiner = combiner;
            this.list = list;
        }

        private synchronized void add(T value)
        {
            list = adder.apply(list, value);
        }

        private synchronized Accumulator<T, C> combine(Accumulator<T, C> other)
        {
            list = combiner.apply(list, other.list);
            return this;
        }
    }

    @Immutable
    private static class CollectorImpl<T, C>
        implements Collector<T, Accumulator<T, C>, C>
    {
        private final Set<Characteristics> characteristics;
        private final C empty;
        private final Func2<C, T, C> adder;
        private final Func2<C, C, C> combiner;

        private CollectorImpl(@Nonnull Set<Characteristics> characteristics,
                              @Nonnull C empty,
                              @Nonnull Func2<C, T, C> adder,
                              @Nonnull Func2<C, C, C> combiner)
        {
            this.characteristics = characteristics;
            this.empty = empty;
            this.adder = adder;
            this.combiner = combiner;
        }

        @Override
        public Supplier<Accumulator<T, C>> supplier()
        {
            return () -> new Accumulator<>(adder, combiner, empty);
        }

        @Override
        public BiConsumer<Accumulator<T, C>, T> accumulator()
        {
            return (a, v) -> a.add(v);
        }

        @Override
        public BinaryOperator<Accumulator<T, C>> combiner()
        {
            return (left, right) -> left.combine(right);
        }

        @Override
        public Function<Accumulator<T, C>, C> finisher()
        {
            return a -> a.list;
        }

        @Override
        public Set<Characteristics> characteristics()
        {
            return characteristics;
        }
    }
}
