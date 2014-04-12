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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.array.trie32.TrieArray;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;

import java.util.Iterator;
import java.util.List;

public class JImmutableArrayList<T>
        implements JImmutableList<T>
{
    @SuppressWarnings("unchecked")
    private static JImmutableArrayList EMPTY = new JImmutableArrayList(TrieArray.of(), 0, 0);

    private final JImmutableArray<T> values;
    private final int first;
    private final int next;

    private JImmutableArrayList(JImmutableArray<T> values,
                                int first,
                                int next)
    {
        this.values = values;
        this.first = first;
        this.next = next;
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableArrayList<T> of()
    {
        return (JImmutableArrayList<T>)EMPTY;
    }

    public static <T> JImmutableArrayList<T> of(Indexed<? extends T> source,
                                                int offset,
                                                int limit)
    {
        final int size = limit - offset;
        if (size == 0) {
            return of();
        } else {
            final JImmutableArray<T> values = TrieArray.of(source, offset, limit);
            return new JImmutableArrayList<T>(values, 0, size);
        }
    }

    public static <T> JImmutableArrayList<T> of(Indexed<T> source)
    {
        return of(source, 0, source.size());
    }

    @Override
    public boolean isEmpty()
    {
        return first == next;
    }

    @Override
    public JImmutableArrayList<T> assign(int index,
                                         T value)
    {
        final int realIndex = calcRealIndex(index);
        return new JImmutableArrayList<T>(values.assign(realIndex, value), first, next);
    }

    @Override
    public JImmutableArrayList<T> insert(T value)
    {
        if (next == Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        final int index = next;
        return new JImmutableArrayList<T>(values.assign(index, value), first, index + 1);
    }

    @Override
    public JImmutableArrayList<T> insertFirst(T value)
    {
        if (first == Integer.MIN_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        final int index = first - 1;
        return new JImmutableArrayList<T>(values.assign(index, value), index, next);
    }

    @Override
    public JImmutableArrayList<T> insertLast(T value)
    {
        return insert(value);
    }

    @Override
    public JImmutableArrayList<T> deleteFirst()
    {
        if (first == next) {
            throw new IndexOutOfBoundsException();
        }
        final int index = first;
        return new JImmutableArrayList<T>(values.delete(index), first + 1, next);
    }

    @Override
    public JImmutableArrayList<T> deleteLast()
    {
        if (first == next) {
            throw new IndexOutOfBoundsException();
        }
        final int index = next - 1;
        return new JImmutableArrayList<T>(values.delete(index), first, index);
    }

    @Override
    public int size()
    {
        return next - first;
    }

    @Override
    public JImmutableList<T> deleteAll()
    {
        return of();
    }

    @Override
    public T get(int index)
    {
        final int realIndex = calcRealIndex(index);
        return values.get(realIndex);
    }

    @Override
    public List<T> getList()
    {
        return ListAdaptor.of(this);
    }

    @Override
    public Cursor<T> cursor()
    {
        return values.valuesCursor();
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || (o instanceof JImmutableList && Cursors.areEqual(cursor(), ((JImmutableList)o).cursor()));
    }

    @Override
    public int hashCode()
    {
        return Cursors.computeHashCode(cursor());
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(cursor());
    }

    private int calcRealIndex(int index)
    {
        final int realIndex = first + index;
        if (realIndex < first || realIndex >= next) {
            throw new IndexOutOfBoundsException();
        }
        return realIndex;
    }
}
