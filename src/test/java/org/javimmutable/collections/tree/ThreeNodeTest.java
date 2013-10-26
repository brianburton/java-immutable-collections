///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

public class ThreeNodeTest
        extends TestCase
{
    private final Comparator<Integer> comparator = ComparableComparator.of();
    private ThreeNode<Integer, Integer> node;

    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();
        node = new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                               new LeafNode<Integer, Integer>(12, 12),
                                               new LeafNode<Integer, Integer>(14, 14),
                                               10,
                                               12,
                                               14);
    }

    @Override
    public void tearDown()
            throws Exception
    {
        node = null;
        super.tearDown();
    }

    public void testFind()
    {
        assertEquals(Holders.<Integer>of(), node.find(comparator, 8));
        assertEquals(Holders.of(10), node.find(comparator, 10));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 11));
        assertEquals(Holders.of(12), node.find(comparator, 12));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 13));
        assertEquals(Holders.of(14), node.find(comparator, 14));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 15));
    }

    public void testFindEntry()
    {
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 8));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(new LeafNode<Integer, Integer>(10, 10)), node.findEntry(comparator, 10));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 11));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(new LeafNode<Integer, Integer>(12, 12)), node.findEntry(comparator, 12));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 13));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(new LeafNode<Integer, Integer>(14, 14)), node.findEntry(comparator, 14));
        assertEquals(Holders.<JImmutableTreeMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 15));
    }

    public void testVarious()
    {
        assertEquals(Integer.valueOf(14), node.getMaxKey());
        assertEquals(Integer.valueOf(10), node.getLeftMaxKey());
        assertEquals(Integer.valueOf(12), node.getMiddleMaxKey());
        assertEquals(Integer.valueOf(14), node.getRightMaxKey());
        assertEquals(new LeafNode<Integer, Integer>(10, 10), node.getLeft());
        assertEquals(new LeafNode<Integer, Integer>(12, 12), node.getMiddle());
        assertEquals(new LeafNode<Integer, Integer>(14, 14), node.getRight());
        List<JImmutableTreeMap.Entry<Integer, Integer>> values = new ArrayList<JImmutableTreeMap.Entry<Integer, Integer>>();
        node.addEntriesTo(values);
        //noinspection AssertEqualsBetweenInconvertibleTypes,unchecked
        assertEquals(Arrays.asList(node.getLeft(), node.getMiddle(), node.getRight()), values);
    }

    public void testVerifyDepthsMatch()
    {
        assertEquals(2, node.verifyDepthsMatch());

        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(node,
                                                                               node.getMiddle(),
                                                                               node.getRight(),
                                                                               8,
                                                                               10,
                                                                               12);
        try {
            testNode.verifyDepthsMatch();
            fail();
        } catch (RuntimeException ex) {
            // expected
        }

        testNode = new ThreeNode<Integer, Integer>(node.getLeft(),
                                                   node,
                                                   node.getRight(),
                                                   8,
                                                   10,
                                                   12);
        try {
            testNode.verifyDepthsMatch();
            fail();
        } catch (RuntimeException ex) {
            // expected
        }

        testNode = new ThreeNode<Integer, Integer>(node.getLeft(),
                                                   node.getMiddle(),
                                                   node,
                                                   8,
                                                   10,
                                                   12);
        try {
            testNode.verifyDepthsMatch();
            fail();
        } catch (RuntimeException ex) {
            // expected
        }
    }

    public void testLeftDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                           node.getLeft(),
                                                                                           8,
                                                                                           10),
                                                             new TwoNode<Integer, Integer>(node.getMiddle(),
                                                                                           node.getRight(),
                                                                                           12,
                                                                                           14)),
                     node.leftDeleteMerge(comparator, new LeafNode<Integer, Integer>(8, 8)));
    }

    public void testRightDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                                           node.getMiddle(),
                                                                                           10,
                                                                                           12),
                                                             new TwoNode<Integer, Integer>(node.getRight(),
                                                                                           new LeafNode<Integer, Integer>(16, 16),
                                                                                           14,
                                                                                           16)),
                     node.rightDeleteMerge(comparator, new LeafNode<Integer, Integer>(16, 16)));
    }

    public void testUpdateLeftUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.update(comparator, 10, 10));
    }

    public void testUpdateLeftInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 20),
                                                                           node.getMiddle(),
                                                                           node.getRight(),
                                                                           node.getLeftMaxKey(),
                                                                           node.getMiddleMaxKey(),
                                                                           node.getRightMaxKey()),
                                           0);
        assertEquals(expected, node.update(comparator, 10, 20));
    }

    public void testUpdateLeftSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createSplit(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                       node.getLeft(),
                                                                       8,
                                                                       node.getLeftMaxKey()),
                                         new TwoNode<Integer, Integer>(node.getMiddle(),
                                                                       node.getRight(),
                                                                       node.getMiddleMaxKey(),
                                                                       node.getRightMaxKey()),
                                         1);
        assertEquals(expected, node.update(comparator, 8, 8));
    }

    public void testUpdateMiddleUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.update(comparator, 12, 12));
    }

    public void testUpdateMiddleInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(node.getLeft(),
                                                                           new LeafNode<Integer, Integer>(12, 24),
                                                                           node.getRight(),
                                                                           node.getLeftMaxKey(),
                                                                           node.getMiddleMaxKey(),
                                                                           node.getRightMaxKey()),
                                           0);
        assertEquals(expected, node.update(comparator, 12, 24));
    }

    public void testUpdateMiddleSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createSplit(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                       new LeafNode<Integer, Integer>(11, 11),
                                                                       node.getLeftMaxKey(),
                                                                       11),
                                         new TwoNode<Integer, Integer>(node.getMiddle(),
                                                                       node.getRight(),
                                                                       node.getMiddleMaxKey(),
                                                                       node.getRightMaxKey()),
                                         1);
        assertEquals(expected, node.update(comparator, 11, 11));
    }

    public void testUpdateRightUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.update(comparator, 14, 14));
    }

    public void testUpdateRightInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(node.getLeft(),
                                                                           node.getMiddle(),
                                                                           new LeafNode<Integer, Integer>(14, 28),
                                                                           node.getLeftMaxKey(),
                                                                           node.getMiddleMaxKey(),
                                                                           node.getRightMaxKey()),
                                           0);
        assertEquals(expected, node.update(comparator, 14, 28));
    }

    public void testUpdateRightSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createSplit(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                       node.getMiddle(),
                                                                       node.getLeftMaxKey(),
                                                                       node.getMiddleMaxKey()),
                                         new TwoNode<Integer, Integer>(node.getRight(),
                                                                       new LeafNode<Integer, Integer>(16, 16),
                                                                       node.getRightMaxKey(),
                                                                       16),
                                         1);
        assertEquals(expected, node.update(comparator, 16, 16));
    }

    public void testDeleteLeftUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.delete(comparator, 8));
    }

    public void testDeleteLeftInPlace()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                               new LeafNode<Integer, Integer>(9, 9),
                                                                                                               new LeafNode<Integer, Integer>(10, 10),
                                                                                                               8,
                                                                                                               9,
                                                                                                               10),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                               new LeafNode<Integer, Integer>(12, 12),
                                                                                                               new LeafNode<Integer, Integer>(13, 13),
                                                                                                               11,
                                                                                                               12,
                                                                                                               13),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(14, 14),
                                                                                                               new LeafNode<Integer, Integer>(15, 15),
                                                                                                               new LeafNode<Integer, Integer>(16, 16),
                                                                                                               14,
                                                                                                               15,
                                                                                                               16),
                                                                               10,
                                                                               13,
                                                                               16);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                           new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                           8,
                                                                                                                                           10),
                                                                                                             new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                             new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                             11,
                                                                                                                                             12,
                                                                                                                                             13),
                                                                                                             new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                             new LeafNode<Integer, Integer>(15, 15),
                                                                                                                                             new LeafNode<Integer, Integer>(16, 16),
                                                                                                                                             14,
                                                                                                                                             15,
                                                                                                                                             16),
                                                                                                             10,
                                                                                                             13,
                                                                                                             16));
        assertEquals(expected, testNode.delete(comparator, 9));
    }

    public void testLeftEliminated()
    {
        assertEquals(DeleteResult.createInPlace(new TwoNode<Integer, Integer>(node.getMiddle(),
                                                                              node.getRight(),
                                                                              node.getMiddleMaxKey(),
                                                                              node.getRightMaxKey())),
                     node.delete(comparator, 10));
    }

    public void testDeleteLeftRemnant1()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                             8,
                                                                                                             10),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             11,
                                                                                                             12),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                             new LeafNode<Integer, Integer>(14, 14),
                                                                                                             13,
                                                                                                             14),
                                                                               10,
                                                                               12,
                                                                               14);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                           new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                           10,
                                                                                                                                           11,
                                                                                                                                           12),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                         new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                         13,
                                                                                                                                         14),
                                                                                                           12,
                                                                                                           14));
        assertEquals(expected, testNode.delete(comparator, 8));
    }

    public void testDeleteLeftRemnant2()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(9, 9),
                                                                                                             8,
                                                                                                             9),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                               new LeafNode<Integer, Integer>(11, 11),
                                                                                                               new LeafNode<Integer, Integer>(12, 12),
                                                                                                               10,
                                                                                                               11,
                                                                                                               12),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                             new LeafNode<Integer, Integer>(14, 14),
                                                                                                             13,
                                                                                                             14),
                                                                               9,
                                                                               12,
                                                                               14);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                           new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                           9,
                                                                                                                                           10),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                           11,
                                                                                                                                           12),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                           new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                           13,
                                                                                                                                           14),
                                                                                                             10,
                                                                                                             12,
                                                                                                             14));
        assertEquals(expected, testNode.delete(comparator, 8));
    }

    public void testDeleteMiddleUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.delete(comparator, 11));
    }

    public void testDeleteMiddleInPlace()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                               new LeafNode<Integer, Integer>(9, 9),
                                                                                                               new LeafNode<Integer, Integer>(10, 10),
                                                                                                               8,
                                                                                                               9,
                                                                                                               10),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                               new LeafNode<Integer, Integer>(12, 12),
                                                                                                               new LeafNode<Integer, Integer>(13, 13),
                                                                                                               11,
                                                                                                               12,
                                                                                                               13),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(14, 14),
                                                                                                               new LeafNode<Integer, Integer>(15, 15),
                                                                                                               new LeafNode<Integer, Integer>(16, 16),
                                                                                                               14,
                                                                                                               15,
                                                                                                               16),
                                                                               10,
                                                                               13,
                                                                               16);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new ThreeNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                             new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                             8,
                                                                                                                                             9,
                                                                                                                                             10),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                           new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                           11,
                                                                                                                                           13),
                                                                                                             new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                             new LeafNode<Integer, Integer>(15, 15),
                                                                                                                                             new LeafNode<Integer, Integer>(16, 16),
                                                                                                                                             14,
                                                                                                                                             15,
                                                                                                                                             16),
                                                                                                             10,
                                                                                                             13,
                                                                                                             16));
        assertEquals(expected, testNode.delete(comparator, 12));
    }

    public void testMiddleEliminated()
    {
        assertEquals(DeleteResult.createInPlace(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                              node.getRight(),
                                                                              node.getLeftMaxKey(),
                                                                              node.getRightMaxKey())),
                     node.delete(comparator, 12));
    }

    public void testDeleteMiddleRemnant1()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                             8,
                                                                                                             10),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             11,
                                                                                                             12),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                             new LeafNode<Integer, Integer>(14, 14),
                                                                                                             13,
                                                                                                             14),
                                                                               10,
                                                                               12,
                                                                               14);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         8,
                                                                                                                                         10),
                                                                                                           new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                           new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                           new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                           12,
                                                                                                                                           13,
                                                                                                                                           14),
                                                                                                           10,
                                                                                                           14));
        assertEquals(expected, testNode.delete(comparator, 11));
    }

    public void testDeleteMiddleRemnant2()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(9, 9),
                                                                                                             8,
                                                                                                             9),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                             new LeafNode<Integer, Integer>(11, 11),
                                                                                                             10,
                                                                                                             11),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                               new LeafNode<Integer, Integer>(13, 13),
                                                                                                               new LeafNode<Integer, Integer>(14, 14),
                                                                                                               12,
                                                                                                               13,
                                                                                                               14),
                                                                               9,
                                                                               11,
                                                                               14);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                           new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                           8,
                                                                                                                                           9),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                           11,
                                                                                                                                           12),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                           new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                           13,
                                                                                                                                           14),
                                                                                                             9,
                                                                                                             12,
                                                                                                             14));
        assertEquals(expected, testNode.delete(comparator, 10));
    }

    public void testDeleteRightUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.delete(comparator, 15));
    }

    public void testDeleteRightInPlace()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                               new LeafNode<Integer, Integer>(9, 9),
                                                                                                               new LeafNode<Integer, Integer>(10, 10),
                                                                                                               8,
                                                                                                               9,
                                                                                                               10),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                               new LeafNode<Integer, Integer>(12, 12),
                                                                                                               new LeafNode<Integer, Integer>(13, 13),
                                                                                                               11,
                                                                                                               12,
                                                                                                               13),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(14, 14),
                                                                                                               new LeafNode<Integer, Integer>(15, 15),
                                                                                                               new LeafNode<Integer, Integer>(16, 16),
                                                                                                               14,
                                                                                                               15,
                                                                                                               16),
                                                                               10,
                                                                               13,
                                                                               16);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new ThreeNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                             new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                             8,
                                                                                                                                             9,
                                                                                                                                             10),
                                                                                                             new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                             new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                             11,
                                                                                                                                             12,
                                                                                                                                             13),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(14, 14),
                                                                                                                                           new LeafNode<Integer, Integer>(16, 16),
                                                                                                                                           14,
                                                                                                                                           16),
                                                                                                             10,
                                                                                                             13,
                                                                                                             16));
        assertEquals(expected, testNode.delete(comparator, 15));
    }

    public void testRightEliminated()
    {
        assertEquals(DeleteResult.createInPlace(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                              node.getMiddle(),
                                                                              node.getLeftMaxKey(),
                                                                              node.getMiddleMaxKey())),
                     node.delete(comparator, 14));
    }

    public void testDeleteRightRemnant1()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                             8,
                                                                                                             10),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             11,
                                                                                                             12),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                             new LeafNode<Integer, Integer>(14, 14),
                                                                                                             13,
                                                                                                             14),
                                                                               10,
                                                                               12,
                                                                               14);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
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
                                                                                                           13));
        assertEquals(expected, testNode.delete(comparator, 14));
    }

    public void testDeleteRightRemnant2()
    {
        ThreeNode<Integer, Integer> testNode = new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(9, 9),
                                                                                                             8,
                                                                                                             9),
                                                                               new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                               new LeafNode<Integer, Integer>(11, 11),
                                                                                                               new LeafNode<Integer, Integer>(12, 12),
                                                                                                               10,
                                                                                                               11,
                                                                                                               12),
                                                                               new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(13, 13),
                                                                                                             new LeafNode<Integer, Integer>(14, 14),
                                                                                                             13,
                                                                                                             14),
                                                                               9,
                                                                               12,
                                                                               14);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new ThreeNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                           new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                           8,
                                                                                                                                           9),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                           new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                           10,
                                                                                                                                           11),
                                                                                                             new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                           new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                           12,
                                                                                                                                           13),
                                                                                                             9,
                                                                                                             11,
                                                                                                             13));
        assertEquals(expected, testNode.delete(comparator, 14));
    }
}
