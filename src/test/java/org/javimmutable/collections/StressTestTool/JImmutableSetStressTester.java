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
import org.javimmutable.collections.inorder.JImmutableInsertOrderSet;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        options = options.insert("set").insert(makeClassOption(set));
        return options;
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Set<String> expected = expectedClass.newInstance();
        JImmutableRandomAccessList<String> setList = JImmutables.ralist();
        final int size = random.nextInt(100000);
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
                    setList = insertUnique(value, setList, expected);
                    set = set.insert(value);
                    expected.add(value);
                    verifySetList(setList);
                    break;
                case 1:
                    setList = insertAllUnique(values, setList, expected);
                    set = set.insertAll(IterableCursorable.of(values));
                    expected.addAll(values);
                    verifySetList(setList);
                    break;
                case 2:
                    setList = insertAllUnique(values, setList, expected);
                    set = set.insertAll(values);
                    expected.addAll(values);
                    verifySetList(setList);
                    break;
                case 3:
                    setList = insertAllUnique(values, setList, expected);
                    set = set.union(IterableCursorable.of(values));
                    expected.addAll(values);
                    verifySetList(setList);
                    break;
                case 4:
                    setList = insertAllUnique(values, setList, expected);
                    set = set.union(values);
                    expected.addAll(values);
                    verifySetList(setList);
                    break;
                default:
                    throw new RuntimeException();
                }

            }
            verifyContents(set, expected);
            verifySetList(setList);

            System.out.printf("shrinking %d%n", set.size());
            for (int i = 0; i < size / 6; ++i) {
                List<String> values = new ArrayList<String>();
                Set<Integer> valueIndex = new TreeSet<Integer>();
                for (int n = 0; n < random.nextInt(3); ++n) {
                    int index = random.nextInt(setList.size());
                    valueIndex.add(index);
                    values.add(setList.get(index));
                }
                verifyValues(values);
                switch (random.nextInt(3)) {
                case 0:
                    int index = random.nextInt(setList.size());
                    set = set.delete(setList.get(index));
                    expected.remove(setList.get(index));
                    setList = setList.delete(index);
                    break;
                case 1:
                    set = set.deleteAll(IterableCursorable.of(values));
                    expected.removeAll(values);
                    setList = deleteAllAt(valueIndex, setList);
                    break;
                case 2:
                    set = set.deleteAll(values);
                    expected.removeAll(values);
                    setList = deleteAllAt(valueIndex, setList);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
        }
        verifyContents(set, expected);
        verifySetList(setList);

        System.out.printf("cleanup %d%n", expected.size());
        while (setList.size() > 2) {
            List<String> values = new ArrayList<String>();
            Set<Integer> valueIndex = new TreeSet<Integer>();
            for (int n = 0; n < random.nextInt(size / 3); ++n) {
                int index = random.nextInt(setList.size());
                valueIndex.add(index);
                values.add(setList.get(index));
            }
            switch (random.nextInt(4)) {
            case 0:
                expected.removeAll(values);
                set = set.intersection(IterableCursorable.of(expected));
                setList = deleteAllAt(valueIndex, setList);
                break;
            case 1:
                expected.removeAll(values);
                set = set.intersection(expected.iterator());
                setList = deleteAllAt(valueIndex, setList);
                break;
            case 2:
                expected.removeAll(values);
                set = set.intersection(expected);
                setList = deleteAllAt(valueIndex, setList);
                break;
            case 3:
                JImmutableSet<String> set2 = set.deleteAll(values);
                set = set.intersection(set2);
                expected.removeAll(values);
                setList = deleteAllAt(valueIndex, setList);
                break;
            default:
                throw new RuntimeException();
            }
            verifySetList(setList);
        }
        verifyContents(set, expected);
        set = set.deleteAll();
        expected.clear();
        if (set.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", set.size()));
        }
        verifyContents(set, expected);
        System.out.printf("JImmutableSetStressTest on %s completed without errors%n", set.getClass().getSimpleName());
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
        if (!set.containsAll(IterableCursorable.of(expected))) {
            throw new RuntimeException("method call failed - containsAll(Cursorable)\n");
        }
        if (!set.containsAll(expected)) {
            throw new RuntimeException("method call failed - containsAll(Collection)\n");
        }
        if ((!set.containsAny(IterableCursorable.of(expected))) && (expected.size() != 0)) {
            throw new RuntimeException("method call failed - containsAny(Cursorable)\n");
        }
        if ((!set.containsAny(expected)) && (expected.size() != 0)) {
            throw new RuntimeException("method call failed - containsAny(Collection)\n");
        }

        if (set instanceof JImmutableHashSet || set instanceof JImmutableHashMultiset) {
            List<String> setAsList = asList(set.getSet());
            StandardCursorTest.listCursorTest(setAsList, set.cursor());
            StandardCursorTest.listIteratorTest(setAsList, set.iterator());
        } else {
            List<String> expectedAsList = asList(expected);
            StandardCursorTest.listCursorTest(expectedAsList, set.cursor());
            StandardCursorTest.listIteratorTest(expectedAsList, set.iterator());
        }
        set.checkInvariants();
    }

    private List<String> asList(Set<String> expectedSet)
    {
        List<String> expectedList = new ArrayList<String>();
        expectedList.addAll(expectedSet);
        return expectedList;
    }

    private JImmutableRandomAccessList<String> insertUnique(String value,
                                                            JImmutableRandomAccessList<String> setList,
                                                            Set<String> expected)
    {
        if (!expected.contains(value)) {
            setList = setList.insert(value);
        }
        return setList;
    }

    private JImmutableRandomAccessList<String> insertAllUnique(List<String> values,
                                                               JImmutableRandomAccessList<String> setList,
                                                               Set<String> expected)
    {
        for (String value : values) {
            if (!expected.contains(value)) {
                setList = setList.insert(value);
            }
        }
        return setList;
    }

    private JImmutableRandomAccessList<String> deleteAllAt(Set<Integer> index,
                                                           JImmutableRandomAccessList<String> setList)
    {
        List<Integer> listIndex = new LinkedList<Integer>(index);
        for (int i = listIndex.size() - 1; i >= 0; --i) {
            setList = setList.delete(listIndex.get(i));
        }
        return setList;
    }

    //Because of how values are inserted into setList, there will sometimes be duplicates (from insertAllUnique).
    //This ensures there are never more than two duplicates, and that setList is never smaller than set. This is
    // a safeguard that setList and set are very similar even though they will not be identical.
    private void verifySetList(JImmutableList<String> setList)
    {
        int setListSize = setList.size();
        if (!((setListSize <= (set.size() + 5)) && (setListSize >= (set.size())))) {
            throw new RuntimeException(String.format("set size mismatch - set: %d, setList: %d", set.size(), setListSize));
        }
    }

    //Because of how values are inserted into setList, there will sometimes be duplicates (from insertAllUnique).
    //This means the List of values being deleted from set during the shrinking loop can contain values that have
    //already been deleted from set. This ensures there is never more than one such value, as a safeguard that setList
    //and set are very similar even though they will not be identical.
    private void verifyValues(List<String> values)
    {
        int track = 0;
        for (String value : values) {
            if (!set.contains(value)) {
                ++track;
            }
        }
        if (track > 1) {
            throw new RuntimeException(String.format("values error - %d not contained in set", track));
        }
    }

}
