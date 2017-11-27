///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.ExpectedOrderSorter;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.util.JImmutables;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * Test program for all implementations of JImmutableSet, including JImmutableMultiset. Divided
 * into four sections: growing (adds new values), shrinking (removes values), contains (tests
 * methods that check for specified values), and cleanup (empties the set of all values).
 * <p>
 * The cleanup is slow due to the nature of the intersection method and the large size of
 * the set. The loop will take several seconds to run an average of 50 intersections
 * before the set is empty.
 */
public class JImmutableSetStressTester
    extends AbstractSetStressTestable
{
    private final JImmutableSet<String> set;
    private final Class<? extends Set> expectedClass;
    private final CursorOrder cursorOrder;

    public JImmutableSetStressTester(JImmutableSet<String> set,
                                     Class<? extends Set> expectedClass,
                                     CursorOrder cursorOrder)
    {
        this.set = set;
        this.expectedClass = expectedClass;
        this.cursorOrder = cursorOrder;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("set").insert(makeClassOption(set));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
        throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Set<String> expected = expectedClass.newInstance();
        final RandomKeyManager keys = new RandomKeyManager(random, tokens);
        JImmutableSet<String> set = this.set;
        final int size = 1 + random.nextInt(100000);
        System.out.printf("JImmutableSetStressTest on %s of size %d%n", getName(set), size);

        for (SizeStepCursor.Step step : SizeStepCursor.steps(6, size, random)) {
            System.out.printf("growing %d%n", set.size());
            while (expected.size() < step.growthSize()) {
                switch (random.nextInt(5)) {
                case 0: { //insert(T)
                    String value = keys.randomKey();
                    set = set.insert(value);
                    expected.add(value);
                    keys.allocate(value);
                    break;
                }
                case 1: { //insertAll(Cursorable)
                    JImmutableList<String> values = keys.randomInsertJList();
                    set = set.insertAll(values);
                    expected.addAll(values.getList());
                    keys.allocate(values);
                    break;
                }
                case 2: { //insertAll(Collection)
                    JImmutableList<String> values = keys.randomInsertJList();
                    set = set.insertAll(values.getList());
                    expected.addAll(values.getList());
                    keys.allocate(values);
                    break;
                }
                case 3: { //union(Cursorable)
                    JImmutableList<String> values = keys.randomInsertJList();
                    set = set.union(values);
                    expected.addAll(values.getList());
                    keys.allocate(values);
                    break;
                }
                case 4: { //union(Collection)
                    JImmutableList<String> values = keys.randomInsertJList();
                    set = set.union(values.getList());
                    expected.addAll(values.getList());
                    keys.allocate(values);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(set, expected);
            verifyKeys(keys, expected);

            System.out.printf("shrinking %d%n", set.size());
            while (expected.size() > step.shrinkSize()) {
                switch (random.nextInt(3)) {
                case 0: { //delete(T)
                    String value = keys.randomKey();
                    set = set.delete(value);
                    expected.remove(value);
                    keys.unallocate(value);
                    break;
                }
                case 1: { //deleteAll(Cursorable)
                    JImmutableList<String> values = keys.randomDeleteJList(step.shrinkSize());
                    set = set.deleteAll(values);
                    expected.removeAll(values.getList());
                    keys.unallocate(values);
                    break;
                }
                case 2: { //deleteAll(Collection)
                    JImmutableList<String> values = keys.randomDeleteJList(step.shrinkSize());
                    set = set.deleteAll(values.getList());
                    expected.removeAll(values.getList());
                    keys.unallocate(values);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(set, expected);
            verifyKeys(keys, expected);

            System.out.printf("contains %d%n", set.size());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(5)) {
                case 0: { //contains(T)
                    String value = keys.randomKey();
                    if (set.contains(value) != expected.contains(value)) {
                        throw new RuntimeException(String.format("contains(value) method call failed for %s - expected %b found %b%n",
                                                                 value, expected.contains(value), set.contains(value)));
                    }
                    break;
                }
                case 1:
                case 2: { //containsAll(Iterable)
                    JImmutableList<String> values = keys.randomContainsJList(5);
                    if (set.containsAll(values.getList()) != expected.containsAll(values.getList())) {
                        throw new RuntimeException(String.format("containsAll(Iterable) method call failed for %s - expected %b found %b%n",
                                                                 values, set.containsAll(values),
                                                                 expected.containsAll(values.getList())));
                    }
                    break;
                }
                case 3:
                case 4: { //containsAny(Iterable)
                    JImmutableList<String> values = keys.randomContainsJList(5);
                    if (set.containsAny(values.getList()) != containsAny(expected, values.getList())) {
                        throw new RuntimeException(String.format("containsAny(Iterable) method call failed for %s - expected %b found %b%n",
                                                                 values, set.containsAny(values),
                                                                 expected.containsAll(values.getList())));
                    }
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(set, expected);
        }
        verifyContents(set, expected);
        verifyKeys(keys, expected);
        verifyFinalSize(size, set.size());

        System.out.printf("cleanup %d%n", expected.size());
        int intersects = 0;
        while (keys.size() > size / 80) {
            final int numberToRemove = 1 + random.nextInt(expected.size());
            final int numberToRetain = expected.size() - numberToRemove;
            final int command = random.nextInt(4);
            System.out.printf("command %d removing %d%n", command, numberToRemove);
            switch (command) {
            case 0: {//intersection(Cursorable)
                JImmutableList<String> intersectionValues = keys.randomIntersectionKeysJList(numberToRetain, Math.min(numberToRetain, random.nextInt(5)), random.nextInt(10));
                set = set.intersection(intersectionValues);
                expected.retainAll(new HashSet<>(intersectionValues.getList()));
                keys.retainAll(intersectionValues);
                break;
            }
            case 1: { //intersection(Collection)
                JImmutableList<String> intersectionValues = keys.randomIntersectionKeysJList(numberToRetain, Math.min(numberToRetain, random.nextInt(5)), random.nextInt(10));
                set = set.intersection(intersectionValues.getList());
                expected.retainAll(new HashSet<>(intersectionValues.getList()));
                keys.retainAll(intersectionValues);
                break;
            }
            case 2: { //intersection(JSet)
                final int padding = random.nextBoolean() ? numberToRemove + 1 : random.nextInt(Math.max(1, numberToRemove - 1));
                final Set<String> intersectionValues = keys.randomIntersectionKeysSet(numberToRetain, padding);
                set = set.intersection(JImmutables.set(intersectionValues));
                expected.retainAll(intersectionValues);
                keys.retainAll(intersectionValues);
                break;
            }
            default: { //intersection(Set)
                final int padding = random.nextBoolean() ? numberToRemove + 1 : random.nextInt(Math.max(1, numberToRemove - 1));
                final Set<String> intersectionValues = keys.randomIntersectionKeysSet(numberToRetain, padding);
                set = set.intersection(intersectionValues);
                expected.retainAll(intersectionValues);
                keys.retainAll(intersectionValues);
                break;
            }
            }
            if (intersects % 3 == 0) {
                verifyContents(set, expected);
                verifyOrder(set, keys.allAllocatedJList());
            }
            ++intersects;
        }
        System.out.printf("cleaned up with %d intersections%n", intersects);
        if (set.size() != 0) {
            verifyContents(set, expected);
            set = set.deleteAll();
            expected.clear();
        }
        if (set.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", set.size()));
        }
        verifyContents(set, expected);
        System.out.printf("JImmutableSetStressTest on %s completed without errors%n", getName(set));
    }

    private void verifyKeys(RandomKeyManager keys,
                            Set<String> expected)
    {
        if (keys.size() != expected.size()) {
            throw new RuntimeException(String.format("expected %d allocated found %d", expected.size(), keys.size()));
        }
        for (String value : expected) {
            if (!keys.allocated(value)) {
                throw new RuntimeException(String.format("expected %s to be allocated but was not", value));
            }
        }
    }

    private void verifyContents(final JImmutableSet<String> set,
                                final Set<String> expected)
    {
        System.out.printf("checking contents with size %d%n", set.size());
        if (set.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n",
                                                     expected.isEmpty(), set.isEmpty()));
        }
        if (set.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n",
                                                     expected.size(), set.size()));
        }
        for (String expectedValue : expected) {
            if (!set.contains(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s but not in %s%n",
                                                         expectedValue, set.getClass().getSimpleName()));
            }
        }
        for (String expectedValue : set) {
            if (!expected.contains(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s but not in Set%n", expectedValue));
            }
        }
        if (!expected.equals(set.getSet())) {
            throw new RuntimeException("method call failed - getSet()\n");
        }
        set.checkInvariants();
    }

    private void verifyCursor(final JImmutableSet<String> set,
                              final Set<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", set.size());

        List<String> expectedList = asList(expected);
        if (cursorOrder == CursorOrder.UNORDERED) {
            final ExpectedOrderSorter<String> ordering = new ExpectedOrderSorter<>(set.iterator());
            expectedList = ordering.sort(expectedList, Function.identity());
        }

        StandardIterableStreamableTests.verifyOrderedUsingCollection(expectedList, set);
        StandardCursorTest.listCursorTest(expectedList, set.cursor());
        StandardCursorTest.listIteratorTest(expectedList, set.iterator());
    }

    private void verifyOrder(JImmutableSet<String> set,
                             JImmutableList<String> expected)
    {
        if (cursorOrder == CursorOrder.INSERT_ORDER) {
            StandardCursorTest.listCursorTest(expected.getList(), set.cursor());
        }
    }

    private boolean containsAny(Set<String> expected,
                                List<String> values)
    {
        for (String value : values) {
            if (expected.contains(value)) {
                return true;
            }
        }
        return false;
    }
}