///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.array.ArrayAssignMapper;
import org.javimmutable.collections.array.TrieArrayBuilder;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.hash.set.ArraySetNode;
import org.javimmutable.collections.hash.set.ArraySingleValueSetNode;
import org.javimmutable.collections.list.ListCollisionSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class HashSetBuilder<T>
    implements JImmutableSet.Builder<T>,
               ArrayAssignMapper<T, T, ArraySetNode<T>>
{
    private final TrieArrayBuilder<ArraySetNode<T>> builder = new TrieArrayBuilder<>();
    private CollisionSet<T> collisionSet = ListCollisionSet.instance();

    @Nonnull
    @Override
    public synchronized JImmutableSet<T> build()
    {
        if (builder.size() == 0) {
            return JImmutableHashSet.of();
        } else {
            return new JImmutableHashSet<>(builder.buildRoot(), collisionSet);
        }
    }

    @Override
    public synchronized int size()
    {
        return builder.size();
    }

    @Nonnull
    @Override
    public synchronized JImmutableSet.Builder<T> add(T value)
    {
        if (builder.size() == 0) {
            collisionSet = JImmutableHashSet.selectCollisionSetForValue(value);
        }
        builder.assign(this, value, value);
        return this;
    }

    @Nonnull
    @Override
    public synchronized JImmutableSet.Builder<T> clear()
    {
        collisionSet = ListCollisionSet.instance();
        builder.reset();
        return this;
    }

    @Nonnull
    @Override
    public synchronized ArraySetNode<T> mappedAssign(@Nonnull T key,
                                                     T ignored)
    {
        assert key == ignored;
        return new ArraySingleValueSetNode<>(key);
    }

    @Nonnull
    @Override
    public synchronized ArraySetNode<T> mappedAssign(@Nonnull ArraySetNode<T> current,
                                                     @Nonnull T key,
                                                     T ignored)
    {
        assert key == ignored;
        return current.insert(collisionSet, key);
    }

    @Override
    public synchronized int mappedSize(@Nonnull ArraySetNode<T> mapping)
    {
        return mapping.size(collisionSet);
    }
}
