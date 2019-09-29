///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class MapBuilderTestAdapter<K, V>
    implements StandardBuilderTests.BuilderAdapter<JImmutableMap.Entry<K, V>, JImmutableMap<K, V>>
{
    private final JImmutableMap.Builder<K, V> builder;

    public MapBuilderTestAdapter(@Nonnull JImmutableMap.Builder<K, V> builder)
    {
        this.builder = builder;
    }

    @Override
    public JImmutableMap<K, V> build()
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
    public void add(JImmutableMap.Entry<K, V> value)
    {
        builder.add(value);
    }

    @Override
    public void add(Iterator<? extends JImmutableMap.Entry<K, V>> source)
    {
        builder.add(source);
    }

    @Override
    public void add(Iterable<? extends JImmutableMap.Entry<K, V>> source)
    {
        builder.add(source);
    }

    @Override
    public <T extends JImmutableMap.Entry<K, V>> void add(T... source)
    {
        JImmutableMap.Entry<K, V>[] entries = new JImmutableMap.Entry[source.length];
        System.arraycopy(source, 0, entries, 0, source.length);
        builder.add(entries);
    }

    @Override
    public void add(Indexed<? extends JImmutableMap.Entry<K, V>> source,
                    int offset,
                    int limit)
    {
        builder.add(source, offset, limit);
    }

    @Override
    public void add(Indexed<? extends JImmutableMap.Entry<K, V>> source)
    {
        builder.add(source);
    }
}
