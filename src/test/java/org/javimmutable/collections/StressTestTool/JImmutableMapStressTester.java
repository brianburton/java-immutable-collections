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
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.btree_list.JImmutableBtreeList;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Map<String, String> expected = expectedClass.newInstance();

        final int size = random.nextInt(100000);
        JImmutableRandomAccessList<String> keys = JImmutableBtreeList.of();

        System.out.printf("JImmutableMapStressTest on %s of size %d%n", map.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(4)) {
                case 0:
                    String key = makeValue(tokens, random);
                    if (!expected.containsKey(key)) {
                        keys = keys.insert(key);
                    }                    map = map.assign(key, key);
                    expected.put(key, key);
                    verifyKeysList(keys);
                    break;
                case 1:
                    key = makeValue(tokens, random);
                    JImmutableMap.Entry<String, String> entry = new MapEntry<String, String>(key, key);
                    if (!expected.containsKey(key)) {
                        keys = keys.insert(key);
                    }                    map = (JImmutableMap<String, String>)map.insert(entry);
                    expected.put(key, key);
                    verifyKeysList(keys);
                    break;
                case 2:
                    JImmutableMap<String, String> jmapValues = JImmutableHashMap.of();
                    for (int n = 0; n < random.nextInt(3); ++n) {
                        key = makeValue(tokens, random);
                        jmapValues = jmapValues.assign(key, key);
                        if (!expected.containsKey(key)) {
                            keys = keys.insert(key);
                        }
                    }
                    map = map.assignAll(jmapValues);
                    expected.putAll(jmapValues.getMap());
                    verifyKeysList(keys);
                    break;
                case 3:
                    Map<String, String> mapValues = new HashMap<String, String>();
                    for (int n = 0; n < random.nextInt(3); ++n) {
                        key = makeValue(tokens, random);
                        mapValues.put(key, key);
                        if (!expected.containsKey(key)) {
                            keys = keys.insert(key);
                        }                    }
                    map = map.assignAll(mapValues);
                    expected.putAll(mapValues);
                    verifyKeysList(keys);
                    break;
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keys);

            System.out.printf("updating %d%n", map.size());
            for (int i = 0; i < map.size(); ++i) {
                switch (random.nextInt(4)) {
                case 0:
                    String key = keys.get(random.nextInt(keys.size()));
                    String value = (random.nextBoolean()) ? keys.get(random.nextInt(keys.size())) : makeValue(tokens, random);
                    map = map.assign(key, value);
                    expected.put(key, value);
                    break;
                case 1:
                    key = keys.get(random.nextInt(keys.size()));
                    value = keys.get(random.nextInt(keys.size()));
                    JImmutableMap.Entry<String, String> entry = new MapEntry<String, String>(key, value);
                    map = (JImmutableMap<String, String>)map.insert(entry);
                    expected.put(key, value);
                    break;
                case 2:
                    JImmutableMap<String, String> jmapValues = JImmutableHashMap.of();
                    for (int n = 0; n < random.nextInt(3); ++n) {
                        key = keys.get(random.nextInt(keys.size()));
                        value = keys.get(random.nextInt(keys.size()));
                        jmapValues = jmapValues.assign(key, value);
                    }
                    map = map.assignAll(jmapValues);
                    expected.putAll(jmapValues.getMap());
                    break;
                case 3:
                    Map<String, String> mapValues = new HashMap<String, String>();
                    for (int n = 0; n < random.nextInt(3); ++n) {
                        key = keys.get(random.nextInt(keys.size()));
                        value = keys.get(random.nextInt(keys.size()));
                        mapValues.put(key, value);
                    }
                    map = map.assignAll(mapValues);
                    expected.putAll(mapValues);
                    break;
                }
            }
            verifyContents(map, expected);
            verifyKeysList(keys);
            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < size / 6; ++i) {
                int keyIndex = random.nextInt(keys.size());
                String key = keys.get(keyIndex);
                expected.remove(key);
                map = map.delete(key);
                keys = keys.delete(keyIndex);
            }
            verifyContents(map, expected);
            verifyKeysList(keys);
        }
        System.out.printf("cleanup %d%n", map.size());
        while(keys.size() > 1) {
            int keyIndex = random.nextInt(keys.size());
            String key = keys.get(keyIndex);
            expected.remove(key);
            map = map.delete(key);
            keys = keys.delete(keyIndex);
        }
        verifyContents(map, expected);
        map = map.deleteAll();
        expected.clear();
        keys.deleteAll();
        if (map.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", map.size()));
        }
        verifyContents(map, expected);

        System.out.printf("JImmutableMapStressTest on %s completed without errors%n", map.getClass().getSimpleName());

    }

    private void verifyContents(final JImmutableMap<String, String> map,
                                final Map<String, String> expected)
    {
        System.out.printf("checking contents with size %d%n", map.size());
        if (map.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b", expected.isEmpty(), map.isEmpty()));
        }
        if (map.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), map.size()));
        }
        for (JImmutableMap.Entry<String, String> entry : map) {
            String key = entry.getKey();
            String mapValue = map.find(key).getValueOrNull();
            if (!mapValue.equals(entry.getValue())) {
                throw new RuntimeException(String.format("value mismatch for key %s - map.find(key): %s, entry.getValue(): %s%n", key, mapValue, entry.getValue()));
            }
            String expectedValue = expected.get(key);
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
            JImmutableMap.Entry<String, String> foundEntry = map.findEntry(key).getValueOrNull();
            if (!foundEntry.equals(entry)) {
                throw new RuntimeException(String.format("entry mismatch for key %s - cursor: %s, map.findEntry(key): %s", key, entry, foundEntry));
            }
        }
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String mapValue = map.find(entry.getKey()).getValueOrNull();
            String expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
        for (Cursor<String> c = map.keysCursor().start(); c.hasValue(); c = c.next()) {
            String key = c.getValue();
            String value = map.get(key);
            String valueOr = map.getValueOr(key, "");
            if (!valueOr.equals(value)) {
                throw new RuntimeException(String.format("value mismatch for key %s - map.get(key): %s, map.getValueOr(key, \"\"): %s%n", key, value, valueOr));
            }
        }
        if (!expected.equals(map.getMap())) {
            throw new RuntimeException("method call failed - getMap()");
        }
        if(map instanceof JImmutableHashMap) {
            //JImmutableHashMap entries are in a different order than HashMap entries. This verifies the cursors and iterator,
            //not the contents. Therefore, new lists are built for the HashMap that will be in the order used by JImmutableHashMap.
            final List<String> keys = new ArrayList<String>();
            final List<String> values = new ArrayList<String>();
            final List<JImmutableMap.Entry<String, String>> entries = new ArrayList<JImmutableMap.Entry<String, String>>();
            //list for cursor tests built from iterator
            for(JImmutableMap.Entry<String, String> entry : map) {
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
            //list for iterator tests built from cursor
            for(Cursor<String> c = map.keysCursor().start(); c.hasValue(); c = c.next()) {
                String key = c.getValue();
                String value = map.get(key);
                entries.add(new MapEntry<String, String>(key, value));
            }
            if (keys.size() != map.size()) {
                throw new RuntimeException("keys list generated incorrectly");
            }
            if (values.size() != map.size()) {
                throw new RuntimeException("values list generated incorrectly");
            }
            if (entries.size() != map.size()) {
                throw new RuntimeException("entries list generated incorrectly");
            }
            StandardCursorTest.listCursorTest(keys, map.keysCursor());
            StandardCursorTest.listCursorTest(values, map.valuesCursor());
            StandardCursorTest.listCursorTest(entries, map.cursor());
            StandardCursorTest.listIteratorTest(entries, map.iterator());

        }else {
            //JImmutableInsertOrderMap and JImmutableTreeMap are sorted in the same way as LinkedHashMap and TreeMap. Therefore,
            // expected can be used to generate the lists to test their cursors and iterator.
            final List<JImmutableMap.Entry<String, String>> expectedEntries = new ArrayList<JImmutableMap.Entry<String, String>>();
            for (Map.Entry<String, String> expectedEntry : expected.entrySet()) {
                expectedEntries.add(new MapEntry<String, String>(expectedEntry.getKey(), expectedEntry.getValue()));
            }
            if (expectedEntries.size() != map.size()) {
                throw new RuntimeException("expectedEntries list generated incorrectly");
            }
            StandardCursorTest.listCursorTest(asList(expected.keySet()), map.keysCursor());
            StandardCursorTest.listCursorTest(asList(expected.values()), map.valuesCursor());
            StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
            StandardCursorTest.listIteratorTest(expectedEntries, map.iterator());
        }
        map.checkInvariants();
    }

    private List<String> asList(Collection<String> expected)
    {
        List<String> expectedList = new ArrayList<String>();
        expectedList.addAll(expected);
        return expectedList;
    }

    private void verifyKeysList(JImmutableList<String> keysList)
    {
        int keysListSize = keysList.size();
        if (!((keysListSize <= (map.size() + 5)) && (keysListSize >= (map.size())))) {
            throw new RuntimeException(String.format("keys size mismatch - map: %d, keyList: %d%n", map.size(), keysListSize));
        }    }
}
