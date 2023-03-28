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

package org.javimmutable.collections.hash;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractSet;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;

public class EmptyHashSet<T>
    extends AbstractSet<T>
    implements Serializable
{
    @SuppressWarnings("rawtypes")
    private static final EmptyHashSet INSTANCE = new EmptyHashSet();

    @SuppressWarnings("unchecked")
    public static <T> EmptyHashSet<T> instance()
    {
        return (EmptyHashSet<T>)INSTANCE;
    }

    @Override
    protected Set<T> emptyMutableSet()
    {
        return new java.util.HashSet<>();
    }

    @Nonnull
    @Override
    public ISet<T> insert(@Nonnull T value)
    {
        return new HashSet<>(value);
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return false;
    }

    @Nonnull
    @Override
    public ISet<T> delete(T value)
    {
        return this;
    }

    @Nonnull
    @Override
    public ISet<T> deleteAll(@Nonnull Iterable<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public ISet<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public ISet<T> union(@Nonnull Iterator<? extends T> values)
    {
        return new HashSetBuilder<T>().add(values).build();
    }

    @Nonnull
    @Override
    public ISet<T> intersection(@Nonnull Iterable<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public ISet<T> intersection(@Nonnull ISet<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public ISet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        return this;
    }

    @Nonnull
    @Override
    public ISet<T> intersection(@Nonnull Set<? extends T> other)
    {
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Nonnull
    @Override
    public ISet<T> deleteAll()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return EmptyIterator.of();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_UNORDERED;
    }
}
