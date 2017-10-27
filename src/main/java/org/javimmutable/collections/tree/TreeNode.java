///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.iterators.SplitableIterable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Comparator;

/**
 * Abstract base class for 2-3 tree nodes.  Provides public methods for searching and modifying
 * the tree and package private methods used to implement the public methods.
 */
@Immutable
public abstract class TreeNode<K, V>
    implements Cursorable<JImmutableMap.Entry<K, V>>,
               SplitableIterable<JImmutableMap.Entry<K, V>>
{
    public static <K, V> TreeNode<K, V> of()
    {
        return EmptyNode.of();
    }

    /**
     * Return the value matching key or defaultValue if no match is found.  Searches this node
     * and its appropriate children.
     */
    public abstract V getValueOr(Comparator<K> comparator,
                                 K key,
                                 V defaultValue);

    /**
     * Return a (possibly empty) Holder containing the value matching key.  Searches this node
     * and its appropriate children.
     */
    public abstract Holder<V> find(Comparator<K> comparator,
                                   K key);

    /**
     * Return a (possibly empty) Holder containing the an Entry matching key.  Searches this node
     * and its appropriate children.
     */
    public abstract Holder<JImmutableMap.Entry<K, V>> findEntry(Comparator<K> comparator,
                                                                K key);

    /**
     * Adds this node's value and all of its children's value to the collection.
     */
    public abstract void addEntriesTo(Collection<JImmutableMap.Entry<K, V>> collection);

    /**
     * Returns a Cursor visiting all entries in sorted order.
     */
    @Nonnull
    public abstract Cursor<JImmutableMap.Entry<K, V>> cursor();

    /**
     * Assign the specified value to the specified key.  Returns a node (possibly this same node)
     * reflecting the assignment and updates sizeDelta with the change in size (if any).
     */
    public TreeNode<K, V> assign(Comparator<K> comparator,
                                 K key,
                                 V value,
                                 MutableDelta sizeDelta)
    {
        UpdateResult<K, V> result = assignImpl(comparator, key, value);
        switch (result.type) {
        case UNCHANGED:
            return this;

        case INPLACE:
            sizeDelta.add(result.sizeDelta);
            return result.newNode;

        case SPLIT:
            sizeDelta.add(result.sizeDelta);
            return new TwoNode<K, V>(result.newNode,
                                     result.extraNode,
                                     result.newNode.getMaxKey(),
                                     result.extraNode.getMaxKey());
        }
        throw new RuntimeException();
    }

    /**
     * Deletes the specified key.  Returns a node (possibly this same node)
     * reflecting the deletion and updates sizeDelta with the change in size (if any).
     */
    public TreeNode<K, V> delete(Comparator<K> comparator,
                                 K key,
                                 MutableDelta sizeDelta)
    {
        DeleteResult<K, V> result = deleteImpl(comparator, key);
        switch (result.type) {
        case UNCHANGED:
            return this;

        default:
            sizeDelta.subtract(1);
            return result.node;
        }
    }

    /**
     * Return true if this node contains no children or value.
     */
    public boolean isEmpty()
    {
        return false;
    }

    abstract int verifyDepthsMatch();

    abstract K getMaxKey();

    abstract UpdateResult<K, V> assignImpl(Comparator<K> comparator,
                                           K key,
                                           V value);

    abstract DeleteResult<K, V> deleteImpl(Comparator<K> comparator,
                                           K key);

    abstract DeleteMergeResult<K, V> leftDeleteMerge(TreeNode<K, V> node);

    abstract DeleteMergeResult<K, V> rightDeleteMerge(TreeNode<K, V> node);
}
