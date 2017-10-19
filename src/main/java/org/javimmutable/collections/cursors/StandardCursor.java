///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitCursor;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.common.IteratorAdaptor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Utility class that implements standard Cursor behavior for classes that do not
 * naturally start at a position before the first element.  Such classes can pass
 * a Source implementation to the of() method and this class will ensure that traversal
 * does not start until the next() method is called and does not progress beyond the
 * point where atEnd() is true.
 */
@Immutable
public abstract class StandardCursor
{
    @SuppressWarnings("unchecked")
    private static final Cursor EMPTY = new Start(new EmptySource());

    /**
     * Simple interface for classes that can iterate immediately (i.e. do not require a lazy start).
     * A Source must start already pointing at a current value.
     * Implementations of this interface must be immutable.
     */
    public interface Source<T>
    {
        /**
         * @return true iff a call to advance() will fail
         */
        boolean atEnd();

        /**
         * @return current value
         */
        T currentValue();

        /**
         * @return new Source pointing at the next value or throw if no next value available
         */
        Source<T> advance();

        default boolean isSplitAllowed()
        {
            return false;
        }

        default Tuple2<Source<T>, Source<T>> splitSource()
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Creates an empty cursor that has no values.
     */
    @SuppressWarnings("unchecked")
    public static <T> Cursor<T> of()
    {
        return (Cursor<T>)EMPTY;
    }

    /**
     * Creates a Cursor for the given Source.  The Source must point to the first value (i.e. cannot be
     * be used for empty collections) to be iterated over.
     */
    public static <T> Cursor<T> of(Source<T> source)
    {
        return source.atEnd() ? of() : new Start<>(source);
    }

    /**
     * Creates a Cursor for the given Indexed.
     */
    public static <T> Cursor<T> of(Indexed<T> source)
    {
        return new Start<>(new IndexedSource<>(source, 0, source.size()));
    }

    /**
     * Creates a java.util.Iterator that iterates over values in the specified Source.
     * The Source must point to the first value (i.e. cannot be
     * be used for empty collections) to be iterated over.
     */
    public static <T> Iterator<T> iterator(Source<T> source)
    {
        return new SourceIterator<>(source);
    }

    /**
     * Creates a Cursor over a range of integers.  Useful for test purposes.
     */
    public static Cursor<Integer> forRange(int low,
                                           int high)
    {
        return of(new RangeSource(low, high));
    }

    /**
     * Utility method, useful in unit tests, that collects all of the values in the Cursor into a List
     * and returns the List.
     */
    public static <T> List<T> makeList(Cursor<T> cursor)
    {
        List<T> answer = new ArrayList<>();
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            answer.add(cursor.getValue());
        }
        return answer;
    }

    /**
     * Creates a Cursorable that always returns an empty Cursor.
     */
    public static <T> Cursorable<T> emptyCursorable()
    {
        return new Cursorable<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> cursor()
            {
                return StandardCursor.of();
            }
        };
    }

    @Immutable
    private static class Start<T>
        extends AbstractStartCursor<T>
    {
        private final Source<T> source;

        private Start(Source<T> source)
        {
            this.source = source;
        }

        @Nonnull
        @Override
        public Cursor<T> next()
        {
            return new Started<>(source);
        }

        @Nonnull
        @Override
        public Iterator<T> iterator()
        {
            return IteratorAdaptor.of(this);
        }
    }

    @Immutable
    private static class Started<T>
        extends AbstractStartedCursor<T>
    {
        private final Source<T> source;

        private Started(Source<T> source)
        {
            this.source = source;
        }

        @Nonnull
        @Override
        public Cursor<T> next()
        {
            return source.atEnd() ? super.next() : new Started<>(source.advance());
        }

        @Override
        public boolean hasValue()
        {
            return !source.atEnd();
        }

        @Override
        public T getValue()
        {
            if (source.atEnd()) {
                throw new NoValueException();
            }
            return source.currentValue();
        }

        @Override
        public Iterator<T> iterator()
        {
            return IteratorAdaptor.of(this);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return source.isSplitAllowed();
        }

        @Override
        public SplitCursor<T> splitCursor()
        {
            Tuple2<Source<T>, Source<T>> split = source.splitSource();
            return new SplitCursor<>(of(split.getFirst()), of(split.getSecond()));
        }
    }

    @Immutable
    private static class EmptySource<T>
        implements Source<T>
    {
        @Override
        public boolean atEnd()
        {
            return true;
        }

        @Override
        public T currentValue()
        {
            throw new Cursor.NoValueException();
        }

        @Override
        public Source<T> advance()
        {
            throw new IllegalStateException();
        }
    }

    @Immutable
    static class RangeSource
        implements Source<Integer>
    {
        private final int low;
        private final int high;

        RangeSource(int low,
                    int high)
        {
            this.low = low;
            this.high = high;
        }

        @Override
        public boolean atEnd()
        {
            return low > high;
        }

        @Override
        public Integer currentValue()
        {
            return low;
        }

        @Override
        public Source<Integer> advance()
        {
            return new RangeSource(low + 1, high);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return high > low;
        }

        @Override
        public Tuple2<Source<Integer>, Source<Integer>> splitSource()
        {
            if (low >= high) {
                throw new UnsupportedOperationException();
            }
            final int middle = low + 1 + (high - low) / 2;
            return Tuple2.of(new RangeSource(low, middle - 1), new RangeSource(middle, high));
        }
    }

    @Immutable
    public static class RepeatingValueCursorSource<T>
        implements StandardCursor.Source<T>
    {
        private int count;
        private final T value;

        public RepeatingValueCursorSource(JImmutableMap.Entry<T, Integer> entry)
        {
            this.count = entry.getValue();
            this.value = entry.getKey();
        }

        private RepeatingValueCursorSource(int count,
                                           T value)
        {
            this.count = count;
            this.value = value;
        }

        @Override
        public boolean atEnd()
        {
            return count <= 0;
        }

        @Override
        public T currentValue()
        {
            return value;
        }

        @Override
        public StandardCursor.Source<T> advance()
        {
            return new RepeatingValueCursorSource<>(count - 1, value);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return count >= 2;
        }

        @Override
        public Tuple2<Source<T>, Source<T>> splitSource()
        {
            if (count < 2) {
                throw new UnsupportedOperationException();
            }
            final int left = count / 2;
            final int right = count - left;
            return Tuple2.of(new RepeatingValueCursorSource<>(left, value), new RepeatingValueCursorSource<>(right, value));
        }
    }

    private static class SourceIterator<T>
        implements Iterator<T>
    {
        private Source<T> source;

        private SourceIterator(Source<T> source)
        {
            this.source = source;
        }

        @Override
        public boolean hasNext()
        {
            return !source.atEnd();
        }

        @Override
        public T next()
        {
            if (source.atEnd()) {
                throw new NoSuchElementException();
            }
            final T value = source.currentValue();
            source = source.advance();
            return value;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    @Immutable
    private static class IndexedSource<T>
        implements Source<T>
    {
        private final Indexed<T> list;
        private final int index;
        private final int limit;

        private IndexedSource(Indexed<T> list,
                              int index,
                              int limit)
        {
            this.list = list;
            this.index = index;
            this.limit = limit;
        }

        @Override
        public boolean atEnd()
        {
            return index >= limit;
        }

        @Override
        public T currentValue()
        {
            return list.get(index);
        }

        @Override
        public Source<T> advance()
        {
            return new IndexedSource<>(list, index + 1, limit);
        }

        @Override
        public boolean isSplitAllowed()
        {
            return (limit - index) > 1;
        }

        @Override
        public Tuple2<Source<T>, Source<T>> splitSource()
        {
            final int splitIndex = index + (limit - index) / 2;
            if (splitIndex == index) {
                throw new UnsupportedOperationException();
            }
            return Tuple2.of(new IndexedSource<>(list, index, splitIndex), new IndexedSource<>(list, splitIndex, limit));
        }
    }
}
