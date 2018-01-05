///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static junit.framework.Assert.*;
import static org.javimmutable.collections.common.StandardIterableStreamableTests.*;

public class StandardJImmutableMapTests
{
    public static void verifyMiscellaneous(@Nonnull JImmutableMap<Integer, Integer> map)
    {
        final Func1<Integer, Integer> updater = x -> 10 * x + 1;

        JImmutableMap<Integer, Integer> x = map.deleteAll();

        x = x.update(1, 3, updater);
        assertEquals(Integer.valueOf(3), x.get(1));
        assertSame(x, x.update(1, 8, v -> v));

        x = x.update(1, 4, updater);
        assertEquals(Integer.valueOf(31), x.get(1));

        x = x.update(2, () -> 7, updater);
        assertEquals(Integer.valueOf(7), x.get(2));
        assertSame(x, x.update(2, 8, v -> v));

        x = x.update(2, 4, updater);
        assertEquals(Integer.valueOf(71), x.get(2));
    }

    public static <K, V> void verifyEmptyEnumeration(@Nonnull JImmutableMap<K, V> map)
    {
        verifyOrderedUsingCollection(Collections.emptyList(), map);
    }

    public static <K, V> void verifyEnumeration(@Nonnull Map<K, V> expectedMap,
                                                @Nonnull JImmutableMap<K, V> map)
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

    public static <K, V> void verifyEnumeration(@Nonnull List<JImmutableMap.Entry<K, V>> expectedEntries,
                                                @Nonnull JImmutableMap<K, V> map)
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
                                                @Nonnull JImmutableMap<K, V> map)
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

    private static <K, V> void testCollector(JImmutableMap<K, V> values,
                                             JImmutableMap<K, V> template)
    {
        JImmutableMap<K, V> expected = template.insertAll(values);
        JImmutableMap<K, V> actual = values.parallelStream().collect(template.mapCollector());
        assertEquals(expected, actual);
    }

    private static <K, V> Function<JImmutableMap.Entry<K, V>, Map.Entry<K, V>> entries()
    {
        return ie -> MapEntry.of(ie);
    }

    private static <K, V> Function<Map.Entry<K, V>, JImmutableMap.Entry<K, V>> reverseEntries()
    {
        return ie -> MapEntry.of(ie);
    }
}
