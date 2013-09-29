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

package org.javimmutable.collections.array.bit32;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.IteratorAdaptor;

import java.util.Iterator;

/**
 * Fixed size (32 entries) persistent array of type T.
 *
 * @param <T>
 */
public abstract class Bit32Array<T>
        implements Cursorable<JImmutableMap.Entry<Integer, T>>,
                   Iterable<JImmutableMap.Entry<Integer, T>>
{
    private static final int INVALID_INDEX_MASK = ~0x1f;
    private static final Bit32Array EMPTY = new EmptyBit32Array();

    @SuppressWarnings("unchecked")
    public static <T> Bit32Array<T> of()
    {
        return (Bit32Array<T>)EMPTY;
    }

    public abstract Holder<T> get(int index);

    public abstract Bit32Array<T> assign(int index,
                                         T value);

    public abstract Bit32Array<T> delete(int index);

    public abstract int size();

    public abstract int firstIndex();

    @Override
    public Iterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return new IteratorAdaptor<JImmutableMap.Entry<Integer, T>>(cursor());
    }

    protected static void checkIndex(int index)
    {
        if ((index & INVALID_INDEX_MASK) != 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
