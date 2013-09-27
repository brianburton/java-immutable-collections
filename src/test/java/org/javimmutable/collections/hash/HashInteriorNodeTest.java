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

import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.common.MutableDelta;
import junit.framework.TestCase;

public class HashInteriorNodeTest
        extends TestCase
{
    public void testBranchGet()
    {
        Bit32Array<HashTrieNode<String, String>> branches = Bit32Array.of();
        branches = branches.set(1, new HashQuickNode<String, String>(2, 3, new HashTrieSingleValue<String, String>("a", "aa")));
        branches = branches.set(2, new HashQuickNode<String, String>(3, 4, new HashTrieSingleValue<String, String>("b", "bb")));

        HashInteriorNode<String, String> node = new HashInteriorNode<String, String>(branches, Bit32Array.<HashTrieValue<String, String>>of());
        assertEquals("aa", node.get((2 << 5) + 1, 3, "a").getValueOrNull());
        assertEquals("bb", node.get((3 << 5) + 2, 4, "b").getValueOrNull());
        assertEquals(null, node.get((3 << 5) + 3, 4, "b").getValueOrNull());

        assertEquals(new HashTrieSingleValue<String, String>("a", "aa"), node.getEntry((2 << 5) + 1, 3, "a"));
        assertEquals(new HashTrieSingleValue<String, String>("b", "bb"), node.getEntry((3 << 5) + 2, 4, "b"));
        assertEquals(null, node.getEntry((3 << 5) + 3, 4, "b"));

        assertEquals(new HashTrieSingleValue<String, String>("a", "aa"), node.getTrieValue((2 << 5) + 1, 3));
        assertEquals(new HashTrieSingleValue<String, String>("b", "bb"), node.getTrieValue((3 << 5) + 2, 4));
        assertEquals(null, node.getTrieValue((3 << 5) + 3, 4));
    }

    public void testValueGet()
    {
        Bit32Array<HashTrieValue<String, String>> values = Bit32Array.of();
        values = values.set(1, new HashTrieSingleValue<String, String>("a", "aa"));
        values = values.set(2, new HashTrieSingleValue<String, String>("b", "bb"));

        HashInteriorNode<String, String> node = new HashInteriorNode<String, String>(Bit32Array.<HashTrieNode<String, String>>of(), values);
        assertEquals("aa", node.get(0, 1, "a").getValueOrNull());
        assertEquals("bb", node.get(0, 2, "b").getValueOrNull());
        assertEquals(null, node.get(0, 3, "b").getValueOrNull());

        assertEquals(new HashTrieSingleValue<String, String>("a", "aa"), node.getEntry(0, 1, "a"));
        assertEquals(new HashTrieSingleValue<String, String>("b", "bb"), node.getEntry(0, 2, "b"));
        assertEquals(null, node.getEntry(0, 31, "b"));

        assertEquals(new HashTrieSingleValue<String, String>("a", "aa"), node.getTrieValue(0, 1));
        assertEquals(new HashTrieSingleValue<String, String>("b", "bb"), node.getTrieValue(0, 2));
        assertEquals(null, node.getTrieValue(0, 16));
    }

    public void testBranchSet()
    {
        HashInteriorNode<String, String> node = new HashInteriorNode<String, String>();

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.set(65, 9, "a", "aa", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals("aa", newNode.get(65, 9, "a").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.set(65, 9, "a", "A", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals("A", newNode.get(65, 9, "a").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.set(77, 9, "c", "cc", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(2, newNode.deepSize());
        assertEquals("A", newNode.get(65, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(77, 9, "c").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.set(98, 31, "x", "xx", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(3, newNode.deepSize());
        assertEquals("A", newNode.get(65, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(77, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(98, 31, "x").getValueOrNull());

        PersistentMap<Class, Integer> counts = newNode.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(3, (int)counts.find(HashQuickNode.class).getValueOr(0));

        sizeDelta = new MutableDelta();
        newNode = newNode.set(98, 30, "z", "zz", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(4, newNode.deepSize());
        assertEquals("A", newNode.get(65, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(77, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(98, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(98, 30, "z").getValueOrNull());

        counts = newNode.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(3, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(HashQuickNode.class).getValueOr(0));
    }

    public void testValueSet()
    {
        HashInteriorNode<String, String> node = new HashInteriorNode<String, String>();

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.set(0, 9, "a", "aa", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals("aa", newNode.get(0, 9, "a").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.set(0, 9, "a", "A", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals("A", newNode.get(0, 9, "a").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.set(0, 9, "c", "cc", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(2, newNode.deepSize());
        assertEquals("A", newNode.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(0, 9, "c").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.set(0, 31, "x", "xx", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(3, newNode.deepSize());
        assertEquals("A", newNode.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(0, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(0, 31, "x").getValueOrNull());
    }

    public void testBranchDelete()
    {
        HashTrieNode<String, String> node = new HashInteriorNode<String, String>();

        MutableDelta sizeDelta = new MutableDelta();
        node = node.set(5, 9, "a", "A", sizeDelta);
        node = node.set(10, 9, "c", "cc", sizeDelta);
        node = node.set(15, 31, "x", "xx", sizeDelta);
        node = node.set(15, 30, "z", "zz", sizeDelta);
        assertEquals(true, node instanceof HashInteriorNode);
        assertEquals(4, node.deepSize());
        assertEquals("A", node.get(5, 9, "a").getValueOrNull());
        assertEquals("cc", node.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", node.get(15, 31, "x").getValueOrNull());
        assertEquals("zz", node.get(15, 30, "z").getValueOrNull());

        PersistentMap<Class, Integer> counts = node.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(HashQuickNode.class).getValueOr(0));

        // delete from non-existent branch
        sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.delete(3, 9, "a", sizeDelta);
        assertSame(node, newNode);
        assertEquals(0, sizeDelta.getValue());

        // delete from non-existent subbranch
        sizeDelta = new MutableDelta();
        newNode = node.delete(15, 9, "a", sizeDelta);
        assertSame(node, newNode);
        assertEquals(0, sizeDelta.getValue());

        // delete with mismatched key
        sizeDelta = new MutableDelta();
        newNode = node.delete(10, 9, "q", sizeDelta);
        assertSame(node, newNode);
        assertEquals(0, sizeDelta.getValue());

        // delete subbranch
        sizeDelta = new MutableDelta();
        newNode = node.delete(5, 9, "a", sizeDelta);
        assertTrue(newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(3, newNode.deepSize());
        assertEquals(null, newNode.get(5, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(15, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(15, 30, "z").getValueOrNull());
        counts = newNode.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(HashQuickNode.class).getValueOr(0));

        // delete subbranch
        sizeDelta = new MutableDelta();
        newNode = newNode.delete(10, 9, "c", sizeDelta);
        assertTrue(newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(2, newNode.deepSize());
        assertEquals(null, newNode.get(5, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(15, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(15, 30, "z").getValueOrNull());
        counts = newNode.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(0, (int)counts.find(HashQuickNode.class).getValueOr(0));

        // delete from subbranch
        sizeDelta = new MutableDelta();
        newNode = newNode.delete(15, 31, "x", sizeDelta);
        assertTrue(newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals(null, newNode.get(5, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(15, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(15, 30, "z").getValueOrNull());
        counts = newNode.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(HashQuickNode.class).getValueOr(0));

        // delete last subbranch
        sizeDelta = new MutableDelta();
        newNode = newNode.delete(15, 30, "z", sizeDelta);
        assertTrue(newNode instanceof HashEmptyNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(0, newNode.deepSize());
        assertEquals(null, newNode.get(5, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(15, 31, "x").getValueOrNull());
        assertEquals(null, newNode.get(15, 30, "z").getValueOrNull());
        counts = newNode.getNodeTypeCounts(PersistentHashMap.<Class, Integer>of());
        assertEquals(1, (int)counts.find(HashEmptyNode.class).getValueOr(0));
        assertEquals(0, (int)counts.find(HashInteriorNode.class).getValueOr(0));
        assertEquals(0, (int)counts.find(HashQuickNode.class).getValueOr(0));
    }

    public void testValuesDelete()
    {
        HashTrieNode<String, String> node = new HashInteriorNode<String, String>();

        MutableDelta sizeDelta = new MutableDelta();
        node = node.set(0, 9, "a", "A", sizeDelta);
        node = node.set(0, 9, "c", "cc", sizeDelta);
        node = node.set(0, 31, "x", "xx", sizeDelta);
        assertEquals(true, node instanceof HashInteriorNode);
        assertEquals(3, node.deepSize());
        assertEquals("A", node.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", node.get(0, 9, "c").getValueOrNull());
        assertEquals("xx", node.get(0, 31, "x").getValueOrNull());

        // delete non-existent value index
        sizeDelta = new MutableDelta();
        assertSame(node, node.delete(0, 1, "a", sizeDelta));
        assertEquals(0, sizeDelta.getValue());

        // delete incorrect key for index
        sizeDelta = new MutableDelta();
        assertSame(node, node.delete(0, 9, "q", sizeDelta));
        assertEquals(0, sizeDelta.getValue());

        // delete one value from multinode
        sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.delete(0, 9, "a", sizeDelta);
        assertTrue(newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(2, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(0, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(0, 31, "x").getValueOrNull());

        // delete penultimate value morphs into quick node
        sizeDelta = new MutableDelta();
        newNode = newNode.delete(0, 31, "x", sizeDelta);
        assertTrue(newNode instanceof HashQuickNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(0, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(0, 31, "x").getValueOrNull());

        // delete last node
        node = new HashInteriorNode<String, String>().set(0, 9, "a", "A", sizeDelta);
        sizeDelta = new MutableDelta();
        newNode = node.delete(0, 9, "a", sizeDelta);
        assertTrue(newNode instanceof HashEmptyNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(0, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(0, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(0, 31, "x").getValueOrNull());
    }

    public void testBothDelete()
    {
        HashTrieNode<String, String> node = new HashInteriorNode<String, String>();

        MutableDelta sizeDelta = new MutableDelta();
        node = node.set(0, 9, "a", "A", sizeDelta);
        node = node.set(0, 31, "x", "xx", sizeDelta);
        node = node.set(10, 9, "c", "cc", sizeDelta);
        node = node.set(15, 31, "z", "zz", sizeDelta);
        assertEquals(true, node instanceof HashInteriorNode);
        assertEquals(4, node.deepSize());
        assertEquals("A", node.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", node.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", node.get(0, 31, "x").getValueOrNull());
        assertEquals("zz", node.get(15, 31, "z").getValueOrNull());

        sizeDelta = new MutableDelta();
        HashTrieNode<String, String> newNode = node.delete(0, 9, "a", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(3, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals("cc", newNode.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(0, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(15, 31, "z").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.delete(10, 9, "c", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(2, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(0, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(15, 31, "z").getValueOrNull());

        node = newNode; // save for below

        // deleting value first then branch
        sizeDelta = new MutableDelta();
        newNode = newNode.delete(0, 31, "x", sizeDelta);
        assertEquals(true, newNode instanceof HashInteriorNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(0, 31, "x").getValueOrNull());
        assertEquals("zz", newNode.get(15, 31, "z").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.delete(15, 31, "z", sizeDelta);
        assertEquals(true, newNode instanceof HashEmptyNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(0, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(0, 31, "x").getValueOrNull());
        assertEquals(null, newNode.get(15, 31, "z").getValueOrNull());

        // deleting branch first then value
        sizeDelta = new MutableDelta();
        newNode = node.delete(15, 31, "z", sizeDelta);
        assertEquals(true, newNode instanceof HashQuickNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(1, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals("xx", newNode.get(0, 31, "x").getValueOrNull());
        assertEquals(null, newNode.get(15, 31, "z").getValueOrNull());

        sizeDelta = new MutableDelta();
        newNode = newNode.delete(0, 31, "x", sizeDelta);
        assertEquals(true, newNode instanceof HashEmptyNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(0, newNode.deepSize());
        assertEquals(null, newNode.get(0, 9, "a").getValueOrNull());
        assertEquals(null, newNode.get(10, 9, "c").getValueOrNull());
        assertEquals(null, newNode.get(0, 31, "x").getValueOrNull());
        assertEquals(null, newNode.get(15, 31, "z").getValueOrNull());
    }
}
