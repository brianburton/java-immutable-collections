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
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.SequenceCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Singly linked list implementation of PersistentList that stores and retrieves values
 * in the reverse order of the corresponding add() method calls.  If forward or random
 * access to stored values is required use PersistentArrayList or PersistentTreeList
 * instead but this class  is significantly faster when its limitations are acceptable.
 *
 * @param <T>
 */
@Immutable
public abstract class JImmutableLinkedStack<T>
        implements JImmutableStack<T>
{
    private static final Empty EMPTY = new Empty();

    @SuppressWarnings("unchecked")
    public static <T> JImmutableLinkedStack<T> of()
    {
        return (JImmutableLinkedStack<T>)EMPTY;
    }

    public static <T> JImmutableLinkedStack<T> of(T value)
    {
        return new Single<T>(value);
    }

    public static <T> JImmutableLinkedStack<T> of(List<T> values)
    {
        JImmutableLinkedStack<T> list = of();
        for (T value : values) {
            list = list.insert(value);
        }
        return list;
    }

    public static <T> JImmutableLinkedStack<T> of(T... values)
    {
        JImmutableLinkedStack<T> list = of();
        for (T value : values) {
            list = list.insert(value);
        }
        return list;
    }

    public abstract JImmutableLinkedStack<T> insert(T value);

    public abstract JImmutableLinkedStack<T> getTail();

    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    public List<T> makeList()
    {
        List<T> answer = new ArrayList<T>();
        if (!isEmpty()) {
            answer.add(getHead());
            for (JImmutableStack<T> next = getTail(); !next.isEmpty(); next = next.getTail()) {
                answer.add(next.getHead());
            }
        }
        return answer;
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof JImmutableStack) && Cursors.areEqual(cursor(), ((JImmutableStack)o).cursor());
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

    @Override
    public JImmutableStack<T> remove()
    {
        return getTail();
    }

    private static class Empty<V>
            extends JImmutableLinkedStack<V>
    {
        public boolean isEmpty()
        {
            return true;
        }

        public V getHead()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public JImmutableLinkedStack<V> getTail()
        {
            return this;
        }

        @Override
        public JImmutableLinkedStack<V> insert(V value)
        {
            return new Single<V>(value);
        }

        public Cursor<V> cursor()
        {
            return StandardCursor.of();
        }
    }

    private static class Single<V>
            extends JImmutableLinkedStack<V>
    {
        private V value;

        private Single(V value)
        {
            this.value = value;
        }

        public boolean isEmpty()
        {
            return false;
        }

        public V getHead()
        {
            return value;
        }

        @Override
        public JImmutableLinkedStack<V> getTail()
        {
            return of();
        }

        @Override
        public JImmutableLinkedStack<V> insert(V value)
        {
            return new Chain<V>(value, this);
        }

        public Cursor<V> cursor()
        {
            return SingleValueCursor.of(value);
        }
    }

    private static class Chain<V>
            extends JImmutableLinkedStack<V>
    {
        private V value;
        private JImmutableLinkedStack<V> next;

        private Chain(V value,
                      JImmutableLinkedStack<V> next)
        {
            this.value = value;
            this.next = next;
        }

        public boolean isEmpty()
        {
            return false;
        }

        public V getHead()
        {
            return value;
        }

        @Override
        public JImmutableLinkedStack<V> getTail()
        {
            return next;
        }

        @Override
        @Nonnull
        public JImmutableLinkedStack<V> insert(V value)
        {
            return new Chain<V>(value, this);
        }

        @Override
        @Nonnull
        public Cursor<V> cursor()
        {
            return SequenceCursor.of(this);
        }
    }
}
