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

package org.javimmutable.collections.btree_map;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.Tuple2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

interface Node<K, V>
    extends Cursorable<JImmutableMap.Entry<K, V>>,
            SplitableIterable<JImmutableMap.Entry<K, V>>
{
    int MIN_CHILDREN = 16;
    int MAX_CHILDREN = 2 * MIN_CHILDREN;

    @Nullable
    K baseKey();

    /**
     * @return number of direct children of this node
     */
    int childCount();

    /**
     * @return number of values of descendants of this node
     */
    int valueCount();

    @Nonnull
    Holder<V> find(@Nonnull Comparator<K> comparator,
                   @Nonnull K key);

    @Nonnull
    Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Comparator<K> comparator,
                                                @Nonnull K key);

    @Nonnull
    UpdateResult<K, V> assign(@Nonnull Comparator<K> comparator,
                              @Nonnull K key,
                              V value);

    @Nonnull
    Node<K, V> delete(@Nonnull Comparator<K> comparator,
                      @Nonnull K key);

    @Nonnull
    Node<K, V> mergeChildren(@Nonnull Node<K, V> sibling);

    @Nonnull
    Tuple2<Node<K, V>, Node<K, V>> distributeChildren(@Nonnull Node<K, V> sibling);

    int depth();

    default boolean isEmpty()
    {
        return false;
    }

    void checkInvariants(@Nonnull Comparator<K> comparator);
}
