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

package org.javimmutable.collection.tree;

import junit.framework.TestCase;
import org.javimmutable.collection.IMap;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.MapEntry;
import org.javimmutable.collection.common.MapBuilderTestAdapter;
import org.javimmutable.collection.common.StandardBuilderTests;
import org.javimmutable.collection.common.TestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TreeMapBuilderTest
    extends TestCase
{
    public void testBuilding()
    {
        TreeMapBuilder<Integer, String> builder = new TreeMapBuilder<>(ComparableComparator.<Integer>of());
        IMap<Integer, String> map = builder.build();
        map.checkInvariants();
        assertEquals(0, map.size());

        final List<Integer> keys = new ArrayList<>();
        final Map<Integer, String> expected = new TreeMap<>();
        for (int i = 1; i <= 512; ++i) {
            expected.put(i, String.valueOf(i));
            keys.add(i);
            Collections.shuffle(keys);
            builder = new TreeMapBuilder<>(ComparableComparator.<Integer>of());
            for (Integer key : keys) {
                builder.add(key, String.valueOf(key));
            }
            map = builder.build();
            map.checkInvariants();
            assertEquals(map, builder.build());
            assertEquals(TestUtil.makeList(expected.keySet()), TestUtil.makeList(map.keys()));
            assertEquals(TestUtil.makeList(expected.values()), TestUtil.makeList(map.values()));
        }
    }

    public void testStandard()
        throws InterruptedException
    {
        final List<IMapEntry<Integer, Integer>> values = new ArrayList<>();
        for (int i = 1; i <= 5000; ++i) {
            values.add(IMapEntry.of(i, 5001 - i));
        }
        Collections.shuffle(values);
        StandardBuilderTests.verifyBuilder(values, this::stdBuilderTestAdaptor, this::stdBuilderTestComparator, new IMapEntry[0]);
        values.sort(MapEntry::compareKeys);
        StandardBuilderTests.verifyThreadSafety(values, MapEntry::compareKeys, this::stdBuilderTestAdaptor, a -> a);
    }

    private MapBuilderTestAdapter<Integer, Integer> stdBuilderTestAdaptor()
    {
        return new MapBuilderTestAdapter<>(new TreeMapBuilder<>(ComparableComparator.<Integer>of()));
    }

    private Boolean stdBuilderTestComparator(List<IMapEntry<Integer, Integer>> expected,
                                             IMap<Integer, Integer> actual)
    {
        List<IMapEntry<Integer, Integer>> sorted = new ArrayList<>(expected);
        sorted.sort(MapEntry::compareKeys);
        assertEquals(sorted, actual.stream().collect(Collectors.toList()));
        return true;
    }
}
