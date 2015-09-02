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
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public abstract class AbstractSetStressTestable
        extends AbstractStressTestable
{

    protected String containedValue(List<String> list,
                                    Random random)
    {
        return (list.isEmpty()) ? "" : list.get(random.nextInt(list.size()));
    }

    protected String notContainedValue(JImmutableList<String> tokens,
                                       Random random,
                                       Collection<String> expected)
    {
        String value = makeValue(tokens, random);
        while (expected.contains(value)) {
            value = makeValue(tokens, random);
        }
        return value;
    }

    protected String makeInsertValue(JImmutableList<String> tokens,
                                     Random random,
                                     List<String> list,
                                     Collection<String> expected)
    {
        return (random.nextBoolean()) ? containedValue(list, random) : notContainedValue(tokens, random, expected);
    }

    protected String makeDeleteValue(JImmutableList<String> tokens,
                                     Random random,
                                     List<String> list,
                                     Collection<String> expected)
    {
        String value;
        if (random.nextBoolean() || list.size() == 0) {
            value = notContainedValue(tokens, random, expected);
        } else {
            int index = random.nextInt(list.size());
            value = list.get(index);
            list.remove(index);
        }
        return value;
    }

    protected List<String> makeContainsList(JImmutableList<String> tokens,
                                            Random random,
                                            List<String> list,
                                            Collection<String> expected)
    {
        List<String> values = new ArrayList<String>();
        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
            if (random.nextBoolean()) {
                values.add(containedValue(list, random));
            } else {
                values.add(notContainedValue(tokens, random, expected));
            }
        }
        return values;
    }

    protected void verifyList(List<String> list,
                              Collection<String> expected)
    {
        if (list.size() != expected.size()) {
            throw new RuntimeException(String.format("list size mismatch - expected: %d, list: %d", expected.size(), list.size()));
        }
    }

    protected void verifyOrder(JImmutableSet<String> set,
                               Collection<String> expected)
    {
        StandardCursorTest.listCursorTest(asList(expected), set.cursor());
    }
}
