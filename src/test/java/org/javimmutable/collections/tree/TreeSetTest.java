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

package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.common.SetBuilderTestAdapter;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.StandardSetTests;
import org.javimmutable.collections.common.StandardStreamableTests;
import org.javimmutable.collections.common.TestUtil;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class TreeSetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardSetTests.verifySet(TreeSet.of(), false);
        StandardIteratorTests.emptyIteratorTest(TreeSet.<Integer>of().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), TreeSet.<Integer>of().union(Arrays.asList(1, 2, 3)).iterator());
    }

    @SuppressWarnings("OverlyLongMethod")
    public void test()
    {
        Iterable<String> expected = Arrays.asList("fred", "wilma", "betty", "barney");

        ISet<String> set = TreeSet.of();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertEquals(false, set.contains("fred"));
        assertEquals(false, set.contains("wilma"));
        assertEquals(false, set.contains("betty"));
        assertEquals(false, set.contains("barney"));
        assertEquals(false, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));
        StandardIteratorTests.emptyIteratorTest(set.iterator());

        set = set.insert("FRED".toLowerCase());
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertEquals(true, set.contains("fred"));
        assertEquals(false, set.contains("wilma"));
        assertEquals(false, set.contains("betty"));
        assertEquals(false, set.contains("barney"));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));
        StandardIteratorTests.listIteratorTest(Arrays.asList("fred"), set.iterator());

        set = set.insert("WILMA".toLowerCase());
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertEquals(true, set.contains("fred"));
        assertEquals(true, set.contains("wilma"));
        assertEquals(false, set.contains("betty"));
        assertEquals(false, set.contains("barney"));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));
        StandardIteratorTests.listIteratorTest(Arrays.asList("fred", "wilma"), set.iterator());

        assertSame(set, set.insert("fred"));
        assertSame(set, set.insert("wilma"));
        StandardIteratorTests.listIteratorTest(Arrays.asList("fred", "wilma"), set.iterator());

        ISet<String> set2 = set.union(expected);
        assertFalse(set2.isEmpty());
        assertEquals(4, set2.size());
        assertEquals(true, set2.contains("fred"));
        assertEquals(true, set2.contains("wilma"));
        assertEquals(true, set2.contains("betty"));
        assertEquals(true, set2.contains("barney"));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(true, set2.containsAll(expected));
        assertEquals(new java.util.TreeSet<>(Arrays.asList("fred", "wilma", "betty", "barney")), set2.getSet());
        StandardIteratorTests.listIteratorTest(Arrays.asList("barney", "betty", "fred", "wilma"), set2.iterator());

        assertEquals(set, set.intersection(set2));
        assertEquals(set, set2.intersection(set));
        assertEquals(set, set2.delete("betty").delete("barney"));
        StandardIteratorTests.listIteratorTest(Arrays.asList("barney", "betty", "fred", "wilma"), set2.iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList("fred", "wilma"), set2.delete("betty").delete("barney").iterator());

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
        StandardIteratorTests.listIteratorTest(Arrays.asList("barney", "betty"), set2.iterator());

        ISet<String> set3 = set.union(expected).insert("homer").insert("marge");
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
        assertEquals(new java.util.TreeSet<>(Arrays.asList("fred", "wilma", "betty", "barney", "homer", "marge")), set3.getSet());
        assertEquals(set, set3.intersection(set));
        assertEquals(set2, set3.intersection(set2));
        assertEquals(set, set.intersection(set));
        assertEquals(set, set.intersection(set3));
        assertEquals(TreeSet.<String>of(), set.intersection(set2));
        assertEquals(TreeSet.<String>of(), set2.intersection(set));
        assertEquals(TreeSet.<String>of(), set3.deleteAll(set3));
    }

    public void testRandom()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            Set<Integer> expected = new java.util.TreeSet<>();
            ISet<Integer> set = TreeSet.of();
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
            assertEquals(new ArrayList<>(expected), new ArrayList<>(set.getSet()));

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
        final Comparator<Integer> reverser = (a, b) -> -a.compareTo(b);

        Set<Integer> expected = new java.util.TreeSet<>(reverser);
        ISet<Integer> set = TreeSet.of(reverser);
        Random random = new Random(2500L);
        for (int i = 0; i < 10000; ++i) {
            int value = random.nextInt(100000) - 50000;
            expected.add(value);
            set = set.insert(value);
        }
        assertEquals(expected, set.getSet());
        assertEquals(new ArrayList<>(expected), new ArrayList<>(set.getSet()));
        StandardIteratorTests.listIteratorTest(new ArrayList<>(expected), set.iterator());
    }

    public void testDeleteAll()
    {
        TreeSet<Integer> map = TreeSet.of();
        map = (TreeSet<Integer>)map.insert(1).insert(3);
        TreeSet<Integer> cleared = map.deleteAll();
        assertNotSame(TreeSet.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());

        map = TreeSet.of((a, b) -> -b.compareTo(a));
        map = (TreeSet<Integer>)map.insert(1).insert(3);
        cleared = map.deleteAll();
        assertNotSame(TreeSet.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());
    }

    @SuppressWarnings("unchecked")
    public void testIntersectWithStringComparisonDifference()
    {
        // from hash set standpoint HELLO and Hello are NOT the same String so an intersection
        // should yield an empty set.  However that's not how java.util.Set works
        Set<String> hset = new HashSet<>(asList("Hello"));
        Set<String> tset = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        tset.add("HELLO");
        // membership tests show no overlap
        assertEquals(true, hset.contains("Hello"));
        assertEquals(false, hset.contains("HELLO"));
        assertEquals(false, hset.containsAll(tset));
        // yet retainAll() retains the mis-matched string because TreeSet says they are the same
        hset.retainAll(tset);
        assertEquals(asList("Hello"), TestUtil.makeList(hset));

        // reversing who receives the retainAll() call results in different answer!
        // TreeSet does not retain the HELLO value because HashSet says it's not a member
        hset = new HashSet<>(asList("Hello"));
        tset = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        tset.add("HELLO");
        // membership tests show complete overlap
        assertEquals(true, tset.contains("Hello"));
        assertEquals(true, tset.contains("HELLO"));
        assertEquals(true, tset.containsAll(hset));
        // yet retainAll() drops the matching String because HashSet says they are not the same
        tset.retainAll(hset);
        assertEquals(asList(), TestUtil.makeList(tset));

        // JImmutableSet inherits Set's retainAll() behavior in intersection(Set)
        // since the treeset's idea of equality is broader than the hash set's intersection works as expected
        hset = new HashSet<>(asList("Hello"));
        ISet<String> jet = TreeSet.of(String.CASE_INSENSITIVE_ORDER).insert("Hello");
        // same as Set
        assertEquals(true, jet.contains("Hello"));
        assertEquals(true, jet.contains("HELLO"));
        assertEquals(true, jet.containsAll(tset));
        // same as Set.retainAll()
        assertEquals(asList("Hello"), TestUtil.makeList(jet.intersection(hset)));
        // same as Set.retainAll()
        assertEquals(asList("Hello"), TestUtil.makeList(jet.intersection(hset.iterator())));
        assertEquals(asList("Hello"), TestUtil.makeList(jet.intersection((Collection)hset)));
    }

    public void testStreams()
    {
        ISet<Integer> mset = TreeSet.<Integer>of().insert(4).insert(3).insert(4).insert(2).insert(1).insert(3);
        StandardStreamableTests.verifyOrderedUsingCollection(asList(1, 2, 3, 4), mset);
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((ISet)a).iterator();
        ISet<String> empty = TreeSet.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4tCSjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlLAwMzC+B7jLDa2IJ0CV6zvm5BYlFIDkoqyS/CGYYE8wwIA0AQ+WRl+oAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetTest::extraSerializationChecks, empty.insert("a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4tCSjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlLAwMzC+B7jLDa2IJ0CV6zvm5BYlFIDkoqyS/CGYYE8wwBsYSBsbECgBDcKss7gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetTest::extraSerializationChecks, empty.insertAll(asList("a", "B", "c", "D")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4tCSjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlLAwMzC+B7jLDa2IJ0CV6zvm5BYlFIDkoqyS/CGYYE8wwBpYSBkYnIHYB4kQgTq4AAMgk47v6AAAA");

        empty = TreeSet.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4tCSjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlLAwMzC+B7tICmpaol5OYl64XXFKUmZeu4pxYnOqZV5yaV5xZklmW6pyfW5BYlFiSX1TOHFMbE/D0HBPMACANAL7Ow7beAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetTest::extraSerializationChecks, empty.insert("a"),
                                                     "H4sIAAAAAAAA/5XOIQ7CQBCF4SkFxzGqEGsQCBRB4ZoUWTNtJs2S7W4zM7SFhBtxFm6BQHAFoBU4DM/95su7PmEmDKvAlTlga+v6qFg4MmVwjkq1wYsRYovOnnFMs2eijDTl0J/e417Jcg7QM6z/UDaFKGOpv6SmmwLEj+HXYtDQOPSVyZStr5ItCu28kBertqVtqBtk1MBdnF/y9H6bfAGIFCLsP7A4W6jiAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetTest::extraSerializationChecks, empty.insertAll(asList("a", "B", "c", "D")),
                                                     "H4sIAAAAAAAA/5XOIQ7CQBCF4YEWxzGqEGsQCBQUg2tSZM20mTRLtrvN7NAWEm7EWbgFAsEVgEXgMEzyizFf3uUBE8+wcFyrPXa6aQ6CpSFVOWOoEu2sV55Yo9En/Lxqx0Q5ScZuOL4+90zmU4CBYfmHsiq9MFbyS2r7GCC6h12zoKEyaGuVC2tbJyl62lpP1mvRHaWuaZFRHPdRcS6y23X8BSAWGGFoHapCm+ENdgOY5O4AAAA=");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeSet setA = (TreeSet)a;
        TreeSet setB = (TreeSet)b;
        assertEquals(setA.getComparator(), setB.getComparator());
        TreeMapTest.extraSerializationChecks(setA.getMap(), setB.getMap());
    }

    public void testBuilder()
        throws InterruptedException
    {
        final Func2<List<Integer>, ISet<Integer>, Boolean> comparator = (list, set) -> {
            set.checkInvariants();
            final List<Integer> sortedExpected = list.stream().sorted().collect(Collectors.toList());
            final List<Integer> actual = set.stream().collect(Collectors.toList());
            assertEquals(sortedExpected, actual);
            return true;
        };
        final ComparableComparator<Integer> intComparator = ComparableComparator.of();
        final List<Integer> expected = IntStream.range(0, 4096).boxed().collect(Collectors.toList());
        StandardBuilderTests.verifyBuilder(expected, this::builderAdaptor, comparator, new Integer[0]);
        StandardBuilderTests.verifyThreadSafety(expected, intComparator, this::builderAdaptor, a -> a);
    }

    @Nonnull
    private SetBuilderTestAdapter<Integer> builderAdaptor()
    {
        return new SetBuilderTestAdapter<>(TreeSet.builder(ComparableComparator.<Integer>of()));
    }
}
