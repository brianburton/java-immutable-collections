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

package org.javimmutable.collection;

import org.javimmutable.collection.common.StreamConstants;
import org.javimmutable.collection.iterators.EmptyIterator;
import org.javimmutable.collection.iterators.SingleValueIterator;
import org.javimmutable.collection.serialization.MaybeProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Used to handle cases when a value may or may not exist.  The value of a {@link Maybe} can
 * be null but the {@link Maybe#notNull} method can be used to
 * change a null value into an empty {@link NotNull} when desired.
 * Alternatively, {@link Maybe#nullToEmpty} can be used to convert a null
 * value into an empty {@link Maybe}.
 * Provides a variety of utility methods to allow call chaining.
 */
public abstract class Maybe<T>
    implements IStreamable<T>,
               Serializable
{
    private Maybe()
    {
    }

    /**
     * Returns a {@link Maybe} with no value. All empty {@link Maybe}s share a common instance.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Maybe<T> empty()
    {
        return Empty.EMPTY;
    }

    /**
     * Returns a {@link Maybe} containing the value.  Null is a valid value.
     */
    @Nonnull
    public static <T> Maybe<T> of(T value)
    {
        return new Full<>(value);
    }

    /**
     * Determine if the object is an instance of the specified Class or a subclass.
     * If the object is null, returns a {@link Maybe} containing null.
     * If the object is not null but not of the correct class, returns an empty Holder.
     * Otherwise returns a Holder containing the value cast to the target type.
     *
     * @param klass       class to cast the object to
     * @param valueOrNull object to be case
     * @param <T>         type of the class
     * @return a {@link Maybe}
     */
    public static <T> Maybe<T> cast(@Nonnull Class<T> klass,
                                    @Nullable Object valueOrNull)
    {
        if (valueOrNull == null) {
            return of(null);
        } else if (klass.isInstance(valueOrNull)) {
            return of(klass.cast(valueOrNull));
        } else {
            return empty();
        }
    }

    /**
     * Returns a {@link Maybe} containing the first value of the collection.  If the collection
     * is empty an empty {@link Maybe} is returned.
     */
    @Nonnull
    public static <T> Maybe<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        if (i.hasNext()) {
            T value = i.next();
            return of(value);
        } else {
            return empty();
        }
    }

    /**
     * Returns a {@link Maybe} containing the first value of the collection
     * for which the predicate returns true.  If the collection
     * is empty or predicate always returns false an empty {@link Maybe} is returned.
     */
    @Nonnull
    public static <T> Maybe<T> first(@Nonnull Iterable<? extends T> collection,
                                     @Nonnull Func1<? super T, Boolean> predicate)
    {
        for (T value : collection) {
            if (predicate.apply(value)) {
                return of(value);
            }
        }
        return empty();
    }

    /**
     * If this empty or has a null value returns {@link NotNull#empty}, otherwise returns
     * a {@link NotNull} containing the same value as this.
     */
    @Nonnull
    public abstract NotNull<T> notNull();

    /**
     * If this empty or has a null value returns {@link #empty}, otherwise returns this.
     */
    @Nonnull
    public abstract Maybe<T> nullToEmpty();

    /**
     * Produce a full {@link Maybe}.  If this {@link Maybe} is full it is returned.
     * Otherwise the absentMapping function is called to provide a value
     * for the result {@link Maybe}.
     *
     * @param absentMapping produces value if this is empty
     * @return a full {@link Maybe}
     */
    @Nonnull
    public abstract Maybe<T> map(@Nonnull Func0<? extends T> absentMapping);

    /**
     * Produce a {@link Maybe} that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param presentMapping maps this value to new value
     * @return a possibly empty {@link Maybe}
     */
    @Nonnull
    public abstract <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> presentMapping);

    /**
     * Produce a full {@link Maybe}.  If this is empty the absentMapping function is called
     * to provide a value.  Otherwise the presentMapping function is called to produce a
     * new value based on this value.
     *
     * @param absentMapping  produces value when this is empty
     * @param presentMapping maps this value to new value
     * @return a full {@link Maybe}
     */
    @Nonnull
    public abstract <U> Maybe<U> map(@Nonnull Func0<? extends U> absentMapping,
                                     @Nonnull Func1<? super T, ? extends U> presentMapping);

    /**
     * Produce a full {@link Maybe}.  If this {@link Maybe} is full it is returned.
     * Otherwise the absentMapping function is called to provide a value
     * for the result {@link Maybe}.
     *
     * @param absentMapping produces value if this is empty
     * @return a full {@link Maybe}
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> absentMapping)
        throws E;

    /**
     * Produce a {@link Maybe} that is empty if this is empty or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param presentMapping maps this value to new value
     * @return a possibly empty {@link Maybe}
     */
    @Nonnull
    public abstract <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
        throws E;

    /**
     * Produce a full {@link Maybe}.  If this is empty the absentMapping function is called
     * to provide a value.  Otherwise the presentMapping function is called to produce a
     * new value based on this value.
     *
     * @param absentMapping  produces value when this is empty
     * @param presentMapping maps this value to new value
     * @return a full {@link Maybe}
     */
    @Nonnull
    public abstract <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> absentMapping,
                                                                @Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
        throws E;

    /**
     * Produce a {@link Maybe} based on this one.  If this is empty absentMapping is called
     * to produce a new {@link Maybe}. Otherwise this is returned.
     *
     * @param absentMapping produce new {@link Maybe} if this is empty
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> absentMapping);

    /**
     * Produce a {@link Maybe} based on this one.  If this is full its value is
     * passed to presentMapping to produce a new {@link Maybe}.  Otherwise this is returned.
     *
     * @param presentMapping produce a new {@link Maybe} from this value
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> presentMapping);

    /**
     * Produce a {@link Maybe} based on this one.  If this is full its value is
     * passed to presentMapping to produce a new {@link Maybe}.  Otherwise absentMapping is
     * called to produce a new {@link Maybe}..
     *
     * @param absentMapping  produce a new {@link Maybe} when this is empty
     * @param presentMapping produce a new {@link Maybe} from this value
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> absentMapping,
                                         @Nonnull Func1<? super T, Maybe<A>> presentMapping);

    /**
     * Produce a {@link Maybe} based on this one.  If this is empty absentMapping is called
     * to produce a new {@link Maybe}. Otherwise this is returned.
     *
     * @param absentMapping produce new {@link Maybe} if this is empty
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> absentMapping)
        throws E;

    /**
     * Produce a {@link Maybe} based on this one.  If this is full its value is
     * passed to presentMapping to produce a new {@link Maybe}.  Otherwise this is returned.
     *
     * @param presentMapping produce a new {@link Maybe} from this value
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> presentMapping)
        throws E;

    /**
     * Produce a {@link Maybe} based on this one.  If this is full its value is
     * passed to presentMapping to produce a new {@link Maybe}.  Otherwise absentMapping is
     * called to produce a new {@link Maybe}..
     *
     * @param absentMapping  produce a new {@link Maybe} when this is empty
     * @param presentMapping produce a new {@link Maybe} from this value
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> absentMapping,
                                                                    @Nonnull Func1Throws<? super T, Maybe<A>, E> presentMapping)
        throws E;

    /**
     * Returns this if this is full and predicate returns true.
     * Otherwise an empty {@link Maybe} is returned.
     *
     * @param predicate determines whether to accept this value
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract Maybe<T> select(@Nonnull Predicate<? super T> predicate);

    /**
     * Returns this if this is full and predicate returns false.
     * Otherwise an empty {@link Maybe} is returned.
     *
     * @param predicate determines whether to reject this value
     * @return a {@link Maybe}
     */
    @Nonnull
    public abstract Maybe<T> reject(@Nonnull Predicate<? super T> predicate);

    /**
     * Invokes absentAction if this is empty.
     *
     * @param absentAction action to call if this is empty
     * @return this
     */
    @Nonnull
    public abstract Maybe<T> apply(@Nonnull Proc0 absentAction);

    /**
     * Invokes presentAction with this value if this is full.
     *
     * @param presentAction action to call if this is full
     * @return this
     */
    @Nonnull
    public abstract Maybe<T> apply(@Nonnull Proc1<? super T> presentAction);

    /**
     * Invokes absentAction if this is empty.
     *
     * @param absentAction action to call if this is empty
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> absentAction)
        throws E;

    /**
     * Invokes presentAction with this value if this is full.
     *
     * @param presentAction action to call if this is full
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> presentAction)
        throws E;

    /**
     * Gets this value.  Throws NoSuchElementException if this is empty.
     *
     * @return this value
     * @throws NoSuchElementException if this is empty
     */
    public abstract T unsafeGet();

    /**
     * Gets this value.  Calls absentExceptionMapping to get an exception
     * to throw if this is empty.
     *
     * @return this value
     * @throws E an exception produced by mapping if this is empty
     */
    public abstract <E extends Exception> T unsafeGet(@Nonnull Func0<E> absentExceptionMapping)
        throws E;

    /**
     * Gets this value.  If this is empty returns absentValue instead.
     *
     * @param absentValue value to return if this is empty
     */
    public abstract T get(T absentValue);

    /**
     * Gets this value.  If this is empty returns null.
     */
    public abstract T getOrNull();

    /**
     * Gets this value.  If this is empty returns result of calling absentMapping instead.
     *
     * @param absentMapping function to generate value to return if this is empty
     */
    public abstract T getOr(@Nonnull Func0<? extends T> absentMapping);

    /**
     * Gets a value based on this value.  If this is empty absentValue is returned.
     * Otherwise this value is passed to presentMapping to obtain a return value.
     *
     * @param absentValue    value to return if this is empty
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U> U match(U absentValue,
                                @Nonnull Func1<? super T, U> presentMapping);

    /**
     * Gets a value based on this value.  If this is empty absentMapping is called
     * to obtain a return value.  Otherwise this value is passed to presentMapping to
     * obtain a return value.
     *
     * @param absentMapping  function to produce a value to return if this is empty
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U> U matchOr(@Nonnull Func0<U> absentMapping,
                                  @Nonnull Func1<? super T, U> presentMapping);

    /**
     * Gets a value based on this value.  If this is empty absentValue is returned.
     * Otherwise this value is passed to presentMapping to obtain a return value.
     *
     * @param absentValue    value to return if this is empty
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U, E extends Exception> U matchThrows(U absentValue,
                                                           @Nonnull Func1Throws<? super T, U, E> presentMapping)
        throws E;

    /**
     * Gets a value based on this value.  If this is empty absentMapping is called
     * to obtain a return value.  Otherwise this value is passed to presentMapping to
     * obtain a return value.
     *
     * @param absentMapping  function to produce a value to return if this is empty
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> absentMapping,
                                                             @Nonnull Func1Throws<? super T, U, E> presentMapping)
        throws E;

    /**
     * Returns true if this has no value
     */
    public abstract boolean isEmpty();

    /**
     * Returns true if this has a value
     */
    public abstract boolean isFull();

    private static class Empty<T>
        extends Maybe<T>
    {
        @SuppressWarnings("rawtypes")
        private static final Empty EMPTY = new Empty();

        private Empty()
        {
        }

        @Nonnull
        @Override
        public NotNull<T> notNull()
        {
            return NotNull.empty();
        }

        @Nonnull
        @Override
        public Maybe<T> nullToEmpty()
        {
            return this;
        }

        @Nonnull
        @Override
        public Maybe<T> map(@Nonnull Func0<? extends T> absentMapping)
        {
            return of(absentMapping.apply());
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return empty();
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func0<? extends U> absentMapping,
                                @Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return of(absentMapping.apply());
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> absentMapping)
            throws E
        {
            return of(absentMapping.apply());
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return empty();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> absentMapping,
                                                           @Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return of(absentMapping.apply());
        }

        @Nonnull
        @Override
        public Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> absentMapping)
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> presentMapping)
        {
            return empty();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> presentMapping)
            throws E
        {
            return empty();
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> absentMapping,
                                    @Nonnull Func1<? super T, Maybe<A>> presentMapping)
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> absentMapping)
            throws E
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> absentMapping,
                                                               @Nonnull Func1Throws<? super T, Maybe<A>, E> presentMapping)
            throws E
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public Maybe<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return empty();
        }

        @Nonnull
        @Override
        public Maybe<T> reject(@Nonnull Predicate<? super T> predicate)
        {
            return empty();
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
        public Maybe<T> apply(@Nonnull Proc0 absentAction)
        {
            absentAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public Maybe<T> apply(@Nonnull Proc1<? super T> presentAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> absentAction)
            throws E
        {
            absentAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> presentAction)
            throws E
        {
            return this;
        }

        @Override
        public T unsafeGet()
        {
            throw new NoSuchElementException();
        }

        @Override
        public <E extends Exception> T unsafeGet(@Nonnull Func0<E> absentExceptionMapping)
            throws E
        {
            throw absentExceptionMapping.apply();
        }

        @Override
        public T get(T absentValue)
        {
            return absentValue;
        }

        @Override
        public T getOr(@Nonnull Func0<? extends T> absentMapping)
        {
            return absentMapping.apply();
        }

        @Override
        public T getOrNull()
        {
            return null;
        }

        @Override
        public <U> U match(U absentValue,
                           @Nonnull Func1<? super T, U> presentMapping)
        {
            return absentValue;
        }

        @Override
        public <U> U matchOr(@Nonnull Func0<U> absentMapping,
                             @Nonnull Func1<? super T, U> presentMapping)
        {
            return absentMapping.apply();
        }

        @Override
        public <U, E extends Exception> U matchThrows(U absentValue,
                                                      @Nonnull Func1Throws<? super T, U, E> presentMapping)
            throws E
        {
            return absentValue;
        }

        @Override
        public <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> absentMapping,
                                                        @Nonnull Func1Throws<? super T, U, E> presentMapping)
            throws E
        {
            return absentMapping.apply();
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        @Override
        public boolean isFull()
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
            return obj instanceof Maybe.Empty;
        }

        @Override
        public String toString()
        {
            return "()";
        }

        private Object writeReplace()
        {
            return new MaybeProxy(this);
        }
    }

    private static class Full<T>
        extends Maybe<T>
    {
        private final T value;

        private Full(T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public NotNull<T> notNull()
        {
            return NotNull.of(value);
        }

        @Nonnull
        @Override
        public Maybe<T> nullToEmpty()
        {
            return value == null ? empty() : this;
        }

        @Nonnull
        @Override
        public Maybe<T> map(@Nonnull Func0<? extends T> absentMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return of(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U> Maybe<U> map(@Nonnull Func0<? extends U> absentMapping,
                                @Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return of(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> mapThrows(@Nonnull Func0Throws<? extends T, E> absentMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return of(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U, E extends Exception> Maybe<U> mapThrows(@Nonnull Func0Throws<? extends U, E> absentMapping,
                                                           @Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return of(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public Maybe<T> flatMap(@Nonnull Func0<Maybe<T>> absentMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func1<? super T, Maybe<A>> presentMapping)
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> flatMapThrows(@Nonnull Func0Throws<Maybe<T>, E> absentMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func1Throws<? super T, Maybe<A>, E> presentMapping)
            throws E
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A> Maybe<A> flatMap(@Nonnull Func0<Maybe<A>> absentMapping,
                                    @Nonnull Func1<? super T, Maybe<A>> presentMapping)
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A, E extends Exception> Maybe<A> flatMapThrows(@Nonnull Func0Throws<Maybe<A>, E> absentMapping,
                                                               @Nonnull Func1Throws<? super T, Maybe<A>, E> presentMapping)
            throws E
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public Maybe<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? this : empty();
        }

        @Nonnull
        @Override
        public Maybe<T> reject(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? empty() : this;
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
        public Maybe<T> apply(@Nonnull Proc0 absentAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public Maybe<T> apply(@Nonnull Proc1<? super T> presentAction)
        {
            presentAction.apply(value);
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc0Throws<E> absentAction)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> Maybe<T> applyThrows(@Nonnull Proc1Throws<? super T, E> presentAction)
            throws E
        {
            presentAction.apply(value);
            return this;
        }

        @Override
        public T unsafeGet()
        {
            return value;
        }

        @Override
        public <E extends Exception> T unsafeGet(@Nonnull Func0<E> absentExceptionMapping)
            throws E
        {
            return value;
        }

        @Override
        public T get(T absentValue)
        {
            return value;
        }

        @Override
        public T getOr(@Nonnull Func0<? extends T> absentMapping)
        {
            return value;
        }

        @Override
        public T getOrNull()
        {
            return value;
        }

        @Override
        public <U> U match(U absentValue,
                           @Nonnull Func1<? super T, U> presentMapping)
        {
            return presentMapping.apply(value);
        }

        @Override
        public <U> U matchOr(@Nonnull Func0<U> absentMapping,
                             @Nonnull Func1<? super T, U> presentMapping)
        {
            return presentMapping.apply(value);
        }

        @Override
        public <U, E extends Exception> U matchThrows(U absentValue,
                                                      @Nonnull Func1Throws<? super T, U, E> presentMapping)
            throws E
        {
            return presentMapping.apply(value);
        }

        @Override
        public <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> absentMapping,
                                                        @Nonnull Func1Throws<? super T, U, E> presentMapping)
            throws E
        {
            return presentMapping.apply(value);
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean isFull()
        {
            return true;
        }

        @Override
        public int hashCode()
        {
            return value == null ? 0 : value.hashCode();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj)
        {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Maybe.Full)) {
                return false;
            }
            Object otherValue = ((Full)obj).value;
            if (value == null) {
                return otherValue == null;
            }
            if (otherValue == null) {
                return false;
            }
            return value.equals(otherValue);
        }

        @Override
        public String toString()
        {
            return "(" + value + ")";
        }

        private Object writeReplace()
        {
            return new MaybeProxy(this);
        }
    }
}
