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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitCursor;

import javax.annotation.Nonnull;

public class LazyMultiCursor<T>
    extends AbstractStartedCursor<T>
{
    @Nonnull
    private final Cursor<Cursorable<T>> source;
    @Nonnull
    private final Cursor<T> cursor;

    private LazyMultiCursor(@Nonnull Cursor<Cursorable<T>> source,
                            @Nonnull Cursor<T> cursor)
    {
        this.source = source;
        this.cursor = cursor;
    }

    /**
     * Constructs a cursor that visits all of the values reachable from all of the
     * Cursorables visited by source.
     */
    public static <T> Cursor<T> cursor(@Nonnull Cursor<Cursorable<T>> source)
    {
        return new AbstractStartCursor<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> next()
            {
                return advance(source.start());
            }
        };
    }

    /**
     * Constructs a cursor that visits all of the values reachable from all of the
     * Cursorables contained in source.
     */
    public static <T> Cursor<T> cursor(@Nonnull Indexed<Cursorable<T>> source)
    {
        return cursor(StandardCursor.of(source));
    }

    /**
     * Constructs a cursor that visits all of the values reachable from all of the
     * Cursorables contained in source.  All values are transformed using the provided method.
     */
    public static <S, T> Cursor<T> transformed(Indexed<S> source,
                                               Func1<S, Cursorable<T>> transforminator)
    {
        return transformed(StandardCursor.of(source), transforminator);
    }

    /**
     * Constructs a cursor that visits all of the values reachable from all of the
     * Cursorables visited by source.  All values are transformed using the provided method.
     */
    public static <S, T> Cursor<T> transformed(Cursor<S> source,
                                               Func1<S, Cursorable<T>> transforminator)
    {
        return cursor(TransformCursor.of(source, transforminator));
    }

    /**
     * Constructs a Cursorable with a cursor() method that returns a LazyMultiCursor from the
     * provided sources.  Note: the type signature has changed from v1.9.1 to require a
     * Cursorable of type T rather than a super class.
     */
    public static <T> Cursorable<T> cursorable(@Nonnull final Cursor<Cursorable<T>> sources)
    {
        return () -> LazyMultiCursor.cursor(sources);
    }

    /**
     * Constructs a Cursorable with a cursor() method that returns a LazyMultiCursor from the
     * provided sources.  Note: the type signature has changed from v1.9.1 to require a
     * Cursorable of type T rather than a super class.
     */
    public static <T> Cursorable<T> cursorable(@Nonnull final Indexed<Cursorable<T>> sources)
    {
        return () -> LazyMultiCursor.cursor(sources);
    }

    /**
     * Used internally to create a LazyMultiCursor with already started source and cursor fields.
     */
    private static <T> Cursor<T> resumeAfterSplitCursor(@Nonnull Cursor<Cursorable<T>> source,
                                                        @Nonnull Cursor<T> cursor)
    {
        return new AbstractStartCursor<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> next()
            {
                return new LazyMultiCursor<>(source, cursor);
            }
        };
    }

    private static <T> Cursor<T> advance(Cursor<Cursorable<T>> source)
    {
        while (source.hasValue()) {
            Cursor<T> cursor = source.getValue().cursor().start();
            if (cursor.hasValue()) {
                return new LazyMultiCursor<>(source, cursor);
            }
            source = source.next();
        }
        return EmptyStartedCursor.of();
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        if (cursor.hasValue()) {
            Cursor<T> nextCursor = cursor.next();
            if (nextCursor.hasValue()) {
                return new LazyMultiCursor<>(source, nextCursor);
            }
        }

        return advance(source.next());
    }

    @Override
    public boolean hasValue()
    {
        return cursor.hasValue();
    }

    @Override
    public T getValue()
    {
        return cursor.getValue();
    }

    @Override
    public boolean isSplitAllowed()
    {
        return source.isSplitAllowed();
    }

    @Override
    public SplitCursor<T> splitCursor()
    {
        final SplitCursor<Cursorable<T>> split = source.splitCursor();
        return new SplitCursor<>(resumeAfterSplitCursor(split.getLeft().start(), cursor), cursor(split.getRight()));
    }
}
