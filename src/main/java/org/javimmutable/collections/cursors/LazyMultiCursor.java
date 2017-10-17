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

import javax.annotation.Nonnull;

public class LazyMultiCursor<T>
    extends AbstractStartedCursor<T>
{
    @Nonnull
    private final Source<T> source;
    @Nonnull
    private final Cursor<T> cursor;

    private LazyMultiCursor(@Nonnull Source<T> source,
                            @Nonnull Cursor<T> cursor)
    {
        this.source = source;
        this.cursor = cursor;
    }

    @SuppressWarnings("unchecked")
    public static <T> Cursor<T> cursor(@Nonnull Cursorable<? extends T> prefix,
                                       @Nonnull Cursorable<? extends T>[] sources,
                                       @Nonnull Cursorable<? extends T> suffix)
    {
        final Cursorable<? extends T>[] array = (Cursorable<? extends T>[])new Cursorable[sources.length + 2];
        array[0] = prefix;
        System.arraycopy(sources, 0, array, 1, sources.length);
        array[array.length - 1] = suffix;
        return cursor(array);
    }

    public static <T> Cursor<T> cursor(@Nonnull final Cursorable<? extends T>... sources)
    {
        if (sources.length == 0) {
            return StandardCursor.of();
        }
        return new AbstractStartCursor<T>()
        {
            @Nonnull
            @Override
            public Cursor<T> next()
            {
                return LazyMultiCursor.start(new Source<T>(sources, 0, sources.length));
            }
        };
    }

    public static <T> Cursorable<T> cursorable(@Nonnull final Cursorable<? extends T>[] sources)
    {
        if (sources.length == 0) {
            return StandardCursor.emptyCursorable();
        } else {
            return () -> LazyMultiCursor.cursor(sources);
        }
    }

    public static <T> Cursorable<T> cursorable(@Nonnull Cursorable<? extends T> prefix,
                                               @Nonnull Cursorable<? extends T>[] sources,
                                               @Nonnull Cursorable<? extends T> suffix)
    {
        if (sources.length == 0) {
            return StandardCursor.emptyCursorable();
        } else {
            return () -> LazyMultiCursor.cursor(prefix, sources, suffix);
        }
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        if (cursor.hasValue()) {
            Cursor<T> nextCursor = cursor.next();
            if (nextCursor.hasValue()) {
                return new LazyMultiCursor<T>(source, nextCursor);
            }
        }
        return advance(source);
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

    @Nonnull
    private static <T> Cursor<T> start(@Nonnull Source<T> source)
    {
        while (true) {
            Cursor<T> cursor = source.cursor();
            if (cursor.hasValue()) {
                return new LazyMultiCursor<T>(source, cursor);
            }
            if (!source.hasNext()) {
                return EmptyStartedCursor.of();
            }
            source = source.next();
        }
    }

    @Nonnull
    private static <T> Cursor<T> advance(@Nonnull Source<T> source)
    {
        while (source.hasNext()) {
            source = source.next();
            Cursor<T> cursor = source.cursor();
            if (cursor.hasValue()) {
                return new LazyMultiCursor<T>(source, cursor);
            }
        }
        return EmptyStartedCursor.of();
    }

    private static class Source<T>
    {
        private final Cursorable<? extends T>[] sources;
        private final int current;
        private final int limit;

        private Source(Cursorable<? extends T>[] sources,
                       int current,
                       int limit)
        {
            this.sources = sources;
            this.current = current;
            this.limit = limit;
        }

        private boolean hasNext()
        {
            return (limit - current) > 1;
        }

        private Source<T> next()
        {
            return new Source<T>(sources, current + 1, limit);
        }

        @SuppressWarnings("unchecked")
        private Cursor<T> cursor()
        {
            return (Cursor<T>)sources[current].cursor().start();
        }

        private boolean canSplit()
        {
            return (limit - current) >= 3;
        }

        private Source<T> splitLeft()
        {
            return new Source<T>(sources, current, splitRightIndex());
        }

        private Source<T> splitRight()
        {
            return new Source<T>(sources, splitRightIndex(), limit);
        }

        private int splitRightIndex()
        {
            final int offset = (limit - current) / 2;
            assert offset >= 2;
            return current + offset;
        }
    }
}
