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

package org.javimmutable.collection.list;

import org.javimmutable.collection.Func0;
import org.javimmutable.collection.Func1;
import org.javimmutable.collection.Func2;
import org.javimmutable.collection.Proc1Throws;
import org.javimmutable.collection.Sum1Throws;
import org.javimmutable.collection.common.ToStringHelper;
import org.javimmutable.collection.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;

@Immutable
class EmptyNode<T>
    extends AbstractNode<T>
{
    private static EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode()
    {
    }

    @SuppressWarnings("unchecked")
    static <T> EmptyNode<T> instance()
    {
        return INSTANCE;
    }

    @Override
    boolean isEmpty()
    {
        return true;
    }

    @Override
    int size()
    {
        return 0;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Override
    <C> C seekImpl(int index,
                   Func0<C> defaultMapping,
                   Func1<T, C> valueMapping)
    {
        return defaultMapping.apply();
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        return new OneValueNode<>(value);
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        return node;
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        return new OneValueNode<>(value);
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        return node;
    }

    @Nonnull
    @Override
    AbstractNode<T> assign(int index,
                           T value)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        if (index == 0) {
            return new OneValueNode<>(value);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> prefix(int limit)
    {
        if (limit == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    void copyTo(T[] array,
                int offset)
    {
    }

    @Nonnull
    @Override
    AbstractNode<T> suffix(int offset)
    {
        if (offset == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> reverse()
    {
        return this;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        assert offset == 0 && limit == 0;
        return parent;
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
    }

    @Override
    public <V> V reduce(V initialValue,
                        Func2<V, T, V> accumulator)
    {
        return initialValue;
    }

    @Override
    public <V, E extends Exception> V reduceThrows(V initialValue,
                                                   Sum1Throws<T, V, E> accumulator)
        throws E
    {
        return initialValue;
    }

    @Override
    public void checkInvariants()
    {
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        forEach(value -> ToStringHelper.addToString(sb, 1, value));
        sb.append("]");
        return sb.toString();
    }
}
