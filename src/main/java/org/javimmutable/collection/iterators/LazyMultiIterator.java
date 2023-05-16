///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection.iterators;

import org.javimmutable.collection.Indexed;
import org.javimmutable.collection.SplitIterator;
import org.javimmutable.collection.SplitableIterable;
import org.javimmutable.collection.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class LazyMultiIterator<T>
    extends AbstractSplitableIterator<T>
{
    @Nonnull
    private final SplitableIterator<SplitableIterable<T>> source;
    @Nullable
    private SplitableIterator<T> iterator;
    private boolean advanced;
    private boolean hasNext;
    private T nextValue;

    private LazyMultiIterator(@Nonnull SplitableIterator<SplitableIterable<T>> source,
                              @Nullable SplitableIterator<T> iterator,
                              boolean advanced,
                              boolean hasNext,
                              T nextValue)
    {
        this.source = source;
        this.iterator = iterator;
        this.advanced = advanced;
        this.hasNext = hasNext;
        this.nextValue = nextValue;
    }

    @Nonnull
    public static <T> LazyMultiIterator<T> iterator(@Nonnull Indexed<SplitableIterable<T>> source)
    {
        return iterator(IndexedIterator.iterator(source));
    }

    @Nonnull
    public static <T> LazyMultiIterator<T> iterator(@Nonnull SplitableIterator<SplitableIterable<T>> source)
    {
        return new LazyMultiIterator<>(source, null, false, false, null);
    }

    /**
     * Constructs an iterator that visits all of the values reachable from all of the
     * SplitableIterables visited by source.  All values are transformed using the provided method.
     */
    public static <S, T> LazyMultiIterator<T> transformed(SplitableIterator<S> source,
                                                          Function<S, SplitableIterable<T>> transforminator)
    {
        return iterator(TransformIterator.of(source, transforminator));
    }

    /**
     * Constructs an iterator that visits all of the values reachable from all of the
     * SplitableIterables visited by source.  All values are transformed using the provided method.
     */
    public static <S, T> LazyMultiIterator<T> transformed(Indexed<S> source,
                                                          Function<S, SplitableIterable<T>> transforminator)
    {
        return iterator(TransformIterator.of(IndexedIterator.iterator(source), transforminator));
    }

    @Override
    public boolean hasNext()
    {
        advance();
        return hasNext;
    }

    @Override
    public T next()
    {
        advance();
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        advanced = false;
        return nextValue;
    }

    @Override
    public boolean isSplitAllowed()
    {
        return source.isSplitAllowed();
    }

    @Nonnull
    @Override
    public SplitIterator<T> splitIterator()
    {
        final SplitIterator<SplitableIterable<T>> split = source.splitIterator();
        return new SplitIterator<>(new LazyMultiIterator<>(split.getLeft(), iterator, advanced, hasNext, nextValue),
                                   new LazyMultiIterator<>(split.getRight(), null, false, false, null));
    }

    private void advance()
    {
        if (!advanced) {
            advanceImpl();
            advanced = true;
        }
    }

    private void advanceImpl()
    {
        if (iterator != null) {
            if (iterator.hasNext()) {
                hasNext = true;
                nextValue = iterator.next();
                return;
            }
        }

        assert (iterator == null) || !iterator.hasNext();

        while (source.hasNext()) {
            final SplitableIterable<T> nextIterable = source.next();
            final SplitableIterator<T> nextIterator = nextIterable.iterator();
            if (nextIterator.hasNext()) {
                iterator = nextIterator;
                hasNext = true;
                nextValue = iterator.next();
                return;
            }
        }

        hasNext = false;
    }
}
