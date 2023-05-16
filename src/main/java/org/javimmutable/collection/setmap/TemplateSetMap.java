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

package org.javimmutable.collection.setmap;

import org.javimmutable.collection.IMap;
import org.javimmutable.collection.ISet;
import org.javimmutable.collection.ISetMap;
import org.javimmutable.collection.serialization.TemplateSetMapProxy;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * JImmutableSetMap implementation that uses arbitrary Map and Set templates.
 * Allows mix and match of map and set types to support all possible combinations.
 */
public class TemplateSetMap<K, V>
    extends AbstractSetMap<K, V>
    implements Serializable
{
    private static final long serialVersionUID = -121805;

    private final IMap<K, ISet<V>> emptyMap;

    private TemplateSetMap(IMap<K, ISet<V>> contents,
                           IMap<K, ISet<V>> emptyMap,
                           ISet<V> emptySet)
    {
        super(contents, emptySet);
        this.emptyMap = emptyMap;
    }

    private TemplateSetMap(IMap<K, ISet<V>> emptyMap,
                           ISet<V> emptySet)
    {
        this(emptyMap, emptyMap, emptySet);
    }

    /**
     * Creates a new empty JImmutableSetMap object using the specified template map and set implementations.
     * The provided templates are always emptied before use.
     */
    public static <K, V> ISetMap<K, V> of(@Nonnull IMap<K, ISet<V>> emptyMap,
                                          @Nonnull ISet<V> emptySet)
    {
        return new TemplateSetMap<>(emptyMap.deleteAll(), emptySet.deleteAll());
    }

    @Override
    public void checkInvariants()
    {
    }

    public IMap<K, ISet<V>> getEmptyMap()
    {
        return emptyMap;
    }

    public ISet<V> getEmptySet()
    {
        return emptySet;
    }

    @Override
    protected ISetMap<K, V> create(IMap<K, ISet<V>> map)
    {
        return new TemplateSetMap<>(map, emptyMap, emptySet);
    }

    private Object writeReplace()
    {
        return new TemplateSetMapProxy(this);
    }
}
