///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection.tree;

import com.google.common.collect.Multiset;
import junit.framework.TestCase;
import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IMultiset;
import org.javimmutable.collection.ISet;
import org.javimmutable.collection.common.StandardMultisetTests;
import org.javimmutable.collection.common.StandardSerializableTests;
import org.javimmutable.collection.common.TestUtil;
import org.javimmutable.collection.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class TreeMultisetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardMultisetTests.verifyMultiset(TreeMultiset.of(), false);
        StandardMultisetTests.testRandom(TreeMultiset.of(),
                                         com.google.common.collect.TreeMultiset.create());
    }

    public void test()
    {
        Multiset<String> values = com.google.common.collect.TreeMultiset.create();
        values.add("tennant", 10);
        values.add("smith", 11);
        values.add("capaldi", 12);
        values.add("eccleston", 9);
        final Set<String> valueSet = values.elementSet();
        final List<String> valueList = new ArrayList<>(values);

        IMultiset<String> jmet = TreeMultiset.of();
        Multiset<String> expected = com.google.common.collect.TreeMultiset.create();
        assertTrue(jmet.isEmpty());
        assertEquals(0, jmet.size());
        assertEquals(0, jmet.occurrenceCount());
        assertEquals(false, jmet.contains("tennant"));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(false, jmet.containsAny(valueSet));
        assertEquals(false, jmet.containsAll(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueList));
        StandardIteratorTests.emptyIteratorTest(jmet.iterator());
        StandardIteratorTests.emptyIteratorTest(jmet.occurrences().iterator());
        StandardIteratorTests.emptyIteratorTest(jmet.entries().iterator());

        jmet = jmet.insert("TENNANT".toLowerCase(), 10);
        jmet.checkInvariants();
        expected.add("TENNANT".toLowerCase(), 10);
        assertFalse(jmet.isEmpty());
        assertEquals(1, jmet.size());
        assertEquals(10, jmet.occurrenceCount());
        assertEquals(true, jmet.containsAtLeast("tennant", 10));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valueSet));
        assertEquals(false, jmet.containsAll(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueList));
        StandardMultisetTests.verifyIterators(jmet, expected);

        jmet = jmet.insert("SMITH".toLowerCase(), 11);
        jmet.checkInvariants();
        expected.add("SMITH".toLowerCase(), 11);
        assertFalse(jmet.isEmpty());
        assertEquals(2, jmet.size());
        assertEquals(21, jmet.occurrenceCount());
        assertEquals(true, jmet.containsAtLeast("tennant", 10));
        assertEquals(true, jmet.containsAtLeast("smith", 11));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valueSet));
        assertEquals(false, jmet.containsAll(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueList));
        StandardMultisetTests.verifyIterators(jmet, expected);

        assertSame(jmet, jmet.union(Arrays.asList("tennant", "smith")));
        assertSame(jmet, jmet.union(TestUtil.makeSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));
        StandardMultisetTests.verifyIterators(jmet.union(Arrays.asList("tennant", "smith")), expected);

        IMultiset<String> jmet2 = jmet.union(valueSet);
        jmet2.checkInvariants();
        Multiset<String> expected2 = com.google.common.collect.TreeMultiset.create();
        expected2.addAll(values);
        expected2.setCount("capaldi", 1);
        expected2.setCount("eccleston", 1);
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(23, jmet2.occurrenceCount());
        assertEquals(true, jmet2.containsAtLeast("tennant", 10));
        assertEquals(true, jmet2.containsAtLeast("smith", 11));
        assertEquals(true, jmet2.contains("capaldi"));
        assertEquals(true, jmet2.contains("eccleston"));
        assertEquals(true, jmet2.containsAny(valueSet));
        assertEquals(true, jmet2.containsAll(valueSet));
        assertEquals(true, jmet2.containsAllOccurrences(valueSet));
        assertEquals(false, jmet2.containsAllOccurrences(valueList));
        assertEquals(TestUtil.makeSet("tennant", "smith", "capaldi", "eccleston"), jmet2.getSet());
        StandardMultisetTests.verifyIterators(jmet2, expected2);

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi").deleteOccurrence("eccleston"));
        StandardMultisetTests.verifyIterators(jmet2.intersection(jmet), expected);

        jmet2 = jmet2.union(values);
        jmet2.checkInvariants();
        expected2.clear();
        expected2.addAll(values);
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(42, jmet2.occurrenceCount());
        assertEquals(true, jmet2.containsAtLeast("tennant", 10));
        assertEquals(true, jmet2.containsAtLeast("smith", 11));
        assertEquals(true, jmet2.containsAtLeast("capaldi", 12));
        assertEquals(true, jmet2.containsAtLeast("eccleston", 9));
        assertEquals(true, jmet2.containsAny(valueSet));
        assertEquals(true, jmet2.containsAll(valueSet));
        assertEquals(true, jmet2.containsAllOccurrences(valueSet));
        assertEquals(true, jmet2.containsAllOccurrences(valueList));
        StandardMultisetTests.verifyIterators(jmet2, expected2);

        assertEquals(jmet, jmet2.delete("capaldi").delete("eccleston"));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi", 12).deleteOccurrence("eccleston", 9));
        assertEquals(jmet, jmet2.deleteAll(Arrays.asList("capaldi", "eccleston")));
        StandardMultisetTests.verifyIterators(jmet2.delete("capaldi").delete("eccleston"), expected);

        Multiset<String> extra = com.google.common.collect.TreeMultiset.create();
        extra.add("eccleston", 9);
        extra.add("capaldi", 12);

        assertEquals(jmet, jmet2.deleteAllOccurrences(extra));
        assertEquals(jmet, jmet2.deleteAllOccurrences(asJMet(extra)));
        assertEquals(jmet2, jmet.insertAll(extra));
        assertEquals(jmet2, jmet.insertAll(asJMet(extra)));
        assertEquals(TreeMultiset.<String>of(), jmet2.deleteAll());
        StandardMultisetTests.verifyIterators(jmet2.deleteAll(extra), expected);
        StandardMultisetTests.verifyIterators(jmet.insertAll(extra), expected2);

        IMultiset<String> jmet3 = asJMet(valueSet).insert("davison").insert("baker");
        jmet3.checkInvariants();
        Multiset<String> expected3 = com.google.common.collect.TreeMultiset.create(Arrays.asList("tennant", "smith", "capaldi", "eccleston", "davison", "baker"));
        assertFalse(jmet3.isEmpty());
        assertEquals(6, jmet3.size());
        assertEquals(6, jmet3.occurrenceCount());
        assertEquals(true, jmet3.contains("tennant"));
        assertEquals(true, jmet3.contains("smith"));
        assertEquals(true, jmet3.contains("capaldi"));
        assertEquals(true, jmet3.contains("eccleston"));
        assertEquals(true, jmet3.contains("davison"));
        assertEquals(true, jmet3.contains("baker"));
        assertEquals(true, jmet3.containsAny(valueSet));
        assertEquals(true, jmet3.containsAny(jmet));
        assertEquals(true, jmet3.containsAny(jmet2));
        assertEquals(true, jmet3.containsAll(valueSet));
        assertEquals(true, jmet3.containsAll(jmet));
        assertEquals(true, jmet3.containsAll(jmet2));
        assertEquals(true, jmet3.containsAllOccurrences(valueSet));
        assertEquals(false, jmet3.containsAllOccurrences(jmet));
        assertEquals(false, jmet3.containsAllOccurrences(jmet2));
        StandardMultisetTests.verifyIterators(jmet3, expected3);

        IMultiset<String> jmet4 = asJMet(Arrays.asList("tennant", "smith", "capaldi", "eccleston"));
        jmet4.checkInvariants();
        assertEquals(jmet4, jmet3.intersection(valueSet));
        assertEquals(jmet4, jmet3.intersection(TestUtil.makeSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(jmet4, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(jmet4, jmet3.intersection(jmet4));
        StandardMultisetTests.verifyIterators(jmet3.intersection(valueSet), com.google.common.collect.TreeMultiset.create(Arrays.asList("tennant", "smith", "capaldi", "eccleston")));
    }

    public void testSortOrder()
    {
        Comparator<Integer> reverser = (a, b) -> -a.compareTo(b);

        Multiset<Integer> expected = com.google.common.collect.TreeMultiset.create(reverser);
        IMultiset<Integer> jmet = TreeMultiset.of(reverser);
        Random random = new Random(2500L);
        for (int i = 0; i < 10000; ++i) {
            int value = random.nextInt(100000) - 50000;
            expected.add(value);
            jmet = jmet.insert(value);
        }
        jmet.checkInvariants();
        assertEquals(expected.elementSet(), jmet.getSet());
        assertEquals(new ArrayList<>(expected), asList(jmet));
        StandardMultisetTests.verifyIterators(jmet, expected);
    }

    public void testDeleteAll()
    {
        TreeMultiset<Integer> jmet = TreeMultiset.of();
        jmet = (TreeMultiset<Integer>)jmet.insert(1).insert(1).insert(3);
        jmet.checkInvariants();
        TreeMultiset<Integer> cleared = jmet.deleteAll();
        cleared.checkInvariants();
        assertNotSame(TreeMultiset.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertEquals(0, cleared.occurrenceCount());
        assertSame(jmet.getComparator(), cleared.getComparator());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());

        jmet = TreeMultiset.of((a, b) -> -b.compareTo(a));
        jmet = (TreeMultiset<Integer>)jmet.insert(1).insert(1).insert(3);
        jmet.checkInvariants();
        cleared = jmet.deleteAll();
        cleared.checkInvariants();
        assertNotSame(TreeMultiset.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertEquals(0, cleared.occurrenceCount());
        assertSame(jmet.getComparator(), cleared.getComparator());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());
    }

    public void testStreams()
    {
        IMultiset<Integer> mset = TreeMultiset.<Integer>of().insert(4).insert(3).insert(4).insert(2).insert(1).insert(3);
        assertEquals(Arrays.asList(1, 2, 3, 4), mset.stream().collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IMultiset)a).entries().iterator();
        IMultiset<String> empty = TreeMultiset.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMultisetTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihjsiTfJMam4pCgxuQSnaQXlLAwMzC+B7jPFZ2oJ0EV6zvm5BYlFICkoqyS/CGYWE8wsIA0AkuIXpfEAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMultisetTest::extraSerializationChecks, empty.insert("a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihjsiTfJMam4pCgxuQSnaQXlLAwMzC+B7jPFZ2oJ0EV6zvm5BYlFICkoqyS/CGYWE8wsBsYSBsZECKsCAAZT5z77AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMultisetTest::extraSerializationChecks, empty.insertAll(Arrays.asList("a", "B", "c", "D")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihjsiTfJMam4pCgxuQSnaQXlLAwMzC+B7jPFZ2oJ0EV6zvm5BYlFICkoqyS/CGYWE8wsBpYSBkYnMIsRyHKBsxLhrGQIqwIArJcshxkBAAA=");

        empty = TreeMultiset.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMultisetTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBOr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihjsiTfJMam4pCgxuQSnaQXlLAwMzC+B7tMCmpiol5OYl64XXFKUmZeu4pxYnOqZV5yaV5xZklmW6pyfW5BYlFiSX1TOHFMbE/D0HBPMACANAPPdPA3mAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMultisetTest::extraSerializationChecks, empty.insert("a"),
                                                     "H4sIAAAAAAAA/43OPQrCQBAF4NFo5zFSWWxjaSFiZSEEtLSZhCGMbHbD7ORHwRt5Fm9hYeEV1KhYCr7qvebjnW4wDAJTL7nZYc1FUSmmlkzmraVM2TsTSBgtH/C9NkK0qqxyIE3Et/vHK/d4MgJoBWb/S/M0qGCmP7WyGQBE1+7fuBPRWHS5Wauwy+MFBlq6QC6wck0LX5QoqF6aaHvcJpdz/wtAT6GHn9Y+AULQEiHwAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMultisetTest::extraSerializationChecks, empty.insertAll(Arrays.asList("a", "B", "c", "D")),
                                                     "H4sIAAAAAAAA/43OMQ4BURDG8VlL5xgqxWuUCmE1ComEcpuxmWxG3r4n82ZZEjdyFrdQKFwBi9BJTPWb5p/veIVWEOh7yc0KN1wUpeLSksm8tZQpe2cCCaPlPb6+hRBNS6scSGfiq939ebdOrw1QCQz+Lw2XQQUz/Vlbb5sA8aXe162LaCy63MxV2OWdBANNXCAXWHlDiS/WKKhetnF6SGfnU+MTgKZChC9FtUZfZV+N36oe+ZwD+w4BAAA=");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeMultiset setA = (TreeMultiset)a;
        TreeMultiset setB = (TreeMultiset)b;
        assertEquals(setA.getComparator(), setB.getComparator());
        TreeMapTest.extraSerializationChecks(setA.getMap(), setB.getMap());
    }

    private ISet<String> asJSet(String... args)
    {
        ISet<String> jet = TreeSet.of();
        for (String arg : args) {
            jet = jet.insert(arg);
        }
        return jet;
    }

    private IMultiset<String> asJMet(String... args)
    {
        IMultiset<String> jmet = TreeMultiset.of();
        for (String arg : args) {
            jmet = jmet.insert(arg);
        }
        return jmet;
    }

    private IMultiset<String> asJMet(Collection<String> collect)
    {
        IMultiset<String> jmet = TreeMultiset.of();
        return jmet.insertAll(collect);
    }

    private ArrayList<Integer> asList(IMultiset<Integer> jmet)
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (Integer value : jmet.occurrences()) {
            list.add(value);
        }
        return list;
    }
}
