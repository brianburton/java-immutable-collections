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
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JImmutableInsertOrderListMapTest
    extends AbstractJImmutableListMapTestCase
{
    @SuppressWarnings("unchecked")
    public void test()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableInsertOrderListMap.of(), Ordering.INORDER);
        StandardCursorTest.listCursorTest(Arrays.asList(1, 3, 2), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(MapEntry.of(1, map.getList(1)),
                                                        MapEntry.of(3, map.getList(3)),
                                                        MapEntry.of(2, map.getList(2))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(MapEntry.of(1, map.getList(1)),
                                                          MapEntry.of(3, map.getList(3)),
                                                          MapEntry.of(2, map.getList(2))),
                                            map.iterator());
    }

    public void testEquals()
    {
        JImmutableListMap<Integer, Integer> a = JImmutableInsertOrderListMap.of();
        JImmutableListMap<Integer, Integer> b = JImmutableInsertOrderListMap.of();
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
        JImmutableListMap<Integer, Integer> listMap = JImmutableInsertOrderListMap.<Integer, Integer>of()
            .insert(4, 40)
            .insert(2, 20)
            .insert(3, 30)
            .insert(1, 10)
            .insert(2, 20)
            .insert(4, 45)
            .insert(4, 50);
        assertEquals(asList(4, 2, 3, 1), listMap.stream().map(e -> e.getKey()).collect(toList()));
        assertEquals(asList(3, 2, 1, 1), listMap.stream().map(e -> e.getValue().size()).collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableInsertOrderListMap)a).iterator();
        JImmutableListMap<String, String> empty = JImmutableInsertOrderListMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJ78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvlkFpf4JhYEFOVXVP4HgX8qxjwMDBVFDJ4kGO2YVFxSlJhcgrACl7kF5RwMDMwvGYCgAgCgsGMpxAAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJ78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvlkFpf4JhYEFOVXVP4HgX8qxjwMDBVFDJ4kGO2YVFxSlJhcgrACl7kF5RwMDMwvGRgYGEsYGB3LWaCsxAoAf4hpPdIAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJ78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvlkFpf4JhYEFOVXVP4HgX8qxjwMDBVFDJ4kGO2YVFxSlJhcgrACl7kF5RwMDMwvGYBECQOjYzkLkMUIZCUWMtQxsMC5SUAcBeclVwAAS4EY5e8AAAA=");
    }
}
