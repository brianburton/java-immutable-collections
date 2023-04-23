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

import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;
import org.javimmutable.collections.serialization.HolderProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Used to handle cases when a value may or may not be present and, when needed, to eliminate
 * the use of null values.  The value can be null but a method is provided to change the instance to None if it is.
 * Provides a variety of utility methods to allow call chaining.
 */
public abstract class Holder<T>
    implements IStreamable<T>,
        Serializable
{
    /**
     * If this value is null returns None, otherwise returns this value.
     */
    @Nonnull
    public abstract Holder<T> notNull();

    /**
     * Produce a non-empty Holder.  If this Holder is non-empty it is returned.
     * Otherwise the noneMapping function is called to provide a value
     * for the result Holder.
     *
     * @param noneMapping produces value if this is empty
     * @return a non-empty Holder
     */
    @Nonnull
    public abstract Holder<T> map(@Nonnull Func0<? extends T> noneMapping);

    /**
     * Produce a Holder that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param someMapping maps this value to new value
     * @return a possibly empty Holder
     */
    @Nonnull
    public abstract <U> Holder<U> map(@Nonnull Function<? super T, ? extends U> someMapping);

    /**
     * Produce a Holder that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param someMapping maps this value to new value
     * @return a possibly empty Holder
     */
    @Nonnull
    public abstract <U> Holder<U> map(@Nonnull Func1<? super T, ? extends U> someMapping);

    /**
     * Produce a non-empty Holder.  If this is empty the noneMapping function is called
     * to provide a value.  Otherwise the someMapping function is called to produce a
     * new value based on this value.
     *
     * @param noneMapping produces value when this is empty
     * @param someMapping maps this value to new value
     * @return a non-empty Holder
     */
    @Nonnull
    public abstract <U> Holder<U> map(@Nonnull Func0<? extends U> noneMapping,
                                      @Nonnull Func1<? super T, ? extends U> someMapping);

    /**
     * Produce a non-empty Holder.  If this Holder is non-empty it is returned.
     * Otherwise the noneMapping function is called to provide a value
     * for the result Holder.
     *
     * @param noneMapping produces value if this is empty
     * @return a non-empty Holder
     */
    @Nonnull
    public abstract <E extends Exception> Holder<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
        throws E;

    /**
     * Produce a Holder that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param someMapping maps this value to new value
     * @return a possibly empty Holder
     */
    @Nonnull
    public abstract <U, E extends Exception> Holder<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
        throws E;

    /**
     * Produce a non-empty Holder.  If this is empty the noneMapping function is called
     * to provide a value.  Otherwise the someMapping function is called to produce a
     * new value based on this value.
     *
     * @param noneMapping produces value when this is empty
     * @param someMapping maps this value to new value
     * @return a non-empty Holder
     */
    @Nonnull
    public abstract <U, E extends Exception> Holder<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                                 @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
        throws E;

    /**
     * Produce a Holder based on this one.  If this is empty noneMapping is called
     * to produce a new Holder. Otherwise this is returned.
     *
     * @param noneMapping produce new Holder if this is empty
     * @return a Holder
     */
    @Nonnull
    public abstract Holder<T> flatMap(@Nonnull Func0<Holder<T>> noneMapping);

    /**
     * Produce a Holder based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Holder.  Otherwise this is returned.
     *
     * @param someMapping produce a new Holder from this value
     * @return a Holder
     */
    @Nonnull
    public abstract <A> Holder<A> flatMap(@Nonnull Func1<? super T, Holder<A>> someMapping);

    /**
     * Produce a Holder based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Holder.  Otherwise noneMapping is
     * called to produce a new Holder..
     *
     * @param noneMapping produce a new Holder when this is empty
     * @param someMapping produce a new Holder from this value
     * @return a Holder
     */
    @Nonnull
    public abstract <A> Holder<A> flatMap(@Nonnull Func0<Holder<A>> noneMapping,
                                          @Nonnull Func1<? super T, Holder<A>> someMapping);

    /**
     * Produce a Holder based on this one.  If this is empty noneMapping is called
     * to produce a new Holder. Otherwise this is returned.
     *
     * @param noneMapping produce new Holder if this is empty
     * @return a Holder
     */
    @Nonnull
    public abstract <E extends Exception> Holder<T> flatMapThrows(@Nonnull Func0Throws<Holder<T>, E> noneMapping)
        throws E;

    /**
     * Produce a Holder based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Holder.  Otherwise this is returned.
     *
     * @param someMapping produce a new Holder from this value
     * @return a Holder
     */
    @Nonnull
    public abstract <A, E extends Exception> Holder<A> flatMapThrows(@Nonnull Func1Throws<? super T, Holder<A>, E> someMapping)
        throws E;

    /**
     * Produce a Holder based on this one.  If this is non-empty its value is
     * passed to someMapping to produce a new Holder.  Otherwise noneMapping is
     * called to produce a new Holder..
     *
     * @param noneMapping produce a new Holder when this is empty
     * @param someMapping produce a new Holder from this value
     * @return a Holder
     */
    @Nonnull
    public abstract <A, E extends Exception> Holder<A> flatMapThrows(@Nonnull Func0Throws<Holder<A>, E> noneMapping,
                                                                     @Nonnull Func1Throws<? super T, Holder<A>, E> someMapping)
        throws E;

    /**
     * Returns this if this is non-empty and predicate returns true.
     * Otherwise an empty Holder is returned.
     *
     * @param predicate determines whether to accept this value
     * @return a Holder
     */
    @Nonnull
    public abstract Holder<T> select(@Nonnull Predicate<? super T> predicate);

    /**
     * Returns this if this is non-empty and predicate returns false.
     * Otherwise an empty Holder is returned.
     *
     * @param predicate determines whether to reject this value
     * @return a Holder
     */
    @Nonnull
    public abstract Holder<T> reject(@Nonnull Predicate<? super T> predicate);

    /**
     * Invokes noneAction if this is empty.
     *
     * @param noneAction action to call if this is empty
     * @return this
     */
    @Nonnull
    public abstract Holder<T> apply(@Nonnull Proc0 noneAction);

    /**
     * Invokes someAction with this value if this is non-empty.
     *
     * @param someAction action to call if this is non-empty
     * @return this
     */
    @Nonnull
    public abstract Holder<T> apply(@Nonnull Proc1<? super T> someAction);

    /**
     * Invokes noneAction if this is empty.
     *
     * @param noneAction action to call if this is empty
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> Holder<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
        throws E;

    /**
     * Invokes someAction with this value if this is non-empty.
     *
     * @param someAction action to call if this is non-empty
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> Holder<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
        throws E;

    /**
     * Gets this value.  Throws NoSuchElementException if this is empty.
     *
     * @return this value
     * @throws NoSuchElementException if this is empty
     */
    public abstract T unsafeGet();

    /**
     * Gets this value.  Calls noneExceptionMapping to get an exception
     * to throw if this is empty.
     *
     * @return this value
     * @throws E an exception produced by mapping if this is empty
     */
    public abstract <E extends Exception> T unsafeGet(@Nonnull Func0<E> noneExceptionMapping)
        throws E;

    /**
     * Gets this value.  If this is empty returns noneValue instead.
     *
     * @param noneValue value to return if this is empty
     */
    public abstract T get(T noneValue);

    /**
     * Gets this value.  If this is empty returns null.
     */
    public abstract T getOrNull();

    /**
     * Gets this value.  If this is empty returns result of calling noneMapping instead.
     *
     * @param noneMapping function to generate value to return if this is empty
     */
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
     * to obtain a return value.  Otherwise this value is passed to someMapping to
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
     * to obtain a return value.  Otherwise this value is passed to someMapping to
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

    private Holder()
    {
    }

    /**
     * Returns an empty Holder. All empty Maybes share a common instance.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Holder<T> none()
    {
        return (Holder<T>)None.NONE;
    }

    /**
     * Returns a Holder containing the value.  The value must be non-null.
     */
    @Nonnull
    public static <T> Holder<T> some(T value)
    {
        return new Some<>(value);
    }

    private static class None<T>
        extends Holder<T>
    {
        @SuppressWarnings("rawtypes")
        private static final None NONE = new None();

        private None()
        {
        }

        @Nonnull
        @Override
        public Holder<T> notNull()
        {
            return this;
        }

        @Nonnull
        @Override
        public Holder<T> map(@Nonnull Func0<? extends T> noneMapping)
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public <U> Holder<U> map(@Nonnull Function<? super T, ? extends U> someMapping)
        {
            return none();
        }

        @Nonnull
        @Override
        public <U> Holder<U> map(@Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return none();
        }

        @Nonnull
        @Override
        public <U> Holder<U> map(@Nonnull Func0<? extends U> noneMapping,
                                 @Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
            throws E
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Holder<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return none();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Holder<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                            @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return some(noneMapping.apply());
        }

        @Nonnull
        @Override
        public Holder<T> flatMap(@Nonnull Func0<Holder<T>> noneMapping)
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public <A> Holder<A> flatMap(@Nonnull Func1<? super T, Holder<A>> someMapping)
        {
            return none();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Holder<A> flatMapThrows(@Nonnull Func1Throws<? super T, Holder<A>, E> someMapping)
            throws E
        {
            return none();
        }

        @Nonnull
        @Override
        public <A> Holder<A> flatMap(@Nonnull Func0<Holder<A>> noneMapping,
                                     @Nonnull Func1<? super T, Holder<A>> someMapping)
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> flatMapThrows(@Nonnull Func0Throws<Holder<T>, E> noneMapping)
            throws E
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Holder<A> flatMapThrows(@Nonnull Func0Throws<Holder<A>, E> noneMapping,
                                                                @Nonnull Func1Throws<? super T, Holder<A>, E> someMapping)
            throws E
        {
            return noneMapping.apply();
        }

        @Nonnull
        @Override
        public Holder<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return none();
        }

        @Nonnull
        @Override
        public Holder<T> reject(@Nonnull Predicate<? super T> predicate)
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
        public Holder<T> apply(@Nonnull Proc0 noneAction)
        {
            noneAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public Holder<T> apply(@Nonnull Proc1<? super T> someAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
            throws E
        {
            noneAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
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

        @Nullable
        @Override
        public T getOrNull()
        {
            return null;
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
        public int hashCode()
        {
            return -1;
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
            return new HolderProxy(this);
        }
    }

    private static class Some<T>
        extends Holder<T>
    {
        private final T value;

        private Some(T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public Holder<T> notNull()
        {
            return value != null ? this : none();
        }

        @Nonnull
        @Override
        public Holder<T> map(@Nonnull Func0<? extends T> noneMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <U> Holder<U> map(@Nonnull Function<? super T, ? extends U> someMapping)
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U> Holder<U> map(@Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U> Holder<U> map(@Nonnull Func0<? extends U> noneMapping,
                                 @Nonnull Func1<? super T, ? extends U> someMapping)
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> mapThrows(@Nonnull Func0Throws<? extends T, E> noneMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Holder<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Holder<U> mapThrows(@Nonnull Func0Throws<? extends U, E> noneMapping,
                                                            @Nonnull Func1Throws<? super T, ? extends U, E> someMapping)
            throws E
        {
            return some(someMapping.apply(value));
        }

        @Nonnull
        @Override
        public Holder<T> flatMap(@Nonnull Func0<Holder<T>> noneMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <A> Holder<A> flatMap(@Nonnull Func1<? super T, Holder<A>> someMapping)
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> flatMapThrows(@Nonnull Func0Throws<Holder<T>, E> noneMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Holder<A> flatMapThrows(@Nonnull Func1Throws<? super T, Holder<A>, E> someMapping)
            throws E
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A> Holder<A> flatMap(@Nonnull Func0<Holder<A>> noneMapping,
                                     @Nonnull Func1<? super T, Holder<A>> someMapping)
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Holder<A> flatMapThrows(@Nonnull Func0Throws<Holder<A>, E> noneMapping,
                                                                @Nonnull Func1Throws<? super T, Holder<A>, E> someMapping)
            throws E
        {
            return someMapping.apply(value);
        }

        @Nonnull
        @Override
        public Holder<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? this : none();
        }

        @Nonnull
        @Override
        public Holder<T> reject(@Nonnull Predicate<? super T> predicate)
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
        public Holder<T> apply(@Nonnull Proc0 noneAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public Holder<T> apply(@Nonnull Proc1<? super T> someAction)
        {
            someAction.apply(value);
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> applyThrows(@Nonnull Proc0Throws<E> noneAction)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Holder<T> applyThrows(@Nonnull Proc1Throws<? super T, E> someAction)
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

        @Nullable
        @Override
        public T getOrNull()
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
            Object otherValue = ((Some)obj).value;
            if (value == null) {
                return otherValue == null;
            }
            return value.equals(otherValue);
        }

        @Override
        public String toString()
        {
            return "Some(" + value + ")";
        }

        private Object writeReplace()
        {
            return new HolderProxy(this);
        }
    }
}
