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

package org.javimmutable.collections.deque;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for classes used to implement 32-way trees that restrict inserts and deletions
 * to the head and tail of the list but allow updates at any index within the list.
 */
interface Node<T>
    extends SplitableIterable<T>,
            GenericIterator.Iterable<T>,
            Indexed<T>,
            InvariantCheckable
{
    boolean isEmpty();

    boolean isFull();

    int getDepth();

    Node<T> deleteFirst();

    Node<T> deleteLast();

    Node<T> insertFirst(T value);

    Node<T> insertLast(T value);

    boolean containsIndex(int index);

    Node<T> assign(int index,
                   T value);

    @Nonnull
    Node<T> reverse();

    int computedSize();

    @Nonnull
    @Override
    default SplitableIterator<T> iterator()
    {
        return new GenericIterator<>(this, 0, size());
    }

    @Nullable
    default LeafNode<T> castAsLeaf()
    {
        return null;
    }

    @Nullable
    default BranchNode<T> castAsBranch()
    {
        return null;
    }

    @Nonnull
    default Node<T> prune()
    {
        return this;
    }

    /**
     * Return the (possibly empty) list containing the first limit values.
     *
     * @param limit last index (exclusive) of values to include
     * @return a possibly empty list containing the values
     */
    @Nonnull
    Node<T> prefix(int limit);

    /**
     * Return the (possibly empty) list containing the values starting at offset (inclusive)
     * and including all remaining items.
     *
     * @param offset first index (inclusive) of values to include
     * @return a possibly empty list containing the values
     */
    @Nonnull
    Node<T> suffix(int offset);
}
