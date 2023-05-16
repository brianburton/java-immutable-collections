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

package org.javimmutable.collection;

import javax.annotation.Nonnull;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface for classes that can produce java.util.Streams and are also Iterable.   The default stream
 * creation implementations use spliterator().
 */
public interface IStreamable<T>
    extends SplitableIterable<T>,
            Streamable<T>
{
    /**
     * Overridden here to require implementations to return a SplitableIterator rather than
     * a basic Iterator.  This is necessary to allow composition of new objects from methods
     * like keys() and values().
     */
    @Nonnull
    @Override
    SplitableIterator<T> iterator();

    /**
     * @return characteristics value used when creating Spliterators
     */
    int getSpliteratorCharacteristics();

    /**
     * Default implementation that creates a Spliterator from a newly created Iterator.
     */
    @Nonnull
    @Override
    default Spliterator<T> spliterator()
    {
        return iterator().spliterator(getSpliteratorCharacteristics());
    }

    @Nonnull
    default Stream<T> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    @Nonnull
    default Stream<T> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }
}
