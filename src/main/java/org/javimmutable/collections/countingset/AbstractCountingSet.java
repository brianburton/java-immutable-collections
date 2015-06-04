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

package org.javimmutable.collections.countingset;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableCountingSet;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.Conditions;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractCountingSet<T>
        implements JImmutableCountingSet<T>
{
    private final JImmutableMap<T, Integer> map;

    protected AbstractCountingSet(JImmutableMap<T, Integer> map)
    {
        this.map = map;
    }

    @Override
    public int count(T value)
    {
        Conditions.stopNull(value);
        Holder<Integer> current = map.find(value);
        return current.isFilled() ? current.getValue() : 0;
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> setCount(@Nonnull T value,
                                             int count)
    {
        Conditions.stopNull(value, count);
        return (count > 0) ? create(map.assign(value, count)) : create(map.delete(value));
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> insert(@Nonnull T value)
    {
        return insert(value, 1);
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> insert(@Nonnull T value,
                                           int count)
    {
        Conditions.stopNull(value, count);
        return create(increaseCount(value, count));
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(values.cursor());
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> insertAll(@Nonnull Collection<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableCountingSet<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        JImmutableMap<T, Integer> newMap = map;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = increaseCount(newMap, value);
            }
        }
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map
     * @return
     */
    protected abstract JImmutableCountingSet<T> create(JImmutableMap<T, Integer> map);

    private JImmutableMap<T, Integer> increaseCount(T value,
                                                    int addBy)
    {
        return map.assign(value, addBy + count(value));
    }


}
