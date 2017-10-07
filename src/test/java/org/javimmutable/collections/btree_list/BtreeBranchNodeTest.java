///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import java.util.ArrayList;
import java.util.List;

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
        assertEquals(BtreeNode.MAX_CHILDREN, node.childCount());
        assertEquals(((BtreeNode.MAX_CHILDREN - 1) * (BtreeNode.MIN_CHILDREN + 1)) + BtreeNode.MAX_CHILDREN, node.valueCount());
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
        assertEquals(BtreeNode.MAX_CHILDREN, node.childCount());
        assertEquals(((BtreeNode.MAX_CHILDREN - 1) * (BtreeNode.MIN_CHILDREN + 1)) + BtreeNode.MAX_CHILDREN, node.valueCount());
        StandardCursorTest.listCursorTest(expected, node.cursor());
    }

    public void testDeleteAt()
    {
        final BtreeNode<Integer> fullNode = filledNode();
        final List<Integer> fullExpected = createExpected(fullNode.valueCount());
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
        List<Integer> expected = createExpected(node.valueCount());
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

    private void verifyNodeContents(List<Integer> expected,
                                    BtreeNode<Integer> node)
    {
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), node.get(i));
        }
    }

    private List<Integer> createExpected(int length)
    {
        List<Integer> answer = new ArrayList<Integer>(length);
        for (int i = 0; i < length; ++i) {
            answer.add(i);
        }
        return answer;
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
        return BtreeBranchNode.forTesting(BtreeEmptyNode.<Integer>of());
    }
}
