///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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
    Maybe<T> find(int index);

    default <U> Indexed<U> transformed(Func1<T, U> transforminator)
    {
        return new Indexed<>()
        {
            @Override
            public U get(int index)
            {
                return transforminator.apply(Indexed.this.get(index));
            }

            @Nonnull
            @Override
            public Maybe<U> find(int index)
            {
                return Indexed.this.find(index).map(transforminator);
            }

            @Override
            public int size()
            {
                return Indexed.this.size();
            }
        };
    }

    @Nonnull
    default Indexed<T> reversed()
    {
        final int size = size();
        final int last = size - 1;
        if (size == 0) {
            return this;
        }
        return new Indexed<>()
        {
            @Override
            public T get(int index)
            {
                return Indexed.this.get(last - index);
            }

            @Override
            public int size()
            {
                return size;
            }

            @Nonnull
            @Override
            public Maybe<T> find(int index)
            {
                return Indexed.this.find(last - index);
            }
        };
    }

    @Nonnull
    default Indexed<T> prefix(int size)
    {
        if (size == size()) {
            return this;
        }
        if (size < 0 || size > size()) {
            throw new ArrayIndexOutOfBoundsException(size);
        }
        final int last = size - 1;
        return new Indexed<>()
        {
            @Override
            public T get(int index)
            {
                if (index < 0 || index > last) {
                    throw new ArrayIndexOutOfBoundsException(index);
                }
                return Indexed.this.get(index);
            }

            @Override
            public int size()
            {
                return size;
            }

            @Nonnull
            @Override
            public Maybe<T> find(int index)
            {
                if (index < 0 || index > last) {
                    return Maybe.empty();
                } else {
                    return Indexed.this.find(index);
                }
            }
        };
    }
}
