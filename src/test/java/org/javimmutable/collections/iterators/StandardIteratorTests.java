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

package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static junit.framework.Assert.*;

public class StandardIteratorTests
{
    public static <T> void verifyUnorderedIterable(@Nonnull Iterable<T> expected,
                                                   @Nonnull Iterable<T> actual)
    {
        verifyUnorderedIterable(expected, actual, identity());
    }

    public static <S, T> void verifyUnorderedIterable(@Nonnull Iterable<T> expected,
                                                      @Nonnull Iterable<S> actual,
                                                      @Nonnull Function<S, T> transforminator)
    {
        verifyUnorderedIteratorUsingHasNext(expected.iterator(), actual.iterator(), transforminator);
        verifyUnorderedIteratorUsingNextOnly(expected.iterator(), actual.iterator(), transforminator);
    }

    public static <T> void verifyOrderedIterable(@Nonnull Iterable<T> expected,
                                                 @Nonnull Iterable<T> actual)
    {
        verifyOrderedIterable(expected, actual, identity());
    }

    public static <S, T> void verifyOrderedIterable(@Nonnull Iterable<T> expected,
                                                    @Nonnull Iterable<S> actual,
                                                    @Nonnull Function<S, T> transforminator)
    {
        verifyOrderedIteratorUsingHasNext(expected.iterator(), actual.iterator(), transforminator);
        verifyOrderedIteratorUsingNextOnly(expected.iterator(), actual.iterator(), transforminator);
    }

    public static <S, T> void verifyUnorderedIteratorUsingNextOnly(@Nonnull Iterator<T> expected,
                                                                   @Nonnull Iterator<S> actual,
                                                                   @Nonnull Function<S, T> transforminator)
    {
        final Set<T> expectedValues = new HashSet<>();
        final Set<T> actualValues = new HashSet<>();
        while (expected.hasNext()) {
            expectedValues.add(expected.next());
            actualValues.add(transforminator.apply(actual.next()));
        }
        assertEquals(expectedValues, actualValues);
        try {
            actual.next();
            fail("did not throw NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifyUnorderedIteratorUsingHasNext(@Nonnull Iterator<T> expected,
                                                               @Nonnull Iterator<T> actual)
    {
        verifyUnorderedIteratorUsingHasNext(expected, actual, identity());
    }

    public static <S, T> void verifyUnorderedIteratorUsingHasNext(@Nonnull Iterator<T> expected,
                                                                  @Nonnull Iterator<S> actual,
                                                                  @Nonnull Function<S, T> transforminator)
    {
        final Set<T> expectedValues = new HashSet<>();
        final Set<T> actualValues = new HashSet<>();
        while (expected.hasNext()) {
            assertEquals(true, actual.hasNext());
            expectedValues.add(expected.next());
            actualValues.add(transforminator.apply(actual.next()));
        }
        assertEquals(false, actual.hasNext());
        assertEquals(expectedValues, actualValues);
        try {
            actual.next();
            fail("did not throw NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <S, T> void verifyOrderedIteratorUsingNextOnly(@Nonnull Iterator<T> expected,
                                                                 @Nonnull Iterator<S> actual,
                                                                 @Nonnull Function<S, T> transforminator)
    {
        while (expected.hasNext()) {
            assertEquals(expected.next(), transforminator.apply(actual.next()));
        }
        try {
            actual.next();
            fail("did not throw NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifyOrderedIteratorUsingHasNext(@Nonnull Iterator<T> expected,
                                                             @Nonnull Iterator<T> actual)
    {
        verifyOrderedIteratorUsingHasNext(expected, actual, identity());
    }

    public static <S, T> void verifyOrderedIteratorUsingHasNext(@Nonnull Iterator<T> expected,
                                                                @Nonnull Iterator<S> actual,
                                                                @Nonnull Function<S, T> transforminator)
    {
        while (expected.hasNext()) {
            assertEquals(true, actual.hasNext());
            assertEquals(expected.next(), transforminator.apply(actual.next()));
        }
        assertEquals(false, actual.hasNext());
        try {
            actual.next();
            fail("did not throw NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifyOrderedSplit(boolean allowed,
                                              List<T> leftExpected,
                                              List<T> rightExpected,
                                              SplitableIterator<T> source)
    {
        assertEquals(allowed, source.isSplitAllowed());
        if (allowed) {
            SplitIterator<T> split = source.splitIterator();
            verifyOrderedIteratorUsingHasNext(leftExpected.iterator(), split.getLeft());
            verifyOrderedIteratorUsingHasNext(rightExpected.iterator(), split.getRight());
        }
    }

    public static <T> void iteratorTest(Func1<Integer, T> lookup,
                                        int size,
                                        Iterator<T> iterator)
    {
        // calling next advances through entire sequence
        for (int i = 0; i < size; ++i) {
            assertEquals(true, iterator.hasNext());
            assertEquals(lookup.apply(i), iterator.next());
        }
        // after expected sequence has no values
        assertEquals(false, iterator.hasNext());
        // calling next() at end throws
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
            // expected
        }
        // safe to call multiple times once at end
        assertEquals(false, iterator.hasNext());
    }

    public static <T> void indexedIteratorTest(Indexed<T> indexed,
                                               int size,
                                               Iterator<T> iterator)
    {
        iteratorTest(new IndexedLookup<>(indexed), size, iterator);
    }

    public static <T> void listIteratorTest(List<T> list,
                                            Iterator<T> iterator)
    {
        iteratorTest(new ListLookup<>(list), list.size(), iterator);
    }

    public static <T> void emptyIteratorTest(Iterator<T> iterator)
    {
        listIteratorTest(Collections.emptyList(), iterator);
    }

    public static <T> void verifySplit(SplitableIterator<T> cursor,
                                       List<T> left,
                                       List<T> right)
    {
        SplitIterator<T> split = cursor.splitIterator();
        listIteratorTest(left, split.getLeft());
        listIteratorTest(right, split.getRight());
    }

    private static class ListLookup<T>
        implements Func1<Integer, T>
    {
        private final List<T> list;

        private ListLookup(List<T> list)
        {
            this.list = list;
        }

        @Override
        public T apply(Integer value)
        {
            return list.get(value);
        }
    }

    private static class IndexedLookup<T>
        implements Func1<Integer, T>
    {
        private final Indexed<T> indexed;

        private IndexedLookup(Indexed<T> indexed)
        {
            this.indexed = indexed;
        }

        @Override
        public T apply(Integer value)
        {
            return indexed.get(value);
        }
    }

    /**
     * Utility method, useful in unit tests, that collects all of the values in the Iterator into a List
     * and returns the List.
     */
    public static <T> List<T> makeList(Iterator<T> iterator)
    {
        List<T> answer = new ArrayList<>();
        while (iterator.hasNext()) {
            answer.add(iterator.next());
        }
        return answer;
    }

    /**
     * Utility method, useful in unit tests, that essentially casts away actual type of an object
     * so that Iterable version of an overload is triggered instead of more specific class.
     */
    public static <T> Iterable<T> plainIterable(Iterable<T> obj)
    {
        return obj::iterator;
    }
}
