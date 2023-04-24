package org.javimmutable.collections;

import java.util.concurrent.Callable;

public abstract class Result<TSuccess, TFailure>
{
    private Result()
    {
    }


    public static <TSuccess, TFailure> Result<TSuccess, TFailure> success(TSuccess value)
    {
        return new Success<>(value);
    }

    public static <TSuccess, TFailure> Result<TSuccess, TFailure> failure(TFailure value)
    {
        return new Failure<>(value);
    }

    public static <TSuccess> Result<TSuccess, Exception> attempt(Callable<TSuccess> func)
    {
        try {
            return success(func.call());
        } catch (Exception error) {
            return failure(error);
        }
    }

    public static class Success<TSuccess, TFailure>
        extends Result<TSuccess, TFailure>
    {
        private final TSuccess value;

        private Success(TSuccess value)
        {
            this.value = value;
        }
    }

    public static class Failure<TSuccess, TFailure>
        extends Result<TSuccess, TFailure>
    {
        private final TFailure value;

        private Failure(TFailure value)
        {
            this.value = value;
        }
    }
}
