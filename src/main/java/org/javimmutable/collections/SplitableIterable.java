///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

/**
 * Extension of Iterable for objects whose iterator method returns a SplitableIterator.
 */
public interface SplitableIterable<T>
    extends Iterable<T>
{
    @Override
    @Nonnull
    SplitableIterator<T> iterator();

    /**
     * Processes every value using the provided function.
     */
    default <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        for (T value : this) {
            proc.apply(value);
        }
    }

    /**
     * Apply the specified accumulator to all elements in iterator order calling the accumulator function
     * for each element.  The first call to accumulator is passed initialValue and first element in the sequence.
     * All remaining calls to accumulator are passed the result from the previous call and next element in the sequence.
     *
     * @param initialValue value passed to accumulator on first call
     * @param accumulator  method called to compute result
     * @return result from last call to accumulator
     */
    default <V> V reduce(V initialValue,
                         Func2<V, T, V> accumulator)
    {
        V answer = initialValue;
        for (T value : this) {
            answer = accumulator.apply(answer, value);
        }
        return answer;
    }

    default <V, E extends Exception> V reduceThrows(V initialValue,
                                                    Sum1Throws<T, V, E> accumulator)
        throws E
    {
        V answer = initialValue;
        for (T value : this) {
            answer = accumulator.apply(answer, value);
        }
        return answer;
    }

    /**
     * Version of forEach that includes an integer index along with each value.
     * Note the index is based solely on the sequence of calls to the lambda.
     * It is not based on any key value associated with the collection (i.e.
     * Integer key in a map or index of an array).  First value is always 0,
     * second is always 1, etc.
     */
    default void indexedForEach(@Nonnull IndexedProc1<T> proc)
    {
        final Temp.Int1 index = Temp.intVar(0);
        forEach(v -> proc.apply(index.a++, v));
    }

    /**
     * Version of forEachThrows that includes an integer index along with each value.
     * Note the index is based solely on the sequence of calls to the lambda.
     * It is not based on any key value associated with the collection (i.e.
     * Integer key in a map or index of an array).  First value is always 0,
     * second is always 1, etc.
     */
    default <E extends Exception> void indexedForEachThrows(@Nonnull IndexedProc1Throws<T, E> proc)
        throws E
    {
        final Temp.Int1 index = Temp.intVar(0);
        forEachThrows(v -> proc.apply(index.a++, v));
    }
}
