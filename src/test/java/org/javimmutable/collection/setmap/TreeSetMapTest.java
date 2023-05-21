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

package org.javimmutable.collection.setmap;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.ISetMap;
import org.javimmutable.collection.common.StandardSerializableTests;
import org.javimmutable.collection.iterators.StandardIteratorTests;
import org.javimmutable.collection.tree.TreeMapTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

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
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5SwMDMwvgW4zxWdmCdA1es75uQWJRSApKKskvwhmFhPMLCANAJtapsrtAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5SwMDMwvgW4zxWdmCdA1es75uQWJRSApKKskvwhmFhPMLAbGEgZGRzgrsQIA7wSRO/sAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5SwMDMwvgW4zxWdmCdA1es75uQWJRSApKKskvwhmFhPMLAbmEgZGRzCLEchKBOIoOC+5kKGOgQ3OTaoAAJINejoYAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5SwMDMwvgW4zxWdmCdA1es75uQWJRSApKKskvwhmFhPMLAamEgZGRzCLAchKArMYgSynCgBw+y7aBQEAAA==");

        empty = TreeSetMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5SwMDMwvgW7TApqXqJeTmJeuF1xSlJmXruKcWJzqmVecmlecWZJZluqcn1uQWJRYkl9UzhxTGxPw9BwTzAAgDQBS8Hn+4gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTQ1OLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5SwMDMwvgW7TApqXqJeTmJeuF1xSlJmXruKcWJzqmVecmlecWZJZluqcn1uQWJRYkl9UzhxTGxPw9BwTzAAGxhIGRkc4K7ECAHt53cjwAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/42OMQrCQBREv0Y7j5HKYhsrCwuxshACsRKbn/AJK5vd+Pcbo6An8izewsLCK6irYik4MDAzxWNON+h6hqHjQq2w1mW5EcwMqdwZQ7loZ5Un1mj0Ht9tzkQpyQyrhF2ze7x0jwc9gIZh9D9nnHlhzOUHq9p2AKJr+NYPPFQGbaFSYW2LeIKeptaT9Vp0TRNXVsgojrfR8rBMLuf2FwBtgRZ+0hqO0A01C168p1ZIefMEP5eJHgMBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/43OMQrCQBCF4YnRzmOkstjGysJCU1kIAS3TTMIQVja7YXaMUfBGnsVbWFh4BXUNWApO9U/z8S4PGHmGmeNK7bDVdb0XLAyp0hlDpWhnlSfWaPQJ+2/LRBuSNTYZu+74+twzmY4BOob5/86i8MJYyg+rOQwB4nvYNgkeKoO2UhthbaskRU8r68l6Lbql1NUNMorjQ5yf8+x2HXwBGAhEi74gVNFXFGrZvQGHACY4+gAAAA==");
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
