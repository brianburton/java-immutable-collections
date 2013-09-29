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
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableStack;

public class FunctionsTest
        extends TestCase
{
    public void testFoldLeft()
    {
        JImmutableStack<Integer> list = JImmutables.stack(1, 2, 3);
        assertEquals(17, (int)Functions.<Integer, Integer>foldLeft(0, list.cursor(), new Func2<Integer, Integer, Integer>()
        {
            @Override
            public Integer apply(Integer accumulator,
                                 Integer value)
            {
                return 2 * accumulator + value;
            }
        }));
    }

    public void testFoldRight()
    {
        JImmutableStack<Integer> list = JImmutables.stack(1, 2, 3);
        assertEquals(11, (int)Functions.<Integer, Integer>foldRight(0, list.cursor(), new Func2<Integer, Integer, Integer>()
        {
            @Override
            public Integer apply(Integer accumulator,
                                 Integer value)
            {
                return 2 * accumulator + value;
            }
        }));
    }

    public void testCollectAll()
    {
        JImmutableList<Integer> expected = JImmutables.list(2, 3, 4);
        JImmutableList<Integer> list = JImmutables.list(1, 2, 3);
        assertEquals(expected, Functions.<Integer, Integer>collectAll(list.cursor(), JImmutables.<Integer>list(), new Func1<Integer, Integer>()
        {
            @Override
            public Integer apply(Integer value)
            {
                return value + 1;
            }
        }));
    }

    public void testCollectSome()
    {
        JImmutableList<Integer> expected = JImmutables.list(2, 4);
        JImmutableList<Integer> list = JImmutables.list(1, 2, 3);
        assertEquals(expected, Functions.<Integer, Integer>collectSome(list.cursor(), JImmutables.<Integer>list(), new Func1<Integer, Holder<Integer>>()
        {
            @Override
            public Holder<Integer> apply(Integer value)
            {
                if (value % 2 == 0) {
                    return Holders.of();
                } else {
                    return Holders.of(value + 1);
                }
            }
        }));
    }

    public void testFind()
    {
        Func1<Integer, Boolean> func = new Func1<Integer, Boolean>()
        {
            @Override
            public Boolean apply(Integer value)
            {
                return value % 2 == 0;
            }
        };

        JImmutableList<Integer> list = JImmutables.list(1, 2, 3);
        assertEquals(Holders.<Integer>of(2), Functions.find(list.cursor(), func));

        list = JImmutables.list(1, 5, 7);
        assertEquals(Holders.<Integer>of(), Functions.find(list.cursor(), func));
    }

    public void testReject()
    {
        Func1<Integer, Boolean> func = new Func1<Integer, Boolean>()
        {
            @Override
            public Boolean apply(Integer value)
            {
                return value % 2 == 0;
            }
        };

        JImmutableList<Integer> list = JImmutables.list(1, 2, 3);
        JImmutableList<Integer> expected = JImmutables.list(1, 3);
        assertEquals(expected, Functions.reject(list.cursor(), JImmutables.<Integer>list(), func));
        list = JImmutables.list(1, 5, 7);
        assertEquals(list, Functions.reject(list.cursor(), JImmutables.<Integer>list(), func));
        list = JImmutables.list(2, 6, 12);
        expected = JImmutables.list();
        assertEquals(expected, Functions.reject(list.cursor(), JImmutables.<Integer>list(), func));
    }

    public void testSelect()
    {
        Func1<Integer, Boolean> func = new Func1<Integer, Boolean>()
        {
            @Override
            public Boolean apply(Integer value)
            {
                return value % 2 == 0;
            }
        };

        JImmutableList<Integer> list = JImmutables.list(1, 2, 3);
        JImmutableList<Integer> expected = JImmutables.list(2);
        assertEquals(expected, Functions.select(list.cursor(), JImmutables.<Integer>list(), func));
        list = JImmutables.list(2, 6, 12);
        assertEquals(list, Functions.select(list.cursor(), JImmutables.<Integer>list(), func));
        list = JImmutables.list(1, 5, 7);
        expected = JImmutables.list();
        assertEquals(expected, Functions.select(list.cursor(), JImmutables.<Integer>list(), func));
    }
}
