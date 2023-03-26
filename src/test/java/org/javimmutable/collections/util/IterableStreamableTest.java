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

import junit.framework.TestCase;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.ILists;
import org.javimmutable.collections.IMaps;
import org.javimmutable.collections.IMultisets;
import org.javimmutable.collections.ISets;
import org.javimmutable.collections.IterableStreamable.Partitions;
import org.javimmutable.collections.MapEntry;

public class IterableStreamableTest
    extends TestCase
{
    public void testSingle()
    {
        assertEquals(Holder.none(), ISets.hashed().single());
        assertEquals(Holder.some("a"), ISets.hashed("a").single());
        assertEquals(Holder.none(), ISets.hashed("a", "b").single());

        assertEquals(Holder.none(), ILists.of().single());
        assertEquals(Holder.some("a"), ILists.of("a").single());
        assertEquals(Holder.some(null), ILists.of((String)null).single());
        assertEquals(Holder.none(), ILists.of("a", "b").single());
    }

    public void testCount()
    {
        assertEquals(0, ILists.of().count());
        assertEquals(1, ILists.of(1).count());
        assertEquals(5, ILists.of(1, 3, 5, 7, 9).count());

        assertEquals(0, ILists.of().count(x -> false));
        assertEquals(0, ILists.of(1).count(x -> false));
        assertEquals(1, ILists.of(1).count(x -> true));
        assertEquals(2, ILists.of(1, 3, 5, 7, 9).count(x -> x % 3 == 0));
    }

    public void testAllMatch()
    {
        assertEquals(true, ILists.of().allMatch(x -> false));
        assertEquals(true, ILists.of().allMatch(x -> true));

        assertEquals(false, ILists.of(1).allMatch(x -> false));
        assertEquals(true, ILists.of(1).allMatch(x -> true));
    }

    public void testAnyMatch()
    {
        assertEquals(false, ILists.of().anyMatch(x -> false));
        assertEquals(false, ILists.of().anyMatch(x -> true));

        assertEquals(false, ILists.of(1).anyMatch(x -> false));
        assertEquals(true, ILists.of(1).anyMatch(x -> true));

        assertEquals(false, ILists.of(1, 3, 5).anyMatch(x -> x > 5));
        assertEquals(true, ILists.of(1, 3, 5).anyMatch(x -> x < 5));
    }

    public void testFirst()
    {
        assertEquals(Holder.none(), ILists.of().first(x -> true));
        assertEquals(Holder.none(), ILists.of(1, 3).first(x -> x >= 5));
        assertEquals(Holders.nullable(3), ILists.of(1, 3, 5).first(x -> x >= 3));
    }

    public void testCollectAll()
    {
        assertSame(ISets.hashed(), ILists.of().collect(ISets.hashed()));
        assertEquals(ISets.hashed(1), ILists.of(1).collect(ISets.hashed()));
        assertEquals(ISets.hashed(1, 2, 3, 4, 5), ILists.of(1, 2, 3, 4, 5).collect(ISets.hashed()));
    }

    public void testCollectAtMost()
    {
        assertSame(ISets.hashed(), ILists.of().collect(100, ISets.hashed()));
        assertSame(ISets.hashed(), ILists.of(1).collect(0, ISets.hashed()));
        assertEquals(ISets.hashed(1), ILists.of(1).collect(1, ISets.hashed()));
        assertEquals(ISets.hashed(1), ILists.of(1).collect(100, ISets.hashed()));
        assertEquals(ISets.hashed(1), ILists.of(1, 2, 3, 4, 5).collect(1, ISets.hashed()));
        assertEquals(ISets.hashed(1, 2, 3), ILists.of(1, 2, 3, 4, 5).collect(3, ISets.hashed()));
        assertEquals(ISets.hashed(1, 2, 3, 4, 5), ILists.of(1, 2, 3, 4, 5).collect(5, ISets.hashed()));
    }

    public void testCollectAllMatching()
    {
        assertEquals(ILists.of(), ILists.of().collect(ILists.of(), x -> true));
        assertEquals(ILists.of(1, 3, 5), ILists.of(1, 3, 5).collect(ILists.of(), x -> true));
        assertEquals(ILists.of(1, 5), ILists.of(1, 3, 5).collect(ILists.of(), x -> x != 3));
    }

    public void testCollectAtMostMatching()
    {
        assertEquals(ILists.of(), ILists.of().collect(3, ILists.of(), x -> true));
        assertEquals(ILists.of(), ILists.of(1, 3, 5).collect(0, ILists.of(), x -> true));
        assertEquals(ILists.of(), ILists.of(1, 3, 5).collect(-10, ILists.of(), x -> true));
        assertEquals(ILists.of(1), ILists.of(1, 3, 5).collect(1, ILists.of(), x -> true));
        assertEquals(ILists.of(1, 3), ILists.of(1, 3, 5).collect(2, ILists.of(), x -> true));
        assertEquals(ILists.of(1, 5), ILists.of(1, 3, 5, 7).collect(2, ILists.of(), x -> x != 3));
    }

    public void testTransformAll()
    {
        assertEquals(ILists.of(), ILists.of().transform(ILists.of(), x -> x));
        assertEquals(ILists.of(-1), ILists.of(1).transform(ILists.of(), x -> -x));
        assertEquals(ILists.of(-1, -3, -5), ILists.of(1, 3, 5).transform(ILists.of(), x -> -x));
    }

    public void testTransformAtMost()
    {
        assertEquals(ILists.of(), ILists.of().transform(10, ILists.of(), x -> x));
        assertEquals(ILists.of(-1, -3), ILists.of(1, 3, 5).transform(2, ILists.of(), x -> -x));
    }

    public void testTransformSome()
    {
        assertEquals(ILists.of(), ILists.of().transformSome(ILists.of(), x -> Holders.nullable(x)));
        assertEquals(ILists.of(9, -1, -5), ILists.of(1, 3, 5).transformSome(ILists.of(9), x -> {
            if (x == 3) {
                return Holder.none();
            } else {
                Integer value = -x;
                return Holders.nullable(value);
            }
        }));
    }

    public void testTransformAtMostSome()
    {
        assertEquals(ILists.of(), ILists.of().transformSome(10, ILists.of(), x -> Holders.nullable(x)));
        assertEquals(ILists.of(9, -1, -5), ILists.of(1, 3, 5).transformSome(10, ILists.of(9), x -> {
            if (x == 3) {
                return Holder.none();
            } else {
                Integer value = -x;
                return Holders.nullable(value);
            }
        }));
        assertEquals(ILists.of(9, -1), ILists.of(1, 3, 5).transformSome(1, ILists.of(9), x -> {
            if (x == 3) {
                return Holder.none();
            } else {
                Integer value = -x;
                return Holders.nullable(value);
            }
        }));
    }

    public void testPartition()
    {
        assertEquals(new Partitions<>(ILists.of(), ILists.of()), ILists.of().partition(ILists.of(), ILists.of(), x -> true));
        assertEquals(new Partitions<>(ILists.of(1, 2, 3, 4, 5), ILists.of()), ILists.of(1, 2, 3, 4, 5).partition(ILists.of(), ILists.of(), x -> true));
        assertEquals(new Partitions<>(ILists.of(), ILists.of(1, 2, 3, 4, 5)), ILists.of(1, 2, 3, 4, 5).partition(ILists.of(), ILists.of(), x -> false));
        assertEquals(new Partitions<>(ILists.of(999, 1, 3, 5), ILists.of(888, 2, 4)), ILists.of(1, 2, 3, 4, 5).partition(ILists.of(999), ILists.of(888), x -> x % 2 == 1));
        Partitions<IList<Integer>> p = ILists.of(1, 2, 3, 4, 5).partition(ILists.of(999), ILists.of(888), x -> x % 2 == 1);
        assertEquals(ILists.of(999, 1, 3, 5), p.getMatched());
        assertEquals(ILists.of(888, 2, 4), p.getUnmatched());
    }

    public void testReduce()
    {
        assertEquals(Holder.none(), ILists.of().reduce((s, x) -> s));
        assertEquals(Holders.nullable(1), ILists.of(1).reduce((s, x) -> s + x));
        assertEquals(Holders.nullable(3), ILists.of(1, 2).reduce((s, x) -> s + x));
        assertEquals(Holders.nullable(6), ILists.of(1, 2, 3).reduce((s, x) -> s + x));

        assertEquals(Integer.valueOf(0), ILists.of().reduce(0, (s, x) -> s));
        assertEquals(Integer.valueOf(1), ILists.of(1).reduce(0, (s, x) -> s + x));
        assertEquals(Integer.valueOf(3), ILists.of(1, 2).reduce(0, (s, x) -> s + x));
        assertEquals(Integer.valueOf(6), ILists.of(1, 2, 3).reduce(0, (s, x) -> s + x));
    }

    public void testInject()
    {
        final Func2<String, Integer, String> accumulator = (s, x) -> String.valueOf(Integer.parseInt(s) + x);
        assertEquals("0", ILists.<Integer>of().reduce("0", accumulator));
        assertEquals("1", ILists.of(1).reduce("0", accumulator));
        assertEquals("9", ILists.of(1, 3, 5).reduce("0", accumulator));
        assertEquals(Integer.valueOf(-9), ILists.of(1, 3, 5).reduce(-18, (s, x) -> s + x));
    }

    public void testConversions()
    {
        assertEquals(ISets.hashed(3, 5), ILists.of(5, 3, 3, 5).transform(ISets.<Integer>sorted(), x -> x));
        assertEquals(IMultisets.hashed(3, 3, 5, 5), ILists.of(5, 3, 3, 5).transform(IMultisets.<Integer>sorted(), x -> x));
        assertEquals(IMaps.hashed().assign(3, 6).assign(5, 10), ILists.of(5, 3, 3, 5).transform(IMaps.sorted(), x -> MapEntry.of(x, 2 * x)));
        assertEquals(IListMap.listMap().assign(3, ILists.of(3, 3)).assign(5, ILists.of(5)), ILists.of(3, 5, 3).transform(IListMap.sortedListMap(), x -> MapEntry.of(x, x)));
    }
}
