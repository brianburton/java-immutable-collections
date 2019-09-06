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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static java.lang.Long.toHexString;

public abstract class InfiniteKey
    implements Comparable<InfiniteKey>
{
    static final int CACHE_SIZE = 256;

    static InfiniteKey testKey(long... values)
    {
        InfiniteKey key = values[0] == 0 ? Tiny.ZERO : new Tiny(values[0]);
        for (int i = 1; i < values.length; ++i) {
            key = new Large(key, values[i]);
        }
        return key;
    }

    @Nonnull
    public static InfiniteKey first()
    {
        return Tiny.ZERO;
    }

    @Nonnull
    public abstract InfiniteKey next();

    abstract void addToString(StringBuilder sb);

    @Override
    public String toString()
    {
        final StringBuilder answer = new StringBuilder();
        addToString(answer);
        return answer.toString();
    }

    @Immutable
    private static class Tiny
        extends InfiniteKey
    {
        private static final InfiniteKey[] CACHE;
        private static final InfiniteKey ZERO;

        static {
            CACHE = new InfiniteKey[CACHE_SIZE];
            for (int i = 0; i < CACHE_SIZE; ++i) {
                CACHE[i] = new Tiny(i);
            }
            ZERO = CACHE[0];
        }

        private final long value;

        private Tiny(long value)
        {
            this.value = value;
        }

        @Override
        @Nonnull
        public InfiniteKey next()
        {
            final long next = value + 1;
            if (next < 0) {
                return new Large(ZERO, 0);
            } else if (next < CACHE_SIZE) {
                return CACHE[(int)next];
            } else {
                return new Tiny(next);
            }
        }

        @Override
        public int compareTo(@Nonnull InfiniteKey key)
        {
            if (key instanceof Tiny) {
                final Tiny o = (Tiny)key;
                return Long.compare(value, o.value);
            } else {
                assert key instanceof Large;
                return -1;
            }
        }

        @Override
        public boolean equals(Object o)
        {
            return o instanceof Tiny && (value == ((Tiny)o).value);
        }

        @Override
        public int hashCode()
        {
            return (int)(value ^ (value >>> 32));
        }

        @Override
        void addToString(StringBuilder sb)
        {
            sb.append(toHexString(value));
        }
    }

    @Immutable
    private static class Large
        extends InfiniteKey
    {
        @Nonnull
        private final InfiniteKey parent;
        private final long value;

        private Large(@Nonnull InfiniteKey parent,
                      long value)
        {
            this.value = value;
            this.parent = parent;
        }

        @Override
        @Nonnull
        public InfiniteKey next()
        {
            final long next = value + 1;
            if (next > 0) {
                return new Large(parent, next);
            } else {
                return new Large(parent.next(), 0);
            }
        }

        @Override
        public int compareTo(@Nonnull InfiniteKey key)
        {
            if (key instanceof Large) {
                final Large o = (Large)key;
                int diff = parent.compareTo(o.parent);
                if (diff != 0) {
                    return diff;
                } else {
                    return Long.compare(value, o.value);
                }
            } else {
                assert key instanceof Tiny;
                return 1;
            }
        }

        @Override
        public boolean equals(Object key)
        {
            if (key instanceof Large) {
                final Large o = (Large)key;
                return value == o.value && parent.equals(o.parent);
            } else {
                assert key instanceof Tiny;
                return false;
            }
        }

        @Override
        public int hashCode()
        {
            int result = (int)(value ^ (value >>> 32));
            result = 31 * result + parent.hashCode();
            return result;
        }

        @Override
        void addToString(StringBuilder sb)
        {
            parent.addToString(sb);
            sb.append(".");
            sb.append(toHexString(value));
        }
    }
}
