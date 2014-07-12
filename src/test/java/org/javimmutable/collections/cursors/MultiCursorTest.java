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

import java.util.Arrays;

public class MultiCursorTest
        extends TestCase
{
    public void testTwoEmpty()
    {
        StandardCursorTest.emptyCursorTest(MultiCursor.of(StandardCursor.<Integer>of(), StandardCursor.<Integer>of()));
    }

    public void testThreeEmpty()
    {
        StandardCursorTest.emptyCursorTest(MultiCursor.of(StandardCursor.<Integer>of(), StandardCursor.<Integer>of(), StandardCursor.<Integer>of()));
    }

    public void testOneNonEmpty()
    {
        StandardCursorTest.listCursorTest(Arrays.asList(100), MultiCursor.of(SingleValueCursor.of(100), StandardCursor.<Integer>of(), StandardCursor.<Integer>of()));
        StandardCursorTest.listCursorTest(Arrays.asList(100), MultiCursor.of(StandardCursor.<Integer>of(), SingleValueCursor.of(100), StandardCursor.<Integer>of()));
        StandardCursorTest.listCursorTest(Arrays.asList(100), MultiCursor.of(StandardCursor.<Integer>of(), StandardCursor.<Integer>of(), SingleValueCursor.of(100)));
    }

    public void testNoneEmpty()
    {
        StandardCursorTest.listCursorTest(Arrays.asList(100, 200), MultiCursor.of(SingleValueCursor.of(100), SingleValueCursor.of(200)));
        StandardCursorTest.listCursorTest(Arrays.asList(100, 200, 300), MultiCursor.of(SingleValueCursor.of(100), SingleValueCursor.of(200), SingleValueCursor.of(300)));
    }

    public void testNested()
    {
        StandardCursorTest.listCursorTest(Arrays.asList(100, 200, 300, 400, 500), MultiCursor.of(MultiCursor.of(SingleValueCursor.of(100),
                                                                                                                SingleValueCursor.of(200)),
                                                                                                 MultiCursor.of(SingleValueCursor.of(300),
                                                                                                                SingleValueCursor.of(400),
                                                                                                                SingleValueCursor.of(500))
        ));
    }
}
