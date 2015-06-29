///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.list.JImmutableArrayList;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class JImmutableSetStressTester
        extends AbstractStressTestable
{
    private JImmutableSet<String> set;
    private final Class<? extends Set> expectedClass;

    public JImmutableSetStressTester(JImmutableSet<String> set,
                                     Class<? extends Set> expectedClass)
    {
        this.set = set;
        this.expectedClass = expectedClass;
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Set<String> expected = expectedClass.newInstance();
        int size = random.nextInt(100000);
        System.out.printf("JImmutableSetStressTest on %s of size %d%n", set.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", set.size());
            for (int i = 0; i < size / 3; ++i) {
                List<String> values = new ArrayList<String>();
                for (int n = 0; n < random.nextInt(3); ++n) {
                    values.add(makeValue(tokens, random));
                }
                switch (random.nextInt(5)) {
                case 0:
                    String value = makeValue(tokens, random);
                    set = set.insert(value);
                    expected.add(value);
                    break;
                case 1:
                    set = set.insertAll(IterableCursorable.of(values));
                    expected.addAll(values);
                    break;
                case 2:
                    set = set.insertAll(values);
                    expected.addAll(values);
                    break;
                case 3:
                    set = set.union(IterableCursorable.of(values));
                    expected.addAll(values);
                    break;
                case 4:
                    set = set.union(values);
                    expected.addAll(values);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(set, expected);
            System.out.printf("shrinking %d%n", set.size());
            for (int i = 0; i < size / 6; ++i) {
                List<String> values = new ArrayList<String>();
                for (int n = 0; n < random.nextInt(3); ++n) {
                    values.add(makeValue(asJList(set), random));
                }
                switch(random.nextInt(5)) {
                case 0:
                    String value = makeValue(asJList(set), random);
                    set = set.delete(value);
                    expected.remove(value);
                    break;
                case 1:
                    set = set.deleteAll(IterableCursorable.of(values));
                    expected.removeAll(values);
                    break;
                case 2:
                    set = set.deleteAll(values);
                    expected.removeAll(values);
                    break;
                case 3:
                    expected.removeAll(values);
                    set = set.intersection(IterableCursorable.of(expected));
                    break;
                case 4:
                    expected.removeAll(values);
                    set = set.intersection(expected.iterator());
                    break;
                case 5:
                    expected.removeAll(values);
                    set = set.intersection(asJSet(expected));
                    break;
                case 6:
                    expected.removeAll(values);
                    set = set.intersection(expected);
                    break;
                }
            }
            verifyContents(set, expected);
        }

        System.out.printf("cleanup %d%n", expected.size());
        while (set.size() > 2) {
            if(random.nextBoolean()) {
                String value = makeValue(asJList(set), random);
                set = set.delete(value);
                expected.remove(value);
            } else {
                List<String> values = new ArrayList<String>();
                for (int n = 0; n < random.nextInt(3); ++n) {
                    values.add(makeValue(asJList(set), random));
                }
                set = (random.nextBoolean()) ? set.deleteAll(IterableCursorable.of(values)) : set.deleteAll(values);
                expected.removeAll(values);
            }
        }
        verifyContents(set, expected);
        set = set.deleteAll();
        expected.clear();
        verifyContents(set, expected);
        System.out.printf("JImmutableSetStressTest on %s completed without errors", set.getClass().getSimpleName());
    }

    private void verifyContents(JImmutableSet<String> set,
                                Set<String> expected)
    {
        System.out.printf("checking contents with size %d%n", set.size());
        if (set.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b", expected.isEmpty(), set.isEmpty()));
        }
        if (set.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), set.size()));
        }
        for (String expectedValue : expected) {
            if (!set.contains(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s but not in %s%n", expectedValue, set.getClass().getSimpleName()));
            }
        }
        for (String expectedValue : set) {
            if (!expected.contains(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s but not in Set%n", expectedValue));
            }
        }
        if (!expected.equals(set.getSet())) {
            throw new RuntimeException("getSet() call failed");
        }
        if (!set.containsAll(IterableCursorable.of(expected))) {
            throw new RuntimeException("method call failed - containsAll(Cursorable)");
        }
        if (!set.containsAll(expected)) {
            throw new RuntimeException("method call failed - containsAll(Collection)");
        }
        if (!set.containsAny(IterableCursorable.of(expected))) {
            throw new RuntimeException("method call failed - containsAny(Cursorable)");
        }
        if (!set.containsAny(expected)) {
            throw new RuntimeException("method call failed - containsAny(Collection)");
        }

        StandardCursorTest.listCursorTest(asList(expected), set.cursor());
        StandardCursorTest.listIteratorTest(asList(expected), set.iterator());
        set.checkInvariants();
    }

    private List<String> asList(Set<String> expectedSet)
    {
        List<String> expectedList = new ArrayList<String>();
        expectedList.addAll(expectedSet);
        return expectedList;
    }

    private JImmutableList<String> asJList(JImmutableSet<String> set)
    {
        JImmutableList<String> jList = JImmutableArrayList.<String>of();
        jList = jList.insertAll(set);
        return jList;
    }

    private JImmutableSet<String> asJSet(Set<String> set)
    {
        JImmutableSet<String> jet = JImmutableHashSet.<String>of();
        jet = jet.insertAll(set);
        if(!set.equals(jet.getSet())) {
            throw new RuntimeException();
        }
        return jet;
    }
}
