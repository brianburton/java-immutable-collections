package org.javimmutable.collections;

import org.junit.Test;

import java.util.stream.IntStream;

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

    private JImmutableList<Integer> factorsOf(int number)
    {
        final int maxPossibleFactor = (int)Math.sqrt(number);
        return IntStream.range(2, maxPossibleFactor + 1).boxed()
            .filter(candidate -> number % candidate == 0)
            .collect(listCollector());
    }
}
