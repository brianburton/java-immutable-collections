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

package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Collections;

public class SingleBranchTrieNodeTest
    extends TestCase
{
    public void testConstructors()
    {
        LeafTrieNode<String> child = LeafTrieNode.of(30 << 20, "value");
        SingleBranchTrieNode<String> node = SingleBranchTrieNode.forBranchIndex(20, 30, child);
        assertEquals(20, node.getShift());
        assertEquals(30, node.getBranchIndex());
        assertSame(child, node.getChild());

        node = SingleBranchTrieNode.forIndex(20, 18 << 20, child);
        assertEquals(20, node.getShift());
        assertEquals(18, node.getBranchIndex());
        assertSame(child, node.getChild());
    }

    public void testNormal()
    {
        LeafTrieNode<String> child = LeafTrieNode.of(30 << 20, "value");
        SingleBranchTrieNode<String> node = SingleBranchTrieNode.forBranchIndex(20, 30, child);
        assertEquals(null, node.getValueOr(20, 31 << 20, null));
        assertEquals("value", node.getValueOr(20, 30 << 20, null));
        assertEquals(Holders.<String>of(), node.find(20, 31 << 20));
        assertEquals(Holders.of("value"), node.find(20, 30 << 20));
        StandardCursorTest.listCursorTest(Collections.singletonList(MapEntry.of(30 << 20, "value")), node.cursor());
        StandardCursorTest.listIteratorTest(Collections.singletonList(MapEntry.of(30 << 20, "value")), node.iterator());

        MutableDelta delta = new MutableDelta();
        assertSame(node, node.assign(20, 30 << 20, "value", delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        assertSame(node, node.delete(20, 18 << 20, delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        assertSame(EmptyTrieNode.instance(), node.delete(20, 30 << 20, delta));
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        TrieNode<String> newNode = node.assign(20, 30 << 20, "30", delta);
        assertEquals(0, delta.getValue());
        assertTrue(newNode instanceof SingleBranchTrieNode);
        assertEquals("30", newNode.getValueOr(20, 30 << 20, null));

        delta = new MutableDelta();
        newNode = node.assign(20, 18 << 20, "18", delta);
        assertEquals(1, delta.getValue());
        assertTrue(newNode instanceof MultiBranchTrieNode);
        assertEquals("value", newNode.getValueOr(20, 30 << 20, null));
        assertEquals("18", newNode.getValueOr(20, 18 << 20, null));

        delta = new MutableDelta();
        newNode = newNode.delete(20, 18 << 20, delta);
        assertTrue(newNode instanceof LeafTrieNode);
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        assertSame(EmptyTrieNode.instance(), newNode.delete(20, 30 << 20, delta));
        assertEquals(-1, delta.getValue());
    }

    @SuppressWarnings({"unchecked", "PointlessBitwiseExpression"})
    public void testDeleteTurnsChildIntoLeaf()
    {
        LeafTrieNode<String> leaf1 = LeafTrieNode.of(0 << 20, "value1");
        LeafTrieNode<String> leaf2 = LeafTrieNode.of(1 << 20, "value2");
        MultiBranchTrieNode<String> child = MultiBranchTrieNode.<String>forEntries(20, new TrieNode[]{leaf1, leaf2});
        assertEquals("value1", child.getValueOr(20, 0 << 20, null));
        assertEquals("value2", child.getValueOr(20, 1 << 20, null));

        SingleBranchTrieNode<String> node = SingleBranchTrieNode.forBranchIndex(25, 0, child);
        assertEquals("value1", node.getValueOr(25, 0 << 20, null));
        assertEquals("value2", node.getValueOr(25, 1 << 20, null));

        MutableDelta delta = new MutableDelta();
        assertSame(leaf2, node.delete(25, 0 << 20, delta));
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        assertSame(leaf1, node.delete(25, 1 << 20, delta));
        assertEquals(-1, delta.getValue());
    }

    public void testPadding()
    {
        LeafTrieNode<String> child = LeafTrieNode.of(30 << 20, "value");
        SingleBranchTrieNode<String> node = SingleBranchTrieNode.forBranchIndex(20, 30, child);

        TrieNode<String> newNode = node.paddedToMinimumDepthForShift(20);
        assertSame(node, newNode);

        newNode = node.paddedToMinimumDepthForShift(25);
        assertTrue(newNode instanceof SingleBranchTrieNode);
        assertEquals(25, newNode.getShift());

        newNode = node.paddedToMinimumDepthForShift(30);
        assertTrue(newNode instanceof SingleBranchTrieNode);
        assertEquals(30, newNode.getShift());
        assertEquals(0, ((SingleBranchTrieNode)newNode).getBranchIndex());

        TrieNode<String> newChild = ((SingleBranchTrieNode<String>)newNode).getChild();
        assertTrue(newChild instanceof SingleBranchTrieNode);
        assertEquals(25, newChild.getShift());
        assertEquals(0, ((SingleBranchTrieNode)newChild).getBranchIndex());

        newChild = ((SingleBranchTrieNode<String>)newChild).getChild();
        assertSame(node, newChild);

        assertSame(node, node.trimmedToMinimumDepth());
        assertSame(node, newNode.trimmedToMinimumDepth());
    }
}
