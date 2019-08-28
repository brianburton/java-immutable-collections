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

package org.javimmutable.collections.hash.collision_map;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;

import java.util.Iterator;

public class MultiValueListNodeTest
    extends TestCase
{
    public void testKeyMatches()
    {
        SingleValueListNode<String, String> a = SingleValueListNode.of("a", "aa");
        SingleValueListNode<String, String> b = SingleValueListNode.of("b", "bb");
        SingleValueListNode<String, String> c = SingleValueListNode.of("c", "cc");
        ListNode<String, String> v = MultiValueListNode.of(a, b, c);
        assertEquals(3, size(v));

        assertEquals(true, v.findValueForKey("a").isFilled());
        assertEquals("aa", v.findValueForKey("a").getValue());
        assertSame(a, v.getEntryForKey("a"));

        assertEquals(true, v.findValueForKey("b").isFilled());
        assertEquals("bb", v.findValueForKey("b").getValue());
        assertSame(b, v.getEntryForKey("b"));

        assertEquals(true, v.findValueForKey("c").isFilled());
        assertEquals("cc", v.findValueForKey("c").getValue());
        assertSame(c, v.getEntryForKey("c"));

        MutableDelta sizeDelta = new MutableDelta();
        ListNode<String, String> nv = v.setValueForKey("a", "A", sizeDelta);
        assertEquals(true, nv instanceof MultiValueListNode);
        assertEquals(true, nv.getEntryForKey("a") instanceof SingleValueListNode);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(true, nv.findValueForKey("a").isFilled());
        assertEquals("A", nv.findValueForKey("a").getValue());
        assertEquals(true, nv.findValueForKey("b").isFilled());
        assertEquals("bb", nv.findValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.findValueForKey("c").isFilled());
        assertEquals("cc", nv.findValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(3, size(nv));

        // test value identity
        assertSame(nv, nv.setValueForKey("a", "A", new MutableDelta()));
        assertSame(nv, nv.setValueForKey("b", "bb", new MutableDelta()));
        assertSame(nv, nv.setValueForKey("c", "cc", new MutableDelta()));

        sizeDelta = new MutableDelta();
        nv = v.deleteValueForKey("a", sizeDelta);
        assertEquals(true, nv instanceof MultiValueListNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(null, nv.getEntryForKey("a"));
        assertEquals(false, nv.findValueForKey("a").isFilled());
        assertEquals(true, nv.findValueForKey("b").isFilled());
        assertEquals("bb", nv.findValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.findValueForKey("c").isFilled());
        assertEquals("cc", nv.findValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(2, size(nv));

        sizeDelta = new MutableDelta();
        nv = nv.deleteValueForKey("b", sizeDelta);
        assertEquals(true, nv instanceof SingleValueListNode);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(null, nv.getEntryForKey("a"));
        assertEquals(false, nv.findValueForKey("a").isFilled());
        assertEquals(null, nv.getEntryForKey("b"));
        assertEquals(false, nv.findValueForKey("b").isFilled());
        assertEquals(true, nv.findValueForKey("c").isFilled());
        assertEquals("cc", nv.findValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(1, size(nv));

        sizeDelta = new MutableDelta();
        nv = nv.deleteValueForKey("c", sizeDelta);
        assertEquals(null, nv);
        assertEquals(-1, sizeDelta.getValue());

        Iterator<JImmutableMap.Entry<String, String>> iter = v.iterator();
        assertEquals(true, iter.hasNext());
        JImmutableMap.Entry<String, String> e = iter.next();
        assertEquals("c", e.getKey());
        assertEquals("cc", e.getValue());

        assertEquals(true, iter.hasNext());
        e = iter.next();
        assertEquals("b", e.getKey());
        assertEquals("bb", e.getValue());

        assertEquals(true, iter.hasNext());
        e = iter.next();
        assertEquals("a", e.getKey());
        assertEquals("aa", e.getValue());

        assertEquals(false, iter.hasNext());
    }

    public void testKeyMismatches()
    {
        SingleValueListNode<String, String> a = SingleValueListNode.of("a", "aa");
        SingleValueListNode<String, String> b = SingleValueListNode.of("b", "bb");
        SingleValueListNode<String, String> c = SingleValueListNode.of("c", "cc");
        ListNode<String, String> v = MultiValueListNode.of(a, b, c);
        assertEquals(3, size(v));

        assertEquals(false, v.findValueForKey("d").isFilled());
        assertSame(null, v.getEntryForKey("d"));

        MutableDelta sizeDelta = new MutableDelta();
        ListNode<String, String> nv = v.setValueForKey("d", "dd", sizeDelta);
        assertEquals(true, nv instanceof MultiValueListNode);
        assertEquals(true, nv.getEntryForKey("d") instanceof SingleValueListNode);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(true, nv.findValueForKey("d").isFilled());
        assertEquals("dd", nv.findValueForKey("d").getValue());
        assertEquals(true, nv.findValueForKey("a").isFilled());
        assertEquals("aa", nv.findValueForKey("a").getValue());
        assertSame(a, nv.getEntryForKey("a"));
        assertEquals(true, nv.findValueForKey("b").isFilled());
        assertEquals("bb", nv.findValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.findValueForKey("c").isFilled());
        assertEquals("cc", nv.findValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(4, size(nv));

        Iterator<JImmutableMap.Entry<String, String>> iter = nv.iterator();
        assertEquals(true, iter.hasNext());
        JImmutableMap.Entry<String, String> e = iter.next();
        assertEquals("d", e.getKey());
        assertEquals("dd", e.getValue());

        assertEquals(true, iter.hasNext());
        e = iter.next();
        assertEquals("c", e.getKey());
        assertEquals("cc", e.getValue());

        assertEquals(true, iter.hasNext());
        e = iter.next();
        assertEquals("b", e.getKey());
        assertEquals("bb", e.getValue());

        assertEquals(true, iter.hasNext());
        e = iter.next();
        assertEquals("a", e.getKey());
        assertEquals("aa", e.getValue());

        assertEquals(false, iter.hasNext());

        sizeDelta = new MutableDelta();
        nv = v.deleteValueForKey("d", sizeDelta);
        assertSame(v, nv);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(true, nv.findValueForKey("a").isFilled());
        assertEquals("aa", nv.findValueForKey("a").getValue());
        assertSame(a, nv.getEntryForKey("a"));
        assertEquals(true, nv.findValueForKey("b").isFilled());
        assertEquals("bb", nv.findValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.findValueForKey("c").isFilled());
        assertEquals("cc", nv.findValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(3, size(nv));
    }

    private int size(ListNode node)
    {
        int answer = 0;
        for (Object o : node) {
            answer += 1;
        }
        return answer;
    }
}
