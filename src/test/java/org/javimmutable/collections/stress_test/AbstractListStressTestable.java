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

package org.javimmutable.collections.stress_test;


import static org.javimmutable.collections.common.StandardSerializableTests.verifySerializable;

import java.util.List;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.common.StandardIterableStreamableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;

abstract class AbstractListStressTestable
    extends StressTester
{
    AbstractListStressTestable(String testName)
    {
        super(testName);
    }

    protected void verifyContents(IList<String> list,
                                  List<String> expected)
    {
        System.out.printf("checking contents with size %d%n", list.size());
        if (list.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), list.isEmpty()));
        }
        if (list.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), list.size()));
        }

        int index = 0;
        for (String expectedValue : expected) {
            String listValue = list.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, listValue));
            }
            index += 1;
        }
        if (!expected.equals(list.getList())) {
            throw new RuntimeException("method call failed - getList()\n");
        }
        list.checkInvariants();
        verifySerializable(null, list, IList.class);
    }

    protected void verifyIterator(IList<String> list,
                                  List<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", list.size());
        StandardIteratorTests.listIteratorTest(expected, list.iterator());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(expected, list);
    }
}
