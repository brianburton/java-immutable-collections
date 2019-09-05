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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.util.JImmutables;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Superclass for test programs for JImmutables. The main purpose of the Testable is to run its execute method.
 * Each version of the method will first generate a goal size. Then it will grow the JImmutable by a third of
 * that size and shrink it by a sixth. This growing/shrinking repeats six times, until the JImmutable is the
 * generated size. All the values are then deleted.
 */
public abstract class AbstractStressTestable
{
    abstract void execute(Random random,
                          JImmutableList<String> tokens)
        throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;

    abstract JImmutableList<String> getOptions();

    protected String makeClassOption(Object obj)
    {
        return obj.getClass().getSimpleName().replaceFirst("JImmutable", "").replace("Empty", "").toLowerCase();
    }

    protected String getName(Object obj)
    {
        return obj.getClass().getSimpleName().replace("Empty", "");
    }

    protected JImmutableList<String> makeInsertJList(JImmutableList<String> tokens,
                                                     Random random)
    {
        return makeInsertJList(tokens, random, 3);
    }

    protected JImmutableList<String> makeInsertJList(JImmutableList<String> tokens,
                                                     Random random,
                                                     int maxToAdd)
    {
        return JImmutables.list(makeInsertList(tokens, random, maxToAdd));
    }

    protected List<String> makeInsertList(JImmutableList<String> tokens,
                                          Random random)
    {
        return makeInsertList(tokens, random, 3);
    }

    protected List<String> makeInsertList(JImmutableList<String> tokens,
                                          Random random,
                                          int maxToAdd)
    {
        List<String> list = new ArrayList<>();
        for (int i = 0, limit = random.nextInt(maxToAdd); i < limit; ++i) {
            list.add(RandomKeyManager.makeValue(tokens, random));
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
        return (holder.isFilled() == expectedHolder.isFilled()) && (!holder.isFilled() || holder.getValue().equals(expectedHolder.getValue()));
    }

    protected <K, V> boolean equivalentEntryHolder(Holder<JImmutableMap.Entry<K, V>> holder,
                                                   Holder<JImmutableMap.Entry<K, V>> expectedHolder)
    {
        if (holder.isFilled() != expectedHolder.isFilled()) {
            return false;
        }
        if (holder.isEmpty()) {
            return true;
        }
        JImmutableMap.Entry<K, V> entry = holder.getValue();
        JImmutableMap.Entry<K, V> expectedEntry = expectedHolder.getValue();
        return (entry.getKey().equals(expectedEntry.getKey())) && (entry.getValue().equals(expectedEntry.getValue()));
    }

    // some tests delete more than one in a step so we allow some variance
    protected void verifyFinalSize(int expected,
                                   int actual)
    {
        if (actual > expected) {
            throw new RuntimeException(String.format("final size is %d but expected at most %d%n", actual, expected));
        }
        if ((expected - actual) > 5) {
            throw new RuntimeException(String.format("final size is %d but expected approx %d%n", actual, expected));
        }
    }

    protected <T> Iterable<T> listIterable(JImmutableList<T> template,
                                           Iterable<T> values)
    {
        JImmutableList<T> answer = template.deleteAll();
        for (T value : values) {
            answer = answer.insert(value);
        }
        return answer;
    }

    protected <T> Iterable<T> plainIterable(Iterable<T> values)
    {
        return () -> values.iterator();
    }

    @Nonnull
    protected <K, V> List<JImmutableMap.Entry<K, V>> makeEntriesList(Map<K, V> expected)
    {
        final List<JImmutableMap.Entry<K, V>> entries = new ArrayList<>();

        for (Map.Entry<K, V> entry : expected.entrySet()) {
            entries.add(new MapEntry<>(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    protected <K, V> List<K> extractKeys(List<JImmutableMap.Entry<K, V>> entries)
    {
        return entries.stream().map(e -> e.getKey()).collect(Collectors.toList());
    }

    protected <K, V> List<V> extractValues(List<JImmutableMap.Entry<K, V>> entries)
    {
        return entries.stream().map(e -> e.getValue()).collect(Collectors.toList());
    }
}
