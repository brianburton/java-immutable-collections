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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Wrapper for an Indexed that only provides access to a portion of the full Indexed's values.
 *
 * @param <T>
 */
@Immutable
public class Subindexed<T>
        implements Indexed<T>
{
    private final Indexed<? extends T> source;
    private final int offset;
    private final int size;

    public Subindexed(@Nonnull Indexed<? extends T> source,
                      int offset,
                      int limit)
    {
        if ((offset < 0) || (offset > source.size()) || (limit < offset) || (limit > source.size())) {
            throw new IndexOutOfBoundsException();
        }
        this.source = source;
        this.offset = offset;
        this.size = limit - offset;
    }

    public static <T> Subindexed<T> of(@Nonnull Indexed<? extends T> source,
                                       int offset)
    {
        return new Subindexed<T>(source, offset, source.size());
    }

    public static <T> Subindexed<T> of(@Nonnull Indexed<? extends T> source,
                                       int offset,
                                       int limit)
    {
        return new Subindexed<T>(source, offset, limit);
    }

    @Override
    public T get(int index)
    {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException();
        }
        return source.get(offset + index);
    }

    @Override
    public int size()
    {
        return size;
    }
}
