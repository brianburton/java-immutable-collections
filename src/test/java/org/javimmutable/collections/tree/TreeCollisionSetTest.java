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

import junit.framework.TestCase;
import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.common.StandardCollisionSetTests;

import java.io.IOException;

import static java.lang.Boolean.TRUE;

public class TreeCollisionSetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardCollisionSetTests.randomTests(TreeCollisionSet.instance());
    }

    public void testUpdateDelete()
    {
        TreeCollisionSet<Integer> collisionMap = TreeCollisionSet.instance();
        CollisionSet.Node node = collisionMap.empty();

        node = collisionMap.insert(node, 10);
        assertEquals(1, collisionMap.size(node));
        assertEquals(ValueNode.instance(10, TRUE), node);
        assertEquals(true, collisionMap.contains(node, 10));
        assertEquals(false, collisionMap.contains(node, 12));

        node = collisionMap.insert(node, 10);
        assertEquals(1, collisionMap.size(node));
        assertEquals(ValueNode.instance(10, TRUE), node);
        assertEquals(true, collisionMap.contains(node, 10));
        assertEquals(false, collisionMap.contains(node, 12));

        node = collisionMap.insert(node, 12);
        assertEquals(2, collisionMap.size(node));
        assertEquals(branch(Tuple2.of(10, TRUE), Tuple2.of(12, TRUE)), node);
        assertEquals(true, collisionMap.contains(node, 10));
        assertEquals(true, collisionMap.contains(node, 12));

        node = collisionMap.insert(node, 12);
        assertEquals(2, collisionMap.size(node));
        assertEquals(branch(Tuple2.of(10, TRUE), Tuple2.of(12, TRUE)), node);
        assertEquals(true, collisionMap.contains(node, 10));
        assertEquals(true, collisionMap.contains(node, 12));

        TreeCollisionSet.Node deleted = collisionMap.delete(node, 87);
        assertEquals(2, collisionMap.size(deleted));
        assertEquals(branch(Tuple2.of(10, TRUE), Tuple2.of(12, TRUE)), deleted);
        assertEquals(true, collisionMap.contains(deleted, 10));
        assertEquals(true, collisionMap.contains(deleted, 12));

        deleted = collisionMap.delete(deleted, 10);
        assertEquals(1, collisionMap.size(deleted));
        assertEquals(ValueNode.instance(12, TRUE), deleted);
        assertEquals(false, collisionMap.contains(deleted, 10));
        assertEquals(true, collisionMap.contains(deleted, 12));

        deleted = collisionMap.delete(deleted, 40);
        assertEquals(1, collisionMap.size(deleted));
        assertEquals(ValueNode.instance(12, TRUE), deleted);
        assertEquals(false, collisionMap.contains(deleted, 10));
        assertEquals(true, collisionMap.contains(deleted, 12));

        deleted = collisionMap.delete(deleted, 12);
        assertEquals(0, collisionMap.size(deleted));
        assertSame(FringeNode.instance(), deleted);
        assertEquals(false, collisionMap.contains(deleted, 10));
        assertEquals(false, collisionMap.contains(deleted, 12));

        assertEquals(true, collisionMap.contains(node, 10));
        assertEquals(true, collisionMap.contains(node, 12));
    }

    public void testForEach()
    {
        TreeCollisionSet<String> transforms = TreeCollisionSet.instance();
        CollisionSet.Node node = transforms.empty();

        final StringBuilder sb = new StringBuilder();
        final Proc1<String> append = k -> {
            sb.append("[");
            sb.append(k);
            sb.append("]");
        };
        transforms.forEach(node, append);
        assertEquals("", sb.toString());

        node = transforms.insert(node, "a");
        node = transforms.insert(node, "c");
        node = transforms.insert(node, "b");
        transforms.forEach(node, append);
        assertEquals("[a][b][c]", sb.toString());

        final Proc1Throws<String, IOException> appendThrows = k -> {
            sb.append("[");
            sb.append(k);
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
            assertEquals("[a][b]", sb.toString());
        }
    }

    public void testReduce()
    {
        TreeCollisionSet<String> transforms = TreeCollisionSet.instance();
        CollisionSet.Node node = transforms.empty();

        final Sum1<String, String> append = (s, k) -> s + "[" + k + "]";
        assertEquals("", transforms.reduce(node, "", append));

        node = transforms.insert(node, "a");
        node = transforms.insert(node, "c");
        node = transforms.insert(node, "b");
        assertEquals("[a][b][c]", transforms.reduce(node, "", append));

        final Sum1Throws<String, String, IOException> appendThrows = (s, k) -> {
            if (k.equals("b")) {
                throw new IOException();
            } else {
                return s + "[" + k + "]";
            }
        };
        try {
            transforms.reduceThrows(node, "", appendThrows);
            fail();
        } catch (IOException ex) {
            // pass
        }
    }

    private AbstractNode<Integer, Boolean> branch(Tuple2<Integer, Boolean> a,
                                                  Tuple2<Integer, Boolean> b)
    {
        return ValueNode.instance(a.getFirst(), a.getSecond()).assign(ComparableComparator.of(), b.getFirst(), b.getSecond());
    }
}
