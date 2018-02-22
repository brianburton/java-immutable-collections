///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.array;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class LeafTrieNode<T>
    extends TrieNode<T>
    implements JImmutableMap.Entry<Integer, T>,
               Holders.Filled<T>
{
    private final int index;
    private final T value;
    private final int shift;

    private LeafTrieNode(int index,
                         T value,
                         int shift)
    {
        this.index = index;
        this.value = value;
        this.shift = shift;
    }

    static <T> LeafTrieNode<T> of(int index,
                                  @Nonnull T value)
    {
        return new LeafTrieNode<>(index, value, shiftForIndex(index));
    }

    @Override
    public boolean isEmpty()
    {
        return false;
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
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        assert shift >= -5;
        return (this.index == index) ? value : defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert shift >= -5;
        return (this.index == index) ? this : Holders.of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert shift >= -5;
        if (this.index == index) {
            if (this.value == value) {
                return this;
            } else {
                return withValue(value);
            }
        } else {
            assert shift >= 0;
            return SingleBranchTrieNode.forIndex(shift, this.index, this).assign(shift, index, value, sizeDelta);
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert shift >= -5;
        if (this.index == index) {
            sizeDelta.subtract(1);
            return of();
        } else {
            return this;
        }
    }

    @Override
    public int getShift()
    {
        return shift;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public TrieNode<T> paddedToMinimumDepthForShift(int shift)
    {
        if (this.shift >= shift) {
            return this;
        } else {
            return SingleBranchTrieNode.forIndex(shift, index, this);
        }
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return SingleValueIterator.of(this);
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return SingleValueCursor.of(this);
    }

    @Override
    public void checkInvariants()
    {
        if (shift < -5 || shift > ROOT_SHIFT) {
            throw new IllegalStateException("illegal shift value: " + shift);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        LeafTrieNode that = (LeafTrieNode)o;

        if (index != that.index) {
            return false;
        }
        if (shift != that.shift) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = index;
        result = 31 * result + value.hashCode();
        result = 31 * result + shift;
        return result;
    }

    @Override
    public String toString()
    {
        return MapEntry.makeToString(this);
    }

    private TrieNode<T> withValue(T newValue)
    {
        return new LeafTrieNode<>(index, newValue, shift);
    }
}
