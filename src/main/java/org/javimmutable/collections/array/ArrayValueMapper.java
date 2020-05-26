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

package org.javimmutable.collections.array;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for objects that can map key/value pairs into single values
 * suitable for storage in an array.
 */
public interface ArrayValueMapper<K, V, T>
{
    V mappedGetValueOr(@Nonnull T mapping,
                       @Nonnull K key,
                       V defaultValue);

    @Nonnull
    Holder<V> mappedFind(@Nonnull T mapping,
                         @Nonnull K key);

    /**
     * Called during assign operation to create a new mapping
     * for the given key and value.
     *
     * @param key   key being assigned to
     * @param value value being assigned
     * @return non-null mapping
     */
    @Nonnull
    T mappedAssign(@Nonnull K key,
                   V value);

    /**
     * Called during assign operation to replace an existing mapping
     * for the given key and value.
     *
     * @param current mapping to be replaced
     * @param key     key being assigned to
     * @param value   value being assigned
     * @return same to keep mapping or non-null to replace mapping
     */
    @Nonnull
    T mappedAssign(@Nonnull T current,
                   @Nonnull K key,
                   V value);

    /**
     * Called during delete operation to delete a key from a mapping.
     *
     * @param current mapping to be replaced
     * @param key     key being deleted
     * @return null to remove mapping, same to keep mapping, or non-null to replace mapping
     */
    @Nullable
    T mappedDelete(@Nonnull T current,
                   @Nonnull K key);

    /**
     * Called to obtain number of keys in a given mapping.
     *
     * @param mapping mapping to be sized
     * @return number of keys in the mapping
     */
    int mappedSize(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<K> mappedKeys(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<V> mappedValues(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<JImmutableMap.Entry<K, V>> mappedEntries(@Nonnull T mapping);
}
