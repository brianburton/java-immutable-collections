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

package org.javimmutable.collection.tree;

import org.javimmutable.collection.IMap;
import org.javimmutable.collection.IMapEntry;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public final class TimingComparison
{
    private TimingComparison()
    {
    }

    public static void main(String[] argv)
    {
        if (argv.length != 2) {
            System.err.println("usage: TimingComparison seed loops");
            System.exit(1);
        }

        final int seed = Integer.parseInt(argv[0]);
        final int loops = Integer.parseInt(argv[1]);

        final int maxValue = 10 * loops;
        final int maxKey = 999999999;
        final int maxCommand = 6;

        for (int i = 0; i < 20; ++i) {
            runLoop(seed + i, loops, maxValue, maxKey, maxCommand);
            System.out.println();
        }
    }

    private static void runLoop(int seed,
                                int loops,
                                int maxValue,
                                int maxKey,
                                int maxCommand)
    {
        Random random = new Random(seed);
        int adds = 0;
        int removes = 0;
        int gets = 0;
        long startMap = System.currentTimeMillis();
        Map<Integer, Integer> expected = new java.util.TreeMap<>();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 1) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                expected.put(key, value);
                adds += 1;
            } else if (command == 2) {
                Integer key = random.nextInt(maxKey);
                expected.remove(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                expected.get(key);
                gets += 1;
            }
        }
        long endMap = System.currentTimeMillis();
        System.out.printf("map adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, expected.size(), (endMap - startMap));

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        long startPer = System.currentTimeMillis();
        IMap<Integer, Integer> map = TreeMap.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (command <= 1) {
                Integer key = random.nextInt(maxKey);
                Integer value = random.nextInt(maxValue);
                map = map.assign(key, value);
                adds += 1;
            } else if (command == 2) {
                Integer key = random.nextInt(maxKey);
                map = map.delete(key);
                removes += 1;
            } else {
                Integer key = random.nextInt(maxKey);
                map.find(key);
                gets += 1;
            }
        }
        long endPer = System.currentTimeMillis();
        System.out.printf("tree adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, expected.size(), (endPer - startPer));
        map.checkInvariants();

        Iterator<Map.Entry<Integer, Integer>> expectedIter = expected.entrySet().iterator();
        Iterator<IMapEntry<Integer, Integer>> mapIter = map.iterator();
        while (expectedIter.hasNext()) {
            if (!mapIter.hasNext()) {
                throw new RuntimeException();
            }
            Map.Entry<Integer, Integer> expectedEntry = expectedIter.next();
            IMapEntry<Integer, Integer> mapEntry = mapIter.next();
            assertEquals(expectedEntry.getKey(), mapEntry.getKey());
            assertEquals(expectedEntry.getValue(), mapEntry.getValue());
        }
    }

    private static void assertEquals(Integer a,
                                     Integer b)
    {
        if (!a.equals(b)) {
            throw new RuntimeException();
        }
    }
}
