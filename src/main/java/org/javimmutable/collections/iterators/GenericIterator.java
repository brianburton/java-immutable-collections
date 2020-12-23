///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;

/**
 * A mutable class implementing the SplitableIterator interface in a reusable way.
 * Maintains a current position in the collection it iterates over and delegates
 * to a "state" object provided by the collection to move forward inside the collection.
 * To implement spliterator functionality for parallel streams it limits itself to
 * a specific subset of the collection by way of an offset (current position) and
 * limit (stopping position).
 */
@ThreadSafe
public class GenericIterator<T>
    extends AbstractSplitableIterator<T>
{
    static final int MIN_SIZE_FOR_SPLIT = 32;

    private final Iterable<T> root;   // the collection we are iterating over
    private final int limit;          // stopping position in our allowed range                                     
    private int offset;               // current position in our allowed range
    private boolean uninitialized;    // true until next or hasNext has been called first time
    private State<T> state;           // tracks the next available position in the collection

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

    /**
     * Interface for collections that can be iterated over by GenericIterator using State objects
     * to track the progress of the iteration.  These do not need to be thread safe.
     */
    public interface Iterable<T>
        extends SplitableIterable<T>
    {
        /**
         * Create a State to iterate over the specified range of values if possible.
         * Can return null if no more values are available.  The range must be valid
         * for the collection being iterated over.  The returned State will return the
         * parent as its result when iteration over the range is completed.
         *
         * @param parent State to return to once the request iteration completes
         * @param offset starting point for iteration
         * @param limit  stopping point for iteration
         * @return null or a valid state
         */
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

        @Nonnull
        default IterableStreamable<T> streamable(int characteristics)
        {
            return new IterableStreamable<T>()
            {
                @Nonnull
                @Override
                public SplitableIterator<T> iterator()
                {
                    return Iterable.this.iterator();
                }

                @Override
                public int getSpliteratorCharacteristics()
                {
                    return characteristics;
                }
            };
        }
    }

    /**
     * Interface for objects that track the current position of an iteration in progress.
     * Unlike an Iterator which "looks ahead" to the next position (hasNext/next) a
     * State object "lives in the moment" and knows the current position (hasValue/value)
     * and doesn't know if a next value exists until told to go there (advance).
     * These do not need to be thread safe.
     */
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

        /**
         * Try to move forward to the next position.  Returns either a valid state (if next position exists)
         * or null (if there is no next position).  The returned State might be this State object or a new
         * State, or null.  The returned State might have a value or it might be empty.
         */
        State<T> advance();
    }

    @Override
    public synchronized boolean hasNext()
    {
        advanceStateToStartingPositionIfNecessary();
        return stateHasValue();
    }

    @Override
    public synchronized T next()
    {
        advanceStateToStartingPositionIfNecessary();
        if (!stateHasValue()) {
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

    /**
     * Ensures that state is on a valid position if possible.  Gets a starting state if we are
     * starting a new iteration otherwise it calls advance if necessary to move to the first
     * available position.
     */
    private void advanceStateToStartingPositionIfNecessary()
    {
        if (uninitialized) {
            state = root.iterateOverRange(null, offset, limit);
            uninitialized = false;
        }
        while (state != null && !state.hasValue()) {
            state = state.advance();
        }
    }

    private boolean stateHasValue()
    {
        return state != null && state.hasValue();
    }

    /**
     * Returns a State for iterating a single value.
     */
    public static <T> State<T> singleValueState(State<T> parent,
                                                T value)
    {
        return new SingleValueState<>(parent, value);
    }

    /**
     * Returns an Iterable for iterating a single value.
     */
    public static <T> Iterable<T> singleValueIterable(T value)
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
                    return new SingleValueState<>(parent, value);
                }
            }

            @Override
            public int iterableSize()
            {
                return 1;
            }
        };
    }

    /**
     * Returns a State for iterating multiple values stored in an Indexed collection.
     */
    public static <T> State<T> multiValueState(@Nullable State<T> parent,
                                               @Nonnull Indexed<T> values,
                                               int offset,
                                               int limit)
    {
        assert offset >= 0 && offset <= limit && limit <= values.size();
        return new MultiValueState<>(parent, values, offset, limit);
    }

    /**
     * Returns a State for iterating multiple collections (Iterables) that are themselves
     * stored in an Indexed collection.
     */
    public static <T> State<T> multiIterableState(@Nullable State<T> parent,
                                                  @Nonnull Indexed<? extends Iterable<T>> children,
                                                  int offset,
                                                  int limit)
    {
        assert 0 <= offset && offset <= limit;
        if (offset == limit) {
            return parent;
        } else {
            return new MultiIterableState<>(parent, children, offset, limit);
        }
    }

    /**
     * Returns a State for iterating over another State's values but transforming each of
     * those values using a function before returning to its caller.
     */
    public static <A, B> State<B> transformState(@Nullable State<B> parent,
                                                 @Nullable State<A> source,
                                                 @Nonnull Func1<A, B> transforminator)
    {
        if (source == null) {
            return parent;
        } else {
            return new TransformState<>(parent, source, transforminator);
        }
    }

    /**
     * Returns an Iterable for iterating over another Iterable's values but transforming each of
     * those values using a function before returning to its caller.
     */
    public static <A, B> Iterable<B> transformIterable(@Nonnull Iterable<A> source,
                                                       @Nonnull Func1<A, B> transforminator)
    {
        return new Iterable<B>()
        {
            @Nullable
            @Override
            public State<B> iterateOverRange(@Nullable State<B> parent,
                                             int offset,
                                             int limit)
            {
                return transformState(parent, source.iterateOverRange(null, offset, limit), transforminator);
            }

            @Override
            public int iterableSize()
            {
                return source.iterableSize();
            }
        };
    }

    private static class SingleValueState<T>
        implements State<T>
    {
        private final State<T> parent;
        private final T value;
        private boolean available;

        private SingleValueState(@Nullable State<T> parent,
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

    private static class MultiIterableState<T>
        implements State<T>
    {
        private final State<T> parent;
        private final Indexed<? extends Iterable<T>> collections;
        private int offset;
        private int limit;
        private int index;

        private MultiIterableState(@Nullable State<T> parent,
                                   @Nonnull Indexed<? extends Iterable<T>> collections,
                                   int offset,
                                   int limit)
        {
            this.parent = parent;
            this.collections = collections;
            this.limit = limit;
            this.offset = offset;
            index = 0;
        }

        @Override
        public State<T> advance()
        {
            final Iterable<T> collection = collections.get(index);
            final int size = collection.iterableSize();
            if (offset >= size) {
                // Starting point for iteration is somewhere beyond this collection.
                // Advance to the next collection to try again.  Offset and limit have
                // to be adjusted accordingly for the next collection.
                index += 1;
                offset -= size;
                limit -= size;
                return this;
            } else if (limit <= size) {
                // this collection contains all remaining values so pass control to it forever
                return collection.iterateOverRange(parent, offset, limit);
            } else {
                // This collection contains at least one value.  Transfer control to it
                // passing us as parent so we can resume control once it's exhausted.
                // Offset and limit have to be adjusted to resume at the next collection
                // when we get control again.
                final State<T> answer = collection.iterateOverRange(this, offset, size);
                index += 1;
                offset = 0;
                limit -= size;
                return answer;
            }
        }
    }

    private static class TransformState<A, B>
        implements GenericIterator.State<B>
    {
        private final Func1<A, B> transforminator;
        private final GenericIterator.State<B> parent;
        private GenericIterator.State<A> source;

        private TransformState(@Nullable GenericIterator.State<B> parent,
                               @Nonnull GenericIterator.State<A> source,
                               @Nonnull Func1<A, B> transforminator)
        {
            this.transforminator = transforminator;
            this.parent = parent;
            this.source = source;
        }

        @Override
        public boolean hasValue()
        {
            return source.hasValue();
        }

        @Override
        public B value()
        {
            return transforminator.apply(source.value());
        }

        @Nullable
        @Override
        public GenericIterator.State<B> advance()
        {
            source = source.advance();
            if (source == null) {
                return parent;
            } else {
                return this;
            }
        }
    }
}
