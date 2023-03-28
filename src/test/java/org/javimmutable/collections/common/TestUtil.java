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

package org.javimmutable.collections.common;

import org.assertj.core.api.ThrowableAssert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;

public class TestUtil
{
    /**
     * Utility method, useful in unit tests, that collects all of the values in the Iterator into a List
     * and returns the List.
     */
    public static <T> List<T> makeList(Iterator<T> iterator)
    {
        List<T> answer = new ArrayList<>();
        while (iterator.hasNext()) {
            answer.add(iterator.next());
        }
        return answer;
    }

    public static <T> List<T> makeList(@Nonnull Iterable<T> src)
    {
        final List<T> dst = new ArrayList<>();
        for (T value : src) {
            dst.add(value);
        }
        return dst;
    }

    /**
     * Creates a copy of the provided list with elements in reversed order.
     */
    public static <T> List<T> reversedList(@Nonnull List<T> src)
    {
        List<T> answer = new ArrayList<>(src.size());
        for (int i = src.size() - 1; i >= 0; --i) {
            answer.add(src.get(i));
        }
        return answer;
    }

    public static Set<String> makeSet(String... args)
    {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, args);
        return set;
    }

    public static Set<Integer> makeSet(int... args)
    {
        Set<Integer> set = new HashSet<>();
        for (int i : args) {
            set.add(i);
        }
        return set;
    }

    public static <T> void verifyContents(Iterable<T> a,
                                          Iterable<T> b)
    {
        List<T> al = makeList(a.iterator());
        List<T> bl = makeList(b.iterator());
        assertEquals(al, bl);
    }

    public static void verifyOutOfBounds(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(IndexOutOfBoundsException.class);
    }

    public static void verifyUnsupported(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(UnsupportedOperationException.class);
    }
}
