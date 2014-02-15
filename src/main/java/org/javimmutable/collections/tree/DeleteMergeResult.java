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

package org.javimmutable.collections.tree;

public class DeleteMergeResult<K, V>
{
    public final TreeNode<K, V> left;
    public final TreeNode<K, V> right;

    public DeleteMergeResult(TreeNode<K, V> left)
    {
        this(left, null);
    }

    public DeleteMergeResult(TreeNode<K, V> left,
                             TreeNode<K, V> right)
    {
        this.left = left;
        this.right = right;
    }

    public TreeNode<K, V> createTwoNode()
    {
        return new TwoNode<K, V>(left, right, left.getMaxKey(), right.getMaxKey());
    }

    public TreeNode<K, V> createLeftTwoNode(TreeNode<K, V> callerRight,
                                            K callerRightMax)
    {
        return new TwoNode<K, V>(left,
                                 callerRight,
                                 left.getMaxKey(),
                                 callerRightMax);
    }

    public TreeNode<K, V> createLeftThreeNode(TreeNode<K, V> callerRight,
                                              K callerRightMax)
    {
        return new ThreeNode<K, V>(left,
                                   right,
                                   callerRight,
                                   left.getMaxKey(),
                                   right.getMaxKey(),
                                   callerRightMax);
    }

    public TreeNode<K, V> createRightTwoNode(TreeNode<K, V> callerLeft,
                                             K callerLeftMax)
    {
        return new TwoNode<K, V>(callerLeft,
                                 left,
                                 callerLeftMax,
                                 left.getMaxKey());
    }

    public TreeNode<K, V> createRightThreeNode(TreeNode<K, V> callerLeft,
                                               K callerLeftMax)
    {
        return new ThreeNode<K, V>(callerLeft,
                                   left,
                                   right,
                                   callerLeftMax,
                                   left.getMaxKey(),
                                   right.getMaxKey());
    }

    @Override
    public String toString()
    {
        return String.format("[%s,%s]", left, right);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeleteMergeResult that = (DeleteMergeResult)o;

        if (left != null ? !left.equals(that.left) : that.left != null) {
            return false;
        }
        if (right != null ? !right.equals(that.right) : that.right != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
