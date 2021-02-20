package org.javimmutable.collections;

import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public abstract class Option<T>
    implements IterableStreamable<T>
{
    @Nonnull
    public abstract <U> Option<U> map(@Nonnull Func1<? super T, ? extends U> transforminator);

    @Nonnull
    public abstract <U, E extends Exception> Option<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
        throws E;

    @Nonnull
    public abstract <A> Option<A> flatMap(@Nonnull Func1<? super T, Option<A>> transforminator);

    @Nonnull
    public abstract <A, E extends Exception> Option<A> flatMapThrows(@Nonnull Func1Throws<? super T, Option<A>, E> transforminator)
        throws E;

    @Nonnull
    public abstract Option<T> select(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    public abstract Option<T> reject(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    public abstract Option<T> apply(@Nonnull Proc1<? super T> action);

    @Nonnull
    public abstract <E extends Exception> Option<T> applyThrows(@Nonnull Proc1Throws<? super T, E> action)
        throws E;

    @Nonnull
    public abstract T unsafeGet();

    @Nonnull
    public abstract <E extends Exception> T unsafeGet(@Nonnull Func0<E> factory)
        throws E;

    @Nonnull
    public abstract T get(@Nonnull T defaultValue);

    @Nonnull
    public abstract T getOr(@Nonnull Func0<? extends T> defaultValue);

    public abstract <U> U match(U defaultValue,
                                @Nonnull Func1<? super T, U> transforminator);

    public abstract <U> U matchOr(@Nonnull Func0<U> defaultValue,
                                  @Nonnull Func1<? super T, U> transforminator);

    public abstract <U, E extends Exception> U matchThrows(U defaultValue,
                                                           @Nonnull Func1Throws<? super T, U, E> transforminator)
        throws E;

    public abstract <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> defaultValue,
                                                             @Nonnull Func1Throws<? super T, U, E> transforminator)
        throws E;

    public abstract boolean isNone();

    public abstract boolean isSome();

    @Nonnull
    public abstract Holder<T> toHolder();

    private Option()
    {
    }

    @Nonnull
    public static <T> Option<T> option(@Nullable T value)
    {
        return value != null ? some(value) : none();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Option<T> none()
    {
        return (Option<T>)None.NONE;
    }

    @Nonnull
    public static <T> Option<T> some(@Nonnull T value)
    {
        return new Some<>(value);
    }

    @Nonnull
    public static <T> Option<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        return i.hasNext() ? some(i.next()) : none();
    }

    @Nonnull
    public static <T> Option<T> first(@Nonnull Iterable<? extends T> collection,
                                      @Nonnull Func1<? super T, Boolean> predicate)
    {
        for (T value : collection) {
            if (predicate.apply(value)) {
                return some(value);
            }
        }
        return none();
    }

    private static class None<T>
        extends Option<T>
    {
        @SuppressWarnings("rawtypes")
        private static final None NONE = new None();

        private None()
        {
        }

        @Nonnull
        @Override
        public <U> Option<U> map(@Nonnull Func1<? super T, ? extends U> transforminator)
        {
            return none();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Option<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
            throws E
        {
            return none();
        }

        @Nonnull
        @Override
        public <A> Option<A> flatMap(@Nonnull Func1<? super T, Option<A>> transforminator)
        {
            return none();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Option<A> flatMapThrows(@Nonnull Func1Throws<? super T, Option<A>, E> transforminator)
            throws E
        {
            return none();
        }

        @Nonnull
        @Override
        public Option<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return none();
        }

        @Nonnull
        @Override
        public Option<T> reject(@Nonnull Predicate<? super T> predicate)
        {
            return none();
        }

        @Nonnull
        @Override
        public SplitableIterator<T> iterator()
        {
            return EmptyIterator.of();
        }

        @Override
        public int getSpliteratorCharacteristics()
        {
            return StreamConstants.SPLITERATOR_UNORDERED;
        }

        @Nonnull
        @Override
        public Option<T> apply(@Nonnull Proc1<? super T> action)
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Option<T> applyThrows(@Nonnull Proc1Throws<? super T, E> action)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public T unsafeGet()
        {
            throw new NoSuchElementException();
        }

        @Nonnull
        @Override
        public <E extends Exception> T unsafeGet(@Nonnull Func0<E> factory)
            throws E
        {
            throw factory.apply();
        }

        @Nonnull
        @Override
        public T get(@Nonnull T defaultValue)
        {
            return defaultValue;
        }

        @Nonnull
        @Override
        public T getOr(@Nonnull Func0<? extends T> defaultValue)
        {
            return defaultValue.apply();
        }

        @Override
        public <U> U match(U defaultValue,
                           @Nonnull Func1<? super T, U> transforminator)
        {
            return defaultValue;
        }

        @Override
        public <U> U matchOr(@Nonnull Func0<U> defaultValue,
                             @Nonnull Func1<? super T, U> transforminator)
        {
            return defaultValue.apply();
        }

        @Override
        public <U, E extends Exception> U matchThrows(U defaultValue,
                                                      @Nonnull Func1Throws<? super T, U, E> transforminator)
            throws E
        {
            return defaultValue;
        }

        @Override
        public <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> defaultValue,
                                                        @Nonnull Func1Throws<? super T, U, E> transforminator)
            throws E
        {
            return defaultValue.apply();
        }

        @Override
        public boolean isNone()
        {
            return true;
        }

        @Override
        public boolean isSome()
        {
            return false;
        }

        @Override
        public @Nonnull
        Holder<T> toHolder()
        {
            return Holders.holder();
        }

        @Override
        public int hashCode()
        {
            return 0;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof None;
        }

        @Override
        public String toString()
        {
            return "None";
        }
    }

    private static class Some<T>
        extends Option<T>
    {
        private final T value;

        private Some(@Nonnull T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public <U> Option<U> map(@Nonnull Func1<? super T, ? extends U> transforminator)
        {
            return some(transforminator.apply(value));
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Option<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> transforminator)
            throws E
        {
            return some(transforminator.apply(value));
        }

        @Nonnull
        @Override
        public <A> Option<A> flatMap(@Nonnull Func1<? super T, Option<A>> transforminator)
        {
            return transforminator.apply(value);
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Option<A> flatMapThrows(@Nonnull Func1Throws<? super T, Option<A>, E> transforminator)
            throws E
        {
            return transforminator.apply(value);
        }

        @Nonnull
        @Override
        public Option<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? this : none();
        }

        @Nonnull
        @Override
        public Option<T> reject(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? none() : this;
        }

        @Nonnull
        @Override
        public SplitableIterator<T> iterator()
        {
            return SingleValueIterator.of(value);
        }

        @Override
        public int getSpliteratorCharacteristics()
        {
            return StreamConstants.SPLITERATOR_UNORDERED;
        }

        @Nonnull
        @Override
        public Option<T> apply(@Nonnull Proc1<? super T> action)
        {
            action.apply(value);
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Option<T> applyThrows(@Nonnull Proc1Throws<? super T, E> action)
            throws E
        {
            action.apply(value);
            return this;
        }

        @Nonnull
        @Override
        public T unsafeGet()
        {
            return value;
        }

        @Nonnull
        @Override
        public <E extends Exception> T unsafeGet(@Nonnull Func0<E> factory)
            throws E
        {
            return value;
        }

        @Nonnull
        @Override
        public T get(@Nonnull T defaultValue)
        {
            return value;
        }

        @Nonnull
        @Override
        public T getOr(@Nonnull Func0<? extends T> defaultValue)
        {
            return value;
        }

        @Override
        public <U> U match(U defaultValue,
                           @Nonnull Func1<? super T, U> transforminator)
        {
            return transforminator.apply(value);
        }

        @Override
        public <U> U matchOr(@Nonnull Func0<U> defaultValue,
                             @Nonnull Func1<? super T, U> transforminator)
        {
            return transforminator.apply(value);
        }

        @Override
        public <U, E extends Exception> U matchThrows(U defaultValue,
                                                      @Nonnull Func1Throws<? super T, U, E> transforminator)
            throws E
        {
            return transforminator.apply(value);
        }

        @Override
        public <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> defaultValue,
                                                        @Nonnull Func1Throws<? super T, U, E> transforminator)
            throws E
        {
            return transforminator.apply(value);
        }

        @Override
        public boolean isNone()
        {
            return false;
        }

        @Override
        public boolean isSome()
        {
            return true;
        }

        @Override
        public @Nonnull
        Holder<T> toHolder()
        {
            return Holders.holder(value);
        }

        @Override
        public int hashCode()
        {
            return value.hashCode();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj)
        {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Some)) {
                return false;
            }
            return value.equals(((Some)obj).value);
        }

        @Override
        public String toString()
        {
            return "Some(" + value + ")";
        }
    }
}
