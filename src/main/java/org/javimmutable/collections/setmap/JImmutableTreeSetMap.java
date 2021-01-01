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

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.serialization.JImmutableTreeSetMapProxy;
import org.javimmutable.collections.tree.JImmutableTreeMap;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Comparator;

/**
 * JImmutableSetMap implementation that allows keys to be traversed in sorted order using a Comparator
 * of the natural ordering of the keys if they implement Comparable.
 */
@Immutable
public class JImmutableTreeSetMap<K, V>
    extends AbstractJImmutableSetMap<K, V>
    implements Serializable
{
    @SuppressWarnings({"unchecked"})
    private static final JImmutableTreeSetMap EMPTY = new JImmutableTreeSetMap(JImmutableTreeMap.of(), JImmutableHashSet.of());
    private static final long serialVersionUID = -121805;

    private final Comparator<K> comparator;

    private JImmutableTreeSetMap(JImmutableTreeMap<K, JImmutableSet<V>> contents,
                                 JImmutableSet<V> emptySet)
    {
        this(contents, contents.getComparator(), emptySet);
    }

    private JImmutableTreeSetMap(JImmutableMap<K, JImmutableSet<V>> contents,
                                 Comparator<K> comparator,
                                 JImmutableSet<V> emptySet)
    {
        super(contents, emptySet);
        this.comparator = comparator;
    }

    /**
     * Constructs an empty set map whose keys are sorted in their natural ordering. The keys must
     * implement Comparable.
     */
    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> JImmutableTreeSetMap<K, V> of()
    {
        return EMPTY;
    }

    /**
     * Constructs an empty set map using the specified Comparator. Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared, so it is imperative that the Comparator be completely immutable.
     */
    public static <K, V> JImmutableTreeSetMap<K, V> of(Comparator<K> comparator)
    {
        return new JImmutableTreeSetMap<>(JImmutableTreeMap.of(comparator), JImmutableHashSet.of());
    }

    public Comparator<K> getComparator()
    {
        return comparator;
    }

    @Override
    public void checkInvariants()
    {
        checkSetMapInvariants();
    }

    @Override
    protected JImmutableSetMap<K, V> create(JImmutableMap<K, JImmutableSet<V>> map)
    {
        return new JImmutableTreeSetMap<>(map, comparator, emptySet);
    }

    JImmutableMap<K, JImmutableSet<V>> getMap()
    {
        return contents;
    }

    private Object writeReplace()
    {
        return new JImmutableTreeSetMapProxy(this);
    }
}
