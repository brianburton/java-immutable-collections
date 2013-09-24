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
import junit.framework.TestCase;

public class MultiCursorTest
        extends TestCase
{
    public void testTwoEmpty()
    {
        Cursor<Integer> multi = MultiCursor.of(EmptyCursor.<Integer>of(), EmptyCursor.<Integer>of());
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    public void testThreeEmpty()
    {
        Cursor<Integer> multi = MultiCursor.of(EmptyCursor.<Integer>of(), EmptyCursor.<Integer>of(), EmptyCursor.<Integer>of());
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    public void testOneNonEmpty()
    {
        Cursor<Integer> multi = MultiCursor.of(EmptyCursor.<Integer>of(), SingleValueCursor.<Integer>of(100), EmptyCursor.<Integer>of());
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(100, (int)multi.getValue());

        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    public void testNoneEmpty()
    {
        Cursor<Integer> multi = MultiCursor.of(SingleValueCursor.<Integer>of(100), SingleValueCursor.<Integer>of(200), SingleValueCursor.<Integer>of(300));
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(100, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(200, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(300, (int)multi.getValue());

        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    public void testNested()
    {
        Cursor<Integer> multi = MultiCursor.of(MultiCursor.of(SingleValueCursor.<Integer>of(100),
                                                              SingleValueCursor.<Integer>of(200)),
                                               MultiCursor.of(SingleValueCursor.<Integer>of(300),
                                                              SingleValueCursor.<Integer>of(400),
                                                              SingleValueCursor.of(500)));
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(100, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(200, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(300, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(400, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(500, (int)multi.getValue());

        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }
}
