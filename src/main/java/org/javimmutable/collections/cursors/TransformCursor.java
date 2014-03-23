///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMap;

/**
 * A Cursor that visits all values in another Cursor and transforms each value
 * using a Func1 object.
 */
public class TransformCursor<S, T>
        implements Cursor<T>
{
    private final Cursor<S> source;
    private final Func1<S, T> transforminator;

    public TransformCursor(Cursor<S> source,
                           Func1<S, T> transforminator)
    {
        this.source = source;
        this.transforminator = transforminator;
    }

    public static <S, T> Cursor<T> of(Cursor<S> cursor,
                                      Func1<S, T> transforminator)
    {
        return new TransformCursor<S, T>(cursor, transforminator);
    }

    public static <K, V> Cursor<K> ofKeys(Cursor<JImmutableMap.Entry<K, V>> cursor)
    {
        return new TransformCursor<JImmutableMap.Entry<K, V>, K>(cursor, new Func1<JImmutableMap.Entry<K, V>, K>()
        {
            @Override
            public K apply(JImmutableMap.Entry<K, V> entry)
            {
                return entry.getKey();
            }
        });
    }

    public static <K, V> Cursor<V> ofValues(Cursor<JImmutableMap.Entry<K, V>> cursor)
    {
        return new TransformCursor<JImmutableMap.Entry<K, V>, V>(cursor, new Func1<JImmutableMap.Entry<K, V>, V>()
        {
            @Override
            public V apply(JImmutableMap.Entry<K, V> entry)
            {
                return entry.getValue();
            }
        });
    }

    @Override
    public Cursor<T> start()
    {
        return new TransformCursor<S, T>(source.start(), transforminator);
    }

    @Override
    public Cursor<T> next()
    {
        return new TransformCursor<S, T>(source.next(), transforminator);
    }

    @Override
    public boolean hasValue()
    {
        return source.hasValue();
    }

    @Override
    public T getValue()
    {
        return transforminator.apply(source.getValue());
    }
}
