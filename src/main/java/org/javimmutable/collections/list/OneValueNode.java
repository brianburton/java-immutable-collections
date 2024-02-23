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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.StringJoiner;
import java.util.function.Consumer;

class OneValueNode<T>
    extends AbstractNode<T>
{
    private final T value;

    OneValueNode(T value)
    {
        this.value = value;
    }

    @Override
    boolean isEmpty()
    {
        return false;
    }

    @Override
    int size()
    {
        return 1;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return value;
    }

    @Override
    <C> C seekImpl(int index,
                   Func0<C> defaultMapping,
                   Func1<T, C> valueMapping)
    {
        return index == 0 ? valueMapping.apply(value) : defaultMapping.apply();
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        return new MultiValueNode<>(this.value, value);
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        if (node.depth() > 0) {
            return node.prepend(this);
        } else if (node.size() == 0) {
            return this;
        } else {
            final int combinedSize = node.size() + 1;
            if (combinedSize <= MultiValueNode.MAX_SIZE) {
                return new MultiValueNode<>(this, node, combinedSize);
            } else {
                return new BranchNode<>(this, node, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        return new MultiValueNode<>(value, this.value);
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        if (node.depth() > 0) {
            return node.append(this);
        } else if (node.size() == 0) {
            return this;
        } else {
            final int combinedSize = node.size() + 1;
            if (combinedSize <= MultiValueNode.MAX_SIZE) {
                return new MultiValueNode<>(node, this, combinedSize);
            } else {
                return new BranchNode<>(node, this, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> assign(int index,
                           T value)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        } else if (value == this.value) {
            return this;
        } else {
            return new OneValueNode<>(value);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        switch (index) {
            case 0:
                return new MultiValueNode<>(value, this.value);

            case 1:
                return new MultiValueNode<>(this.value, value);

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        return EmptyNode.instance();
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        return EmptyNode.instance();
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        if (index == 0) {
            return EmptyNode.instance();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prefix(int limit)
    {
        switch (limit) {
            case 0:
                return EmptyNode.instance();

            case 1:
                return this;

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> suffix(int offset)
    {
        switch (offset) {
            case 0:
                return this;

            case 1:
                return EmptyNode.instance();

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> reverse()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {
    }

    @Override
    void copyTo(T[] array,
                int offset)
    {
        array[offset] = value;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return GenericIterator.singleValueState(parent, value, offset, limit);
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
        action.accept(value);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        proc.apply(value);
    }

    @Override
    public <V> V reduce(V initialValue,
                        Func2<V, T, V> accumulator)
    {
        return accumulator.apply(initialValue, value);
    }

    @Override
    public <V, E extends Exception> V reduceThrows(V initialValue,
                                                   Sum1Throws<T, V, E> accumulator)
        throws E
    {
        return accumulator.apply(initialValue, value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OneValueNode<?> that = (OneValueNode<?>)o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", OneValueNode.class.getSimpleName() + "[", "]")
            .add("value=" + value)
            .toString();
    }
}
