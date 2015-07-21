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
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.btree_list.JImmutableBtreeList;
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
 */
public class JImmutableMapStressTester
        extends AbstractStressTestable
{
    private JImmutableMap<String, String> map;
    private final Class<? extends Map> expectedClass;

    public JImmutableMapStressTester(JImmutableMap<String, String> map,
                                     Class<? extends Map> expectedClass)
    {
        this.map = map;
        this.expectedClass = expectedClass;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        options = options.insert("map").insert(makeClassOption(map));
        return options;
    }

    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Map<String, String> expected = expectedClass.newInstance();
        JImmutableRandomAccessList<String> keys = JImmutableBtreeList.of();
        JImmutableMap<String, String> map = this.map;

        final int size = random.nextInt(100000);
        System.out.printf("JImmutableMapStressTest on %s of size %d%n", getName(map), size);
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(4)) {
                case 0: //assign(K, V)
                    String key = makeValue(tokens, random);
                    keys = insertUnique(key, expected, keys);
                    map = map.assign(key, key);
                    expected.put(key, key);
                    verifyKeysList(keys, expected);
                    break;
                case 1: //insert(Entry<K, V>)
                    key = makeValue(tokens, random);
                    JImmutableMap.Entry<String, String> entry = new MapEntry<String, String>(key, key);
                    keys = insertUnique(key, expected, keys);
                    map = (JImmutableMap<String, String>)map.insert(entry);
                    expected.put(key, key);
                    verifyKeysList(keys, expected);
                    break;
                case 2: //assignAll(JImmutableMap)
                    JImmutableMap<String, String> values = makeInsertValues(tokens, random);
                    keys = insertAllUnique(values, expected, keys);
                    map = map.assignAll(values);
                    expected.putAll(values.getMap());
                    verifyKeysList(keys, expected);
                    break;
                case 3: //assignAll(Map)
                    values = makeInsertValues(tokens, random);
                    keys = insertAllUnique(values, expected, keys);
                    map = map.assignAll(values.getMap());
                    expected.putAll(values.getMap());
                    verifyKeysList(keys, expected);
                    break;
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keys, expected);

            System.out.printf("updating %d%n", map.size());
            for (int i = 0; i < map.size(); ++i) {
                switch (random.nextInt(4)) {
                case 0: //assign(K, V)
                    String key = keys.get(random.nextInt(keys.size()));
                    String value = makeValue(tokens, random);
                    map = map.assign(key, value);
                    expected.put(key, value);
                    break;
                case 1: //insert(Entry<K, V>)
                    key = keys.get(random.nextInt(keys.size()));
                    value = makeValue(tokens, random);
                    JImmutableMap.Entry<String, String> entry = new MapEntry<String, String>(key, value);
                    map = (JImmutableMap<String, String>)map.insert(entry);
                    expected.put(key, value);
                    break;
                case 2: //assignAll(JImmutableMap)
                    JImmutableMap<String, String> values = makeUpdateValues(tokens, random, keys);
                    keys = insertAllUnique(values, expected, keys);
                    map = map.assignAll(values);
                    expected.putAll(values.getMap());
                    break;
                case 3: //assignAll(Map)
                    values = makeUpdateValues(tokens, random, keys);
                    keys = insertAllUnique(values, expected, keys);
                    map = map.assignAll(values.getMap());
                    expected.putAll(values.getMap());
                    break;
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keys, expected);
            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < size / 6; ++i) {
                int keyIndex = random.nextInt(keys.size());
                String key = keys.get(keyIndex);
                expected.remove(key);
                map = map.delete(key);
                keys = keys.delete(keyIndex);
            }
            verifyContents(map, expected);
            verifyKeysList(keys, expected);
            System.out.printf("contains %d%n", map.size());
            for (int i = 0; i < size / 12; ++i) {
                String key = (random.nextBoolean()) ? keys.get(random.nextInt(keys.size())) : makeValue(tokens, random);
                switch (random.nextInt(4)) {
                case 0: //get(K)
                    String value = map.get(key);
                    String expectedValue = (expected.containsKey(key)) ? expected.get(key) : null;
                    if (!((value == null && expectedValue == null) || (expectedValue != null && expectedValue.equals(value)))) {
                        throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedValue, value));
                    }
                    break;
                case 1: //getValueOr(K, V)
                    value = map.getValueOr(key, "");
                    expectedValue = (expected.containsKey(key)) ? expected.get(key) : "";
                    if (!value.equals(expectedValue)) {
                        throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - found %s expected %s%n", key, expectedValue, value));
                    }
                    break;
                case 2: //find(K)
                    Holder<String> holder = map.find(key);
                    Holder<String> expectedHolder = (expected.containsKey(key)) ? Holders.of(expected.get(key)) : Holders.<String>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedHolder, holder));
                    }
                    break;
                case 3: //findEntry(K)
                    Holder<JImmutableMap.Entry<String, String>> entryHolder = map.findEntry(key);
                    Holder<JImmutableMap.Entry<String, String>> expectedEntryHolder = (expected.containsKey(key)) ? Holders.<JImmutableMap.Entry<String, String>>of(new MapEntry<String, String>(key, expected.get(key))) : Holders.<JImmutableMap.Entry<String, String>>of();
                    if (!equivalentEntryHolder(entryHolder, expectedEntryHolder)) {
                        throw new RuntimeException(String.format("findEntry(key) method call failed for %s - expected %s found %s%n", key, entryHolder, entryHolder));
                    }
                    break;
                }
            }
            verifyCursor(map, expected);
        }
        System.out.printf("cleanup %d%n", map.size());
        while (keys.size() > random.nextInt(3)) {
            int keyIndex = random.nextInt(keys.size());
            String key = keys.get(keyIndex);
            expected.remove(key);
            map = map.delete(key);
            keys = keys.delete(keyIndex);
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
        System.out.printf("JImmutableMapStressTest on %s completed without errors%n", getName(map));

    }

    private void verifyContents(final JImmutableMap<String, String> map,
                                final Map<String, String> expected)
    {
        System.out.printf("checking contents with size %d%n", map.size());
        if (map.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), map.isEmpty()));
        }
        if (map.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), map.size()));
        }
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String key = entry.getKey();
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

    private void verifyCursor(JImmutableMap<String, String> map,
                              Map<String, String> expected)
    {
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        List<JImmutableMap.Entry<String, String>> cursorTestEntries = new ArrayList<JImmutableMap.Entry<String, String>>();
        List<JImmutableMap.Entry<String, String>> iteratorTestEntries = new ArrayList<JImmutableMap.Entry<String, String>>();

        if (map instanceof JImmutableHashMap) {
            //list for cursor tests built from iterator
            for (JImmutableMap.Entry<String, String> entry : map) {
                String key = entry.getKey();
                String value = entry.getValue();
                keys.add(key);
                values.add(value);
                iteratorTestEntries.add(new MapEntry<String, String>(key, value));
            }
            //list for iterator tests built from cursor
            for (Cursor<JImmutableMap.Entry<String, String>> c = map.cursor().start(); c.hasValue(); c = c.next()) {
                String key = c.getValue().getKey();
                String value = c.getValue().getValue();
                cursorTestEntries.add(new MapEntry<String, String>(key, value));
            }
        } else {
            keys = asList(expected.keySet());
            values = asList(expected.values());
            for (Map.Entry<String, String> expectedEntry : expected.entrySet()) {
                cursorTestEntries.add(new MapEntry<String, String>(expectedEntry.getKey(), expectedEntry.getValue()));
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

    private void verifyKeysList(JImmutableList<String> keysList,
                                Map<String, String> expected)
    {
        if (keysList.size() != expected.size()) {
            throw new RuntimeException(String.format("keys size mismatch - map: %d, keyList: %d%n", expected.size(), keysList.size()));
        }
    }

    private JImmutableMap<String, String> makeUpdateValues(JImmutableList<String> tokens,
                                                           Random random,
                                                           JImmutableList<String> keys)
    {
        JImmutableMap<String, String> values = JImmutables.map();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            String key = keys.get(random.nextInt(keys.size()));
            String value = makeValue(tokens, random);
            values = values.assign(key, value);
        }
        return values;
    }

    private JImmutableMap<String, String> makeInsertValues(JImmutableList<String> tokens,
                                                           Random random)
    {
        JImmutableMap<String, String> values = JImmutables.map();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            String key = makeValue(tokens, random);
            values = values.assign(key, key);
        }
        return values;
    }

    private JImmutableRandomAccessList<String> insertAllUnique(JImmutableMap<String, String> values,
                                                               Map<String, String> expected,
                                                               JImmutableRandomAccessList<String> list)
    {
        for (String key : values.keysCursor()) {
            list = insertUnique(key, expected, list);
        }
        return list;
    }

    private JImmutableRandomAccessList<String> insertUnique(String key,
                                                            Map<String, String> expected,
                                                            JImmutableRandomAccessList<String> list)
    {
        return (expected.containsKey(key)) ? list : list.insert(key);
    }
}

