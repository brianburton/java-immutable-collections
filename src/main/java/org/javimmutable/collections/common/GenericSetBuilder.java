///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapBuilder;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.ISetBuilder;

import javax.annotation.Nonnull;

public class GenericSetBuilder<T>
    implements ISetBuilder<T>
{
    private final IMapBuilder<T, Boolean> mapBuilder;
    private final Func1<IMap<T, Boolean>, ISet<T>> setFactory;

    public GenericSetBuilder(IMapBuilder<T, Boolean> mapBuilder,
                             Func1<IMap<T, Boolean>, ISet<T>> setFactory)
    {
        this.mapBuilder = mapBuilder;
        this.setFactory = setFactory;
    }

    @Nonnull
    @Override
    public ISet<T> build()
    {
        return setFactory.apply(mapBuilder.build());
    }

    @Nonnull
    @Override
    public ISetBuilder<T> clear()
    {
        mapBuilder.clear();
        return this;
    }

    @Override
    public int size()
    {
        return mapBuilder.size();
    }

    @Nonnull
    @Override
    public ISetBuilder<T> add(T value)
    {
        mapBuilder.add(value, Boolean.TRUE);
        return this;
    }
}
