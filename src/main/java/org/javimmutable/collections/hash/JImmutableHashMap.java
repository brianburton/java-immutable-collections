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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.functional.Each2;
import org.javimmutable.collections.functional.Each2Throws;
import org.javimmutable.collections.functional.Sum2;
import org.javimmutable.collections.functional.Sum2Throws;
import org.javimmutable.collections.hash.hamt.HamtBuilder;
import org.javimmutable.collections.hash.hamt.HamtEmptyNode;
import org.javimmutable.collections.hash.hamt.HamtNode;
import org.javimmutable.collections.list.ListCollisionMap;
import org.javimmutable.collections.serialization.JImmutableHashMapProxy;
import org.javimmutable.collections.tree.TreeCollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.stream.Collector;

@Immutable
public class JImmutableHashMap<T, K, V>
    extends AbstractJImmutableMap<K, V>
    implements Serializable
{
    // we only need one instance of the transformations object
    static final CollisionMap LIST_COLLISION_MAP = ListCollisionMap.instance();

    // we only need one instance of the transformations object
    static final CollisionMap TREE_COLLISION_MAP = TreeCollisionMap.instance();

    // this is safe since the transformations object works for any possible K and V
    @SuppressWarnings("unchecked")
    static final JImmutableHashMap LIST_EMPTY = new JImmutableHashMap(HamtEmptyNode.of(), LIST_COLLISION_MAP);

    // this is safe since the transformations object works for any possible K and V
    @SuppressWarnings("unchecked")
    static final JImmutableHashMap TREE_EMPTY = new JImmutableHashMap(HamtEmptyNode.of(), TREE_COLLISION_MAP);

    private static final long serialVersionUID = -121805;

    private final HamtNode<K, V> root;
    private final CollisionMap<K, V> collisionMap;

    private JImmutableHashMap(HamtNode<K, V> root,
                              CollisionMap<K, V> collisionMap)
    {
        this.root = root;
        this.collisionMap = collisionMap;
    }

    /**
     * Returns an empty hash map.  The empty map will automatically select a collision handling strategy
     * on the first call to assign() based on the key for that call.  For this reason all keys used for a
     * given map must either implement or not implement Comparable.  If some keys implement it and some do
     * not the collision handling code will likely fail due to a class cast exception or a method
     * not defined exception.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> EmptyHashMap<K, V> of()
    {
        return EmptyHashMap.INSTANCE;
    }

    /**
     * Returns an empty map using the appropriate collision handling strategy for keys of the given
     * class.  All keys used with that map should derive from the specified class to avoid runtime
     * problems with incompatible keys.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableMap<K, V> of(Class<K> klass)
    {
        return klass.isAssignableFrom(Comparable.class) ? TREE_EMPTY : LIST_EMPTY;
    }

    /**
     * Returns an empty map using the appropriate collision handling strategy for the given key's
     * class.  All keys used with that map should derive from the specified key's class to avoid runtime
     * problems with incompatible keys.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableMap<K, V> forKey(K key)
    {
        return (key instanceof Comparable) ? TREE_EMPTY : LIST_EMPTY;
    }

    /**
     * Returns an empty map using linked lists for handling hash code collisions.  This is safe
     * for any type of key but is slower when many keys have the same hash code.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableMap<K, V> usingList()
    {
        return (JImmutableMap<K, V>)LIST_EMPTY;
    }

    /**
     * Returns an empty map using linked lists for handling hash code collisions.  This is faster
     * than the list based collision handling but depends on all keys implementing Comparable and
     * being able to compare themselves to all other keys.
     */
    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> JImmutableMap<K, V> usingTree()
    {
        return (JImmutableMap<K, V>)TREE_EMPTY;
    }

    public static <K, V> JImmutableMap.Builder<K, V> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    @Override
    public JImmutableMap.Builder<K, V> mapBuilder()
    {
        return builder();
    }

    @Nonnull
    public static <K, V> Collector<Entry<K, V>, ?, JImmutableMap<K, V>> createMapCollector()
    {
        return Collector.<Entry<K, V>, JImmutableMap.Builder<K, V>, JImmutableMap<K, V>>of(JImmutableHashMap::builder,
                                                                                           (b, v) -> b.add(v),
                                                                                           (b1, b2) -> b1.add(b2),
                                                                                           b -> b.build(),
                                                                                           Collector.Characteristics.UNORDERED,
                                                                                           Collector.Characteristics.CONCURRENT);
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        return root.getValueOr(collisionMap, key.hashCode(), key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        return root.find(collisionMap, key.hashCode(), key);
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull K key)
    {
        final Holder<V> value = find(key);
        if (value.isEmpty()) {
            return Holders.of();
        } else {
            return Holders.of(MapEntry.of(key, value.getValue()));
        }
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> assign(@Nonnull K key,
                                      V value)
    {
        final HamtNode<K, V> newRoot = root.assign(collisionMap, key.hashCode(), key, value);
        if (newRoot == root) {
            return this;
        } else {
            return new JImmutableHashMap<>(newRoot, collisionMap);
        }
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> update(@Nonnull K key,
                                      @Nonnull Func1<Holder<V>, V> generator)
    {
        final HamtNode<K, V> newRoot = root.update(collisionMap, key.hashCode(), key, generator);
        if (newRoot == root) {
            return this;
        } else {
            return new JImmutableHashMap<>(newRoot, collisionMap);
        }
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> delete(@Nonnull K key)
    {
        final HamtNode<K, V> newRoot = root.delete(collisionMap, key.hashCode(), key);
        if (newRoot == root) {
            return this;
        } else if (newRoot.isEmpty(collisionMap)) {
            return of();
        } else {
            return new JImmutableHashMap<>(newRoot, collisionMap);
        }
    }

    @Override
    public int size()
    {
        return root.size(collisionMap);
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return root.iterator(collisionMap);
    }

    @Override
    public void forEach(@Nonnull Each2<K, V> proc)
    {
        root.forEach(collisionMap, proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Each2Throws<K, V, E> proc)
        throws E
    {
        root.forEachThrows(collisionMap, proc);
    }

    @Override
    public <R> R reduce(R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return root.reduce(collisionMap, sum, proc);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return root.reduceThrows(collisionMap, sum, proc);
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants(collisionMap);
    }

    // for unit test to verify proper transforms selected
    CollisionMap getCollisionMap()
    {
        return collisionMap;
    }

    private Object writeReplace()
    {
        return new JImmutableHashMapProxy(this);
    }

    @ThreadSafe
    public static class Builder<K, V>
        implements JImmutableMap.Builder<K, V>
    {
        private final HamtBuilder<K, V> builder = new HamtBuilder<>();

        @Nonnull
        @Override
        public synchronized JImmutableMap<K, V> build()
        {
            final HamtNode<K, V> root = builder.build();
            final CollisionMap<K, V> collisionMap = builder.getCollisionMap();
            if (root.isEmpty(collisionMap)) {
                return of();
            } else {
                return new JImmutableHashMap<>(root, collisionMap);
            }
        }

        @Nonnull
        @Override
        public synchronized JImmutableMap.Builder<K, V> add(@Nonnull K key,
                                                            V value)
        {
            builder.add(key, value);
            return this;
        }

        @Override
        public synchronized int size()
        {
            return builder.size();
        }
    }
}
