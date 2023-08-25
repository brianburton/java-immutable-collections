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

package org.javimmutable.collections.indexed;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import junit.framework.TestCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.assertj.core.api.ThrowableAssert;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Maybe;

import java.util.List;

public class IndexedHelperTest
    extends TestCase
{
    public void testEmpty()
    {
        verifyIndexed(emptyList(), IndexedHelper.empty());
    }

    public void testIndexed()
    {
        verifyIndexed(asList(1), IndexedHelper.indexed(1));
        verifyIndexed(asList(1, 2), IndexedHelper.indexed(1, 2));
        verifyIndexed(asList(1, 2, 3), IndexedHelper.indexed(1, 2, 3));
        verifyIndexed(asList(1, 2, 3, 4, 5), IndexedHelper.indexed(1, 2, 3, 4, 5));
    }

    public void testRepeating()
    {
        verifyOutOfBounds(() -> IndexedHelper.repeating(1, -1));
        verifyIndexed(emptyList(), IndexedHelper.repeating(1, 0));
        verifyIndexed(asList(1), IndexedHelper.repeating(1, 1));
        verifyIndexed(asList(1, 1), IndexedHelper.repeating(1, 2));
        verifyIndexed(asList(1, 1, 1), IndexedHelper.repeating(1, 3));
    }

    public void testRange()
    {
        verifyOutOfBounds(() -> IndexedHelper.range(1, -1));
        verifyIndexed(emptyList(), IndexedHelper.range(1, 0));
        verifyIndexed(asList(1), IndexedHelper.range(1, 1));
        verifyIndexed(asList(1, 2), IndexedHelper.range(1, 2));
        verifyIndexed(asList(1, 2, 3), IndexedHelper.range(1, 3));
    }

    public void testTransformed()
    {
        verifyIndexed(asList(-1, -2, -3), IndexedHelper.indexed(1, 2, 3).transformed(x -> -x));
    }

    public void testReversed()
    {
        final Indexed<Integer> empty = IndexedHelper.empty();
        assertSame(empty, empty.reversed());

        verifyIndexed(emptyList(), empty.reversed());
        verifyIndexed(asList(1), IndexedHelper.indexed(1).reversed());
        verifyIndexed(asList(2, 1), IndexedHelper.indexed(1, 2).reversed());
        verifyIndexed(asList(3, 2, 1), IndexedHelper.indexed(1, 2, 3).reversed());
        verifyIndexed(asList(5, 4, 3, 2, 1), IndexedHelper.indexed(1, 2, 3, 4, 5).reversed());
    }

    public void testPrefix()
    {
        final Indexed<Integer> empty = IndexedHelper.empty();
        verifyOutOfBounds(() -> empty.prefix(-1));
        assertSame(empty, empty.prefix(0));
        verifyOutOfBounds(() -> empty.prefix(1));

        final Indexed<Integer> full = IndexedHelper.indexed(1, 2, 3);
        verifyOutOfBounds(() -> full.prefix(-1));
        verifyIndexed(emptyList(), full.prefix(0));
        verifyIndexed(asList(1), full.prefix(1));
        verifyIndexed(asList(1, 2), full.prefix(2));
        assertSame(full, full.prefix(3));
        verifyOutOfBounds(() -> full.prefix(4));
    }

    private void verifyIndexed(List<Integer> expected,
                               Indexed<Integer> actual)
    {
        assertThat(actual.size()).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            assertThat(actual.get(i)).isEqualTo(expected.get(i));
        }
        verifyOutOfBounds(() -> actual.get(-1));
        verifyOutOfBounds(() -> actual.get(expected.size()));
        assertEquals(Maybe.empty(), actual.find(-1));
        assertEquals(Maybe.empty(), actual.find(expected.size()));
    }

    private void verifyOutOfBounds(ThrowableAssert.ThrowingCallable callable)
    {
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
            .isThrownBy(callable);
    }
}
