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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.cursors.LazyCursor;

public final class TrieArray<T>
        implements Cursorable<T>
{
    @SuppressWarnings("unchecked")
    private static final TrieArray EMPTY = new TrieArray(EmptyTrieNode.of());

    private final TrieNode<T> root;

    private TrieArray(TrieNode<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    public static <T> TrieArray<T> of()
    {
        return (TrieArray<T>)EMPTY;
    }

    public Holder<T> get(int index)
    {
        return root.get(index >>> 5, index & 0x1f);
    }

    public TrieArray<T> assign(int index,
                               T value)
    {
        TrieNode<T> newRoot = root.assign(index >>> 5, index & 0x1f, value);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot);
    }

    public TrieArray<T> delete(int index)
    {
        TrieNode<T> newRoot = root.delete(index >>> 5, index & 0x1f);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot);
    }

    @Override
    public Cursor<T> cursor()
    {
        return LazyCursor.of(root);
    }

    public PersistentMap<Class, Integer> getNodeTypeCounts(PersistentMap<Class, Integer> map)
    {
        return root.getNodeTypeCounts(map);
    }
}
