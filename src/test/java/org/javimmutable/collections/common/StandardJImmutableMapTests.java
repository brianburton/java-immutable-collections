package org.javimmutable.collections.common;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;

public class StandardJImmutableMapTests
{
    private static final Comparator<JImmutableMap.Entry<Integer, Integer>> entryComparator =
        Comparator.<JImmutableMap.Entry<Integer, Integer>>comparingInt(e -> e.getKey())
            .thenComparing(Comparator.comparingInt(e -> e.getValue()));
    private static final Comparator<Integer> intComparator = Comparator.comparingInt(i -> i);

    public static <K, V> void verifyEmptyEnumeration(@Nonnull JImmutableMap<K, V> map)
    {
        verifyEnumeration(Collections.emptyList(), map);
    }

    public static <K, V> void verifyEnumeration(@Nonnull Map<K, V> expectedMap,
                                                @Nonnull JImmutableMap<K, V> map)
    {
        final List<JImmutableMap.Entry<K, V>> expectedEntries = expectedMap.entrySet().stream().map(e -> MapEntry.of(e)).collect(Collectors.toList());
        verifyEnumeration(expectedEntries, map);
    }

    public static <K, V> void verifyEnumeration(@Nonnull List<JImmutableMap.Entry<K, V>> expectedEntries,
                                                @Nonnull JImmutableMap<K, V> map)
    {
        final List<K> expectedKeys = expectedEntries.stream().map(e -> e.getKey()).collect(Collectors.toList());
        final List<V> expectedValues = expectedEntries.stream().map(e -> e.getValue()).collect(Collectors.toList());

        // extract using a Stream
        List<JImmutableMap.Entry<K, V>> actualEntries = map.stream().collect(Collectors.toList());
        List<K> actualKeys = map.stream().map(e -> e.getKey()).collect(Collectors.toList());
        List<V> actualValues = map.stream().map(e -> e.getValue()).collect(Collectors.toList());
        assertEquals(expectedEntries, actualEntries);
        assertEquals(expectedKeys, actualKeys);
        assertEquals(expectedValues, actualValues);

        // extract using a parallel Stream
        actualEntries = map.parallelStream().collect(Collectors.toList());
        actualKeys = map.parallelStream().map(e -> e.getKey()).collect(Collectors.toList());
        actualValues = map.parallelStream().map(e -> e.getValue()).collect(Collectors.toList());
        assertEquals(expectedEntries, actualEntries);
        assertEquals(expectedKeys, actualKeys);
        assertEquals(expectedValues, actualValues);

        // extract keys and values using special Streamables
        actualKeys = map.keys().stream().collect(Collectors.toList());
        actualValues = map.values().stream().collect(Collectors.toList());
        assertEquals(expectedKeys, actualKeys);
        assertEquals(expectedValues, actualValues);

        // extract keys and values using special Streamables (parallel)
        actualKeys = map.keys().parallelStream().collect(Collectors.toList());
        actualValues = map.values().parallelStream().collect(Collectors.toList());
        assertEquals(expectedKeys, actualKeys);
        assertEquals(expectedValues, actualValues);

        // extract using a Cursor
        actualEntries = StandardCursor.makeList(map.cursor());
        actualKeys = StandardCursor.makeList(map.keysCursor());
        actualValues = StandardCursor.makeList(map.valuesCursor());
        assertEquals(expectedEntries, actualEntries);
        assertEquals(expectedKeys, actualKeys);
        assertEquals(expectedValues, actualValues);

        // extract using an Iterator
        actualEntries = StandardCursor.makeList(map.iterator());
        actualKeys = StandardCursor.makeList(map.keys().iterator());
        actualValues = StandardCursor.makeList(map.values().iterator());
        assertEquals(expectedEntries, actualEntries);
        assertEquals(expectedKeys, actualKeys);
        assertEquals(expectedValues, actualValues);
    }

    public static void verifyEnumeration(@Nonnull HashMap<Integer, Integer> hashed,
                                         @Nonnull JImmutableMap<Integer, Integer> map)
    {

        final List<JImmutableMap.Entry<Integer, Integer>> expectedEntries = hashed.entrySet().stream().map(e -> MapEntry.of(e)).collect(Collectors.toList());
        final List<Integer> expectedKeys = expectedEntries.stream().map(e -> e.getKey()).collect(Collectors.toList());
        final List<Integer> expectedValues = expectedEntries.stream().map(e -> e.getValue()).collect(Collectors.toList());
        expectedEntries.sort(entryComparator);
        expectedKeys.sort(intComparator);
        expectedValues.sort(intComparator);

        // extract using a Stream
        List<JImmutableMap.Entry<Integer, Integer>> actualEntries = map.stream().collect(Collectors.toList());
        List<Integer> actualKeys = map.stream().map(e -> e.getKey()).collect(Collectors.toList());
        List<Integer> actualValues = map.stream().map(e -> e.getValue()).collect(Collectors.toList());
        sortedEntryAssertEquals(expectedEntries, actualEntries);
        sortedAssertEquals(expectedKeys, actualKeys);
        sortedAssertEquals(expectedValues, actualValues);

        // extract using a parallel Stream
        actualEntries = map.parallelStream().collect(Collectors.toList());
        actualKeys = map.parallelStream().map(e -> e.getKey()).collect(Collectors.toList());
        actualValues = map.parallelStream().map(e -> e.getValue()).collect(Collectors.toList());
        sortedEntryAssertEquals(expectedEntries, actualEntries);
        sortedAssertEquals(expectedKeys, actualKeys);
        sortedAssertEquals(expectedValues, actualValues);

        // extract keys and values using special Streamables
        actualKeys = map.keys().stream().collect(Collectors.toList());
        actualValues = map.values().stream().collect(Collectors.toList());
        sortedAssertEquals(expectedKeys, actualKeys);
        sortedAssertEquals(expectedValues, actualValues);

        // extract keys and values using special Streamables (parallel)
        actualKeys = map.keys().parallelStream().collect(Collectors.toList());
        actualValues = map.values().parallelStream().collect(Collectors.toList());
        sortedAssertEquals(expectedKeys, actualKeys);
        sortedAssertEquals(expectedValues, actualValues);

        // extract using a Cursor
        actualEntries = StandardCursor.makeList(map.cursor());
        actualKeys = StandardCursor.makeList(map.keysCursor());
        actualValues = StandardCursor.makeList(map.valuesCursor());
        sortedEntryAssertEquals(expectedEntries, actualEntries);
        sortedAssertEquals(expectedKeys, actualKeys);
        sortedAssertEquals(expectedValues, actualValues);

        // extract using an Iterator
        actualEntries = StandardCursor.makeList(map.iterator());
        actualKeys = StandardCursor.makeList(map.keys().iterator());
        actualValues = StandardCursor.makeList(map.values().iterator());
        sortedEntryAssertEquals(expectedEntries, actualEntries);
        sortedAssertEquals(expectedKeys, actualKeys);
        sortedAssertEquals(expectedValues, actualValues);
    }

    private static void sortedAssertEquals(List<Integer> expected,
                                           List<Integer> actual)
    {
        actual.sort(intComparator);
        assertEquals(expected, actual);
    }

    private static void sortedEntryAssertEquals(List<JImmutableMap.Entry<Integer, Integer>> expected,
                                                List<JImmutableMap.Entry<Integer, Integer>> actual)
    {
        actual.sort(entryComparator);
        assertEquals(expected, actual);
    }
}
