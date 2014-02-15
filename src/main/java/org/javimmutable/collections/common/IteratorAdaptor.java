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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;

import java.util.Iterator;

/**
 * Adaptor to traverse a Cursor using the Iterator API.   Evaluation of the Cursor
 * is lazy in the sense that Cursor.next() is not called for the first time until
 * the hasNext() method is called.  The next() method automatically calls the
 * Cursor's next() method after obtaining the current value to return as its
 * result.  In this way the protocol matches how Iterators behave.
 *
 * @param <T>
 */
public class IteratorAdaptor<T>
        implements Iterator<T>
{
    private boolean starting;
    private Cursor<T> cursor;

    public IteratorAdaptor(Cursor<T> cursor)
    {
        this.starting = true;
        this.cursor = cursor;
    }

    public static <V> IteratorAdaptor<V> of(Cursor<V> cursor)
    {
        return new IteratorAdaptor<V>(cursor);
    }

    public boolean hasNext()
    {
        if (starting) {
            starting = false;
            cursor = cursor.next();
        }
        return cursor.hasValue();
    }

    public T next()
    {
        T answer = cursor.getValue();
        cursor = cursor.next();
        return answer;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
