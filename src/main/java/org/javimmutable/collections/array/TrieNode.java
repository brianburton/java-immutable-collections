///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.concurrent.Immutable;

@Immutable
abstract class TrieNode<T>
    implements GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>,
               InvariantCheckable
{
    public static final int ROOT_SHIFT = 30;

    public abstract int valueCount();

    public abstract boolean isEmpty();

    public abstract T getValueOr(int shift,
                                 int index,
                                 T defaultValue);

    public abstract Holder<T> find(int shift,
                                   int index);

    public abstract TrieNode<T> assign(int shift,
                                       int index,
                                       T value);

    public abstract TrieNode<T> delete(int shift,
                                       int index);

    public abstract int getShift();

    public abstract boolean isLeaf();

    public TrieNode<T> trimmedToMinimumDepth()
    {
        return this;
    }

    public TrieNode<T> paddedToMinimumDepthForShift(int shift)
    {
        TrieNode<T> node = this;
        int nodeShift = node.getShift();
        while (nodeShift < shift) {
            nodeShift += 5;
            node = SingleBranchTrieNode.forBranchIndex(nodeShift, 0, node);
        }
        return node;
    }

    @Override
    public int iterableSize()
    {
        return valueCount();
    }

    public static int computeValueCount(TrieNode<?>[] nodes)
    {
        int answer = 0;
        for (TrieNode<?> node : nodes) {
            answer += node.valueCount();
        }
        return answer;
    }

    public static <T> TrieNode<T> of()
    {
        return EmptyTrieNode.instance();
    }

    public static int shiftForIndex(int index)
    {
        switch (Integer.numberOfLeadingZeros(index)) {
            case 0:
            case 1:
                return 30;

            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 25;

            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return 20;

            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return 15;

            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
                return 10;

            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
                return 5;

            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
                return 0;
        }
        throw new IllegalArgumentException();
    }
}
