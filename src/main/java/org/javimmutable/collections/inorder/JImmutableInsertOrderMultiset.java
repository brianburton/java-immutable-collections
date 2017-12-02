///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.AbstractJImmutableMultiset;
import org.javimmutable.collections.serialization.JImmutableInsertOrderMultisetProxy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collector;

/**
 * JImmutableMultisetImplementation built on top of a JImmutableInsertOrderMap. During iteration,
 * elements are returned in the same order they were inserted into the set. Performance is slower
 * than hash or tree sets, but should be sufficient for most algorithms where insert order matters.
 */
@Immutable
public class JImmutableInsertOrderMultiset<T>
    extends AbstractJImmutableMultiset<T>
    implements Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableInsertOrderMultiset EMPTY = new JImmutableInsertOrderMultiset(JImmutableInsertOrderMap.of(), 0);
    private static final long serialVersionUID = -121805;

    private JImmutableInsertOrderMultiset(JImmutableMap<T, Integer> map,
                                          int occurrences)
    {
        super(map, occurrences);
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableInsertOrderMultiset<T> of()
    {
        return (JImmutableInsertOrderMultiset<T>)EMPTY;
    }

    @Nonnull
    @Override
    public JImmutableInsertOrderMultiset<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public Collector<T, ?, JImmutableSet<T>> setCollector()
    {
        return GenericCollector.ordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Nonnull
    @Override
    public Collector<T, ?, JImmutableMultiset<T>> multisetCollector()
    {
        return GenericCollector.ordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    @Override
    protected JImmutableInsertOrderMultiset<T> create(JImmutableMap<T, Integer> map,
                                                      int occurrences)
    {
        return new JImmutableInsertOrderMultiset<>(map, occurrences);
    }

    @Override
    protected Map<T, Integer> emptyMutableMap()
    {
        return new LinkedHashMap<>();
    }

    private Object writeReplace()
    {
        return new JImmutableInsertOrderMultisetProxy(this);
    }
}