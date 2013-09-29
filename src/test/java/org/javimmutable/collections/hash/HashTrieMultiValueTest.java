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
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.list.JImmutableLinkedStack;

public class HashTrieMultiValueTest
        extends TestCase
{
    public void testKeyMatches()
    {
        HashTrieSingleValue<String, String> a = new HashTrieSingleValue<String, String>("a", "aa");
        HashTrieSingleValue<String, String> b = new HashTrieSingleValue<String, String>("b", "bb");
        HashTrieSingleValue<String, String> c = new HashTrieSingleValue<String, String>("c", "cc");
        HashTrieMultiValue<String, String> v = new HashTrieMultiValue<String, String>(JImmutableLinkedStack.<HashTrieSingleValue<String, String>>of(a).insert(b).insert(c));
        assertEquals(3, v.size());

        assertEquals(true, v.getValueForKey("a").isFilled());
        assertEquals("aa", v.getValueForKey("a").getValue());
        assertSame(a, v.getEntryForKey("a"));

        assertEquals(true, v.getValueForKey("b").isFilled());
        assertEquals("bb", v.getValueForKey("b").getValue());
        assertSame(b, v.getEntryForKey("b"));

        assertEquals(true, v.getValueForKey("c").isFilled());
        assertEquals("cc", v.getValueForKey("c").getValue());
        assertSame(c, v.getEntryForKey("c"));

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieValue<String, String> nv = v.setValueForKey("a", "A", sizeDelta);
        assertEquals(true, nv instanceof HashTrieMultiValue);
        assertEquals(true, nv.getEntryForKey("a") instanceof HashTrieSingleValue);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(true, nv.getValueForKey("a").isFilled());
        assertEquals("A", nv.getValueForKey("a").getValue());
        assertEquals(true, nv.getValueForKey("b").isFilled());
        assertEquals("bb", nv.getValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.getValueForKey("c").isFilled());
        assertEquals("cc", nv.getValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(3, nv.size());

        // test value identity
        assertSame(nv, nv.setValueForKey("a", "A", null));
        assertSame(nv, nv.setValueForKey("b", "bb", null));
        assertSame(nv, nv.setValueForKey("c", "cc", null));

        sizeDelta = new MutableDelta();
        nv = v.deleteValueForKey("a", sizeDelta);
        assertEquals(true, nv instanceof HashTrieMultiValue);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(null, nv.getEntryForKey("a"));
        assertEquals(false, nv.getValueForKey("a").isFilled());
        assertEquals(true, nv.getValueForKey("b").isFilled());
        assertEquals("bb", nv.getValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.getValueForKey("c").isFilled());
        assertEquals("cc", nv.getValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(2, nv.size());

        sizeDelta = new MutableDelta();
        nv = nv.deleteValueForKey("b", sizeDelta);
        assertEquals(true, nv instanceof HashTrieSingleValue);
        assertEquals(-1, sizeDelta.getValue());
        assertEquals(null, nv.getEntryForKey("a"));
        assertEquals(false, nv.getValueForKey("a").isFilled());
        assertEquals(null, nv.getEntryForKey("b"));
        assertEquals(false, nv.getValueForKey("b").isFilled());
        assertEquals(true, nv.getValueForKey("c").isFilled());
        assertEquals("cc", nv.getValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(1, nv.size());

        sizeDelta = new MutableDelta();
        nv = nv.deleteValueForKey("c", sizeDelta);
        assertEquals(null, nv);
        assertEquals(-1, sizeDelta.getValue());

        Cursor<JImmutableMap.Entry<String, String>> cursor = v.cursor().next();
        assertEquals(true, cursor.hasValue());
        assertEquals("c", cursor.getValue().getKey());
        assertEquals("cc", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("b", cursor.getValue().getKey());
        assertEquals("bb", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("a", cursor.getValue().getKey());
        assertEquals("aa", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
    }

    public void testKeyMismatches()
    {
        HashTrieSingleValue<String, String> a = new HashTrieSingleValue<String, String>("a", "aa");
        HashTrieSingleValue<String, String> b = new HashTrieSingleValue<String, String>("b", "bb");
        HashTrieSingleValue<String, String> c = new HashTrieSingleValue<String, String>("c", "cc");
        HashTrieMultiValue<String, String> v = new HashTrieMultiValue<String, String>(JImmutableLinkedStack.<HashTrieSingleValue<String, String>>of(a).insert(b).insert(c));
        assertEquals(3, v.size());

        assertEquals(false, v.getValueForKey("d").isFilled());
        assertSame(null, v.getEntryForKey("d"));

        MutableDelta sizeDelta = new MutableDelta();
        HashTrieValue<String, String> nv = v.setValueForKey("d", "dd", sizeDelta);
        assertEquals(true, nv instanceof HashTrieMultiValue);
        assertEquals(true, nv.getEntryForKey("d") instanceof HashTrieSingleValue);
        assertEquals(1, sizeDelta.getValue());
        assertEquals(true, nv.getValueForKey("d").isFilled());
        assertEquals("dd", nv.getValueForKey("d").getValue());
        assertEquals(true, nv.getValueForKey("a").isFilled());
        assertEquals("aa", nv.getValueForKey("a").getValue());
        assertSame(a, nv.getEntryForKey("a"));
        assertEquals(true, nv.getValueForKey("b").isFilled());
        assertEquals("bb", nv.getValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.getValueForKey("c").isFilled());
        assertEquals("cc", nv.getValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(4, nv.size());

        Cursor<JImmutableMap.Entry<String, String>> cursor = nv.cursor().next();
        assertEquals(true, cursor.hasValue());
        assertEquals("d", cursor.getValue().getKey());
        assertEquals("dd", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("a", cursor.getValue().getKey());
        assertEquals("aa", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("b", cursor.getValue().getKey());
        assertEquals("bb", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("c", cursor.getValue().getKey());
        assertEquals("cc", cursor.getValue().getValue());
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());

        sizeDelta = new MutableDelta();
        nv = v.deleteValueForKey("d", sizeDelta);
        assertEquals(true, nv instanceof HashTrieMultiValue);
        assertEquals(0, sizeDelta.getValue());
        assertEquals(true, nv.getValueForKey("a").isFilled());
        assertEquals("aa", nv.getValueForKey("a").getValue());
        assertSame(a, nv.getEntryForKey("a"));
        assertEquals(true, nv.getValueForKey("b").isFilled());
        assertEquals("bb", nv.getValueForKey("b").getValue());
        assertSame(b, nv.getEntryForKey("b"));
        assertEquals(true, nv.getValueForKey("c").isFilled());
        assertEquals("cc", nv.getValueForKey("c").getValue());
        assertSame(c, nv.getEntryForKey("c"));
        assertEquals(3, nv.size());
    }
}
