package org.javimmutable.collections;

import org.javimmutable.collections.util.JImmutables;
import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.util.JImmutables.*;

public class ReadmeTest
{
    @Test
    public void creation()
    {
        // use a stream to build a map of numbers to lists of the number's factors
        JImmutableMap<Integer, JImmutableList<Integer>> factorMap =
            IntStream.range(2, 100).boxed()
                .map(i -> MapEntry.of(i, factorsOf(i)))
                .collect(mapCollector());

        // extract a list of prime numbers using a stream by filtering out numbers that have any factors 
        JImmutableList<Integer> primes = factorMap.stream()
            .filter(e -> e.getValue().isEmpty())
            .map(e -> e.getKey())
            .collect(listCollector());
        assertThat(primes)
            .isEqualTo(list(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97));

        // build a new list by selectively taking values from the primes list using transformSome() method instead of a stream
        JImmutableList<Integer> threes = primes.transformSome(list(), i -> i % 10 == 3 ? Holders.of(i) : Holders.of());
        assertThat(threes).isEqualTo(list(3, 13, 23, 43, 53, 73, 83));

        // transformSome() can also append to an existing list rather than an empty one
        JImmutableList<Integer> onesAndThrees = primes.transformSome(threes, i -> i % 10 == 1 ? Holders.of(i) : Holders.of());
        assertThat(onesAndThrees).isEqualTo(list(3, 13, 23, 43, 53, 73, 83, 11, 31, 41, 61, 71));
        // threes wasn't changed (it's immutable)
        assertThat(threes).isEqualTo(list(3, 13, 23, 43, 53, 73, 83));

        // you can easily grab sub-lists from a list
        assertThat(onesAndThrees.prefix(7)).isEqualTo(threes);
        assertThat(onesAndThrees.middle(3, 10)).isEqualTo(list(43, 53, 73, 83, 11, 31, 41));
    }

    @Test
    public void forEach()
    {
        JImmutableSet<Integer> numbers = IntStream.range(1, 20).boxed().collect(setCollector());
        numbers.forEach(i -> System.out.println(i));

        numbers = numbers.reject(i -> i % 3 != 2);
        numbers.forEach(i -> System.out.println(i));
    }

    @Test
    public void filtering()
    {
        JImmutableSet<Integer> numbers = IntStream.range(1, 20).boxed().collect(setCollector());
        JImmutableSet<Integer> changed = numbers.reject(i -> i % 3 != 2);
        assertThat(changed).isEqualTo(set(2, 5, 8, 11, 14, 17));
        changed = numbers.select(i -> i % 3 == 1);
        assertThat(changed).isEqualTo(set(1, 4, 7, 10, 13, 16, 19));
        JImmutableList<Integer> transformed = changed.collect(list());
        assertThat(transformed).isEqualTo(list(1, 4, 7, 10, 13, 16, 19));
    }

    @Test
    public void slicingAndDicing()
    {
        JImmutableList<Integer> numbers = IntStream.range(1, 21).boxed().collect(listCollector());
        JImmutableList<Integer> changed = numbers.prefix(6);
        assertThat(changed).isEqualTo(list(1, 2, 3, 4, 5, 6));
        changed = numbers.suffix(16);
        assertThat(changed).isEqualTo(list(17, 18, 19, 20));
        changed = changed.insertAll(2, numbers.prefix(3).insertAllLast(numbers.middle(9, 12)));
        assertThat(changed).isEqualTo(list(17, 18, 1, 2, 3, 10, 11, 12, 19, 20));
    }

    @Test
    public void indexingWithSetMap()
    {
        JImmutableList<String> source = list("Now is our time.",
                                             "Our moment has arrived.",
                                             "Shall we embrace immutable collections?",
                                             "Or tread in dangerous synchronized waters forever?");
        JImmutableSetMap<String, String> index = source
            .stream()
            .flatMap(line -> Stream.of(line
                                           .toLowerCase()
                                           .replace(".", "")
                                           .replace("?", "")
                                           .split(" "))
                .map(word -> MapEntry.entry(word, line)))
            .collect(setMapCollector());
        assertThat(index.get("our")).isEqualTo(set("Now is our time.", "Our moment has arrived."));
    }

    @Test
    public void listMaps()
    {
        JImmutableListMap<String, Integer> index = JImmutables.<String, Integer>sortedListMap()
            .insert("c", 2)
            .insert("a", 1)
            .insert("d", 640)
            .insert("b", 3)
            .insert("d", 512)
            .insertAll("a", list(-4, 40, 18)); // could be any Iterable not just list
        // keys are sorted in the map
        assertThat(list(index.keys())).isEqualTo(list("a", "b", "c", "d"));
        // values appear in the list in order they are added
        assertThat(index.getList("a")).isEqualTo(list(1, -4, 40, 18));
        assertThat(index.getList("d")).isEqualTo(list(640, 512));
        assertThat(index.getList("x")).isEqualTo(list());
    }

    private JImmutableList<Integer> factorsOf(int number)
    {
        final int maxPossibleFactor = (int)Math.sqrt(number);
        return IntStream.range(2, maxPossibleFactor + 1).boxed()
            .filter(candidate -> number % candidate == 0)
            .collect(listCollector());
    }
}
