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

package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.list.ListCollisionMap;

public class HamtSingleKeyLeafNodeTest
    extends TestCase
{
    private final int HASH_CODE = 12;
    private final ListCollisionMap<String, String> collisionMap = ListCollisionMap.instance();
    private final HamtSingleKeyLeafNode<String, String> ten = new HamtSingleKeyLeafNode<>(HASH_CODE, "10", "ten");

    public void testOperations()
    {
        assertEquals(Holders.of(), ten.find(collisionMap, 0, "10"));
        assertEquals(Holders.of(), ten.find(collisionMap, 0, "20"));
        assertEquals(Holders.of("ten"), ten.find(collisionMap, HASH_CODE, "10"));

        assertEquals("nope", ten.getValueOr(collisionMap, 0, "10", "nope"));
        assertEquals("nope", ten.getValueOr(collisionMap, 0, "20", "nope"));
        assertEquals("ten", ten.getValueOr(collisionMap, HASH_CODE, "10", "nope"));

        assertSame(ten, ten.assign(collisionMap, HASH_CODE, "10", "ten"));
        assertEquals("(0xc,10,ten-change)", ten.assign(collisionMap, HASH_CODE, "10", "ten-change").toString());
        assertEquals("(2,0x3000,2,[],[(0x0,10,ten),(0x0,20,twenty)])", ten.assign(collisionMap, HASH_CODE + 1, "20", "twenty").toString());
        assertEquals("(0xc,[10=ten,20=twenty])", ten.assign(collisionMap, HASH_CODE, "20", "twenty").toString());

        assertSame(ten, ten.update(collisionMap, HASH_CODE, "10", h -> h.getValueOr("new")));
        assertEquals("(0xc,10,ten-change)", ten.update(collisionMap, HASH_CODE, "10", h -> h.isFilled() ? h.getValue() + "-change" : "new").toString());
        assertEquals("(2,0x3000,2,[],[(0x0,10,ten),(0x0,20,twenty)])", ten.update(collisionMap, HASH_CODE + 1, "20", h -> h.getValueOr("twenty")).toString());
        assertEquals("(0xc,[10=ten,20=twenty])", ten.update(collisionMap, HASH_CODE, "20", h -> "twenty").toString());

        assertSame(ten, ten.delete(collisionMap, 0, "10"));
        assertSame(ten, ten.delete(collisionMap, HASH_CODE, "20"));
        assertSame(HamtEmptyNode.of(), ten.delete(collisionMap, HASH_CODE, "10"));

        assertEquals("(0x1e3,10,ten)", ten.liftNode(99).toString());
    }
}
