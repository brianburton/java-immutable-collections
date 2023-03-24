///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import static org.javimmutable.collections.MapEntry.entry;

import java.util.Comparator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.iterators.GenericIterator;

/**
 * ¬ * A Node containing one value and no children.
 */
public class LeafNode<K, V>
    extends AbstractNode<K, V>
{
    private final K key;
    private final V value;

    LeafNode(@Nonnull K key,
             @Nullable V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    boolean containsKey(@Nonnull Comparator<K> comp,
                        @Nonnull K key)
    {
        return isMatch(comp, key);
    }

    @Override
    V get(@Nonnull Comparator<K> comp,
          @Nonnull K key,
          V defaultValue)
    {
        return isMatch(comp, key) ? value : defaultValue;
    }

    @Nonnull
    @Override
    Holder<V> find(@Nonnull Comparator<K> comp,
                   @Nonnull K key)
    {
        if (isMatch(comp, key)) {
            return Holders.of(value);
        } else {
            return Holders.of();
        }
    }

    @Nonnull
    @Override
    Holder<IMapEntry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                      @Nonnull K key)
    {
        if (isMatch(comp, key)) {
            return Holders.of(asEntry());
        } else {
            return Holders.of();
        }
    }

    @Override
    boolean isEmpty()
    {
        return false;
    }

    @Override
    int size()
    {
        return 1;
    }

    @Nonnull
    @Override
    AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                              @Nonnull K key,
                              @Nullable V value)
    {
        if (isMatch(comp, key)) {
            if (this.value == value) {
                return this;
            } else {
                return new LeafNode<>(key, value);
            }
        } else {
            return ValueNode.instance(comp, this.key, this.value, key, value);
        }
    }

    @Nonnull
    @Override
    AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                              @Nonnull K key)
    {
        if (isMatch(comp, key)) {
            return FringeNode.instance();
        } else {
            return this;
        }
    }

    @Nonnull
    @Override
    AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
                              @Nonnull K key,
                              @Nonnull Func1<Holder<V>, V> generator)
    {
        if (isMatch(comp, key)) {
            final V value = generator.apply(Holders.of(this.value));
            if (this.value == value) {
                return this;
            } else {
                return new LeafNode<>(key, value);
            }
        } else {
            final V value = generator.apply(Holders.of());
            return ValueNode.instance(comp, this.key, this.value, key, value);
        }
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeftmost()
    {
        return new DeleteResult<>(key, value, FringeNode.instance());
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteRightmost()
    {
        return new DeleteResult<>(key, value, FringeNode.instance());
    }

    @Override
    int depth()
    {
        return 1;
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
    AbstractNode<K, V> leftMost()
    {
        return this;
    }

    @Nonnull
    @Override
    AbstractNode<K, V> left()
    {
        return FringeNode.instance();
    }

    @Nonnull
    @Override
    AbstractNode<K, V> right()
    {
        return FringeNode.instance();
    }

    @Override
    void checkInvariants(@Nonnull Comparator<K> comp)
    {
    }

    @Override
    void forEach(@Nonnull Proc2<K, V> proc)
    {
        proc.apply(key, value);
    }

    @Override
    <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        proc.apply(key, value);
    }

    @Override
    <R> R reduce(R sum,
                 @Nonnull Sum2<K, V, R> proc)
    {
        return proc.apply(sum, key, value);
    }

    @Override
    <R, E extends Exception> R reduceThrows(R sum,
                                            @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return proc.apply(sum, key, value);
    }

    @Nullable
    @Override
    public GenericIterator.State<IMapEntry<K, V>> iterateOverRange(@Nullable GenericIterator.State<IMapEntry<K, V>> parent,
                                                                   int offset,
                                                                   int limit)
    {
        return GenericIterator.singleValueState(parent, asEntry());
    }

    @Override
    public int iterableSize()
    {
        return 1;
    }

    private boolean isMatch(@Nonnull Comparator<K> comp,
                            @Nonnull K key)
    {
        return comp.compare(key, this.key) == 0;
    }

    private IMapEntry<K, V> asEntry()
    {
        return entry(key, value);
    }
}
