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

package org.javimmutable.collections.listmap;

import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.concurrent.Immutable;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.list.TreeList;
import org.javimmutable.collections.serialization.JImmutableTreeListMapProxy;
import org.javimmutable.collections.tree.TreeMap;

/**
 * JImmutableListMap implementation that allows keys to be traversed in sorted order using a Comparator
 * of the natural ordering of the keys if they implement Comparable.
 */
@Immutable
public class TreeListMap<K, V>
    extends AbstractListMap<K, V>
    implements Serializable
{
    @SuppressWarnings({"unchecked"})
    private static final TreeListMap EMPTY = new TreeListMap(TreeMap.of(), TreeList.of());
    private static final long serialVersionUID = -121805;

    private final Comparator<K> comparator;

    private TreeListMap(TreeMap<K, IList<V>> contents,
                        IList<V> emptyList)
    {
        this(contents, contents.getComparator(), emptyList);
    }

    private TreeListMap(IMap<K, IList<V>> contents,
                        Comparator<K> comparator,
                        IList<V> emptyList)
    {
        super(contents, emptyList);
        this.comparator = comparator;
    }

    /**
     * Constructs an empty list map whose keys are sorted in their natural ordering.  The keys
     * must implement Comparable.
     */
    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> TreeListMap<K, V> of()
    {
        return EMPTY;
    }

    /**
     * Constructs an empty list map using the specified Comparator.  Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <K, V> TreeListMap<K, V> of(Comparator<K> comparator)
    {
        return new TreeListMap<>(TreeMap.of(comparator), TreeList.of());
    }

    public Comparator<K> getComparator()
    {
        return comparator;
    }

    @Override
    public void checkInvariants()
    {
        checkListMapInvariants();
    }

    @Override
    protected IListMap<K, V> create(IMap<K, IList<V>> map)
    {
        return (map == contents) ? this : new TreeListMap<>(map, comparator, emptyList);
    }

    IMap<K, IList<V>> getMap()
    {
        return contents;
    }

    private Object writeReplace()
    {
        return new JImmutableTreeListMapProxy(this);
    }
}
