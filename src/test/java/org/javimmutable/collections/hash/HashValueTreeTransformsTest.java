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

package org.javimmutable.collections.hash;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.tree.BranchNode;
import org.javimmutable.collections.tree.LeafNode;
import org.javimmutable.collections.tree.Node;

import java.util.ArrayList;
import java.util.List;

public class HashValueTreeTransformsTest
        extends TestCase
{
    public void testUpdateDelete()
    {
        HashValueTreeTransforms<Integer, Integer> transforms = new HashValueTreeTransforms<>();
        MutableDelta delta = new MutableDelta();
        Node<Integer, Integer> value = transforms.update(Holders.of(), 10, 100, delta);
        assertEquals(1, delta.getValue());
        assertEquals(new LeafNode<>(10, 100), value);

        delta = new MutableDelta();
        value = transforms.update(Holders.of(value), 10, 1000, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new LeafNode<>(10, 1000), value);

        delta = new MutableDelta();
        value = transforms.update(Holders.of(value), 12, 60, delta);
        assertEquals(1, delta.getValue());
        assertEquals(new BranchNode<>(new LeafNode<>(10, 1000), new LeafNode<>(12, 60)), value);

        delta = new MutableDelta();
        value = transforms.update(Holders.of(value), 12, 90, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new BranchNode<>(new LeafNode<>(10, 1000), new LeafNode<>(12, 90)), value);

        delta = new MutableDelta();
        Holder<Node<Integer, Integer>> deleted = transforms.delete(value, 87, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new BranchNode<>(new LeafNode<>(10, 1000), new LeafNode<>(12, 90)), deleted.getValue());

        delta = new MutableDelta();
        deleted = transforms.delete(deleted.getValue(), 10, delta);
        assertEquals(-1, delta.getValue());
        assertEquals(new LeafNode<>(12, 90), deleted.getValue());

        delta = new MutableDelta();
        deleted = transforms.delete(deleted.getValue(), 40, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new LeafNode<>(12, 90), deleted.getValue());

        delta = new MutableDelta();
        deleted = transforms.delete(deleted.getValue(), 12, delta);
        assertEquals(-1, delta.getValue());
        assertEquals(true, deleted.isEmpty());
    }

    public void testFindGet()
    {
        HashValueTreeTransforms<Integer, Integer> transforms = new HashValueTreeTransforms<>();
        MutableDelta delta = new MutableDelta();
        Node<Integer, Integer> value = transforms.update(Holders.of(), 10, 100, delta);
        value = transforms.update(Holders.of(value), 18, 180, delta);
        value = transforms.update(Holders.of(value), 12, 60, delta);
        value = transforms.update(Holders.of(value), -6, -60, delta);
        value = transforms.update(Holders.of(value), 12, 90, delta);
        assertEquals(4, delta.getValue());

        assertEquals(Holders.of(100), transforms.findValue(value, 10));
        assertEquals(Holders.of(90), transforms.findValue(value, 12));
        assertEquals(Holders.of(180), transforms.findValue(value, 18));
        assertEquals(Holders.of(-60), transforms.findValue(value, -6));
        assertEquals(Holders.<Integer>of(), transforms.findValue(value, 11));

        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(10, 100)), transforms.findEntry(value, 10));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(12, 90)), transforms.findEntry(value, 12));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(18, 180)), transforms.findEntry(value, 18));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(-6, -60)), transforms.findEntry(value, -6));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), transforms.findEntry(value, 11));

        List<JImmutableMap.Entry<Integer, Integer>> expected = new ArrayList<>();
        expected.add(MapEntry.of(-6, -60));
        expected.add(MapEntry.of(10, 100));
        expected.add(MapEntry.of(12, 90));
        expected.add(MapEntry.of(18, 180));
        StandardCursorTest.listCursorTest(expected, transforms.cursor(value));
    }
}
