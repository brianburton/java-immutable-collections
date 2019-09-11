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

package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.common.StandardCollisionMapTests;

import java.io.IOException;
import java.util.stream.IntStream;

public class ListCollisionMapTest
    extends TestCase
{
    public void testStandard()
    {
        StandardCollisionMapTests.randomTests(ListCollisionMap.instance());
    }

    public void testForEach()
    {
        ListCollisionMap<Integer, Integer> transforms = ListCollisionMap.instance();
        CollisionMap.Node node = transforms.emptyNode();

        final StringBuilder sb = new StringBuilder();
        final Proc2<Integer, Integer> append = (k, v) -> {
            sb.append("[");
            sb.append(k);
            sb.append(",");
            sb.append(v);
            sb.append("]");
        };
        transforms.forEach(node, append);
        assertEquals("", sb.toString());

        node = transforms.update(node, 1, -1);
        transforms.forEach(node, append);
        assertEquals("[1,-1]", sb.toString());

        sb.delete(0, sb.length());
        JImmutableList<Integer> integers = IntStream.range(1, 500)
            .boxed()
            .collect(JImmutableTreeList.createListCollector());
        node = integers.reduce(node, (n, i) -> transforms.update(n, i, -i));
        String expected = integers.reduce("", (s, i) -> s + "[" + i + "," + -i + "]");
        transforms.forEach(node, append);
        assertEquals(expected, sb.toString());

        try {
            sb.delete(0, sb.length());
            transforms.forEachThrows(transforms.update(transforms.emptyNode(), 1, -1), (k, v) -> {
                sb.append("[");
                sb.append(k);
                sb.append(",");
                sb.append(v);
                sb.append("]");
                if (k == 1) {
                    throw new IOException();
                }
            });
            fail();
        } catch (IOException ex) {
            assertEquals("[1,-1]", sb.toString());
        }
        try {
            sb.delete(0, sb.length());
            transforms.forEachThrows(node, (k, v) -> {
                sb.append("[");
                sb.append(k);
                sb.append(",");
                sb.append(v);
                sb.append("]");
                if (k == 499) {
                    throw new IOException();
                }
            });
            fail();
        } catch (IOException ex) {
            assertEquals(expected, sb.toString());
        }
    }

    public void testReduce()
    {
        ListCollisionMap<Integer, Integer> transforms = ListCollisionMap.instance();
        CollisionMap.Node node = transforms.emptyNode();

        final Sum2<Integer, Integer, String> append = (s, k, v) -> s + "[" + k + "," + v + "]";
        assertEquals("", transforms.reduce(node, "", append));
        assertEquals("[1,-1]", transforms.reduce(transforms.update(node, 1, -1), "", append));

        JImmutableList<Integer> integers = IntStream.range(1, 500)
            .boxed()
            .collect(JImmutableTreeList.createListCollector());
        node = integers.reduce(node, (n, i) -> transforms.update(n, i, -i));
        String expected = integers.reduce("", (s, i) -> s + "[" + i + "," + -i + "]");
        assertEquals(expected, transforms.reduce(node, "", append));

        try {
            transforms.reduceThrows(transforms.update(transforms.emptyNode(), 1, -1), "", (s, k, v) -> {
                if (k == 1) {
                    throw new IOException();
                } else {
                    return s + "[" + k + "," + v + "]";
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
        try {
            transforms.reduceThrows(node, "", (s, k, v) -> {
                if (k == 499) {
                    throw new IOException();
                } else {
                    return s + "[" + k + "," + v + "]";
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
    }
}
