///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.IndexedIterator;
import org.javimmutable.collections.iterators.LazyMultiIterator;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

class BtreeBranchNode<T>
    implements BtreeNode<T>,
               ArrayHelper.Allocator<BtreeNode<T>>
{
    private final BtreeNode<T>[] children;
    private final int valueCount;

    BtreeBranchNode(@Nonnull BtreeNode<T> child1,
                    @Nonnull BtreeNode<T> child2)
    {
        this(child1, child2, child1.valueCount() + child2.valueCount());
    }

    BtreeBranchNode(@Nonnull BtreeNode<T> child1,
                    @Nonnull BtreeNode<T> child2,
                    int valueCount)
    {
        children = allocateNodes(2);
        children[0] = child1;
        children[1] = child2;
        this.valueCount = valueCount;
    }

    private BtreeBranchNode(@Nonnull BtreeNode<T>[] children)
    {
        this(children, countValues(children));
    }

    private BtreeBranchNode(@Nonnull BtreeNode<T>[] children,
                            int valueCount)
    {
        this.children = children;
        this.valueCount = valueCount;
    }

    static <T> BtreeBranchNode<T> of(Indexed<BtreeNode<T>> source,
                                     int offset,
                                     int limit)
    {
        final int length = limit - offset;
        assert (length > 0) && (length <= MAX_CHILDREN);
        assert limit <= source.size();
        final BtreeNode<T>[] children = allocateNodes(length);
        for (int i = 0; i < length; ++i) {
            children[i] = source.get(offset + i);
        }
        return new BtreeBranchNode<>(children);
    }

    /**
     * For unit test purposes only - creates a new branch node using a copy of the specified array.
     */
    @SafeVarargs
    @Nonnull
    static <T> BtreeBranchNode<T> forTesting(BtreeNode<T>... children)
    {
        assert children.length <= MAX_CHILDREN;
        return new BtreeBranchNode<>(children.clone());
    }

    @Nonnull
    static <T> BtreeBranchNode<T> forTesting(List<BtreeNode<T>> childrenList)
    {
        BtreeNode<T>[] children = allocateNodes(childrenList.size());
        return new BtreeBranchNode<>(childrenList.toArray(children));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> BtreeNode<T>[] allocateNodes(int size)
    {
        return (BtreeNode<T>[])new BtreeNode[size];
    }

    @Override
    public int childCount()
    {
        return children.length;
    }

    @Override
    public int valueCount()
    {
        return valueCount;
    }

    @Override
    public T get(int index)
    {
        final Location<T> loc = findIndexForGetAssign(index);
        return loc.child.get(loc.logicalIndex);
    }

    @Nonnull
    @Override
    public BtreeNode<T> assign(int index,
                               T value)
    {
        final Location<T> loc = findIndexForGetAssign(index);
        final BtreeNode<T>[] newChildren = ArrayHelper.assign(children, loc.childIndex, loc.child.assign(loc.logicalIndex, value));
        return new BtreeBranchNode<>(newChildren, valueCount);
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> insertAt(int index,
                                         T value)
    {
        final Location<T> loc = findIndexForInsertAppend(index);
        final BtreeInsertResult<T> childResult = loc.child.insertAt(loc.logicalIndex, value);
        return resultForInsert(index < valueCount, loc.childIndex, 1, childResult);
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> append(T value)
    {
        return insertAt(valueCount, value);
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> insertNode(int addWhenZero,
                                           boolean atEnd,
                                           @Nonnull BtreeNode<T> node)
    {
        if (addWhenZero == 0) {
            final int totalChildren = childCount() + node.childCount();
            if (totalChildren <= BtreeNode.MAX_CHILDREN) {
                final BtreeNode<T> newNode;
                if (atEnd) {
                    newNode = mergeChildren(node);
                } else {
                    newNode = node.mergeChildren(this);
                }
                return BtreeInsertResult.createInPlace(newNode);
            } else {
                final Tuple2<BtreeNode<T>, BtreeNode<T>> newNodes;
                if (atEnd) {
                    newNodes = distributeChildren(node);
                } else {
                    newNodes = node.distributeChildren(this);
                }
                return BtreeInsertResult.createSplit(newNodes);
            }
        } else {
            final int childIndex = atEnd ? children.length - 1 : 0;
            final BtreeInsertResult<T> childResult = children[childIndex].insertNode(addWhenZero - 1, atEnd, node);
            return resultForInsert(!atEnd, childIndex, node.valueCount(), childResult);
        }
    }

    @Override
    public boolean containsIndex(int index)
    {
        return index < valueCount;
    }

    @Nonnull
    @Override
    public BtreeNode<T> delete(int index)
    {
        final BtreeNode<T>[] children = this.children;
        final int thisChildCount = children.length;
        final Location<T> loc = findIndexForGetAssign(index);
        final int childIndex = loc.childIndex;
        final int newValueCount = this.valueCount - 1;
        final BtreeNode<T> newChild = loc.child.delete(loc.logicalIndex);
        final int newChildCount = newChild.childCount();
        if (newChildCount >= MIN_CHILDREN) {
            return new BtreeBranchNode<>(ArrayHelper.assign(children, childIndex, newChild), newValueCount);
        } else if (newChildCount == 0) {
            if (thisChildCount == 1) {
                return BtreeEmptyNode.of();
            } else {
                return new BtreeBranchNode<>(ArrayHelper.delete(this, children, childIndex), newValueCount);
            }
        } else if (thisChildCount == 1) {
            // special case for the root
            return new BtreeBranchNode<>(ArrayHelper.assign(children, childIndex, newChild), newValueCount);
        } else {
            BtreeNode<T> mergeChild;
            BtreeNode<T> nextChild;
            int mergeIndex;
            if (childIndex == (thisChildCount - 1)) {
                // can't merge at the end of the array
                mergeIndex = childIndex - 1;
                mergeChild = children[mergeIndex];
                nextChild = newChild;
            } else {
                mergeIndex = childIndex;
                mergeChild = newChild;
                nextChild = children[childIndex + 1];
            }
            if ((mergeChild.childCount() + nextChild.childCount()) <= MAX_CHILDREN) {
                final BtreeNode<T> merged = mergeChild.mergeChildren(nextChild);
                return new BtreeBranchNode<>(ArrayHelper.assignDelete(this, children, mergeIndex, merged), newValueCount);
            } else {
                final Tuple2<BtreeNode<T>, BtreeNode<T>> distributed = mergeChild.distributeChildren(nextChild);
                return new BtreeBranchNode<>(ArrayHelper.assignTwo(children, mergeIndex, distributed.getFirst(), distributed.getSecond()), newValueCount);
            }
        }
    }

    @Nonnull
    @Override
    public BtreeNode<T> mergeChildren(BtreeNode<T> sibling)
    {
        final BtreeBranchNode<T> branch = (BtreeBranchNode<T>)sibling;
        assert (children.length + branch.children.length) <= MAX_CHILDREN;
        return new BtreeBranchNode<>(ArrayHelper.concat(this, children, branch.children), valueCount + branch.valueCount);
    }

    @Nonnull
    @Override
    public Tuple2<BtreeNode<T>, BtreeNode<T>> distributeChildren(BtreeNode<T> sibling)
    {
        final BtreeBranchNode<T> branch = (BtreeBranchNode<T>)sibling;
        assert (branch.children.length + children.length) >= MAX_CHILDREN;
        assert (branch.children.length + children.length) <= (2 * MAX_CHILDREN);
        final int totalLength = children.length + branch.children.length;
        final int breakIndex = totalLength / 2;
        return Tuple2.of(new BtreeBranchNode<>(ArrayHelper.subArray(this, children, branch.children, 0, breakIndex)),
                         new BtreeBranchNode<>(ArrayHelper.subArray(this, children, branch.children, breakIndex, totalLength)));
    }

    @Nonnull
    @Override
    public BtreeNode<T> compress()
    {
        return children.length == 1 ? children[0].compress() : this;
    }

    @Override
    public void checkInvariants(boolean isRoot)
    {
        if (children.length > MAX_CHILDREN) {
            throw new IllegalStateException();
        }
        if (children.length < MIN_CHILDREN && !isRoot) {
            throw new IllegalStateException();
        }
        if (valueCount != countValues(children)) {
            throw new IllegalStateException();
        }
        int depth = children[0].depth();
        for (BtreeNode<T> child : children) {
            if (child.depth() != depth) {
                throw new IllegalStateException();
            }
            child.checkInvariants(false);
        }
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return LazyMultiIterator.iterator(IndexedArray.retained(children));
    }

    @Nonnull
    @Override
    public Iterator<BtreeNode<T>> childIterator()
    {
        return IndexedIterator.iterator(IndexedArray.retained(children));
    }

    @Override
    public int depth()
    {
        return 1 + children[0].depth();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public BtreeNode<T>[] allocate(int size)
    {
        return (BtreeNode<T>[])new BtreeNode[size];
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BtreeBranchNode<?> that = (BtreeBranchNode<?>)o;
        return valueCount == that.valueCount &&
               Arrays.equals(children, that.children);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(children, valueCount);
    }

    @Nonnull
    private BtreeInsertResult<T> resultForInsert(boolean breakLeft,
                                                 int childIndex,
                                                 int sizeDelta,
                                                 BtreeInsertResult<T> result)
    {
        final BtreeNode<T>[] children = this.children;
        if (result.type == BtreeInsertResult.Type.INPLACE) {
            final BtreeNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, result.newNode);
            return BtreeInsertResult.createInPlace(new BtreeBranchNode<>(newChildren, valueCount + sizeDelta));
        } else {
            assert result.type == BtreeInsertResult.Type.SPLIT;
            final BtreeNode<T>[] newChildren = ArrayHelper.assignInsert(this, children, childIndex, result.newNode, result.extraNode);
            final int newLength = newChildren.length;
            if (newLength <= MAX_CHILDREN) {
                return BtreeInsertResult.createInPlace(new BtreeBranchNode<>(newChildren, valueCount + sizeDelta));
            } else {
                final int breakPoint = breakLeft ? MIN_CHILDREN : newLength - MIN_CHILDREN;
                return BtreeInsertResult.createSplit(new BtreeBranchNode<>(ArrayHelper.subArray(this, newChildren, 0, breakPoint)),
                                                     new BtreeBranchNode<>(ArrayHelper.subArray(this, newChildren, breakPoint, newLength)));
            }
        }
    }

    private Location<T> findIndexForGetAssign(int index)
    {
        int childIndex = 0;
        for (BtreeNode<T> child : children) {
            if (child.containsIndex(index)) {
                return new Location<>(child, childIndex, index);
            }
            childIndex += 1;
            index -= child.valueCount();
        }
        throw new IndexOutOfBoundsException();
    }

    private Location<T> findIndexForInsertAppend(int index)
    {
        if (index == 0) {
            return new Location<>(children[0], 0, 0);
        } else if (index == valueCount) {
            final int lastIndex = children.length - 1;
            final BtreeNode<T> lastChild = children[lastIndex];
            return new Location<>(lastChild, lastIndex, lastChild.valueCount());
        } else {
            return findIndexForGetAssign(index);
        }
    }

    private static <T> int countValues(BtreeNode<T>[] children)
    {
        int answer = 0;
        for (BtreeNode<T> child : children) {
            answer += child.valueCount();
        }
        return answer;
    }

    private static class Location<T>
    {
        private final BtreeNode<T> child;
        private final int childIndex;
        private final int logicalIndex;

        private Location(BtreeNode<T> child,
                         int childIndex,
                         int logicalIndex)
        {
            this.child = child;
            this.childIndex = childIndex;
            this.logicalIndex = logicalIndex;
        }
    }
}
