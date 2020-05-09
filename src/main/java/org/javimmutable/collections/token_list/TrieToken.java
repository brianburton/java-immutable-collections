///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

package org.javimmutable.collections.token_list;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
class TrieToken
    implements JImmutableTokenList.Token
{
    private static final TrieToken[] CACHE;
    static final TrieToken ZERO;

    private final byte[] values;

    static {
        CACHE = new TrieToken[65];
        for (int i = 0; i < 64; ++i) {
            CACHE[i] = token(i);
        }
        CACHE[64] = token(1, 0);
        ZERO = CACHE[0];
    }

    TrieToken(byte[] values)
    {
        assert values.length > 0;
        this.values = values;
    }

    @Nonnull
    static TrieToken token(int... values)
    {
        final byte[] tokenValues = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            tokenValues[i] = (byte)values[values.length - i - 1];
        }
        return new TrieToken(tokenValues);
    }

    static boolean sameBaseAt(@Nonnull TrieToken a,
                              @Nonnull TrieToken b,
                              int shift)
    {
        return basesMatch(a, b, Math.max(a.maxShift(), b.maxShift()), shift + 1);
    }

    static boolean equivalentTo(@Nonnull TrieToken a,
                                @Nonnull TrieToken b)
    {
        return basesMatch(a, b, Math.max(a.maxShift(), b.maxShift()), 0);
    }

    static int maxCommonShift(@Nonnull TrieToken a,
                              @Nonnull TrieToken b)
    {
        int shift = Math.max(a.maxShift(), b.maxShift());
        while (shift > 0) {
            if (a.indexAt(shift) != b.indexAt(shift)) {
                return shift;
            }
            shift -= 1;
        }
        return 0;
    }

    static int commonAncestorShift(@Nonnull TrieToken a,
                                   @Nonnull TrieToken b)
    {
        int shift = Math.max(a.trieDepth(), b.trieDepth());
        final int maxShift = Math.max(maxCommonShift(a, b), shift);
        while (shift < maxShift && !basesMatch(a, b, maxShift, shift)) {
            shift += 1;
        }
        assert shift <= maxShift;
        return shift;
    }

    @Nonnull
    TrieToken base(int shift)
    {
        final int myLength = values.length;
        final int resultLength = Math.max(shift + 1, myLength);
        final byte[] newValues = new byte[resultLength];
        final int copyLength = myLength - shift - 1;
        if (copyLength > 0) {
            System.arraycopy(values, shift + 1, newValues, shift + 1, copyLength);
        }
        return new TrieToken(newValues);
    }

    @Nonnull
    TrieToken next()
    {
        final int length = values.length;
        if (length == 1) {
            return CACHE[values[0] + 1];
        }
        byte[] newValues = values.clone();
        for (int i = 0, last = length - 1; i <= last; ++i) {
            final byte value = newValues[i];
            assert value >= 0;
            assert value < 64;
            if (value < 63) {
                newValues[i] = (byte)(value + 1);
                break;
            }
            newValues[i] = 0;
            if (i == last) {
                byte[] extendedValues = new byte[last + 2];
                extendedValues[last + 1] = 1;
                System.arraycopy(newValues, 0, extendedValues, 0, length);
                newValues = extendedValues;
            }
        }
        return new TrieToken(newValues);
    }

    int indexAt(int shift)
    {
        return shift >= values.length ? 0 : (int)values[shift];
    }

    int maxShift()
    {
        return values.length - 1;
    }

    int trieDepth()
    {
        for (int i = 0, limit = values.length; i < limit; ++i) {
            if (values[i] != 0) {
                return i;
            }
        }
        return 0;
    }

    @Nonnull
    TrieToken withIndexAt(int shift,
                          int index)
    {
        assert index >= 0;
        assert index < 64;
        assert shift < values.length;
        byte[] newValues = values.clone();
        newValues[shift] = (byte)index;
        return new TrieToken(newValues);
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj == this) || ((obj instanceof TrieToken) && equivalentTo(this, (TrieToken)obj));
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for (int i = values.length - 1; i >= 0; --i) {
            result = result * 31 + (int)values[i];
        }
        return result;
    }

    @Nonnull
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = values.length - 1; i >= 0; --i) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    private static boolean basesMatch(@Nonnull TrieToken a,
                                      @Nonnull TrieToken b,
                                      int maxShift,
                                      int shift)
    {
        while (maxShift >= shift) {
            if (a.indexAt(maxShift) != b.indexAt(maxShift)) {
                return false;
            }
            maxShift -= 1;
        }
        return true;
    }
}
