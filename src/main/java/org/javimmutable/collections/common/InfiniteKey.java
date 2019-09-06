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

import static java.lang.Integer.*;

public abstract class InfiniteKey
    implements Comparable<InfiniteKey>
{
    static final int CACHE_SIZE = 512;
    static final int FLOOR = MIN_VALUE;
    static final int LOW = FLOOR + 1;
    static final int HIGH = MAX_VALUE;

    static InfiniteKey testKey(int... values)
    {
        InfiniteKey key = values[0] == LOW ? Single.FIRST : new Single(values[0]);
        for (int i = 1; i < values.length; ++i) {
            key = new Multi(key, values[i]);
        }
        return key;
    }

    @Nonnull
    public static InfiniteKey first()
    {
        return Single.FIRST;
    }

    @Nonnull
    public abstract InfiniteKey next();

    abstract void addToString(StringBuilder sb);

    abstract int value();

    @Nonnull
    abstract InfiniteKey parent();

    @Override
    public String toString()
    {
        final StringBuilder answer = new StringBuilder();
        addToString(answer);
        return answer.toString();
    }

    @Override
    public int compareTo(@Nonnull InfiniteKey o)
    {
        if (this == o) {
            return 0;
        }
        int diff = parent().compareTo(o.parent());
        if (diff != 0) {
            return diff;
        }
        return Integer.compare(value(), o.value());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof InfiniteKey)) {
            return false;
        }
        return equalTo((InfiniteKey)obj);
    }

    private boolean equalTo(InfiniteKey o)
    {
        if (this == o) {
            return true;
        }
        if (value() != o.value()) {
            return false;
        }
        return parent().equalTo(o.parent());
    }

    private static void stringValue(StringBuilder sb,
                                    int value)
    {
        sb.append((long)value - (long)LOW);
    }

    @Immutable
    private static class Single
        extends InfiniteKey
    {
        private static final int CACHE_LIMIT;
        private static final InfiniteKey[] CACHE;
        private static final InfiniteKey STOP;
        private static final InfiniteKey FIRST;

        static {
            STOP = new Single(FLOOR);
            CACHE = new InfiniteKey[CACHE_SIZE];
            for (int i = 0; i < CACHE_SIZE; ++i) {
                CACHE[i] = new Single(LOW + i);
            }
            FIRST = CACHE[0];
            CACHE_LIMIT = LOW + CACHE_SIZE - 1;
        }

        private final int value;

        private Single(int value)
        {
            this.value = value;
        }

        @Override
        @Nonnull
        public InfiniteKey next()
        {
            if (value == HIGH) {
                return new Multi(FIRST, LOW);
            }
            final int next = value + 1;
            if (next <= CACHE_LIMIT) {
                return CACHE[next - LOW];
            }
            return new Single(next);
        }

        @Override
        public int hashCode()
        {
            return value;
        }

        @Override
        void addToString(StringBuilder sb)
        {
            stringValue(sb, value);
        }

        @Override
        int value()
        {
            return value;
        }

        @Nonnull
        @Override
        InfiniteKey parent()
        {
            return STOP;
        }
    }

    @Immutable
    private static class Multi
        extends InfiniteKey
    {
        @Nonnull
        private final InfiniteKey parent;
        private final int value;
        private final int hashCode;

        private Multi(@Nonnull InfiniteKey parent,
                      int value)
        {
            this.value = value;
            this.parent = parent;
            hashCode = value + 31 * parent.hashCode();
        }

        @Override
        @Nonnull
        public InfiniteKey next()
        {
            if (value == HIGH) {
                return new Multi(parent.next(), LOW);
            } else {
                return new Multi(parent, value + 1);
            }
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

        @Override
        void addToString(StringBuilder sb)
        {
            parent.addToString(sb);
            sb.append(".");
            stringValue(sb, value);
        }

        @Override
        int value()
        {
            return value;
        }

        @Nonnull
        @Override
        InfiniteKey parent()
        {
            return parent;
        }
    }
}
