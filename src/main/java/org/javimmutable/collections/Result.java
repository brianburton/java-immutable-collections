package org.javimmutable.collections;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public abstract class Result<T>
{
    public enum Kind
    {
        Value, Error
    }

    private Result()
    {
    }

    public static <T> Result<T> value(T value)
    {
        return new Value<>(value);
    }

    public static <T> Result<T> error(Exception error)
    {
        return new Error<>(error);
    }

    /**
     * Gets this {@link Result} kind.  Useful for processing with a switch.
     *
     * @return the type
     */
    @Nonnull
    public abstract Kind getKind();

    /**
     * @return true iff this {@link Result} has a value to return
     */
    public abstract boolean isValue();

    /**
     * @return true iff this {@link Result} has an error to return
     */
    public abstract boolean isError();

    /**
     * Retrieve the content of a value {@link Result}.
     *
     * @return the value
     * @throws UnsupportedOperationException if {@link Result} is an error
     */
    public abstract T getValue();

    /**
     * Retrieve the content of an error {@link Result}.
     *
     * @return the error
     * @throws UnsupportedOperationException if {@link Result} is a value
     */
    @Nonnull
    public abstract Exception getError();

    /**
     * Retrieve a {@link Holder} containing the value if this {@link Result} is a value.
     * Otherwise an empty {@link Holder} is returned.
     *
     * @return possibly empty {@link Holder}
     */
    @Nonnull
    public abstract Holder<T> toHolder();

    /**
     * Retrieve a {@link Option} containing the value if this {@link Result} is a non-null value.
     * Otherwise an empty {@link Option} is returned.
     *
     * @return possibly empty {@link Option}
     */
    @Nonnull
    public abstract Option<T> toOption();

    /**
     * Retrieve the value if we have one o or the defaultValue otherwise.
     *
     * @param defaultValue value to return if we do not have one
     * @return value or defaultValue
     */
    public abstract T getValueOr(T defaultValue);

    /**
     * Apply the transform function to my value (if I have one) and return a new {@link Result} containing the result.
     * If I am an error return an error {@link Result}.
     */
    @Nonnull
    public abstract <U, E extends Exception> Result<U> map(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator);

    /**
     * Apply the transform function to my value (if I have one) and return a new {@link Result} containing the result.
     * If I am an error return an error {@link Result}.
     */
    @Nonnull
    public abstract <U, E extends Exception> Result<U> flatMap(@Nonnull Func1Throws<? super T, Result<U>, E> transforminator);

    /**
     * Apply the transform function to my error (if I have one) and return a new {@link Result} containing the result.
     * If I am a value return myself.
     */
    @Nonnull
    public abstract <E extends Exception> Result<T> mapError(@Nonnull Func1Throws<? super Exception, ? extends Exception, E> transforminator);

    /**
     * Apply the transform function to my value (if I have one) and return a new {@link Result} containing the result.
     * If I am a value return myself.
     */
    @Nonnull
    public abstract <E extends Exception> Result<T> flatMapError(@Nonnull Func1Throws<? super Exception, Result<T>, E> transforminator);

    /**
     * Return my value if I have one.  Otherwise return defaultValue.
     */
    public abstract T orElse(T defaultValue);

    /**
     * Return my value if I have one.  Otherwise call supplier and return its result.
     */
    public abstract T orElseGet(@Nonnull Supplier<? extends T> supplier);

    /**
     * Return my value if I have one.  Otherwise call supplier and throw its result.
     */
    @Nonnull
    public abstract <X extends Throwable> T orElseThrow(@Nonnull Supplier<? extends X> supplier)
        throws X;

    public abstract <E extends Exception> Result<T> ifValue(@Nonnull Proc1Throws<T, E> action);

    public abstract <E extends Exception> Result<T> ifError(@Nonnull Proc1Throws<Exception, E> action);

    private static class Value<T>
        extends Result<T>
    {
        private final T value;

        private Value(T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public Kind getKind()
        {
            return Kind.Value;
        }

        @Override
        public boolean isValue()
        {
            return true;
        }

        @Override
        public boolean isError()
        {
            return false;
        }

        @Override
        public T getValue()
        {
            return value;
        }

        @Nonnull
        @Override
        public Exception getError()
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public Holder<T> toHolder()
        {
            return Holders.of(value);
        }

        @Nonnull
        @Override
        public Option<T> toOption()
        {
            return Option.of(value);
        }

        @Override
        public T getValueOr(T defaultValue)
        {
            return value;
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Result<U> map(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
        {
            try {
                return value(transforminator.apply(value));
            } catch (Exception newError) {
                return error(newError);
            }
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Result<U> flatMap(@Nonnull Func1Throws<? super T, Result<U>, E> transforminator)
        {
            try {
                return transforminator.apply(value);
            } catch (Exception newError) {
                return error(newError);
            }
        }

        @Nonnull
        @Override
        public <E extends Exception> Result<T> mapError(@Nonnull Func1Throws<? super Exception, ? extends Exception, E> transforminator)
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Result<T> flatMapError(@Nonnull Func1Throws<? super Exception, Result<T>, E> transforminator)
        {
            return this;
        }

        @Override
        public T orElse(T defaultValue)
        {
            return value;
        }

        @Override
        public T orElseGet(@Nonnull Supplier<? extends T> supplier)
        {
            return value;
        }

        @Nonnull
        @Override
        public <X extends Throwable> T orElseThrow(@Nonnull Supplier<? extends X> supplier)
            throws X
        {
            return value;
        }

        @Override
        public <E extends Exception> Result<T> ifValue(@Nonnull Proc1Throws<T, E> action)
        {
            try {
                action.apply(value);
                return this;
            } catch (Exception error) {
                return error(error);
            }
        }

        @Override
        public <E extends Exception> Result<T> ifError(@Nonnull Proc1Throws<Exception, E> action)
        {
            return this;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Value)) {
                return false;
            }
            Value<?> other = (Value<?>)o;
            return Objects.equals(value, other.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value);
        }

        @Override
        public String toString()
        {
            return "[value:" + (value == null ? "null" : value) + "]";
        }
    }

    private static class Error<T>
        extends Result<T>
    {
        @Nonnull
        private final Exception error;

        private Error(@Nonnull Exception error)
        {
            this.error = error;
        }

        @Nonnull
        @Override
        public Kind getKind()
        {
            return Kind.Error;
        }

        @Override
        public boolean isValue()
        {
            return false;
        }

        @Override
        public boolean isError()
        {
            return true;
        }

        @Override
        public T getValue()
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public Exception getError()
        {
            return error;
        }

        @Nonnull
        @Override
        public Holder<T> toHolder()
        {
            return Holders.of();
        }

        @Nonnull
        @Override
        public Option<T> toOption()
        {
            return Option.of();
        }

        @Override
        public T getValueOr(T defaultValue)
        {
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <U, E extends Exception> Result<U> map(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
        {
            return (Result<U>)this;
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <U, E extends Exception> Result<U> flatMap(@Nonnull Func1Throws<? super T, Result<U>, E> transforminator)
        {
            return (Result<U>)this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Result<T> mapError(@Nonnull Func1Throws<? super Exception, ? extends Exception, E> transforminator)
        {
            Exception newError;
            try {
                newError = transforminator.apply(error);
            } catch (Exception ex) {
                newError = ex;
                newError.addSuppressed(error);
            }
            return error(newError);
        }

        @Nonnull
        @Override
        public <E extends Exception> Result<T> flatMapError(@Nonnull Func1Throws<? super Exception, Result<T>, E> transforminator)
        {
            try {
                return transforminator.apply(error);
            } catch (Exception newError) {
                newError.addSuppressed(error);
                return error(newError);
            }
        }

        @Override
        public T orElse(T defaultValue)
        {
            return defaultValue;
        }

        @Override
        public T orElseGet(@Nonnull Supplier<? extends T> supplier)
        {
            return supplier.get();
        }

        @Nonnull
        @Override
        public <X extends Throwable> T orElseThrow(@Nonnull Supplier<? extends X> supplier)
            throws X
        {
            throw supplier.get();
        }

        @Override
        public <E extends Exception> Result<T> ifValue(@Nonnull Proc1Throws<T, E> action)
        {
            return this;
        }

        @Override
        public <E extends Exception> Result<T> ifError(@Nonnull Proc1Throws<Exception, E> action)
        {
            try {
                action.apply(error);
                return this;
            } catch (Exception newError) {
                newError.addSuppressed(error);
                return error(newError);
            }
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Error)) {
                return false;
            }
            Error<?> other = (Error<?>)o;
            return error.equals(other.error);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(error);
        }

        @Override
        public String toString()
        {
            return "[error:" + error.getClass().getName() + "]";
        }
    }
}
