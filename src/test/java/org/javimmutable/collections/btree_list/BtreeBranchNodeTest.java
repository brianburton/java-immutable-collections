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

package org.javimmutable.collections.btree_list;

import junit.framework.TestCase;
import org.javimmutable.collections.cursors.StandardCursorTest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.btree_list.BtreeBranchNode.forTesting;
import static org.javimmutable.collections.btree_list.BtreeNode.*;
import static org.javimmutable.collections.cursors.StandardCursor.*;

public class BtreeBranchNodeTest
    extends TestCase
{
    public void testAppend()
    {
        BtreeNode<Integer> node = emptyNode();
        assertEquals(1, node.childCount());
        assertEquals(0, node.valueCount());
        List<Integer> expected = new ArrayList<Integer>();
        while (true) {
            final int nextValue = node.valueCount();
            BtreeInsertResult<Integer> result = node.append(nextValue);
            if (result.type == BtreeInsertResult.Type.SPLIT) {
                break;
            }
            expected.add(nextValue);
            node = result.newNode;
            assertEquals(Integer.valueOf(nextValue), node.get(node.valueCount() - 1));
        }
        assertEquals(MAX_CHILDREN, node.childCount());
        assertEquals(((MAX_CHILDREN - 1) * (MIN_CHILDREN + 1)) + MAX_CHILDREN, node.valueCount());
        StandardCursorTest.listCursorTest(expected, node.cursor());
    }

    public void testInsertZero()
    {
        BtreeNode<Integer> node = emptyNode();
        assertEquals(1, node.childCount());
        assertEquals(0, node.valueCount());
        List<Integer> expected = new ArrayList<Integer>();
        while (true) {
            final int nextValue = node.valueCount();
            BtreeInsertResult<Integer> result = node.insertAt(0, nextValue);
            if (result.type == BtreeInsertResult.Type.SPLIT) {
                break;
            }
            expected.add(0, nextValue);
            node = result.newNode;
            assertEquals(Integer.valueOf(nextValue), node.get(0));
        }
        assertEquals(MAX_CHILDREN, node.childCount());
        assertEquals(((MAX_CHILDREN - 1) * (MIN_CHILDREN + 1)) + MAX_CHILDREN, node.valueCount());
        StandardCursorTest.listCursorTest(expected, node.cursor());
    }

    public void testDeleteAt()
    {
        final BtreeNode<Integer> fullNode = filledNode();
        final List<Integer> fullExpected = list(fullNode.valueCount());
        verifyNodeContents(fullExpected, fullNode);

        for (int i = 0; i < fullNode.valueCount(); ++i) {
            // test deleting all values from i forward
            BtreeNode<Integer> node = fullNode;
            List<Integer> expected = new ArrayList<Integer>(fullExpected);
            while (expected.size() > i) {
                node = node.delete(i);
                expected.remove(i);
                verifyNodeContents(expected, node);
            }
        }
    }

    public void testAssign()
    {
        BtreeNode<Integer> node = filledNode();
        List<Integer> expected = list(node.valueCount());
        while (node.valueCount() > 0) {
            verifyNodeContents(expected, node);
            for (int i = 0; i < expected.size(); ++i) {
                int newValue = expected.get(i) + 1;
                node = node.assign(i, newValue);
                expected.set(i, newValue);
                verifyNodeContents(expected, node);
            }
            node = node.delete(node.valueCount() - 1);
            expected.remove(expected.size() - 1);
        }
    }

    public void testInsertNode()
    {
        BtreeNode<Integer> left = node(1, MIN_CHILDREN);
        BtreeNode<Integer> right = node(MIN_CHILDREN + 1, MAX_CHILDREN);
        verifyInplaceResult(1, MAX_CHILDREN, left.insertNode(0, true, right));
        verifyInplaceResult(1, MAX_CHILDREN, right.insertNode(0, false, left));

        left = node(1, MAX_CHILDREN);
        right = node(MAX_CHILDREN + 1, 2 * MAX_CHILDREN);
        verifySplitResult(1, MAX_CHILDREN + 1, 2 * MAX_CHILDREN, left.insertNode(0, true, right));
        verifySplitResult(1, MAX_CHILDREN + 1, 2 * MAX_CHILDREN, right.insertNode(0, false, left));

        left = node(1, MAX_CHILDREN);
        right = node(MAX_CHILDREN + 1, MAX_CHILDREN + 2);
        verifySplitResult(1, MIN_CHILDREN + 2, MAX_CHILDREN + 2, left.insertNode(0, true, right));
        verifySplitResult(1, MIN_CHILDREN + 2, MAX_CHILDREN + 2, right.insertNode(0, false, left));
    }

    public void testInsertNodeAtChild()
    {
        BtreeNode<Integer> left = node(1, MIN_CHILDREN);
        BtreeNode<Integer> right = node(MIN_CHILDREN + 1, MAX_CHILDREN);
        BtreeNode<Integer> leftParent = leftParent(-3, 0, left);
        BtreeNode<Integer> rightParent = rightParent(right, MAX_CHILDREN + 1, MAX_CHILDREN + 4);
        verifyInplaceResult(-3, MAX_CHILDREN, leftParent.insertNode(1, true, right));
        verifyInplaceResult(1, MAX_CHILDREN + 4, rightParent.insertNode(1, false, left));

        left = node(1, MAX_CHILDREN);
        right = node(MAX_CHILDREN + 1, 2 * MAX_CHILDREN);
        leftParent = leftParent(-MAX_CHILDREN + 2, 0, left);
        rightParent = rightParent(right, 2 * MAX_CHILDREN + 1, 3 * MAX_CHILDREN - 1);
        verifySplitResult(-MAX_CHILDREN + 2, MAX_CHILDREN + 1, 2 * MAX_CHILDREN, leftParent.insertNode(0, true, right));
        verifySplitResult(1, MAX_CHILDREN + 1, 3 * MAX_CHILDREN - 1, rightParent.insertNode(0, false, left));
    }

    private void verifySplitResult(int first,
                                   int middle,
                                   int last,
                                   BtreeInsertResult<Integer> result)
    {
        verifySplitResult(list(first, middle - 1),
                          list(middle, last),
                          result);
    }

    private void verifySplitResult(List<Integer> expected1,
                                   List<Integer> expected2,
                                   BtreeInsertResult<Integer> result)
    {
        assertEquals(BtreeInsertResult.Type.SPLIT, result.type);
        verifyNodeContents(expected1, result.newNode);
        verifyNodeContents(expected2, result.extraNode);
    }

    private void verifyInplaceResult(int first,
                                     int last,
                                     BtreeInsertResult<Integer> result)
    {
        verifyInplaceResult(makeList(forRange(first, last)), result);
    }

    private void verifyInplaceResult(List<Integer> expected,
                                     BtreeInsertResult<Integer> result)
    {
        assertEquals(BtreeInsertResult.Type.INPLACE, result.type);
        verifyNodeContents(expected, result.newNode);
        assertNull(result.extraNode);
    }

    private void verifyNodeContents(List<Integer> expected,
                                    BtreeNode<Integer> node)
    {
        assertEquals(expected, makeList(node.iterator()));
    }

    private List<Integer> list(int length)
    {
        return list(0, length - 1);
    }

    private List<Integer> list(int first,
                               int last)
    {
        return makeList(forRange(first, last));
    }

    private BtreeNode<Integer> node(int first,
                                    int last)
    {
        return forTesting(leaves(first, last));
    }

    private BtreeNode<Integer> leftParent(int first,
                                          int last,
                                          BtreeNode<Integer> child)
    {
        List<BtreeNode<Integer>> siblings = leaves(first, last);
        siblings.add(child);
        return forTesting(siblings);
    }

    private BtreeNode<Integer> rightParent(BtreeNode<Integer> child,
                                           int first,
                                           int last)
    {
        List<BtreeNode<Integer>> siblings = leaves(first, last);
        siblings.add(0, child);
        return forTesting(siblings);
    }

    @Nonnull
    private List<BtreeNode<Integer>> leaves(int first,
                                            int last)
    {
        List<BtreeNode<Integer>> leaves = new ArrayList<>();
        for (int value = first; value <= last; ++value) {
            leaves.add(new BtreeLeafNode<>(value));
        }
        return leaves;
    }

    private BtreeNode<Integer> filledNode()
    {
        BtreeNode<Integer> node = emptyNode();
        while (true) {
            final int nextValue = node.valueCount();
            BtreeInsertResult<Integer> result = node.append(nextValue);
            if (result.type == BtreeInsertResult.Type.SPLIT) {
                break;
            }
            node = result.newNode;
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    private BtreeBranchNode<Integer> emptyNode()
    {
        return forTesting(BtreeEmptyNode.<Integer>of());
    }
}
