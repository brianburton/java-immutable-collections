///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

public class UpdateResult<T>
{
    public enum Type
    {
        UNCHANGED,
        INPLACE,
        SPLIT
    }

    public final Type type;
    public final TreeNode<T> newNode;
    public final TreeNode<T> extraNode;

    private UpdateResult(Type type,
                         TreeNode<T> newNode,
                         TreeNode<T> extraNode)
    {
        this.type = type;
        this.newNode = newNode;
        this.extraNode = extraNode;
    }

    public static <T> UpdateResult<T> createInPlace(TreeNode<T> newNode)
    {
        return new UpdateResult<T>(Type.INPLACE, newNode, null);
    }

    public static <T> UpdateResult<T> createSplit(TreeNode<T> newNode,
                                                  TreeNode<T> extraNode)
    {
        return new UpdateResult<T>(Type.SPLIT, newNode, extraNode);
    }

    public TreeNode<T> createTwoNode()
    {
        return new TwoNode<T>(newNode,
                              extraNode,
                              newNode.getSize(),
                              extraNode.getSize());
    }

    public TreeNode<T> createLeftTwoNode(TreeNode<T> right,
                                         int rightSize)
    {
        return new TwoNode<T>(newNode,
                              right,
                              newNode.getSize(),
                              rightSize);
    }

    public TreeNode<T> createLeftThreeNode(TreeNode<T> right,
                                           int rightSize)
    {
        return new ThreeNode<T>(newNode,
                                extraNode,
                                right,
                                newNode.getSize(),
                                extraNode.getSize(),
                                rightSize);
    }

    public TreeNode<T> createRightTwoNode(TreeNode<T> left,
                                          int leftSize)
    {
        return new TwoNode<T>(left,
                              newNode,
                              leftSize,
                              newNode.getSize());
    }

    public TreeNode<T> createRightThreeNode(TreeNode<T> left,
                                            int leftSize)
    {
        return new ThreeNode<T>(left,
                                newNode,
                                extraNode,
                                leftSize,
                                newNode.getSize(),
                                extraNode.getSize());
    }

    @Override
    public String toString()
    {
        return String.format("<%s,%s,%s>", type, newNode, extraNode);
    }
}
