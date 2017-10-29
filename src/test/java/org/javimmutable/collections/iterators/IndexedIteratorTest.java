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

package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.IndexedHelper;
import org.javimmutable.collections.common.IndexedList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;

public class IndexedIteratorTest
    extends TestCase
{
    public void test()
    {
        verifyIterable(asList(), () -> IndexedIterator.iterator(IndexedHelper.empty()));
        verifyIterable(asList(1), () -> IndexedIterator.iterator(IndexedHelper.indexed(1)));
        verifyIterable(asList(1, 2), () -> IndexedIterator.iterator(IndexedHelper.indexed(1, 2)));
        verifySplit(false, asList(), asList(), IndexedIterator.iterator(IndexedHelper.empty()));
        verifySplit(false, asList(), asList(), IndexedIterator.iterator(IndexedHelper.indexed(1)));
        verifySplit(true, asList(1), asList(2), IndexedIterator.iterator(IndexedHelper.indexed(1, 2)));
        verifySplit(true, asList(1), asList(2, 3), IndexedIterator.iterator(IndexedHelper.indexed(1, 2, 3)));
        verifySplit(true, asList(1, 2), asList(3, 4, 5), IndexedIterator.iterator(IndexedList.retained(asList(1, 2, 3, 4, 5))));
    }

    public static <T> void verifyIterable(List<T> expected,
                                          SplitableIterable<T> source)
    {
        verifyIterator(expected, source.iterator());
        verifyNextOnlyIteration(expected, source.iterator());
    }

    public static <T> void verifyIterator(List<T> expected,
                                          SplitableIterator<T> iterator)
    {
        // normal iteration
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(true, iterator.hasNext());
            assertEquals(expected.get(i), iterator.next());
        }
        assertEquals(false, iterator.hasNext());
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifyNextOnlyIteration(List<T> expected,
                                                   SplitableIterator<T> iterator)
    {
        // next only iteration
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), iterator.next());
        }
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifySplit(boolean allowed,
                                       List<T> leftExpected,
                                       List<T> rightExpected,
                                       SplitableIterator<T> source)
    {
        assertEquals(allowed, source.isSplitAllowed());
        if (allowed) {
            SplitIterator<T> split = source.splitIterator();
            verifyIterator(leftExpected, split.getLeft());
            verifyIterator(rightExpected, split.getRight());
        }
    }

    @Nonnull
    public static <T> List<T> collect(Iterator<T> iterator)
    {
        final List<T> answer = new ArrayList<>();
        while (iterator.hasNext()) {
            answer.add(iterator.next());
        }
        return answer;
    }
}
