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
import org.javimmutable.collections.Cursorable;
import junit.framework.TestCase;

public class LazyCursorTest
        extends TestCase
{
    public void test()
    {
        TestIterable source = new TestIterable();
        assertEquals(false, source.isCreated());

        LazyCursor<String> lazyIterator = new LazyCursor<String>(source);
        assertEquals(false, source.isCreated());
        try {
            lazyIterator.hasValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        assertEquals(false, source.isCreated());

        try {
            lazyIterator.getValue();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        assertEquals(false, source.isCreated());

        Cursor<String> cursor = lazyIterator.next();
        assertEquals(true, source.isCreated());
        assertTrue(cursor instanceof TestIterator);

        TestIterator testIterator = (TestIterator)cursor;
        assertEquals(true, testIterator.isAdvanced());
        assertEquals("success!", testIterator.getValue());
    }

    private static class TestIterator
            implements Cursor<String>
    {
        private boolean advanced;

        private TestIterator()
        {
            advanced = false;
        }

        @Override
        public Cursor<String> next()
        {
            if (advanced) {
                throw new RuntimeException("advanced twice!");
            }
            advanced = true;
            return this;
        }

        @Override
        public boolean hasValue()
        {
            return advanced;
        }

        private boolean isAdvanced()
        {
            return advanced;
        }

        public String getValue()
        {
            if (advanced) {
                return "success!";
            } else {
                return "not advanced";
            }
        }
    }

    private static class TestIterable
            implements Cursorable<String>
    {
        private boolean created;

        private TestIterable()
        {
            created = false;
        }

        private boolean isCreated()
        {
            return created;
        }

        @Override
        public Cursor<String> cursor()
        {
            if (created) {
                throw new RuntimeException("created twice!");
            }
            created = true;
            return new TestIterator();
        }
    }
}
