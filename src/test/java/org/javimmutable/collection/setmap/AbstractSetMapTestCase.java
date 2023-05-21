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

package org.javimmutable.collection.setmap;

import junit.framework.TestCase;
import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IList;
import org.javimmutable.collection.ILists;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.ISet;
import org.javimmutable.collection.ISetMap;
import org.javimmutable.collection.ISets;
import org.javimmutable.collection.Maybe;
import org.javimmutable.collection.Temp;
import org.javimmutable.collection.hash.HashSet;
import org.javimmutable.collection.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javimmutable.collection.common.TestUtil.makeSet;

public abstract class AbstractSetMapTestCase
    extends TestCase
{
    public enum Ordering
    {
        INORDER,
        REVERSED,
        HASH
    }

    public ISetMap<Integer, Integer> verifyOperations(ISetMap<Integer, Integer> map,
                                                      Ordering ordering)
    {
        verifySetOperations(map);
        verifyContains(map);

        assertTrue(map.isEmpty());
        assertFalse(map.isNonEmpty());
        assertEquals(0, map.size());
        assertNull(map.get(1));
        assertEquals(0, map.getSet(1).size());

        map = map.insert(1, 100);
        assertFalse(map.isEmpty());
        assertTrue(map.isNonEmpty());
        assertEquals(1, map.size());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(1, map.getSet(1).size());

        map = map.insert(1, 18);
        assertFalse(map.isEmpty());
        assertTrue(map.isNonEmpty());
        assertEquals(1, map.size());
        assertEquals(new java.util.HashSet<>(Arrays.asList(100, 18)), map.getSet(1).getSet());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(2, map.getSet(1).size());

        map = map.insert(3, 87);
        map = map.insert(2, 87);
        map = map.insert(1, 87);
        map = map.insert(1, 87);
        assertFalse(map.isEmpty());
        assertTrue(map.isNonEmpty());
        assertEquals(3, map.size());
        assertEquals(new java.util.HashSet<>(Arrays.asList(100, 18, 87)), map.getSet(1).getSet());
        assertEquals(3, map.getSet(1).size());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(new java.util.HashSet<>(Arrays.asList(87)), map.getSet(2).getSet());
        assertSame(map.getSet(2), map.get(2));
        assertEquals(new java.util.HashSet<>(Arrays.asList(87)), map.getSet(3).getSet());
        assertSame(map.getSet(3), map.get(3));

        map = map.assign(3, map.getSet(Integer.MIN_VALUE).insert(300).insert(7).insert(7).insert(14));
        assertFalse(map.isEmpty());
        assertTrue(map.isNonEmpty());
        assertEquals(3, map.size());
        assertEquals(new java.util.HashSet<>(Arrays.asList(100, 18, 87)), map.getSet(1).getSet());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(new java.util.HashSet<>(Arrays.asList(87)), map.getSet(2).getSet());
        assertSame(map.getSet(2), map.get(2));
        assertEquals(new java.util.HashSet<>(Arrays.asList(300, 7, 14)), map.getSet(3).getSet());
        assertSame(map.getSet(3), map.get(3));
        assertEquals(map.getSet(3), map.values(3).stream().collect(Collectors.toSet()));

        final ISet<Integer> defaultValue = HashSet.<Integer>of().insert(17);
        Maybe<ISet<Integer>> iSets = map.find(8);
        assertTrue(iSets.isEmpty());
        assertNull(map.get(8));
        assertNull(map.getValueOr(8, null));
        assertSame(defaultValue, map.getValueOr(8, defaultValue));
        Maybe<ISet<Integer>> iSets1 = map.find(3);
        assertSame(map.get(3), iSets1.unsafeGet());
        assertSame(map.get(3), map.getValueOr(3, defaultValue));
        assertTrue(map.deleteAll().isEmpty());
        assertFalse(map.deleteAll().isNonEmpty());
        assertTrue(map.delete(3).delete(2).delete(1).delete(0).isEmpty());

        if (ordering == Ordering.HASH) {
            StandardIteratorTests.listIteratorTest(Arrays.asList(18, 87, 100), map.values(1).iterator());
            StandardIteratorTests.listIteratorTest(Arrays.asList(87), map.values(2).iterator());
            StandardIteratorTests.listIteratorTest(Arrays.asList(7, 14, 300), map.values(3).iterator());
            StandardIteratorTests.listIteratorTest(Collections.emptyList(), map.values(4).iterator());
        } else if (ordering == Ordering.REVERSED) {
            StandardIteratorTests.listIteratorTest(Arrays.asList(100, 87, 18), map.values(1).iterator());
            StandardIteratorTests.listIteratorTest(Arrays.asList(87), map.values(2).iterator());
            StandardIteratorTests.listIteratorTest(Arrays.asList(300, 14, 7), map.values(3).iterator());
            StandardIteratorTests.listIteratorTest(Collections.emptyList(), map.values(4).iterator());
        } else {
            StandardIteratorTests.listIteratorTest(Arrays.asList(18, 87, 100), map.values(1).iterator());
            StandardIteratorTests.listIteratorTest(Arrays.asList(87), map.values(2).iterator());
            StandardIteratorTests.listIteratorTest(Arrays.asList(7, 14, 300), map.values(3).iterator());
            StandardIteratorTests.listIteratorTest(Collections.emptyList(), map.values(4).iterator());
        }

        verifyTransform(map);
        verifyCollector(map.insert(-10, -20).insert(-45, 90));

        return map;
    }

    private void verifyTransform(ISetMap<Integer, Integer> map)
    {
        final Func1<ISet<Integer>, ISet<Integer>> removeAll = set -> set.deleteAll();
        final Func1<ISet<Integer>, ISet<Integer>> removeLarge = set -> set.reject(x -> x >= 10);
        final Func1<ISet<Integer>, ISet<Integer>> removeEven = set -> set.reject(x -> x % 2 == 0);

        final int goodKey = 1;
        final int badKey = 2;

        final ISetMap<Integer, Integer> start = map.deleteAll().insertAll(goodKey, Arrays.asList(1, 2, 3, 4, 5, 6));
        final ISet<Integer> oddOnly = start.getSet(goodKey).reject(x -> x % 2 == 0);

        assertSame(start, start.transform(goodKey, removeLarge));
        assertEquals(start.assign(goodKey, oddOnly), start.transform(goodKey, removeEven));
        assertSame(start, start.transform(badKey, removeLarge));

        assertSame(start, start.transformIfPresent(goodKey, removeLarge));
        assertEquals(start.assign(goodKey, oddOnly), start.transformIfPresent(goodKey, removeEven));
        assertSame(start, start.transform(badKey, removeLarge));
        assertSame(start, start.transformIfPresent(badKey, removeAll));
        assertSame(start, start.transformIfPresent(badKey, removeAll));
    }

    private void verifyContains(ISetMap<Integer, Integer> emptyMap)
    {
        IList<Integer> values = ILists.of();
        ISetMap<Integer, Integer> jetMap = emptyMap.insertAll(1, ISets.hashed(1, 2, 4));

        //empty with empty
        assertEquals(false, emptyMap.contains(1));
        assertEquals(false, emptyMap.contains(1, null));
        assertEquals(false, emptyMap.containsAll(1, values));
        assertEquals(false, emptyMap.containsAll(1, values.getList()));
        assertEquals(false, emptyMap.containsAny(1, values));
        assertEquals(false, emptyMap.containsAny(1, values.getList()));

        //values with empty
        assertEquals(true, jetMap.contains(1));
        assertEquals(false, jetMap.contains(1, null));
        assertEquals(true, jetMap.containsAll(1, values));
        assertEquals(true, jetMap.containsAll(1, values.getList()));
        assertEquals(false, jetMap.containsAny(1, values));
        assertEquals(false, jetMap.containsAny(1, values.getList()));

        //empty with values
        values = values.insert(1).insert(1).insert(2);
        assertEquals(false, emptyMap.contains(1, 1));
        assertEquals(false, emptyMap.containsAll(1, values));
        assertEquals(false, emptyMap.containsAll(1, values.getList()));
        assertEquals(false, emptyMap.containsAny(1, values));
        assertEquals(false, emptyMap.containsAny(1, values.getList()));

        //values with values
        //smaller values
        assertEquals(true, jetMap.contains(1, 1));
        assertEquals(true, jetMap.containsAll(1, values));
        assertEquals(true, jetMap.containsAll(1, values.getList()));
        assertEquals(true, jetMap.containsAny(1, values));
        assertEquals(true, jetMap.containsAny(1, values.getList()));

        //extra values
        values = values.insert(5);
        assertEquals(false, jetMap.containsAll(1, values));
        assertEquals(false, jetMap.containsAll(1, values.getList()));
        assertEquals(true, jetMap.containsAny(1, values));
        assertEquals(true, jetMap.containsAny(1, values.getList()));

        //different values
        values = values.deleteAll().insert(3).insert(5);
        assertEquals(false, jetMap.containsAll(1, values));
        assertEquals(false, jetMap.containsAll(1, values.getList()));
        assertEquals(false, jetMap.containsAny(1, values));
        assertEquals(false, jetMap.containsAny(1, values.getList()));
    }

    private void verifySetOperations(ISetMap<Integer, Integer> emptyMap)
    {
        ISet<Integer> emptyJet = HashSet.of();
        final Set<Integer> emptySet = Collections.emptySet();

        assertEquals(0, emptyMap.size());
        assertEquals(true, emptyMap.isEmpty());
        assertEquals(emptyMap.getSet(0).getSet(), new java.util.HashSet<Integer>());

        ISetMap<Integer, Integer> jetMap = emptyMap;
        assertEquals(false, jetMap.contains(1, 10));
        jetMap = jetMap.insert(1, 10).insert(1, 20);
        assertEquals(true, jetMap != emptyMap);
        assertEquals(false, jetMap.isEmpty());
        assertEquals(true, jetMap.contains(1, 10));
        assertEquals(true, jetMap.contains(1, 20));
        assertEquals(true, jetMap.containsAll(1, Arrays.asList(10, 20)));

        jetMap = jetMap.delete(1);
        assertEquals(0, jetMap.size());
        assertEquals(false, jetMap.contains(1, 10));
        assertEquals(false, jetMap.contains(1, 20));

        final Set<Integer> values = makeSet(1, 2, 3, 4);
        HashMap<Integer, Set<Integer>> expected = new HashMap<>();
        expected.put(1, values);
        verifyExpected(expected, 1, values);
        verifyContents(jetMap.union(1, values), expected);
        verifyContents(jetMap.union(1, values.iterator()), expected);

        //intersect with larger set
        jetMap = emptyMap.union(1, values);
        final Set<Integer> withExtra = makeSet(0, 1, 2, 3, 4, 5);
        Set<Integer> intersectionSet = new java.util.HashSet<>(withExtra);
        ISet<Integer> intersectionJet = emptyJet.union(withExtra);
        verifyContents(jetMap.intersection(1, asIterable(withExtra)), expected);
        verifyContents(jetMap.intersection(1, withExtra), expected);
        verifyContents(jetMap.intersection(1, withExtra.iterator()), expected);
        verifyContents(jetMap.intersection(1, intersectionJet), expected);
        verifyContents(jetMap.intersection(1, intersectionSet), expected);

        //intersect with smaller set
        jetMap = emptyMap.union(1, withExtra);
        intersectionSet = new java.util.HashSet<>(values);
        intersectionJet = emptyJet.union(values);
        verifyContents(jetMap.intersection(1, asIterable(values)), expected);
        verifyContents(jetMap.intersection(1, values), expected);
        verifyContents(jetMap.intersection(1, values.iterator()), expected);
        verifyContents(jetMap.intersection(1, intersectionJet), expected);
        verifyContents(jetMap.intersection(1, intersectionSet), expected);

        //empty set intersection with non-empty set
        expected.put(1, emptySet);
        verifyExpected(expected, 1, emptySet);
        verifyExpected(expected, 1, emptySet);
        verifyContents(emptyMap.intersection(1, asIterable(withExtra)), expected);
        verifyContents(emptyMap.intersection(1, withExtra), expected);
        verifyContents(emptyMap.intersection(1, withExtra.iterator()), expected);
        verifyContents(emptyMap.intersection(1, intersectionJet), expected);
        verifyContents(emptyMap.intersection(1, intersectionSet), expected);

        //non-empty set intersection with empty set
        intersectionSet = new java.util.HashSet<>();
        intersectionJet = emptyJet;
        verifyContents(jetMap.intersection(1, asIterable(emptySet)), expected);
        verifyContents(jetMap.intersection(1, emptySet), expected);
        verifyContents(jetMap.intersection(1, emptySet.iterator()), expected);
        verifyContents(jetMap.intersection(1, intersectionJet), expected);
        verifyContents(jetMap.intersection(1, intersectionSet), expected);

        //deleteAll from smaller set
        expected.put(1, makeSet(0, 5));
        verifyExpected(expected, 1, makeSet(0, 5));
        jetMap = emptyMap.union(1, withExtra);
        verifyContents(jetMap.deleteAll(1, values), expected);
        verifyContents(jetMap.deleteAll(1, values.iterator()), expected);

        //deleteAll from larger set
        jetMap = emptyMap.union(1, values);
        expected.put(1, emptySet);
        verifyExpected(expected, 1, emptySet);
        verifyContents(jetMap.deleteAll(1, withExtra), expected);
        verifyContents(jetMap.deleteAll(1, withExtra.iterator()), expected);

        //insertAll to empty set
        jetMap = emptyMap;
        expected.put(1, values);
        verifyExpected(expected, 1, values);
        verifyContents(jetMap.insertAll(1, values), expected);
        verifyContents(jetMap.insertAll(1, values.iterator()), expected);

        //insertAll to non-empty set
        jetMap = emptyMap.union(1, values);
        expected.put(1, withExtra);
        verifyExpected(expected, 1, withExtra);
        verifyContents(jetMap.insertAll(1, withExtra), expected);
        verifyContents(jetMap.insertAll(1, withExtra.iterator()), expected);

        //insertAll to smaller set
        jetMap = emptyMap.union(1, Arrays.asList(4, 5, 6, 7));
        expected.put(1, makeSet(0, 1, 2, 3, 4, 5, 6, 7));
        verifyExpected(expected, 1, makeSet(0, 1, 2, 3, 4, 5, 6, 7));
        verifyContents(jetMap.insertAll(1, withExtra), expected);
        verifyContents(jetMap.insertAll(1, withExtra.iterator()), expected);
    }

    public void verifyContents(ISetMap<Integer, Integer> jetMap,
                               Map<Integer, Set<Integer>> expected)
    {
        assertEquals(expected.isEmpty(), jetMap.isEmpty());
        assertEquals(expected.size(), jetMap.size());
        for (Map.Entry<Integer, Set<Integer>> expectedEntry : expected.entrySet()) {
            Integer key = expectedEntry.getKey();
            Set<Integer> values = expectedEntry.getValue();
            for (Integer v : values) {
                assertEquals(true, jetMap.contains(key, v));
            }
            assertEquals(true, jetMap.containsAll(key, values));
            assertEquals(true, jetMap.containsAll(key, values.iterator()));

            assertEquals(!values.isEmpty(), jetMap.containsAny(key, values));
            assertEquals(!values.isEmpty(), jetMap.containsAny(key, values.iterator()));

            if (!values.isEmpty()) {
                List<Integer> subset = Arrays.asList(values.iterator().next());
                assertEquals(true, jetMap.containsAll(key, subset));
                assertEquals(true, jetMap.containsAll(key, subset.iterator()));

                assertEquals(true, jetMap.containsAny(key, subset));
                assertEquals(true, jetMap.containsAny(key, subset.iterator()));
            }
        }

        verifyForEach(jetMap);
    }

    private void verifyForEach(ISetMap<Integer, Integer> jetMap)
    {
        final Temp.Int1 count = Temp.intVar(0);
        jetMap.forEach((key, set) -> {
            count.a += 1;
            assertEquals(set, jetMap.getSet(key));
        });
        assertEquals(jetMap.size(), count.a);

        count.a = 0;
        jetMap.forEachThrows((key, set) -> {
            count.a += 1;
            assertEquals(set, jetMap.getSet(key));
        });
        assertEquals(jetMap.size(), count.a);
    }

    public void verifyRandom(ISetMap<Integer, Integer> emptyJetMap,
                             Map<Integer, Set<Integer>> expected)
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);

            ISetMap<Integer, Integer> jetMap = emptyJetMap;

            for (int loops = 0; loops < (4 * size); ++loops) {
                Integer key = random.nextInt(size);
                Set<Integer> set = new java.util.HashSet<>();
                for (int n = random.nextInt(3); n > 0; --n) {
                    set.add(random.nextInt(size));
                }
                int command = random.nextInt(10);
                switch (command) {
                    case 0:
                        jetMap = jetMap.insert(key, key);
                        if (expected.containsKey(key)) {
                            expected.get(key).add(key);
                        } else {
                            expected.put(key, makeSet(key));
                        }
                        verifyExpected(expected, key, makeSet(key));
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 1:
                        jetMap = jetMap.insertAll(key, set);
                        if (expected.containsKey(key)) {
                            expected.get(key).addAll(set);
                        } else {
                            expected.put(key, set);
                        }
                        verifyExpected(expected, key, set);
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 2:
                        jetMap = jetMap.delete(key);
                        if (expected.containsKey(key)) {
                            expected.remove(key);
                        }
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 3:
                        jetMap = jetMap.deleteAll(key, set);
                        if (expected.containsKey(key)) {
                            expected.get(key).removeAll(set);
                        }
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 4:
                        if (expected.containsKey(key)) {
                            assertEquals(jetMap.contains(key, key), expected.get(key).contains(key));
                        }
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 5:
                        if (expected.containsKey(key)) {
                            assertEquals(jetMap.containsAll(key, set), expected.get(key).containsAll(set));
                        }
                        break;
                    case 6:
                        jetMap = jetMap.union(key, set);
                        if (expected.containsKey(key)) {
                            expected.get(key).addAll(set);
                        } else {
                            expected.put(key, set);
                        }
                        verifyExpected(expected, key, set);
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 7:
                        jetMap = jetMap.intersection(key, set);
                        if (expected.containsKey(key)) {
                            expected.get(key).retainAll(set);
                        } else {
                            expected.put(key, new java.util.HashSet<>());
                        }
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 8:
                        for (Integer value : set) {
                            jetMap = jetMap.delete(key, value);
                        }
                        if (expected.containsKey(key)) {
                            expected.get(key).removeAll(set);
                        }
                        assertEquals(expected.size(), jetMap.size());
                        break;
                    case 9:
                        if (expected.containsKey(key)) {
                            Iterator<Integer> values = expected.get(key).iterator();
                            if (values.hasNext()) {
                                Integer value = values.next();
                                jetMap = jetMap.delete(key, value);
                                values.remove();
                            }
                        }
                        assertEquals(expected.size(), jetMap.size());
                        break;
                }
                assertEquals(expected.size(), jetMap.size());
            }
            jetMap.checkInvariants();
            verifyContents(jetMap, expected);
            for (IMapEntry<Integer, ISet<Integer>> e : jetMap) {
                jetMap = jetMap.delete(e.getKey());
                expected.remove(e.getKey());
            }
            jetMap.checkInvariants();
            assertEquals(0, jetMap.size());
            assertEquals(true, jetMap.isEmpty());
            assertEquals(expected.size(), jetMap.size());
        }
    }

    // forces java type system to use Iterable version of method
    protected Iterable<Integer> asIterable(Collection<Integer> values)
    {
        return values;
    }

    protected void verifyExpected(Map<Integer, Set<Integer>> expected,
                                  int key,
                                  Set<Integer> values)
    {
        assertEquals(true, expected.containsKey(key));
        assertEquals(true, expected.get(key).containsAll(values));
    }

    private static void verifyCollector(ISetMap<Integer, Integer> template)
    {
        ISetMap<Integer, Integer> expected = template;
        Collection<IMapEntry<Integer, Integer>> values = new ArrayList<>();
        for (int i = 1; i <= 500; ++i) {
            values.add(IMapEntry.of(i, i));
            expected = expected.insert(i, i);
            if (i % 2 == 0) {
                values.add(IMapEntry.of(i, -i));
                expected = expected.insert(i, -i);
            }
        }

        ISetMap<Integer, Integer> actual = values.parallelStream().collect(template.toCollector());
        assertEquals(expected, actual);
    }
}
