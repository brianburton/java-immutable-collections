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

package org.javimmutable.collections.array.bit32;

import java.util.Random;

public final class Bit32ArrayTimingLoop
{
    private Bit32ArrayTimingLoop()
    {
    }

    public static void main(String[] argv)
    {
        final int seed = 100;
        final int loops = 25000;
        final int operations = 1000;
        Random random = new Random(seed);
        for (int i = 0; i < 10; ++i) {
            runLoops(loops, operations, random);
        }
    }

    private static void runLoops(int loops,
                                 int operations,
                                 Random random)
    {
        final long totalStart = System.currentTimeMillis();
        for (int loop = 1; loop <= loops; ++loop) {
//            final long loopStart = System.currentTimeMillis();
            Bit32Array<Integer> array = Bit32Array.of();
            for (int i = 0; i < operations; ++i) {
                int command = random.nextInt(4);
                int index = random.nextInt(31);
                if (command <= 1) {
                    int value = random.nextInt(1000);
                    array = array.assign(index, value);
                } else if (command == 2) {
                    array = array.delete(index);
                } else {
                    array.find(index);
                }
            }
//            final long loopElapsed = System.currentTimeMillis() - loopStart;
//            System.out.printf("completed loop %d of %d operations in %d millis%n", loop, operations, loopElapsed);
        }
        final long totalElapsed = System.currentTimeMillis() - totalStart;
        System.out.printf("completed %d loops of %d operations in %d millis%n", loops, operations, totalElapsed);
    }
}
