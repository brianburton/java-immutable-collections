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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for containers that associate keys with values.
 */
public interface Mapped<K, V>
{
    /**
     * Return the value associated with key or null if no value is associated.
     * Note that if null is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param key identifies the value to retrieve
     * @return value associated with key or null if no value is associated
     */
    @Nullable
    V get(K key);

    /**
     * Return the value associated with key or defaultValue if no value is associated.
     * Note that if defaultValue is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param key          identifies the value to retrieve
     * @param defaultValue value to return if no entry exists for key
     * @return value associated with key or defaultValue if no value is associated
     */
    V getValueOr(K key,
                 V defaultValue);

    /**
     * Return a Holder containing the value associated wth the key or an empty
     * Holder if no value is associated with the key.
     *
     * @param key identifies the value to retrieve
     * @return possibly empty Holder containing any value associated with the key
     */
    @Nonnull
    Holder<V> find(K key);

    /**
     * Return a Holder containing the non-null value associated wth the key or a None
     * if no value or a null value is associated with the key.
     *
     * @param key identifies the value to retrieve
     * @return possibly empty Holder containing any value associated with the key
     */
    @Nonnull
    default Holder<V> seek(K key)
    {
        return Holders.notNull(get(key));
    }
}
