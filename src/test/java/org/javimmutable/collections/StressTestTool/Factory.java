///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.tree_list.JImmutableTreeList;
import org.javimmutable.collections.util.JImmutables;

public abstract class Factory<T>
{
    abstract T create();

    public Factory<JImmutableList<String>> listFactory()
    {
        return new ListFactory();
    }

    public Factory<JImmutableMap<String, String>> mapFactory()
    {
        return new MapFactory();
    }

    private static class ListFactory
            extends Factory<JImmutableList<String>>
    {
        private int count;

        @Override
        public JImmutableList<String> create()
        {
            //returns random access lists to do normal list-y things on them. That way, no
            //duplicate code needed in randomAccess tests--that can just test the randomAccess-y things
            count += 1;
            if (count % 3 == 0) {
                return JImmutables.list();  //returns ArrayList
            } else if (count % 3 == 1) {
                return JImmutables.ralist();    //returns BtreeList
            } else {
                return JImmutableTreeList.of();
            }
        }
    }

    private static class RandomAccessListFactory
            extends Factory<JImmutableRandomAccessList<String>>
    {
        private int count;

        @Override
        public JImmutableRandomAccessList<String> create()
        {
            count += 1;
            if (count % 2 == 0) {
                return JImmutables.ralist();    //returns BtreeList
            } else {
                return JImmutableTreeList.of();
            }
        }
    }

    private static class MapFactory
            extends Factory<JImmutableMap<String, String>>
    {
        private int count;

        @Override
        public JImmutableMap<String, String> create()
        {
            count += 1;
            if (count % 2 == 0) {
                return JImmutables.map();
            } else {
                return JImmutables.sortedMap();
            }
        }
    }
}
