///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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
import org.javimmutable.collections.IList;
import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.common.StandardCollisionSetTests;

import java.io.IOException;
import java.util.stream.IntStream;

public class ListCollisionSetTest
    extends TestCase
{
    public void testStandard()
    {
        StandardCollisionSetTests.randomTests(ListCollisionSet.instance());
    }

    public void testForEach()
    {
        ListCollisionSet<Integer> transforms = ListCollisionSet.instance();
        CollisionSet.Node node = transforms.empty();

        final StringBuilder sb = new StringBuilder();
        final Proc1<Integer> append = k -> {
            sb.append("[");
            sb.append(k);
            sb.append("]");
        };
        transforms.forEach(node, append);
        assertEquals("", sb.toString());

        node = transforms.insert(node, 1);
        transforms.forEach(node, append);
        assertEquals("[1]", sb.toString());

        sb.delete(0, sb.length());
        IList<Integer> integers = IntStream.range(1, 500)
            .boxed()
            .collect(TreeList.createListCollector());
        node = integers.reduce(node, (n, i) -> transforms.insert(n, i));
        String expected = integers.reduce("", (s, i) -> s + "[" + i + "]");
        transforms.forEach(node, append);
        assertEquals(expected, sb.toString());

        try {
            sb.delete(0, sb.length());
            transforms.forEachThrows(transforms.insert(transforms.empty(), 1), k -> {
                sb.append("[");
                sb.append(k);
                sb.append("]");
                if (k == 1) {
                    throw new IOException();
                }
            });
            fail();
        } catch (IOException ex) {
            assertEquals("[1]", sb.toString());
        }
        try {
            sb.delete(0, sb.length());
            transforms.forEachThrows(node, k -> {
                sb.append("[");
                sb.append(k);
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
        ListCollisionSet<Integer> transforms = ListCollisionSet.instance();
        CollisionSet.Node node = transforms.empty();

        final Sum1<Integer, String> append = (s, k) -> s + "[" + k + "]";
        assertEquals("", transforms.reduce(node, "", append));
        assertEquals("[1]", transforms.reduce(transforms.insert(node, 1), "", append));

        IList<Integer> integers = IntStream.range(1, 500)
            .boxed()
            .collect(TreeList.createListCollector());
        node = integers.reduce(node, (n, i) -> transforms.insert(n, i));
        String expected = integers.reduce("", (s, i) -> s + "[" + i + "]");
        assertEquals(expected, transforms.reduce(node, "", append));

        try {
            transforms.reduceThrows(transforms.insert(transforms.empty(), 1), "", (s, k) -> {
                if (k == 1) {
                    throw new IOException();
                } else {
                    return s + "[" + k + "]";
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
        try {
            transforms.reduceThrows(node, "", (s, k) -> {
                if (k == 499) {
                    throw new IOException();
                } else {
                    return s + "[" + k + "]";
                }
            });
            fail();
        } catch (IOException ex) {
            // pass
        }
    }
}
