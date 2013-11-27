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

package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class JImmutableArrayListTest
        extends TestCase
{
    public void test()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list = list.insert(100);
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insert(200);
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(200, (int)list.get(1));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insertFirst(80);
        assertEquals(3, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        assertEquals(200, (int)list.get(2));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteFirst();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertDeleteFirst()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        for (int index = 0; index < 100; ++index) {
            list = list.insertFirst(index);
            assertEquals(index + 1, list.size());
            for (int k = 0; k <= index; ++k) {
                assertEquals(index - k, (int)list.get(k));
            }
            {
                int kk = 0;
                for (Integer value : list) {
                    assertEquals(index - kk, (int)value);
                    kk += 1;
                }
            }
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        for (int index = 0; index < 100; ++index) {
            assertEquals(list.size() - 1, (int)list.get(0));
            list = list.deleteFirst();
            assertEquals(99 - index, list.size());
            for (int k = 0; k < list.size(); ++k) {
                assertEquals(list.size() - k - 1, (int)list.get(k));
            }
            {
                int kk = 0;
                for (Integer value : list) {
                    assertEquals(list.size() - kk - 1, (int)value);
                    kk += 1;
                }
            }
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        assertEquals(true, list.isEmpty());
        assertEquals(0, list.size());

        try {
            list.deleteFirst();
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    public void testDeleteLast()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        for (int index = 0; index < 100; ++index) {
            list = list.insert(index);
            assertEquals(index + 1, list.size());
            for (int k = 0; k <= index; ++k) {
                assertEquals(k, (int)list.get(k));
            }
            {
                int kk = 0;
                for (Integer value : list) {
                    assertEquals(kk, (int)value);
                    kk += 1;
                }
            }
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        for (int index = 0; index < 100; ++index) {
            list = list.deleteLast();
            assertEquals(99 - index, list.size());
            for (int k = 0; k < list.size(); ++k) {
                assertEquals(k, (int)list.get(k));
            }
            {
                int kk = 0;
                for (Integer value : list) {
                    assertEquals(kk, (int)value);
                    kk += 1;
                }
            }
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        assertEquals(true, list.isEmpty());
        assertEquals(0, list.size());

        try {
            list.deleteLast();
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    public void testRandom()
    {
        Random random = new Random(100);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(4000);
            JImmutableArrayList<Integer> list = JImmutableArrayList.of();
            List<Integer> expected = new ArrayList<Integer>();
            for (int i = 0; i < size; ++i) {
                int value = random.nextInt(10000000);
                if (value % 2 == 0) {
                    list = (value % 3 == 0) ? list.insert(value) : list.insertLast(value);
                    expected.add(value);
                } else {
                    list = list.insertFirst(value);
                    expected.add(0, value);
                }
                assertEquals(expected.size(), list.size());
            }
            assertEquals(expected, list.getList());

            Cursor<Integer> cursor = list.cursor().next();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, cursor.hasValue());
                assertEquals(expected.get(i), list.get(i));
                assertEquals(expected.get(i), cursor.getValue());
                cursor = cursor.next();
            }
            assertEquals(false, cursor.hasValue());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }
    }

    public void testCursor()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        Cursor<Integer> cursor = list.cursor().next();
        assertEquals(false, cursor.hasValue());

        for (int size = 1; size <= 10; ++size) {
            list = list.insert(size);
            cursor = list.cursor();
            for (int i = 0; i < size; ++i) {
                cursor = cursor.next();
                assertEquals(true, cursor.hasValue());
                assertEquals(Integer.valueOf(i + 1), cursor.getValue());
            }
            cursor = cursor.next();
            assertEquals(false, cursor.hasValue());
        }

        list = JImmutableArrayList.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertFirst(size);
            cursor = list.cursor();
            for (int i = 0; i < size; ++i) {
                cursor = cursor.next();
                assertEquals(true, cursor.hasValue());
                assertEquals(Integer.valueOf(size - i), cursor.getValue());
            }
            cursor = cursor.next();
            assertEquals(false, cursor.hasValue());
        }

        list = JImmutableArrayList.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertLast(size);
            cursor = list.cursor();
            for (int i = 0; i < size; ++i) {
                cursor = cursor.next();
                assertEquals(true, cursor.hasValue());
                assertEquals(Integer.valueOf(i + 1), cursor.getValue());
            }
            cursor = cursor.next();
            assertEquals(false, cursor.hasValue());
        }

        Cursor<Integer> oldCursor = list.cursor();
        //noinspection UnusedAssignment
        list = list.deleteFirst().deleteLast();
        cursor = oldCursor;
        for (int i = 0; i < 10; ++i) {
            cursor = cursor.next();
            assertEquals(true, cursor.hasValue());
            assertEquals(Integer.valueOf(i + 1), cursor.getValue());
        }
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
    }

    public void testIterator()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        Iterator<Integer> iterator = list.iterator();
        assertEquals(false, iterator.hasNext());

        for (int size = 1; size <= 10; ++size) {
            list = list.insert(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(i + 1), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = JImmutableArrayList.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertFirst(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(size - i), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = JImmutableArrayList.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertLast(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(i + 1), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }
    }

    public void testDeleteAll()
    {
        JImmutableList<Integer> list = JImmutableArrayList.<Integer>of().insert(1).insert(2);
        assertSame(JImmutableArrayList.of(), list.deleteAll());
    }

    public void testIndexedConstructor()
    {
        Integer[] values = new Integer[33 * 32];
        for (int i = 0; i < values.length; ++i) {
            values[i] = i;
        }
        final IndexedArray<Integer> source = IndexedArray.retained(values);
        for (int offset = 0; offset < values.length; ++offset) {
            for (int limit = offset; limit <= values.length; ++limit) {
                final int size = limit - offset;
                JImmutableArrayList<Integer> list = JImmutableArrayList.of(source, offset, limit);
                if (size == 0) {
                    assertSame(JImmutableArrayList.<Integer>of(), list);
                }
                for (int i = 0; i < size; ++i) {
                    final Integer value = list.get(i);
                    assertEquals(values[offset + i], value);
                }
            }
        }

        assertSame(JImmutableArrayList.<Integer>of(), JImmutableArrayList.<Integer>of(JImmutableArrayList.<Integer>of()));
    }
}
