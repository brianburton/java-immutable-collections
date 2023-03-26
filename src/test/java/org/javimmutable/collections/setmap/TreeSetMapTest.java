///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.ISetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.TreeMapTest;

public class TreeSetMapTest
    extends AbstractSetMapTestCase
{
    public void testNormalOrder()
    {
        ISetMap<Integer, Integer> map = verifyOperations(TreeSetMap.of(), Ordering.HASH);
        verifyRandom(TreeSetMap.of(), new TreeMap<>());
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getSet(1)),
                                                             IMapEntry.of(2, map.getSet(2)),
                                                             IMapEntry.of(3, map.getSet(3))),
                                               map.iterator());
    }

    public void testReverseOrder()
    {
        ISetMap<Integer, Integer> map = verifyOperations(TreeSetMap.of(Comparator.<Integer>reverseOrder()), Ordering.HASH);
        StandardIteratorTests.listIteratorTest(Arrays.asList(3, 2, 1), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(3, map.getSet(3)),
                                                             IMapEntry.of(2, map.getSet(2)),
                                                             IMapEntry.of(1, map.getSet(1))),
                                               map.iterator());
    }

    public void testEquals()
    {
        ISetMap<Integer, Integer> a = TreeSetMap.of();
        ISetMap<Integer, Integer> b = TreeSetMap.of();
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
        final Func1<Object, Iterator> iteratorFactory = a -> ((TreeSetMap)a).iterator();
        ISetMap<String, String> empty = TreeSetMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhQBoAsMRTvgQBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhDIwlDIyOcFZiBQDScJPFEgEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhDMwlDIyOYBYjkJUIxFFwXnIhQx0DG5ybVAEAraHYsi8BAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXWyG1/gSoMv0nPNzCxKLQHJQVkl+EcwwJphhDEwlDIyOYBYDkJUEZjECWU4VANgmCJwcAQAA");

        empty = TreeSetMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKWRgYmF8CXawFNDpRLycxL10vuKQoMy9dxTmxONUzrzg1rzizJLMs1Tk/tyCxKLEkv6icOaY2JuDpOSaYAUAaAHaXI874AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAJXOMQrCQBAF0InRzmOkstjGEwQtjCAEtEwzCUNY2eyG2TFGwRt5Fm9hYeEV1FhEKwun+h8+jznfYeQZ5o5LtcVGV9VOMDekCmcMFaKd9coTazT6iO+qlkk/2jDRmmSFdcquPTzf94imY4CWYfEHGedeGAv50j/Yej8ECG/dx5OORmXQlmotrG0ZzdBTYj1Zr0U3NHNVjYzieB9mpyy9XgY9AIFAEH8Sti/Pq7ORBgEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAJWOPQrCQBCFJ/50HsPKYhtPIFoYQRC0kjSTMISVzW6cHTUKeiLP4i0sLLyCuipiZeHAg/cew8c7XqHpGQaOc7XAtS6KlWBqSGXOGMpEO+uVJ9Zo9A6fUY3iz9OMiaYkYywn7Krt/Xm3drcFUDEM/0D2Ui+MmXzRP7DlpgFQv4TFnYBGZdDmaiqsbd7uo6fYerJei15T3xUlMorjTT3ZJ5PzqfYBQE0gwrdbwgGaIaZB81cVBZdVD8vHREAZAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAAAJXOMQrCUAyA4Wh18xidHN7iCbQOVhAKOrqkJZQnr++VvNhWwRt5Fm/h4OAV1CoUJwcz/YHwkfMdhp5h7jhXO6x0UewFU0Mqc8ZQJtpZrzyxRqOP+F7VMu6ONky0JllhmbBrDs/3PMLJCKBhWPxBTlMvjJl86R9sWQ8Aglv78bilURm0uVoLa5uHEXqKrSfrteiKIleUyCiO62B72ibXS78DoC/Qm34K2ko/1Wtr1rwAllzm9hABAAA=");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeSetMap mapA = (TreeSetMap)a;
        TreeSetMap mapB = (TreeSetMap)b;
        assertEquals(mapA.getComparator(), mapB.getComparator());
        TreeMapTest.extraSerializationChecks(mapA.getMap(), mapB.getMap());
    }
}
