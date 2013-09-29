///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections;

/**
 * Provides static utility methods for constructing Holder instances.
 *
 * @param <V>
 */
public abstract class Holders<V>
        implements Holder<V>
{
    private static final Empty EMPTY = new Empty();

    /**
     * Creates an empty Holder for the specified type.
     * Shares a single instance for all empty Holders to save memory.
     *
     * @param <V>
     * @return the Holder
     */
    @SuppressWarnings("unchecked")
    public static <V> Holders<V> of()
    {
        return (Holders<V>)EMPTY;
    }

    /**
     * Creates a filled Holder for the specified type and (possibly null) value.
     *
     * @param value
     * @param <V>
     * @return the Holder
     */
    public static <V> Holders<V> of(V value)
    {
        return new Filled<V>(value);
    }

    /**
     * Creates an empty Holder if value is null or a filled Holder if value is non-null.
     *
     * @param value
     * @param <V>
     * @return the Holder
     */
    public static <V> Holders<V> fromNullable(V value)
    {
        if (value == null) {
            return of();
        } else {
            return of(value);
        }
    }

    private Holders()
    {
        // prevent unrelated derived classes
    }

    @Override
    public int hashCode()
    {
        return Holders.hashCode(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o)
    {
        return (o instanceof Holder) && Holders.areEqual(this, (Holder<V>)o);
    }

    private static class Empty<V>
            extends Holders<V>
    {
        private Empty()
        {
        }

        public boolean isEmpty()
        {
            return true;
        }

        public boolean isFilled()
        {
            return false;
        }

        public V getValue()
        {
            throw new UnsupportedOperationException("cannot get empty value");
        }

        @Override
        public V getValueOrNull()
        {
            return null;
        }

        @Override
        public V getValueOr(V defaultValue)
        {
            return defaultValue;
        }

        @Override
        public String toString()
        {
            return "[]";
        }
    }

    private static class Filled<V>
            extends Holders<V>
    {
        private final V value;

        private Filled(V value)
        {
            this.value = value;
        }

        public boolean isEmpty()
        {
            return false;
        }

        public boolean isFilled()
        {
            return true;
        }

        public V getValue()
        {
            return value;
        }

        @Override
        public V getValueOrNull()
        {
            return value;
        }

        @Override
        public V getValueOr(V defaultValue)
        {
            return value;
        }

        @Override
        public String toString()
        {
            return String.format("[%s]", value);
        }
    }

    public static <T> boolean areEqual(Holder<T> a,
                                       Holder<T> b)
    {
        if (a == null || b == null) {
            return a == null && b == null;
        } else if (a.isEmpty()) {
            return b.isEmpty();
        } else if (b.isEmpty()) {
            return false;
        } else {
            T v1 = a.getValue();
            T v2 = b.getValue();
            if (v1 == null || v2 == null) {
                return v1 == null && v2 == null;
            } else {
                return v1.equals(v2);
            }
        }
    }

    public static <T> int hashCode(Holder<T> a)
    {
        if (a.isEmpty()) {
            return -1;
        }
        if (a.getValue() == null) {
            return 1;
        }
        return a.getValue().hashCode();
    }
}
