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
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class GenericCollector
{
    private static final Set<Collector.Characteristics> ORDERED = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT));
    private static final Set<Collector.Characteristics> UNORDERED = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.CONCURRENT));

    private GenericCollector()
    {
    }

    @Nonnull
    public static <T, C> Collector<T, ?, C> ordered(@Nonnull C initialValue,
                                                    @Nonnull C empty,
                                                    @Nonnull Predicate<C> isEmpty,
                                                    @Nonnull Func2<C, T, C> adder,
                                                    @Nonnull Func2<C, C, C> combiner)
    {
        return new CollectorImpl<>(ORDERED, initialValue, empty, isEmpty, adder, combiner);
    }

    @Nonnull
    public static <T, C> Collector<T, ?, C> unordered(@Nonnull C initialValue,
                                                      @Nonnull C empty,
                                                      @Nonnull Predicate<C> isEmpty,
                                                      @Nonnull Func2<C, T, C> adder,
                                                      @Nonnull Func2<C, C, C> combiner)
    {
        return new CollectorImpl<>(UNORDERED, initialValue, empty, isEmpty, adder, combiner);
    }


    @Immutable
    private static class CollectorImpl<T, C>
        implements Collector<T, Accumulator<T, C>, C>
    {
        private final Set<Characteristics> characteristics;
        private final C initialValues;
        private final C empty;
        private final Func2<C, T, C> adder;
        private final Func2<C, C, C> combiner;

        private CollectorImpl(@Nonnull Set<Characteristics> characteristics,
                              @Nonnull C initialValues,
                              @Nonnull C empty,
                              @Nonnull Predicate<C> isEmpty,
                              @Nonnull Func2<C, T, C> adder,
                              @Nonnull Func2<C, C, C> combiner)
        {
            this.characteristics = characteristics;
            this.initialValues = isEmpty.test(initialValues) ? null : initialValues;
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
            if (initialValues == null) {
                return a -> a.list;
            } else {
                return a -> combiner.apply(initialValues, a.list);
            }
        }

        @Override
        public Set<Characteristics> characteristics()
        {
            return characteristics;
        }
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
}
