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

import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.Sequence;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.tree_list.JImmutableTreeList;
import org.javimmutable.collections.util.JImmutables;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Test program to run an infinite loop feeding data to a PersistentMap, querying the
 * data, and deleting the data to verify the map always contains what it should.
 */
public class StressTestLoop
{
    public void execute(String[] filenames)
            throws Exception
    {
        JImmutableList<AbstractStressTestable> testers = JImmutables.<AbstractStressTestable>list()
                .insert(new JImmutableListStressTester(JImmutables.<String>list()))
                .insert(new JImmutableListStressTester(JImmutables.<String>ralist()))
                .insert(new JImmutableListStressTester(JImmutableTreeList.<String>of()))

                .insert(new JImmutableRandomAccessListStressTester(JImmutables.<String>ralist()))
                .insert(new JImmutableRandomAccessListStressTester(JImmutableTreeList.<String>of()))

                .insert(new JImmutableSetStressTester(JImmutables.<String>set(), HashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>insertOrderSet(), LinkedHashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>sortedSet(), TreeSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>multiset(), HashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>insertOrderMultiset(), LinkedHashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>sortedMultiset(), TreeSet.class))

                .insert(new JImmutableMapStressTester(JImmutableHashMap.<String, String>usingTree(), HashMap.class))
                .insert(new JImmutableMapStressTester(JImmutableHashMap.<String, String>usingList(), HashMap.class))
                .insert(new JImmutableMapStressTester(JImmutables.<String, String>sortedMap(), TreeMap.class))
                .insert(new JImmutableMapStressTester(JImmutables.<String, String>insertOrderMap(), LinkedHashMap.class));


        Random random = new Random();
        JImmutableList<String> tokens = loadTokens(filenames);
        System.out.printf("Loaded %d tokens from %d files%n", tokens.size(), filenames.length);
        //noinspection InfiniteLoopStatement
        while (true) {
            for (AbstractStressTestable tester : testers) {
                Long seed = System.currentTimeMillis();
                random.setSeed(seed);
                System.out.printf("Starting with seed %d%n", seed);
                tester.execute(random, tokens);
            }
        }
    }

    public static void main(String[] argv)
            throws Exception
    {
        new StressTestLoop().execute(argv);
    }

    private void testStack(Random random)
    {
        JImmutableStack<Integer> stack = JImmutables.stack();
        LinkedList<Integer> expected = new LinkedList<Integer>();
        int size = random.nextInt(1000);
        System.out.printf("Testing PersistentStack of size %d%n", size);
        for (int i = 0; i < size; ++i) {
            int value = random.nextInt(999999999);
            stack = stack.insert(value);
            expected.add(0, value);
        }
        Sequence<Integer> seq = stack;
        for (Integer value : expected) {
            if (!value.equals(seq.getHead())) {
                throw new RuntimeException(String.format("found mismatch expected %d found %d", value, seq.getHead()));
            }
            seq = seq.getTail();
        }
        if (!seq.isEmpty()) {
            throw new RuntimeException("expected to be at end of stack but found more values");
        }
        System.out.println("PersistentStack test completed without errors");
    }

    private void testArray(JImmutableList<String> tokens,
                           Random random)
    {
        final int tokenCount = 1 + random.nextInt(100000);
        final List<Integer> keys = new ArrayList<Integer>();
        final Map<Integer, String> expected = new HashMap<Integer, String>();
        JImmutableArray<String> map = JImmutables.array();
        JImmutableRandomAccessList<Integer> pkeys = JImmutables.ralist();
        JImmutableRandomAccessList<String> pvalues = JImmutables.ralist();
        System.out.printf("starting %s test with %d tokens and factory %s%n", map.getClass().getSimpleName(), tokenCount, map.getClass().getSimpleName());
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < tokenCount / 3; ++i) {
                int key = random.nextInt();
                String value = makeKey(tokens, random);
                keys.add(key);
                pkeys = pkeys.insert(key);
                pvalues = pvalues.insert(value);
                expected.put(key, value);
                map = map.assign(key, value);
            }
            verifyContents(expected, map);
            System.out.printf("updating %d%n", map.size());
            for (int i = 0; i < map.size(); ++i) {
                int keyIndex = random.nextInt(keys.size());
                int key = pkeys.get(keyIndex);
                int valueIndex = random.nextInt(keys.size());
                String value = pvalues.get(valueIndex);
                expected.put(key, value);
                pvalues = pvalues.assign(keyIndex, value);
                map = map.assign(key, value);
            }
            verifyContents(expected, map);
            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < tokenCount / 6; ++i) {
                int keyIndex = random.nextInt(keys.size());
                int key = pkeys.get(keyIndex);
                expected.remove(key);
                map = map.delete(key);
                keys.remove(keyIndex);
                pkeys = pkeys.delete(keyIndex);
                pvalues = pvalues.delete(keyIndex);
            }
            verifyContents(expected, map);
        }
        if (keys.size() != pkeys.size()) {
            throw new RuntimeException(String.format("key size mismatch - expected %d found %d%n", keys.size(), pkeys.size()));
        }
        System.out.printf("comparing %d keys%n", pkeys.size());
        for (int i = 0; i < pkeys.size(); ++i) {
            int key = keys.get(i);
            int pkey = pkeys.get(i);
            if (key != pkey) {
                throw new RuntimeException(String.format("key mismatch - expected %s found %s%n", key, pkey));
            }
        }
        System.out.printf("cleanup %d%n", map.size());
        for (Integer key : keys) {
            expected.remove(key);
            map = map.delete(key);
        }
        if (map.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", map.size()));
        }
        verifyContents(expected, map);
        System.out.printf("completed %s test without errors%n", map.getClass().getSimpleName());
    }

    private void testBadHashMap(JImmutableList<String> tokens,
                                Random random)
    {
        final int tokenCount = 1 + random.nextInt(100000);
        final List<BadHash<String>> keys = new ArrayList<BadHash<String>>();
        final Map<BadHash<String>, String> expected = new HashMap<BadHash<String>, String>();
        JImmutableMap<BadHash<String>, String> map = JImmutables.map();
        JImmutableRandomAccessList<BadHash<String>> pkeys = JImmutables.ralist();
        System.out.printf("starting %s BadHash test with %d tokens and factory %s%n", map.getClass().getSimpleName(), tokenCount, map.getClass().getSimpleName());
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < tokenCount / 3; ++i) {
                BadHash<String> key = new BadHash<String>(makeKey(tokens, random));
                keys.add(key);
                pkeys = pkeys.insert(key);
                expected.put(key, key.value);
                map = map.assign(key, key.value);
            }
            verifyContents(expected, map);
            System.out.printf("updating %d%n", map.size());
            for (int i = 0; i < map.size(); ++i) {
                int keyIndex = random.nextInt(keys.size());
                BadHash<String> key = pkeys.get(keyIndex);
                int valueIndex = random.nextInt(keys.size());
                String value = pkeys.get(valueIndex).value;
                expected.put(key, value);
                map = map.assign(key, value);
            }
            verifyContents(expected, map);
            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < tokenCount / 6; ++i) {
                int keyIndex = random.nextInt(keys.size());
                BadHash<String> key = pkeys.get(keyIndex);
                expected.remove(key);
                map = map.delete(key);
                keys.remove(keyIndex);
                pkeys = pkeys.delete(keyIndex);
            }
            verifyContents(expected, map);
        }
        if (keys.size() != pkeys.size()) {
            throw new RuntimeException(String.format("key size mismatch - expected %d found %d%n", keys.size(), pkeys.size()));
        }
        System.out.printf("comparing %d keys%n", pkeys.size());
        for (int i = 0; i < pkeys.size(); ++i) {
            BadHash<String> key = keys.get(i);
            BadHash<String> pkey = pkeys.get(i);
            if (!key.equals(pkey)) {
                throw new RuntimeException(String.format("key mismatch - expected %s found %s%n", key, pkey));
            }
        }
        System.out.printf("cleanup %d%n", map.size());
        for (BadHash<String> key : keys) {
            expected.remove(key);
            map = map.delete(key);
        }
        if (map.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", map.size()));
        }
        verifyContents(expected, map);
        System.out.printf("completed %s test without errors%n", map.getClass().getSimpleName());
    }

    private void testComparableBadHashMap(JImmutableList<String> tokens,
                                          Random random)
    {
        final int tokenCount = 1 + random.nextInt(100000);
        final List<ComparableBadHash<String>> keys = new ArrayList<ComparableBadHash<String>>();
        final Map<ComparableBadHash<String>, String> expected = new HashMap<ComparableBadHash<String>, String>();
        JImmutableMap<ComparableBadHash<String>, String> map = JImmutables.map();
        JImmutableRandomAccessList<ComparableBadHash<String>> pkeys = JImmutables.ralist();
        System.out.printf("starting %s ComparableBadHash test with %d tokens and factory %s%n", map.getClass().getSimpleName(), tokenCount, map.getClass().getSimpleName());
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < tokenCount / 3; ++i) {
                ComparableBadHash<String> key = new ComparableBadHash<String>(makeKey(tokens, random));
                keys.add(key);
                pkeys = pkeys.insert(key);
                expected.put(key, key.value);
                map = map.assign(key, key.value);
            }
            verifyContents(expected, map);
            System.out.printf("updating %d%n", map.size());
            for (int i = 0; i < map.size(); ++i) {
                int keyIndex = random.nextInt(keys.size());
                ComparableBadHash<String> key = pkeys.get(keyIndex);
                int valueIndex = random.nextInt(keys.size());
                String value = pkeys.get(valueIndex).value;
                expected.put(key, value);
                map = map.assign(key, value);
            }
            verifyContents(expected, map);
            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < tokenCount / 6; ++i) {
                int keyIndex = random.nextInt(keys.size());
                ComparableBadHash<String> key = pkeys.get(keyIndex);
                expected.remove(key);
                map = map.delete(key);
                keys.remove(keyIndex);
                pkeys = pkeys.delete(keyIndex);
            }
            verifyContents(expected, map);
        }
        if (keys.size() != pkeys.size()) {
            throw new RuntimeException(String.format("key size mismatch - expected %d found %d%n", keys.size(), pkeys.size()));
        }
        System.out.printf("comparing %d keys%n", pkeys.size());
        for (int i = 0; i < pkeys.size(); ++i) {
            ComparableBadHash<String> key = keys.get(i);
            ComparableBadHash<String> pkey = pkeys.get(i);
            if (!key.equals(pkey)) {
                throw new RuntimeException(String.format("key mismatch - expected %s found %s%n", key, pkey));
            }
        }
        System.out.printf("cleanup %d%n", map.size());
        for (ComparableBadHash<String> key : keys) {
            expected.remove(key);
            map = map.delete(key);
        }
        if (map.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", map.size()));
        }
        verifyContents(expected, map);
        System.out.printf("completed %s test without errors%n", map.getClass().getSimpleName());
    }

    private <K, V> void verifyContents(Map<K, V> expected,
                                       JImmutableMap<K, V> map)
    {
        System.out.printf("checking contents with size %d%n", map.size());
        if (map.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), map.size()));
        }
        for (JImmutableMap.Entry<K, V> entry : map) {
            V mapValue = map.find(entry.getKey()).getValueOrNull();
            V expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
        for (Map.Entry<K, V> entry : expected.entrySet()) {
            V mapValue = map.find(entry.getKey()).getValueOrNull();
            V expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
    }

    private <V> void verifyContents(Map<Integer, V> expected,
                                    JImmutableArray<V> map)
    {
        System.out.printf("checking contents with size %d%n", map.size());
        if (map.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), map.size()));
        }
        for (JImmutableMap.Entry<Integer, V> entry : map) {
            V mapValue = map.find(entry.getKey()).getValueOrNull();
            V expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
        for (Map.Entry<Integer, V> entry : expected.entrySet()) {
            V mapValue = map.find(entry.getKey()).getValueOrNull();
            V expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
    }

    private String makeKey(JImmutableList<String> tokens,
                           Random random)
    {
        int length = 1 + random.nextInt(250);
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(tokens.get(random.nextInt(tokens.size())));
        }
        return sb.toString();
    }

    private JImmutableList<String> loadTokens(String[] filenames)
            throws IOException
    {
        JImmutableSet<String> tokens = JImmutables.set();
        for (String filename : filenames) {
            tokens = addTokensFromFile(tokens, filename);
        }
        return JImmutables.list(tokens);
    }

    private JImmutableSet<String> addTokensFromFile(JImmutableSet<String> tokens,
                                                    String filename)
            throws IOException
    {
        BufferedReader inp = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        try {
            for (String line = inp.readLine(); line != null; line = inp.readLine()) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                while (tokenizer.hasMoreTokens()) {
                    tokens = tokens.insert(tokenizer.nextToken());
                }
            }
        } finally {
            inp.close();
        }
        return tokens;
    }

    @SuppressWarnings("unchecked")
    private static class BadHash<T>
    {
        private final T value;

        private BadHash(T value)
        {
            this.value = value;
        }

        @Override
        public int hashCode()
        {
            return value.hashCode() >>> 8;
        }

        @Override
        public boolean equals(Object o)
        {
            return (o instanceof BadHash) && value.equals(((BadHash<T>)o).value);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ComparableBadHash<T extends Comparable<T>>
            implements Comparable<ComparableBadHash<T>>
    {
        private final T value;

        private ComparableBadHash(T value)
        {
            this.value = value;
        }

        @Override
        public int hashCode()
        {
            return value.hashCode() >>> 8;
        }

        @Override
        public boolean equals(Object o)
        {
            return (o instanceof ComparableBadHash) && value.equals(((ComparableBadHash<T>)o).value);
        }

        @Override
        public int compareTo(@Nonnull ComparableBadHash<T> other)
        {
            return value.compareTo(other.value);
        }
    }
}
