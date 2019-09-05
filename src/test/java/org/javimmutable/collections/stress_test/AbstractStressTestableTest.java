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

package org.javimmutable.collections.stress_test;


import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AbstractStressTestableTest
    extends TestCase
{
    public void test()
        throws IOException
    {
        JImmutableList<String> tokens = StressTestUtil.loadTokens("src/site/markdown/index.md");
        testStandard(tokens);
    }

    private void testStandard(JImmutableList<String> tokens)
    {
        Random random = new Random();
        AbstractStressTestable testable = new JImmutableArrayStressTester(JImmutables.<String>array(), ArrayIndexRange.INTEGER);
        testMakeInsertList(testable, tokens, random);
        testMakeInsertJList(testable, tokens, random);
    }

    private void testMakeInsertList(AbstractStressTestable testable,
                                    JImmutableList<String> tokens,
                                    Random random)
    {
        int times = 100000;
        int total = 0;
        for (int i = 0; i < times; ++i) {
            List<String> list = testable.makeInsertList(tokens, random);
            total = total + list.size();
        }
        double average = (double)total / (double)times;
        assertTrue(average >= 0.9);
        assertTrue(average <= 1.1);
    }

    private void testMakeInsertJList(AbstractStressTestable testable,
                                     JImmutableList<String> tokens,
                                     Random random)
    {
        int times = 100000;
        int total = 0;
        for (int i = 0; i < times; ++i) {
            JImmutableList<String> list = testable.makeInsertJList(tokens, random);
            total = total + list.size();
        }
        double average = (double)total / (double)times;
        assertTrue(average >= 0.9);
        assertTrue(average <= 1.1);
    }
}
