///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.hash;


import com.google.common.collect.TreeMultiset;
import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMultiset;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.common.StandardMultisetTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.TestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class HashMultisetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardMultisetTests.verifyMultiset(HashMultiset.of(), true);
        StandardMultisetTests.testRandom(HashMultiset.of(),
                                         TreeMultiset.create());
    }

    public void test()
    {
        List<String> valuesL = asList("tennant", "smith", "capaldi", "eccleston");
        IMultiset<String> valuesM = HashMultiset.<String>of().union(valuesL);
        valuesM = valuesM.setCount("tennant", 10).setCount("smith", 11).setCount("capaldi", 12).setCount("eccleston", 9);

        IMultiset<String> jmet = HashMultiset.of();
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

        assertSame(jmet, jmet.union(asList("tennant", "smith")));
        assertSame(jmet, jmet.union(TestUtil.makeSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));

        IMultiset<String> jmet2 = jmet.union(valuesL);
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
        assertEquals(jmet, jmet2.deleteAll(asList("capaldi", "eccleston")));

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
        assertEquals(HashMultiset.of(), jmet2.deleteAll());

        IMultiset<String> jmet3 = asJMet(valuesL).insert("davison").insert("baker");
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

        IMultiset<String> expected = asJMet("tennant", "smith", "capaldi", "eccleston");
        expected.checkInvariants();
        assertEquals(expected, jmet3.intersection(valuesL));
        assertEquals(expected, jmet3.intersection(TestUtil.makeSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(expected));
    }

    public void testDeleteAll()
    {
        IMultiset<String> jmet = HashMultiset.<String>of().insert("TENNANT").insert("ECCLESTON");
        assertSame(HashMultiset.of(), jmet.deleteAll());
    }

    public void testStreams()
    {
        IMultiset<Integer> mset = HashMultiset.<Integer>of().insert(4).insert(3).insert(4).insert(2).insert(1).insert(3);
        assertEquals(asList(1, 2, 3, 4), mset.stream().collect(toList()));
        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            for (int j = 0; j <= i % 5; j++) {
                expected.add(i);
            }
        }
        Collections.shuffle(expected);
        mset = mset.deleteAll();
        for (Integer i : expected) {
            mset = mset.insert(i);
        }
        Collections.sort(expected);
        List<Integer> actual = mset.occurrences().parallelStream().sorted().collect(toList());
        assertEquals(expected, actual);
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IMultiset)a).entries().iterator();
        final IMultiset<String> empty = HashMultiset.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBJr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzSCzO8C3NKcksTi0JKMqvqPwPAv9UjHkYGCqKGBxIMMoxqbikKDG5BKdxBeUcDAzMLxmAoAIApOZm4qsAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBJr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzSCzO8C3NKcksTi0JKMqvqPwPAv9UjHkYGCqKGBxIMMoxqbikKDG5BKdxBeUcDAzMLxkYGBhLGBgTy1lArAoAjCqw57UAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList("a", "b", "c", "b")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBJr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzSCzO8C3NKcksTi0JKMqvqPwPAv9UjHkYGCqKGBxIMMoxqbikKDG5BKdxBeUcDAzMLxmARAkDY2I5C5DFCGQlgVlMQFYyRKwCAMvclmzJAAAA");
    }

    private ISet<String> asJSet(String... args)
    {
        ISet<String> jet = HashSet.of();
        for (String arg : args) {
            jet = jet.insert(arg);
        }
        return jet;
    }

    private IMultiset<String> asJMet(String... args)
    {
        IMultiset<String> jmet = HashMultiset.of();
        for (String arg : args) {
            jmet = jmet.insert(arg);
        }
        return jmet;
    }

    private IMultiset<String> asJMet(List<String> list)
    {
        IMultiset<String> jmet = HashMultiset.of();
        return jmet.insertAll(list);
    }
}
