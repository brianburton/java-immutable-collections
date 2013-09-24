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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.EmptyCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;

/**
 * Singly linked list implementation of PersistentList that stores and retrieves values
 * in the reverse order of the corresponding add() method calls.  If forward or random
 * access to stored values is required use PersistentArrayList or PersistentTreeList
 * instead but this class  is significantly faster when its limitations are acceptable.
 *
 * @param <V>
 */
public abstract class PersistentLinkedList<V>
        extends AbstractPersistentList<V>
{
    private static final Empty EMPTY = new Empty();

    @SuppressWarnings("unchecked")
    public static <V> PersistentLinkedList<V> of()
    {
        return (PersistentLinkedList<V>)EMPTY;
    }

    public static <V> PersistentLinkedList<V> of(V value)
    {
        return new Single<V>(value);
    }

    public static <V> PersistentLinkedList<V> of(V... values)
    {
        PersistentLinkedList<V> list = of();
        for (V value : values) {
            list = list.add(value);
        }
        return list;
    }

    public static <V> PersistentLinkedList<V> of(Cursorable<V> values)
    {
        return of(values.cursor());
    }

    public static <V> PersistentLinkedList<V> of(Cursor<V> cursor)
    {
        PersistentLinkedList<V> list = of();
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            list = list.add(cursor.getValue());
        }
        return list;
    }

    public static <V> PersistentLinkedList<V> of(Iterable<V> values)
    {
        PersistentLinkedList<V> list = of();
        for (V value : values) {
            list = list.add(value);
        }
        return list;
    }

    public abstract PersistentLinkedList<V> add(V value);

    public abstract PersistentLinkedList<V> getTail();

    @Override
    public PersistentList<V> delete()
    {
        return getTail();
    }

    private static class Empty<V>
            extends PersistentLinkedList<V>
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
        public PersistentLinkedList<V> getTail()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public PersistentLinkedList<V> add(V value)
        {
            return new Single<V>(value);
        }

        public Cursor<V> cursor()
        {
            return EmptyCursor.of();
        }
    }

    private static class Single<V>
            extends PersistentLinkedList<V>
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
        public PersistentLinkedList<V> getTail()
        {
            return of();
        }

        @Override
        public PersistentLinkedList<V> add(V value)
        {
            return new Chain<V>(value, this);
        }

        public Cursor<V> cursor()
        {
            return SingleValueCursor.of(value);
        }
    }

    private static class Chain<V>
            extends PersistentLinkedList<V>
    {
        private V value;
        private PersistentLinkedList<V> next;

        private Chain(V value,
                      PersistentLinkedList<V> next)
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
        public PersistentLinkedList<V> getTail()
        {
            return next;
        }

        @Override
        public PersistentLinkedList<V> add(V value)
        {
            return new Chain<V>(value, this);
        }

        public Cursor<V> cursor()
        {
            return PersistentListCursor.of(this);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof PersistentList && Cursors.areEqual(cursor(), ((PersistentList)o).cursor());
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
}
