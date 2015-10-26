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

package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.StandardJImmutableSetTests;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class JImmutableTreeSetTest
        extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableSetTests.verifySet(JImmutableTreeSet.<Integer>of());
        StandardCursorTest.emptyCursorTest(JImmutableTreeSet.<Integer>of().cursor());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), JImmutableTreeSet.<Integer>of().union(Arrays.asList(1, 2, 3)).cursor());
    }

    @SuppressWarnings("OverlyLongMethod")
    public void test()
    {
        Cursorable<String> expected = IterableCursorable.of(Arrays.asList("fred", "wilma", "betty", "barney"));

        JImmutableSet<String> set = JImmutableTreeSet.of();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertEquals(false, set.contains("fred"));
        assertEquals(false, set.contains("wilma"));
        assertEquals(false, set.contains("betty"));
        assertEquals(false, set.contains("barney"));
        assertEquals(false, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));
        StandardCursorTest.emptyCursorTest(set.cursor());

        set = set.insert("FRED".toLowerCase());
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertEquals(true, set.contains("fred"));
        assertEquals(false, set.contains("wilma"));
        assertEquals(false, set.contains("betty"));
        assertEquals(false, set.contains("barney"));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));
        StandardCursorTest.listCursorTest(Arrays.asList("fred"), set.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("fred"), set.iterator());

        set = set.insert("WILMA".toLowerCase());
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertEquals(true, set.contains("fred"));
        assertEquals(true, set.contains("wilma"));
        assertEquals(false, set.contains("betty"));
        assertEquals(false, set.contains("barney"));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));
        StandardCursorTest.listCursorTest(Arrays.asList("fred", "wilma"), set.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("fred", "wilma"), set.iterator());

        assertSame(set, set.insert("fred"));
        assertSame(set, set.insert("wilma"));
        StandardCursorTest.listCursorTest(Arrays.asList("fred", "wilma"), set.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("fred", "wilma"), set.iterator());

        JImmutableSet<String> set2 = set.union(expected);
        assertFalse(set2.isEmpty());
        assertEquals(4, set2.size());
        assertEquals(true, set2.contains("fred"));
        assertEquals(true, set2.contains("wilma"));
        assertEquals(true, set2.contains("betty"));
        assertEquals(true, set2.contains("barney"));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(true, set2.containsAll(expected));
        assertEquals(new TreeSet<String>(Arrays.asList("fred", "wilma", "betty", "barney")), set2.getSet());
        StandardCursorTest.listCursorTest(Arrays.asList("barney", "betty", "fred", "wilma"), set2.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("barney", "betty", "fred", "wilma"), set2.iterator());

        assertEquals(set, set.intersection(set2));
        assertEquals(set, set2.intersection(set));
        assertEquals(set, set2.delete("betty").delete("barney"));
        StandardCursorTest.listCursorTest(Arrays.asList("barney", "betty", "fred", "wilma"), set2.cursor());
        StandardCursorTest.listCursorTest(Arrays.asList("fred", "wilma"), set2.delete("betty").delete("barney").cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("fred", "wilma"), set2.delete("betty").delete("barney").iterator());

        set2 = set2.deleteAll(set);
        assertFalse(set2.isEmpty());
        assertEquals(2, set2.size());
        assertEquals(false, set2.contains("fred"));
        assertEquals(false, set2.contains("wilma"));
        assertEquals(true, set2.contains("betty"));
        assertEquals(true, set2.contains("barney"));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(false, set2.containsAny(set));
        assertEquals(false, set2.containsAll(expected));
        StandardCursorTest.listCursorTest(Arrays.asList("barney", "betty"), set2.cursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("barney", "betty"), set2.iterator());

        JImmutableSet<String> set3 = set.union(expected).insert("homer").insert("marge");
        assertFalse(set3.isEmpty());
        assertEquals(6, set3.size());
        assertEquals(true, set3.contains("fred"));
        assertEquals(true, set3.contains("wilma"));
        assertEquals(true, set3.contains("betty"));
        assertEquals(true, set3.contains("barney"));
        assertEquals(true, set3.contains("homer"));
        assertEquals(true, set3.contains("marge"));
        assertEquals(true, set3.containsAny(expected));
        assertEquals(true, set3.containsAny(set));
        assertEquals(true, set3.containsAny(set2));
        assertEquals(true, set3.containsAll(expected));
        assertEquals(true, set3.containsAll(set));
        assertEquals(true, set3.containsAll(set2));
        assertEquals(new TreeSet<String>(Arrays.asList("fred", "wilma", "betty", "barney", "homer", "marge")), set3.getSet());
        assertEquals(set, set3.intersection(set));
        assertEquals(set2, set3.intersection(set2));
        assertEquals(set, set.intersection(set));
        assertEquals(set, set.intersection(set3));
        assertEquals(JImmutableTreeSet.<String>of(), set.intersection(set2));
        assertEquals(JImmutableTreeSet.<String>of(), set2.intersection(set));
        assertEquals(JImmutableTreeSet.<String>of(), set3.deleteAll(set3));
    }

    public void testRandom()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            Set<Integer> expected = new TreeSet<Integer>();
            JImmutableSet<Integer> set = JImmutableTreeSet.of();
            for (int loops = 0; loops < (4 * size); ++loops) {
                int command = random.nextInt(4);
                int value = random.nextInt(size);
                switch (command) {
                case 0:
                case 1:
                    set = set.insert(value);
                    expected.add(value);
                    assertEquals(true, set.contains(value));
                    break;
                case 2:
                    assertEquals(expected.contains(value), set.contains(value));
                    break;
                case 3:
                    set = set.delete(value);
                    expected.remove(value);
                    assertEquals(false, set.contains(value));
                    break;
                }
                assertEquals(expected.size(), set.size());
            }
            assertEquals(expected, set.getSet());

            // verify ordering is the same in both sets
            assertEquals(new ArrayList<Integer>(expected), new ArrayList<Integer>(set.getSet()));

            // verify value identity
            for (Integer value : set) {
                assertSame(set, set.insert(value));
            }

            for (Integer value : set) {
                set = set.delete(value);
            }
            assertEquals(0, set.size());
            assertEquals(true, set.isEmpty());
        }
    }

    public void testSortOrder()
    {
        Comparator<Integer> reverser = new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return -a.compareTo(b);
            }
        };

        Set<Integer> expected = new TreeSet<Integer>(reverser);
        JImmutableSet<Integer> set = JImmutableTreeSet.of(reverser);
        Random random = new Random(2500L);
        for (int i = 0; i < 10000; ++i) {
            int value = random.nextInt(100000) - 50000;
            expected.add(value);
            set = set.insert(value);
        }
        assertEquals(expected, set.getSet());
        assertEquals(new ArrayList<Integer>(expected), new ArrayList<Integer>(set.getSet()));
        StandardCursorTest.listCursorTest(new ArrayList<Integer>(expected), set.cursor());
        StandardCursorTest.listIteratorTest(new ArrayList<Integer>(expected), set.iterator());
    }

    public void testDeleteAll()
    {
        JImmutableTreeSet<Integer> map = JImmutableTreeSet.of();
        map = (JImmutableTreeSet<Integer>)map.insert(1).insert(3);
        JImmutableTreeSet<Integer> cleared = map.deleteAll();
        assertNotSame(JImmutableTreeSet.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        StandardCursorTest.emptyCursorTest(cleared.cursor());

        map = JImmutableTreeSet.of(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return -b.compareTo(a);
            }
        });
        map = (JImmutableTreeSet<Integer>)map.insert(1).insert(3);
        cleared = map.deleteAll();
        assertNotSame(JImmutableTreeSet.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        StandardCursorTest.emptyCursorTest(cleared.cursor());
    }
}
