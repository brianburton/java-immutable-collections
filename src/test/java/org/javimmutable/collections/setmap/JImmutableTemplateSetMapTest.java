///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ComparableComparator;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.JImmutableTreeMap;
import org.javimmutable.collections.tree.JImmutableTreeMapTest;
import org.javimmutable.collections.tree.JImmutableTreeSet;
import org.javimmutable.collections.tree.JImmutableTreeSetTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JImmutableTemplateSetMapTest
    extends AbstractJImmutableSetMapTestCase
{
    public void testVarious()
    {
        final Comparator<Integer> reverse = ComparableComparator.<Integer>of().reversed();
        final JImmutableTreeMap<Integer, JImmutableSet<Integer>> emptyMap = JImmutableTreeMap.of();
        final JImmutableTreeSet<Integer> emptySet = JImmutableTreeSet.of(reverse);
        final JImmutableSetMap<Integer, Integer> empty = JImmutableTemplateSetMap.of(emptyMap.assign(1, emptySet.insert(10)),
                                                                                     emptySet.insert(8).insert(25));
        assertEquals(true, empty.isEmpty());
        assertEquals(0, empty.count());
        assertEquals(0, empty.keys().count());
        assertNull(empty.get(1));
        JImmutableSetMap<Integer, Integer> map = verifyOperations(empty, Ordering.REVERSED);
        verifyRandom(JImmutableTreeSetMap.of(), new TreeMap<>());
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(MapEntry.of(1, map.getSet(1)),
                                                             MapEntry.of(2, map.getSet(2)),
                                                             MapEntry.of(3, map.getSet(3))),
                                               map.iterator());

        map = empty
            .insert(10, 100)
            .insert(10, 111)
            .insert(7, 5)
            .insert(3, 8)
            .insert(7, 12)
            .insert(3, 90);
        assertEquals(Arrays.asList(3, 7, 10), map.keys().stream().collect(toList()));
        assertEquals(Arrays.asList(90, 8), map.getSet(3).stream().collect(toList()));
        assertEquals(Arrays.asList(12, 5), map.getSet(7).stream().collect(toList()));
        assertEquals(Arrays.asList(111, 100), map.getSet(10).stream().collect(toList()));
    }

    public void testEquals()
    {
        final JImmutableTreeMap<Integer, JImmutableSet<Integer>> emptyMap = JImmutableTreeMap.of();
        final JImmutableTreeSet<Integer> emptySet = JImmutableTreeSet.of();
        final JImmutableSetMap<Integer, Integer> empty = JImmutableTemplateSetMap.of(emptyMap.assign(1, emptySet.insert(10)),
                                                                                     emptySet.insert(8).insert(25));
        JImmutableSetMap<Integer, Integer> a = empty;
        JImmutableSetMap<Integer, Integer> b = empty;
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
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableTemplateSetMap)a).iterator();
        JImmutableSetMap<String, String> empty = JImmutableTemplateSetMap.of(JImmutableTreeMap.<String, JImmutableSet<String>>of(), JImmutableTreeSet.of());
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTemplateSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerGV6zS4Bu0nPOzy1ILALJQVkl+UUww5hghgFpinwODFSq+xybmTCfFzLUMbBBHQ6lAIrlf6p0AgAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTemplateSetMapTest::extraSerializationChecks, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerGV6zS4Bu0nPOzy1ILALJQVkl+UUww5hghgFpinwODFSq+xybmTCfFzLUMbBBHQ6mGEsYGB3hrMQKAL1djtGCAgAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTemplateSetMapTest::extraSerializationChecks, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerGV6zS4Bu0nPOzy1ILALJQVkl+UUww5hghgFpinwODFSq+xybmTCfFzLUMbBBHQ6mmEsYGB3BLEYgKxGIo+C8ZJBqXjg3qQIAfGkuEJ8CAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerGV6zS4Bu0nPOzy1ILALJQVkl+UUww5hghgFpinwODFSq+xybmTCfFzLUMbBBHQ6mmEoYGB0hAkBWEpjFCGQ5VQAA9oB1u4wCAAA=");

        empty = JImmutableTemplateSetMap.of(JImmutableTreeMap.of(String.CASE_INSENSITIVE_ORDER), JImmutableTreeSet.of(String.CASE_INSENSITIVE_ORDER));
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTemplateSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerFtDcRL2cxLx0veCSosy8dBXnxOJUz7zi1LzizJLMslTn/NyCxKLEkvyicuaY2piAp+eYYAYAaYp8CwxIqvsWm5lQ3xYy1DGwQ90NpQAsnvP+ZwIAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTemplateSetMapTest::extraSerializationChecks, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerFtDcRL2cxLx0veCSosy8dBXnxOJUz7zi1LzizJLMslTn/NyCxKLEkvyicuaY2piAp+eYYAYAaYp8CwxIqvsWm5lQ3xYy1DGwQ90NphhLGBgd4azECgDRzsyXdQIAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTemplateSetMapTest::extraSerializationChecks, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerFtDcRL2cxLx0veCSosy8dBXnxOJUz7zi1LzizJLMslTn/NyCxKLEkvyicuaY2piAp+eYYAYAaYp8CwxIqvsWm5lQ3xYy1DGwQ90NpphKGBgTISyQHDeQmwTEUWAhRiAruQIAxZwVLIgCAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCknNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIM9YxqbikKDG5BGE8DmMLylkYGJhfAl3tSJ6ri1JTsTvXlSLn4nerFtDcRL2cxLx0veCSosy8dBXnxOJUz7zi1LzizJLMslTn/NyCxKLEkvyicuaY2piAp+eYYAYAaYp8CwxIqvsWm5lQ3xYy1DGwQ90NpphKGBgdIQJAVhKYxQhkOVUAACCf1c9/AgAA");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        JImmutableTemplateSetMap mapA = (JImmutableTemplateSetMap)a;
        JImmutableTemplateSetMap mapB = (JImmutableTemplateSetMap)b;
        assertEquals(mapA.getEmptyMap().getClass(), mapB.getEmptyMap().getClass());
        if (mapA.getEmptyMap() instanceof JImmutableTreeMap) {
            JImmutableTreeMapTest.extraSerializationChecks(mapA.getEmptyMap(), mapB.getEmptyMap());
        }
        assertEquals(mapA.getEmptySet().getClass(), mapB.getEmptySet().getClass());
        if (mapA.getEmptySet() instanceof JImmutableTreeSet) {
            JImmutableTreeSetTest.extraSerializationChecks(mapA.getEmptySet(), mapB.getEmptySet());
        }
    }
}
