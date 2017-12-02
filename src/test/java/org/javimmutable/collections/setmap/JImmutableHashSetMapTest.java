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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import static java.util.Arrays.asList;

public class JImmutableHashSetMapTest
    extends AbstractJImmutableSetMapTestCase
{
    @SuppressWarnings("unchecked")
    public void test()
    {
        JImmutableSetMap<Integer, Integer> map = verifyOperations(JImmutableHashSetMap.of(), Ordering.HASH);
        verifyRandom(JImmutableHashSetMap.of(), new HashMap<>());
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

    public void testEquals()
    {
        JImmutableSetMap<Integer, Integer> a = JImmutableHashSetMap.of();
        JImmutableSetMap<Integer, Integer> b = JImmutableHashSetMap.of();
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
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableHashSetMap)a).iterator();
        JImmutableSetMap<String, String> empty = JImmutableHashSetMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8kgszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKORgYmF8yAEEFAFu31gu7AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insert(MapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8kgszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKORgYmF8yMDAwljAwOpazQFmJFQCP8YNUyQAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insertAll(asList(MapEntry.of("A", "a"), MapEntry.of("a", "b"), MapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBJb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8kgszghOLfFNLAgoyq+o/A8C/1SMeRgYKooYPEgw0jGpuKQoMbkEYTQOYwvKORgYmF8yAIkSBkbHchYgixHISixkqGNggXOTgDgKzkuuAADcYrsE5gAAAA==");
    }
}
