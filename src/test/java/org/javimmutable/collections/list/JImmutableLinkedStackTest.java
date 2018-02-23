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

package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JImmutableLinkedStackTest
    extends TestCase
{
    public void test()
    {
        JImmutableStack<Integer> list = JImmutableLinkedStack.of();
        assertEquals(true, list.isEmpty());
        try {
            list.getHead();
            fail();
        } catch (UnsupportedOperationException ignored) {
            // expected
        }
        assertSame(list, list.getTail());
        StandardCursorTest.emptyCursorTest(list.cursor());
        StandardCursorTest.emptyIteratorTest(list.iterator());

        JImmutableStack<Integer> list2 = list.insert(10);
        assertEquals(false, list2.isEmpty());
        assertEquals(10, (int)list2.getHead());
        assertEquals(list, list2.getTail());
        StandardCursorTest.listCursorTest(Arrays.asList(10), list2.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(10), list2.iterator());

        JImmutableStack<Integer> list3 = list2.insert(30);
        assertEquals(false, list3.isEmpty());
        assertEquals(30, (int)list3.getHead());
        assertEquals(list2, list3.getTail());
        StandardCursorTest.listCursorTest(Arrays.asList(30, 10), list3.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(30, 10), list3.iterator());

        assertEquals(Collections.<Integer>emptyList(), list.makeList());
        assertEquals(Arrays.asList(10), list2.makeList());
        assertEquals(Arrays.asList(30, 10), list3.makeList());
        assertEquals("[30,10]", list3.toString());
        assertEquals(940, list3.hashCode());

        assertEquals(list, JImmutableLinkedStack.of(Collections.<Integer>emptyList()));
        assertEquals(list2, JImmutableLinkedStack.of(Arrays.asList(10)));
        assertEquals(list3, JImmutableLinkedStack.of(Arrays.asList(10, 30)));
    }

    @SuppressWarnings("SimplifyStreamApiCallChains")
    public void testStreams()
    {
        JImmutableStack<Integer> list = JImmutableLinkedStack.of(1, 2, 3, 4, 5, 6, 7);
        assertEquals(asList(4, 3, 2, 1), list.stream().filter(x -> x < 5).collect(toList()));
        assertEquals(asList(4, 3, 2, 1), list.stream().filter(x -> x < 5).collect(toList()));

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 2048; ++i) {
            expected.add(i);
        }
        list = JImmutableLinkedStack.of(expected);
        Collections.reverse(expected);
        assertEquals(expected.stream().collect(toList()), list.stream().collect(toList()));
        assertEquals(expected.parallelStream().collect(toList()), list.stream().collect(toList()));
    }

    public void testParallelStreams()
    {
        final JImmutableStack<Integer> original = JImmutableLinkedStack.of(StandardCursor.makeList(StandardCursor.forRange(1, 10000)));
        final List<Integer> collected = original.stream().parallel().collect(toList());
        assertEquals(StandardCursor.makeList(original.cursor()), collected);
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableStack)a).iterator();
        final JImmutableStack<String> empty = JImmutableLinkedStack.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCi5JTM4OKMqvqPwPAv9UjHkYGCoKylkZGJhfMlQAAGBF3jRcAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCi5JTM4OKMqvqPwPAv9UjHkYGCoKylkZGJhfMpYwMCaWMzJUAADCIe2GYwAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList("a", "b", "c")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCi5JTM4OKMqvqPwPAv9UjHkYGCoKylkZGJhfMpYwMCaXM4KoJAiVWM7IUAEAZv4PknEAAAA=");
    }
}
