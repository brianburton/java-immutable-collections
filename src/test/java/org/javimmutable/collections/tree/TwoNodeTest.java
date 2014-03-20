///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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
import org.javimmutable.collections.Holders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TwoNodeTest
        extends TestCase
{
    private Comparator<Integer> comparator;
    private TwoNode<Integer, Integer> node;

    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();
        comparator = ComparableComparator.of();
        node = new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                             new LeafNode<Integer, Integer>(12, 12),
                                             10,
                                             12);
    }

    @Override
    public void tearDown()
            throws Exception
    {
        comparator = null;
        node = null;
        super.tearDown();
    }

    public void testGetValueOr()
    {
        Integer defaultValue = -99;
        assertSame(defaultValue, node.getValueOr(comparator, 8, defaultValue));
        assertEquals(Integer.valueOf(10), node.getValueOr(comparator, 10, defaultValue));
        assertSame(defaultValue, node.getValueOr(comparator, 11, defaultValue));
        assertEquals(Integer.valueOf(12), node.getValueOr(comparator, 12, defaultValue));
        assertSame(defaultValue, node.getValueOr(comparator, 13, defaultValue));
    }

    public void testFind()
    {
        assertEquals(Holders.<Integer>of(), node.find(comparator, 8));
        assertEquals(Holders.of(10), node.find(comparator, 10));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 11));
        assertEquals(Holders.of(12), node.find(comparator, 12));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 13));
    }

    public void testFindEntry()
    {
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 8));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(new LeafNode<Integer, Integer>(10, 10)), node.findEntry(comparator, 10));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 11));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(new LeafNode<Integer, Integer>(12, 12)), node.findEntry(comparator, 12));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 13));
    }

    public void testVarious()
    {
        assertEquals(Integer.valueOf(12), node.getMaxKey());
        assertEquals(Integer.valueOf(10), node.getLeftMaxKey());
        assertEquals(Integer.valueOf(12), node.getRightMaxKey());
        assertEquals(new LeafNode<Integer, Integer>(10, 10), node.getLeft());
        assertEquals(new LeafNode<Integer, Integer>(12, 12), node.getRight());
        List<JImmutableTreeMap.Entry<Integer, Integer>> values = new ArrayList<JImmutableTreeMap.Entry<Integer, Integer>>();
        node.addEntriesTo(values);
        //noinspection AssertEqualsBetweenInconvertibleTypes,unchecked
        assertEquals(Arrays.asList(node.getLeft(), node.getRight()), values);
    }

    public void testVerifyDepthsMatch()
    {
        assertEquals(2, node.verifyDepthsMatch());

        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(node,
                                                                           node.getRight(),
                                                                           8,
                                                                           10);
        try {
            testNode.verifyDepthsMatch();
            fail();
        } catch (RuntimeException ex) {
            // expected
        }

        testNode = new TwoNode<Integer, Integer>(node.getLeft(),
                                                 node,
                                                 8,
                                                 10);
        try {
            testNode.verifyDepthsMatch();
            fail();
        } catch (RuntimeException ex) {
            // expected
        }
    }

    public void testLeftDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                             node.getLeft(),
                                                                                             node.getRight(),
                                                                                             8,
                                                                                             10,
                                                                                             12)),
                     node.leftDeleteMerge(comparator, new LeafNode<Integer, Integer>(8, 8)));
    }

    public void testRightDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new ThreeNode<Integer, Integer>(node.getLeft(),
                                                                                             node.getRight(),
                                                                                             new LeafNode<Integer, Integer>(14, 14),
                                                                                             10,
                                                                                             12,
                                                                                             14)),
                     node.rightDeleteMerge(comparator, new LeafNode<Integer, Integer>(14, 14)));
    }

    public void testUpdateLeftUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.assignImpl(comparator, 10, 10));
    }

    public void testUpdateLeftInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 20),
                                                                         node.getRight(),
                                                                         10,
                                                                         node.getRightMaxKey()),
                                           0);
        assertEquals(expected, node.assignImpl(comparator, 10, 20));
    }

    public void testUpdateLeftSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                           node.getLeft(),
                                                                           node.getRight(),
                                                                           8,
                                                                           node.getLeftMaxKey(),
                                                                           node.getRightMaxKey()),
                                           1);
        assertEquals(expected, node.assignImpl(comparator, 8, 8));
    }

    public void testUpdateRightUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.assignImpl(comparator, 12, 12));
    }

    public void testUpdateRightInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                         new LeafNode<Integer, Integer>(12, 20),
                                                                         node.getLeftMaxKey(),
                                                                         12),
                                           0);
        assertEquals(expected, node.assignImpl(comparator, 12, 20));
    }

    public void testUpdateRightSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(node.getLeft(),
                                                                           node.getRight(),
                                                                           new LeafNode<Integer, Integer>(14, 14),
                                                                           node.getLeftMaxKey(),
                                                                           node.getRightMaxKey(),
                                                                           14),
                                           1);
        assertEquals(expected, node.assignImpl(comparator, 14, 14));
    }

    public void testDeleteLeftUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.deleteImpl(comparator, 8));
    }

    public void testDeleteLeftInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                           new LeafNode<Integer, Integer>(9, 9),
                                                                                                           new LeafNode<Integer, Integer>(10, 10),
                                                                                                           8,
                                                                                                           9,
                                                                                                           10),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                         11,
                                                                                                         12),
                                                                           10,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         8,
                                                                                                                                         10),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                         11,
                                                                                                                                         12),
                                                                                                           10,
                                                                                                           12));
        assertEquals(expected, testNode.deleteImpl(comparator, 9));
    }

    public void testLeftEliminated()
    {
        assertEquals(DeleteResult.createRemnant(node.getRight()), node.deleteImpl(comparator, 10));
    }

    public void testDeleteLeftRemnant()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                         8,
                                                                                                         10),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                         11,
                                                                                                         12),
                                                                           10,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createRemnant(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(11, 11),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             8,
                                                                                                             11,
                                                                                                             12));
        assertEquals(expected, testNode.deleteImpl(comparator, 10));
    }

    public void testDeleteLeftRemnantInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                         8,
                                                                                                         10),
                                                                           new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                           new LeafNode<Integer, Integer>(13, 13),
                                                                                                           11,
                                                                                                           12,
                                                                                                           13),
                                                                           10,
                                                                           13);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         10,
                                                                                                                                         11),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                         new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                         12,
                                                                                                                                         13),
                                                                                                           11,
                                                                                                           13));
        assertEquals(expected, testNode.deleteImpl(comparator, 8));
    }

    public void testDeleteRightUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.deleteImpl(comparator, 14));
    }

    public void testDeleteRightInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(9, 9),
                                                                                                         8,
                                                                                                         9),
                                                                           new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                           new LeafNode<Integer, Integer>(11, 11),
                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                           10,
                                                                                                           11,
                                                                                                           12),
                                                                           9,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                         8,
                                                                                                                                         9),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                         11,
                                                                                                                                         12),
                                                                                                           9,
                                                                                                           12));
        assertEquals(expected, testNode.deleteImpl(comparator, 10));
    }

    public void testRightEliminated()
    {
        assertEquals(DeleteResult.createRemnant(node.getLeft()), node.deleteImpl(comparator, 12));
    }

    public void testDeleteRightRemnant()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                         8,
                                                                                                         10),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                         11,
                                                                                                         12),
                                                                           10,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createRemnant(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             8,
                                                                                                             10,
                                                                                                             12));
        assertEquals(expected, testNode.deleteImpl(comparator, 11));
    }

    public void testDeleteRightRemnantInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                           new LeafNode<Integer, Integer>(10, 10),
                                                                                                           new LeafNode<Integer, Integer>(11, 11),
                                                                                                           8,
                                                                                                           10,
                                                                                                           11),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                         new LeafNode<Integer, Integer>(13, 13),
                                                                                                         12,
                                                                                                         13),
                                                                           11,
                                                                           13);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         8,
                                                                                                                                         10),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                         11,
                                                                                                                                         13),
                                                                                                           10,
                                                                                                           13));
        assertEquals(expected, testNode.deleteImpl(comparator, 12));
    }
}
