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

package org.javimmutable.collections.stress_test;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.io.Serializable;

public abstract class KeyWrapper<T>
    implements Serializable
{
    protected final T value;

    KeyWrapper(T value)
    {
        this.value = value;
    }

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

    T getValue()
    {
        return value;
    }

    static class RegularKey<T>
        extends KeyWrapper<T>
        implements Serializable
    {
        public RegularKey(T value)
        {
            super(value);
        }

        @Override
        public int hashCode()
        {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj instanceof RegularKey) && value.equals(((RegularKey)obj).getValue());
        }
    }

    static class ComparableRegularKey<T extends Comparable<T>>
        extends RegularKey<T>
        implements Comparable<ComparableRegularKey<T>>,
                   Serializable
    {
        public ComparableRegularKey(T value)
        {
            super(value);
        }

        @Override
        public int compareTo(@Nonnull ComparableRegularKey<T> other)
        {
            return getValue().compareTo(other.getValue());
        }
    }

    static class BadHashKey<T>
        extends KeyWrapper<T>
        implements Serializable
    {
        public BadHashKey(T value)
        {
            super(value);
        }

        @Override
        public int hashCode()
        {
            return value.hashCode() & 0xff;
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj instanceof BadHashKey) && value.equals(((BadHashKey)obj).getValue());
        }
    }

    static class ComparableBadHashKey<T extends Comparable<T>>
        extends BadHashKey<T>
        implements Comparable<ComparableBadHashKey<T>>,
                   Serializable
    {
        public ComparableBadHashKey(T value)
        {
            super(value);
        }

        @Override
        public int compareTo(@Nonnull ComparableBadHashKey<T> other)
        {
            return getValue().compareTo(other.getValue());
        }
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("value", value)
            .toString();
    }
}
