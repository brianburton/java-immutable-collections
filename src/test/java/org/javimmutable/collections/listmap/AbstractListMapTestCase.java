///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.listmap;

import static com.google.common.primitives.Ints.asList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Temp;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.list.TreeList;

public abstract class AbstractListMapTestCase
    extends TestCase
{
    public enum Ordering
    {
        HASH,
        INORDER,
        REVERSED
    }

    public IListMap<Integer, Integer> verifyOperations(IListMap<Integer, Integer> map,
                                                       Ordering ordering)
    {
        assertTrue(map.isEmpty());
        assertFalse(map.isNonEmpty());
        assertEquals(0, map.size());
        assertNull(map.get(1));
        assertEquals(0, map.getList(1).size());

        map = map.insert(1, 100);
        assertFalse(map.isEmpty());
        assertTrue(map.isNonEmpty());
        assertEquals(1, map.size());
        assertSame(map.getList(1), map.get(1));
        assertEquals(1, map.getList(1).size());

        map = map.insert(1, 18);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEquals(Arrays.asList(100, 18), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(2, map.getList(1).size());

        map = map.insert(MapEntry.of(3, 87));
        map = map.insert(MapEntry.of(2, 87));
        map = map.insert(MapEntry.of(1, 87));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(100, 18, 87), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(Arrays.asList(87), map.getList(2).getList());
        assertSame(map.getList(2), map.get(2));
        assertEquals(Arrays.asList(87), map.getList(3).getList());
        assertSame(map.getList(3), map.get(3));

        IListMap<Integer, Integer> preInsertMap = map;
        map = map.assign(3, TreeList.<Integer>of().insert(300).insert(7).insert(7).insert(14));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(100, 18, 87), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(Arrays.asList(87), map.getList(2).getList());
        assertSame(map.getList(2), map.get(2));
        assertEquals(Arrays.asList(300, 7, 7, 14), map.getList(3).getList());
        assertSame(map.getList(3), map.get(3));

        assertSame(map, map.delete(12)); // nothing to delete returns same map

        preInsertMap = preInsertMap.delete(3);
        final List<Integer> insertValuesList = asList(300, 7, 7, 14);
        assertEquals(map, preInsertMap.insertAll(3, insertValuesList));
        assertEquals(map, preInsertMap.insertAll(3, insertValuesList.iterator()));

        assertEquals(map.delete(1).delete(3), map.deleteAll(asList(3, 1)));
        assertEquals(map.delete(1).delete(3), map.deleteAll(asList(3, 1).iterator()));

        final IList<Integer> defaultValue = TreeList.<Integer>of().insert(17);
        Holder<IList<Integer>> iLists = map.find(8);
        assertTrue(iLists.isNone());
        assertNull(map.get(8));
        assertNull(map.getValueOr(8, null));
        assertSame(defaultValue, map.getValueOr(8, defaultValue));
        Holder<IList<Integer>> iLists1 = map.find(3);
        assertSame(map.get(3), iLists1.unsafeGet());
        assertSame(map.get(3), map.getValueOr(3, defaultValue));
        assertTrue(map.deleteAll().isEmpty());
        assertTrue(map.delete(3).delete(2).delete(1).delete(0).isEmpty());

        StandardIteratorTests.listIteratorTest(Arrays.asList(100, 18, 87), map.values(1).iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(87), map.values(2).iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(300, 7, 7, 14), map.values(3).iterator());
        StandardIteratorTests.listIteratorTest(Collections.emptyList(), map.values(4).iterator());

        verifyTransform(map);
        verifyCollector(map.insert(-10, -20).insert(-45, 90), ordering);

        return map;
    }

    private void verifyTransform(IListMap<Integer, Integer> map)
    {
        final Func1<IList<Integer>, IList<Integer>> removeAll = list -> list.deleteAll();
        final Func1<IList<Integer>, IList<Integer>> removeLarge = list -> list.reject(x -> x >= 10);
        final Func1<IList<Integer>, IList<Integer>> removeEven = list -> list.reject(x -> x % 2 == 0);

        final int goodKey = 1;
        final int badKey = 2;

        final IListMap<Integer, Integer> start = map.deleteAll().insertAll(goodKey, Arrays.asList(1, 2, 3, 4, 5, 6));
        final IList<Integer> oddOnly = start.getList(goodKey).reject(x -> x % 2 == 0);

        assertSame(start, start.transform(goodKey, removeLarge));
        assertEquals(start.assign(goodKey, oddOnly), start.transform(goodKey, removeEven));
        assertSame(start, start.transform(badKey, removeLarge));

        assertSame(start, start.transformIfPresent(goodKey, removeLarge));
        assertEquals(start.assign(goodKey, oddOnly), start.transformIfPresent(goodKey, removeEven));
        assertSame(start, start.transform(badKey, removeLarge));
        assertSame(start, start.transformIfPresent(badKey, removeAll));
        assertSame(start, start.transformIfPresent(badKey, removeAll));
    }

    private static void verifyCollector(IListMap<Integer, Integer> template,
                                        Ordering ordering)
    {
        List<IMapEntry<Integer, Integer>> values = IntStream.range(1, 2500).boxed().map(i -> MapEntry.of(i, -i)).collect(Collectors.toList());
        switch (ordering) {
            case HASH: {
                Set<IMapEntry<Integer, Integer>> entries = template.deleteAll().insertAll(values).entries().parallelStream().collect(Collectors.toSet());
                Set<IMapEntry<Integer, Integer>> expected = new HashSet<>(values);
                assertEquals(expected, entries);
                break;
            }
            case INORDER: {
                List<IMapEntry<Integer, Integer>> entries = template.deleteAll().insertAll(values).entries().parallelStream().collect(Collectors.toList());
                assertEquals(values, entries);
                break;
            }
            case REVERSED: {
                List<IMapEntry<Integer, Integer>> entries = template.deleteAll().insertAll(values).entries().parallelStream().collect(Collectors.toList());
                Collections.reverse(entries);
                assertEquals(values, entries);
                break;
            }
        }

        IListMap<Integer, Integer> expected = template.insertAll(values);
        IListMap<Integer, Integer> actual = values.parallelStream().collect(template.toCollector());
        assertEquals(expected, actual);

        verifyForEach(actual);
    }

    private static void verifyForEach(IListMap<Integer, Integer> jetMap)
    {
        final Temp.Int1 count = Temp.intVar(0);
        jetMap.forEach((key, set) -> {
            count.a += 1;
            assertEquals(set, jetMap.getList(key));
        });
        assertEquals(jetMap.size(), count.a);

        count.a = 0;
        jetMap.forEachThrows((key, set) -> {
            count.a += 1;
            assertEquals(set, jetMap.getList(key));
        });
        assertEquals(jetMap.size(), count.a);
    }
}
