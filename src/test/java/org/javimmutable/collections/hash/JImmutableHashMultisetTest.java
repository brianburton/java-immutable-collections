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

package org.javimmutable.collections.hash;


import com.google.common.collect.TreeMultiset;
import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.StandardJImmutableMultisetTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JImmutableHashMultisetTest
        extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableMultisetTests.verifyMultiset(JImmutableHashMultiset.<Integer>of());
        StandardJImmutableMultisetTests.testRandom(JImmutableHashMultiset.<Integer>of(),
                                                   TreeMultiset.<Integer>create());
    }

    public void test()
    {
        List<String> valuesL = Arrays.asList("tennant", "smith", "capaldi", "eccleston");
        JImmutableMultiset<String> valuesM = JImmutableHashMultiset.<String>of().union(valuesL);
        valuesM = valuesM.setCount("tennant", 10).setCount("smith", 11).setCount("capaldi", 12).setCount("eccleston", 9);

        JImmutableMultiset<String> jmet = JImmutableHashMultiset.of();
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
        assertSame(jmet, jmet.union(asSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJSet("tennant", "smith")));
        assertSame(jmet, jmet.union(asJMet("tennant", "smith")));
        assertNotSame(jmet, jmet.union(asJMet("tennant").insert("smith", 12)));

        JImmutableMultiset<String> jmet2 = jmet.union(valuesL);
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
        assertEquals(asSet("tennant", "smith", "capaldi", "eccleston"), jmet2.getSet());

        assertEquals(jmet, jmet.intersection(jmet2));
        assertEquals(jmet, jmet2.intersection(jmet));
        assertEquals(jmet, jmet2.deleteOccurrence("capaldi").deleteOccurrence("eccleston"));

        jmet2 = jmet2.union(valuesM);
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
        assertEquals(JImmutableHashMultiset.of(), jmet2.deleteAll());

        JImmutableMultiset<String> jmet3 = asJMet(valuesL).insert("davison").insert("baker");
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
        assertEquals(expected, jmet3.intersection(valuesL));
        assertEquals(expected, jmet3.intersection(asSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(asJSet("tennant", "smith", "capaldi", "eccleston")));
        assertEquals(expected, jmet3.intersection(expected));
    }

    public void testDeleteAll()
    {
        JImmutableMultiset<String> jmet = JImmutableHashMultiset.<String>of().insert("TENNANT").insert("ECCLESTON");
        assertSame(JImmutableHashMultiset.of(), jmet.deleteAll());
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
        JImmutableSet<String> jet = JImmutableHashSet.of();
        for (String arg : args) {
            jet = jet.insert(arg);
        }
        return jet;
    }

    private JImmutableMultiset<String> asJMet(String... args)
    {
        JImmutableMultiset<String> jmet = JImmutableHashMultiset.of();
        for (String arg : args) {
            jmet = jmet.insert(arg);
        }
        return jmet;
    }

    private JImmutableMultiset<String> asJMet(List<String> list)
    {
        JImmutableMultiset<String> jmet = JImmutableHashMultiset.of();
        return jmet.insertAll(list);
    }
}