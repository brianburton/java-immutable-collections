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

package org.javimmutable.collections.listmap;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.TreeMapTest;

public class TreeListMapTest
    extends AbstractListMapTestCase
{
    @SuppressWarnings("unchecked")
    public void testNormalOrder()
    {
        IListMap<Integer, Integer> map = verifyOperations(TreeListMap.of(), Ordering.HASH);
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getList(1)),
                                                             IMapEntry.of(2, map.getList(2)),
                                                             IMapEntry.of(3, map.getList(3))),
                                               map.iterator());
    }

    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        IListMap<Integer, Integer> map = verifyOperations(TreeListMap.of(Comparator.<Integer>reverseOrder()), Ordering.REVERSED);
        StandardIteratorTests.listIteratorTest(Arrays.asList(3, 2, 1), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(3, map.getList(3)),
                                                             IMapEntry.of(2, map.getList(2)),
                                                             IMapEntry.of(1, map.getList(1))),
                                               map.iterator());
    }

    public void testEquals()
    {
        IListMap<Integer, Integer> a = TreeListMap.of();
        IListMap<Integer, Integer> b = TreeListMap.of();
        assertEquals(a, b);
        assertEquals(b, a);

        a = a.insert(1, 10);
        assertFalse(a.equals(b));
        b = b.insert(1, 10);
        assertEquals(a, b);
        assertEquals(b, a);
        a = a.insert(1, 12);
        assertFalse(a.equals(b));
        b = b.insert(1, 12);
        assertEquals(a, b);
        assertEquals(b, a);
    }

    public void testStreams()
    {
        IListMap<Integer, Integer> listMap = TreeListMap.<Integer, Integer>of()
            .insert(4, 40)
            .insert(3, 30)
            .insert(2, 20)
            .insert(1, 10)
            .insert(2, 20)
            .insert(4, 45)
            .insert(4, 50);
        assertEquals(asList(1, 2, 3, 4), listMap.stream().map(e -> e.getKey()).collect(toList()));
        assertEquals(asList(1, 2, 1, 3), listMap.stream().map(e -> e.getValue().size()).collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((TreeListMap)a).iterator();
        IListMap<String, String> empty = TreeListMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYUAaAGoIkSAGAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYQyMJQyMjnBWYgUArB9/AxQBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYQzMJQyMjmAWI5CVCMRRcF5yIUMdAxucm1QBANqG5HMxAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt1shtf8EqDb9JzzcwsSi0ByUFZJfhHMMCaYYQxMJQyMjmAWA5CVBGYxAllOFQAZR+CMHgEAAA==");

        empty = TreeListMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt2sBTQ7US8nMS9dL7ikKDMvXcU5sTjVM684Na84sySzLNU5P7cgsSixJL+onDmmNibg6TkmmAFAGgAG1XVw+gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBNb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpCilKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGDxJMNMxqbikKDG5BGE2LnMLylkYGJhfAt2sBTQ7US8nMS9dL7ikKDMvXcU5sTjVM684Na84sySzLNU5P7cgsSixJL+onDmmNibg6TkmmAEMjCUMjI5wVmIFAAOWtngIAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAAAJWOMQrCQBBFx2g6j2FlsY0nkGARURC0kjSTsISVzW6cHTUKeiLP4i0sLLyCulGClYUDH/7/DI9/vkPoCEaWcrHCrSqKDWOqpcis1jJjZY0TTpJCrQ5YRzGOm6cFSTlRjqdYzshW+2d9j96gC1ARxH8wh6ljwoy/7F/cctcBaN/85r5no9BocjFnUibvRehkbJw0TrHaysgWJRKypV07OSaz6yVoABAwtPDj1nCC0MfUa/muWt5l1Qs2SrO7GwEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAAAJXOMQrCUAyA4dTq5jE6ObzFE6g4VBQKOnZJSyhPXt8rebGtgjfyLN7CwcErqFUQJwcz/YHwkdMNBp5h7rhQW6x1We4EM0Mqd8ZQLtpZrzyxRqMP+FrVIv4cbZhoqb2ssErYtfvHa+7ReAjQMsR/mJPMC2MuX/uXWzV9gPDa/TzqbFQGbaHWwtoW0Qw9xdaT9Vp0TTNXVsgojpswPabJ5dz7ANATCCbvgq6ydwVdTdsnnHjDlRIBAAA=");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeListMap mapA = (TreeListMap)a;
        TreeListMap mapB = (TreeListMap)b;
        assertEquals(mapA.getComparator(), mapB.getComparator());
        TreeMapTest.extraSerializationChecks(mapA.getMap(), mapB.getMap());
    }
}
