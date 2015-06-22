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

package org.javimmutable.collections.tree;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import junit.framework.TestCase;

import com.google.common.collect.LinkedHashMultiset;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.StandardJImmutableMultisetTests;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMultiset;
import java.util.Collection;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class JImmutableTreeMultisetTest
        extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableMultisetTests.verifyMultiset(JImmutableTreeMultiset.<Integer>of());
        StandardJImmutableMultisetTests.testRandom(JImmutableTreeMultiset.<Integer>of(),
                                                   TreeMultiset.<Integer>create());
    }

    public void test()
    {

        Multiset<String> values = TreeMultiset.create();
        values.add("tennant", 10);
        values.add("smith", 11);
        values.add("capaldi", 12);
        values.add("eccleston", 9);
        final Set<String> valueSet = values.elementSet();
        final List<String> valueList = new ArrayList<String>(values);

        JImmutableMultiset<String> jmet = JImmutableTreeMultiset.of();
        Multiset<String> expected = TreeMultiset.create();
        assertTrue(jmet.isEmpty());
        assertEquals(0, jmet.size());
        assertEquals(0, jmet.valueCount());
        assertEquals(false, jmet.contains("tennant"));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(false, jmet.containsAny(valueSet));
        assertEquals(false, jmet.containsAll(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueList));
        StandardCursorTest.emptyCursorTest(jmet.cursor());
        StandardCursorTest.emptyCursorTest(jmet.occurrenceCursor());
        StandardCursorTest.emptyCursorTest(jmet.entryCursor());

        jmet = jmet.insert("TENNANT".toLowerCase(), 10);
        expected.add("TENNANT".toLowerCase(), 10);
        assertFalse(jmet.isEmpty());
        assertEquals(1, jmet.size());
        assertEquals(10, jmet.valueCount());
        assertEquals(true, jmet.contains("tennant", 10));
        assertEquals(false, jmet.contains("smith"));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valueSet));
        assertEquals(false, jmet.containsAll(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueList));
        StandardJImmutableMultisetTests.verifyCursor(jmet, expected);

        jmet = jmet.insert("SMITH".toLowerCase(), 11);
        expected.add("SMITH".toLowerCase(), 11);
        assertFalse(jmet.isEmpty());
        assertEquals(2, jmet.size());
        assertEquals(21, jmet.valueCount());
        assertEquals(true, jmet.contains("tennant", 10));
        assertEquals(true, jmet.contains("smith", 11));
        assertEquals(false, jmet.contains("capaldi"));
        assertEquals(false, jmet.contains("eccleston"));
        assertEquals(true, jmet.containsAny(valueSet));
        assertEquals(false, jmet.containsAll(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueSet));
        assertEquals(false, jmet.containsAllOccurrences(valueList));
        StandardJImmutableMultisetTests.verifyCursor(jmet, expected);

        assertSame(jmet, jmet.union(Arrays.asList("tennant", "smith")));
        assertSame(jmet, jmet.union(asSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));
        StandardJImmutableMultisetTests.verifyCursor(jmet.union(Arrays.asList("tennant", "smith")), expected);


        JImmutableMultiset<String> jmet2 = jmet.union(valueSet);
        Multiset<String> expected2 = TreeMultiset.create();
        expected2.addAll(values);
        expected2.setCount("capaldi", 1);
        expected2.setCount("eccleston", 1);
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(23, jmet2.valueCount());
        assertEquals(true, jmet2.contains("tennant", 10));
        assertEquals(true, jmet2.contains("smith", 11));
        assertEquals(true, jmet2.contains("capaldi"));
        assertEquals(true, jmet2.contains("eccleston"));
        assertEquals(true, jmet2.containsAny(valueSet));
        assertEquals(true, jmet2.containsAll(valueSet));
        assertEquals(true, jmet2.containsAllOccurrences(valueSet));
        assertEquals(false, jmet2.containsAllOccurrences(valueList));
        assertEquals(asSet("tennant", "smith", "capaldi", "eccleston"), jmet2.getSet());
        System.out.println(expected2);
        StandardJImmutableMultisetTests.verifyCursor(jmet2, expected2);


        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi").deleteOccurrence("eccleston"));
        StandardJImmutableMultisetTests.verifyCursor(jmet2.intersection(jmet), expected);

        jmet2 = jmet2.union(values);
        expected2.clear();
        expected2.addAll(values);
        assertFalse(jmet2.isEmpty());
        assertEquals(4, jmet2.size());
        assertEquals(42, jmet2.valueCount());
        assertEquals(true, jmet2.contains("tennant", 10));
        assertEquals(true, jmet2.contains("smith", 11));
        assertEquals(true, jmet2.contains("capaldi", 12));
        assertEquals(true, jmet2.contains("eccleston", 9));
        assertEquals(true, jmet2.containsAny(valueSet));
        assertEquals(true, jmet2.containsAll(valueSet));
        assertEquals(true, jmet2.containsAllOccurrences(valueSet));
        assertEquals(true, jmet2.containsAllOccurrences(valueList));
        StandardJImmutableMultisetTests.verifyCursor(jmet2, expected2);


        assertEquals(jmet, jmet2.delete("capaldi").delete("eccleston"));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi", 12).deleteOccurrence("eccleston", 9));
        assertEquals(jmet, jmet2.deleteAll(Arrays.asList("capaldi", "eccleston")));
        StandardJImmutableMultisetTests.verifyCursor(jmet2.delete("capaldi").delete("eccleston"), expected);

        Multiset<String> extra = TreeMultiset.create();
        extra.add("eccleston", 9);
        extra.add("capaldi", 12);

        assertEquals(jmet, jmet2.deleteAllOccurrences(extra));
        assertEquals(jmet, jmet2.deleteAllOccurrences(asJMet(extra)));
        assertEquals(jmet2, jmet.insertAll(extra));
        assertEquals(jmet2, jmet.insertAll(asJMet(extra)));
        assertEquals(JImmutableTreeMultiset.<String>of(), jmet2.deleteAll());
        StandardJImmutableMultisetTests.verifyCursor(jmet2.deleteAll(extra), expected);
        StandardJImmutableMultisetTests.verifyCursor(jmet.insertAll(extra), expected2);



        JImmutableMultiset<String> jmet3 = asJMet(valueSet).insert("davison").insert("baker");
        Multiset<String> expected3 = TreeMultiset.create(Arrays.asList("tennant", "smith", "capaldi", "eccleston", "davison", "baker"));
        assertFalse(jmet3.isEmpty());
        assertEquals(6, jmet3.size());
        assertEquals(6, jmet3.valueCount());
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
        StandardJImmutableMultisetTests.verifyCursor(jmet3, expected3);


        JImmutableMultiset<String> jmet4 = asJMet(Arrays.asList("tennant", "smith", "capaldi", "eccleston"));
        assertEquals(jmet4, jmet3.intersection(valueSet));
        assertEquals(jmet4, jmet3.intersection(asSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(jmet4, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(jmet4, jmet3.intersection(jmet4));
        StandardJImmutableMultisetTests.verifyCursor(jmet3.intersection(valueSet), TreeMultiset.create(Arrays.asList("tennant", "smith", "capaldi", "eccleston")));
    }

    private Set<String> asSet(String... args)
    {
        Set<String> set = new HashSet<String>();
        for (String arg : args) {
            set.add(arg);
        }
        return set;
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

}