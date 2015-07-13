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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractStressTestable
{
    abstract void execute(Random random,
                          JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;

    abstract JImmutableList<String> getOptions();

    protected String makeValue(JImmutableList<String> tokens,
                               Random random)
    {
        int length = 1 + random.nextInt(250);
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(tokens.get(random.nextInt(tokens.size())));
        }
        return sb.toString();
    }

    protected String makeClassOption(Object obj)
    {
        return obj.getClass().getSimpleName().replaceFirst("JImmutable", "").toLowerCase();
    }

    protected JImmutableList<String> makeInsertList(JImmutableList<String> tokens,
                                                    Random random)
    {
        JImmutableList<String> list = JImmutables.list();
        for (int i = 0; i < random.nextInt(3); ++i) {
            list = list.insert(makeValue(tokens, random));
        }
        return list;
    }

    protected <T> List<T> asList(Iterable<T> values)
    {
        List<T> list = new ArrayList<T>();
        for (T value : values) {
            list.add(value);
        }
        return list;
    }

    protected <T> boolean equivalentHolder(Holder<T> holder,
                                           Holder<T> expectedHolder)
    {
        return (holder.isEmpty() == expectedHolder.isEmpty()) &&
               (holder.isFilled() == expectedHolder.isFilled()) &&
               !(holder.isFilled() && !(holder.getValue().equals(expectedHolder.getValue()))) &&
               (((holder.getValueOrNull() == null) && (expectedHolder.getValueOrNull() == null)) || (holder.getValueOrNull() != null && holder.getValueOrNull().equals(expectedHolder.getValueOrNull())));
        // && (holder.getValueOr(JImmutables.<String>set()).equals(expectedHolder.getValueOr(JImmutables.<String>set())));
    }
}
