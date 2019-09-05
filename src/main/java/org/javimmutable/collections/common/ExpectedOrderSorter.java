///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Class intended for use in unit tests to simplify sorting collections based on
 * a standard ordering.  The standard ordering comes from the order that values
 * appear in an Iterator.  During sorting process any values encountered in collection
 * that did not appear in the Iterator trigger an IllegalArgumentException.
 */
@NotThreadSafe
public class ExpectedOrderSorter<T>
{
    private final Map<T, Integer> ordering;

    public ExpectedOrderSorter(Iterator<T> iterator)
    {
        Map<T, Integer> ordering = new HashMap<>();
        while (iterator.hasNext()) {
            ordering.put(iterator.next(), ordering.size());
        }
        this.ordering = Collections.unmodifiableMap(ordering);
    }

    /**
     * Creates a new List containing all of the elements of collection sorted based on the Iterator.
     */
    public <U, C extends Collection<U>> List<U> sort(@Nonnull C collection,
                                                     @Nonnull Function<U, T> mapper)
    {
        List<U> sorted = new ArrayList<>();
        sorted.addAll(collection);
        sorted.sort(comparator(mapper));
        return sorted;
    }

    /**
     * Creates a new List containing all of the elements of collection sorted based on the Iterator.
     */
    public <U> Iterator<U> sort(@Nonnull Iterator<U> iterator,
                                @Nonnull Function<U, T> mapper)
    {
        List<U> sorted = new ArrayList<>();
        while (iterator.hasNext()) {
            sorted.add(iterator.next());
        }
        sorted.sort(comparator(mapper));
        return sorted.iterator();
    }

    private <U> Comparator<U> comparator(@Nonnull Function<U, T> mapper)
    {
        return new Comparator<U>()
        {
            @Override
            public int compare(U a,
                               U b)
            {
                final Integer aOrder = orderOf(mapper.apply(a));
                final Integer bOrder = orderOf(mapper.apply(b));
                return aOrder - bOrder;
            }

            private int orderOf(T key)
            {
                final Integer answer = ordering.get(key);
                if (answer == null) {
                    throw new IllegalArgumentException();
                }
                return answer;
            }
        };
    }
}
