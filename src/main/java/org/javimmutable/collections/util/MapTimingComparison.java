///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.trie32.TrieArray;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.hash.JImmutableHamtMap;
import org.javimmutable.collections.tree.JImmutableTreeMap;

import java.util.HashMap;
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

        MutableDelta javaElapsed = new MutableDelta();
        MutableDelta hashElapsed = new MutableDelta();
        MutableDelta treeElapsed = new MutableDelta();
        MutableDelta arrayElapsed = new MutableDelta();
        System.out.println("warm up runs");

        final int maxValue = 10 * loops;
        final int maxKey = 100000000;
        final int maxCommand = 10;
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new MutableDelta(), new MutableDelta(), new MutableDelta(), new MutableDelta());
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new MutableDelta(), new MutableDelta(), new MutableDelta(), new MutableDelta());
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new MutableDelta(), new MutableDelta(), new MutableDelta(), new MutableDelta());
        runLoop(seed, loops, maxValue, maxKey, maxCommand, new MutableDelta(), new MutableDelta(), new MutableDelta(), new MutableDelta());
        System.out.println();

        System.out.println("real runs");
        for (int i = 0; i < 25; ++i) {
            runLoop(seed + i, loops, maxValue, maxKey, maxCommand, javaElapsed, hashElapsed, treeElapsed, arrayElapsed);
            System.out.println();
        }
        System.out.printf("java avg: %.1f  tree avg: %.1f  hash avg: %.1f  array avg: %.1f%n", javaElapsed.getValue() / 25.0, treeElapsed.getValue() / 25.0, hashElapsed.getValue() / 25.0, arrayElapsed.getValue() / 25.0);
    }

    @SuppressWarnings("UnusedAssignment")
    private static void runLoop(int seed,
                                int loops,
                                int maxValue,
                                int maxKey,
                                int maxCommand,
                                MutableDelta javaElapsed,
                                MutableDelta hashElapsed,
                                MutableDelta treeElapsed,
                                MutableDelta arrayElapsed)
        throws Exception
    {
        Random random = new Random(seed);
        int adds = 0;
        int removes = 0;
        int gets = 0;
        long startMillis = System.currentTimeMillis();
        Map<Integer, Integer> expected = new HashMap<>();
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
        javaElapsed.add((int)(endMillis - startMillis));
        System.out.printf("java map adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, expected.size(), (endMillis - startMillis));
        expected = null;
        System.gc();
        Thread.sleep(500);

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        JImmutableMap<Integer, Integer> map = JImmutableTreeMap.of();
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

//        random = new Random(seed);
//        adds = 0;
//        removes = 0;
//        gets = 0;
//        startMillis = System.currentTimeMillis();
//        map = JImmutableHamtMap.of();
//        for (int i = 1; i <= loops; ++i) {
//            int command = random.nextInt(maxCommand);
//            if (command <= 2) {
//                Integer key = random.nextInt(maxKey);
//                Integer value = random.nextInt(maxValue);
//                map = map.assign(key, value);
//                adds += 1;
//            } else if (command == 3) {
//                Integer key = random.nextInt(maxKey);
//                map = map.delete(key);
//                removes += 1;
//            } else {
//                Integer key = random.nextInt(maxKey);
//                map.getValueOr(key, null);
//                gets += 1;
//            }
//        }
//        endMillis = System.currentTimeMillis();
//        hashElapsed.add((int)(endMillis - startMillis));
//        System.out.printf("jimm hamt adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, map.size(), (endMillis - startMillis));
//        map = null;
//        System.gc();
//        Thread.sleep(500);
                  
        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        startMillis = System.currentTimeMillis();
        map = JImmutableHamtMap.of();
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
        JImmutableArray<Integer> array = TrieArray.of();
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
