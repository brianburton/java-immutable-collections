///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.SingleValueCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
public class LeafNode<T>
        extends TreeNode<T>
{
    private final T value;

    public LeafNode(T value)
    {
        this.value = value;
    }

    @Override
    public int getSize()
    {
        return 1;
    }

    @Override
    public T get(int index)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return value;
    }

    @Override
    public UpdateResult<T> insertBefore(int index,
                                        T value)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return UpdateResult.createSplit(new LeafNode<T>(value),
                                        this);
    }

    @Override
    public UpdateResult<T> insertAfter(int index,
                                       T value)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return UpdateResult.createSplit(this,
                                        new LeafNode<T>(value));
    }

    @Override
    public UpdateResult<T> assign(int index,
                                  T value)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return UpdateResult.createInPlace(new LeafNode<T>(value));
    }

    @Override
    public DeleteResult<T> delete(int index)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return DeleteResult.createEliminated();
    }

    @Override
    public int verifyDepthsMatch()
    {
        return 1;
    }

    @Override
    public DeleteMergeResult<T> leftDeleteMerge(TreeNode<T> node)
    {
        return new DeleteMergeResult<T>(new TwoNode<T>(node,
                                                       this,
                                                       node.getSize(),
                                                       1));
    }

    @Override
    public DeleteMergeResult<T> rightDeleteMerge(TreeNode<T> node)
    {
        return new DeleteMergeResult<T>(new TwoNode<T>(this,
                                                       node,
                                                       1,
                                                       node.getSize()));
    }

    @Override
    public String toString()
    {
        return String.format("(%s)", value != null ? value.toString() : "null");
    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return SingleValueCursor.of(value);
    }
}
