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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.cursors.StandardCursor;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public final class StandardMutableBuilderTests
{
    private StandardMutableBuilderTests()
    {
    }

    /**
     * Tests all of the standard MutableBuilder add methods using the specified build and comparison functions.
     *
     * @param values
     * @param builderFactory
     * @param comparator
     * @param <T>
     * @param <C>
     */
    public static <T, C> void verifyBuilder(List<T> values,
                                            Func0<? extends MutableBuilder<T, C>> builderFactory,
                                            Func2<List<T>, C, Boolean> comparator)
    {
        @SuppressWarnings("TooBroadScope") C collection;

        Indexed<T> indexed = IndexedList.retained(values);

        // add via Cursor
        collection = builderFactory.apply().add(StandardCursor.of(indexed)).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Iterator
        collection = builderFactory.apply().add(values.iterator()).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Collection
        builderFactory.apply().add(values).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via array
        //noinspection unchecked
        T[] array = (T[])values.toArray();
        //noinspection unchecked
        collection = builderFactory.apply().add(array).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Indexed in its entirety
        builderFactory.apply().add(indexed).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via indexed range
        builderFactory.apply().add(indexed, 0, indexed.size()).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));
    }
}
