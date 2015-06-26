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

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JImmutableRandomAccessListStressTester
    extends JImmutableListVerifier
    implements StressTestable
{
    private final JImmutableRandomAccessList<String> ralist;

    public JImmutableRandomAccessListStressTester(JImmutableRandomAccessList<String> ralist)
    {
        this.ralist = ralist;
    }
    @Override
    public void execute(Random random, JImmutableList<String> tokens)
    {
        JImmutableRandomAccessList<Integer> list = JImmutables.ralist();
        ArrayList<Integer> expected = new ArrayList<Integer>();
        int size = random.nextInt(10000);
        System.out.printf("Testing PersistentRandomAccessList of size %d%n", size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", list.size());
            ArrayList<Integer> col = new ArrayList<Integer>();
            for (int i = 0; i < size / 3; ++i) {
                int value = random.nextInt(999999999);
                if (list.isEmpty()) {
                    list = list.insert(value);
                    expected.add(value);
                } else {
                    switch (random.nextInt(10)) {
                    case 0:
                        list = list.insert(value);
                        expected.add(value);
                        break;
                    case 1:
                        list = list.insertLast(value);
                        expected.add(value);
                        break;
                    case 2:
                        list = list.insertFirst(value);
                        expected.add(0, value);
                        break;
                    case 3:
                        col.clear();
                        int times = random.nextInt(3);
                        for (int n = 0; n < times; n++) {
                            col.add(random.nextInt(value));
                        }
                        expected.addAll(col);
                        list = list.insertAllLast(col.iterator());
                        break;
                    case 4:
                        col.clear();
                        times = random.nextInt(3);
                        for (int n = 0; n < times; n++) {
                            col.add(random.nextInt(value));
                        }
                        expected.addAll(0, col);
                        list = list.insertAllFirst(col.iterator());
                        break;
                    case 5:
                        col.clear();
                        int index = random.nextInt(list.size());
                        times = random.nextInt(3);
                        for (int n = 0; n < times; n++) {
                            col.add(random.nextInt(value));
                        }
                        expected.addAll(index, col);
                        list = list.insertAll(index, col.iterator());
                        break;
                    default:
                        index = random.nextInt(list.size());
                        list = list.insert(index, value);
                        expected.add(index, value);
                        break;
                    }
                }
            }
            verifyContents(expected, list);
            System.out.printf("shrinking %d%n", list.size());
            for (int i = 0; i < size / 6; ++i) {
                if (list.size() == 1) {
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                } else {
                    switch (random.nextInt(8)) {
                    case 0:
                        list = list.deleteLast();
                        expected.remove(expected.size() - 1);
                        break;
                    case 1:
                        list = list.deleteFirst();
                        expected.remove(0);
                        break;
                    default:
                        int index = random.nextInt(list.size());
                        list = list.delete(index);
                        expected.remove(index);
                    }
                }
            }
            verifyContents(expected, list);
        }
        System.out.printf("cleanup %d%n", expected.size());
        while (list.size() > 0) {
            list = list.delete(0);
            expected.remove(0);
        }
        verifyContents(expected, list);
        System.out.println("PersistentRandomAccessList test completed without errors");
    }
}
