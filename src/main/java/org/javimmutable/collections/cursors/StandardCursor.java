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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class that implements standard Cursor behavior for classes that do not
 * naturally start at a position before the first element.  Such classes can pass
 * a Source implementation to the of() method and this class will ensure that traversal
 * does not start until the next() method is called and does not progress beyond the
 * point where atEnd() is true.
 */
public abstract class StandardCursor
{
    @SuppressWarnings("unchecked")
    private static final Cursor EMPTY = new Start(new EmptySource());

    /**
     * Simple interface for classes that can iterate immediately (i.e. do not require a lazy start).
     * A Source must start already pointing at a current value.
     * Implementations of this interface must be immutable.
     *
     * @param <T>
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
    }

    /**
     * Creates an empty cursor that has no values.
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Cursor<T> of()
    {
        return (Cursor<T>)EMPTY;
    }

    /**
     * Creates a Cursor for the given Source.  The Source must point to the first value (i.e. cannot be
     * be used for empty collections) to be iterated over.
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> Cursor<T> of(Source<T> source)
    {
        return new Start<T>(source);
    }

    /**
     * Creates a java.util.Iterator that iterates over values in the specified Source.
     * The Source must point to the first value (i.e. cannot be
     * be used for empty collections) to be iterated over.
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> Iterator<T> iterator(Source<T> source)
    {
        return new SourceIterator<T>(source);
    }

    /**
     * Creates a Cursor over a range of integers.  Useful for test purposes.
     *
     * @param low
     * @param high
     * @return
     */
    public static Cursor<Integer> forRange(int low,
                                           int high)
    {
        return StandardCursor.of(new RangeSource(low, high));
    }

    /**
     * Utility method, useful in unit tests, that collects all of the values in the Cursor into a List
     * and returns the List.
     *
     * @param cursor
     * @param <T>
     * @return
     */
    public static <T> List<T> makeList(Cursor<T> cursor)
    {
        List<T> answer = new ArrayList<T>();
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            answer.add(cursor.getValue());
        }
        return answer;
    }

    private static class Start<V>
            extends AbstractStartCursor<V>
    {
        private final Source<V> source;

        private Start(Source<V> source)
        {
            this.source = source;
        }

        @Override
        public Cursor<V> next()
        {
            return new Started<V>(source);
        }
    }

    private static class Started<V>
            implements Cursor<V>
    {
        private final Source<V> source;

        private Started(Source<V> source)
        {
            this.source = source;
        }

        @Override
        public Cursor<V> next()
        {
            return source.atEnd() ? EmptyStartedCursor.<V>of() : new Started<V>(source.advance());
        }

        @Override
        public boolean hasValue()
        {
            return !source.atEnd();
        }

        @Override
        public V getValue()
        {
            if (source.atEnd()) {
                throw new NoValueException();
            }
            return source.currentValue();
        }
    }

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
    }

    private static class SourceIterator<V>
            implements Iterator<V>
    {
        private Source<V> source;

        private SourceIterator(Source<V> source)
        {
            this.source = source;
        }

        @Override
        public boolean hasNext()
        {
            return !source.atEnd();
        }

        @Override
        public V next()
        {
            final V value = source.currentValue();
            source = source.advance();
            return value;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
