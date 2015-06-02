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

package org.javimmutable.collections.setmap;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public abstract class AbstractJImmutableSetMapTestTestCase
        extends TestCase
{
    public JImmutableSetMap<Integer, Integer> verifyOperations(JImmutableSetMap<Integer, Integer> map)
    {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertNull(map.get(1));
        assertEquals(0, map.getSet(1).size());

        map = map.insert(1, 100);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(1, map.getSet(1).size());

        map = map.insert(1, 18);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 18)), map.getSet(1).getSet());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(2, map.getSet(1).size());

        map = (JImmutableSetMap<Integer, Integer>)map.insert(MapEntry.of(3, 87));
        map = (JImmutableSetMap<Integer, Integer>)map.insert(MapEntry.of(2, 87));
        map = (JImmutableSetMap<Integer, Integer>)map.insert(MapEntry.of(1, 87));
        map = (JImmutableSetMap<Integer, Integer>)map.insert(MapEntry.of(1, 87));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 18, 87)), map.getSet(1).getSet());
        assertEquals(3, map.getSet(1).size());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(new HashSet<Integer>(Arrays.asList(87)), map.getSet(2).getSet());
        assertSame(map.getSet(2), map.get(2));
        assertEquals(new HashSet<Integer>(Arrays.asList(87)), map.getSet(3).getSet());
        assertSame(map.getSet(3), map.get(3));

        map = map.assign(3, JImmutableHashSet.<Integer>of().insert(300).insert(7).insert(7).insert(14));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 18, 87)), map.getSet(1).getSet());
        assertSame(map.getSet(1), map.get(1));
        assertEquals(new HashSet<Integer>(Arrays.asList(87)), map.getSet(2).getSet());
        assertSame(map.getSet(2), map.get(2));
        assertEquals(new HashSet<Integer>(Arrays.asList(300, 7, 14)), map.getSet(3).getSet());
        assertSame(map.getSet(3), map.get(3));

        final JImmutableSet<Integer> defaultValue = JImmutableHashSet.<Integer>of().insert(17);
        assertTrue(map.find(8).isEmpty());
        assertNull(map.get(8));
        assertNull(map.getValueOr(8, null));
        assertSame(defaultValue, map.getValueOr(8, defaultValue));
        assertSame(map.get(3), map.find(3).getValue());
        assertSame(map.get(3), map.getValueOr(3, defaultValue));
        assertTrue(map.deleteAll().isEmpty());
        assertTrue(map.delete(3).delete(2).delete(1).delete(0).isEmpty());

        StandardCursorTest.listCursorTest(Arrays.asList(18, 87, 100), map.valuesCursor(1));
        StandardCursorTest.listCursorTest(Arrays.asList(87), map.valuesCursor(2));
        StandardCursorTest.listCursorTest(Arrays.asList(7, 14, 300), map.valuesCursor(3));
        StandardCursorTest.listCursorTest(Collections.<Integer>emptyList(), map.valuesCursor(4));
        return map;
    }

}
