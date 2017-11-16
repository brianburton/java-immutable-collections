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

package org.javimmutable.collections.btree_list;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.Tuple2;

import javax.annotation.Nonnull;

interface BtreeNode<T>
    extends InvariantCheckable,
            Cursorable<T>,
            SplitableIterable<T>
{
    int MIN_CHILDREN = 9;
    int MAX_CHILDREN = 2 * MIN_CHILDREN;

    /**
     * @return number of direct children of this node
     */
    int childCount();

    /**
     * @return number of values of descendants of this node
     */
    int valueCount();

    T get(int index);

    @Nonnull
    BtreeNode<T> assign(int index,
                        T value);

    @Nonnull
    BtreeInsertResult<T> insertAt(int index,
                                  T value);

    @Nonnull
    BtreeInsertResult<T> append(T value);

    @Nonnull
    BtreeNode<T> delete(int index);

    @Nonnull
    BtreeNode<T> mergeChildren(BtreeNode<T> sibling);

    @Nonnull
    Tuple2<BtreeNode<T>, BtreeNode<T>> distributeChildren(BtreeNode<T> sibling);

    @Nonnull
    BtreeNode<T> firstChild();

    @Nonnull
    default BtreeNode<T> compress()
    {
        return this;
    }

    boolean containsIndex(int index);

    int depth();
}
