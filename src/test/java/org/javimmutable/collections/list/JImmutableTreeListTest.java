///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.common.StandardJImmutableListTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.TestUtil;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.IndexedIterator;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.list.TreeBuilder.*;

public class JImmutableTreeListTest
    extends TestCase
{
    public void testStandard()
    {
        StandardJImmutableListTests.standardTests(JImmutableTreeList.of());
    }

    public void test()
    {
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list = list.insert(100);
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insert(200);
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(200, (int)list.get(1));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
        assertEquals("[100,200]", list.toString());
        assertEquals(3300, list.hashCode());

        JImmutableTreeList<Integer> saved = list;

        list = list.insertFirst(80);
        assertEquals(3, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        assertEquals(200, (int)list.get(2));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteFirst();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = saved;
        list = list.assign(1, 210);
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(210, (int)list.get(1));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.delete(0);
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(210, (int)list.get(0));
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.delete(0);
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertIterable()
    {
        JImmutableList<Integer> list = JImmutableTreeList.of();
        StandardIteratorTests.emptyIteratorTest(list.iterator());

        list = list.insertAll(Arrays.asList(1, 2, 3));
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), list.iterator());

        list = list.insert(6).insertAllLast(Arrays.asList(10, 11, 12)).insert(20);
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3, 6, 10, 11, 12, 20), list.iterator());
    }

    public void testInsertAllAtIndex()
    {
        JImmutableTreeList<Integer> checkIterable;
        JImmutableTreeList<Integer> checkCollection;
        JImmutableTreeList<Integer> checkIterator;

        //insert at negative index -- all should fail
        //empty into empty
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
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
            list.insertAll(-1, Arrays.asList(0, 1, 2, 3));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //insert at 0 -- all should work
        //empty into empty
        list = JImmutableTreeList.of();
        JImmutableTreeList<Integer> expected = list;
        checkIterable = list.insertAll(0, plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(0, Collections.emptyList());
        checkIterator = list.insertAll(0, Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(4).insert(5);
        checkIterable = list.insertAll(0, plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAll(0, Arrays.asList(4, 5));
        checkIterator = list.insertAll(0, Arrays.asList(4, 5).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //empty into values
        list = list.insert(4).insert(5);
        checkIterable = list.insertAll(0, plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(0, Collections.emptyList());
        checkIterator = list.insertAll(0, Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into values
        expected = JImmutableTreeList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3).insert(4).insert(5);
        checkIterable = list.insertAll(0, plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAll(0, Arrays.asList(0, 1, 2, 3));
        checkIterator = list.insertAll(0, Arrays.asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //insert in middle
        //empty into empty -- should fail
        list = JImmutableTreeList.of();
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
            list.insertAll(1, Arrays.asList(1, 2));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }

        //empty into values -- should work
        list = list.insert(0).insert(3);
        expected = list;
        checkIterable = list.insertAll(0, plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(0, Collections.emptyList());
        checkIterator = list.insertAll(0, Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into values -- should work
        expected = JImmutableTreeList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3);
        checkIterable = list.insertAll(1, plainIterable(Arrays.asList(1, 2)));
        checkCollection = list.insertAll(1, Arrays.asList(1, 2));
        checkIterator = list.insertAll(1, Arrays.asList(1, 2).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //insert at size -- all should work
        //empty into empty
        list = JImmutableTreeList.of();
        expected = list;
        checkIterable = list.insertAll(list.size(), plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(list.size(), Collections.emptyList());
        checkIterator = list.insertAll(list.size(), Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(0).insert(1).insert(2).insert(3);
        checkIterable = list.insertAll(list.size(), plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAll(list.size(), Arrays.asList(0, 1, 2, 3));
        checkIterator = list.insertAll(list.size(), Arrays.asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //empty into values
        list = list.insert(0).insert(1).insert(2).insert(3);
        checkIterable = list.insertAll(list.size(), plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(list.size(), Collections.emptyList());
        checkIterator = list.insertAll(list.size(), Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into values
        expected = expected.insert(4).insert(5);
        checkIterable = list.insertAll(list.size(), plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAll(list.size(), Arrays.asList(4, 5));
        checkIterator = list.insertAll(list.size(), Arrays.asList(4, 5).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //insert at >size -- all should fail
        // empty into empty
        list = JImmutableTreeList.of();
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
            list.insertAll(list.size() + 1, Arrays.asList(0, 1, 2, 3));
        } catch (IndexOutOfBoundsException ignored) {
            //expected
        }
    }

    public void testInsertAllFirst()
    {
        //empty into empty
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        JImmutableTreeList<Integer> expected = list;
        JImmutableTreeList<Integer> checkIterable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        JImmutableTreeList<Integer> checkCollection = list.insertAllFirst(Collections.emptyList());
        JImmutableTreeList<Integer> checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        JImmutableTreeList<Integer> checkTree = list.insertAllFirst(btree());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkTree);

        //values into empty
        expected = list.insert(4).insert(5);
        checkIterable = list.insertAllFirst(plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAllFirst(Arrays.asList(4, 5));
        checkIterator = list.insertAllFirst(Arrays.asList(4, 5).iterator());
        checkTree = list.insertAllFirst(btree(4, 5));
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkTree);

        //empty into values
        list = list.insert(4).insert(5);
        expected = list;
        checkIterable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllFirst(Collections.emptyList());
        checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        checkTree = list.insertAllFirst(btree());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkTree);

        //values into values
        expected = JImmutableTreeList.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3).insert(4).insert(5);
        checkIterable = list.insertAllFirst(plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAllFirst(Arrays.asList(0, 1, 2, 3));
        checkIterator = list.insertAllFirst(Arrays.asList(0, 1, 2, 3).iterator());
        checkTree = list.insertAllFirst(btree(0, 1, 2, 3));
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        assertEquals(expected, checkTree);
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

    public void testAssignAtSize()
    {
        TestUtil.verifyOutOfBounds(() -> range(1, 1).assign(1, -999));
        TestUtil.verifyOutOfBounds(() -> range(1, MultiValueNode.MAX_SIZE).assign(MultiValueNode.MAX_SIZE, -999));
        TestUtil.verifyOutOfBounds(() -> range(1, MultiValueNode.MAX_SIZE + 1).assign(MultiValueNode.MAX_SIZE + 1, -999));
        TestUtil.verifyOutOfBounds(() -> range(1, 2 * MultiValueNode.MAX_SIZE + 1).assign(2 * MultiValueNode.MAX_SIZE + 1, -999));
    }

    @SafeVarargs
    private final <T> JImmutableTreeList<T> btree(T... values)
    {
        return JImmutableTreeList.of(IndexedArray.retained(values));
    }

    private JImmutableList<Integer> range(int first,
                                          int last)
    {
        return JImmutableTreeList.<Integer>listBuilder()
            .add(IndexedIterator.forRange(first, last))
            .build();
    }

    public void testInsertAllLast()
    {
        //test insertAll
        //empty into empty
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        JImmutableTreeList<Integer> expected = list;
        JImmutableTreeList<Integer> checkIterable = list.insertAll(plainIterable(Collections.emptyList()));
        JImmutableTreeList<Integer> checkCollection = list.insertAll(Collections.emptyList());
        JImmutableTreeList<Integer> checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(0);
        checkIterable = list.insertAll(plainIterable(Collections.singletonList(0)));
        checkCollection = list.insertAll(Collections.singletonList(0));
        checkIterator = list.insertAll(Collections.singletonList(0).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //empty into values
        list = list.insert(0);
        expected = list;
        checkIterable = list.insertAll(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(Collections.emptyList());
        checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into values
        expected = list.insert(1).insert(2).insert(3);
        checkIterable = list.insertAll(plainIterable(Arrays.asList(1, 2, 3)));
        checkCollection = list.insertAll(Arrays.asList(1, 2, 3));
        checkIterator = list.insertAll(Arrays.asList(1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //test insertAllLast
        //empty into empty
        list = JImmutableTreeList.of();
        expected = list;
        checkIterable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into empty
        expected = list.insert(0).insert(1).insert(2).insert(3);
        checkIterable = list.insertAllLast(plainIterable(Arrays.asList(0, 1, 2, 3)));
        checkCollection = list.insertAllLast(Arrays.asList(0, 1, 2, 3));
        checkIterator = list.insertAll(Arrays.asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //empty into values
        list = list.insert(0).insert(1).insert(2).insert(3);
        expected = list;
        checkIterable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);

        //values into values
        expected = list.insert(4).insert(5);
        checkIterable = list.insertAllLast(plainIterable(Arrays.asList(4, 5)));
        checkCollection = list.insertAllLast(Arrays.asList(4, 5));
        checkIterator = list.insertAllLast(Arrays.asList(4, 5).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
    }

    public void testInsertDeleteFirst()
    {
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
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
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
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
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        List<Integer> expected = new ArrayList<>();
        list = list.insert(0);
        expected.add(0);
        for (int i = 1; i <= 1000; ++i) {
            list = list.insert(0, i);
            expected.add(0, i);
        }
        assertEquals(expected, list.getList());
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        for (int i = 1; i <= 1000; ++i) {
            list = list.insert(999, i);
            expected.add(999, i);
        }
        assertEquals(expected, list.getList());
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertAtSize()
    {
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 1000; ++i) {
            list = list.insert(list.size(), i);
            expected.add(i);
        }
        assertEquals(expected, list.getList());
        list.checkInvariants();
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testSlice()
    {
        final JImmutableList<Integer> list = rangeList(1, 9);

        assertThat(list.slice(0, 9)).isEqualTo(list);
        assertThat(list.slice(0, -1)).isEqualTo(list);
        assertThat(list.slice(-1000, 10000)).isEqualTo(list);

        assertThat(list.slice(-5, -1)).isEqualTo(rangeList(5, 9));
        assertThat(list.slice(-2, -2)).isEqualTo(rangeList(8, 8));
        assertThat(list.slice(-10, -2)).isEqualTo(rangeList(1, 8));

        assertThat(list.slice(0, Integer.MAX_VALUE)).isEqualTo(rangeList(1, 9));
        assertThat(list.slice(-3, Integer.MAX_VALUE)).isEqualTo(rangeList(7, 9));
        assertThat(list.slice(5, 4)).isEqualTo(JImmutableTreeList.of());
        assertThat(list.slice(12, 9)).isEqualTo(JImmutableTreeList.of());
    }

    public void testIndexedBuild()
    {
        List<Integer> values = new ArrayList<>();
        JImmutableList.Builder<Integer> builder = JImmutableTreeList.listBuilder();
        for (int i = 1; i <= 4096; ++i) {
            values.add(i);
            builder.add(i);
            final IndexedList<Integer> expected = IndexedList.retained(values);
            final JImmutableTreeList<Integer> actual = JImmutableTreeList.create(nodeFromIndexed(expected));
            assertThat(actual).isEqualTo(builder.build());
            actual.checkInvariants();
        }

        assertEquals(JImmutableTreeList.<Integer>of().insert(1), builder.clear().add(1).build());

        for (int i = 4097; i <= 20480; ++i) {
            values.add(i);
        }

        int size = 0;
        long start = System.currentTimeMillis();
        for (int i = 1; i <= 3000; ++i) {
            size += JImmutableTreeList.create(nodeFromIndexed(IndexedList.retained(values))).size();
        }
        long ibTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (int i = 1; i <= 3000; ++i) {
            size -= JImmutableTreeList.create(nodeFromIterator(values.iterator())).size();
        }
        long ofTime = System.currentTimeMillis() - start;
//        System.out.printf("indexBuilder=%d  builder=%d\n", ibTime, ofTime);
        assertEquals(0, size);
    }

    public void testRandom()
    {
        Random random = new Random(100L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(500);
            JImmutableTreeList<Integer> list = JImmutableTreeList.of();
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
            assertEquals(expected.isEmpty(), list.isEmpty());
            assertEquals(!expected.isEmpty(), list.isNonEmpty());
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            while (!list.isEmpty()) {
                int op = random.nextInt(3);
                if (op == 0) {
                    int index = random.nextInt(list.size());
                    list = list.delete(index);
                    expected.remove(index);
                } else if (op == 1) {
                    list = list.deleteFirst();
                    expected.remove(0);
                } else {
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                }
                if ((list.size() % 100) == 0) {
                    assertEquals(expected, list.getList());
                }
                list.checkInvariants();
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            assertEquals(expected.isEmpty(), list.isEmpty());
            assertEquals(!expected.isEmpty(), list.isNonEmpty());
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testRandom2()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            List<Integer> expected = new ArrayList<>();
            JImmutableTreeList<Integer> list = JImmutableTreeList.of();

            for (int loops = 1; loops <= 200; ++loops) {
                switch (random.nextInt(4)) {
                    case 0: { //insertAllFirst(Collection)
                        List<Integer> values = makeValues(random, size);
                        list = (random.nextBoolean()) ? list.insertAllFirst(values) : list.insertAllFirst(values.iterator());
                        expected.addAll(0, values);
                        break;
                    }
                    case 1: {//insertAllLast(Collection)
                        List<Integer> values = makeValues(random, size);
                        list = (random.nextBoolean()) ? list.insertAllLast(values) : list.insertAllLast(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 2: { //deleteFirst
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
                    case 3: { //deleteLast
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                assertEquals(expected.size(), list.size());
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testRandom3()
    {
        Random random = new Random(100L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(3000);
            JImmutableTreeList<Integer> list = JImmutableTreeList.of();
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                assertEquals(expected.size(), list.size());
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }


    public void testRandom4()
    {
        Random random = new Random(2500L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = 1 + random.nextInt(3000);
            JImmutableTreeList<Integer> list = JImmutableTreeList.of();
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i < size; ++i) {
                List<Integer> values = makeValues(random, size);
                int index = (list.size() == 0) ? 0 : random.nextInt(list.size());
                expected.addAll(index, values);
                int parameter = random.nextInt(3);
                switch (parameter) {
                    case 0: //insertAll(Collection)
                        list = list.insertAll(index, values);
                        break;
                    case 1: //insertAll(Iterator)
                        list = list.insertAll(index, values.iterator());
                        break;
                    case 2: //insertAll(JImmutableTreeList)
                        list = list.insertAll(index, JImmutableTreeList.of(values.iterator()));
                        break;
                }
                if (list.size() > 0) {
                    assertEquals(expected.size(), list.size());
                    int offset = random.nextInt(list.size());
                    int limit = offset + random.nextInt(list.size() - offset);
                    switch (random.nextInt(3)) {
                        case 0: {
                            JImmutableTreeList<Integer> a = list.prefix(offset);
                            assertEquals(a.getList(), expected.subList(0, offset));
                            break;
                        }
                        case 1: {
                            JImmutableTreeList<Integer> b = list.middle(offset, limit);
                            assertEquals(b.getList(), expected.subList(offset, limit));
                            break;
                        }
                        case 2: {
                            JImmutableTreeList<Integer> c = list.suffix(limit);
                            assertEquals(c.getList(), expected.subList(limit, expected.size()));
                            break;
                        }
                    }
                }
            }
            assertEquals(expected, list.getList());
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);

            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                list = list.delete(index);
                expected.remove(index);
                assertEquals(expected.size(), list.size());
            }
            assertEquals(true, list.isEmpty());
            assertEquals(0, list.size());
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testIterator()
    {
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        Iterator<Integer> iterator = list.iterator();
        assertEquals(false, iterator.hasNext());

        for (int size = 1; size <= 1024; ++size) {
            list = list.insert(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(i + 1), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = JImmutableTreeList.of();
        for (int size = 1; size <= 1024; ++size) {
            list = list.insertFirst(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(size - i), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = JImmutableTreeList.of();
        for (int size = 1; size <= 1024; ++size) {
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
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        list = list.insert(1).insert(2);
        assertSame(JImmutableTreeList.of(), list.deleteAll());
    }

    public void testSelect()
    {
        JImmutableList<Integer> list = list();
        assertSame(list, list.select(x -> false));
        assertSame(list, list.select(x -> true));

        list = list(1);
        assertEquals(true, list.select(x -> false).isEmpty());
        assertSame(list, list.select(x -> true));

        list = list(1, 2, 3);
        assertEquals(list(1, 3), list.select(x -> x % 2 == 1));
        assertEquals(list(2), list.select(x -> x % 2 == 0));
    }

    public void testReject()
    {
        JImmutableList<Integer> list = list();
        assertSame(list, list.reject(x -> false));
        assertSame(list, list.reject(x -> true));

        list = list(1);
        assertSame(list, list.reject(x -> false));
        assertEquals(true, list.reject(x -> true).isEmpty());

        list = list(1, 2, 3);
        assertEquals(list(2), list.reject(x -> x % 2 == 1));
        assertEquals(list(1, 3), list.reject(x -> x % 2 == 0));
    }

    public void testStreams()
    {
        JImmutableList<Integer> list = JImmutableTreeList.<Integer>listBuilder().add(1, 2, 3, 4, 5, 6, 7).build();
        assertEquals(asList(1, 2, 3, 4), list.stream().filter(x -> x < 5).collect(toList()));
        assertEquals(asList(1, 2, 3, 4), list.parallelStream().filter(x -> x < 5).collect(toList()));

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 2048; ++i) {
            expected.add(i);
        }
        list = JImmutableTreeList.of(IndexedList.retained(expected));
        assertEquals(expected.stream().collect(toList()), list.stream().collect(toList()));
        assertEquals(list, list.stream().collect(JImmutableTreeList.createListCollector()));
        assertEquals(expected.parallelStream().collect(toList()), list.parallelStream().collect(toList()));
        assertEquals(list, list.parallelStream().collect(JImmutableTreeList.createListCollector()));
    }

    public void testParallelStreams()
    {
        final JImmutableList<Integer> original = JImmutableTreeList.of(IndexedList.retained(TestUtil.makeList(IndexedIterator.forRange(1, 10000))));
        final JImmutableList<Object> collected = original.stream().parallel().collect(JImmutableTreeList.of().listCollector());
        collected.checkInvariants();
        assertEquals(original, collected);
        assertEquals(original.getList(), original.stream().parallel().collect(toList()));
        assertEquals(original, original.stream().parallel().collect(JImmutableTreeList.createListCollector()));
    }

    public void testReverse()
    {
        JImmutableTreeList<Integer> list = JImmutableTreeList.of();
        assertSame(list, list.reverse());
        list = JImmutableTreeList.of(IndexedList.retained(Arrays.asList(1)));
        assertSame(list, list.reverse());
        for (Integer length : asList(2, 3, 5, 6, 200, 201, 400, 401)) {
            final List<Integer> expected = TestUtil.makeList(IndexedIterator.forRange(1, length));
            list = JImmutableTreeList.of(IndexedList.retained(expected));
            assertEquals(TestUtil.reversedList(expected), list.reverse().getList());
        }
    }

    public void testBuilder()
        throws InterruptedException
    {
        assertSame(JImmutableTreeList.of(), JImmutableTreeList.listBuilder().build());

        final JImmutableTreeList.ListBuilder<Integer> builder = JImmutableTreeList.listBuilder();
        final List<Integer> expected = new ArrayList<>();
        JImmutableList<Integer> manual = JImmutableTreeList.of();
        for (int i = 0; i <= 2049; ++i) {
            expected.add(i);
            builder.add(i);
            manual = manual.insertLast(i);
            assertEquals(i + 1, builder.size());
            JImmutableList<Integer> list = builder.build();
            assertEquals(expected, list.getList());
            list.checkInvariants();
            builder.checkInvariants();
        }
        assertEquals(manual, builder.build());

        Func2<List<Integer>, JImmutableList<Integer>, Boolean> comparator = (list, tree) -> {
            tree.checkInvariants();
            for (int i = 0; i < list.size(); ++i) {
                assertEquals(list.get(i), tree.get(i));
            }
            return true;
        };

        StandardBuilderTests.verifyBuilder(expected, this::builder, comparator, new Integer[0]);
        StandardBuilderTests.verifyThreadSafety(this::builder);
    }

    private BuilderTestAdapter<Integer> builder()
    {
        return new BuilderTestAdapter<>(JImmutableTreeList.listBuilder());
    }

    public void testStaticBuilderMethod()
    {
        List<Integer> source = new ArrayList<>();
        for (int i = 0; i <= 11842; ++i) {
            source.add(i);
            JImmutableTreeList<Integer> list = JImmutableTreeList.of(source.iterator());
            assertEquals(source, list.getList());
            list.checkInvariants();
        }
    }

    public void testListSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableList)a).iterator();
        final JImmutableList<String> empty = JImmutableTreeList.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8sksLgkoyq+o/A8C/1SMeRgYKooY3EgwzDGpuKQoMbmEgKEF5RwMDMwvGYCgAgAFkzNDswAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8sksLgkoyq+o/A8C/1SMeRgYKooY3EgwzDGpuKQoMbmEgKEF5RwMDMwvGRgYGEsYGBMrAOarJ9+3AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList("a", "b", "c")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBLr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8sksLgkoyq+o/A8C/1SMeRgYKooY3EgwzDGpuKQoMbmEgKEF5RwMDMwvGYBECQNjIhAnAXFyBQAT1rKNvwAAAA==");
    }

    public void testRAListSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableList)a).iterator();
        final JImmutableList<String> empty = JImmutableTreeList.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBK78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCkrMS8nPdUxOTi0u9sksLgkoyq+o/A8C/1SMeRgYKooY3Egw2DGpuKQoMbkEYQFWQwvKORgYmF8yAEEFAEFQ7MC/AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBK78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCkrMS8nPdUxOTi0u9sksLgkoyq+o/A8C/1SMeRgYKooY3Egw2DGpuKQoMbkEYQFWQwvKORgYmF8yMDAwljAwJlYAAN9HqSrDAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList("a", "b", "c")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBK78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCkrMS8nPdUxOTi0u9sksLgkoyq+o/A8C/1SMeRgYKooY3Egw2DGpuKQoMbkEYQFWQwvKORgYmF8yAIkSBsZEIE4C4uQKAKP4XnfLAAAA");
    }

    public void testForEach()
    {
        final StringBuilder sb = new StringBuilder();
        final JImmutableList<Integer> empty = JImmutableTreeList.of();
        empty.forEach(i -> sb.append("[").append(i).append("]"));
        assertEquals("", sb.toString());

        JImmutableList<Integer> list = empty.insert(1);
        list.forEach(i -> sb.append("[").append(i).append("]"));
        assertEquals("[1]", sb.toString());

        sb.delete(0, sb.length());
        list = IntStream.range(1, 500)
            .boxed()
            .collect(JImmutableTreeList.createListCollector());
        String expected = IntStream.range(1, 500).boxed().reduce("", (s, i) -> s + "[" + i + "]", (a, b) -> a + b);
        list.forEach(i -> sb.append("[").append(i).append("]"));
        assertEquals(expected, sb.toString());

        try {
            sb.delete(0, sb.length());
            empty.insert(1).forEachThrows(i -> {
                sb.append("[");
                sb.append(i);
                sb.append("]");
                if (i == 1) {
                    throw new IOException();
                }
            });
            fail();
        } catch (IOException ex) {
            assertEquals("[1]", sb.toString());
        }
        try {
            sb.delete(0, sb.length());
            list.forEachThrows(i -> {
                sb.append("[");
                sb.append(i);
                sb.append("]");
                if (i == 499) {
                    throw new IOException();
                }
            });
            fail();
        } catch (IOException ex) {
            assertEquals(expected, sb.toString());
        }
    }

    public void testInject()
    {
        final JImmutableList<Integer> empty = JImmutableTreeList.of();

        assertEquals("", empty.reduce("", (s, i) -> s + "[" + i + "]"));
        assertEquals("[1]", empty.insert(1).reduce("", (s, i) -> s + "[" + i + "]"));

        JImmutableList<Integer> list = IntStream.range(1, 500)
            .boxed()
            .collect(JImmutableTreeList.createListCollector());
        String expected = IntStream.range(1, 500).boxed().reduce("", (s, i) -> s + "[" + i + "]", (a, b) -> a + b);
        assertEquals(expected, list.reduce("", (s, i) -> s + "[" + i + "]"));

        try {
            empty.insert(1).reduceThrows("", (s, i) -> {
                if (i == 1) {
                    throw new IOException();
                } else {
                    return s + "[" + i + "]";
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
        try {
            list.insert(1).reduceThrows("", (s, i) -> {
                if (i == 499) {
                    throw new IOException();
                } else {
                    return s + "[" + i + "]";
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
    }

    private JImmutableList<Integer> list(Integer... values)
    {
        return JImmutableTreeList.of(IndexedArray.retained(values));
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

    private JImmutableList<Integer> rangeList(int first,
                                              int last)
    {
        JImmutableList.Builder<Integer> builder = JImmutableTreeList.listBuilder();
        for (int i = first; i <= last; ++i) {
            builder.add(i);
        }
        return builder.build();
    }

    private Iterable<Integer> plainIterable(List<Integer> values)
    {
        return values::iterator;
    }
}
