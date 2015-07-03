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

package org.javimmutable.collections.StressTestTool;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class JImmutableMultisetStressTester
        extends AbstractStressTestable
{
    private JImmutableMultiset<String> multi;
    private final Class<? extends Multiset> expectedClass;

    public JImmutableMultisetStressTester(JImmutableMultiset<String> multi,
                                          Class<? extends Multiset> expectedClass)
    {
        this.multi = multi;
        this.expectedClass = expectedClass;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        options = options.insert("mset").insert("multiset").insert(makeClassOption(multi));
        return options;
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Multiset<String> expected = expectedClass.newInstance();
        JImmutableRandomAccessList<String> multiList = JImmutables.ralist();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableMultisetStressTest on %s of size %d%n", multi.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", multi.size());
            for (int i = 0; i < size / 3; ++i) {

            }
            verifyContents(multi, expected);
            verifyMultiList(multiList);
            System.out.printf("shrinking %d%n", multi.size());
            for (int i = 0; i < size / 6; ++i) {

            }
            verifyContents(multi, expected);
            verifyMultiList(multiList);
            System.out.println("checking contains methods");
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(6)) {
                case 0:
                    String value = (random.nextBoolean()) ? valueInMulti(multiList, random) : makeValue(tokens, random);
                    int count = random.nextInt(multi.valueCount());
                    if (multi.contains(value, count) != (expected.count(value) >= count)) {
                        throw new RuntimeException(String.format("contains(value, count) method call failed for %s, %d - expected %b found %b%n", value, count, expected.contains(value), multi.contains(value)));
                    }
                    break;
                case 1:
                    List<String> values = new ArrayList<String>();
                    for (int n = 0; n < random.nextInt(10); ++n) {
                        if (random.nextBoolean()) {
                            values.add(valueInMulti(multiList, random));
                        } else {
                            values.add(makeValue(tokens, random));
                        }
                    }
                    if (multi.containsAllOccurrences(IterableCursorable.of(values)) != containsAllByOccurrence(expected, values)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(Cursorable) method call failed for %s - expected %b found %b%n", values, multi.containsAllOccurrences(IterableCursorable.of(values)), containsAllByOccurrence(expected, values)));
                    }
                    break;
                case 2:
                    values = new ArrayList<String>();
                    for (int n = 0; n < random.nextInt(10); ++n) {
                        if (random.nextBoolean()) {
                            values.add(valueInMulti(multiList, random));
                        } else {
                            values.add(makeValue(tokens, random));
                        }
                    }
                    if (multi.containsAllOccurrences(values) != containsAllByOccurrence(expected, values)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(Collection) method call failed for %s - expected %b found %b%n", values, multi.containsAllOccurrences(values), containsAllByOccurrence(expected, values)));
                    }
                    break;
                case 3:
                    JImmutableMultiset<String> values2 = JImmutables.multiset();
                    for (int n = 0; n < random.nextInt(10); ++n) {
                        values2 = (random.nextBoolean()) ? values2.insert(valueInMulti(multiList, random)) : values2.insert(makeValue(tokens, random));
                    }
                    if (multi.containsAllOccurrences(values2) != containsAllByOccurrence(expected, values2)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(JImmutableMultiset) method call failed for %s - expected %b found %b%n", values2, multi.containsAllOccurrences(values2), containsAllByOccurrence(expected, values2)));

                    }
                    break;
                case 4:
                    JImmutableSet<String> values3 = JImmutables.set();
                    for (int n = 0; n < random.nextInt(10); ++n) {
                        values3 = (random.nextBoolean()) ? values3.insert(valueInMulti(multiList, random)) : values3.insert(makeValue(tokens, random));
                    }
                    if (multi.containsAllOccurrences(values3) != expected.containsAll(values3.getSet())) {
                        throw new RuntimeException(String.format("containsAllOccurrences(JImmutableSet) method call failed for %s - expected %b found %b%n", values3, multi.containsAllOccurrences(values3), expected.containsAll(values3.getSet())));

                    }
                    break;
                case 5:
                    Set<String> values4 = new HashSet<String>();
                    for (int n = 0; n < random.nextInt(10); ++n) {
                        if (random.nextBoolean()) {
                            values4.add(valueInMulti(multiList, random));
                        } else {
                            values4.add(makeValue(tokens, random));
                        }
                    }
                    if (multi.containsAllOccurrences(values4) != expected.containsAll(values4)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(Set) method call failed for %s - expected %b found %b%n", values4, multi.containsAllOccurrences(values4), expected.containsAll(values4)));

                    }
                    break;
                default:
                    throw new RuntimeException();
                }

            }
            verifyCursor(multi, expected);
        }

        System.out.printf("cleanup %d%n", multi.size());
        while (multiList.size() > 2) {

        }
        verifyContents(multi, expected);
        multi = multi.deleteAll();
        expected.clear();

        if (multi.size() != 0) {
            throw new RuntimeException(String.format("expected multiset to be empty but it contained %d keys%n", multi.size()));
        }
        verifyContents(multi, expected);
        System.out.printf("JImmutableMultisetStressTest on %s completed without errors%n", multi.getClass().getSimpleName());

    }

    private void verifyCursor(final JImmutableMultiset<String> multi,
                              final Multiset<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", multi.size());
        final List<String> expectedSet = new ArrayList<String>();
        expectedSet.addAll(expected.elementSet());
        if (expectedSet.size() != multi.size()) {
            throw new RuntimeException(String.format("expectedSet built incorrectly - size expected %d size found %d%n", multi.size(), expectedSet.size()));
        }

        final List<String> expectedList = asList(expected);
        if (expectedList.size() != multi.valueCount()) {
            throw new RuntimeException(String.format("expectedList built incorrectly - size expected %d size found %d%n", multi.valueCount(), expectedList.size()));
        }

        final List<JImmutableMap.Entry<String, Integer>> entries = new ArrayList<JImmutableMap.Entry<String, Integer>>();
        for (Multiset.Entry<String> expectedEntry : expected.entrySet()) {
            entries.add(new MapEntry<String, Integer>(expectedEntry.getElement(), expectedEntry.getCount()));
        }
        if (entries.size() != multi.size()) {
            throw new RuntimeException(String.format("entries list built incorrectly - size expected %d size found %d%n", multi.size(), entries.size()));
        }

        StandardCursorTest.listCursorTest(expectedSet, multi.cursor());
        StandardCursorTest.listCursorTest(expectedList, multi.occurrenceCursor());
        StandardCursorTest.listCursorTest(entries, multi.entryCursor());
        StandardCursorTest.listIteratorTest(expectedSet, multi.iterator());
    }

    private void verifyContents(final JImmutableMultiset<String> multi,
                                final Multiset<String> expected)
    {
        System.out.printf("checking contents with size %d%n", multi.size());
        if (multi.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), multi.isEmpty()));
        }
        if (multi.size() != expected.elementSet().size()) {
            throw new RuntimeException(String.format("unique value size mismatch - expected %d found %d%n", expected.size(), multi.size()));
        }
        if (multi.valueCount() != expected.size()) {
            throw new RuntimeException(String.format("occurrence size mismatch - expected %d found %d%n", expected.size(), multi.valueCount()));
        }
        for (String expectedValue : multi) {
            if (expected.count(expectedValue) != multi.count(expectedValue)) {
                throw new RuntimeException(String.format("count mismatch on %s - expected %d found %d%n", expectedValue, expected.count(expectedValue), multi.count(expectedValue)));
            }
        }
//        for(Multiset.Entry<String> entry : expected.entrySet()) {
//            String value = entry.getElement();
//            if(!multi.contains(value)) {
//                throw new RuntimeException(String.format("value mismatch - expected %s but not in %s%n", value, multi.getClass().getSimpleName()));
//            }
//            int count = entry.getCount();
//            if(!multi.contains(value, count)) {
//                throw new RuntimeException(String.format("count mismatch on %s - expected %d found %d%n", value, count, multi.count(value)));
//            }
//        }
        if (!expected.elementSet().equals(multi.getSet())) {
            throw new RuntimeException("method call failed - getSet()\n");
        }
        multi.checkInvariants();
//        if(!multi.containsAll(IterableCursorable.of(expected))) {
//            throw new RuntimeException("method call failed - containsAll(Cursorable)");
//        }
//        if (!multi.containsAll(expected)) {
//            throw new RuntimeException("method call failed - containsAll(Collection)");
//        }
//        if(!multi.containsAllOccurrences(IterableCursorable.of(expected))) {
//            throw new RuntimeException("method call failed - containsAllOccurrences(Cursorable)");
//        }
//        if (!multi.containsAllOccurrences(expected)) {
//            throw new RuntimeException("method call failed - containsAllOccurrences(Collection)");
//        }
//        if(!multi.containsAllOccurrences(multi)) {
//            throw new RuntimeException("method call failed - containsAllOccurrences(JImmutableMultiset)");
//        }
//        if(!multi.containsAllOccurrences((JImmutableSet<String>)multi)) {
//            throw new RuntimeException("method call failed - containsAllOccurrences(JImmutableSet)");
//        }
//        if(!multi.containsAllOccurrences(expected.elementSet())) {
//            throw new RuntimeException("method call failed - containsAllOccurrences(Set)");
//        }
//        if ((!multi.containsAny(IterableCursorable.of(expected))) && (expected.size() != 0)) {
//            throw new RuntimeException("method call failed - containsAny(Cursorable)");
//        }
//        if ((!multi.containsAny(expected)) && (expected.size() != 0)) {
//            throw new RuntimeException("method call failed - containsAny(Collection)");
//        }
    }

    private List<String> asList(Multiset<String> expectedMultiset)
    {
        List<String> expectedList = new ArrayList<String>();
        expectedList.addAll(expectedMultiset);
        return expectedList;
    }

    private void verifyMultiList(JImmutableRandomAccessList<String> multiList)
    {
        int multiListSize = multiList.size();
        if (!((multiListSize <= (multi.size() + 5)) && (multiListSize >= (multi.size())))) {
            throw new RuntimeException(String.format("multiList size mismatch - multi: %d, multiList: %d", multi.size(), multiListSize));
        }
    }

    private String valueInMulti(JImmutableRandomAccessList<String> multiList,
                                Random random)
    {
        return multiList.get(random.nextInt(multiList.size()));
    }

    private boolean containsAllByOccurrence(Multiset<String> expected,
                                            JImmutableMultiset<String> values)
    {
        for (Cursor<JImmutableMap.Entry<String, Integer>> c = values.entryCursor().start(); c.hasValue(); c = c.next()) {
            String value = c.getValue().getKey();
            int count = c.getValue().getValue();
            if (count > expected.count(value)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAllByOccurrence(Multiset<String> expected,
                                            List<String> values)
    {
        JImmutableMultiset<String> multiset = JImmutables.multiset();
        multiset = multiset.insertAll(values);
        return containsAllByOccurrence(expected, multiset);
    }

    private boolean containsAny(Multiset<String> expected,
                                Multiset<String> values)
    {
        for (String value : values.elementSet()) {
            if (expected.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
