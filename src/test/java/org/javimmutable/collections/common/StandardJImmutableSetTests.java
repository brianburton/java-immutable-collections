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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.inorder.JImmutableInsertOrderSet;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static junit.framework.Assert.*;

public final class StandardJImmutableSetTests
{
    private StandardJImmutableSetTests()
    {
    }

    public static void verifySet(JImmutableSet<Integer> template)
    {
        testVarious(template);
        testWithMultiset(template);
        testRandom(template);
        testCollector(template.insert(-1).insert(-2));
        testTransform(template);
        verifyIntersectionOrder(template);

        assertEquals(0, template.size());
        assertEquals(true, template.isEmpty());
        assertEquals(false, template.isNonEmpty());
        assertEquals(template, new HashSet<Integer>());
        assertEquals(template.getSet(), new HashSet<Integer>());

        JImmutableSet<Integer> jet = template;
        assertEquals(false, jet.contains(10));
        jet = jet.insert(10);
        assertEquals(true, jet != template);
        assertEquals(1, jet.size());
        assertEquals(false, jet.isEmpty());
        assertEquals(true, jet.isNonEmpty());
        assertEquals(true, jet.contains(10));

        assertEquals((Integer)10, jet.get(10));
        assertEquals((Integer)10, jet.getValueOr(10, 25));
        assertEquals(Holders.of(10), jet.find(10));

        jet = jet.delete(10);
        assertEquals(0, jet.size());
        assertEquals(true, template.isEmpty());
        assertEquals(false, template.isNonEmpty());
        assertEquals(false, jet.contains(10));

        assertEquals(null, jet.get(10));
        assertEquals((Integer)25, jet.getValueOr(10, 25));
        assertEquals(Holders.of(), jet.find(10));

        final List<Integer> values = Arrays.asList(1, 2, 3, 4);
        verifyContents(jet.union(values), values);
        verifyContents(jet.union(values.iterator()), values);

        // intersect with larger set
        jet = template.union(values);
        final List<Integer> withExtra = Arrays.asList(0, 1, 2, 3, 4, 5);
        Set<Integer> intersectionSet = new HashSet<>(withExtra);
        JImmutableSet<Integer> intersectionJet = template.union(withExtra);
        verifyContents(jet.intersection(withExtra), values);
        verifyContents(jet.intersection(withExtra.iterator()), values);
        verifyContents(jet.intersection(intersectionJet), values);
        verifyContents(jet.intersection(intersectionSet), values);

        // intersect with smaller set
        jet = template.union(withExtra);
        intersectionSet = new HashSet<>(values);
        intersectionJet = template.union(values);
        verifyContents(jet.intersection(values), values);
        verifyContents(jet.intersection(values.iterator()), values);
        verifyContents(jet.intersection(intersectionJet), values);
        verifyContents(jet.intersection(intersectionSet), values);

        // empty set intersection with non-empty set
        final List<Integer> empty = Collections.emptyList();
        verifyContents(template.intersection(withExtra), empty);
        verifyContents(template.intersection(withExtra.iterator()), empty);
        verifyContents(template.intersection(intersectionJet), empty);
        verifyContents(template.intersection(intersectionSet), empty);

        // non-empty set intersection with empty set
        intersectionSet = new HashSet<>();
        intersectionJet = template;
        verifyContents(jet.intersection(empty), empty);
        verifyContents(jet.intersection(empty.iterator()), empty);
        verifyContents(jet.intersection(intersectionJet), empty);
        verifyContents(jet.intersection(intersectionSet), empty);

        // deleteAll from smaller set
        final List<Integer> extra = Arrays.asList(0, 5);
        jet = template.union(withExtra);
        verifyContents(jet.deleteAll(values), extra);
        verifyContents(jet.deleteAll(values.iterator()), extra);

        // deleteAll from larger set
        jet = template.union(values);
        verifyContents(jet.deleteAll(withExtra), empty);
        verifyContents(jet.deleteAll(withExtra.iterator()), empty);

        //insertAll
        //empty into empty
        assertSame(template, template.insertAll(plainIterable(template)));
        assertSame(template, template.insertAll(template));
        assertSame(template, template.insertAll(template.iterator()));

        //values into empty
        verifyContents(template.insertAll(plainIterable(values)), values);
        verifyContents(template.insertAll(values), values);
        verifyContents(template.insertAll(values.iterator()), values);

        //empty into values
        jet = template.union(values);
        assertSame(jet, jet.insertAll(plainIterable(template)));
        assertSame(jet, jet.insertAll(template));
        assertSame(jet, jet.insertAll(template.iterator()));

        // union with self should yield same set (no changes)
        if (!(template instanceof JImmutableMultiset)) {
            assertSame(jet, jet.insertAll(plainIterable(jet)));
            assertSame(jet, jet.insertAll(jet));
            assertSame(jet, jet.insertAll(jet.iterator()));
        }

        //values into values
        verifyContents(jet.insertAll(plainIterable(withExtra)), withExtra);
        verifyContents(jet.insertAll(withExtra), withExtra);
        verifyContents(jet.insertAll(withExtra.iterator()), withExtra);

        final List<Integer> higher = Arrays.asList(4, 5, 6, 7);
        jet = template.union(higher);
        final List<Integer> combinedSet = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        verifyContents(jet.insertAll(plainIterable(withExtra)), combinedSet);
        verifyContents(jet.insertAll(withExtra), combinedSet);
        verifyContents(jet.insertAll(withExtra.iterator()), combinedSet);
    }

    // forces java type system to use overload for plain Iterable
    private static <T> Iterable<T> plainIterable(Iterable<T> source)
    {
        return () -> source.iterator();
    }

    private static void testCollector(JImmutableSet<Integer> template)
    {
        Collection<Integer> values = new ArrayList<>();
        for (int i = 1; i <= 10; ++i) {
            values.add(i);
            if (i % 2 == 0) {
                values.add(11 - i);
            }
        }

        if (template instanceof JImmutableMultiset) {
            values = new LinkedHashSet<>(values);
        }
        JImmutableSet<Integer> expected = template.insertAll(values);
        JImmutableSet<Integer> actual = values.parallelStream().collect(template.setCollector());
        assertEquals(expected, actual);
    }

    private static void testVarious(JImmutableSet<Integer> template)
    {
        List<Integer> expected = Arrays.asList(100, 200, 300, 400);

        JImmutableSet<Integer> set = JImmutableHashSet.of();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertEquals(false, set.contains(100));
        assertEquals(false, set.contains(200));
        assertEquals(false, set.contains(300));
        assertEquals(false, set.contains(400));
        assertEquals(false, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));

        set = set.insert(100);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertEquals(true, set.contains(100));
        assertEquals(false, set.contains(200));
        assertEquals(false, set.contains(300));
        assertEquals(false, set.contains(400));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));

        set = set.insert(200);
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertEquals(true, set.contains(100));
        assertEquals(true, set.contains(200));
        assertEquals(false, set.contains(300));
        assertEquals(false, set.contains(400));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));

        assertSame(set, set.insert(100));
        assertSame(set, set.insert(200));

        JImmutableSet<Integer> set2 = set.union(expected);
        assertFalse(set2.isEmpty());
        assertEquals(4, set2.size());
        assertEquals(true, set2.contains(100));
        assertEquals(true, set2.contains(200));
        assertEquals(true, set2.contains(300));
        assertEquals(true, set2.contains(400));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(true, set2.containsAll(expected));
        assertEquals(new HashSet<>(Arrays.asList(100, 200, 300, 400)), set2.getSet());

        assertEquals(set, set.intersection(set2));
        assertEquals(set, set2.intersection(set));
        assertEquals(set, set2.delete(300).delete(400));

        set2 = set2.deleteAll(set);
        assertFalse(set2.isEmpty());
        assertEquals(2, set2.size());
        assertEquals(false, set2.contains(100));
        assertEquals(false, set2.contains(200));
        assertEquals(true, set2.contains(300));
        assertEquals(true, set2.contains(400));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(false, set2.containsAny(set));
        assertEquals(false, set2.containsAll(expected));

        set2 = set2.insertAll(set);
        assertFalse(set2.isEmpty());
        assertEquals(4, set2.size());
        assertEquals(true, set2.contains(100));
        assertEquals(true, set2.contains(200));
        assertEquals(true, set2.contains(300));
        assertEquals(true, set2.contains(400));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(true, set2.containsAny(set));
        assertEquals(true, set2.containsAll(expected));
        assertEquals(true, set2.containsAll(set));
        assertEquals(new HashSet<>(Arrays.asList(100, 200, 300, 400)), set2.getSet());

        set2 = set2.deleteAll(set);

        JImmutableSet<Integer> set3 = set.union(expected).insert(500).insert(600);
        assertFalse(set3.isEmpty());
        assertEquals(6, set3.size());
        assertEquals(true, set3.contains(100));
        assertEquals(true, set3.contains(200));
        assertEquals(true, set3.contains(300));
        assertEquals(true, set3.contains(400));
        assertEquals(true, set3.contains(500));
        assertEquals(true, set3.contains(600));
        assertEquals(true, set3.containsAny(expected));
        assertEquals(true, set3.containsAny(set));
        assertEquals(true, set3.containsAny(set2));
        assertEquals(true, set3.containsAll(expected));
        assertEquals(true, set3.containsAll(set));
        assertEquals(true, set3.containsAll(set2));
        assertEquals(new HashSet<>(Arrays.asList(100, 200, 300, 400, 500, 600)), set3.getSet());
        assertEquals(set, set3.intersection(set));
        assertEquals(set2, set3.intersection(set2));
        assertEquals(set, set.intersection(set));
        assertEquals(set, set.intersection(set3));
        assertEquals(template, set.intersection(set2));
        assertEquals(template, set2.intersection(set));
        assertEquals(template, set3.deleteAll(set3));
    }

    private static void verifyIntersectionOrder(JImmutableSet<Integer> template)
    {
        JImmutableSet<Integer> jet = template.insert(100).insert(50).insert(100).insert(600).insert(0).insert(400);
        final List<Integer> expected = new ArrayList<>();
        expected.addAll(jet.getSet());
        StandardIteratorTests.listIteratorTest(expected, jet.iterator());

        JImmutableSet<Integer> diffOrder = JImmutableInsertOrderSet.<Integer>of().insert(400).insert(0).insert(600)
            .insert(100).insert(50).insert(100);
        //Iterable
        jet = jet.intersection(iterable(diffOrder));
        StandardIteratorTests.listIteratorTest(expected, jet.iterator());
        //Iterable
        jet = jet.intersection(Arrays.asList(400, 0, 600, 100, 50, 100));
        StandardIteratorTests.listIteratorTest(expected, jet.iterator());
        //Set
        jet = jet.intersection(diffOrder.getSet());
        StandardIteratorTests.listIteratorTest(expected, jet.iterator());
        //JSet
        jet = jet.intersection(diffOrder);
        StandardIteratorTests.listIteratorTest(expected, jet.iterator());
    }

    private static void testWithMultiset(JImmutableSet<Integer> template)
    {
        JImmutableSet<Integer> multi = JImmutables.multiset();
        JImmutableSet<Integer> jet = template;

        //intersection(Jet)
        //empty into empty
        List<Integer> expected = new ArrayList<>();
        verifyContents(template.intersection(multi), expected);

        //values into empty
        multi = multi.insert(0).insert(0).insert(1).insert(3).insert(3).insert(3);
        verifyContents(template.intersection(multi), expected);

        //empty into values
        multi = multi.deleteAll();
        jet = jet.insert(0).insert(1).insert(2);
        verifyContents(jet.intersection(multi), expected);

        //values into values
        multi = multi.insert(0).insert(0).insert(1).insert(3).insert(3).insert(3);
        expected = Arrays.asList(0, 1);
        verifyContents(jet.intersection(multi), expected);

        //equals(Jet)
        multi = multi.deleteAll().insert(0).insert(1).insert(2);
        assertTrue(jet.equals(multi));
        assertTrue(multi.equals(jet));
        multi = multi.insert(0);
        assertFalse(jet.equals(multi));
        assertFalse(multi.equals(jet));
        assertTrue(jet.equals(multi.getSet()));
    }

    private static void testRandom(JImmutableSet<Integer> template)
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            Set<Integer> expected = new HashSet<>();
            JImmutableSet<Integer> set = template;

            for (int loops = 0; loops < (4 * size); ++loops) {
                int command = random.nextInt(5);
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
                    case 4:
                        JImmutableSet<Integer> values = template;
                        int times = random.nextInt(4);
                        for (int rep = 0; rep < times; ++rep) {
                            int num = random.nextInt(size);
                            expected.add(num);
                            values = values.insert(num);
                        }
                        set = set.insertAll(values);
                        assertEquals(true, set.containsAll(values));
                        break;
                }
                assertEquals(expected.size(), set.size());
            }
            assertEquals(expected, set.getSet());
            if (!(set instanceof JImmutableMultiset)) {
                for (Integer value : set) {
                    assertSame(set, set.insert(value));
                }
            }
            for (Integer value : set) {
                set = set.delete(value);
            }
            assertEquals(0, set.size());
            assertEquals(true, set.isEmpty());
        }
    }

    private static void testTransform(JImmutableSet<Integer> template)
    {
        JImmutableSet<Integer> ints = template;
        JImmutableSet<String> strings = template.transform(String::valueOf);
        List<String> expected = new ArrayList<>();
        verifyContents(strings, expected);
        for (int i = 1; i <= 100; ++i) {
            ints = ints.insert(i);
            strings = ints.transform(String::valueOf);
            assertEquals(ints.getClass(), strings.getClass());
            expected.add(String.valueOf(i));
            verifyContents(strings, expected);
        }
    }

    // forces java type system to use Iterable version of method
    private static <T> Iterable<T> iterable(Iterable<T> collection)
    {
        return collection;
    }

    private static <T> void verifyContents(JImmutableSet<T> jet,
                                           List<T> expected)
    {
        assertEquals(expected.isEmpty(), jet.isEmpty());
        assertEquals(expected.size(), jet.size());
        for (T value : expected) {
            assertEquals(true, jet.contains(value));
        }
        assertEquals(true, jet.containsAll(expected));
        assertEquals(true, jet.containsAll(expected.iterator()));

        assertEquals(!expected.isEmpty(), jet.containsAny(expected));
        assertEquals(!expected.isEmpty(), jet.containsAny(expected.iterator()));

        if (!expected.isEmpty()) {
            List<T> subset = Arrays.asList(expected.get(0));
            assertEquals(true, jet.containsAll(subset));
            assertEquals(true, jet.containsAll(subset.iterator()));

            assertEquals(true, jet.containsAny(subset));
            assertEquals(true, jet.containsAny(subset.iterator()));
        }

        StandardIterableStreamableTests.verifyUnorderedUsingCollection(expected, jet);
    }
}
