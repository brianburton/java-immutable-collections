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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import static java.util.Arrays.asList;

public class HashSetMapTest
    extends AbstractSetMapTestCase
{
    @SuppressWarnings("unchecked")
    public void test()
    {
        ISetMap<Integer, Integer> map = verifyOperations(HashSetMap.of(), Ordering.HASH);
        verifyRandom(HashSetMap.of(), new HashMap<>());
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getSet(1)),
                                                             IMapEntry.of(2, map.getSet(2)),
                                                             IMapEntry.of(3, map.getSet(3))),
                                               map.iterator());
    }

    public void testEquals()
    {
        ISetMap<Integer, Integer> a = HashSetMap.of();
        ISetMap<Integer, Integer> b = HashSetMap.of();
        assertEquals(a, b);
        assertEquals(b, a);

        a = a.insert(1, 10);
        assertFalse(a.equals(b));
        b = b.insert(1, 10).insert(1, 10);
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
        final Func1<Object, Iterator> iteratorFactory = a -> ((HashSetMap)a).iterator();
        ISetMap<String, String> empty = HashSetMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM80gszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5RwMDMwvGYCgAgBoXpNBpQAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM80gszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5RwMDMwvGRgYGEsYGB3LWaCsxAoAyuQfNrMAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM80gszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5RwMDMwvGYBECQOjYzkLkMUIZCUCcRScl1zIUMfAAucmVQAAAJi7zdAAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBMr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM80gszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYbIk3xzGpuKQoMbkEh1kF5RwMDMwvGRgYmEoYGB3LWYAsBiArCcxiBLKcKgAnRApuvQAAAA==");
    }
}
