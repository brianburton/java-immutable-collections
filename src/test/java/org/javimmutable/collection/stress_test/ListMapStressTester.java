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

package org.javimmutable.collection.stress_test;

import org.javimmutable.collection.IList;
import org.javimmutable.collection.IListMap;
import org.javimmutable.collection.ILists;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.MapEntry;
import org.javimmutable.collection.Maybe;
import org.javimmutable.collection.common.ExpectedOrderSorter;
import org.javimmutable.collection.common.StandardStreamableTests;
import org.javimmutable.collection.iterators.StandardIteratorTests;
import org.javimmutable.collection.listmap.HashListMap;
import org.javimmutable.collection.listmap.TreeListMap;
import org.javimmutable.collection.listmap.TreeListMapTest;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.javimmutable.collection.common.StandardSerializableTests.verifySerializable;

/**
 * Test program for all implementations of JImmutableListMap. Divided into five sections:
 * growing (adds new key-list pairs), updating (adds values to the lists of the listmap
 * without changing the keys), shrinking (removes key-list pairts), contains (tests methods that
 * find lists contained in the listmap), and cleanup (empties the listmap of all key-list
 * pairs).
 * <p>
 * This tester was designed so that the listmap produced would contains lists of a large
 * variety of sizes. On average, 11% of the lists in the listmap will be empty by the end
 * of the test. 26% will contain only one value. 58% will contain between two and ten
 * values, and the remaining 5% will contain between eleven and over a hundred values.
 */
@SuppressWarnings("Duplicates")
public class ListMapStressTester
    extends AbstractMapStressTestable
{
    private final IListMap<String, String> listmap;
    private final Class<? extends Map> expectedClass;

    public ListMapStressTester(IListMap<String, String> listmap,
                               Class<? extends Map> expectedClass)
    {
        super(getName(listmap));
        this.listmap = listmap;
        this.expectedClass = expectedClass;
    }

    @Override
    public IList<String> getOptions()
    {
        return ILists.of("lmap", "listmap", getNameOption(listmap));
    }

    @Override
    public void execute(Random random,
                        IList<String> tokens)
        throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Map<String, IList<String>> expected = expectedClass.newInstance();
        final RandomKeyManager keys = new RandomKeyManager(random, tokens);
        IListMap<String, String> listmap = this.listmap;
        final int size = 1 + random.nextInt(100000);
        System.out.printf("ListMapStressTest on %s of size %d%n", getName(listmap), size);

        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(6, size, random)) {
            System.out.printf("growing keys %d%n", listmap.size());
            while (expected.size() < step.growthSize()) {
                String key = keys.randomUnallocatedKey();
                keys.allocate(key);
                switch (random.nextInt(3)) {
                    case 0: { //assign(K, JList)
                        IList<String> values = makeGrowingList(tokens, random);
                        listmap = listmap.assign(key, values);
                        expected.put(key, values);
                        break;
                    }
                    case 1: { //insert(K, V)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        listmap = listmap.insert(key, value);
                        addAt(expected, key, value);
                        break;
                    }
                    case 2: { //insert(Entry<K, IList<V>>)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        IMapEntry<String, IList<String>> entry = new MapEntry<>(key, ILists.of(value));
                        listmap = listmap.insert(entry);
                        addAt(expected, key, value);
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(listmap, expected);
            verifyKeys(keys, expected);
            System.out.printf("updating %d%n", listmap.size());
            for (int i = 0; i < listmap.size(); ++i) {
                String key = keys.randomAllocatedKey();
                switch (random.nextInt(3)) {
                    case 0: { //assign(K, JList)
                        IList<String> values = makeUpdateList(tokens, random);
                        listmap = listmap.assign(key, values);
                        expected.put(key, values);
                        break;
                    }
                    case 1: { //insert(K, V)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        listmap = listmap.insert(key, value);
                        addAt(expected, key, value);
                        break;
                    }
                    case 2: { //insert(Entry<K, IList<V>>)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        IMapEntry<String, IList<String>> entry = new MapEntry<>(key, ILists.of(value));
                        listmap = listmap.insert(entry);
                        addAt(expected, key, value);
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(listmap, expected);
            verifyKeys(keys, expected);
            System.out.printf("shrinking keys %d%n", listmap.size());
            while (expected.size() > step.shrinkSize()) {
                //delete(K)
                String key = keys.randomKey();
                listmap = listmap.delete(key);
                expected.remove(key);
                keys.unallocate(key);
            }
            verifyContents(listmap, expected);
            verifyKeys(keys, expected);
            System.out.printf("contains %d%n", listmap.size());
            for (int i = 0; i < size / 12; ++i) {
                String key = keys.randomKey();
                switch (random.nextInt(3)) {
                    case 0: { //get(K)
                        IList<String> list = listmap.get(key);
                        IList<String> expectedList = expected.get(key);
                        if (!((list == null && expectedList == null) || (expectedList != null && expectedList.equals(list)))) {
                            throw new RuntimeException(String.format("get(key) method call failed for %s - expected %s found %s%n", key, expectedList, list));
                        }
                        break;
                    }
                    case 1: { //getValueOr(K, V)
                        IList<String> list = listmap.getValueOr(key, ILists.of(""));
                        IList<String> expectedList = (expected.containsKey(key)) ? expected.get(key) : ILists.of("");
                        if (!list.equals(expectedList)) {
                            throw new RuntimeException(String.format("getValueOr(key, default) method call failed for %s - expected %s found %s%n", key, expectedList, list));
                        }
                        break;
                    }
                    case 2: { //find(K)
                        Maybe<IList<String>> maybe = listmap.find(key);
                        Maybe<IList<String>> expectedMaybe;
                        if (expected.containsKey(key)) {
                            IList<String> value = expected.get(key);
                            expectedMaybe = Maybe.of(value);
                        } else {
                            expectedMaybe = Maybe.empty();
                        }
                        if (!equivalentHolder(maybe, expectedMaybe)) {
                            throw new RuntimeException(String.format("find(key) method call failed for %s - expected %s found %s%n", key, expectedMaybe, maybe));
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
        }
        verifyIteration(listmap, expected);
        verifyFinalSize(size, listmap.size());
//        printStats(listmap);
        System.out.printf("cleanup %d%n", listmap.size());
        int threshold = random.nextInt(3);
        while (listmap.size() > threshold) {
            //delete(K)
            String key = keys.randomKey();
            listmap = listmap.delete(key);
            expected.remove(key);
            keys.unallocate(key);
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
        System.out.printf("ListMapStressTest on %s completed without errors%n", getName(listmap));
    }

    private void verifyContents(final IListMap<String, String> listmap,
                                Map<String, IList<String>> expected)
    {
        System.out.printf("checking contents with size %s%n", listmap.size());
        if (listmap.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), listmap.isEmpty()));
        }
        if (listmap.size() != expected.size()) {
            throw new RuntimeException(String.format("key size mismatch - expected %d found %d%n", expected.size(), listmap.size()));
        }
        for (Map.Entry<String, IList<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            IList<String> expectedValue = entry.getValue();
            IList<String> value = listmap.getList(key);
            if (!expectedValue.equals(value)) {
                throw new RuntimeException(String.format("values mismatch for key %s - expected set %s found jlist %s%n", key, expectedValue, value));
            }
        }
        listmap.checkInvariants();
        verifySerializable(this::extraSerializationChecks, listmap, IListMap.class);
    }

    private void verifyIteration(final IListMap<String, String> listmap,
                                 Map<String, IList<String>> expected)

    {
        System.out.printf("checking cursor with size %d%n", listmap.size());
        List<IMapEntry<String, IList<String>>> entries = makeEntriesList(expected);
        if (listmap instanceof HashListMap) {
            final ExpectedOrderSorter<String> ordering = new ExpectedOrderSorter<>(listmap.keys().iterator());
            entries = ordering.sort(entries, e -> e.getKey());
        }
        List<String> keys = extractKeys(entries);

        StandardIteratorTests.listIteratorTest(keys, listmap.keys().iterator());
        StandardIteratorTests.listIteratorTest(entries, listmap.iterator());
        StandardStreamableTests.verifyOrderedUsingCollection(keys, listmap.keys());
        StandardStreamableTests.verifyOrderedUsingCollection(entries, listmap);

        for (Map.Entry<String, IList<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            List<String> values = asList(entry.getValue());
            StandardIteratorTests.listIteratorTest(values, listmap.values(key).iterator());
            StandardStreamableTests.verifyOrderedUsingCollection(values, listmap.values(key));
        }
    }

    private void addAt(Map<String, IList<String>> expected,
                       String key,
                       String value)
    {
        IList<String> list = (expected.containsKey(key)) ? expected.get(key) : ILists.of();
        list = list.insert(value);
        expected.put(key, list);
    }


    //Precondition: only to be used in update. Key must always be in expected.
    protected IList<String> makeUpdateList(IList<String> tokens,
                                           Random random)
    {
        IList<String> values = ILists.of();
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            values = values.insert(RandomKeyManager.makeValue(tokens, random));
        }
        return values;
    }

    //Precondition: only to be used in update. Key must always be in expected.
    protected IList<String> makeGrowingList(IList<String> tokens,
                                            Random random)
    {
        IList<String> list = ILists.of();
        int limit;
        int command = random.nextInt(100);
        if (command < 20) {
            limit = 0;
        } else if (command < 50) {
            limit = random.nextInt(3);
        } else if (command < 70) {
            limit = random.nextInt(10);
        } else if (command < 80) {
            limit = random.nextInt(20);
        } else if (command < 95) {
            limit = random.nextInt(command) + 10;
        } else {
            limit = random.nextInt(command + 50) + 250;
        }
        for (int i = 0; i < limit; ++i) {
            list = list.insert(RandomKeyManager.makeValue(tokens, random));
        }
        return list;
    }

    //used in debugging
    @SuppressWarnings("unused")
    private void printStats(IListMap<String, String> listmap)
    {
        double size = listmap.size();
        double zero = 0;
        double one = 0;
        double OneToTen = 0;
        double TenToTwenty = 0;
        double TwentyToFifty = 0;
        double FiftyToHundred = 0;
        double OverHundred = 0;

        for (String key : listmap.keys()) {
            IList<String> list = listmap.get(key);
            assert (list != null);
            if (list.size() == 0) {
                ++zero;
            } else if (list.size() == 1) {
                ++one;
            } else if (list.size() <= 10) {
                ++OneToTen;
            } else if (list.size() <= 20) {
                ++TenToTwenty;
            } else if (list.size() <= 50) {
                ++TwentyToFifty;
            } else if (list.size() <= 100) {
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

    private void extraSerializationChecks(Object a,
                                          Object b)
    {
        if (a instanceof TreeListMap) {
            TreeListMapTest.extraSerializationChecks(a, b);
        }
    }
}
