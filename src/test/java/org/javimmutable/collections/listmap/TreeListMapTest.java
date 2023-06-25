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

package org.javimmutable.collections.listmap;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.TreeMapTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class TreeListMapTest
    extends AbstractListMapTestCase
{
    @SuppressWarnings("unchecked")
    public void testNormalOrder()
    {
        IListMap<Integer, Integer> map = verifyOperations(TreeListMap.of(), Ordering.HASH);
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getList(1)),
                                                             IMapEntry.of(2, map.getList(2)),
                                                             IMapEntry.of(3, map.getList(3))),
                                               map.iterator());
    }

    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        IListMap<Integer, Integer> map = verifyOperations(TreeListMap.of(Comparator.<Integer>reverseOrder()), Ordering.REVERSED);
        StandardIteratorTests.listIteratorTest(Arrays.asList(3, 2, 1), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(3, map.getList(3)),
                                                             IMapEntry.of(2, map.getList(2)),
                                                             IMapEntry.of(1, map.getList(1))),
                                               map.iterator());
    }

    public void testEquals()
    {
        IListMap<Integer, Integer> a = TreeListMap.of();
        IListMap<Integer, Integer> b = TreeListMap.of();
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
        IListMap<Integer, Integer> listMap = TreeListMap.<Integer, Integer>of()
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
        final Func1<Object, Iterator> iteratorFactory = a -> ((TreeListMap)a).iterator();
        IListMap<String, String> empty = TreeListMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpN9cksLvFNLAgoyq+o/A8C/1SMeRgYKooY7EkwyTGpuKQoMbkEl2kF5SwMDMwvge4zw2tqCdBFes75uQWJRSA5KKskvwhmGBPMMCANAH64jVLyAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpN9cksLvFNLAgoyq+o/A8C/1SMeRgYKooY7EkwyTGpuKQoMbkEl2kF5SwMDMwvge4zw2tqCdBFes75uQWJRSA5KKskvwhmGBPMMAbGEgZGRzgrsQIArC8udAABAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpN9cksLvFNLAgoyq+o/A8C/1SMeRgYKooY7EkwyTGpuKQoMbkEl2kF5SwMDMwvge4zw2tqCdBFes75uQWJRSA5KKskvwhmGBPMMAbmEgZGRzCLEchKBOIoOC+5kKGOgQ3OTaoAAGcYwfUdAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpN9cksLvFNLAgoyq+o/A8C/1SMeRgYKooY7EkwyTGpuKQoMbkEl2kF5SwMDMwvge4zw2tqCdBFes75uQWJRSA5KKskvwhmGBPMMAamEgZGRzCLAchKArMYgSynCgA2mpISCgEAAA==");

        empty = TreeListMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpN9cksLvFNLAgoyq+o/A8C/1SMeRgYKooY7EkwyTGpuKQoMbkEl2kF5SwMDMwvge7TApqYqJeTmJeuF1xSlJmXruKcWJzqmVecmlecWZJZluqcn1uQWJRYkl9UzhxTGxPw9BwTzAAgDQDGTHrD5gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/5XOMQ6CQBAF0EG08xhUFttYWhhCZaIJiZY0A9mQMcsumR0BTbyRZ/EWFhZeQYVCOwt/9X/z8i8PmHiGheNS7bGhqjoI5karwhmjCyFnvfKaCQ2dcJhqx1qvycsG65Rdd3wNeUbzKUDHsPxDinMvjIX80up2DBDe+3+zXkRl0JZqK0y2jBL0emW9tp6EGp24qkZGcdyG2TlLb9fRB4BAIIi/Dbs3xoB5nfQAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/5WOMQrCQBREv4npPEYqi20sLUSsBAVBK0nzsyxhZbMb/341Cnoiz+ItLCy8grpRLC0cGJiZ4jHnOySeoO+oECvc6rLcMOZGCemMUZK1s154RRqNPmBTxYKUmmjPU6xm5Or9s9Ej7XUAaoLBH6Rh7plQ8i9atWsDxLfwrxuIKAzaQsyZtC3SEXo1tl5Zr1lv1ciVFRKyo12cHbPZ9RJ9ARAxtPCT1nCCJNQ8ePmeWiHJ+gXFM1JMBwEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAA/5XOMQrCQBCF4YnRzmOkstjG0kJiKkEhoGWaSVjCymY3zI5JFLyRZ/EWFhZeQd0ELC2c6p/m412fMHEEC0ulOGCjqurImGspCqu1LFhZ44STpFCrM/av2JOUG+V4i3VKtju9+3tF8ylAR7D8Q4pzx4QF/9LqdgwQPvy+mRdRaDSl2DEpU0YJOrk2ThqnWDUysVWNhGypDbNLlt5voy8AI4YgHgp85UMFvlbdB6lakSb+AAAA");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeListMap mapA = (TreeListMap)a;
        TreeListMap mapB = (TreeListMap)b;
        assertEquals(mapA.getComparator(), mapB.getComparator());
        TreeMapTest.extraSerializationChecks(mapA.getMap(), mapB.getMap());
    }
}
