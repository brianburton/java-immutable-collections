///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import java.util.Arrays;
import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.ILists;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IStack;
import org.javimmutable.collections.IStacks;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.JImmutableTreeMap;

public class FunctionsTest
    extends TestCase
{
    public void testFoldLeft()
    {
        IStack<Integer> list = IStacks.of(1, 2, 3);
        assertEquals(17, (int)Functions.<Integer, Integer>foldLeft(0, list.iterator(), (accumulator, value) -> 2 * accumulator + value));
    }

    public void testFoldRight()
    {
        IStack<Integer> list = IStacks.of(1, 2, 3);
        assertEquals(11, (int)Functions.<Integer, Integer>foldRight(0, list.iterator(), (accumulator, value) -> 2 * accumulator + value));
    }

    public void testReverse()
    {
        StandardIteratorTests.listIteratorTest(Arrays.asList(1), Functions.reverse(ILists.of(1).iterator()));
        StandardIteratorTests.listIteratorTest(Arrays.asList(3, 2, 1), Functions.reverse(ILists.of(1, 2, 3).iterator()));
    }

    public void testCollectAll()
    {
        IList<Integer> expected = ILists.of(2, 3, 4);
        IList<Integer> list = ILists.of(1, 2, 3);
        assertEquals(expected, Functions.collectAll(list.iterator(), ILists.of(), value -> value + 1));
    }

    public void testCollectSome()
    {
        IList<Integer> expected = ILists.of(2, 4);
        IList<Integer> list = ILists.of(1, 2, 3);
        assertEquals(expected, Functions.collectSome(list.iterator(), ILists.of(), value -> {
            if (value % 2 == 0) {
                return Holder.none();
            } else {
                return Holder.maybe(value + 1);
            }
        }));
    }

    public void testFind()
    {
        Func1<Integer, Boolean> func = value -> value % 2 == 0;

        IList<Integer> list = ILists.of(1, 2, 3, 4);
        assertEquals(Holder.maybe(2), Functions.find(list.iterator(), func));

        list = ILists.of(1, 5, 7);
        assertEquals(Holder.<Integer>none(), Functions.find(list.iterator(), func));
    }

    public void testReject()
    {
        Func1<Integer, Boolean> func = value -> value % 2 == 0;

        IList<Integer> list = ILists.of(1, 2, 3, 4);
        IList<Integer> expected = ILists.of(1, 3);
        assertEquals(expected, Functions.reject(list.iterator(), ILists.of(), func));
        list = ILists.of(1, 5, 7);
        assertEquals(list, Functions.reject(list.iterator(), ILists.of(), func));
        list = ILists.of(2, 6, 12);
        expected = ILists.of();
        assertEquals(expected, Functions.reject(list.iterator(), ILists.of(), func));
    }

    public void testSelect()
    {
        Func1<Integer, Boolean> func = value -> value % 2 == 0;

        IList<Integer> list = ILists.of(1, 2, 3, 4);
        IList<Integer> expected = ILists.of(2, 4);
        assertEquals(expected, Functions.select(list.iterator(), ILists.of(), func));
        list = ILists.of(2, 6, 12);
        assertEquals(list, Functions.select(list.iterator(), ILists.of(), func));
        list = ILists.of(1, 5, 7);
        expected = ILists.of();
        assertEquals(expected, Functions.select(list.iterator(), ILists.of(), func));
    }
    
    public void testAssignAll()
    {
        final IMap<String, String> expected = JImmutableTreeMap.<String, String>of().assign("a", "A").assign("b", "B");
        assertEquals(expected, Functions.assignAll(JImmutableTreeMap.of(), expected));
        assertEquals(expected, Functions.assignAll(JImmutableTreeMap.of(), expected.getMap()));
    }
}
