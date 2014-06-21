///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.array.bit32;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.common.AbstractJImmutableArray;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * JImmutableArray implementation that only accepts indexes in the range [0, 31].
 *
 * @param <T>
 */
@Immutable
public abstract class Bit32Array<T>
        extends AbstractJImmutableArray<T>
{
    private static final int INVALID_INDEX_MASK = ~0x1f;
    private static final Bit32Array EMPTY = new EmptyBit32Array();

    @SuppressWarnings("unchecked")
    public static <T> Bit32Array<T> of()
    {
        return (Bit32Array<T>)EMPTY;
    }

    /**
     * Creates a new Bit32Array containing exactly one element.
     *
     * @param index
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Bit32Array<T> of(int index,
                                       T value)
    {
        checkIndex(index);
        return new SingleBit32Array<T>(index, value);
    }

    /**
     * Constructor for efficiently creating a Bit32Array with consecutive indexes of up to 32 elements
     * from an Indexed collection.  (limit - offset) must be in the range 0 to 32 inclusive.
     *
     * @param source
     * @param offset
     * @param limit
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Bit32Array<T> of(Indexed<T> source,
                                       int offset,
                                       int limit)
    {
        final int size = limit - offset;
        if ((size < 0) || (size > 32)) {
            throw new IllegalArgumentException(String.format("size must be 0..32 (%d)", size));
        }
        switch (size) {
        case 0:
            return of();
        case 1:
            return new SingleBit32Array<T>(0, source.get(offset));
        case 32:
            return new FullBit32Array<T>(source, offset);
        default:
            return new StandardBit32Array(source, offset, limit);
        }
    }

    @Nonnull
    @Override
    public abstract Bit32Array<T> assign(int key,
                                         @Nullable T value);

    @Nonnull
    @Override
    public abstract Bit32Array<T> delete(int key);

    public abstract int firstIndex();

    @Override
    @Nullable
    public T get(int index)
    {
        return getValueOr(index, null);
    }

    @Nonnull
    @Override
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    protected static void checkIndex(int index)
    {
        if ((index & INVALID_INDEX_MASK) != 0) {
            throw new IndexOutOfBoundsException();
        }
    }
}
