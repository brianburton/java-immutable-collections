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

package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;

@ThreadSafe
public class GenericIterator<T>
    extends AbstractSplitableIterator<T>
{
    static final int MIN_SIZE_FOR_SPLIT = 32;

    private final Iterable<T> root;
    private final int limit;
    private int offset;
    private boolean uninitialized;
    private State<T> state;

    public GenericIterator(@Nonnull Iterable<T> root,
                           int offset,
                           int limit)
    {
        assert offset <= limit;
        this.root = root;
        this.limit = limit;
        this.offset = offset;
        uninitialized = true;
    }

    public interface Iterable<T>
        extends SplitableIterable<T>
    {
        @Nullable
        State<T> iterateOverRange(@Nullable State<T> parent,
                                  int offset,
                                  int limit);

        int iterableSize();

        @Nonnull
        default SplitableIterator<T> iterator()
        {
            return new GenericIterator<>(this, 0, iterableSize());
        }
    }

    public interface State<T>
    {
        default boolean hasValue()
        {
            return false;
        }

        default T value()
        {
            throw new NoSuchElementException();
        }

        State<T> advance();
    }

    @Override
    public synchronized boolean hasNext()
    {
        return prepare();
    }

    @Override
    public synchronized T next()
    {
        if (!prepare()) {
            throw new NoSuchElementException();
        }
        final T answer = state.value();
        offset += 1;
        if (offset < limit) {
            state = state.advance();
        } else {
            state = null;
        }
        assert offset <= limit;
        return answer;
    }

    @Override
    public synchronized boolean isSplitAllowed()
    {
        return (limit - offset) >= MIN_SIZE_FOR_SPLIT;
    }

    @Nonnull
    @Override
    public synchronized SplitIterator<T> splitIterator()
    {
        final int splitIndex = offset + (limit - offset) / 2;
        return new SplitIterator<>(new GenericIterator<>(root, offset, splitIndex),
                                   new GenericIterator<>(root, splitIndex, limit));
    }

    private boolean prepare()
    {
        if (uninitialized) {
            state = root.iterateOverRange(null, offset, limit);
            uninitialized = false;
        }
        while (state != null) {
            if (state.hasValue()) {
                return true;
            }
            state = state.advance();
        }
        return false;
    }

    public static <T> State<T> valueState(State<T> parent,
                                          T value)
    {
        return new SingleValueState<>(parent, value);
    }

    public static <T> Iterable<T> valueIterable(T value)
    {
        return new Iterable<T>()
        {
            @Override
            public State<T> iterateOverRange(@Nullable State<T> parent,
                                             int offset,
                                             int limit)
            {
                assert offset >= 0 && offset <= limit && limit <= 1;
                if (offset == limit) {
                    return parent;
                } else {
                    return new SingleValueState<T>(parent, value);
                }
            }

            @Override
            public int iterableSize()
            {
                return 1;
            }
        };
    }

    public static <T> State<T> multiValueState(@Nullable State<T> parent,
                                               @Nonnull Indexed<T> values,
                                               int offset,
                                               int limit)
    {
        assert offset >= 0 && offset <= limit && limit <= values.size();
        return new MultiValueState<>(parent, values, offset, limit);
    }

    public static <T> State<T> indexedState(State<T> parent,
                                            Indexed<? extends Iterable<T>> children,
                                            int offset,
                                            int limit)
    {
        assert 0 <= offset && offset <= limit;
        if (offset == limit) {
            return parent;
        } else {
            return new IndexedState<>(parent, children, offset, limit);
        }
    }

    private static class SingleValueState<T>
        implements State<T>
    {
        private final State<T> parent;
        private final T value;
        private boolean available;

        private SingleValueState(State<T> parent,
                                 T value)
        {
            this.parent = parent;
            this.value = value;
            available = true;
        }

        @Override
        public boolean hasValue()
        {
            return available;
        }

        @Override
        public T value()
        {
            assert available;
            available = false;
            return value;
        }

        @Override
        public State<T> advance()
        {
            assert !available;
            return parent;
        }
    }

    private static class MultiValueState<T>
        implements GenericIterator.State<T>
    {
        private final GenericIterator.State<T> parent;
        private final Indexed<T> values;
        private final int limit;
        private int offset;

        private MultiValueState(@Nullable GenericIterator.State<T> parent,
                                @Nonnull Indexed<T> values,
                                int offset,
                                int limit)
        {
            this.parent = parent;
            this.values = values;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        public boolean hasValue()
        {
            return offset < limit;
        }

        @Override
        public T value()
        {
            return values.get(offset);
        }

        @Nullable
        @Override
        public GenericIterator.State<T> advance()
        {
            offset += 1;
            if (offset < limit) {
                return this;
            } else {
                return parent;
            }
        }
    }

    private static class IndexedState<T>
        implements State<T>
    {
        private final State<T> parent;
        private final Indexed<? extends Iterable<T>> children;
        private int offset;
        private int limit;
        private int index;

        public IndexedState(State<T> parent,
                            Indexed<? extends Iterable<T>> children,
                            int offset,
                            int limit)
        {
            this.parent = parent;
            this.children = children;
            this.limit = limit;
            this.offset = offset;
            index = 0;
        }

        @Override
        public State<T> advance()
        {
            final Iterable<T> child = children.get(index);
            final int size = child.iterableSize();
            if (offset >= size) {
                index += 1;
                offset -= size;
                limit -= size;
                return this;
            } else if (limit <= size) {
                return child.iterateOverRange(parent, offset, limit);
            } else {
                final State<T> answer = child.iterateOverRange(this, offset, size);
                index += 1;
                offset = 0;
                limit -= size;
                return answer;
            }
        }
    }
}
