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

package org.javimmutable.collections.inorder;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.common.AbstractJImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * JImmutableSet implementation built on top of a JImmutableInsertOrderMap.  During iteration
 * elements are returned in the same order they were inserted into the set.  Performance is
 * slower than hash or tree sets but should be sufficient for most algorithms where insert
 * order matters.
 *
 * @param <T>
 */
@Immutable
public class JImmutableInsertOrderSet<T>
        extends AbstractJImmutableSet<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableInsertOrderSet EMPTY = new JImmutableInsertOrderSet(JImmutableInsertOrderMap.of());

    private JImmutableInsertOrderSet(JImmutableMap<T, Boolean> map)
    {
        super(map);
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableInsertOrderSet<T> of()
    {
        return (JImmutableInsertOrderSet<T>)EMPTY;
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
        return new JImmutableInsertOrderSet<T>(map);
    }

    @Override
    protected JImmutableMap<T, Boolean> emptyMap()
    {
        return JImmutableInsertOrderMap.of();
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Cursor<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }
        Set<T> otherSet = new HashSet<T>();
        for (values = values.start(); values.hasValue(); values = values.next()) {
            otherSet.add(values.getValue());
        }
        if (otherSet.isEmpty()) {
            return deleteAll();
        } else {
            return intersectionWithFilledMap(otherSet);
        }
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }
        Set<T> otherSet = new HashSet<T>();
        while (values.hasNext()) {
            otherSet.add(values.next());
        }
        if (otherSet.isEmpty()) {
            return deleteAll();
        } else {
            return intersectionWithFilledMap(otherSet);
        }
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull JImmutableSet<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            return intersectionWithFilledMap(other.getSet());
        }
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            return intersectionWithFilledMap(other);
        }
    }
}
