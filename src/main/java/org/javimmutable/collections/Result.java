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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Container for the result of some computation.  Contains either the computed value or
 * some exception that was thrown when attempting to compute the value.  Allows success
 * or failure to be treated.
 *
 * @param <T> type of value being computed
 */
@Immutable
public abstract class Result<T>
{
    private Result()
    {
    }

    /**
     * Creates a successful {@link Result} containing the given value.
     */
    @Nonnull
    public static <T> Result<T> success(T value)
    {
        return new Success<>(value);
    }

    /**
     * Creates a failure {@link Result} containing the exception that was thrown.
     */
    @Nonnull
    public static <T> Result<T> failure(@Nonnull Exception value)
    {
        if (value == null) {
            value = new IllegalArgumentException("null exception passed to failure()");
        }
        return new Failure<>(value);
    }

    /**
     * Attempts to compute a value and returns an appropriate {@link Result}.
     * Captures an {@link Exception} thrown and returns a {@link Result#failure} or,
     * if no exception was thrown, returns a {@link Result#success} containing the value.
     *
     * @param func the computation that should produce a result
     * @return the success or failure result
     */
    @Nonnull
    public static <T> Result<T> attempt(Callable<T> func)
    {
        try {
            return success(func.call());
        } catch (Exception error) {
            return failure(error);
        }
    }

    /**
     * Gets the value or throws the exception.  Used to unwrap the result so it can
     * be handled using try/catch.
     *
     * @return the value if we are a successful result
     * @throws Exception if we are a failure result
     */
    public abstract T get()
        throws Exception;

    /**
     * Converts a failure result into a success result with a specified value.
     * If we are a failure result return a success result containing the specified value.
     * Otherwise return this.
     */
    public abstract T orElse(T defaultValue);

    /**
     * Converts a failure result into a success result with the value returned by a {@link Supplier}.
     * If we are a failure result return a success result containing the value returned by the {@link Supplier}.
     * Otherwise return this.  Does not capture any runtime exception thrown by the supplier.
     */
    public abstract T orElseGet(Supplier<T> defaultValue);

    /**
     * Replaces our successful result with a new value computed using the provided function.
     * Simply returns this if we are a failure result.
     */
    @Nonnull
    public abstract <U> Result<U> map(Func1Throws<T, U, Exception> func);

    /**
     * Replaces our successful result with a new value computed using the provided function.
     * Simply returns this if we are a failure result.
     */
    @Nonnull
    public abstract <U> Result<U> flatMap(Func1<T, Result<U>> func);

    /**
     * Replaces our failure result with a new value computed using the provided function.
     * Simply returns this if we are a success result.
     */
    @Nonnull
    public abstract Result<T> mapFailure(Func1Throws<Exception, T, Exception> func);

    /**
     * Replaces our failure result with a new value computed using the provided function.
     * Simply returns this if we are a success result.
     */
    @Nonnull
    public abstract Result<T> flatMapFailure(Func1Throws<Exception, Result<T>, Exception> func);

    /**
     * Does nothing if we are a failure result.
     * Calls a function with our value if we are a success result.
     * If the function throws an exception returns a new failure result containing that exception.
     * Otherwise returns this.
     */
    @Nonnull
    public abstract Result<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc);

    public static class Success<T>
        extends Result<T>
    {
        private final T value;

        private Success(T value)
        {
            this.value = value;
        }

        @Override
        public T get()
            throws Exception
        {
            return value;
        }

        @Override
        public T orElse(T defaultValue)
        {
            return value;
        }

        @Override
        public T orElseGet(Supplier<T> defaultValue)
        {
            return value;
        }

        @Nonnull
        @Override
        public <U> Result<U> map(Func1Throws<T, U, Exception> func)
        {
            try {
                return new Success<>(func.apply(value));
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Nonnull
        @Override
        public <U> Result<U> flatMap(Func1<T, Result<U>> func)
        {
            try {
                return func.apply(value);
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Nonnull
        @Override
        public Result<T> mapFailure(Func1Throws<Exception, T, Exception> func)
        {
            return this;
        }

        @Nonnull
        @Override
        public Result<T> flatMapFailure(Func1Throws<Exception, Result<T>, Exception> func)
        {
            return this;
        }

        @Nonnull
        @Override
        public Result<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            try {
                proc.apply(value);
                return this;
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Success)) {
                return false;
            }
            Object otherValue = ((Success)obj).value;
            if (value == null) {
                return otherValue == null;
            }
            if (otherValue == null) {
                return false;
            }
            return value.equals(otherValue);
        }

        @Override
        public int hashCode()
        {
            return value == null ? 0 : value.hashCode();
        }
    }

    public static class Failure<T>
        extends Result<T>
    {
        private final Exception exception;

        private Failure(Exception exception)
        {
            assert exception != null;
            this.exception = exception;
        }

        @Override
        public T get()
            throws Exception
        {
            throw exception;
        }

        @Override
        public T orElse(T defaultValue)
        {
            return defaultValue;
        }

        @Override
        public T orElseGet(Supplier<T> defaultValue)
        {
            return defaultValue.get();
        }

        @Nonnull
        @Override
        public <U> Result<U> map(Func1Throws<T, U, Exception> func)
        {
            return new Failure<>(exception);
        }

        @Nonnull
        @Override
        public <U> Result<U> flatMap(Func1<T, Result<U>> func)
        {
            return new Failure<>(exception);
        }

        @Nonnull
        @Override
        public Result<T> mapFailure(Func1Throws<Exception, T, Exception> func)
        {
            try {
                return new Success<>(func.apply(exception));
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Nonnull
        @Override
        public Result<T> flatMapFailure(Func1Throws<Exception, Result<T>, Exception> func)
        {
            try {
                return func.apply(exception);
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Nonnull
        @Override
        public Result<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            return this;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Failure)) {
                return false;
            }
            Failure<?> failure = (Failure<?>)o;
            return exception.equals(failure.exception);
        }

        @Override
        public int hashCode()
        {
            return exception.hashCode();
        }
    }
}
