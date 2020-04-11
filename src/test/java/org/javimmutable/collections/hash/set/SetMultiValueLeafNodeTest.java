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

package org.javimmutable.collections.hash.set;

import junit.framework.TestCase;
import org.javimmutable.collections.hash.map.Checked;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.list.ListCollisionSet;

import java.util.ArrayList;
import java.util.List;

public class SetMultiValueLeafNodeTest
    extends TestCase
{
    public void testDelete()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final ListCollisionSet<Checked> collisionSet = ListCollisionSet.instance();

        ListCollisionSet.Node value = collisionSet.empty();
        value = collisionSet.insert(value, a);
        value = collisionSet.insert(value, b);
        value = collisionSet.insert(value, c);
        assertEquals(3, collisionSet.size(value));

        SetNode<Checked> node = new SetMultiValueLeafNode<>(1, value);
        assertEquals(3, node.size(collisionSet));

        assertSame(node, node.delete(collisionSet, 1, d));
        assertSame(node, node.delete(collisionSet, 99, a));
        assertEquals(3, node.size(collisionSet));

        node = node.delete(collisionSet, 1, b);
        assertEquals(2, node.size(collisionSet));
        assertTrue(node instanceof SetMultiValueLeafNode);

        node = node.delete(collisionSet, 1, c);
        assertEquals(1, node.size(collisionSet));
        assertTrue(node instanceof SetSingleValueLeafNode);

        node = node.delete(collisionSet, 1, a);
        assertSame(SetEmptyNode.of(), node);

        value = collisionSet.insert(collisionSet.empty(), a);
        node = new SetMultiValueLeafNode<>(1, value);
        assertSame(SetEmptyNode.of(), node.delete(collisionSet, 1, a));
    }

    public void testIterator()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final ListCollisionSet<Checked> collisionSet = ListCollisionSet.instance();

        ListCollisionSet.Node node = collisionSet.empty();
        node = collisionSet.insert(node, a);
        node = collisionSet.insert(node, b);
        node = collisionSet.insert(node, c);
        node = collisionSet.insert(node, d);

        SetNode<Checked> leaf = new SetMultiValueLeafNode<>(1, node);
        assertEquals(4, leaf.size(collisionSet));

        List<Checked> expected = new ArrayList<>();
        expected.add(a);
        expected.add(b);
        expected.add(c);
        expected.add(d);
        StandardIteratorTests.verifyOrderedIterable(expected, leaf.iterable(collisionSet));
    }
}
