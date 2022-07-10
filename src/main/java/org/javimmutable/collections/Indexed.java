///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import java.util.function.Function;
import javax.annotation.Nonnull;

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
     * Return a {@link Holder} containing the value associated wth the index or an empty
     * {@link Holder} if no value is associated with the index.
     *
     * @param index identifies the value to retrieve
     * @return possibly empty {@link Holder} containing any value associated with the index
     */
    @Nonnull
    default Holder<T> find(int index)
    {
        return (index >= 0 && index < size()) ? Holders.of(get(index)) : Holders.of();
    }

    /**
     * Return a {@link Option} containing the non-null value associated wth the index or an empty
     * {@link Option} if no non-null value is associated with the index.
     *
     * @param index identifies the value to retrieve
     * @return possibly empty {@link Option} containing any non-null value associated with the index
     */
    @Nonnull
    default Option<T> seek(int index)
    {
        return find(index).toOption();
    }

    /**
     * Retrieve the number of values available in the container.
     */
    int size();

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

            @Override
            public int size()
            {
                return source.size();
            }
        };
    }
}
