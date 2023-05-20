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

package org.javimmutable.collection.util;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.Func2;
import org.javimmutable.collection.IMap;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.Maybe;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Library of static functions that perform various operations on Iterators.
 */
public final class Functions
{
    private Functions()
    {
    }

    /**
     * Calls func for every value in iterator passing in the accumulator and each value as parameters
     * and setting accumulator to the result.  The final accumulator value is returned.
     */
    public static <T, R> R foldLeft(R accumulator,
                                    Iterator<? extends T> iterator,
                                    Func2<R, ? super T, R> func)
    {
        while (iterator.hasNext()) {
            accumulator = func.apply(accumulator, iterator.next());
        }
        return accumulator;
    }

    /**
     * Calls func for every value in iterator from right to left (i.e. in reverse order) passing in the accumulator and each value
     * as parameters and setting the accumulator to the result.  The final accumulator value is returned.
     * Requires 2x time compared to foldLeft() since it reverses the order of the iterator before calling the function.
     */
    public static <T, R> R foldRight(R accumulator,
                                     Iterator<? extends T> iterator,
                                     Func2<R, ? super T, R> func)
    {
        return foldLeft(accumulator, reverse(iterator), func);
    }

    /**
     * Creates a new Iterator whose values are in the reverse order of the provided Iterator.
     * Requires O(n) time and creates an intermediate copy of the Iterator's values.
     */
    public static <T> Iterator<T> reverse(Iterator<? extends T> iterator)
    {
        List<T> reversed = new LinkedList<>();
        while (iterator.hasNext()) {
            reversed.add(0, iterator.next());
        }
        return reversed.iterator();
    }

    /**
     * Calls func for each value in iterator and passes it to func until func returns true.
     * If func returns true the value is returned.  If func never returns true an empty
     * value is returned.
     */
    public static <T> Maybe<T> find(Iterator<? extends T> iterator,
                                    Func1<? super T, Boolean> func)
    {
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (func.apply(value)) {
                return Maybe.of(value);
            }
        }
        return Maybe.empty();
    }

    public static <K, V> IMap<K, V> assignAll(IMap<K, V> dest,
                                              IMap<K, V> src)
    {
        for (IMapEntry<K, V> entry : src) {
            dest = dest.assign(entry.getKey(), entry.getValue());
        }
        return dest;
    }

    public static <K, V> IMap<K, V> assignAll(IMap<K, V> dest,
                                              Map<K, V> src)
    {
        for (Map.Entry<K, V> entry : src.entrySet()) {
            dest = dest.assign(entry.getKey(), entry.getValue());
        }
        return dest;
    }

}
