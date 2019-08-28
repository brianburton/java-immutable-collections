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

package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.List;

public class TreeCollisionMapTest
    extends TestCase
{
    public void testUpdateDelete()
    {
        TreeCollisionMap<Integer, Integer> transforms = TreeCollisionMap.empty();
        transforms = transforms.update(10, 100);
        assertEquals(1, transforms.size());
        assertEquals(Node.single(10, 100), transforms.root());

        transforms = transforms.update(10, 1000);
        assertEquals(1, transforms.size());
        assertEquals(Node.single(10, 1000), transforms.root());

        transforms = transforms.update(12, 60);
        assertEquals(2, transforms.size());
        assertEquals(branch(Tuple2.of(10, 1000), Tuple2.of(12, 60)), transforms.root());

        transforms = transforms.update(12, 90);
        assertEquals(2, transforms.size());
        assertEquals(branch(Tuple2.of(10, 1000), Tuple2.of(12, 90)), transforms.root());

        TreeCollisionMap<Integer, Integer> deleted = transforms.delete(87);
        assertEquals(2, deleted.size());
        assertEquals(branch(Tuple2.of(10, 1000), Tuple2.of(12, 90)), deleted.root());

        deleted = deleted.delete(10);
        assertEquals(1, deleted.size());
        assertEquals(Node.single(12, 90), deleted.root());

        deleted = deleted.delete(40);
        assertEquals(1, deleted.size());
        assertEquals(Node.single(12, 90), deleted.root());

        deleted = deleted.delete(12);
        assertEquals(0, deleted.size());
        assertSame(TreeCollisionMap.empty(), deleted);
    }

    public void testFindGet()
    {
        TreeCollisionMap<Integer, Integer> transforms = TreeCollisionMap.empty();
        transforms = transforms.update(10, 100);
        transforms = transforms.update(18, 180);
        transforms = transforms.update(12, 60);
        transforms = transforms.update(-6, -60);
        transforms = transforms.update(12, 90);
        assertEquals(4, transforms.size());

        assertEquals(Holders.of(100), transforms.findValue(10));
        assertEquals(Holders.of(90), transforms.findValue(12));
        assertEquals(Holders.of(180), transforms.findValue(18));
        assertEquals(Holders.of(-60), transforms.findValue(-6));
        assertEquals(Holders.<Integer>of(), transforms.findValue(11));

        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(10, 100)), transforms.findEntry(10));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(12, 90)), transforms.findEntry(12));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(18, 180)), transforms.findEntry(18));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(-6, -60)), transforms.findEntry(-6));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), transforms.findEntry(11));

        List<JImmutableMap.Entry<Integer, Integer>> expected = new ArrayList<>();
        expected.add(MapEntry.of(-6, -60));
        expected.add(MapEntry.of(10, 100));
        expected.add(MapEntry.of(12, 90));
        expected.add(MapEntry.of(18, 180));
        StandardIteratorTests.listIteratorTest(expected, transforms.iterator());
    }

    private Node<Integer, Integer> branch(Tuple2<Integer, Integer> a,
                                          Tuple2<Integer, Integer> b)
    {
        return Node.single(a.getFirst(), a.getSecond()).assign(ComparableComparator.of(), b.getFirst(), b.getSecond());
    }
}
