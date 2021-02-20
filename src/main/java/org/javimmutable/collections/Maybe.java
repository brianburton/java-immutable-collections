package org.javimmutable.collections;

import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public abstract class Maybe<T>
    implements IterableStreamable<T>
{
    @Nonnull
    public abstract <U> Maybe<T> map(@Nonnull Func0<? extends T> noneMapping);

    @Nonnull
    public abstract <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> someMapping);

    @Nonnull
    public abstract <U> Maybe<U> map(@Nonnull Func0<? extends U> noneMapping,
                                     @Nonnull Func1<? super T, ? extends U> someMapping);

    @Nonnull
    public abstract <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
        throws E;

    @Nonnull
    public abstract <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
        throws E;

    @Nonnull
    public abstract <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                                @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
        throws E;

    @Nonnull
    public abstract Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> noneMapping);

    @Nonnull
    public abstract <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> someMapping);

    @Nonnull
    public abstract <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> noneMapping,
                                         @Nonnull Func1<? super T, Maybe<A>> someMapping);

    @Nonnull
    public abstract <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> noneMapping)
        throws E;

    @Nonnull
    public abstract <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
        throws E;

    @Nonnull
    public abstract <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> noneMapping,
                                                                    @Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
        throws E;

    @Nonnull
    public abstract Maybe<T> select(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    public abstract Maybe<T> reject(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    public abstract Maybe<T> apply(@Nonnull Proc0 noneAction);

    @Nonnull
    public abstract Maybe<T> apply(@Nonnull Proc1<? super T> someAction);

    @Nonnull
    public abstract <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
        throws E;

    @Nonnull
    public abstract <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
        throws E;

    @Nonnull
    public abstract T unsafeGet();

    @Nonnull
    public abstract <E extends Exception> T unsafeGet(@Nonnull Func0<E> noneExceptionMapping)
        throws E;

    @Nonnull
    public abstract T get(@Nonnull T noneValue);

    @Nonnull
    public abstract T getOr(@Nonnull Func0<? extends T> noneMapping);

    public abstract <U> U match(U noneValue,
                                @Nonnull Func1<? super T, U> someMapping);

    public abstract <U> U matchOr(@Nonnull Func0<U> noneMapping,
                                  @Nonnull Func1<? super T, U> someMapping);

    public abstract <U, E extends Exception> U matchThrows(U noneValue,
                                                           @Nonnull Func1Throws<? super T, U, E> someMapping)
        throws E;

    public abstract <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> noneMapping,
                                                             @Nonnull Func1Throws<? super T, U, E> someMapping)
        throws E;

    public abstract boolean isNone();

    public abstract boolean isSome();

    @Nonnull
    public abstract Holder<T> toHolder();

    private Maybe()
    {
    }

    @Nonnull
    public static <T> Maybe<T> of()
    {
        return none();
    }

    @Nonnull
    public static <T> Maybe<T> of(@Nullable T value)
    {
        return value != null ? some(value) : none();
    }

    @Nonnull
    public static <T> Maybe<T> maybe(@Nullable T value)
    {
        return value != null ? some(value) : none();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Maybe<T> none()
    {
        return (Maybe<T>)None.NONE;
    }

    @Nonnull
    public static <T> Maybe<T> some(@Nonnull T value)
    {
        return new Some<>(value);
    }

    @Nonnull
    public static <T> Maybe<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        return i.hasNext() ? some(i.next()) : none();
    }

    @Nonnull
    public static <T> Maybe<T> first(@Nonnull Iterable<? extends T> collection,
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
        extends Maybe<T>
    {
        @SuppressWarnings("rawtypes")
        private static final None NONE = new None();

        private None()
        {
        }

        @Nonnull
        @Override
        public <U> Maybe<T> map(@Nonnull Func0<? extends T> noneMapping)
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return none();
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func0<? extends U> noneMapping,
                                @Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
            throws E
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return none();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                           @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> noneMapping)
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> someMapping)
        {
            return none();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
            throws E
        {
            return none();
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> noneMapping,
                                    @Nonnull Func1<? super T, Maybe<A>> someMapping)
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> noneMapping)
            throws E
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> noneMapping,
                                                               @Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
            throws E
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public Maybe<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return none();
        }

        @Nonnull
        @Override
        public Maybe<T> reject(@Nonnull Predicate<? super T> predicate)
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
        public Maybe<T> apply(@Nonnull Proc0 noneAction)
        {
            noneAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public Maybe<T> apply(@Nonnull Proc1<? super T> someAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
            throws E
        {
            noneAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
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
        public <E extends Exception> T unsafeGet(@Nonnull Func0<E> noneExceptionMapping)
            throws E
        {
            throw noneExceptionMapping.apply();
        }

        @Nonnull
        @Override
        public T get(@Nonnull T noneValue)
        {
            return noneValue;
        }

        @Nonnull
        @Override
        public T getOr(@Nonnull Func0<? extends T> noneMapping)
        {
            return noneMapping.apply();
        }

        @Override
        public <U> U match(U noneValue,
                           @Nonnull Func1<? super T, U> someMapping)
        {
            return noneValue;
        }

        @Override
        public <U> U matchOr(@Nonnull Func0<U> noneMapping,
                             @Nonnull Func1<? super T, U> someMapping)
        {
            return noneMapping.apply();
        }

        @Override
        public <U, E extends Exception> U matchThrows(U noneValue,
                                                      @Nonnull Func1Throws<? super T, U, E> someMapping)
            throws E
        {
            return noneValue;
        }

        @Override
        public <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> noneMapping,
                                                        @Nonnull Func1Throws<? super T, U, E> someMapping)
            throws E
        {
            return noneMapping.apply();
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
        extends Maybe<T>
    {
        private final T value;

        private Some(@Nonnull T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public <U> Maybe<T> map(@Nonnull Func0<? extends T> noneMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func0<? extends U> noneMapping,
                                @Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                           @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> noneMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> someMapping)
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> noneMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
            throws E
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> noneMapping,
                                    @Nonnull Func1<? super T, Maybe<A>> someMapping)
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> noneMapping,
                                                               @Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
            throws E
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public Maybe<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? this : none();
        }

        @Nonnull
        @Override
        public Maybe<T> reject(@Nonnull Predicate<? super T> predicate)
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
        public Maybe<T> apply(@Nonnull Proc0 noneAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public Maybe<T> apply(@Nonnull Proc1<? super T> someAction)
        {
            someAction.apply(value);
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
            throws E
        {
            someAction.apply(value);
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
        public <E extends Exception> T unsafeGet(@Nonnull Func0<E> noneExceptionMapping)
            throws E
        {
            return value;
        }

        @Nonnull
        @Override
        public T get(@Nonnull T noneValue)
        {
            return value;
        }

        @Nonnull
        @Override
        public T getOr(@Nonnull Func0<? extends T> noneMapping)
        {
            return value;
        }

        @Override
        public <U> U match(U noneValue,
                           @Nonnull Func1<? super T, U> someMapping)
        {
            return someMapping.apply(value);
        }

        @Override
        public <U> U matchOr(@Nonnull Func0<U> noneMapping,
                             @Nonnull Func1<? super T, U> someMapping)
        {
            return someMapping.apply(value);
        }

        @Override
        public <U, E extends Exception> U matchThrows(U noneValue,
                                                      @Nonnull Func1Throws<? super T, U, E> someMapping)
            throws E
        {
            return someMapping.apply(value);
        }

        @Override
        public <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> noneMapping,
                                                        @Nonnull Func1Throws<? super T, U, E> someMapping)
            throws E
        {
            return someMapping.apply(value);
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
