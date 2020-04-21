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

package org.javimmutable.collections.hash.map;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.list.ListCollisionMap;

import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.MapEntry.entry;

public class MapMultiKeyLeafNodeTest
    extends TestCase
{
    public void testDelete()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final ListCollisionMap<Checked, Integer> collisionMap = ListCollisionMap.instance();

        ListCollisionMap.Node value = collisionMap.empty();
        value = collisionMap.update(value, a, 100);
        value = collisionMap.update(value, b, 200);
        value = collisionMap.update(value, c, 300);
        assertEquals(3, collisionMap.size(value));

        MapNode<Checked, Integer> node = new MapMultiKeyLeafNode<>(1, value);
        assertEquals(3, node.size(collisionMap));

        assertSame(node, node.delete(collisionMap, 1, d));
        assertSame(node, node.delete(collisionMap, 99, a));
        assertEquals(3, node.size(collisionMap));

        node = node.delete(collisionMap, 1, b);
        assertEquals(2, node.size(collisionMap));
        assertTrue(node instanceof MapMultiKeyLeafNode);

        node = node.delete(collisionMap, 1, c);
        assertEquals(1, node.size(collisionMap));
        assertTrue(node instanceof MapSingleKeyLeafNode);

        node = node.delete(collisionMap, 1, a);
        assertSame(MapEmptyNode.of(), node);

        value = collisionMap.update(collisionMap.empty(), a, 100);
        node = new MapMultiKeyLeafNode<>(1, value);
        assertSame(MapEmptyNode.of(), node.delete(collisionMap, 1, a));
    }

    public void testIterator()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final ListCollisionMap<Checked, Integer> collisionMap = ListCollisionMap.instance();

        ListCollisionMap.Node node = collisionMap.empty();
        node = collisionMap.update(node, a, 100);
        node = collisionMap.update(node, b, 200);
        node = collisionMap.update(node, c, 300);
        node = collisionMap.update(node, d, 400);

        MapNode<Checked, Integer> leaf = new MapMultiKeyLeafNode<>(1, node);
        assertEquals(4, leaf.size(collisionMap));

        List<JImmutableMap.Entry<Checked, Integer>> expected = new ArrayList<>();
        expected.add(entry(a, 100));
        expected.add(entry(b, 200));
        expected.add(entry(c, 300));
        expected.add(entry(d, 400));
        StandardIteratorTests.verifyOrderedIterable(expected, leaf.iterable(collisionMap));
    }
}
