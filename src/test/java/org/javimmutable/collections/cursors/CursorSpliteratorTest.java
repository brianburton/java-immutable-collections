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

package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.indexed.IndexedArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public class CursorSpliteratorTest
    extends TestCase
{
    public void testEmpty()
    {
        Spliterator<Integer> si = spliterator();
        ValueCollector consumer = new ValueCollector();
        assertEquals(false, si.tryAdvance(consumer));
        assertEquals(0, consumer.values.size());
        assertEquals(null, si.trySplit());
    }

    public void testSingle()
    {
        Spliterator<Integer> si = spliterator(1);
        ValueCollector consumer = new ValueCollector();
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(false, si.tryAdvance(consumer));
        assertEquals(asList(1), consumer.values);
    }

    public void testMulti()
    {
        Spliterator<Integer> si = spliterator(1, 2, 3, 4);
        ValueCollector consumer = new ValueCollector();
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(false, si.tryAdvance(consumer));
        assertEquals(asList(1, 2, 3, 4), consumer.values);

        si = spliterator(1, 2, 3, 4);
        Spliterator<Integer> left = si.trySplit();
        assertNotNull(left);
        assertEquals(asList(1, 2), list(left));
        assertEquals(asList(3, 4), list(si));
    }

    public void testStream()
    {
        Stream<Integer> str = stream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8), str.collect(Collectors.toList()));

        str = stream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(2, 4, 6, 8), str.filter(x -> x % 2 == 0).collect(Collectors.toList()));

        str = parstream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8), str.collect(Collectors.toList()));

        str = parstream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(2, 4, 6, 8), str.filter(x -> x % 2 == 0).collect(Collectors.toList()));
    }

    private List<Integer> list(Spliterator<Integer> si)
    {
        ValueCollector consumer = new ValueCollector();
        si.forEachRemaining(consumer);
        return consumer.values;
    }

    private Spliterator<Integer> spliterator(Integer... values)
    {
        return new CursorSpliterator<Integer>(Spliterator.IMMUTABLE, StandardCursor.of(IndexedArray.retained(values)));
    }

    private Stream<Integer> stream(Integer... values)
    {
        return StreamSupport.stream(spliterator(values), false);
    }

    private Stream<Integer> parstream(Integer... values)
    {
        return StreamSupport.stream(spliterator(values), true);
    }

    private static class ValueCollector
        implements Consumer<Integer>
    {
        private final List<Integer> values = new ArrayList<>();

        @Override
        public void accept(Integer value)
        {
            values.add(value);
        }
    }
}
