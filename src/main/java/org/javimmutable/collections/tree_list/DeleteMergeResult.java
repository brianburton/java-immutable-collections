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

public class DeleteMergeResult<T>
{
    public final TreeNode<T> left;
    public final TreeNode<T> right;

    public DeleteMergeResult(TreeNode<T> left)
    {
        this(left, null);
    }

    public DeleteMergeResult(TreeNode<T> left,
                             TreeNode<T> right)
    {
        this.left = left;
        this.right = right;
    }

    public TreeNode<T> createTwoNode()
    {
        return new TwoNode<T>(left, right, left.getSize(), right.getSize());
    }

    public TreeNode<T> createLeftTwoNode(TreeNode<T> callerRight,
                                            int callerRightSize)
    {
        return new TwoNode<T>(left,
                                 callerRight,
                                 left.getSize(),
                                 callerRightSize);
    }

    public TreeNode<T> createLeftThreeNode(TreeNode<T> callerRight,
                                              int callerRightSize)
    {
        return new ThreeNode<T>(left,
                                   right,
                                   callerRight,
                                   left.getSize(),
                                   right.getSize(),
                                   callerRightSize);
    }

    public TreeNode<T> createRightTwoNode(TreeNode<T> callerLeft,
                                             int callerLeftMax)
    {
        return new TwoNode<T>(callerLeft,
                                 left,
                                 callerLeftMax,
                                 left.getSize());
    }

    public TreeNode<T> createRightThreeNode(TreeNode<T> callerLeft,
                                               int callerLeftMax)
    {
        return new ThreeNode<T>(callerLeft,
                                   left,
                                   right,
                                   callerLeftMax,
                                   left.getSize(),
                                   right.getSize());
    }

    @Override
    public String toString()
    {
        return String.format("[%s,%s]", left, right);
    }
}
