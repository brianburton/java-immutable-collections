///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ListTimingComparison
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
        final int maxCommand = 4;

        Random random = new Random(seed);
        int adds = 0;
        int sets = 0;
        int inserts = 0;
        int removes = 0;
        int gets = 0;
        long startMap = System.currentTimeMillis();
        List<Integer> expected = new ArrayList<Integer>();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (expected.size() == 0 || command <= 1) {
                int value = random.nextInt(maxValue);
                expected.add(value);
                adds += 1;
            } else {
                int index = random.nextInt(expected.size());
                expected.get(index);
                gets += 1;
            }
        }
        long endMap = System.currentTimeMillis();
        System.out.printf("jlist adds %d inserts %d sets %s removes %d gets %d size %d elapsed %d%n", adds, inserts, sets, removes, gets, expected.size(), (endMap - startMap));

        random = new Random(seed);
        adds = 0;
        sets = 0;
        removes = 0;
        gets = 0;
        long startPer = System.currentTimeMillis();
        PersistentArrayList<Integer> list = PersistentArrayList.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (list.size() == 0 || command <= 1) {
                int value = random.nextInt(maxValue);
                list = list.add(value);
                adds += 1;
            } else {
                int index = random.nextInt(list.size());
                list.get(index);
                gets += 1;
            }
        }
        long endPer = System.currentTimeMillis();
        System.out.printf("flist adds %d inserts %d sets %d removes %d gets %d size %d elapsed %d%n", adds, inserts, sets, removes, gets, list.size(), (endPer - startPer));

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

        random = new Random(seed);
        adds = 0;
        sets = 0;
        removes = 0;
        gets = 0;
        startPer = System.currentTimeMillis();
        PersistentLinkedStack<Integer> rlist = PersistentLinkedStack.of();
        for (int i = 1; i <= loops; ++i) {
            int command = random.nextInt(maxCommand);
            if (rlist.isEmpty() || command <= 1) {
                int value = random.nextInt(maxValue);
                rlist = rlist.add(value);
                adds += 1;
            }
        }
        endPer = System.currentTimeMillis();
        System.out.printf("rlist adds %d inserts %d sets %d removes %d gets %d size %d elapsed %d%n", adds, inserts, sets, removes, gets, list.size(), (endPer - startPer));

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
