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
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StandardCursorTest
        extends TestCase
{
    public static void testVarious()
    {
        emptyCursorTest(StandardCursor.<Integer>of());
        listCursorTest(Arrays.<Integer>asList(1), StandardCursor.forRange(1, 1));
        listCursorTest(Arrays.<Integer>asList(1, 2), StandardCursor.forRange(1, 2));
        listCursorTest(Arrays.<Integer>asList(-1, 0, 1, 2, 3), StandardCursor.forRange(-1, 3));
        assertEquals(Arrays.<Integer>asList(-1, 0, 1, 2, 3), StandardCursor.makeList(StandardCursor.forRange(-1, 3)));

        emptyIteratorTest(StandardCursor.iterator(new StandardCursor.RangeSource(1, -1)));
        listIteratorTest(Arrays.asList(1), StandardCursor.iterator(new StandardCursor.RangeSource(1, 1)));
        listIteratorTest(Arrays.asList(1, 2), StandardCursor.iterator(new StandardCursor.RangeSource(1, 2)));
        listIteratorTest(Arrays.asList(1, 2, 3), StandardCursor.iterator(new StandardCursor.RangeSource(1, 3)));
    }

    public static <T> void cursorTest(Func1<Integer, T> lookup,
                                      int size,
                                      Cursor<T> cursor)
    {
        // have to call next() before other methods
        try {
            cursor.hasValue();
            fail();
        } catch (Cursor.NotStartedException ex) {
            // expected
        }
        try {
            cursor.getValue();
            fail();
        } catch (Cursor.NotStartedException ex) {
            // expected
        }

        // calling next advances through entire sequence
        for (int i = 0; i < size; ++i) {
            cursor = cursor.next();
            assertEquals(true, cursor.hasValue());
            assertEquals(lookup.apply(i), cursor.getValue());
        }

        // after expected sequence has no values
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        try {
            cursor.getValue();
            fail();
        } catch (Cursor.NoValueException ex) {
            // expected
        }

        // safe to call multiple times once at end
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
    }

    public static <T> void indexedCursorTest(Indexed<T> indexed,
                                             int size,
                                             Cursor<T> cursor)
    {
        cursorTest(new IndexedLookup<T>(indexed), size, cursor);
    }

    public static <T> void listCursorTest(List<T> list,
                                          Cursor<T> cursor)
    {
        cursorTest(new ListLookup<T>(list), list.size(), cursor);
    }

    public static <T> void emptyCursorTest(Cursor<T> cursor)
    {
        listCursorTest(Collections.<T>emptyList(), cursor);
    }

    public static <T> void iteratorTest(Func1<Integer, T> lookup,
                                        int size,
                                        Iterator<T> iterator)
    {
        // calling next advances through entire sequence
        for (int i = 0; i < size; ++i) {
            assertEquals(true, iterator.hasNext());
            assertEquals(lookup.apply(i), iterator.next());
        }

        // after expected sequence has no values
        assertEquals(false, iterator.hasNext());

        // safe to call multiple times once at end
        assertEquals(false, iterator.hasNext());
    }

    public static <T> void indexedIteratorTest(Indexed<T> indexed,
                                               int size,
                                               Iterator<T> iterator)
    {
        iteratorTest(new IndexedLookup<T>(indexed), size, iterator);
    }

    public static <T> void listIteratorTest(List<T> list,
                                            Iterator<T> iterator)
    {
        iteratorTest(new ListLookup<T>(list), list.size(), iterator);
    }

    public static <T> void emptyIteratorTest(Iterator<T> iterator)
    {
        listIteratorTest(Collections.<T>emptyList(), iterator);
    }

    private static class ListLookup<T>
            implements Func1<Integer, T>
    {
        private final List<T> list;

        private ListLookup(List<T> list)
        {
            this.list = list;
        }

        @Override
        public T apply(Integer value)
        {
            return list.get(value);
        }
    }

    private static class IndexedLookup<T>
            implements Func1<Integer, T>
    {
        private final Indexed<T> indexed;

        private IndexedLookup(Indexed<T> indexed)
        {
            this.indexed = indexed;
        }

        @Override
        public T apply(Integer value)
        {
            return indexed.get(value);
        }
    }
}
