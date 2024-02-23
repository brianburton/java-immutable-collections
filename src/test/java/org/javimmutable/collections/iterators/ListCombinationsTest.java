///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.iterators;

import static org.javimmutable.collections.iterators.ListCombinations.combosOfListsOfLength;
import static org.javimmutable.collections.iterators.ListCombinations.listsOfLength;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;

public class ListCombinationsTest
{
    @Test
    public void testListsOfLength()
    {
        assertEquals(List.of(List.of()), listsOfLength(0, 0));
        assertEquals(List.of(List.of(), List.of(1)), listsOfLength(0, 1));
        assertEquals(List.of(List.of(), List.of(1), List.of(1, 2)), listsOfLength(0, 2));
        assertEquals(List.of(List.of(), List.of(1), List.of(1, 2), List.of(1, 2, 3)), listsOfLength(0, 3));
    }

    @Test
    public void testCombos()
    {
        assertEquals(List.of(), combosOfListsOfLength(0, 1, 2));
        assertEquals(List.of(List.of(List.of(1)),
                             List.of(List.of(1, 2))),
                     combosOfListsOfLength(1, 1, 2));
        assertEquals(List.of(List.of(List.of(1), List.of(1)),
                             List.of(List.of(1), List.of(1, 2)),
                             List.of(List.of(1, 2), List.of(1)),
                             List.of(List.of(1, 2), List.of(1, 2))),
                     combosOfListsOfLength(2, 1, 2));
        assertEquals(List.of(List.of(List.of(1), List.of(1), List.of(1)),
                             List.of(List.of(1), List.of(1), List.of(1, 2)),
                             List.of(List.of(1), List.of(1, 2), List.of(1)),
                             List.of(List.of(1), List.of(1, 2), List.of(1, 2)),

                             List.of(List.of(1, 2), List.of(1), List.of(1)),
                             List.of(List.of(1, 2), List.of(1), List.of(1, 2)),
                             List.of(List.of(1, 2), List.of(1, 2), List.of(1)),
                             List.of(List.of(1, 2), List.of(1, 2), List.of(1, 2))),
                     combosOfListsOfLength(3, 1, 2));
    }

    public void testRenumberCombos() {
        List<List<List<Integer>>> combos = combosOfListsOfLength(3, 1, 2);
        ListCombinations.renumberCombos(combos);
        assertEquals(List.of(List.of(List.of(1), List.of(2), List.of(3)),
                             List.of(List.of(1), List.of(2), List.of(3, 4)),
                             List.of(List.of(1), List.of(2, 3), List.of(4)),
                             List.of(List.of(1), List.of(2, 3), List.of(4, 5)),

                             List.of(List.of(1, 2), List.of(3), List.of(4)),
                             List.of(List.of(1, 2), List.of(3), List.of(4, 5)),
                             List.of(List.of(1, 2), List.of(3, 4), List.of(5)),
                             List.of(List.of(1, 2), List.of(3, 4), List.of(5, 6))),
                     combos);
    }
}
