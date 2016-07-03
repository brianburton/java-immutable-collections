///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.common.AbstractJImmutableMultiset;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;

@Immutable
public class JImmutableHashMultiset<T>
        extends AbstractJImmutableMultiset<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableHashMultiset EMPTY = new JImmutableHashMultiset(JImmutableHashMap.of(), 0);

    private JImmutableHashMultiset(JImmutableMap<T, Integer> map,
                                   int occurrences)
    {
        super(map, occurrences);
    }

    @Override
    protected JImmutableMultiset<T> create(JImmutableMap<T, Integer> map,
                                           int occurrences)
    {
        return new JImmutableHashMultiset<T>(map, occurrences);
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableHashMultiset<T> of()
    {
        return (JImmutableHashMultiset<T>)EMPTY;
    }

    @Nonnull
    @Override
    public JImmutableMultiset<T> deleteAll()
    {
        return of();
    }

    @Override
    protected JImmutableMap<T, Integer> emptyMap()
    {
        return JImmutableHashMap.of();
    }

    @Override
    protected Counter<T> emptyCounter()
    {
        return new Counter<T>(new HashMap<T, Integer>());
    }
}