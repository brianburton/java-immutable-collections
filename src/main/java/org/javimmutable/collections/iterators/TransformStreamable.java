///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.iterators;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class TransformStreamable<S, T>
    implements IterableStreamable<T>
{
    private final IterableStreamable<S> source;
    private final Function<S, T> transforminator;

    private TransformStreamable(IterableStreamable<S> source,
                                Function<S, T> transforminator)
    {
        this.source = source;
        this.transforminator = transforminator;
    }

    public static <S, T> IterableStreamable<T> of(@Nonnull IterableStreamable<S> source,
                                                  @Nonnull Function<S, T> transforminator)
    {
        return new TransformStreamable<>(source, transforminator);
    }

    public static <K, V> IterableStreamable<K> ofKeys(@Nonnull IterableStreamable<JImmutableMap.Entry<K, V>> source)
    {
        return of(source, JImmutableMap.Entry::getKey);
    }

    public static <K, V> IterableStreamable<V> ofValues(@Nonnull IterableStreamable<JImmutableMap.Entry<K, V>> source)
    {
        return of(source, JImmutableMap.Entry::getValue);
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return TransformIterator.of(source.iterator(), transforminator);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return source.getSpliteratorCharacteristics();
    }
}
