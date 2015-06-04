///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JImmutableTreeSetMapTest
    extends AbstractJImmutableSetMapTestTestCase
{
    @SuppressWarnings("unchecked")
    public void testNormalOrder()
    {
        JImmutableSetMap<Integer, Integer> map = verifyOperations(JImmutableTreeSetMap.<Integer, Integer>of());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableSet<Integer>>>asList(MapEntry.of(1, map.getSet(1)),
                                                                                                               MapEntry.of(2, map.getSet(2)),
                                                                                                               MapEntry.of(3, map.getSet(3))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableSet<Integer>>>asList(MapEntry.of(1, map.getSet(1)),
                                                                                                                 MapEntry.of(2, map.getSet(2)),
                                                                                                                 MapEntry.of(3, map.getSet(3))),
                                            map.iterator());
    }

    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        JImmutableSetMap<Integer, Integer> map = verifyOperations(JImmutableTreeSetMap.<Integer, Integer>of(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return b.compareTo(a);
            }
        }));
        StandardCursorTest.listCursorTest(Arrays.asList(3, 2, 1), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableSet<Integer>>>asList(MapEntry.of(3, map.getSet(3)),
                                                                                                               MapEntry.of(2, map.getSet(2)),
                                                                                                               MapEntry.of(1, map.getSet(1))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableSet<Integer>>>asList(MapEntry.of(3, map.getSet(3)),
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

    public void testRandom()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);

            JImmutableSetMap<Integer, Integer> jetMap = JImmutableHashSetMap.of();
            HashMap<Integer, Set<Integer>> expected = new HashMap<Integer, Set<Integer>>();

            for (int loops = 0; loops < (4 * size); ++loops) {
                Integer key = random.nextInt(size);
                Set<Integer> set = new HashSet<Integer>();
                for (int n = random.nextInt(3); n > 0; --n) {
                    set.add(random.nextInt(size));
                }
                int command = random.nextInt(8);
                switch (command) {
                case 0:
                    jetMap = jetMap.insert(key, key);
                    if (expected.containsKey(key)) {
                        expected.get(key).add(key);
                    } else {
                        expected.put(key, asSet(key));
                    }
                    verifyExpected(expected, key, asSet(key));
                    break;
                case 1:
                    jetMap = jetMap.insertAll(key, set);
                    if (expected.containsKey(key)) {
                        expected.get(key).addAll(set);
                    } else {
                        expected.put(key, set);
                    }
                    verifyExpected(expected, key, set);
                    break;
                case 2:
                    jetMap = jetMap.delete(key);
                    if (expected.containsKey(key)) {
                        expected.remove(key);
                    }
                    break;
                case 3:
                    jetMap = jetMap.deleteAll(key, set);
                    if (expected.containsKey(key)) {
                        expected.get(key).removeAll(set);
                    }
                    break;
                case 4:
                    if (expected.containsKey(key)) {
                        assertEquals(jetMap.contains(key, key), expected.get(key).contains(key));
                    }
                    break;
                case 5:
                    if (expected.containsKey(key)) {
                        assertEquals(jetMap.containsAll(key, set), expected.get(key).containsAll(set));
                    }
                case 6:
                    jetMap = jetMap.union(key, set);
                    if (expected.containsKey(key)) {
                        expected.get(key).addAll(set);
                    } else {
                        expected.put(key, set);
                    }
                    verifyExpected(expected, key, set);
                    break;
                case 7:
                    jetMap = jetMap.intersection(key, set);
                    if (expected.containsKey(key)) {
                        expected.get(key).retainAll(set);
                    } else {
                        expected.put(key, new HashSet<Integer>());
                    }
                    break;
                }
                assertEquals(expected.size(), jetMap.size());
            }
            verifyContents(jetMap, expected);
            for (JImmutableMap.Entry<Integer, JImmutableSet<Integer>> e : jetMap) {
                jetMap = jetMap.delete(e.getKey());
            }
            assertEquals(0, jetMap.size());
            assertEquals(true, jetMap.isEmpty());

        }
    }

    private JImmutableTreeSetMap<Integer, Integer> remove(JImmutableSetMap<Integer, Integer> map,
                                                          Integer value)
    {
        map = map.delete(value);
        JImmutableTreeSetMap<Integer, Integer> treeMap = (JImmutableTreeSetMap<Integer, Integer>)map;
//        treeMap.verifyDepthsMatch();
        assertEquals(true, treeMap.find(value).isEmpty());
        return treeMap;
    }
}
