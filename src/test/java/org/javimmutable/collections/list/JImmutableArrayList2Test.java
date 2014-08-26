package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class JImmutableArrayList2Test
        extends TestCase
{
    public void test()
    {
        JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
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
        JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
        StandardCursorTest.emptyCursorTest(list.cursor());

        list = list.insert(Arrays.asList(1, 2, 3));
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), list.cursor());

        list = list.insert(6).insert(Arrays.asList(10, 11, 12)).insert(20);
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3, 6, 10, 11, 12, 20), list.cursor());

        list.checkInvariants();
    }

    public void testInsertDeleteFirst()
    {
        JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
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
        JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
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
            JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
            List<Integer> expected = new ArrayList<Integer>();
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
        }
    }

    public void testCursor()
    {
        JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
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

        list = JImmutableArrayList2.of();
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

        list = JImmutableArrayList2.of();
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
        JImmutableArrayList2<Integer> list = JImmutableArrayList2.of();
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

        list = JImmutableArrayList2.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertFirst(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(size - i), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = JImmutableArrayList2.of();
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
        JImmutableList<Integer> list = JImmutableArrayList2.<Integer>of().insert(1).insert(2);
        assertSame(JImmutableArrayList2.of(), list.deleteAll());
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
                JImmutableArrayList2<Integer> list = JImmutableArrayList2.of(source, offset, limit);
                if (size == 0) {
                    assertSame(JImmutableArrayList2.<Integer>of(), list);
                }
                for (int i = 0; i < size; ++i) {
                    final Integer value = list.get(i);
                    assertEquals(values[offset + i], value);
                }
            }
        }

        assertSame(JImmutableArrayList2.<Integer>of(), JImmutableArrayList2.of(JImmutableArrayList2.<Integer>of()));
    }
}
