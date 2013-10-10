///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JImmutablesTest
        extends TestCase
{
    public void testStack()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);
        List<Integer> expected = Arrays.asList(3, 2, 1);

        JImmutableStack<Integer> stack = JImmutables.stack();
        stack = stack.insert(1).insert(2).insert(3);
        assertEquals(expected, JImmutables.list(stack.cursor()).getList());

        JImmutableList<Integer> inlist = JImmutableArrayList.of();
        inlist = inlist.insert(1).insert(2).insert(3);
        assertEquals(stack, JImmutables.stack(inlist));
        assertEquals(stack, JImmutables.stack((inlist.cursor())));
        assertEquals(stack, JImmutables.stack(input));
        assertEquals(stack, JImmutables.stack(input.iterator()));
        assertEquals(stack, JImmutables.stack(1, 2, 3));
    }

    public void testList()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);

        JImmutableList<Integer> list = JImmutables.list(input);
        assertEquals(input, list.getList());
        assertEquals(list, JImmutables.list(input.iterator()));
        assertEquals(list, JImmutables.list(list));
        assertEquals(list, JImmutables.list(list.cursor()));
        assertEquals(list, JImmutables.list(1, 2, 3));
    }

    public void testRandomAccessList()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);

        JImmutableRandomAccessList<Integer> list = JImmutables.ralist(input);
        assertEquals(input, list.getList());
        assertEquals(list, JImmutables.ralist(input.iterator()));
        assertEquals(list, JImmutables.ralist(list));
        assertEquals(list, JImmutables.ralist(list.cursor()));
        assertEquals(list, JImmutables.ralist(1, 2, 3));
    }

    public void testMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        JImmutableMap<Integer, Integer> map = JImmutables.map(input);
        assertEquals(input, map.getMap());
        assertSame(map, JImmutables.map(map));
        assertEquals(map, JImmutables.map(map));
    }

    public void testSortedMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        JImmutableMap<Integer, Integer> map = JImmutables.sortedMap(input);
        assertEquals(input, map.getMap());
        assertSame(map, JImmutables.sortedMap(map));
        assertEquals(map, JImmutables.map(map));

        JImmutableMap<Integer, Integer> map2 = JImmutables.sortedMap(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return -b.compareTo(a);
            }
        }, input);
        assertEquals(input, map2.getMap());
        assertEquals(map, JImmutables.sortedMap(map2));
        assertNotSame(map, JImmutables.sortedMap(map2));
        assertEquals(map, JImmutables.map(map2));
        assertNotSame(map, JImmutables.map(map2));
    }

    public void testSet()
    {
        List<Integer> input = Arrays.asList(1, 87, 100, 1, 45);

        JImmutableSet<Integer> set = JImmutables.set(input);
        assertEquals(new HashSet<Integer>(input), set.getSet());
        assertEquals(set, JImmutables.set(input.iterator()));
        assertEquals(set, JImmutables.set(set));
        assertEquals(set, JImmutables.set(set.cursor()));
        assertEquals(set, JImmutables.set(1, 100, 45, 87, 1));
    }

    public void testSortedSet()
    {
        List<Integer> input = Arrays.asList(1, 87, 100, 1, 45);

        JImmutableSet<Integer> set = JImmutables.sortedSet(input);
        assertEquals(new HashSet<Integer>(input), set.getSet());
        assertEquals(set, JImmutables.sortedSet(input.iterator()));
        assertEquals(set, JImmutables.sortedSet(set));
        assertEquals(set, JImmutables.sortedSet(set.cursor()));
        assertEquals(set, JImmutables.sortedSet(1, 100, 45, 87, 1));
        Cursors.areEqual(set.cursor(), JImmutables.list(Arrays.asList(1, 45, 87, 100).iterator()).cursor());

        Comparator<Integer> reverser = new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return -a.compareTo(b);
            }
        };
        set = JImmutables.sortedSet(reverser, input);
        assertEquals(new HashSet<Integer>(input), set.getSet());
        assertEquals(set, JImmutables.sortedSet(reverser, input.iterator()));
        assertEquals(set, JImmutables.sortedSet(reverser, set));
        assertEquals(set, JImmutables.sortedSet(reverser, set.cursor()));
        assertEquals(set, JImmutables.sortedSet(reverser, 1, 100, 45, 87, 1));
        Cursors.areEqual(set.cursor(), JImmutables.list(Arrays.asList(100, 87, 45, 1).iterator()).cursor());
    }

    private void assertEquals(int expected,
                              Integer actual)
    {
        assertEquals(expected, (int)actual);
    }

    public void testListTutorialCode()
    {
        JImmutableList<Integer> list = JImmutables.list();
        list = list.insert(10).insert(20).insert(30);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));

        JImmutableList<Integer> changed = list.deleteLast().insert(45);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
        assertEquals(10, changed.get(0));
        assertEquals(20, changed.get(1));
        assertEquals(45, changed.get(2));

        assertEquals(Arrays.asList(10, 20, 30), list.getList());
        assertEquals(Arrays.asList(10, 20, 45), changed.getList());

        JImmutableRandomAccessList<Integer> ralist = JImmutables.ralist();
        ralist = ralist.insert(30).insert(0, 20).insert(0, 10);
        assertEquals(10, ralist.get(0));
        assertEquals(20, ralist.get(1));
        assertEquals(30, ralist.get(2));
        JImmutableRandomAccessList<Integer> ralist2 = ralist;
        ralist2 = ralist2.delete(1).insert(1, 87);
        assertEquals(10, ralist.get(0));
        assertEquals(20, ralist.get(1));
        assertEquals(30, ralist.get(2));
        assertEquals(10, ralist2.get(0));
        assertEquals(87, ralist2.get(1));
        assertEquals(30, ralist2.get(2));
        assertEquals(Arrays.asList(10, 20, 30), ralist.getList());
        assertEquals(Arrays.asList(10, 87, 30), ralist2.getList());
    }

    public void testMapTutorialCode()
    {
        JImmutableMap<Integer, Integer> hmap = JImmutables.map();
        hmap = hmap.assign(10, 11).assign(20, 21).assign(30, 31).assign(20, 19);

        JImmutableMap<Integer, Integer> hmap2 = hmap.delete(20).assign(18, 19);

        assertEquals(11, hmap.get(10));
        assertEquals(19, hmap.get(20));
        assertEquals(31, hmap.get(30));

        assertEquals(11, hmap2.get(10));
        assertEquals(19, hmap2.get(18));
        assertEquals(null, hmap2.get(20));
        assertEquals(31, hmap2.get(30));

        hmap2 = hmap2.assign(80, null);
        assertEquals(null, hmap2.get(20));
        assertEquals(true, hmap2.find(20).isEmpty());
        // hmap2.find(20).getValue() would throw since the Holder is empty

        assertEquals(null, hmap2.get(80));
        assertEquals(false, hmap2.find(80).isEmpty());
        assertEquals(null, hmap2.find(80).getValue());

        JImmutableMap<Integer, Integer> smap = JImmutables.sortedMap();
        smap = smap.assign(10, 80).assign(20, 21).assign(30, 31).assign(20, 19);
        assertEquals(Arrays.asList(10, 20, 30), new ArrayList<Integer>(smap.getMap().keySet()));
        assertEquals(Arrays.asList(80, 19, 31), new ArrayList<Integer>(smap.getMap().values()));
    }
}
