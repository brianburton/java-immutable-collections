///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitCursor;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.cursors.StandardCursor.*;

public class StandardCursorTest
    extends TestCase
{
    public static void testVarious()
    {
        emptyCursorTest(StandardCursor.<Integer>of());
        listCursorTest(Arrays.asList(1), forRange(1, 1));
        listCursorTest(Arrays.asList(1, 2), forRange(1, 2));
        listCursorTest(Arrays.asList(-1, 0, 1, 2, 3), forRange(-1, 3));
        assertEquals(Arrays.asList(-1, 0, 1, 2, 3), StandardCursor.makeList(forRange(-1, 3)));

        emptyIteratorTest(StandardCursor.iterator(new StandardCursor.RangeSource(1, -1)));
        listIteratorTest(Arrays.asList(1), StandardCursor.iterator(new StandardCursor.RangeSource(1, 1)));
        listIteratorTest(Arrays.asList(1, 2), StandardCursor.iterator(new StandardCursor.RangeSource(1, 2)));
        listIteratorTest(Arrays.asList(1, 2, 3), StandardCursor.iterator(new StandardCursor.RangeSource(1, 3)));

        emptyCursorTest(StandardCursor.of(IndexedList.retained(Collections.<Integer>emptyList())));
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i < 10; ++i) {
            source.add(i);
            listCursorTest(source, StandardCursor.of(IndexedList.retained(source)));
        }

        emptyCursorTest(StandardCursor.of(() -> new StandardCursor.RangeSource(1, -1)));
        listCursorTest(Arrays.asList(1), StandardCursor.of(() -> new StandardCursor.RangeSource(1, 1)));
        listCursorTest(Arrays.asList(1, 2), StandardCursor.of(() -> new StandardCursor.RangeSource(1, 2)));
    }

    public void testSplitAllowed()
    {
        assertEquals(false, forRange(1, 0).start().isSplitAllowed());
        assertEquals(false, forRange(1, 1).start().isSplitAllowed());
        assertEquals(true, forRange(1, 2).start().isSplitAllowed());
        assertEquals(true, forRange(1, 3).start().isSplitAllowed());

        assertEquals(false, repeatCursor(-1, 0).start().isSplitAllowed());
        assertEquals(false, repeatCursor(-1, 1).start().isSplitAllowed());
        assertEquals(true, repeatCursor(-1, 2).start().isSplitAllowed());
        assertEquals(true, repeatCursor(-1, 3).start().isSplitAllowed());
        assertEquals(true, repeatCursor(-1, 4).start().isSplitAllowed());

        assertEquals(false, indexedCursor().start().isSplitAllowed());
        assertEquals(false, indexedCursor(1).start().isSplitAllowed());
        assertEquals(true, indexedCursor(1, 2).start().isSplitAllowed());
        assertEquals(true, indexedCursor(1, 2, 3).start().isSplitAllowed());
        assertEquals(true, indexedCursor(1, 2, 3, 4).start().isSplitAllowed());
    }

    public void testSplit()
    {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> forRange(1, 0).start().splitCursor());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> forRange(1, 1).start().splitCursor());
        verifySplit(forRange(1, 2).start(), asList(1), asList(2));
        verifySplit(forRange(1, 3).start(), asList(1, 2), asList(3));
        verifySplit(forRange(1, 4).start(), asList(1, 2), asList(3, 4));
        verifySplit(forRange(1, 5).start(), asList(1, 2, 3), asList(4, 5));

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> repeatCursor(-1, 0).start().splitCursor());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> repeatCursor(-1, 1).start().splitCursor());
        verifySplit(repeatCursor(-1, 2).start(), asList(-1), asList(-1));
        verifySplit(repeatCursor(-1, 3).start(), asList(-1), asList(-1, -1));
        verifySplit(repeatCursor(-1, 4).start(), asList(-1, -1), asList(-1, -1));
        verifySplit(repeatCursor(-1, 5).start(), asList(-1, -1), asList(-1, -1, -1));

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> indexedCursor().start().splitCursor());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> indexedCursor(1).start().splitCursor());
        StandardCursorTest.verifySplit(indexedCursor(1, 2).start(), asList(1), asList(2));
        StandardCursorTest.verifySplit(indexedCursor(1, 2, 3).start(), asList(1), asList(2, 3));
        StandardCursorTest.verifySplit(indexedCursor(1, 2, 3, 4).start(), asList(1, 2), asList(3, 4));
    }

    private Cursor<Integer> repeatCursor(int value,
                                         int count)
    {
        return StandardCursor.repeating(count, value);
    }

    private Cursor<Integer> indexedCursor(Integer... values)
    {
        return StandardCursor.of(IndexedArray.retained(values));
    }

    private Cursorable<Integer> rangeCursorable(int low,
                                                int high)
    {
        return () -> forRange(low, high);
    }

    public static <T> void cursorTest(Func1<Integer, T> lookup,
                                      int size,
                                      Cursor<T> cursor)
    {
        // have to call next() before other methods
        try {
            cursor.hasValue();
            fail();
        } catch (Cursor.NotStartedException ignored) {
            // expected
        }
        try {
            cursor.getValue();
            fail();
        } catch (Cursor.NotStartedException ignored) {
            // expected
        }

        final int iteratorTestMultiple = Math.max(1, size / 5);
        // calling next advances through entire sequence
        cursor = cursor.start();
        for (int i = 0, last = size - 1; i < size; ++i) {
            assertEquals(true, cursor.hasValue());
            assertEquals(lookup.apply(i), cursor.getValue());

            if ((i % iteratorTestMultiple) == 0) {
                verifyIterableCursor(lookup, i, size, cursor);
            }

            // calling start in mid-traversal must not disrupt the sequence
            cursor = cursor.start();

            // ready for next loop
            if (i < last) {
                cursor = cursor.next();
            }
        }

        // after expected sequence has no values
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        try {
            cursor.getValue();
            fail();
        } catch (Cursor.NoValueException ignored) {
            // expected
        }

        // safe to call multiple times once at end
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
    }

    private static <T> void verifyIterableCursor(Func1<Integer, T> lookup,
                                                 int offset,
                                                 int size,
                                                 Cursor<T> cursor)
    {
        for (T value : cursor) {
            assertEquals(lookup.apply(offset), value);
            offset += 1;
        }
        assertEquals(size, offset);
    }

    public static <T> void indexedCursorTest(Indexed<T> indexed,
                                             int size,
                                             Cursor<T> cursor)
    {
        cursorTest(new IndexedLookup<>(indexed), size, cursor);
    }

    public static <T> void listCursorTest(List<T> list,
                                          Cursor<T> cursor)
    {
        assertEquals(list, makeList(cursor));
        cursorTest(new ListLookup<>(list), list.size(), cursor);
    }

    public static <T> void emptyCursorTest(Cursor<T> cursor)
    {
        listCursorTest(Collections.emptyList(), cursor);
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

        // calling next() at end throws
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
            // expected
        }

        // safe to call multiple times once at end
        assertEquals(false, iterator.hasNext());
    }

    public static <T> void indexedIteratorTest(Indexed<T> indexed,
                                               int size,
                                               Iterator<T> iterator)
    {
        iteratorTest(new IndexedLookup<>(indexed), size, iterator);
    }

    public static <T> void listIteratorTest(List<T> list,
                                            Iterator<T> iterator)
    {
        iteratorTest(new ListLookup<>(list), list.size(), iterator);
    }

    public static <T> void emptyIteratorTest(Iterator<T> iterator)
    {
        listIteratorTest(Collections.emptyList(), iterator);
    }

    public static <T> void verifySplit(Cursor<T> cursor,
                                       List<T> left,
                                       List<T> right)
    {
        SplitCursor<T> split = cursor.splitCursor();
        listCursorTest(left, split.getLeft());
        listCursorTest(right, split.getRight());
    }

    public static <T> void verifySplit(SplitableIterator<T> cursor,
                                       List<T> left,
                                       List<T> right)
    {
        SplitIterator<T> split = cursor.splitIterator();
        listIteratorTest(left, split.getLeft());
        listIteratorTest(right, split.getRight());
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
