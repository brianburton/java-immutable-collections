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
import org.javimmutable.collections.Func1;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.cursors.StandardCursorTest.*;

public class TransformCursorTest
    extends TestCase
{
    private final Func1<Integer, String> transforminator = value -> String.valueOf(value);

    public void testEmpty()
    {
        listCursorTest(Collections.emptyList(), TransformCursor.of(StandardCursor.of(), transforminator));
    }

    public void testRange()
    {
        listCursorTest(Arrays.asList("3", "4", "5", "6"), forRange(3, 6));
    }

    public void testSplitAllowed()
    {
        assertEquals(false, forRange(1, 1).start().isSplitAllowed());
        assertEquals(true, forRange(1, 4).start().isSplitAllowed());
    }

    public void testSplit()
    {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> forRange(1, 1).start().splitCursor());
        verifySplit(forRange(1, 4).start(), asList("1", "2"), asList("3", "4"));
    }

    private Cursor<String> forRange(int first,
                                    int last)
    {
        return TransformCursor.of(StandardCursor.forRange(first, last), transforminator);
    }
}
