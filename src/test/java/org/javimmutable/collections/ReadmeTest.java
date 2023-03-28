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

package org.javimmutable.collections;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class ReadmeTest
{
    @Test
    public void creation()
    {
        List<String> sourceList = Arrays.asList("these", "are", "some", "strings");
        IList<String> empty = ILists.of();
        IList<String> aList = empty
            .insert("these")
            .insert("are")
            .insert("some")
            .insert("strings");
        IList<String> literal = ILists.of("these", "are", "some", "strings");
        IList<String> fromJavaList = ILists.allOf(sourceList);
        IList<String> fromBuilder = IBuilders.<String>list()
            .add("these")
            .add("are")
            .add("some", "strings")
            .build();
        assertThat(aList).isEqualTo(literal);
        assertThat(fromJavaList).isEqualTo(literal);
        assertThat(fromBuilder).isEqualTo(literal);

        int eWordCount = 0;
        for (String word : fromBuilder) {
            if (word.contains("e")) {
                eWordCount += 1;
            }
        }
        assertThat(eWordCount).isEqualTo(3);

        // use a stream to build a map of numbers to lists of the number's factors
        IMap<Integer, IList<Integer>> factorMap =
            IntStream.range(2, 100).boxed()
                .map(i -> IMapEntry.of(i, factorsOf(i)))
                .collect(ICollectors.toMap());

        // extract a list of prime numbers using a stream by filtering out numbers that have any factors 
        IList<Integer> primes = factorMap.stream()
            .filter(e -> e.getValue().isEmpty())
            .map(e -> e.getKey())
            .collect(ICollectors.toList());
        assertThat(primes)
            .isEqualTo(ILists.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97));

        // build a new list by selectively taking values from the primes list using transformSome() method instead of a stream
        IList<Integer> threes = primes.transformSome(ILists.of(), i -> i % 10 == 3 ? Holders.nullable(i) : Holder.none());
        assertThat(threes).isEqualTo(ILists.of(3, 13, 23, 43, 53, 73, 83));

        // transformSome() can also append to an existing list rather than an empty one
        IList<Integer> onesAndThrees = primes.transformSome(threes, i -> i % 10 == 1 ? Holders.nullable(i) : Holder.none());
        assertThat(onesAndThrees).isEqualTo(ILists.of(3, 13, 23, 43, 53, 73, 83, 11, 31, 41, 61, 71));
        // threes wasn't changed (it's immutable)
        assertThat(threes).isEqualTo(ILists.of(3, 13, 23, 43, 53, 73, 83));

        // you can easily grab sub-lists from a list
        assertThat(onesAndThrees.prefix(7)).isEqualTo(threes);
        assertThat(onesAndThrees.middle(3, 10)).isEqualTo(ILists.of(43, 53, 73, 83, 11, 31, 41));
    }

    @Test
    public void forEach()
    {
        ISet<Integer> numbers = IntStream.range(1, 20).boxed().collect(ICollectors.toSet());
        numbers.forEach(i -> System.out.println(i));

        numbers = numbers.reject(i -> i % 3 != 2);
        numbers.forEach(i -> System.out.println(i));
    }

    @Test
    public void filtering()
    {
        ISet<Integer> numbers = IntStream.range(1, 20).boxed().collect(ICollectors.toSet());
        ISet<Integer> changed = numbers.reject(i -> i % 3 != 2);
        assertThat(changed).isEqualTo(ISets.hashed(2, 5, 8, 11, 14, 17));
        changed = numbers.select(i -> i % 3 == 1);
        assertThat(changed).isEqualTo(ISets.hashed(1, 4, 7, 10, 13, 16, 19));
        IList<Integer> transformed = changed.collect(ILists.of());
        assertThat(transformed).isEqualTo(ILists.of(1, 4, 7, 10, 13, 16, 19));
    }

    @Test
    public void slicingAndDicing()
    {
        IList<Integer> numbers = IntStream.range(1, 21).boxed().collect(ICollectors.toList());
        IList<Integer> changed = numbers.prefix(6);
        assertThat(changed).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6));
        changed = numbers.suffix(16);
        assertThat(changed).isEqualTo(ILists.of(17, 18, 19, 20));
        changed = changed.insertAll(2, numbers.prefix(3).insertAllLast(numbers.middle(9, 12)));
        assertThat(changed).isEqualTo(ILists.of(17, 18, 1, 2, 3, 10, 11, 12, 19, 20));
    }

    @Test
    public void indexingWithSetMap()
    {
        IList<String> source = ILists.of("Now is our time.",
                                         "Our moment has arrived.",
                                         "Shall we embrace immutable collections?",
                                         "Or tread in dangerous synchronized waters forever?");
        ISetMap<String, String> index = source
            .stream()
            .flatMap(line -> Stream.of(line
                                           .toLowerCase()
                                           .replace(".", "")
                                           .replace("?", "")
                                           .split(" "))
                .map(word -> MapEntry.entry(word, line)))
            .collect(ICollectors.toSetMap());
        assertThat(index.get("our")).isEqualTo(ISets.hashed("Now is our time.", "Our moment has arrived."));
    }

    @Test
    public void listMaps()
    {
        IListMap<String, Integer> index = IListMap.<String, Integer>sortedListMap()
            .insert("c", 2)
            .insert("a", 1)
            .insert("d", 640)
            .insert("b", 3)
            .insert("d", 512)
            .insertAll("a", ILists.of(-4, 40, 18)); // could be any Iterable not just list
        // keys are sorted in the map
        assertThat(ILists.allOf(index.keys())).isEqualTo(ILists.of("a", "b", "c", "d"));
        // values appear in the list in order they are added
        assertThat(index.getList("a")).isEqualTo(ILists.of(1, -4, 40, 18));
        assertThat(index.getList("d")).isEqualTo(ILists.of(640, 512));
        assertThat(index.getList("x")).isEqualTo(ILists.of());
    }

    @Test
    public void changesInLoop()
    {
        assertThatThrownBy(() -> {
            Map<Integer, Integer> ints = IntStream.range(1, 11).boxed().collect(Collectors.toMap(i -> i, i -> i));
            for (Map.Entry<Integer, Integer> entry : ints.entrySet()) {
                ints.put(2 * entry.getKey(), 2 * entry.getValue());
            }
        }).isInstanceOf(ConcurrentModificationException.class);

        IMap<Integer, Integer> myMap = IntStream.range(1, 11).boxed().map(i -> IMapEntry.of(i, i)).collect(ICollectors.toMap());
        for (IMapEntry<Integer, Integer> entry : myMap) {
            myMap = myMap.assign(2 * entry.getKey(), 2 * entry.getValue());
        }
        assertThat(ILists.allOf(myMap.keys())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20));
        assertThat(ILists.allOf(myMap.values())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20));

        myMap = IntStream.range(1, 11).boxed().map(i -> IMapEntry.of(i, i)).collect(ICollectors.toMap());
        IMap<Integer, Integer> changed = myMap.stream()
            .map(entry -> IMapEntry.of(5 + entry.getKey(), 10 + entry.getValue()))
            .collect(myMap.mapCollector());
        // 6-10 were updated, 11-15 were added
        assertThat(ILists.allOf(changed.keys())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        assertThat(ILists.allOf(changed.values())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
        // original map is unchanged 
        assertThat(ILists.allOf(myMap.keys())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    private IList<Integer> factorsOf(int number)
    {
        final int maxPossibleFactor = (int)Math.sqrt(number);
        return IntStream.range(2, maxPossibleFactor + 1).boxed()
            .filter(candidate -> number % candidate == 0)
            .collect(ICollectors.toList());
    }
}
