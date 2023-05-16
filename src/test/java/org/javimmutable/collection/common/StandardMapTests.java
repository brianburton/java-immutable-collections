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

package org.javimmutable.collection.common;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.ICollectors;
import org.javimmutable.collection.IMap;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.MapEntry;
import org.javimmutable.collection.Maybe;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.Assert.*;
import static org.javimmutable.collection.common.StandardStreamableTests.*;

public class StandardMapTests
{
    public static void verifyMiscellaneous(@Nonnull IMap<Integer, Integer> map)
    {
        IMap<Integer, Integer> x = map.deleteAll();

        assertEquals(true, x.isEmpty());
        assertEquals(false, x.isNonEmpty());

        x = x.update(1, generator(3));
        assertEquals(Integer.valueOf(3), x.get(1));
        assertSame(x, x.update(1, h -> h.get(8)));
        assertSame(map.deleteAll(), x.delete(1));

        assertEquals(false, x.isEmpty());
        assertEquals(true, x.isNonEmpty());

        x = x.update(1, generator(4));
        assertEquals(Integer.valueOf(31), x.get(1));

        x = x.update(2, generator(7));
        assertEquals(Integer.valueOf(7), x.get(2));
        assertSame(x, x.update(2, h -> h.get(8)));

        x = x.update(2, generator(4));
        assertEquals(Integer.valueOf(71), x.get(2));

        assertSame(x, x.delete(-1));

        verifyForEach(map.deleteAll());
        verifyReduce(map.deleteAll());
        verifySelectReject(map.deleteAll());
    }

    public static <K, V> void verifyEmptyEnumeration(@Nonnull IMap<K, V> map)
    {
        verifyOrderedUsingCollection(Collections.emptyList(), map);
    }

    public static <K, V> void verifyEnumeration(@Nonnull Map<K, V> expectedMap,
                                                @Nonnull IMap<K, V> map)
    {
        verifyOrderedUsingCollection(expectedMap.entrySet(), map, entries());
        verifyOrderedUsingCollection(expectedMap.keySet(), map.keys());
        verifyOrderedUsingCollection(expectedMap.values(), map.values());
        final Map<K, V> proxy = map.getMap();
        verifyOrderedUsingCollection(expectedMap.entrySet(), proxy.entrySet());
        verifyOrderedUsingCollection(expectedMap.keySet(), proxy.keySet());
        verifyOrderedUsingCollection(expectedMap.values(), proxy.values());
        testCollector(map.assignAll(expectedMap), map);
    }

    public static <K, V> void verifyEnumeration(@Nonnull List<IMapEntry<K, V>> expectedEntries,
                                                @Nonnull IMap<K, V> map)
    {
        final List<K> expectedKeys = expectedEntries.stream().map(e -> e.getKey()).collect(Collectors.toList());
        final List<V> expectedValues = expectedEntries.stream().map(e -> e.getValue()).collect(Collectors.toList());
        verifyOrderedUsingCollection(expectedEntries, map);
        verifyOrderedUsingCollection(expectedKeys, map.keys());
        verifyOrderedUsingCollection(expectedValues, map.values());
        final Map<K, V> proxy = map.getMap();
        verifyOrderedUsingCollection(expectedEntries, proxy.entrySet(), reverseEntries());
        verifyOrderedUsingCollection(expectedKeys, proxy.keySet());
        verifyOrderedUsingCollection(expectedValues, proxy.values());
        testCollector(map.insertAll(expectedEntries), map);
    }

    public static <K, V> void verifyEnumeration(@Nonnull HashMap<K, V> expectedMap,
                                                @Nonnull IMap<K, V> map)
    {
        verifyUnorderedUsingCollection(expectedMap.entrySet(), map, entries());
        verifyUnorderedUsingCollection(expectedMap.keySet(), map.keys());
        verifyUnorderedUsingCollection(expectedMap.values(), map.values());
        final Map<K, V> proxy = map.getMap();
        verifyUnorderedUsingCollection(expectedMap.entrySet(), proxy.entrySet());
        verifyUnorderedUsingCollection(expectedMap.keySet(), proxy.keySet());
        verifyUnorderedUsingCollection(expectedMap.values(), proxy.values());
        testCollector(map.assignAll(expectedMap), map);
    }

    private static void verifyReduce(@Nonnull IMap<Integer, Integer> empty)
    {
        assertEquals(Integer.valueOf(0), empty.reduce(0, (s, k, v) -> s + k - v));

        final Integer expected = 2 * IntStream.range(1, 1001).sum();
        @Nonnull IMap<Integer, Integer> map = IntStream.range(1, 1001)
            .boxed()
            .map(i -> MapEntry.entry(i, -i))
            .collect(ICollectors.toMap());
        assertEquals(expected, map.reduce(0, (s, k, v) -> s + k - v));

        try {
            int x = empty.reduceThrows(0, (s, k, v) -> {
                if (k == 1000) {
                    throw new IOException();
                }
                return s + k - v;
            });
            assertEquals(0, x);
        } catch (IOException ex) {
            fail();
        }

        try {
            int x = map.reduceThrows(0, (s, k, v) -> {
                if (k == 1000) {
                    throw new IOException();
                }
                return s + k - v;
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
    }

    private static void verifyForEach(@Nonnull IMap<Integer, Integer> empty)
    {
        final int sum = IntStream.range(1, 1001).sum();
        @Nonnull IMap<Integer, Integer> map = IntStream.range(1, 1001)
            .boxed()
            .map(i -> MapEntry.entry(i, -i))
            .collect(ICollectors.toMap());
        MutableDelta delta = new MutableDelta();
        map.forEach((k, v) -> delta.add(k - v));
        assertEquals(2 * sum, delta.getValue());

        try {
            map.forEachThrows((k, v) -> {
                if (k == 1000) {
                    throw new IOException();
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
    }

    private static void verifySelectReject(@Nonnull IMap<Integer, Integer> empty)
    {
        final IMap<Integer, Integer> all = IntStream.range(1, 50)
            .boxed()
            .map(i -> MapEntry.entry(i, -i))
            .collect(ICollectors.toMap());

        final IMap<Integer, Integer> evens = all.stream()
            .filter(e -> e.getKey() % 2 == 0)
            .collect(ICollectors.toMap());

        final IMap<Integer, Integer> odds = all.stream()
            .filter(e -> e.getKey() % 2 == 1)
            .collect(ICollectors.toMap());

        assertEquals(evens, all.select((k, v) -> k % 2 == 0));
        assertEquals(odds, all.select((k, v) -> k % 2 == 1));

        assertEquals(odds, all.reject((k, v) -> k % 2 == 0));
        assertEquals(evens, all.reject((k, v) -> k % 2 == 1));
    }

    private static Func1<Maybe<Integer>, Integer> generator(int newValue)
    {
        return h -> h.isAbsent() ? newValue : h.unsafeGet() * 10 + 1;
    }

    private static <K, V> void testCollector(IMap<K, V> values,
                                             IMap<K, V> template)
    {
        IMap<K, V> expected = template.insertAll(values);
        IMap<K, V> actual = values.parallelStream().collect(ICollectors.toMap());
        assertEquals(expected, actual);
    }

    private static <K, V> Function<IMapEntry<K, V>, Map.Entry<K, V>> entries()
    {
        return MapEntry::new;
    }

    private static <K, V> Function<Map.Entry<K, V>, IMapEntry<K, V>> reverseEntries()
    {
        return MapEntry::new;
    }
}
