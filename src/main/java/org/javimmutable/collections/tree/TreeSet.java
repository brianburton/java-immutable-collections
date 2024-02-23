///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.IMap;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.ISetBuilder;
import org.javimmutable.collections.common.AbstractSetUsingMap;
import org.javimmutable.collections.common.GenericSetBuilder;
import org.javimmutable.collections.serialization.TreeSetProxy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;

@Immutable
public class TreeSet<T>
    extends AbstractSetUsingMap<T>
    implements Serializable
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TreeSet EMPTY = new TreeSet(new ComparableComparator());

    private final Comparator<T> comparator;

    private TreeSet(Comparator<T> comparator)
    {
        this(TreeMap.of(comparator), comparator);
    }

    private TreeSet(IMap<T, Boolean> map,
                    Comparator<T> comparator)
    {
        super(map);
        this.comparator = comparator;
    }

    @Nonnull
    @Override
    public TreeSet<T> deleteAll()
    {
        return of(comparator);
    }

    public Comparator<T> getComparator()
    {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> TreeSet<T> of()
    {
        return EMPTY;
    }

    public static <T> TreeSet<T> of(Comparator<T> comparator)
    {
        return new TreeSet<>(comparator);
    }

    public static <T extends Comparable<T>> ISetBuilder<T> builder()
    {
        return builder(ComparableComparator.<T>of());
    }

    @Nonnull
    public static <T> ISetBuilder<T> builder(Comparator<T> comparator)
    {
        return new GenericSetBuilder<>(TreeMap.builder(comparator), map -> map.isEmpty() ? of(comparator) : new TreeSet<>(map, comparator));
    }

    @Override
    protected ISet<T> create(IMap<T, Boolean> map)
    {
        return map.isEmpty() ? of(comparator) : new TreeSet<>(map, comparator);
    }

    @Override
    protected Set<T> emptyMutableSet()
    {
        return new java.util.TreeSet<>(comparator);
    }

    IMap getMap()
    {
        return map;
    }

    private Object writeReplace()
    {
        return new TreeSetProxy(this);
    }
}
