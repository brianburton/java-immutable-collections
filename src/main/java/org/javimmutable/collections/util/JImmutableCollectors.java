package org.javimmutable.collections.util;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Utility class providing static methods for collecting various immutable collections using streams.
 * Unfortunately the Collector interface forces the collectors themselves to be mutable so all of these
 * methods first construct a mutable object and then transform it into an immutable one.
 */
public class JImmutableCollectors
{
    private JImmutableCollectors()
    {
    }

    public static <T> Collector<T, ?, JImmutableList<T>> list()
    {
        return Collectors.collectingAndThen(Collectors.toList(), collected -> JImmutables.list(collected));
    }

    public static <T> Collector<T, ?, JImmutableRandomAccessList<T>> ralist()
    {
        return Collectors.collectingAndThen(Collectors.toList(), collected -> JImmutables.ralist(collected));
    }

    public static <T> Collector<T, ?, JImmutableSet<T>> set()
    {
        return Collectors.collectingAndThen(Collectors.toSet(), collected -> JImmutables.set(collected));
    }

    public static <T extends Comparable<T>> Collector<T, ?, JImmutableSet<T>> sortedSet()
    {
        return Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<T>()), collected -> JImmutables.sortedSet(collected));
    }

    public static <T> Collector<T, ?, JImmutableSet<T>> sortedSet(@Nonnull Comparator<T> comparator)
    {
        return Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<T>(comparator)), collected -> JImmutables.sortedSet(comparator, collected));
    }

    public static <T, K> Collector<T, ?, JImmutableListMap<K, T>> groupingBy(@Nonnull Function<? super T, ? extends K> classifier)
    {
        return Collectors.collectingAndThen(Collectors.groupingBy(classifier), collected -> mapToListMap(JImmutables.listMap(), collected));
    }

    private static <K, T> JImmutableListMap<K, T> mapToListMap(JImmutableListMap<K, T> answer,
                                                               Map<? extends K, List<T>> collected)
    {
        for (Map.Entry<? extends K, List<T>> entry : collected.entrySet()) {
            answer = answer.assign(entry.getKey(), JImmutables.list(entry.getValue()));
        }
        return answer;
    }
}
