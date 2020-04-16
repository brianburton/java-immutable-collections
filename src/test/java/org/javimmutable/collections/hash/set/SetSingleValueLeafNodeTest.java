///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections.hash.set;

import junit.framework.TestCase;
import org.javimmutable.collections.list.ListCollisionSet;

public class SetSingleValueLeafNodeTest
    extends TestCase
{
    private final int HASH_CODE = 12;
    private final ListCollisionSet<String> collisionSet = ListCollisionSet.instance();
    private final SetSingleValueLeafNode<String> ten = new SetSingleValueLeafNode<>(HASH_CODE, "10");

    public void testOperations()
    {
        assertEquals(false, ten.contains(collisionSet, 0, "10"));
        assertEquals(false, ten.contains(collisionSet, 0, "20"));
        assertEquals(true, ten.contains(collisionSet, HASH_CODE, "10"));

        assertSame(ten, ten.insert(collisionSet, HASH_CODE, "10"));
        assertEquals("(2,0x3000,2,[],[(0x0,10),(0x0,20)])", ten.insert(collisionSet, HASH_CODE + 1, "20").toString());
        assertEquals("(0xc,[10,20])", ten.insert(collisionSet, HASH_CODE, "20").toString());

        assertSame(ten, ten.insert(collisionSet, HASH_CODE, "10"));

        assertSame(ten, ten.delete(collisionSet, 0, "10"));
        assertSame(ten, ten.delete(collisionSet, HASH_CODE, "20"));
        assertSame(SetEmptyNode.of(), ten.delete(collisionSet, HASH_CODE, "10"));

        assertEquals("(0x363,10)", ten.liftNode(99).toString());
    }
}
