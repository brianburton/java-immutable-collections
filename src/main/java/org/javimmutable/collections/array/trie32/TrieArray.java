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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.AbstractJImmutableArray;
import org.javimmutable.collections.common.MutableDelta;

public class TrieArray<T>
        extends AbstractJImmutableArray<T>
{
    @SuppressWarnings("unchecked")
    private static final TrieArray EMPTY = new TrieArray(TrieNode.of(), 0);

    private final TrieNode<T> root;
    private final int size;

    private TrieArray(TrieNode<T> root,
                      int size)
    {
        this.root = root;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> TrieArray<T> of()
    {
        return (TrieArray<T>)EMPTY;
    }

    public static <T> JImmutableArray<T> of(Indexed<T> source,
                                            int offset,
                                            int limit)
    {
        final int size = limit - offset;
        if (size == 0) {
            return of();
        }

        // small lists can be directly constructed from a single leaf array
        if (size <= 32) {
            return new TrieArray<T>(TrieNode.<T>fromSource(0, source, offset, limit), size);
        }

        // first construct an array containing a single level of arrays of leaves
        final int numBranches = Math.min(32, (limit - offset + 31) / 32);
        @SuppressWarnings("unchecked") final TrieNode<T>[] branchArray = (TrieNode<T>[])new TrieNode[numBranches];
        int index = 0;
        for (int b = 0; b < numBranches; ++b) {
            int branchSize = Math.min(32, limit - offset);
            branchArray[b] = TrieNode.fromSource(index, source, offset, limit);
            offset += branchSize;
            index += branchSize;
        }

        // then add any extras left over above that size
        JImmutableArray<T> array = new TrieArray<T>(MultiBranchTrieNode.forEntries(5, branchArray), index);
        while (offset < limit) {
            array = array.assign(index++, source.get(offset++));
        }
        return array;
    }

    @Override
    public T getValueOr(int index,
                        T defaultValue)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return defaultValue;
        } else {
            return root.getValueOr(root.getShift(), index, defaultValue);
        }
    }

    @Override
    public Holder<T> find(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return Holders.of();
        } else {
            return root.find(root.getShift(), index);
        }
    }

    @Override
    public JImmutableArray<T> assign(int index,
                                     T value)
    {
        MutableDelta sizeDelta = new MutableDelta();
        TrieNode<T> newRoot = root.paddedToMinimumDepthForShift(TrieNode.shiftForIndex(index));
        newRoot = newRoot.assign(newRoot.getShift(), index, value, sizeDelta);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
    }

    @Override
    public JImmutableArray<T> delete(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return this;
        } else {
            MutableDelta sizeDelta = new MutableDelta();
            final TrieNode<T> newRoot = root.delete(root.getShift(), index, sizeDelta).trimmedToMinimumDepth();
            return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return root.signedOrderEntryCursor();
    }
}
