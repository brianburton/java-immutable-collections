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

package org.javimmutable.collections.array.trie32;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        StandardCursorTest.listCursorTest(Arrays.asList("value"), node.anyOrderValueCursor());
        StandardCursorTest.listCursorTest(Arrays.asList("value"), node.signedOrderValueCursor());
        StandardCursorTest.listCursorTest(Collections.singletonList(MapEntry.of(30 << 20, "value")), node.anyOrderEntryCursor());
        StandardCursorTest.listCursorTest(Collections.singletonList(MapEntry.of(30 << 20, "value")), node.signedOrderEntryCursor());
        StandardCursorTest.listIteratorTest(Arrays.asList("value"), node.anyOrderValueIterator());
        StandardCursorTest.listIteratorTest(Arrays.asList("value"), node.signedOrderValueIterator());
        StandardCursorTest.listIteratorTest(Collections.singletonList(MapEntry.of(30 << 20, "value")), node.anyOrderEntryIterator());
        StandardCursorTest.listIteratorTest(Collections.singletonList(MapEntry.of(30 << 20, "value")), node.signedOrderEntryIterator());

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

    public void testTransforms()
    {
        Transforms<Map<String, String>, String, String> tx = new TestOnlyTransforms<>();
        Map<String, String> map = new TreeMap<>();
        map.put("a", "A");
        map.put("b", "B");
        LeafTrieNode<Map<String, String>> child = LeafTrieNode.of(30 << 20, map);
        SingleBranchTrieNode<Map<String, String>> node = SingleBranchTrieNode.forBranchIndex(20, 30, child);
        assertEquals(null, node.getValueOr(20, 31 << 20, null));
        assertEquals("A", node.getValueOr(20, 30 << 20, "a", tx, null));
        assertEquals("B", node.getValueOr(20, 30 << 20, "b", tx, null));
        assertEquals(null, node.getValueOr(20, 30 << 20, "c", tx, null));
        assertEquals(Holders.<String>of(), node.find(20, 31 << 20, "a", tx));
        assertEquals(Holders.of("A"), node.find(20, 30 << 20, "a", tx));
        assertEquals(Holders.of("B"), node.find(20, 30 << 20, "b", tx));
        assertEquals(Holders.<String>of(), node.find(20, 30 << 20, "c", tx));
        List<JImmutableMap.Entry<String, String>> expectedEntries = new ArrayList<>();
        expectedEntries.add(MapEntry.of("a", "A"));
        expectedEntries.add(MapEntry.of("b", "B"));
        StandardCursorTest.listCursorTest(expectedEntries, node.anyOrderEntryCursor(tx));
        StandardCursorTest.listCursorTest(expectedEntries, node.signedOrderEntryCursor(tx));
        StandardCursorTest.listIteratorTest(expectedEntries, node.anyOrderEntryIterator(tx));
        StandardCursorTest.listIteratorTest(expectedEntries, node.signedOrderEntryIterator(tx));

        MutableDelta delta = new MutableDelta();
        assertSame(node, node.assign(20, 30 << 20, "a", "A", tx, delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        assertSame(node, node.delete(20, 18 << 20, "a", tx, delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        TrieNode<Map<String, String>> newNode = node.delete(20, 30 << 20, "a", tx, delta);
        assertTrue(newNode instanceof LeafTrieNode);
        assertEquals(-1, delta.getValue());
        assertEquals(null, newNode.getValueOr(20, 30 << 20, "a", tx, null));
        assertEquals("B", newNode.getValueOr(20, 30 << 20, "b", tx, null));

        delta = new MutableDelta();
        assertSame(EmptyTrieNode.instance(), newNode.delete(20, 30 << 20, "b", tx, delta));
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        newNode = node.assign(20, 30 << 20, "a", "AA", tx, delta);
        assertEquals(0, delta.getValue());
        assertTrue(newNode instanceof SingleBranchTrieNode);
        assertEquals("AA", newNode.getValueOr(20, 30 << 20, "a", tx, null));

        delta = new MutableDelta();
        newNode = node.assign(20, 18 << 20, "x", "X", tx, delta);
        assertEquals(1, delta.getValue());
        assertTrue(newNode instanceof MultiBranchTrieNode);
        assertEquals("A", newNode.getValueOr(20, 30 << 20, "a", tx, null));
        assertEquals("X", newNode.getValueOr(20, 18 << 20, "x", tx, null));

        delta = new MutableDelta();
        newNode = newNode.delete(20, 18 << 20, "x", tx, delta);
        assertTrue(newNode instanceof LeafTrieNode);
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        assertSame(EmptyTrieNode.instance(), newNode.delete(20, 30 << 20, "a", tx, delta).delete(20, 30 << 20, "b", tx, delta));
        assertEquals(-2, delta.getValue());
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
