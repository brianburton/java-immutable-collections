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

package org.javimmutable.collections.setmap;

import java.util.stream.Collector;
import javax.annotation.Nonnull;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.ISetMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashSet;

public class JImmutableSetMapFactory<K, V>
{
    private IMap<K, ISet<V>> map = JImmutableHashMap.of();
    private ISet<V> set = JImmutableHashSet.of();

    public ISetMap<K, V> create()
    {
        return JImmutableTemplateSetMap.of(map, set);
    }

    public Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> collector()
    {
        return create().setMapCollector();
    }

    public JImmutableSetMapFactory<K, V> withMap(@Nonnull IMap<K, ISet<V>> map)
    {
        this.map = map.deleteAll();
        return this;
    }

    public JImmutableSetMapFactory<K, V> withSet(@Nonnull ISet<V> set)
    {
        this.set = set.deleteAll();
        return this;
    }
}
