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

package org.javimmutable.collections.token_list;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.common.StreamConstants;

import javax.annotation.Nonnull;

class TrieTokenList<T>
    implements JImmutableTokenList<T>
{
    private final TrieNode<T> root;
    private final TokenImpl lastToken;

    TrieTokenList(@Nonnull TrieNode<T> root,
                  @Nonnull TokenImpl lastToken)
    {
        this.root = root;
        this.lastToken = lastToken;
    }

    @Nonnull
    @Override
    public JImmutableTokenList<T> insertLast(T value)
    {
        final TokenImpl token = lastToken.next();
        return new TrieTokenList<>(root.assign(token, value), token);
    }

    @Nonnull
    @Override
    public JImmutableTokenList<T> delete(@Nonnull Token token)
    {
        final TrieNode<T> newRoot = root.delete((TokenImpl)token);
        if (newRoot == root) {
            return this;
        } else if (newRoot.isEmpty()) {
            return EmptyTokenList.instance();
        } else {
            return new TrieTokenList<>(newRoot, lastToken);
        }
    }

    @Nonnull
    @Override
    public Token lastToken()
    {
        return lastToken;
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Override
    @Nonnull
    public IterableStreamable<Token> tokens()
    {
        return root.tokens().streamable(StreamConstants.SPLITERATOR_ORDERED);
    }

    @Override
    @Nonnull
    public IterableStreamable<T> values()
    {
        return root.values().streamable(StreamConstants.SPLITERATOR_ORDERED);
    }

    @Nonnull
    @Override
    public IterableStreamable<Entry<T>> entries()
    {
        return root.entries().streamable(StreamConstants.SPLITERATOR_ORDERED);
    }
}
