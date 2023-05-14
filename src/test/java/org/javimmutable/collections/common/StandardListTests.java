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

import org.javimmutable.collections.IList;
import org.javimmutable.collections.Maybe;

import static org.junit.Assert.*;

public class StandardListTests
{
    public static void standardTests(IList<Integer> empty)
    {
        verifyInsertAllFirst(empty);
        verifyInsertAllLast(empty);
        verifyTransform(empty);
        verifyAssign(empty);
        verifySingle(empty);
    }

    public static void verifyInsertAllFirst(IList<Integer> empty)
    {
        IList<Integer> actual = appendAll(empty, 1, 10);
        IList<Integer> expected = actual;
        while (actual.size() < 250_000) {
            final int addSize = actual.size() / 5;
            final int first = actual.size() + 1;
            final int last = actual.size() + addSize;
            actual = actual.insertAllFirst(appendAll(empty, first, last));
            expected = prependAll(expected, first, last);
            assertEquals(expected, actual);
        }
    }

    public static void verifyInsertAllLast(IList<Integer> empty)
    {
        IList<Integer> actual = appendAll(empty, 1, 10);
        IList<Integer> expected = actual;
        while (actual.size() < 250_000) {
            final int addSize = actual.size() / 5;
            final int first = actual.size() + 1;
            final int last = actual.size() + addSize;
            actual = actual.insertAllLast(appendAll(empty, first, last));
            expected = appendAll(expected, first, last);
            assertEquals(expected, actual);
        }
    }

    public static void verifyAssign(IList<Integer> empty)
    {
        for (int size = 0; size < 4096; ++size) {
            IList<Integer> expected = appendAll(empty, 1, size);
            IList<Integer> actual = appendAll(empty, 101, 100 + size);
            for (int i = 0; i < size; i++) {
                actual = actual.assign(i, 1 + i);
            }
            assertEquals(expected, actual);
        }
    }

    public static void verifyTransform(IList<Integer> empty)
    {
        IList<Integer> orig = appendAll(empty, 1, 20);
        IList<Integer> transformed = orig.transform(i -> i + 10);
        assertSame(orig.getClass(), transformed.getClass());
        assertEquals(appendAll(empty, 11, 30), transformed);

        transformed = orig.transformSome(i -> i < 11 ? Maybe.present(i) : Maybe.absent());
        assertSame(orig.getClass(), transformed.getClass());
        assertEquals(appendAll(empty, 1, 10), transformed);
    }

    private static void verifySingle(IList<Integer> empty)
    {
        assertEquals(Maybe.absent(), empty.single());
        assertEquals(Maybe.present(1), empty.insert(1).single());
        assertEquals(Maybe.present(null), empty.insert(null).single());
        assertEquals(Maybe.absent(), empty.insert(1).insert(2).single());
    }

    private static IList<Integer> appendAll(IList<Integer> answer,
                                            int first,
                                            int last)
    {
        for (int i = first; i <= last; ++i) {
            answer = answer.insert(i);
        }
        return answer;
    }

    private static IList<Integer> prependAll(IList<Integer> answer,
                                             int first,
                                             int last)
    {
        for (int i = last; i >= first; --i) {
            answer = answer.insertFirst(i);
        }
        return answer;
    }
}
