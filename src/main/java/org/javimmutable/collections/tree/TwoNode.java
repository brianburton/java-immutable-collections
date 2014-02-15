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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;

import java.util.Collection;
import java.util.Comparator;

public class TwoNode<K, V>
        implements TreeNode<K, V>
{
    private final TreeNode<K, V> left;
    private final TreeNode<K, V> right;
    private final K leftMaxKey;
    private final K rightMaxKey;

    public TwoNode(TreeNode<K, V> left,
                   TreeNode<K, V> right,
                   K leftMaxKey,
                   K rightMaxKey)
    {
        this.left = left;
        this.right = right;
        this.leftMaxKey = leftMaxKey;
        this.rightMaxKey = rightMaxKey;
    }

    @Override
    public Holder<V> find(Comparator<K> props,
                          K key)
    {
        if (props.compare(key, leftMaxKey) <= 0) {
            return left.find(props, key);
        } else {
            return right.find(props, key);
        }
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Comparator<K> props,
                                                       K key)
    {
        if (props.compare(key, leftMaxKey) <= 0) {
            return left.findEntry(props, key);
        } else {
            return right.findEntry(props, key);
        }
    }

    @Override
    public K getMaxKey()
    {
        return rightMaxKey;
    }

    @Override
    public UpdateResult<K, V> update(Comparator<K> props,
                                     K key,
                                     V value)
    {
        if (props.compare(key, leftMaxKey) <= 0) {
            UpdateResult<K, V> result = left.update(props, key, value);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return UpdateResult.createInPlace(result.createLeftTwoNode(right, rightMaxKey), result.sizeDelta);

            case SPLIT:
                return UpdateResult.createInPlace(result.createLeftThreeNode(right, rightMaxKey), result.sizeDelta);
            }
        } else {
            UpdateResult<K, V> result = right.update(props, key, value);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return UpdateResult.createInPlace(result.createRightTwoNode(left, leftMaxKey), result.sizeDelta);

            case SPLIT:
                return UpdateResult.createInPlace(result.createRightThreeNode(left, leftMaxKey), result.sizeDelta);
            }
        }
        throw new RuntimeException();
    }

    @Override
    public void addEntriesTo(Collection<JImmutableMap.Entry<K, V>> collection)
    {
        left.addEntriesTo(collection);
        right.addEntriesTo(collection);
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
    public DeleteResult<K, V> delete(Comparator<K> props,
                                     K key)
    {
        if (props.compare(key, leftMaxKey) <= 0) {
            DeleteResult<K, V> result = left.delete(props, key);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return DeleteResult.createInPlace(new TwoNode<K, V>(result.node,
                                                                    right,
                                                                    result.node.getMaxKey(),
                                                                    rightMaxKey));

            case ELIMINATED:
                return DeleteResult.createRemnant(right);

            case REMNANT:
                DeleteMergeResult<K, V> mergeResult = right.leftDeleteMerge(props, result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createRemnant(mergeResult.left);
                } else {
                    return DeleteResult.createInPlace(mergeResult.createTwoNode());
                }
            }
        } else {
            DeleteResult<K, V> result = right.delete(props, key);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return DeleteResult.createInPlace(new TwoNode<K, V>(left,
                                                                    result.node,
                                                                    leftMaxKey,
                                                                    result.node.getMaxKey()));

            case ELIMINATED:
                return DeleteResult.createRemnant(left);

            case REMNANT:
                DeleteMergeResult<K, V> mergeResult = left.rightDeleteMerge(props, result.node);
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
    public DeleteMergeResult<K, V> leftDeleteMerge(Comparator<K> props,
                                                   TreeNode<K, V> node)
    {
        return new DeleteMergeResult<K, V>(new ThreeNode<K, V>(node,
                                                               left,
                                                               right,
                                                               node.getMaxKey(),
                                                               leftMaxKey,
                                                               rightMaxKey));
    }

    @Override
    public DeleteMergeResult<K, V> rightDeleteMerge(Comparator<K> props,
                                                    TreeNode<K, V> node)
    {
        return new DeleteMergeResult<K, V>(new ThreeNode<K, V>(left,
                                                               right,
                                                               node,
                                                               leftMaxKey,
                                                               rightMaxKey,
                                                               node.getMaxKey()));
    }

    public TreeNode<K, V> getLeft()
    {
        return left;
    }

    public TreeNode<K, V> getRight()
    {
        return right;
    }

    public K getLeftMaxKey()
    {
        return leftMaxKey;
    }

    public K getRightMaxKey()
    {
        return rightMaxKey;
    }

    @Override
    public String toString()
    {
        return String.format("(%s,%s)", left, right);
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return MultiCursor.of(LazyCursor.of(left), LazyCursor.of(right));
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

        TwoNode twoNode = (TwoNode)o;

        if (left != null ? !left.equals(twoNode.left) : twoNode.left != null) {
            return false;
        }
        if (leftMaxKey != null ? !leftMaxKey.equals(twoNode.leftMaxKey) : twoNode.leftMaxKey != null) {
            return false;
        }
        if (right != null ? !right.equals(twoNode.right) : twoNode.right != null) {
            return false;
        }
        if (rightMaxKey != null ? !rightMaxKey.equals(twoNode.rightMaxKey) : twoNode.rightMaxKey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (leftMaxKey != null ? leftMaxKey.hashCode() : 0);
        result = 31 * result + (rightMaxKey != null ? rightMaxKey.hashCode() : 0);
        return result;
    }
}
