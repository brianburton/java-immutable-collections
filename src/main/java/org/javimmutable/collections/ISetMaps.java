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

import java.util.Comparator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.setmap.HashSetMap;
import org.javimmutable.collections.setmap.OrderedSetMap;
import org.javimmutable.collections.setmap.SetMapFactory;
import org.javimmutable.collections.setmap.TemplateSetMap;
import org.javimmutable.collections.setmap.TreeSetMap;

public final class ISetMaps
{
    private ISetMaps()
    {
    }

    /**
     * Creates a set map with higher performance but no specific ordering of keys.
     * Sets for each key are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> hashed()
    {
        return HashSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by order they are inserted.
     * Sets for each value are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> ordered()
    {
        return OrderedSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by their natural ordering.
     * Sets for each key are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K extends Comparable<K>, V> ISetMap<K, V> sorted()
    {
        return TreeSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     * Sets for each value are equivalent to one created by JImmutables.set().
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> sorted(@Nonnull Comparator<K> comparator)
    {
        return TreeSetMap.of(comparator);
    }

    /**
     * Creates a set map using the provided templates for map and set.  The templates do not have to be
     * empty.  The set map will always use empty versions of them internally.  This factory method
     * provided complete flexibility in the choice of map and set types by caller.
     *
     * @param templateMap instance of the type of map to use
     * @param templateSet instance of the type of set to use
     */
    @Nonnull
    public static <K, V> ISetMap<K, V> templated(@Nonnull IMap<K, ISet<V>> templateMap,
                                                 @Nonnull ISet<V> templateSet)
    {
        return TemplateSetMap.of(templateMap, templateSet);
    }

    /**
     * Creates a builder to build a custom JImmutableSetMap configuration from a
     * base map and set type.
     */
    @Nonnull
    public static <K, V> SetMapFactory<K, V> factory()
    {
        return new SetMapFactory<>();
    }

    /**
     * Creates a builder to build a custom JImmutableSetMap configuration from a
     * base map and set type.   The provided classes are used to tell the java
     * type system what the target times are.  Sometimes this can be more
     * convenient than angle brackets.
     */
    @Nonnull
    public static <K, V> SetMapFactory<K, V> factory(@Nonnull Class<K> keyClass,
                                                     @Nonnull Class<V> valueClass)
    {
        return new SetMapFactory<>();
    }
}
