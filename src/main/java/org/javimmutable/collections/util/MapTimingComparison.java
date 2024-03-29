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

package org.javimmutable.collections.util;

import org.javimmutable.collections.IArray;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.array.TrieArray;
import org.javimmutable.collections.hash.HashMap;
import org.javimmutable.collections.inorder.OrderedMap;
import org.javimmutable.collections.tree.TreeMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class MapTimingComparison
{
    private MapTimingComparison()
    {
    }

    public static void main(String[] argv)
        throws Exception
    {
        if (argv.length != 2) {
            System.err.println("usage: TimingComparison seed loops");
            System.exit(1);
        }

        final int seed = Integer.parseInt(argv[0]);
        final int loops = Integer.parseInt(argv[1]);

        List<Integer> javaHashTimes = new ArrayList<>();
        List<Integer> javaTreeTimes = new ArrayList<>();
        List<Integer> hashTimes = new ArrayList<>();
        List<Integer> treeTimes = new ArrayList<>();
        List<Integer> arrayTimes = new ArrayList<>();
        List<Integer> inOrderTimes = new ArrayList<>();
        System.out.println("warm up runs");

        final int maxValue = 10 * loops;
        final int maxKey = 100000000;
        final int maxCommand = 10;
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        System.out.println();

        System.out.println("real runs");
        for (int i = 0; i < 25; ++i) {
            runLoop(seed + i, loops, maxValue, maxKey, maxCommand, javaHashTimes, javaTreeTimes, hashTimes, treeTimes, arrayTimes, inOrderTimes);
            System.out.println();
        }
        System.out.printf("jhash avg: %.1f  jtree avg: %.1f  tree avg: %.1f  hash avg: %.1f  array avg: %.1f  order avg: %.1f%n",
                          avg(javaHashTimes), avg(javaTreeTimes), avg(treeTimes), avg(hashTimes), avg(arrayTimes), avg(inOrderTimes));
    }

    private static double avg(List<Integer> times)
    {
        times.sort(Comparator.naturalOrder());
        int tenPercent = (times.size() + 9) / 10;
        int limit = times.size() - tenPercent;
        double total = 0.0;
        for (int i = 0; i < limit; ++i) {
            total += times.get(i);
        }
        return total / (double)limit;
    }

    @SuppressWarnings("UnusedAssignment")
    private static void runLoop(int seed,
                                int loops,
                                int maxValue,
                                int maxKey,
                                int maxCommand,
                                List<Integer> javaHashElapsed,
                                List<Integer> javaTreeElapsed,
                                List<Integer> hashElapsed,
                                List<Integer> treeElapsed,
                                List<Integer> arrayElapsed,
                                List<Integer> inOrderElapsed)
        throws Exception
    {
        Random random = new Random(seed);
        int adds = 0;
        int removes = 0;
        int gets = 0;
        long startMillis = System.currentTimeMillis();
        Map<Integer, Integer> expected = new java.util.HashMap<>();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 2) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                expected.put(key, value);
                adds += 1;
            } else if (command == 3) {
                Integer key = random.nextInt(maxKey);
                expected.remove(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                expected.get(key);
                gets += 1;
            }
        }
        long endMillis = System.currentTimeMillis();
        javaHashElapsed.add((int)(endMillis - startMillis));
        System.out.printf("java map adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, expected.size(), (endMillis - startMillis));
        expected = null;
        System.gc();
        Thread.sleep(500);

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        expected = new java.util.TreeMap<>();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 2) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                expected.put(key, value);
                adds += 1;
            } else if (command == 3) {
                Integer key = random.nextInt(maxKey);
                expected.remove(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                expected.get(key);
                gets += 1;
            }
        }
        endMillis = System.currentTimeMillis();
        javaTreeElapsed.add((int)(endMillis - startMillis));
        System.out.printf("java tree adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, expected.size(), (endMillis - startMillis));
        expected = null;
        System.gc();
        Thread.sleep(500);

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        IMap<Integer, Integer> map = TreeMap.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 2) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                map = map.assign(key, value);
                adds += 1;
            } else if (command == 3) {
                Integer key = random.nextInt(maxKey);
                map = map.delete(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                map.getValueOr(key, null);
                gets += 1;
            }
        }
        endMillis = System.currentTimeMillis();
        treeElapsed.add((int)(endMillis - startMillis));
        System.out.printf("jimm tree adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, map.size(), (endMillis - startMillis));
        map = null;
        System.gc();
        Thread.sleep(500);

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        map = HashMap.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 2) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                map = map.assign(key, value);
                adds += 1;
            } else if (command == 3) {
                Integer key = random.nextInt(maxKey);
                map = map.delete(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                map.getValueOr(key, null);
                gets += 1;
            }
        }
        endMillis = System.currentTimeMillis();
        hashElapsed.add((int)(endMillis - startMillis));
        System.out.printf("jimm hash adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, map.size(), (endMillis - startMillis));
        map = null;
        System.gc();
        Thread.sleep(500);

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        map = OrderedMap.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 2) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                map = map.assign(key, value);
                adds += 1;
            } else if (command == 3) {
                Integer key = random.nextInt(maxKey);
                map = map.delete(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                map.getValueOr(key, null);
                gets += 1;
            }
        }
        endMillis = System.currentTimeMillis();
        inOrderElapsed.add((int)(endMillis - startMillis));
        System.out.printf("jimm iord adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, map.size(), (endMillis - startMillis));
        map = null;
        System.gc();
        Thread.sleep(500);

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        IArray<Integer> array = TrieArray.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 2) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                array = array.assign(key, value);
                adds += 1;
            } else if (command == 3) {
                Integer key = random.nextInt(maxKey);
                array = array.delete(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                array.find(key);
                gets += 1;
            }
        }
        endMillis = System.currentTimeMillis();
        arrayElapsed.add((int)(endMillis - startMillis));
        System.out.printf("jimm arry adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, array.size(), (endMillis - startMillis));
        array = null;
        System.gc();
        Thread.sleep(500);
    }
}
