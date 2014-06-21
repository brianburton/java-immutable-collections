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
import javax.annotation.concurrent.Immutable;

@Immutable
public class StandardBit32Array<T>
        extends Bit32Array<T>
{
    private final int bitmask;
    private final T[] entries;

    private StandardBit32Array(int bitmask,
                               T[] entries)
    {
        this.bitmask = bitmask;
        this.entries = entries;
    }

    @SuppressWarnings("unchecked")
    StandardBit32Array()
    {
        this(0, (T[])new Object[0]);
    }

    /**
     * Constructor for efficiently creating a Bit32Array with consecutive indexes of up to 32 - startIndex elements
     * from an Indexed collection.  (limit - offset) must be in the range 0 to (32 - startIndex) inclusive.
     *
     * @param source
     * @param offset
     * @param limit
     */
    @SuppressWarnings("unchecked")
    StandardBit32Array(Indexed<T> source,
                       int offset,
                       int limit)
    {
        final int size = limit - offset;
        if (size < 0 || size > 32) {
            throw new IllegalArgumentException("invalid size " + size);
        } else {
            final T[] entries = (T[])new Object[size];
            for (int i = 0; i < size; ++i) {
                entries[i] = source.get(offset + i);
            }
            if (size == 32) {
                this.bitmask = -1;
            } else {
                this.bitmask = (1 << size) - 1;
            }
            this.entries = entries;
        }
    }

    /**
     * Constructor for efficiently creating a new instance with exactly two values.
     * index1 and index2 must not be equal.
     *
     * @param index1
     * @param value1
     * @param index2
     * @param value2
     */
    @SuppressWarnings("unchecked")
    StandardBit32Array(int index1,
                       T value1,
                       int index2,
                       T value2)
    {
        assert index1 != index2;
        bitmask = (1 << index1) | (1 << index2);
        entries = (T[])(index1 > index2 ? new Object[]{value2, value1} : new Object[]{value1, value2});
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    static <T> StandardBit32Array<T> fullWithout(Object[] entries,
                                                 int withoutIndex)
    {
        assert entries.length == 32;
        final T[] newEntries = (T[])new Object[31];
        System.arraycopy((T[])entries, 0, newEntries, 0, withoutIndex);
        System.arraycopy((T[])entries, withoutIndex + 1, newEntries, withoutIndex, 31 - withoutIndex);
        final int newMask = ~(1 << withoutIndex);
        return new StandardBit32Array<T>(newMask, newEntries);
    }

    @Override
    public T getValueOr(int index,
                        T defaultValue)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            return entries[realIndex(bitmask, bit)];
        }
    }

    public Holder<T> find(int index)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            return Holders.of(entries[realIndex(bitmask, bit)]);
        }
    }

    @SuppressWarnings("unchecked")
    public Bit32Array<T> assign(int index,
                                T value)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        final int arrayIndex = realIndex(bitmask, bit);
        final T[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            final int oldLength = entries.length;
            final T[] newEntries = (T[])new Object[oldLength + 1];
            if (bitmask != 0) {
                System.arraycopy(entries, 0, newEntries, 0, arrayIndex);
                System.arraycopy(entries, arrayIndex, newEntries, arrayIndex + 1, oldLength - arrayIndex);
            }
            newEntries[arrayIndex] = value;
            if (newEntries.length == 32) {
                return new FullBit32Array<T>(newEntries);
            } else {
                return new StandardBit32Array<T>(bitmask | bit, newEntries);
            }
        } else if (entries[arrayIndex] == value) {
            return this;
        } else {
            final T[] newEntries = entries.clone();
            newEntries[arrayIndex] = value;
            return new StandardBit32Array<T>(bitmask, newEntries);
        }
    }

    @SuppressWarnings("unchecked")
    public Bit32Array<T> delete(int index)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        final T[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            switch (entries.length) {
            case 1:
                return Bit32Array.of();
            case 2: {
                final int newBitmask = bitmask & ~bit;
                final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                return new SingleBit32Array<T>(remainingIndex, entries[realIndex(bitmask, 1 << remainingIndex)]);
            }
            default: {
                int skipIndex = realIndex(bitmask, bit);
                final int newLength = entries.length - 1;
                final T[] newArray = (T[])new Object[newLength];
                System.arraycopy(entries, 0, newArray, 0, skipIndex);
                System.arraycopy(entries, skipIndex + 1, newArray, skipIndex, newLength - skipIndex);
                return new StandardBit32Array<T>(bitmask & ~bit, newArray);
            }
            }
        }
    }

    public int size()
    {
        return entries.length;
    }

    @Override
    public int firstIndex()
    {
        return Integer.numberOfTrailingZeros(bitmask);
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return StandardCursor.of(new CursorSource(bitmask));
    }

    private class CursorSource
            implements StandardCursor.Source<JImmutableMap.Entry<Integer, T>>
    {
        private final int remainingMask;
        private final int index;

        private CursorSource(int remainingMask)
        {
            this.remainingMask = remainingMask;
            this.index = Integer.numberOfTrailingZeros(remainingMask);
        }

        @Override
        public boolean atEnd()
        {
            return remainingMask == 0;
        }

        @Override
        public JImmutableMap.Entry<Integer, T> currentValue()
        {
            return MapEntry.of(index, find(index).getValue());
        }

        @Override
        public StandardCursor.Source<JImmutableMap.Entry<Integer, T>> advance()
        {
            if (remainingMask == 0) {
                return this;
            } else {
                final int bit = 1 << index;
                return new CursorSource(remainingMask & ~bit);
            }
        }
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }
}
