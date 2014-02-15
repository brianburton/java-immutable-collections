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
import org.javimmutable.collections.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IterableCursorTest
        extends TestCase
{
    public void testEmptyIterable()
    {
        List<Integer> source = Collections.emptyList();
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.emptyCursorTest(cursor);

        // subsequent passes will use the next pointers
        assertEquals(source, remainingValuesAsList(cursor.next()));
    }

    public void testSingleValueIterable()
    {
        List<Integer> source = Arrays.asList(1);
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will use the next pointers
        Cursor<Integer> advanced = cursor.next();
        assertEquals(source, remainingValuesAsList(advanced));
        assertEquals(Collections.<Integer>emptyList(), remainingValuesAsList(advanced.next()));
    }

    public void testMultiValueIterable()
    {
        List<Integer> source = Arrays.asList(1, 2, 3, 4);
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will use the next pointers
        Cursor<Integer> advanced = cursor.next();
        assertEquals(source, remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(2, 3, 4), remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(3, 4), remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(4), remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Collections.<Integer>emptyList(), remainingValuesAsList(advanced.next()));
    }

    private List<Integer> remainingValuesAsList(Cursor<Integer> cursor)
    {
        List<Integer> answer = new ArrayList<Integer>();
        while (cursor.hasValue()) {
            answer.add(cursor.getValue());
            cursor = cursor.next();
        }
        return answer;
    }
}
