///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;
import org.javimmutable.collections.serialization.MaybeProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Similar to Holder but implemented as a concrete ADT rather than an interface.
 * Used to handle cases when a value may or may not be present and to eliminate
 * the use of null values.  Unlike a Holder, the value of a Maybe can never be null.
 * Provides a variety of utility methods to allow call chaining.
 */
public abstract class Maybe<T>
    implements IterableStreamable<T>,
               Serializable
{
    /**
     * Produce a non-empty Maybe.  If this Maybe is non-empty it is returned.
     * Otherwise the noneMapping function is called to provide a value
     * for the result Maybe.
     *
     * @param noneMapping produces value if this is empty
     * @return a non-empty Maybe
     */
    @Nonnull
    public abstract <U> Maybe<T> map(@Nonnull Func0<? extends T> noneMapping);

    /**
     * Produce a Maybe that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param someMapping maps this value to new value
     * @return a possibly empty Maybe
     */
    @Nonnull
    public abstract <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> someMapping);

    /**
     * Produce a non-empty Maybe.  If this is empty the noneMapping function is called
     * to provide a value.  Otherwise the someMapping function is called to produce a
     * new value based on this value.
     *
     * @param noneMapping produces value when this is empty
     * @param someMapping maps this value to new value
     * @return a non-empty Maybe
     */
    @Nonnull
    public abstract <U> Maybe<U> map(@Nonnull Func0<? extends U> noneMapping,
                                     @Nonnull Func1<? super T, ? extends U> someMapping);

    /**
     * Produce a non-empty Maybe.  If this Maybe is non-empty it is returned.
     * Otherwise the noneMapping function is called to provide a value
     * for the result Maybe.
     *
     * @param noneMapping produces value if this is empty
     * @return a non-empty Maybe
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
        throws E;

    /**
     * Produce a Maybe that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param someMapping maps this value to new value
     * @return a possibly empty Maybe
     */
    @Nonnull
    public abstract <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
        throws E;

    /**
     * Produce a non-empty Maybe.  If this is empty the noneMapping function is called
     * to provide a value.  Otherwise the someMapping function is called to produce a
     * new value based on this value.
     *
     * @param noneMapping produces value when this is empty
     * @param someMapping maps this value to new value
     * @return a non-empty Maybe
     */
    @Nonnull
    public abstract <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                                @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
        throws E;

    /**
     * Produce a Maybe based on this one.  If this is empty noneMapping is called
     * to produce a new Maybe. Otherwise this is returned.
     *
     * @param noneMapping produce new Maybe if this is empty
     * @return a Maybe
     */
    @Nonnull
    public abstract Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> noneMapping);

    /**
     * Produce a Maybe based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Maybe.  Otherwise this is returned.
     *
     * @param someMapping produce a new Maybe from this value
     * @return a Maybe
     */
    @Nonnull
    public abstract <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> someMapping);

    /**
     * Produce a Maybe based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Maybe.  Otherwise noneMapping is
     * called to produce a new Maybe..
     *
     * @param noneMapping produce a new Maybe when this is empty
     * @param someMapping produce a new Maybe from this value
     * @return a Maybe
     */
    @Nonnull
    public abstract <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> noneMapping,
                                         @Nonnull Func1<? super T, Maybe<A>> someMapping);

    /**
     * Produce a Maybe based on this one.  If this is empty noneMapping is called
     * to produce a new Maybe. Otherwise this is returned.
     *
     * @param noneMapping produce new Maybe if this is empty
     * @return a Maybe
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> noneMapping)
        throws E;

    /**
     * Produce a Maybe based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Maybe.  Otherwise this is returned.
     *
     * @param someMapping produce a new Maybe from this value
     * @return a Maybe
     */
    @Nonnull
    public abstract <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
        throws E;

    /**
     * Produce a Maybe based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Maybe.  Otherwise noneMapping is
     * called to produce a new Maybe..
     *
     * @param noneMapping produce a new Maybe when this is empty
     * @param someMapping produce a new Maybe from this value
     * @return a Maybe
     */
    @Nonnull
    public abstract <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> noneMapping,
                                                                    @Nonnull Func1Throws<? super T, Maybe<A>, E> someMapping)
        throws E;

    /**
     * Returns this is this is non-empty and predicate returns true.
     * Otherwise an empty Maybe is returned.
     *
     * @param predicate determines whether to accept this value
     * @return a Maybe
     */
    @Nonnull
    public abstract Maybe<T> select(@Nonnull Predicate<? super T> predicate);

    /**
     * Returns this is this is non-empty and predicate returns false.
     * Otherwise an empty Maybe is returned.
     *
     * @param predicate determines whether to reject this value
     * @return a Maybe
     */
    @Nonnull
    public abstract Maybe<T> reject(@Nonnull Predicate<? super T> predicate);

    /**
     * Invokes noneAction is this is empty.
     *
     * @param noneAction action to call if this is empty
     * @return this
     */
    @Nonnull
    public abstract Maybe<T> apply(@Nonnull Proc0 noneAction);

    /**
     * Invokes someAction with this value is this is non-empty.
     *
     * @param someAction action to call if this is non-empty
     * @return this
     */
    @Nonnull
    public abstract Maybe<T> apply(@Nonnull Proc1<? super T> someAction);

    /**
     * Invokes noneAction is this is empty.
     *
     * @param noneAction action to call if this is empty
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
        throws E;

    /**
     * Invokes someAction with this value is this is non-empty.
     *
     * @param someAction action to call if this is non-empty
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
        throws E;

    /**
     * Gets this value.  Throws NoSuchElementException if this is empty.
     *
     * @return this value
     * @throw NoSuchElementException if this is empty
     */
    @Nonnull
    public abstract T unsafeGet();

    /**
     * Gets this value.  Calls noneExceptionMapping to get an exception
     * to throw if this is empty.
     *
     * @return this value
     * @throw an exception produced by mapping if this is empty
     */
    @Nonnull
    public abstract <E extends Exception> T unsafeGet(@Nonnull Func0<E> noneExceptionMapping)
        throws E;

    /**
     * Gets this value.  If this is empty returns noneValue instead.
     *
     * @param noneValue value to return if this is empty
     */
    @Nonnull
    public abstract T get(@Nonnull T noneValue);

    /**
     * Gets this value.  If this is empty returns result of calling noneMapping instead.
     *
     * @param noneMapping function to generate value to return if this is empty
     */
    @Nonnull
    public abstract T getOr(@Nonnull Func0<? extends T> noneMapping);

    /**
     * Gets a value based on this value.  If this is empty noneValue is returned.
     * Otherwise this value is passed to someMapping to obtain a return value.
     *
     * @param noneValue   value to return if this is empty
     * @param someMapping function to map this value into return value
     * @return a value
     */
    public abstract <U> U match(U noneValue,
                                @Nonnull Func1<? super T, U> someMapping);

    /**
     * Gets a value based on this value.  If this is empty noneMapping is called
     * to obain a return value.  Otherwise this value is passed to someMapping to
     * obtain a return value.
     *
     * @param noneMapping function to produce a value to return if this is empty
     * @param someMapping function to map this value into return value
     * @return a value
     */
    public abstract <U> U matchOr(@Nonnull Func0<U> noneMapping,
                                  @Nonnull Func1<? super T, U> someMapping);

    /**
     * Gets a value based on this value.  If this is empty noneValue is returned.
     * Otherwise this value is passed to someMapping to obtain a return value.
     *
     * @param noneValue   value to return if this is empty
     * @param someMapping function to map this value into return value
     * @return a value
     */
    public abstract <U, E extends Exception> U matchThrows(U noneValue,
                                                           @Nonnull Func1Throws<? super T, U, E> someMapping)
        throws E;

    /**
     * Gets a value based on this value.  If this is empty noneMapping is called
     * to obain a return value.  Otherwise this value is passed to someMapping to
     * obtain a return value.
     *
     * @param noneMapping function to produce a value to return if this is empty
     * @param someMapping function to map this value into return value
     * @return a value
     */
    public abstract <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> noneMapping,
                                                             @Nonnull Func1Throws<? super T, U, E> someMapping)
        throws E;

    /**
     * Returns true if this has no value
     */
    public abstract boolean isNone();

    /**
     * Returns true if this has a value
     */
    public abstract boolean isSome();

    /**
     * Returns empty holder if this is empty, otherwise a holder containing this value.
     */
    @Nonnull
    public abstract Holder<T> toHolder();

    private Maybe()
    {
    }

    /**
     * Returns an empty Maybe. All empty Maybes share a common instance.
     */
    @Nonnull
    public static <T> Maybe<T> of()
    {
        return none();
    }

    /**
     * Returns an empty Maybe if value is null, otherwise a Maybe containing
     * the value is returned.
     */
    @Nonnull
    public static <T> Maybe<T> of(@Nullable T value)
    {
        return value != null ? some(value) : none();
    }

    /**
     * Returns an empty Maybe if value is null, otherwise a Maybe containing
     * the value is returned.
     */
    @Nonnull
    public static <T> Maybe<T> maybe(@Nullable T value)
    {
        return value != null ? some(value) : none();
    }

    /**
     * Returns an empty Maybe. All empty Maybes share a common instance.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Maybe<T> none()
    {
        return (Maybe<T>)None.NONE;
    }

    /**
     * Returns a Maybe containing the value.  The value must be non-null.
     */
    @Nonnull
    public static <T> Maybe<T> some(@Nonnull T value)
    {
        //noinspection ConstantConditions
        assert value != null;
        return new Some<>(value);
    }

    /**
     * Returns a Maybe containing the first value of the collection.  If the collection
     * is empty or the first value is null an empty Maybe is returned.
     */
    @Nonnull
    public static <T> Maybe<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        return i.hasNext() ? maybe(i.next()) : none();
    }

    /**
     * Returns a Maybe containing the first non-null value of the collection
     * for which the predicate returns true.  If the collection
     * is empty, there are no non-null values, or predicate always
     * returns false an empty Maybe is returned.
     */
    @Nonnull
    public static <T> Maybe<T> first(@Nonnull Iterable<? extends T> collection,
                                     @Nonnull Func1<? super T, Boolean> predicate)
    {
        for (T value : collection) {
            if (value != null && predicate.apply(value)) {
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

        private Object writeReplace()
        {
            return new MaybeProxy(this);
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

        private Object writeReplace()
        {
            return new MaybeProxy(this);
        }
    }
}
