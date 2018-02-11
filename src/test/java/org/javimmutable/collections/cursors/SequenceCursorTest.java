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
import org.javimmutable.collections.InsertableSequence;
import org.javimmutable.collections.Sequence;
import org.javimmutable.collections.sequence.EmptySequenceNode;
import org.javimmutable.collections.sequence.FilledSequenceNode;

import java.util.Arrays;

public class SequenceCursorTest
    extends TestCase
{
    public void testEmptyList()
    {
        Sequence<Integer> list = EmptySequenceNode.of();
        Cursor<Integer> cursor = SequenceCursor.of(list);
        StandardCursorTest.emptyCursorTest(cursor);
    }

    public void testSingleList()
    {
        Sequence<Integer> list = FilledSequenceNode.of(100);
        Cursor<Integer> cursor = SequenceCursor.of(list);
        StandardCursorTest.listCursorTest(Arrays.asList(100), cursor);
    }

    public void testMultiList()
    {
        InsertableSequence<Integer> list = EmptySequenceNode.of();
        for (int i = 8; i >= 1; --i) {
            list = list.insert(i);
        }
        Cursor<Integer> cursor = SequenceCursor.of(list);
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), cursor);
    }
}
