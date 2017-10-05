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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.cursors.LazyMultiCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Comparator;

@Immutable
public class ThreeNode<K, V>
    extends TreeNode<K, V>
{
    private final TreeNode<K, V> left;
    private final TreeNode<K, V> middle;
    private final TreeNode<K, V> right;
    private final K leftMaxKey;
    private final K middleMaxKey;
    private final K rightMaxKey;

    public ThreeNode(TreeNode<K, V> left,
                     TreeNode<K, V> middle,
                     TreeNode<K, V> right,
                     K leftMaxKey,
                     K middleMaxKey,
                     K rightMaxKey)
    {
        this.left = left;
        this.middle = middle;
        this.right = right;
        this.leftMaxKey = leftMaxKey;
        this.middleMaxKey = middleMaxKey;
        this.rightMaxKey = rightMaxKey;
    }

    @Override
    public V getValueOr(Comparator<K> comparator,
                        K key,
                        V defaultValue)
    {
        if (comparator.compare(key, leftMaxKey) <= 0) {
            return left.getValueOr(comparator, key, defaultValue);
        }
        if (comparator.compare(key, middleMaxKey) <= 0) {
            return middle.getValueOr(comparator, key, defaultValue);
        } else {
            return right.getValueOr(comparator, key, defaultValue);
        }
    }

    @Override
    public Holder<V> find(Comparator<K> comparator,
                          K key)
    {
        if (comparator.compare(key, leftMaxKey) <= 0) {
            return left.find(comparator, key);
        }
        if (comparator.compare(key, middleMaxKey) <= 0) {
            return middle.find(comparator, key);
        } else {
            return right.find(comparator, key);
        }
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Comparator<K> comparator,
                                                       K key)
    {
        if (comparator.compare(key, leftMaxKey) <= 0) {
            return left.findEntry(comparator, key);
        }
        if (comparator.compare(key, middleMaxKey) <= 0) {
            return middle.findEntry(comparator, key);
        } else {
            return right.findEntry(comparator, key);
        }
    }

    @Override
    K getMaxKey()
    {
        return rightMaxKey;
    }

    @Override
    UpdateResult<K, V> assignImpl(Comparator<K> comparator,
                                  K key,
                                  V value)
    {
        if (comparator.compare(key, leftMaxKey) <= 0) {
            UpdateResult<K, V> result = left.assignImpl(comparator, key, value);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return UpdateResult.createInPlace(new ThreeNode<K, V>(result.newNode,
                                                                      middle,
                                                                      right,
                                                                      result.newNode.getMaxKey(),
                                                                      middleMaxKey,
                                                                      rightMaxKey),
                                                  result.sizeDelta
                );
            case SPLIT:
                return UpdateResult.createSplit(result.createTwoNode(),
                                                new TwoNode<K, V>(middle,
                                                                  right,
                                                                  middleMaxKey,
                                                                  rightMaxKey),
                                                result.sizeDelta
                );
            }
        } else if (comparator.compare(key, middleMaxKey) <= 0) {
            UpdateResult<K, V> result = middle.assignImpl(comparator, key, value);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return UpdateResult.createInPlace(new ThreeNode<K, V>(left,
                                                                      result.newNode,
                                                                      right,
                                                                      leftMaxKey,
                                                                      result.newNode.getMaxKey(),
                                                                      rightMaxKey),
                                                  result.sizeDelta
                );
            case SPLIT:
                return UpdateResult.createSplit(new TwoNode<K, V>(left,
                                                                  result.newNode,
                                                                  leftMaxKey,
                                                                  result.newNode.getMaxKey()),
                                                new TwoNode<K, V>(result.extraNode,
                                                                  right,
                                                                  result.extraNode.getMaxKey(),
                                                                  rightMaxKey),
                                                result.sizeDelta
                );
            }
        } else {
            UpdateResult<K, V> result = right.assignImpl(comparator, key, value);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return UpdateResult.createInPlace(new ThreeNode<K, V>(left,
                                                                      middle,
                                                                      result.newNode,
                                                                      leftMaxKey,
                                                                      middleMaxKey,
                                                                      result.newNode.getMaxKey()),
                                                  result.sizeDelta
                );

            case SPLIT:
                return UpdateResult.createSplit(new TwoNode<K, V>(left,
                                                                  middle,
                                                                  leftMaxKey,
                                                                  middleMaxKey),
                                                result.createTwoNode(),
                                                result.sizeDelta
                );
            }
        }
        throw new RuntimeException();
    }

    @Override
    public void addEntriesTo(Collection<JImmutableMap.Entry<K, V>> collection)
    {
        left.addEntriesTo(collection);
        middle.addEntriesTo(collection);
        right.addEntriesTo(collection);
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
    DeleteResult<K, V> deleteImpl(Comparator<K> comparator,
                                  K key)
    {
        if (comparator.compare(key, leftMaxKey) <= 0) {
            DeleteResult<K, V> result = left.deleteImpl(comparator, key);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return DeleteResult.createInPlace(new ThreeNode<K, V>(result.node,
                                                                      middle,
                                                                      right,
                                                                      result.node.getMaxKey(),
                                                                      middleMaxKey,
                                                                      rightMaxKey));

            case ELIMINATED:
                return DeleteResult.createInPlace(new TwoNode<K, V>(middle,
                                                                    right,
                                                                    middleMaxKey,
                                                                    rightMaxKey));

            case REMNANT:
                DeleteMergeResult<K, V> mergeResult = middle.leftDeleteMerge(result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createInPlace(mergeResult.createLeftTwoNode(right, rightMaxKey));
                } else {
                    return DeleteResult.createInPlace(mergeResult.createLeftThreeNode(right, rightMaxKey));
                }
            }
        } else if (comparator.compare(key, middleMaxKey) <= 0) {
            DeleteResult<K, V> result = middle.deleteImpl(comparator, key);
            switch (result.type) {
            case UNCHANGED:
                return result;

            case INPLACE:
                return DeleteResult.createInPlace(new ThreeNode<K, V>(left,
                                                                      result.node,
                                                                      right,
                                                                      leftMaxKey,
                                                                      result.node.getMaxKey(),
                                                                      rightMaxKey));

            case ELIMINATED:
                return DeleteResult.createInPlace(new TwoNode<K, V>(left,
                                                                    right,
                                                                    leftMaxKey,
                                                                    rightMaxKey));

            case REMNANT:
                DeleteMergeResult<K, V> mergeResult = right.leftDeleteMerge(result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createInPlace(mergeResult.createRightTwoNode(left, leftMaxKey));
                } else {
                    return DeleteResult.createInPlace(mergeResult.createRightThreeNode(left, leftMaxKey));
                }
            }
        } else {
            DeleteResult<K, V> result = right.deleteImpl(comparator, key);
            switch (result.type) {
            case UNCHANGED:
                return DeleteResult.createUnchanged();

            case INPLACE:
                return DeleteResult.createInPlace(new ThreeNode<K, V>(left,
                                                                      middle,
                                                                      result.node,
                                                                      leftMaxKey,
                                                                      middleMaxKey,
                                                                      result.node.getMaxKey()));

            case ELIMINATED:
                return DeleteResult.createInPlace(new TwoNode<K, V>(left,
                                                                    middle,
                                                                    leftMaxKey,
                                                                    middleMaxKey));

            case REMNANT:
                DeleteMergeResult<K, V> mergeResult = middle.rightDeleteMerge(result.node);
                if (mergeResult.right == null) {
                    return DeleteResult.createInPlace(mergeResult.createRightTwoNode(left, leftMaxKey));
                } else {
                    return DeleteResult.createInPlace(mergeResult.createRightThreeNode(left, leftMaxKey));
                }
            }
        }
        throw new RuntimeException();
    }

    @Override
    DeleteMergeResult<K, V> leftDeleteMerge(TreeNode<K, V> node)
    {
        return new DeleteMergeResult<K, V>(new TwoNode<K, V>(node,
                                                             left,
                                                             node.getMaxKey(),
                                                             leftMaxKey),
                                           new TwoNode<K, V>(middle,
                                                             right,
                                                             middleMaxKey,
                                                             rightMaxKey)
        );
    }

    @Override
    DeleteMergeResult<K, V> rightDeleteMerge(TreeNode<K, V> node)
    {
        return new DeleteMergeResult<K, V>(new TwoNode<K, V>(left,
                                                             middle,
                                                             leftMaxKey,
                                                             middleMaxKey),
                                           new TwoNode<K, V>(right,
                                                             node,
                                                             rightMaxKey,
                                                             node.getMaxKey())
        );
    }

    public TreeNode<K, V> getLeft()
    {
        return left;
    }

    public TreeNode<K, V> getMiddle()
    {
        return middle;
    }

    public TreeNode<K, V> getRight()
    {
        return right;
    }

    public K getLeftMaxKey()
    {
        return leftMaxKey;
    }

    public K getMiddleMaxKey()
    {
        return middleMaxKey;
    }

    public K getRightMaxKey()
    {
        return rightMaxKey;
    }

    @Override
    public String toString()
    {
        return String.format("(%s,%s,%s)", left, middle, right);
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return LazyMultiCursor.<JImmutableMap.Entry<K, V>>builder(3)
            .insert(left)
            .insert(middle)
            .insert(right)
            .cursor();
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

        ThreeNode threeNode = (ThreeNode)o;

        if (left != null ? !left.equals(threeNode.left) : threeNode.left != null) {
            return false;
        }
        if (leftMaxKey != null ? !leftMaxKey.equals(threeNode.leftMaxKey) : threeNode.leftMaxKey != null) {
            return false;
        }
        if (middle != null ? !middle.equals(threeNode.middle) : threeNode.middle != null) {
            return false;
        }
        if (middleMaxKey != null ? !middleMaxKey.equals(threeNode.middleMaxKey) : threeNode.middleMaxKey != null) {
            return false;
        }
        if (right != null ? !right.equals(threeNode.right) : threeNode.right != null) {
            return false;
        }
        if (rightMaxKey != null ? !rightMaxKey.equals(threeNode.rightMaxKey) : threeNode.rightMaxKey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (middle != null ? middle.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (leftMaxKey != null ? leftMaxKey.hashCode() : 0);
        result = 31 * result + (middleMaxKey != null ? middleMaxKey.hashCode() : 0);
        result = 31 * result + (rightMaxKey != null ? rightMaxKey.hashCode() : 0);
        return result;
    }
}
