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

package org.javimmutable.collection.hash;

import org.javimmutable.collection.IMap;

import java.util.Random;

public final class TimingLoop
{
    private TimingLoop()
    {
    }

    public static void main(String[] args)
    {
        final int maxLoops = 250000;
        final int maxKey = 999999999;
        final int maxValue = 999999999;
        Random random = new Random(1000);
        int adds = 0;
        int removes = 0;
        int gets = 0;
        long startPer = System.currentTimeMillis();
        IMap<Integer, Integer> map = HashMap.of();
        for (int i = 1; i <= maxLoops; ++i) {
            int command = random.nextInt(8);
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
        System.out.printf("phm adds %d removes %d gets %d elapsed %d%n", adds, removes, gets, (endPer - startPer));
    }
}
