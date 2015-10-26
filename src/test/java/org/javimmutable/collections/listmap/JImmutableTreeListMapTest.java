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

package org.javimmutable.collections.listmap;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;
import java.util.Comparator;

public class JImmutableTreeListMapTest
        extends AbstractJImmutableListMapTestTestCase
{
    @SuppressWarnings("unchecked")
    public void testNormalOrder()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableTreeListMap.<Integer, Integer>of());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(1, map.getList(1)),
                                                                                                               MapEntry.of(2, map.getList(2)),
                                                                                                               MapEntry.of(3, map.getList(3))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(1, map.getList(1)),
                                                                                                                 MapEntry.of(2, map.getList(2)),
                                                                                                                 MapEntry.of(3, map.getList(3))),
                                            map.iterator());
    }

    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableTreeListMap.<Integer, Integer>of(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return b.compareTo(a);
            }
        }));
        StandardCursorTest.listCursorTest(Arrays.asList(3, 2, 1), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(3, map.getList(3)),
                                                                                                               MapEntry.of(2, map.getList(2)),
                                                                                                               MapEntry.of(1, map.getList(1))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(3, map.getList(3)),
                                                                                                                 MapEntry.of(2, map.getList(2)),
                                                                                                                 MapEntry.of(1, map.getList(1))),
                                            map.iterator());
    }

    public void testEquals()
    {
        JImmutableListMap<Integer, Integer> a = JImmutableTreeListMap.of();
        JImmutableListMap<Integer, Integer> b = JImmutableTreeListMap.of();
        assertEquals(a, b);
        assertEquals(b, a);

        a = a.insert(1, 10);
        assertFalse(a.equals(b));
        b = b.insert(1, 10);
        assertEquals(a, b);
        assertEquals(b, a);
        a = a.insert(1, 12);
        assertFalse(a.equals(b));
        b = b.insert(1, 12);
        assertEquals(a, b);
        assertEquals(b, a);
    }
}
