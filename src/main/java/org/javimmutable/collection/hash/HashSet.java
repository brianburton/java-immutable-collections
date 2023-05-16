///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection.hash;

import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.ISet;
import org.javimmutable.collection.ISetBuilder;
import org.javimmutable.collection.Proc1Throws;
import org.javimmutable.collection.SplitableIterator;
import org.javimmutable.collection.array.ArrayAssignMapper;
import org.javimmutable.collection.array.ArrayContainsMapper;
import org.javimmutable.collection.array.ArrayDeleteMapper;
import org.javimmutable.collection.array.ArrayIterationMapper;
import org.javimmutable.collection.array.TrieArrayNode;
import org.javimmutable.collection.common.AbstractSet;
import org.javimmutable.collection.common.CollisionSet;
import org.javimmutable.collection.common.StreamConstants;
import org.javimmutable.collection.hash.set.ArraySetNode;
import org.javimmutable.collection.hash.set.ArraySingleValueSetNode;
import org.javimmutable.collection.iterators.GenericIterator;
import org.javimmutable.collection.list.ListCollisionSet;
import org.javimmutable.collection.serialization.HashSetProxy;
import org.javimmutable.collection.tree.TreeCollisionSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

@Immutable
public class HashSet<T>
    extends AbstractSet<T>
    implements ArrayAssignMapper<T, T, ArraySetNode<T>>,
               ArrayContainsMapper<T, ArraySetNode<T>>,
               ArrayIterationMapper<T, T, ArraySetNode<T>>,
               ArrayDeleteMapper<T, ArraySetNode<T>>,
               Serializable
{
    private static final long serialVersionUID = -121805;

    private final TrieArrayNode<ArraySetNode<T>> root;
    private final CollisionSet<T> collisionSet;

    HashSet(@Nonnull TrieArrayNode<ArraySetNode<T>> root,
            @Nonnull CollisionSet<T> collisionSet)
    {
        this.root = root;
        this.collisionSet = collisionSet;
    }

    HashSet(@Nonnull T value)
    {
        root = TrieArrayNode.<ArraySetNode<T>>empty().mappedAssign(this, value, value);
        collisionSet = selectCollisionSetForValue(value);
    }

    public static <T> ISet<T> of()
    {
        return EmptyHashSet.instance();
    }

    static <T> ISet<T> usingList()
    {
        return new HashSet<>(TrieArrayNode.empty(), ListCollisionSet.instance());
    }

    static <T> ISet<T> usingTree()
    {
        return new HashSet<>(TrieArrayNode.empty(), TreeCollisionSet.instance());
    }

    @Nonnull
    public static <T> ISetBuilder<T> builder()
    {
        return new HashSetBuilder<>();
    }

    static <T> CollisionSet<T> selectCollisionSetForValue(@Nonnull T value)
    {
        if (value instanceof Comparable) {
            return TreeCollisionSet.instance();
        } else {
            return ListCollisionSet.instance();
        }
    }

    @Nonnull
    @Override
    public ISet<T> deleteAll()
    {
        return of();
    }

    @Override
    protected Set<T> emptyMutableSet()
    {
        return new java.util.HashSet<>();
    }

    @Nonnull
    @Override
    public ISet<T> insert(@Nonnull T value)
    {
        return createForUpdate(root.mappedAssign(this, value, value));
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return value != null && root.mappedContains(this, value);
    }

    @Nonnull
    @Override
    public ISet<T> delete(T value)
    {
        return createForDelete(root.mappedDelete(this, value));
    }

    @Nonnull
    @Override
    public ISet<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        TrieArrayNode<ArraySetNode<T>> newRoot = root;
        while (other.hasNext()) {
            T value = other.next();
            newRoot = newRoot.mappedDelete(this, value);
        }
        return createForDelete(newRoot);
    }

    @Nonnull
    @Override
    public ISet<T> union(@Nonnull Iterator<? extends T> other)
    {
        TrieArrayNode<ArraySetNode<T>> newRoot = root;
        while (other.hasNext()) {
            T value = other.next();
            newRoot = newRoot.mappedAssign(this, value, value);
        }
        return createForUpdate(newRoot);
    }

    @Nonnull
    @Override
    public ISet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }

        if (!values.hasNext()) {
            return deleteAll();
        }

        Set<T> otherSet = emptyMutableSet();
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                otherSet.add(value);
            }
        }

        return intersection(otherSet);
    }

    @Nonnull
    @Override
    public ISet<T> intersection(@Nonnull Set<? extends T> otherSet)
    {
        TrieArrayNode<ArraySetNode<T>> newRoot = root;
        for (T value : root.mappedKeys(this)) {
            if (!otherSet.contains(value)) {
                newRoot = newRoot.mappedDelete(this, value);
            }
        }
        return createForDelete(newRoot);
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants(this);
    }

    @Override
    public void forEach(@Nonnull Consumer<? super T> action)
    {
        root.forEach(node -> node.forEach(collisionSet, action::accept));
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        root.forEachThrows(node -> node.forEachThrows(collisionSet, proc));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return root.mappedKeys(this).iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_UNORDERED;
    }

    @Override
    public boolean mappedContains(@Nonnull ArraySetNode<T> mapping,
                                  @Nonnull T key)
    {
        return mapping.contains(collisionSet, key);
    }

    @Nonnull
    @Override
    public ArraySetNode<T> mappedAssign(@Nonnull T key,
                                        T ignored)
    {
        assert key == ignored;
        return new ArraySingleValueSetNode<>(key);
    }

    @Nonnull
    @Override
    public ArraySetNode<T> mappedAssign(@Nonnull ArraySetNode<T> current,
                                        @Nonnull T key,
                                        T ignored)
    {
        assert key == ignored;
        return current.insert(collisionSet, key);
    }

    @Nullable
    @Override
    public ArraySetNode<T> mappedDelete(@Nonnull ArraySetNode<T> current,
                                        @Nonnull T key)
    {
        return current.delete(collisionSet, key);
    }

    @Override
    public int mappedSize(@Nonnull ArraySetNode<T> mapping)
    {
        return mapping.size(collisionSet);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<T> mappedKeys(@Nonnull ArraySetNode<T> mapping)
    {
        return mapping.values(collisionSet);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<T> mappedValues(@Nonnull ArraySetNode<T> mapping)
    {
        return mapping.values(collisionSet);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<IMapEntry<T, T>> mappedEntries(@Nonnull ArraySetNode<T> mapping)
    {
        return GenericIterator.transformIterable(mapping.values(collisionSet), k -> IMapEntry.of(k, k));
    }

    private Object writeReplace()
    {
        return new HashSetProxy(this);
    }

    private ISet<T> createForUpdate(@Nonnull TrieArrayNode<ArraySetNode<T>> newRoot)
    {
        if (root == newRoot) {
            return this;
        } else {
            assert newRoot.size() > 0;
            return new HashSet<>(newRoot, collisionSet);
        }
    }

    private ISet<T> createForDelete(@Nonnull TrieArrayNode<ArraySetNode<T>> newRoot)
    {
        if (root == newRoot) {
            return this;
        } else if (newRoot.isEmpty()) {
            return of();
        } else {
            return new HashSet<>(newRoot, collisionSet);
        }
    }
}
