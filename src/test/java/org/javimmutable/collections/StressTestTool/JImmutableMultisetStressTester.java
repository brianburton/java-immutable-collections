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
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMultiset;
import org.javimmutable.collections.tree.JImmutableTreeMultiset;
import org.javimmutable.collections.util.JImmutables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Test program for all implementations of JImmutableMultiset. Divided into four sections:
 * growing (adds new occurrences), shrinking (removes occurrences), contains (tests methods
 * that check for specific occurrences), and cleanup (empties the set of all occurrences).
 * <p/>
 * This class alone does not test every method in JImmutableMultiset. Methods that manipulate
 * the values in the multiset with no effect on the number of occurrences are tested in the
 * JImmutableSetStressTester. Those tests are not repeated here. Similarly, the goal size
 * in this test refers to the total number of occurrences (multi.valueCount()), rather than
 * the number of unique values.
 */
public class JImmutableMultisetStressTester
        extends AbstractSetStressTestable
{
    private final JImmutableMultiset<String> multi;

    public static int runs;
    public static long totalTime;

    public JImmutableMultisetStressTester(JImmutableMultiset<String> multi)
            throws NoSuchMethodException
    {
        this.multi = multi.deleteAll();

        runs = 0;
        totalTime = 0;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("mset").insert("multiset").insert(makeClassOption(multi));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
    {
        ++runs;
        JImmutableMultiset<String> multi = this.multi;
        Multiset<String> expected = getEmptyMultiset();
        ArrayList<String> multiList = new ArrayList<String>();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableMultisetStressTest on %s of size %d%n", multi.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", multi.valueCount());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(10)) {
                case 0: //insert(T)
                    String value = makeInsertValue(tokens, random, multiList, expected);
                    multi = multi.insert(value);
                    expected.add(value);
                    multiList.add(value);
                    break;
                case 1: //insert(T, int)
                    value = makeInsertValue(tokens, random, multiList, expected);
                    int count = random.nextInt(3);
                    multi = multi.insert(value, count);
                    expected.add(value, count);
                    for (int n = 0; n < count; ++n) {
                        multiList.add(value);
                    }
                    break;
                case 2: //insertAll(Cursorable)
                    Multiset<String> values = HashMultiset.create(makeInsertValues(tokens, random, multiList, expected));
                    multi = multi.insertAll(IterableCursorable.of(values));
                    expected.addAll(values);
                    multiList.addAll(values);
                    break;
                case 3: //insertAll(Collection)
                    values = HashMultiset.create(makeInsertValues(tokens, random, multiList, expected));
                    multi = multi.insertAll(values);
                    expected.addAll(values);
                    multiList.addAll(values);
                    break;
                case 4: //union(Cursorable)
                    values = HashMultiset.create(makeInsertValues(tokens, random, multiList, expected));
                    multiListUnion(multiList, multi, values);
                    expectedUnion(expected, values);
                    multi = multi.union(IterableCursorable.of(values));
                    break;
                case 5: //union(Collection)
                    values = HashMultiset.create(makeInsertValues(tokens, random, multiList, expected));
                    multiListUnion(multiList, multi, values);
                    expectedUnion(expected, values);
                    multi = multi.union(values);
                    break;
                case 6: //union(Jet)
                    JImmutableSet<String> values2 = makeInsertJSet(tokens, random, multiList, expected);
                    multiListUnion(multiList, multi, values2);
                    expectedUnion(expected, values2);
                    multi = multi.union(values2);
                    break;
                case 7: //union(Set)
                    values2 = makeInsertJSet(tokens, random, multiList, expected);
                    multiListUnion(multiList, multi, values2);
                    expectedUnion(expected, values2);
                    multi = multi.union(values2.getSet());
                    break;
                case 8: //insertAll(JMet)
                    JImmutableMultiset<String> values3 = JImmutables.multiset(makeInsertValues(tokens, random, multiList, expected));
                    multi = multi.insertAll(values3);
                    for (Cursor<String> c = values3.occurrenceCursor().start(); c.hasValue(); c = c.next()) {
                        value = c.getValue();
                        expected.add(value);
                        multiList.add(value);
                    }
                    break;
                case 9: //union(JMet)
                    values3 = JImmutables.multiset(makeInsertValues(tokens, random, multiList, expected));
                    multiListUnion(multiList, multi, values3.occurrenceCursor());
                    expectedUnion(expected, values3.occurrenceCursor());
                    multi = multi.union(values3);
                    break;
                }
            }
            verifyContents(multi, expected);
            verifyMultiList(multiList, expected);
            System.out.printf("shrinking %d%n", multi.valueCount());
            for (int i = 0; i < size / 6; ++i) {
                switch (random.nextInt(5)) {
                case 0: //deleteOccurrence(T)
                    String value = makeDeleteValue(tokens, random, multiList, expected);
                    multi = multi.deleteOccurrence(value);
                    expected.remove(value);
                    break;
                case 1: //deleteOccurrence(T, int)
                    int index = random.nextInt(multiList.size());
                    value = multiList.get(index);
                    int count = random.nextInt(3);
                    if (count != 0) {
                        multiList.remove(index);
                    }
                    if ((count == 2) && (expected.count(value) >= 2)) {
                        multiList.remove(value);
                    }
                    multi = multi.deleteOccurrence(value, count);
                    expected.remove(value, count);
                    break;
                case 2: //deleteAllOccurrences(Cursorable)
                    Multiset<String> values = HashMultiset.create(makeDeleteValues(tokens, random, multiList, expected));//containedValuesAsMultiset(multiList, random);
                    multi = multi.deleteAllOccurrences(IterableCursorable.of(values));
                    removeAllByOccurrence(expected, values);
                    break;
                case 3: //deleteAllOccurrences(Collection)
                    values = HashMultiset.create(makeDeleteValues(tokens, random, multiList, expected));//containedValuesAsMultiset(multiList, random);
                    multi = multi.deleteAllOccurrences(values);
                    removeAllByOccurrence(expected, values);
                    break;
                case 4: //deleteAllOccurrences(JMet)
                    JImmutableMultiset<String> values2 = JImmutables.multiset(makeDeleteValues(tokens, random, multiList, expected));//containedValuesAsJMet(multiList, random);
                    multi = multi.deleteAllOccurrences(values2);
                    removeAllByOccurrence(expected, values2.occurrenceCursor());
                    break;
                }

            }
            verifyContents(multi, expected);
            verifyMultiList(multiList, expected);
            System.out.printf("contains %d%n", multi.valueCount());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(6)) {
                case 0: //containsAtLeast(T, int)
                    String value = (random.nextBoolean()) ? containedValue(multiList, random) : notContainedValue(tokens, random, expected);
                    int count = expected.count(value);
                    int checkCount = (count == 0) ? random.nextInt(2) : random.nextInt(2 * count);
                    if (multi.containsAtLeast(value, checkCount) != (expected.count(value) >= checkCount)) {
                        throw new RuntimeException(String.format("containsAtLeast(value, count) method call failed for %s, %d - expected %b found %b%n", value, count, expected.contains(value), multi.contains(value)));
                    }
                    break;
                case 1: //containsAllOccurrences(Cursorable)
                    List<String> values = makeContainsRepeatsList(tokens, random, multiList, expected);
                    if (multi.containsAllOccurrences(IterableCursorable.of(values)) != containsAllByOccurrence(expected, values)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(Cursorable) method call failed for %s - expected %b found %b%n", values, multi.containsAllOccurrences(IterableCursorable.of(values)), containsAllByOccurrence(expected, values)));
                    }
                    break;
                case 2: //containsAllOccurrences(Collection)
                    values = makeContainsRepeatsList(tokens, random, multiList, expected);
                    if (multi.containsAllOccurrences(values) != containsAllByOccurrence(expected, values)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(Collection) method call failed for %s - expected %b found %b%n", values, multi.containsAllOccurrences(values), containsAllByOccurrence(expected, values)));
                    }
                    break;
                case 3: //containsAllOccurrences(JMet)
                    JImmutableMultiset<String> values2 = JImmutables.multiset(makeContainsRepeatsList(tokens, random, multiList, expected));
                    if (multi.containsAllOccurrences(values2) != containsAllByOccurrence(expected, values2)) {
                        throw new RuntimeException(String.format("containsAllOccurrences(JImmutableMultiset) method call failed for %s - expected %b found %b%n", values2, multi.containsAllOccurrences(values2), containsAllByOccurrence(expected, values2)));

                    }
                    break;
                case 4: //containsAllOccurrences(JSet)
                    JImmutableSet<String> values3 = JImmutables.set(makeContainsList(tokens, random, multiList, expected));
                    if (multi.containsAllOccurrences(values3) != expected.containsAll(values3.getSet())) {
                        throw new RuntimeException(String.format("containsAllOccurrences(JImmutableSet) method call failed for %s - expected %b found %b%n", values3, multi.containsAllOccurrences(values3), expected.containsAll(values3.getSet())));

                    }
                    break;
                case 5: //containsAllOccurrences(Set)
                    Set<String> values4 = new HashSet<String>(makeContainsList(tokens, random, multiList, expected));
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

        System.out.printf("cleanup %d%n", multi.valueCount());
        long start = System.nanoTime();
        while (multiList.size() > 20) {
            switch (random.nextInt(5)) {
            case 0: //intersection(Cursorable)
                Multiset<String> values = HashMultiset.create();
                Set<Integer> valueIndex = new TreeSet<Integer>();
                for (int n = 0, limit = random.nextInt(size / 3); n < limit; ++n) {
                    int index = random.nextInt(multiList.size());
                    valueIndex.add(index);
                    values.add(multiList.get(index));
                }
                expected.removeAll(values);
                multi = multi.intersection(IterableCursorable.of(expected));
                deleteAllAt(valueIndex, multiList);
                break;
            case 1: //intersection(Collection)
                values = HashMultiset.create();
                valueIndex = new TreeSet<Integer>();
                for (int n = 0, limit = random.nextInt(size / 3); n < limit; ++n) {
                    int index = random.nextInt(multiList.size());
                    valueIndex.add(index);
                    values.add(multiList.get(index));
                }
                expected.removeAll(values);
                multi = multi.intersection(expected);
                deleteAllAt(valueIndex, multiList);
                break;
            case 2: //intersection(Jet)
                JImmutableSet<String> values2 = JImmutables.set();
                valueIndex = new TreeSet<Integer>();
                for (int n = 0, limit = random.nextInt(size / 3); n < limit; ++n) {
                    int index = random.nextInt(multiList.size());
                    valueIndex.add(index);
                    values2 = values2.insert(multiList.get(index));
                }
                expected = expectedIntersection(expected, values2);
                multi = multi.intersection(values2);
                multiList = intersectionAt(multiList, valueIndex);
                break;
            case 3: //intersection(Set)
                values2 = JImmutables.set();
                valueIndex = new TreeSet<Integer>();
                for (int n = 0, limit = random.nextInt(size / 3); n < limit; ++n) {
                    int index = random.nextInt(multiList.size());
                    valueIndex.add(index);
                    values2 = values2.insert(multiList.get(index));
                }
                expected = expectedIntersection(expected, values2);
                multi = multi.intersection(values2.getSet());
                multiList = intersectionAt(multiList, valueIndex);
                break;
            case 4: //intersection(JMet)
                if (random.nextBoolean()) {
                    JImmutableMultiset<String> values3 = JImmutables.multiset();
                    valueIndex = new TreeSet<Integer>();
                    for (int n = 0; n < size - (random.nextInt(size / 3)); ++n) {
                        int index = random.nextInt(multiList.size());
                        valueIndex.add(index);
                        values3 = values3.insert(multiList.get(index));
                    }
                    expected = expectedIntersection(expected, values3);
                    multi = multi.intersection(values3);
                    multiList = intersectionAt(multiList, valueIndex);
                }
                break;
            case 5: //setCount(T, int)
                int index = random.nextInt(multiList.size());
                String value = multiList.get(index);
                int newCount = random.nextInt(3);
                multi = multi.setCount(value, newCount);
                expected.setCount(value, newCount);
                multiList.remove(index);    //?????????
                break;
            }
        }
        long elapsed = System.nanoTime() - start;
        totalTime += elapsed;

        if (multi.valueCount() != 0) {
            verifyContents(multi, expected);
            multi = multi.deleteAll();
            expected.clear();
        }
        if (multi.size() != 0) {
            throw new RuntimeException(String.format("expected multiset to be empty but it contained %d keys%n", multi.size()));
        }
        verifyContents(multi, expected);
        System.out.printf("JImmutableMultisetStressTest on %s completed without errors%n", multi.getClass().getSimpleName());
        System.out.println("----------");
        System.out.println("elapsed time : " + elapsed);
        System.out.println("average time: " + totalTime / (double)runs);
    }

    private void verifyCursor(final JImmutableMultiset<String> multi,
                              final Multiset<String> expected)
    {
        final List<String> expectedList;
        final List<JImmutableMap.Entry<String, Integer>> entries = new ArrayList<JImmutableMap.Entry<String, Integer>>();
        System.out.printf("checking cursor with size %d%n", multi.valueCount());

        //HashMultiset iterates in a different order than JImmutableHashMultiset. Therefore, to test
        // the cursor and iterator for that class, the list of values cannot be built from
        //expected. Other implementations are in the same order as the Guava classes,
        //and can have the test list built from expected.
        if (multi instanceof JImmutableHashMultiset) {
            expectedList = asList(multi.occurrenceCursor());
            for (String uniqueVal : multi) {
                entries.add(new MapEntry<String, Integer>(uniqueVal, multi.count(uniqueVal)));
            }
        } else {
            expectedList = asList(expected);
            for (Multiset.Entry<String> expectedEntry : expected.entrySet()) {
                entries.add(new MapEntry<String, Integer>(expectedEntry.getElement(), expectedEntry.getCount()));
            }
        }

        if (expectedList.size() != multi.valueCount()) {
            throw new RuntimeException(String.format("expectedList built incorrectly - size expected %d size found %d%n", multi.valueCount(), expectedList.size()));
        }
        if (entries.size() != multi.size()) {
            throw new RuntimeException(String.format("entries list built incorrectly - size expected %d size found %d%n", multi.size(), entries.size()));
        }

        StandardCursorTest.listCursorTest(expectedList, multi.occurrenceCursor());
        StandardCursorTest.listCursorTest(entries, multi.entryCursor());
    }

    private void verifyContents(final JImmutableMultiset<String> multi,
                                final Multiset<String> expected)
    {
        System.out.printf("checking contents with size %d%n", multi.valueCount());
        if (multi.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), multi.isEmpty()));
        }
        if (multi.size() != expected.elementSet().size()) {
            throw new RuntimeException(String.format("unique value size mismatch - expected %d found %d%n", expected.elementSet().size(), multi.size()));
        }
        if (multi.valueCount() != expected.size()) {
            throw new RuntimeException(String.format("occurrence size mismatch - expected %d found %d%n", expected.size(), multi.valueCount()));
        }
        for (String expectedValue : expected.elementSet()) {
            if (expected.count(expectedValue) != multi.count(expectedValue)) {
                throw new RuntimeException(String.format("count mismatch on %s - expected %d found %d%n", expectedValue, expected.count(expectedValue), multi.count(expectedValue)));
            }
        }
        if (!expected.elementSet().equals(multi.getSet())) {
            throw new RuntimeException("method call failed - getSet()\n");
        }
        multi.checkInvariants();
    }

    private void verifyMultiList(ArrayList<String> multiList,
                                 Multiset<String> expected)
    {
        if (multiList.size() != expected.size()) {
            throw new RuntimeException(String.format("multiList size mismatch - expected: %d, multiList: %d", expected.size(), multiList.size()));
        }
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

    //precondition: multi must not have had a union performed with values yet.
    private void multiListUnion(List<String> multiList,
                                JImmutableMultiset<String> multi,
                                Iterable<String> values)
    {
        Multiset<String> intermediate = HashMultiset.create();
        for (String value : values) {
            intermediate.add(value);
        }

        for (Multiset.Entry<String> entry : intermediate.entrySet()) {
            String value = entry.getElement();
            int multiCount = multi.count(value);
            int valuesCount = entry.getCount();
            if (valuesCount > multiCount) {
                int difference = valuesCount - multiCount;
                for (int n = 0; n < difference; ++n) {
                    multiList.add(value);
                }
            }
        }
    }

    private Multiset<String> removeAllByOccurrence(Multiset<String> expected,
                                                   Iterable<String> values)
    {
        for (String value : values) {
            expected.remove(value);
        }
        return expected;
    }

    private Multiset<String> expectedUnion(Multiset<String> expected,
                                           Iterable<String> values)
    {
        Multiset<String> intermediate = LinkedHashMultiset.create();
        for (String value : values) {
            intermediate.add(value);
        }
        for (Multiset.Entry<String> entry : intermediate.entrySet()) {
            String value = entry.getElement();
            int valueCount = entry.getCount();
            int expectedCount = expected.count(value);
            if (valueCount > expectedCount) {
                expected.setCount(value, valueCount);
            }
        }
        return expected;
    }

    //on average, adds 1 occurrence to multi
    private JImmutableList<String> makeInsertValues(JImmutableList<String> tokens,
                                                    Random random,
                                                    List<String> multiList,
                                                    Multiset<String> expected)
    {
        JImmutableList<String> values = JImmutables.list();
        String value = "";
        int size = random.nextInt(3);
        if (size > 0) {
            value = (random.nextBoolean()) ? notContainedValue(tokens, random, expected) : containedValue(multiList, random);
            values = values.insert(value);
        }
        if (size > 1) {
            switch (random.nextInt(3)) {
            case 0:
                values.insert(value);
                break;
            case 1:
                values.insert(containedValue(multiList, random));
                break;
            case 2:
                values.insert(notContainedValue(tokens, random, expected));
            }
        }
        return values;
    }

    //on average, removes 1 occurrence from multi
    private JImmutableList<String> makeDeleteValues(JImmutableList<String> tokens,
                                                    Random random,
                                                    List<String> multiList,
                                                    Multiset<String> expected)
    {
        JImmutableList<String> values = JImmutables.list();
        switch (random.nextInt(10)) {
        case 0: //deletes 0 - empty
            break;
        case 1: //deletes 0 - value not in multi
            values = values.insert(notContainedValue(tokens, random, expected));
            break;
        case 2: //deletes 1 - value in multi
            int index = random.nextInt(multiList.size());   //method is called in delete loop, so multiList.size should never be zero
            values = values.insert(multiList.get(index));
            multiList.remove(index);
            break;
        case 3: //deletes 1 - value in multi, value not in multi
            index = random.nextInt(multiList.size());
            values = values.insert(multiList.get(index));
            multiList.remove(index);

            values = values.insert(notContainedValue(tokens, random, expected));
            break;
        case 4: //deletes 2 - two unique values in multi
        case 5: //deletes 2 - two unique values in multi
            for (int n = 0; n < 2; ++n) {
                index = random.nextInt(multiList.size());
                values = values.insert(multiList.get(index));
                multiList.remove(index);
            }
            break;
        }
        return values;
    }

    private JImmutableSet<String> makeInsertJSet(JImmutableList<String> tokens,
                                                 Random random,
                                                 List<String> multiList,
                                                 Multiset<String> expected)
    {
        JImmutableSet<String> values = JImmutables.set();
        int size = random.nextInt(3);
        while (values.size() < size) {
            String value = (random.nextBoolean()) ? notContainedValue(tokens, random, expected) : containedValue(multiList, random);
            values = values.insert(value);
        }
        return values;
    }

    private List<String> makeContainsRepeatsList(JImmutableList<String> tokens,
                                                 Random random,
                                                 List<String> multiList,
                                                 Multiset<String> expected)
    {
        JImmutableList<String> values = JImmutables.list(makeContainsList(tokens, random, multiList, expected));
        List<String> repeats = asList(values);
        for (String value : values) {
            int count = expected.count(value);
            int listCount = (count == 0) ? random.nextInt(2) : random.nextInt(2 * count);
            if (listCount <= 0) {
                repeats.remove(value);
            } else {
                for (int n = 1; n < listCount; ++n) {
                    repeats.add(value);
                }
            }
        }
        return repeats;
    }

    protected void deleteAllAt(Set<Integer> index,
                               ArrayList<String> setList)
    {
        List<Integer> listIndex = new LinkedList<Integer>(index);
        for (int i = listIndex.size() - 1; i >= 0; --i) {
            setList.remove(listIndex.get(i).intValue());
        }
    }

    private Multiset<String> expectedIntersection(Multiset<String> expected,
                                                  JImmutableMultiset<String> values)
    {
        Multiset<String> newMulti = getEmptyMultiset();
        for (Cursor<JImmutableMap.Entry<String, Integer>> c = values.entryCursor().start(); c.hasValue(); c = c.next()) {
            String value = c.getValue().getKey();
            int valueCount = c.getValue().getValue();
            int expectedCount = expected.count(value);
            newMulti.add(value, Math.min(valueCount, expectedCount));
        }
        return newMulti;
    }

    private Multiset<String> expectedIntersection(Multiset<String> expected,
                                                  JImmutableSet<String> values)
    {

        Multiset<String> newMulti = getEmptyMultiset();
        for (String value : values) {
            if (expected.contains(value)) {
                newMulti.add(value);
            }
        }
        return newMulti;
    }

    private ArrayList<String> intersectionAt(ArrayList<String> multiList,
                                             Set<Integer> indices)
    {
        ArrayList<String> newList = new ArrayList<String>();
        for (int index : indices) {
            newList.add(multiList.get(index));
        }
        return newList;
    }

    private Multiset<String> getEmptyMultiset()
    {
        if (multi instanceof JImmutableHashMultiset) {
            return HashMultiset.create();
        } else if (multi instanceof JImmutableTreeMultiset) {
            return TreeMultiset.create();
        } else if (multi instanceof JImmutableInsertOrderMultiset) {
            return LinkedHashMultiset.create();
        } else {
            throw new RuntimeException();
        }
    }
}