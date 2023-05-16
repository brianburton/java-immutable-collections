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

package org.javimmutable.collection.list;

import org.javimmutable.collection.IList;
import org.javimmutable.collection.IListBuilder;
import org.javimmutable.collection.Indexed;
import org.javimmutable.collection.common.StandardBuilderTests;

import java.util.Iterator;

public class BuilderTestAdapter<T>
    implements StandardBuilderTests.BuilderAdapter<T, IList<T>>
{
    private final IListBuilder<T> builder;

    public BuilderTestAdapter(IListBuilder<T> builder)
    {
        this.builder = builder;
    }

    @Override
    public IList<T> build()
    {
        return builder.build();
    }

    @Override
    public void clear()
    {
        builder.clear();
    }

    @Override
    public int size()
    {
        return builder.size();
    }

    @Override
    public void add(T value)
    {
        builder.add(value);
    }

    @Override
    public void add(Iterator<? extends T> source)
    {
        builder.addAll(source);
    }

    @Override
    public void add(Iterable<? extends T> source)
    {
        builder.addAll(source);
    }

    @Override
    public <K extends T> void add(K... source)
    {
        builder.addAll(source);
    }

    @Override
    public void add(Indexed<? extends T> source,
                    int offset,
                    int limit)
    {
        builder.addAll(source, offset, limit);
    }

    @Override
    public void add(Indexed<? extends T> source)
    {
        builder.addAll(source);
    }
}
