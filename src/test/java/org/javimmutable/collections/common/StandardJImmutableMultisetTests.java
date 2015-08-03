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


import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.IterableCursor;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.hash.JImmutableHashSet;

import java.util.*;

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

        verifyContents(empty, HashMultiset.<Integer>create());

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
        assertEquals(true, jmet.containsAtLeast(10, 1));
        assertEquals(false, jmet.containsAtLeast(10, 2));

        jmet = jmet.delete(10);
        assertEquals(0, jmet.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(false, jmet.contains(10));

        jmet = jmet.insert(10, 3);
        assertEquals(1, jmet.size());
        assertEquals(3, jmet.valueCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.containsAtLeast(10, 3));
        assertEquals(false, jmet.containsAtLeast(10, 4));
        assertEquals(false, jmet.isEmpty());

        jmet = jmet.setCount(10, 5);
        assertEquals(1, jmet.size());
        assertEquals(5, jmet.valueCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.containsAtLeast(10, 5));
        jmet = jmet.setCount(10, 3);
        assertEquals(false, jmet.containsAtLeast(10, 5));
        assertEquals(true, jmet.containsAtLeast(10, 3));

        assertEquals(empty, jmet.delete(10));
        assertEquals(empty, jmet.deleteOccurrence(10, 3));
        assertEquals(empty, jmet.deleteOccurrence(10).deleteOccurrence(10).deleteOccurrence(10));
        assertEquals(empty, jmet.deleteAll());
        assertEquals(empty, jmet.setCount(10, 0));

        jmet = jmet.setCount(10, 5);
        assertEquals(true, jmet.containsAtLeast(10, 5));

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
        Multiset<Integer> expected = HashMultiset.create();
        Multiset<Integer> setExpected = HashMultiset.create();

        //empty into empty
        verifyContents(empty.union(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.union(emptyList), expected);
        verifyContents(empty.union(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.union(emptyList.iterator()), expected);
        verifyContents(empty.union(asJMSet(emptyList)), expected);
        verifyContents(empty.union(asJSet(emptyList)), setExpected);
        verifyContents(empty.union(asSet(emptyList)), setExpected);

        //values into empty
        expected.addAll(values);
        setExpected.addAll(Arrays.asList(1, 3, 4));
        verifyContents(empty.union(IterableCursorable.of(values)), expected);
        verifyContents(empty.union(values), expected);
        verifyContents(empty.union(IterableCursor.of(values)), expected);
        verifyContents(empty.union(values.iterator()), expected);
        verifyContents(empty.union(asJMSet(values)), expected);
        verifyContents(empty.union(asJSet(values)), setExpected);
        verifyContents(empty.union(asSet(values)), setExpected);

        //empty into values
        setExpected.clear();
        setExpected.addAll(values);
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
        expected.clear();
        expected.addAll(wideDeep);
        setExpected.clear();
        setExpected.addAll(Arrays.asList(1, 2, 3, 3, 4));
        verifyContents(jmet.union(IterableCursorable.of(wideDeep)), expected);
        verifyContents(jmet.union(wideDeep), expected);
        verifyContents(jmet.union(IterableCursor.of(wideDeep)), expected);
        verifyContents(jmet.union(wideDeep.iterator()), expected);
        verifyContents(jmet.union(asJMSet(wideDeep)), expected);
        verifyContents(jmet.union(asJSet(wideDeep)), setExpected);
        verifyContents(jmet.union(asSet(wideDeep)), setExpected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(1, 2, 3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 2, 3, 3, 4));
        verifyContents(jmet.union(IterableCursorable.of(wideShallow)), expected);
        verifyContents(jmet.union(wideShallow), expected);
        verifyContents(jmet.union(IterableCursor.of(wideShallow)), expected);
        verifyContents(jmet.union(wideShallow.iterator()), expected);
        verifyContents(jmet.union(asJMSet(wideShallow)), expected);
        verifyContents(jmet.union(asJSet(wideShallow)), setExpected);
        verifyContents(jmet.union(asSet(wideShallow)), setExpected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(2, 2, 2, 4, 4, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 2, 2, 2, 3, 3, 4, 4, 4));
        verifyContents(jmet.union(IterableCursorable.of(narrowDeep)), expected);
        verifyContents(jmet.union(narrowDeep), expected);
        verifyContents(jmet.union(IterableCursor.of(narrowDeep)), expected);
        verifyContents(jmet.union(narrowDeep.iterator()), expected);
        verifyContents(jmet.union(asJMSet(narrowDeep)), expected);
        verifyContents(jmet.union(asJSet(narrowDeep)), setExpected);
        verifyContents(jmet.union(asSet(narrowDeep)), setExpected);

        //with narrower shallower
        final List<Integer> narrowShallow = Arrays.asList(3, 4);
        expected.clear();
        expected.addAll(values);
        setExpected.clear();
        setExpected.addAll(Arrays.asList(1, 3, 3, 4));
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
        Multiset<Integer> expected = HashMultiset.create();
        Multiset<Integer> setExpected = HashMultiset.create();

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
        expected.addAll(values);
        setExpected.addAll(Arrays.asList(1, 3, 4));
        verifyContents(jmet.intersection(IterableCursorable.of(wideDeep)), expected);
        verifyContents(jmet.intersection(wideDeep), expected);
        verifyContents(jmet.intersection(IterableCursor.of(wideDeep)), expected);
        verifyContents(jmet.intersection(wideDeep.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(wideDeep)), expected);
        verifyContents(jmet.intersection(asJSet(wideDeep)), setExpected);
        verifyContents(jmet.intersection(asSet(wideDeep)), setExpected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(1, 2, 3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 3, 4));
        verifyContents(jmet.intersection(IterableCursorable.of(wideShallow)), expected);
        verifyContents(jmet.intersection(wideShallow), expected);
        verifyContents(jmet.intersection(IterableCursor.of(wideShallow)), expected);
        verifyContents(jmet.intersection(wideShallow.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(wideShallow)), expected);
        verifyContents(jmet.intersection(asJSet(wideShallow)), setExpected);
        verifyContents(jmet.intersection(asSet(wideShallow)), setExpected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(2, 2, 2, 4, 4, 4);
        expected.clear();
        expected.add(4);
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
        expected.clear();
        expected.addAll(Arrays.asList(3, 4));
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
        List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final JImmutableMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<Integer>();
        Multiset<Integer> expected = HashMultiset.create();

        //empty into empty
        verifyContents(empty.insertAll(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.insertAll(emptyList), expected);
        verifyContents(empty.insertAll(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.insertAll(emptyList.iterator()), expected);
        verifyContents(empty.insertAll(asJMSet(emptyList)), expected);

        //values into empty
        expected.addAll(values);
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
        //with overlap
        values = Arrays.asList(0, 0, 1, 2, 3);
        expected.clear();
        expected.addAll(Arrays.asList(0, 0, 1, 1, 2, 3, 3, 3, 4));
        verifyContents(jmet.insertAll(IterableCursorable.of(values)), expected);
        verifyContents(jmet.insertAll(values), expected);
        verifyContents(jmet.insertAll(IterableCursor.of(values)), expected);
        verifyContents(jmet.insertAll(values.iterator()), expected);
        verifyContents(jmet.insertAll(asJMSet(values)), expected);

        //without overlap
        values = Arrays.asList(0, 0, 2);
        expected.clear();
        expected.addAll(Arrays.asList(0, 0, 1, 2, 3, 3, 4));
        verifyContents(jmet.insertAll(IterableCursorable.of(values)), expected);
        verifyContents(jmet.insertAll(values), expected);
        verifyContents(jmet.insertAll(IterableCursor.of(values)), expected);
        verifyContents(jmet.insertAll(values.iterator()), expected);
        verifyContents(jmet.insertAll(asJMSet(values)), expected);
    }

    private static void verifyDeleteAll(JImmutableMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final JImmutableMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<Integer>();
        Multiset<Integer> expected = HashMultiset.create();

        //empty from empty
        verifyContents(empty.deleteAll(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.deleteAll(emptyList), expected);
        verifyContents(empty.deleteAll(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.deleteAll(emptyList.iterator()), expected);
        verifyContents(empty.deleteAll(asJMSet(emptyList)), expected);
        verifyContents(empty.deleteAll(asJSet(emptyList)), expected);
        verifyContents(empty.deleteAll(asSet(emptyList)), expected);

        //values from empty
        verifyContents(empty.deleteAll(IterableCursorable.of(values)), expected);
        verifyContents(empty.deleteAll(values), expected);
        verifyContents(empty.deleteAll(IterableCursor.of(values)), expected);
        verifyContents(empty.deleteAll(values.iterator()), expected);
        verifyContents(empty.deleteAll(asJMSet(values)), expected);
        verifyContents(empty.deleteAll(asJSet(values)), expected);
        verifyContents(empty.deleteAll(asSet(values)), expected);

        //empty from values
        expected.addAll(values);
        verifyContents(jmet.deleteAll(IterableCursorable.of(emptyList)), expected);
        verifyContents(jmet.deleteAll(emptyList), expected);
        verifyContents(jmet.deleteAll(IterableCursor.of(emptyList)), expected);
        verifyContents(jmet.deleteAll(emptyList.iterator()), expected);
        verifyContents(jmet.deleteAll(asJMSet(emptyList)), expected);
        verifyContents(jmet.deleteAll(asJSet(emptyList)), expected);
        verifyContents(jmet.deleteAll(asSet(emptyList)), expected);

        //values from values
        //from smaller
        values = Arrays.asList(3, 4);
        expected.clear();
        expected.add(1);
        verifyContents(jmet.deleteAll(IterableCursorable.of(values)), expected);
        verifyContents(jmet.deleteAll(values), expected);
        verifyContents(jmet.deleteAll(IterableCursor.of(values)), expected);
        verifyContents(jmet.deleteAll(values.iterator()), expected);
        verifyContents(jmet.deleteAll(asJMSet(values)), expected);
        verifyContents(jmet.deleteAll(asJSet(values)), expected);
        verifyContents(jmet.deleteAll(asSet(values)), expected);

        //from larger
        values = Arrays.asList(1, 1, 2, 3, 4, 5);
        expected.clear();
        verifyContents(jmet.deleteAll(IterableCursorable.of(values)), expected);
        verifyContents(jmet.deleteAll(values), expected);
        verifyContents(jmet.deleteAll(IterableCursor.of(values)), expected);
        verifyContents(jmet.deleteAll(values.iterator()), expected);
        verifyContents(jmet.deleteAll(asJMSet(values)), expected);
        verifyContents(jmet.deleteAll(asJSet(values)), expected);
        verifyContents(jmet.deleteAll(asSet(values)), expected);
    }

    private static void verifyDeleteAllOccurrences(JImmutableMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final JImmutableMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<Integer>();
        Multiset<Integer> expected = HashMultiset.create();
        Multiset<Integer> setExpected = HashMultiset.create();

        //empty from empty
        verifyContents(empty.deleteAllOccurrences(IterableCursorable.of(emptyList)), expected);
        verifyContents(empty.deleteAllOccurrences(emptyList), expected);
        verifyContents(empty.deleteAllOccurrences(IterableCursor.of(emptyList)), expected);
        verifyContents(empty.deleteAllOccurrences(emptyList.iterator()), expected);
        verifyContents(empty.deleteAllOccurrences(asJMSet(emptyList)), expected);
        verifyContents(empty.deleteAllOccurrences(asJSet(emptyList)), expected);
        verifyContents(empty.deleteAllOccurrences(asSet(emptyList)), expected);

        //values from empty
        verifyContents(empty.deleteAllOccurrences(IterableCursorable.of(values)), expected);
        verifyContents(empty.deleteAllOccurrences(values), expected);
        verifyContents(empty.deleteAllOccurrences(IterableCursor.of(values)), expected);
        verifyContents(empty.deleteAllOccurrences(values.iterator()), expected);
        verifyContents(empty.deleteAllOccurrences(asJMSet(values)), expected);
        verifyContents(empty.deleteAllOccurrences(asJSet(values)), expected);
        verifyContents(empty.deleteAllOccurrences(asSet(values)), expected);

        //empty from values
        expected.addAll(values);
        verifyContents(jmet.deleteAllOccurrences(IterableCursorable.of(emptyList)), expected);
        verifyContents(jmet.deleteAllOccurrences(emptyList), expected);
        verifyContents(jmet.deleteAllOccurrences(IterableCursor.of(emptyList)), expected);
        verifyContents(jmet.deleteAllOccurrences(emptyList.iterator()), expected);
        verifyContents(jmet.deleteAllOccurrences(asJMSet(emptyList)), expected);
        verifyContents(jmet.deleteAllOccurrences(asJSet(emptyList)), expected);
        verifyContents(jmet.deleteAllOccurrences(asSet(emptyList)), expected);

        //values from values
        //from smaller
        values = Arrays.asList(3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 3));
        verifyContents(jmet.deleteAllOccurrences(IterableCursorable.of(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(values), expected);
        verifyContents(jmet.deleteAllOccurrences(IterableCursor.of(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(values.iterator()), expected);
        verifyContents(jmet.deleteAllOccurrences(asJMSet(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(asJSet(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(asSet(values)), expected);

        //from larger
        values = Arrays.asList(1, 1, 2, 3, 3, 4, 5);
        expected.clear();
        setExpected.add(3);
        verifyContents(jmet.deleteAllOccurrences(IterableCursorable.of(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(values), expected);
        verifyContents(jmet.deleteAllOccurrences(IterableCursor.of(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(values.iterator()), expected);
        verifyContents(jmet.deleteAllOccurrences(asJMSet(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(asJSet(values)), setExpected);
        verifyContents(jmet.deleteAllOccurrences(asSet(values)), setExpected);
    }

    private static void testVarious(JImmutableMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(100, 200, 300, 300);

        JImmutableMultiset<Integer> jmet = empty;
        assertTrue(jmet.isEmpty());
        assertEquals(0, jmet.size());
        assertEquals(0, jmet.valueCount());
        assertEquals(false, jmet.contains(100));
        assertEquals(false, jmet.contains(200));
        assertEquals(false, jmet.contains(300));
        assertEquals(false, jmet.containsAny(values));
        assertEquals(false, jmet.containsAll(values));
        assertEquals(false, jmet.containsAllOccurrences(values));

        jmet = jmet.insert(100);
        assertFalse(jmet.isEmpty());
        assertEquals(1, jmet.size());
        assertEquals(1, jmet.valueCount());
        assertEquals(true, jmet.contains(100));
        assertEquals(false, jmet.contains(200));
        assertEquals(false, jmet.contains(300));
        assertEquals(true, jmet.containsAny(values));
        assertEquals(false, jmet.containsAll(values));
        assertEquals(false, jmet.containsAllOccurrences(values));

        jmet = jmet.insert(200);
        assertFalse(jmet.isEmpty());
        assertEquals(2, jmet.size());
        assertEquals(2, jmet.valueCount());
        assertEquals(true, jmet.contains(100));
        assertEquals(true, jmet.contains(200));
        assertEquals(false, jmet.contains(300));
        assertEquals(true, jmet.containsAny(values));
        assertEquals(false, jmet.containsAll(values));
        assertEquals(false, jmet.containsAllOccurrences(values));

        assertSame(jmet, jmet.union(Collections.singletonList(100)));
        assertSame(jmet, jmet.union(Collections.singletonList(200)));

        JImmutableMultiset<Integer> jmet2 = jmet.union(values);
        assertFalse(jmet2.isEmpty());
        assertEquals(3, jmet2.size());
        assertEquals(4, jmet2.valueCount());
        assertEquals(true, jmet2.contains(100));
        assertEquals(true, jmet2.contains(200));
        assertEquals(true, jmet2.containsAtLeast(300, 2));
        assertEquals(true, jmet2.containsAny(values));
        assertEquals(true, jmet2.containsAll(values));
        assertEquals(true, jmet2.containsAllOccurrences(values));
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 200, 300)), jmet2.getSet());

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.delete(300));
        assertEquals(jmet, jmet2.deleteOccurrence(300).deleteOccurrence(300));

        jmet2 = jmet2.deleteAll(jmet);
        assertFalse(jmet2.isEmpty());
        assertEquals(1, jmet2.size());
        assertEquals(2, jmet2.valueCount());
        assertEquals(false, jmet2.contains(100));
        assertEquals(false, jmet2.contains(200));
        assertEquals(true, jmet2.containsAtLeast(300, 2));
        assertEquals(true, jmet2.containsAny(values));
        assertEquals(false, jmet2.containsAny(jmet));
        assertEquals(false, jmet2.containsAll(values));
        assertEquals(false, jmet2.containsAllOccurrences(values));

        JImmutableMultiset<Integer> jmet3 = jmet.union(values).insert(400).insert(500, 3);
        assertFalse(jmet3.isEmpty());
        assertEquals(5, jmet3.size());
        assertEquals(8, jmet3.valueCount());
        assertEquals(true, jmet3.contains(100));
        assertEquals(true, jmet3.contains(200));
        assertEquals(true, jmet3.containsAtLeast(300, 2));
        assertEquals(true, jmet3.contains(400));
        assertEquals(true, jmet3.containsAtLeast(500, 3));
        assertEquals(false, jmet3.contains(600));
        assertEquals(true, jmet3.containsAny(values));
        assertEquals(true, jmet3.containsAny(jmet));
        assertEquals(true, jmet3.containsAny(jmet2));
        assertEquals(true, jmet3.containsAll(values));
        assertEquals(true, jmet3.containsAll(jmet));
        assertEquals(true, jmet3.containsAll(jmet2));
        assertEquals(true, jmet3.containsAllOccurrences(values));
        assertEquals(true, jmet3.containsAllOccurrences(jmet));
        assertEquals(true, jmet3.containsAllOccurrences(jmet2));
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 200, 300, 400, 500)), jmet3.getSet());
        assertEquals(jmet, jmet3.intersection(jmet));
        assertEquals(jmet, jmet.intersection(jmet3));
        assertEquals(jmet2, jmet3.intersection(jmet2));
        assertEquals(jmet2, jmet2.intersection(jmet3));
        assertEquals(empty, jmet.intersection(jmet2));
        assertEquals(empty, jmet2.intersection(jmet));
        assertEquals(empty, jmet3.deleteAll(jmet3));
    }

    public static void testRandom(final JImmutableMultiset<Integer> emptyJMet,
                                  final Multiset<Integer> expected)
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            expected.clear();
            JImmutableMultiset<Integer> jmet = emptyJMet;
            for (int loops = 0; loops < (4 * size); ++loops) {
                int command = random.nextInt(8);
                Integer value = random.nextInt(size);
                int count = random.nextInt(3) + 1;
                switch (command) {
                case 0:
                    jmet = jmet.insert(value);
                    expected.add(value);
                    assertEquals(true, jmet.contains(value));
                case 1:
                    assertEquals(expected.contains(value), jmet.contains(value));
                    assertEquals(expected.count(value) >= count, jmet.containsAtLeast(value, count));
                    break;
                case 2:
                    jmet = jmet.deleteOccurrence(value, count);
                    expected.remove(value, count);
                    break;
                case 3:
                    jmet = jmet.delete(value);
                    int expectedCount = expected.count(value);
                    for (int n = 0; n < expectedCount; ++n) {
                        expected.remove(value);
                    }
                    assertEquals(expected.contains(value), jmet.contains(value));
                    break;
                case 4:
                    jmet = jmet.deleteOccurrence(value);
                    expected.remove(value);
                    assertEquals(expected.count(value), jmet.count(value));
                    break;
                case 5:
                    jmet = jmet.setCount(value, count);
                    expected.setCount(value, count);
                    break;
                default:
                    jmet = jmet.insert(value, count);
                    expected.add(value, count);
                    assertEquals(true, jmet.containsAtLeast(value, count));
                    break;
                }
                assertEquals(expected.size(), jmet.valueCount());
            }
            //verify multisets have the same contents
            verifyContents(jmet, expected);

            //verify ordering is the same in both sets
            assertEquals(new ArrayList<Integer>(expected.elementSet()), new ArrayList<Integer>(jmet.getSet()));
            verifyCursor(jmet, expected);

            for (Integer value : expected) {
                jmet = jmet.deleteOccurrence(value);
            }
            assertEquals(0, jmet.size());
            assertEquals(0, jmet.valueCount());
            assertEquals(true, jmet.isEmpty());

        }
    }

    public static void verifyContents(JImmutableMultiset<Integer> jmet,
                                      Multiset<Integer> expected)
    {
        assertEquals(expected.isEmpty(), jmet.isEmpty());
        assertEquals(expected.size(), jmet.valueCount());
        assertEquals(expected.elementSet().size(), jmet.size());
        assertEquals(expected.elementSet(), jmet.getSet());

        for (Cursor<JImmutableMap.Entry<Integer, Integer>> e = jmet.entryCursor().start(); e.hasValue(); e = e.next()) {
            List<Integer> eList = entryAsList(e.getValue());
            Integer value = eList.get(0);
            assertEquals(true, expected.containsAll(eList));
            assertEquals(true, jmet.contains(value));
            assertEquals(true, jmet.containsAtLeast(value, eList.size()));
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
        assertEquals(true, jmet.containsAllOccurrences(expected.elementSet()));

        assertEquals(!expected.isEmpty(), jmet.containsAny(IterableCursorable.of(expected)));
        assertEquals(!expected.isEmpty(), jmet.containsAny(expected));
        assertEquals(!expected.isEmpty(), jmet.containsAny(IterableCursor.of(expected)));
        assertEquals(!expected.isEmpty(), jmet.containsAny(expected.iterator()));

    }

    public static <T> void verifyCursor(final JImmutableMultiset<T> jmet,
                                        final Multiset<T> expected)
    {
        final List<T> expectedSet = new ArrayList<T>();
        expectedSet.addAll(expected.elementSet());

        final List<T> expectedList = new ArrayList<T>();
        for (T value : expected) {
            expectedList.add(value);
        }

        final List<JImmutableMap.Entry<T, Integer>> entries = new ArrayList<JImmutableMap.Entry<T, Integer>>();
        for (Multiset.Entry<T> mentry : expected.entrySet()) {
            entries.add(new MapEntry<T, Integer>(mentry.getElement(), mentry.getCount()));
        }
        assertEquals(entries.size(), jmet.size());

        StandardCursorTest.listCursorTest(expectedSet, jmet.cursor());
        StandardCursorTest.listIteratorTest(expectedSet, jmet.iterator());
        StandardCursorTest.cursorTest(new Func1<Integer, T>()
        {
            @Override
            public T apply(Integer value)
            {
                return entries.get(value).getKey();
            }
        }, entries.size(), jmet.cursor());
        StandardCursorTest.iteratorTest(new Func1<Integer, T>()
        {
            @Override
            public T apply(Integer value)
            {
                return entries.get(value).getKey();
            }
        }, entries.size(), jmet.iterator());


        StandardCursorTest.listCursorTest(expectedList, jmet.occurrenceCursor());
        StandardCursorTest.listIteratorTest(expectedList, jmet.occurrenceCursor().iterator());
        StandardCursorTest.cursorTest(new Func1<Integer, T>()
        {
            @Override
            public T apply(Integer value)
            {
                return expectedList.get(value);
            }
        }, expected.size(), jmet.occurrenceCursor());
        StandardCursorTest.iteratorTest(new Func1<Integer, T>()
        {
            @Override
            public T apply(Integer value)
            {
                return expectedList.get(value);
            }
        }, expected.size(), jmet.occurrenceCursor().iterator());

        StandardCursorTest.listCursorTest(entries, jmet.entryCursor());
        StandardCursorTest.listIteratorTest(entries, jmet.entryCursor().iterator());
        StandardCursorTest.cursorTest(new Func1<Integer, JImmutableMap.Entry<T, Integer>>()
        {
            @Override
            public JImmutableMap.Entry<T, Integer> apply(Integer value)
            {
                return entries.get(value);
            }
        }, entries.size(), jmet.entryCursor());
        StandardCursorTest.iteratorTest(new Func1<Integer, JImmutableMap.Entry<T, Integer>>()
        {
            @Override
            public JImmutableMap.Entry<T, Integer> apply(Integer value)
            {
                return entries.get(value);
            }
        }, entries.size(), jmet.entryCursor().iterator());

    }

    private static Set<Integer> asSet(List<Integer> list)
    {
        Set<Integer> set = new LinkedHashSet<Integer>();
        set.addAll(list);
        return set;
    }

    private static JImmutableSet<Integer> asJSet(Multiset<Integer> multi)
    {
        JImmutableSet<Integer> jet = JImmutableHashSet.of();
        for (Integer value : multi) {
            jet = jet.insert(value);
        }
        return jet;
    }

    private static JImmutableSet<Integer> asJSet(List<Integer> list)
    {
        JImmutableSet<Integer> jet = JImmutableHashSet.of();
        for (int i : list) {
            jet = jet.insert(i);
        }
        return jet;
    }

    private static JImmutableMultiset<Integer> asJMSet(Multiset<Integer> multi)
    {
        JImmutableMultiset<Integer> jmet = JImmutableHashMultiset.of();
        for (Integer value : multi) {
            jmet = jmet.insert(value);
        }
        return jmet;
    }

    private static JImmutableMultiset<Integer> asJMSet(List<Integer> list)
    {
        JImmutableMultiset<Integer> jmet = JImmutableHashMultiset.of();
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
