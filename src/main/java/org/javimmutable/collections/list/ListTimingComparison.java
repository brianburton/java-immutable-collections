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

package org.javimmutable.collections.list;

import org.javimmutable.collections.JImmutableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class ListTimingComparison
{
    @SuppressWarnings("CallToSystemExit")
    public static void main(String[] argv)
            throws Exception
    {
        if (argv.length != 2) {
            System.err.println("usage: TimingComparison seed loops");
            System.exit(1);
        }

        final long seed = Long.parseLong(argv[0]);
        final int loops = Integer.parseInt(argv[1]);

        final int maxCommand = 6;

        runTests(seed, loops, 32, maxCommand);
        System.gc();
        Thread.sleep(500L);
        runTests(seed, loops, 1024, maxCommand);
        System.gc();
        Thread.sleep(500L);
        runTests(seed, loops, 10000, maxCommand);
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    private static void runTests(final long seed,
                                 int loops,
                                 int maxSize,
                                 int maxCommand)
    {
        System.out.printf("STARTING TEST WITH seed %d loops %d maxSize %d maxCommand %d%n", seed, loops, maxSize, maxCommand);
        for (int outerloop = 0; outerloop < 10; ++outerloop) {
            Random random = new Random(seed);
            int adds = 0;
            int sets = 0;
            int removes = 0;
            int gets = 0;
            long startMap = System.currentTimeMillis();
            List<Integer> expected = new ArrayList<Integer>();
            for (int i = 1; i <= loops; ++i) {
                int command = random.nextInt(maxCommand);
                if (expected.isEmpty()) {
                    int value = random.nextInt();
                    expected.add(value);
                    adds += 1;
                } else if (command <= 1) {
                    int value = random.nextInt();
                    if (expected.size() < maxSize) {
                        expected.add(value);
                        adds += 1;
                    } else {
                        int index = random.nextInt(expected.size());
                        expected.set(index, value);
                        sets += 1;
                    }
                } else if (command == 2) {
                    expected.remove(expected.size() - 1);
                    removes += 1;
                } else if (command == 3) {
                    int index = random.nextInt(expected.size());
                    int value = random.nextInt();
                    expected.set(index, value);
                    sets += 1;
                } else {
                    int index = random.nextInt(expected.size());
                    expected.get(index);
                    gets += 1;
                }
            }
            long endMap = System.currentTimeMillis();
            System.out.printf("jlist adds %d sets %s removes %d gets %d size %d elapsed %d%n", adds, sets, removes, gets, expected.size(), (endMap - startMap));

            random = new Random(seed);
            adds = 0;
            sets = 0;
            removes = 0;
            gets = 0;
            long startPer = System.currentTimeMillis();
//            JImmutableList<Integer> list = JImmutableArrayList.of();
//            JImmutableList<Integer> list = JImmutableBtreeList.of();
            JImmutableList<Integer> list = JImmutableTreeList.of();
            for (int i = 1; i <= loops; ++i) {
                int command = random.nextInt(maxCommand);
                if (list.isEmpty()) {
                    int value = random.nextInt();
                    list = list.insert(value);
                    adds += 1;
                } else if (command <= 1) {
                    int value = random.nextInt();
                    if (list.size() < maxSize) {
                        list = list.insert(value);
                        adds += 1;
                    } else {
                        int index = random.nextInt(list.size());
                        list = list.assign(index, value);
                        sets += 1;
                    }
                } else if (command == 2) {
                    list = list.deleteLast();
                    removes += 1;
                } else if (command == 3) {
                    int index = random.nextInt(list.size());
                    int value = random.nextInt();
                    list = list.assign(index, value);
                    sets += 1;
                } else {
                    int index = random.nextInt(list.size());
                    list.get(index);
                    gets += 1;
                }
            }
            long endPer = System.currentTimeMillis();
            System.out.printf("ilist adds %d sets %d removes %d gets %d size %d elapsed %d%n", adds, sets, removes, gets, list.size(), (endPer - startPer));

            Iterator<Integer> expectedIterator = expected.iterator();
            Iterator<Integer> listIterator = list.iterator();
            while (true) {
                assertEquals(expectedIterator.hasNext(), listIterator.hasNext());
                if (expectedIterator.hasNext()) {
                    assertEquals(expectedIterator.next(), listIterator.next());
                } else {
                    break;
                }
            }

            System.out.println();
        }
    }

    private static void assertEquals(boolean a,
                                     boolean b)
    {
        if (a != b) {
            throw new RuntimeException();
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
