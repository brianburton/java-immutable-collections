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

package org.javimmutable.collections.common;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.tree.ComparableComparator;

public final class StandardBuilderTests
{
    private StandardBuilderTests()
    {
    }

    public interface BuilderAdapter<T, C>
    {
        C build();

        int size();

        void add(T value);

        void add(Iterator<? extends T> source);

        void add(Iterable<? extends T> source);

        <K extends T> void add(K... source);

        void add(Indexed<? extends T> source,
                 int offset,
                 int limit);

        void add(Indexed<? extends T> source);

        void clear();
    }

    /**
     * Tests all of the standard Builder add methods using the specified build and comparison functions.
     */
    public static <T, C> void verifyBuilder(List<T> values,
                                            Func0<? extends BuilderAdapter<T, C>> builderFactory,
                                            Func2<List<T>, C, Boolean> comparator,
                                            T[] arrayTemplate)
    {
        @SuppressWarnings("TooBroadScope") C collection;

        Indexed<T> indexed = IndexedList.retained(values);

        // add via Iterator
        BuilderAdapter<T, C> builder = builderFactory.apply();
        builder.add(values.iterator());
        collection = builder.build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Collection
        builder = builderFactory.apply();
        builder.add(values);
        collection = builder.build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via array
        //noinspection unchecked
        T[] array = (T[])values.toArray(arrayTemplate);
        //noinspection unchecked
        builder.clear();
        builder.add(array);
        collection = builder.build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Indexed in its entirety
        builder.clear();
        builder.add(indexed);
        collection = builder.build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via indexed range
        builder.clear();
        builder.add(indexed, 0, indexed.size());
        collection = builder.build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // verify safe to call build() multiple times
        verifyMultipleCallOk(values, builderFactory, comparator, indexed);
    }

    private static <T, C> void verifyMultipleCallOk(List<T> values,
                                                    Func0<? extends BuilderAdapter<T, C>> builderFactory,
                                                    Func2<List<T>, C, Boolean> comparator,
                                                    Indexed<T> indexed)
    {
        final BuilderAdapter<T, C> multi = builderFactory.apply();
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

    public static <T, C> void verifyThreadSafety(@Nonnull List<T> expected,
                                                 @Nonnull Comparator<T> comparator,
                                                 @Nonnull Func0<BuilderAdapter<T, C>> builderFactory,
                                                 @Nonnull Func1<C, Iterable<T>> transform)
        throws InterruptedException
    {
        final int numThreads = 32;
        final int perThread = (expected.size() + numThreads - 1) / numThreads;
        for (int loop = 1; loop <= 100; ++loop) {
            final BuilderAdapter<T, C> builder = builderFactory.apply();
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
            List<T> sorted = new ArrayList<>();
            for (T value : transform.apply(builder.build())) {
                sorted.add(value);
            }
            sorted.sort(comparator);
            assertEquals(expected, sorted);
        }
    }

    public static <C> void verifyThreadSafety(@Nonnull Func0<BuilderAdapter<Integer, C>> builderFactory,
                                              @Nonnull Func1<C, Iterable<Integer>> transform)
        throws InterruptedException
    {
        final List<Integer> expected = IntStream.range(0, 4096).boxed().collect(Collectors.toList());
        verifyThreadSafety(expected, ComparableComparator.of(), builderFactory, transform);
    }

    public static <C extends Iterable<Integer>> void verifyThreadSafety(Func0<BuilderAdapter<Integer, C>> builderFactory)
        throws InterruptedException
    {
        verifyThreadSafety(builderFactory, list -> list);
    }

    private static <T, C> void addValues(BuilderAdapter<T, C> builder,
                                         List<T> indexed,
                                         int first,
                                         int limit)
    {
        for (int i = first; i < limit; ++i) {
            builder.add(indexed.get(i));
        }
    }
}
