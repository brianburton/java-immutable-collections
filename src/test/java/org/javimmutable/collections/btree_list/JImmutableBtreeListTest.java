///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.btree_list;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.common.StandardJImmutableListTests;
import org.javimmutable.collections.common.StandardMutableBuilderTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class JImmutableBtreeListTest
    extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableListTests.standardTests(JImmutableBtreeList.of());
    }

    public void test()
    {
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list = list.insert(100);
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insert(200);
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(200, (int)list.get(1));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        JImmutableBtreeList<Integer> saved = list;

        list = list.insertFirst(80);
        assertEquals(3, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        assertEquals(200, (int)list.get(2));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteFirst();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = saved;
        list = list.assign(1, 210);
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(210, (int)list.get(1));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.delete(0);
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(210, (int)list.get(0));
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.delete(0);
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertIterable()
    {
        JImmutableRandomAccessList<Integer> list = JImmutableBtreeList.of();
        StandardCursorTest.emptyCursorTest(list.cursor());

        list = list.insert(Arrays.asList(1, 2, 3));
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), list.cursor());

        list = list.insert(6).insert(Arrays.asList(10, 11, 12)).insert(20);
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3, 6, 10, 11, 12, 20), list.cursor());
    }

    public void testInsertAllAtIndex()
    {
        JImmutableBtreeList<Integer> checkCursorable;
        JImmutableBtreeList<Integer> checkCollection;
        JImmutableBtreeList<Integer> checkCursor;
        JImmutableBtreeList<Integer> checkIterator;

        //insert at negative index -- all should fail
        //empty into empty
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        try {
            list.insertAll(-1, plainIterable(Collections.emptyList()));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Collections.emptyList());
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, getCursor(Collections.emptyList()));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Collections.<Integer>emptyList().iterator());
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //values into empty
        try {
            list.insertAll(-1, plainIterable(Arrays.asList(0, 1, 2, 3)));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Arrays.asList(0, 1, 2, 3));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, getCursor(Arrays.asList(0, 1, 2, 3)));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Arrays.asList(0, 1, 2, 3));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //empty into values
        list = list.insert(4).insert(5);
        try {
            list.insertAll(-1, plainIterable(Collections.emptyList()));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Collections.emptyList());
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, getCursor(Collections.emptyList()));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Collections.<Integer>emptyList().iterator());
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //values into values
        try {
            list.insertAll(-1, plainIterable(Arrays.asList(0, 1, 2, 3)));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Arrays.asList(0, 1, 2, 3));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, getCursor(Arrays.asList(0, 1, 2, 3)));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(-1, Arrays.asList(0, 1, 2, 3));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //insert at 0 -- all should work
        //empty into empty
        list = JImmutableBtreeList.of();
        JImmutableBtreeList<Integer> expected = list;
        checkCursorable = list.insertAll(0, plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(0, Collections.emptyList());
        checkCursor = list.insertAll(0, getCursor(Collections.emptyList()));
        checkIterator = list.insertAll(0, Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(4).insert(5);
        checkCursorable = list.insertAll(0, plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAll(0, Arrays.asList(4, 5));
        checkCursor = list.insertAll(0, getCursor(Arrays.asList(4, 5)));
        checkIterator = list.insertAll(0, Arrays.asList(4, 5).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //empty into values
        list = list.insert(4).insert(5);
        checkCursorable = list.insertAll(0, plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(0, Collections.emptyList());
        checkCursor = list.insertAll(0, getCursor(Collections.emptyList()));
        checkIterator = list.insertAll(0, Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //values into values
        expected = JImmutableBtreeList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3).insert(4).insert(5);
        checkCursorable = list.insertAll(0, plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAll(0, Arrays.asList(0, 1, 2, 3));
        checkCursor = list.insertAll(0, getCursor(Arrays.asList(0, 1, 2, 3)));
        checkIterator = list.insertAll(0, Arrays.asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //insert in middle
        //empty into empty -- should fail
        list = JImmutableBtreeList.of();
        try {
            list.insertAll(1, plainIterable(Collections.emptyList()));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(1, Collections.emptyList());
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(1, getCursor(Collections.emptyList()));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(1, Collections.<Integer>emptyList().iterator());
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //values into empty -- should fail
        try {
            list.insertAll(1, plainIterable(Arrays.asList(1, 2)));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(1, Arrays.asList(1, 2));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(1, getCursor(Arrays.asList(1, 2)));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(1, Arrays.asList(1, 2));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //empty into values -- should work
        list = list.insert(0).insert(3);
        expected = list;
        checkCursorable = list.insertAll(0, plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(0, Collections.emptyList());
        checkCursor = list.insertAll(0, getCursor(Collections.emptyList()));
        checkIterator = list.insertAll(0, Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //values into values -- should work
        expected = JImmutableBtreeList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3);
        checkCursorable = list.insertAll(1, plainIterable(Arrays.asList(1, 2)));
        checkCollection = list.insertAll(1, Arrays.asList(1, 2));
        checkCursor = list.insertAll(1, getCursor(Arrays.asList(1, 2)));
        checkIterator = list.insertAll(1, Arrays.asList(1, 2).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //insert at size -- all should work
        //empty into empty
        list = JImmutableBtreeList.of();
        expected = list;
        checkCursorable = list.insertAll(list.size(), plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(list.size(), Collections.emptyList());
        checkCursor = list.insertAll(list.size(), getCursor(Collections.emptyList()));
        checkIterator = list.insertAll(list.size(), Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(0).insert(1).insert(2).insert(3);
        checkCursorable = list.insertAll(list.size(), plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAll(list.size(), Arrays.asList(0, 1, 2, 3));
        checkCursor = list.insertAll(list.size(), getCursor(Arrays.asList(0, 1, 2, 3)));
        checkIterator = list.insertAll(list.size(), Arrays.asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //empty into values
        list = list.insert(0).insert(1).insert(2).insert(3);
        checkCursorable = list.insertAll(list.size(), plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(list.size(), Collections.emptyList());
        checkCursor = list.insertAll(list.size(), getCursor(Collections.emptyList()));
        checkIterator = list.insertAll(list.size(), Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //values into values
        expected = expected.insert(4).insert(5);
        checkCursorable = list.insertAll(list.size(), plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAll(list.size(), Arrays.asList(4, 5));
        checkCursor = list.insertAll(list.size(), getCursor(Arrays.asList(4, 5)));
        checkIterator = list.insertAll(list.size(), Arrays.asList(4, 5).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //insert at >size -- all should fail
        // empty into empty
        list = JImmutableBtreeList.of();
        try {
            list.insertAll(list.size() + 1, plainIterable(Collections.emptyList()));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Collections.emptyList());
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, getCursor(Collections.emptyList()));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Collections.<Integer>emptyList().iterator());
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //values into empty
        try {
            list.insertAll(list.size() + 1, plainIterable(Arrays.asList(0, 1, 2, 3)));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Arrays.asList(0, 1, 2, 3));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, getCursor(Arrays.asList(0, 1, 2, 3)));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Arrays.asList(0, 1, 2, 3));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //empty into values
        list = list.insert(4).insert(5);
        try {
            list.insertAll(list.size() + 1, plainIterable(Collections.emptyList()));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Collections.emptyList());
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, getCursor(Collections.emptyList()));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Collections.<Integer>emptyList().iterator());
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //values into values
        try {
            list.insertAll(list.size() + 1, plainIterable(Arrays.asList(0, 1, 2, 3)));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Arrays.asList(0, 1, 2, 3));
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, getCursor(Arrays.asList(0, 1, 2, 3)));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
        try {
            list.insertAll(list.size() + 1, Arrays.asList(0, 1, 2, 3));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
    }

    public void testInsertAllFirst()
    {
        //empty into empty
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        JImmutableBtreeList<Integer> expected = list;
        JImmutableBtreeList<Integer> checkCursorable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        JImmutableBtreeList<Integer> checkCollection = list.insertAllFirst(Collections.emptyList());
        JImmutableBtreeList<Integer> checkCursor = list.insertAllFirst(getCursor(Collections.emptyList()));
        JImmutableBtreeList<Integer> checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        JImmutableBtreeList<Integer> checkBtree = list.insertAllFirst(btree());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkBtree);

        //values into empty
        expected = list.insert(4).insert(5);
        checkCursorable = list.insertAllFirst(plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAllFirst(Arrays.asList(4, 5));
        checkCursor = list.insertAllFirst(getCursor(Arrays.asList(4, 5)));
        checkIterator = list.insertAllFirst(Arrays.asList(4, 5).iterator());
        checkBtree = list.insertAllFirst(btree(4, 5));
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkBtree);

        //empty into values
        list = list.insert(4).insert(5);
        expected = list;
        checkCursorable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllFirst(Collections.emptyList());
        checkCursor = list.insertAllFirst(getCursor(Collections.emptyList()));
        checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        checkBtree = list.insertAllFirst(btree());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkBtree);

        //values into values
        expected = JImmutableBtreeList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3).insert(4).insert(5);
        checkCursorable = list.insertAllFirst(plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAllFirst(Arrays.asList(0, 1, 2, 3));
        checkCursor = list.insertAllFirst(getCursor(Arrays.asList(0, 1, 2, 3)));
        checkIterator = list.insertAllFirst(Arrays.asList(0, 1, 2, 3).iterator());
        checkBtree = list.insertAllFirst(btree(0, 1, 2, 3));
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkBtree);
    }

    public void testAppend()
    {
        assertEquals(range(1, 50), range(1, 15).insertAllLast(range(16, 50)));
        assertEquals(range(1, 50), range(36, 50).insertAllFirst(range(1, 35)));

        assertEquals(range(1, 600), range(1, 500).insertAllLast(range(501, 600)));
        assertEquals(range(1, 600), range(501, 600).insertAllFirst(range(1, 500)));

        assertEquals(range(1, 600), range(1, 100).insertAllLast(range(101, 600)));
        assertEquals(range(1, 600), range(101, 600).insertAllFirst(range(1, 100)));
    }

    @SafeVarargs
    private final <T> JImmutableBtreeList<T> btree(T... values)
    {
        return JImmutableBtreeList.of(IndexedArray.retained(values));
    }

    private JImmutableBtreeList<Integer> range(int first,
                                               int last)
    {
        return JImmutableBtreeList.<Integer>builder()
            .add(StandardCursor.forRange(first, last))
            .build();
    }

    public void testInsertAllLast()
    {
        //test insertAll
        //empty into empty
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        JImmutableBtreeList<Integer> expected = list;
        JImmutableBtreeList<Integer> checkCursorable = list.insertAll(plainIterable(Collections.emptyList()));
        JImmutableBtreeList<Integer> checkCollection = list.insertAll(Collections.emptyList());
        JImmutableBtreeList<Integer> checkCursor = list.insertAll(getCursor(Collections.emptyList()));
        JImmutableBtreeList<Integer> checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

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

        //values into values
        expected = list.insert(1).insert(2).insert(3);
        checkCursorable = list.insertAll(plainIterable(Arrays.asList(1, 2, 3)));
        checkCollection = list.insertAll(Arrays.asList(1, 2, 3));
        checkCursor = list.insertAll(getCursor(Arrays.asList(1, 2, 3)));
        checkIterator = list.insertAll(Arrays.asList(1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //test insertAllLast
        //empty into empty
        list = JImmutableBtreeList.of();
        expected = list;
        checkCursorable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkCursor = list.insertAllLast(getCursor(Collections.emptyList()));
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(0).insert(1).insert(2).insert(3);
        checkCursorable = list.insertAllLast(plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAllLast(Arrays.asList(0, 1, 2, 3));
        checkCursor = list.insertAllLast(getCursor(Arrays.asList(0, 1, 2, 3)));
        checkIterator = list.insertAll(Arrays.asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);

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

        //values into values
        expected = list.insert(4).insert(5);
        checkCursorable = list.insertAllLast(plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAllLast(Arrays.asList(4, 5));
        checkCursor = list.insertAllLast(getCursor(Arrays.asList(4, 5)));
        checkIterator = list.insertAllLast(Arrays.asList(4, 5).iterator());
        assertEquals(expected, checkCursorable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkCursor);
        assertEquals(expected, checkIterator);
    }

    public void testInsertDeleteFirst()
    {
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        for (int index = 0; index < 100; ++index) {
            list = list.insertFirst(index);
            list.checkInvariants();
            assertEquals(index + 1, list.size());
            for (int k = 0; k <= index; ++k) {
                assertEquals(index - k, (int)list.get(k));
            }

            int kk = 0;
            for (Integer value : list) {
                assertEquals(index - kk, (int)value);
                kk += 1;
            }
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
        }

        for (int index = 0; index < 100; ++index) {
            assertEquals(list.size() - 1, (int)list.get(0));
            list = list.deleteFirst();
            list.checkInvariants();
            assertEquals(99 - index, list.size());
            for (int k = 0; k < list.size(); ++k) {
                assertEquals(list.size() - k - 1, (int)list.get(k));
            }

            int kk = 0;
            for (Integer value : list) {
                assertEquals(list.size() - kk - 1, (int)value);
                kk += 1;
            }
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
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
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

    public void testInsert()
    {
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        List<Integer> expected = new ArrayList<>();
        list = list.insert(0);
        expected.add(0);
        for (int i = 1; i <= 1000; ++i) {
            list = list.insert(0, i);
            expected.add(0, i);
        }
        assertEquals(expected, list.getList());
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());

        for (int i = 1; i <= 1000; ++i) {
            list = list.insert(999, i);
            expected.add(999, i);
        }
        assertEquals(expected, list.getList());
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertAtSize()
    {
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 1000; ++i) {
            list = list.insert(list.size(), i);
            expected.add(i);
        }
        assertEquals(expected, list.getList());
        list.checkInvariants();
        StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
        StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testRandom()
    {
        Random random = new Random(100L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(500);
            JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
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
                list.checkInvariants();
            }
            assertEquals(expected, list.getList());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

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

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                if ((list.size() % 100) == 0) {
                    assertEquals(expected, list.getList());
                }
                list.checkInvariants();
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
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
            JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();

            for (int loops = 1; loops <= 200; ++loops) {
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
            assertEquals(expected, list.getList());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            Cursor<Integer> cursor = list.cursor().next();
            for (int n = 0; n < list.size(); ++n) {
                assertEquals(true, cursor.hasValue());
                assertEquals(expected.get(n), list.get(n));
                assertEquals(expected.get(n), cursor.getValue());
                cursor = cursor.next();
            }
            assertEquals(false, cursor.hasValue());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                assertEquals(expected.size(), list.size());
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testRandom3()
    {
        Random random = new Random(100L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(3000);
            JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i < size; ++i) {
                int value = random.nextInt(10000000);
                if (list.isEmpty()) {
                    list = list.insert(value);
                    expected.add(value);
                } else {
                    int index = random.nextInt(list.size());
                    if (random.nextInt(3) == 0) {
                        list = list.delete(index);
                        expected.remove(index);
                    } else {
                        list = list.insert(index, value);
                        expected.add(index, value);
                    }
                }
                assertEquals(expected.size(), list.size());
            }
            assertEquals(expected, list.getList());
            list.checkInvariants();
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            Cursor<Integer> cursor = list.cursor().next();
            for (int i = 0; i < list.size(); ++i) {
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

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                assertEquals(expected.size(), list.size());
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }


    public void testRandom4()
    {
        Random random = new Random(2500L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = 1 + random.nextInt(3000);
            JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i < size; ++i) {
                List<Integer> values = makeValues(random, size);
                int index = (list.size() == 0) ? 0 : random.nextInt(list.size());
                expected.addAll(index, values);
                int parameter = random.nextInt(4);
                switch (parameter) {
                    case 0:  //insertAll(Cursorable)
                        list = list.insertAll(index, plainIterable(values));
                        break;
                    case 1: //insertAll(Collection)
                        list = list.insertAll(index, values);
                        break;
                    case 2: //insertAll(Cursor)
                        list = list.insertAll(index, getCursor(values));
                        break;
                    case 3: //insertAll(Iterator)
                        list = list.insertAll(index, values.iterator());
                        break;
                }
                assertEquals(expected.size(), list.size());
            }
            assertEquals(expected, list.getList());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            Cursor<Integer> cursor = list.cursor().next();
            for (int i = 0; i < list.size(); ++i) {
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

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                assertEquals(expected.size(), list.size());
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            StandardCursorTest.indexedCursorTest(list, list.size(), list.cursor());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.iterator());
            StandardCursorTest.indexedIteratorTest(list, list.size(), list.cursor().iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testCursor()
    {
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
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

        list = JImmutableBtreeList.of();
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

        list = JImmutableBtreeList.of();
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
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
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

        list = JImmutableBtreeList.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertFirst(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(size - i), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = JImmutableBtreeList.of();
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
        JImmutableBtreeList<Integer> list = JImmutableBtreeList.of();
        list = list.insert(1).insert(2);
        assertSame(JImmutableBtreeList.of(), list.deleteAll());
    }

    public void testSelect()
    {
        JImmutableRandomAccessList<Integer> list = ralist();
        assertSame(list, list.select(x -> false));
        assertSame(list, list.select(x -> true));

        list = ralist(1);
        assertEquals(true, list.select(x -> false).isEmpty());
        assertSame(list, list.select(x -> true));

        list = ralist(1, 2, 3);
        assertEquals(ralist(1, 3), list.select(x -> x % 2 == 1));
        assertEquals(ralist(2), list.select(x -> x % 2 == 0));
    }

    public void testReject()
    {
        JImmutableRandomAccessList<Integer> list = ralist();
        assertSame(list, list.reject(x -> false));
        assertSame(list, list.reject(x -> true));

        list = ralist(1);
        assertSame(list, list.reject(x -> false));
        assertEquals(true, list.reject(x -> true).isEmpty());

        list = ralist(1, 2, 3);
        assertEquals(ralist(2), list.reject(x -> x % 2 == 1));
        assertEquals(ralist(1, 3), list.reject(x -> x % 2 == 0));
    }

    public void testStreams()
    {
        JImmutableRandomAccessList<Integer> list = JImmutableBtreeList.<Integer>builder().add(1, 2, 3, 4, 5, 6, 7).build();
        assertEquals(asList(1, 2, 3, 4), list.stream().filter(x -> x < 5).collect(toList()));
        assertEquals(asList(1, 2, 3, 4), list.parallelStream().filter(x -> x < 5).collect(toList()));

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 2048; ++i) {
            expected.add(i);
        }
        list = JImmutableBtreeList.of(IndexedList.retained(expected));
        assertEquals(expected.stream().collect(toList()), list.stream().collect(toList()));
        assertEquals(expected.parallelStream().collect(toList()), list.parallelStream().collect(toList()));
    }

    public void testParallelStreams()
    {
        final JImmutableRandomAccessList<Integer> original = JImmutableBtreeList.of(IndexedList.retained(StandardCursor.makeList(StandardCursor.forRange(1, 10000))));
        final JImmutableRandomAccessList<Object> collected = original.stream().parallel().collect(JImmutableBtreeList.of().ralistCollector());
        collected.checkInvariants();
        assertEquals(original, collected);
        assertEquals(original.getList(), original.stream().parallel().collect(toList()));
    }

    public void testBuilder()
    {
        List<Integer> source = new ArrayList<>();
        for (int i = 0; i <= 11842; ++i) {
            source.add(i);
            JImmutableBtreeList<Integer> list = JImmutableBtreeList.<Integer>builder().add(source).build();
            assertEquals(source, list.getList());
            list.checkInvariants();
        }

        Func0<? extends MutableBuilder<Integer, JImmutableRandomAccessList<Integer>>> factory = (Func0<JImmutableBtreeList.Builder<Integer>>)() -> JImmutableBtreeList.builder();

        Func2<List<Integer>, JImmutableRandomAccessList<Integer>, Boolean> comparator = (list, tree) -> {
            tree.checkInvariants();
            for (int i = 0; i < list.size(); ++i) {
                assertEquals(list.get(i), tree.get(i));
            }
            return true;
        };

        StandardMutableBuilderTests.verifyBuilder(source, factory, comparator);
    }

    public void testStaticBuilderMethod()
    {
        List<Integer> source = new ArrayList<>();
        for (int i = 0; i <= 11842; ++i) {
            source.add(i);
            JImmutableBtreeList<Integer> list = JImmutableBtreeList.of(IndexedList.retained(source));
            assertEquals(source, list.getList());
            list.checkInvariants();
        }
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableRandomAccessList)a).iterator();
        final JImmutableRandomAccessList<String> empty = JImmutableBtreeList.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBK78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCkrMS8nPdUxOTi0u9sksLgkoyq+o/A8C/1SMeRgYKooY3Egw2DGpuKQoMbkEYQFWQwvKORgYmF8yAEEFAEFQ7MC/AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBK78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCkrMS8nPdUxOTi0u9sksLgkoyq+o/A8C/1SMeRgYKooY3Egw2DGpuKQoMbkEYQFWQwvKORgYmF8yMDAwljAwJlYAAN9HqSrDAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList("a", "b", "c")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBK78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCkrMS8nPdUxOTi0u9sksLgkoyq+o/A8C/1SMeRgYKooY3Egw2DGpuKQoMbkEYQFWQwvKORgYmF8yAIkSBsZEIE4C4uQKAKP4XnfLAAAA");
    }

    private JImmutableRandomAccessList<Integer> ralist(Integer... values)
    {
        return JImmutableBtreeList.of(IndexedArray.retained(values));
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

    private Iterable<Integer> plainIterable(List<Integer> values)
    {
        return () -> values.iterator();
    }

    private Cursor<Integer> getCursor(List<Integer> values)
    {
        return IterableCursorable.of(values).cursor();
    }
}
