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

package org.javimmutable.collections.array.trie;

import org.javimmutable.collections.Holders;
import junit.framework.TestCase;

public class QuickTrieNodeTest
        extends TestCase
{
    public void test()
    {
        QuickTrieNode<Integer> node = new QuickTrieNode<Integer>(100, 5, 100);
        assertEquals(1, node.deepSize());
        assertEquals(1, node.shallowSize());
        assertEquals(Holders.of(100), node.get(100, 5));
        assertEquals(Holders.<Integer>of(), node.get(101, 5));
        assertEquals(Holders.<Integer>of(), node.get(100, 6));

        TrieNode<Integer> newNode = node.set(100, 5, 101);
        assertEquals(QuickTrieNode.class, newNode.getClass());
        assertEquals(Holders.of(101), newNode.get(100, 5));
        assertEquals(Holders.<Integer>of(), newNode.get(101, 5));
        assertEquals(Holders.<Integer>of(), newNode.get(100, 6));

        newNode = node.set(101, 5, 101);
        assertEquals(StandardTrieNode.class, newNode.getClass());
        assertEquals(2, newNode.deepSize());
        assertEquals(2, newNode.shallowSize());
        assertEquals(Holders.of(100), newNode.get(100, 5));
        assertEquals(Holders.of(101), newNode.get(101, 5));
        assertEquals(Holders.<Integer>of(), newNode.get(100, 6));

        newNode = node.set(100, 6, 106);
        assertEquals(StandardTrieNode.class, newNode.getClass());
        assertEquals(2, newNode.deepSize());
        assertEquals(1, newNode.shallowSize());
        assertEquals(Holders.of(100), newNode.get(100, 5));
        assertEquals(Holders.of(106), newNode.get(100, 6));
        assertEquals(Holders.<Integer>of(), newNode.get(101, 5));

        assertSame(node, node.delete(101, 5));
        assertSame(node, node.delete(100, 6));
        assertTrue(node.delete(100, 5) instanceof EmptyTrieNode);

        assertEquals(true, node.cursor().next().hasValue());
        assertEquals(100, (int)node.cursor().next().getValue());
        assertEquals(false, node.cursor().next().next().hasValue());
    }
}
