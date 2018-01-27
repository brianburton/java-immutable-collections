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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
class BtreeEmptyNode<T>
    implements BtreeNode<T>
{
    private static final BtreeEmptyNode EMPTY = new BtreeEmptyNode();

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> BtreeEmptyNode<T> of()
    {
        return (BtreeEmptyNode<T>)EMPTY;
    }

    @Override
    public int childCount()
    {
        return 0;
    }

    @Override
    public int valueCount()
    {
        return 0;
    }

    @Override
    public T get(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public BtreeNode<T> assign(int index,
                               T value)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> insertAt(int index,
                                         T value)
    {
        if (index == 0) {
            return BtreeInsertResult.createInPlace(new BtreeLeafNode<>(value));
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> append(T value)
    {
        return BtreeInsertResult.createInPlace(new BtreeLeafNode<>(value));
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> insertNode(int addWhenZero,
                                           boolean atEnd,
                                           @Nonnull BtreeNode<T> node)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public BtreeNode<T> delete(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public BtreeNode<T> mergeChildren(BtreeNode<T> sibling)
    {
        return sibling;
    }

    @Nonnull
    @Override
    public Tuple2<BtreeNode<T>, BtreeNode<T>> distributeChildren(BtreeNode<T> sibling)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public BtreeNode<T> firstChild()
    {
        return this;
    }

    @Override
    public boolean containsIndex(int index)
    {
        return false;
    }

    @Override
    public void checkInvariants(boolean atRoot)
    {
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of();
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return EmptyIterator.of();
    }

    @Override
    public int depth()
    {
        return 1;
    }
}
