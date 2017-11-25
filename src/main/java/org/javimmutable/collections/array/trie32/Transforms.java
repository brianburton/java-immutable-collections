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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.iterators.TransformIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for transformation objects that manage the leaf nodes in the hash table.
 * Implementations are free to use any class for their leaf nodes and manage them as needed.
 * NOTE: The transforms object is shared across all versions of a hash table so it MUST BE IMMUTABLE.
 * If the transforms object is not immutable it can cause the table to become
 * corrupted over time and/or make older versions of the table invalid.
 */
public interface Transforms<T, K, V>
{
    /**
     * Take the current leaf object (if there is one) and produce a new one
     * (possibly the same) with the specified key and value.  If there is not currently
     * a leaf for this key in the array the leaf will be null.  The result must be
     * a non-null leaf object with the specified value associated with the specified key.
     * If this key was not previously present the method must add 1 to the delta
     * so that the size of the array can be properly maintained.
     */
    @Nonnull
    T update(@Nullable T leaf,
             @Nonnull K key,
             @Nullable V value,
             @Nonnull MutableDelta delta);

    /**
     * Take the current leaf object and produce a new one (possibly the same)
     * with the specified key removed.  If the key was previously present in the leaf
     * the method must subtract 1 from the delta so that the size of the array can be
     * properly maintained.  The returned leaf will be null of no values remain after
     * the deletion.
     */
    @Nullable
    T delete(@Nonnull T leaf,
             @Nonnull K key,
             @Nonnull MutableDelta delta);

    /**
     * Look for the specified key in the leaf object and return a Holder
     * that is empty if the key is not in the leaf or else contains the value associated
     * with the key.
     */
    V getValueOr(@Nonnull T leaf,
                 @Nonnull K key,
                 V defaultValue);

    /**
     * Look for the specified key in the leaf object and return a Holder
     * that is empty if the key is not in the leaf or else contains the value associated
     * with the key.
     */
    Holder<V> findValue(@Nonnull T leaf,
                        @Nonnull K key);

    /**
     * Look for the specified key in the leaf object and return a Holder
     * that is empty if the key is not in the leaf or else contains a JImmutableMap.Entry
     * associated with the key and value.
     */
    Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull T leaf,
                                                @Nonnull K key);

    /**
     * Return a (possibly empty) Cursor over all of the JImmutableMap.Entries
     * in the specified leaf object.
     */
    Cursor<JImmutableMap.Entry<K, V>> cursor(@Nonnull T leaf);

    /**
     * Return a (possibly empty) SplitableIterator over all of the JImmutableMap.Entries
     * in the specified leaf object.
     */
    SplitableIterator<JImmutableMap.Entry<K, V>> iterator(@Nonnull T leaf);
}
