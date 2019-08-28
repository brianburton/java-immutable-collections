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
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.list.ListCollisionMap;

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
        final ListCollisionMap<Integer, String> transforms = ListCollisionMap.empty();

        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        assertEquals(null, empty.getValueOr(1, 1, null));
        verifyContents(transforms, empty);

        MutableDelta delta = new MutableDelta();
        HamtNode<Integer, String> node = empty.assign(transforms, 1, 1, "able");
        assertEquals(1, node.size());
        assertEquals("able", node.getValueOr(1, 1, null));
        verifyContents(transforms, node, "able");

        assertSame(node, node.assign(transforms, 1, 1, "able"));

        node = node.assign(transforms, 1, 1, "baker");
        assertEquals(1, node.size());
        assertEquals("baker", node.getValueOr(1, 1, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, -1, -1, "charlie");
        assertEquals(2, node.size());
        assertEquals("charlie", node.getValueOr(-1, -1, null));
        verifyContents(transforms, node, "baker", "charlie");

        assertSame(node, node.assign(transforms, -1, -1, "charlie"));

        node = node.assign(transforms, 7, 7, "delta");
        assertEquals(3, node.size());
        assertEquals("delta", node.getValueOr(7, 7, null));
        verifyContents(transforms, node, "baker", "charlie", "delta");

        node = node.assign(transforms, 4725297, 4725297, "echo");
        assertEquals(4, node.size());
        assertEquals("echo", node.getValueOr(4725297, 4725297, null));
        verifyContents(transforms, node, "baker", "charlie", "delta", "echo");

        assertSame(node, node.delete(transforms, -2, -2));
        assertEquals(4, node.size());
        verifyContents(transforms, node, "baker", "charlie", "delta", "echo");

        node = node.assign(transforms, 33, 33, "foxtrot");
        assertEquals(5, node.size());
        assertEquals("foxtrot", node.getValueOr(33, 33, null));
        verifyContents(transforms, node, "baker", "charlie", "delta", "echo", "foxtrot");

        node = node.delete(transforms, 1, 1);
        assertEquals(4, node.size());
        assertEquals(null, node.getValueOr(1, 1, null));
        verifyContents(transforms, node, "charlie", "delta", "echo", "foxtrot");

        assertSame(node, node.delete(transforms, -2, -2));

        node = node.delete(transforms, 4725297, 4725297);
        assertEquals(3, node.size());
        assertEquals(null, node.getValueOr(4725297, 4725297, null));
        verifyContents(transforms, node, "charlie", "delta", "foxtrot");

        node = node.delete(transforms, -1, -1);
        assertEquals(2, node.size());
        assertEquals(null, node.getValueOr(-1, -1, null));
        verifyContents(transforms, node, "delta", "foxtrot");

        node = node.delete(transforms, 7, 7);
        assertEquals(1, node.size());
        assertEquals(null, node.getValueOr(7, 7, null));
        verifyContents(transforms, node, "foxtrot");

        node = node.delete(transforms, 33, 33);
        assertEquals(0, node.size());
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
        final ListCollisionMap<Checked, Integer> transforms = ListCollisionMap.empty();

        HamtNode<Checked, Integer> node = HamtEmptyNode.of();

        node = node.assign(transforms, a.hashCode, a, 100);
        assertEquals(1, node.size());
        assertSame(node, node.assign(transforms, a.hashCode, a, 100));

        node = node.assign(transforms, b.hashCode, b, 100);
        assertEquals(2, node.size());
        assertSame(node, node.assign(transforms, a.hashCode, a, 100));
        assertSame(node, node.assign(transforms, b.hashCode, b, 100));
        assertSame(node, node.delete(transforms, z.hashCode, z));
        assertEquals(null, node.find(z.hashCode, z).getValueOrNull());
        assertEquals(null, node.getValueOr(z.hashCode, z, null));

        node = node.assign(transforms, c.hashCode, c, 100);
        assertEquals(3, node.size());

        node = node.assign(transforms, x.hashCode, x, 200);
        assertEquals(4, node.size());

        node = node.assign(transforms, y.hashCode, y, 200);
        assertEquals(5, node.size());

        assertSame(node, node.delete(transforms, d.hashCode, d));
        assertSame(node, node.delete(transforms, e.hashCode, e));
        assertSame(node, node.delete(transforms, z.hashCode, z));

        node = node
            .delete(transforms, x.hashCode, x)
            .delete(transforms, a.hashCode, a)
            .delete(transforms, b.hashCode, b)
            .delete(transforms, c.hashCode, c)
            .delete(transforms, y.hashCode, y);
        assertEquals(0, node.size());
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testRollupOnDelete()
    {
        final CollisionMap<Integer, String> transforms = ListCollisionMap.empty();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(transforms, 0x1fffff, 0x1fffff, "able");
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        assertEquals("able", node.getValueOr(0x1fffff, 0x1fffff, null));
        verifyContents(transforms, node, "able");

        node = node.assign(transforms, 0x2fffff, 0x2fffff, "baker");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x1fffff, 0x1fffff);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        verifyContents(transforms, node, "baker");

        node = node.delete(transforms, 0x2fffff, 0x2fffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size());
        verifyContents(transforms, node);
    }

    public void testRollupOnDelete2()
    {
        final CollisionMap<Integer, String> transforms = ListCollisionMap.empty();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(transforms, 0x2fffff, 0x2fffff, "baker");
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        assertEquals("baker", node.getValueOr(0x2fffff, 0x2fffff, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, 0x1fffff, 0x1fffff, "able");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x2fffff, 0x2fffff);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        verifyContents(transforms, node, "able");

        node = node.delete(transforms, 0x1fffff, 0x1fffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size());
        verifyContents(transforms, node);
    }

    public void testRollupOnDelete3()
    {
        final CollisionMap<Integer, String> transforms = ListCollisionMap.empty();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(transforms, 0x2fffff, 0x2fffff, "baker");
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        assertEquals("baker", node.getValueOr(0x2fffff, 0x2fffff, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, 0x4fffffff, 0x4fffffff, "able");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x2fffff, 0x2fffff);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        verifyContents(transforms, node, "able");

        node = node.delete(transforms, 0x4fffffff, 0x4fffffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size());
        verifyContents(transforms, node);
    }

    public void testRollupOnDelete4()
    {
        final CollisionMap<Integer, String> transforms = ListCollisionMap.empty();
        HamtNode<Integer, String> empty = HamtEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        HamtNode<Integer, String> node = empty.assign(transforms, 0x2fffffff, 0x2fffffff, "baker");
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        assertEquals("baker", node.getValueOr(0x2fffffff, 0x2fffffff, null));
        verifyContents(transforms, node, "baker");

        node = node.assign(transforms, 0x4fffff, 0x4fffff, "able");
        assertEquals(true, node instanceof HamtBranchNode);
        assertEquals(2, node.size());
        verifyContents(transforms, node, "able", "baker");

        node = node.delete(transforms, 0x2fffffff, 0x2fffffff);
        assertEquals(true, node instanceof HamtLeafNode);
        assertEquals(1, node.size());
        verifyContents(transforms, node, "able");

        node = node.delete(transforms, 0x4fffff, 0x4fffff);
        assertEquals(true, node instanceof HamtEmptyNode);
        assertEquals(0, node.size());
        verifyContents(transforms, node);
    }

    public void testDeleteCollapseBranchIntoLeaf()
    {
        final CollisionMap<Integer, Integer> transforms = ListCollisionMap.empty();
        final MutableDelta size = new MutableDelta();
        HamtNode<Integer, Integer> node = HamtEmptyNode.of();
        node = node.assign(transforms, 7129, 7129, 1);
        node = node.assign(transforms, 985, 985, 2);
        node = node.delete(transforms, 7129, 7129);
        assertEquals(Integer.valueOf(2), node.getValueOr(985, 985, -1));
        node = node.delete(transforms, 985, 985);
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testRandom()
    {
        final CollisionMap<Integer, Integer> transforms = ListCollisionMap.empty();
        final Random r = new Random();

        for (int loop = 1; loop <= 50; ++loop) {
            final List<Integer> domain = IntStream.range(1, 1200)
                .boxed()
                .map(i -> r.nextInt())
                .collect(Collectors.toList());

            final MutableDelta size = new MutableDelta();
            HamtNode<Integer, Integer> node = HamtEmptyNode.of();
            for (Integer key : domain) {
                node = node.assign(transforms, key, key, key);
                assertEquals(node.getValueOr(key, key, -1), node.find(key, key).getValueOr(-1));
            }
            node.checkInvariants();
            verifyIntContents(transforms, node, domain);

            Collections.shuffle(domain);
            for (Integer key : domain) {
                node = node.delete(transforms, key, key);
                assertSame(node, node.delete(transforms, key, key));
                assertEquals(null, node.getValueOr(key, key, null));
                assertEquals(null, node.find(key, key).getValueOr(null));
            }
            node.checkInvariants();
            assertSame(HamtEmptyNode.of(), node);
            assertEquals(0, node.size());
        }
    }

    private void verifyContents(CollisionMap<Integer, String> transforms,
                                HamtNode<Integer, String> node,
                                String... values)
    {
        Set<String> expected = new HashSet<>(asList(values));
        Set<String> actual = collectValues(node);
        assertEquals(expected, actual);
        verifyConnectivity(transforms, node);
    }

    private void verifyIntContents(CollisionMap<Integer, Integer> transforms,
                                   HamtNode<Integer, Integer> node,
                                   List<Integer> values)
    {
        Set<Integer> expected = new HashSet<>(values);
        Set<Integer> actual = collectValues(node);
        assertEquals(expected, actual);
        verifyConnectivity(transforms, node);
    }

    private <T, K, V> void verifyConnectivity(CollisionMap<K, V> transforms,
                                              HamtNode<K, V> node)
    {
        for (JImmutableMap.Entry<K, V> entry : node) {
            final V usingGet = node.getValueOr(entry.getKey().hashCode(), entry.getKey(), null);
            final V usingFind = node.find(entry.getKey().hashCode(), entry.getKey()).getValueOrNull();
            assertEquals(entry.getValue(), usingGet);
        }
    }

    @Nonnull
    private <T, K, V> Set<V> collectValues(HamtNode<K, V> node)
    {
        Iterator<JImmutableMap.Entry<K, V>> iterator = node.iterator();
        Set<V> actual = new HashSet<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next().getValue());
        }
        return actual;
    }

}
