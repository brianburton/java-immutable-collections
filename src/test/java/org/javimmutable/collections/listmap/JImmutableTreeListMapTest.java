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

package org.javimmutable.collections.listmap;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JImmutableTreeListMapTest
    extends AbstractJImmutableListMapTestCase
{
    @SuppressWarnings("unchecked")
    public void testNormalOrder()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableTreeListMap.of(), Ordering.HASH);
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(MapEntry.of(1, map.getList(1)),
                                                        MapEntry.of(2, map.getList(2)),
                                                        MapEntry.of(3, map.getList(3))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(MapEntry.of(1, map.getList(1)),
                                                          MapEntry.of(2, map.getList(2)),
                                                          MapEntry.of(3, map.getList(3))),
                                            map.iterator());
    }

    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableTreeListMap.of(Comparator.<Integer>reverseOrder()), Ordering.REVERSED);
        StandardCursorTest.listCursorTest(Arrays.asList(3, 2, 1), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(MapEntry.of(3, map.getList(3)),
                                                        MapEntry.of(2, map.getList(2)),
                                                        MapEntry.of(1, map.getList(1))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(MapEntry.of(3, map.getList(3)),
                                                          MapEntry.of(2, map.getList(2)),
                                                          MapEntry.of(1, map.getList(1))),
                                            map.iterator());
    }

    public void testEquals()
    {
        JImmutableListMap<Integer, Integer> a = JImmutableTreeListMap.of();
        JImmutableListMap<Integer, Integer> b = JImmutableTreeListMap.of();
        assertEquals(a, b);
        assertEquals(b, a);

        a = a.insert(1, 10);
        assertFalse(a.equals(b));
        b = b.insert(1, 10);
        assertEquals(a, b);
        assertEquals(b, a);
        a = a.insert(1, 12);
        assertFalse(a.equals(b));
        b = b.insert(1, 12);
        assertEquals(a, b);
        assertEquals(b, a);
    }

    public void testStreams()
    {
        JImmutableListMap<Integer, Integer> listMap = JImmutableTreeListMap.<Integer, Integer>of()
            .insert(4, 40)
            .insert(3, 30)
            .insert(2, 20)
            .insert(1, 10)
            .insert(2, 20)
            .insert(4, 45)
            .insert(4, 50);
        assertEquals(asList(1, 2, 3, 4), listMap.stream().map(e -> e.getKey()).collect(toList()));
        assertEquals(asList(1, 2, 1, 3), listMap.stream().map(e -> e.getValue().size()).collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableTreeListMap)a).iterator();
        JImmutableListMap<String, String> empty = JImmutableTreeListMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYUAaAGoIkSAGAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYQyMJQyMjnBWYgUArB9/AxQBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYQzMJQyMjmAWI5CVCMRRcF5yIUMdAxucm1QBANqG5HMxAQAA");

        empty = JImmutableTreeListMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt2sBTQ7US8nMS9dL7ikKDMvXcU5sTjVM684Na84sySzLNU5P7cgsSixJL+onDmmNibg6TkmmAFAGgAG1XVw+gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt2sBTQ7US8nMS9dL7ikKDMvXcU5sTjVM684Na84sySzLNU5P7cgsSixJL+onDmmNibg6TkmmAEMjCUMjI5wVmIFAAOWtngIAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAJWOMQrCQBBFx2g6j2FlsY0nkGARURC0kjSTsISVzW6cHTUKeiLP4i0sLLyCulGClYUDH/7/DI9/vkPoCEaWcrHCrSqKDWOqpcis1jJjZY0TTpJCrQ5YRzGOm6cFSTlRjqdYzshW+2d9j96gC1ARxH8wh6ljwoy/7F/cctcBaN/85r5no9BocjFnUibvRehkbJw0TrHaysgWJRKypV07OSaz6yVoABAwtPDj1nCC0MfUa/muWt5l1Qs2SrO7GwEAAA==");
    }
}
