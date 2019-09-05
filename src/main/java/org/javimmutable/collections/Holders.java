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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides static utility methods for constructing Holder instances.
 */
public class Holders<V>
{
    private static final Empty EMPTY = new Empty();

    /**
     * Creates an empty Holder for the specified type.
     * Shares a single instance for all empty Holders to save memory.
     *
     * @return the Holder
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <V> Holder<V> of()
    {
        return EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <V> Holder<V> holder()
    {
        return EMPTY;
    }

    public static <V> Holder<V> holder(@Nullable V value)
    {
        return of(value);
    }

    /**
     * Creates a filled Holder for the specified type and (possibly null) value.
     */
    @Nonnull
    public static <V> Holder<V> of(@Nullable V value)
    {
        return new Filled<V>()
        {
            @Override
            public V getValue()
            {
                return value;
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

            @Override
            public String toString()
            {
                return String.format("[%s]", value);
            }
        };
    }

    /**
     * Creates an empty Holder if value is null or a filled Holder if value is non-null.
     *
     * @return the Holder
     */
    @Nonnull
    public static <V> Holder<V> fromNullable(@Nullable V value)
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

    private static class Empty<V>
        implements Holder<V>
    {
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
        public void ifPresent(@Nonnull Consumer<? super V> consumer)
        {
        }

        @Override
        public <U> Holder<U> map(@Nonnull Function<? super V, ? extends U> transforminator)
        {
            return of();
        }

        @Override
        public V orElse(V defaultValue)
        {
            return defaultValue;
        }

        @Override
        public V orElseGet(@Nonnull Supplier<? extends V> supplier)
        {
            return supplier.get();
        }

        @Override
        public <X extends Throwable> V orElseThrow(@Nonnull Supplier<? extends X> supplier)
            throws X
        {
            throw supplier.get();
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

        @Override
        public String toString()
        {
            return "[]";
        }
    }

    public interface Filled<V>
        extends Holder<V>
    {
        default boolean isEmpty()
        {
            return false;
        }

        default boolean isFilled()
        {
            return true;
        }

        @Override
        default V getValueOrNull()
        {
            return getValue();
        }

        @Override
        default V getValueOr(V defaultValue)
        {
            return getValue();
        }

        @Override
        default void ifPresent(@Nonnull Consumer<? super V> consumer)
        {
            consumer.accept(getValue());
        }

        @Override
        default <U> Holder<U> map(@Nonnull Function<? super V, ? extends U> transforminator)
        {
            return of(transforminator.apply(getValue()));
        }

        @Override
        default V orElse(V defaultValue)
        {
            return getValue();
        }

        @Override
        default V orElseGet(@Nonnull Supplier<? extends V> supplier)
        {
            return getValue();
        }

        @Override
        default <X extends Throwable> V orElseThrow(@Nonnull Supplier<? extends X> supplier)
            throws X
        {
            return getValue();
        }
    }

    public static <T> boolean areEqual(Holder<T> a,
                                       Holder<T> b)
    {
        if ((a == null) || (b == null)) {
            return (a == null) && (b == null);
        } else if (a.isEmpty()) {
            return b.isEmpty();
        } else if (b.isEmpty()) {
            return false;
        } else {
            T v1 = a.getValue();
            T v2 = b.getValue();
            if ((v1 == null) || (v2 == null)) {
                return (v1 == null) && (v2 == null);
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
