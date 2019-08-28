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

package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.list.ListCollisionMap;

import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.MapEntry.mapEntry;

public class HamtLeafNodeTest
    extends TestCase
{
    public void testDelete()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final ListCollisionMap<Checked, Integer> empty = ListCollisionMap.empty();

        ListCollisionMap<Checked, Integer> transforms = empty;
        transforms = transforms.update(a, 100);
        transforms = transforms.update(b, 200);
        transforms = transforms.update(c, 300);
        assertEquals(3, transforms.size());

        HamtNode<Checked, Integer> node = new HamtLeafNode<>(1, transforms);
        assertEquals(3, node.size());

        assertSame(node, node.delete(empty, 1, d));
        assertEquals(3, node.size());

        node = node.delete(empty, 1, b);
        assertEquals(2, node.size());

        node = node.delete(empty, 1, c);
        assertEquals(1, node.size());

        node = node.delete(empty, 1, a);
        assertSame(HamtEmptyNode.of(), node);
    }

    public void testIterator()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final ListCollisionMap<Checked, Integer> empty = ListCollisionMap.empty();

        ListCollisionMap<Checked, Integer> node = empty
            .update(a, 100)
            .update(b, 200)
            .update(c, 300)
            .update(d, 400);

        List<JImmutableMap.Entry<Checked, Integer>> expected = new ArrayList<>();
        expected.add(mapEntry(a, 100));
        expected.add(mapEntry(b, 200));
        expected.add(mapEntry(c, 300));
        expected.add(mapEntry(d, 400));
        StandardIteratorTests.verifyOrderedIterable(expected, node);
    }
}
