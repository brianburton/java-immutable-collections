///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ReadmeTest
{
    @Test
    public void streams()
    {
        IList<String> source = ILists.of("axle", "wheel", "apple", "wall");
        ISet<String> copied = source.stream().collect(ICollectors.toSet());
        assertEquals(ISets.hashed("axle", "wheel", "apple", "wall"), copied);
    }

    @Test
    public void reduce()
    {
        IList<Integer> values = ILists.of(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(36, (int)values.reduce(0, (s, x) -> s + x));        // 0 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8
        assertEquals(-18, (int)values.reduce(18, (s, x) -> s - x));      // 18 - 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8
        assertEquals(16, (int)values.reduce(0, (s, x) -> s + (x / 2)));  // 0 + 0 + 1 + 1 + 2 + 2 + 3 + 3 + 4
    }

    @Test
    public void listTutorial()
    {
        {
            IList<Integer> list = ILists.of();
            list = list.insert(10).insert(20).insert(30);
            assertEquals(Integer.valueOf(10), list.get(0));
            assertEquals(Integer.valueOf(20), list.get(1));
            assertEquals(Integer.valueOf(30), list.get(2));

            IList<Integer> changed = list.deleteLast().insert(45);
            assertEquals(Integer.valueOf(10), list.get(0));
            assertEquals(Integer.valueOf(20), list.get(1));
            assertEquals(Integer.valueOf(30), list.get(2));
            assertEquals(Integer.valueOf(10), changed.get(0));
            assertEquals(Integer.valueOf(20), changed.get(1));
            assertEquals(Integer.valueOf(45), changed.get(2));

            assertEquals(Arrays.asList(10, 20, 30), list.getList());
            assertEquals(Arrays.asList(10, 20, 45), changed.getList());
        }
        {
            IList<Integer> list = ILists.of();
            list = list.insert(30).insert(0, 20).insert(0, 10);
            assertEquals(Arrays.asList(10, 20, 30), list.getList());

            IList<Integer> list2 = list.delete(1).insert(1, 87);
            assertEquals(Arrays.asList(10, 20, 30), list.getList());
            assertEquals(Arrays.asList(10, 87, 30), list2.getList());
        }
        {
            IList<String> source = ILists.of("able", "baker", "charlie", "delta", "echo");
            assertEquals(ILists.of("baker", "charlie"), source.select(str -> str.contains("r")));
            assertEquals(ILists.of("able", "baker", "delta"), source.reject(str -> str.contains("h")));
            assertEquals("ablebakercharliedeltaecho", source.reduce("", (answer, str) -> answer + str));
            assertEquals(ILists.of("baker", "charlie"),
                         source.stream()
                             .filter(str -> str.contains("r"))
                             .collect(ICollectors.toList()));
        }
    }

    @Test
    public void mapTutorial()
    {
        {
            IMap<Integer, Integer> hmap = IMaps.hashed();
            hmap = hmap.assign(10, 11).assign(20, 21).assign(30, 31).assign(20, 19);

            IMap<Integer, Integer> hmap2 = hmap.delete(20).assign(18, 19);

            assertEquals(Integer.valueOf(11), hmap.get(10));
            assertEquals(Integer.valueOf(19), hmap.get(20));
            assertEquals(Integer.valueOf(31), hmap.get(30));

            assertEquals(Integer.valueOf(11), hmap2.get(10));
            assertEquals(Integer.valueOf(19), hmap2.get(18));
            assertEquals(null, hmap2.get(20));
            assertEquals(Integer.valueOf(31), hmap2.get(30));

            // find

            hmap2 = hmap2.assign(80, null);
            assertEquals(null, hmap2.get(20));
            assertEquals(true, hmap2.find(20).isEmpty());
            // since the Maybe is empty we get the default value
            assertEquals(Integer.valueOf(-1), hmap2.find(20).get(-1));

            assertEquals(null, hmap2.get(80));
            assertEquals(false, hmap2.find(80).isEmpty());
            // the Maybe is full and value is null
            assertEquals(null, hmap2.find(80).get(-1));
        }

        // sorted
        {
            IMap<Integer, Integer> smap = IMaps.sorted();
            smap = smap.assign(30, 31).assign(20, 21).assign(20, 19).assign(10, 80);
            assertEquals(Arrays.asList(10, 20, 30), new ArrayList<>(smap.getMap().keySet()));
            assertEquals(Arrays.asList(80, 19, 31), new ArrayList<>(smap.getMap().values()));
        }

        // ordered
        {
            IMap<Integer, Integer> omap = IMaps.ordered();
            omap = omap.assign(30, 31).assign(20, 21).assign(10, 80).assign(20, 19);
            assertEquals(Arrays.asList(30, 20, 10), new ArrayList<>(omap.getMap().keySet()));
            assertEquals(Arrays.asList(31, 19, 80), new ArrayList<>(omap.getMap().values()));
        }
    }

    @Test
    public void arrayTutorial()
    {
        {
            IArray<String> array = IArrays.of();
            array = array.assign(25000, "charlie");
            array = array.assign(0, "baker");
            array = array.assign(-50000, "able");
            assertEquals("baker", array.get(0));
        }
        {
            IArray<String> array = IArrays.<String>of()
                .assign(25000, "charlie")
                .assign(-50000, "able")
                .assign(0, "baker");
            assertEquals(-25000, array.keys().stream().mapToInt(i -> i).sum());
            assertEquals("ab,ba,ch", array.values().stream().map(x -> x.substring(0, 2)).collect(Collectors.joining(",")));
        }
    }

    public String callWebService(String host,
                                 int port)
        throws IOException
    {
        return "";
    }

    public IList<String> extractHouseAddresses(String webServiceResult)
        throws ParseException
    {
        return ILists.of();
    }

    public IList<BigDecimal> lookupHouseValues(IList<String> houseAddresses)
        throws IOException
    {
        return ILists.of();
    }

    @Test
    public void tropes()
    {
        IMap<String, String> envVars = IMaps.hashed(System.getenv());
        int port = envVars.find("PORT").map(Integer::parseInt).get(80);

        Result<BigDecimal> totalValue =
            Result.attempt(() -> callWebService("some-host", 443))
                .map(resultJson -> extractHouseAddresses(resultJson))
                .map(addresses -> lookupHouseValues(addresses))
                .map(values -> values.reduce(BigDecimal.ZERO, BigDecimal::add));
    }

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
        IList<String> fromBuilder = ILists.<String>builder()
            .add("these")
            .add("are")
            .addAll("some", "strings")
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
            .isEqualTo(ILists.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
                                 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97));

        // you can easily grab sub-lists from a list
        assertThat(primes.prefix(7)).isEqualTo(ILists.of(2, 3, 5, 7, 11, 13, 17));
        assertThat(primes.middle(3, 10)).isEqualTo(ILists.of(7, 11, 13, 17, 19, 23, 29));
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

        IDeque<Integer> transformed = changed.stream().collect(ICollectors.toDeque());
        assertThat(transformed).isEqualTo(IDeques.of(1, 4, 7, 10, 13, 16, 19));
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
        IListMap<String, Integer> index = IListMaps.<String, Integer>sorted()
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
