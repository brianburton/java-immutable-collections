///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class TwoNode<T>
        extends TreeNode<T>
{
    private final TreeNode<T> left;
    private final TreeNode<T> right;
    private final int leftSize;
    private final int rightSize;
    private final int size;

    public TwoNode(TreeNode<T> left,
                   TreeNode<T> right,
                   int leftSize,
                   int rightSize)
    {
        this.left = left;
        this.right = right;
        this.leftSize = leftSize;
        this.rightSize = rightSize;
        this.size = leftSize + rightSize;
    }

    @Override
    public T get(int index)
    {
        if (index < leftSize) {
            return left.get(index);
        } else {
            return right.get(index - leftSize);
        }
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public UpdateResult<T> insertBefore(int index,
                                        T value)
    {
        if (index < leftSize) {
            UpdateResult<T> result = left.insertBefore(index, value);
            return updateLeft(result);
        } else {
            UpdateResult<T> result = right.insertBefore(index - leftSize, value);
            return updateRight(result);
        }
    }

    @Override
    public UpdateResult<T> insertAfter(int index,
                                       T value)
    {
        if (index < leftSize) {
            UpdateResult<T> result = left.insertAfter(index, value);
            return updateLeft(result);
        } else {
            UpdateResult<T> result = right.insertAfter(index - leftSize, value);
            return updateRight(result);
        }
    }

    @Override
    public UpdateResult<T> assign(int index,
                                  T value)
    {
        if (index < leftSize) {
            UpdateResult<T> result = left.assign(index, value);
            return updateLeft(result);
        } else {
            UpdateResult<T> result = right.assign(index - leftSize, value);
            return updateRight(result);
        }
    }

    @Override
    public int verifyDepthsMatch()
    {
        final int leftDepth = left.verifyDepthsMatch();
        final int rightDepth = right.verifyDepthsMatch();
        if (leftDepth != rightDepth) {
            throw new RuntimeException(String.format("depth mismatch %d  %d", leftDepth, rightDepth));
        }
        return leftDepth + 1;
    }

    @Override
    public DeleteResult<T> delete(int index)
    {
        if (index < leftSize) {
            DeleteResult<T> result = left.delete(index);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return DeleteResult.createInPlace(new TwoNode<T>(result.node,
                                                                 right,
                                                                 result.node.getSize(),
                                                                 rightSize));

            case ELIMINATED:
                return DeleteResult.createRemnant(right);

            case REMNANT:
                DeleteMergeResult<T> mergeResult = right.leftDeleteMerge(result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createRemnant(mergeResult.left);
                } else {
                    return DeleteResult.createInPlace(mergeResult.createTwoNode());
                }
            }
        } else {
            DeleteResult<T> result = right.delete(index - leftSize);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return DeleteResult.createInPlace(new TwoNode<T>(left,
                                                                 result.node,
                                                                 leftSize,
                                                                 result.node.getSize()));

            case ELIMINATED:
                return DeleteResult.createRemnant(left);

            case REMNANT:
                DeleteMergeResult<T> mergeResult = left.rightDeleteMerge(result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createRemnant(mergeResult.left);
                } else {
                    return DeleteResult.createInPlace(mergeResult.createTwoNode());
                }
            }
        }
        throw new RuntimeException();
    }

    @Override
    public DeleteMergeResult<T> leftDeleteMerge(TreeNode<T> node)
    {
        return new DeleteMergeResult<T>(new ThreeNode<T>(node,
                                                         left,
                                                         right,
                                                         node.getSize(),
                                                         leftSize,
                                                         rightSize));
    }

    @Override
    public DeleteMergeResult<T> rightDeleteMerge(TreeNode<T> node)
    {
        return new DeleteMergeResult<T>(new ThreeNode<T>(left,
                                                         right,
                                                         node,
                                                         leftSize,
                                                         rightSize,
                                                         node.getSize()));
    }

    @Override
    public String toString()
    {
        return String.format("(%s,%s)", left, right);
    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return MultiCursor.of(LazyCursor.of(left), LazyCursor.of(right));
    }

    private UpdateResult<T> updateLeft(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return result;

        case INPLACE:
            return UpdateResult.createInPlace(result.createLeftTwoNode(right, rightSize));

        case SPLIT:
            return UpdateResult.createInPlace(result.createLeftThreeNode(right, rightSize));
        }
        throw new RuntimeException();
    }

    private UpdateResult<T> updateRight(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return result;

        case INPLACE:
            return UpdateResult.createInPlace(result.createRightTwoNode(left, leftSize));

        case SPLIT:
            return UpdateResult.createInPlace(result.createRightThreeNode(left, leftSize));
        }
        throw new RuntimeException();
    }
}
