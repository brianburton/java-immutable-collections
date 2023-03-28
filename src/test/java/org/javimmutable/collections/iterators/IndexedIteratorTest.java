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

package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.indexed.IndexedHelper;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

public class IndexedIteratorTest
    extends TestCase
{
    public void testForward()
    {
        verifyOrderedIterable(asList(), IndexedIterator.fwd(IndexedHelper.empty()));
        verifyOrderedIterable(asList(1), IndexedIterator.fwd(IndexedHelper.indexed(1)));
        verifyOrderedIterable(asList(1, 2), IndexedIterator.fwd(IndexedHelper.indexed(1, 2)));
        verifyOrderedSplit(false, asList(), asList(), IndexedIterator.iterator(IndexedHelper.empty()));
        verifyOrderedSplit(false, asList(), asList(), IndexedIterator.iterator(IndexedHelper.indexed(1)));
        verifyOrderedSplit(true, asList(1), asList(2), IndexedIterator.iterator(IndexedHelper.indexed(1, 2)));
        verifyOrderedSplit(true, asList(1), asList(2, 3), IndexedIterator.iterator(IndexedHelper.indexed(1, 2, 3)));
        verifyOrderedSplit(true, asList(1, 2), asList(3, 4, 5), IndexedIterator.forRange(1, 5));
    }

    public void testReverse()
    {
        verifyOrderedIterable(asList(), IndexedIterator.rev(IndexedHelper.empty()));
        verifyOrderedIterable(asList(1), IndexedIterator.rev(IndexedHelper.indexed(1)));
        verifyOrderedIterable(asList(2, 1), IndexedIterator.rev(IndexedHelper.indexed(1, 2)));
        verifyOrderedSplit(false, asList(), asList(), IndexedIterator.reverse(IndexedHelper.empty()));
        verifyOrderedSplit(false, asList(), asList(), IndexedIterator.reverse(IndexedHelper.indexed(1)));
        verifyOrderedSplit(true, asList(2), asList(1), IndexedIterator.reverse(IndexedHelper.indexed(1, 2)));
        verifyOrderedSplit(true, asList(3), asList(2, 1), IndexedIterator.reverse(IndexedHelper.indexed(1, 2, 3)));
        verifyOrderedSplit(true, asList(5, 4), asList(3, 2, 1), IndexedIterator.reverse(IndexedHelper.indexed(1, 2, 3, 4, 5)));
    }

    public void testReduce()
    {
        final Double zero = 0.0;
        final Func2<Double, Integer, Double> operator = (s, v) -> s + ((double)v) / 2.0;
        assertSame(zero, emptyIterable().reduce(zero, operator));
        assertEquals(0.0, rangeIterable(0).reduce(zero, operator));
        assertEquals(0.5, rangeIterable(1).reduce(zero, operator));
        assertEquals(1.5, rangeIterable(2).reduce(zero, operator));
        assertEquals(3.0, rangeIterable(3).reduce(zero, operator));
    }

    private SplitableIterable<Integer> emptyIterable()
    {
        return () -> EmptyIterator.of();
    }

    private SplitableIterable<Integer> rangeIterable(int high)
    {
        return () -> IndexedIterator.forRange(0, high);
    }
}
