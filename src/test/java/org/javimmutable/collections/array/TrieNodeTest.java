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

package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.common.MutableDelta;

public class TrieNodeTest
    extends TestCase
{
    public void testVarious()
    {
        TrieNode<Integer> root = EmptyTrieNode.instance();
        assertEquals((Integer)1, root.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));

        MutableDelta delta = new MutableDelta();
        TrieNode<Integer> node = root.assign(TrieNode.ROOT_SHIFT, 87, 88, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals(1, delta.getValue());

        delta = new MutableDelta();
        assertSame(node, node.assign(TrieNode.ROOT_SHIFT, 87, 88, delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        node = node.assign(TrieNode.ROOT_SHIFT, -45, 46, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)46, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(1, delta.getValue());

        delta = new MutableDelta();
        node = node.assign(TrieNode.ROOT_SHIFT, -45, 47, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)47, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        node = node.delete(TrieNode.ROOT_SHIFT, -300, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)47, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        node = node.delete(TrieNode.ROOT_SHIFT, -45, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)1, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        node = node.assign(TrieNode.ROOT_SHIFT, -45, 47, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)47, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(1, delta.getValue());

        for (int i = 30; i < 32; ++i) {
            assertEquals(30, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 25; i < 30; ++i) {
            assertEquals(25, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 20; i < 25; ++i) {
            assertEquals(20, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 15; i < 20; ++i) {
            assertEquals(15, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 10; i < 15; ++i) {
            assertEquals(10, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 5; i < 10; ++i) {
            assertEquals(5, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 0; i < 5; ++i) {
            assertEquals(0, TrieNode.shiftForIndex(1 << i));
        }
    }
}
