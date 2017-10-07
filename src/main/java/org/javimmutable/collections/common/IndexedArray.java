///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.Indexed;

import javax.annotation.concurrent.Immutable;

/**
 * Indexed implementation backed by a java array.
 */
@Immutable
public class IndexedArray<T>
    implements Indexed<T>
{
    private final T[] values;

    /**
     * Produces an instance using a clone of the specified array to ensure that changes to the array
     * will not influence the values returned by the instance's methods.  This is generally preferred
     * to the unsafe() constructor.
     */
    public static <T> IndexedArray<T> copied(T[] values)
    {
        return new IndexedArray<T>(values.clone());
    }

    /**
     * Produces an instance using the provided array.  This makes the instance unsafe for sharing since
     * changes to the array will cause changes to this instance's values.  However this can be useful
     * when performance is important and the instance will not be shared or retained beyond a single
     * method scope.
     */
    public static <T> IndexedArray<T> retained(T[] values)
    {
        return new IndexedArray<T>(values);
    }

    private IndexedArray(T[] values)
    {
        this.values = values;
    }

    @Override
    public T get(int index)
    {
        return values[index];
    }

    @Override
    public int size()
    {
        return values.length;
    }
}
