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

package org.javimmutable.collection.hash.set;

import org.javimmutable.collection.Proc1;
import org.javimmutable.collection.Proc1Throws;
import org.javimmutable.collection.common.CollisionSet;
import org.javimmutable.collection.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ArraySetNode<T>
{
    int size(@Nonnull CollisionSet<T> collisionSet);

    boolean contains(@Nonnull CollisionSet<T> collisionSet,
                     @Nonnull T value);

    @Nonnull
    ArraySetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                           @Nonnull T value);

    @Nullable
    ArraySetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                           @Nonnull T value);

    @Nonnull
    GenericIterator.Iterable<T> values(@Nonnull CollisionSet<T> collisionSet);

    void forEach(@Nonnull CollisionSet<T> collisionSet,
                 @Nonnull Proc1<T> proc);

    <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                             @Nonnull Proc1Throws<T, E> proc)
        throws E;
}
