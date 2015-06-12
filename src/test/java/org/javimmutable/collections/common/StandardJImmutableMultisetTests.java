///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.common;


import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.IterableCursor;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class StandardJImmutableMultisetTests
{
    public static void cursorTest(JImmutableMultiset<Integer> empty)
    {
        StandardCursorTest.emptyCursorTest(empty.cursor());
        StandardCursorTest.emptyCursorTest(empty.entryCursor());
        StandardCursorTest.emptyCursorTest(empty.occurrenceCursor());
        JImmutableMultiset<Integer> jmet = empty.insert(1).insert(1).insert(2).insert(3).insert(3);

        final List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        entries.add(new MapEntry<Integer, Integer>(1, 2));
        entries.add(new MapEntry<Integer, Integer>(2, 1));
        entries.add(new MapEntry<Integer, Integer>(3, 2));

        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), jmet.cursor());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 1, 2, 3, 3), jmet.occurrenceCursor());
        StandardCursorTest.listIteratorTest(Arrays.asList(1, 2, 3), jmet.iterator());

        StandardCursorTest.listCursorTest(entries, jmet.entryCursor());

        verifyCursor(jmet.insert(5), Arrays.asList(1, 1, 2, 3, 3, 5));

    }

    public static void verifyCursor(JImmutableMultiset<Integer> jmet,
                                    final List<Integer> expected)
    {
        List<Integer> setValues = new ArrayList<Integer>();
        setValues.addAll(asSet(expected));

        final List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        JImmutableMap<Integer, Integer> expectedMap = JImmutableHashMap.<Integer, Integer>of();
        for (int value : expected) {
            Holder<Integer> holder = expectedMap.find(value);
            int count = holder.getValueOr(0);
            expectedMap = expectedMap.assign(value, count + 1);
        }
        for (JImmutableMap.Entry<Integer, Integer> entry : expectedMap) {
            entries.add(entry);
        }
        assertEquals(expectedMap.size(), entries.size());
        assertEquals(entries.size(), jmet.size());

        StandardCursorTest.listCursorTest(setValues, jmet.cursor());
        StandardCursorTest.listIteratorTest(setValues, jmet.iterator());
        StandardCursorTest.cursorTest(new Func1<Integer, Integer>()
        {
            @Override
            public Integer apply(Integer value)
            {
                return entries.get(value).getKey();
            }
        }, entries.size(), jmet.cursor());
        StandardCursorTest.iteratorTest(new Func1<Integer, Integer>()
        {
            @Override
            public Integer apply(Integer value)
            {
                return entries.get(value).getKey();
            }
        }, entries.size(), jmet.iterator());


        StandardCursorTest.listCursorTest(expected, jmet.occurrenceCursor());
        StandardCursorTest.listIteratorTest(expected, jmet.occurrenceCursor().iterator());
        StandardCursorTest.cursorTest(new Func1<Integer, Integer>()
        {
            @Override
            public Integer apply(Integer value)
            {
                return expected.get(value);
            }
        }, expected.size(), jmet.occurrenceCursor());
        StandardCursorTest.iteratorTest(new Func1<Integer, Integer>()
        {
            @Override
            public Integer apply(Integer value)
            {
                return expected.get(value);
            }
        }, expected.size(), jmet.occurrenceCursor().iterator());

        StandardCursorTest.listCursorTest(entries, jmet.entryCursor());
        StandardCursorTest.listIteratorTest(entries, jmet.entryCursor().iterator());
        StandardCursorTest.cursorTest(new Func1<Integer, JImmutableMap.Entry<Integer, Integer>>()
        {
            @Override
            public JImmutableMap.Entry<Integer, Integer> apply(Integer value)
            {
                return entries.get(value);
            }
        }, entries.size(), jmet.entryCursor());
        StandardCursorTest.iteratorTest(new Func1<Integer, JImmutableMap.Entry<Integer, Integer>>()
        {
            @Override
            public JImmutableMap.Entry<Integer, Integer> apply(Integer value)
            {
                return entries.get(value);
            }
        }, entries.size(), jmet.entryCursor().iterator());

    }

    //based on StandardJImmutableSetTests, modified for multisets
    public static void verifyMultiset(JImmutableMultiset<Integer> empty)
    {
        StandardCursorTest.emptyCursorTest(empty.cursor());
        StandardCursorTest.emptyCursorTest(empty.entryCursor());
        StandardCursorTest.emptyCursorTest(empty.occurrenceCursor());
        testVarious(empty);
        testRandom(empty);

        assertEquals(0, empty.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(empty, new HashSet<Integer>());
        assertEquals(empty.getSet(), new HashSet<Integer>());

        JImmutableMultiset<Integer> jmet = empty;
        assertEquals(false, jmet.contains(10));
        jmet = jmet.insert(10);
        assertEquals(true, jmet != empty);
        assertEquals(1, jmet.size());
        assertEquals(1, jmet.valueCount());
        assertEquals(false, jmet.isEmpty());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.contains(10, 1));
        assertEquals(false, jmet.contains(10, 2));

        jmet = jmet.delete(10);
        assertEquals(0, jmet.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(false, jmet.contains(10));

        jmet = jmet.insert(10, 3);
        assertEquals(1, jmet.size());
        assertEquals(3, jmet.valueCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.contains(10, 3));
        assertEquals(false, jmet.contains(10, 4));
        assertEquals(false, jmet.isEmpty());

        assertEquals(empty, jmet.delete(10));
        assertEquals(empty, jmet.deleteOccurrence(10, 3));
        assertEquals(empty, jmet.deleteOccurrence(10).deleteOccurrence(10).deleteOccurrence(10));
        assertEquals(empty, jmet.deleteAll());

        verifyUnion(empty);

        jmet = empty;
        final List<Integer> values = Arrays.asList(1, 2, 3, 4);
        verifyContents(jmet.union(IterableCursorable.of(values)), values);
        verifyContents(jmet.union(values), values);
        verifyContents(jmet.union(IterableCursor.of(values)), values);
        verifyContents(jmet.union(values.iterator()), values);

        final List<Integer> withDuplicates = Arrays.asList(1, 1, 2, 3, 3, 4);
        verifyContents(jmet.union(IterableCursorable.of(withDuplicates)), withDuplicates);
        verifyContents(jmet.union(withDuplicates), withDuplicates);
        verifyContents(jmet.union(IterableCursor.of(withDuplicates)), withDuplicates);
        verifyContents(jmet.union(withDuplicates.iterator()), withDuplicates);


    }

    public static void verifyUnion(JImmutableMultiset<Integer> empty)
    {
        JImmutableMultiset<Integer> jmet = empty;
        final List<Integer> values = Arrays.asList(1, 1, 1, 2, 3, 3, 4);
        List<Integer> expected = values;

        //values into empty
        verifyContents(jmet.union(IterableCursorable.of(values)), expected);
        verifyContents(jmet.union(values), expected);
        verifyContents(jmet.union(IterableCursor.of(values)), expected);
        verifyContents(jmet.union(values.iterator()), expected);
        verifyContents(jmet.union(asJMSet(values)), expected);
        verifyContents(jmet.union(asJSet(values)), expected);
        verifyContents(jmet.union(asSet(values)), expected);

        //empty into empty
        final List<Integer> emptyList = new ArrayList<Integer>();
        expected = emptyList;
        verifyContents(jmet.union(IterableCursorable.of(emptyList)), expected);
        verifyContents(jmet.union(emptyList), expected);
        verifyContents(jmet.union(IterableCursor.of(emptyList)), expected);
        verifyContents(jmet.union(emptyList.iterator()), expected);
        verifyContents(jmet.union(asJMSet(emptyList)), expected);
        verifyContents(jmet.union(asJSet(emptyList)), expected);
        verifyContents(jmet.union(asSet(emptyList)), expected);


    }

    public static void testVarious(JImmutableMultiset<Integer> empty)
    {

    }

    public static void testRandom(JImmutableMultiset<Integer> empty)
    {

    }

    private static void verifyContents(JImmutableMultiset<Integer> jmet,
                                       List<Integer> expected)
    {
        assertEquals(expected.isEmpty(), jmet.isEmpty());
        assertEquals(expected.size(), jmet.valueCount());
        assertEquals(asSet(expected).size(), jmet.size());

        for (Cursor<JImmutableMap.Entry<Integer, Integer>> e = jmet.entryCursor().start(); e.hasValue(); e = e.next()) {
            List<Integer> eList = entryAsList(e.getValue());
            assertEquals(true, expected.containsAll(eList));
            assertEquals(true, jmet.contains(eList.get(0)));
            assertEquals(true, jmet.contains(eList.get(0), eList.size()));
            assertEquals(true, jmet.containsAll(eList));
        }

        assertEquals(true, jmet.containsAll(IterableCursorable.of(expected)));
        assertEquals(true, jmet.containsAll(expected));
        assertEquals(true, jmet.containsAll(IterableCursor.of(expected)));
        assertEquals(true, jmet.containsAll(expected.iterator()));

        assertEquals(true, jmet.containsAllOccurrences(IterableCursorable.of(expected)));
        assertEquals(true, jmet.containsAllOccurrences(expected));
        assertEquals(true, jmet.containsAllOccurrences(IterableCursor.of(expected)));
        assertEquals(true, jmet.containsAllOccurrences(expected.iterator()));
        assertEquals(true, jmet.containsAllOccurrences(asJMSet(expected)));
        assertEquals(true, jmet.containsAllOccurrences(asJSet(expected)));
        assertEquals(true, jmet.containsAllOccurrences(asSet(expected)));

        assertEquals(!expected.isEmpty(), jmet.containsAny(IterableCursorable.of(expected)));
        assertEquals(!expected.isEmpty(), jmet.containsAny(expected));
        assertEquals(!expected.isEmpty(), jmet.containsAny(IterableCursor.of(expected)));
        assertEquals(!expected.isEmpty(), jmet.containsAny(expected.iterator()));

        if (!expected.isEmpty()) {
            List<Integer> subset = Arrays.asList(expected.get(0));
            assertEquals(true, jmet.containsAll(IterableCursorable.of(subset)));
            assertEquals(true, jmet.containsAll(subset));
            assertEquals(true, jmet.containsAll(IterableCursor.of(subset)));
            assertEquals(true, jmet.containsAll(subset.iterator()));

            assertEquals(true, jmet.containsAllOccurrences(IterableCursorable.of(subset)));
            assertEquals(true, jmet.containsAllOccurrences(subset));
            assertEquals(true, jmet.containsAllOccurrences(IterableCursor.of(subset)));
            assertEquals(true, jmet.containsAllOccurrences(subset.iterator()));
            assertEquals(true, jmet.containsAllOccurrences(asJMSet(subset)));
            assertEquals(true, jmet.containsAllOccurrences(asJSet(subset)));
            assertEquals(true, jmet.containsAllOccurrences(asSet(subset)));

            assertEquals(true, jmet.containsAny(IterableCursorable.of(subset)));
            assertEquals(true, jmet.containsAny(subset));
            assertEquals(true, jmet.containsAny(IterableCursor.of(subset)));
            assertEquals(true, jmet.containsAny(subset.iterator()));
        }

    }

    private static Set<Integer> asSet(List<Integer> list)
    {
        Set<Integer> set = new HashSet<Integer>();
        set.addAll(list);
        return set;
    }

    private static JImmutableSet<Integer> asJSet(List<Integer> list)
    {
        JImmutableSet<Integer> jet = JImmutableHashSet.<Integer>of();
        for (int i : list) {
            jet = jet.insert(i);
        }
        return jet;
    }

    private static JImmutableMultiset<Integer> asJMSet(List<Integer> list)
    {
        JImmutableMultiset<Integer> jmet = JImmutableHashMultiset.<Integer>of();
        for (int i : list) {
            jmet = jmet.insert(i);
        }
        return jmet;
    }

    private static List<Integer> entryAsList(JImmutableMap.Entry<Integer, Integer> entry)
    {
        List<Integer> list = new ArrayList<Integer>();
        int count = entry.getValue();
        int value = entry.getKey();
        for (int i = 0; i < count; i++) {
            list.add(value);
        }
        return list;
    }

}
