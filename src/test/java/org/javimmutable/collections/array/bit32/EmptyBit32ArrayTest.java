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

package org.javimmutable.collections.array.bit32;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;

public class EmptyBit32ArrayTest
        extends TestCase
{
    public void test()
    {
        EmptyBit32Array<Integer> array = new EmptyBit32Array<Integer>();
        assertEquals(0, array.size());

        Bit32Array<Integer> newArray = array.delete(10);
        assertSame(array, newArray);

        newArray = array.assign(10, 100);
        assertTrue(newArray instanceof SingleBit32Array);
        assertEquals(Holders.of(100), newArray.get(10));
    }

    public void testBoundsCheck()
    {
        Bit32Array<Integer> array = new EmptyBit32Array<Integer>();
        for (int i = 0; i < 32; ++i) {
            array.get(i);
            array.assign(i, i);
            array.delete(i);
        }
        try {
            array.get(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            array.get(32);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }
}
