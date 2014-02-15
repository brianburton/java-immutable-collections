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

public class Tuple4Test
        extends TestCase
{
    public void test()
    {
        Tuple4<String, Integer, String, Integer> a10b900 = new Tuple4<String, Integer, String, Integer>("a", 10, "b", 900);
        Tuple4<String, Integer, String, Integer> a12b900 = new Tuple4<String, Integer, String, Integer>("a", 12, "b", 900);
        Tuple4<String, Integer, String, Integer> a10c900 = new Tuple4<String, Integer, String, Integer>("a", 10, "c", 900);
        Tuple4<String, Integer, String, Integer> b10b900 = new Tuple4<String, Integer, String, Integer>("b", 10, "b", 900);
        Tuple4<String, Integer, String, Integer> a10b1200 = new Tuple4<String, Integer, String, Integer>("a", 10, "b", 1200);
        assertEquals(true, a10b900.equals(a10b900));
        assertEquals(true, a10b900.equals(Tuple4.of("a", 10, "b", 900)));
        assertEquals(false, a10b900.equals(a12b900));
        assertEquals(false, a10b900.equals(a10c900));
        assertEquals(false, a10b900.equals(b10b900));
        assertEquals(false, a10b900.equals(a10b1200));
    }
}
