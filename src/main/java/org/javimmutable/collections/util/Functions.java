///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.util;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.list.JImmutableLinkedStack;

import java.util.Iterator;
import java.util.Map;

/**
 * Library of static functions that perform various operations on Cursors.
 */
public final class Functions
{
    private Functions()
    {
    }

    /**
     * Calls func for every value in cursor passing in the accumulator and each value as parameters
     * and setting accumulator to the result.  The final accumulator value is returned.
     */
    public static <T, R> R foldLeft(R accumulator,
                                    Cursor<? extends T> cursor,
                                    Func2<R, ? super T, R> func)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            accumulator = func.apply(accumulator, cursor.getValue());
        }
        return accumulator;
    }

    /**
     * Calls func for every value in cursor from right to left (i.e. in reverse order) passing in the accumulator and each value
     * as parameters and setting the accumulator to the result.  The final accumulator value is returned.
     * Requires 2x time compared to foldLeft() since it reverses the order of the cursor before calling the function.
     */
    public static <T, R> R foldRight(R accumulator,
                                     Cursor<? extends T> cursor,
                                     Func2<R, ? super T, R> func)
    {
        return foldLeft(accumulator, reverse(cursor), func);
    }

    /**
     * Creates a new Cursor whose values are in the reverse order of the provided Cursor.
     * Requires O(n) time and creates an intermediate copy of the Cursor's values.
     */
    public static <T> Cursor<T> reverse(Cursor<? extends T> cursor)
    {
        return insertAll(JImmutableLinkedStack.<T>of(), cursor).cursor();
    }

    /**
     * Calls func for every value in cursor and adds each value returned by func
     * to a list.  Returns the resulting list.
     *
     * @param cursor source of the values
     * @param func   function to transform the values
     * @param list   list to receive the values
     */
    @SuppressWarnings("unchecked")
    public static <T, R, A extends Insertable<R>> A collectAll(Cursor<? extends T> cursor,
                                                               A list,
                                                               Func1<? super T, R> func)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            list = (A)list.insert(func.apply(cursor.getValue()));
        }
        return list;
    }

    /**
     * Calls func for every value in cursor and adds each value for which func returns a non-empty
     * Holder to a list.  Returns the resulting list.
     *
     * @param cursor source of the values
     * @param func   function to reject the values
     * @param list   list to receive the values
     */
    @SuppressWarnings("unchecked")
    public static <T, R, A extends Insertable<R>> A collectSome(Cursor<? extends T> cursor,
                                                                A list,
                                                                Func1<? super T, Holder<R>> func)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            Holder<R> mappedValue = func.apply(cursor.getValue());
            if (mappedValue.isFilled()) {
                list = (A)list.insert(mappedValue.getValue());
            }
        }
        return list;
    }

    /**
     * Calls func for each value in cursor and passes it to func until func returns true.
     * If func returns true the value is returned.  If func never returns true an empty
     * value is returned.
     */
    public static <T> Holder<T> find(Cursor<? extends T> cursor,
                                     Func1<? super T, Boolean> func)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            if (func.apply(cursor.getValue())) {
                return Holders.of(cursor.getValue());
            }
        }
        return Holders.of();
    }

    /**
     * Calls func for every value in cursor and adds each value for which func returns false
     * to a list.  Returns the resulting list.
     *
     * @param cursor source of the values
     * @param func   function to reject the values
     * @param list   list to receive the values
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Insertable<T>> A reject(Cursor<? extends T> cursor,
                                                        A list,
                                                        Func1<? super T, Boolean> func)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            if (!func.apply(cursor.getValue())) {
                list = (A)list.insert(cursor.getValue());
            }
        }
        return list;
    }

    /**
     * Calls func for every value in cursor and adds each value for which func returns true
     * to a list.  Returns the resulting list.
     *
     * @param cursor source of the values
     * @param func   function to select the values
     * @param list   list to receive the values
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Insertable<T>> A select(Cursor<? extends T> cursor,
                                                        A list,
                                                        Func1<? super T, Boolean> func)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            if (func.apply(cursor.getValue())) {
                list = (A)list.insert(cursor.getValue());
            }
        }
        return list;
    }


    /**
     * Add all values form the iterator to the addable.
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Insertable<T>> A insertAll(A addable,
                                                           Iterator<? extends T> iterator)
    {
        while (iterator.hasNext()) {
            addable = (A)addable.insert(iterator.next());
        }
        return addable;
    }

    /**
     * Add all values form the cursor to the addable.
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Insertable<T>> A insertAll(A addable,
                                                           Cursor<? extends T> cursor)
    {
        for (cursor = cursor.start(); cursor.hasValue(); cursor = cursor.next()) {
            addable = (A)addable.insert(cursor.getValue());
        }
        return addable;
    }

    /**
     * Add all values form the array to the addable.
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Insertable<T>> A insertAll(A addable,
                                                           T[] values)
    {
        for (T value : values) {
            addable = (A)addable.insert(value);
        }
        return addable;
    }

    public static <K, V> JImmutableMap<K, V> assignAll(JImmutableMap<K, V> dest,
                                                       JImmutableMap<K, V> src)
    {
        for (JImmutableMap.Entry<K, V> entry : src) {
            dest = dest.assign(entry.getKey(), entry.getValue());
        }
        return dest;
    }

    public static <K, V> JImmutableMap<K, V> assignAll(JImmutableMap<K, V> dest,
                                                       Map<K, V> src)
    {
        for (Map.Entry<K, V> entry : src.entrySet()) {
            dest = dest.assign(entry.getKey(), entry.getValue());
        }
        return dest;
    }

    /**
     * Returns an Iterable that can be used to navigate each element in the specified Cursor.
     * The Cursor must not have been started yet.  Intended for use in java foreach loops.
     *
     * @deprecated Cursors are Iterable so this function is not needed
     */
    @Deprecated
    public static <T> Iterable<T> each(final Cursor<T> cursor)
    {
        return cursor;
    }

    /**
     * Returns an Iterable that can be used to navigate each element in the specified Cursorable.
     * Intended for use in java foreach loops.
     *
     * @deprecated Cursors are Iterable so just use cursorable.cursor()
     */
    @Deprecated
    public static <T> Iterable<T> each(final Cursorable<T> cursorable)
    {
        return cursorable.cursor();
    }
}
