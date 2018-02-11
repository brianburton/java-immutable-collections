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

package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedHelper;

import java.util.Arrays;

import static com.google.common.primitives.Ints.asList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.cursors.StandardCursorTest.*;

@SuppressWarnings("unchecked")
public class LazyMultiCursorTest
    extends TestCase
{
    public void test()
    {
        emptyCursorTest(cursor(values()));
        emptyCursorTest(cursor(values(), values()));
        emptyCursorTest(cursor(values(), values(), values()));

        listCursorTest(Arrays.asList(1), cursor(values(1)));
        listCursorTest(Arrays.asList(1), cursor(values(), values(1)));
        listCursorTest(Arrays.asList(1), cursor(values(1), values()));

        listCursorTest(Arrays.asList(1, 2), cursor(values(1, 2)));
        listCursorTest(Arrays.asList(1, 2), cursor(values(1), values(2)));
        listCursorTest(Arrays.asList(1, 2), cursor(values(), values(1), values(), values(2), values()));

        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(1, 2, 3, 4)));
        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(1, 2), values(3, 4)));
        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(), values(1), values(2, 3), values(), values(4)));

        Cursor<Integer> multi = LazyMultiCursor.transformed(StandardCursor.forRange(0, 3), value -> () -> StandardCursor.forRange(1, value));
        StandardCursorTest.listCursorTest(Arrays.asList(1, 1, 2, 1, 2, 3), multi);

        multi = LazyMultiCursor.transformed(IndexedHelper.indexed(2, 3, 4), value -> () -> StandardCursor.forRange(1, value));
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 1, 2, 3, 1, 2, 3, 4), multi);
    }

    public void testSplitAllowed()
    {
        assertEquals(false, valueCursor(0).start().isSplitAllowed());
        assertEquals(false, valueCursor(1).start().isSplitAllowed());
        assertEquals(true, valueCursor(2).start().isSplitAllowed());
        assertEquals(true, valueCursor(3).start().isSplitAllowed());
        assertEquals(true, valueCursor(4).start().isSplitAllowed());
    }

    public void testSplit()
    {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> valueCursor(0).start().splitCursor());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> valueCursor(1).start().splitCursor());
        StandardCursorTest.verifySplit(valueCursor(2).start(), asList(1), asList(2));
        StandardCursorTest.verifySplit(valueCursor(3).start(), asList(1), asList(2, 3));
        StandardCursorTest.verifySplit(valueCursor(4).start(), asList(1, 2), asList(3, 4));
    }

    private Cursor<Integer> valueCursor(int length)
    {
        final Cursorable<Integer>[] array = new Cursorable[length];
        for (int i = 1; i <= length; ++i) {
            array[i - 1] = values(i);
        }
        return cursor(array);
    }

    private Cursor<Integer> cursor(Cursorable<Integer>... array)
    {
        return LazyMultiCursor.cursor(IndexedArray.retained(array));
    }

    private Cursorable<Integer> values(Integer... array)
    {
        return IterableCursorable.of(Arrays.asList(array));
    }
}
