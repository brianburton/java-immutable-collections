///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.hash.collision_map.CollisionMap;
import org.javimmutable.collections.hash.collision_map.ListCollisionMap;
import org.javimmutable.collections.hash.collision_map.ListNode;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
        final ListCollisionMap<Integer, String> transforms = new ListCollisionMap<>();

        HamtNode<ListNode<Integer, String>, Integer, String> empty = HamtEmptyNode.of();
        assertEquals(null, empty.getValueOr(transforms, 1, 1, null));
        verifyContents(transforms, empty);

        MutableDelta delta = new MutableDelta();
        HamtNode<ListNode<Integer, String>, Integer, String> node = empty.assign(transforms, 1, 1, "able", delta);
        assertEquals(1, delta.getValue());
        assertEquals("able", node.getValueOr(transforms, 1, 1, null));
        verifyContents(transforms, node, "able");

        assertSame(node, node.assign(transforms, 1, 1, "able", delta));
        assertEquals(1, delta.getValue());

        node = node.assign(transforms, 1, 1, "baker", delta);
        assertEquals(1, delta.getValue());
        assertEquals("baker", node.getValueOr(transforms, 1, 1, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, -1, -1, "charlie", delta);
        assertEquals(2, delta.getValue());
        assertEquals("charlie", node.getValueOr(transforms, -1, -1, null));
        verifyContents(transforms, node, "baker", "charlie");

        assertSame(node, node.assign(transforms, -1, -1, "charlie", delta));
        assertEquals(2, delta.getValue());

        node = node.assign(transforms, 7, 7, "delta", delta);
        assertEquals(3, delta.getValue());
        assertEquals("delta", node.getValueOr(transforms, 7, 7, null));
        verifyContents(transforms, node, "baker", "charlie", "delta");

        node = node.assign(transforms, 4725297, 4725297, "echo", delta);
        assertEquals(4, delta.getValue());
        assertEquals("echo", node.getValueOr(transforms, 4725297, 4725297, null));
        verifyContents(transforms, node, "baker", "charlie", "delta", "echo");

        assertSame(node, node.delete(transforms, -2, -2, delta));
        assertEquals(4, delta.getValue());
        verifyContents(transforms, node, "baker", "charlie", "delta", "echo");

        node = node.assign(transforms, 33, 33, "foxtrot", delta);
        assertEquals(5, delta.getValue());
        assertEquals("foxtrot", node.getValueOr(transforms, 33, 33, null));
        verifyContents(transforms, node, "baker", "charlie", "delta", "echo", "foxtrot");

        node = node.delete(transforms, 1, 1, delta);
        assertEquals(4, delta.getValue());
        assertEquals(null, node.getValueOr(transforms, 1, 1, null));
        verifyContents(transforms, node, "charlie", "delta", "echo", "foxtrot");

        assertSame(node, node.delete(transforms, -2, -2, delta));
        assertEquals(4, delta.getValue());

        node = node.delete(transforms, 4725297, 4725297, delta);
        assertEquals(3, delta.getValue());
        assertEquals(null, node.getValueOr(transforms, 4725297, 4725297, null));
        verifyContents(transforms, node, "charlie", "delta", "foxtrot");

        node = node.delete(transforms, -1, -1, delta);
        assertEquals(2, delta.getValue());
        assertEquals(null, node.getValueOr(transforms, -1, -1, null));
        verifyContents(transforms, node, "delta", "foxtrot");

        node = node.delete(transforms, 7, 7, delta);
        assertEquals(1, delta.getValue());
        assertEquals(null, node.getValueOr(transforms, 7, 7, null));
        verifyContents(transforms, node, "foxtrot");

        node = node.delete(transforms, 33, 33, delta);
        assertEquals(0, delta.getValue());
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
        final ListCollisionMap<Checked, Integer> transforms = new ListCollisionMap<>();

        HamtNode<ListNode<Checked, Integer>, Checked, Integer> node = HamtEmptyNode.of();

        node = node.assign(transforms, a.hashCode, a, 100, size);
        assertEquals(1, size.getValue());
        assertSame(node, node.assign(transforms, a.hashCode, a, 100, size));

        node = node.assign(transforms, b.hashCode, b, 100, size);
        assertEquals(2, size.getValue());
        assertSame(node, node.assign(transforms, a.hashCode, a, 100, size));
        assertSame(node, node.assign(transforms, b.hashCode, b, 100, size));
        assertSame(node, node.delete(transforms, z.hashCode, z, size));
        assertEquals(null, node.find(transforms, z.hashCode, z).getValueOrNull());
        assertEquals(null, node.getValueOr(transforms, z.hashCode, z, null));

        node = node.assign(transforms, c.hashCode, c, 100, size);
        assertEquals(3, size.getValue());

        node = node.assign(transforms, x.hashCode, x, 200, size);
        assertEquals(4, size.getValue());

        node = node.assign(transforms, y.hashCode, y, 200, size);
        assertEquals(5, size.getValue());

        assertSame(node, node.delete(transforms, d.hashCode, d, size));
        assertSame(node, node.delete(transforms, e.hashCode, e, size));
        assertSame(node, node.delete(transforms, z.hashCode, z, size));

        node = node
            .delete(transforms, x.hashCode, x, size)
            .delete(transforms, a.hashCode, a, size)
            .delete(transforms, b.hashCode, b, size)
            .delete(transforms, c.hashCode, c, size)
            .delete(transforms, y.hashCode, y, size);
        assertEquals(0, size.getValue());
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testRollupOnDelete()
    {
        final CollisionMap<ListNode<Integer, String>, Integer, String> transforms = new ListCollisionMap<>();
        HamtNode<ListNode<Integer, String>, Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<ListNode<Integer, String>, Integer, String> node = empty.assign(transforms, 0x1fffff, 0x1fffff, "able", delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        assertEquals("able", node.getValueOr(transforms, 0x1fffff, 0x1fffff, null));
        verifyContents(transforms, node, "able");

        node = node.assign(transforms, 0x2fffff, 0x2fffff, "baker", delta);
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, delta.getValue());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x1fffff, 0x1fffff, delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        verifyContents(transforms, node, "baker");

        node = node.delete(transforms, 0x2fffff, 0x2fffff, delta);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, delta.getValue());
        verifyContents(transforms, node);
    }

    public void testRollupOnDelete2()
    {
        final CollisionMap<ListNode<Integer, String>, Integer, String> transforms = new ListCollisionMap<>();
        HamtNode<ListNode<Integer, String>, Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<ListNode<Integer, String>, Integer, String> node = empty.assign(transforms, 0x2fffff, 0x2fffff, "baker", delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        assertEquals("baker", node.getValueOr(transforms, 0x2fffff, 0x2fffff, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, 0x1fffff, 0x1fffff, "able", delta);
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, delta.getValue());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x2fffff, 0x2fffff, delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        verifyContents(transforms, node, "able");

        node = node.delete(transforms, 0x1fffff, 0x1fffff, delta);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, delta.getValue());
        verifyContents(transforms, node);
    }

    public void testRollupOnDelete3()
    {
        final CollisionMap<ListNode<Integer, String>, Integer, String> transforms = new ListCollisionMap<>();
        HamtNode<ListNode<Integer, String>, Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<ListNode<Integer, String>, Integer, String> node = empty.assign(transforms, 0x2fffff, 0x2fffff, "baker", delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        assertEquals("baker", node.getValueOr(transforms, 0x2fffff, 0x2fffff, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, 0x4fffffff, 0x4fffffff, "able", delta);
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, delta.getValue());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x2fffff, 0x2fffff, delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        verifyContents(transforms, node, "able");

        node = node.delete(transforms, 0x4fffffff, 0x4fffffff, delta);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, delta.getValue());
        verifyContents(transforms, node);
    }

    public void testRollupOnDelete4()
    {
        final CollisionMap<ListNode<Integer, String>, Integer, String> transforms = new ListCollisionMap<>();
        HamtNode<ListNode<Integer, String>, Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<ListNode<Integer, String>, Integer, String> node = empty.assign(transforms, 0x2fffffff, 0x2fffffff, "baker", delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        assertEquals("baker", node.getValueOr(transforms, 0x2fffffff, 0x2fffffff, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, 0x4fffff, 0x4fffff, "able", delta);
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, delta.getValue());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x2fffffff, 0x2fffffff, delta);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, delta.getValue());
        verifyContents(transforms, node, "able");

        node = node.delete(transforms, 0x4fffff, 0x4fffff, delta);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, delta.getValue());
        verifyContents(transforms, node);
    }

    public void testDeleteCollapseBranchIntoLeaf()
    {
        final CollisionMap<ListNode<Integer, Integer>, Integer, Integer> transforms = new ListCollisionMap<>();
        final MutableDelta size = new MutableDelta();
        HamtNode<ListNode<Integer, Integer>, Integer, Integer> node = HamtEmptyNode.of();
        node = node.assign(transforms, 7129, 7129, 1, size);
        node = node.assign(transforms, 985, 985, 2, size);
        node = node.delete(transforms, 7129, 7129, size);
        assertEquals(Integer.valueOf(2), node.getValueOr(transforms, 985, 985, -1));
        node = node.delete(transforms, 985, 985, size);
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testRandom()
    {
        final CollisionMap<ListNode<Integer, Integer>, Integer, Integer> transforms = new ListCollisionMap<>();
        final Random r = new Random();

        for (int loop = 1; loop <= 50; ++loop) {
            final List<Integer> domain = IntStream.range(1, 1200)
                .boxed()
                .map(i -> r.nextInt())
                .collect(Collectors.toList());

            final MutableDelta size = new MutableDelta();
            HamtNode<ListNode<Integer, Integer>, Integer, Integer> node = HamtEmptyNode.of();
            for (Integer key : domain) {
                node = node.assign(transforms, key, key, key, size);
                assertEquals(node.getValueOr(transforms, key, key, -1), node.find(transforms, key, key).getValueOr(-1));
            }
            node.checkInvariants();
            verifyIntContents(transforms, node, domain);

            final MutableDelta zero = new MutableDelta();
            Collections.shuffle(domain);
            for (Integer key : domain) {
                node = node.delete(transforms, key, key, size);
                assertSame(node, node.delete(transforms, key, key, zero));
                assertEquals(null, node.getValueOr(transforms, key, key, null));
                assertEquals(null, node.find(transforms, key, key).getValueOr(null));
            }
            node.checkInvariants();
            assertSame(HamtEmptyNode.of(), node);
            assertEquals(0, size.getValue());
            assertEquals(0, zero.getValue());
        }
    }

    private void verifyContents(CollisionMap<ListNode<Integer, String>, Integer, String> transforms,
                                HamtNode<ListNode<Integer, String>, Integer, String> node,
                                String... values)
    {
        Set<String> expected = new HashSet<>(asList(values));
        Set<String> actual = collectValues(transforms, node);
        assertEquals(expected, actual);
        verifyConnectivity(transforms, node);
    }

    private void verifyIntContents(CollisionMap<ListNode<Integer, Integer>, Integer, Integer> transforms,
                                   HamtNode<ListNode<Integer, Integer>, Integer, Integer> node,
                                   List<Integer> values)
    {
        Set<Integer> expected = new HashSet<>(values);
        Set<Integer> actual = collectValues(transforms, node);
        assertEquals(expected, actual);
        verifyConnectivity(transforms, node);
    }

    private <T, K, V> void verifyConnectivity(CollisionMap<T, K, V> transforms,
                                              HamtNode<T, K, V> node)
    {
        for (JImmutableMap.Entry<K, V> entry : node.iterable(transforms)) {
            final V usingGet = node.getValueOr(transforms, entry.getKey().hashCode(), entry.getKey(), null);
            final V usingFind = node.find(transforms, entry.getKey().hashCode(), entry.getKey()).getValueOrNull();
            assertEquals(entry.getValue(), usingGet);
        }
    }

    @Nonnull
    private <T, K, V> Set<V> collectValues(CollisionMap<T, K, V> transforms,
                                           HamtNode<T, K, V> node)
    {
        Iterator<JImmutableMap.Entry<K, V>> iterator = node.iterator(transforms);
        Set<V> actual = new HashSet<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next().getValue());
        }
        return actual;
    }

}
