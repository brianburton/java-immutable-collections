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

import junit.framework.TestCase;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.javimmutable.collections.util.JImmutables.*;

public class ZipTest
    extends TestCase
{
    public void test()
    {
        AtomicInteger sum = new AtomicInteger();
        Zip.forEach(list(1, 2), list(3, 4), (a, b) -> sum.addAndGet(a + b));
        assertEquals(10, sum.get());
        assertEquals(Integer.valueOf(10), Zip.reduce(0, list(1, 2), list(3, 4), ZipTest::adder));
    }

    public void testThrows()
        throws IOException
    {
        AtomicInteger sum = new AtomicInteger();
        Zip.forEachThrows(list(1, 2), list(3, 4), (a, b) -> {
            if (a == -1) {
                throw new IOException();
            }
            sum.addAndGet(a + b);
        });
        assertEquals(10, sum.get());
        assertEquals(Integer.valueOf(10), Zip.reduceThrows(0, list(1, 2), list(3, 4), ZipTest::adderThrows));
    }

    private static Integer adder(Integer s,
                                 Integer a,
                                 Integer b)
    {
        return s + a + b;
    }

    private static Integer adderThrows(Integer s,
                                       Integer a,
                                       Integer b)
        throws IOException
    {
        return s + a + b;
    }
}
