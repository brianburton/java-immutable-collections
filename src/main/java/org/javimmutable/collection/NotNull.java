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
import org.javimmutable.collection.serialization.NotNullProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Used to handle cases when a value may or may not be present and to eliminate the use of
 * null values.  The value of a {@link NotNull} cannot be null.
 * Provides a variety of utility methods to allow call chaining.
 */
public abstract class NotNull<T>
    implements IStreamable<T>,
               Serializable
{
    private NotNull()
    {
    }

    /**
     * Returns a {@link NotNull} with no value. All absent {@link NotNull}s share a common instance.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> NotNull<T> absent()
    {
        return Absent.ABSENT;
    }

    /**
     * Returns a {@link NotNull} containing the value.  Null is treated as absent.
     */
    @Nonnull
    public static <T> NotNull<T> present(T value)
    {
        return (value == null) ? absent() : new Present<>(value);
    }

    /**
     * Determine if the object is an instance of the specified Class or a subclass.
     * If the object is null, returns a {@link NotNull} containing null.
     * If the object is not null but not of the correct class, returns an empty Holder.
     * Otherwise returns a Holder containing the value cast to the target type.
     *
     * @param klass class to cast the object to
     * @param value object to be case
     * @param <T>   type of the class
     * @return a {@link NotNull}
     */
    public static <T> NotNull<T> cast(@Nonnull Class<T> klass,
                                      @Nullable Object value)
    {
        if (klass.isInstance(value)) {
            return present(klass.cast(value));
        } else {
            return absent();
        }
    }

    /**
     * Returns a {@link NotNull} containing the first value of the collection.  If the collection
     * is empty an absent {@link NotNull} is returned.
     */
    @Nonnull
    public static <T> NotNull<T> first(@Nonnull Iterable<? extends T> collection)
    {
        final Iterator<? extends T> i = collection.iterator();
        if (i.hasNext()) {
            T value = i.next();
            return present(value);
        } else {
            return absent();
        }
    }

    /**
     * Returns a {@link NotNull} containing the first value of the collection
     * for which the predicate returns true.  If the collection
     * is empty or predicate always returns false an absent {@link NotNull} is returned.
     */
    @Nonnull
    public static <T> NotNull<T> first(@Nonnull Iterable<? extends T> collection,
                                       @Nonnull Func1<? super T, Boolean> predicate)
    {
        for (T value : collection) {
            if (predicate.apply(value)) {
                return present(value);
            }
        }
        return absent();
    }

    /**
     * If this absent  returns {@link Maybe#absent}, otherwise returns {@link Maybe#present}.
     */
    @Nonnull
    public abstract Maybe<T> maybe();

    /**
     * Produce a present {@link NotNull}.  If this {@link NotNull} is present it is returned.
     * Otherwise the absentMapping function is called to provide a value
     * for the result {@link NotNull}.
     *
     * @param absentMapping produces value if this is absent
     * @return a present {@link NotNull}
     */
    @Nonnull
    public abstract NotNull<T> map(@Nonnull Func0<? extends T> absentMapping);

    /**
     * Produce a {@link NotNull} that is absent if this is absent or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param presentMapping maps this value to new value
     * @return a possibly absent {@link NotNull}
     */
    @Nonnull
    public abstract <U> NotNull<U> map(@Nonnull Function<? super T, ? extends U> presentMapping);

    /**
     * Produce a {@link NotNull} that is absent if this is absent or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param presentMapping maps this value to new value
     * @return a possibly absent {@link NotNull}
     */
    @Nonnull
    public abstract <U> NotNull<U> map(@Nonnull Func1<? super T, ? extends U> presentMapping);

    /**
     * Produce a present {@link NotNull}.  If this is absent the absentMapping function is called
     * to provide a value.  Otherwise the presentMapping function is called to produce a
     * new value based on this value.
     *
     * @param absentMapping  produces value when this is absent
     * @param presentMapping maps this value to new value
     * @return a present {@link NotNull}
     */
    @Nonnull
    public abstract <U> NotNull<U> map(@Nonnull Func0<? extends U> absentMapping,
                                       @Nonnull Func1<? super T, ? extends U> presentMapping);

    /**
     * Produce a present {@link NotNull}.  If this {@link NotNull} is present it is returned.
     * Otherwise the absentMapping function is called to provide a value
     * for the result {@link NotNull}.
     *
     * @param absentMapping produces value if this is absent
     * @return a present {@link NotNull}
     */
    @Nonnull
    public abstract <E extends Exception> NotNull<T> mapThrows(@Nonnull Func0Throws<? extends T, E> absentMapping)
        throws E;

    /**
     * Produce a {@link NotNull} that is absent if this is absent or else contains the result
     * of passing this value to the given mapping function.
     *
     * @param presentMapping maps this value to new value
     * @return a possibly absent {@link NotNull}
     */
    @Nonnull
    public abstract <U, E extends Exception> NotNull<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
        throws E;

    /**
     * Produce a present {@link NotNull}.  If this is absent the absentMapping function is called
     * to provide a value.  Otherwise the presentMapping function is called to produce a
     * new value based on this value.
     *
     * @param absentMapping  produces value when this is absent
     * @param presentMapping maps this value to new value
     * @return a present {@link NotNull}
     */
    @Nonnull
    public abstract <U, E extends Exception> NotNull<U> mapThrows(@Nonnull Func0Throws<? extends U, E> absentMapping,
                                                                  @Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
        throws E;

    /**
     * Produce a {@link NotNull} based on this one.  If this is absent absentMapping is called
     * to produce a new {@link NotNull}. Otherwise this is returned.
     *
     * @param absentMapping produce new {@link NotNull} if this is absent
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract NotNull<T> flatMap(@Nonnull Func0<NotNull<T>> absentMapping);

    /**
     * Produce a {@link NotNull} based on this one.  If this is present its value is
     * passed to presentMapping to produce a new {@link NotNull}.  Otherwise this is returned.
     *
     * @param presentMapping produce a new {@link NotNull} from this value
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract <A> NotNull<A> flatMap(@Nonnull Func1<? super T, NotNull<A>> presentMapping);

    /**
     * Produce a {@link NotNull} based on this one.  If this is present its value is
     * passed to presentMapping to produce a new {@link NotNull}.  Otherwise absentMapping is
     * called to produce a new {@link NotNull}..
     *
     * @param absentMapping  produce a new {@link NotNull} when this is absent
     * @param presentMapping produce a new {@link NotNull} from this value
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract <A> NotNull<A> flatMap(@Nonnull Func0<NotNull<A>> absentMapping,
                                           @Nonnull Func1<? super T, NotNull<A>> presentMapping);

    /**
     * Produce a {@link NotNull} based on this one.  If this is absent absentMapping is called
     * to produce a new {@link NotNull}. Otherwise this is returned.
     *
     * @param absentMapping produce new {@link NotNull} if this is absent
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract <E extends Exception> NotNull<T> flatMapThrows(@Nonnull Func0Throws<NotNull<T>, E> absentMapping)
        throws E;

    /**
     * Produce a {@link NotNull} based on this one.  If this is present its value is
     * passed to presentMapping to produce a new {@link NotNull}.  Otherwise this is returned.
     *
     * @param presentMapping produce a new {@link NotNull} from this value
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract <A, E extends Exception> NotNull<A> flatMapThrows(@Nonnull Func1Throws<? super T, NotNull<A>, E> presentMapping)
        throws E;

    /**
     * Produce a {@link NotNull} based on this one.  If this is present its value is
     * passed to presentMapping to produce a new {@link NotNull}.  Otherwise absentMapping is
     * called to produce a new {@link NotNull}..
     *
     * @param absentMapping  produce a new {@link NotNull} when this is absent
     * @param presentMapping produce a new {@link NotNull} from this value
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract <A, E extends Exception> NotNull<A> flatMapThrows(@Nonnull Func0Throws<NotNull<A>, E> absentMapping,
                                                                      @Nonnull Func1Throws<? super T, NotNull<A>, E> presentMapping)
        throws E;

    /**
     * Returns this if this is present and predicate returns true.
     * Otherwise an absent {@link NotNull} is returned.
     *
     * @param predicate determines whether to accept this value
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract NotNull<T> select(@Nonnull Predicate<? super T> predicate);

    /**
     * Returns this if this is present and predicate returns false.
     * Otherwise an absent {@link NotNull} is returned.
     *
     * @param predicate determines whether to reject this value
     * @return a {@link NotNull}
     */
    @Nonnull
    public abstract NotNull<T> reject(@Nonnull Predicate<? super T> predicate);

    /**
     * Invokes absentAction if this is absent.
     *
     * @param absentAction action to call if this is absent
     * @return this
     */
    @Nonnull
    public abstract NotNull<T> apply(@Nonnull Proc0 absentAction);

    /**
     * Invokes presentAction with this value if this is present.
     *
     * @param presentAction action to call if this is present
     * @return this
     */
    @Nonnull
    public abstract NotNull<T> apply(@Nonnull Proc1<? super T> presentAction);

    /**
     * Invokes absentAction if this is absent.
     *
     * @param absentAction action to call if this is absent
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> NotNull<T> applyThrows(@Nonnull Proc0Throws<E> absentAction)
        throws E;

    /**
     * Invokes presentAction with this value if this is present.
     *
     * @param presentAction action to call if this is present
     * @return this
     */
    @Nonnull
    public abstract <E extends Exception> NotNull<T> applyThrows(@Nonnull Proc1Throws<? super T, E> presentAction)
        throws E;

    /**
     * Gets this value.  Throws NoSuchElementException if this is absent.
     *
     * @return this value
     * @throws NoSuchElementException if this is absent
     */
    public abstract T unsafeGet();

    /**
     * Gets this value.  Calls absentExceptionMapping to get an exception
     * to throw if this is absent.
     *
     * @return this value
     * @throws E an exception produced by mapping if this is absent
     */
    public abstract <E extends Exception> T unsafeGet(@Nonnull Func0<E> absentExceptionMapping)
        throws E;

    /**
     * Gets this value.  If this is absent returns absentValue instead.
     *
     * @param absentValue value to return if this is absent
     */
    public abstract T get(T absentValue);

    /**
     * Gets this value.  If this is absent returns null.
     */
    public abstract T getOrNull();

    /**
     * Gets this value.  If this is absent returns result of calling absentMapping instead.
     *
     * @param absentMapping function to generate value to return if this is absent
     */
    public abstract T getOr(@Nonnull Func0<? extends T> absentMapping);

    /**
     * Gets a value based on this value.  If this is absent absentValue is returned.
     * Otherwise this value is passed to presentMapping to obtain a return value.
     *
     * @param absentValue    value to return if this is absent
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U> U match(U absentValue,
                                @Nonnull Func1<? super T, U> presentMapping);

    /**
     * Gets a value based on this value.  If this is absent absentMapping is called
     * to obtain a return value.  Otherwise this value is passed to presentMapping to
     * obtain a return value.
     *
     * @param absentMapping  function to produce a value to return if this is absent
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U> U matchOr(@Nonnull Func0<U> absentMapping,
                                  @Nonnull Func1<? super T, U> presentMapping);

    /**
     * Gets a value based on this value.  If this is absent absentValue is returned.
     * Otherwise this value is passed to presentMapping to obtain a return value.
     *
     * @param absentValue    value to return if this is absent
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U, E extends Exception> U matchThrows(U absentValue,
                                                           @Nonnull Func1Throws<? super T, U, E> presentMapping)
        throws E;

    /**
     * Gets a value based on this value.  If this is absent absentMapping is called
     * to obtain a return value.  Otherwise this value is passed to presentMapping to
     * obtain a return value.
     *
     * @param absentMapping  function to produce a value to return if this is absent
     * @param presentMapping function to map this value into return value
     * @return a value
     */
    public abstract <U, E extends Exception> U matchOrThrows(@Nonnull Func0Throws<U, E> absentMapping,
                                                             @Nonnull Func1Throws<? super T, U, E> presentMapping)
        throws E;

    /**
     * Returns true if this has no value
     */
    public abstract boolean isAbsent();

    /**
     * Returns true if this has a value
     */
    public abstract boolean isPresent();

    private static class Absent<T>
        extends NotNull<T>
    {
        @SuppressWarnings("rawtypes")
        private static final Absent ABSENT = new Absent();

        private Absent()
        {
        }

        @Nonnull
        @Override
        public Maybe<T> maybe()
        {
            return Maybe.absent();
        }

        @Nonnull
        @Override
        public NotNull<T> map(@Nonnull Func0<? extends T> absentMapping)
        {
            return present(absentMapping.apply());
        }

        @Nonnull
        @Override
        public <U> NotNull<U> map(@Nonnull Function<? super T, ? extends U> presentMapping)
        {
            return absent();
        }

        @Nonnull
        @Override
        public <U> NotNull<U> map(@Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return absent();
        }

        @Nonnull
        @Override
        public <U> NotNull<U> map(@Nonnull Func0<? extends U> absentMapping,
                                  @Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return present(absentMapping.apply());
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> mapThrows(@Nonnull Func0Throws<? extends T, E> absentMapping)
            throws E
        {
            return present(absentMapping.apply());
        }

        @Nonnull
        @Override
        public <U, E extends Exception> NotNull<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return absent();
        }

        @Nonnull
        @Override
        public <U, E extends Exception> NotNull<U> mapThrows(@Nonnull Func0Throws<? extends U, E> absentMapping,
                                                             @Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return present(absentMapping.apply());
        }

        @Nonnull
        @Override
        public NotNull<T> flatMap(@Nonnull Func0<NotNull<T>> absentMapping)
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public <A> NotNull<A> flatMap(@Nonnull Func1<? super T, NotNull<A>> presentMapping)
        {
            return absent();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> NotNull<A> flatMapThrows(@Nonnull Func1Throws<? super T, NotNull<A>, E> presentMapping)
            throws E
        {
            return absent();
        }

        @Nonnull
        @Override
        public <A> NotNull<A> flatMap(@Nonnull Func0<NotNull<A>> absentMapping,
                                      @Nonnull Func1<? super T, NotNull<A>> presentMapping)
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> flatMapThrows(@Nonnull Func0Throws<NotNull<T>, E> absentMapping)
            throws E
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public <A, E extends Exception> NotNull<A> flatMapThrows(@Nonnull Func0Throws<NotNull<A>, E> absentMapping,
                                                                 @Nonnull Func1Throws<? super T, NotNull<A>, E> presentMapping)
            throws E
        {
            return absentMapping.apply();
        }

        @Nonnull
        @Override
        public NotNull<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return absent();
        }

        @Nonnull
        @Override
        public NotNull<T> reject(@Nonnull Predicate<? super T> predicate)
        {
            return absent();
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
        public NotNull<T> apply(@Nonnull Proc0 absentAction)
        {
            absentAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public NotNull<T> apply(@Nonnull Proc1<? super T> presentAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> applyThrows(@Nonnull Proc0Throws<E> absentAction)
            throws E
        {
            absentAction.apply();
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> applyThrows(@Nonnull Proc1Throws<? super T, E> presentAction)
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
        public boolean isAbsent()
        {
            return true;
        }

        @Override
        public boolean isPresent()
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
            return obj instanceof NotNull.Absent;
        }

        @Override
        public String toString()
        {
            return "Absent";
        }

        private Object writeReplace()
        {
            return new NotNullProxy(this);
        }
    }

    private static class Present<T>
        extends NotNull<T>
    {
        private final T value;

        private Present(T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public Maybe<T> maybe()
        {
            return Maybe.present(value);
        }

        @Nonnull
        @Override
        public NotNull<T> map(@Nonnull Func0<? extends T> absentMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <U> NotNull<U> map(@Nonnull Function<? super T, ? extends U> presentMapping)
        {
            return present(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U> NotNull<U> map(@Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return present(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U> NotNull<U> map(@Nonnull Func0<? extends U> absentMapping,
                                  @Nonnull Func1<? super T, ? extends U> presentMapping)
        {
            return present(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> mapThrows(@Nonnull Func0Throws<? extends T, E> absentMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <U, E extends Exception> NotNull<U> mapThrows(@Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return present(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public <U, E extends Exception> NotNull<U> mapThrows(@Nonnull Func0Throws<? extends U, E> absentMapping,
                                                             @Nonnull Func1Throws<? super T, ? extends U, E> presentMapping)
            throws E
        {
            return present(presentMapping.apply(value));
        }

        @Nonnull
        @Override
        public NotNull<T> flatMap(@Nonnull Func0<NotNull<T>> absentMapping)
        {
            return this;
        }

        @Nonnull
        @Override
        public <A> NotNull<A> flatMap(@Nonnull Func1<? super T, NotNull<A>> presentMapping)
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> flatMapThrows(@Nonnull Func0Throws<NotNull<T>, E> absentMapping)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <A, E extends Exception> NotNull<A> flatMapThrows(@Nonnull Func1Throws<? super T, NotNull<A>, E> presentMapping)
            throws E
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A> NotNull<A> flatMap(@Nonnull Func0<NotNull<A>> absentMapping,
                                      @Nonnull Func1<? super T, NotNull<A>> presentMapping)
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public <A, E extends Exception> NotNull<A> flatMapThrows(@Nonnull Func0Throws<NotNull<A>, E> absentMapping,
                                                                 @Nonnull Func1Throws<? super T, NotNull<A>, E> presentMapping)
            throws E
        {
            return presentMapping.apply(value);
        }

        @Nonnull
        @Override
        public NotNull<T> select(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? this : absent();
        }

        @Nonnull
        @Override
        public NotNull<T> reject(@Nonnull Predicate<? super T> predicate)
        {
            return predicate.test(value) ? absent() : this;
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
        public NotNull<T> apply(@Nonnull Proc0 absentAction)
        {
            return this;
        }

        @Nonnull
        @Override
        public NotNull<T> apply(@Nonnull Proc1<? super T> presentAction)
        {
            presentAction.apply(value);
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> applyThrows(@Nonnull Proc0Throws<E> absentAction)
            throws E
        {
            return this;
        }

        @Nonnull
        @Override
        public <E extends Exception> NotNull<T> applyThrows(@Nonnull Proc1Throws<? super T, E> presentAction)
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
        public boolean isAbsent()
        {
            return false;
        }

        @Override
        public boolean isPresent()
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
            if (!(obj instanceof NotNull.Present)) {
                return false;
            }
            Object otherValue = ((Present)obj).value;
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
            return "Present(" + value + ")";
        }

        private Object writeReplace()
        {
            return new NotNullProxy(this);
        }
    }
}
