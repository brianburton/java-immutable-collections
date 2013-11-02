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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func0;

/**
 * Cursor that creates and returns a real Cursor for a specific iterable only when next() is
 * called for the first time.
 *
 * @param <V>
 */
public class LazyCursor<V>
        implements Cursor<V>
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
     * Creates a new LazyCursor for the specified iterable.
     *
     * @param iterable to return an actual Cursor to use for iteration
     */
    public LazyCursor(final Cursorable<V> iterable)
    {
        this.factory = new Func0<Cursor<V>>()
        {
            @Override
            public Cursor<V> apply()
            {
                return iterable.cursor();
            }
        };
    }

    /**
     * Creates a new LazyPersistentIterator using the specified factory.
     *
     * @param factory a Func0 capable of creating a real Cursor
     */
    public static <V> LazyCursor<V> of(Func0<Cursor<V>> factory)
    {
        return new LazyCursor<V>(factory);
    }

    /**
     * Creates a new LazyPersistentIterator for the specified iterable.
     *
     * @param iterable a PersistentIterable capable of producing a non-empty PersistentIterator
     */
    public static <V> LazyCursor<V> of(Cursorable<V> iterable)
    {
        return new LazyCursor<V>(iterable);
    }

    /**
     * Creates the real cursor using the factory and returns its next() method's result.
     * Thus when the lazy cursor creates the real cursor it always immediately calls its
     * next() method to start its iteration and get it into the state expected by the
     * caller of this method.
     *
     * @return real cursor's next() value
     */
    @Override
    public Cursor<V> next()
    {
        return factory.apply().next();
    }

    /**
     * Always throws IllegalStateException
     *
     * @return never returns
     * @throws IllegalStateException
     */
    @Override
    public boolean hasValue()
    {
        throw new NotStartedException();
    }

    /**
     * Always throws IllegalStateException
     *
     * @return never returns
     * @throws IllegalStateException
     */
    public V getValue()
    {
        throw new NotStartedException();
    }
}
