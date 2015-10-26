///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.array.bit32;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;

public class SingleBit32ArrayTest
        extends TestCase
{
    public void testSet()
    {
        Bit32Array<Integer> array = new SingleBit32Array<Integer>(10, 100);
        assertEquals(Integer.valueOf(100), array.get(10));
        assertEquals(Integer.valueOf(100), array.getValueOr(10, -99));
        assertEquals(Holders.of(100), array.find(10));
        assertEquals(1, array.size());

        // wrong index
        assertEquals(null, array.get(11));
        assertEquals(Integer.valueOf(-99), array.getValueOr(11, -99));
        assertEquals(Holders.<Integer>of(), array.find(11));

        array = array.assign(10, 200);
        assertTrue(array instanceof SingleBit32Array);
        assertEquals(Integer.valueOf(200), array.get(10));
        assertEquals(Integer.valueOf(200), array.getValueOr(10, -99));
        assertEquals(Holders.of(200), array.find(10));
        assertEquals(1, array.size());

        array = array.assign(11, 220);
        assertTrue(array instanceof StandardBit32Array);
        assertEquals(Holders.of(200), array.find(10));
        assertEquals(Holders.of(220), array.find(11));
        assertEquals(2, array.size());
    }

    public void testValueIdentity()
    {
        final String a = "a";
        final String b = "ab";
        Bit32Array<String> array = new SingleBit32Array<String>(10, a);
        assertSame(array, array.assign(10, a));
        assertFalse(array == array.assign(10, b));

        array = array.assign(10, b);
        assertSame(array, array.assign(10, b));
        assertEquals(b, "abc".substring(0, 2));
        assertFalse(array == array.assign(10, "abc".substring(0, 2)));
    }

    public void testDelete()
    {
        Bit32Array<Integer> array = new SingleBit32Array<Integer>(10, 100);
        assertEquals(Holders.of(100), array.find(10));
        assertEquals(1, array.size());

        Bit32Array<Integer> newArray = array.delete(18);
        assertSame(newArray, array);
        assertTrue(newArray instanceof SingleBit32Array);
        assertEquals(Holders.of(100), newArray.find(10));
        assertEquals(1, newArray.size());

        array = newArray;
        newArray = array.delete(10);
        assertTrue(newArray instanceof EmptyBit32Array);
        assertEquals(Holders.<Integer>of(), newArray.find(10));
        assertEquals(0, newArray.size());
    }

    public void testBoundsCheck()
    {
        Bit32Array<Integer> array = new SingleBit32Array<Integer>(10, 100);
        for (int i = 0; i < 32; ++i) {
            array.find(i);
            array.assign(i, i);
            array.delete(i);
        }
        try {
            array.find(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            array.find(32);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }
}
