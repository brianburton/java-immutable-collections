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

    public static void verifyMultiset(JImmutableMultiset<Integer> empty)
    {
        StandardCursorTest.emptyCursorTest(empty.cursor());
        StandardCursorTest.emptyCursorTest(empty.entryCursor());
        StandardCursorTest.emptyCursorTest(empty.occurrenceCursor());
        testVarious(empty);
        testRandom(empty);
        verifyContents(empty, new ArrayList<Integer>());

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

        jmet = jmet.setCount(10, 5);
        assertEquals(1, jmet.size());
        assertEquals(5, jmet.valueCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.contains(10, 5));
        jmet = jmet.setCount(10, 3);
        assertEquals(false, jmet.contains(10, 5));
        assertEquals(true, jmet.contains(10, 3));

        assertEquals(empty, jmet.delete(10));
        assertEquals(empty, jmet.deleteOccurrence(10, 3));
        assertEquals(empty, jmet.deleteOccurrence(10).deleteOccurrence(10).deleteOccurrence(10));
        assertEquals(empty, jmet.deleteAll());
        assertEquals(empty, jmet.setCount(10, 0));

        jmet = jmet.setCount(10, 5);
        assertEquals(true, jmet.contains(10, 5));

        verifyUnion(empty);
        verifyIntersection(empty);
        verifyInsertAll(empty);
        verifyDeleteAll(empty);
        verifyDeleteAllOccurrences(empty);
    }

    private static void verifyUnion(JImmutableMultiset<Integer> empty)
    {
        final List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final JImmutableMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<Integer>();
        List<Integer> expected = emptyList;
        List<Integer> setExpected = emptyList;

        //empty into empty
        verifyContents(empty.union(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.union(emptyList), expected);
        verifyContents(empty.union(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.union(emptyList.iterator()), expected);
        verifyContents(empty.union(asJMSet(emptyList)), expected);
        verifyContents(empty.union(asJSet(emptyList)), setExpected);
        verifyContents(empty.union(asSet(emptyList)), setExpected);

        //values into empty
        expected = values;
        setExpected = Arrays.asList(1, 3, 4);
        verifyContents(empty.union(IterableCursorable.of(values)), expected);
        verifyContents(empty.union(values), expected);
        verifyContents(empty.union(IterableCursor.of(values)), expected);
        verifyContents(empty.union(values.iterator()), expected);
        verifyContents(empty.union(asJMSet(values)), expected);
        verifyContents(empty.union(asJSet(values)), setExpected);
        verifyContents(empty.union(asSet(values)), setExpected);

        //empty into values
        setExpected = values;
        verifyContents(jmet.union(IterableCursorable.of(emptyList)), expected);
        verifyContents(jmet.union(emptyList), expected);
        verifyContents(jmet.union(IterableCursor.of(emptyList)), expected);
        verifyContents(jmet.union(emptyList.iterator()), expected);
        verifyContents(jmet.union(asJMSet(emptyList)), expected);
        verifyContents(jmet.union(asJSet(emptyList)), setExpected);
        verifyContents(jmet.union(asSet(emptyList)), setExpected);

        //values into values
        //with wider and deeper
        final List<Integer> wideDeep = Arrays.asList(1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 4);
        expected = wideDeep;
        setExpected = Arrays.asList(1, 2, 3, 3, 4);
        verifyContents(jmet.union(IterableCursorable.of(wideDeep)), expected);
        verifyContents(jmet.union(wideDeep), expected);
        verifyContents(jmet.union(IterableCursor.of(wideDeep)), expected);
        verifyContents(jmet.union(wideDeep.iterator()), expected);
        verifyContents(jmet.union(asJMSet(wideDeep)), expected);
        verifyContents(jmet.union(asJSet(wideDeep)), setExpected);
        verifyContents(jmet.union(asSet(wideDeep)), setExpected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(1, 2, 3, 4);
        expected = Arrays.asList(1, 2, 3, 3, 4);
        verifyContents(jmet.union(IterableCursorable.of(wideShallow)), expected);
        verifyContents(jmet.union(wideShallow), expected);
        verifyContents(jmet.union(IterableCursor.of(wideShallow)), expected);
        verifyContents(jmet.union(wideShallow.iterator()), expected);
        verifyContents(jmet.union(asJMSet(wideShallow)), expected);
        verifyContents(jmet.union(asJSet(wideShallow)), setExpected);
        verifyContents(jmet.union(asSet(wideShallow)), setExpected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(2, 2, 2, 4, 4, 4);
        expected = Arrays.asList(1, 2, 2, 2, 3, 3, 4, 4, 4);
        verifyContents(jmet.union(IterableCursorable.of(narrowDeep)), expected);
        verifyContents(jmet.union(narrowDeep), expected);
        verifyContents(jmet.union(IterableCursor.of(narrowDeep)), expected);
        verifyContents(jmet.union(narrowDeep.iterator()), expected);
        verifyContents(jmet.union(asJMSet(narrowDeep)), expected);
        verifyContents(jmet.union(asJSet(narrowDeep)), setExpected);
        verifyContents(jmet.union(asSet(narrowDeep)), setExpected);

        //with narrower shallower
        final List<Integer> narrowShallow = Arrays.asList(3, 4);
        expected = values;
        setExpected = Arrays.asList(1, 3, 3, 4);
        verifyContents(jmet.union(IterableCursorable.of(narrowShallow)), expected);
        verifyContents(jmet.union(narrowShallow), expected);
        verifyContents(jmet.union(IterableCursor.of(narrowShallow)), expected);
        verifyContents(jmet.union(narrowShallow.iterator()), expected);
        verifyContents(jmet.union(asJMSet(narrowShallow)), expected);
        verifyContents(jmet.union(asJSet(narrowShallow)), setExpected);
        verifyContents(jmet.union(asSet(narrowShallow)), setExpected);

    }

    private static void verifyIntersection(JImmutableMultiset<Integer> empty)
    {
        final List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final JImmutableMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<Integer>();
        List<Integer> expected = emptyList;
        List<Integer> setExpected = emptyList;

        //empty into empty
        verifyContents(empty.intersection(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.intersection(emptyList), expected);
        verifyContents(empty.intersection(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.intersection(emptyList.iterator()), expected);
        verifyContents(empty.intersection(asJMSet(emptyList)), expected);
        verifyContents(empty.intersection(asJSet(emptyList)), setExpected);
        verifyContents(empty.intersection(asSet(emptyList)), setExpected);

        //values into empty
        verifyContents(empty.intersection(IterableCursorable.of(values)), expected);
        verifyContents(empty.intersection(values), expected);
        verifyContents(empty.intersection(IterableCursor.of(values)), expected);
        verifyContents(empty.intersection(values.iterator()), expected);
        verifyContents(empty.intersection(asJMSet(values)), expected);
        verifyContents(empty.intersection(asJSet(values)), setExpected);
        verifyContents(empty.intersection(asSet(values)), setExpected);

        //empty into values
        verifyContents(jmet.intersection(IterableCursorable.of(emptyList)), expected);
        verifyContents(jmet.intersection(emptyList), expected);
        verifyContents(jmet.intersection(IterableCursor.of(emptyList)), expected);
        verifyContents(jmet.intersection(emptyList.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(emptyList)), expected);
        verifyContents(jmet.intersection(asJSet(emptyList)), setExpected);
        verifyContents(jmet.intersection(asSet(emptyList)), setExpected);

        //values into values
        //with wider and deeper
        final List<Integer> wideDeep = Arrays.asList(1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 4);
        expected = values;
        setExpected = Arrays.asList(1, 3, 4);
        verifyContents(jmet.intersection(IterableCursorable.of(wideDeep)), expected);
        verifyContents(jmet.intersection(wideDeep), expected);
        verifyContents(jmet.intersection(IterableCursor.of(wideDeep)), expected);
        verifyContents(jmet.intersection(wideDeep.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(wideDeep)), expected);
        verifyContents(jmet.intersection(asJSet(wideDeep)), setExpected);
        verifyContents(jmet.intersection(asSet(wideDeep)), setExpected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(1, 2, 3, 4);
        expected = Arrays.asList(1, 3, 4);
        verifyContents(jmet.intersection(IterableCursorable.of(wideShallow)), expected);
        verifyContents(jmet.intersection(wideShallow), expected);
        verifyContents(jmet.intersection(IterableCursor.of(wideShallow)), expected);
        verifyContents(jmet.intersection(wideShallow.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(wideShallow)), expected);
        verifyContents(jmet.intersection(asJSet(wideShallow)), setExpected);
        verifyContents(jmet.intersection(asSet(wideShallow)), setExpected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(2, 2, 2, 4, 4, 4);
        expected = Arrays.asList(4);
        setExpected = expected;
        verifyContents(jmet.intersection(IterableCursorable.of(narrowDeep)), expected);
        verifyContents(jmet.intersection(narrowDeep), expected);
        verifyContents(jmet.intersection(IterableCursor.of(narrowDeep)), expected);
        verifyContents(jmet.intersection(narrowDeep.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(narrowDeep)), expected);
        verifyContents(jmet.intersection(asJSet(narrowDeep)), setExpected);
        verifyContents(jmet.intersection(asSet(narrowDeep)), setExpected);

        //with narrower shallower
        final List<Integer> narrowShallow = Arrays.asList(3, 4);
        expected = Arrays.asList(3, 4);
        setExpected = expected;
        verifyContents(jmet.intersection(IterableCursorable.of(narrowShallow)), expected);
        verifyContents(jmet.intersection(narrowShallow), expected);
        verifyContents(jmet.intersection(IterableCursor.of(narrowShallow)), expected);
        verifyContents(jmet.intersection(narrowShallow.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(narrowShallow)), expected);
        verifyContents(jmet.intersection(asJSet(narrowShallow)), setExpected);
        verifyContents(jmet.intersection(asSet(narrowShallow)), setExpected);

    }

    private static void verifyInsertAll(JImmutableMultiset<Integer> empty)
    {
        final List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final JImmutableMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<Integer>();
        List<Integer> expected = emptyList;

        //empty into empty
        verifyContents(empty.insertAll(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.insertAll(emptyList), expected);
        verifyContents(empty.insertAll(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.insertAll(emptyList.iterator()), expected);
        verifyContents(empty.insertAll(asJMSet(emptyList)), expected);

        //values into empty
        expected = values;
        verifyContents(empty.insertAll(IterableCursorable.of(values)), expected);
        verifyContents(empty.insertAll(values), expected);
        verifyContents(empty.insertAll(IterableCursor.of(values)), expected);
        verifyContents(empty.insertAll(values.iterator()), expected);
        verifyContents(empty.insertAll(asJMSet(values)), expected);

        //empty into values
        verifyContents(jmet.insertAll(IterableCursorable.of(emptyList)), expected);
        verifyContents(jmet.insertAll(emptyList), expected);
        verifyContents(jmet.insertAll(IterableCursor.of(emptyList)), expected);
        verifyContents(jmet.insertAll(emptyList.iterator()), expected);
        verifyContents(jmet.insertAll(asJMSet(emptyList)), expected);

        //values into values
    }

    private static void verifyDeleteAll(JImmutableMultiset<Integer> empty)
    {
        //empty from empty

        //values from empty

        //empty from values

        //values from values
    }

    private static void verifyDeleteAllOccurrences(JImmutableMultiset<Integer> empty)
    {
        //empty from empty

        //values from empty

        //empty from values

        //values from values
    }

    private static void testVarious(JImmutableMultiset<Integer> empty)
    {

    }

    private static void testRandom(JImmutableMultiset<Integer> empty)
    {

    }

    private static void verifyContents(JImmutableMultiset<Integer> jmet,
                                       List<Integer> expected)
    {
        assertEquals(expected.isEmpty(), jmet.isEmpty());
        assertEquals(expected.size(), jmet.valueCount());
        assertEquals(asSet(expected).size(), jmet.size());
        assertEquals(asSet(expected), jmet.getSet());

        for (Cursor<JImmutableMap.Entry<Integer, Integer>> e = jmet.entryCursor().start(); e.hasValue(); e = e.next()) {
            List<Integer> eList = entryAsList(e.getValue());
            Integer value = eList.get(0);
            assertEquals(true, expected.containsAll(eList));
            assertEquals(true, jmet.contains(value));
            assertEquals(true, jmet.contains(value, eList.size()));
            assertEquals(true, jmet.containsAll(eList));
            assertEquals(eList.size(), jmet.count(value));
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

    private static void verifyCursor(JImmutableMultiset<Integer> jmet,
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
