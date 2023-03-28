///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

import java.util.Map;
import junit.framework.TestCase;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.list.ListCollisionMap;
import org.javimmutable.collections.tree.TreeCollisionMap;

public class EmptyHashMapTest
    extends TestCase
{
    @SuppressWarnings("ConstantConditions")
    public void testAssign()
    {
        IMap<String, Integer> comparableMap = HashMap.of();
        comparableMap = comparableMap.assign("a", 100);
        assertTrue(comparableMap instanceof HashMap);
        assertSame(TreeCollisionMap.instance(), ((HashMap)comparableMap).getCollisionMap());

        IMap<Object, Integer> otherMap = HashMap.of();
        otherMap = otherMap.assign(new Object(), 100);
        assertTrue(otherMap instanceof HashMap);
        assertSame(ListCollisionMap.instance(), ((HashMap)otherMap).getCollisionMap());
    }

    public void testAssignAll()
    {
        IMap<String, Number> map = HashMap.of();
        IMap<String, Integer> expected = HashMap.of();
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(0, map.size());
        assertFalse(map instanceof HashMap);

        expected = expected.assign("a", 10);
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertTrue(map instanceof HashMap);
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());

        map = HashMap.of();
        expected = expected.assign("b", 12).assign("c", 14);
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertTrue(map instanceof HashMap);
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());


        map = HashMap.of();
        Map<String, Integer> expected2 = new java.util.HashMap<String, Integer>();
        map = map.assignAll(expected2);
        assertEquals(expected2, map.getMap());
        assertEquals(0, map.size());
        assertFalse(map instanceof HashMap);

        expected2.put("a", 10);
        map = map.assignAll(expected2);
        assertEquals(expected2, map.getMap());
        assertTrue(map instanceof HashMap);
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());

        map = HashMap.of();
        expected2.put("b", 12);
        expected2.put("c", 14);
        map = map.assignAll(expected2);
        assertEquals(expected2, map.getMap());
        assertTrue(map instanceof HashMap);
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());
    }
}
