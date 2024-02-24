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

import org.javimmutable.collections.array.TrieArray;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.stream.Collector;

public final class IArrays
{
    private IArrays()
    {
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     */
    @Nonnull
    public static <T> IArray<T> of()
    {
        return TrieArray.of();
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     * Copies all values into the array starting at index zero.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IArray<T> of(T... source)
    {
        return TrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Iterator<IMapEntry<Integer, T>> source)
    {
        return TrieArray.<T>of().insertAll(source);
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Indexed<? extends T> source)
    {
        return TrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values in the specified range from source that
     * supports any integer (positive or negative) as an index.  Indexes do not need to be
     * consecutive there can be gaps of any size between indexes. Copies all entries into the
     * array using each key as an index for storing the corresponding value.  The values copied
     * from source are those whose index are in the range offset to (limit - 1).
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Indexed<? extends T> source,
                                      int offset,
                                      int limit)
    {
        return TrieArray.<T>builder().add(source, offset, limit).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return TrieArray.<T>builder().add(source).build();
    }

    /**
     * Produces a Builder for efficiently constructing a IArray
     * built atop a 32-way integer trie.  All values added by the builder are
     * assigned consecutive indices starting with zero.
     */
    @Nonnull
    public static <T> IArrayBuilder<T> builder()
    {
        return TrieArray.builder();
    }

    /**
     * Collects values into a {@link IArray}.
     */
    @Nonnull
    public static <T> Collector<T, ?, IArray<T>> collector()
    {
        return TrieArray.collector();
    }
}
