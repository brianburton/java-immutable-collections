package org.javimmutable.collections;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * Equivalent to {@link Holder} except that only two classes exist, one holding a non-null value and the other holding nothing.
 * Intended for use when null values are not acceptable.
 * Helper methods are provided to switch between {@link Holder} and {@link Option}.
 *
 * @param <T> Type of value being stored
 */
public abstract class Option<T>
{
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Option<T> of()
    {
        return (Empty<T>)Empty.EMPTY;
    }

    @Nonnull
    public static <T> Option<T> of(T valueOrNull)
    {
        return valueOrNull == null ? of() : new Filled<>(valueOrNull);
    }

    // only subclasses defined here are allowed
    private Option()
    {
    }

    @Nonnull
    public abstract Holder<T> toHolder();

    /**
     * @return true iff this {@link Option} has no value to return
     */
    public abstract boolean isEmpty();

    /**
     * @return true iff this {@link Option} has a value to return
     */
    public abstract boolean isNonEmpty();

    /**
     * @return true iff this {@link Option} has a value to return
     */
    public abstract boolean isFilled();

    /**
     * Retrieve the value of a filled {@link Option}.  Must throw if {@link Option} is empty.
     *
     * @return the value
     * @throws UnsupportedOperationException if {@link Option} is empty
     */
    @Nonnull
    public abstract T getValue();

    /**
     * Retrieve the value of a filled {@link Option} or null if {@link Option} is empty.
     *
     * @return null (empty) or value (filled)
     */
    public abstract T getValueOrNull();

    /**
     * Retrieve the value of a filled {@link Option} or the defaultValue if {@link Option} is empty
     *
     * @param defaultValue value to return if {@link Option} is empty
     * @return value or defaultValue
     */
    public abstract T getValueOr(T defaultValue);

    /**
     * Call consumer with my value if I am filled.  Otherwise do nothing.
     */
    public abstract void ifPresent(@Nonnull Consumer<? super T> consumer);

    /**
     * Call consumer with my value if I am filled.  Otherwise do nothing.
     */
    public abstract <E extends Exception> void ifPresentThrows(@Nonnull Proc1Throws<? super T, E> consumer)
        throws E;

    /**
     * Apply the transform function to my value (if I am filled) and return a new {@link Option} containing the result.
     * If I am empty return an empty {@link Option}.
     */
    @Nonnull
    public abstract <U> Option<U> map(@Nonnull Function<? super T, ? extends U> transforminator);

    /**
     * Apply the transform function to my value (if I am filled) and return a new {@link Option} containing the result.
     * If I am empty return an empty {@link Option}.
     */
    @Nonnull
    public abstract <U, E extends Exception> Option<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
        throws E;

    /**
     * Apply the transform function to my value (if I am filled) and return a new {@link Option} containing the result.
     * If I am empty return an empty {@link Option}.
     */
    @Nonnull
    public abstract <U> Option<U> flatMap(@Nonnull Function<? super T, Option<U>> transforminator);

    /**
     * Apply the transform function to my value (if I am filled) and return a new {@link Option} containing the result.
     * If I am empty return an empty {@link Option}.
     */
    @Nonnull
    public abstract <U, E extends Exception> Option<U> flatMapThrows(@Nonnull Func1Throws<? super T, Option<U>, E> transforminator)
        throws E;

    /**
     * Return my value if I am filled.  Otherwise return defaultValue.
     */
    public abstract T orElse(T defaultValue);

    /**
     * Return my value if I am filled.  Otherwise call supplier and return its result.
     */
    public abstract T orElseGet(@Nonnull Supplier<? extends T> supplier);

    /**
     * Return my value if I am filled.  Otherwise call supplier and throw its result.
     */
    @Nonnull
    public abstract <X extends Throwable> T orElseThrow(@Nonnull Supplier<? extends X> supplier)
        throws X;

    private static class Empty<T>
        extends Option<T>
    {
        private static final Option<?> EMPTY = new Empty<>();

        private Empty()
        {
        }

        @Nonnull
        @Override
        public Holder<T> toHolder()
        {
            return Holders.of();
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        @Override
        public boolean isNonEmpty()
        {
            return false;
        }

        @Override
        public boolean isFilled()
        {
            return false;
        }

        @Nonnull
        @Override
        public T getValue()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public T getValueOrNull()
        {
            return null;
        }

        @Override
        public T getValueOr(T defaultValue)
        {
            return defaultValue;
        }

        @Override
        public void ifPresent(@Nonnull Consumer<? super T> consumer)
        {
        }

        @Override
        public <E extends Exception> void ifPresentThrows(@Nonnull Proc1Throws<? super T, E> consumer)
            throws E
        {
        }

        @Nonnull
        @Override
        public <U> Option<U> map(@Nonnull Function<? super T, ? extends U> transforminator)
        {
            return of();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Option<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
            throws E
        {
            return of();
        }

        @Nonnull
        @Override
        public <U> Option<U> flatMap(@Nonnull Function<? super T, Option<U>> transforminator)
        {
            return of();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Option<U> flatMapThrows(@Nonnull Func1Throws<? super T, Option<U>, E> transforminator)
            throws E
        {
            return of();
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
    }

    private static class Filled<T>
        extends Option<T>
    {
        @Nonnull
        private final T value;

        private Filled(@Nonnull T value)
        {
            assert value != null;
            this.value = value;
        }

        @Nonnull
        @Override
        public Holder<T> toHolder()
        {
            return Holders.of(value);
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean isNonEmpty()
        {
            return true;
        }

        @Override
        public boolean isFilled()
        {
            return true;
        }

        @Nonnull
        @Override
        public T getValue()
        {
            return value;
        }

        @Override
        public T getValueOrNull()
        {
            return value;
        }

        @Override
        public T getValueOr(T defaultValue)
        {
            return value;
        }

        @Override
        public void ifPresent(@Nonnull Consumer<? super T> consumer)
        {
            consumer.accept(value);
        }

        @Override
        public <E extends Exception> void ifPresentThrows(@Nonnull Proc1Throws<? super T, E> consumer)
            throws E
        {
            consumer.apply(value);
        }

        @Nonnull
        @Override
        public <U> Option<U> map(@Nonnull Function<? super T, ? extends U> transforminator)
        {
            return of(transforminator.apply(value));
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Option<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
            throws E
        {
            return of(transforminator.apply(value));
        }

        @Nonnull
        @Override
        public <U> Option<U> flatMap(@Nonnull Function<? super T, Option<U>> transforminator)
        {
            return transforminator.apply(value);
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Option<U> flatMapThrows(@Nonnull Func1Throws<? super T, Option<U>, E> transforminator)
            throws E
        {
            return transforminator.apply(value);
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
    }
}
