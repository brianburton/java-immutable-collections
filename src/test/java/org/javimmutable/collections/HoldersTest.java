///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

public class HoldersTest
        extends TestCase
{
    public void testEmpty()
    {
        Holder<String> e1 = Holders.of();
        Holder<String> e2 = Holders.of();
        assertSame(e1, e2);
        assertEquals(e1, e2);
        assertEquals(true, e1.isEmpty());
        assertEquals(false, e1.isFilled());
        try {
            e1.getValue();
            fail();
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        assertEquals(null, e1.getValueOrNull());
        assertEquals("default", e1.getValueOr("default"));
        assertEquals(-1, e1.hashCode());
    }

    public void testFilled()
    {
        Holder<String> empty = Holders.of();
        Holder<String> filled1 = Holders.of(null);
        Holder<String> filled2 = Holders.of("ABC");
        Holder<String> filled3 = Holders.of("BC");
        Holder<String> filled4 = Holders.of("ABC");
        assertEquals(false, empty.equals(filled1));
        assertEquals(false, filled1.equals(empty));

        assertEquals(false, filled1.isEmpty());
        assertEquals(true, filled1.isFilled());
        assertFalse(filled1.equals(filled2));
        assertFalse(filled1.equals(filled3));
        assertFalse(filled1.equals(filled4));
        assertEquals(null, filled1.getValue());
        assertEquals(null, filled1.getValueOrNull());
        assertEquals(null, filled1.getValueOr("ZZZ"));
        assertEquals(1, filled1.hashCode());

        assertEquals(false, filled2.isEmpty());
        assertEquals(true, filled2.isFilled());
        assertFalse(filled2.equals(filled1));
        assertFalse(filled2.equals(filled3));
        assertTrue(filled2.equals(filled4));
        assertEquals("ABC", filled2.getValue());
        assertEquals("ABC", filled2.getValueOrNull());
        assertEquals("ABC", filled2.getValueOr("ZZZ"));
        assertEquals(64578, filled2.hashCode());

        assertEquals(false, filled3.isEmpty());
        assertEquals(true, filled3.isFilled());
        assertFalse(filled3.equals(filled1));
        assertFalse(filled3.equals(filled2));
        assertFalse(filled3.equals(filled4));
        assertEquals("BC", filled3.getValue());
        assertEquals("BC", filled3.getValueOrNull());
        assertEquals("BC", filled3.getValueOr("ZZZ"));
        assertEquals(2113, filled3.hashCode());

        assertEquals(false, filled4.isEmpty());
        assertEquals(true, filled4.isFilled());
        assertFalse(filled4.equals(filled1));
        assertTrue(filled4.equals(filled2));
        assertFalse(filled4.equals(filled3));
        assertEquals("ABC", filled4.getValue());
        assertEquals("ABC", filled4.getValueOrNull());
        assertEquals("ABC", filled4.getValueOr("ZZZ"));
        assertEquals(64578, filled4.hashCode());
    }
}
