///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.array.trie.EmptyTrieNode;
import org.javimmutable.collections.array.trie.TrieNode;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.StandardCursor;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation of PersistentIndexedList that uses a sparse array for its implementation.
 * Values are stored and traversed in the same order as they are added using insert().
 * Performance is slower than PersistentLinkedList so if forward order and/or random
 * access are not required using that class may be a better option.
 *
 * @param <T>
 */
public class JImmutableArrayList<T>
        implements JImmutableList<T>
{
    private static final JImmutableArrayList EMPTY = new JImmutableArrayList();

    private final TrieNode<T> values;
    private final int first;
    private final int next;

    public JImmutableArrayList()
    {
        this(EmptyTrieNode.<T>of(), 0, 0);
    }

    private JImmutableArrayList(TrieNode<T> values,
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

    @Override
    public boolean isEmpty()
    {
        return first == next;
    }

    @Override
    public JImmutableList<T> assign(int index,
                                    T value)
    {
        final int realIndex = calcRealIndex(index);
        return new JImmutableArrayList<T>(values.assign(realIndex >>> 5, realIndex & 0x1f, value), first, next);
    }

    @Override
    public JImmutableArrayList<T> insert(T value)
    {
        final int index = next;
        return new JImmutableArrayList<T>(values.assign(index >>> 5, index & 0x1f, value), first, index + 1);
    }

    @Override
    public JImmutableArrayList<T> insertFirst(T value)
    {
        final int index = first - 1;
        return new JImmutableArrayList<T>(values.assign(index >>> 5, index & 0x1f, value), index, next);
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
        return new JImmutableArrayList<T>(values.delete(index >>> 5, index & 0x1f), first + 1, next);
    }

    @Override
    public JImmutableArrayList<T> deleteLast()
    {
        if (first == next) {
            throw new IndexOutOfBoundsException();
        }
        final int index = next - 1;
        return new JImmutableArrayList<T>(values.delete(index >>> 5, index & 0x1f), first, index);
    }

    @Override
    public int size()
    {
        return next - first;
    }

    @Override
    public T get(int index)
    {
        final int realIndex = calcRealIndex(index);
        return values.get(realIndex >>> 5, realIndex & 0x1f).getValue();
    }

    @Override
    public List<T> getList()
    {
        return ListAdaptor.of(this);
    }

    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of(new CursorSource(0));
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof JImmutableList && Cursors.areEqual(cursor(), ((JImmutableList)o).cursor());
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

    private class CursorSource
            implements StandardCursor.Source<T>
    {
        private int realIndex = first;

        private CursorSource(int realIndex)
        {
            this.realIndex = realIndex;
        }

        @Override
        public boolean atEnd()
        {
            return realIndex >= next;
        }

        @Override
        public T currentValue()
        {
            return get(realIndex);
        }

        @Override
        public StandardCursor.Source<T> advance()
        {
            return new CursorSource(realIndex + 1);
        }
    }
}
