///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableList;

import java.util.ArrayList;
import java.util.List;

public class ListAppendTimingComparison
{
    public static void main(String[] argv)
    {
        final Mode mode = (argv.length == 0) ? Mode.LAST : Mode.valueOf(argv[0].replace("-", "_").toUpperCase());
        final long startMillis = System.currentTimeMillis();
        runTest(mode);
        final long elapsedMillis = System.currentTimeMillis() - startMillis;
        System.out.printf("%s  %d%n", mode, elapsedMillis);
    }

    private enum Mode
    {
        FIRST,
        MIDDLE,
        LAST
    }

    private static void runTest(Mode mode)
    {
        for (int loop = 1; loop <= 10_000; ++loop) {
            JImmutableList<Integer> list = JImmutableTreeList.of();
            List<Integer> extras = new ArrayList<>();
            for (int length = 1; length <= 250; ++length) {
                extras.add(length);
                switch (mode) {
                    case FIRST:
                        list = list.insertAllFirst(extras);
                        break;
                    case MIDDLE:
                        list = list.insertAll(list.size() / 2, extras);
                        break;
                    case LAST:
                        list = list.insertAllLast(extras);
                        break;
                }
            }
        }
    }
}
