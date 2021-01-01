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

package org.javimmutable.collections.inorder;

import org.javimmutable.collections.GenericCollector;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.AbstractJImmutableSetUsingMap;
import org.javimmutable.collections.common.GenericSetBuilder;
import org.javimmutable.collections.serialization.JImmutableInsertOrderSetProxy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;

/**
 * JImmutableSet implementation built on top of a JImmutableInsertOrderMap.  During iteration
 * elements are returned in the same order they were inserted into the set.  Performance is
 * slower than hash or tree sets but should be sufficient for most algorithms where insert
 * order matters.
 */
@Immutable
public class JImmutableInsertOrderSet<T>
    extends AbstractJImmutableSetUsingMap<T>
    implements Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableInsertOrderSet EMPTY = new JImmutableInsertOrderSet(JImmutableInsertOrderMap.of());
    private static final long serialVersionUID = -121805;

    private JImmutableInsertOrderSet(JImmutableMap<T, Boolean> map)
    {
        super(map);
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableInsertOrderSet<T> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <T> JImmutableSet.Builder<T> builder()
    {
        return new GenericSetBuilder<>(JImmutableInsertOrderMap.builder(), map -> map.isEmpty() ? of() : new JImmutableInsertOrderSet<>(map));
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public Collector<T, ?, JImmutableSet<T>> setCollector()
    {
        return GenericCollector.ordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Override
    protected JImmutableSet<T> create(JImmutableMap<T, Boolean> map)
    {
        return map.isEmpty() ? of() : new JImmutableInsertOrderSet<>(map);
    }

    @Override
    protected Set<T> emptyMutableSet()
    {
        return new LinkedHashSet<>();
    }

    private Object writeReplace()
    {
        return new JImmutableInsertOrderSetProxy(this);
    }
}
