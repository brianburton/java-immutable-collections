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

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.list.JImmutableLinkedStack;

public class SequenceCursorTest
        extends TestCase
{
    public void testEmptyList()
    {
        JImmutableLinkedStack<Integer> list = JImmutableLinkedStack.of();
        Cursor<Integer> cursor = SequenceCursor.of(list);
        try {
            cursor.hasValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            cursor.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }

        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        try {
            cursor.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }

        assertTrue(cursor.next() instanceof EmptyStartedCursor);
    }

    public void testSingleList()
    {
        JImmutableLinkedStack<Integer> list = JImmutableLinkedStack.of(100);
        Cursor<Integer> cursor = SequenceCursor.of(list);
        try {
            cursor.hasValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            cursor.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals(100, (int)cursor.getValue());

        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        try {
            cursor.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    public void testMultiList()
    {
        JImmutableLinkedStack<Integer> list = JImmutableLinkedStack.of(8, 7, 6, 5, 4, 3, 2, 1);
        Cursor<Integer> cursor = SequenceCursor.of(list);
        try {
            cursor.hasValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            cursor.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }

        for (int i = 1; i <= 8; ++i) {
            cursor = cursor.next();
            assertEquals(true, cursor.hasValue());
            assertEquals(i, (int)cursor.getValue());
        }
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        try {
            cursor.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
    }
}
