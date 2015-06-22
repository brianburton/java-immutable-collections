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

package org.javimmutable.collections.inorder;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.StandardJImmutableMultisetTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class JImmutableInsertOrderMultisetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableMultisetTests.verifyMultiset(JImmutableInsertOrderMultiset.<Integer>of());
    }

    public void test()
    {
        List<String> valuesL = new LinkedList<String>();
        valuesL.addAll(Arrays.asList("tennant", "smith", "capaldi", "eccleston"));
        JImmutableMultiset<String> valuesM = JImmutableInsertOrderMultiset.<String>of().union(valuesL);
        valuesM = valuesM.setCount("tennant", 10).setCount("smith", 11).setCount("capaldi", 12).setCount("eccleston", 9);

        JImmutableMultiset<String> jmet = JImmutableInsertOrderMultiset.<String>of();
        assertTrue(jmet.isEmpty());
        assertEquals(0, jmet.size());
        assertEquals(0, jmet.valueCount());
        assertEquals(false, jmet.contains("tennant"));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(false, jmet.containsAny(valuesL));
        assertEquals(false, jmet.containsAll(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesM));

        jmet = jmet.insert("TENNANT".toLowerCase(), 10);
        assertFalse(jmet.isEmpty());
        assertEquals(1, jmet.size());
        assertEquals(10, jmet.valueCount());
        assertEquals(true, jmet.contains("tennant", 10));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valuesL));
        assertEquals(false, jmet.containsAll(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesM));

        jmet = jmet.insert("SMITH".toLowerCase(), 11);
        assertFalse(jmet.isEmpty());
        assertEquals(2, jmet.size());
        assertEquals(21, jmet.valueCount());
        assertEquals(true, jmet.contains("tennant", 10));
        assertEquals(true, jmet.contains("smith", 11));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valuesL));
        assertEquals(false, jmet.containsAll(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesL));
        assertEquals(false, jmet.containsAllOccurrences(valuesM));

        assertSame(jmet, jmet.union(Arrays.asList("tennant", "smith")));
        assertSame(jmet, jmet.union(asSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));

        JImmutableMultiset<String> jmet2 = jmet.union(valuesL);
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(23, jmet2.valueCount());
        assertEquals(true, jmet2.contains("tennant", 10));
        assertEquals(true, jmet2.contains("smith", 11));
        assertEquals(true, jmet2.contains("capaldi"));
        assertEquals(true, jmet2.contains("eccleston"));
        assertEquals(true, jmet2.containsAny(valuesL));
        assertEquals(true, jmet2.containsAll(valuesL));
        assertEquals(true, jmet2.containsAllOccurrences(valuesL));
        assertEquals(false, jmet2.containsAllOccurrences(valuesM));
        assertEquals(asSet("tennant", "smith", "capaldi", "eccleston"), jmet2.getSet());

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi").deleteOccurrence("eccleston"));

        jmet2 = jmet2.union(valuesM);
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(42, jmet2.valueCount());
        assertEquals(true, jmet2.contains("tennant", 10));
        assertEquals(true, jmet2.contains("smith", 11));
        assertEquals(true, jmet2.contains("capaldi", 12));
        assertEquals(true, jmet2.contains("eccleston", 9));
        assertEquals(true, jmet2.containsAny(valuesL));
        assertEquals(true, jmet2.containsAll(valuesL));
        assertEquals(true, jmet2.containsAllOccurrences(valuesL));
        assertEquals(true, jmet2.containsAllOccurrences(valuesM));

        assertEquals(jmet, jmet2.delete("capaldi").delete("eccleston"));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi", 12).deleteOccurrence("eccleston", 9));
        assertEquals(jmet, jmet2.deleteAll(Arrays.asList("capaldi", "eccleston")));

        List<String> extra = new ArrayList<String>();
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
        assertFalse(jmet3.isEmpty());
        assertEquals(6, jmet3.size());
        assertEquals(6, jmet3.valueCount());
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
        assertEquals(expected, jmet3.intersection(valuesL));
        assertEquals(expected, jmet3.intersection(asSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(expected));

    }

    public static void testRandom()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            List<Integer> expected = new LinkedList<Integer>();
            JImmutableMultiset<Integer> jmet = JImmutableInsertOrderMultiset.<Integer>of();
            for (int loops = 0; loops < 10; ++loops) {
                int command = random.nextInt(5);
                Integer value = random.nextInt(size);
                int count = random.nextInt(3) + 1;
                switch (command) {
                case 0:
                case 1:
                    jmet = jmet.insert(value, count);
                    int index = expected.indexOf(value);
                    index = (index != -1) ? index : expected.size();
                    for (int n = 0; n < count; ++n) {
                        expected.add(index, value);
                    }
                    assertEquals(true, jmet.contains(value));
                    break;
                case 2:
                    assertEquals(expected.contains(value), jmet.contains(value));
                    break;
                case 3:
                    jmet = jmet.deleteOccurrence(value, count);
                    for (int n = 0; n < count; ++n) {
                        expected.remove(value);
                    }
                    break;

                case 4:
                    jmet = jmet.setCount(value, count);
                    while (expected.contains(value)) {
                        expected.remove(value);
                    }
                    for (int n = 0; n < count; ++n) {
                        expected.add(value);
                    }
                    break;
                }
                assertEquals(expected.size(), jmet.valueCount());
            }
            List<Integer> jmetList = new ArrayList<Integer>(jmet.getSet());
            List<Integer> expectedList = new ArrayList<Integer>(new LinkedHashSet<Integer>(expected));
           // assertEquals(expectedList, jmetList);

           // StandardJImmutableMultisetTests.verifyCursor(jmet, expected);
            for (Integer value : expected) {
                jmet = jmet.deleteOccurrence(value);
            }
            assertEquals(0, jmet.size());
            assertEquals(0, jmet.valueCount());
            assertEquals(true, jmet.isEmpty());

        }
    }



    private Set<String> asSet(String... args)
    {
        Set<String> set = new LinkedHashSet<String>();
        for (String arg : args) {
            set.add(arg);
        }
        return set;
    }

    private JImmutableSet<String> asJSet(String... args)
    {
        JImmutableSet<String> jet = JImmutableInsertOrderSet.<String>of();
        for (String arg : args) {
            jet = jet.insert(arg);
        }
        return jet;
    }

    private JImmutableMultiset<String> asJMet(String... args)
    {
        JImmutableMultiset<String> jmet = JImmutableInsertOrderMultiset.<String>of();
        for (String arg : args) {
            jmet = jmet.insert(arg);
        }
        return jmet;
    }

    private JImmutableMultiset<String> asJMet(List<String> list)
    {
        JImmutableMultiset<String> jmet = JImmutableInsertOrderMultiset.<String>of();
        return jmet.insertAll(list);
    }

    private static List<JImmutableMap.Entry<Integer, Integer>> makeEntryList(List<Integer> expected,
                                             JImmutableMultiset<Integer> jmet)
    {
        final List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        JImmutableMap<Integer, Integer> expectedMap = JImmutableInsertOrderMap.<Integer, Integer>of();
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
        return entries;
    }

}
