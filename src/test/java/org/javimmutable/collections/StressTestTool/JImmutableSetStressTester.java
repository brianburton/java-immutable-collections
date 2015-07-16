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
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Test program for all implementations of JImmutableSet, including JImmutableMultiset. Divided
 * into four sections: growing (adds new values), shrinking (removes values), contains (tests
 * methods that check for specified values), and cleanup (empties the set of all values).
 */
public class JImmutableSetStressTester
        extends AbstractStressTestable
{
    private JImmutableSet<String> set;
    private final Class<? extends Set> expectedClass;
    public int repeats;
    public static int smallerByReps;
    public static int biggerByReps;
    public int growth;
    public int generatedSize;
    public static int deleteAll;
    private static int runs;

    public JImmutableSetStressTester(JImmutableSet<String> set,
                                     Class<? extends Set> expectedClass)
    {
        this.set = set;
        this.expectedClass = expectedClass;
        repeats = 0;
        smallerByReps = 0;
        growth = 0;
        generatedSize = 0;
        deleteAll = 0;
        runs = 0;

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
        repeats = 0;
        growth = 0;
        generatedSize = 0;
        ++runs;
        @SuppressWarnings("unchecked") Set<String> expected = expectedClass.newInstance();
        JImmutableSet<String> set = this.set;
        JImmutableRandomAccessList<String> setList = JImmutables.ralist();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableSetStressTest on %s of size %d%n", set.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", set.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(5)) {
                case 0: //insert(T)
                    int initialSize = set.size();
                    String value = makeValue(tokens, random);
                    int growthRepeats = (expected.contains(value)) ? 0 : 1;

                    setList = insertUnique(value, setList, expected);
                    set = set.insert(value);
                    expected.add(value);

                    verifySetList(setList, expected);
                    growth += set.size() - initialSize;
                    checkGrowth(set.size() - initialSize, growthRepeats);
                    generatedSize += 1;
                    break;
                case 1: //insertAll(Cursorable)
                    initialSize = set.size();
                    JImmutableList<String> values = makeInsertJList(tokens, random);
                    growthRepeats = getGrowth(expected, values);

                    setList = insertAllUnique(values, setList, expected);
                    set = set.insertAll(values);
                    expected.addAll(values.getList());

                    verifySetList(setList, expected);
                    growth += set.size() - initialSize;
                    checkGrowth(set.size() - initialSize, growthRepeats);
                    generatedSize += values.size();
                    break;
                case 2: //insertAll(Collection)
                    initialSize = set.size();
                    values = makeInsertJList(tokens, random);
                    growthRepeats = getGrowth(expected, values);

                    setList = insertAllUnique(values, setList, expected);
                    set = set.insertAll(values.getList());
                    expected.addAll(values.getList());

                    verifySetList(setList, expected);
                    growth += set.size() - initialSize;
                    checkGrowth(set.size() - initialSize, growthRepeats);
                    generatedSize += values.size();
                    break;
                case 3: //union(Cursorable)
                    initialSize = set.size();
                    values = makeInsertJList(tokens, random);
                    setList = insertAllUnique(values, setList, expected);
                    growthRepeats = getGrowth(expected, values);

                    set = set.union(values);
                    expected.addAll(values.getList());

                    verifySetList(setList, expected);
                    growth += set.size() - initialSize;
                    checkGrowth(set.size() - initialSize, growthRepeats);
                    generatedSize += values.size();
                    break;
                case 4: //union(Collection)
                    initialSize = set.size();
                    values = makeInsertJList(tokens, random);
                    setList = insertAllUnique(values, setList, expected);
                    growthRepeats = getGrowth(expected, values);

                    set = set.union(values.getList());
                    expected.addAll(values.getList());

                    verifySetList(setList, expected);
                    growth += set.size() - initialSize;
                    checkGrowth(set.size() - initialSize, growthRepeats);
                    generatedSize += values.size();
                    break;
                default:
                    throw new RuntimeException();
                }

            }
            verifyContents(set, expected);
            verifySetList(setList, expected);

            System.out.printf("shrinking %d%n", set.size());
            for (int i = 0; i < size / 6; ++i) {
                switch (random.nextInt(3)) {
                case 0: //delete(T)
                    int index = random.nextInt(setList.size());
                    set = set.delete(setList.get(index));
                    expected.remove(setList.get(index));
                    setList = setList.delete(index);
                    break;
                case 1: //deleteAll(Cursorable)
                    JImmutableList<String> jvalues = JImmutables.list();
                    switch (random.nextInt(10)) {
                    case 0: //deletes 0 - empty
                        break;
                    case 1: //deletes 0 - value not in set
                        jvalues = jvalues.insert(makeValue(tokens, random));
                        break;
                    case 2: //deletes 1 - value in set
                        index = random.nextInt(setList.size());
                        jvalues = jvalues.insert(setList.get(index));
                        setList = setList.delete(index);
                        break;
                    case 3: //deletes 1 - two copies of value in set
                        index = random.nextInt(setList.size());
                        jvalues = jvalues.insert(setList.get(index)).insert(setList.get(index));
                        setList = setList.delete(index);
                        break;
                    case 4: //deletes 1 - value in set, value not in set
                        index = random.nextInt(setList.size());
                        jvalues = jvalues.insert(setList.get(index));
                        jvalues = jvalues.insert(makeValue(tokens, random));
                        setList = setList.delete(index);
                        break;
                    case 5: //deletes 1 - two copies of value in set, value not in set
                        index = random.nextInt(setList.size());
                        jvalues = jvalues.insert(setList.get(index)).insert(setList.get(index));
                        jvalues = jvalues.insert(makeValue(tokens, random));
                        setList = setList.delete(index);
                        break;
                    case 6: //deletes 2 - two unique values in set
                    case 7: //deletes 2 - two unique values in set
                        for (int n = 0; n < 2; ++n) {
                            index = random.nextInt(setList.size());
                            jvalues = jvalues.insert(setList.get(index));
                            setList = setList.delete(index);
                        }
                        break;
                    default:
                        throw new RuntimeException();
                    }
//                    for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
//                        index = random.nextInt(setList.size());
//                        jvalues = jvalues.insert(setList.get(index));
//                        setList = setList.delete(index);
//                    }
                    set = set.deleteAll(jvalues);
                    expected.removeAll(jvalues.getList());
                    break;
                case 2: //deleteAll(Collection)
                    List<String> values = new ArrayList<String>();
                    for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
                        index = random.nextInt(setList.size());
                        values.add(setList.get(index));
                        setList = setList.delete(index);
                    }
                    set = set.deleteAll(values);
                    expected.removeAll(values);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(set, expected);
            verifySetList(setList, expected);


            System.out.printf("contains %d%n", set.size());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(5)) {
                case 0: //contains(T)
                    String value = (random.nextBoolean()) ? valueInSet(setList, random) : makeValue(tokens, random);
                    if (set.contains(value) != expected.contains(value)) {
                        throw new RuntimeException(String.format("contains(value) method call failed for %s - expected %b found %b%n", value, expected.contains(value), set.contains(value)));
                    }
                    break;
                case 1: //containsAll(Cursorable)
                    List<String> values = makeContainsList(tokens, random, setList);
                    if (set.containsAll(IterableCursorable.of(values)) != expected.containsAll(values)) {
                        throw new RuntimeException(String.format("containsAll(Cursorable) method call failed for %s - expected %b found %b%n", values, set.containsAll(IterableCursorable.of(values)), expected.containsAll(values)));
                    }
                    break;
                case 2: //containsAll(Collection)
                    values = makeContainsList(tokens, random, setList);
                    if (set.containsAll(values) != expected.containsAll(values)) {
                        throw new RuntimeException(String.format("containsAll(Collection) method call failed for %s - expected %b found %b%n", values, set.containsAll(values), expected.containsAll(values)));
                    }
                    break;
                case 3: //containsAny(Cursorable)
                    values = makeContainsList(tokens, random, setList);
                    if (set.containsAny(IterableCursorable.of(values)) != containsAny(expected, values)) {
                        throw new RuntimeException(String.format("containsAny(Cursorable) method call failed for %s - expected %b found %b%n", values, set.containsAny(IterableCursorable.of(values)), containsAny(expected, values)));
                    }
                    break;
                case 4: //containsAny(Collection)
                    values = makeContainsList(tokens, random, setList);
                    if (set.containsAny(values) != containsAny(expected, values)) {
                        throw new RuntimeException(String.format("containsAny(Collection) method call failed for %s - expected %b found %b%n", values, set.containsAny(values), containsAny(expected, values)));
                    }
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(set, expected);
        }
        verifyContents(set, expected);
        verifySetList(setList, expected);

        if (set.size() + repeats < size) {
            ++smallerByReps;

        }
        if (set.size() + repeats >= size) {
            ++biggerByReps;
        }


        System.out.printf("cleanup %d%n", expected.size());
        int threshold = size / 100;
        while (setList.size() > threshold) {
            List<String> deleteValues = new ArrayList<String>();
            for (int n = 0, limit = random.nextInt(size / 30); setList.size() >= 1 && n < limit; ++n) {
                int index = random.nextInt(setList.size());
                deleteValues.add(setList.get(index));
                setList = setList.delete(index);
            }
            switch (random.nextInt(1)) {
            case 0: //intersection(Cursorable)
//                ArrayList<String> intersectValues = new ArrayList<String>(expected);
//                intersectValues.removeAll(deleteValues);
//                for (int n = 0, limit = random.nextInt(100); n < limit; ++n) {
//                    intersectValues.add(makeValue(tokens, random));
//                }
                expected.removeAll(deleteValues);
                set = set.intersection(IterableCursorable.of(expected));
                //expected.retainAll(intersectValues);
                break;
            case 1: //intersection(Collection)
//                intersectValues = new ArrayList<String>(expected);
//                intersectValues.removeAll(deleteValues);
//                for (int n = 0, limit = random.nextInt(size / 12); n < limit; ++n) {
//                    intersectValues.add(makeValue(tokens, random));
//                }
                expected.removeAll(deleteValues);
                set = set.intersection(expected.iterator());
                //expected.retainAll(intersectValues);
                break;
            case 2: //intersection(Set)
//                JImmutableSet<String> intersectJSet = JImmutables.set(set);
//                intersectJSet = intersectJSet.deleteAll(deleteValues);
//                for (int n = 0, limit = random.nextInt(size / 12); n < limit; ++n) {
//                    intersectJSet = intersectJSet.insert(makeValue(tokens, random));
//                }
                expected.removeAll(deleteValues);
                set = set.intersection(expected);
                //expected.retainAll(intersectJSet.getSet());
                break;
            case 3: //intersection(JSet)
//                intersectJSet = JImmutables.set(set);
//                intersectJSet = intersectJSet.deleteAll(deleteValues);
//                for (int n = 0, limit = random.nextInt(size / 12); n < limit; ++n) {
//                    intersectJSet = intersectJSet.insert(makeValue(tokens, random));
//                }
                expected.removeAll(deleteValues);
                set = set.intersection(JImmutables.set(expected));
                //expected.retainAll(intersectJSet.getSet());
                break;
            default:
                throw new RuntimeException();
            }
            verifySetList(setList, expected);
        }

        if (set.size() != 0) {
            verifyContents(set, expected);
            set = set.deleteAll();
            expected.clear();
            ++deleteAll;
        }

        if (set.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", set.size()));
        }
        verifyContents(set, expected);
        System.out.printf("JImmutableSetStressTest on %s completed without errors%n", set.getClass().getSimpleName());
        System.out.println("-------STATS-------");
        System.out.println("smaller by repeats: " + smallerByReps);
        System.out.println("bigger by repeats: " + biggerByReps);
        System.out.println("-------");
        System.out.println("deleteAlls: " + ((double)deleteAll / (double)runs));
    }

    private void verifyContents(final JImmutableSet<String> set,
                                final Set<String> expected)
    {
        System.out.printf("checking contents with size %d%n", set.size());
        if (set.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), set.isEmpty()));
        }
        if (set.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), set.size()));
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
            throw new RuntimeException("method call failed - getSet()\n");
        }

        set.checkInvariants();
    }

    private void verifyCursor(final JImmutableSet<String> set,
                              final Set<String> expected)
    {
        if (set instanceof JImmutableHashSet || set instanceof JImmutableHashMultiset) {
            List<String> setAsList = asList(set.getSet());
            StandardCursorTest.listCursorTest(setAsList, set.cursor());
            StandardCursorTest.listIteratorTest(setAsList, set.iterator());
        } else {
            List<String> expectedAsList = asList(expected);
            StandardCursorTest.listCursorTest(expectedAsList, set.cursor());
            StandardCursorTest.listIteratorTest(expectedAsList, set.iterator());
        }
    }

    private JImmutableRandomAccessList<String> insertUnique(String value,
                                                            JImmutableRandomAccessList<String> setList,
                                                            Set<String> expected)
    {
        if (!expected.contains(value)) {
            setList = setList.insert(value);
        } else {
            ++repeats;
        }
        return setList;
    }

    private JImmutableRandomAccessList<String> insertAllUnique(Iterable<String> values,
                                                               JImmutableRandomAccessList<String> setList,
                                                               Set<String> expected)
    {
        JImmutableSet<String> duplicates = JImmutables.set();
        for (String value : values) {
            if (!expected.contains(value) && !duplicates.contains(value)) {
                setList = setList.insert(value);
            } else if (expected.contains(value) && !duplicates.contains(value)){
                ++repeats;
            }
            duplicates = duplicates.insert(value);
        }
        return setList;
    }

    private void verifySetList(JImmutableList<String> setList,
                               Set<String> expected)
    {
        int setListSize = setList.size();
        if (!((setListSize <= (expected.size())) && (setListSize >= (expected.size())))) {
            throw new RuntimeException(String.format("set size mismatch - set: %d, setList: %d", expected.size(), setListSize));
        }
    }

    private List<String> makeContainsList(JImmutableList<String> tokens,
                                          Random random,
                                          JImmutableRandomAccessList<String> setList)
    {
        List<String> values = new ArrayList<String>();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            if (random.nextBoolean()) {
                values.add(valueInSet(setList, random));
            } else {
                values.add(makeValue(tokens, random));
            }
        }
        return values;
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

    private String valueInSet(JImmutableRandomAccessList<String> list,
                              Random random)
    {
        return list.get(random.nextInt(list.size()));
    }


    public int getGrowth(Set<String> expected,
                         Iterable<String> values)
    {
        int extra = 0;
        JImmutableSet<String> duplicates = JImmutables.set();
        for (String value : values) {
            if (!expected.contains(value) && !duplicates.contains(value)) {
                ++extra;
            }
            duplicates = duplicates.insert(value);
        }
        return extra;
    }

    public void checkGrowth(int change,
                            int expectedChange)
    {
        if (change != expectedChange) {
            throw new RuntimeException(String.format("incorrect size change - expected %d found %d", expectedChange, change));
        }
    }

    @Override
    //on average, adds 1 value to the set
    protected JImmutableList<String> makeInsertJList(JImmutableList<String> tokens,
                                                     Random random)
    {
        JImmutableList<String> list = JImmutables.list();
        switch (random.nextInt(4)) {
        case 0: //adds 0 - empty
            break;
        case 1: //adds 1 - unique value
            list = list.insert(makeValue(tokens, random));
            break;
        case 2: //adds 1 - two copies of unique value
            String value = makeValue(tokens, random);
            list = list.insert(value).insert(value);
            break;
        case 3: //adds 2 - two unique values
            list = list.insert(makeValue(tokens, random)).insert(makeValue(tokens, random));
            break;
        }
        return list;
    }

    //on average, adds 1 value to set
    private JImmutableList<String> makeUnionJList(JImmutableList<String> tokens,
                                                  Random random,
                                                  JImmutableRandomAccessList<String> setList)
    {
        JImmutableList<String> list = JImmutables.list();
        switch (random.nextInt(8)) {
        case 0: //adds 0 - empty
            break;
        case 1: //adds 0 - value already in set
            list = list.insert(setList.get(random.nextInt(setList.size())));
            break;
        case 2: //adds 1 - unique value
            list = list.insert(makeValue(tokens, random));
            break;
        case 3: //adds 1 - unique value, value already in set
            list = list.insert(makeValue(tokens, random));
            list = list.insert(setList.get(random.nextInt(setList.size())));
            break;
        case 4: //adds 1 - two copies of unique value
            String value = makeValue(tokens, random);
            list = list.insert(value).insert(value);
            break;
        case 5: //adds 1 - two copies of unique value, value already in set
            value = makeValue(tokens, random);
            list = list.insert(value).insert(value);
            list = list.insert(setList.get(random.nextInt(setList.size())));
            break;
        case 6:
        case 7:
            list = list.insert(makeValue(tokens, random)).insert(makeValue(tokens, random));
            break;
        default:
            throw new RuntimeException();
        }
        return list;
    }
}
