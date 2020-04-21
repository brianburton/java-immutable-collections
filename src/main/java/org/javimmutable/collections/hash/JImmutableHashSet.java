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

import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractJImmutableSet;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.hash.set.SetBuilder;
import org.javimmutable.collections.hash.set.SetEmptyNode;
import org.javimmutable.collections.hash.set.SetNode;
import org.javimmutable.collections.serialization.JImmutableHashSetProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Immutable
public class JImmutableHashSet<T>
    extends AbstractJImmutableSet<T>
    implements Serializable
{
    private static final long serialVersionUID = -121805;

    private final SetNode<T> root;
    private final CollisionSet<T> collisionSet;

    JImmutableHashSet(@Nonnull SetNode<T> root,
                      @Nonnull CollisionSet<T> collisionSet)
    {
        this.root = root;
        this.collisionSet = collisionSet;
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableSet<T> of()
    {
        return EmptyHashSet.instance();
    }

    public static <T> JImmutableSet<T> of(@Nonnull T value)
    {
        final CollisionSet<T> collisionSet = SetBuilder.selectCollisionSetForValue(value);
        final SetNode<T> root = SetEmptyNode.<T>of().insert(collisionSet, value.hashCode(), value);
        return new JImmutableHashSet<>(root, collisionSet);
    }

    @Nonnull
    public static <T> JImmutableSet.Builder<T> builder()
    {
        return new HashSetBuilder<>();
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll()
    {
        return of();
    }

    @Override
    protected Set<T> emptyMutableSet()
    {
        return new HashSet<>();
    }

    @Nonnull
    @Override
    public JImmutableSet<T> insert(@Nonnull T value)
    {
        return createForUpdate(root.insert(collisionSet, value.hashCode(), value));
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && root.contains(collisionSet, value.hashCode(), value);
    }

    @Nonnull
    @Override
    public JImmutableSet<T> delete(T value)
    {
        return createForDelete(root.delete(collisionSet, value.hashCode(), value));
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        SetNode<T> newRoot = root;
        while (other.hasNext()) {
            T value = other.next();
            newRoot = newRoot.delete(collisionSet, value.hashCode(), value);
        }
        return createForDelete(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Iterator<? extends T> other)
    {
        SetNode<T> newRoot = root;
        while (other.hasNext()) {
            T value = other.next();
            newRoot = newRoot.insert(collisionSet, value.hashCode(), value);
        }
        return createForUpdate(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
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
    public JImmutableSet<T> intersection(@Nonnull Set<? extends T> otherSet)
    {
        SetNode<T> newRoot = root;
        for (T value : root.genericIterable(collisionSet)) {
            if (!otherSet.contains(value)) {
                newRoot = newRoot.delete(collisionSet, value.hashCode(), value);
            }
        }
        return createForDelete(newRoot);
    }

    @Override
    public int size()
    {
        return root.size(collisionSet);
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty(collisionSet);
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants(collisionSet);
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return root.iterator(collisionSet);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_UNORDERED;
    }

    private Object writeReplace()
    {
        return new JImmutableHashSetProxy(this);
    }

    private JImmutableSet<T> createForUpdate(@Nonnull SetNode<T> newRoot)
    {
        if (root == newRoot) {
            return this;
        } else {
            assert newRoot.size(collisionSet) > 0;
            return new JImmutableHashSet<>(newRoot, collisionSet);
        }
    }

    private JImmutableSet<T> createForDelete(@Nonnull SetNode<T> newRoot)
    {
        if (root == newRoot) {
            return this;
        } else if (root.isEmpty(collisionSet)) {
            return of();
        } else {
            return new JImmutableHashSet<>(newRoot, collisionSet);
        }
    }
}
