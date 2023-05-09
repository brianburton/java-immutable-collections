package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public abstract class Result<T>
{
    private Result()
    {
    }

    public abstract T get()
        throws Exception;

    public abstract T orElse(T defaultValue);

    public abstract T orElseGet(Supplier<T> defaultValue);

    @Nonnull
    public abstract <U> Result<U> map(Func1Throws<T, U, Exception> func);

    @Nonnull
    public abstract <U> Result<U> flatMap(Func1<T, Result<U>> func);

    @Nonnull
    public abstract Result<T> mapFailure(Func1Throws<Exception, T, Exception> func);

    @Nonnull
    public abstract Result<T> flatMapFailure(Func1<Exception, Result<T>> func);

    /**
     * Add a step to the computation that executes the procedure on the current value without changing that value.
     * If the procedure throws it will terminate the computation at that point.
     *
     * @param proc the procedure to apply
     * @return the new computation
     */
    @Nonnull
    public abstract Result<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc);

    @Nonnull
    public static <T> Result<T> success(T value)
    {
        return new Success<>(value);
    }

    @Nonnull
    public static <T> Result<T> failure(Exception value)
    {
        return new Failure<>(value);
    }

    @Nonnull
    public static <T> Result<T> attempt(Callable<T> func)
    {
        try {
            return success(func.call());
        } catch (Exception error) {
            return failure(error);
        }
    }

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
        public Result<T> flatMapFailure(Func1<Exception, Result<T>> func)
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
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Success)) {
                return false;
            }
            Success<?> success = (Success<?>)o;
            return Objects.equals(value, success.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value);
        }
    }

    public static class Failure<T>
        extends Result<T>
    {
        private final Exception exception;

        private Failure(Exception exception)
        {
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
        public Result<T> flatMapFailure(Func1<Exception, Result<T>> func)
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
            return Objects.equals(exception, failure.exception);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(exception);
        }
    }
}
