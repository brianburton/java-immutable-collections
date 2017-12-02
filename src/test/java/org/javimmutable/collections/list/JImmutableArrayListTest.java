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

package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JImmutableArrayListTest
    extends TestCase
{
    public void test()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list = list.insert(100);
        list.checkInvariants();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insert(200);
        list.checkInvariants();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(200, (int)list.get(1));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insertFirst(80);
        list.checkInvariants();
        assertEquals(3, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        assertEquals(200, (int)list.get(2));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        list.checkInvariants();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteFirst();
        list.checkInvariants();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        list.checkInvariants();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertIterable()
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        StandardCursorTest.emptyCursorTest(list.cursor());

        list = list.insert(asList(1, 2, 3));
        StandardCursorTest.listCursorTest(asList(1, 2, 3), list.cursor());

        list = list.insert(6).insert(asList(10, 11, 12)).insert(20);
        StandardCursorTest.listCursorTest(asList(1, 2, 3, 6, 10, 11, 12, 20), list.cursor());

        list.checkInvariants();
    }

    public void testInsertAllFirst()
    {
        //empty into empty
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        JImmutableArrayList<Integer> expected = list;
        JImmutableArrayList<Integer> checkCursorable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        JImmutableArrayList<Integer> checkCollection = list.insertAllFirst(Collections.emptyList());
        JImmutableArrayList<Integer> checkCursor = list.insertAllFirst(getCursor(Collections.emptyList()));
        JImmutableArrayList<Integer> checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCollection.checkInvariants();

        //values into empty
        expected = list.insert(4).insert(5);
        checkCursorable = list.insertAllFirst(plainIterable(asList(4, 5)));
        checkCollection = list.insertAllFirst(asList(4, 5));
        checkCursor = list.insertAllFirst(getCursor(asList(4, 5)));
        checkIterator = list.insertAllFirst(asList(4, 5).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCursorable.checkInvariants();

        //empty into values
        list = list.insert(4).insert(5);
        expected = list;
        checkCursorable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllFirst(Collections.emptyList());
        checkCursor = list.insertAllFirst(getCursor(Collections.emptyList()));
        checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();

        //values into values
        expected = JImmutableArrayList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3).insert(4).insert(5);
        checkCursorable = list.insertAllFirst(plainIterable(asList(0, 1, 2, 3)));
        checkCollection = list.insertAllFirst(asList(0, 1, 2, 3));
        checkCursor = list.insertAllFirst(getCursor(asList(0, 1, 2, 3)));
        checkIterator = list.insertAllFirst(asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCursor.checkInvariants();
    }

    public void testInsertAllLast()
    {
        //test insertAll
        //empty into empty
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        JImmutableArrayList<Integer> expected = list;
        JImmutableArrayList<Integer> checkCursorable = list.insertAll(plainIterable(Collections.emptyList()));
        JImmutableArrayList<Integer> checkCollection = list.insertAll(Collections.emptyList());
        JImmutableArrayList<Integer> checkCursor = list.insertAll(getCursor(Collections.emptyList()));
        JImmutableArrayList<Integer> checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCollection.checkInvariants();

        //values into empty
        expected = list.insert(0);
        checkCursorable = list.insertAll(plainIterable(Collections.singletonList(0)));
        checkCollection = list.insertAll(Collections.singletonList(0));
        checkCursor = list.insertAll(getCursor(Collections.singletonList(0)));
        checkIterator = list.insertAll(Collections.singletonList(0).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCursorable.checkInvariants();

        //empty into values
        list = list.insert(0);
        expected = list;
        checkCursorable = list.insertAll(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(Collections.emptyList());
        checkCursor = list.insertAll(getCursor(Collections.emptyList()));
        checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();

        //values into values
        expected = list.insert(1).insert(2).insert(3);
        checkCursorable = list.insertAll(plainIterable(asList(1, 2, 3)));
        checkCollection = list.insertAll(asList(1, 2, 3));
        checkCursor = list.insertAll(getCursor(asList(1, 2, 3)));
        checkIterator = list.insertAll(asList(1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCursor.checkInvariants();

        //test insertAllLast
        //empty into empty
        list = JImmutableArrayList.of();
        expected = list;
        checkCursorable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkCursor = list.insertAllLast(getCursor(Collections.emptyList()));
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCursorable.checkInvariants();

        //values into empty
        expected = list.insert(0).insert(1).insert(2).insert(3);
        checkCursorable = list.insertAllLast(plainIterable(asList(0, 1, 2, 3)));
        checkCollection = list.insertAllLast(asList(0, 1, 2, 3));
        checkCursor = list.insertAllLast(getCursor(asList(0, 1, 2, 3)));
        checkIterator = list.insertAll(asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCollection.checkInvariants();

        //empty into values
        list = list.insert(0).insert(1).insert(2).insert(3);
        expected = list;
        checkCursorable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkCursor = list.insertAllLast(getCursor(Collections.emptyList()));
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkCursor.checkInvariants();

        //values into values
        expected = list.insert(4).insert(5);
        checkCursorable = list.insertAllLast(plainIterable(asList(4, 5)));
        checkCollection = list.insertAllLast(asList(4, 5));
        checkCursor = list.insertAllLast(getCursor(asList(4, 5)));
        checkIterator = list.insertAllLast(asList(4, 5).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();


    }

    private Iterable<Integer> plainIterable(List<Integer> values)
    {
        return () -> values.iterator();
    }

    private Cursor<Integer> getCursor(List<Integer> values)
    {
        return IterableCursorable.of(values).cursor();
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
            int kk = 0;
            for (Integer value : list) {
                assertEquals(index - kk, (int)value);
                kk += 1;
            }
            list.checkInvariants();
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
            int kk = 0;
            for (Integer value : list) {
                assertEquals(list.size() - kk - 1, (int)value);
                kk += 1;
            }
            list.checkInvariants();
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        assertEquals(true, list.isEmpty());
        assertEquals(0, list.size());

        try {
            list.deleteFirst();
            fail();
        } catch (IndexOutOfBoundsException ignored) {
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
            int kk = 0;
            for (Integer value : list) {
                assertEquals(kk, (int)value);
                kk += 1;
            }
            list.checkInvariants();
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        for (int index = 0; index < 100; ++index) {
            list = list.deleteLast();
            assertEquals(99 - index, list.size());
            for (int k = 0; k < list.size(); ++k) {
                assertEquals(k, (int)list.get(k));
            }
            int kk = 0;
            for (Integer value : list) {
                assertEquals(kk, (int)value);
                kk += 1;
            }
            list.checkInvariants();
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        assertEquals(true, list.isEmpty());
        assertEquals(0, list.size());

        try {
            list.deleteLast();
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            // expected
        }
    }

    public void testRandom()
    {
        Random random = new Random(100L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(4000);
            JImmutableArrayList<Integer> list = JImmutableArrayList.of();
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i < size; ++i) {
                int value = random.nextInt(10000000);
                if ((value % 2) == 0) {
                    list = ((value % 3) == 0) ? list.insert(value) : list.insertLast(value);
                    expected.add(value);
                } else {
                    list = list.insertFirst(value);
                    expected.add(0, value);
                }
                assertEquals(expected.size(), list.size());
            }
            assertEquals(expected, list.getList());
            list.checkInvariants();

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
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testRandom2()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            List<Integer> expected = new ArrayList<>();
            JImmutableArrayList<Integer> list = JImmutableArrayList.of();

            for (int loops = 0; loops < (4 * size); ++loops) {
                switch (random.nextInt(5)) {
                    case 0: { //insertAllFirst(Cursorable), insertAllFirst(Cursor)
                        List<Integer> values = makeValues(random, size);
                        list = (random.nextBoolean()) ? list.insertAllFirst(plainIterable(values)) : list.insertAllFirst(getCursor(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 1: { //insertAllFirst(Collection)
                        List<Integer> values = makeValues(random, size);
                        list = (random.nextBoolean()) ? list.insertAllFirst(values) : list.insertAllFirst(values.iterator());
                        expected.addAll(0, values);
                        break;
                    }
                    case 2: { //insertAllLast(Cursorable)
                        List<Integer> values = makeValues(random, size);
                        list = (random.nextBoolean()) ? list.insertAllLast(plainIterable(values)) : list.insertAllLast(getCursor(values));
                        expected.addAll(values);
                        break;
                    }
                    case 3: {//insertAllLast(Collection)
                        List<Integer> values = makeValues(random, size);
                        list = (random.nextBoolean()) ? list.insertAllLast(values) : list.insertAllLast(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 4: { //deleteFirst
                        if (list.size() > 0) {
                            list = list.deleteFirst();
                            expected.remove(0);
                        } else {
                            try {
                                list = list.deleteFirst();
                                fail();
                            } catch (IndexOutOfBoundsException ignore) {
                                //expected
                            }
                        }
                        break;
                    }
                    case 5: { //deleteLast
                        if (list.size() > 0) {
                            list = list.deleteLast();
                            expected.remove(expected.size() - 1);
                        } else {
                            try {
                                list = list.deleteLast();
                                fail();
                            } catch (IndexOutOfBoundsException ignore) {
                                //expected
                            }
                        }
                        break;
                    }
                }
                assertEquals(expected.size(), list.size());
            }
            StandardCursorTest.listIteratorTest(expected, list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
            assertEquals(expected, list.getList());
            list = list.deleteAll();
            assertEquals(0, list.size());
            assertEquals(true, list.isEmpty());
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

    public void testSelect()
    {
        JImmutableList<Integer> list = mklist();
        assertSame(list, list.select(x -> false));
        assertSame(list, list.select(x -> true));

        list = mklist(1);
        assertEquals(true, list.select(x -> false).isEmpty());
        assertSame(list, list.select(x -> true));

        list = mklist(1, 2, 3);
        assertEquals(mklist(1, 3), list.select(x -> x % 2 == 1));
        assertEquals(mklist(2), list.select(x -> x % 2 == 0));
    }

    public void testReject()
    {
        JImmutableList<Integer> list = mklist();
        assertSame(list, list.reject(x -> false));
        assertSame(list, list.reject(x -> true));

        list = mklist(1);
        assertSame(list, list.reject(x -> false));
        assertEquals(true, list.reject(x -> true).isEmpty());

        list = mklist(1, 2, 3);
        assertEquals(mklist(2), list.reject(x -> x % 2 == 1));
        assertEquals(mklist(1, 3), list.reject(x -> x % 2 == 0));
    }

    public void testBuilder()
    {
        List<Integer> expected = new ArrayList<>();
        assertSame(EmptyNode.of(), BranchNode.<Integer>builder().build());

        for (int size = 1; size <= 1500; ++size) {
            expected.add(size);
            JImmutableArrayList<Integer> list = JImmutableArrayList.<Integer>builder().add(expected).build();
            list.checkInvariants();
            assertEquals(expected, list.getList());
        }
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

        assertSame(JImmutableArrayList.<Integer>of(), JImmutableArrayList.of(JImmutableArrayList.<Integer>of()));
    }

    public void testStreams()
    {
        JImmutableList<Integer> list = JImmutableArrayList.<Integer>builder().add(1, 2, 3, 4, 5, 6, 7).build();
        assertEquals(asList(1, 2, 3, 4), list.stream().filter(x -> x < 5).collect(toList()));
        assertEquals(asList(1, 2, 3, 4), list.parallelStream().filter(x -> x < 5).collect(toList()));

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 2048; ++i) {
            expected.add(i);
        }
        list = JImmutableArrayList.of(IndexedList.retained(expected));
        assertEquals(expected.stream().collect(toList()), list.stream().collect(toList()));
        assertEquals(expected.parallelStream().collect(toList()), list.parallelStream().collect(toList()));
    }

    public void testParallelStreams()
    {
        final JImmutableList<Integer> original = JImmutableArrayList.of(IndexedList.retained(StandardCursor.makeList(StandardCursor.forRange(1, 10000))));
        assertEquals(original, original.stream().parallel().collect(JImmutableArrayList.of().listCollector()));
        assertEquals(original.getList(), original.stream().parallel().collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableList)a).iterator();
        final JImmutableList<String> empty = JImmutableArrayList.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8sksLgkoyq+o/A8C/1SMeRgYKooY3EgwzDGpuKQoMbmEgKEF5RwMDMwvGYCgAgAFkzNDswAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8sksLgkoyq+o/A8C/1SMeRgYKooY3EgwzDGpuKQoMbmEgKEF5RwMDMwvGRgYGEsYGBMrAOarJ9+3AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, empty.insertAll(asList("a", "b", "c")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8sksLgkoyq+o/A8C/1SMeRgYKooY3EgwzDGpuKQoMbmEgKEF5RwMDMwvGYBECQNjIhAnAXFyBQAT1rKNvwAAAA==");
    }

    private JImmutableList<Integer> mklist(Integer... values)
    {
        return JImmutableArrayList.of(IndexedArray.retained(values));
    }

    private List<Integer> makeValues(Random random,
                                     int size)
    {
        List<Integer> list = new ArrayList<>();
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            list.add(random.nextInt(size));
        }
        return list;
    }
}
