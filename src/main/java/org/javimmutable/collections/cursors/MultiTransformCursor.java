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
import org.javimmutable.collections.Func1;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Cursor that produces values by visiting all values in a Cursor of objects and
 * using a Func1 on each object to produce a Cursor that is then visited to
 * reach all of its elements.   No values are precomputed so LazyCursors can
 * be used to minimize memory consumption.
 */
@Immutable
public class MultiTransformCursor<S, T>
    extends AbstractCursor<T>
{
    private final Cursor<S> sourceCursor;
    private final Cursor<T> visitCursor;
    private final Func1<S, Cursor<T>> transforminator;   // BEHOLD!

    private MultiTransformCursor(Cursor<S> sourceCursor,
                                 Cursor<T> visitCursor,
                                 Func1<S, Cursor<T>> transforminator)
    {
        this.sourceCursor = sourceCursor;
        this.visitCursor = visitCursor;
        this.transforminator = transforminator;
    }

    public static <S, T> Cursor<T> of(Cursor<S> source,
                                      Func1<S, Cursor<T>> transforminator)
    {
        return new MultiTransformCursor<S, T>(source, null, transforminator);
    }

    @Nonnull
    @Override
    public Cursor<T> start()
    {
        return (visitCursor == null) ? next() : this;
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        if ((visitCursor != null) && visitCursor.hasValue()) {
            Cursor<T> nextCursor = visitCursor.next();
            if (nextCursor.hasValue()) {
                return new MultiTransformCursor<S, T>(sourceCursor, nextCursor, transforminator);
            }
        }

        Cursor<S> nextSource = sourceCursor.next();
        while (nextSource.hasValue()) {
            Cursor<T> nextCursor = transforminator.apply(nextSource.getValue()).start();
            if (nextCursor.hasValue()) {
                return new MultiTransformCursor<S, T>(nextSource, nextCursor, transforminator);
            }
            nextSource = nextSource.next();
        }

        return EmptyStartedCursor.of();
    }

    @Override
    public boolean hasValue()
    {
        if (visitCursor == null) {
            throw new NotStartedException();
        }
        return visitCursor.hasValue();
    }

    @Override
    public T getValue()
    {
        if (visitCursor == null) {
            throw new NotStartedException();
        }
        return visitCursor.getValue();
    }
}
