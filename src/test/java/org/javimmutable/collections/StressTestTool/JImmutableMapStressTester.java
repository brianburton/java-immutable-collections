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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableMap. Divided into five sections:
 * growing (adds new key-value pairs), updating (changes the value in existing pairs),
 * shrinking (removes pairs), contains (tests methods that search for values in the map),
 * and cleanup (empties the map of all key-value pairs).
 * <p/>
 * This tester is used to verify six different versions of JImmutableMap in the StressTestLoop:
 * JImmutableTreeMap, JImmutableInsertOrderMap, a hashmap made from keys that are not Comparable
 * and have a good hash function (JImmutableHashMap), keys that are Comparable with a good hash
 * function (JImmutableComparableHashMap), keys that  not Comparable and have a bad hash function
 * (JImmutableBadHashMap), and keys that are Comparable and have a bad hash function
 * (JImmutableComparableBadHashMap).
 */
public class JImmutableMapStressTester<K extends KeyWrapper<String>>
        extends AbstractStressTestable
{
    private JImmutableMap<K, String> map;
    private final Class<? extends Map> expectedClass;
    KeyFactory<K> factory;

    public JImmutableMapStressTester(JImmutableMap<K, String> map,
                                     Class<? extends Map> expectedClass,
                                     KeyFactory<K> factory)
    {
        this.map = map;
        this.expectedClass = expectedClass;
        this.factory = factory;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("map").insert(makeMapOption(map, factory));
    }

    private String makeMapOption(JImmutableMap<K, String> map,
                                 KeyFactory factory)
    {
        String option = makeClassOption(map);
        if (map instanceof JImmutableHashMap) {
            if (factory instanceof BadHashKeyFactory || factory instanceof ComparableBadHashKeyFactory) {
                option = "bad" + option;
            }
            if (factory instanceof ComparableRegularKeyFactory || factory instanceof ComparableBadHashKeyFactory) {
                option = "comparable" + option;
            }
        }
        return option;
    }

    private String getName(JImmutableMap<K, String> map,
                           KeyFactory factory)
    {
        if (map instanceof JImmutableHashMap) {
            String name = map.getClass().getSimpleName().replace("Empty", "").replaceFirst("JImmutable", "");
            if (factory instanceof BadHashKeyFactory || factory instanceof ComparableBadHashKeyFactory) {
                name = "Bad" + name;
            }
            if (factory instanceof ComparableRegularKeyFactory || factory instanceof ComparableBadHashKeyFactory) {
                name = "Comparable" + name;
            }
            return "JImmutable" + name;
        } else {
            return getName(map);
        }
    }

    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Map<K, String> expected = expectedClass.newInstance();
        List<K> keysList = new ArrayList<K>();
        JImmutableMap<K, String> map = this.map;
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableMapStressTest on %s of size %d%n", getName(map, factory), size);
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(4)) {
                case 0: { //assign(K, V)
                    K key = unusedKey(tokens, random, expected);
                    keysList.add(key);
                    map = map.assign(key, key.getValue());
                    expected.put(key, key.getValue());
                    break;
                }
                case 1: { //insert(Entry<K, V>)
                    K key = unusedKey(tokens, random, expected);
                    JImmutableMap.Entry<K, String> entry = new MapEntry<K, String>(key, key.getValue());
                    keysList.add(key);
                    map = (JImmutableMap<K, String>)map.insert(entry);
                    expected.put(key, key.getValue());
                    break;
                }
                case 2: { //assignAll(JImmutableMap)
                    JImmutableMap<K, String> values = makeInsertValues(tokens, random, expected);
                    keysList.addAll(values.getMap().keySet());
                    map = map.assignAll(values);
                    expected.putAll(values.getMap());
                    break;
                }
                case 3: { //assignAll(Map)
                    JImmutableMap<K, String> values = makeInsertValues(tokens, random, expected);
                    keysList.addAll(values.getMap().keySet());
                    map = map.assignAll(values.getMap());
                    expected.putAll(values.getMap());
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keysList, expected);

            System.out.printf("updating %d%n", map.size());
            for (int i = 0; i < map.size(); ++i) {
                switch (random.nextInt(4)) {
                case 0: { //assign(K, V)
                    K key = keysList.get(random.nextInt(keysList.size()));
                    String value = makeValue(tokens, random);
                    map = map.assign(key, value);
                    expected.put(key, value);
                    break;
                }
                case 1: { //insert(Entry<K, V>)
                    K key = keysList.get(random.nextInt(keysList.size()));
                    String value = makeValue(tokens, random);
                    JImmutableMap.Entry<K, String> entry = new MapEntry<K, String>(key, value);
                    map = (JImmutableMap<K, String>)map.insert(entry);
                    expected.put(key, value);
                    break;
                }
                case 2: { //assignAll(JImmutableMap)
                    JImmutableMap<K, String> values = makeUpdateValues(tokens, random, keysList);
                    map = map.assignAll(values);
                    expected.putAll(values.getMap());
                    break;
                }
                case 3: { //assignAll(Map)
                    JImmutableMap<K, String> values = makeUpdateValues(tokens, random, keysList);
                    map = map.assignAll(values.getMap());
                    expected.putAll(values.getMap());
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keysList, expected);

            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < size / 6 && keysList.size() >= 1; ++i) {
                for (int n = 0; n < 2; ++n) {
                    K key = makeDeleteKey(tokens, random, keysList, expected);
                    expected.remove(key);
                    map = map.delete(key);
                    verifyKeysList(keysList, expected);
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keysList, expected);

            System.out.printf("contains %d%n", map.size());
            for (int i = 0; i < size / 12; ++i) {
                K key = (random.nextBoolean()) ? keysList.get(random.nextInt(keysList.size())) : unusedKey(tokens, random, expected);
                switch (random.nextInt(4)) {
                case 0: { //get(K)
                    String value = map.get(key);
                    String expectedValue = (expected.containsKey(key)) ? expected.get(key) : null;
                    if (!((value == null && expectedValue == null) || (expectedValue != null && expectedValue.equals(value)))) {
                        throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedValue, value));
                    }
                    break;
                }
                case 1: { //getValueOr(K, V)
                    String value = map.getValueOr(key, "");
                    String expectedValue = (expected.containsKey(key)) ? expected.get(key) : "";
                    if (!value.equals(expectedValue)) {
                        throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - found %s expected %s%n", key, expectedValue, value));
                    }
                    break;
                }
                case 2: { //find(K)
                    Holder<String> holder = map.find(key);
                    Holder<String> expectedHolder = (expected.containsKey(key)) ? Holders.of(expected.get(key)) : Holders.<String>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedHolder, holder));
                    }
                    break;
                }
                case 3: { //findEntry(K)
                    Holder<JImmutableMap.Entry<K, String>> holder = map.findEntry(key);
                    Holder<JImmutableMap.Entry<K, String>> expectedHolder = (expected.containsKey(key)) ? Holders.<JImmutableMap.Entry<K, String>>of(new MapEntry<K, String>(key, expected.get(key))) : Holders.<JImmutableMap.Entry<K, String>>of();
                    if (!equivalentEntryHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("findEntry(key) method call failed for %s - expected %s found %s%n", key, holder, holder));
                    }
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(map, expected);
        }
        verifyFinalSize(size, map.size());
        System.out.printf("cleanup %d%n", map.size());
        int threshold = random.nextInt(3);
        while (keysList.size() > threshold) {
            for (int n = 0; n < 2; ++n) {
                K key = makeDeleteKey(tokens, random, keysList, expected);
                expected.remove(key);
                map = map.delete(key);
                verifyKeysList(keysList, expected);
            }
        }
        if (map.size() != 0) {
            verifyContents(map, expected);
            map = map.deleteAll();
            expected.clear();
        }
        if (map.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", map.size()));
        }
        verifyContents(map, expected);
        System.out.printf("JImmutableMapStressTest on %s completed without errors%n", getName(map, factory));
    }

    private void verifyContents(final JImmutableMap<K, String> map,
                                final Map<K, String> expected)
    {
        System.out.printf("checking contents with size %d%n", map.size());
        if (map.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), map.isEmpty()));
        }
        if (map.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), map.size()));
        }
        for (Map.Entry<K, String> entry : expected.entrySet()) {
            K key = entry.getKey();
            String expectedValue = entry.getValue();
            Holder<String> holder = map.find(key);
            if (holder.isEmpty()) {
                throw new RuntimeException(String.format("key mismatch - %s expected but not found%n", key));
            }
            String mapValue = holder.getValue();
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch for %s - expected %s found %s%n", key, expectedValue, mapValue));
            }
        }
        if (!expected.equals(map.getMap())) {
            throw new RuntimeException("method call failed - getMap()\n");
        }

        map.checkInvariants();
    }

    private void verifyCursor(JImmutableMap<K, String> map,
                              Map<K, String> expected)
    {
        System.out.printf("checking cursor with size %d%n", map.size());
        List<K> keys = new ArrayList<K>();
        List<String> values = new ArrayList<String>();
        List<JImmutableMap.Entry<K, String>> cursorTestEntries = new ArrayList<JImmutableMap.Entry<K, String>>();
        List<JImmutableMap.Entry<K, String>> iteratorTestEntries = new ArrayList<JImmutableMap.Entry<K, String>>();

        if (map instanceof JImmutableHashMap) {
            //list for cursor tests built from iterator
            for (JImmutableMap.Entry<K, String> entry : map) {
                K key = entry.getKey();
                String value = entry.getValue();
                keys.add(key);
                values.add(value);
                iteratorTestEntries.add(new MapEntry<K, String>(key, value));
            }
            //list for iterator tests built from cursor
            for (Cursor<JImmutableMap.Entry<K, String>> c = map.cursor().start(); c.hasValue(); c = c.next()) {
                K key = c.getValue().getKey();
                String value = c.getValue().getValue();
                cursorTestEntries.add(new MapEntry<K, String>(key, value));
            }
        } else {
            keys = asList(expected.keySet());
            values = asList(expected.values());
            for (Map.Entry<K, String> expectedEntry : expected.entrySet()) {
                cursorTestEntries.add(new MapEntry<K, String>(expectedEntry.getKey(), expectedEntry.getValue()));
            }
            iteratorTestEntries = cursorTestEntries;
        }
        if (keys.size() != map.size()) {
            throw new RuntimeException("keys list generated incorrectly\n");
        }
        if (values.size() != map.size()) {
            throw new RuntimeException("values list generated incorrectly\n");
        }
        if (cursorTestEntries.size() != map.size()) {
            throw new RuntimeException("cursorTestEntries list generated incorrectly\n");
        }
        if (iteratorTestEntries.size() != map.size()) {
            throw new RuntimeException("iteratorTestEntries list generated incorrectly\n");
        }
        StandardCursorTest.listCursorTest(keys, map.keysCursor());
        StandardCursorTest.listCursorTest(values, map.valuesCursor());
        StandardCursorTest.listCursorTest(cursorTestEntries, map.cursor());
        StandardCursorTest.listIteratorTest(iteratorTestEntries, map.iterator());
    }

    private JImmutableMap<K, String> makeUpdateValues(JImmutableList<String> tokens,
                                                      Random random,
                                                      List<K> keys)
    {
        JImmutableMap<K, String> values = JImmutables.map();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            K key = containedKey(keys, random);
            String value = makeValue(tokens, random);
            values = values.assign(key, value);
        }
        return values;
    }

    private JImmutableMap<K, String> makeInsertValues(JImmutableList<String> tokens,
                                                      Random random,
                                                      Map<K, String> expected)
    {
        JImmutableMap<K, String> values = JImmutables.map();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            K key = unusedKey(tokens, random, expected);
            values = values.assign(key, key.getValue());
        }
        return values;
    }

    protected K unusedKey(JImmutableList<String> tokens,
                          Random random,
                          Map<K, String> expected)
    {
        K key = factory.newKey(tokens, random);
        while (expected.containsKey(key)) {
            key = factory.newKey(tokens, random);
        }
        return key;
    }

    protected K containedKey(List<K> keysList,
                             Random random)
    {
        return (keysList.isEmpty()) ? factory.makeKey("") : keysList.get(random.nextInt(keysList.size()));
    }

    protected K makeDeleteKey(JImmutableList<String> tokens,
                              Random random,
                              List<K> keysList,
                              Map<K, String> expected)
    {
        K key;
        if (random.nextBoolean() || keysList.size() == 0) {
            key = unusedKey(tokens, random, expected);
        } else {
            int index = random.nextInt(keysList.size());
            key = keysList.get(index);
            keysList.remove(index);
        }
        return key;
    }

    protected void verifyKeysList(List<K> keysList,
                                  Map<K, String> expected)
    {
        if (keysList.size() != expected.size()) {
            throw new RuntimeException(String.format("keys size mismatch - map: %d, keyList: %d%n", expected.size(), keysList.size()));
        }
    }
}