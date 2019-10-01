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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * A Node containing one value and two (possibly empty) children.  Class invariant
 * is that the difference in depth of the two children is no more than one.  Rotations
 * are used when necessary to maintain that invariant whenever ValueNodes are constructed.
 * Additionally values in left subtree are always less than this nodes value and values
 * in right subtree are always greater than this nodes value.
 */
@Immutable
class ValueNode<K, V>
    extends AbstractNode<K, V>
{
    private final K key;
    private final V value;
    private final AbstractNode<K, V> left;
    private final AbstractNode<K, V> right;
    private final int depth;
    private final int size;

    ValueNode(K key,
              V value,
              AbstractNode<K, V> left,
              AbstractNode<K, V> right)
    {
        this.key = key;
        this.value = value;
        this.left = left;
        this.right = right;
        depth = 1 + Math.max(left.depth(), right.depth());
        size = 1 + left.size() + right.size();
    }

    /**
     * Convenience method to create a node with two empty children.
     */
    static <K, V> AbstractNode<K, V> instance(K key,
                                              V value)
    {
        return new ValueNode<>(key, value, FringeNode.instance(), FringeNode.instance());
    }

    /**
     * Creates a new node with one value while enforcing the class invariant by ensuring
     * depth of the two children are within one of each other.  Rotation is performed
     * when invariant would be violated to bring the depth of the two children
     * back into range.
     */
    static <K, V> AbstractNode<K, V> balance(@Nonnull K key,
                                             @Nullable V value,
                                             @Nonnull AbstractNode<K, V> left,
                                             @Nonnull AbstractNode<K, V> right)
    {
        final int diff = left.depth() - right.depth();
        if (diff < -1) {
            return rotateLeft(key, value, left, right);
        } else if (diff > 1) {
            return rotateRight(key, value, left, right);
        } else {
            return new ValueNode<>(key, value, left, right);
        }
    }

    @Nonnull
    private static <K, V> AbstractNode<K, V> rotateRight(@Nonnull K key,
                                                         @Nullable V value,
                                                         @Nonnull AbstractNode<K, V> left,
                                                         @Nonnull AbstractNode<K, V> right)
    {
        left = ensureLeftBranchTaller(left);
        final AbstractNode<K, V> newRight = new ValueNode<>(key, value, left.right(), right);
        return new ValueNode<>(left.key(), left.value(), left.left(), newRight);
    }

    @Nonnull
    private static <K, V> AbstractNode<K, V> rotateLeft(@Nonnull K key,
                                                        @Nullable V value,
                                                        @Nonnull AbstractNode<K, V> left,
                                                        @Nonnull AbstractNode<K, V> right)
    {
        right = ensureRightBranchTaller(right);
        final AbstractNode<K, V> newLeft = new ValueNode<>(key, value, left, right.left());
        return new ValueNode<>(right.key(), right.value(), newLeft, right.right());
    }

    @Nonnull
    private static <K, V> AbstractNode<K, V> ensureLeftBranchTaller(@Nonnull AbstractNode<K, V> node)
    {
        final AbstractNode<K, V> left = node.left();
        final AbstractNode<K, V> right = node.right();
        if (right.depth() > left.depth()) {
            final AbstractNode<K, V> newLeft = new ValueNode<>(node.key(), node.value(), left, right.left());
            node = new ValueNode<>(right.key(), right.value(), newLeft, right.right());
        }
        return node;
    }

    @Nonnull
    private static <K, V> AbstractNode<K, V> ensureRightBranchTaller(@Nonnull AbstractNode<K, V> node)
    {
        final AbstractNode<K, V> left = node.left();
        final AbstractNode<K, V> right = node.right();
        if (left.depth() > right.depth()) {
            final AbstractNode<K, V> newRight = new ValueNode<>(node.key(), node.value(), left.right(), right);
            node = new ValueNode<>(left.key(), left.value(), left.left(), newRight);
        }
        return node;
    }

    @Nonnull
    @Override
    public AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                                     @Nonnull K key,
                                     @Nullable V value)
    {
        final K thisKey = this.key;
        final V thisValue = this.value;
        final AbstractNode<K, V> left = this.left;
        final AbstractNode<K, V> right = this.right;
        final int diff = comp.compare(key, thisKey);
        if (diff == 0) {
            if (value != thisValue) {
                return new ValueNode<>(key, value, left, right);
            }
        } else if (diff < 0) {
            final AbstractNode<K, V> newLeft = left.assign(comp, key, value);
            if (newLeft != left) {
                return balance(thisKey, thisValue, newLeft, right);
            }
        } else {
            final AbstractNode<K, V> newRight = right.assign(comp, key, value);
            if (newRight != right) {
                return balance(thisKey, thisValue, left, newRight);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    public AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
                                     @Nonnull K key,
                                     @Nonnull Func1<Holder<V>, V> generator)
    {
        final K thisKey = this.key;
        final V thisValue = this.value;
        final AbstractNode<K, V> left = this.left;
        final AbstractNode<K, V> right = this.right;
        final int diff = comp.compare(key, thisKey);
        if (diff == 0) {
            final V newValue = generator.apply(Holders.of(thisValue));
            if (newValue != thisValue) {
                return new ValueNode<>(key, newValue, left, right);
            }
        } else if (diff < 0) {
            final AbstractNode<K, V> newLeft = left.update(comp, key, generator);
            if (newLeft != left) {
                return balance(thisKey, thisValue, newLeft, right);
            }
        } else {
            final AbstractNode<K, V> newRight = right.update(comp, key, generator);
            if (newRight != right) {
                return balance(thisKey, thisValue, left, newRight);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    public AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                                     @Nonnull K key)
    {
        final K thisKey = this.key;
        final V thisValue = this.value;
        final AbstractNode<K, V> left = this.left;
        final AbstractNode<K, V> right = this.right;
        final int diff = comp.compare(key, thisKey);
        if (diff == 0) {
            if (left.isEmpty()) {
                return right;
            } else if (right.isEmpty()) {
                return left;
            } else if (left.depth() > right.depth()) {
                final DeleteResult<K, V> result = left.deleteRightmost();
                return balance(result.key, result.value, result.remainder, right);
            } else {
                final DeleteResult<K, V> result = right.deleteLeftmost();
                return balance(result.key, result.value, left, result.remainder);
            }
        } else if (diff < 0) {
            final AbstractNode<K, V> newLeft = left.delete(comp, key);
            if (newLeft != left) {
                return balance(thisKey, thisValue, newLeft, right);
            }
        } else {
            final AbstractNode<K, V> newRight = right.delete(comp, key);
            if (newRight != right) {
                return balance(thisKey, thisValue, left, newRight);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeftmost()
    {
        if (left.isEmpty()) {
            return new DeleteResult<>(key, value, right);
        } else {
            final DeleteResult<K, V> result = left.deleteLeftmost();
            return result.withRemainder(balance(key, value, result.remainder, right));
        }
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteRightmost()
    {
        if (right.isEmpty()) {
            return new DeleteResult<>(key, value, left);
        } else {
            final DeleteResult<K, V> result = right.deleteRightmost();
            return result.withRemainder(balance(key, value, left, result.remainder));
        }
    }

    @Nullable
    @Override
    public V get(@Nonnull Comparator<K> comp,
                 @Nonnull K key,
                 V defaultValue)
    {
        final int diff = comp.compare(key, this.key);
        if (diff == 0) {
            return value;
        } else if (diff < 0) {
            return left.get(comp, key, defaultValue);
        } else {
            return right.get(comp, key, defaultValue);
        }
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull Comparator<K> comp,
                          @Nonnull K key)
    {
        final int diff = comp.compare(key, this.key);
        if (diff == 0) {
            return Holders.of(value);
        } else if (diff < 0) {
            return left.find(comp, key);
        } else {
            return right.find(comp, key);
        }
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                         @Nonnull K key)
    {
        final int diff = comp.compare(key, this.key);
        if (diff == 0) {
            return Holders.of(entry());
        } else if (diff < 0) {
            return left.findEntry(comp, key);
        } else {
            return right.findEntry(comp, key);
        }
    }

    private Entry<K, V> entry()
    {
        return MapEntry.of(key, value);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    int depth()
    {
        return depth;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Nonnull
    @Override
    K key()
    {
        return key;
    }

    @Nullable
    @Override
    V value()
    {
        return value;
    }

    @Nonnull
    @Override
    AbstractNode<K, V> left()
    {
        return left;
    }

    @Nonnull
    @Override
    AbstractNode<K, V> right()
    {
        return right;
    }

    @Override
    public void checkInvariants(@Nonnull Comparator<K> comp)
    {
        if (key == null) {
            throw new IllegalStateException();
        }
        if (left.size() > 0 && comp.compare(left.key(), key) >= 0) {
            throw new IllegalStateException();
        }
        if (right.size() > 0 && comp.compare(right.key(), key) <= 0) {
            throw new IllegalStateException();
        }
        if (Math.abs(left.depth() - right.depth()) > 1) {
            throw new IllegalStateException();
        }
        if (depth != 1 + Math.max(left.depth(), right.depth())) {
            throw new IllegalStateException();
        }
        if (size != 1 + left.size() + right.size()) {
            throw new IllegalStateException();
        }
        left.checkInvariants(comp);
        right.checkInvariants(comp);
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        assert offset >= 0 && limit <= size && offset <= limit;
        return GenericIterator.indexedState(parent, IndexedHelper.indexed(left, GenericIterator.valueIterable(MapEntry.of(key, value)), right), offset, limit);
    }

    @Override
    public int iterableSize()
    {
        return size;
    }

    @Override
    void forEach(@Nonnull Proc2<K, V> proc)
    {
        left.forEach(proc);
        proc.apply(key, value);
        right.forEach(proc);
    }

    @Override
    <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        left.forEachThrows(proc);
        proc.apply(key, value);
        right.forEachThrows(proc);
    }

    @Override
    <R> R reduce(R sum,
                 @Nonnull Sum2<K, V, R> proc)
    {
        sum = left.reduce(sum, proc);
        sum = proc.apply(sum, key, value);
        sum = right.reduce(sum, proc);
        return sum;
    }

    @Override
    <R, E extends Exception> R reduceThrows(R sum,
                                            @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        sum = left.reduceThrows(sum, proc);
        sum = proc.apply(sum, key, value);
        sum = right.reduceThrows(sum, proc);
        return sum;
    }
}
