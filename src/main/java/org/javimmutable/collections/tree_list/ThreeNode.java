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

package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.LazyMultiCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
public class ThreeNode<T>
        extends TreeNode<T>
{
    private final TreeNode<T> left;
    private final TreeNode<T> middle;
    private final TreeNode<T> right;
    private final int leftSize;
    private final int middleSize;
    private final int rightSize;
    private final int size;

    public ThreeNode(TreeNode<T> left,
                     TreeNode<T> middle,
                     TreeNode<T> right,
                     int leftSize,
                     int middleSize,
                     int rightSize)
    {
        this.left = left;
        this.middle = middle;
        this.right = right;
        this.leftSize = leftSize;
        this.middleSize = middleSize;
        this.rightSize = rightSize;
        this.size = leftSize + middleSize + rightSize;
    }

    @Override
    public T get(int index)
    {
        if (index < leftSize) {
            return left.get(index);
        } else {
            index -= leftSize;
            if (index < middleSize) {
                return middle.get(index);
            } else {
                index -= middleSize;
                return right.get(index);
            }
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
            return leftUpdate(result);
        } else {
            index -= leftSize;
            if (index < middleSize) {
                UpdateResult<T> result = middle.insertBefore(index, value);
                return middleUpdate(result);
            } else {
                index -= middleSize;
                UpdateResult<T> result = right.insertBefore(index, value);
                return rightUpdate(result);
            }
        }
    }

    @Override
    public UpdateResult<T> insertAfter(int index,
                                       T value)
    {
        if (index < leftSize) {
            UpdateResult<T> result = left.insertAfter(index, value);
            return leftUpdate(result);
        } else {
            index -= leftSize;
            if (index < middleSize) {
                UpdateResult<T> result = middle.insertAfter(index, value);
                return middleUpdate(result);
            } else {
                index -= middleSize;
                UpdateResult<T> result = right.insertAfter(index, value);
                return rightUpdate(result);
            }
        }
    }

    @Override
    public UpdateResult<T> assign(int index,
                                  T value)
    {
        if (index < leftSize) {
            UpdateResult<T> result = left.assign(index, value);
            return leftUpdate(result);
        } else {
            index -= leftSize;
            if (index < middleSize) {
                UpdateResult<T> result = middle.assign(index, value);
                return middleUpdate(result);
            } else {
                index -= middleSize;
                UpdateResult<T> result = right.assign(index, value);
                return rightUpdate(result);
            }
        }
    }

    @Override
    public int verifyDepthsMatch()
    {
        final int leftDepth = left.verifyDepthsMatch();
        final int middleDepth = middle.verifyDepthsMatch();
        final int rightDepth = right.verifyDepthsMatch();
        if (leftDepth != middleDepth || leftDepth != rightDepth) {
            throw new RuntimeException(String.format("depth mismatch %d  %d  %d", leftDepth, middleDepth, rightDepth));
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
                return DeleteResult.createInPlace(new ThreeNode<T>(result.node,
                                                                   middle,
                                                                   right,
                                                                   result.node.getSize(),
                                                                   middleSize,
                                                                   rightSize));

            case ELIMINATED:
                return DeleteResult.createInPlace(new TwoNode<T>(middle,
                                                                 right,
                                                                 middleSize,
                                                                 rightSize));

            case REMNANT:
                DeleteMergeResult<T> mergeResult = middle.leftDeleteMerge(result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createInPlace(mergeResult.createLeftTwoNode(right, rightSize));
                } else {
                    return DeleteResult.createInPlace(mergeResult.createLeftThreeNode(right, rightSize));
                }
            }
        } else {
            index -= leftSize;
            if (index < middleSize) {
                DeleteResult<T> result = middle.delete(index);
                switch (result.type) {
                case UNCHANGED:
                    return result;

                case INPLACE:
                    return DeleteResult.createInPlace(new ThreeNode<T>(left,
                                                                       result.node,
                                                                       right,
                                                                       leftSize,
                                                                       result.node.getSize(),
                                                                       rightSize));

                case ELIMINATED:
                    return DeleteResult.createInPlace(new TwoNode<T>(left,
                                                                     right,
                                                                     leftSize,
                                                                     rightSize));

                case REMNANT:
                    DeleteMergeResult<T> mergeResult = right.leftDeleteMerge(result.node);
                    if (mergeResult.right == null) {
                        return DeleteResult.createInPlace(mergeResult.createRightTwoNode(left, leftSize));
                    } else {
                        return DeleteResult.createInPlace(mergeResult.createRightThreeNode(left, leftSize));
                    }
                }
            } else {
                index -= middleSize;
                DeleteResult<T> result = right.delete(index);
                switch (result.type) {
                case UNCHANGED:
                    return result;

                case INPLACE:
                    return DeleteResult.createInPlace(new ThreeNode<T>(left,
                                                                       middle,
                                                                       result.node,
                                                                       leftSize,
                                                                       middleSize,
                                                                       result.node.getSize()));

                case ELIMINATED:
                    return DeleteResult.createInPlace(new TwoNode<T>(left,
                                                                     middle,
                                                                     leftSize,
                                                                     middleSize));

                case REMNANT:
                    DeleteMergeResult<T> mergeResult = middle.rightDeleteMerge(result.node);
                    if (mergeResult.right == null) {
                        return DeleteResult.createInPlace(mergeResult.createRightTwoNode(left, leftSize));
                    } else {
                        return DeleteResult.createInPlace(mergeResult.createRightThreeNode(left, leftSize));
                    }
                }
            }
        }
        throw new RuntimeException();
    }

    @Override
    public DeleteMergeResult<T> leftDeleteMerge(TreeNode<T> node)
    {
        return new DeleteMergeResult<T>(new TwoNode<T>(node,
                                                       left,
                                                       node.getSize(),
                                                       leftSize),
                                        new TwoNode<T>(middle,
                                                       right,
                                                       middleSize,
                                                       rightSize)
        );
    }

    @Override
    public DeleteMergeResult<T> rightDeleteMerge(TreeNode<T> node)
    {
        return new DeleteMergeResult<T>(new TwoNode<T>(left,
                                                       middle,
                                                       leftSize,
                                                       middleSize),
                                        new TwoNode<T>(right,
                                                       node,
                                                       rightSize,
                                                       node.getSize())
        );
    }

    @Override
    public String toString()
    {
        return String.format("(%s,%s,%s)", left, middle, right);
    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return LazyMultiCursor.<T>builder(3)
            .insert(left)
            .insert(middle)
            .insert(right)
            .cursor();
    }

    private UpdateResult<T> leftUpdate(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return result;

        case INPLACE:
            return UpdateResult.createInPlace(new ThreeNode<T>(result.newNode,
                                                               middle,
                                                               right,
                                                               result.newNode.getSize(),
                                                               middleSize,
                                                               rightSize));
        case SPLIT:
            return UpdateResult.createSplit(result.createTwoNode(),
                                            new TwoNode<T>(middle,
                                                           right,
                                                           middleSize,
                                                           rightSize)
            );
        }
        throw new RuntimeException();
    }

    private UpdateResult<T> middleUpdate(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return result;

        case INPLACE:
            return UpdateResult.createInPlace(new ThreeNode<T>(left,
                                                               result.newNode,
                                                               right,
                                                               leftSize,
                                                               result.newNode.getSize(),
                                                               rightSize));
        case SPLIT:
            return UpdateResult.createSplit(new TwoNode<T>(left,
                                                           result.newNode,
                                                           leftSize,
                                                           result.newNode.getSize()),
                                            new TwoNode<T>(result.extraNode,
                                                           right,
                                                           result.extraNode.getSize(),
                                                           rightSize)
            );
        }
        throw new RuntimeException();
    }

    private UpdateResult<T> rightUpdate(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return result;

        case INPLACE:
            return UpdateResult.createInPlace(new ThreeNode<T>(left,
                                                               middle,
                                                               result.newNode,
                                                               leftSize,
                                                               middleSize,
                                                               result.newNode.getSize()));

        case SPLIT:
            return UpdateResult.createSplit(new TwoNode<T>(left,
                                                           middle,
                                                           leftSize,
                                                           middleSize),
                                            result.createTwoNode()
            );
        }
        throw new RuntimeException();
    }
}
