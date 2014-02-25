///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.list.JImmutableLinkedStack;

/**
 * TrieNode variation that can handle multiple key/value pairs.
 */
public class MultiValueLeafNode<K, V>
        implements LeafNode<K, V>
{
    private final JImmutableLinkedStack<SingleValueLeafNode<K, V>> nodes;

    public MultiValueLeafNode(K key,
                              V value)
    {
        nodes = JImmutableLinkedStack.of(new SingleValueLeafNode<K, V>(key, value));
    }

    public MultiValueLeafNode(JImmutableLinkedStack<SingleValueLeafNode<K, V>> nodes)
    {
        this.nodes = nodes;
    }

    @Override
    public Holder<V> getValueForKey(K key)
    {
        final JImmutableLinkedStack<SingleValueLeafNode<K, V>> nodes = this.nodes;
        for (JImmutableLinkedStack<SingleValueLeafNode<K, V>> list = nodes; !list.isEmpty(); list = list.getTail()) {
            if (list.getHead().getKey().equals(key)) {
                return list.getHead();
            }
        }
        return Holders.of();
    }

    @Override
    public JImmutableMap.Entry<K, V> getEntryForKey(K key)
    {
        final JImmutableLinkedStack<SingleValueLeafNode<K, V>> nodes = this.nodes;
        for (JImmutableLinkedStack<SingleValueLeafNode<K, V>> list = nodes; !list.isEmpty(); list = list.getTail()) {
            if (list.getHead().getKey().equals(key)) {
                return list.getHead();
            }
        }
        return null;
    }

    @Override
    public LeafNode<K, V> setValueForKey(K key,
                                         V value,
                                         MutableDelta sizeDelta)
    {
        JImmutableLinkedStack<SingleValueLeafNode<K, V>> newList = JImmutableLinkedStack.of();
        boolean found = false;
        for (JImmutableLinkedStack<SingleValueLeafNode<K, V>> list = nodes; !list.isEmpty(); list = list.getTail()) {
            final SingleValueLeafNode<K, V> head = list.getHead();
            if (head.getKey().equals(key)) {
                if (head.getValue() == value) {
                    return this;
                }
                found = true;
            } else {
                newList = newList.insert(head);
            }
        }
        newList = newList.insert(new SingleValueLeafNode<K, V>(key, value));
        if (!found) {
            sizeDelta.add(1);
        }
        return new MultiValueLeafNode<K, V>(newList);
    }

    @Override
    public LeafNode<K, V> deleteValueForKey(K key,
                                            MutableDelta sizeDelta)
    {
        boolean found = false;
        JImmutableLinkedStack<SingleValueLeafNode<K, V>> newList = JImmutableLinkedStack.of();
        for (JImmutableLinkedStack<SingleValueLeafNode<K, V>> list = nodes; !list.isEmpty(); list = list.getTail()) {
            final SingleValueLeafNode<K, V> entry = list.getHead();
            if (entry.getKey().equals(key)) {
                found = true;
            } else {
                newList = newList.insert(entry);
            }
        }
        if (found) {
            sizeDelta.subtract(1);
            if (newList.isEmpty()) {
                return null;
            } else if (newList.getTail().isEmpty()) {
                return newList.getHead();
            } else {
                return new MultiValueLeafNode<K, V>(newList);
            }
        } else {
            return this;
        }
    }

    @Override
    public int size()
    {
        int total = 0;
        JImmutableLinkedStack<SingleValueLeafNode<K, V>> nodes = this.nodes;
        while (!nodes.isEmpty()) {
            total += 1;
            nodes = nodes.getTail();
        }
        return total;
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return MultiTransformCursor.of(nodes.cursor(), new Func1<SingleValueLeafNode<K, V>, Cursor<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<K, V>> apply(SingleValueLeafNode<K, V> node)
            {
                return node.cursor();
            }
        });
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MultiValueLeafNode that = (MultiValueLeafNode)o;

        if (nodes != null ? !nodes.equals(that.nodes) : that.nodes != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return nodes != null ? nodes.hashCode() : 0;
    }
}
