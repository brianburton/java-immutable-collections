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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Callable;

/**
 * A deferred computation.  Allows multiple processing steps to be queued into an immutable
 * object for later evaluation.  Nothing is done until the {@link #compute} method is called.
 * An exception at any step stops the computation at that point.
 */
@Immutable
public abstract class Computation<T>
    implements Callable<T>
{
    /**
     * Add a step to the computation that applies the function to the current value to produce a new value.
     *
     * @param func function to apply
     * @param <U>  the type of the new value
     * @return the new computation
     */
    @Nonnull
    public abstract <U> Computation<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func);

    /**
     * Add a step to the computation that applies the function to the current value to produce a new computation.
     *
     * @param func function to apply
     * @param <U>  the type of the new value
     * @return the new computation
     */
    @Nonnull
    public abstract <U> Computation<U> flatMap(@Nonnull Func1Throws<T, Computation<U>, ? super Exception> func);

    /**
     * Add a step to the computation that executes the procedure on the current value without changing that value.
     * If the procedure throws it will terminate the computation at that point.
     *
     * @param proc the procedure to apply
     * @return the new computation
     */
    @Nonnull
    public abstract Computation<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc);

    /**
     * Execute the computation and return a {@link Result} containing the final value or an exception
     * that terminated the computation.
     *
     * @return the outcome as a {@link Result}
     */
    @Nonnull
    public Result<T> compute()
    {
        return Result.attempt(this);
    }

    /**
     * Execute the computation and return the final value or throw any exception that terminated the computation.
     *
     * @return the computed value
     * @throws Exception if the computation terminates due to an exception
     */
    @Override
    public abstract T call()
        throws Exception;

    private Computation()
    {
    }

    /**
     * Produce a {@link Computation} that simply returns the value.
     *
     * @param value the value to return
     * @param <T>   the type of value
     * @return the computation
     */
    @Nonnull
    public static <T> Computation<T> success(T value)
    {
        return new Mapped<>(() -> value);
    }

    /**
     * Produce a {@link Computation} that simply throws an exception.
     *
     * @param exception the exception to throw
     * @param <T>       the type of value
     * @return the computation
     */
    @Nonnull
    public static <T> Computation<T> failure(Exception exception)
    {
        return new Mapped<>(() -> {
            throw exception;
        });
    }

    /**
     * Produce a {@link Computation} that evaluations the function and returns its result.
     *
     * @param func the function to evaluate
     * @param <T>  the type of the result
     * @return the computation
     */
    @Nonnull
    public static <T> Computation<T> of(@Nonnull Func0Throws<T, ? super Exception> func)
    {
        return new Mapped<>(func);
    }

    private static class Mapped<T>
        extends Computation<T>
    {
        private final Func0Throws<T, ? extends Exception> func;

        private Mapped(Func0Throws<T, ? extends Exception> func)
        {
            this.func = func;
        }

        @Nonnull
        @Override
        public <U> Computation<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            return new Mapped<>(() -> func.apply(this.func.apply()));
        }

        @Nonnull
        @Override
        public <U> Computation<U> flatMap(@Nonnull Func1Throws<T, Computation<U>, ? super Exception> func)
        {
            return new FlatMapped<>(() -> func.apply(this.func.apply()));
        }

        @Nonnull
        @Override
        public Computation<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            return map(x -> {
                proc.apply(x);
                return x;
            });
        }

        @Override
        public T call()
            throws Exception
        {
            return func.apply();
        }
    }

    private static class FlatMapped<T>
        extends Computation<T>
    {
        private final Func0Throws<Computation<T>, ? extends Exception> func;

        private FlatMapped(Func0Throws<Computation<T>, ? extends Exception> func)
        {
            this.func = func;
        }

        @Nonnull
        @Override
        public <U> Computation<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            return new FlatMapped<>(() -> this.func.apply().map(func));
        }

        @Nonnull
        @Override
        public <U> Computation<U> flatMap(@Nonnull Func1Throws<T, Computation<U>, ? super Exception> func)
        {
            return new FlatMapped<>(() -> this.func.apply().flatMap(func));
        }

        @Nonnull
        @Override
        public Computation<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            return map(x -> {
                proc.apply(x);
                return x;
            });
        }

        @Override
        public T call()
            throws Exception
        {
            return func.apply().call();
        }
    }
}
