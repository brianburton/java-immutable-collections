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

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Maybe;

import java.util.Comparator;

public class LeafNodeTest
    extends TestCase
{
    private final Comparator<Integer> comparator = Comparator.naturalOrder();
    private final AbstractNode<Integer, Integer> empty = FringeNode.instance();

    public void testGetters()
    {
        final AbstractNode<Integer, Integer> node = new LeafNode<>(1, 5);
        assertEquals(true, node.containsKey(comparator, 1));
        assertEquals(false, node.containsKey(comparator, 3));
        assertEquals((Integer)1, node.key());
        assertEquals((Integer)5, node.value());
        assertEquals((Integer)5, node.get(comparator, 1, 20));
        assertEquals((Integer)20, node.get(comparator, 3, 20));
        assertEquals(Maybe.of(5), node.find(comparator, 1));
        assertEquals(Maybe.empty(), node.find(comparator, 3));
        assertEquals(Maybe.of(MapEntry.entry(1, 5)), node.findEntry(comparator, 1));
        assertEquals(Maybe.empty(), node.findEntry(comparator, 3));
    }

    public void testDelete()
    {
        final AbstractNode<Integer, Integer> node = new LeafNode<>(1, -1);
        assertSame(empty, node.delete(comparator, 1));
        assertSame(node, node.delete(comparator, 2));
    }

    public void testAssign()
    {
        AbstractNode<Integer, Integer> node = empty.assign(comparator, 1, 10);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,10)]", node.toString());

        assertSame(node, node.assign(comparator, 1, 10));

        node = node.assign(comparator, 1, 20);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,20)]", node.toString());

        AbstractNode<Integer, Integer> valueNode = node.assign(comparator, 0, 5);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(0,5),(1,20)]", valueNode.toString());

        valueNode = node.assign(comparator, 3, 5);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(1,20),(3,5)]", valueNode.toString());
    }

    public void testUpdate()
    {
        final Func1<Maybe<Integer>, Integer> generator = h -> h.isFull() ? h.unsafeGet() + 1 : 1;

        AbstractNode<Integer, Integer> node = empty.update(comparator, 1, generator);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,1)]", node.toString());

        assertSame(node, node.update(comparator, 1, h -> h.get(-1)));

        node = node.update(comparator, 1, generator);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,2)]", node.toString());

        AbstractNode<Integer, Integer> valueNode = node.update(comparator, 0, generator);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(0,1),(1,2)]", valueNode.toString());

        valueNode = node.update(comparator, 3, generator);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(1,2),(3,1)]", valueNode.toString());
    }

}
