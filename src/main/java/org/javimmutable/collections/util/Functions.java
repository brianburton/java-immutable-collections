///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

import org.javimmutable.collections.Addable;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.list.PersistentLinkedStack;

import java.util.Iterator;
import java.util.Map;

/**
 * Library of static functions that perform various operations on Cursors.
 */
public class Functions
{
    /**
     * Calls func for every value in cursor passing in the accumulator and each value as parameters
     * and setting accumulator to the result.  The final accumulator value is returned.
     *
     * @param accumulator
     * @param cursor
     * @param func
     * @return
     */
    public static <T, R> R foldLeft(R accumulator,
                                    Cursor<T> cursor,
                                    Func2<R, T, R> func)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            accumulator = func.apply(accumulator, cursor.getValue());
        }
        return accumulator;
    }

    /**
     * Calls func for every value in cursor from right to left (i.e. in reverse order) passing in the accumulator and each value
     * as parameters and setting the accumulator to the result.  The final accumulator value is returned.
     * Requires 2x time compared to foldLeft() since it reverses the order of the cursor before calling the function.
     *
     * @param accumulator
     * @param func
     * @param cursor
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R foldRight(R accumulator,
                                     Cursor<T> cursor,
                                     Func2<R, T, R> func)
    {
        return foldLeft(accumulator, reverse(cursor), func);
    }

    /**
     * Creates a new Cursor whose values are in the reverse order of the provided Cursor.
     * Requires O(n) time and creates an intermediate copy of the Cursor's values.
     *
     * @param cursor
     * @param <T>
     * @return
     */
    public static <T> Cursor<T> reverse(Cursor<T> cursor)
    {
        return addAll(PersistentLinkedStack.<T>of(), cursor).cursor();
    }

    /**
     * Calls func for every value in cursor and adds each value for which func returns true
     * to a list.  Returns the resulting list.
     *
     * @param cursor source of the values
     * @param func   function to reject the values
     * @param list   list to receive the values
     * @param <T>
     * @return
     */
    public static <T, R> Addable<R> collectAll(Cursor<T> cursor,
                                               Addable<R> list,
                                               Func1<T, R> func)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            list = list.add(func.apply(cursor.getValue()));
        }
        return list;
    }

    /**
     * Calls func for every value in cursor and adds each value for which func returns a non-empty
     * value to a list.  Returns the resulting list.
     *
     * @param cursor source of the values
     * @param func   function to reject the values
     * @param list   list to receive the values
     * @param <T>
     * @return
     */
    public static <T, R> Addable<R> collectSome(Cursor<T> cursor,
                                                Addable<R> list,
                                                Func1<T, Holder<R>> func)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            Holder<R> mappedValue = func.apply(cursor.getValue());
            if (mappedValue.isFilled()) {
                list = list.add(mappedValue.getValue());
            }
        }
        return list;
    }

    /**
     * Calls func for each value in cursor and passes it to func until func returns true.
     * If func returns true the value is returned.  If func never returns true an empty
     * value is returned.
     *
     * @param cursor
     * @param func
     * @param <T>
     * @return
     */
    public static <T> Holder<T> find(Cursor<T> cursor,
                                     Func1<T, Boolean> func)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
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
     * @param <T>
     * @return
     */
    public static <T> Addable<T> reject(Cursor<T> cursor,
                                        Addable<T> list,
                                        Func1<T, Boolean> func)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            if (!func.apply(cursor.getValue())) {
                list = list.add(cursor.getValue());
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
     * @return
     */
    public static <T> Addable<T> select(Cursor<T> cursor,
                                        Addable<T> list,
                                        Func1<T, Boolean> func)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            if (func.apply(cursor.getValue())) {
                list = list.add(cursor.getValue());
            }
        }
        return list;
    }


    /**
     * Add all values form the iterator to the addable.
     *
     * @param iterator
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Addable<T>> A addAll(A addable,
                                                     Iterator<T> iterator)
    {
        while (iterator.hasNext()) {
            addable = (A)addable.add(iterator.next());
        }
        return addable;
    }

    /**
     * Add all values form the cursor to the addable.
     *
     * @param cursor
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Addable<T>> A addAll(A addable,
                                                     Cursor<T> cursor)
    {
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            addable = (A)addable.add(cursor.getValue());
        }
        return addable;
    }

    /**
     * Add all values form the array to the addable.
     *
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Addable<T>> A addAll(A addable,
                                                     T[] values)
    {
        for (T value : values) {
            addable = (A)addable.add(value);
        }
        return addable;
    }

    public static <K, V> PersistentMap<K, V> setAll(PersistentMap<K, V> dest,
                                                    PersistentMap<K, V> src)
    {
        for (PersistentMap.Entry<K, V> entry : src) {
            dest = dest.set(entry.getKey(), entry.getValue());
        }
        return dest;
    }

    public static <K, V> PersistentMap<K, V> setAll(PersistentMap<K, V> dest,
                                                    Map<K, V> src)
    {
        for (Map.Entry<K, V> entry : src.entrySet()) {
            dest = dest.set(entry.getKey(), entry.getValue());
        }
        return dest;
    }
}
