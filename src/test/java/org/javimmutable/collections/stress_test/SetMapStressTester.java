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

import static org.javimmutable.collections.common.StandardSerializableTests.verifySerializable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.ILists;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.ISetMap;
import org.javimmutable.collections.ISets;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ExpectedOrderSorter;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.setmap.HashSetMap;
import org.javimmutable.collections.setmap.TreeSetMap;
import org.javimmutable.collections.setmap.TreeSetMapTest;

/**
 * Test program for all implementations of JImmutableSetMap. Divided into five sections:
 * growing (adds new key-set pairs), updating (adds and removes values in the sets of the
 * setmap without changing the keys), shrinking (removes key-set pairs), contains (tests
 * methods that check for keys or values in a set), and cleanup (empties the setmap of
 * all key-set pairs).
 * <p>
 * This tester was designed so that the setmap produced would contain sets of a large
 * variety of sizes. On average, 25% of the sets in the setmap will be empty by the end
 * of the test. 18% will contain only one value. 52% will contain between two and ten
 * values, and the remaining 5% will contain between eleven and over a hundred values.
 */
@SuppressWarnings("Duplicates")
public class SetMapStressTester
    extends AbstractMapStressTestable
{
    private final ISetMap<String, String> setmap;
    private final Class<? extends Map> expectedClass;

    public SetMapStressTester(ISetMap<String, String> setmap,
                              Class<? extends Map> expectedClass)
    {
        super(getName(setmap));
        this.setmap = setmap;
        this.expectedClass = expectedClass;
    }

    @Override
    public IList<String> getOptions()
    {
        return ILists.of("smap", "setmap", getNameOption(setmap));
    }

    @Override
    public void execute(Random random,
                        IList<String> tokens)
        throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Map<String, ISet<String>> expected = expectedClass.newInstance();
        final RandomKeyManager keys = new RandomKeyManager(random, tokens);
        ISetMap<String, String> setmap = this.setmap;
        final int size = 1 + random.nextInt(100000);
        System.out.printf("SetMapStressTest on %s of size %d%n", getName(setmap), size);

        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(6, size, random)) {
            System.out.printf("growing keys %d%n", setmap.size());
            while (expected.size() < step.growthSize()) {
                String key = keys.randomUnallocatedKey();
                keys.allocate(key);
                switch (random.nextInt(11)) {
                    case 0: { //assign(K, JSet)
                        ISet<String> values = makeGrowingSet(tokens, random);
                        setmap = setmap.assign(key, values);
                        expected.put(key, values);
                        break;
                    }
                    case 1: { //insert(K, V)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        setmap = setmap.insert(key, value);
                        addAt(expected, key, value);
                        break;
                    }
                    case 2: { //insert(Entry<K, V>)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        MapEntry<String, String> entry = new MapEntry<>(key, value);
                        setmap = setmap.insert(entry);
                        addAt(expected, key, value);
                        break;
                    }
                    case 3: { //insertAll(K, Iterable)
                        IList<String> values = makeGrowingList(tokens, random);
                        setmap = setmap.insertAll(key, values);
                        addAllAt(expected, key, values);
                        break;
                    }
                    case 4: { //insertAll(K, Collection)
                        IList<String> values = makeGrowingList(tokens, random);
                        setmap = setmap.insertAll(key, values.getList());
                        addAllAt(expected, key, values);
                        break;
                    }
                    case 5: { //union(K, Iterable)
                        IList<String> values = makeGrowingList(tokens, random);
                        setmap = setmap.union(key, values);
                        unionAt(expected, key, values);
                        break;
                    }
                    case 6: { //union(K, Collection)
                        IList<String> values = makeGrowingList(tokens, random);
                        setmap = setmap.union(key, values.getList());
                        unionAt(expected, key, values);
                        break;
                    }
                    case 7: { //intersection(K, Iterable)
                        IList<String> values = makeIntersectList(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values);
                        expected.put(key, ISets.hashed());
                        break;
                    }
                    case 8: { //intersection(K, Collection)
                        IList<String> values = makeIntersectList(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values.getList());
                        expected.put(key, ISets.hashed());
                        break;
                    }
                    case 9: { //intersection(K, JSet)
                        ISet<String> values = makeIntersectSet(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values);
                        expected.put(key, ISets.hashed());
                        break;
                    }
                    case 10: { //intersection(K, Set)
                        ISet<String> values = makeIntersectSet(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values.getSet());
                        expected.put(key, ISets.hashed());
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(setmap, expected);
            verifyKeys(keys, expected);
            System.out.printf("updating %d%n", setmap.size());
            System.out.printf("  growing sets%n");
            for (int i = 0; i < setmap.size(); ++i) {
                String key = keys.randomAllocatedKey();
                switch (random.nextInt(7)) {
                    case 0: { //assign(K, JSet)
                        ISet<String> valuesSet = makeUpdateSet(tokens, random, key, expected);
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
                        MapEntry<String, String> entry = new MapEntry<>(key, value);
                        setmap = setmap.insert(entry);
                        addAt(expected, key, value);
                        break;
                    }
                    case 3: { //insertAll(K, Iterable)
                        IList<String> values = makeUpdateList(tokens, random, key, expected);
                        setmap = setmap.insertAll(key, values);
                        addAllAt(expected, key, values);
                        break;
                    }
                    case 4: { //insertAll(K, Collection)
                        IList<String> values = makeUpdateList(tokens, random, key, expected);
                        setmap = setmap.insertAll(key, values.getList());
                        addAllAt(expected, key, values);
                        break;
                    }
                    case 5: { //union(K, Iterable)
                        IList<String> values = makeUpdateList(tokens, random, key, expected);
                        setmap = setmap.union(key, values);
                        unionAt(expected, key, values);
                        break;
                    }
                    case 6: { //union(K, Collection)
                        IList<String> values = makeUpdateList(tokens, random, key, expected);
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
            verifyKeys(keys, expected);
            System.out.printf("  shrinking sets%n");
            for (int i = 0; i < setmap.size(); ++i) {
                switch (random.nextInt(7)) {
                    case 0: { //deleteAll(K, Iterable)
                        String key = keys.randomKey();
                        IList<String> values = makeDeleteList(tokens, random, key, expected);
                        setmap = setmap.deleteAll(key, values);
                        removeAllAt(expected, key, values);
                        break;
                    }
                    case 1: { //deleteAll(K, Collection)
                        String key = keys.randomKey();
                        IList<String> values = makeDeleteList(tokens, random, key, expected);
                        setmap = setmap.deleteAll(key, values.getList());
                        removeAllAt(expected, key, values);
                        break;
                    }
                    case 2: { //delete(K, V)
                        String key = keys.randomKey();
                        String value = makeDeleteValue(tokens, random, key, expected);
                        setmap = setmap.delete(key, value);
                        removeAt(expected, key, value);
                        break;
                    }
                    case 3: { //intersection(K, Iterable)
                        String key = keys.randomAllocatedKey();
                        IList<String> values = makeIntersectList(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values);
                        intersectionAt(expected, key, values);
                        break;
                    }
                    case 4: { //intersection(K, Collection)
                        String key = keys.randomAllocatedKey();
                        IList<String> values = makeIntersectList(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values.getList());
                        intersectionAt(expected, key, values);
                        break;
                    }
                    case 5: { //intersection(K, JSet)
                        String key = keys.randomAllocatedKey();
                        ISet<String> values = makeIntersectSet(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values);
                        intersectionAt(expected, key, values);
                        break;
                    }
                    case 6: { //intersection(K, Set)
                        String key = keys.randomAllocatedKey();
                        ISet<String> values = makeIntersectSet(tokens, random, key, expected);
                        setmap = setmap.intersection(key, values.getSet());
                        intersectionAt(expected, key, values);
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(setmap, expected);
            verifyKeys(keys, expected);

            System.out.printf("shrinking keys %d%n", setmap.size());
            while (expected.size() > step.shrinkSize()) {
                //delete(K)
                String key = keys.randomKey();
                setmap = setmap.delete(key);
                expected.remove(key);
                keys.unallocate(key);
            }
            verifyContents(setmap, expected);
            verifyKeys(keys, expected);
            System.out.printf("contains %d%n", setmap.size());
            for (int i = 0; i < size / 12; ++i) {
                String key = keys.randomKey();
                switch (random.nextInt(8)) {
                    case 0: { //contains(K, V)
                        String value = makeDeleteValue(tokens, random, key, expected);
                        if (setmap.contains(key, value) != (expected.containsKey(key) && expected.get(key).contains(value))) {
                            throw new RuntimeException(String.format("contains(key, value) method call failed for %s, %s - expected %b found %b%n", key, value, setmap.contains(key, value), (expected.containsKey(key) && expected.get(key).contains(value))));
                        }
                        break;
                    }
                    case 1: { //containsAll(K, Iterable)
                        IList<String> values = makeContainsList(key, expected, random, tokens);
                        if (setmap.containsAll(key, values) != (expected.containsKey(key) && expected.get(key).containsAll(values))) {
                            throw new RuntimeException(String.format("containsAll(key, Iterable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAll(key, values), (expected.containsKey(key) && expected.get(key).containsAll(values))));
                        }
                        break;
                    }
                    case 2: { //containsAll(K, Collection)
                        IList<String> values = makeContainsList(key, expected, random, tokens);
                        if (setmap.containsAll(key, values.getList()) != (expected.containsKey(key) && expected.get(key).containsAll(values))) {
                            throw new RuntimeException(String.format("containsAll(key, Collection) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAll(key, values.getList()), (expected.containsKey(key) && expected.get(key).containsAll(values))));
                        }
                        break;
                    }
                    case 3: { //containsAny(K, Iterable)
                        IList<String> values = makeContainsList(key, expected, random, tokens);
                        if (setmap.containsAny(key, values) != (expected.containsKey(key) && expected.get(key).containsAny(values))) {
                            throw new RuntimeException(String.format("containsAll(key, Iterable) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAny(key, values), (expected.containsKey(key) && expected.get(key).containsAny(values))));
                        }
                        break;
                    }
                    case 4: { //containsAny(K, Collection)
                        IList<String> values = makeContainsList(key, expected, random, tokens);
                        if (setmap.containsAny(key, values.getList()) != (expected.containsKey(key) && expected.get(key).containsAny(values))) {
                            throw new RuntimeException(String.format("containsAll(key, Collection) method call failed for %s, %s - expected %b, found %b%n", key, values, setmap.containsAny(key, values.getList()), (expected.containsKey(key) && expected.get(key).containsAny(values))));
                        }
                        break;
                    }
                    case 5: { //get(K)
                        ISet<String> set = setmap.get(key);
                        ISet<String> expectedSet = expected.getOrDefault(key, null);
                        if (!((set == null && expectedSet == null) || (expectedSet != null && expectedSet.equals(set)))) {
                            throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedSet, set));
                        }
                        break;
                    }
                    case 6: { //getValueOr(K, V)
                        ISet<String> set = setmap.getValueOr(key, ISets.hashed(""));
                        ISet<String> expectedSet = (expected.containsKey(key)) ? expected.get(key) : ISets.hashed("");
                        if (!set.equals(expectedSet)) {
                            throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - expected %s found %s%n", key, expectedSet, set));
                        }
                        break;
                    }
                    case 7: { //find(K)
                        Holder<ISet<String>> holder = setmap.find(key);
                        Holder<ISet<String>> expectedHolder;
                        if (expected.containsKey(key)) {
                            ISet<String> value = expected.get(key);
                            expectedHolder = Holders.nullable(value);
                        } else {
                            expectedHolder = Holder.none();
                        }
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
        verifyIteration(setmap, expected);
        verifyFinalSize(size, setmap.size());
        //printStats(setmap);
        System.out.printf("cleanup %d%n", setmap.size());
        int threshold = random.nextInt(3);
        while (setmap.size() > threshold) {
            //delete(K)
            String key = keys.randomKey();
            setmap = setmap.delete(key);
            expected.remove(key);
            keys.unallocate(key);
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
        System.out.printf("SetMapStressTest on %s completed without errors%n", getName(setmap));
    }

    private void verifyContents(final ISetMap<String, String> setmap,
                                final Map<String, ISet<String>> expected)
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
        for (Map.Entry<String, ISet<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            ISet<String> expectedSet = entry.getValue();
            ISet<String> set = setmap.getSet(key);
            if (!expectedSet.equals(set)) {
                throw new RuntimeException(String.format("values mismatch for key %s - expected jset %s found jset %s%n", key, expectedSet, set));
            }
        }
        setmap.checkInvariants();
        verifySerializable(this::extraSerializationChecks, setmap, ISetMap.class);
    }

    private void verifyIteration(final ISetMap<String, String> setmap,
                                 final Map<String, ISet<String>> expected)
    {
        System.out.printf("checking iterator with size %d%n", setmap.size());

        List<IMapEntry<String, ISet<String>>> entries = makeEntriesList(expected);
        if (setmap instanceof HashSetMap) {
            final ExpectedOrderSorter<String> ordering = new ExpectedOrderSorter<>(setmap.keys().iterator());
            entries = ordering.sort(entries, e -> e.getKey());
        }
        List<String> keys = extractKeys(entries);

        StandardIteratorTests.listIteratorTest(keys, setmap.keys().iterator());
        StandardIteratorTests.listIteratorTest(entries, setmap.iterator());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(keys, setmap.keys());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(entries, setmap);

        for (Map.Entry<String, ISet<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            List<String> values = asList(entry.getValue());
            StandardIteratorTests.listIteratorTest(values, setmap.values(key).iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(values, setmap.values(key));
        }
    }


    private void unionAt(Map<String, ISet<String>> expected,
                         String key,
                         Iterable<String> values)
    {
        ISet<String> set = (expected.containsKey(key)) ? expected.get(key) : ISets.hashed();
        expected.put(key, set.union(values));
    }

    private void intersectionAt(Map<String, ISet<String>> expected,
                                String key,
                                Iterable<String> values)
    {
        ISet<String> set = (expected.containsKey(key)) ? expected.get(key) : ISets.hashed();
        expected.put(key, set.intersection(values));
    }

    private void removeAllAt(Map<String, ISet<String>> expected,
                             String key,
                             Iterable<String> values)
    {
        if (expected.containsKey(key)) {
            ISet<String> set = expected.get(key);
            expected.put(key, set.deleteAll(values));
        }
    }

    private void removeAt(Map<String, ISet<String>> expected,
                          String key,
                          String value)
    {
        if (expected.containsKey(key)) {
            ISet<String> set = expected.get(key);
            expected.put(key, set.delete(value));
        }
    }

    private void addAt(Map<String, ISet<String>> expected,
                       String key,
                       String value)
    {
        ISet<String> set = (expected.containsKey(key)) ? expected.get(key) : ISets.hashed();
        set = set.insert(value);
        expected.put(key, set);
    }

    private void addAllAt(Map<String, ISet<String>> expected,
                          String key,
                          Iterable<String> values)
    {
        ISet<String> set = (expected.containsKey(key)) ? expected.get(key) : ISets.hashed();
        expected.put(key, set.insertAll(values.iterator()));
    }

    private int randomGrowingSize(Random random)
    {
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
        return limit;
    }

    private IList<String> makeGrowingList(IList<String> tokens,
                                          Random random)
    {
        final int limit = randomGrowingSize(random);
        IList<String> list = ILists.of();
        for (int i = 0; i < limit; ++i) {
            list = list.insert(RandomKeyManager.makeValue(tokens, random));
        }
        return list;
    }

    private ISet<String> makeGrowingSet(IList<String> tokens,
                                        Random random)
    {
        final int limit = randomGrowingSize(random);
        ISet<String> set = ISets.hashed();
        while (set.size() < limit) {
            set = set.insert(RandomKeyManager.makeValue(tokens, random));
        }
        return set;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    private IList<String> makeUpdateList(IList<String> tokens,
                                         Random random,
                                         String key,
                                         Map<String, ISet<String>> expected)
    {
        IList<String> values = ILists.of();
        IList<String> expectedSet = ILists.allOf(expected.get(key));
        for (int i = 0, limit = random.nextInt(5); i < limit; ++i) {
            if (random.nextBoolean() || expectedSet.size() == 0) {
                values = values.insert(RandomKeyManager.makeValue(tokens, random));
            } else {
                values = values.insert(expectedSet.get(random.nextInt(expectedSet.size())));
            }
        }
        return values;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    private ISet<String> makeUpdateSet(IList<String> tokens,
                                       Random random,
                                       String key,
                                       Map<String, ISet<String>> expected)
    {
        ISet<String> values = ISets.hashed();
        IList<String> expectedSet = ILists.allOf(expected.get(key));
        int limit = random.nextInt(5);
        while (values.size() < limit) {
            if (random.nextBoolean() || expectedSet.size() == 0) {
                values = values.insert(RandomKeyManager.makeValue(tokens, random));
            } else {
                values = values.insert(expectedSet.get(random.nextInt(expectedSet.size())));
            }
        }
        return values;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    protected String makeUpdateValue(IList<String> tokens,
                                     Random random,
                                     String key,
                                     Map<String, ISet<String>> expected)
    {
        String value;
        ISet<String> expectedSet = expected.get(key);
        if (random.nextBoolean() || expectedSet.size() == 0) {
            value = RandomKeyManager.makeValue(tokens, random);
            while (expectedSet.contains(value)) {
                value = RandomKeyManager.makeValue(tokens, random);
            }
        } else {
            IList<String> list = ILists.allOf(expectedSet);
            value = list.get(random.nextInt(list.size()));
        }
        return value;
    }

    private IList<String> makeIntersectList(IList<String> tokens,
                                            Random random,
                                            String key,
                                            Map<String, ISet<String>> expected)
    {
        IList<String> values;
        if (expected.containsKey(key)) {
            values = ILists.allOf(expected.get(key));
            for (int i = 0, limit = random.nextInt(3); i < limit && values.size() >= 1; ++i) {
                values = values.delete(random.nextInt(values.size()));
            }
        } else {
            values = ILists.of();
        }
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            values = values.insert(RandomKeyManager.makeValue(tokens, random));
        }
        return values;
    }

    private ISet<String> makeIntersectSet(IList<String> tokens,
                                          Random random,
                                          String key,
                                          Map<String, ISet<String>> expected)
    {
        ISet<String> values;
        if (expected.containsKey(key)) {
            values = expected.get(key);
            IList<String> list = ILists.allOf(values);
            for (int i = 0, limit = random.nextInt(3); i < limit && list.size() >= 1; ++i) {
                int index = random.nextInt(list.size());
                values = values.delete(list.get(index));
                list = list.delete(index);
            }
        } else {
            values = ISets.hashed();
        }
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            values = values.insert(RandomKeyManager.makeValue(tokens, random));
        }
        return values;
    }

    private IList<String> makeContainsList(String key,
                                           Map<String, ISet<String>> expected,
                                           Random random,
                                           IList<String> tokens)
    {
        IList<String> values = ILists.of();
        if (!expected.containsKey(key) || expected.get(key).isEmpty()) {
            for (int n = 0, limit = random.nextInt(5); n < limit; ++n) {
                values = values.insert(RandomKeyManager.makeValue(tokens, random));
            }
        } else {
            IList<String> setValues = ILists.allOf(expected.get(key));
            for (int n = 0, limit = random.nextInt(5); n < limit; ++n) {
                values = (random.nextBoolean()) ? values.insert(RandomKeyManager.makeValue(setValues, random)) : values.insert(RandomKeyManager.makeValue(tokens, random));

            }
        }
        return values;
    }

    protected IList<String> makeDeleteList(IList<String> tokens,
                                           Random random,
                                           String key,
                                           Map<String, ISet<String>> expected)
    {
        IList<String> list = ILists.of();
        IList<String> jImmutableInMap = listFromExpected(key, expected);
        for (int i = 0, limit = random.nextInt(4); i < limit; ++i) {
            if (random.nextBoolean() || jImmutableInMap.size() == 0) {
                list = list.insert(RandomKeyManager.makeValue(tokens, random));
            } else {
                list = list.insert(jImmutableInMap.get(random.nextInt(jImmutableInMap.size())));
            }
        }
        return list;
    }

    protected String makeDeleteValue(IList<String> tokens,
                                     Random random,
                                     String key,
                                     Map<String, ISet<String>> expected)
    {
        if (random.nextBoolean() || !expected.containsKey(key)) {
            return RandomKeyManager.makeValue(tokens, random);
        } else {
            IList<String> jImmutableInMap = listFromExpected(key, expected);
            return jImmutableInMap.isEmpty() ? RandomKeyManager.makeValue(tokens, random) : jImmutableInMap.get(random.nextInt(jImmutableInMap.size()));
        }
    }

    private IList<String> listFromExpected(String key,
                                           Map<String, ISet<String>> expected)
    {
        ISet<String> value = expected.get(key);
        if (value == null) {
            return ILists.of();
        } else {
            return ILists.allOf(value);
        }
    }

    private void extraSerializationChecks(Object a,
                                          Object b)
    {
        if (a instanceof TreeSetMap) {
            TreeSetMapTest.extraSerializationChecks(a, b);
        }
    }
}
