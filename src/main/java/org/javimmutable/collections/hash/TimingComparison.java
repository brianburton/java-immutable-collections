///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.hash;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class TimingComparison
{
    public static void main(String[] argv)
            throws Exception
    {
        if (argv.length != 2) {
            System.err.println("usage: TimingComparison seed loops");
            System.exit(1);
        }

        final int seed = Integer.valueOf(argv[0]);
        final int loops = Integer.valueOf(argv[1]);

        final int maxValue = 10 * loops;
        final int maxKey = 999999999;
        final int maxCommand = 6;

        for (int i = 0; i < 25; ++i) {
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
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
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
        System.gc();

        random = new Random(seed);
        adds = 0;
        removes = 0;
        gets = 0;
        long startPer = System.currentTimeMillis();
        JImmutableHashMap<Integer, Integer> map = JImmutableHashMap.of();
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
        System.out.printf("phm adds %d removes %d gets %d size %d elapsed %d%n", adds, removes, gets, map.size(), (endPer - startPer));
        System.gc();
    }
}
