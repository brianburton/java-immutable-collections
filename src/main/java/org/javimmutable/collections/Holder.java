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
import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Instances are immutable containers for at most a single object.  A Holder is either empty or filled
 * and always remain in the same state once created, i.e. value returned by isEmpty() and isFilled()
 * and getValue() must not change over time for a single instance.  null is a legitimate value for a
 * Holder and a filled Holder could return null from getValue().
 */
@Immutable
public interface Holder<T>
{
    /**
     * @return true iff this Holder has no value to return
     */
    boolean isEmpty();

    /**
     * @return true iff this Holder has a value to return
     */
    boolean isNonEmpty();

    /**
     * @return true iff this Holder has a value to return
     */
    boolean isFilled();

    /**
     * Retrieve the value of a filled Holder.  Must throw if Holder is empty.
     *
     * @return the (possibly null) value
     * @throws UnsupportedOperationException if Holder is empty
     */
    T getValue();

    /**
     * Retrieve the value of a filled Holder or null if Holder is empty.
     *
     * @return null (empty) or value (filled)
     */
    T getValueOrNull();

    /**
     * Retrieve the value of a filled Holder or the defaultValue if Holder is empty
     *
     * @param defaultValue value to return if Holder is empty
     * @return value or defaultValue
     */
    T getValueOr(T defaultValue);

    /**
     * Call consumer with my value if I am filled.  Otherwise do nothing.
     */
    default void ifPresent(@Nonnull Consumer<? super T> consumer)
    {
        if (isFilled()) {
            consumer.accept(getValue());
        }
    }

    /**
     * Call consumer with my value if I am filled.  Otherwise do nothing.
     */
    default <E extends Exception> void ifPresentThrows(@Nonnull Proc1Throws<? super T, E> consumer)
        throws E
    {
        if (isFilled()) {
            consumer.apply(getValue());
        }
    }

    /**
     * Apply the transform function to my value (if I am filled) and return a new Holder containing the result.
     * If I am empty return an empty Holder.
     */
    default <U> Holder<U> map(@Nonnull Function<? super T, ? extends U> transforminator)
    {
        return isFilled() ? Holders.of(transforminator.apply(getValue())) : Holders.of();
    }

    /**
     * Return my value if I am filled.  Otherwise return defaultValue.
     */
    default T orElse(T defaultValue)
    {
        return getValueOr(defaultValue);
    }

    /**
     * Return my value if I am filled.  Otherwise call supplier and return its result.
     */
    default T orElseGet(@Nonnull Supplier<? extends T> supplier)
    {
        return isFilled() ? getValue() : supplier.get();
    }

    /**
     * Return my value if I am filled.  Otherwise call supplier and throw its result.
     */
    default <X extends Throwable> T orElseThrow(@Nonnull Supplier<? extends X> supplier)
        throws X
    {
        if (isFilled()) {
            return getValue();
        } else {
            throw supplier.get();
        }
    }
}
