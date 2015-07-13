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
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.listmap.JImmutableHashListMap;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableListMap. Divided into five sections:
 * growing (adds new key-list pairs), updating (modifies the lists in the listmap without
 * changing the keys), shrinking (removes key-list pairts), contains (tests methods that
 * find lists contained in the listmap), and cleanup (empties the listmap of all key-list
 * pairs).
 */
public class JImmutableListMapStressTester
        extends AbstractMapVariantStressTestable
{
    private final JImmutableListMap<String, String> listmap;
    private final Class<? extends Map> expectedClass;

    public JImmutableListMapStressTester(JImmutableListMap<String, String> listmap,
                                         Class<? extends Map> expectedClass)
    {
        this.listmap = listmap;
        this.expectedClass = expectedClass;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        options = options.insert("lmap").insert("listmap").insert(makeClassOption(listmap));
        return options;
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        JImmutableListMap<String, String> listmap = this.listmap;
        @SuppressWarnings("unchecked") Map<String, JImmutableList<String>> expected = expectedClass.newInstance();
        JImmutableRandomAccessList<String> keysList = JImmutables.ralist();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableListMapStressTest on %s of size %d%n", listmap.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", listmap.size());
            for (int i = 0; i < size / 3; ++i) {
                String key = makeValue(tokens, random);
                keysList = (expected.containsKey(key)) ? keysList : keysList.insert(key);
                switch (random.nextInt(3)) {
                case 0: //assign(K, JList)
                    JImmutableList<String> values = makeInsertList(tokens, random);
                    listmap = listmap.assign(key, values);
                    expected.put(key, values);
                    break;
                case 1: //insert(K, V)
                    String value = makeValue(tokens, random);
                    listmap = listmap.insert(key, value);
                    addAt(expected, key, value);
                    break;
                case 2: //insert(Entry<K, V>)
                    value = makeValue(tokens, random);
                    JImmutableMap.Entry<String, String> entry = new MapEntry<String, String>(key, value);
                    listmap = (JImmutableListMap<String, String>)listmap.insert(entry);
                    addAt(expected, key, value);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(listmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("updating %d%n", listmap.size());
            for (int i = 0; i < listmap.size(); ++i) {
                String key = keysList.get(random.nextInt(keysList.size()));
                switch (random.nextInt(3)) {
                case 0: //assign(K, JList)
                    JImmutableList<String> values = makeInsertList(tokens, random);
                    listmap = listmap.assign(key, values);
                    expected.put(key, values);
                    break;
                case 1: //insert(K, V)
                    String value = makeValue(tokens, random);
                    listmap = listmap.insert(key, value);
                    addAt(expected, key, value);
                    break;
                case 2: //insert(Entry<K, V>)
                    value = makeValue(tokens, random);
                    JImmutableMap.Entry<String, String> entry = new MapEntry<String, String>(key, value);
                    listmap = (JImmutableListMap<String, String>)listmap.insert(entry);
                    addAt(expected, key, value);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(listmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("shrinking %d%n", listmap.size());
            for (int i = 0; i < size / 6; ++i) {
                //delete(K)
                int index = random.nextInt(keysList.size());
                String key = keysList.get(index);
                listmap = listmap.delete(key);
                expected.remove(key);
                keysList = keysList.delete(index);
            }

            verifyContents(listmap, expected);
            verifyKeysList(keysList, expected);
            System.out.printf("contains %d%n", listmap.size());
            for (int i = 0; i < size / 12; ++i) {
                String key = (random.nextBoolean()) ? makeValue(tokens, random) : keysList.get(random.nextInt(keysList.size()));
                switch (random.nextInt(3)) {
                case 0: //get(K)
                    JImmutableList<String> list = listmap.get(key);
                    JImmutableList<String> expectedList = expected.get(key);
                    if (!((list == null && expectedList == null) || (expectedList != null && expectedList.equals(list)))) {
                        throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedList, list));
                    }
                    break;
                case 1: //getValueOr(K, V)
                    list = listmap.getValueOr(key, JImmutables.list(""));
                    expectedList = (expected.containsKey(key)) ? expected.get(key) : JImmutables.list("");
                    if (!list.equals(expectedList)) {
                        throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - expected %s found %s%n", key, expectedList, list));
                    }
                    break;
                case 2: //find(K)
                    Holder<JImmutableList<String>> holder = listmap.find(key);
                    Holder<JImmutableList<String>> expectedHolder = (expected.containsKey(key)) ? Holders.of(expected.get(key)) : Holders.<JImmutableList<String>>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedHolder, holder));
                    }
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(listmap, expected);
        }

        System.out.printf("cleanup %d%n", listmap.size());
        while (listmap.size() > random.nextInt(20)) {
            //delete(K)
            int index = random.nextInt(keysList.size());
            String key = keysList.get(index);
            listmap = listmap.delete(key);
            expected.remove(key);
            keysList = keysList.delete(index);
        }
        if (listmap.size() != 0) {
            verifyContents(listmap, expected);
            listmap = listmap.deleteAll();
            expected.clear();
        }
        if (listmap.size() != 0) {
            throw new RuntimeException(String.format("expected listmap to be empty but it contained %d keys%n", listmap.size()));
        }
        verifyContents(listmap, expected);
        System.out.printf("JImmutableListMapStressTest on %s completed without errors%n", listmap.getClass().getSimpleName());
    }

    private void verifyContents(final JImmutableListMap<String, String> listmap,
                                Map<String, JImmutableList<String>> expected)
    {
        System.out.printf("checking contents with size %s%n", listmap.size());
        if (listmap.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), listmap.isEmpty()));
        }
        if (listmap.size() != expected.size()) {
            throw new RuntimeException(String.format("key size mismatch - expected %d found %d%n", expected.size(), listmap.size()));
        }
        for (Map.Entry<String, JImmutableList<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            JImmutableList<String> expectedValue = entry.getValue();
            JImmutableList<String> value = listmap.getList(key);
            if (!expectedValue.equals(value)) {
                throw new RuntimeException(String.format("values mismatch for key %s - expected set %s found jlist %s%n", key, expectedValue, value));
            }
        }
        listmap.checkInvariants();
    }

    private void verifyCursor(final JImmutableListMap<String, String> listmap,
                              Map<String, JImmutableList<String>> expected)

    {
        List<String> keys = new ArrayList<String>();
        List<JImmutableMap.Entry<String, JImmutableList<String>>> entriesForCursor = new ArrayList<JImmutableMap.Entry<String, JImmutableList<String>>>();
        List<JImmutableMap.Entry<String, JImmutableList<String>>> entriesForIterator = new ArrayList<JImmutableMap.Entry<String, JImmutableList<String>>>();

        if (listmap instanceof JImmutableHashListMap) {
            for (JImmutableMap.Entry<String, JImmutableList<String>> entry : listmap) {
                keys.add(entry.getKey());
                entriesForCursor.add(new MapEntry<String, JImmutableList<String>>(entry.getKey(), entry.getValue()));
            }
            for (Cursor<JImmutableMap.Entry<String, JImmutableList<String>>> c = listmap.cursor().start(); c.hasValue(); c = c.next()) {
                entriesForIterator.add(new MapEntry<String, JImmutableList<String>>(c.getValue().getKey(), c.getValue().getValue()));
            }
        } else {
            keys.addAll(expected.keySet());
            for (Map.Entry<String, JImmutableList<String>> entry : expected.entrySet()) {
                entriesForCursor.add(new MapEntry<String, JImmutableList<String>>(entry.getKey(), entry.getValue()));
            }
            entriesForIterator = entriesForCursor;
        }

        StandardCursorTest.listCursorTest(keys, listmap.keysCursor());
        StandardCursorTest.listCursorTest(entriesForCursor, listmap.cursor());
        StandardCursorTest.listIteratorTest(entriesForIterator, listmap.iterator());

        for (Map.Entry<String, JImmutableList<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            List<String> values = asList(entry.getValue());
            StandardCursorTest.listCursorTest(values, listmap.valuesCursor(key));
        }
    }

    private List<String> asList(Iterable<String> values)
    {
        List<String> list = new ArrayList<String>();
        for (String value : values) {
            list.add(value);
        }
        return list;
    }

    private void verifyKeysList(JImmutableRandomAccessList<String> keysList,
                                Map<String, JImmutableList<String>> expected)
    {
        int keysListSize = keysList.size();
        if (keysListSize != expected.size()) {
            throw new RuntimeException(String.format("keysList size mismatch - expected: %d, keysList: %d", expected.size(), keysListSize));
        }
    }

    private void addAt(Map<String, JImmutableList<String>> expected,
                       String key,
                       String value)
    {
        JImmutableList<String> list = (expected.containsKey(key)) ? expected.get(key) : JImmutables.<String>list();
        list = list.insert(value);
        expected.put(key, list);
    }
}
