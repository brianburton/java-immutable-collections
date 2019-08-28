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
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.list.EntryList;
import org.javimmutable.collections.list.ListCollisionMap;

public class HamtLeafNodeTest
    extends TestCase
{
    public void testDelete()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final MutableDelta size = new MutableDelta();
        final ListCollisionMap<Checked, Integer> transforms = new ListCollisionMap<>();
        EntryList<Checked, Integer> values = transforms.update(null, a, 100, size);
        values = transforms.update(values, b, 200, size);
        values = transforms.update(values, c, 300, size);
        assertEquals(3, size.getValue());

        HamtNode<EntryList<Checked, Integer>, Checked, Integer> node = new HamtLeafNode<>(1, values);
        assertSame(node, node.delete(transforms, 1, d, size));
        assertEquals(3, size.getValue());

        node = node.delete(transforms, 1, b, size);
        assertEquals(2, size.getValue());

        node = node.delete(transforms, 1, c, size);
        assertEquals(1, size.getValue());

        node = node.delete(transforms, 1, a, size);
        assertSame(HamtEmptyNode.of(), node);
    }
}
