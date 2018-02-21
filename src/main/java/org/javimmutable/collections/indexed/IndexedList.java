///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.indexed;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Immutable
public class IndexedList<T>
    implements Indexed<T>,
               Iterable<T>
{
    private final List<? extends T> values;

    private IndexedList(List<? extends T> values)
    {
        this.values = values;
    }

    /**
     * Produces an instance using a copy of the specified List to ensure that changes to the List
     * will not influence the values returned by the instance's methods.  This is generally preferred
     * to the unsafe() constructor.
     */
    public static <T> IndexedList<T> copied(List<? extends T> values)
    {
        return new IndexedList<T>(new ArrayList<T>(values));
    }

    /**
     * Produces an instance using the provided List.  This makes the instance unsafe for sharing since
     * changes to the List will cause changes to this instance's values.  However this can be useful
     * when performance is important and the instance will not be shared or retained beyond a single
     * method scope.
     */
    public static <T> IndexedList<T> retained(List<? extends T> values)
    {
        return new IndexedList<T>(values);
    }

    @Override
    public T get(int index)
    {
        return values.get(index);
    }

    @Override
    public int size()
    {
        return values.size();
    }

    @Override
    @Nonnull
    public Iterator<T> iterator()
    {
        final Iterator<? extends T> i = values.iterator();
        return new Iterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return i.hasNext();
            }

            @Override
            public T next()
            {
                return i.next();
            }
        };
    }
}
