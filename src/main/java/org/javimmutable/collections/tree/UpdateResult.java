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

package org.javimmutable.collections.tree;

import javax.annotation.concurrent.Immutable;

@Immutable
public class UpdateResult<K, V>
{
    public enum Type
    {
        UNCHANGED,
        INPLACE,
        SPLIT
    }

    public final Type type;
    public final TreeNode<K, V> newNode;
    public final TreeNode<K, V> extraNode;
    public final int sizeDelta;

    private UpdateResult(Type type,
                         TreeNode<K, V> newNode,
                         TreeNode<K, V> extraNode,
                         int sizeDelta)
    {
        this.type = type;
        this.newNode = newNode;
        this.extraNode = extraNode;
        this.sizeDelta = sizeDelta;
    }

    public static <K, V> UpdateResult<K, V> createUnchanged()
    {
        return new UpdateResult<K, V>(Type.UNCHANGED, null, null, 0);
    }

    public static <K, V> UpdateResult<K, V> createInPlace(TreeNode<K, V> newNode,
                                                          int sizeDelta)
    {
        return new UpdateResult<K, V>(Type.INPLACE, newNode, null, sizeDelta);
    }

    public static <K, V> UpdateResult<K, V> createSplit(TreeNode<K, V> newNode,
                                                        TreeNode<K, V> extraNode,
                                                        int sizeDelta)
    {
        return new UpdateResult<K, V>(Type.SPLIT, newNode, extraNode, sizeDelta);
    }

    public TreeNode<K, V> createTwoNode()
    {
        return new TwoNode<K, V>(newNode,
                                 extraNode,
                                 newNode.getMaxKey(),
                                 extraNode.getMaxKey());
    }

    public TreeNode<K, V> createLeftTwoNode(TreeNode<K, V> right,
                                            K rightMax)
    {
        return new TwoNode<K, V>(newNode,
                                 right,
                                 newNode.getMaxKey(),
                                 rightMax);
    }

    public TreeNode<K, V> createLeftThreeNode(TreeNode<K, V> right,
                                              K rightMax)
    {
        return new ThreeNode<K, V>(newNode,
                                   extraNode,
                                   right,
                                   newNode.getMaxKey(),
                                   extraNode.getMaxKey(),
                                   rightMax);
    }

    public TreeNode<K, V> createRightTwoNode(TreeNode<K, V> left,
                                             K leftMax)
    {
        return new TwoNode<K, V>(left,
                                 newNode,
                                 leftMax,
                                 newNode.getMaxKey());
    }

    public TreeNode<K, V> createRightThreeNode(TreeNode<K, V> left,
                                               K leftMax)
    {
        return new ThreeNode<K, V>(left,
                                   newNode,
                                   extraNode,
                                   leftMax,
                                   newNode.getMaxKey(),
                                   extraNode.getMaxKey());
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

        UpdateResult that = (UpdateResult)o;

        if (sizeDelta != that.sizeDelta) {
            return false;
        }
        if (extraNode != null ? !extraNode.equals(that.extraNode) : that.extraNode != null) {
            return false;
        }
        if (newNode != null ? !newNode.equals(that.newNode) : that.newNode != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (newNode != null ? newNode.hashCode() : 0);
        result = 31 * result + (extraNode != null ? extraNode.hashCode() : 0);
        result = 31 * result + sizeDelta;
        return result;
    }

    @Override
    public String toString()
    {
        return String.format("<%s,%s,%s>", type, newNode, extraNode);
    }
}
