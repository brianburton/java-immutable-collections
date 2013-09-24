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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import junit.framework.TestCase;

public class TransformCursorTest
        extends TestCase
{
    public void testEmpty()
    {
        Cursor<String> cursor = TransformCursor.of(EmptyCursor.<Integer>of(), new Func1<Integer, String>()
        {
            @Override
            public String apply(Integer value)
            {
                return String.valueOf(value);
            }
        });

        try {
            cursor.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            cursor.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }

        assertEquals(false, cursor.hasValue());
    }

    public void testRange()
    {
        Cursor<String> cursor = TransformCursor.of(StandardCursor.forRange(3, 6), new Func1<Integer, String>()
        {
            @Override
            public String apply(Integer value)
            {
                return String.valueOf(value);
            }
        });
        try {
            cursor.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            cursor.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }

        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("3", cursor.getValue());

        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("4", cursor.getValue());

        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("5", cursor.getValue());

        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("6", cursor.getValue());

        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
    }
}
