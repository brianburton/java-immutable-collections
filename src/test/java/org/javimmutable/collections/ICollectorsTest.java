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

import junit.framework.TestCase;
import org.javimmutable.collections.tree.ComparableComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ICollectorsTest
    extends TestCase
{
    public void testCollections()
    {
        final List<Integer> source = IntStream.rangeClosed(1, 1200).boxed().collect(Collectors.toList());
        final Comparator<Integer> comparator = ComparableComparator.of();
        Collections.shuffle(source);
        verifyCollection(source, values -> IArrays.allOf(values), () -> ICollectors.toArray());
        verifyCollection(source, values -> IDeques.allOf(values), () -> ICollectors.toDeque());
        verifyCollection(source, values -> ILists.allOf(values), () -> ICollectors.toList());
        verifyCollection(source, values -> ISets.hashed(values), () -> ICollectors.toSet());
        verifyCollection(source, values -> ISets.sorted(values), () -> ICollectors.toSortedSet());
        verifyCollection(source, values -> ISets.sorted(comparator, values), () -> ICollectors.toSortedSet(comparator));
        verifyCollection(source, values -> createGroupingByExpected(values, x -> x / 7), () -> ICollectors.groupingBy(x -> x / 7));
    }

    private IListMap<Integer, Integer> createGroupingByExpected(List<Integer> source,
                                                                Func1<Integer, Integer> keyTransform)
    {
        IListMap<Integer, Integer> expected = IListMaps.hashed();
        for (Integer value : source) {
            final Integer key = keyTransform.apply(value);
            expected = expected.insert(key, value);
        }
        return expected;
    }

    private <C> void verifyCollection(List<Integer> source,
                                      Func1<List<Integer>, C> collectionFactory,
                                      Func0<Collector<Integer, ?, C>> collectorFactory)
    {
        C expected = collectionFactory.apply(source);
        C actual = source.stream().collect(collectorFactory.apply());
        assertEquals(expected, actual);
        actual = source.parallelStream().collect(collectorFactory.apply());
        assertEquals(expected, actual);
    }
}
