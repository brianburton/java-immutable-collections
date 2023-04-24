///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IBuilders;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListBuilder;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IMaps;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ExpectedOrderSorter;
import org.javimmutable.collections.common.StandardMapTests;
import org.javimmutable.collections.hash.HashMap;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.TreeMap;
import org.javimmutable.collections.tree.TreeMapTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.javimmutable.collections.common.StandardSerializableTests.verifySerializable;
import static org.javimmutable.collections.stress_test.KeyFactory.*;

/**
 * Test program for all implementations of JImmutableMap. Divided into five sections:
 * growing (adds new key-value pairs), updating (changes the value in existing pairs),
 * shrinking (removes pairs), contains (tests methods that search for values in the map),
 * and cleanup (empties the map of all key-value pairs).
 * <p>
 * This tester is used to verify six different versions of JImmutableMap in the StressTestLoop:
 * JImmutableTreeMap, JImmutableInsertOrderMap, a hashmap made from keys that are not Comparable
 * and have a good hash function (JImmutableHashMap), keys that are Comparable with a good hash
 * function (JImmutableComparableHashMap), keys that  not Comparable and have a bad hash function
 * (JImmutableBadHashMap), and keys that are Comparable and have a bad hash function
 * (JImmutableComparableBadHashMap).
 */
@SuppressWarnings("Duplicates")
public class MapStressTester<K extends KeyWrapper<String>>
    extends StressTester
{
    private final IMap<K, String> map;
    private final Class<? extends Map> expectedClass;
    private final KeyFactory<K> factory;

    public MapStressTester(IMap<K, String> map,
                           Class<? extends Map> expectedClass,
                           KeyFactory<K> factory)
    {
        super(getName(map));
        this.map = map;
        this.expectedClass = expectedClass;
        this.factory = factory;
    }

    @Override
    public IList<String> getOptions()
    {
        final String option = getNameOption(map);
        final IListBuilder<String> options = IBuilders.list();
        options.add("map");
        options.add(option);
        if (map instanceof HashMap) {
            if (factory instanceof BadHashKeyFactory || factory instanceof ComparableBadHashKeyFactory) {
                options.add("bad-hash");
                options.add("bad" + option);
            }
            if (factory instanceof ComparableRegularKeyFactory || factory instanceof ComparableBadHashKeyFactory) {
                options.add("good-hash");
                options.add("comparable" + option);
            }
        }
        return options.build();
    }

    private String getName(IMap<K, String> map,
                           KeyFactory factory)
    {
        if (map instanceof HashMap) {
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
                        IList<String> tokens)
        throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Map<K, String> expected = expectedClass.newInstance();
        List<K> keysList = new ArrayList<>();
        IMap<K, String> map = this.map;
        final int size = 1 + random.nextInt(100000);
        System.out.printf("MapStressTest on %s of size %d%n", getName(map, factory), size);
        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(6, size, random)) {
            System.out.printf("growing %d%n", map.size());
            while (expected.size() < step.growthSize()) {
                switch (random.nextInt(5)) {
                    case 0: { //assign(K, V)
                        K key = unusedKey(tokens, random, expected);
                        keysList.add(key);
                        map = map.assign(key, key.getValue());
                        expected.put(key, key.getValue());
                        break;
                    }
                    case 1: { //update(K, V)
                        K key = unusedKey(tokens, random, expected);
                        keysList.add(key);
                        map = map.update(key, h -> h.isNone() ? key.getValue() : h.unsafeGet() + "," + key.getValue());
                        expected.put(key, key.getValue());
                        break;
                    }
                    case 2: { //insert(Entry<K, V>)
                        K key = unusedKey(tokens, random, expected);
                        IMapEntry<K, String> entry = new MapEntry<>(key, key.getValue());
                        keysList.add(key);
                        map = map.insert(entry);
                        expected.put(key, key.getValue());
                        break;
                    }
                    case 3: { //assignAll(JImmutableMap)
                        IMap<K, String> values = makeInsertValues(tokens, random, expected);
                        keysList.addAll(values.getMap().keySet());
                        map = map.assignAll(values);
                        expected.putAll(values.getMap());
                        break;
                    }
                    case 4: { //assignAll(Map)
                        IMap<K, String> values = makeInsertValues(tokens, random, expected);
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
                switch (random.nextInt(5)) {
                    case 0: { //assign(K, V)
                        K key = keysList.get(random.nextInt(keysList.size()));
                        String value = RandomKeyManager.makeValue(tokens, random);
                        map = map.assign(key, value);
                        expected.put(key, value);
                        break;
                    }
                    case 1: { //update(K, V)
                        K key = keysList.get(random.nextInt(keysList.size()));
                        String value = RandomKeyManager.makeValue(tokens, random);
                        map = map.update(key, h -> h.isNone() ? value : h.unsafeGet() + "," + value);
                        expected.put(key, expected.get(key) + "," + value);
                        break;
                    }
                    case 2: { //insert(Entry<K, V>)
                        K key = keysList.get(random.nextInt(keysList.size()));
                        String value = RandomKeyManager.makeValue(tokens, random);
                        IMapEntry<K, String> entry = new MapEntry<>(key, value);
                        map = map.insert(entry);
                        expected.put(key, value);
                        break;
                    }
                    case 3: { //assignAll(JImmutableMap)
                        IMap<K, String> values = makeUpdateValues(tokens, random, keysList);
                        map = map.assignAll(values);
                        expected.putAll(values.getMap());
                        break;
                    }
                    case 4: { //assignAll(Map)
                        IMap<K, String> values = makeUpdateValues(tokens, random, keysList);
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
            while (expected.size() > step.shrinkSize()) {
                K key = makeDeleteKey(tokens, random, keysList, expected);
                expected.remove(key);
                map = map.delete(key);
                verifyKeysList(keysList, expected);
            }
            verifyContents(map, expected);
            verifyKeysList(keysList, expected);

            System.out.printf("contains %d%n", map.size());
            for (int i = 0; i < size / 12; ++i) {
                K key = (random.nextBoolean()) ? keysList.get(random.nextInt(keysList.size())) : unusedKey(tokens, random, expected);
                switch (random.nextInt(4)) {
                    case 0: { //get(K)
                        String value = map.get(key);
                        String expectedValue = expected.getOrDefault(key, null);
                        if (!((value == null && expectedValue == null) || (expectedValue != null && expectedValue.equals(value)))) {
                            throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedValue, value));
                        }
                        break;
                    }
                    case 1: { //getValueOr(K, V)
                        String value = map.getValueOr(key, "");
                        String expectedValue = expected.getOrDefault(key, "");
                        if (!value.equals(expectedValue)) {
                            throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - found %s expected %s%n", key, expectedValue, value));
                        }
                        break;
                    }
                    case 2: { //find(K)
                        Holder<String> holder = map.find(key);
                        Holder<String> expectedHolder;
                        if (expected.containsKey(key)) {
                            String value = expected.get(key);
                            expectedHolder = Holders.nullable(value);
                        } else {
                            expectedHolder = Holder.none();
                        }
                        if (!equivalentHolder(holder, expectedHolder)) {
                            throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedHolder, holder));
                        }
                        break;
                    }
                    case 3: { //findEntry(K)
                        Holder<IMapEntry<K, String>> holder = map.findEntry(key);
                        Holder<IMapEntry<K, String>> expectedHolder;
                        if (expected.containsKey(key)) {
                            IMapEntry<K, String> value = new MapEntry<>(key, expected.get(key));
                            expectedHolder = Holders.nullable(value);
                        } else {
                            expectedHolder = Holder.none();
                        }
                        if (!equivalentEntryHolder(holder, expectedHolder)) {
                            throw new RuntimeException(String.format("findEntry(key) method call failed for %s - expected %s found %s%n", key, holder, holder));
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyIteration(map, expected);
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
        System.out.printf("MapStressTest on %s completed without errors%n", getName(map, factory));
    }

    private void verifyContents(final IMap<K, String> map,
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
            if (holder.isNone()) {
                throw new RuntimeException(String.format("key mismatch - %s expected but not found%n", key));
            }
            String mapValue = holder.unsafeGet();
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch for %s - expected %s found %s%n", key, expectedValue, mapValue));
            }
        }
        if (!expected.equals(map.getMap())) {
            throw new RuntimeException("method call failed - getMap()\n");
        }

        System.out.printf("checking invariants with size %d%n", map.size());
        map.checkInvariants();
        System.out.printf("checking serializable with size %d%n", map.size());
        verifySerializable(this::extraSerializationChecks, map, IMap.class);
        System.out.printf("done checking contents with size %d%n", map.size());
    }

    private void verifyIteration(IMap<K, String> map,
                                 Map<K, String> expected)
    {
        System.out.printf("checking cursor with size %d%n", map.size());

        List<IMapEntry<K, String>> entries = makeEntriesList(expected);
        if (map instanceof HashMap) {
            final ExpectedOrderSorter<K> ordering = new ExpectedOrderSorter<>(map.keys().iterator());
            entries = ordering.sort(entries, e -> e.getKey());
        }
        List<K> keys = extractKeys(entries);
        List<String> values = extractValues(entries);

        if (keys.size() != map.size()) {
            throw new RuntimeException("keys list generated incorrectly\n");
        }
        if (values.size() != map.size()) {
            throw new RuntimeException("values list generated incorrectly\n");
        }
        if (entries.size() != map.size()) {
            throw new RuntimeException("entries list generated incorrectly\n");
        }
        StandardIteratorTests.listIteratorTest(keys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(values, map.values().iterator());
        StandardIteratorTests.listIteratorTest(entries, map.iterator());
        StandardMapTests.verifyEnumeration(entries, map);
    }

    private IMap<K, String> makeUpdateValues(IList<String> tokens,
                                             Random random,
                                             List<K> keys)
    {
        IMap<K, String> values = IMaps.hashed();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            K key = containedKey(keys, random);
            String value = RandomKeyManager.makeValue(tokens, random);
            values = values.assign(key, value);
        }
        return values;
    }

    private IMap<K, String> makeInsertValues(IList<String> tokens,
                                             Random random,
                                             Map<K, String> expected)
    {
        IMap<K, String> values = IMaps.hashed();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            K key = unusedKey(tokens, random, expected);
            values = values.assign(key, key.getValue());
        }
        return values;
    }

    protected K unusedKey(IList<String> tokens,
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

    protected K makeDeleteKey(IList<String> tokens,
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

    private void extraSerializationChecks(Object a,
                                          Object b)
    {
        if (a instanceof TreeMap) {
            TreeMapTest.extraSerializationChecks(a, b);
        }
    }
}
