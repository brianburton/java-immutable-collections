///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BranchNodeTest
        extends TestCase
{
    public void testDeleteFirst()
    {
        Node<Integer> node = BranchNode.forTesting(2, leaf(2, 1), nodesArray(1, 3), leaf(2, 35));
        assertEquals(36, node.size());
        node.checkInvariants();

        // deleting from prefix
        Node<Integer> changed = node.deleteFirst();
        assertEquals(35, changed.size());
        StandardCursorTest.listCursorTest(values(2, 36), changed.cursor());

        changed = changed.deleteFirst();
        assertEquals(34, changed.size());
        StandardCursorTest.listCursorTest(values(3, 36), changed.cursor());

        // deleting from body nodes
        for (int i = 1; i <= 32; ++i) {
            changed = changed.deleteFirst();
            assertEquals(34 - i, changed.size());
            StandardCursorTest.listCursorTest(values(3 + i, 36), changed.cursor());
        }

        // deleting from suffix
        changed = changed.deleteFirst();
        assertEquals(1, changed.size());
        StandardCursorTest.listCursorTest(Collections.singletonList(36), changed.cursor());

        changed = changed.deleteFirst();
        assertEquals(0, changed.size());
        StandardCursorTest.listCursorTest(Collections.<Integer>emptyList(), changed.cursor());

        try {
            changed.deleteFirst();
            fail();
        } catch (IllegalStateException ignored) {
            // expected
        }
    }

    public void testDeleteLast()
    {
        Node<Integer> node = BranchNode.forTesting(2, leaf(2, 1), nodesArray(1, 3), leaf(2, 35));
        assertEquals(36, node.size());
        node.checkInvariants();

        // deleting from suffix
        Node<Integer> changed = node.deleteLast();
        assertEquals(35, changed.size());
        StandardCursorTest.listCursorTest(values(1, 35), changed.cursor());
        changed.checkInvariants();

        changed = changed.deleteLast();
        assertEquals(34, changed.size());
        StandardCursorTest.listCursorTest(values(1, 34), changed.cursor());
        changed.checkInvariants();

        // deleting from body nodes
        for (int i = 1; i <= 32; ++i) {
            changed = changed.deleteLast();
            assertEquals(34 - i, changed.size());
            StandardCursorTest.listCursorTest(values(1, 34 - i), changed.cursor());
            changed.checkInvariants();
        }

        // deleting from prefix
        changed = changed.deleteLast();
        assertEquals(1, changed.size());
        StandardCursorTest.listCursorTest(Collections.singletonList(1), changed.cursor());
        changed.checkInvariants();

        changed = changed.deleteLast();
        assertEquals(0, changed.size());
        StandardCursorTest.listCursorTest(Collections.<Integer>emptyList(), changed.cursor());
        changed.checkInvariants();

        try {
            changed.deleteLast();
            fail();
        } catch (IllegalStateException ignored) {
            // expected
        }
    }

    public void testInsertFirst()
    {
        Node<Integer> node = BranchNode.forTesting(2, EmptyNode.<Integer>of(), ListHelper.<Integer>allocateNodes(0), EmptyNode.<Integer>of());
        assertEquals(0, node.size());
        node.checkInvariants();

        for (int i = 1; i <= 1024; ++i) {
            node = node.insertFirst(i);
            node.checkInvariants();
            assertEquals(i, node.size());
            assertEquals(2, node.getDepth());
        }
        StandardCursorTest.listCursorTest(values(1024, 1), node.cursor());

        // node is full so adding one more increases depth
        node = node.insertFirst(1025);
        node.checkInvariants();
        assertEquals(1025, node.size());
        assertEquals(3, node.getDepth());
        StandardCursorTest.listCursorTest(values(1025, 1), node.cursor());

        // fill up the node
        for (int i = 1026; i <= 32768; ++i) {
            node = node.insertFirst(i);
            node.checkInvariants();
            assertEquals(i, node.size());
            assertEquals(3, node.getDepth());
        }
        StandardCursorTest.listCursorTest(values(32768, 1), node.cursor());

        // node is full so adding one more increases depth
        node = node.insertFirst(32769);
        node.checkInvariants();
        assertEquals(32769, node.size());
        assertEquals(4, node.getDepth());
        StandardCursorTest.listCursorTest(values(32769, 1), node.cursor());

        // test get()
        for (int i = 1; i <= 32769; ++i) {
            assertEquals(Integer.valueOf(32770 - i), node.get(i - 1));
        }
    }

    public void testInsertLast()
    {
        Node<Integer> node = BranchNode.forTesting(2, EmptyNode.<Integer>of(), ListHelper.<Integer>allocateNodes(0), EmptyNode.<Integer>of());
        assertEquals(0, node.size());
        node.checkInvariants();

        for (int i = 1; i <= 1024; ++i) {
            node = node.insertLast(i);
            node.checkInvariants();
            assertEquals(i, node.size());
            assertEquals(2, node.getDepth());
        }
        StandardCursorTest.listCursorTest(values(1, 1024), node.cursor());

        // node is full so adding one more increases depth
        node = node.insertLast(1025);
        node.checkInvariants();
        assertEquals(1025, node.size());
        assertEquals(3, node.getDepth());
        StandardCursorTest.listCursorTest(values(1, 1025), node.cursor());

        // fill up the node
        for (int i = 1026; i <= 32768; ++i) {
            node = node.insertLast(i);
            node.checkInvariants();
            assertEquals(i, node.size());
            assertEquals(3, node.getDepth());
        }
        StandardCursorTest.listCursorTest(values(1, 32768), node.cursor());

        // node is full so adding one more increases depth
        node = node.insertLast(32769);
        node.checkInvariants();
        assertEquals(32769, node.size());
        assertEquals(4, node.getDepth());
        StandardCursorTest.listCursorTest(values(1, 32769), node.cursor());

        try {
            node.assign(-1, 0);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            // expected
        }
        try {
            node.assign(32770, 0);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            // expected
        }

        // test get()
        for (int i = 1; i <= 32769; ++i) {
            assertEquals(Integer.valueOf(i), node.get(i - 1));
        }
    }

    public void testGetAndAssign()
    {
        Node<Integer> node = BranchNode.forTesting(2, leaf(2, 1), nodesArray(3, 3), leaf(2, 99));
        assertEquals(100, node.size());
        node.checkInvariants();

        for (int i = 1; i <= 100; ++i) {
            assertEquals(Integer.valueOf(i), node.get(i - 1));
        }

        for (int i = 1; i <= 100; ++i) {
            node = node.assign(i - 1, -i);
        }

        for (int i = 1; i <= 100; ++i) {
            assertEquals(Integer.valueOf(-i), node.get(i - 1));
        }

        try {
            node.get(-1);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            // expected
        }
        try {
            node.get(100);
            fail();
        } catch (IndexOutOfBoundsException ignored) {
            // expected
        }
    }

    public void testInsertFirstDeleteFirstDepthReduction()
    {
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32769; ++i) {
            node = node.insertFirst(i);
        }
        node.checkInvariants();
        assertEquals(4, node.getDepth());
        assertEquals(32769, node.size());

        node = node.deleteFirst();
        node.checkInvariants();
        assertEquals(3, node.getDepth());
        assertEquals(32768, node.size());

        for (int i = 1; i <= 31743; ++i) {
            node = node.deleteFirst();
            assertEquals(String.valueOf(i), 3, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(1025, node.size());

        for (int i = 1; i <= 992; ++i) {
            node = node.deleteFirst();
            assertEquals(String.valueOf(i), 2, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(33, node.size());

        for (int i = 1; i <= 32; ++i) {
            node = node.deleteFirst();
            assertEquals(String.valueOf(i), 1, node.getDepth());
            assertTrue(node instanceof LeafNode);
        }
        node.checkInvariants();
        assertEquals(1, node.size());

        node = node.deleteFirst();
        assertSame(EmptyNode.of(), node);
        assertEquals(1, node.getDepth());
        assertEquals(0, node.size());
    }

    public void testInsertLastDeleteFirstDepthReduction()
    {
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32769; ++i) {
            node = node.insertLast(i);
        }
        node.checkInvariants();
        assertEquals(4, node.getDepth());
        assertEquals(32769, node.size());

        for (int i = 1; i <= 31743; ++i) {
            node = node.deleteFirst();
            assertEquals(String.valueOf(i), 4, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(1026, node.size());

        for (int i = 1; i <= 992; ++i) {
            node = node.deleteFirst();
            assertEquals(String.valueOf(i), 3, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(34, node.size());

        for (int i = 1; i <= 32; ++i) {
            node = node.deleteFirst();
            assertEquals(String.valueOf(i), 2, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(2, node.size());

        node = node.deleteFirst();
        assertEquals(1, node.getDepth());
        assertEquals(1, node.size());

        node = node.deleteFirst();
        assertSame(EmptyNode.of(), node);
        assertEquals(1, node.getDepth());
        assertEquals(0, node.size());
    }

    public void testInsertFirstDeleteLastDepthReduction()
    {
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32769; ++i) {
            node = node.insertFirst(i);
        }
        node.checkInvariants();
        assertEquals(4, node.getDepth());
        assertEquals(32769, node.size());

        for (int i = 1; i <= 31743; ++i) {
            node = node.deleteLast();
            assertEquals(String.valueOf(i), 4, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(1026, node.size());

        for (int i = 1; i <= 992; ++i) {
            node = node.deleteLast();
            assertEquals(String.valueOf(i), 3, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(34, node.size());

        for (int i = 1; i <= 32; ++i) {
            node = node.deleteLast();
            assertEquals(String.valueOf(i), 2, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(2, node.size());

        node = node.deleteLast();
        assertEquals(1, node.getDepth());
        assertEquals(1, node.size());

        node = node.deleteLast();
        assertSame(EmptyNode.of(), node);
        assertEquals(1, node.getDepth());
        assertEquals(0, node.size());
    }

    public void testInsertLastDeleteLastDepthReduction()
    {
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32769; ++i) {
            node = node.insertLast(i);
        }
        node.checkInvariants();
        assertEquals(4, node.getDepth());
        assertEquals(32769, node.size());

        for (int i = 1; i <= 31744; ++i) {
            node = node.deleteLast();
            assertEquals(String.valueOf(i), 3, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(1025, node.size());

        for (int i = 1; i <= 992; ++i) {
            node = node.deleteLast();
            assertEquals(String.valueOf(i), 2, node.getDepth());
        }
        node.checkInvariants();
        assertEquals(33, node.size());

        for (int i = 1; i <= 32; ++i) {
            node = node.deleteLast();
            assertEquals(String.valueOf(i), 1, node.getDepth());
            assertTrue(node instanceof LeafNode);
        }
        node.checkInvariants();
        assertEquals(1, node.size());

        node = node.deleteLast();
        assertSame(EmptyNode.of(), node);
        assertEquals(1, node.getDepth());
        assertEquals(0, node.size());
    }

    public void testBuilder()
    {
        List<Integer> expected = new ArrayList<Integer>();
        assertSame(EmptyNode.of(), BranchNode.<Integer>builder().build());

        for (int size = 1; size <= 33000; ++size) {
            expected.add(size);
            Node<Integer> node = BranchNode.<Integer>builder().add(expected).build();
            node.checkInvariants();
            for (int i = 0; i < expected.size(); ++i) {
                assertEquals(expected.get(i), node.get(i));
            }
        }

        Random r = new Random(10L);
        for (int loop = 1; loop <= 100; ++loop) {
            expected = values(1, 32768 + r.nextInt(1500000));
            Node<Integer> node = BranchNode.<Integer>builder().add(expected).build();
            node.checkInvariants();
            for (int i = 0; i < expected.size(); ++i) {
                assertEquals(expected.get(i), node.get(i));
            }
        }
    }

    static Node<Integer>[] nodesArray(int length,
                                      int value)
    {
        Node<Integer>[] nodes = ListHelper.allocateNodes(length);
        for (int i = 0; i < length; ++i) {
            nodes[i] = leaf(32, value);
            value += 32;
        }
        return nodes;
    }

    static Node<Integer> leaf(int size,
                              int value)
    {
        Integer[] values = new Integer[size];
        for (int index = 0; index < size; ++index) {
            values[index] = value++;
        }
        return LeafNode.forTesting(values);
    }

    static List<Integer> values(int first,
                                int last)
    {
        List<Integer> answer = new ArrayList<Integer>();
        if (last == first) {
            answer.add(first);
        } else if (last < first) {
            for (int i = first; i >= last; --i) {
                answer.add(i);
            }
        } else {
            for (int i = first; i <= last; ++i) {
                answer.add(i);
            }
        }
        return answer;
    }
}
