///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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
import org.javimmutable.collections.common.MutableDelta;

public class SingleHashValueListNodeTest
        extends TestCase
{
    public void testKeyMatches()
    {
        SingleHashValueListNode<String, String> v = SingleHashValueListNode.of("a", "aa");
        assertEquals(true, v.getValueForKey("a").isFilled());
        assertEquals("aa", v.getValueForKey("a").getValue());
        assertEquals("aa", v.getValueForKey("a").getValueOr("x"));
        assertEquals("aa", v.getValueForKey("a").getValueOrNull());

        assertEquals("a", v.getKey());
        assertEquals("aa", v.getValue());

        assertSame(v, v.getEntryForKey("a"));

        // test value identity
        assertSame(v, v.setValueForKey("a", "aa", null));

        MutableDelta sizeDelta = new MutableDelta();
        HashValueListNode<String, String> nv = v.setValueForKey("a", "A", sizeDelta);
        assertEquals(true, nv instanceof SingleHashValueListNode);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(true, nv.getValueForKey("a").isFilled());
        assertEquals("A", nv.getValueForKey("a").getValue());
        assertEquals("A", nv.getValueForKey("a").getValueOr("x"));
        assertEquals("A", nv.getValueForKey("a").getValueOrNull());

        sizeDelta = new MutableDelta();
        nv = v.deleteValueForKey("a", sizeDelta);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(null, nv);
    }

    public void testKeyMismatches()
    {
        SingleHashValueListNode<String, String> v = SingleHashValueListNode.of("b", "bb");
        assertEquals(false, v.getValueForKey("a").isFilled());
        assertEquals("x", v.getValueForKey("a").getValueOr("x"));
        assertEquals(null, v.getValueForKey("a").getValueOrNull());

        assertEquals(null, v.getEntryForKey("a"));

        MutableDelta sizeDelta = new MutableDelta();
        HashValueListNode<String, String> nv = v.setValueForKey("a", "A", sizeDelta);
        assertEquals(true, nv instanceof MultiHashValueListNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(true, nv.getValueForKey("a").isFilled());
        assertEquals("A", nv.getValueForKey("a").getValue());
        assertEquals("A", nv.getValueForKey("a").getValueOr("x"));
        assertEquals("A", nv.getValueForKey("a").getValueOrNull());
        assertEquals(true, nv.getValueForKey("b").isFilled());
        assertEquals("bb", nv.getValueForKey("b").getValue());
        assertEquals("bb", nv.getValueForKey("b").getValueOr("x"));
        assertEquals("bb", nv.getValueForKey("b").getValueOrNull());

        sizeDelta = new MutableDelta();
        nv = v.deleteValueForKey("a", sizeDelta);
        assertEquals(0, sizeDelta.getValue());
        assertSame(nv, v);
    }
}
