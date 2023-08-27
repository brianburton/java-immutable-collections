///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collections.deque;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IDeque;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardDequeTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.StandardStreamableTests;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class ArrayDequeTest
    extends TestCase
{
    public void testStandard()
    {
        StandardDequeTests.standardTests(ArrayDeque.of());
    }

    public void test()
    {
        ArrayDeque<Integer> list = ArrayDeque.of();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        list = list.insert(100);
        list.checkInvariants();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insert(200);
        list.checkInvariants();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        assertEquals(200, (int)list.get(1));
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.insertFirst(80);
        list.checkInvariants();
        assertEquals(3, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        assertEquals(200, (int)list.get(2));
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        list.checkInvariants();
        assertEquals(2, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(80, (int)list.get(0));
        assertEquals(100, (int)list.get(1));
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteFirst();
        list.checkInvariants();
        assertEquals(1, list.size());
        assertEquals(false, list.isEmpty());
        assertEquals(100, (int)list.get(0));
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());

        list = list.deleteLast();
        list.checkInvariants();
        assertEquals(0, list.size());
        assertEquals(true, list.isEmpty());
        StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
    }

    public void testInsertIterable()
    {
        ArrayDeque<Integer> list = ArrayDeque.of();
        StandardIteratorTests.emptyIteratorTest(list.iterator());

        list = list.insertAllLast(asList(1, 2, 3));
        StandardIteratorTests.listIteratorTest(asList(1, 2, 3), list.iterator());

        list = list.insert(6).insertAllLast(asList(10, 11, 12)).insert(20);
        StandardIteratorTests.listIteratorTest(asList(1, 2, 3, 6, 10, 11, 12, 20), list.iterator());

        list.checkInvariants();
    }

    public void testInsertAllFirst()
    {
        //empty into empty
        ArrayDeque<Integer> list = ArrayDeque.of();
        ArrayDeque<Integer> expected = list;
        ArrayDeque<Integer> checkIterable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        ArrayDeque<Integer> checkCollection = list.insertAllFirst(Collections.emptyList());
        ArrayDeque<Integer> checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkCollection.checkInvariants();

        //values into empty
        expected = list.insert(4).insert(5);
        checkIterable = list.insertAllFirst(plainIterable(asList(4, 5)));
        checkCollection = list.insertAllFirst(asList(4, 5));
        checkIterator = list.insertAllFirst(asList(4, 5).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterable.checkInvariants();

        //empty into values
        list = list.insert(4).insert(5);
        expected = list;
        checkIterable = list.insertAllFirst(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllFirst(Collections.emptyList());
        checkIterator = list.insertAllFirst(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();

        //values into values
        expected = ArrayDeque.of();
        expected = expected.insert(0).insert(1).insert(2).insert(3).insert(4).insert(5);
        checkIterable = list.insertAllFirst(plainIterable(asList(0, 1, 2, 3)));
        checkCollection = list.insertAllFirst(asList(0, 1, 2, 3));
        checkIterator = list.insertAllFirst(asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();
    }

    public void testInsertAllLast()
    {
        //test insertAll
        //empty into empty
        ArrayDeque<Integer> list = ArrayDeque.of();
        ArrayDeque<Integer> expected = list;
        ArrayDeque<Integer> checkIterable = list.insertAll(plainIterable(Collections.emptyList()));
        ArrayDeque<Integer> checkCollection = list.insertAll(Collections.emptyList());
        ArrayDeque<Integer> checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkCollection.checkInvariants();

        //values into empty
        expected = list.insert(0);
        checkIterable = list.insertAll(plainIterable(Collections.singletonList(0)));
        checkCollection = list.insertAll(Collections.singletonList(0));
        checkIterator = list.insertAll(Collections.singletonList(0).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterable.checkInvariants();

        //empty into values
        list = list.insert(0);
        expected = list;
        checkIterable = list.insertAll(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAll(Collections.emptyList());
        checkIterator = list.insertAll(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();

        //values into values
        expected = list.insert(1).insert(2).insert(3);
        checkIterable = list.insertAll(plainIterable(asList(1, 2, 3)));
        checkCollection = list.insertAll(asList(1, 2, 3));
        checkIterator = list.insertAll(asList(1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();

        //test insertAllLast
        //empty into empty
        list = ArrayDeque.of();
        expected = list;
        checkIterable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterable.checkInvariants();

        //values into empty
        expected = list.insert(0).insert(1).insert(2).insert(3);
        checkIterable = list.insertAllLast(plainIterable(asList(0, 1, 2, 3)));
        checkCollection = list.insertAllLast(asList(0, 1, 2, 3));
        checkIterator = list.insertAll(asList(0, 1, 2, 3).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkCollection.checkInvariants();

        //empty into values
        list = list.insert(0).insert(1).insert(2).insert(3);
        expected = list;
        checkIterable = list.insertAllLast(plainIterable(Collections.emptyList()));
        checkCollection = list.insertAllLast(Collections.emptyList());
        checkIterator = list.insertAllLast(Collections.<Integer>emptyList().iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterable.checkInvariants();

        //values into values
        expected = list.insert(4).insert(5);
        checkIterable = list.insertAllLast(plainIterable(asList(4, 5)));
        checkCollection = list.insertAllLast(asList(4, 5));
        checkIterator = list.insertAllLast(asList(4, 5).iterator());
        assertEquals(expected, checkIterable);
        assertEquals(expected, checkCollection);
        assertEquals(expected, checkIterator);
        checkIterator.checkInvariants();
    }

    private Iterable<Integer> plainIterable(List<Integer> values)
    {
        return () -> values.iterator();
    }

    public void testInsertDeleteFirst()
    {
        ArrayDeque<Integer> list = ArrayDeque.of();
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
            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
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
        ArrayDeque<Integer> list = ArrayDeque.of();
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
            list.checkInvariants();
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

    public void testRandom()
    {
        Random random = new Random(100L);
        for (int loop = 1; loop <= 200; ++loop) {
            int size = random.nextInt(4000);
            ArrayDeque<Integer> list = ArrayDeque.of();
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
            assertEquals(list, expected.parallelStream().collect(ArrayDeque.collector()));
            list.checkInvariants();

            StandardIteratorTests.indexedIteratorTest(list, list.size(), list.iterator());
            StandardStreamableTests.verifyOrderedUsingCollection(expected, list);
        }
    }

    public void testRandom2()
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            List<Integer> expected = new ArrayList<>();
            ArrayDeque<Integer> list = ArrayDeque.of();

            for (int loops = 0; loops < (4 * size); ++loops) {
                switch (random.nextInt(14)) {
                    case 0: { // insert
                        final int value = random.nextInt(size);
                        list = list.insert(value);
                        expected.add(value);
                    }
                    break;
                    case 1: { // insertLast
                        final int value = random.nextInt(size);
                        list = list.insertLast(value);
                        expected.add(value);
                    }
                    break;
                    case 2: { // insertFirst
                        final int value = random.nextInt(size);
                        list = list.insertFirst(value);
                        expected.add(0, value);
                    }
                    break;
                    case 3: { //insertAllFirst(Iterable:IDeque)
                        IDeque<Integer> values = makeDeque(random, size);
                        ;
                        list = list.insertAllFirst(values);
                        expected.addAll(0, values.getList());
                        break;
                    }
                    case 4: { //insertAllFirst(Iterable:Indexed)
                        List<Integer> values = makeValues(random, size);
                        list = list.insertAllFirst(IndexedList.retained(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 5: { //insertAllFirst(Iterable:List)
                        List<Integer> values = makeValues(random, size);
                        list = list.insertAllFirst(values);
                        expected.addAll(0, values);
                        break;
                    }
                    case 6: { //insertAllFirst(Iterable:plain)
                        List<Integer> values = makeValues(random, size);
                        list = list.insertAllFirst(plainIterable(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 7: {//insertAllFirst(Iterator)
                        List<Integer> values = makeValues(random, size);
                        list = list.insertAllFirst(values.iterator());
                        expected.addAll(0, values);
                        break;
                    }
                    case 8: { //insertAllLast(Iterable:IDeque)
                        IDeque<Integer> values = makeDeque(random, size);
                        ;
                        list = list.insertAllLast(values);
                        expected.addAll(values.getList());
                        break;
                    }
                    case 9: { //insertAllLast(Iterable:List)
                        List<Integer> values = makeValues(random, size);
                        list = list.insertAllLast(values);
                        expected.addAll(values);
                        break;
                    }
                    case 10: { //insertAllLast(Iterator)
                        List<Integer> values = makeValues(random, size);
                        list = list.insertAllLast(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 11: { //deleteFirst
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
                    case 12: { //deleteLast
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
                    case 13: { // assign
                        if (list.size() > 1) {
                            final int index = random.nextInt(list.size() - 1);
                            final int value = random.nextInt(size);
                            list = list.assign(index, value);
                            expected.set(index, value);
                        }
                        break;
                    }
                }
                assertEquals(expected.size(), list.size());
            }
            StandardIteratorTests.listIteratorTest(expected, list.iterator());
            StandardStreamableTests.verifyOrderedUsingCollection(expected, list);
            assertEquals(expected, list.getList());
            list = list.deleteAll();
            assertEquals(0, list.size());
            assertEquals(true, list.isEmpty());
        }
    }

    public void testIterator()
    {
        ArrayDeque<Integer> list = ArrayDeque.of();
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

        list = ArrayDeque.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertFirst(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(size - i), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        list = ArrayDeque.of();
        for (int size = 1; size <= 10; ++size) {
            list = list.insertLast(size);
            iterator = list.iterator();
            for (int i = 0; i < size; ++i) {
                assertEquals(true, iterator.hasNext());
                assertEquals(Integer.valueOf(i + 1), iterator.next());
            }
            assertEquals(false, iterator.hasNext());
        }

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 2000; ++i) {
            expected.add(i);
        }
        StandardIteratorTests.verifyOrderedIterable(expected, ArrayDeque.of(IndexedList.retained(expected)));
    }

    public void testDeleteAll()
    {
        IDeque<Integer> list = ArrayDeque.<Integer>of().insert(1).insert(2);
        assertSame(ArrayDeque.of(), list.deleteAll());
    }

    public void testSelect()
    {
        IDeque<Integer> list = mkDeque();
        assertSame(list, list.select(x -> false));
        assertSame(list, list.select(x -> true));

        list = mkDeque(1);
        assertEquals(true, list.select(x -> false).isEmpty());
        assertSame(list, list.select(x -> true));

        list = mkDeque(1, 2, 3);
        assertEquals(mkDeque(1, 3), list.select(x -> x % 2 == 1));
        assertEquals(mkDeque(2), list.select(x -> x % 2 == 0));
    }

    public void testReject()
    {
        IDeque<Integer> list = mkDeque();
        assertSame(list, list.reject(x -> false));
        assertSame(list, list.reject(x -> true));

        list = mkDeque(1);
        assertSame(list, list.reject(x -> false));
        assertEquals(true, list.reject(x -> true).isEmpty());

        list = mkDeque(1, 2, 3);
        assertEquals(mkDeque(2), list.reject(x -> x % 2 == 1));
        assertEquals(mkDeque(1, 3), list.reject(x -> x % 2 == 0));
    }

    public void testBuilder()
        throws InterruptedException
    {
        assertSame(ArrayDeque.of(), ArrayDeque.builder().build());

        final ArrayDeque.Builder<Integer> builder = ArrayDeque.builder();
        final List<Integer> expected = new ArrayList<>();
        IDeque<Integer> manual = ArrayDeque.of();
        for (int size = 1; size <= 33000; ++size) {
            expected.add(size);
            builder.add(size);
            manual = manual.insertLast(size);
            assertEquals(size, builder.size());
            ArrayDeque<Integer> list = builder.build();
            assertEquals(expected, list.getList());
            list.checkInvariants();
        }
        assertEquals(manual, builder.build());

        StandardBuilderTests.verifyBuilder(expected, this::builder, (l, j) -> l.equals(j.getList()), new Integer[0]);
        StandardBuilderTests.verifyThreadSafety(this::builder);
    }

    private BuilderTestAdapter<Integer> builder()
    {
        return new BuilderTestAdapter<>(ArrayDeque.builder());
    }

    public void testStreams()
    {
        IDeque<Integer> list = ArrayDeque.<Integer>builder().addAll(1, 2, 3, 4, 5, 6, 7).build();
        assertEquals(asList(1, 2, 3, 4), list.stream().filter(x -> x < 5).collect(toList()));
        assertEquals(asList(1, 2, 3, 4), list.parallelStream().filter(x -> x < 5).collect(toList()));

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 2048; ++i) {
            expected.add(i);
        }
        list = ArrayDeque.of(IndexedList.retained(expected));
        assertEquals(expected.stream().collect(toList()), list.stream().collect(toList()));
        assertEquals(expected.parallelStream().collect(toList()), list.parallelStream().collect(toList()));
    }

    public void testParallelStreams()
    {
        final IDeque<Integer> original = ArrayDeque.of(IndexedHelper.range(1, 10000));
        assertEquals(original, original.stream().parallel().collect(ArrayDeque.of().dequeCollector()));
        assertEquals(original.getList(), original.stream().parallel().collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IDeque)a).iterator();
        final IDeque<String> empty = ArrayDeque.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNVzLCpKrHRJLSxNDSjKr6j8DwL/VIx5GBgqCso5GBiYXzIAQQUAXJsJcVoAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNVzLCpKrHRJLSxNDSjKr6j8DwL/VIx5GBgqCso5GBiYXzIwMDCWMDAmVgAAjzVPil4AAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList("a", "b", "c")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNVzLCpKrHRJLSxNDSjKr6j8DwL/VIx5GBgqCso5GBiYXzIAiRIGxkQgTgLi5AoAbq216WYAAAA=");
    }

    private IDeque<Integer> mkDeque(Integer... values)
    {
        return ArrayDeque.of(IndexedArray.retained(values));
    }

    private IDeque<Integer> makeDeque(Random random,
                                      int size)
    {
        IDeque<Integer> list = ArrayDeque.of();
        for (int i = 0, limit = random.nextInt(3); i < limit; ++i) {
            list = list.insertLast(random.nextInt(size));
        }
        return list;
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
