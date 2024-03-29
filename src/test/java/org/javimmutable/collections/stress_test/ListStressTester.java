///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.stress_test;

import org.javimmutable.collections.ICollectors;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListBuilder;
import org.javimmutable.collections.ILists;
import org.javimmutable.collections.Maybe;
import static org.javimmutable.collections.common.StandardSerializableTests.verifySerializable;
import org.javimmutable.collections.common.StandardStreamableTests;
import org.javimmutable.collections.common.TestUtil;
import static org.javimmutable.collections.iterators.IteratorHelper.plainIterable;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test program for all implementations of JImmutableList.
 * Divided into five sections: growing (adds new values to the beginning or end of the list),
 * updating (changes values at any index in the list), shrinking (removes values from either
 * end of the list), contains (tests methods that search for values in the list), and cleanup
 * (empties the list of all values).
 */
@SuppressWarnings("Duplicates")
public class ListStressTester
    extends
    StressTester
{
    private final Collector<String, ?, ? extends IList<String>> collector;

    private final IList<String> list;

    public ListStressTester(IList<String> list,
                            Collector<String, ?, ? extends IList<String>> collector)
    {
        super(getName(list));
        this.collector = collector;
        this.list = list;
    }

    @Override
    public IList<String> getOptions()
    {
        return ILists.of("list", getNameOption(list));
    }

    @Override
    public void execute(Random random,
                        IList<String> tokens)
    {
        IList<String> list = this.list;
        List<String> expected = new ArrayList<>();
        int size = 1 + random.nextInt(100000);
        System.out.printf("ListStressTest on %s of size %d%n", getName(list), size);

        // Verify that builder and collector work correctly.
        System.out.println("Filling a builder.");
        IListBuilder<String> builder = ILists.builder();
        for (int i = 0; i < size; ++i) {
            String value = RandomKeyManager.makeValue(tokens, random);
            expected.add(value);
            builder.add(value);
        }
        System.out.println("Verifying list from builder.");
        verifyContents(builder.build(), expected);
        System.out.println("Verifying list from parallel collector.");
        verifyContents(expected.parallelStream().collect(ICollectors.toList()), expected);
        System.out.println("Verifying list from reverse method.");
        Collections.reverse(expected);
        verifyContents(builder.build().reverse(), expected);
        expected.clear();

        System.out.println("Starting step tests.");
        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(6, size, random)) {
            assert expected.size() == list.size();
            System.out.printf("growing %d%n", list.size());
            while (expected.size() < step.growthSize()) {
                final int maxToAdd = Math.min(1000, step.growthSize() - expected.size());
                final int maxToDelete = maxToAdd / 4;
                switch (random.nextInt(16)) {
                    case 0: { //insert(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insert(value);
                        expected.add(value);
                        break;
                    }
                    case 1: { //insert(int, T)
                        int index = random.nextInt(Math.max(1, list.size()));
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insert(index, value);
                        expected.add(index, value);
                        break;
                    }
                    case 2: { //insertFirst(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insertFirst(value);
                        expected.add(0, value);
                        break;
                    }
                    case 3: { //insertLast(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insertLast(value);
                        expected.add(value);
                        break;
                    }
                    case 4: { //insertAll(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(plainIterable(values));
                        expected.addAll(values);
                        break;
                    }
                    case 5: { //insertAll(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 6: { //insertAll(int, Iterable);
                        int index = random.nextInt(Math.max(1, list.size()));
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(index, plainIterable(values));
                        expected.addAll(index, values);
                        break;
                    }
                    case 7: { //insertAll(int, Iterator)
                        int index = random.nextInt(Math.max(1, list.size()));
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(index, values.iterator());
                        expected.addAll(index, values);
                        break;
                    }
                    case 8: { //insertAllFirst(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(plainIterable(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 9: { //insertAllFirst(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(values.iterator());
                        expected.addAll(0, values);
                        break;
                    }
                    case 10: { //insertAllLast(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllLast(plainIterable(values));
                        expected.addAll(values);
                        break;
                    }
                    case 11: { //insertAllLast(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllLast(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 12: { //prefix(int)
                        int index = list.size() - random.nextInt(Math.max(1, maxToDelete));
                        if (index >= 0 && index <= list.size()) {
                            list = list.prefix(index);
                            if (index < expected.size()) {
                                expected.subList(index, expected.size()).clear();
                            }
                        }
                        break;
                    }
                    case 13: { //suffix(int)
                        int index = random.nextInt(Math.max(1, maxToDelete));
                        if (index >= 0 && index <= list.size()) {
                            list = list.suffix(index);
                            if (index > 0) {
                                expected.subList(0, index).clear();
                            }
                        }
                        break;
                    }
                    case 14: { //middle(int,int)
                        int offset = random.nextInt(Math.max(1, maxToDelete));
                        int limit = list.size() - random.nextInt(Math.max(1, maxToDelete));
                        if ((offset < limit) && (limit <= list.size()) && ((limit - offset) >= (expected.size() - maxToDelete))) {
                            list = list.middle(offset, limit);
                            if (limit < expected.size()) {
                                expected.subList(limit, expected.size()).clear();
                            }
                            if (offset > 0) {
                                expected.subList(0, offset).clear();
                            }
                        }
                        break;
                    }
                    case 15: { //reverse()
                        expected = TestUtil.reversedList(expected);
                        list = list.reverse();
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(list, expected);
            verifyContents(list.select(s -> s.length() % 2 == 0),
                           expected.stream().filter(s -> s.length() % 2 == 0).collect(Collectors.toList()));
            verifyContents(list.reject(s -> s.length() % 2 == 0),
                           expected.stream().filter(s -> s.length() % 2 != 0).collect(Collectors.toList()));
            verifyContents(list.stream().parallel().collect(collector), expected);
            verifyContents(list.transformSome(s -> s.length() % 2 == 0 ? Maybe.of(s + "x") : Maybe.empty()),
                           expected.stream().filter(s -> s.length() % 2 == 0).map(s -> s + "x").collect(Collectors.toList()));
            verifyContents(list.transform(s -> s + "x"), expected.stream().map(s -> s + "x").collect(Collectors.toList()));
            assertEquals(Stream.of("1", "2").collect(list.dequeCollector()),
                         list.insertAllLast(List.of("1", "2")));

            if (list.size() > 0) {
                int index = random.nextInt(list.size());
                verifyContents(list.prefix(index), expected.subList(0, index));
                verifyContents(list.suffix(index), expected.subList(index, expected.size()));
                if (index > 0) {
                    int start = random.nextInt(index);
                    verifyContents(list.middle(start, index), expected.subList(start, index));
                    verifyContents(list.slice(start, index), expected.subList(start, index));
                }
            }

            System.out.printf("updating %d%n", list.size());
            for (int i = 0; i < list.size() / 6; ++i) {
                switch (random.nextInt(2)) {
                    case 0: { //assign(int, T)
                        int index = random.nextInt(list.size());
                        String value = (random.nextBoolean()) ? list.get(index) : RandomKeyManager.makeValue(tokens, random);
                        list = list.assign(index, value);
                        expected.set(index, value);
                        break;
                    }
                    case 1: { //assign(int, T) - throw exception
                        int index = random.nextInt(list.size()) + list.size();
                        try {
                            list.assign(index, RandomKeyManager.makeValue(tokens, random));
                            throw new RuntimeException(String.format("error in assign(index, value) method call - index %d was out of bounds (size=%d), but method did not fail%n", index, list.size()));
                        } catch (IndexOutOfBoundsException e) {
                            //ignored -- expected
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(list, expected);

            System.out.printf("shrinking %d%n", list.size());
            while (expected.size() > step.shrinkSize()) {
                switch (random.nextInt(3)) {
                    case 0: //deleteFirst()
                        list = list.deleteFirst();
                        expected.remove(0);
                        break;
                    case 1: //deleteLast()
                        list = list.deleteLast();
                        expected.remove(expected.size() - 1);
                        break;
                    case 2: { //delete(int)
                        int index = random.nextInt(Math.max(1, list.size()));
                        list = list.delete(index);
                        expected.remove(index);
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(list, expected);

            System.out.printf("contains %d%n", list.size());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(2)) {
                    case 0: { //get(int)
                        int index = random.nextInt(list.size());
                        String value = list.get(index);
                        String expectedValue = expected.get(index);
                        if (!value.equals(expectedValue)) {
                            throw new RuntimeException(String.format("get(index) method call failed for %d - expected %s found %s%n", index, expectedValue, value));
                        }
                        break;
                    }
                    case 1: { //get(int) - throw exception
                        int index = random.nextInt(list.size()) + list.size();
                        try {
                            list.get(index);
                            throw new RuntimeException(String.format("error in get(index) method call - index %d was out of bounds, but method did not fail%n", index));
                        } catch (IndexOutOfBoundsException e) {
                            //ignored -- expected
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyIterator(list, expected);
        }
        verifyFinalSize(size, list.size());
        System.out.printf("cleanup %d%n", expected.size());
        int threshold = random.nextInt(3);
        while (list.size() > threshold) {
            switch (random.nextInt(3)) {
                case 0: //deleteLast()
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                    break;
                case 1: //deleteFirst()
                    list = list.deleteFirst();
                    expected.remove(0);
                    break;
                case 2: { //delete(int)
                    int index = random.nextInt(Math.max(1, list.size()));
                    list = list.delete(index);
                    expected.remove(index);
                    break;
                }
                default:
                    throw new RuntimeException();
            }
        }
        if (list.size() != 0) {
            verifyContents(list, expected);
            list = list.deleteAll();
            expected.clear();
        }
        if (list.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", list.size()));
        }
        verifyContents(list, expected);
        System.out.printf("ListStressTest on %s completed without errors%n", getName(list));
    }

    protected void verifyContents(IList<String> list,
                                  List<String> expected)
    {
        System.out.printf("checking contents with size %d%n", list.size());
        if (list.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), list.isEmpty()));
        }
        if (list.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), list.size()));
        }

        int index = 0;
        for (String expectedValue : expected) {
            String listValue = list.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, listValue));
            }
            index += 1;
        }
        if (!expected.equals(list.getList())) {
            throw new RuntimeException("method call failed - getList()\n");
        }
        list.checkInvariants();
        verifySerializable(null, list, IList.class);
    }

    protected void verifyIterator(IList<String> list,
                                  List<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", list.size());
        StandardIteratorTests.listIteratorTest(expected, list.iterator());
        StandardStreamableTests.verifyOrderedUsingCollection(expected, list);
    }
}
