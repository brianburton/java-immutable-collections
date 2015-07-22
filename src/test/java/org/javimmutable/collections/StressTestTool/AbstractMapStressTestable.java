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

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractMapStressTestable
        extends AbstractStressTestable
{

    protected <V> String unusedKey(JImmutableList<String> tokens,
                                   Random random,
                                   Map<String, V> expected)
    {
        String key = makeValue(tokens, random);
        while (expected.containsKey(key)) {
            key = makeValue(tokens, random);
        }
        return key;
    }

    protected String containedKey(List<String> keysList,
                                  Random random)
    {
        return (keysList.isEmpty()) ? "" : keysList.get(random.nextInt(keysList.size()));
    }

    protected <V> String makeDeleteKey(JImmutableList<String> tokens,
                                       Random random,
                                       List<String> keysList,
                                       Map<String, V> expected)
    {
        String key = "";
        if (random.nextBoolean() || keysList.size() == 0) {
            key = unusedKey(tokens, random, expected);
        } else {
            int index = random.nextInt(keysList.size());
            key = keysList.get(index);
            keysList.remove(index);
        }
        return key;
    }

    protected <V> void verifyKeysList(List<String> keysList,
                                      Map<String, V> expected)
    {
        if (keysList.size() != expected.size()) {
            throw new RuntimeException(String.format("keys size mismatch - map: %d, keyList: %d%n", expected.size(), keysList.size()));
        }
    }
}
