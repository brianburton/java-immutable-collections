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
import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;

public class CursorableCursors<T, C extends Cursorable<T>>
    implements Cursorable<Cursorable<T>>
{
    private final Indexed<C> sources;

    private CursorableCursors(@Nonnull Indexed<C> sources)
    {
        this.sources = sources;
    }

    @Nonnull
    @Override
    public Cursor<Cursorable<T>> cursor()
    {
        return StandardCursor.of(new CursorableSource(0));
    }

    @Nonnull
    public static <T, C extends Cursorable<T>> Cursorable<Cursorable<T>> of(@Nonnull Indexed<C> sources)
    {
        return new CursorableCursors<T, C>(sources);
    }

    private class CursorableSource
        implements StandardCursor.Source<Cursorable<T>>
    {
        private final int index;

        private CursorableSource(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index >= sources.size();
        }

        @Override
        public Cursorable<T> currentValue()
        {
            return sources.get(index);
        }

        @Override
        public StandardCursor.Source<Cursorable<T>> advance()
        {
            return new CursorableSource(index + 1);
        }
    }
}
