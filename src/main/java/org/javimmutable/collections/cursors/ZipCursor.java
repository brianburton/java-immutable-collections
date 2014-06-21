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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Tuple2;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Provides static factory method to create a cursor to combines corresponding values from
 * other cursors into Tuples.
 */
@Immutable
public final class ZipCursor
{
    private ZipCursor()
    {
    }

    public static <C1, C2> Cursor<Tuple2<C1, C2>> of(final Cursor<C1> cursor1,
                                                     final Cursor<C2> cursor2)
    {
        return new AbstractStartCursor<Tuple2<C1, C2>>()
        {
            @Nonnull
            @Override
            public Cursor<Tuple2<C1, C2>> next()
            {
                return ZipCursor.next(cursor1.next(), cursor2.next());
            }
        };
    }

    private static <C1, C2> Cursor<Tuple2<C1, C2>> next(final Cursor<C1> cursor1,
                                                        final Cursor<C2> cursor2)
    {
        if (!(cursor1.hasValue() && cursor2.hasValue())) {
            return EmptyStartedCursor.of();
        }

        return new AbstractStartedCursor<Tuple2<C1, C2>>()
        {
            @Nonnull
            @Override
            public Cursor<Tuple2<C1, C2>> next()
            {
                return ZipCursor.next(cursor1.next(), cursor2.next());
            }

            @Override
            public Tuple2<C1, C2> getValue()
            {
                return new Tuple2<C1, C2>(cursor1.getValue(), cursor2.getValue());
            }
        };
    }
}
