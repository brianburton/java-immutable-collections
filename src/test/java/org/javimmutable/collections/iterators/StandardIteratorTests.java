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

import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
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
}
