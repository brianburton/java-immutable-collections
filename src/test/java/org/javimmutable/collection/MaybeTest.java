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

package org.javimmutable.collection;

import org.javimmutable.collection.common.StandardSerializableTests;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.javimmutable.collection.common.TestUtil.makeList;
import static org.junit.Assert.*;

public class MaybeTest
{
    @Test
    public void testEmpty()
        throws Exception
    {
        assertSame(Maybe.empty(), Maybe.empty());
        assertSame(Maybe.empty(), Maybe.cast(Float.class, 10));
        assertSame(Maybe.<Integer>empty(), Maybe.first(Collections.<Integer>emptySet()));
        assertSame(Maybe.empty(), Maybe.first(Collections.<Integer>emptySet(), x -> x == 3));
        assertSame(Maybe.empty(), Maybe.first(Arrays.asList(1, 2), x -> x == 3));
        assertSame(NotNull.empty(), Maybe.empty().notNull());
        assertSame(Maybe.empty(), Maybe.empty().nullToEmpty());

        // test map
        final Maybe<String> empty = Maybe.empty();
        assertEquals(Maybe.of("1"), empty.map(() -> "1"));
        assertSame(Maybe.empty(), empty.map(Integer::parseInt));
        assertEquals(Maybe.of(1), empty.map(() -> 1, Integer::parseInt));

        assertThrows(IOException.class, () -> empty.mapThrows(() -> {
            throw new IOException();
        }));
        assertEquals(Maybe.empty(), empty.mapThrows(MaybeTest::toIntThrows));
        assertThrows(IOException.class, () -> empty.mapThrows(() -> {
                                                                  throw new IOException();
                                                              },
                                                              MaybeTest::toIntThrows));

        // test flatMap
        assertEquals(Maybe.of("a"), empty.flatMap(() -> Maybe.of("a")));
        assertEquals(Maybe.empty(), empty.flatMap(MaybeTest::toIntMaybe));
        assertEquals(Maybe.of(1), empty.flatMap(() -> Maybe.of(1), MaybeTest::toIntMaybe));
        assertThrows(IOException.class, () -> empty.flatMapThrows(() -> {
            throw new IOException();
        }));
        assertEquals(Maybe.empty(), empty.flatMapThrows(MaybeTest::toIntMaybeThrows));
        assertThrows(IOException.class, () -> empty.flatMapThrows(() -> {
                                                                      throw new IOException();
                                                                  },
                                                                  MaybeTest::toIntMaybeThrows));

        // test selection
        assertSame(empty, empty.select(x -> true));
        assertSame(empty, empty.select(x -> false));
        assertSame(empty, empty.reject(x -> true));
        assertSame(empty, empty.reject(x -> false));

        // test application
        Temp.Var1<String> change = Temp.var("");
        assertSame(empty, empty.apply(() -> change.x = "1"));
        assertEquals("1", change.x);
        assertSame(empty, empty.apply(x -> change.x = x));
        assertEquals("1", change.x);
        assertSame(empty, empty.applyThrows(() -> change.x = "2"));
        assertEquals("2", change.x);
        assertThrows(IOException.class, () -> empty.applyThrows(() -> {
            throw new IOException();
        }));
        assertSame(empty, empty.applyThrows(x -> change.x = x));
        assertEquals("2", change.x);
        assertSame(empty, empty.applyThrows(x -> {
            throw new IOException();
        }));

        // test get
        assertThrows(NoSuchElementException.class, () -> empty.unsafeGet());
        assertThrows(IOException.class, () -> empty.unsafeGet(IOException::new));
        assertEquals("1", empty.get("1"));
        assertEquals(null, empty.getOrNull());
        assertEquals("2", empty.getOr(() -> "2"));

        // test match
        assertEquals(Integer.valueOf(-1), empty.match(-1, Integer::parseInt));
        assertEquals(Integer.valueOf(-1), empty.matchOr(() -> -1, Integer::parseInt));
        assertEquals(Integer.valueOf(-1), empty.matchThrows(-1, MaybeTest::toIntThrows));
        assertEquals(Integer.valueOf(-1), empty.matchOrThrows(() -> -1, MaybeTest::toIntThrows));

        // miscellaneous
        assertEquals(true, empty.isEmpty());
        assertEquals(false, empty.isFull());
        assertEquals(-1, empty.hashCode());
        assertEquals("()", empty.toString());
        assertEquals(Collections.emptyList(), makeList(empty));
        assertEquals(Collections.emptyList(), empty.stream().collect(Collectors.toList()));
        assertEquals(false, empty.iterator().hasNext());
    }

    @Test
    public void testFull()
        throws Exception
    {
        final Maybe<String> full = Maybe.of("8");
        assertEquals(Maybe.of(1), Maybe.of(3 - 2));
        assertEquals(Maybe.of(null), Maybe.cast(Integer.class, null));
        assertEquals(Maybe.of(10), Maybe.cast(Integer.class, 10));
        assertEquals(Maybe.of(1), Maybe.first(Arrays.asList(1, 2, 3)));
        assertEquals(Maybe.of(3), Maybe.first(Arrays.asList(1, 2, 3), x -> x == 3));
        assertEquals(Maybe.of(2), Maybe.first(Arrays.asList(1, 2, 3), x -> x > 1));
        assertEquals(NotNull.empty(), Maybe.of(null).notNull());
        assertEquals(NotNull.of(3), Maybe.of(3).notNull());
        assertSame(Maybe.empty(), Maybe.of(null).nullToEmpty());
        assertSame(full, full.nullToEmpty());

        // test map
        assertEquals(Maybe.of("8"), full.map(() -> "1"));
        assertEquals(Maybe.of(8), full.map(Integer::parseInt));
        assertEquals(Maybe.of(8), full.map(() -> 1, Integer::parseInt));

        assertSame(full, full.mapThrows(() -> {
            throw new IOException();
        }));
        assertEquals(Maybe.of(8), full.mapThrows(MaybeTest::toIntThrows));
        assertEquals(Maybe.of(8), full.mapThrows(() -> {
                                                     throw new IOException();
                                                 },
                                                 MaybeTest::toIntThrows));

        // test flatMap
        assertSame(full, full.flatMap(() -> Maybe.of("a")));
        assertEquals(Maybe.of(8), full.flatMap(MaybeTest::toIntMaybe));
        assertEquals(Maybe.of(8), full.flatMap(() -> Maybe.of(1), MaybeTest::toIntMaybe));
        assertSame(full, full.flatMapThrows(() -> {
            throw new IOException();
        }));
        assertEquals(Maybe.of(8), full.flatMapThrows(MaybeTest::toIntMaybeThrows));
        assertEquals(Maybe.of(8), full.flatMapThrows(() -> {
                                                         throw new IOException();
                                                     },
                                                     MaybeTest::toIntMaybeThrows));

        // test selection
        assertEquals(Maybe.empty(), full.select(x -> false));
        assertSame(full, full.select(x -> true));
        assertSame(full, full.reject(x -> false));
        assertEquals(Maybe.empty(), full.reject(x -> true));

        // test application
        Temp.Var1<String> change = Temp.var("");
        assertSame(full, full.apply(() -> change.x = "1"));
        assertEquals("", change.x);
        assertSame(full, full.apply(x -> change.x = x));
        assertEquals("8", change.x);
        assertSame(full, full.applyThrows(() -> change.x = "2"));
        assertEquals("8", change.x);
        assertSame(full, full.applyThrows(() -> {
            throw new IOException();
        }));
        change.x = "";
        assertSame(full, full.applyThrows(x -> change.x = x));
        assertEquals("8", change.x);
        assertThrows(IOException.class, () -> full.applyThrows(x -> {
            throw new IOException();
        }));

        // test get
        assertEquals("8", full.unsafeGet());
        assertEquals("8", full.unsafeGet(IOException::new));
        assertEquals("8", full.get("1"));
        assertEquals("8", full.getOrNull());
        assertEquals("8", full.getOr(() -> "2"));

        // test match
        assertEquals(Integer.valueOf(8), full.match(-1, Integer::parseInt));
        assertEquals(Integer.valueOf(8), full.matchOr(() -> -1, Integer::parseInt));
        assertEquals(Integer.valueOf(8), full.matchThrows(-1, MaybeTest::toIntThrows));
        assertEquals(Integer.valueOf(8), full.matchOrThrows(() -> -1, MaybeTest::toIntThrows));

        // miscellaneous
        assertEquals(false, full.isEmpty());
        assertEquals(true, full.isFull());
        assertEquals("8".hashCode(), full.hashCode());
        assertEquals("(8)", full.toString());
        assertEquals(Collections.singletonList("8"), makeList(full));
        assertEquals(Collections.singletonList("8"), full.stream().collect(Collectors.toList()));

        // equals
        assertEquals(true, full.equals(full));
        assertEquals(false, full.equals("8"));
        assertEquals(false, full.equals(null));
        assertEquals(false, Maybe.of(null).equals(full));
        assertEquals(false, full.equals(Maybe.of(null)));
        assertEquals(false, Maybe.of("x").equals(Maybe.of("y")));
        assertEquals(true, Maybe.of("x").equals(Maybe.of("x")));
        assertEquals(false, full.equals(Maybe.empty()));
        assertEquals(false, Maybe.empty().equals(full));
    }

    @Test
    public void testSerialization()
        throws Exception
    {
        Maybe<String> maybe = Maybe.empty();
        StandardSerializableTests.verifySerializable(maybe,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBJL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM802sTEoNKMqvqPwPAn9OXeZhYKgoKGdjYGB+uWpVBQDCJ5peUgAAAA==");
        maybe = Maybe.of(null);
        StandardSerializableTests.verifySerializable(maybe,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBJL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM802sTEoNKMqvqPwPAn9OXeZhYKgoKGdjYGB+uXt3QQUAjVxwxFMAAAA=");
        maybe = Maybe.of("hello");
        StandardSerializableTests.verifySerializable(maybe,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBJL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM802sTEoNKMqvqPwPAn9OXeZhYKgoKGdjYGB+uXt3CQNrRmpOTn4FAENwjkZaAAAA");
    }

    private static int toIntThrows(String s)
        throws IOException
    {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private static Maybe<Integer> toIntMaybeThrows(String s)
        throws IOException
    {
        if (s.isEmpty()) {
            return Maybe.empty();
        }
        try {
            return Maybe.of(Integer.parseInt(s));
        } catch (NumberFormatException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private static Maybe<Integer> toIntMaybe(String s)
    {
        if (s.isEmpty()) {
            return Maybe.empty();
        }
        try {
            return Maybe.of(Integer.parseInt(s));
        } catch (NumberFormatException ex) {
            return Maybe.empty();
        }
    }
}
