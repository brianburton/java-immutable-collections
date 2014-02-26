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

package org.javimmutable.collections.array.trie32;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trie32ArrayTest
        extends TestCase
{
    public void testShift()
    {
        int shift = 0;
        assertEquals(0xff00, (0xff00 >>> shift));
        shift = 4;
        assertEquals(0x0ff0, (0xff00 >>> shift));
    }

    public void testCursor()
    {
        Trie32Array<Integer> array = Trie32Array.of();
        array = array.assign(-500, -5001).assign(-10, -101).assign(-1, -11).assign(0, 0).assign(1, 11).assign(10, 101).assign(500, 5001);

        List<Integer> indexes = Arrays.asList(-500, -10, -1, 0, 1, 10, 500);
        StandardCursorTest.listCursorTest(indexes, array.keysCursor());

        List<Integer> values = Arrays.asList(-5001, -101, -11, 0, 11, 101, 5001);
        StandardCursorTest.listCursorTest(values, array.valuesCursor());

        List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        entries.add(MapEntry.of(-500, -5001));
        entries.add(MapEntry.of(-10, -101));
        entries.add(MapEntry.of(-1, -11));
        entries.add(MapEntry.of(0, 0));
        entries.add(MapEntry.of(1, 11));
        entries.add(MapEntry.of(10, 101));
        entries.add(MapEntry.of(500, 5001));
        StandardCursorTest.listCursorTest(entries, array.cursor());
    }
}
