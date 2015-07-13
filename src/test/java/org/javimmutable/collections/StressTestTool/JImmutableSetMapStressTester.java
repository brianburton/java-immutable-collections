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

import org.javimmutable.collections.*;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.setmap.JImmutableHashSetMap;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableSetMap. Divided into five sections:
 * growing (adds new key-set pairs), updating (modifies the sets in the setmap without
 * changing the keys), shrinking (removes key-set pairs), contains (tests methods that check
 * for keys or values in a set), and cleanup (empties the setmap of all key-set pairs).
 */
public class JImmutableSetMapStressTester
        extends AbstractStressTestable
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
        options = options.insert("smap").insert("setmap").insert(makeClassOption(setmap));
        return options;
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        JImmutableSetMap<String, String> setmap = this.setmap;
        @SuppressWarnings("unchecked") Map<String, JImmutableSet<String>> expected = expectedClass.newInstance();
        JImmutableRandomAccessList<String> keysList = JImmutables.ralist();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableSetMapStressTest on %s of size %d%n", setmap.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", setmap.size());
            for (int i = 0; i < size / 3; ++i) {
                String key = makeValue(tokens, random);
                keysList = (expected.containsKey(key)) ? keysList : keysList.insert(key);
                switch (random.nextInt(11)) {
                case 0: //assign(K, JSet)
                    JImmutableSet<String> valuesSet = makeInsertSet(tokens, random);
                    setmap = setmap.assign(key, valuesSet);
                    expected.put(key, valuesSet);
                    break;
                case 1: //insert(K, V)
                    String value = makeValue(tokens, random);
                    setmap = setmap.insert(key, value);
                    addAt(expected, key, value);
                    break;
                case 2: //insert(Entry<K, V>)
                    value = makeValue(tokens, random);
                    MapEntry<String, String> entry = new MapEntry<String, String>(key, value);
                    setmap = (JImmutableSetMap<String, String>)setmap.insert(entry);
                    addAt(expected, key, value);
                    break;
                case 3: //insertAll(K, Cusorable)
                    JImmutableList<String> values = makeInsertList(tokens, random);
                    setmap = setmap.insertAll(key, values);
                    addAllToSetAt(expected, key, values);
                    break;
                case 4: //insertAll(K, Collection)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.insertAll(key, values.getList());
                    addAllToSetAt(expected, key, values);
                    break;
                case 5: //union(K, Cursorable)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.union(key, values);
                    unionOnSetAt(expected, key, values);
                    break;
                case 6: //union(K, Collection)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.union(key, values.getList());
                    unionOnSetAt(expected, key, values);
                    break;
                case 7: //intersection(K, Cursorable)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.intersection(key, values);
                    intersectionOnSetAt(expected, key, values);
                    break;
                case 8: //intersection(K, Collection)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.intersection(key, values.getList());
                    intersectionOnSetAt(expected, key, values);
                    break;
                case 9: //intersection(K, JSet)
                    valuesSet = makeInsertSet(tokens, random);
                    setmap = setmap.intersection(key, valuesSet);
                    intersectionOnSetAt(expected, key, valuesSet);
                    break;
                case 10: //intersection(K, Set)
                    valuesSet = makeInsertSet(tokens, random);
                    setmap = setmap.intersection(key, valuesSet.getSet());
                    intersectionOnSetAt(expected, key, valuesSet);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("updating %d%n", setmap.size());
            for (int i = 0; i < setmap.size(); ++i) {
                int index = random.nextInt(keysList.size());
                String key = keysList.get(index);
                switch (random.nextInt(17)) {
                case 0: //assign(K, JSet)
                    JImmutableSet<String> valuesSet = makeInsertSet(tokens, random);
                    setmap = setmap.assign(key, valuesSet);
                    expected.put(key, valuesSet);
                    break;
                case 1: //insert(K, V)
                    String value = makeValue(tokens, random);
                    setmap = setmap.insert(key, value);
                    addAt(expected, key, value);
                    break;
                case 2: //insert(Entry<K, V>)
                    value = makeValue(tokens, random);
                    MapEntry<String, String> entry = new MapEntry<String, String>(key, value);
                    setmap = (JImmutableSetMap<String, String>)setmap.insert(entry);
                    addAt(expected, key, value);
                    break;
                case 3: //insertAll(K, Cursorable)
                    JImmutableList<String> values = makeInsertList(tokens, random);
                    setmap = setmap.insertAll(key, values);
                    addAllToSetAt(expected, key, values);
                    break;
                case 4: //insertAll(K, Collection)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.insertAll(key, values.getList());
                    addAllToSetAt(expected, key, values);
                    break;
                case 5: //union(K, Cursorable)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.union(key, values);
                    unionOnSetAt(expected, key, values);
                    break;
                case 6: //union(K, Collection)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.union(key, values.getList());
                    unionOnSetAt(expected, key, values);
                    break;
                case 7: //intersection(K, Cursorable)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.intersection(key, values);
                    intersectionOnSetAt(expected, key, values);
                    break;
                case 8: //intersection(K, Collection)
                    values = makeInsertList(tokens, random);
                    setmap = setmap.intersection(key, values.getList());
                    intersectionOnSetAt(expected, key, values);
                    break;
                case 9: //intersection(K, JSet)
                    valuesSet = makeInsertSet(tokens, random);
                    setmap = setmap.intersection(key, valuesSet);
                    intersectionOnSetAt(expected, key, valuesSet);
                    break;
                case 10: //intersection(K, Set)
                    valuesSet = makeInsertSet(tokens, random);
                    setmap = setmap.intersection(key, valuesSet.getSet());
                    intersectionOnSetAt(expected, key, valuesSet);
                    break;
                case 11:
                case 12: //deleteAll(K, Cursorable)
                    values = makeDeleteList(key, expected, random);
                    setmap = setmap.deleteAll(key, values);
                    removeAllAt(expected, key, values);
                    break;
                case 13:
                case 14: //deleteAll(K, Collection)
                    values = makeDeleteList(key, expected, random);
                    setmap = setmap.deleteAll(key, values.getList());
                    removeAllAt(expected, key, values);
                    break;
                case 15:
                case 16: //delete(K, V)
                    value = keysList.get(random.nextInt(keysList.size()));
                    setmap = setmap.delete(key, value);
                    removeAt(expected, key, value);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("shrinking %d%n", setmap.size());
            for (int i = 0; i < size / 6; ++i) {
                //delete(K)
                int index = random.nextInt(keysList.size());
                String key = keysList.get(index);
                setmap = setmap.delete(key);
                expected.remove(key);
                keysList = keysList.delete(index);
            }
            verifyContents(setmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("contains %d%n", setmap.size());
            for (int i = 0; i < size / 12; ++i) {
                String key = (random.nextBoolean()) ? makeValue(tokens, random) : keysList.get(random.nextInt(keysList.size()));
                switch (random.nextInt(8)) {
                case 0: //contains(K, V)
                    String value = (random.nextBoolean()) ? makeValue(tokens, random) : valueInSet(key, expected, random);
                    if (setmap.contains(key, value) != (expected.containsKey(key) && expected.get(key).contains(value))) {
                        throw new RuntimeException(String.format("contains(key, value) method call failed for %s, %s - expected %b found %b%n", key, value, setmap.contains(key, value), (expected.containsKey(key) && expected.get(key).contains(value))));
                    }
                    break;
                case 1: //containsAll(K, Cursorable)
                    JImmutableList<String> values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAll(key, values) != (expected.containsKey(key) && expected.get(key).containsAll(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAll(key, values), (expected.containsKey(key) && expected.get(key).containsAll(values))));
                    }
                    break;
                case 2: //containsAll(K, Collection)
                    values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAll(key, values.getList()) != (expected.containsKey(key) && expected.get(key).containsAll(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAll(key, values.getList()), (expected.containsKey(key) && expected.get(key).containsAll(values))));
                    }
                    break;
                case 3: //containsAny(K, Cursorable)
                    values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAny(key, values) != (expected.containsKey(key) && expected.get(key).containsAny(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAny(key, values), (expected.containsKey(key) && expected.get(key).containsAny(values))));
                    }
                    break;
                case 4: //containsAny(K, Collection)
                    values = makeContainsList(key, expected, random, tokens);
                    if (setmap.containsAny(key, values.getList()) != (expected.containsKey(key) && expected.get(key).containsAny(values))) {
                        throw new RuntimeException(String.format("containsAll(key, Cursorable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAny(key, values.getList()), (expected.containsKey(key) && expected.get(key).containsAny(values))));
                    }
                    break;
                case 5: //get(K)
                    JImmutableSet<String> set = setmap.get(key);
                    JImmutableSet<String> expectedSet = (expected.containsKey(key)) ? expected.get(key) : null;
                    if (!((set == null && expectedSet == null) || (expectedSet != null && expectedSet.equals(set)))) {
                        throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedSet, set));
                    }
                    break;
                case 6: //getValueOr(K, V)
                    set = setmap.getValueOr(key, JImmutables.set(""));
                    expectedSet = (expected.containsKey(key)) ? expected.get(key) : JImmutables.set("");
                    if (!set.equals(expectedSet)) {
                        throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - expected %s found %s%n", key, expectedSet, set));
                    }
                    break;
                case 7: //find(K)
                    Holder<JImmutableSet<String>> holder = setmap.find(key);
                    Holder<JImmutableSet<String>> expectedHolder = (expected.containsKey(key)) ? Holders.of(expected.get(key)) : Holders.<JImmutableSet<String>>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedHolder, holder));
                    }
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(setmap, expected);
        }

        System.out.printf("cleanup %d%n", setmap.size());
        while (setmap.size() > random.nextInt(20)) {
            //delete(K)
            int index = random.nextInt(keysList.size());
            String key = keysList.get(index);
            setmap = setmap.delete(key);
            expected.remove(key);
            keysList = keysList.delete(index);
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
        System.out.printf("JImmutableSetMapStressTest on %s completed without errors%n", setmap.getClass().getSimpleName());
    }

    private void verifyKeysList(JImmutableRandomAccessList<String> keysList,
                                Map<String, JImmutableSet<String>> expected)
    {
        int keysListSize = keysList.size();
        if (keysListSize != expected.size()) {
            throw new RuntimeException(String.format("keysList size mismatch - expected: %d, keysList: %d", expected.size(), keysListSize));
        }
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
            JImmutableSet<String> expectedValue = entry.getValue();
            JImmutableSet<String> value = setmap.getSet(key);
            if (!expectedValue.equals(value)) {
                throw new RuntimeException(String.format("values mismatch for key %s - expected set %s found jet %s%n", key, expectedValue, value));
            }
        }
        setmap.checkInvariants();
    }

    private void verifyCursor(final JImmutableSetMap<String, String> setmap,
                              final Map<String, JImmutableSet<String>> expected)
    {
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

    private JImmutableSet<String> makeInsertSet(JImmutableList<String> tokens,
                                                Random random)
    {
        JImmutableSet<String> set = JImmutables.set();
        for (int i = 0; i < random.nextInt(3); ++i) {
            set = set.insert(makeValue(tokens, random));
        }
        return set;
    }

    private JImmutableList<String> makeDeleteList(String key,
                                                  Map<String, JImmutableSet<String>> expected,
                                                  Random random)
    {
        JImmutableList<String> list = JImmutables.list();
        JImmutableList<String> setInMap = JImmutables.ralist(expected.get(key)).insert("");
        for (int i = 0; i < random.nextInt(3); ++i) {
            list = list.insert(setInMap.get(random.nextInt(setInMap.size())));
        }
        return list;
    }


    private void addAllToSetAt(Map<String, JImmutableSet<String>> expected,
                               String key,
                               Cursorable<String> values)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.insertAll(values));
    }

    private void unionOnSetAt(Map<String, JImmutableSet<String>> expected,
                              String key,
                              Cursorable<String> values)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.union(values));
    }

    private void intersectionOnSetAt(Map<String, JImmutableSet<String>> expected,
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
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.deleteAll(values));
    }

    private void removeAt(Map<String, JImmutableSet<String>> expected,
                          String key,
                          String value)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        expected.put(key, set.delete(value));
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
            for (int n = 0; n < random.nextInt(10); ++n) {
                values = values.insert(makeValue(tokens, random));
            }
        } else {
            JImmutableList<String> setValues = JImmutables.list(expected.get(key));
            for (int n = 0; n < random.nextInt(10); ++n) {
                values = (random.nextBoolean()) ? values.insert(makeValue(setValues, random)) : values.insert(makeValue(tokens, random));

            }
        }
        return values;
    }

    private void addAt(Map<String, JImmutableSet<String>> expected,
                       String key,
                       String value)
    {
        JImmutableSet<String> set = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>set();
        set = set.insert(value);
        expected.put(key, set);
    }
}
