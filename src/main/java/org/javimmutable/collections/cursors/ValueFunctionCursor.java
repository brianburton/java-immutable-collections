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
import org.javimmutable.collections.Holder;

import javax.annotation.Nonnull;

/**
 * Provides standard Cursor objects for iterating over the values of arbitrary functions.
 * The cursors are actually created using a factory object that can produce the functions.
 * The factories ensure that a Cursor is restartable and permits lazy evaluation of
 * the beginning of iteration.  The function will only be created if the starting
 * Cursor's start() or next() method is invoked.
 * <p>
 * The Cursors are not immutable but are designed to act as though they are.
 * Cursors remember their next value between invocations so that standard look ahead
 * behavior is preserved.
 * <p>
 * The Cursors are not thread safe.
 */
public abstract class ValueFunctionCursor
{
    /**
     * Produces a non-thread safe Cursor to traverse the values returned by a function.  The function
     * will be created using the factory when the traversal is initiated by calling the next() method.
     * Multiple invocations of the initial Cursor's next() method will produce multiple functions
     * using the factory.
     *
     * @param factory
     * @param <T>
     * @return
     */
    public static <T, F extends ValueFunction<T>, A extends ValueFunctionFactory<T, F>> Cursor<T> of(A factory)
    {
        return new Start<T, F, A>(factory);
    }

    protected static class Start<T, F extends ValueFunction<T>, A extends ValueFunctionFactory<T, F>>
            extends AbstractStartCursor<T>
    {
        private final A factory;

        protected Start(A factory)
        {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public Cursor<T> next()
        {
            final F function = createFunction();
            final Holder<T> firstValue = function.nextValue();
            return firstValue.isFilled() ? new Started<T, F>(function, firstValue.getValue()) : super.next();
        }

        protected F createFunction()
        {
            return factory.createFunction();
        }
    }

    private static class Started<T, F extends ValueFunction<T>>
            extends AbstractStartedCursor<T>
    {
        private final F function;
        private Cursor<T> next;
        private final T value;

        private Started(F function,
                        T value)
        {
            this.function = function;
            this.value = value;
        }

        @Override
        public T getValue()
        {
            return value;
        }

        @Nonnull
        @Override
        public Cursor<T> next()
        {
            if (next == null) {
                final Holder<T> nextValue = function.nextValue();
                next = nextValue.isFilled() ? new Started<T, F>(function, nextValue.getValue()) : super.next();
            }
            return next;
        }
    }
}
