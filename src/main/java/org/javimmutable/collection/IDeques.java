///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection;

import org.javimmutable.collection.deque.ArrayDeque;
import org.javimmutable.collection.indexed.IndexedArray;

import javax.annotation.Nonnull;
import java.util.Iterator;

public final class IDeques
{
    private IDeques()
    {
    }

    /**
     * Produces an empty JImmutableList built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> of()
    {
        return ArrayDeque.of();
    }

    /**
     * Efficiently produces a JImmutableList containing all of the specified values built atop a balanced binary tree.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IDeque<T> of(T... values)
    {
        return IBuilders.<T>deque().addAll(IndexedArray.retained(values)).build();
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull Iterator<? extends T> source)
    {
        return IBuilders.<T>deque().addAll(source).build();
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return IBuilders.<T>deque().addAll(source).build();
    }
}
