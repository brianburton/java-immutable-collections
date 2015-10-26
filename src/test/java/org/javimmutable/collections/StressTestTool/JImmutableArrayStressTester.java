///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Test program for all implementations of JImmutableArray. Divided into four sections: growing
 * (adds new values at random indices), updating (changes the value at existing indices), shrinking
 * (removes values), contains (tests methods that check for values at specific indices), and
 * cleanup (empties the array of all values).
 */
public class JImmutableArrayStressTester
        extends AbstractStressTestable
{

    private JImmutableArray<String> array;
    private final int MAX_INDEX;
    private final int MAX_SIZE;

    public JImmutableArrayStressTester(JImmutableArray<String> array)
    {
        this.array = array;
        MAX_INDEX = (array instanceof Bit32Array) ? 32 : Integer.MAX_VALUE;
        MAX_SIZE = (array instanceof Bit32Array) ? 32 : 100000;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("array").insert(makeClassOption(array));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
    {
        final int size = random.nextInt(MAX_SIZE);
        final Map<Integer, String> expected = new TreeMap<Integer, String>();
        JImmutableArray<String> array = this.array;
        JImmutableRandomAccessList<Integer> indexList = JImmutables.ralist();

        System.out.printf("JImmutableArrayStressTest on %s of size %d%n", getName(array), size);
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", array.size());
            for (int i = 0; i < size / 3; ++i) {
                if (array instanceof Bit32Array && array.size() == MAX_SIZE) {
                    break;
                }
                int index = unusedIndex(expected, random);
                indexList = indexList.insert(index);
                String value = makeValue(tokens, random);
                switch (random.nextInt(2)) {
                case 0: {//assign(int, T)
                    array = array.assign(index, value);
                    expected.put(index, value);
                    break;
                }
                case 1: { //insert(Entry<Integer, T>)
                    JImmutableMap.Entry<Integer, String> entry = new MapEntry<Integer, String>(index, value);
                    array = (JImmutableArray<String>)array.insert(entry);
                    expected.put(index, value);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(array, expected);
            verifyIndexList(indexList, expected);

            System.out.printf("updating %d%n", array.size());
            for (int i = 0; i < array.size(); ++i) {
                int index = indexList.get(random.nextInt(indexList.size()));
                String value = makeValue(tokens, random);
                switch (random.nextInt(2)) {
                case 0: { //assign(int, T)
                    array = array.assign(index, value);
                    expected.put(index, value);
                    break;
                }
                case 1: { //insert(Entry<Integer, T>)
                    JImmutableMap.Entry<Integer, String> entry = new MapEntry<Integer, String>(index, value);
                    array = (JImmutableArray<String>)array.insert(entry);
                    expected.put(index, value);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(array, expected);
            verifyIndexList(indexList, expected);

            System.out.printf("shrinking %d%n", array.size());
            for (int i = 0; array.size() > 1 && i < size / 6; ++i) {
                //delete(int)
                for (int n = 0; n < 2 && array.size() > 1; ++n) {
                    boolean contains = random.nextBoolean() || (array instanceof Bit32Array && array.size() == MAX_SIZE);
                    int loc = random.nextInt(indexList.size());
                    int index = (contains) ? indexList.get(loc) : unusedIndex(expected, random);
                    array = array.delete(index);
                    expected.remove(index);
                    indexList = (contains) ? indexList.delete(loc) : indexList;
                }
            }
            verifyContents(array, expected);
            verifyIndexList(indexList, expected);

            System.out.printf("contains %d%n", array.size());
            for (int i = 0; i < size / 12; ++i) {
                int index = (random.nextBoolean()) ? random.nextInt(MAX_INDEX) : indexList.get(random.nextInt(indexList.size()));
                switch (random.nextInt(4)) {
                case 0: { //get(int)
                    String value = array.get(index);
                    String expectedValue = (expected.containsKey(index)) ? expected.get(index) : null;
                    if (!((value == null && expectedValue == null) || (expectedValue != null && expectedValue.equals(value)))) {
                        throw new RuntimeException(String.format("get(index) method call failed for %d - expected %s found %s%n", index, expectedValue, value));
                    }
                    break;

                }
                case 1: { //getValueOr(int, T)
                    String value = array.getValueOr(index, "");
                    String expectedValue = (expected.containsKey(index)) ? expected.get(index) : "";
                    assert value != null;
                    if (!value.equals(expectedValue)) {
                        throw new RuntimeException(String.format("getValueOr(index, default) method call failed for %d - expected %s found %s%n", index, expectedValue, value));
                    }
                    break;

                }
                case 2: { //find(int)
                    Holder<String> holder = array.find(index);
                    Holder<String> expectedHolder = (expected.containsKey(index)) ? Holders.of(expected.get(index)) : Holders.<String>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("find(index) method call failed for %d - expected %s found %s%n", index, expectedHolder, holder));
                    }
                    break;
                }
                case 3: { //findEntry(int)
                    Holder<JImmutableMap.Entry<Integer, String>> holder = array.findEntry(index);
                    Holder<JImmutableMap.Entry<Integer, String>> expectedHolder = (expected.containsKey(index)) ? Holders.<JImmutableMap.Entry<Integer, String>>of(new MapEntry<Integer, String>(index, expected.get(index))) : Holders.<JImmutableMap.Entry<Integer, String>>of();
                    if (!equivalentHolder(holder, expectedHolder)) {
                        throw new RuntimeException(String.format("findEntry(index) method call failed for %d - expected %s found %s%n", index, expectedHolder, holder));
                    }
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(array, expected);
        }
        verifyFinalSize(size, array.size());
        System.out.printf("cleanup %d%n", array.size());
        while (indexList.size() > random.nextInt(20)) {
            //delete(int)
            int loc = random.nextInt(indexList.size());
            int index = indexList.get(loc);
            array = array.delete(index);
            expected.remove(index);
            indexList = indexList.delete(loc);
        }
        if (array.size() != 0) {
            verifyContents(array, expected);
            array = array.deleteAll();
            expected.clear();
        }
        if (array.size() != 0) {
            throw new RuntimeException(String.format("expected array to be empty but it contained %d keys%n", array.size()));
        }
        verifyContents(array, expected);
        System.out.printf("JImmutableArrayStressTest on %s completed without errors%n", getName(array));
    }

    public void verifyContents(JImmutableArray<String> array,
                               Map<Integer, String> expected)
    {
        System.out.printf("checking contents with size %d%n", array.size());
        if (array.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), array.isEmpty()));
        }
        if (array.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), array.size()));
        }
        for (Map.Entry<Integer, String> entry : expected.entrySet()) {
            Integer index = entry.getKey();
            String expectedValue = entry.getValue();
            String value = array.getValueOr(index, null);
            if (!expectedValue.equals(value)) {
                throw new RuntimeException(String.format("value mismatch at index %d - expected %s found %s%n", index, expectedValue, value));
            }
        }
        if (!expected.equals(array.getMap())) {
            throw new RuntimeException("method call failed - getMap()\n");
        }
        array.checkInvariants();
    }

    private void verifyCursor(JImmutableArray<String> array,
                              Map<Integer, String> expected)
    {
        System.out.printf("checking cursor with size %d%n", array.size());
        final List<Integer> indices = asList(expected.keySet());
        final List<String> values = asList(expected.values());
        final List<JImmutableMap.Entry<Integer, String>> entries = new ArrayList<JImmutableMap.Entry<Integer, String>>();

        for (Map.Entry<Integer, String> entry : expected.entrySet()) {
            entries.add(new MapEntry<Integer, String>(entry.getKey(), entry.getValue()));
        }

        StandardCursorTest.listCursorTest(indices, array.keysCursor());
        StandardCursorTest.listCursorTest(values, array.valuesCursor());
        StandardCursorTest.listCursorTest(entries, array.cursor());
        StandardCursorTest.listIteratorTest(entries, array.iterator());
    }

    private void verifyIndexList(JImmutableRandomAccessList<Integer> indexList,
                                 Map<Integer, String> expected)
    {
        int indexListSize = indexList.size();
        if (indexListSize != expected.size()) {
            throw new RuntimeException(String.format("keys size mismatch - map: %d, keyList: %d%n", expected.size(), indexListSize));
        }
    }

    private int unusedIndex(Map<Integer, String> expected,
                            Random random)
    {
        int index = random.nextInt(MAX_INDEX);
        while (expected.containsKey(index)) {
            index = random.nextInt(MAX_INDEX);
        }
        return index;
    }
}

