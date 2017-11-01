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
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedHelper;

import static com.google.common.primitives.Ints.asList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

@SuppressWarnings("unchecked")
public class LazyMultiIteratorTest
    extends TestCase
{
    public void test()
    {
        verifyOrderedIterable(asList(), iterator(values()));
        verifyOrderedIterable(asList(), iterator(values(), values()));
        verifyOrderedIterable(asList(), iterator(values(), values(), values()));

        verifyOrderedIterable(asList(1), iterator(values(1)));
        verifyOrderedIterable(asList(1), iterator(values(), values(1)));
        verifyOrderedIterable(asList(1), iterator(values(1), values()));

        verifyOrderedIterable(asList(1, 2), iterator(values(1, 2)));
        verifyOrderedIterable(asList(1, 2), iterator(values(1), values(2)));
        verifyOrderedIterable(asList(1, 2), iterator(values(), values(1), values(), values(2), values()));

        verifyOrderedIterable(asList(1, 2, 3, 4), iterator(values(1, 2, 3, 4)));
        verifyOrderedIterable(asList(1, 2, 3, 4), iterator(values(1, 2), values(3, 4)));
        verifyOrderedIterable(asList(1, 2, 3, 4), iterator(values(), values(1), values(2, 3), values(), values(4)));

        Iterable<Integer> multi = () -> LazyMultiIterator.transformed(IndexedHelper.range(0, 3), value -> () -> IndexedIterator.forRange(1, value));
        verifyOrderedIterable(asList(1, 1, 2, 1, 2, 3), multi);

        multi = () -> LazyMultiIterator.transformed(IndexedHelper.indexed(2, 3, 4), value -> () -> IndexedIterator.forRange(1, value));
        verifyOrderedIterable(asList(1, 2, 1, 2, 3, 1, 2, 3, 4), multi);

        multi = () -> LazyMultiIterator.transformed(IndexedIterator.forRange(2, 4), value -> () -> IndexedIterator.forRange(1, value));
        verifyOrderedIterable(asList(1, 2, 1, 2, 3, 1, 2, 3, 4), multi);
    }

    public void testSplitAllowed()
    {
        assertEquals(false, valueIterator(0).isSplitAllowed());
        assertEquals(false, valueIterator(1).isSplitAllowed());
        assertEquals(true, valueIterator(2).isSplitAllowed());
        assertEquals(true, valueIterator(3).isSplitAllowed());
        assertEquals(true, valueIterator(4).isSplitAllowed());
    }

    public void testSplit()
    {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> valueIterator(0).splitIterator());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> valueIterator(1).splitIterator());
        verifyOrderedSplit(true, asList(1), asList(2), valueIterator(2));
        verifyOrderedSplit(true, asList(1), asList(2, 3), valueIterator(3));
        verifyOrderedSplit(true, asList(1, 2), asList(3, 4), valueIterator(4));
        SplitableIterator<Integer> splitMe = valueIterator(4);
        splitMe.next();
        verifyOrderedSplit(true, asList(2), asList(3, 4), splitMe);
    }

    private SplitableIterator<Integer> valueIterator(int length)
    {
        final SplitableIterable<Integer>[] array = new SplitableIterable[length];
        for (int i = 1; i <= length; ++i) {
            array[i - 1] = values(i);
        }
        return iterator(array).iterator();
    }

    private SplitableIterable<Integer> iterator(SplitableIterable<Integer>... array)
    {
        return () -> LazyMultiIterator.iterator(IndexedArray.retained(array));
    }

    private SplitableIterable<Integer> values(Integer... array)
    {
        return () -> IndexedIterator.iterator(IndexedArray.retained(array));
    }
}
