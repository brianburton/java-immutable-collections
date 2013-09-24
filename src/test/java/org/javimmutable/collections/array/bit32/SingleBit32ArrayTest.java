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

package org.javimmutable.collections.array.bit32;

import org.javimmutable.collections.Holders;
import junit.framework.TestCase;

public class SingleBit32ArrayTest
        extends TestCase
{
    public void testSet()
    {
        Bit32Array<Integer> array = new SingleBit32Array<Integer>(10, 100);
        assertEquals(Holders.of(100), array.get(10));
        assertEquals(1, array.size());

        array = array.set(10, 200);
        assertTrue(array instanceof SingleBit32Array);
        assertEquals(Holders.of(200), array.get(10));
        assertEquals(1, array.size());

        array = array.set(11, 220);
        assertTrue(array instanceof StandardBit32Array);
        assertEquals(Holders.of(200), array.get(10));
        assertEquals(Holders.of(220), array.get(11));
        assertEquals(2, array.size());
    }

    public void testDelete()
    {
        Bit32Array<Integer> array = new SingleBit32Array<Integer>(10, 100);
        assertEquals(Holders.of(100), array.get(10));
        assertEquals(1, array.size());

        Bit32Array<Integer> newArray = array.delete(18);
        assertSame(newArray, array);
        assertTrue(newArray instanceof SingleBit32Array);
        assertEquals(Holders.of(100), newArray.get(10));
        assertEquals(1, newArray.size());

        array = newArray;
        newArray = array.delete(10);
        assertTrue(newArray instanceof EmptyBit32Array);
        assertEquals(Holders.<Integer>of(), newArray.get(10));
        assertEquals(0, newArray.size());
    }

    public void testBoundsCheck()
    {
        Bit32Array<Integer> array = new SingleBit32Array<Integer>(10, 100);
        for (int i = 0; i < 32; ++i) {
            array.get(i);
            array.set(i, i);
            array.delete(i);
        }
        try {
            array.get(-1);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
        try {
            array.get(32);
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
    }
}
