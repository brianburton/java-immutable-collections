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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func0;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Cursor that creates and returns a real Cursor for a specific iterable only when start() or next() is
 * called for the first time.
 *
 * @param <V>
 */
@Immutable
public class LazyCursor<V>
        extends AbstractStartCursor<V>
{
    private final Func0<Cursor<V>> factory;

    /**
     * Creates a new LazyCursor for the specified factory.
     *
     * @param factory a function to return an actual Cursor to use for iteration
     */
    public LazyCursor(Func0<Cursor<V>> factory)
    {
        this.factory = factory;
    }

    /**
     * Creates a new LazyCursor for the specified cursorable.
     *
     * @param cursorable to return an actual Cursor to use for iteration
     */
    public LazyCursor(final Cursorable<V> cursorable)
    {
        this.factory = new Func0<Cursor<V>>()
        {
            @Override
            public Cursor<V> apply()
            {
                return cursorable.cursor();
            }
        };
    }

    /**
     * Creates a new LazyCursor using the specified factory.
     *
     * @param factory a Func0 capable of creating a real Cursor
     */
    public static <V> LazyCursor<V> of(Func0<Cursor<V>> factory)
    {
        return new LazyCursor<V>(factory);
    }

    /**
     * Creates a new LazyCursor for the specified cursorable.
     *
     * @param cursorable a Cursorable capable of producing a non-empty LazyCursor
     */
    public static <V> LazyCursor<V> of(Cursorable<V> cursorable)
    {
        return new LazyCursor<V>(cursorable);
    }

    /**
     * Creates the real cursor using the factory and returns its start() method's result.
     * Thus when the lazy cursor creates the real cursor it always immediately calls its
     * start() method to start its iteration and get it into the state expected by the
     * caller of this method.
     *
     * @return real cursor's start() value
     */
    @Nonnull
    @Override
    public Cursor<V> next()
    {
        return factory.apply().start();
    }
}
