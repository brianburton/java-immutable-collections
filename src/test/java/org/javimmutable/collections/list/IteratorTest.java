///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterator;

import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.iterators.StandardIteratorTests.verifyOrderedIterable;

public class IteratorTest
    extends TestCase
{
    public void testVarious()
    {
        List<Integer> list = new ArrayList<>();
        TreeBuilder<Integer> tree = new TreeBuilder<>();
        for (int i = 1; i <= 1024; ++i) {
            verifyOrderedIterable(list, tree.build());

            List<Integer> traversed = new ArrayList<>();
            traverse(traversed, tree.build().iterator());
            assertEquals(traversed, list);

            list.add(i);
            tree.add(i);
        }
    }

    private void traverse(List<Integer> values,
                          SplitableIterator<Integer> source)
    {
        if (source.isSplitAllowed()) {
            SplitIterator<Integer> split = source.splitIterator();
            traverse(values, split.getLeft());
            traverse(values, split.getRight());
        } else {
            while (source.hasNext()) {
                values.add(source.next());
            }
        }
    }
}
