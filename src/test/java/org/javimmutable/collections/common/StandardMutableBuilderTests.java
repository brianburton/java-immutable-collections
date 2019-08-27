///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.tree.ComparableComparator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.Assert.assertEquals;

public final class StandardMutableBuilderTests
{
    private StandardMutableBuilderTests()
    {
    }

    /**
     * Tests all of the standard MutableBuilder add methods using the specified build and comparison functions.
     */
    public static <T, C> void verifyBuilder(List<T> values,
                                            Func0<? extends MutableBuilder<T, C>> builderFactory,
                                            Func2<List<T>, C, Boolean> comparator)
    {
        @SuppressWarnings("TooBroadScope") C collection;

        Indexed<T> indexed = IndexedList.retained(values);

        // add via Iterator
        collection = builderFactory.apply().add(values.iterator()).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Collection
        builderFactory.apply().add(values).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via array
        //noinspection unchecked
        T[] array = (T[])values.toArray();
        //noinspection unchecked
        collection = builderFactory.apply().add(array).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Indexed in its entirety
        builderFactory.apply().add(indexed).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via indexed range
        builderFactory.apply().add(indexed, 0, indexed.size()).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // verify safe to call build() multiple times
        verifyMultipleCallOk(values, builderFactory, comparator, indexed);
    }

    private static <T, C> void verifyMultipleCallOk(List<T> values,
                                                    Func0<? extends MutableBuilder<T, C>> builderFactory,
                                                    Func2<List<T>, C, Boolean> comparator,
                                                    Indexed<T> indexed)
    {
        final MutableBuilder<T, C> multi = builderFactory.apply();
        final int multiStep = Math.max(1, indexed.size() / 8);
        final List<T> sublist = new ArrayList<>();
        int multiOffset = 0;
        while (multiOffset < indexed.size()) {
            final int nextOffset = Math.min(indexed.size(), multiOffset + multiStep);
            for (int i = multiOffset; i < nextOffset; ++i) {
                multi.add(values.get(i));
                sublist.add(values.get(i));
            }
            assertEquals(sublist.size(), multi.size());
            assertEquals(Boolean.TRUE, comparator.apply(sublist, multi.build()));
            multiOffset = nextOffset;
        }
        assertEquals(Boolean.TRUE, comparator.apply(values, multi.build()));
    }

    public static <C> void verifyThreadSafety(@Nonnull Func0<MutableBuilder<Integer, C>> builderFactory,
                                              @Nonnull Func1<C, Iterable<Integer>> transform)
        throws InterruptedException
    {
        final List<Integer> expected = IntStream.range(0, 4096).boxed().collect(Collectors.toList());
        final int numThreads = 32;
        final int perThread = (expected.size() + numThreads - 1) / numThreads;
        for (int loop = 1; loop <= 100; ++loop) {
            final MutableBuilder<Integer, C> builder = builderFactory.apply();
            final ExecutorService es = Executors.newFixedThreadPool(numThreads);
            try {
                int offset = 0;
                while (offset < expected.size()) {
                    int start = offset;
                    int limit = start + perThread;
                    es.submit(() -> addValues(builder, expected, start, limit));
                    offset += perThread;
                }
            } finally {
                es.shutdown();
                es.awaitTermination(10, TimeUnit.SECONDS);
            }
            List<Integer> sorted = new ArrayList<>();
            for (Integer value : transform.apply(builder.build())) {
                sorted.add(value);
            }
            sorted.sort(ComparableComparator.of());
            assertEquals(expected, sorted);
        }
    }

    public static <C extends JImmutableList<Integer>> void verifyThreadSafety(Func0<MutableBuilder<Integer, C>> builderFactory)
        throws InterruptedException
    {
        verifyThreadSafety(builderFactory, list -> list);
    }

    private static <T, C> void addValues(MutableBuilder<Integer, C> builder,
                                         List<Integer> indexed,
                                         int first,
                                         int limit)
    {
        for (int i = first; i < limit; ++i) {
            builder.add(indexed.get(i));
        }
    }
}
