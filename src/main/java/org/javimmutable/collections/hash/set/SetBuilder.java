///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.list.ListCollisionSet;
import org.javimmutable.collections.tree.TreeCollisionSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import static org.javimmutable.collections.common.HamtLongMath.*;

@NotThreadSafe
public class SetBuilder<T>
{
    private CollisionSet<T> collisionSet = ListCollisionSet.instance();
    private Node<T> root = new Empty<>();

    @Nonnull
    public SetNode<T> build()
    {
        return root.toSet(collisionSet);
    }

    public void clear()
    {
        collisionSet = ListCollisionSet.instance();
        root = new Empty<>();
    }

    public void add(@Nonnull T value)
    {
        if (root.isEmpty()) {
            collisionSet = selectCollisionSetForValue(value);
            root = new Leaf<>(collisionSet, value.hashCode(), value);
        } else {
            root = root.add(collisionSet, value.hashCode(), value);
        }
    }

    @Nonnull
    public CollisionSet<T> getCollisionSet()
    {
        return collisionSet;
    }

    public int size()
    {
        return root.size();
    }

    public static <T> CollisionSet<T> selectCollisionSetForValue(@Nonnull T value)
    {
        if (value instanceof Comparable) {
            return TreeCollisionSet.instance();
        } else {
            return ListCollisionSet.instance();
        }
    }

    private static abstract class Node<T>
    {
        @Nonnull
        abstract Node<T> add(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value);

        @Nonnull
        abstract SetNode<T> toSet(@Nonnull CollisionSet<T> collisionSet);

        abstract int size();

        abstract boolean isEmpty();
    }

    private static class Empty<T>
        extends Node<T>
    {
        @Nonnull
        @Override
        Node<T> add(@Nonnull CollisionSet<T> collisionSet,
                    int hashCode,
                    @Nonnull T value)
        {
            return new Leaf<>(collisionSet, hashCode, value);
        }

        @Nonnull
        @Override
        SetNode<T> toSet(@Nonnull CollisionSet<T> collisionSet)
        {
            return SetEmptyNode.of();
        }

        @Override
        int size()
        {
            return 0;
        }

        @Override
        boolean isEmpty()
        {
            return true;
        }
    }

    private static class Leaf<T>
        extends Node<T>
    {
        private final int hashCode;
        private CollisionSet.Node values;
        private int size;

        private Leaf(@Nonnull Leaf<T> other)
        {
            this.hashCode = remainderFromHashCode(other.hashCode);
            this.values = other.values;
            size = other.size;
        }

        private Leaf(@Nonnull CollisionSet<T> collisionSet,
                     int hashCode,
                     @Nonnull T value)
        {
            this.hashCode = hashCode;
            this.values = collisionSet.single(value);
            size = 1;
        }

        @Nonnull
        @Override
        Node<T> add(@Nonnull CollisionSet<T> collisionSet,
                    int hashCode,
                    @Nonnull T value)
        {
            if (hashCode == this.hashCode) {
                values = collisionSet.insert(values, value);
                size = collisionSet.size(values);
                return this;
            } else {
                return new Branch<>(collisionSet, this, hashCode, value);
            }
        }

        @Nonnull
        @Override
        SetNode<T> toSet(@Nonnull CollisionSet<T> collisionSet)
        {
            return SetMultiValueLeafNode.createLeaf(collisionSet, hashCode, values);
        }

        @Override
        int size()
        {
            return size;
        }

        @Override
        boolean isEmpty()
        {
            return false;
        }
    }

    private static class Branch<T>
        extends Node<T>
    {
        private final Node<T>[] children;
        private CollisionSet.Node values;
        private int size;

        private Branch(@Nonnull CollisionSet<T> collisionSet,
                       @Nonnull Leaf<T> leaf,
                       int hashCode,
                       @Nonnull T value)
        {
            assert hashCode != leaf.hashCode;
            children = new Node[ARRAY_SIZE];
            if (leaf.hashCode == 0) {
                values = leaf.values;
            } else {
                values = collisionSet.empty();
                children[indexFromHashCode(leaf.hashCode)] = new Leaf<>(leaf);
            }
            size = leaf.size;
            add(collisionSet, hashCode, value);
        }

        @Nonnull
        @Override
        Node<T> add(@Nonnull CollisionSet<T> collisionSet,
                    int hashCode,
                    @Nonnull T value)
        {
            if (hashCode == 0) {
                int beforeSize = collisionSet.size(values);
                values = collisionSet.insert(values, value);
                size = size - beforeSize + collisionSet.size(values);
            } else {
                final int index = indexFromHashCode(hashCode);
                final Node<T> beforeChild = children[index];
                if (beforeChild == null) {
                    children[index] = new Leaf<>(collisionSet, remainderFromHashCode(hashCode), value);
                    size += 1;
                } else {
                    // note: afterChild might be same object as beforeChild so capture size now
                    final int beforeSize = beforeChild.size();
                    final Node<T> afterChild = beforeChild.add(collisionSet, remainderFromHashCode(hashCode), value);
                    children[index] = afterChild;
                    size = size - beforeSize + afterChild.size();
                }
            }
            assert invariant(collisionSet);
            return this;
        }

        @Nonnull
        @Override
        SetNode<T> toSet(@Nonnull CollisionSet<T> collisionSet)
        {
            int count = 0;
            for (Node<T> child : children) {
                if (child != null) {
                    count += 1;
                }
            }
            long bitmask = 0;
            long bit = 1;
            final SetNode<T>[] nodes = new SetNode[count];
            int index = 0;
            for (Node<T> child : children) {
                if (child != null) {
                    final SetNode<T> node = child.toSet(collisionSet);
                    nodes[index++] = node;
                    bitmask = addBit(bitmask, bit);
                }
                bit <<= 1;
            }
            return new SetBranchNode<>(bitmask, values, nodes, size);
        }

        @Override
        int size()
        {
            return size;
        }

        @Override
        boolean isEmpty()
        {
            return false;
        }

        private boolean invariant(@Nonnull CollisionSet<T> collisionSet)
        {
            final int valuesSize = collisionSet.size(values);
            int childCount = 0;
            int leafCount = 0;
            for (Node<T> child : children) {
                if (child != null) {
                    childCount += 1;
                    if (child instanceof Leaf) {
                        leafCount += 1;
                    }
                }
            }
            return (childCount > 0) && (valuesSize > 0 || childCount > 1 || leafCount == 0);
        }
    }
}
