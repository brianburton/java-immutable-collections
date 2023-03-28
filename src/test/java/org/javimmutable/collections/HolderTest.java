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

import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;

public class HolderTest
    extends TestCase
{
    public void testEmpty()
        throws IOException
    {
        Holder<String> e1 = Holder.none();
        Holder<String> e2 = Holder.none();
        assertSame(e1, e2);
        assertEquals(e1, e2);
        assertEquals(true, e1.isNone());
        assertEquals(false, e1.isSome());
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
        assertEquals(Holder.none(), e1.map(String::hashCode));
        assertEquals(Holder.none(), e1.mapThrows(this::hashCodeThrows));
        assertEquals("ZZZ", e1.get("ZZZ"));
        assertEquals("ZZZ", e1.getOr(() -> "ZZZ"));
        try {
            e1.unsafeGet(() -> new RuntimeException("threw"));
            fail();
        } catch (RuntimeException ex) {
            assertEquals("threw", ex.getMessage());
        }
    }

    public void testFilled()
        throws IOException
    {
        Holder<String> empty1 = Holder.none();
        Holder<String> empty2 = Holders.notNull(null);
        Holder<String> filled1 = Holders.notNull("ABC");
        Holder<String> filled2 = Holders.notNull("BC");
        Holder<String> filled3 = Holders.notNull("ABC");
        assertEquals(true, empty1.equals(empty2));
        assertEquals(true, empty2.equals(empty1));

        assertEquals(true, empty2.isNone());
        assertEquals(false, empty2.isSome());
        assertFalse(empty2.equals(filled1));
        assertFalse(empty2.equals(filled2));
        assertFalse(empty2.equals(filled3));
        assertThrows(NoSuchElementException.class, empty2::unsafeGet);
        assertEquals(null, empty2.getOrNull());
        assertEquals("ZZZ", empty2.get("ZZZ"));
        assertEquals(-1, empty2.hashCode());

        assertEquals(false, filled1.isNone());
        assertEquals(true, filled1.isSome());
        assertFalse(filled1.equals(empty2));
        assertFalse(filled1.equals(filled2));
        assertTrue(filled1.equals(filled3));
        assertEquals("ABC", filled1.unsafeGet());
        assertEquals("ABC", filled1.getOrNull());
        assertEquals("ABC", filled1.get("ZZZ"));
        assertEquals(64578, filled1.hashCode());

        assertEquals(false, filled2.isNone());
        assertEquals(true, filled2.isSome());
        assertFalse(filled2.equals(empty2));
        assertFalse(filled2.equals(filled1));
        assertFalse(filled2.equals(filled3));
        assertEquals("BC", filled2.unsafeGet());
        assertEquals("BC", filled2.getOrNull());
        assertEquals("BC", filled2.get("ZZZ"));
        assertEquals(2113, filled2.hashCode());

        assertEquals(false, filled3.isNone());
        assertEquals(true, filled3.isSome());
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
        assertEquals(Holders.nullable("ABC".hashCode()), filled3.map(String::hashCode));
        assertEquals(Holders.nullable("ABC".hashCode()), filled3.mapThrows(this::hashCodeThrows));
        assertEquals("ABC", filled3.get("ZZZ"));
        assertEquals("ABC", filled3.getOr(() -> "ZZZ"));
        assertEquals("ABC", filled3.unsafeGet(() -> new RuntimeException("threw")));
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
