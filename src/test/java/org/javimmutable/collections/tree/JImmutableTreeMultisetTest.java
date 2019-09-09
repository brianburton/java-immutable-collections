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

package org.javimmutable.collections.tree;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.StandardJImmutableMultisetTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.TestUtil;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class JImmutableTreeMultisetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableMultisetTests.verifyMultiset(JImmutableTreeMultiset.of());
        StandardJImmutableMultisetTests.testRandom(JImmutableTreeMultiset.of(),
                                                   TreeMultiset.create());
    }

    public void test()
    {
        Multiset<String> values = TreeMultiset.create();
        values.add("tennant", 10);
        values.add("smith", 11);
        values.add("capaldi", 12);
        values.add("eccleston", 9);
        final Set<String> valueSet = values.elementSet();
        final List<String> valueList = new ArrayList<>(values);

        JImmutableMultiset<String> jmet = JImmutableTreeMultiset.of();
        Multiset<String> expected = TreeMultiset.create();
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
        StandardJImmutableMultisetTests.verifyIterators(jmet, expected);

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
        StandardJImmutableMultisetTests.verifyIterators(jmet, expected);

        assertSame(jmet, jmet.union(Arrays.asList("tennant", "smith")));
        assertSame(jmet, jmet.union(TestUtil.makeSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));
        StandardJImmutableMultisetTests.verifyIterators(jmet.union(Arrays.asList("tennant", "smith")), expected);

        JImmutableMultiset<String> jmet2 = jmet.union(valueSet);
        jmet2.checkInvariants();
        Multiset<String> expected2 = TreeMultiset.create();
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
        StandardJImmutableMultisetTests.verifyIterators(jmet2, expected2);

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi").deleteOccurrence("eccleston"));
        StandardJImmutableMultisetTests.verifyIterators(jmet2.intersection(jmet), expected);

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
        StandardJImmutableMultisetTests.verifyIterators(jmet2, expected2);

        assertEquals(jmet, jmet2.delete("capaldi").delete("eccleston"));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi", 12).deleteOccurrence("eccleston", 9));
        assertEquals(jmet, jmet2.deleteAll(Arrays.asList("capaldi", "eccleston")));
        StandardJImmutableMultisetTests.verifyIterators(jmet2.delete("capaldi").delete("eccleston"), expected);

        Multiset<String> extra = TreeMultiset.create();
        extra.add("eccleston", 9);
        extra.add("capaldi", 12);

        assertEquals(jmet, jmet2.deleteAllOccurrences(extra));
        assertEquals(jmet, jmet2.deleteAllOccurrences(asJMet(extra)));
        assertEquals(jmet2, jmet.insertAll(extra));
        assertEquals(jmet2, jmet.insertAll(asJMet(extra)));
        assertEquals(JImmutableTreeMultiset.<String>of(), jmet2.deleteAll());
        StandardJImmutableMultisetTests.verifyIterators(jmet2.deleteAll(extra), expected);
        StandardJImmutableMultisetTests.verifyIterators(jmet.insertAll(extra), expected2);

        JImmutableMultiset<String> jmet3 = asJMet(valueSet).insert("davison").insert("baker");
        jmet3.checkInvariants();
        Multiset<String> expected3 = TreeMultiset.create(Arrays.asList("tennant", "smith", "capaldi", "eccleston", "davison", "baker"));
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
        StandardJImmutableMultisetTests.verifyIterators(jmet3, expected3);

        JImmutableMultiset<String> jmet4 = asJMet(Arrays.asList("tennant", "smith", "capaldi", "eccleston"));
        jmet4.checkInvariants();
        assertEquals(jmet4, jmet3.intersection(valueSet));
        assertEquals(jmet4, jmet3.intersection(TestUtil.makeSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(jmet4, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(jmet4, jmet3.intersection(jmet4));
        StandardJImmutableMultisetTests.verifyIterators(jmet3.intersection(valueSet), TreeMultiset.create(Arrays.asList("tennant", "smith", "capaldi", "eccleston")));
    }

    public void testSortOrder()
    {
        Comparator<Integer> reverser = (a, b) -> -a.compareTo(b);

        Multiset<Integer> expected = TreeMultiset.create(reverser);
        JImmutableMultiset<Integer> jmet = JImmutableTreeMultiset.of(reverser);
        Random random = new Random(2500L);
        for (int i = 0; i < 10000; ++i) {
            int value = random.nextInt(100000) - 50000;
            expected.add(value);
            jmet = jmet.insert(value);
        }
        jmet.checkInvariants();
        assertEquals(expected.elementSet(), jmet.getSet());
        assertEquals(new ArrayList<>(expected), asList(jmet));
        StandardJImmutableMultisetTests.verifyIterators(jmet, expected);
    }

    public void testDeleteAll()
    {
        JImmutableTreeMultiset<Integer> jmet = JImmutableTreeMultiset.of();
        jmet = (JImmutableTreeMultiset<Integer>)jmet.insert(1).insert(1).insert(3);
        jmet.checkInvariants();
        JImmutableTreeMultiset<Integer> cleared = jmet.deleteAll();
        cleared.checkInvariants();
        assertNotSame(JImmutableTreeMultiset.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertEquals(0, cleared.occurrenceCount());
        assertSame(jmet.getComparator(), cleared.getComparator());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());

        jmet = JImmutableTreeMultiset.of((a, b) -> -b.compareTo(a));
        jmet = (JImmutableTreeMultiset<Integer>)jmet.insert(1).insert(1).insert(3);
        jmet.checkInvariants();
        cleared = jmet.deleteAll();
        cleared.checkInvariants();
        assertNotSame(JImmutableTreeMultiset.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertEquals(0, cleared.occurrenceCount());
        assertSame(jmet.getComparator(), cleared.getComparator());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());
    }

    public void testStreams()
    {
        JImmutableMultiset<Integer> mset = JImmutableTreeMultiset.<Integer>of().insert(4).insert(3).insert(4).insert(2).insert(1).insert(3);
        assertEquals(Arrays.asList(1, 2, 3, 4), mset.stream().collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableMultiset)a).entries().iterator();
        JImmutableMultiset<String> empty = JImmutableTreeMultiset.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeMultisetTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihi8SDDUMam4pCgxuQRhOE6DC8pZGBiYXwJdbYbXghKg6/Sc83MLEotAclBWSX4RzDAmmGFAGgAiIJUFCAEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeMultisetTest::extraSerializationChecks, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihi8SDDUMam4pCgxuQRhOE6DC8pZGBiYXwJdbYbXghKg6/Sc83MLEotAclBWSX4RzDAmmGEMjCUMjIkQVgUAOKKVcBIBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeMultisetTest::extraSerializationChecks, empty.insertAll(Arrays.asList("a", "B", "c", "D")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihi8SDDUMam4pCgxuQRhOE6DC8pZGBiYXwJdbYbXghKg6/Sc83MLEotAclBWSX4RzDAmmGEMLCUMjE5gFiOQ5QJnJcJZyRBWBQDaY1yQMAEAAA==");

        empty = JImmutableTreeMultiset.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeMultisetTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfUtzSnJLE4tCSjKr6j8DwL/VIx5GBgqihi8SDDUMam4pCgxuQRhOE6DC8pZGBiYXwJdrQU0PFEvJzEvXS+4pCgzL13FObE41TOvODWvOLMksyzVOT+3ILEosSS/qJw5pjYm4Ok5JpgBQBoAHgvJTvwAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeMultisetTest::extraSerializationChecks, empty.insert("a"),
                                                     "H4sIAAAAAAAAAJXOOwrCQBAG4ImPzmOkstjGE0hASEAIaGkzCUNY2eyG2clDwRt5Fm9hYeEVfAQJVhZO9f/w8zHnO0w9w8pxofbY6LKsBTNDKnfGUC7aWa88sUajj9hXlcTDaMtE69qI9iQpu+7w7O8RLmYAHUPyB7rMvDDm8sV/wlU7ARjf3l/P3zgqg7ZQG2FtizBCT7H1ZL0W3VDkygoZxXE73p126fUyGgAIBAL8pO4F+ht7RgYBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, JImmutableTreeMultisetTest::extraSerializationChecks, empty.insertAll(Arrays.asList("a", "B", "c", "D")),
                                                     "H4sIAAAAAAAAAJXOOwrCQBCA4dHEzmNYWWzjCXwgJCAEtLQZwxBWNrsyO3koeCPP4i0sLLyCGhW1snCqf2D4mMMFOp5h6jhTayx1nheCK0MqdcZQKtpZrzyxRqN3+FhVHL2PFkw0K4xoT5Kwq7e3x1x7gy5AzRD/gQ5XXhhT+eI/4U0VAgTn5ut+g6MyaDM1F9Y2643RU2Q9Wa9FlzR2+QYZxXEVLPfL5HRsvwEIBVr4rFZTo0+ln5q8qr4D942AaCQBAAA=");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        JImmutableTreeMultiset setA = (JImmutableTreeMultiset)a;
        JImmutableTreeMultiset setB = (JImmutableTreeMultiset)b;
        assertEquals(setA.getComparator(), setB.getComparator());
        JImmutableTreeMapTest.extraSerializationChecks(setA.getMap(), setB.getMap());
    }

    private JImmutableSet<String> asJSet(String... args)
    {
        JImmutableSet<String> jet = JImmutableTreeSet.of();
        for (String arg : args) {
            jet = jet.insert(arg);
        }
        return jet;
    }

    private JImmutableMultiset<String> asJMet(String... args)
    {
        JImmutableMultiset<String> jmet = JImmutableTreeMultiset.of();
        for (String arg : args) {
            jmet = jmet.insert(arg);
        }
        return jmet;
    }

    private JImmutableMultiset<String> asJMet(Collection<String> collect)
    {
        JImmutableMultiset<String> jmet = JImmutableTreeMultiset.of();
        return jmet.insertAll(collect);
    }

    private ArrayList<Integer> asList(JImmutableMultiset<Integer> jmet)
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (Integer value : jmet.occurrences()) {
            list.add(value);
        }
        return list;
    }
}