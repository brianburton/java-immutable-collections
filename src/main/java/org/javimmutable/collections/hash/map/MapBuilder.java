///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.list.ListCollisionMap;
import org.javimmutable.collections.tree.TreeCollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import static org.javimmutable.collections.common.HamtLongMath.*;

@NotThreadSafe
public class MapBuilder<K, V>
{
    private CollisionMap<K, V> collisionMap = ListCollisionMap.instance();
    private Node<K, V> root = new Empty<>();

    @Nonnull
    public MapNode<K, V> build()
    {
        return root.toHamt(collisionMap);
    }

    public void clear()
    {
        collisionMap = ListCollisionMap.instance();
        root = new Empty<>();
    }

    public void add(@Nonnull K key,
                    V value)
    {
        if (root.isEmpty()) {
            collisionMap = selectCollisionMapForKey(key);
            root = new Leaf<>(collisionMap, key.hashCode(), key, value);
        } else {
            root = root.add(collisionMap, key.hashCode(), key, value);
        }
    }

    @Nonnull
    public CollisionMap<K, V> getCollisionMap()
    {
        return collisionMap;
    }

    public int size()
    {
        return root.size();
    }

    private static <K, V> CollisionMap<K, V> selectCollisionMapForKey(@Nonnull K key)
    {
        if (key instanceof Comparable) {
            return TreeCollisionMap.instance();
        } else {
            return ListCollisionMap.instance();
        }
    }

    private static abstract class Node<K, V>
    {
        @Nonnull
        abstract Node<K, V> add(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K key,
                                @Nullable V value);

        @Nonnull
        abstract MapNode<K, V> toHamt(@Nonnull CollisionMap<K, V> collisionMap);

        abstract int size();

        abstract boolean isEmpty();
    }

    private static class Empty<K, V>
        extends Node<K, V>
    {
        @Nonnull
        @Override
        Node<K, V> add(@Nonnull CollisionMap<K, V> collisionMap,
                       int hashCode,
                       @Nonnull K key,
                       @Nullable V value)
        {
            return new Leaf<>(collisionMap, hashCode, key, value);
        }

        @Nonnull
        @Override
        MapNode<K, V> toHamt(@Nonnull CollisionMap<K, V> collisionMap)
        {
            return MapEmptyNode.of();
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

    private static class Leaf<K, V>
        extends Node<K, V>
    {
        private final int hashCode;
        private CollisionMap.Node values;
        private int size;

        private Leaf(@Nonnull Leaf<K, V> other)
        {
            this.hashCode = remainderFromHashCode(other.hashCode);
            this.values = other.values;
            size = other.size;
        }

        private Leaf(@Nonnull CollisionMap<K, V> collisionMap,
                     int hashCode,
                     @Nonnull K key,
                     @Nullable V value)
        {
            this.hashCode = hashCode;
            this.values = collisionMap.single(key, value);
            size = 1;
        }

        @Nonnull
        @Override
        Node<K, V> add(@Nonnull CollisionMap<K, V> collisionMap,
                       int hashCode,
                       @Nonnull K key,
                       @Nullable V value)
        {
            if (hashCode == this.hashCode) {
                values = collisionMap.update(values, key, value);
                size = collisionMap.size(values);
                return this;
            } else {
                return new Branch<>(collisionMap, this, hashCode, key, value);
            }
        }

        @Nonnull
        @Override
        MapNode<K, V> toHamt(@Nonnull CollisionMap<K, V> collisionMap)
        {
            return MapMultiKeyLeafNode.createLeaf(collisionMap, hashCode, values);
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

    private static class Branch<K, V>
        extends Node<K, V>
    {
        private final Node<K, V>[] children;
        private CollisionMap.Node values;
        private int size;

        private Branch(@Nonnull CollisionMap<K, V> collisionMap,
                       @Nonnull Leaf<K, V> leaf,
                       int hashCode,
                       @Nonnull K key,
                       V value)
        {
            assert hashCode != leaf.hashCode;
            children = new Node[ARRAY_SIZE];
            if (leaf.hashCode == 0) {
                values = leaf.values;
            } else {
                values = collisionMap.empty();
                children[indexFromHashCode(leaf.hashCode)] = new Leaf<>(leaf);
            }
            size = leaf.size;
            add(collisionMap, hashCode, key, value);
        }

        @Nonnull
        @Override
        Node<K, V> add(@Nonnull CollisionMap<K, V> collisionMap,
                       int hashCode,
                       @Nonnull K key,
                       @Nullable V value)
        {
            if (hashCode == 0) {
                int beforeSize = collisionMap.size(values);
                values = collisionMap.update(values, key, value);
                size = size - beforeSize + collisionMap.size(values);
            } else {
                final int index = indexFromHashCode(hashCode);
                final Node<K, V> beforeChild = children[index];
                if (beforeChild == null) {
                    children[index] = new Leaf<>(collisionMap, remainderFromHashCode(hashCode), key, value);
                    size += 1;
                } else {
                    // note: afterChild might be same object as beforeChild so capture size now
                    final int beforeSize = beforeChild.size();
                    final Node<K, V> afterChild = beforeChild.add(collisionMap, remainderFromHashCode(hashCode), key, value);
                    children[index] = afterChild;
                    size = size - beforeSize + afterChild.size();
                }
            }
            assert invariant(collisionMap);
            return this;
        }

        @Nonnull
        @Override
        MapNode<K, V> toHamt(@Nonnull CollisionMap<K, V> collisionMap)
        {
            int count = 0;
            for (Node<K, V> child : children) {
                if (child != null) {
                    count += 1;
                }
            }
            long bitmask = 0;
            long bit = 1;
            final MapNode<K, V>[] nodes = new MapNode[count];
            int index = 0;
            for (Node<K, V> child : children) {
                if (child != null) {
                    final MapNode<K, V> hamt = child.toHamt(collisionMap);
                    nodes[index++] = hamt;
                    bitmask |= bit;
                }
                bit <<= 1;
            }
            return new MapBranchNode<>(bitmask, values, nodes, size);
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

        private boolean invariant(@Nonnull CollisionMap<K, V> collisionMap)
        {
            final int valuesSize = collisionMap.size(values);
            int childCount = 0;
            int leafCount = 0;
            for (Node<K, V> child : children) {
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
