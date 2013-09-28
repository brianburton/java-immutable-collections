///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.cursors.StandardCursor;

public class StandardBit32Array<T>
        extends Bit32Array<T>
{
    @SuppressWarnings("unchecked")
    private static final StandardBit32Array EMPTY = new StandardBit32Array(0, new Holder[0]);

    private final int bitmask;
    private final Holder<T>[] entries;

    private StandardBit32Array(int bitmask,
                               Holder<T>[] entries)
    {
        this.bitmask = bitmask;
        this.entries = entries;
    }

    @SuppressWarnings("unchecked")
    public static <T> StandardBit32Array<T> of()
    {
        return (StandardBit32Array<T>)EMPTY;
    }

    public Holder<T> get(int index)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            return entries[realIndex(bitmask, bit)];
        }
    }

    public Bit32Array<T> set(int index,
                             T value)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        final int arrayIndex = realIndex(bitmask, bit);
        final Holder<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            final Holder<T>[] newEntries = copyToLargerArray(entries, arrayIndex);
            newEntries[arrayIndex] = Holders.of(value);
            return new StandardBit32Array<T>(bitmask | bit, newEntries);
        } else if (entries[arrayIndex].getValueOrNull() == value) {
            return this;
        } else {
            final Holder<T>[] newEntries = entries.clone();
            newEntries[arrayIndex] = Holders.of(value);
            return new StandardBit32Array<T>(bitmask, newEntries);
        }
    }

    public Bit32Array<T> delete(int index)
    {
        checkIndex(index);
        final int bit = 1 << index;
        final int bitmask = this.bitmask;
        final Holder<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            switch (entries.length) {
            case 1:
                return Bit32Array.of();
            case 2: {
                final int newBitmask = bitmask & ~bit;
                final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                return new SingleBit32Array<T>(remainingIndex, entries[realIndex(bitmask, 1 << remainingIndex)].getValue());
            }
            default:
                return new StandardBit32Array<T>(bitmask & ~bit, copyToSmallerArray(entries, realIndex(bitmask, bit)));
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
    public Cursor<PersistentMap.Entry<Integer, T>> cursor()
    {
        return StandardCursor.of(new CursorSource(bitmask));
    }

    private class CursorSource
            implements StandardCursor.Source<PersistentMap.Entry<Integer, T>>
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
        public PersistentMap.Entry<Integer, T> currentValue()
        {
            return MapEntry.of(index, get(index).getValue());
        }

        @Override
        public StandardCursor.Source<PersistentMap.Entry<Integer, T>> advance()
        {
            if (remainingMask == 0) {
                return this;
            } else {
                final int bit = 1 << index;
                return new CursorSource(remainingMask & ~bit);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Holder<T>[] copyToSmallerArray(Holder<T>[] oldArray,
                                                      int skipIndex)
    {
        final int newLength = oldArray.length - 1;
        Holder<T>[] newArray = (Holder<T>[])new Holder[newLength];
        System.arraycopy(oldArray, 0, newArray, 0, skipIndex);
        System.arraycopy(oldArray, skipIndex + 1, newArray, skipIndex, newLength - skipIndex);
        return newArray;
    }

    @SuppressWarnings("unchecked")
    private static <T> Holder<T>[] copyToLargerArray(Holder<T>[] oldArray,
                                                     int insertIndex)
    {
        final int oldLength = oldArray.length;
        Holder<T>[] newArray = (Holder<T>[])new Holder[oldLength + 1];
        if (oldLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, insertIndex);
            System.arraycopy(oldArray, insertIndex, newArray, insertIndex + 1, oldLength - insertIndex);
        }
        return newArray;
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }
}
