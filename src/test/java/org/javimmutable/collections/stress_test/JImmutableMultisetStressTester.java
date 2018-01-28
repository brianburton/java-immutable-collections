///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.stress_test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ExpectedOrderSorter;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMultiset;
import org.javimmutable.collections.tree.JImmutableTreeMultiset;
import org.javimmutable.collections.tree.JImmutableTreeMultisetTest;
import org.javimmutable.collections.util.JImmutables;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.javimmutable.collections.common.StandardSerializableTests.verifySerializable;

/**
 * Test program for all implementations of JImmutableMultiset. Divided into four sections:
 * growing (adds new occurrences), shrinking (removes occurrences), contains (tests methods
 * that check for specific occurrences), and cleanup (empties the set of all occurrences).
 * <p>
 * This class alone does not test every method in JImmutableMultiset. Methods that manipulate
 * the values in the multiset with no effect on the number of occurrences are tested in the
 * JImmutableSetStressTester. Those tests are not repeated here. Similarly, the goal size
 * in this test refers to the total number of occurrences (multi.occurrenceCount()), rather than
 * the number of unique values.
 * <p>
 * The cleanup is slow due to the nature of the intersection method and the large size of
 * the multiset. The loop will take several seconds to run an average of 35 intersections
 * before the multiset is empty.
 */
public class JImmutableMultisetStressTester
    extends AbstractSetStressTestable
{
    private final JImmutableMultiset<String> multi;

    public JImmutableMultisetStressTester(JImmutableMultiset<String> multi)
        throws NoSuchMethodException
    {
        this.multi = multi.deleteAll();
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
        JImmutableMultiset<String> multi = this.multi;
        Multiset<String> expected = getEmptyMultiset();
        List<String> multiList = new ArrayList<>();
        final int size = 1 + random.nextInt(100000);
        System.out.printf("JImmutableMultisetStressTest on %s of size %d%n", getName(multi), size);

        for (SizeStepCursor.Step step : SizeStepCursor.steps(6, size, random)) {
            System.out.printf("growing %d%n", multi.occurrenceCount());
            while (expected.size() < step.growthSize()) {
                switch (random.nextInt(11)) {
                    case 0: { //insert(T)
                        String value = makeInsertValue(tokens, random, multiList, expected);
                        multi = multi.insert(value);
                        expected.add(value);
                        multiList.add(value);
                        break;
                    }
                    case 1: { //insert(T, int)
                        String value = makeInsertValue(tokens, random, multiList, expected);
                        int count = random.nextInt(3);
                        multi = multi.insert(value, count);
                        expected.add(value, count);
                        for (int n = 0; n < count; ++n) {
                            multiList.add(value);
                        }
                        break;
                    }
                    case 2: { //setCount(T, int) - also tested in the shrink loop.
                        String value = makeInsertValue(tokens, random, multiList, expected);
                        int oldCount = multi.count(value);
                        int newCount = oldCount + random.nextInt(3);
                        multi = multi.setCount(value, newCount);
                        expected.setCount(value, newCount);
                        for (int n = 0; n < newCount - oldCount; ++n) {
                            multiList.add(value);
                        }
                        break;
                    }
                    case 3: { //insertAll(Cursorable)
                        Multiset<String> values = HashMultiset.create(makeInsertJList(tokens, random, multiList, expected));
                        multi = multi.insertAll(plainIterable(values));
                        expected.addAll(values);
                        multiList.addAll(values);
                        break;
                    }
                    case 4: { //insertAll(Collection)
                        Multiset<String> values = HashMultiset.create(makeInsertJList(tokens, random, multiList, expected));
                        multi = multi.insertAll(values);
                        expected.addAll(values);
                        multiList.addAll(values);
                        break;
                    }
                    case 5: { //insertAll(JMet)
                        JImmutableMultiset<String> values = JImmutables.multiset(makeInsertJList(tokens, random, multiList, expected));
                        multi = multi.insertAll(values);
                        for (Cursor<String> c = values.occurrenceCursor().start(); c.hasValue(); c = c.next()) {
                            String value = c.getValue();
                            expected.add(value);
                            multiList.add(value);
                        }
                        break;
                    }
                    case 6:
                    case 7: { //union(Iterable)
                        Multiset<String> values = HashMultiset.create(makeUnionJList(tokens, random, multiList, expected));
                        multiListUnion(multiList, multi, values);
                        expectedUnion(expected, values);
                        multi = multi.union(values);
                        break;
                    }
                    case 8: { //union(JMet)
                        JImmutableMultiset<String> values = JImmutables.multiset(makeUnionJList(tokens, random, multiList, expected));
                        multiListUnion(multiList, multi, values.occurrenceCursor());
                        expectedUnion(expected, values.occurrenceCursor());
                        multi = multi.union(values);
                        break;
                    }
                    case 9: { //union(JSet)
                        JImmutableSet<String> values = makeUnionJSet(tokens, random, multiList, expected);
                        multiListUnion(multiList, multi, values);
                        expectedUnion(expected, values);
                        multi = multi.union(values);
                        break;
                    }
                    case 10: { //union(Set)
                        JImmutableSet<String> values = makeUnionJSet(tokens, random, multiList, expected);
                        multiListUnion(multiList, multi, values);
                        expectedUnion(expected, values);
                        multi = multi.union(values.getSet());
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(multi, expected);
            verifyList(multiList, expected);

            System.out.printf("shrinking %d%n", multi.occurrenceCount());
            while (expected.size() > step.shrinkSize()) {
                switch (random.nextInt(6)) {
                    case 0: { //deleteOccurrence(T, int)
                        int expectedChange = random.nextInt(3);
                        int initialSize = multi.occurrenceCount();
                        multi = shrinkingCaseZero(expectedChange, random, multi, expected, multiList);
                        if (multi.occurrenceCount() == initialSize - expectedChange) {   //ensures 1 value is deleted on average
                            break;
                        }
                    }
                    case 1: { //deleteOccurrence(T)
                        String value = makeDeleteValue(tokens, random, multiList, expected);
                        multi = multi.deleteOccurrence(value);
                        expected.remove(value);
                        break;
                    }
                    case 2: { //setCount(T, int) - also tested in growth loop
                        int expectedChange = random.nextInt(3);
                        int initialSize = multi.occurrenceCount();
                        multi = shrinkingCaseTwo(expectedChange, random, multi, expected, multiList);
                        if (multi.occurrenceCount() == initialSize - expectedChange) {   //ensures 1 value is deleted on average
                            break;
                        }
                    }
                    case 3:
                    case 4: { //deleteAllOccurrences(Iterable)
                        Multiset<String> values = HashMultiset.create(makeDeleteJList(tokens, random, multiList, expected));
                        multi = multi.deleteAllOccurrences(values);
                        removeAllByOccurrence(expected, values);
                        break;
                    }
                    case 5: { //deleteAllOccurrences(JMet)
                        JImmutableMultiset<String> values = JImmutables.multiset(makeDeleteJList(tokens, random, multiList, expected));
                        multi = multi.deleteAllOccurrences(values);
                        removeAllByOccurrence(expected, values.occurrenceCursor());
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(multi, expected);
            verifyList(multiList, expected);

            System.out.printf("contains %d%n", multi.occurrenceCount());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(6)) {
                    case 0: { //containsAtLeast(T, int)
                        String value = (random.nextBoolean()) ? containedValue(multiList, random) : notContainedValue(tokens, random, expected);
                        int count = expected.count(value);
                        int checkCount = (count == 0) ? random.nextInt(2) : random.nextInt(2 * count);
                        if (multi.containsAtLeast(value, checkCount) != (expected.count(value) >= checkCount)) {
                            throw new RuntimeException(String.format("containsAtLeast(value, count) method call failed for" +
                                                                     " %s, %d - expected %b found %b%n",
                                                                     value, count, expected.contains(value), multi.contains(value)));
                        }
                        break;
                    }
                    case 1:
                    case 2: { //containsAllOccurrences(Iterable)
                        List<String> values = makeContainsRepeatsList(tokens, random, multiList, expected);
                        if (multi.containsAllOccurrences(values) != containsAllByOccurrence(expected, values)) {
                            throw new RuntimeException(String.format("containsAllOccurrences(Collection) method call failed " +
                                                                     "for %s - expected %b found %b%n",
                                                                     values, multi.containsAllOccurrences(values),
                                                                     containsAllByOccurrence(expected, values)));
                        }
                        break;
                    }
                    case 3: { //containsAllOccurrences(JMet)
                        JImmutableMultiset<String> values = JImmutables.multiset(makeContainsRepeatsList(tokens, random, multiList, expected));
                        if (multi.containsAllOccurrences(values) != containsAllByOccurrence(expected, values)) {
                            throw new RuntimeException(String.format("containsAllOccurrences(JImmutableMultiset) method call" +
                                                                     " failed for %s - expected %b found %b%n",
                                                                     values, multi.containsAllOccurrences(values),
                                                                     containsAllByOccurrence(expected, values)));
                        }
                        break;
                    }
                    case 4: { //containsAllOccurrences(JSet)
                        JImmutableSet<String> values = JImmutables.set(makeContainsList(tokens, random, multiList, expected));
                        if (multi.containsAllOccurrences(values) != expected.containsAll(values.getSet())) {
                            throw new RuntimeException(String.format("containsAllOccurrences(JImmutableSet) method call " +
                                                                     "failed for %s - expected %b found %b%n",
                                                                     values, multi.containsAllOccurrences(values),
                                                                     expected.containsAll(values.getSet())));
                        }
                        break;
                    }
                    case 5: { //containsAllOccurrences(Set)
                        Set<String> values = new HashSet<>(makeContainsList(tokens, random, multiList, expected));
                        if (multi.containsAllOccurrences(values) != expected.containsAll(values)) {
                            throw new RuntimeException(String.format("containsAllOccurrences(Set) method call failed for %s" +
                                                                     " - expected %b found %b%n",
                                                                     values, multi.containsAllOccurrences(values),
                                                                     expected.containsAll(values)));

                        }
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyCursor(multi, expected);
        }
        verifyContents(multi, expected);
        verifyList(multiList, expected);
        verifyFinalSize(size, multi.occurrenceCount());

        System.out.printf("cleanup %d%n", multi.occurrenceCount());
        multiList = asList(multi.occurrenceCursor());
        int intersects = 0;
        while (multiList.size() > 0) {
            ++intersects;
            Multiset<String> deleteValues = HashMultiset.create();
            for (int n = 0, limit = random.nextInt((size / 18) + 3); multiList.size() >= 1 && n < limit; ++n) {
                int index = random.nextInt(multiList.size());
                deleteValues.add(multiList.get(index));
                multiList.remove(index);
            }
            int command = (multiList.size() < size / 3) ? 5 : 3;
            switch (random.nextInt(command)) {
                case 0:
                case 1: { //intersection(Iterable)
                    Multiset<String> values = makeIntersectValues(tokens, random, multiList, expected);
                    removeAllByOccurrence(expected, deleteValues);
                    multi = multi.intersection(values);
                    break;
                }
                case 2: { //intersection(JMet)
                    //need bigger and smaller versions
                    JImmutableMultiset<String> values;
                    if (random.nextBoolean()) {
                        values = (JImmutableMultiset<String>)makeIntersectInsertable(tokens, random,
                                                                                     multiList, expected,
                                                                                     JImmutables.multiset(multiList));
                    } else {
                        values = makeBigIntersectValues(tokens, random, multiList, expected, JImmutables.multiset(multiList));
                    }
                    removeAllByOccurrence(expected, deleteValues);
                    multi = multi.intersection(values);
                    break;
                }
                case 3: { //intersection(JSet)
                    //need bigger and smaller versions
                    JImmutableSet<String> values;
                    if (random.nextBoolean()) {
                        values = (JImmutableSet<String>)makeIntersectInsertable(tokens, random, multiList,
                                                                                expected, JImmutables.set(multiList));
                    } else {
                        values = makeBigIntersectValues(tokens, random, multiList, expected, JImmutables.multiset(multiList));
                    }
                    expectedSetIntersect(expected, deleteValues);
                    multiList = multiListSetIntersect(multiList);
                    multi = multi.intersection(values);
                    break;
                }
                case 4: { //intersection(Set)
                    //need bigger and smaller versions
                    JImmutableSet<String> values;
                    if (random.nextBoolean()) {
                        values = (JImmutableSet<String>)makeIntersectInsertable(tokens, random, multiList,
                                                                                expected, JImmutables.set(multiList));
                    } else {
                        values = makeBigIntersectValues(tokens, random, multiList, expected, JImmutables.multiset(multiList));

                    }
                    multiList = multiListSetIntersect(multiList);
                    expectedSetIntersect(expected, deleteValues);
                    multi = multi.intersection(values.getSet());
                    break;
                }
                default:
                    throw new RuntimeException();
            }
            verifyList(multiList, expected);
            if (intersects % 10 == 0) {
                verifyContents(multi, expected);
                verifyOrder(multi, multiList);
            }
        }
        if (multi.size() != 0) {
            throw new RuntimeException(String.format("expected multiset to be empty but it contained %d keys%n", multi.size()));
        }
        verifyContents(multi, expected);
        System.out.printf("JImmutableMultisetStressTest on %s completed without errors%n", getName(multi));
    }

    private JImmutableMultiset<String> shrinkingCaseTwo(int toRemove,
                                                        Random random,
                                                        JImmutableMultiset<String> multi,
                                                        Multiset<String> expected,
                                                        List<String> multiList)
    {
        int index = random.nextInt(multiList.size());
        String value = multiList.get(index);
        int oldCount = multi.count(value);
        int newCount = oldCount - toRemove;
        try {
            multi = multi.setCount(value, newCount);
        } catch (IllegalArgumentException exception) {
            if (newCount >= 0) { //exception expected for negative counts, should be ignored in those cases
                throw exception;
            }
            newCount = 0;
            multi = multi.setCount(value, newCount);
        }
        multiListDeleteCount(oldCount - newCount, index, value, multiList, expected);
        expected.setCount(value, newCount);
        return multi;
    }

    private JImmutableMultiset<String> shrinkingCaseZero(int count,
                                                         Random random,
                                                         JImmutableMultiset<String> multi,
                                                         Multiset<String> expected,
                                                         List<String> multiList)
    {
        int index = random.nextInt(multiList.size());
        String value = multiList.get(index);

        multiListDeleteCount(count, index, value, multiList, expected);
        multi = multi.deleteOccurrence(value, count);
        expected.remove(value, count);
        return multi;
    }

    @Nonnull
    private <K> List<JImmutableMap.Entry<K, Integer>> makeMultisetEntriesList(Multiset<K> expected)
    {
        final List<JImmutableMap.Entry<K, Integer>> entries = new ArrayList<>();

        for (Multiset.Entry<K> entry : expected.entrySet()) {
            entries.add(new MapEntry<>(entry.getElement(), entry.getCount()));
        }
        return entries;
    }

    private <K> List<K> extractOccurrences(List<JImmutableMap.Entry<K, Integer>> entries)
    {
        return entries.stream()
            .flatMap(e -> IntStream.range(0, e.getValue()).boxed().map(i -> e.getKey()))
            .collect(Collectors.toList());
    }

    private void verifyCursor(final JImmutableMultiset<String> multi,
                              final Multiset<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", multi.occurrenceCount());

        List<JImmutableMap.Entry<String, Integer>> entries = makeMultisetEntriesList(expected);
        if (multi instanceof JImmutableHashMultiset) {
            final ExpectedOrderSorter<String> ordering = new ExpectedOrderSorter<>(multi.iterator());
            entries = ordering.sort(entries, e -> e.getKey());
        }
        List<String> expectedList = extractOccurrences(entries);

        if (expectedList.size() != multi.occurrenceCount()) {
            throw new RuntimeException(String.format("expectedList built incorrectly - size expected %d size found %d%n",
                                                     multi.occurrenceCount(), expectedList.size()));
        }
        if (entries.size() != multi.size()) {
            throw new RuntimeException(String.format("entries list built incorrectly - size expected %d size found %d%n",
                                                     multi.size(), entries.size()));
        }

        StandardCursorTest.listCursorTest(expectedList, multi.occurrenceCursor());
        StandardCursorTest.listCursorTest(entries, multi.entryCursor());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(expectedList, multi.occurrences());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(entries, multi.entries());
    }

    private void verifyContents(final JImmutableMultiset<String> multi,
                                final Multiset<String> expected)
    {
        System.out.printf("checking contents with size %d%n", multi.occurrenceCount());
        if (multi.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n",
                                                     expected.isEmpty(), multi.isEmpty()));
        }
        if (multi.size() != expected.elementSet().size()) {
            throw new RuntimeException(String.format("unique value size mismatch - expected %d found %d%n",
                                                     expected.elementSet().size(), multi.size()));
        }
        if (multi.occurrenceCount() != expected.size()) {
            throw new RuntimeException(String.format("occurrence size mismatch - expected %d found %d%n",
                                                     expected.size(), multi.occurrenceCount()));
        }
        for (String expectedValue : expected.elementSet()) {
            if (expected.count(expectedValue) != multi.count(expectedValue)) {
                throw new RuntimeException(String.format("count mismatch on %s - expected %d found %d%n",
                                                         expectedValue, expected.count(expectedValue), multi.count(expectedValue)));
            }
        }
        if (!expected.elementSet().equals(multi.getSet())) {
            throw new RuntimeException("method call failed - getSet()\n");
        }
        multi.checkInvariants();
        verifySerializable(this::extraSerializationChecks, multi);
    }

    private void verifyOrder(JImmutableMultiset<String> set,
                             List<String> expected)
    {
        StandardCursorTest.listCursorTest(expected, set.occurrenceCursor());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, set.occurrences());
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

    private void multiListDeleteCount(int change,
                                      int index,
                                      String value,
                                      List<String> multiList,
                                      Multiset<String> expected)
    {
        if (change >= 1) {
            multiList.remove(index);
        }
        if ((change == 2) && (expected.count(value) >= 2)) {
            multiList.remove(value);
        }
    }

    private List<String> multiListSetIntersect(List<String> list)
    {
        return asList(JImmutables.insertOrderSet(list));
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

    private void removeAllByOccurrence(Multiset<String> expected,
                                       Iterable<String> values)
    {
        for (String value : values) {
            expected.remove(value);
        }
    }

    private void expectedSetIntersect(Multiset<String> expected,
                                      Iterable<String> values)
    {
        removeAllByOccurrence(expected, values);
        for (String value : expected) {
            expected.setCount(value, 1);
        }
    }

    private void expectedUnion(Multiset<String> expected,
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
    }

    //on average, adds 1 occurrence to multi when used with insert methods
    private JImmutableList<String> makeInsertJList(JImmutableList<String> tokens,
                                                   Random random,
                                                   List<String> multiList,
                                                   Multiset<String> expected)
    {
        JImmutableList<String> values = JImmutables.list();
        String value = "";
        int size = random.nextInt(3);
        if (size > 0) { //50-50 chance of duplicate or new value
            value = (random.nextBoolean()) ? notContainedValue(tokens, random, expected) : containedValue(multiList, random);
            values = values.insert(value);
        }
        if (size > 1) { //1 in 3 chance of repeat of first value, different value not in multi, different value in multi
            switch (random.nextInt(3)) {
                case 0:
                    values = values.insert(value);
                    break;
                case 1:
                    values = values.insert(containedValue(multiList, random));
                    break;
                case 2:
                    values = values.insert(notContainedValue(tokens, random, expected));
            }
        }
        return values;
    }

    //on average, adds 1 occurrences to multi when used with union methods
    private JImmutableList<String> makeUnionJList(JImmutableList<String> tokens,
                                                  Random random,
                                                  List<String> multiList,
                                                  Multiset<String> expected)
    {
        JImmutableList<String> values = JImmutables.list();
        switch (random.nextInt(6)) {
            case 0: //adds 0 - empty
                break;
            case 1: //adds 0 - value already in multi
                values = values.insert(containedValue(multiList, random));
                break;
            case 2: //adds 1 - value not in multi
                values = values.insert(notContainedValue(tokens, random, expected));
                break;
            case 3: //adds 1 - value not in multi, value in multi
                values = values.insert(notContainedValue(tokens, random, expected));
                values = values.insert(containedValue(multiList, random));
                break;
            case 4: //adds 2 - two different values not in multi
                values = values.insert(notContainedValue(tokens, random, expected));
                values = values.insert(notContainedValue(tokens, random, expected));
                break;
            case 5: //adds 2 - two copies of the same value not in multi
                String value = notContainedValue(tokens, random, expected);
                values = values.insert(value).insert(value);
                break;
        }
        return values;
    }

    private JImmutableSet<String> makeUnionJSet(JImmutableList<String> tokens,
                                                Random random,
                                                List<String> multiList,
                                                Multiset<String> expected)
    {
        JImmutableSet<String> values = JImmutables.set();
        int size = random.nextInt(3);
        for (int n = 0; n < size; ++n) {
            values = values.insert(notContainedValue(tokens, random, expected));
            if (random.nextBoolean()) {
                values = values.insert(containedValue(multiList, random));
            }
        }
        return values;
    }

    //on average, removes 1 occurrence from multi
    private JImmutableList<String> makeDeleteJList(JImmutableList<String> tokens,
                                                   Random random,
                                                   List<String> multiList,
                                                   Multiset<String> expected)
    {
        JImmutableList<String> values = JImmutables.list();
        switch (random.nextInt(6)) {
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
                for (int n = 0; (n < 2) && (multiList.size() > 0); ++n) {
                    index = random.nextInt(multiList.size());
                    values = values.insert(multiList.get(index));
                    multiList.remove(index);
                }
                break;
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

    private <C extends Insertable<String, C>> Insertable<String, C> makeIntersectInsertable(JImmutableList<String> tokens,
                                                                                            Random random,
                                                                                            List<String> multiList,
                                                                                            Multiset<String> expected,
                                                                                            Insertable<String, C> values)
    {
        int maxSize = multiList.size() / 20;
        for (int n = 0, limit = (maxSize > 0) ? random.nextInt(maxSize) : random.nextInt(3); n < limit; ++n) {
            values = values.insert(notContainedValue(tokens, random, expected));
        }
        return values;
    }

    private JImmutableMultiset<String> makeBigIntersectValues(JImmutableList<String> tokens,
                                                              Random random,
                                                              List<String> multiList,
                                                              Multiset<String> expected,
                                                              JImmutableMultiset<String> values)
    {
        int extraSize = (multiList.size() < 60) ? random.nextInt(3) : random.nextInt(multiList.size() / 20);
        while (values.occurrenceCount() < expected.size() + extraSize) {
            String value = notContainedValue(tokens, random, expected);
            values = values.insert(value);
        }
        return values;
    }

    private Multiset<String> makeIntersectValues(JImmutableList<String> tokens,
                                                 Random random,
                                                 List<String> multiList,
                                                 Multiset<String> expected)
    {
        Multiset<String> values = HashMultiset.create(multiList);
        int maxSize = multiList.size() / 20;
        for (int n = 0, limit = (maxSize > 0) ? random.nextInt(maxSize) : random.nextInt(3); n < limit; ++n) {
            values.add(notContainedValue(tokens, random, expected));
        }
        return values;
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


    private void extraSerializationChecks(Object a,
                                          Object b)
    {
        if (a instanceof JImmutableTreeMultiset) {
            JImmutableTreeMultisetTest.extraSerializationChecks(a, b);
        }
    }
}