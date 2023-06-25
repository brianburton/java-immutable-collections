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

package org.javimmutable.collections;

import org.javimmutable.collections.listmap.HashListMap;
import org.javimmutable.collections.listmap.OrderedListMap;
import org.javimmutable.collections.listmap.TreeListMap;

import javax.annotation.Nonnull;
import java.util.Comparator;

public final class IListMaps
{
    private IListMaps()
    {
    }

    /**
     * Creates a list map with higher performance but no specific ordering of keys.
     */
    @Nonnull
    public static <K, V> IListMap<K, V> listMap()
    {
        return HashListMap.of();
    }

    /**
     * Creates a list map with keys sorted by order they are inserted.
     */
    @Nonnull
    public static <K, V> IListMap<K, V> insertOrderListMap()
    {
        return OrderedListMap.of();
    }

    /**
     * Creates a list map with keys sorted by their natural ordering.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> IListMap<K, V> sortedListMap()
    {
        return TreeListMap.of();
    }

    /**
     * Creates a list map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     */
    @Nonnull
    public static <K, V> IListMap<K, V> sortedListMap(@Nonnull Comparator<K> comparator)
    {
        return TreeListMap.of(comparator);
    }
}
