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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.PersistentRandomAccessList;
import org.javimmutable.collections.PersistentStack;
import org.javimmutable.collections.list.PersistentArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImmutablesTest
        extends TestCase
{
    public void testStack()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);
        List<Integer> expected = Arrays.asList(3, 2, 1);

        PersistentStack<Integer> stack = Immutables.stack();
        stack = stack.add(1).add(2).add(3);
        assertEquals(expected, Immutables.list(stack.cursor()).asList());

        PersistentList<Integer> inlist = PersistentArrayList.of();
        inlist = inlist.add(1).add(2).add(3);
        assertEquals(stack, Immutables.stack((Cursorable<Integer>)inlist));
        assertEquals(stack, Immutables.stack((inlist.cursor())));
        assertEquals(stack, Immutables.stack(input));
        assertEquals(stack, Immutables.stack(input.iterator()));
    }

    public void testList()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);

        PersistentList<Integer> list = Immutables.list(input);
        assertEquals(input, list.asList());
        assertEquals(list, Immutables.list(input.iterator()));
        assertEquals(list, Immutables.list((Cursorable<Integer>)list));
        assertEquals(list, Immutables.list(list.cursor()));
    }

    public void testRandomAccessList()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);

        PersistentRandomAccessList<Integer> list = Immutables.ralist(input);
        assertEquals(input, list.asList());
        assertEquals(list, Immutables.ralist(input.iterator()));
        assertEquals(list, Immutables.ralist((Cursorable<Integer>)list));
        assertEquals(list, Immutables.ralist(list.cursor()));
    }

    public void testMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        PersistentMap<Integer, Integer> map = Immutables.map(input);
        assertEquals(input, map.asMap());
        assertEquals(map, Immutables.map(map));
        assertEquals(map, Immutables.map(map));
    }

    public void testSortedMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        PersistentMap<Integer, Integer> map = Immutables.sortedMap(input);
        assertEquals(input, map.asMap());
        assertEquals(map, Immutables.sortedMap(map));
        assertEquals(map, Immutables.map(map));
    }
}
