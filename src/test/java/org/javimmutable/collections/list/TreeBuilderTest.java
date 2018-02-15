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

package org.javimmutable.collections.list;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class TreeBuilderTest
    extends TestCase
{
    public void testForwardSimple()
    {
        TreeBuilder<Integer> builder = new TreeBuilder<>(true);
        Node<Integer> expected = EmptyNode.of();
        for (int i = 1; i <= 1025; ++i) {
            assertEquals(expected, builder.build());
            builder.add(i);
            expected = expected.insertLast(i);
        }
        assertEquals(expected, builder.build());
        builder.build().checkInvariants();
    }

    public void testReverseSimple()
    {
        TreeBuilder<Integer> builder = new TreeBuilder<>(false);
        Node<Integer> expected = EmptyNode.of();
        for (int i = 1; i <= 1025; ++i) {
            assertEquals(expected, builder.build());
            builder.add(i);
            expected = expected.insertFirst(i);
        }
        assertEquals(expected, builder.build());
        builder.build().checkInvariants();
    }

    private void assertEquals(Node<Integer> expected,
                              Node<Integer> actual)
    {
        List<Integer> expectedList = new ArrayList<>(expected.size());
        expected.iterator().forEachRemaining(value -> expectedList.add(value));

        List<Integer> actualList = new ArrayList<>(actual.size());
        actual.iterator().forEachRemaining(value -> actualList.add(value));

        assertEquals(expectedList, actualList);
    }
}
