package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.list.ListCollisionMap;
import org.javimmutable.collections.tree.TreeCollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import static org.javimmutable.collections.hash.hamt.HamtBranchNode.*;

@ThreadSafe
class HamtBuilder<K, V>
    implements JImmutableMap.Builder<K, V>
{
    private CollisionMap<K, V> collisionMap;
    private Node<K, V> root;

    @Nonnull
    @Override
    public synchronized JImmutableMap<K, V> build()
    {
        if (root == null) {
            return JImmutableHashMap.of();
        } else {
            return JImmutableHashMap.forBuilder(root.toHamt(collisionMap), collisionMap);
        }
    }

    private CollisionMap<K, V> selectCollisionMapForKey(@Nonnull K key)
    {
        if (key instanceof Comparable) {
            return TreeCollisionMap.instance();
        } else {
            return ListCollisionMap.instance();
        }
    }

    @Nonnull
    @Override
    public synchronized JImmutableMap.Builder<K, V> add(@Nonnull K key,
                                                        V value)
    {
        if (root == null) {
            collisionMap = selectCollisionMapForKey(key);
            root = new Leaf<>(collisionMap, key.hashCode(), key, value);
        } else {
            root = root.add(collisionMap, key.hashCode(), key, value);
        }
        return this;
    }

    private static abstract class Node<K, V>
    {

        abstract Node<K, V> add(CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K key,
                                @Nullable V value);

        abstract HamtNode<K, V> toHamt(CollisionMap<K, V> collisionMap);
    }

    private static class Leaf<K, V>
        extends Node<K, V>
    {
        private CollisionMap.Node values;
        private int hashCode;

        private Leaf(int hashCode,
                     CollisionMap.Node values)
        {
            this.values = values;
            this.hashCode = hashCode;
        }

        private Leaf(@Nonnull CollisionMap<K, V> collisionMap,
                     int hashCode,
                     @Nonnull K key,
                     @Nullable V value)
        {
            this.values = collisionMap.update(collisionMap.emptyNode(), key, value);
            this.hashCode = hashCode;
        }

        @Override
        Node<K, V> add(CollisionMap<K, V> collisionMap,
                       int hashCode,
                       @Nonnull K key,
                       @Nullable V value)
        {
            if (hashCode == this.hashCode) {
                values = collisionMap.update(values, key, value);
                return this;
            } else {
                return new Branch<>(collisionMap, this, hashCode, key, value);
            }
        }

        @Override
        HamtNode<K, V> toHamt(CollisionMap<K, V> collisionMap)
        {
            return new HamtLeafNode<>(hashCode, values);
        }
    }

    private static class Branch<K, V>
        extends Node<K, V>
    {
        private CollisionMap.Node values;
        private Node<K, V>[] children;

        private Branch(@Nonnull CollisionMap<K, V> collisionMap,
                       Leaf<K, V> leaf,
                       int hashCode,
                       @Nonnull K key,
                       V value)
        {
            assert hashCode != leaf.hashCode;
            children = new Node[32];
            if (leaf.hashCode == 0) {
                values = leaf.values;
            } else {
                values = collisionMap.emptyNode();
                children[leaf.hashCode & MASK] = new Leaf<>(leaf.hashCode >>> SHIFT, leaf.values);
            }
            add(collisionMap, hashCode, key, value);
        }

        @Override
        Node<K, V> add(@Nonnull CollisionMap<K, V> collisionMap,
                       int hashCode,
                       @Nonnull K key,
                       @Nullable V value)
        {
            if (hashCode == 0) {
                values = collisionMap.update(values, key, value);
            } else {
                final int index = hashCode & MASK;
                final Node<K, V> child = children[index];
                if (child == null) {
                    children[index] = new Leaf<>(collisionMap, hashCode >>> SHIFT, key, value);
                } else {
                    children[index] = child.add(collisionMap, hashCode >>> SHIFT, key, value);
                }
            }
            assert invariant(collisionMap);
            return this;
        }

        @Override
        HamtNode<K, V> toHamt(@Nonnull CollisionMap<K, V> collisionMap)
        {
            int count = 0;
            for (Node<K, V> child : children) {
                if (child != null) {
                    count += 1;
                }
            }
            int size = collisionMap.size(values);
            int bitmask = 0;
            int bit = 1;
            final HamtNode<K, V>[] nodes = new HamtNode[count];
            int index = 0;
            for (Node<K, V> child : children) {
                if (child != null) {
                    final HamtNode<K, V> hamt = child.toHamt(collisionMap);
                    size += hamt.size(collisionMap);
                    nodes[index++] = hamt;
                    bitmask |= bit;
                }
                bit <<= 1;
            }
            return new HamtBranchNode<>(bitmask, values, nodes, size);
        }

        private boolean invariant(@Nonnull CollisionMap<K, V> collisionMap)
        {
            final int size = collisionMap.size(values);
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
            return (childCount > 0) && (size > 0 || childCount > 1 || leafCount == 0);
        }
    }
}
