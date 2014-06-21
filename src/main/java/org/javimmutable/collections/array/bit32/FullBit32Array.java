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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class FullBit32Array<T>
        extends Bit32Array<T>
{
    private final T[] entries;

    FullBit32Array(T[] entries)
    {
        assert entries.length == 32;
        this.entries = entries;
    }

    @SuppressWarnings("unchecked")
    FullBit32Array(Indexed<T> source,
                   int offset)
    {
        assert source.size() - offset >= 32;
        T[] entries = (T[])new Object[32];
        for (int index = 0; index < 32; ++index) {
            entries[index] = source.get(offset++);
        }
        this.entries = entries;
    }

    @Nonnull
    @Override
    public Bit32Array<T> assign(int key,
                                @Nullable T value)
    {
        T current = entries[key];
        if (current == value) {
            return this;
        } else {
            T[] newEntries = entries.clone();
            newEntries[key] = value;
            return new FullBit32Array<T>(newEntries);
        }
    }

    @Nonnull
    @Override
    public Bit32Array<T> delete(int key)
    {
        return StandardBit32Array.fullWithout(entries, key);
    }

    @Override
    public int firstIndex()
    {
        return 0;
    }

    @Override
    @Nullable
    public T getValueOr(int index,
                        @Nullable T defaultValue)
    {
        return entries[index];
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        return Holders.of(entries[index]);
    }

    @Override
    public int size()
    {
        return 32;
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return StandardCursor.of(new CursorSource(0));
    }

    private class CursorSource
            implements StandardCursor.Source<JImmutableMap.Entry<Integer, T>>
    {
        private int index;

        private CursorSource(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index >= 32;
        }

        @Override
        public JImmutableMap.Entry<Integer, T> currentValue()
        {
            return MapEntry.of(index, entries[index]);
        }

        @Override
        public StandardCursor.Source<JImmutableMap.Entry<Integer, T>> advance()
        {
            return new CursorSource(index + 1);
        }
    }
}
