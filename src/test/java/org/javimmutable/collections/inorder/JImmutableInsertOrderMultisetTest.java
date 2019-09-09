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

package org.javimmutable.collections.inorder;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import junit.framework.AssertionFailedError;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class JImmutableInsertOrderMultisetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableMultisetTests.verifyMultiset(JImmutableInsertOrderMultiset.of());
        StandardJImmutableMultisetTests.testRandom(JImmutableInsertOrderMultiset.of(),
                                                   LinkedHashMultiset.create());
    }

    public void test()
    {
        List<String> valuesL = new LinkedList<>();
        valuesL.addAll(Arrays.asList("tennant", "smith", "capaldi", "eccleston"));
        JImmutableMultiset<String> valuesM = JImmutableInsertOrderMultiset.<String>of().union(valuesL);
        valuesM = valuesM.setCount("tennant", 10).setCount("smith", 11).setCount("capaldi", 12).setCount("eccleston", 9);

        JImmutableMultiset<String> jmet = JImmutableInsertOrderMultiset.of();
        assertTrue(jmet.isEmpty());
        assertEquals(0, jmet.size());
        assertEquals(0, jmet.occurrenceCount());
        assertEquals(false, jmet.contains("tennant"));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(false, jmet.containsAny(valuesL));
        assertEquals(false, jmet.containsAll(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesM));

        jmet = jmet.insert("TENNANT".toLowerCase(), 10);
        jmet.checkInvariants();
        assertFalse(jmet.isEmpty());
        assertEquals(1, jmet.size());
        assertEquals(10, jmet.occurrenceCount());
        assertEquals(true, jmet.containsAtLeast("tennant", 10));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valuesL));
        assertEquals(false, jmet.containsAll(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesM));

        jmet = jmet.insert("SMITH".toLowerCase(), 11);
        jmet.checkInvariants();
        assertFalse(jmet.isEmpty());
        assertEquals(2, jmet.size());
        assertEquals(21, jmet.occurrenceCount());
        assertEquals(true, jmet.containsAtLeast("tennant", 10));
        assertEquals(true, jmet.containsAtLeast("smith", 11));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valuesL));
        assertEquals(false, jmet.containsAll(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesM));

        assertSame(jmet, jmet.union(Arrays.asList("tennant", "smith")));
        assertSame(jmet, jmet.union(TestUtil.makeSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));

        JImmutableMultiset<String> jmet2 = jmet.union(valuesL);
        jmet2.checkInvariants();
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(23, jmet2.occurrenceCount());
        assertEquals(true, jmet2.containsAtLeast("tennant", 10));
        assertEquals(true, jmet2.containsAtLeast("smith", 11));
        assertEquals(true, jmet2.contains("capaldi"));
        assertEquals(true, jmet2.contains("eccleston"));
        assertEquals(true, jmet2.containsAny(valuesL));
        assertEquals(true, jmet2.containsAll(valuesL));
        assertEquals(true, jmet2.containsAllOccurrences(valuesL));
        assertEquals(false, jmet2.containsAllOccurrences(valuesM));
        assertEquals(TestUtil.makeSet("tennant", "smith", "capaldi", "eccleston"), jmet2.getSet());

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi").deleteOccurrence("eccleston"));

        jmet2 = jmet2.union(valuesM);
        jmet2.checkInvariants();
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(42, jmet2.occurrenceCount());
        assertEquals(true, jmet2.containsAtLeast("tennant", 10));
        assertEquals(true, jmet2.containsAtLeast("smith", 11));
        assertEquals(true, jmet2.containsAtLeast("capaldi", 12));
        assertEquals(true, jmet2.containsAtLeast("eccleston", 9));
        assertEquals(true, jmet2.containsAny(valuesL));
        assertEquals(true, jmet2.containsAll(valuesL));
        assertEquals(true, jmet2.containsAllOccurrences(valuesL));
        assertEquals(true, jmet2.containsAllOccurrences(valuesM));

        assertEquals(jmet, jmet2.delete("capaldi").delete("eccleston"));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi", 12).deleteOccurrence("eccleston", 9));
        assertEquals(jmet, jmet2.deleteAll(Arrays.asList("capaldi", "eccleston")));

        List<String> extra = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            extra.add("eccleston");
        }
        for (int i = 0; i < 12; ++i) {
            extra.add("capaldi");
        }
        assertEquals(jmet, jmet2.deleteAllOccurrences(extra));
        assertEquals(jmet, jmet2.deleteAllOccurrences(asJMet(extra)));
        assertEquals(jmet2, jmet.insertAll(extra));
        assertEquals(jmet2, jmet.insertAll(asJMet(extra)));
        assertEquals(JImmutableInsertOrderMultiset.of(), jmet2.deleteAll());

        JImmutableMultiset<String> jmet3 = asJMet(valuesL).insert("davison").insert("baker");
        jmet3.checkInvariants();
        assertFalse(jmet3.isEmpty());
        assertEquals(6, jmet3.size());
        assertEquals(6, jmet3.occurrenceCount());
        assertEquals(true, jmet3.contains("tennant"));
        assertEquals(true, jmet3.contains("smith"));
        assertEquals(true, jmet3.contains("capaldi"));
        assertEquals(true, jmet3.contains("eccleston"));
        assertEquals(true, jmet3.contains("davison"));
        assertEquals(true, jmet3.contains("baker"));
        assertEquals(true, jmet3.containsAny(valuesL));
        assertEquals(true, jmet3.containsAny(jmet));
        assertEquals(true, jmet3.containsAny(jmet2));
        assertEquals(true, jmet3.containsAll(valuesL));
        assertEquals(true, jmet3.containsAll(jmet));
        assertEquals(true, jmet3.containsAll(jmet2));
        assertEquals(true, jmet3.containsAllOccurrences(valuesL));
        assertEquals(false, jmet3.containsAllOccurrences(jmet));
        assertEquals(false, jmet3.containsAllOccurrences(jmet2));

        JImmutableMultiset<String> expected = asJMet("tennant", "smith", "capaldi", "eccleston");
        expected.checkInvariants();
        assertEquals(expected, jmet3.intersection(valuesL));
        assertEquals(expected, jmet3.intersection(TestUtil.makeSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(expected));

    }

    public void testInsertOrder()
    {
        JImmutableMultiset<Integer> jmet = JImmutableInsertOrderMultiset.of();
        Multiset<Integer> multi = LinkedHashMultiset.create();
        Random random = new Random(2500L);
        for (int i = 0; i < 5; ++i) {
            int value = random.nextInt(100000) - 50000;
            multi.add(value);
            jmet = jmet.insert(value);
        }
        jmet.checkInvariants();
        assertEquals(multi.elementSet(), jmet.getSet());
        assertEquals(new ArrayList<>(multi), asList(jmet));
        StandardJImmutableMultisetTests.verifyIterators(jmet, multi);

        int a = random.nextInt(100000) - 50000;
        int b = random.nextInt(100000) - 50000;

        multi.add(a);
        jmet = jmet.insert(b);
        jmet.checkInvariants();
        multi.add(b);
        jmet = jmet.insert(a);
        jmet.checkInvariants();
        assertEquals(multi.elementSet(), jmet.getSet());
        assertEquals(multi.size(), jmet.occurrenceCount());
        StandardJImmutableMultisetTests.verifyContents(jmet, multi);
        try {
            assertEquals(new ArrayList<>(multi), asList(jmet));
        } catch (AssertionFailedError ignored) {
            //expected
        }
        try {
            StandardJImmutableMultisetTests.verifyIterators(jmet, multi);
        } catch (AssertionFailedError ignored) {
            //expected
        }
    }

    public void testDeleteAll()
    {
        JImmutableInsertOrderMultiset<Integer> jmet = JImmutableInsertOrderMultiset.of();
        jmet = (JImmutableInsertOrderMultiset<Integer>)jmet.insert(1).insert(3).insert(1);
        JImmutableInsertOrderMultiset<Integer> cleared = jmet.deleteAll();
        assertSame(JImmutableInsertOrderMultiset.<Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertEquals(0, cleared.occurrenceCount());
        StandardIteratorTests.emptyIteratorTest(cleared.iterator());
    }

    public void testStreams()
    {
        JImmutableMultiset<Integer> mset = JImmutableInsertOrderMultiset.<Integer>of().insert(4).insert(3).insert(4).insert(2).insert(1).insert(3);
        assertEquals(Arrays.asList(4, 3, 2, 1), mset.stream().collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableMultiset)a).entries().iterator();
        final JImmutableMultiset<String> empty = JImmutableInsertOrderMultiset.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBN78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvmW5pRkFqeWBBTlV1T+B4F/KsY8DAwVRQxeJJjtmFRcUpSYXIKwA6fBBeUcDAzMLxmAoAIAeQvVccYAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBN78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvmW5pRkFqeWBBTlV1T+B4F/KsY8DAwVRQxeJJjtmFRcUpSYXIKwA6fBBeUcDAzMLxkYGBhLGBgTy1lArAoALPV/ANAAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(Arrays.asList("c", "b", "a", "b")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBN78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvmW5pRkFqeWBBTlV1T+B4F/KsY8DAwVRQxeJJjtmFRcUpSYXIKwA6fBBeUcDAzMLxmARAkDY3I5C5DFCGQlgVlMQFYiRKwCADN6c9DkAAAA");
    }

    private JImmutableSet<String> asJSet(String... args)
    {
        JImmutableSet<String> jet = JImmutableInsertOrderSet.of();
        for (String arg : args) {
            jet = jet.insert(arg);
        }
        return jet;
    }

    private JImmutableMultiset<String> asJMet(String... args)
    {
        JImmutableMultiset<String> jmet = JImmutableInsertOrderMultiset.of();
        for (String arg : args) {
            jmet = jmet.insert(arg);
        }
        return jmet;
    }

    private JImmutableMultiset<String> asJMet(List<String> list)
    {
        JImmutableMultiset<String> jmet = JImmutableInsertOrderMultiset.of();
        return jmet.insertAll(list);
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
