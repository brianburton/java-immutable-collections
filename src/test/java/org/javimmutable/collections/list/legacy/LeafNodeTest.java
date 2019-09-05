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

package org.javimmutable.collections.list.legacy;

import junit.framework.TestCase;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeafNodeTest
    extends TestCase
{
    public void testFromList()
    {
        List<Integer> expected = new ArrayList<Integer>();
        for (int i = 1; i <= 32; ++i) {
            expected.add(0, i);
            Node<Integer> node = LeafNode.fromList(expected, 0, expected.size());
            node.checkInvariants();
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
        }

        List<Integer> reduced = new ArrayList<Integer>(expected);
        for (int i = 1; i <= 31; ++i) {
            reduced.remove(0);
            Node<Integer> node = LeafNode.fromList(expected, i, expected.size());
            node.checkInvariants();
            StandardIteratorTests.listIteratorTest(reduced, node.iterator());
        }
    }

    public void testRandomInserts()
    {
        Random r = new Random(10L);
        for (int loop = 1; loop <= 1000; ++loop) {
            List<Integer> expected = new ArrayList<Integer>();
            Node<Integer> node = EmptyNode.of();
            for (int i = 1; i <= 32; ++i) {
                if (r.nextBoolean()) {
                    expected.add(0, i);
                    node = node.insertFirst(i);
                } else {
                    expected.add(i);
                    node = node.insertLast(i);
                }
                assertTrue(node instanceof LeafNode);
                node.checkInvariants();
            }
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
        }
    }

    public void testRandomDeletes()
    {
        Random r = new Random(10L);
        for (int loop = 1; loop <= 1000; ++loop) {
            List<Integer> expected = new ArrayList<Integer>();
            Node<Integer> node = EmptyNode.of();
            for (int i = 1; i <= 32; ++i) {
                expected.add(i);
                node = node.insertLast(i);
                assertTrue(node instanceof LeafNode);
                node.checkInvariants();
            }
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
            for (int i = 1; i <= 32; ++i) {
                if (r.nextBoolean()) {
                    expected.remove(0);
                    node = node.deleteFirst();
                } else {
                    expected.remove(expected.size() - 1);
                    node = node.deleteLast();
                }
                if (node.isEmpty()) {
                    assertTrue(node instanceof EmptyNode);
                } else {
                    assertTrue(node instanceof LeafNode);
                }
                node.checkInvariants();
                StandardIteratorTests.listIteratorTest(expected, node.iterator());
            }
        }
    }

    public void testInsertFirstBranchCreation()
    {
        List<Integer> expected = new ArrayList<Integer>();
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32; ++i) {
            expected.add(0, i);
            node = node.insertFirst(i);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
        }
        expected.add(0, 33);
        node = node.insertFirst(33);
        assertTrue(node instanceof BranchNode);
        node.checkInvariants();
        StandardIteratorTests.listIteratorTest(expected, node.iterator());
    }

    public void testInsertLastBranchCreation()
    {
        List<Integer> expected = new ArrayList<Integer>();
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32; ++i) {
            expected.add(i);
            node = node.insertLast(i);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
        }
        expected.add(33);
        node = node.insertLast(33);
        assertTrue(node instanceof BranchNode);
        node.checkInvariants();
        StandardIteratorTests.listIteratorTest(expected, node.iterator());
    }

    public void testAssign()
    {
        List<Integer> expected = new ArrayList<Integer>();
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32; ++i) {
            expected.add(i);
            node = node.insertLast(i);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
        }
        for (int x = 0; x < 32; ++x) {
            expected.set(x, 1000 - x);
            node = node.assign(x, 1000 - x);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardIteratorTests.listIteratorTest(expected, node.iterator());
        }
    }
}
