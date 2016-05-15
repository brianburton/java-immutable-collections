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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.setmap.JImmutableHashSetMap;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableSetMap. Divided into five sections:
 * growing (adds new key-set pairs), updating (adds and removes values in the sets of the
 * setmap without changing the keys), shrinking (removes key-set pairs), contains (tests
 * methods that check for keys or values in a set), and cleanup (empties the setmap of
 * all key-set pairs).
 * <p/>
 * This tester was designed so that the setmap produced would contain sets of a large
 * variety of sizes. On average, 25% of the sets in the setmap will be empty by the end
 * of the test. 18% will contain only one value. 52% will contain between two and ten
 * values, and the remaining 5% will contain between eleven and over a hundred values.
 */
public class JImmutableSetMapStressTester
        extends AbstractMapStressTestable
{
    private final JImmutableSetMap<String, String> setmap;
    private final Class<? extends Map> expectedClass;

    public JImmutableSetMapStressTester(JImmutableSetMap<String, String> setmap,
                                        Class<? extends Map> expectedClass)
    {
        this.setmap = setmap;
        this.expectedClass = expectedClass;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("smap").insert("setmap").insert(makeClassOption(setmap));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        JImmutableSetMap<String, String> setmap = this.setmap;
        @SuppressWarnings("unchecked") Map<String, JImmutableSet<String>> expected = expectedClass.newInstance();
        List<String> keysList = new ArrayList<String>();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableSetMapStressTest on %s of size %d%n", getName(setmap), size);

        for (SizeStepCursor.Step step : SizeStepCursor.steps(6, size, random)) {
            System.out.printf("growing keys %d%n", setmap.size());
            while (expected.size() < step.growthSize()) {
                String key = unusedKey(tokens, random, expected);
                keysList.add(key);
                switch (random.nextInt(4) + 7) {
                case 0: { //assign(K, JSet)
                    JImmutableSet<String> values = makeGrowingSet(tokens, random);
                    setmap = setmap.assign(key, values);
                    expected.put(key, values);
                    break;
                }
                case 1: { //insert(K, V)
                    String value = makeValue(tokens, random);
                    setmap = setmap.insert(key, value);
                    addAt(expected, key, value);
                    break;
                }
                case 2: { //insert(Entry<K, V>)
                    String value = makeValue(tokens, random);
                    MapEntry<String, String> entry = new MapEntry<String, String>(key, value);
                    setmap = (JImmutableSetMap<String, String>)setmap.insert(entry);
                    addAt(expected, key, value);
                    break;
                }
                case 3: { //insertAll(K, Cusorable)
                    JImmutableList<String> values = makeGrowingList(tokens, random);
                    setmap = setmap.insertAll(key, values);
                    addAllAt(expected, key, values);
                    break;
                }
                case 4: { //insertAll(K, Collection)
                    JImmutableList<String> values = makeGrowingList(tokens, random);
                    setmap = setmap.insertAll(key, values.getList());
                    addAllAt(expected, key, values);
                    break;
                }
                case 5: { //union(K, Cursorable)
                    JImmutableList<String> values = makeGrowingList(tokens, random);
                    setmap = setmap.union(key, values);
                    unionAt(expected, key, values);
                    break;
                }
                case 6: { //union(K, Collection)
                    JImmutableList<String> values = makeGrowingList(tokens, random);
                    setmap = setmap.union(key, values.getList());
                    unionAt(expected, key, values);
                    break;
                }
                case 7: { //intersection(K, Cursorable)
                    JImmutableList<String> values = makeIntersectList(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values);
                    expected.put(key, JImmutables.<String>set());
                    break;
                }
                case 8: { //intersection(K, Collection)
                    JImmutableList<String> values = makeIntersectList(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values.getList());
                    expected.put(key, JImmutables.<String>set());
                    break;
                }
                case 9: { //intersection(K, JSet)
                    JImmutableSet<String> values = makeIntersectSet(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values);
                    expected.put(key, JImmutables.<String>set());
                    break;
                }
                case 10: { //intersection(K, Set)
                    JImmutableSet<String> values = makeIntersectSet(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values.getSet());
                    expected.put(key, JImmutables.<String>set());
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("updating %d%n", setmap.size());
            System.out.printf("  growing sets%n");
            for (int i = 0; i < setmap.size(); ++i) {
                String key = containedKey(keysList, random);
                switch (random.nextInt(7)) {
                case 0: { //assign(K, JSet)
                    JImmutableSet<String> valuesSet = makeUpdateSet(tokens, random, key, expected);
                    setmap = setmap.assign(key, valuesSet);
                    expected.put(key, valuesSet);
                    break;
                }
                case 1: { //insert(K, V)
                    String value = makeUpdateValue(tokens, random, key, expected);
                    setmap = setmap.insert(key, value);
                    addAt(expected, key, value);
                    break;
                }
                case 2: { //insert(Entry<K, V>)
                    String value = makeUpdateValue(tokens, random, key, expected);
                    MapEntry<String, String> entry = new MapEntry<String, String>(key, value);
                    setmap = (JImmutableSetMap<String, String>)setmap.insert(entry);
                    addAt(expected, key, value);
                    break;
                }
                case 3: { //insertAll(K, Cursorable)
                    JImmutableList<String> values = makeUpdateList(tokens, random, key, expected);
                    setmap = setmap.insertAll(key, values);
                    addAllAt(expected, key, values);
                    break;
                }
                case 4: { //insertAll(K, Collection)
                    JImmutableList<String> values = makeUpdateList(tokens, random, key, expected);
                    setmap = setmap.insertAll(key, values.getList());
                    addAllAt(expected, key, values);
                    break;
                }
                case 5: { //union(K, Cursorable)
                    JImmutableList<String> values = makeUpdateList(tokens, random, key, expected);
                    setmap = setmap.union(key, values);
                    unionAt(expected, key, values);
                    break;
                }
                case 6: { //union(K, Collection)
                    JImmutableList<String> values = makeUpdateList(tokens, random, key, expected);
                    setmap = setmap.union(key, values.getList());
                    unionAt(expected, key, values);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            System.out.print("  ");
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("  shrinking sets%n");
            for (int i = 0; i < setmap.size(); ++i) {
                switch (random.nextInt(7)) {
                case 0: { //deleteAll(K, Cursorable)
                    String key = (random.nextBoolean()) ? containedKey(keysList, random) : unusedKey(tokens, random, expected);
                    JImmutableList<String> values = makeDeleteList(tokens, random, key, expected);
                    setmap = setmap.deleteAll(key, values);
                    removeAllAt(expected, key, values);
                    break;
                }
                case 1: { //deleteAll(K, Collection)
                    String key = (random.nextBoolean()) ? containedKey(keysList, random) : unusedKey(tokens, random, expected);
                    JImmutableList<String> values = makeDeleteList(tokens, random, key, expected);
                    setmap = setmap.deleteAll(key, values.getList());
                    removeAllAt(expected, key, values);
                    break;
                }
                case 2: { //delete(K, V)
                    String key = (random.nextBoolean()) ? containedKey(keysList, random) : unusedKey(tokens, random, expected);
                    String value = (random.nextBoolean()) ? keysList.get(random.nextInt(keysList.size())) : makeValue(tokens, random);
                    setmap = setmap.delete(key, value);
                    removeAt(expected, key, value);
                    break;
                }
                case 3: { //intersection(K, Cursorable)
                    String key = containedKey(keysList, random);
                    JImmutableList<String> values = makeIntersectList(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values);
                    intersectionAt(expected, key, values);
                    break;
                }
                case 4: { //intersection(K, Collection)
                    String key = containedKey(keysList, random);
                    JImmutableList<String> values = makeIntersectList(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values.getList());
                    intersectionAt(expected, key, values);
                    break;
                }
                case 5: { //intersection(K, JSet)
                    String key = containedKey(keysList, random);
                    JImmutableSet<String> values = makeIntersectSet(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values);
                    intersectionAt(expected, key, values);
                    break;
                }
                case 6: { //intersection(K, Set)
                    String key = containedKey(keysList, random);
                    JImmutableSet<String> values = makeIntersectSet(tokens, random, key, expected);
                    setmap = setmap.intersection(key, values.getSet());
                    intersectionAt(expected, key, values);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);

            System.out.printf("shrinking keys %d%n", setmap.size());
            while (expected.size() > step.shrinkSize()) {
                //delete(K)
                String key = makeDeleteKey(tokens, random, keysList, expected);
                setmap = setmap.delete(key);
                expected.remove(key);
            }
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("contains %d%n", setmap.size());
            for (int i = 0; i < size / 12; ++i) {
                String key = (random.nextBoolean()) ? unusedKey(tokens, random, expected) : keysList.get(random.nextInt(keysList.size()));
                switch (random.nextInt(8)) {
                case 0: { //contains(K, V)
                    String value = (random.nextBoolean()) ? makeValue(tokens, random) : valueInSet(key, expected, random);
                    if (setmap.contains(key, value) != (expected.containsKey(key) && expected.get(key).contains(value))) {
                        throw new RuntimeException(String.format("contains(key, value) method call failed for %s, %s - expected %b found %b%n", key, value, setmap.contains(key, value), (expected.containsKey(key) && expected.get(key).contains(value))));
                    }
                    break;
                }
                case 1: { //containsAll(K, Cursorable)
                    JImmutableList<String> values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAll(key, values) != (expected.containsKey(key) && expected.get(key).containsAll(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAll(key, values), (expected.containsKey(key) && expected.get(key).containsAll(values))));
                    }
                    break;
                }
                case 2: { //containsAll(K, Collection)
                    JImmutableList<String> values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAll(key, values.getList()) != (expected.containsKey(key) && expected.get(key).containsAll(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAll(key, values.getList()), (expected.containsKey(key) && expected.get(key).containsAll(values))));
                    }
                    break;
                }
                case 3: { //containsAny(K, Cursorable)
                    JImmutableList<String> values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAny(key, values) != (expected.containsKey(key) && expected.get(key).containsAny(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAny(key, values), (expected.containsKey(key) && expected.get(key).containsAny(values))));
                    }
                    break;
                }
                case 4: { //containsAny(K, Collection)
                    JImmutableList<String> values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAny(key, values.getList()) != (expected.containsKey(key) && expected.get(key).containsAny(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAny(key, values.getList()), (expected.containsKey(key) && expected.get(key).containsAny(values))));
                    }
                    break;
                }
                case 5: { //get(K)
                    JImmutableSet<String> set = setmap.get(key);
                    JImmutableSet<String> expectedSet = (expected.containsKey(key)) ? expected.get(key) : null;
                    if (!((set == null && expectedSet == null) || (expectedSet != null && expectedSet.equals(set)))) {
                        throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedSet, set));
                    }
                    break;
                }
                case 6: { //getValueOr(K, V)
                    JImmutableSet<String> set = setmap.getValueOr(key, JImmutables.set(""));
                    JImmutableSet<String> expectedSet = (expected.containsKey(key)) ? expected.get(key) : JImmutables.set("");
                    if (!set.equals(expectedSet)) {
                        throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - expected %s found %s%n", key, expectedSet, set));
                    }
                    break;
                }
                case 7: { //find(K)
                    Holder<JImmutableSet<String>> holder = setmap.find(key);
                    Holder<JImmutableSet<String>> expectedHolder = (expected.containsKey(key)) ? Holders.of(expected.get(key)) : Holders.<JImmutableSet<String>>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedHolder, holder));
                    }
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
        }
        verifyCursor(setmap, expected);
        verifyFinalSize(size, setmap.size());
        //printStats(setmap);
        System.out.printf("cleanup %d%n", setmap.size());
        int threshold = random.nextInt(3);
        while (setmap.size() > threshold) {
            //delete(K)
            int index = random.nextInt(keysList.size());
            String key = keysList.get(index);
            setmap = setmap.delete(key);
            expected.remove(key);
            keysList.remove(index);
        }
        if (setmap.size() != 0) {
            verifyContents(setmap, expected);
            setmap = setmap.deleteAll();
            expected.clear();
        }
        if (setmap.size() != 0) {
            throw new RuntimeException(String.format("expected setmap to be empty but it contained %d keys%n", setmap.size()));
        }
        verifyContents(setmap, expected);
        System.out.printf("JImmutableSetMapStressTest on %s completed without errors%n", getName(setmap));
    }

    private void verifyContents(final JImmutableSetMap<String, String> setmap,
                                final Map<String, JImmutableSet<String>> expected)
    {
        System.out.printf("checking contents with size %s%n", setmap.size());
        if (setmap.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), setmap.isEmpty()));
        }
        if (setmap.size() != expected.size()) {
            throw new RuntimeException(String.format("key size mismatch - expected %d found %d%n", expected.size(), setmap.size()));
        }
        for (String key : expected.keySet()) {
            if (!setmap.contains(key)) {
                throw new RuntimeException(String.format("key mismatch - %s expected but not found in setmap ", key));
            }
        }
        for (Map.Entry<String, JImmutableSet<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            JImmutableSet<String> expectedSet = entry.getValue();
            JImmutableSet<String> set = setmap.getSet(key);
            if (!expectedSet.equals(set)) {
                throw new RuntimeException(String.format("values mismatch for key %s - expected jset %s found jset %s%n", key, expectedSet, set));
            }
        }
        setmap.checkInvariants();
    }

    private void verifyCursor(final JImmutableSetMap<String, String> setmap,
                              final Map<String, JImmutableSet<String>> expected)
    {
        System.out.printf("checking cursor with size %d%n", setmap.size());
        List<String> keys = new ArrayList<String>();
        List<JImmutableMap.Entry<String, JImmutableSet<String>>> entriesForCursor = new ArrayList<JImmutableMap.Entry<String, JImmutableSet<String>>>();
        List<JImmutableMap.Entry<String, JImmutableSet<String>>> entriesForIterator = new ArrayList<JImmutableMap.Entry<String, JImmutableSet<String>>>();


        if (setmap instanceof JImmutableHashSetMap) {
            for (JImmutableMap.Entry<String, JImmutableSet<String>> entry : setmap) {
                keys.add(entry.getKey());
                entriesForCursor.add(new MapEntry<String, JImmutableSet<String>>(entry.getKey(), entry.getValue()));
            }
            for (Cursor<JImmutableMap.Entry<String, JImmutableSet<String>>> c = setmap.cursor().start(); c.hasValue(); c = c.next()) {
                entriesForIterator.add(new MapEntry<String, JImmutableSet<String>>(c.getValue().getKey(), c.getValue().getValue()));
            }
        } else {
            keys.addAll(expected.keySet());
            for (Map.Entry<String, JImmutableSet<String>> entry : expected.entrySet()) {
                entriesForCursor.add(new MapEntry<String, JImmutableSet<String>>(entry.getKey(), entry.getValue()));
            }
            entriesForIterator = entriesForCursor;
        }

        StandardCursorTest.listCursorTest(keys, setmap.keysCursor());
        StandardCursorTest.listCursorTest(entriesForCursor, setmap.cursor());
        StandardCursorTest.listIteratorTest(entriesForIterator, setmap.iterator());

        for (Map.Entry<String, JImmutableSet<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            List<String> values = asList(entry.getValue());
            StandardCursorTest.listCursorTest(values, setmap.valuesCursor(key));
        }
    }


    private void unionAt(Map<String, JImmutableSet<String>> expected,
                         String key,
                         Cursorable<String> values)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.union(values));
    }

    private void intersectionAt(Map<String, JImmutableSet<String>> expected,
                                String key,
                                Cursorable<String> values)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.intersection(values));
    }

    private void removeAllAt(Map<String, JImmutableSet<String>> expected,
                             String key,
                             Cursorable<String> values)
    {
        if (expected.containsKey(key)) {
            JImmutableSet<String> set = expected.get(key);
            expected.put(key, set.deleteAll(values));
        }
    }

    private void removeAt(Map<String, JImmutableSet<String>> expected,
                          String key,
                          String value)
    {
        if (expected.containsKey(key)) {
            JImmutableSet<String> set = expected.get(key);
            expected.put(key, set.delete(value));
        }
    }

    private void addAt(Map<String, JImmutableSet<String>> expected,
                       String key,
                       String value)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        set = set.insert(value);
        expected.put(key, set);
    }

    private void addAllAt(Map<String, JImmutableSet<String>> expected,
                          String key,
                          Cursorable<String> values)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.insertAll(values));
    }

    private JImmutableList<String> makeGrowingList(JImmutableList<String> tokens,
                                                   Random random)
    {
        JImmutableList<String> list = JImmutables.list();
        int limit;
        int command = random.nextInt(100);
        if (command < 5) {
            limit = 0;
        } else if (command < 50) {
            limit = random.nextInt(2) + 1;
        } else if (command < 90) {
            limit = random.nextInt(10) + 1;
        } else {
            limit = random.nextInt(command) + 10;
        }
        for (int i = 0; i < limit; ++i) {
            list = list.insert(makeValue(tokens, random));
        }
        return list;
    }

    private JImmutableSet<String> makeGrowingSet(JImmutableList<String> tokens,
                                                 Random random)
    {
        JImmutableSet<String> set = JImmutables.set();
        int limit;
        int command = random.nextInt(100);
        if (command < 5) {
            limit = 0;
        } else if (command < 50) {
            limit = random.nextInt(2) + 1;
        } else if (command < 90) {
            limit = random.nextInt(10) + 1;
        } else {
            limit = random.nextInt(command) + 10;
        }
        while (set.size() < limit) {
            set = set.insert(makeValue(tokens, random));
        }
        return set;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    private JImmutableList<String> makeUpdateList(JImmutableList<String> tokens,
                                                  Random random,
                                                  String key,
                                                  Map<String, JImmutableSet<String>> expected)
    {
        JImmutableList<String> values = JImmutables.list();
        JImmutableList<String> expectedSet = JImmutables.list(expected.get(key));
        for (int i = 0, limit = random.nextInt(5); i < limit; ++i) {
            if (random.nextBoolean() || expectedSet.size() == 0) {
                values = values.insert(makeValue(tokens, random));
            } else {
                values = values.insert(expectedSet.get(random.nextInt(expectedSet.size())));
            }
        }
        return values;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    private JImmutableSet<String> makeUpdateSet(JImmutableList<String> tokens,
                                                Random random,
                                                String key,
                                                Map<String, JImmutableSet<String>> expected)
    {
        JImmutableSet<String> values = JImmutables.set();
        JImmutableList<String> expectedSet = JImmutables.list(expected.get(key));
        int limit = random.nextInt(5);
        while (values.size() < limit) {
            if (random.nextBoolean() || expectedSet.size() == 0) {
                values = values.insert(makeValue(tokens, random));
            } else {
                values = values.insert(expectedSet.get(random.nextInt(expectedSet.size())));
            }
        }
        return values;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    protected String makeUpdateValue(JImmutableList<String> tokens,
                                     Random random,
                                     String key,
                                     Map<String, JImmutableSet<String>> expected)
    {
        String value;
        JImmutableSet<String> expectedSet = expected.get(key);
        if (random.nextBoolean() || expectedSet.size() == 0) {
            value = makeValue(tokens, random);
            while (expectedSet.contains(value)) {
                value = makeValue(tokens, random);
            }
        } else {
            JImmutableRandomAccessList<String> list = JImmutables.ralist(expectedSet);
            value = list.get(random.nextInt(list.size()));
        }
        return value;
    }

    private JImmutableList<String> makeIntersectList(JImmutableList<String> tokens,
                                                     Random random,
                                                     String key,
                                                     Map<String, JImmutableSet<String>> expected)
    {
        JImmutableRandomAccessList<String> values;
        if (expected.containsKey(key)) {
            values = JImmutables.ralist(expected.get(key));
            for (int i = 0, limit = random.nextInt(3); i < limit && values.size() >= 1; ++i) {
                values = values.delete(random.nextInt(values.size()));
            }
        } else {
            values = JImmutables.ralist();
        }
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            values = values.insert(makeValue(tokens, random));
        }
        return values;
    }

    private JImmutableSet<String> makeIntersectSet(JImmutableList<String> tokens,
                                                   Random random,
                                                   String key,
                                                   Map<String, JImmutableSet<String>> expected)
    {
        JImmutableSet<String> values;
        if (expected.containsKey(key)) {
            values = expected.get(key);
            JImmutableRandomAccessList<String> list = JImmutables.ralist(values);
            for (int i = 0, limit = random.nextInt(3); i < limit && list.size() >= 1; ++i) {
                int index = random.nextInt(list.size());
                values = values.delete(list.get(index));
                list = list.delete(index);
            }
        } else {
            values = JImmutables.set();
        }
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            values = values.insert(makeValue(tokens, random));
        }
        return values;
    }

    private String valueInSet(String key,
                              Map<String, JImmutableSet<String>> expected,
                              Random random)
    {
        return (!expected.containsKey(key) || expected.get(key).isEmpty()) ? key : makeValue(JImmutables.list(expected.get(key)), random);
    }

    private JImmutableList<String> makeContainsList(String key,
                                                    Map<String, JImmutableSet<String>> expected,
                                                    Random random,
                                                    JImmutableList<String> tokens)
    {
        JImmutableList<String> values = JImmutables.list();
        if (!expected.containsKey(key) || expected.get(key).isEmpty()) {
            for (int n = 0, limit = random.nextInt(5); n < limit; ++n) {
                values = values.insert(makeValue(tokens, random));
            }
        } else {
            JImmutableList<String> setValues = JImmutables.list(expected.get(key));
            for (int n = 0, limit = random.nextInt(5); n < limit; ++n) {
                values = (random.nextBoolean()) ? values.insert(makeValue(setValues, random)) : values.insert(makeValue(tokens, random));

            }
        }
        return values;
    }

    //used in debugging
    private void printStats(JImmutableSetMap<String, String> setmap)
    {
        double size = setmap.size();
        double zero = 0;
        double one = 0;
        double OneToTen = 0;
        double TenToTwenty = 0;
        double TwentyToFifty = 0;
        double FiftyToHundred = 0;
        double OverHundred = 0;

        for (String key : setmap.keysCursor()) {
            JImmutableSet<String> set = setmap.get(key);
            assert (set != null);
            if (set.size() == 0) {
                ++zero;
            } else if (set.size() == 1) {
                ++one;
            } else if (set.size() <= 10) {
                ++OneToTen;
            } else if (set.size() <= 20) {
                ++TenToTwenty;
            } else if (set.size() <= 50) {
                ++TwentyToFifty;
            } else if (set.size() <= 100) {
                ++FiftyToHundred;
            } else {
                ++OverHundred;
            }
        }
        zero = zero / size;
        one = one / size;
        OneToTen = OneToTen / size;
        TenToTwenty = TenToTwenty / size;
        TwentyToFifty = TwentyToFifty / size;
        FiftyToHundred = FiftyToHundred / size;
        OverHundred = OverHundred / size;

        System.out.printf("       0: %.2f\n", zero * 100);
        System.out.printf("       1: %.2f\n", one * 100);
        System.out.printf("  2 - 10: %.2f\n", OneToTen * 100);
        System.out.printf(" 11 - 20: %.2f\n", TenToTwenty * 100);
        System.out.printf(" 21 - 50: %.2f\n", TwentyToFifty * 100);
        System.out.printf("51 - 100: %.2f\n", FiftyToHundred * 100);
        System.out.printf("    +101: %.2f\n", OverHundred * 100);
    }
}
