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

import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.JImmutableTreeMap;
import org.javimmutable.collections.tree.JImmutableTreeSet;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

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
        JImmutableSetMap<Integer, Integer> map = verifyOperations(JImmutableTreeSetMap.of());
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
}
