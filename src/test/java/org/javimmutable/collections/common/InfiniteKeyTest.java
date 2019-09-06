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

package org.javimmutable.collections.common;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.common.InfiniteKey.testKey;

public class InfiniteKeyTest
    extends TestCase
{
    private static final int MAX = 0x7fffffff;

    @SuppressWarnings("NumericOverflow")
    public void testMax()
    {
        assertThat(MAX).isGreaterThan(0);
        assertThat(MAX + 1).isLessThan(0);
    }

    public void testCompareTo()
    {
        verifyNext(InfiniteKey.first());
        verifyNext(testKey(MAX - 500));
        verifyNext(testKey(0, MAX - 500));
        verifyNext(testKey(MAX, MAX - 500));
    }

    public void testToString()
    {
        assertThat(testKey(0).toString()).isEqualTo("0");
        assertThat(testKey(0, 1).toString()).isEqualTo("0.1");
        assertThat(testKey(0, 1, 2).toString()).isEqualTo("0.1.2");
    }

    public void testCache()
    {
        InfiniteKey key = InfiniteKey.first();
        for (int i = 0; i < InfiniteKey.CACHE_SIZE - 1; ++i) {
            InfiniteKey next = key.next();
            assertThat(next).isSameAs(key.next());
            key = next;
        }
    }

    private void verifyNext(InfiniteKey key)
    {
        for (int i = 1; i <= 1000; ++i) {
            InfiniteKey next = key.next();
            assertThat(key.compareTo(next)).isEqualTo(-1);
            assertThat(next.compareTo(key.next())).isEqualTo(0);
            assertThat(next.compareTo(key)).isEqualTo(1);
            assertThat(next.hashCode()).isEqualTo(key.next().hashCode());
            assertThat(key).isNotEqualTo(next);
            assertThat(next).isNotEqualTo(key);
            assertThat(key.next()).isEqualTo(next);
            key = next;
        }
    }
}
