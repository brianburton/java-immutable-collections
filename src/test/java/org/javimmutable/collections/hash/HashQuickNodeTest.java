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

package org.javimmutable.collections.hash;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.common.MutableDelta;

public class HashQuickNodeTest
        extends TestCase
{
    public void testMatches()
    {
        HashQuickNode<String, String> node = new HashQuickNode<String, String>(0, 1, new HashTrieSingleValue<String, String>("k", "kk"));
        assertEquals(false, node.get(0, 1, "k").isEmpty());
        assertEquals("kk", node.get(0, 1, "k").getValue());
        assertEquals(new HashTrieSingleValue<String, String>("k", "kk"), node.getEntry(0, 1, "k"));
        assertEquals(new HashTrieSingleValue<String, String>("k", "kk"), node.getTrieValue(0, 1));

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.set(0, 1, "k", "K", sizeDelta);
        assertEquals(true, newNode instanceof HashQuickNode);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(Holders.of("K"), newNode.get(0, 1, "k"));

        sizeDelta = new MutableDelta();
        newNode = node.delete(0, 1, "k", sizeDelta);
        assertEquals(true, newNode instanceof HashEmptyNode);
        assertEquals(-1, sizeDelta.getValue());

        assertEquals(true, node.cursor().next().hasValue());
        assertEquals(new HashTrieSingleValue<String, String>("k", "kk"), node.cursor().next().getValue());
        assertEquals(false, node.cursor().next().next().hasValue());
    }

    public void testBranchIndexMismatches()
    {
        HashQuickNode<String, String> node = new HashQuickNode<String, String>(0, 1, new HashTrieSingleValue<String, String>("k", "kk"));
        assertEquals(true, node.get(1, 0, "k").isEmpty());
        assertEquals(null, node.getEntry(1, 0, "k"));
        assertEquals(null, node.getTrieValue(1, 0));

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.set(1, 0, "k", "K", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(Holders.of("kk"), newNode.get(0, 1, "k"));
        assertEquals(Holders.of("K"), newNode.get(1, 0, "k"));

        sizeDelta = new MutableDelta();
        newNode = node.delete(1, 0, "k", sizeDelta);
        assertSame(newNode, node);
        assertEquals(0, sizeDelta.getValue());
    }

    public void testValueIndexMismatches()
    {
        HashQuickNode<String, String> node = new HashQuickNode<String, String>(0, 1, new HashTrieSingleValue<String, String>("k", "kk"));
        assertEquals(true, node.get(0, 2, "k").isEmpty());
        assertEquals(null, node.getEntry(0, 2, "k"));
        assertEquals(null, node.getTrieValue(0, 2));

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.set(0, 2, "k", "K", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(Holders.of("kk"), newNode.get(0, 1, "k"));
        assertEquals(Holders.of("K"), newNode.get(0, 2, "k"));

        sizeDelta = new MutableDelta();
        newNode = node.delete(0, 2, "k", sizeDelta);
        assertSame(newNode, node);
        assertEquals(0, sizeDelta.getValue());
    }

    public void testKeyMismatches()
    {
        HashQuickNode<String, String> node = new HashQuickNode<String, String>(0, 1, new HashTrieSingleValue<String, String>("k", "kk"));
        assertEquals(true, node.get(0, 1, "x").isEmpty());
        assertEquals(null, node.getEntry(0, 1, "x"));
        assertEquals(new HashTrieSingleValue<String, String>("k", "kk"), node.getTrieValue(0, 1));

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.set(0, 1, "x", "X", sizeDelta);
        assertEquals(true, newNode instanceof HashQuickNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(Holders.of("kk"), newNode.get(0, 1, "k"));
        assertEquals(Holders.of("X"), newNode.get(0, 1, "x"));

        sizeDelta = new MutableDelta();
        newNode = node.delete(0, 1, "x", sizeDelta);
        assertSame(newNode, node);
        assertEquals(0, sizeDelta.getValue());
    }

    public void testValueIdentity()
    {
        HashQuickNode<String, String> node = new HashQuickNode<String, String>(0, 1, new HashTrieSingleValue<String, String>("k", "kk"));
        assertSame(node, node.set(0, 1, "k", "kk", null));
    }
}
