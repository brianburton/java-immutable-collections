///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.common.StandardCollisionMapTests;
import org.javimmutable.collections.common.TestUtil;

public class TreeCollisionMapTest
    extends TestCase
{
    public void testStandard()
    {
        StandardCollisionMapTests.randomTests(TreeCollisionMap.instance());
    }

    public void testUpdateDelete()
    {
        TreeCollisionMap<Integer, Integer> collisionMap = TreeCollisionMap.instance();
        CollisionMap.Node node = collisionMap.empty();

        node = collisionMap.update(node, 10, 100);
        assertEquals(1, collisionMap.size(node));
        assertEquals(ValueNode.instance(10, 100), node);

        node = collisionMap.update(node, 10, 1000);
        assertEquals(1, collisionMap.size(node));
        assertEquals(ValueNode.instance(10, 1000), node);

        node = collisionMap.update(node, 12, 60);
        assertEquals(2, collisionMap.size(node));
        assertEquals(branch(Tuple2.of(10, 1000), Tuple2.of(12, 60)), node);

        node = collisionMap.update(node, 12, 90);
        assertEquals(2, collisionMap.size(node));
        assertEquals(branch(Tuple2.of(10, 1000), Tuple2.of(12, 90)), node);

        TreeCollisionMap.Node deleted = collisionMap.delete(node, 87);
        assertEquals(2, collisionMap.size(deleted));
        assertEquals(branch(Tuple2.of(10, 1000), Tuple2.of(12, 90)), deleted);

        deleted = collisionMap.delete(deleted, 10);
        assertEquals(1, collisionMap.size(deleted));
        assertEquals(ValueNode.instance(12, 90), deleted);

        deleted = collisionMap.delete(deleted, 40);
        assertEquals(1, collisionMap.size(deleted));
        assertEquals(ValueNode.instance(12, 90), deleted);

        deleted = collisionMap.delete(deleted, 12);
        assertEquals(0, collisionMap.size(deleted));
        assertSame(FringeNode.instance(), deleted);
    }

    public void testFindGet()
    {
        TreeCollisionMap<Integer, Integer> transforms = TreeCollisionMap.instance();
        CollisionMap.Node node = transforms.empty();
        node = transforms.update(node, 10, 100);
        node = transforms.update(node, 18, 180);
        node = transforms.update(node, 12, 60);
        node = transforms.update(node, -6, -60);
        node = transforms.update(node, 12, 90);
        assertEquals(4, transforms.size(node));

        assertEquals(Holders.nullable(100), transforms.findValue(node, 10));
        assertEquals(Holders.nullable(90), transforms.findValue(node, 12));
        assertEquals(Holders.nullable(180), transforms.findValue(node, 18));
        assertEquals(Holders.nullable(-60), transforms.findValue(node, -6));
        assertEquals(Holder.<Integer>none(), transforms.findValue(node, 11));

        assertEquals(Holders.<IMapEntry<Integer, Integer>>nullable(MapEntry.of(10, 100)), transforms.findEntry(node, 10));
        assertEquals(Holders.<IMapEntry<Integer, Integer>>nullable(MapEntry.of(12, 90)), transforms.findEntry(node, 12));
        assertEquals(Holders.<IMapEntry<Integer, Integer>>nullable(MapEntry.of(18, 180)), transforms.findEntry(node, 18));
        assertEquals(Holders.<IMapEntry<Integer, Integer>>nullable(MapEntry.of(-6, -60)), transforms.findEntry(node, -6));
        assertEquals(Holder.<IMapEntry<Integer, Integer>>none(), transforms.findEntry(node, 11));

        List<IMapEntry<Integer, Integer>> expected = new ArrayList<>();
        expected.add(MapEntry.of(-6, -60));
        expected.add(MapEntry.of(10, 100));
        expected.add(MapEntry.of(12, 90));
        expected.add(MapEntry.of(18, 180));
        TestUtil.verifyContents(expected, transforms.iterable(node));
    }

    public void testForEach()
    {
        TreeCollisionMap<String, String> transforms = TreeCollisionMap.instance();
        CollisionMap.Node node = transforms.empty();

        final StringBuilder sb = new StringBuilder();
        final Proc2<String, String> append = (k, v) -> {
            sb.append("[");
            sb.append(k);
            sb.append(",");
            sb.append(v);
            sb.append("]");
        };
        transforms.forEach(node, append);
        assertEquals("", sb.toString());

        node = transforms.update(node, "a", "A");
        node = transforms.update(node, "c", "C");
        node = transforms.update(node, "b", "B");
        transforms.forEach(node, append);
        assertEquals("[a,A][b,B][c,C]", sb.toString());

        final Proc2Throws<String, String, IOException> appendThrows = (k, v) -> {
            sb.append("[");
            sb.append(k);
            sb.append(",");
            sb.append(v);
            sb.append("]");
            if (k.equals("b")) {
                throw new IOException();
            }
        };
        try {
            sb.delete(0, sb.length());
            transforms.forEachThrows(node, appendThrows);
            fail();
        } catch (IOException ex) {
            assertEquals("[a,A][b,B]", sb.toString());
        }
    }

    public void testReduce()
    {
        TreeCollisionMap<String, String> transforms = TreeCollisionMap.instance();
        CollisionMap.Node node = transforms.empty();

        final Sum2<String, String, String> append = (s, k, v) -> s + "[" + k + "," + v + "]";
        assertEquals("", transforms.reduce(node, "", append));

        node = transforms.update(node, "a", "A");
        node = transforms.update(node, "c", "C");
        node = transforms.update(node, "b", "B");
        assertEquals("[a,A][b,B][c,C]", transforms.reduce(node, "", append));

        final Sum2Throws<String, String, String, IOException> appendThrows = (s, k, v) -> {
            if (k.equals("b")) {
                throw new IOException();
            } else {
                return s + "[" + k + "," + v + "]";
            }
        };
        try {
            transforms.reduceThrows(node, "", appendThrows);
            fail();
        } catch (IOException ex) {
            // pass
        }
    }

    private AbstractNode<Integer, Integer> branch(Tuple2<Integer, Integer> a,
                                                  Tuple2<Integer, Integer> b)
    {
        return ValueNode.instance(a.getFirst(), a.getSecond()).assign(ComparableComparator.of(), b.getFirst(), b.getSecond());
    }
}
