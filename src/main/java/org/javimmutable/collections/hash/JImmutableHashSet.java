///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.AbstractJImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class JImmutableHashSet<T>
        extends AbstractJImmutableSet<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableHashSet EMPTY = new JImmutableHashSet(JImmutableHashMap.of());

    private JImmutableHashSet(JImmutableMap<T, Boolean> map)
    {
        super(map);
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableHashSet<T> of()
    {
        return (JImmutableHashSet<T>)EMPTY;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll()
    {
        return of();
    }

    @Override
    public void checkInvariants()
    {
        checkSetInvariants();
        //TODO: fix generalized checkInvariants()
    }

    @Override
    protected JImmutableSet<T> create(JImmutableMap<T, Boolean> map)
    {
        return new JImmutableHashSet<T>(map);
    }

    @Override
    protected JImmutableMap<T, Boolean> emptyMap()
    {
        return JImmutableHashMap.of();
    }
}
