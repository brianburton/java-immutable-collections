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

package org.javimmutable.collection.stress_test;

import org.javimmutable.collection.IDeque;
import org.javimmutable.collection.IList;
import org.javimmutable.collection.ILists;
import org.javimmutable.collection.Maybe;
import org.javimmutable.collection.common.StandardStreamableTests;
import org.javimmutable.collection.common.TestUtil;
import org.javimmutable.collection.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.javimmutable.collection.common.StandardSerializableTests.verifySerializable;
import static org.javimmutable.collection.iterators.IteratorHelper.plainIterable;

/**
 * Test program for all implementations of IDeque.
 * Divided into five sections: growing (adds new values to the beginning or end of the deque),
 * updating (changes values at any index in the deque), shrinking (removes values from either
 * end of the deque), contains (tests methods that search for values in the deque), and cleanup
 * (empties the deque of all values).
 */
@SuppressWarnings("Duplicates")
public class DequeStressTester
    extends StressTester
{
    private final Collector<String, ?, ? extends IDeque<String>> collector;

    private final IDeque<String> deque;

    public DequeStressTester(IDeque<String> deque,
                             Collector<String, ?, ? extends IDeque<String>> collector)
    {
        super(getName(deque));
        this.collector = collector;
        this.deque = deque;
    }

    @Override
    public IList<String> getOptions()
    {
        return ILists.of("deque", getNameOption(deque));
    }

    @Override
    public void execute(Random random,
                        IList<String> tokens)
    {
        IDeque<String> deque = this.deque;
        List<String> expected = new ArrayList<>();
        int size = 1 + random.nextInt(100000);
        System.out.printf("DequeStressTest on %s of size %d%n", getName(deque), size);

        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(6, size, random)) {
            assert expected.size() == deque.size();
            System.out.printf("growing %d%n", deque.size());
            while (expected.size() < step.growthSize()) {
                final int maxToAdd = Math.min(1000, step.growthSize() - expected.size());
                switch (random.nextInt(12)) {
                    case 0: { //insert(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        deque = deque.insert(value);
                        expected.add(value);
                        break;
                    }
                    case 1: { //insertFirst(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        deque = deque.insertFirst(value);
                        expected.add(0, value);
                        break;
                    }
                    case 2: { //insertLast(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        deque = deque.insertLast(value);
                        expected.add(value);
                        break;
                    }
                    case 3: { //insertAll(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        deque = deque.insertAll(plainIterable(values));
                        expected.addAll(values);
                        break;
                    }
                    case 4: { //insertAll(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        deque = deque.insertAll(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 5: { //insertAllLast(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        deque = deque.insertAllLast(plainIterable(values));
                        expected.addAll(values);
                        break;
                    }
                    case 6: { //insertAllLast(iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        deque = deque.insertAllLast(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 7: { //insertAllFirst(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        deque = deque.insertAllFirst(plainIterable(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 8: { //insertAllFirst(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        deque = deque.insertAllFirst(values.iterator());
                        expected.addAll(0, values);
                        break;
                    }
                    case 9: { //deleteFirst()
                        if (expected.size() > 0) {
                            deque = deque.deleteFirst();
                            expected.remove(0);
                        }
                        break;
                    }
                    case 10: { //deleteFirst()
                        if (expected.size() > 0) {
                            deque = deque.deleteLast();
                            expected.remove(expected.size() - 1);
                        }
                        break;
                    }
                    case 11: { //reverse()
                        expected = TestUtil.reversedList(expected);
                        deque = deque.reverse();
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(deque, expected);
            verifyContents(deque.stream().parallel().collect(collector), expected);
            verifyContents(deque.transformSome(s -> s.length() % 2 == 0 ? Maybe.present(s + "x") : Maybe.absent()),
                           expected.stream().filter(s -> s.length() % 2 == 0).map(s -> s + "x").collect(Collectors.toList()));
            verifyContents(deque.transform(s -> s + "x"), expected.stream().map(s -> s + "x").collect(Collectors.toList()));

            System.out.printf("updating %d%n", deque.size());
            for (int i = 0; i < deque.size() / 6; ++i) {
                switch (random.nextInt(2)) {
                    case 0: { //assign(int, T)
                        int index = random.nextInt(deque.size());
                        String value = (random.nextBoolean()) ? deque.get(index) : RandomKeyManager.makeValue(tokens, random);
                        deque = deque.assign(index, value);
                        expected.set(index, value);
                        break;
                    }
                    case 1: { //assign(int, T) - throw exception
                        int index = random.nextInt(deque.size()) + deque.size();
                        try {
                            deque.assign(index, RandomKeyManager.makeValue(tokens, random));
                            throw new RuntimeException(String.format("error in assign(index, value) method call - index %d was out of bounds (size=%d), but method did not fail%n", index, deque.size()));
                        } catch (IndexOutOfBoundsException e) {
                            //ignored -- expected
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(deque, expected);

            System.out.printf("shrinking %d%n", deque.size());
            while (expected.size() > step.shrinkSize()) {
                switch (random.nextInt(2)) {
                    case 0: //deleteLast()
                        deque = deque.deleteLast();
                        expected.remove(expected.size() - 1);
                        break;
                    case 1: //deleteFirst()
                        deque = deque.deleteFirst();
                        expected.remove(0);
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(deque, expected);

            System.out.printf("contains %d%n", deque.size());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(2)) {
                    case 0: { //get(int)
                        int index = random.nextInt(deque.size());
                        String value = deque.get(index);
                        String expectedValue = expected.get(index);
                        if (!value.equals(expectedValue)) {
                            throw new RuntimeException(String.format("get(index) method call failed for %d - expected %s found %s%n", index, expectedValue, value));
                        }
                        break;
                    }
                    case 1: { //get(int) - throw exception
                        int index = random.nextInt(deque.size()) + deque.size();
                        try {
                            deque.get(index);
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
            verifyIterator(deque, expected);
        }
        verifyFinalSize(size, deque.size());
        System.out.printf("cleanup %d%n", expected.size());
        int threshold = random.nextInt(3);
        while (deque.size() > threshold) {
            switch (random.nextInt(2)) {
                case 0: //deleteLast()
                    deque = deque.deleteLast();
                    expected.remove(expected.size() - 1);
                    break;
                case 1: //deleteFirst()
                    deque = deque.deleteFirst();
                    expected.remove(0);
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        if (deque.size() != 0) {
            verifyContents(deque, expected);
            deque = deque.deleteAll();
            expected.clear();
        }
        if (deque.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", deque.size()));
        }
        verifyContents(deque, expected);
        System.out.printf("DequeStressTest on %s completed without errors%n", getName(deque));
    }

    protected void verifyContents(IDeque<String> deque,
                                  List<String> expected)
    {
        System.out.printf("checking contents with size %d%n", deque.size());
        if (deque.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), deque.isEmpty()));
        }
        if (deque.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), deque.size()));
        }

        int index = 0;
        for (String expectedValue : expected) {
            String listValue = deque.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, listValue));
            }
            index += 1;
        }
        if (!expected.equals(deque.getList())) {
            throw new RuntimeException("method call failed - getList()\n");
        }
        deque.checkInvariants();
        verifySerializable(null, deque, IDeque.class);
    }

    protected void verifyIterator(IDeque<String> deque,
                                  List<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", deque.size());
        StandardIteratorTests.listIteratorTest(expected, deque.iterator());
        StandardStreamableTests.verifyOrderedUsingCollection(expected, deque);
    }
}
