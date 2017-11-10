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

package org.javimmutable.collections.listmap;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractJImmutableListMapTestTestCase
    extends TestCase
{
    public JImmutableListMap<Integer, Integer> verifyOperations(JImmutableListMap<Integer, Integer> map)
    {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertNull(map.get(1));
        assertEquals(0, map.getList(1).size());

        map = map.insert(1, 100);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertSame(map.getList(1), map.get(1));
        assertEquals(1, map.getList(1).size());

        map = map.insert(1, 18);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEquals(Arrays.asList(100, 18), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(2, map.getList(1).size());

        map = map.insert(MapEntry.of(3, 87));
        map = map.insert(MapEntry.of(2, 87));
        map = map.insert(MapEntry.of(1, 87));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(100, 18, 87), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(Arrays.asList(87), map.getList(2).getList());
        assertSame(map.getList(2), map.get(2));
        assertEquals(Arrays.asList(87), map.getList(3).getList());
        assertSame(map.getList(3), map.get(3));

        map = map.assign(3, JImmutableArrayList.<Integer>of().insert(300).insert(7).insert(7).insert(14));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(100, 18, 87), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(Arrays.asList(87), map.getList(2).getList());
        assertSame(map.getList(2), map.get(2));
        assertEquals(Arrays.asList(300, 7, 7, 14), map.getList(3).getList());
        assertSame(map.getList(3), map.get(3));

        final JImmutableList<Integer> defaultValue = JImmutableArrayList.<Integer>of().insert(17);
        assertTrue(map.find(8).isEmpty());
        assertNull(map.get(8));
        assertNull(map.getValueOr(8, null));
        assertSame(defaultValue, map.getValueOr(8, defaultValue));
        assertSame(map.get(3), map.find(3).getValue());
        assertSame(map.get(3), map.getValueOr(3, defaultValue));
        assertTrue(map.deleteAll().isEmpty());
        assertTrue(map.delete(3).delete(2).delete(1).delete(0).isEmpty());

        StandardCursorTest.listCursorTest(Arrays.asList(100, 18, 87), map.valuesCursor(1));
        StandardCursorTest.listCursorTest(Arrays.asList(87), map.valuesCursor(2));
        StandardCursorTest.listCursorTest(Arrays.asList(300, 7, 7, 14), map.valuesCursor(3));
        StandardCursorTest.listCursorTest(Collections.emptyList(), map.valuesCursor(4));

        verifyTransform(map);

        return map;
    }

    private void verifyTransform(JImmutableListMap<Integer, Integer> map)
    {
        final Func1<JImmutableList<Integer>, JImmutableList<Integer>> removeAll = list -> list.deleteAll();
        final Func1<JImmutableList<Integer>, JImmutableList<Integer>> removeLarge = list -> list.reject(x -> x >= 10);
        final Func1<JImmutableList<Integer>, JImmutableList<Integer>> removeEven = list -> list.reject(x -> x % 2 == 0);

        final int goodKey = 1;
        final int badKey = 2;

        final JImmutableListMap<Integer, Integer> start = map.deleteAll().insertAll(goodKey, Arrays.asList(1, 2, 3, 4, 5, 6));
        final JImmutableList<Integer> oddOnly = start.getList(goodKey).reject(x -> x % 2 == 0);

        assertSame(start, start.transform(goodKey, removeLarge));
        assertEquals(start.assign(goodKey, oddOnly), start.transform(goodKey, removeEven));
        assertSame(start, start.transform(badKey, removeLarge));

        assertSame(start, start.transformIfPresent(goodKey, removeLarge));
        assertEquals(start.assign(goodKey, oddOnly), start.transformIfPresent(goodKey, removeEven));
        assertSame(start, start.transform(badKey, removeLarge));
        assertSame(start, start.transformIfPresent(badKey, removeAll));
        assertSame(start, start.transformIfPresent(badKey, removeAll));
    }
}
