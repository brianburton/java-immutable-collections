///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.iterators.SequenceIterator;
import org.javimmutable.collections.serialization.JImmutableStackProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Singly linked list implementation of JImmutableStack that stores and retrieves values
 * in the reverse order of the corresponding add() method calls.  If forward or random
 * access to stored values is required use JImmutableList instead, but this class may be 
 * significantly faster when its limitations are acceptable.
 */
@Immutable
public class JImmutableLinkedStack<V>
    implements JImmutableStack<V>,
               Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableLinkedStack EMPTY = new JImmutableLinkedStack(null, null);
    private static final long serialVersionUID = -121805;

    private final V value;
    private final JImmutableLinkedStack<V> next;

    private JImmutableLinkedStack(V value,
                                  JImmutableLinkedStack<V> next)
    {
        this.value = value;
        this.next = next;
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableStack<T> of()
    {
        return EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableStack<T> of(T value)
    {
        return new JImmutableLinkedStack<>(value, EMPTY);
    }

    public static <T> JImmutableStack<T> of(List<T> values)
    {
        JImmutableStack<T> list = of();
        for (T value : values) {
            list = list.insert(value);
        }
        return list;
    }

    @SafeVarargs
    public static <T> JImmutableStack<T> of(T... values)
    {
        JImmutableStack<T> list = of();
        for (T value : values) {
            list = list.insert(value);
        }
        return list;
    }

    public boolean isEmpty()
    {
        return next == null;
    }

    public V getHead()
    {
        if (next == null) {
            throw new UnsupportedOperationException();
        }
        return value;
    }

    @Nonnull
    @Override
    public JImmutableStack<V> getTail()
    {
        if (next == null) {
            return this;
        } else {
            return next;
        }
    }

    @Nonnull
    @Override
    public JImmutableStack<V> insert(@Nullable V value)
    {
        return new JImmutableLinkedStack<>(value, this);
    }

    @Nonnull
    @Override
    public JImmutableStack<V> remove()
    {
        return getTail();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    public List<V> makeList()
    {
        final List<V> answer = new ArrayList<>();
        JImmutableStack<V> next = this;
        while (!next.isEmpty()) {
            answer.add(next.getHead());
            next = next.getTail();
        }
        return answer;
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof JImmutableStack) && IteratorHelper.iteratorEquals(iterator(), ((JImmutableStack)o).iterator());
    }

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(iterator());
    }

    @Nonnull
    @Override
    public JImmutableStack<V> getInsertableSelf()
    {
        return this;
    }

    @Nonnull
    @Override
    public SplitableIterator<V> iterator()
    {
        return SequenceIterator.iterator(this);
    }

    @Override
    public void checkInvariants()
    {
        if ((next == null) && !(this == EMPTY)) {
            throw new IllegalStateException();
        }
    }

    private Object writeReplace()
    {
        return new JImmutableStackProxy(this);
    }
}
