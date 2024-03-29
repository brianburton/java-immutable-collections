///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Spliterator;

/**
 * Merges the concepts of Spliterator and Iterator.  Objects implementing this
 * interface are Iterators that are capable of splitting into two parts under
 * certain conditions and are also capable of creating Spliterators on demand.
 */
public interface SplitableIterator<T>
    extends Iterator<T>
{
    /**
     * Tests to determine if this Iterator is capable of splitting into two new Iterators.
     */
    default boolean isSplitAllowed()
    {
        return false;
    }

    /**
     * Whenever isSplitAllowed returns true this method can be used to create two new
     * SplitableIterators that collectively visit all the same elements as this Iterator.
     * Whenever isSplitAllowed returns false this method throws UnsupportedOperationException.
     *
     * @return two new SplitableIterators spanning the same elements as this
     * @throws UnsupportedOperationException if splitting is not allowed
     */
    @Nonnull
    default SplitIterator<T> splitIterator()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Utility method that creates a Spliterator with the specified characteristics that
     * encounters all of the same elements as this Iterator.  Implementations are allowed
     * to link the Spliterator to use this Iterator directly.
     */
    @Nonnull
    Spliterator<T> spliterator(int characteristics);
}
