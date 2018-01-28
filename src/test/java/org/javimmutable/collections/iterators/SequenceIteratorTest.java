///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.Sequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

public class SequenceIteratorTest
    extends TestCase
{
    public void test()
    {
        verifyOrderedIterable(asList(), () -> SequenceIterator.iterator(sequence()));
        verifyOrderedIterable(asList(1), () -> SequenceIterator.iterator(sequence(1)));
        verifyOrderedIterable(asList(1, 2), () -> SequenceIterator.iterator(sequence(1, 2)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence()));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1, 2)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1, 2, 3)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1, 2, 3, 4, 5)));
    }

    private Sequence<Integer> sequence(int... values)
    {
        SimpleSequence<Integer> seq = new SimpleSequence<>(null, null);
        for (int i = values.length - 1; i >= 0; --i) {
            seq = new SimpleSequence<>(values[i], seq);
        }
        return seq;
    }

    private static class SimpleSequence<T>
        implements Sequence<T>
    {
        private final T head;
        private final Sequence<T> tail;

        private SimpleSequence(@Nullable T head,
                               @Nullable Sequence<T> tail)
        {
            this.tail = tail;
            this.head = head;
        }

        @Override
        public boolean isEmpty()
        {
            return tail == null;
        }

        @Override
        public T getHead()
        {
            return head;
        }

        @Nonnull
        @Override
        public Sequence<T> getTail()
        {
            if (tail == null) {
                throw new NoSuchElementException();
            }
            return tail;
        }
    }
}
