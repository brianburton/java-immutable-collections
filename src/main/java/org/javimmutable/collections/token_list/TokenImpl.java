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

class TokenImpl
    implements JImmutableTokenList.Token
{
    private static final TokenImpl[] CACHE;
    static final TokenImpl ZERO;

    private final byte[] values;

    static {
        CACHE = new TokenImpl[65];
        for (int i = 0; i < 64; ++i) {
            CACHE[i] = token(i);
        }
        CACHE[64] = token(1, 0);
        ZERO = CACHE[0];
    }

    TokenImpl(byte[] values)
    {
        assert values.length > 0;
        this.values = values;
    }

    @Nonnull
    static TokenImpl token(int... values)
    {
        final byte[] tokenValues = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            tokenValues[i] = (byte)values[i];
        }
        return new TokenImpl(tokenValues);
    }

    static boolean sameBaseAt(@Nonnull TokenImpl a,
                              @Nonnull TokenImpl b,
                              int shift)
    {
        return basesMatch(a, b, Math.max(a.maxShift(), b.maxShift()), shift + 1);
    }

    static boolean equivalentTo(@Nonnull TokenImpl a,
                                @Nonnull TokenImpl b)
    {
        return basesMatch(a, b, Math.max(a.maxShift(), b.maxShift()), 0);
    }

    private static boolean basesMatch(@Nonnull TokenImpl a,
                                      @Nonnull TokenImpl b,
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

    static int maxCommonShift(@Nonnull TokenImpl a,
                              @Nonnull TokenImpl b)
    {
        int shift = Math.max(a.maxShift(), b.maxShift());
        while (shift > 0) {
            final int index1 = a.indexAt(shift);
            final int index2 = b.indexAt(shift);
            if (index1 != index2) {
                return shift;
            }
            shift -= 1;
        }
        return 0;
    }

    static int commonAncestorShift(@Nonnull TokenImpl a,
                                   @Nonnull TokenImpl b)
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
    TokenImpl base(int shift)
    {
        final int myLength = values.length;
        final int resultLength = Math.max(shift + 1, myLength);
        final byte[] newValues = new byte[resultLength];
        final int myStartIndex = resultLength - myLength;
        final int copyLength = myLength - shift - 1;
        if (copyLength > 0) {
            System.arraycopy(values, 0, newValues, myStartIndex, copyLength);
        }
        return new TokenImpl(newValues);
    }

    @Nonnull
    TokenImpl next()
    {
        if (values.length == 1) {
            return CACHE[values[0] + 1];
        }
        byte[] newValues = values.clone();
        for (int i = newValues.length - 1; i >= 0; --i) {
            assert newValues[i] >= 0;
            assert newValues[i] < 64;
            newValues[i] += 1;
            if (newValues[i] < 64) {
                break;
            }
            newValues[i] = 0;
            if (i == 0) {
                byte[] extendedValues = new byte[newValues.length + 1];
                extendedValues[0] = 1;
                System.arraycopy(newValues, 0, extendedValues, 1, newValues.length);
                newValues = extendedValues;
            }
        }
        return new TokenImpl(newValues);
    }

    int indexAt(int shift)
    {
        return shift >= values.length ? 0 : (int)values[values.length - shift - 1];
    }

    int maxShift()
    {
        return values.length - 1;
    }

    int trieDepth()
    {
        for (int i = 0, index = values.length - 1; i < values.length; ++i, --index) {
            if (values[index] != 0) {
                return i;
            }
        }
        return 0;
    }

    @Nonnull
    TokenImpl withIndexAt(int shift,
                          int index)
    {
        assert index >= 0;
        assert index < 64;
        assert shift < values.length;
        byte[] newValues = values.clone();
        newValues[values.length - 1 - shift] = (byte)index;
        return new TokenImpl(newValues);
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj == this) || ((obj instanceof TokenImpl) && equivalentTo(this, (TokenImpl)obj));
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for (byte value : values) {
            result = result * 31 + (int)value;
        }
        return result;
    }

    @Nonnull
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (byte value : values) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(value);
        }
        return sb.toString();
    }
}
