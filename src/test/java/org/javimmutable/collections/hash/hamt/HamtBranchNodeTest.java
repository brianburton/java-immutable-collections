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

package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.list.ListCollisionMap;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class HamtBranchNodeTest
    extends TestCase
{
    public void testVarious()
    {
        final ListCollisionMap<Integer, String> collisionMap = ListCollisionMap.instance();

        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        assertEquals(null, empty.getValueOr(collisionMap, 1, 1, null));
        verifyContents(collisionMap, empty);

        MutableDelta delta = new MutableDelta();
        HamtNode<Integer, String> node = empty.assign(collisionMap, 1, 1, "able");
        assertEquals(1, node.size(collisionMap));
        assertEquals("able", node.getValueOr(collisionMap, 1, 1, null));
        verifyContents(collisionMap, node, "able");

        assertSame(node, node.assign(collisionMap, 1, 1, "able"));

        node = node.assign(collisionMap, 1, 1, "baker");
        assertEquals(1, node.size(collisionMap));
        assertEquals("baker", node.getValueOr(collisionMap, 1, 1, null));
        verifyContents(collisionMap, node, "baker");

        node = node.assign(collisionMap, -1, -1, "charlie");
        assertEquals(2, node.size(collisionMap));
        assertEquals("charlie", node.getValueOr(collisionMap, -1, -1, null));
        verifyContents(collisionMap, node, "baker", "charlie");

        assertSame(node, node.assign(collisionMap, -1, -1, "charlie"));

        node = node.assign(collisionMap, 7, 7, "delta");
        assertEquals(3, node.size(collisionMap));
        assertEquals("delta", node.getValueOr(collisionMap, 7, 7, null));
        verifyContents(collisionMap, node, "baker", "charlie", "delta");

        node = node.assign(collisionMap, 4725297, 4725297, "echo");
        assertEquals(4, node.size(collisionMap));
        assertEquals("echo", node.getValueOr(collisionMap, 4725297, 4725297, null));
        verifyContents(collisionMap, node, "baker", "charlie", "delta", "echo");

        node = node.assign(collisionMap, 0, -3, "zoom");
        assertEquals(5, node.size(collisionMap));
        assertEquals("zoom", node.getValueOr(collisionMap, 0, -3, null));
        assertSame(node, node.assign(collisionMap, 0, -3, "zoom"));
        node = node.delete(collisionMap, 0, -3);
        assertEquals(4, node.size(collisionMap));
        assertEquals("echo", node.getValueOr(collisionMap, 4725297, 4725297, null));
        verifyContents(collisionMap, node, "baker", "charlie", "delta", "echo");

        assertSame(node, node.delete(collisionMap, -2, -2));
        assertEquals(4, node.size(collisionMap));
        verifyContents(collisionMap, node, "baker", "charlie", "delta", "echo");

        node = node.assign(collisionMap, 33, 33, "foxtrot");
        assertEquals(5, node.size(collisionMap));
        assertEquals("foxtrot", node.getValueOr(collisionMap, 33, 33, null));
        verifyContents(collisionMap, node, "baker", "charlie", "delta", "echo", "foxtrot");

        node = node.delete(collisionMap, 1, 1);
        assertEquals(4, node.size(collisionMap));
        assertEquals(null, node.getValueOr(collisionMap, 1, 1, null));
        verifyContents(collisionMap, node, "charlie", "delta", "echo", "foxtrot");

        assertSame(node, node.delete(collisionMap, -2, -2));

        node = node.delete(collisionMap, 4725297, 4725297);
        assertEquals(3, node.size(collisionMap));
        assertEquals(null, node.getValueOr(collisionMap, 4725297, 4725297, null));
        verifyContents(collisionMap, node, "charlie", "delta", "foxtrot");

        node = node.delete(collisionMap, -1, -1);
        assertEquals(2, node.size(collisionMap));
        assertEquals(null, node.getValueOr(collisionMap, -1, -1, null));
        verifyContents(collisionMap, node, "delta", "foxtrot");

        node = node.delete(collisionMap, 7, 7);
        assertEquals(1, node.size(collisionMap));
        assertEquals(null, node.getValueOr(collisionMap, 7, 7, null));
        verifyContents(collisionMap, node, "foxtrot");

        node = node.delete(collisionMap, 33, 33);
        assertEquals(0, node.size(collisionMap));
        assertSame(empty, node);
    }

    public void testAssignDelete()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(2, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(2, 14);
        final Checked e = new Checked(2, 15);
        final Checked x = new Checked(0, 20);
        final Checked y = new Checked(0, 25);
        final Checked z = new Checked(0, 29);
        final MutableDelta size = new MutableDelta();
        final ListCollisionMap<Checked, Integer> collisionMap = ListCollisionMap.instance();

        HamtNode<Checked, Integer> node = HamtEmptyNode.of();

        node = node.assign(collisionMap, a.hashCode, a, 100);
        assertEquals(1, node.size(collisionMap));
        assertSame(node, node.assign(collisionMap, a.hashCode, a, 100));

        node = node.assign(collisionMap, b.hashCode, b, 100);
        assertEquals(2, node.size(collisionMap));
        assertSame(node, node.assign(collisionMap, a.hashCode, a, 100));
        assertSame(node, node.assign(collisionMap, b.hashCode, b, 100));
        assertSame(node, node.delete(collisionMap, z.hashCode, z));
        assertEquals(null, node.find(collisionMap, z.hashCode, z).getValueOrNull());
        assertEquals(null, node.getValueOr(collisionMap, z.hashCode, z, null));

        node = node.assign(collisionMap, c.hashCode, c, 100);
        assertEquals(3, node.size(collisionMap));

        node = node.assign(collisionMap, x.hashCode, x, 200);
        assertEquals(4, node.size(collisionMap));

        node = node.assign(collisionMap, y.hashCode, y, 200);
        assertEquals(5, node.size(collisionMap));

        assertSame(node, node.delete(collisionMap, d.hashCode, d));
        assertSame(node, node.delete(collisionMap, e.hashCode, e));
        assertSame(node, node.delete(collisionMap, z.hashCode, z));

        node = node
            .delete(collisionMap, x.hashCode, x)
            .delete(collisionMap, a.hashCode, a)
            .delete(collisionMap, b.hashCode, b)
            .delete(collisionMap, c.hashCode, c)
            .delete(collisionMap, y.hashCode, y);
        assertEquals(0, node.size(collisionMap));
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testRollupOnDelete()
    {
        final CollisionMap<Integer, String> collisionMap = ListCollisionMap.instance();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(collisionMap, 0x1fffff, 0x1fffff, "able");
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionMap));
        assertEquals("able", node.getValueOr(collisionMap, 0x1fffff, 0x1fffff, null));
        verifyContents(collisionMap, node, "able");

        node = node.assign(collisionMap, 0x2fffff, 0x2fffff, "baker");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size(collisionMap));
        verifyContents(collisionMap, node, "able", "baker");

        node = node.delete(collisionMap, 0x1fffff, 0x1fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionMap));
        verifyContents(collisionMap, node, "baker");

        node = node.delete(collisionMap, 0x2fffff, 0x2fffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size(collisionMap));
        verifyContents(collisionMap, node);
    }

    public void testRollupOnDelete2()
    {
        final CollisionMap<Integer, String> collisionMap = ListCollisionMap.instance();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(collisionMap, 0x2fffff, 0x2fffff, "baker");
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionMap));
        assertEquals("baker", node.getValueOr(collisionMap, 0x2fffff, 0x2fffff, null));
        verifyContents(collisionMap, node, "baker");

        node = node.assign(collisionMap, 0x1fffff, 0x1fffff, "able");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size(collisionMap));
        verifyContents(collisionMap, node, "able", "baker");

        node = node.delete(collisionMap, 0x2fffff, 0x2fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionMap));
        verifyContents(collisionMap, node, "able");

        node = node.delete(collisionMap, 0x1fffff, 0x1fffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size(collisionMap));
        verifyContents(collisionMap, node);
    }

    public void testRollupOnDelete3()
    {
        final CollisionMap<Integer, String> collisionMap = ListCollisionMap.instance();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(collisionMap, 0x2fffff, 0x2fffff, "baker");
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionMap));
        assertEquals("baker", node.getValueOr(collisionMap, 0x2fffff, 0x2fffff, null));
        verifyContents(collisionMap, node, "baker");

        node = node.assign(collisionMap, 0x4fffffff, 0x4fffffff, "able");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size(collisionMap));
        verifyContents(collisionMap, node, "able", "baker");

        node = node.delete(collisionMap, 0x2fffff, 0x2fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionMap));
        verifyContents(collisionMap, node, "able");

        node = node.delete(collisionMap, 0x4fffffff, 0x4fffffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size(collisionMap));
        verifyContents(collisionMap, node);
    }

    public void testRollupOnDelete4()
    {
        final CollisionMap<Integer, String> collisionMap = ListCollisionMap.instance();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(collisionMap, 0x2fffffff, 0x2fffffff, "baker");
        assertEquals(true, node instanceof HamtSingleKeyLeafNode);
        assertEquals(1, node.size(collisionMap));
        assertEquals("baker", node.getValueOr(collisionMap, 0x2fffffff, 0x2fffffff, null));
        verifyContents(collisionMap, node, "baker");

        node = node.assign(collisionMap, 0x4fffff, 0x4fffff, "able");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size(collisionMap));
        verifyContents(collisionMap, node, "able", "baker");

        node = node.delete(collisionMap, 0x2fffffff, 0x2fffffff);
        assertEquals(true, node instanceof HamtSingleKeyLeafNode);
        assertEquals(1, node.size(collisionMap));
        verifyContents(collisionMap, node, "able");

        node = node.delete(collisionMap, 0x4fffff, 0x4fffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size(collisionMap));
        verifyContents(collisionMap, node);
    }

    public void testDeleteCollapseBranchIntoLeaf()
    {
        final CollisionMap<Integer, Integer> collisionMap = ListCollisionMap.instance();
        final MutableDelta size = new MutableDelta();
        HamtNode<Integer, Integer> node = HamtEmptyNode.of();
        node = node.assign(collisionMap, 7129, 7129, 1);
        node = node.assign(collisionMap, 985, 985, 2);
        node = node.delete(collisionMap, 7129, 7129);
        assertEquals(Integer.valueOf(2), node.getValueOr(collisionMap, 985, 985, -1));
        node = node.delete(collisionMap, 985, 985);
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testRandom()
    {
        final CollisionMap<Integer, Integer> collisionMap = ListCollisionMap.instance();
        final Random r = new Random();

        for (int loop = 1; loop <= 50; ++loop) {
            final List<Integer> domain = IntStream.range(1, 1200)
                .boxed()
                .map(i -> r.nextInt())
                .collect(Collectors.toList());

            final MutableDelta size = new MutableDelta();
            HamtNode<Integer, Integer> node = HamtEmptyNode.of();
            for (Integer key : domain) {
                node = node.assign(collisionMap, key, key, key);
                assertEquals(node.getValueOr(collisionMap, key, key, -1), node.find(collisionMap, key, key).getValueOr(-1));
            }
            node.checkInvariants(collisionMap);
            verifyIntContents(collisionMap, node, domain);

            Collections.shuffle(domain);
            for (Integer key : domain) {
                node = node.delete(collisionMap, key, key);
                assertSame(node, node.delete(collisionMap, key, key));
                assertEquals(null, node.getValueOr(collisionMap, key, key, null));
                assertEquals(null, node.find(collisionMap, key, key).getValueOr(null));
            }
            node.checkInvariants(collisionMap);
            assertSame(HamtEmptyNode.of(), node);
            assertEquals(0, node.size(collisionMap));
        }
    }

    private void verifyContents(CollisionMap<Integer, String> collisionMap,
                                HamtNode<Integer, String> node,
                                String... values)
    {
        Set<String> expected = new HashSet<>(asList(values));
        Set<String> actual = collectValues(collisionMap, node);
        assertEquals(expected, actual);
        verifyConnectivity(collisionMap, node);
    }

    private void verifyIntContents(CollisionMap<Integer, Integer> collisionMap,
                                   HamtNode<Integer, Integer> node,
                                   List<Integer> values)
    {
        Set<Integer> expected = new HashSet<>(values);
        Set<Integer> actual = collectValues(collisionMap, node);
        assertEquals(expected, actual);
        verifyConnectivity(collisionMap, node);
    }

    private <T, K, V> void verifyConnectivity(CollisionMap<K, V> collisionMap,
                                              HamtNode<K, V> node)
    {
        for (JImmutableMap.Entry<K, V> entry : node.iterable(collisionMap)) {
            final V usingGet = node.getValueOr(collisionMap, entry.getKey().hashCode(), entry.getKey(), null);
            final V usingFind = node.find(collisionMap, entry.getKey().hashCode(), entry.getKey()).getValueOrNull();
            assertEquals(entry.getValue(), usingGet);
        }
    }

    @Nonnull
    private <T, K, V> Set<V> collectValues(CollisionMap<K, V> collisionMap,
                                           HamtNode<K, V> node)
    {
        Set<V> actual = new HashSet<>();
        for (JImmutableMap.Entry<K, V> entry : node.iterable(collisionMap)) {
            actual.add(entry.getValue());
        }
        return actual;
    }
}
