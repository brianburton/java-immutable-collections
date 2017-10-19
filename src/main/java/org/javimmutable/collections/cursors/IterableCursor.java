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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;

/**
 * Provides objects implementing the Cursor interface that can be used to traverse
 * an Iterable.  The Cursors are not actually immutable but are designed to behave
 * as though they are.  However since they depend on a mutable object tied to a
 * mutable collection for their state they are not thread safe.  They should only
 * be used when no other threads could be modifying the underlying collection.
 * Also their next() methods can throw exceptions due to concurrent modification
 * of the collection.
 */
@Immutable
public abstract class IterableCursor
{
    /**
     * Creates a mutable Cursor implementation that traverses the specified iterable.  Note that because
     * the Iterator and the collection it points to are not immutable outside factors can interfere with
     * the operation of this class.  In particular this class is not thread safe so the caller must take
     * precautions to synchronize access to the underlying collection.
     * <p>
     * The Cursor retains information obtained from the Iterator so that multiple passes can be made over
     * the data, intermediate Cursor values can be saved and resumed for look-ahead etc.
     */
    public static <T> Cursor<T> of(Iterable<T> iterable)
    {
        return ValueFunctionCursor.<T, Function, Factory>of(new Factory<T>(iterable));
    }

    private static class Factory<T>
        implements ValueFunctionFactory<T, Function<T>>
    {
        private final Iterable<T> iterable;

        private Factory(Iterable<T> iterable)
        {
            this.iterable = iterable;
        }

        @Override
        public Function<T> createFunction()
        {
            return new Function<T>(iterable.iterator());
        }
    }

    private static class Function<T>
        implements ValueFunction<T>
    {
        private final Iterator<T> iterator;

        private Function(Iterator<T> iterator)
        {
            this.iterator = iterator;
        }

        @Override
        public Holder<T> nextValue()
        {
            return iterator.hasNext() ? Holders.of(iterator.next()) : Holders.<T>of();
        }
    }
}
