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

package org.javimmutable.collections;

import junit.framework.TestCase;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.Maybe.*;

public class MaybeTest
    extends TestCase
{
    public void testNone()
    {
        final Maybe<Integer> o = none();
        assertSame(o, none());
        assertSame(o, Maybe.maybe(null));
        assertSame(o, Maybe.of());
        assertThat(none()).isEqualTo(none());
        assertThat(none()).isNotEqualTo(some(4));
        assertEquals(o, o);
        assertEquals("None", o.toString());
        assertEquals(some(4), o.map(() -> 4));
        assertEquals(some(7), o.mapThrows(() -> 7));
        assertSame(o, o.map(x -> dontCallMe(1, "map")));
        assertSame(o, o.mapThrows(x -> dontCallMe(1, "mapThrows")));
        assertEquals(some(4), o.map(() -> 4, x -> dontCallMe(some(1), "map2")));
        assertEquals(some(7), o.mapThrows(() -> 7, x -> dontCallMe(some(1), "mapThrows2")));
        assertEquals(some(6), o.flatMap(() -> some(6)));
        assertEquals(some(-1), o.flatMapThrows(() -> some(-1)));
        assertSame(o, o.flatMap(x -> dontCallMe(some(1), "flatMap")));
        assertSame(o, o.flatMapThrows(x -> dontCallMe(some(1), "flatMapThrows")));
        assertEquals(some(6), o.flatMap(() -> some(6), x -> dontCallMe(some(1), "flatMap2")));
        assertEquals(some(-1), o.flatMapThrows(() -> some(-1), x -> dontCallMe(some(1), "flatMapThrows2")));
        assertSame(o, o.select(x -> dontCallMe(true, "select")));
        assertSame(o, o.select(x -> dontCallMe(false, "select")));
        assertSame(o, o.reject(x -> dontCallMe(true, "reject")));
        assertSame(o, o.reject(x -> dontCallMe(false, "reject")));
        List<Integer> values = new ArrayList<>();
        assertSame(o, o.apply(() -> values.add(1)));
        assertSame(o, o.applyThrows(() -> values.add(2)));
        assertEquals(Arrays.asList(1, 2), values);
        assertSame(o, o.apply(x -> dontCallMe("apply")));
        assertSame(o, o.applyThrows(x -> dontCallMe("applyThrows")));
        assertThatThrownBy(() -> o.unsafeGet()).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> o.unsafeGet(IOException::new)).isInstanceOf(IOException.class);
        assertEquals(Integer.valueOf(-1), o.get(-1));
        assertEquals(Integer.valueOf(-5), o.getOr(() -> -5));
        assertEquals("a", o.match("a", x -> "b"));
        assertEquals("a", o.matchOr(() -> "a", x -> "b"));
        assertEquals("a", o.matchThrows("a", x -> "b"));
        assertEquals("a", o.matchOrThrows(() -> "a", x -> "b"));
        assertEquals(true, o.isNone());
        assertEquals(false, o.isSome());
        assertSame(Holders.of(), o.toHolder());
        assertSame(o, o.toHolder().toMaybe());
        assertSame(o, first(Collections.emptyList()));
        assertSame(o, first(Collections.emptyList(), x -> true));
        assertSame(o, first(Collections.singleton(null)));
        StandardIteratorTests.emptyIteratorTest(o.iterator());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(Collections.emptyList(), o);
    }

    public void testSome()
        throws IOException
    {
        final Maybe<Integer> o = some(1);
        assertEquals(o, o);
        assertEquals(o, Maybe.of(1));
        assertThat(some(1)).isEqualTo(o);
        assertThat(Maybe.maybe(3)).isNotEqualTo(o);
        assertEquals("Some(1)", o.toString());
        assertSame(o, o.map(() -> dontCallMe(22, "map0")));
        assertSame(o, o.mapThrows(() -> dontCallMe(30, "mapThrows0")));
        assertEquals(some(12), o.map(x -> 12));
        assertEquals(some(12), o.mapThrows(x -> 12));
        assertEquals(some(2), o.map(() -> dontCallMe(22, "map0"), x -> x + 1));
        assertEquals(some(8), o.mapThrows(() -> dontCallMe(30, "mapThrows0"), x -> x + 7));
        assertSame(o, o.flatMap(() -> dontCallMe(some(22), "flatMap2")));
        assertSame(o, o.flatMapThrows(() -> dontCallMe(some(30), "flatMap2")));
        assertEquals(some(15), o.flatMap(x -> some(15)));
        assertEquals(some(15), o.flatMapThrows(x -> some(15)));
        assertEquals(some(13), o.flatMap(() -> dontCallMe(some(22), "flatMap2"), x -> some(x + 12)));
        assertEquals(some(-4), o.flatMapThrows(() -> dontCallMe(some(30), "flatMap2"), x -> some(x - 5)));
        assertSame(o, o.select(x -> true));
        assertSame(none(), o.select(x -> false));
        assertSame(none(), o.reject(x -> true));
        assertSame(o, o.reject(x -> false));
        assertSame(o, o.apply(() -> dontCallMe("apply")));
        assertSame(o, o.applyThrows(() -> dontCallMe("applyThrows")));
        final Temp.Int1 called = Temp.intVar(0);
        assertSame(o, o.apply(x -> called.a += 1));
        assertSame(o, o.applyThrows(x -> called.a += 1));
        assertEquals(2, called.a);
        assertEquals(Integer.valueOf(1), o.unsafeGet());
        assertEquals(Integer.valueOf(1), o.unsafeGet(IOException::new));
        assertEquals(Integer.valueOf(1), o.get(-1));
        assertEquals(Integer.valueOf(1), o.getOr(() -> -5));
        assertEquals("b", o.match("a", x -> "b"));
        assertEquals("b", o.matchOr(() -> "a", x -> "b"));
        assertEquals("b", o.matchThrows("a", x -> "b"));
        assertEquals("b", o.matchOrThrows(() -> "a", x -> "b"));
        assertEquals(false, o.isNone());
        assertEquals(true, o.isSome());
        assertEquals(Holders.of(1), o.toHolder());
        assertEquals(o, o.toHolder().toMaybe());
        assertEquals(maybe("y"), first(Arrays.asList("x", "y", "z"), x -> x.equals("y")));
        StandardIteratorTests.listIteratorTest(Collections.singletonList(1), o.iterator());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(Collections.singleton(1), o);
        assertThat(some(4)).isEqualTo(some(4));
        assertThat(some(7)).isNotEqualTo(some(4));
        assertThat(some(5)).isNotEqualTo(none());
    }

    private static void dontCallMe(String message)
    {
        fail(message + " called its lambda");
    }

    private static <T> T dontCallMe(T value,
                                    String message)
    {
        fail(message + " called its lambda");
        return value;
    }
}
