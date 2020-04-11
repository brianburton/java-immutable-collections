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
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.hash.map.Checked;
import org.javimmutable.collections.list.ListCollisionSet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class SetBranchNodeTest
    extends TestCase
{
    public void testVarious()
    {
        final ListCollisionSet<Integer> collisionSet = ListCollisionSet.instance();

        SetNode<Integer> empty = SetEmptyNode.of();
        assertEquals(false, empty.contains(collisionSet, 1, 1));
        verifyContents(collisionSet, empty);

        MutableDelta delta = new MutableDelta();
        SetNode<Integer> node = empty.insert(collisionSet, 1, 1);
        assertEquals(1, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 1, 1));
        verifyContents(collisionSet, node, 1);

        assertSame(node, node.insert(collisionSet, 1, 1));

        node = node.insert(collisionSet, 1, 1);
        assertEquals(1, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 1, 1));
        verifyContents(collisionSet, node, 1);

        node = node.insert(collisionSet, -1, -1);
        assertEquals(2, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, -1, -1));
        verifyContents(collisionSet, node, 1, -1);

        assertSame(node, node.insert(collisionSet, -1, -1));

        node = node.insert(collisionSet, 7, 7);
        assertEquals(3, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 7, 7));
        verifyContents(collisionSet, node, 1, 7, -1);

        node = node.insert(collisionSet, 4725297, 4725297);
        assertEquals(4, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 4725297, 4725297));
        verifyContents(collisionSet, node, 1, 7, -1, 4725297);

        node = node.insert(collisionSet, 0, -3);
        assertEquals(5, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 0, -3));
        node = node.delete(collisionSet, 0, -3);
        assertEquals(4, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 4725297, 4725297));
        verifyContents(collisionSet, node, 1, 7, -1, 4725297);

        assertSame(node, node.delete(collisionSet, -2, -2));
        assertEquals(4, node.size(collisionSet));
        verifyContents(collisionSet, node, 1, 7, -1, 4725297);

        node = node.insert(collisionSet, 33, 33);
        assertEquals(5, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 33, 33));
        verifyContents(collisionSet, node, 1, 7, -1, 4725297, 33);

        node = node.delete(collisionSet, 1, 1);
        assertEquals(4, node.size(collisionSet));
        assertEquals(false, node.contains(collisionSet, 1, 1));
        verifyContents(collisionSet, node, 7, -1, 4725297, 33);

        assertSame(node, node.delete(collisionSet, -2, -2));

        node = node.delete(collisionSet, 4725297, 4725297);
        assertEquals(3, node.size(collisionSet));
        assertEquals(false, node.contains(collisionSet, 4725297, 4725297));
        verifyContents(collisionSet, node, 7, -1, 33);

        node = node.delete(collisionSet, -1, -1);
        assertEquals(2, node.size(collisionSet));
        assertEquals(false, node.contains(collisionSet, -1, -1));
        verifyContents(collisionSet, node, 7, 33);

        node = node.delete(collisionSet, 7, 7);
        assertEquals(1, node.size(collisionSet));
        assertEquals(false, node.contains(collisionSet, 7, 7));
        verifyContents(collisionSet, node, 33);

        node = node.delete(collisionSet, 33, 33);
        assertEquals(0, node.size(collisionSet));
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
        final ListCollisionSet<Checked> collisionSet = ListCollisionSet.instance();

        SetNode<Checked> node = SetEmptyNode.of();

        node = node.insert(collisionSet, a.hashCode(), a);
        assertEquals(1, node.size(collisionSet));
        assertSame(node, node.insert(collisionSet, a.hashCode(), a));

        node = node.insert(collisionSet, b.hashCode(), b);
        assertEquals(2, node.size(collisionSet));
        assertSame(node, node.insert(collisionSet, a.hashCode(), a));
        assertSame(node, node.insert(collisionSet, b.hashCode(), b));
        assertSame(node, node.delete(collisionSet, z.hashCode(), z));
        assertEquals(false, node.contains(collisionSet, z.hashCode(), z));

        node = node.insert(collisionSet, c.hashCode(), c);
        assertEquals(3, node.size(collisionSet));

        node = node.insert(collisionSet, x.hashCode(), x);
        assertEquals(4, node.size(collisionSet));

        node = node.insert(collisionSet, y.hashCode(), y);
        assertEquals(5, node.size(collisionSet));

        assertSame(node, node.delete(collisionSet, d.hashCode(), d));
        assertSame(node, node.delete(collisionSet, e.hashCode(), e));
        assertSame(node, node.delete(collisionSet, z.hashCode(), z));

        node = node
            .delete(collisionSet, x.hashCode(), x)
            .delete(collisionSet, a.hashCode(), a)
            .delete(collisionSet, b.hashCode(), b)
            .delete(collisionSet, c.hashCode(), c)
            .delete(collisionSet, y.hashCode(), y);
        assertEquals(0, node.size(collisionSet));
        assertSame(SetEmptyNode.of(), node);
    }

    public void testRollupOnDelete()
    {
        final CollisionSet<Integer> collisionSet = ListCollisionSet.instance();
        SetNode<Integer> empty = SetEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        SetNode<Integer> node = empty.insert(collisionSet, 0x1fffff, 0x1fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 0x1fffff, 0x1fffff));
        verifyContents(collisionSet, node, 0x1fffff);

        node = node.insert(collisionSet, 0x2fffff, 0x2fffff);
        assertEquals(true, node instanceof SetBranchNode);
        assertEquals(2, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x1fffff, 0x2fffff);

        node = node.delete(collisionSet, 0x1fffff, 0x1fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x2fffff);

        node = node.delete(collisionSet, 0x2fffff, 0x2fffff);
        assertEquals(true, node instanceof SetEmptyNode);
        assertEquals(0, node.size(collisionSet));
        verifyContents(collisionSet, node);
    }

    public void testRollupOnDelete2()
    {
        final CollisionSet<Integer> collisionSet = ListCollisionSet.instance();
        SetNode<Integer> empty = SetEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        SetNode<Integer> node = empty.insert(collisionSet, 0x2fffff, 0x2fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 0x2fffff, 0x2fffff));
        verifyContents(collisionSet, node, 0x2fffff);

        node = node.insert(collisionSet, 0x1fffff, 0x1fffff);
        assertEquals(true, node instanceof SetBranchNode);
        assertEquals(2, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x2fffff, 0x1fffff);

        node = node.delete(collisionSet, 0x2fffff, 0x2fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x1fffff);

        node = node.delete(collisionSet, 0x1fffff, 0x1fffff);
        assertEquals(true, node instanceof SetEmptyNode);
        assertEquals(0, node.size(collisionSet));
        verifyContents(collisionSet, node);
    }

    public void testRollupOnDelete3()
    {
        final CollisionSet<Integer> collisionSet = ListCollisionSet.instance();
        SetNode<Integer> empty = SetEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        SetNode<Integer> node = empty.insert(collisionSet, 0x2fffff, 0x2fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 0x2fffff, 0x2fffff));
        verifyContents(collisionSet, node, 0x2fffff);

        node = node.insert(collisionSet, 0x4fffffff, 0x4fffffff);
        assertEquals(true, node instanceof SetBranchNode);
        assertEquals(2, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x4fffffff, 0x2fffff);

        node = node.delete(collisionSet, 0x2fffff, 0x2fffff);
        assertEquals(true, node.isLeaf());
        assertEquals(1, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x4fffffff);

        node = node.delete(collisionSet, 0x4fffffff, 0x4fffffff);
        assertEquals(true, node instanceof SetEmptyNode);
        assertEquals(0, node.size(collisionSet));
        verifyContents(collisionSet, node);
    }

    public void testRollupOnDelete4()
    {
        final CollisionSet<Integer> collisionSet = ListCollisionSet.instance();
        SetNode<Integer> empty = SetEmptyNode.of();
        MutableDelta delta = new MutableDelta();

        SetNode<Integer> node = empty.insert(collisionSet, 0x2fffffff, 0x2fffffff);
        assertEquals(true, node instanceof SetSingleValueLeafNode);
        assertEquals(1, node.size(collisionSet));
        assertEquals(true, node.contains(collisionSet, 0x2fffffff, 0x2fffffff));
        verifyContents(collisionSet, node, 0x2fffffff);

        node = node.insert(collisionSet, 0x4fffff, 0x4fffff);
        assertEquals(true, node instanceof SetBranchNode);
        assertEquals(2, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x4fffff, 0x2fffffff);

        node = node.delete(collisionSet, 0x2fffffff, 0x2fffffff);
        assertEquals(true, node instanceof SetSingleValueLeafNode);
        assertEquals(1, node.size(collisionSet));
        verifyContents(collisionSet, node, 0x4fffff);

        node = node.delete(collisionSet, 0x4fffff, 0x4fffff);
        assertEquals(true, node instanceof SetEmptyNode);
        assertEquals(0, node.size(collisionSet));
        verifyContents(collisionSet, node);
    }

    public void testDeleteCollapseBranchIntoLeaf()
    {
        final CollisionSet<Integer> collisionSet = ListCollisionSet.instance();
        final MutableDelta size = new MutableDelta();
        SetNode<Integer> node = SetEmptyNode.of();
        node = node.insert(collisionSet, 7129, 7129);
        node = node.insert(collisionSet, 985, 985);
        node = node.delete(collisionSet, 7129, 7129);
        assertEquals(true, node.contains(collisionSet, 985, 985));
        node = node.delete(collisionSet, 985, 985);
        assertSame(SetEmptyNode.of(), node);
    }

    public void testRandom()
    {
        final CollisionSet<Integer> collisionSet = ListCollisionSet.instance();
        final Random r = new Random();

        for (int loop = 1; loop <= 50; ++loop) {
            final List<Integer> domain = IntStream.range(1, 1200)
                .boxed()
                .map(i -> r.nextInt())
                .collect(Collectors.toList());

            final MutableDelta size = new MutableDelta();
            SetNode<Integer> node = SetEmptyNode.of();
            for (Integer key : domain) {
                node = node.insert(collisionSet, key, key);
                assertEquals(true, node.contains(collisionSet, key, key));
            }
            node.checkInvariants(collisionSet);
            verifyIntContents(collisionSet, node, domain);

            Collections.shuffle(domain);
            for (Integer key : domain) {
                node = node.delete(collisionSet, key, key);
                assertSame(node, node.delete(collisionSet, key, key));
                assertEquals(false, node.contains(collisionSet, key, key));
            }
            node.checkInvariants(collisionSet);
            assertSame(SetEmptyNode.of(), node);
            assertEquals(0, node.size(collisionSet));
        }
    }

    private void verifyContents(CollisionSet<Integer> collisionSet,
                                SetNode<Integer> node,
                                Integer... values)
    {
        Set<Integer> expected = new HashSet<>(asList(values));
        Set<Integer> actual = collectValues(collisionSet, node);
        assertEquals(expected, actual);
        verifyConnectivity(collisionSet, node);
    }

    private void verifyIntContents(CollisionSet<Integer> collisionSet,
                                   SetNode<Integer> node,
                                   List<Integer> values)
    {
        Set<Integer> expected = new HashSet<>(values);
        Set<Integer> actual = collectValues(collisionSet, node);
        assertEquals(expected, actual);
        verifyConnectivity(collisionSet, node);
    }

    private <T, K, V> void verifyConnectivity(CollisionSet<T> collisionSet,
                                              SetNode<T> node)
    {
        for (T value : node.iterable(collisionSet)) {
            assertEquals(true, node.contains(collisionSet, value.hashCode(), value));
        }
    }

    @Nonnull
    private <T> Set<T> collectValues(CollisionSet<T> collisionSet,
                                     SetNode<T> node)
    {
        Set<T> actual = new HashSet<>();
        for (T value : node.iterable(collisionSet)) {
            actual.add(value);
        }
        return actual;
    }
}
