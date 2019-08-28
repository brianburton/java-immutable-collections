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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyTrieNode<T>
    extends TrieNode<T>
{
    private static final EmptyTrieNode EMPTY = new EmptyTrieNode();

    @SuppressWarnings("unchecked")
    static <T> EmptyTrieNode<T> instance()
    {
        return (EmptyTrieNode<T>)EMPTY;
    }

    @Override
    public int valueCount()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        return defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        return Holders.of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value)
    {
        return LeafTrieNode.of(index, value);
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index)
    {
        return this;
    }

    @Override
    public int getShift()
    {
        return 0;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public TrieNode<T> paddedToMinimumDepthForShift(int shift)
    {
        return this;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        return parent;
    }

    @Override
    public void checkInvariants()
    {
    }
}
