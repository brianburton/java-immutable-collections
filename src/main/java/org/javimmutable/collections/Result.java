package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;

public abstract class Result<T>
{
    private Result()
    {
    }

    public abstract T get()
        throws Exception;

    public abstract <U> Result<U> map(Func1Throws<T, U, Exception> func);

    public abstract <U> Result<U> flatMap(Func1<T, Result<U>> func);

    public abstract Result<T> mapFailure(Func1Throws<Exception, T, Exception> func);

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

    public static <T> Result<T> success(T value)
    {
        return new Success<>(value);
    }

    public static <T> Result<T> failure(Exception value)
    {
        return new Failure<>(value);
    }

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
        public <U> Result<U> map(Func1Throws<T, U, Exception> func)
        {
            try {
                return new Success<>(func.apply(value));
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Override
        public <U> Result<U> flatMap(Func1<T, Result<U>> func)
        {
            try {
                return func.apply(value);
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

        @Override
        public Result<T> mapFailure(Func1Throws<Exception, T, Exception> func)
        {
            return this;
        }

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
        public <U> Result<U> map(Func1Throws<T, U, Exception> func)
        {
            return new Failure<>(exception);
        }

        @Override
        public <U> Result<U> flatMap(Func1<T, Result<U>> func)
        {
            return new Failure<>(exception);
        }

        @Override
        public Result<T> mapFailure(Func1Throws<Exception, T, Exception> func)
        {
            try {
                return new Success<>(func.apply(exception));
            } catch (Exception ex) {
                return new Failure<>(ex);
            }
        }

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
    }
}
