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
import org.javimmutable.collections.JImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LeafNodeTest
        extends TestCase
{
    private final Comparator<Integer> comparator = ComparableComparator.of();
    private final LeafNode<Integer, Integer> node = new LeafNode<Integer, Integer>(10, 20);

    public void testVarious()
    {
        assertEquals(Integer.valueOf(10), node.getKey());
        assertEquals(Integer.valueOf(20), node.getValue());
        assertEquals(false, node.isEmpty());
        assertEquals(true, node.isFilled());
        assertEquals(Integer.valueOf(20), node.getValueOrNull());
        assertEquals(Integer.valueOf(20), node.getValueOr(12));
        assertEquals(Integer.valueOf(-99), node.getValueOr(comparator, 11, -99));
        assertEquals(Integer.valueOf(20), node.getValueOr(comparator, 10, -99));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 11));
        assertSame(node, node.find(comparator, 10));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 11));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(node), node.findEntry(comparator, 10));
        assertEquals(Integer.valueOf(10), node.getMaxKey());
        List<JImmutableMap.Entry<Integer, Integer>> values = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        node.addEntriesTo(values);
        //noinspection unchecked
        assertEquals(Arrays.<JImmutableMap.Entry<Integer, Integer>>asList(node), values);
        assertEquals(1, node.verifyDepthsMatch());
    }

    public void testUpdate()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.assignImpl(comparator, 10, 20));
        assertEquals(UpdateResult.createInPlace(new LeafNode<Integer, Integer>(10, 18), 0), node.assignImpl(comparator, 10, 18));
        assertEquals(UpdateResult.createSplit(new LeafNode<Integer, Integer>(8, 16), node, 1), node.assignImpl(comparator, 8, 16));
        assertEquals(UpdateResult.createSplit(node, new LeafNode<Integer, Integer>(12, 24), 1), node.assignImpl(comparator, 12, 24));
    }

    public void testDelete()
    {
        assertEquals(DeleteResult.<Integer, Integer>createEliminated(), node.deleteImpl(comparator, 10));
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.deleteImpl(comparator, 11));
    }

    public void testLeftDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8), node, 8, 10)),
                     node.leftDeleteMerge(comparator, new LeafNode<Integer, Integer>(8, 8)));
    }

    public void testRightDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new TwoNode<Integer, Integer>(node, new LeafNode<Integer, Integer>(12, 18), 10, 12)),
                     node.rightDeleteMerge(comparator, new LeafNode<Integer, Integer>(12, 18)));
    }
}
