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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.AbstractJImmutableMultiset;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Immutable
public class JImmutableTreeMultiset<T>
        extends AbstractJImmutableMultiset<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeMultiset EMPTY = new JImmutableTreeMultiset(new ComparableComparator());

    private final Comparator<T> comparator;

    private JImmutableTreeMultiset(Comparator<T> comparator)
    {
        this(JImmutableTreeMap.<T, Integer>of(comparator), 0, comparator);
    }

    private JImmutableTreeMultiset(JImmutableMap<T, Integer> map,
                                   int occurrences,
                                   Comparator<T> comparator)
    {
        super(map, occurrences);
        this.comparator = comparator;
    }

    @Nonnull
    @Override
    public JImmutableTreeMultiset<T> deleteAll()
    {
        return of(comparator);
    }

    public Comparator<T> getComparator()
    {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> JImmutableTreeMultiset<T> of()
    {
        return (JImmutableTreeMultiset<T>)EMPTY;
    }

    public static <T> JImmutableTreeMultiset<T> of(Comparator<T> comparator)
    {
        return new JImmutableTreeMultiset<T>(comparator);
    }

    @Override
    protected JImmutableTreeMultiset<T> create(JImmutableMap<T, Integer> map,
                                               int occurrences)
    {
        return new JImmutableTreeMultiset<T>(map, occurrences, comparator);
    }

    @Override
    protected Map<T, Integer> emptyMutableMap()
    {
        return new TreeMap<T, Integer>(comparator);
    }
}