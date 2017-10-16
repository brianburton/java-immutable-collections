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

package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.IndexedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.javimmutable.collections.cursors.StandardCursorTest.*;

@SuppressWarnings("unchecked")
public class LazyMultiCursorTest
    extends TestCase
{
    public void test()
    {
        emptyCursorTest(LazyMultiCursor.cursor(indexed()));
        emptyCursorTest(cursor(values()));
        emptyCursorTest(cursor(values(), values()));
        emptyCursorTest(cursor(values(), values(), values()));

        listCursorTest(Arrays.asList(1), LazyMultiCursor.cursor(indexed(values(1))));
        listCursorTest(Arrays.asList(1), cursor(values(1)));
        listCursorTest(Arrays.asList(1), cursor(values(), values(1)));
        listCursorTest(Arrays.asList(1), cursor(values(1), values()));

        listCursorTest(Arrays.asList(1, 2), cursor(values(1, 2)));
        listCursorTest(Arrays.asList(1, 2), cursor(values(1), values(2)));
        listCursorTest(Arrays.asList(1, 2), cursor(values(), values(1), values(), values(2), values()));

        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(1, 2, 3, 4)));
        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(1, 2), values(3, 4)));
        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(), values(1), values(2, 3), values(), values(4)));
    }

    public void testBuilder()
    {
        LazyMultiCursor.Builder builder = LazyMultiCursor.builder(0);
        emptyCursorTest(builder.cursor());

        builder = LazyMultiCursor.builder(1);
        emptyCursorTest(builder.cursor());

        builder = LazyMultiCursor.builder(1);
        builder.insert(values(10));
        listCursorTest(Arrays.asList(10), builder.cursor());

        List<Cursorable<Integer>> list = new ArrayList<Cursorable<Integer>>();
        list.add(values(1, 2, 3));
        builder = LazyMultiCursor.builder(5);
        builder.insert(IndexedList.copied(list));
        builder.insert(values(4));
        list.clear();
        builder.insert(IndexedList.copied(list));
        list.add(values(5));
        list.add(values(6, 7));
        list.add(values(8));
        builder.insert(IndexedList.copied(list));
        builder.insert(values(9, 10));
        listCursorTest(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), builder.cursorable().cursor());

        listCursorTest(Arrays.asList(5, 6, 7, 8), LazyMultiCursor.cursorable(IndexedList.copied(list)).cursor());
    }

    private Cursor<Integer> cursor(Cursorable<Integer>... array)
    {
        return LazyMultiCursor.cursor(IndexedArray.retained(array));
    }

    private Cursorable<Integer> values(Integer... array)
    {
        return IterableCursorable.of(Arrays.asList(array));
    }

    private Indexed<Cursorable<Integer>> indexed(Cursorable<Integer>... array)
    {
        return IndexedArray.retained(array);
    }
}
