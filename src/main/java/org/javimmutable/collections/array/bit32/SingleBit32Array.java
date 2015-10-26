///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.cursors.SingleValueCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SingleBit32Array<T>
        extends Bit32Array<T>
        implements JImmutableMap.Entry<Integer, T>
{
    private final int index;
    private final T value;

    SingleBit32Array(int index,
                     T value)
    {
        this.index = index;
        this.value = value;
    }

    @Override
    @Nullable
    public T getValueOr(int index,
                        @Nullable T defaultValue)
    {
        checkIndex(index);
        return (this.index == index) ? value : defaultValue;
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        checkIndex(index);
        return (this.index == index) ? Holders.of(value) : Holders.<T>of();
    }

    @Nonnull
    @Override
    public Bit32Array<T> assign(int index,
                                @Nullable T value)
    {
        checkIndex(index);
        if (this.index == index) {
            return (this.value != value) ? new SingleBit32Array<T>(index, value) : this;
        } else {
            return new StandardBit32Array<T>(this.index, this.value, index, value);
        }
    }

    @Nonnull
    @Override
    public Bit32Array<T> delete(int index)
    {
        checkIndex(index);
        return (this.index == index) ? Bit32Array.<T>of() : this;
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public int firstIndex()
    {
        return index;
    }

    @Nonnull
    @Override
    public Integer getKey()
    {
        return index;
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return SingleValueCursor.<JImmutableMap.Entry<Integer, T>>of(this);
    }

    @Override
    public void checkInvariants()
    {
        //TODO: fix empty checkInvariants()
    }
}
