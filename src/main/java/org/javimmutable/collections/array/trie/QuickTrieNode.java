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

package org.javimmutable.collections.array.trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.cursors.SingleValueCursor;

public class QuickTrieNode<T>
        extends AbstractTrieNode<T>
{
    private final int branchIndex;
    private final int valueIndex;
    private final T value;

    public QuickTrieNode(int branchIndex,
                         int valueIndex,
                         T value)
    {
        this.branchIndex = branchIndex;
        this.valueIndex = valueIndex;
        this.value = value;
    }

    @Override
    public Holder<T> get(int branchIndex,
                         int valueIndex)
    {
        return (this.branchIndex == branchIndex && this.valueIndex == valueIndex) ? Holders.of(value) : Holders.<T>of();
    }

    @Override
    public TrieNode<T> assign(int branchIndex,
                              int valueIndex,
                              T value)
    {
        final int thisBranchIndex = this.branchIndex;
        final int thisValueIndex = this.valueIndex;
        if (thisBranchIndex == branchIndex && thisValueIndex == valueIndex) {
            return (this.value == value) ? this : new QuickTrieNode<T>(branchIndex, valueIndex, value);
        } else {
            return new StandardTrieNode<T>(thisBranchIndex, thisValueIndex, this.value).assign(branchIndex, valueIndex, value);
        }
    }

    @Override
    public TrieNode<T> delete(int branchIndex,
                              int valueIndex)
    {
        return (this.branchIndex == branchIndex && this.valueIndex == valueIndex) ? EmptyTrieNode.<T>of() : this;
    }

    @Override
    public Cursor<T> cursor()
    {
        return SingleValueCursor.of(value);
    }

    @Override
    public int shallowSize()
    {
        return 1;
    }

    @Override
    public int deepSize()
    {
        return 1;
    }
}
