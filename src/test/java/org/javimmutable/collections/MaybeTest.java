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

package org.javimmutable.collections;

import junit.framework.TestCase;
import org.javimmutable.collections.common.StandardSerializableTests;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertThrows;

public class MaybeTest
    extends TestCase
{
    public void testAbsent()
        throws IOException
    {
        Maybe<String> e1 = Maybe.absent();
        Maybe<String> e2 = Maybe.absent();
        assertSame(e1, e2);
        assertEquals(e1, e2);
        assertEquals(true, e1.isAbsent());
        assertEquals(false, e1.isPresent());
        try {
            e1.unsafeGet();
            fail();
        } catch (NoSuchElementException ex) {
            // expected
        }
        assertEquals(null, e1.getOrNull());
        assertEquals("default", e1.get("default"));
        assertEquals(-1, e1.hashCode());

        final AtomicReference<String> called = new AtomicReference<>();
        e1.apply(x -> called.set(x));
        assertEquals(null, called.get());
        e1.applyThrows(x -> called.set(x));
        assertEquals(null, called.get());
        assertEquals(Maybe.absent(), e1.map(String::hashCode));
        assertEquals(Maybe.absent(), e1.mapThrows(this::hashCodeThrows));
        assertEquals("ZZZ", e1.get("ZZZ"));
        assertEquals("ZZZ", e1.getOr(() -> "ZZZ"));
        try {
            e1.unsafeGet(() -> new RuntimeException("threw"));
            fail();
        } catch (RuntimeException ex) {
            assertEquals("threw", ex.getMessage());
        }
    }

    public void testPresent()
        throws IOException
    {
        Maybe<String> empty1 = Maybe.absent();
        Maybe<String> empty2 = Maybe.notNull(null);
        Maybe<String> filled1 = Maybe.notNull("ABC");
        Maybe<String> filled2 = Maybe.notNull("BC");
        Maybe<String> filled3 = Maybe.notNull("ABC");
        assertEquals(true, empty1.equals(empty2));
        assertEquals(true, empty2.equals(empty1));

        assertEquals(true, empty2.isAbsent());
        assertEquals(false, empty2.isPresent());
        assertFalse(empty2.equals(filled1));
        assertFalse(empty2.equals(filled2));
        assertFalse(empty2.equals(filled3));
        assertThrows(NoSuchElementException.class, empty2::unsafeGet);
        assertEquals(null, empty2.getOrNull());
        assertEquals("ZZZ", empty2.get("ZZZ"));
        assertEquals(-1, empty2.hashCode());

        assertEquals(false, filled1.isAbsent());
        assertEquals(true, filled1.isPresent());
        assertFalse(filled1.equals(empty2));
        assertFalse(filled1.equals(filled2));
        assertTrue(filled1.equals(filled3));
        assertEquals("ABC", filled1.unsafeGet());
        assertEquals("ABC", filled1.getOrNull());
        assertEquals("ABC", filled1.get("ZZZ"));
        assertEquals(64578, filled1.hashCode());

        assertEquals(false, filled2.isAbsent());
        assertEquals(true, filled2.isPresent());
        assertFalse(filled2.equals(empty2));
        assertFalse(filled2.equals(filled1));
        assertFalse(filled2.equals(filled3));
        assertEquals("BC", filled2.unsafeGet());
        assertEquals("BC", filled2.getOrNull());
        assertEquals("BC", filled2.get("ZZZ"));
        assertEquals(2113, filled2.hashCode());

        assertEquals(false, filled3.isAbsent());
        assertEquals(true, filled3.isPresent());
        assertFalse(filled3.equals(empty2));
        assertTrue(filled3.equals(filled1));
        assertFalse(filled3.equals(filled2));
        assertEquals("ABC", filled3.unsafeGet());
        assertEquals("ABC", filled3.getOrNull());
        assertEquals("ABC", filled3.get("ZZZ"));
        assertEquals(64578, filled3.hashCode());

        final AtomicReference<String> called = new AtomicReference<>();
        filled3.apply(x -> called.set(x));
        assertEquals("ABC", called.get());
        called.set(null);
        assertNull(called.get());
        filled3.applyThrows(x -> called.set(x));
        assertEquals("ABC", called.get());
        assertEquals(Maybe.present("ABC".hashCode()), filled3.map(String::hashCode));
        assertEquals(Maybe.present("ABC".hashCode()), filled3.mapThrows(this::hashCodeThrows));
        assertEquals("ABC", filled3.get("ZZZ"));
        assertEquals("ABC", filled3.getOr(() -> "ZZZ"));
        assertEquals("ABC", filled3.unsafeGet(() -> new RuntimeException("threw")));
    }

    public void testSerialization()
        throws Exception
    {
        Maybe<String> maybe = Maybe.absent();
        StandardSerializableTests.verifySerializable(maybe,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzyM9JSS0KKMqvqPwPAn9OXeZhYKgoKGdjYGB+uWpVBQBDnBE5VAAAAA==");
        maybe = Maybe.present(null);
        StandardSerializableTests.verifySerializable(maybe,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzyM9JSS0KKMqvqPwPAn9OXeZhYKgoKGdjYGB+uXt3QQUAgGSoXlUAAAA=");
        maybe = Maybe.present("hello");
        StandardSerializableTests.verifySerializable(maybe,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzyM9JSS0KKMqvqPwPAn9OXeZhYKgoKGdjYGB+uXt3CQNrRmpOTn4FABuhCKRcAAAA");
    }

    private Integer hashCodeThrows(String value)
        throws IOException
    {
        if (value == null) {
            throw new IOException();
        }
        return value.hashCode();
    }
}
