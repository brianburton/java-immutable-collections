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

import static org.javimmutable.collections.util.JImmutables.*;

import junit.framework.TestCase;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IterableStreamable.Partitions;
import org.javimmutable.collections.MapEntry;

public class IterableStreamableTest
    extends TestCase
{
    public void testSingle()
    {
        assertEquals(none(), set().single());
        assertEquals(some("a"), set("a").single());
        assertEquals(none(), set("a", "b").single());

        assertEquals(none(), stack().single());
        assertEquals(some("a"), stack("a").single());
        assertEquals(none(), stack((String)null).single());
        assertEquals(none(), stack("a", "b").single());
    }

    public void testCount()
    {
        assertEquals(0, list().count());
        assertEquals(1, list(1).count());
        assertEquals(5, list(1, 3, 5, 7, 9).count());

        assertEquals(0, list().count(x -> false));
        assertEquals(0, list(1).count(x -> false));
        assertEquals(1, list(1).count(x -> true));
        assertEquals(2, list(1, 3, 5, 7, 9).count(x -> x % 3 == 0));
    }

    public void testAllMatch()
    {
        assertEquals(true, list().allMatch(x -> false));
        assertEquals(true, list().allMatch(x -> true));

        assertEquals(false, list(1).allMatch(x -> false));
        assertEquals(true, list(1).allMatch(x -> true));
    }

    public void testAnyMatch()
    {
        assertEquals(false, list().anyMatch(x -> false));
        assertEquals(false, list().anyMatch(x -> true));

        assertEquals(false, list(1).anyMatch(x -> false));
        assertEquals(true, list(1).anyMatch(x -> true));

        assertEquals(false, list(1, 3, 5).anyMatch(x -> x > 5));
        assertEquals(true, list(1, 3, 5).anyMatch(x -> x < 5));
    }

    public void testFirst()
    {
        assertEquals(Holder.none(), list().first(x -> true));
        assertEquals(Holder.none(), list(1, 3).first(x -> x >= 5));
        assertEquals(Holder.maybe(3), list(1, 3, 5).first(x -> x >= 3));
    }

    public void testCollectAll()
    {
        assertSame(set(), list().collect(set()));
        assertEquals(set(1), list(1).collect(set()));
        assertEquals(set(1, 2, 3, 4, 5), list(1, 2, 3, 4, 5).collect(set()));
    }

    public void testCollectAtMost()
    {
        assertSame(set(), list().collect(100, set()));
        assertSame(set(), list(1).collect(0, set()));
        assertEquals(set(1), list(1).collect(1, set()));
        assertEquals(set(1), list(1).collect(100, set()));
        assertEquals(set(1), list(1, 2, 3, 4, 5).collect(1, set()));
        assertEquals(set(1, 2, 3), list(1, 2, 3, 4, 5).collect(3, set()));
        assertEquals(set(1, 2, 3, 4, 5), list(1, 2, 3, 4, 5).collect(5, set()));
    }

    public void testCollectAllMatching()
    {
        assertEquals(list(), list().collect(list(), x -> true));
        assertEquals(list(1, 3, 5), list(1, 3, 5).collect(list(), x -> true));
        assertEquals(list(1, 5), list(1, 3, 5).collect(list(), x -> x != 3));
    }

    public void testCollectAtMostMatching()
    {
        assertEquals(list(), list().collect(3, list(), x -> true));
        assertEquals(list(), list(1, 3, 5).collect(0, list(), x -> true));
        assertEquals(list(), list(1, 3, 5).collect(-10, list(), x -> true));
        assertEquals(list(1), list(1, 3, 5).collect(1, list(), x -> true));
        assertEquals(list(1, 3), list(1, 3, 5).collect(2, list(), x -> true));
        assertEquals(list(1, 5), list(1, 3, 5, 7).collect(2, list(), x -> x != 3));
    }

    public void testTransformAll()
    {
        assertEquals(list(), list().transform(list(), x -> x));
        assertEquals(list(-1), list(1).transform(list(), x -> -x));
        assertEquals(list(-1, -3, -5), list(1, 3, 5).transform(list(), x -> -x));
    }

    public void testTransformAtMost()
    {
        assertEquals(list(), list().transform(10, list(), x -> x));
        assertEquals(list(-1, -3), list(1, 3, 5).transform(2, list(), x -> -x));
    }

    public void testTransformSome()
    {
        assertEquals(list(), list().transformSome(list(), x -> Holder.maybe(x)));
        assertEquals(list(9, -1, -5), list(1, 3, 5).transformSome(list(9), x -> {
            if (x == 3) {
                return Holder.none();
            } else {
                Integer value = -x;
                return Holder.maybe(value);
            }
        }));
    }

    public void testTransformAtMostSome()
    {
        assertEquals(list(), list().transformSome(10, list(), x -> Holder.maybe(x)));
        assertEquals(list(9, -1, -5), list(1, 3, 5).transformSome(10, list(9), x -> {
            if (x == 3) {
                return Holder.none();
            } else {
                Integer value = -x;
                return Holder.maybe(value);
            }
        }));
        assertEquals(list(9, -1), list(1, 3, 5).transformSome(1, list(9), x -> {
            if (x == 3) {
                return Holder.none();
            } else {
                Integer value = -x;
                return Holder.maybe(value);
            }
        }));
    }

    public void testPartition()
    {
        assertEquals(new Partitions<>(list(), list()), list().partition(list(), list(), x -> true));
        assertEquals(new Partitions<>(list(1, 2, 3, 4, 5), list()), list(1, 2, 3, 4, 5).partition(list(), list(), x -> true));
        assertEquals(new Partitions<>(list(), list(1, 2, 3, 4, 5)), list(1, 2, 3, 4, 5).partition(list(), list(), x -> false));
        assertEquals(new Partitions<>(list(999, 1, 3, 5), list(888, 2, 4)), list(1, 2, 3, 4, 5).partition(list(999), list(888), x -> x % 2 == 1));
        Partitions<IList<Integer>> p = list(1, 2, 3, 4, 5).partition(list(999), list(888), x -> x % 2 == 1);
        assertEquals(list(999, 1, 3, 5), p.getMatched());
        assertEquals(list(888, 2, 4), p.getUnmatched());
    }

    public void testReduce()
    {
        assertEquals(Holder.none(), list().reduce((s, x) -> s));
        assertEquals(Holder.maybe(1), list(1).reduce((s, x) -> s + x));
        assertEquals(Holder.maybe(3), list(1, 2).reduce((s, x) -> s + x));
        assertEquals(Holder.maybe(6), list(1, 2, 3).reduce((s, x) -> s + x));

        assertEquals(Integer.valueOf(0), list().reduce(0, (s, x) -> s));
        assertEquals(Integer.valueOf(1), list(1).reduce(0, (s, x) -> s + x));
        assertEquals(Integer.valueOf(3), list(1, 2).reduce(0, (s, x) -> s + x));
        assertEquals(Integer.valueOf(6), list(1, 2, 3).reduce(0, (s, x) -> s + x));
    }

    public void testInject()
    {
        final Func2<String, Integer, String> accumulator = (s, x) -> String.valueOf(Integer.parseInt(s) + x);
        assertEquals("0", JImmutables.<Integer>list().reduce("0", accumulator));
        assertEquals("1", JImmutables.list(1).reduce("0", accumulator));
        assertEquals("9", JImmutables.list(1, 3, 5).reduce("0", accumulator));
        assertEquals(Integer.valueOf(-9), JImmutables.list(1, 3, 5).reduce(-18, (s, x) -> s + x));
    }

    public void testConversions()
    {
        assertEquals(set(3, 5), list(5, 3, 3, 5).transform(JImmutables.<Integer>sortedSet(), x -> x));
        assertEquals(JImmutables.multiset(3, 3, 5, 5), list(5, 3, 3, 5).transform(JImmutables.<Integer>sortedMultiset(), x -> x));
        assertEquals(JImmutables.map().assign(3, 6).assign(5, 10), list(5, 3, 3, 5).transform(JImmutables.sortedMap(), x -> MapEntry.of(x, 2 * x)));
        assertEquals(JImmutables.listMap().assign(3, list(3, 3)).assign(5, list(5)), list(3, 5, 3).transform(JImmutables.sortedListMap(), x -> MapEntry.of(x, x)));
    }
}
