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

import java.io.Serializable;
import javax.annotation.concurrent.Immutable;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.ISetMap;
import org.javimmutable.collections.hash.HashSet;
import org.javimmutable.collections.inorder.OrderedMap;
import org.javimmutable.collections.serialization.JImmutableInsertOrderSetMapProxy;

/**
 * JImmutableSetMap implementation that allows keys to be traversed in the same order as they
 * were inserted into the collection
 */
@Immutable
public class OrderedSetMap<K, V>
    extends AbstractSetMap<K, V>
    implements Serializable
{
    @SuppressWarnings("unchecked")
    private static final OrderedSetMap EMPTY = new OrderedSetMap(OrderedMap.of(), HashSet.of());
    private static final long serialVersionUID = -121805;

    private OrderedSetMap(IMap<K, ISet<V>> contents,
                          ISet<V> emptySet)
    {
        super(contents, emptySet);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> OrderedSetMap<K, V> of()
    {
        return EMPTY;
    }

    @Override
    public void checkInvariants()
    {
        checkSetMapInvariants();
    }

    @Override
    protected ISetMap<K, V> create(IMap<K, ISet<V>> map)
    {
        return new OrderedSetMap<K, V>(map, emptySet);
    }

    private Object writeReplace()
    {
        return new JImmutableInsertOrderSetMapProxy(this);
    }
}
