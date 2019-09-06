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
import static org.javimmutable.collections.common.InfiniteKey.*;

public class InfiniteKeyTest
    extends TestCase
{
    public void testCompareTo()
    {
        verifyNext(InfiniteKey.first());
        verifyNext(testKey(HIGH - 500));
        verifyNext(testKey(LOW, HIGH - 500));
        verifyNext(testKey(HIGH, HIGH - 500));
    }

    public void testToString()
    {

        assertThat(testKey(LOW).toString()).isEqualTo("0");
        assertThat(testKey(LOW, LOW + 1).toString()).isEqualTo("0.1");
        assertThat(testKey(LOW, LOW + 1, LOW + 2).toString()).isEqualTo("0.1.2");
        assertThat(testKey(HIGH).next().toString()).isEqualTo("0.0");
        assertThat(testKey(HIGH, HIGH).next().toString()).isEqualTo("0.0.0");
    }

    public void testCache()
    {
        InfiniteKey key = InfiniteKey.first();
        for (int i = 0; i < InfiniteKey.CACHE_SIZE - 1; ++i) {
            InfiniteKey next = key.next();
            assertThat(next).isSameAs(key.next());
            key = next;
        }
        assertThat(key.next()).isNotSameAs(key.next());
    }

    private void verifyNext(InfiniteKey key)
    {
        for (int i = 1; i <= 1000; ++i) {
            InfiniteKey next = key.next();
            verifyCompare(key, next, -1);
            verifyCompare(next, key.next(), 0);
            verifyCompare(next, key, 1);
            assertThat(next.hashCode()).isEqualTo(key.next().hashCode());
            assertThat(key).isNotEqualTo(next);
            assertThat(next).isNotEqualTo(key);
            assertThat(key.next()).isEqualTo(next);
            key = next;
        }
    }

    private void verifyCompare(InfiniteKey a,
                               InfiniteKey b,
                               int diff)
    {
        assertThat(a.compareTo(b)).describedAs("%s <=> %s", a, b).isEqualTo(diff);
    }
}
