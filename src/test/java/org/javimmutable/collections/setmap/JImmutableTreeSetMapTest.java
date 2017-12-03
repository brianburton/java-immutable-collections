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

package org.javimmutable.collections.setmap;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.tree.JImmutableTreeMapTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import static java.util.Arrays.asList;

public class JImmutableTreeSetMapTest
    extends AbstractJImmutableSetMapTestCase
{
    public void testNormalOrder()
    {
        JImmutableSetMap<Integer, Integer> map = verifyOperations(JImmutableTreeSetMap.of(), Ordering.HASH);
        verifyRandom(JImmutableTreeSetMap.of(), new TreeMap<>());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(MapEntry.of(1, map.getSet(1)),
                                                        MapEntry.of(2, map.getSet(2)),
                                                        MapEntry.of(3, map.getSet(3))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(MapEntry.of(1, map.getSet(1)),
                                                          MapEntry.of(2, map.getSet(2)),
                                                          MapEntry.of(3, map.getSet(3))),
                                            map.iterator());
    }

    public void testReverseOrder()
    {
        JImmutableSetMap<Integer, Integer> map = verifyOperations(JImmutableTreeSetMap.of(Comparator.<Integer>reverseOrder()), Ordering.HASH);
        StandardCursorTest.listCursorTest(Arrays.asList(3, 2, 1), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(MapEntry.of(3, map.getSet(3)),
                                                        MapEntry.of(2, map.getSet(2)),
                                                        MapEntry.of(1, map.getSet(1))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(MapEntry.of(3, map.getSet(3)),
                                                          MapEntry.of(2, map.getSet(2)),
                                                          MapEntry.of(1, map.getSet(1))),
                                            map.iterator());
    }

    public void testEquals()
    {
        JImmutableSetMap<Integer, Integer> a = JImmutableTreeSetMap.of();
        JImmutableSetMap<Integer, Integer> b = JImmutableTreeSetMap.of();
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

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableTreeSetMap)a).iterator();
        JImmutableSetMap<String, String> empty = JImmutableTreeSetMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhQBoAsMRTvgQBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeSetMapTest::extraSerializationChecks, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhDIwlDIyOcFZiBQDScJPFEgEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeSetMapTest::extraSerializationChecks, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhDMwlDIyOYBYjkJUIxFFwXnIhQx0DG5ybVAEAraHYsi8BAAA=");

        empty = JImmutableTreeSetMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXawFNDpRLycxL10vuKQoMy9dxTmxONUzrzg1rzizJLMs1Tk/tyCxKLEkv6icOaY2JuDpOSaYAUAaAHaXI874AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeSetMapTest::extraSerializationChecks, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAJXOMQrCQBAF0InRzmOkstjGEwQtjCAEtEwzCUNY2eyG2TFGwRt5Fm9hYeEV1FhEKwun+h8+jznfYeQZ5o5LtcVGV9VOMDekCmcMFaKd9coTazT6iO+qlkk/2jDRmmSFdcquPTzf94imY4CWYfEHGedeGAv50j/Yej8ECG/dx5OORmXQlmotrG0ZzdBTYj1Zr0U3NHNVjYzieB9mpyy9XgY9AIFAEH8Sti/Pq7ORBgEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeSetMapTest::extraSerializationChecks, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAJWOPQrCQBCFJ/50HsPKYhtPIFoYQRC0kjSTMISVzW6cHTUKeiLP4i0sLLyCuipiZeHAg/cew8c7XqHpGQaOc7XAtS6KlWBqSGXOGMpEO+uVJ9Zo9A6fUY3iz9OMiaYkYywn7Krt/Xm3drcFUDEM/0D2Ui+MmXzRP7DlpgFQv4TFnYBGZdDmaiqsbd7uo6fYerJei15T3xUlMorjTT3ZJ5PzqfYBQE0gwrdbwgGaIaZB81cVBZdVD8vHREAZAQAA");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        JImmutableTreeSetMap mapA = (JImmutableTreeSetMap)a;
        JImmutableTreeSetMap mapB = (JImmutableTreeSetMap)b;
        assertEquals(mapA.getComparator(), mapB.getComparator());
        JImmutableTreeMapTest.extraSerializationChecks(mapA.getMap(), mapB.getMap());
    }
}
