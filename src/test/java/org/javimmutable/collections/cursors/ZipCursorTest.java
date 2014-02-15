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

package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class ZipCursorTest
        extends TestCase
{
    public void test()
    {
        StandardCursorTest.emptyCursorTest(ZipCursor.of(StandardCursor.forRange(1, 2), StandardCursor.of()));
        StandardCursorTest.emptyCursorTest(ZipCursor.of(StandardCursor.of(), StandardCursor.forRange(1, 2)));

        List<Tuple2<Integer, Integer>> expected = new ArrayList<Tuple2<Integer, Integer>>();
        expected.add(new Tuple2<Integer, Integer>(10, 20));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 20)));
        expected.add(new Tuple2<Integer, Integer>(11, 21));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 21)));
        expected.add(new Tuple2<Integer, Integer>(12, 22));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 22)));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 50)));

        expected.clear();
        expected.add(new Tuple2<Integer, Integer>(10, 20));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 10), StandardCursor.forRange(20, 50)));
        expected.add(new Tuple2<Integer, Integer>(11, 21));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 11), StandardCursor.forRange(20, 50)));
        expected.add(new Tuple2<Integer, Integer>(12, 22));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 50)));
    }
}
