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

package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.indexed.IndexedList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;

public final class IndexedIterator
{
    private IndexedIterator()
    {
    }

    public static <T> SplitableIterator<T> forward(@Nonnull Indexed<T> values)
    {
        return new Forward<>(values, -1, values.size() - 1);
    }

    public static <T> SplitableIterable<T> fwd(@Nonnull Indexed<T> values)
    {
        return () -> forward(values);
    }

    public static <T> SplitableIterator<T> iterator(@Nonnull Indexed<T> values)
    {
        return forward(values);
    }

    public static <T> SplitableIterator<T> reverse(@Nonnull Indexed<T> values)
    {
        return new Reverse<>(values, values.size(), 0);
    }

    public static <T> SplitableIterator<T> reverse(@Nonnull List<T> values)
    {
        return new Reverse<>(IndexedList.retained(values), values.size(), 0);
    }

    public static <T> SplitableIterable<T> rev(@Nonnull Indexed<T> values)
    {
        return () -> reverse(values);
    }

    public static SplitableIterator<Integer> forRange(int low,
                                                      int high)
    {
        return iterator(IndexedHelper.range(low, high));
    }

    private static class Forward<T>
        extends AbstractSplitableIterator<T>
    {
        @Nonnull
        private final Indexed<T> values;
        private final int limit;
        private int index;

        public Forward(@Nonnull Indexed<T> values,
                       int index,
                       int limit)
        {
            this.values = values;
            this.limit = limit;
            this.index = index;
        }

        @Override
        public boolean hasNext()
        {
            return index < limit;
        }

        @Override
        public T next()
        {
            if (index >= limit) {
                throw new NoSuchElementException();
            }
            index += 1;
            return values.get(index);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return (limit - index) > 1;
        }

        @Nonnull
        @Override
        public SplitIterator<T> splitIterator()
        {
            final int splitIndex = index + (limit - index) / 2;
            if (splitIndex == index) {
                throw new UnsupportedOperationException();
            }
            return new SplitIterator<>(new Forward<>(values, index, splitIndex),
                                       new Forward<>(values, splitIndex, limit));
        }
    }

    private static class Reverse<T>
        extends AbstractSplitableIterator<T>
    {
        @Nonnull
        private final Indexed<T> values;
        private final int limit;
        private int index;

        public Reverse(@Nonnull Indexed<T> values,
                       int index,
                       int limit)
        {
            this.values = values;
            this.limit = limit;
            this.index = index;
        }

        @Override
        public boolean hasNext()
        {
            return index > limit;
        }

        @Override
        public T next()
        {
            if (index <= limit) {
                throw new NoSuchElementException();
            }
            index -= 1;
            return values.get(index);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return (index - limit) > 1;
        }

        @Nonnull
        @Override
        public SplitIterator<T> splitIterator()
        {
            final int splitIndex = index - (index - limit) / 2;
            if (splitIndex == index) {
                throw new UnsupportedOperationException();
            }
            return new SplitIterator<>(new Reverse<>(values, index, splitIndex),
                                       new Reverse<>(values, splitIndex, limit));
        }
    }
}
