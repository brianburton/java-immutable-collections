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

package org.javimmutable.collections;

import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Holders
{
    private Holders()
    {
    }

    /**
     * Returns an empty Holder. All empty Holder share a common instance.
     */
    @Nonnull
    public static <T> Holder<T> none()
    {
        return Holder.none();
    }

    /**
     * Returns a Holder containing the (possibly null) value.
     */
    @Nonnull
    public static <T> Holder<T> nullable(@Nullable T value)
    {
        return Holder.some(value);
    }

    /**
     * Returns an empty Holder if value is null, otherwise a Holder containing
     * the value is returned.
     */
    @Nonnull
    public static <T> Holder<T> notNull(@Nullable T value)
    {
        return value != null ? Holder.some(value) : Holder.none();
    }

    /**
     * Determine if the object is an instance of the specified Class or a subclass.
     * If the object is null, returns a Holder containing null.
     * If the object is not null but not of the correct class, returns an empty Holder.
     * Otherwise returns a Holder containing the value cast to the target type.
     *
     * @param klass       class to cast the object to
     * @param valueOrNull object to be case
     * @param <T>         type of the class
     * @return a Holder
     */
    public static <T> Holder<T> cast(@Nonnull Class<T> klass,
                                     @Nullable Object valueOrNull)
    {
        if (valueOrNull == null) {
            return Holder.some(null);
        } else if (klass.isInstance(valueOrNull)) {
            return Holder.some(klass.cast(valueOrNull));
        } else {
            return Holder.none();
        }
    }

    /**
     * Returns a Holder containing the first value of the collection.  If the collection
     * is empty  an empty Holder is returned.
     */
    @Nonnull
    public static <T> Holder<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        return i.hasNext() ? nullable(i.next()) : Holder.none();
    }

    /**
     * Returns a Holder containing the first value of the collection
     * for which the predicate returns true.  If the collection
     * is empty or predicate always
     * returns false an empty Holder is returned.
     */
    @Nonnull
    public static <T> Holder<T> first(@Nonnull Iterable<? extends T> collection,
                                      @Nonnull Func1<? super T, Boolean> predicate)
    {
        for (T value : collection) {
            if (predicate.apply(value)) {
                return Holder.some(value);
            }
        }
        return Holder.none();
    }
}
