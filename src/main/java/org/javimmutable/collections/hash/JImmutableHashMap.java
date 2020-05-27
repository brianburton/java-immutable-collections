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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.Temp;
import org.javimmutable.collections.array.ArrayValueMapper;
import org.javimmutable.collections.array.TrieArrayBuilder;
import org.javimmutable.collections.array.TrieArrayNode;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.hash.map.ArrayMapNode;
import org.javimmutable.collections.hash.map.ArraySingleValueMapNode;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.list.ListCollisionMap;
import org.javimmutable.collections.serialization.JImmutableHashMapProxy;
import org.javimmutable.collections.tree.TreeCollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.stream.Collector;

@Immutable
public class JImmutableHashMap<T, K, V>
    extends AbstractJImmutableMap<K, V>
    implements ArrayValueMapper<K, V, ArrayMapNode<K, V>>,
               Serializable
{
    // we only need one instance of the transformations object
    @SuppressWarnings({"rawtypes"})
    static final CollisionMap LIST_COLLISION_MAP = ListCollisionMap.instance();

    // we only need one instance of the transformations object
    @SuppressWarnings({"rawtypes"})
    static final CollisionMap TREE_COLLISION_MAP = TreeCollisionMap.instance();

    // this is safe since the transformations object works for any possible K and V
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final JImmutableHashMap LIST_EMPTY = new JImmutableHashMap(TrieArrayNode.empty(), LIST_COLLISION_MAP);

    // this is safe since the transformations object works for any possible K and V
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final JImmutableHashMap TREE_EMPTY = new JImmutableHashMap(TrieArrayNode.empty(), TREE_COLLISION_MAP);

    private static final long serialVersionUID = -121805;

    private final TrieArrayNode<ArrayMapNode<K, V>> root;
    private final CollisionMap<K, V> collisionMap;

    private JImmutableHashMap(TrieArrayNode<ArrayMapNode<K, V>> root,
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
        return root.mappedGetValueOr(this, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        return root.mappedFind(this, key);
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
        final TrieArrayNode<ArrayMapNode<K, V>> newRoot = root.mappedAssign(this, key, value);
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
        final TrieArrayNode<ArrayMapNode<K, V>> newRoot = root.mappedUpdate(this, key, generator);
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
        final TrieArrayNode<ArrayMapNode<K, V>> newRoot = root.mappedDelete(this, key);
        if (newRoot == root) {
            return this;
        } else if (newRoot.isEmpty()) {
            return of();
        } else {
            return new JImmutableHashMap<>(newRoot, collisionMap);
        }
    }

    @Override
    public int size()
    {
        return root.size();
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
        return root.mappedEntries(this).iterator();
    }

    @Override
    public void forEach(@Nonnull Proc2<K, V> proc)
    {
        root.forEach(node -> node.forEach(collisionMap, proc));
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        root.forEachThrows(node -> node.forEachThrows(collisionMap, proc));
    }

    @Override
    public <R> R reduce(R startingSum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        final Temp.Var1<R> sum = Temp.var(startingSum);
        forEach((k, v) -> sum.x = proc.apply(sum.x, k, v));
        return sum.x;
    }

    @Override
    public <R, E extends Exception> R reduceThrows(R startingSum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        final Temp.Var1<R> sum = Temp.var(startingSum);
        forEachThrows((k, v) -> sum.x = proc.apply(sum.x, k, v));
        return sum.x;
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants(this);
    }

    @Override
    public V mappedGetValueOr(@Nonnull ArrayMapNode<K, V> mapping,
                              @Nonnull K key,
                              V defaultValue)
    {
        return mapping.getValueOr(collisionMap, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> mappedFind(@Nonnull ArrayMapNode<K, V> mapping,
                                @Nonnull K key)
    {
        return mapping.find(collisionMap, key);
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> mappedAssign(@Nonnull K key,
                                           V value)
    {
        return new ArraySingleValueMapNode<>(key, value);
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> mappedAssign(@Nonnull ArrayMapNode<K, V> current,
                                           @Nonnull K key,
                                           V value)
    {
        return current.assign(collisionMap, key, value);
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> mappedUpdate(@Nonnull ArrayMapNode<K, V> current,
                                           @Nonnull K key,
                                           @Nonnull Func1<Holder<V>, V> generator)
    {
        return current.update(collisionMap, key, generator);
    }

    @Nullable
    @Override
    public ArrayMapNode<K, V> mappedDelete(@Nonnull ArrayMapNode<K, V> current,
                                           @Nonnull K key)
    {
        return current.delete(collisionMap, key);
    }

    @Override
    public int mappedSize(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.size(collisionMap);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<K> mappedKeys(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.keys(collisionMap);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<V> mappedValues(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.values(collisionMap);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<Entry<K, V>> mappedEntries(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.entries(collisionMap);
    }

    // for unit test to verify proper transforms selected
    @SuppressWarnings("rawtypes")
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
        implements JImmutableMap.Builder<K, V>,
                   ArrayValueMapper<K, V, ArrayMapNode<K, V>>

    {
        private final TrieArrayBuilder<ArrayMapNode<K, V>> builder = new TrieArrayBuilder<>();
        private CollisionMap<K, V> collisionMap = ListCollisionMap.instance();

        @Nonnull
        @Override
        public synchronized JImmutableMap<K, V> build()
        {
            final TrieArrayNode<ArrayMapNode<K, V>> root = builder.buildRoot();
            if (root.isEmpty()) {
                return of();
            } else {
                return new JImmutableHashMap<>(root, collisionMap);
            }
        }

        @Nonnull
        @Override
        public synchronized JImmutableMap.Builder<K, V> clear()
        {
            builder.reset();
            collisionMap = ListCollisionMap.instance();
            return this;
        }

        @Nonnull
        @Override
        public synchronized JImmutableMap.Builder<K, V> add(@Nonnull K key,
                                                            V value)
        {
            if (builder.size() == 0) {
                if (key instanceof Comparable) {
                    collisionMap = TreeCollisionMap.instance();
                } else {
                    collisionMap = ListCollisionMap.instance();
                }
            }
            builder.assign(this, key, value);
            return this;
        }

        @Override
        public synchronized int size()
        {
            return builder.size();
        }

        @Override
        public V mappedGetValueOr(@Nonnull ArrayMapNode<K, V> mapping,
                                  @Nonnull K key,
                                  V defaultValue)
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public Holder<V> mappedFind(@Nonnull ArrayMapNode<K, V> mapping,
                                    @Nonnull K key)
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public synchronized ArrayMapNode<K, V> mappedAssign(@Nonnull K key,
                                                            V value)
        {
            return new ArraySingleValueMapNode<>(key, value);
        }

        @Nonnull
        @Override
        public synchronized ArrayMapNode<K, V> mappedAssign(@Nonnull ArrayMapNode<K, V> current,
                                                            @Nonnull K key,
                                                            V value)
        {
            return current.assign(collisionMap, key, value);
        }

        @Nonnull
        @Override
        public ArrayMapNode<K, V> mappedUpdate(@Nonnull ArrayMapNode<K, V> current,
                                               @Nonnull K key,
                                               @Nonnull Func1<Holder<V>, V> generator)
        {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public synchronized ArrayMapNode<K, V> mappedDelete(@Nonnull ArrayMapNode<K, V> current,
                                                            @Nonnull K key)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized int mappedSize(@Nonnull ArrayMapNode<K, V> mapping)
        {
            return mapping.size(collisionMap);
        }

        @Nonnull
        @Override
        public synchronized GenericIterator.Iterable<K> mappedKeys(@Nonnull ArrayMapNode<K, V> mapping)
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public synchronized GenericIterator.Iterable<V> mappedValues(@Nonnull ArrayMapNode<K, V> mapping)
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public synchronized GenericIterator.Iterable<JImmutableMap.Entry<K, V>> mappedEntries(@Nonnull ArrayMapNode<K, V> mapping)
        {
            throw new UnsupportedOperationException();
        }
    }
}
