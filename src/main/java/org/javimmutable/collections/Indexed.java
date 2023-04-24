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

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Interface for containers that allow access to values by an integer index.
 */
public interface Indexed<T>
{
    /**
     * Retrieve the value.  The index must be valid for the container's current size (i.e. [0-size)
     *
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    T get(int index);

    /**
     * Retrieve the number of values available in the container.
     */
    int size();

    /**
     * Retrieves a Holder containing the (possibly null) value at the specified index if it exists.
     * If no such value exists returns an empty Holder.
     */
    @Nonnull
    Holder<T> find(int index);

    /**
     * Retrieves a {@link Holder} containing the value at the specified index if it exists and is non-null.
     * If no such value exists or the value is null returns none().
     */
    @Nonnull
    Holder<T> seek(int index);

    @SuppressWarnings("unchecked")
    default T[] subArray(int offset,
                         int limit)
    {
        final Object[] answer = new Object[limit - offset];
        for (int i = offset; i < limit; ++i) {
            answer[i - offset] = get(i);
        }
        return (T[])answer;
    }

    static <S, T> Indexed<T> transformed(Indexed<S> source,
                                         Function<S, T> transforminator)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                return transforminator.apply(source.get(index));
            }

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                return source.find(index).map(transforminator);
            }

            @Nonnull
            @Override
            public Holder<T> seek(int index)
            {
                return source.seek(index).map(transforminator::apply);
            }

            @Override
            public int size()
            {
                return source.size();
            }
        };
    }
}
