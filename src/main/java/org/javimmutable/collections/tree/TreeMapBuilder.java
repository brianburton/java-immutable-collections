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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@ThreadSafe
class TreeMapBuilder<K, V>
    implements IMapBuilder<K, V>
{
    private final Comparator<K> comparator;
    private final Map<K, V> values;

    TreeMapBuilder(@Nonnull Comparator<K> comparator)
    {
        this.comparator = comparator;
        values = new java.util.TreeMap<>(comparator);
    }

    @Nonnull
    @Override
    public synchronized IMap<K, V> build()
    {
        if (values.isEmpty()) {
            return TreeMap.of(comparator);
        } else {
            final List<Entry<K, V>> sorted = new ArrayList<>(values.entrySet());
            final AbstractNode<K, V> root = buildTree(sorted, 0, sorted.size());
            return new TreeMap<>(comparator, root);
        }
    }

    @Nonnull
    @Override
    public synchronized IMapBuilder<K, V> clear()
    {
        values.clear();
        return this;
    }

    @Nonnull
    @Override
    public synchronized IMapBuilder<K, V> add(@Nonnull K key,
                                              V value)
    {
        values.put(key, value);
        return this;
    }

    public synchronized int size()
    {
        return values.size();
    }

    private AbstractNode<K, V> buildTree(@Nonnull List<Entry<K, V>> values,
                                         int offset,
                                         int limit)
    {
        assert limit > offset;
        int count = limit - offset;
        if (count == 1) {
            final Entry<K, V> e = values.get(offset);
            return ValueNode.instance(e.getKey(), e.getValue());
        } else if (count == 2) {
            final Entry<K, V> a = values.get(offset);
            final Entry<K, V> b = values.get(offset + 1);
            final AbstractNode<K, V> right = ValueNode.instance(b.getKey(), b.getValue());
            return new ValueNode<>(a.getKey(), a.getValue(), FringeNode.instance(), right);
        } else {
            final int middle = offset + count / 2;
            final Entry<K, V> e = values.get(middle);
            final AbstractNode<K, V> left = buildTree(values, offset, middle);
            final AbstractNode<K, V> right = buildTree(values, middle + 1, limit);
            return new ValueNode<>(e.getKey(), e.getValue(), left, right);
        }
    }
}
