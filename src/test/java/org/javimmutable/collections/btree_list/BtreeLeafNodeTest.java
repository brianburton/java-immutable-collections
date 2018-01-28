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

public class BtreeLeafNodeTest
        extends TestCase
{
    public void testAppend()
    {
        BtreeNode<Integer> node = new BtreeLeafNode<Integer>(1);
        assertEquals(1, node.childCount());
        assertEquals(node.childCount(), node.valueCount());
        assertEquals(Integer.valueOf(1), node.get(0));
        for (int i = 2; i <= BtreeNode.MAX_CHILDREN; ++i) {
            BtreeInsertResult<Integer> result = node.append(i);
            assertEquals(BtreeInsertResult.Type.INPLACE, result.type);
            node = result.newNode;
            assertEquals(i, node.childCount());
            assertEquals(node.childCount(), node.valueCount());
            assertEquals(Integer.valueOf(i), node.get(i - 1));
        }
        BtreeInsertResult<Integer> result = node.append(BtreeNode.MAX_CHILDREN + 1);
        assertEquals(BtreeInsertResult.Type.SPLIT, result.type);
        assertEquals(BtreeNode.MIN_CHILDREN + 1, result.newNode.childCount());
        for (int i = 1; i <= BtreeNode.MIN_CHILDREN + 1; ++i) {
            assertEquals(Integer.valueOf(i), result.newNode.get(i - 1));
        }
        assertEquals(BtreeNode.MIN_CHILDREN, result.extraNode.childCount());
        for (int i = 1; i <= BtreeNode.MIN_CHILDREN; ++i) {
            assertEquals(Integer.valueOf(BtreeNode.MIN_CHILDREN + i + 1), result.extraNode.get(i - 1));
        }
    }

    public void testAssign()
    {
        BtreeNode<Integer> node = new BtreeLeafNode<Integer>(1);
        for (int i = 2; i <= BtreeNode.MAX_CHILDREN; ++i) {
            try {
                node.assign(i - 1, 1000);
                fail();
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        for (int i = 2; i <= BtreeNode.MAX_CHILDREN; ++i) {
            BtreeInsertResult<Integer> result = node.append(i);
            assertEquals(BtreeInsertResult.Type.INPLACE, result.type);
            node = result.newNode;
            assertEquals(i, node.childCount());
            assertEquals(node.childCount(), node.valueCount());
            assertEquals(Integer.valueOf(i), node.get(i - 1));
        }

        final BtreeNode<Integer> preAssignNode = node;
        for (int i = 0; i < BtreeNode.MAX_CHILDREN; ++i) {
            node = node.assign(i, BtreeNode.MAX_CHILDREN - i);
        }

        for (int i = 0; i < BtreeNode.MAX_CHILDREN; ++i) {
            assertEquals(Integer.valueOf(i + 1), preAssignNode.get(i));
            assertEquals(Integer.valueOf(BtreeNode.MAX_CHILDREN - i), node.get(i));
        }
        assertEquals(BtreeNode.MAX_CHILDREN, node.childCount());
        assertEquals(BtreeNode.MAX_CHILDREN, node.valueCount());
    }

    public void testInsertFirst()
    {
        BtreeNode<Integer> node = new BtreeLeafNode<Integer>(BtreeNode.MAX_CHILDREN);
        for (int i = BtreeNode.MAX_CHILDREN - 1; i >= 1; --i) {
            final BtreeInsertResult<Integer> result = node.insertAt(0, i);
            assertEquals(BtreeInsertResult.Type.INPLACE, result.type);
            node = result.newNode;
            assertEquals((BtreeNode.MAX_CHILDREN - i) + 1, node.childCount());
            assertEquals(node.childCount(), node.valueCount());
            assertEquals(Integer.valueOf(i), node.get(0));
        }
        final BtreeInsertResult<Integer> result = node.insertAt(0, 100);
        assertEquals(BtreeInsertResult.Type.SPLIT, result.type);
        assertEquals(BtreeNode.MIN_CHILDREN, result.newNode.childCount());
        assertEquals(Integer.valueOf(100), result.newNode.get(0));
        assertEquals(BtreeNode.MIN_CHILDREN + 1, result.extraNode.childCount());
    }
}
