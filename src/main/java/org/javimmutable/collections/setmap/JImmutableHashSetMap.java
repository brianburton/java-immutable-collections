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

package org.javimmutable.collections.setmap;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.hash.JImmutableHashMap;

import javax.annotation.concurrent.Immutable;


/**
 * JImmutableSetMap using a hash map for fast lookup.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
public class JImmutableHashSetMap<K, V>
        extends AbstractJImmutableSetMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableHashSetMap EMPTY = new JImmutableHashSetMap(JImmutableHashMap.of());

    private JImmutableHashSetMap(JImmutableMap<K, JImmutableSet<V>> contents)
    {
        super(contents);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableHashSetMap<K, V> of()
    {
        return (JImmutableHashSetMap<K, V>)EMPTY;
    }

    @Override
    public void checkInvariants()
    {
        checkSetMapInvariants();
        //TODO: fix generalized checkInvariants()
    }

    @Override
    protected JImmutableSetMap<K, V> create(JImmutableMap<K, JImmutableSet<V>> map)
    {
        return new JImmutableHashSetMap<K, V>(map);
    }
}
