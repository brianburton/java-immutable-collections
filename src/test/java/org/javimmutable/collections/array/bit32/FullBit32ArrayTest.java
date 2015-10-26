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
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.List;

public class FullBit32ArrayTest
        extends TestCase
{
    public void testVarious()
    {
        Integer[] entries = new Integer[32];
        for (int k = 0; k < 32; ++k) {
            entries[k] = k;
        }
        for (int i = 0; i < 32; ++i) {
            Bit32Array<Integer> full = new FullBit32Array<Integer>(entries.clone());
            for (int k = 0; k < 32; ++k) {
                assertEquals((Integer)k, full.get(k));
                assertEquals((Integer)k, full.getValueOr(k, -99));
                assertEquals((Integer)k, full.find(k).getValue());
            }
            for (int k = 0; k < 32; ++k) {
                assertSame(full, full.assign(k, k));
            }
            for (int k = 0; k < 32; ++k) {
                full = full.assign(k, k + 1);
                assertEquals(true, full instanceof FullBit32Array);
            }
            for (int k = 0; k < 32; ++k) {
                assertEquals((Integer)(k + 1), full.find(k).getValue());
            }
            Bit32Array<Integer> std = full.delete(i);
            assertEquals(true, std instanceof StandardBit32Array);
            assertEquals(31, std.size());
            for (int k = 0; k < 32; ++k) {
                if (k == i) {
                    assertEquals(true, std.find(k).isEmpty());
                } else {
                    assertEquals((Integer)(k + 1), std.find(k).getValue());
                }
            }
        }
    }

    public void testIndexedConstructor()
    {
        List<Integer> values = new ArrayList<Integer>(64);
        for (int i = 0; i < 64; ++i) {
            values.add(i);
        }
        for (int offset = 0; offset < 32; ++offset) {
            FullBit32Array<Integer> full = new FullBit32Array<Integer>(IndexedList.retained(values), offset);
            for (int i = 0; i < 32; ++i) {
                assertEquals((Integer)(i + offset), full.get(i));
            }
        }
    }

    public void testCursor()
    {
        List<JImmutableMap.Entry<Integer, Integer>> expected = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        Integer[] entries = new Integer[32];
        for (int k = 0; k < 32; ++k) {
            entries[k] = k;
            expected.add(MapEntry.of(k, k));
        }
        Bit32Array<Integer> full = new FullBit32Array<Integer>(entries.clone());
        StandardCursorTest.listCursorTest(expected, full.cursor());
    }
}
