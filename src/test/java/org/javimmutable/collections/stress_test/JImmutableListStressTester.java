///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Test program for all implementations of JImmutableList.
 * Divided into five sections: growing (adds new values to the beginning or end of the list),
 * updating (changes values at any index in the list), shrinking (removes values from either
 * end of the list), contains (tests methods that search for values in the list), and cleanup
 * (empties the list of all values).
 */
@SuppressWarnings("Duplicates")
public class JImmutableListStressTester
    extends AbstractListStressTestable
{
    private final Collector<String, ?, ? extends JImmutableList<String>> collector;

    private final JImmutableList<String> list;

    public JImmutableListStressTester(JImmutableList<String> list,
                                      Collector<String, ?, ? extends JImmutableList<String>> collector)
    {
        this.collector = collector;
        this.list = list;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("list").insert(makeClassOption(list));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
    {
        JImmutableList<String> list = this.list;
        List<String> expected = new ArrayList<>();
        int size = 1 + random.nextInt(100000);
        System.out.printf("JImmutableListStressTest on %s of size %d%n", getName(list), size);

        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(6, size, random)) {
            assert expected.size() == list.size();
            System.out.printf("growing %d%n", list.size());
            while (expected.size() < step.growthSize()) {
                final int maxToAdd = Math.min(1000, step.growthSize() - expected.size());
                final int maxToDelete = maxToAdd / 4;
                switch (random.nextInt(25)) {
                    case 0: { //insert(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insert(value);
                        expected.add(value);
                        break;
                    }
                    case 1: { //insertFirst(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insertFirst(value);
                        expected.add(0, value);
                        break;
                    }
                    case 2: { //insertLast(T)
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insertLast(value);
                        expected.add(value);
                        break;
                    }
                    case 3: { //insert(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insert(values);
                        expected.addAll(values);
                        break;
                    }
                    case 4: { //insert(jlist)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insert(listIterable(list, values));
                        expected.addAll(values);
                        break;
                    }
                    case 5: { //insertAll(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(plainIterable(values));
                        expected.addAll(values);
                        break;
                    }
                    case 6: { //insertAll(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(values.iterator());
                        expected.addAll(values);
                        break;
                    }
                    case 7: { //insertAll(jlist)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(listIterable(list, values));
                        expected.addAll(values);
                        break;
                    }
                    case 8: { //insertAll(Collection)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(values);
                        expected.addAll(values);
                        break;
                    }
                    case 9: { //insertAllLast(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllLast(plainIterable(values));
                        expected.addAll(values);
                        break;
                    }
                    case 10: { //insertAllLast(jlist)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllLast(listIterable(list, values));
                        expected.addAll(values);
                        break;
                    }
                    case 11: { //insertAllLast(Collection)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllLast(values);
                        expected.addAll(values);
                        break;
                    }
                    case 12: { //insertAllFirst(Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(plainIterable(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 13: { //insertAllFirst(Iterator)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(values.iterator());
                        expected.addAll(0, values);
                        break;
                    }
                    case 14: { //insertAllFirst(Indexed and Iterable)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(IndexedList.retained(values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 15: { //insertAllFirst(jlist)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(listIterable(list, values));
                        expected.addAll(0, values);
                        break;
                    }
                    case 16: { //insertAllFirst(Collection)
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAllFirst(values);
                        expected.addAll(0, values);
                        break;
                    }
                    case 17: { //insert(int, T)
                        int index = random.nextInt(Math.max(1, list.size()));
                        String value = RandomKeyManager.makeValue(tokens, random);
                        list = list.insert(index, value);
                        expected.add(index, value);
                        break;
                    }
                    case 18: { //insertAll(int, Cursorable);
                        int index = random.nextInt(Math.max(1, list.size()));
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(index, plainIterable(values));
                        expected.addAll(index, values);
                        break;
                    }
                    case 19: { //insertAll(int, Collection)
                        int index = random.nextInt(Math.max(1, list.size()));
                        List<String> values = makeInsertList(tokens, random, maxToAdd);
                        list = list.insertAll(index, values);
                        expected.addAll(index, values);
                        break;
                    }
                    case 20: { //insertAll(int, JImmutableList)
                        int index = random.nextInt(Math.max(1, list.size()));
                        JImmutableList<String> values = makeInsertJList(tokens, random, maxToAdd);
                        list = list.insertAll(index, values);
                        expected.addAll(index, values.getList());
                        break;
                    }
                    case 21: { //delete(int)
                        if (expected.size() > 0) {
                            int index = random.nextInt(Math.max(1, list.size()));
                            list = list.delete(index);
                            expected.remove(index);
                        }
                        break;
                    }
                    case 22: { //prefix(int)
                        int index = list.size() - random.nextInt(Math.max(1, maxToDelete));
                        if (index >= 0 && index <= list.size()) {
                            list = list.prefix(index);
                            if (index < expected.size()) {
                                expected.subList(index, expected.size()).clear();
                            }
                        }
                        break;
                    }
                    case 23: { //suffix(int)
                        int index = random.nextInt(Math.max(1, maxToDelete));
                        if (index >= 0 && index <= list.size()) {
                            list = list.suffix(index);
                            if (index > 0) {
                                expected.subList(0, index).clear();
                            }
                        }
                        break;
                    }
                    case 24: { //middle(int,int)
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
                    default:
                        throw new RuntimeException();
                }
            }
            verifyContents(list, expected);
            verifyContents(list.stream().parallel().collect(collector), expected);
            verifyContents(list.transformSome(s -> (s.length() % 2 == 0) ? Holders.of(s + "x") : Holders.of()),
                           expected.stream().filter(s -> s.length() % 2 == 0).map(s -> s + "x").collect(Collectors.toList()));
            verifyContents(list.transform(s -> s + "x"), expected.stream().map(s -> s + "x").collect(Collectors.toList()));

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
                            throw new RuntimeException(String.format("error in assign(index, value) method call - index %d was out of bounds, but method did not fail%n", index));
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
                switch (random.nextInt(2)) {
                    case 0: //deleteLast()
                        list = list.deleteLast();
                        expected.remove(expected.size() - 1);
                        break;
                    case 1: //deleteFirst()
                        list = list.deleteFirst();
                        expected.remove(0);
                        break;
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
            switch (random.nextInt(2)) {
                case 0: //deleteLast()
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                    break;
                case 1: //deleteFirst()
                    list = list.deleteFirst();
                    expected.remove(0);
                    break;
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
        System.out.printf("JImmutableListStressTest on %s completed without errors%n", getName(list));
    }
}