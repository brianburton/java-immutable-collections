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
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableRandomAccessList. Tests only the functionality
 * that is not contained in a regular JImmutableList. Divided into three sections: growing (adds
 * values to the middle of the ralist), shrinking (removes values from the middle), and cleanup
 * (empties the ralist of all values).
 */
public class JImmutableRandomAccessListStressTester
        extends AbstractListStressTestable
{
    private JImmutableRandomAccessList<String> ralist;

    public JImmutableRandomAccessListStressTester(JImmutableRandomAccessList<String> ralist)
    {
        this.ralist = ralist;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("ralist").insert(makeClassOption(ralist));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
    {
        List<String> expected = new ArrayList<String>();
        JImmutableRandomAccessList<String> ralist = this.ralist;
        int size = random.nextInt(100000);

        String initialValue = makeValue(tokens, random);
        ralist = ralist.insert(initialValue);
        expected.add(initialValue);

        System.out.printf("JImmutableRandomAccessListStressTest on %s of size %d%n", getName(ralist), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", ralist.size());

            for (int i = 0; i < size / 3; ++i) {
                int index = random.nextInt(ralist.size());
                switch (random.nextInt(2)) {
                case 0: { //insert(int, T)
                    String value = makeValue(tokens, random);
                    ralist = ralist.insert(index, value);
                    expected.add(index, value);
                    break;
                }
                case 1: { //insertAll(int, Cursorable);
                    List<String> values = makeInsertList(tokens, random);
                    ralist = ralist.insertAll(index, IterableCursorable.of(values));
                    expected.addAll(index, values);
                    break;
                }
                case 2: { //insertAll(int, Collection)
                    List<String> values = makeInsertList(tokens, random);
                    ralist = ralist.insertAll(index, values);
                    expected.addAll(index, values);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(ralist, expected);
            System.out.printf("shrinking %d%n", ralist.size());
            for (int i = 0; i < size / 6; ++i) {
                int index = random.nextInt(ralist.size());
                ralist = ralist.delete(index);
                expected.remove(index);
            }
            verifyContents(ralist, expected);
            verifyCursor(ralist, expected);
        }
        System.out.printf("cleanup %d%n", expected.size());
        while (ralist.size() > 0) {
            int index = random.nextInt(ralist.size());
            ralist = ralist.delete(index);
            expected.remove(index);
        }
        if (ralist.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", ralist.size()));
        }
        verifyContents(ralist, expected);
        System.out.printf("JImmutableRandomAccessListStressTest on %s completed without errors%n", getName(ralist));
    }
}
