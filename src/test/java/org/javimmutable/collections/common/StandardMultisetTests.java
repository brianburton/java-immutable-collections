///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IMultiset;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.hash.HashMultiset;
import org.javimmutable.collections.hash.HashSet;
import org.javimmutable.collections.inorder.OrderedMultiset;
import org.javimmutable.collections.iterators.StandardIteratorTests;

public class StandardMultisetTests
{
    public static void verifyMultiset(IMultiset<Integer> empty,
                                      boolean emptyIsUnique)
    {
        StandardIteratorTests.emptyIteratorTest(empty.iterator());
        StandardIteratorTests.emptyIteratorTest(empty.entries().iterator());
        StandardIteratorTests.emptyIteratorTest(empty.occurrences().iterator());
        StandardSetTests.verifySet(empty, emptyIsUnique);
        testVarious(empty);
        testEquals(empty);
        testCollector(empty.insert(1).insert(1));
        testTransform(empty);

        verifyContents(empty, com.google.common.collect.HashMultiset.create());

        assertEquals(0, empty.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(empty, new java.util.HashSet<Integer>());
        assertEquals(empty.getSet(), new java.util.HashSet<Integer>());
        empty.checkInvariants();

        IMultiset<Integer> jmet = empty;
        assertEquals(false, jmet.contains(10));
        jmet = jmet.insert(10);
        jmet.checkInvariants();
        assertEquals(true, jmet != empty);
        assertEquals(1, jmet.size());
        assertEquals(1, jmet.occurrenceCount());
        assertEquals(false, jmet.isEmpty());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.containsAtLeast(10, 1));
        assertEquals(false, jmet.containsAtLeast(10, 2));

        jmet = jmet.delete(10);
        jmet.checkInvariants();
        assertEquals(0, jmet.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(false, jmet.contains(10));

        jmet = jmet.insert(10, 3);
        jmet.checkInvariants();
        assertEquals(1, jmet.size());
        assertEquals(3, jmet.occurrenceCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.containsAtLeast(10, 3));
        assertEquals(false, jmet.containsAtLeast(10, 4));
        assertEquals(false, jmet.isEmpty());

        jmet = jmet.setCount(10, 5);
        jmet.checkInvariants();
        assertEquals(1, jmet.size());
        assertEquals(5, jmet.occurrenceCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.containsAtLeast(10, 5));
        jmet = jmet.setCount(10, 3);
        jmet.checkInvariants();
        assertEquals(false, jmet.containsAtLeast(10, 5));
        assertEquals(true, jmet.containsAtLeast(10, 3));

        assertEquals(empty, jmet.delete(10));
        assertEquals(empty, jmet.deleteOccurrence(10, 3));
        assertEquals(empty, jmet.deleteOccurrence(10).deleteOccurrence(10).deleteOccurrence(10));
        assertEquals(empty, jmet.deleteAll());
        assertEquals(empty, jmet.setCount(10, 0));

        jmet = jmet.setCount(10, 5);
        jmet.checkInvariants();
        assertEquals(true, jmet.containsAtLeast(10, 5));

        verifyUnion(empty);
        verifyIntersection(empty);
        verifyInsertAll(empty);
        verifyDeleteAll(empty);
        verifyDeleteAllOccurrences(empty);
        verifyIntersectionOrder(empty);
    }

    private static void testCollector(IMultiset<Integer> template)
    {
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i <= 500; ++i) {
            values.add(i);
            if (i % 2 == 0) {
                values.add(501 - i);
            }
        }
        IMultiset<Integer> expected = template.insertAll(values);
        IMultiset<Integer> actual = values.parallelStream().collect(template.multisetCollector());
        assertEquals(expected, actual);
    }

    private static void verifyUnion(IMultiset<Integer> empty)
    {
        final List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final IMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<>();
        Multiset<Integer> expected = com.google.common.collect.HashMultiset.create();
        Multiset<Integer> setExpected = com.google.common.collect.HashMultiset.create();

        //empty into empty
        verifyContents(empty.union(emptyList), expected);
        verifyContents(empty.union(emptyList.iterator()), expected);
        verifyContents(empty.union(asJMSet(emptyList)), expected);
        verifyContents(empty.union(asJSet(emptyList)), setExpected);
        verifyContents(empty.union(asSet(emptyList)), setExpected);

        //values into empty
        expected.addAll(values);
        setExpected.addAll(Arrays.asList(1, 3, 4));
        verifyContents(empty.union(values), expected);
        verifyContents(empty.union(values.iterator()), expected);
        verifyContents(empty.union(asJMSet(values)), expected);
        verifyContents(empty.union(asJSet(values)), setExpected);
        verifyContents(empty.union(asSet(values)), setExpected);

        //empty into values
        setExpected.clear();
        setExpected.addAll(values);
        verifyContents(jmet.union(emptyList), expected);
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
        verifyContents(jmet.union(wideDeep), expected);
        verifyContents(jmet.union(wideDeep.iterator()), expected);
        verifyContents(jmet.union(asJMSet(wideDeep)), expected);
        verifyContents(jmet.union(asJSet(wideDeep)), setExpected);
        verifyContents(jmet.union(asSet(wideDeep)), setExpected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(1, 2, 3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 2, 3, 3, 4));
        verifyContents(jmet.union(wideShallow), expected);
        verifyContents(jmet.union(wideShallow.iterator()), expected);
        verifyContents(jmet.union(asJMSet(wideShallow)), expected);
        verifyContents(jmet.union(asJSet(wideShallow)), setExpected);
        verifyContents(jmet.union(asSet(wideShallow)), setExpected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(2, 2, 2, 4, 4, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 2, 2, 2, 3, 3, 4, 4, 4));
        verifyContents(jmet.union(narrowDeep), expected);
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
        verifyContents(jmet.union(narrowShallow), expected);
        verifyContents(jmet.union(narrowShallow.iterator()), expected);
        verifyContents(jmet.union(asJMSet(narrowShallow)), expected);
        verifyContents(jmet.union(asJSet(narrowShallow)), setExpected);
        verifyContents(jmet.union(asSet(narrowShallow)), setExpected);

    }

    private static void verifyIntersection(IMultiset<Integer> empty)
    {
        final List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final IMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<>();
        Multiset<Integer> expected = com.google.common.collect.HashMultiset.create();
        Multiset<Integer> setExpected = com.google.common.collect.HashMultiset.create();

        //empty into empty
        verifyContents(empty.intersection(emptyList), expected);
        verifyContents(empty.intersection(emptyList.iterator()), expected);
        verifyContents(empty.intersection(asJMSet(emptyList)), expected);
        verifyContents(empty.intersection(asJSet(emptyList)), setExpected);
        verifyContents(empty.intersection(asSet(emptyList)), setExpected);

        //values into empty
        verifyContents(empty.intersection(values), expected);
        verifyContents(empty.intersection(values.iterator()), expected);
        verifyContents(empty.intersection(asJMSet(values)), expected);
        verifyContents(empty.intersection(asJSet(values)), setExpected);
        verifyContents(empty.intersection(asSet(values)), setExpected);

        //empty into values
        verifyContents(jmet.intersection(emptyList), expected);
        verifyContents(jmet.intersection(emptyList.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(emptyList)), expected);
        verifyContents(jmet.intersection(asJSet(emptyList)), setExpected);
        verifyContents(jmet.intersection(asSet(emptyList)), setExpected);

        //values into values
        //with wider and deeper
        final List<Integer> wideDeep = Arrays.asList(1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 4);
        expected.addAll(values);
        setExpected.addAll(Arrays.asList(1, 3, 4));
        verifyContents(jmet.intersection(wideDeep), expected);
        verifyContents(jmet.intersection(wideDeep.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(wideDeep)), expected);
        verifyContents(jmet.intersection(asJSet(wideDeep)), setExpected);
        verifyContents(jmet.intersection(asSet(wideDeep)), setExpected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(1, 2, 3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 3, 4));
        verifyContents(jmet.intersection(wideShallow), expected);
        verifyContents(jmet.intersection(wideShallow.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(wideShallow)), expected);
        verifyContents(jmet.intersection(asJSet(wideShallow)), setExpected);
        verifyContents(jmet.intersection(asSet(wideShallow)), setExpected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(2, 2, 2, 4, 4, 4);
        expected.clear();
        expected.add(4);
        setExpected = expected;
        verifyContents(jmet.intersection(narrowDeep), expected);
        verifyContents(jmet.intersection(narrowDeep.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(narrowDeep)), expected);
        verifyContents(jmet.intersection(asJSet(narrowDeep)), setExpected);
        verifyContents(jmet.intersection(asSet(narrowDeep)), setExpected);

        //with narrower shallower
        final List<Integer> narrowShallow = Arrays.asList(3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(3, 4));
        setExpected = expected;
        verifyContents(jmet.intersection(narrowShallow), expected);
        verifyContents(jmet.intersection(narrowShallow.iterator()), expected);
        verifyContents(jmet.intersection(asJMSet(narrowShallow)), expected);
        verifyContents(jmet.intersection(asJSet(narrowShallow)), setExpected);
        verifyContents(jmet.intersection(asSet(narrowShallow)), setExpected);

    }

    private static void verifyOrder(IMultiset<Integer> jmet,
                                    List<Integer> expected)
    {
        StandardIteratorTests.listIteratorTest(expected, jmet.iterator());

    }

    private static void verifyIntersectionOrder(IMultiset<Integer> empty)
    {
        IMultiset<Integer> jmet = empty.insert(100).insert(50).insert(100).insert(600).insert(0).insert(400);
        List<Integer> expected = new ArrayList<>();
        expected.addAll(jmet.getSet());
        StandardIteratorTests.listIteratorTest(expected, jmet.iterator());

        IMultiset<Integer> diffOrder = OrderedMultiset.<Integer>of().insert(400).insert(0)
            .insert(600).insert(100).insert(50).insert(100);

        //with same width, same depth
        verifyOrder(jmet.intersection(Arrays.asList(400, 0, 600, 100, 50, 100)), expected);
        verifyOrder(jmet.intersection(diffOrder), expected);
        verifyOrder(jmet.intersection(diffOrder.iterator()), expected);
        verifyOrder(jmet.intersection((ISet<Integer>)diffOrder), expected);
        verifyOrder(jmet.intersection(diffOrder.getSet()), expected);

        //with wider and deeper
        final List<Integer> wideDeep = Arrays.asList(0, 0, 50, 100, 100, 100, 200, 200, 400, 600);
        verifyOrder(jmet.intersection(wideDeep), expected);
        verifyOrder(jmet.intersection(wideDeep.iterator()), expected);
        verifyOrder(jmet.intersection(asJMSet(wideDeep)), expected);
        verifyOrder(jmet.intersection(asJSet(wideDeep)), expected);
        verifyOrder(jmet.intersection(asSet(wideDeep)), expected);

        //with wider and shallower
        final List<Integer> wideShallow = Arrays.asList(500, 400, 100, 150);
        expected.clear();
        for (Integer value : empty.insertAll(Arrays.asList(100, 400))) {
            expected.add(value);
        }
        verifyOrder(jmet.intersection(wideShallow), expected);
        verifyOrder(jmet.intersection(wideShallow.iterator()), expected);
        verifyOrder(jmet.intersection(asJMSet(wideShallow)), expected);
        verifyOrder(jmet.intersection(asJSet(wideShallow)), expected);
        verifyOrder(jmet.intersection(asSet(wideShallow)), expected);

        //with narrower and deeper
        final List<Integer> narrowDeep = Arrays.asList(400, 400, 100, 400, 100, 100);
        expected.clear();
        for (Integer value : empty.insertAll(Arrays.asList(100, 100, 400))) {
            expected.add(value);
        }
        verifyOrder(jmet.intersection(narrowDeep), expected);
        verifyOrder(jmet.intersection(narrowDeep.iterator()), expected);
        verifyOrder(jmet.intersection(asJMSet(narrowDeep)), expected);
        verifyOrder(jmet.intersection(asJSet(narrowDeep)), expected);
        verifyOrder(jmet.intersection(asSet(narrowDeep)), expected);

        //with narrower shallower
        final List<Integer> narrowShallow = Arrays.asList(400, 100);
        expected.clear();
        for (Integer value : empty.insertAll(Arrays.asList(100, 400))) {
            expected.add(value);
        }
        verifyOrder(jmet.intersection(narrowShallow), expected);
        verifyOrder(jmet.intersection(narrowShallow.iterator()), expected);
        verifyOrder(jmet.intersection(asJMSet(narrowShallow)), expected);
        verifyOrder(jmet.intersection(asJSet(narrowShallow)), expected);
        verifyOrder(jmet.intersection(asSet(narrowShallow)), expected);
    }

    private static void verifyInsertAll(IMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final IMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<>();
        Multiset<Integer> expected = com.google.common.collect.HashMultiset.create();

        //empty into empty
        verifyContents(empty.insertAll(plainIterable(emptyList)), expected);
        verifyContents(empty.insertAll(emptyList), expected);
        verifyContents(empty.insertAll(emptyList.iterator()), expected);
        verifyContents(empty.insertAll(asJMSet(emptyList)), expected);

        //values into empty
        expected.addAll(values);
        verifyContents(empty.insertAll(plainIterable(values)), expected);
        verifyContents(empty.insertAll(values), expected);
        verifyContents(empty.insertAll(values.iterator()), expected);
        verifyContents(empty.insertAll(asJMSet(values)), expected);

        //empty into values
        verifyContents(jmet.insertAll(plainIterable(emptyList)), expected);
        verifyContents(jmet.insertAll(emptyList), expected);
        verifyContents(jmet.insertAll(emptyList.iterator()), expected);
        verifyContents(jmet.insertAll(asJMSet(emptyList)), expected);

        //values into values
        //with overlap
        values = Arrays.asList(0, 0, 1, 2, 3);
        expected.clear();
        expected.addAll(Arrays.asList(0, 0, 1, 1, 2, 3, 3, 3, 4));
        verifyContents(jmet.insertAll(plainIterable(values)), expected);
        verifyContents(jmet.insertAll(values), expected);
        verifyContents(jmet.insertAll(values.iterator()), expected);
        verifyContents(jmet.insertAll(asJMSet(values)), expected);

        //without overlap
        values = Arrays.asList(0, 0, 2);
        expected.clear();
        expected.addAll(Arrays.asList(0, 0, 1, 2, 3, 3, 4));
        verifyContents(jmet.insertAll(plainIterable(values)), expected);
        verifyContents(jmet.insertAll(values), expected);
        verifyContents(jmet.insertAll(values.iterator()), expected);
        verifyContents(jmet.insertAll(asJMSet(values)), expected);
    }

    private static void testTransform(IMultiset<Integer> template)
    {
        IMultiset<Integer> ints = template;
        IMultiset<Integer> strings = template.transform(x -> x * 100);
        Multiset<Integer> expected = com.google.common.collect.HashMultiset.create();
        verifyContents(strings, expected);
        for (int i = 1; i <= 100; ++i) {
            ints = ints.insert(i);
            strings = ints.transform(x -> x * 100);
            assertEquals(ints.getClass(), strings.getClass());
            expected.add(100 * i);
            verifyContents(strings, expected);
        }
    }

    private static Iterable<Integer> plainIterable(List<Integer> values)
    {
        return () -> values.iterator();
    }

    private static void verifyDeleteAll(IMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final IMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<>();
        Multiset<Integer> expected = com.google.common.collect.HashMultiset.create();

        //empty from empty
        verifyContents(empty.deleteAll(emptyList), expected);
        verifyContents(empty.deleteAll(emptyList.iterator()), expected);
        verifyContents(empty.deleteAll(asJMSet(emptyList)), expected);
        verifyContents(empty.deleteAll(asJSet(emptyList)), expected);
        verifyContents(empty.deleteAll(asSet(emptyList)), expected);

        //values from empty
        verifyContents(empty.deleteAll(values), expected);
        verifyContents(empty.deleteAll(values.iterator()), expected);
        verifyContents(empty.deleteAll(asJMSet(values)), expected);
        verifyContents(empty.deleteAll(asJSet(values)), expected);
        verifyContents(empty.deleteAll(asSet(values)), expected);

        //empty from values
        expected.addAll(values);
        verifyContents(jmet.deleteAll(emptyList), expected);
        verifyContents(jmet.deleteAll(emptyList.iterator()), expected);
        verifyContents(jmet.deleteAll(asJMSet(emptyList)), expected);
        verifyContents(jmet.deleteAll(asJSet(emptyList)), expected);
        verifyContents(jmet.deleteAll(asSet(emptyList)), expected);

        //values from values
        //from smaller
        values = Arrays.asList(3, 4);
        expected.clear();
        expected.add(1);
        verifyContents(jmet.deleteAll(values), expected);
        verifyContents(jmet.deleteAll(values.iterator()), expected);
        verifyContents(jmet.deleteAll(asJMSet(values)), expected);
        verifyContents(jmet.deleteAll(asJSet(values)), expected);
        verifyContents(jmet.deleteAll(asSet(values)), expected);

        //from larger
        values = Arrays.asList(1, 1, 2, 3, 4, 5);
        expected.clear();
        verifyContents(jmet.deleteAll(values), expected);
        verifyContents(jmet.deleteAll(values.iterator()), expected);
        verifyContents(jmet.deleteAll(asJMSet(values)), expected);
        verifyContents(jmet.deleteAll(asJSet(values)), expected);
        verifyContents(jmet.deleteAll(asSet(values)), expected);
    }

    private static void verifyDeleteAllOccurrences(IMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(1, 3, 3, 4);
        final IMultiset<Integer> jmet = empty.insert(1).insert(3).insert(3).insert(4);
        final List<Integer> emptyList = new ArrayList<>();
        Multiset<Integer> expected = com.google.common.collect.HashMultiset.create();
        Multiset<Integer> setExpected = com.google.common.collect.HashMultiset.create();

        //empty from empty
        verifyContents(empty.deleteAllOccurrences(emptyList), expected);
        verifyContents(empty.deleteAllOccurrences(emptyList.iterator()), expected);
        verifyContents(empty.deleteAllOccurrences(asJMSet(emptyList)), expected);
        verifyContents(empty.deleteAllOccurrences(asJSet(emptyList)), expected);
        verifyContents(empty.deleteAllOccurrences(asSet(emptyList)), expected);

        //values from empty
        verifyContents(empty.deleteAllOccurrences(values), expected);
        verifyContents(empty.deleteAllOccurrences(values.iterator()), expected);
        verifyContents(empty.deleteAllOccurrences(asJMSet(values)), expected);
        verifyContents(empty.deleteAllOccurrences(asJSet(values)), expected);
        verifyContents(empty.deleteAllOccurrences(asSet(values)), expected);

        //empty from values
        expected.addAll(values);
        verifyContents(jmet.deleteAllOccurrences(emptyList), expected);
        verifyContents(jmet.deleteAllOccurrences(emptyList.iterator()), expected);
        verifyContents(jmet.deleteAllOccurrences(asJMSet(emptyList)), expected);
        verifyContents(jmet.deleteAllOccurrences(asJSet(emptyList)), expected);
        verifyContents(jmet.deleteAllOccurrences(asSet(emptyList)), expected);

        //values from values
        //from smaller
        values = Arrays.asList(3, 4);
        expected.clear();
        expected.addAll(Arrays.asList(1, 3));
        verifyContents(jmet.deleteAllOccurrences(values), expected);
        verifyContents(jmet.deleteAllOccurrences(values.iterator()), expected);
        verifyContents(jmet.deleteAllOccurrences(asJMSet(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(asJSet(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(asSet(values)), expected);

        //from larger
        values = Arrays.asList(1, 1, 2, 3, 3, 4, 5);
        expected.clear();
        setExpected.add(3);
        verifyContents(jmet.deleteAllOccurrences(values), expected);
        verifyContents(jmet.deleteAllOccurrences(values.iterator()), expected);
        verifyContents(jmet.deleteAllOccurrences(asJMSet(values)), expected);
        verifyContents(jmet.deleteAllOccurrences(asJSet(values)), setExpected);
        verifyContents(jmet.deleteAllOccurrences(asSet(values)), setExpected);
    }

    private static void testVarious(IMultiset<Integer> empty)
    {
        List<Integer> values = Arrays.asList(100, 200, 300, 300);

        IMultiset<Integer> jmet = empty;
        assertTrue(jmet.isEmpty());
        assertEquals(0, jmet.size());
        assertEquals(0, jmet.occurrenceCount());
        assertEquals(false, jmet.contains(100));
        assertEquals(false, jmet.contains(200));
        assertEquals(false, jmet.contains(300));
        assertEquals(false, jmet.containsAny(values));
        assertEquals(false, jmet.containsAll(values));
        assertEquals(false, jmet.containsAllOccurrences(values));

        jmet = jmet.insert(100);
        assertFalse(jmet.isEmpty());
        assertEquals(1, jmet.size());
        assertEquals(1, jmet.occurrenceCount());
        assertEquals(true, jmet.contains(100));
        assertEquals(false, jmet.contains(200));
        assertEquals(false, jmet.contains(300));
        assertEquals(true, jmet.containsAny(values));
        assertEquals(false, jmet.containsAll(values));
        assertEquals(false, jmet.containsAllOccurrences(values));

        jmet = jmet.insert(200);
        assertFalse(jmet.isEmpty());
        assertEquals(2, jmet.size());
        assertEquals(2, jmet.occurrenceCount());
        assertEquals(true, jmet.contains(100));
        assertEquals(true, jmet.contains(200));
        assertEquals(false, jmet.contains(300));
        assertEquals(true, jmet.containsAny(values));
        assertEquals(false, jmet.containsAll(values));
        assertEquals(false, jmet.containsAllOccurrences(values));

        assertSame(jmet, jmet.union(Collections.singletonList(100)));
        assertSame(jmet, jmet.union(Collections.singletonList(200)));

        IMultiset<Integer> jmet2 = jmet.union(values);
        assertFalse(jmet2.isEmpty());
        assertEquals(3, jmet2.size());
        assertEquals(4, jmet2.occurrenceCount());
        assertEquals(true, jmet2.contains(100));
        assertEquals(true, jmet2.contains(200));
        assertEquals(true, jmet2.containsAtLeast(300, 2));
        assertEquals(true, jmet2.containsAny(values));
        assertEquals(true, jmet2.containsAll(values));
        assertEquals(true, jmet2.containsAllOccurrences(values));
        assertEquals(new java.util.HashSet<>(Arrays.asList(100, 200, 300)), jmet2.getSet());

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.delete(300));
        assertEquals(jmet, jmet2.deleteOccurrence(300).deleteOccurrence(300));

        jmet2 = jmet2.deleteAll(jmet);
        assertFalse(jmet2.isEmpty());
        assertEquals(1, jmet2.size());
        assertEquals(2, jmet2.occurrenceCount());
        assertEquals(false, jmet2.contains(100));
        assertEquals(false, jmet2.contains(200));
        assertEquals(true, jmet2.containsAtLeast(300, 2));
        assertEquals(true, jmet2.containsAny(values));
        assertEquals(false, jmet2.containsAny(jmet));
        assertEquals(false, jmet2.containsAll(values));
        assertEquals(false, jmet2.containsAllOccurrences(values));

        IMultiset<Integer> jmet3 = jmet.union(values).insert(400).insert(500, 3);
        assertFalse(jmet3.isEmpty());
        assertEquals(5, jmet3.size());
        assertEquals(8, jmet3.occurrenceCount());
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
        assertEquals(new java.util.HashSet<>(Arrays.asList(100, 200, 300, 400, 500)), jmet3.getSet());
        assertEquals(jmet, jmet3.intersection(jmet));
        assertEquals(jmet, jmet.intersection(jmet3));
        assertEquals(jmet2, jmet3.intersection(jmet2));
        assertEquals(jmet2, jmet2.intersection(jmet3));
        assertEquals(empty, jmet.intersection(jmet2));
        assertEquals(empty, jmet2.intersection(jmet));
        assertEquals(empty, jmet3.deleteAll(jmet3));
    }

    private static void testEquals(IMultiset<Integer> empty)
    {
        assertEquals(false, empty.equals(null));
        assertEquals(true, empty.equals(empty));

        final java.util.HashSet<Integer> javaSet = new java.util.HashSet<>();
        IMultiset<Integer> multiset = empty;
        assertEquals(true, multiset.equals(javaSet));
        multiset = multiset.insert(42);
        assertEquals(false, multiset.equals(javaSet));
        javaSet.add(42);
        assertEquals(true, multiset.equals(javaSet));
        multiset = multiset.insert(42);
        assertEquals(false, multiset.equals(javaSet));

        ISet<Integer> set = HashSet.of();
        multiset = empty;
        assertEquals(true, multiset.equals(set));
        set = set.insert(42);
        assertEquals(false, multiset.equals(set));
        multiset = multiset.insert(42);
        assertEquals(true, multiset.equals(set));
        set = set.insert(1701);
        assertEquals(false, multiset.equals(set));
        multiset = multiset.insert(1701);
        assertEquals(true, multiset.equals(set));
    }

    public static void testRandom(final IMultiset<Integer> emptyJMet,
                                  final Multiset<Integer> expected)
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            expected.clear();
            IMultiset<Integer> jmet = emptyJMet;
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
                assertEquals(expected.size(), jmet.occurrenceCount());
            }
            jmet.checkInvariants();
            //verify multisets have the same contents
            verifyContents(jmet, expected);

            //verify ordering is the same in both sets
            if ((jmet.getSpliteratorCharacteristics() & Spliterator.ORDERED) != 0) {
                assertEquals(new ArrayList<>(expected.elementSet()), new ArrayList<>(jmet.getSet()));
            }
            verifyIterators(jmet, expected);

            for (Integer value : expected) {
                jmet = jmet.deleteOccurrence(value);
            }
            jmet.checkInvariants();
            assertEquals(0, jmet.size());
            assertEquals(0, jmet.occurrenceCount());
            assertEquals(true, jmet.isEmpty());
        }
    }

    public static void verifyContents(IMultiset<Integer> jmet,
                                      Multiset<Integer> expected)
    {
        jmet.checkInvariants();
        assertEquals(expected.isEmpty(), jmet.isEmpty());
        assertEquals(expected.size(), jmet.occurrenceCount());
        assertEquals(expected.elementSet().size(), jmet.size());
        assertEquals(expected.elementSet(), jmet.getSet());

        for (IMapEntry<Integer, Integer> entry : jmet.entries()) {
            List<Integer> eList = entryAsList(entry);
            Integer value = eList.get(0);
            assertEquals(true, expected.containsAll(eList));
            assertEquals(true, jmet.contains(value));
            assertEquals(true, jmet.containsAtLeast(value, eList.size()));
            assertEquals(true, jmet.containsAll(eList));
            assertEquals(eList.size(), jmet.count(value));
        }

        assertEquals(true, jmet.containsAll(iterable(expected)));
        assertEquals(true, jmet.containsAll(expected));
        assertEquals(true, jmet.containsAll(expected.iterator()));

        assertEquals(true, jmet.containsAllOccurrences(iterable(expected)));
        assertEquals(true, jmet.containsAllOccurrences(expected.iterator()));
        assertEquals(true, jmet.containsAllOccurrences(asJMSet(expected)));
        assertEquals(true, jmet.containsAllOccurrences(asJSet(expected)));
        assertEquals(true, jmet.containsAllOccurrences(expected.elementSet()));

        assertEquals(!expected.isEmpty(), jmet.containsAny(iterable(expected)));
        assertEquals(!expected.isEmpty(), jmet.containsAny(expected));
        assertEquals(!expected.isEmpty(), jmet.containsAny(expected.iterator()));
    }

    public static <T> void verifyIterators(final IMultiset<T> jmet,
                                           final Multiset<T> expected)
    {
        final ExpectedOrderSorter<T> ordering = new ExpectedOrderSorter<>(jmet.iterator());
        List<T> expectedSet = ordering.sort(expected.elementSet(), Function.identity());
        List<T> expectedList = ordering.sort(expected, Function.identity());

        List<IMapEntry<T, Integer>> rawEntries = new ArrayList<>();
        for (Multiset.Entry<T> mentry : expected.entrySet()) {
            rawEntries.add(new MapEntry<>(mentry.getElement(), mentry.getCount()));
        }
        List<IMapEntry<T, Integer>> entries = ordering.sort(rawEntries, e -> e.getKey());
        assertEquals(entries.size(), jmet.size());

        StandardIteratorTests.listIteratorTest(expectedSet, jmet.iterator());
        StandardIteratorTests.iteratorTest(value -> entries.get(value).getKey(), entries.size(), jmet.iterator());

        StandardIteratorTests.listIteratorTest(expectedList, jmet.occurrences().iterator());
        StandardIteratorTests.iteratorTest(value -> expectedList.get(value), expected.size(), jmet.occurrences().iterator());

        StandardIteratorTests.listIteratorTest(entries, jmet.entries().iterator());
        StandardIteratorTests.iteratorTest(value -> entries.get(value), entries.size(), jmet.entries().iterator());

        StandardIterableStreamableTests.verifyUnorderedUsingCollection(jmet.getSet(), jmet);
        StandardIterableStreamableTests.verifyUnorderedUsingCollection(entries, jmet.entries(), MapEntry::new);
        StandardIterableStreamableTests.verifyUnorderedUsingCollection(expectedList, jmet.occurrences());
    }

    private static Set<Integer> asSet(List<Integer> list)
    {
        Set<Integer> set = new LinkedHashSet<>();
        set.addAll(list);
        return set;
    }

    private static ISet<Integer> asJSet(Multiset<Integer> multi)
    {
        ISet<Integer> jet = HashSet.of();
        for (Integer value : multi) {
            jet = jet.insert(value);
        }
        return jet;
    }

    private static ISet<Integer> asJSet(List<Integer> list)
    {
        ISet<Integer> jet = HashSet.of();
        for (int i : list) {
            jet = jet.insert(i);
        }
        return jet;
    }

    private static IMultiset<Integer> asJMSet(Multiset<Integer> multi)
    {
        IMultiset<Integer> jmet = HashMultiset.of();
        for (Integer value : multi) {
            jmet = jmet.insert(value);
        }
        return jmet;
    }

    private static IMultiset<Integer> asJMSet(List<Integer> list)
    {
        IMultiset<Integer> jmet = HashMultiset.of();
        for (int i : list) {
            jmet = jmet.insert(i);
        }
        return jmet;
    }

    private static List<Integer> entryAsList(IMapEntry<Integer, Integer> entry)
    {
        List<Integer> list = new ArrayList<>();
        int count = entry.getValue();
        int value = entry.getKey();
        for (int i = 0; i < count; i++) {
            list.add(value);
        }
        return list;
    }

    // forces java type system to use Iterable version of a method
    private static Iterable<Integer> iterable(Iterable<Integer> object)
    {
        return object;
    }
}
