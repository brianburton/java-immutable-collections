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

import javax.annotation.Nonnull;

/**
 * Immutable data structure supporting only three operations:
 * 1. Add a value to the list and receive a token in return.
 * 2. Remove a value from the list using the token provided earlier.
 * 3. Iterate through token/value pairs in the order of insertion.
 */
public interface JImmutableTokenList<T>
{
    interface Token
    {
    }

    interface Entry<T>
    {
        @Nonnull
        Token token();

        T value();
    }

    static <T> JImmutableTokenList<T> of()
    {
        return EmptyTokenList.instance();
    }

    /**
     * Adds the specified value and return a new list containing that value.
     * The new list's lastToken() method will return the newly added token.
     */
    @Nonnull
    JImmutableTokenList<T> insertLast(T value);

    /**
     * Remove the specified token from this list.
     * If this list contains the token returns a new one that does not.
     * If this list does not contain the token returns this list unmodified.
     */
    @Nonnull
    JImmutableTokenList<T> delete(@Nonnull Token token);

    /**
     * Returns the most recently added token.
     * The token may or may not actually be present in this list.
     */
    @Nonnull
    Token lastToken();

    int size();

    @Nonnull
    IterableStreamable<Token> tokens();

    @Nonnull
    IterableStreamable<T> values();

    @Nonnull
    IterableStreamable<Entry<T>> entries();
}
