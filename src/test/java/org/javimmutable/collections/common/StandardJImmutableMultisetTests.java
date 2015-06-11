///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.common;


import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class StandardJImmutableMultisetTests
{
    public static void cursorTest(JImmutableMultiset<Integer> empty)
    {
        StandardCursorTest.emptyCursorTest(empty.cursor());
        StandardCursorTest.emptyCursorTest(empty.entryCursor());
        StandardCursorTest.emptyCursorTest(empty.occurrenceCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), empty.union(Arrays.asList(1, 1, 2, 3, 3)).cursor());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 1, 2, 3, 3), empty.union(Arrays.asList(1, 1, 2, 3, 3)).occurrenceCursor());

    }

    //based on StandardJImmutableSetTests, modified for multisets
    public static void verifySet(JImmutableMultiset<Integer> empty)
    {
        testVarious(empty);
        testRandom(empty);

        assertEquals(0, empty.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(empty, new HashSet<Integer>());
        assertEquals(empty.getSet(), new HashSet<Integer>());

        JImmutableMultiset<Integer> jmet = empty;
        assertEquals(false, jmet.contains(10));
        jmet = jmet.insert(10);
        assertEquals(true, jmet != empty);
        assertEquals(1, jmet.size());
        assertEquals(1, jmet.valueCount());
        assertEquals(false, jmet.isEmpty());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.contains(10, 1));
        assertEquals(false, jmet.contains(10, 2));

        jmet = jmet.delete(10);
        assertEquals(0, jmet.size());
        assertEquals(true, empty.isEmpty());
        assertEquals(false, jmet.contains(10));

        jmet = jmet.insert(10, 3);
        assertEquals(1, jmet.size());
        assertEquals(3, jmet.valueCount());
        assertEquals(true, jmet.contains(10));
        assertEquals(true, jmet.contains(10, 3));
        assertEquals(false, jmet.contains(10, 4));
        assertEquals(false, jmet.isEmpty());

        assertEquals(empty, jmet.delete(10));
        assertEquals(empty, jmet.deleteOccurrence(10, 3));
        assertEquals(empty, jmet.deleteOccurrence(10).deleteOccurrence(10).deleteOccurrence(10));
        assertEquals(empty, jmet.deleteAll());

        jmet = empty;
        final List<Integer> values = Arrays.asList(1, 2, 3, 4);



    }

    public static void testVarious(JImmutableMultiset<Integer> empty)
    {

    }

    public static void testRandom(JImmutableMultiset<Integer> empty)
    {

    }

    private static void verifyContents(JImmutableMultiset<Integer> jmet,
                                       List<Integer> expected)
    {
        assertEquals(expected.isEmpty(), jmet.isEmpty());
        assertEquals(expected.size(), jmet.valueCount());
        
    }

}
