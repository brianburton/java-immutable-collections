///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection.listmap;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IListMap;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.common.StandardSerializableTests;
import org.javimmutable.collection.iterators.StandardIteratorTests;

import java.util.Arrays;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class OrderedListMapTest
    extends AbstractListMapTestCase
{
    @SuppressWarnings("unchecked")
    public void test()
    {
        IListMap<Integer, Integer> map = verifyOperations(OrderedListMap.of(), Ordering.INORDER);
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 3, 2), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getList(1)),
                                                             IMapEntry.of(3, map.getList(3)),
                                                             IMapEntry.of(2, map.getList(2))),
                                               map.iterator());
    }

    public void testEquals()
    {
        IListMap<Integer, Integer> a = OrderedListMap.of();
        IListMap<Integer, Integer> b = OrderedListMap.of();
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
        IListMap<Integer, Integer> listMap = OrderedListMap.<Integer, Integer>of()
            .insert(4, 40)
            .insert(2, 20)
            .insert(3, 30)
            .insert(1, 10)
            .insert(2, 20)
            .insert(4, 45)
            .insert(4, 50);
        assertEquals(asList(4, 2, 3, 1), listMap.stream().map(e -> e.getKey()).collect(toList()));
        assertEquals(asList(3, 2, 1, 1), listMap.stream().map(e -> e.getValue().size()).collect(toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((OrderedListMap)a).iterator();
        IListMap<String, String> empty = OrderedListMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM8y9KSS1KTfHJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8sxqbikKDG5BJdhBeUcDAzMLxmAoAIAaguVCaoAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM8y9KSS1KTfHJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8sxqbikKDG5BJdhBeUcDAzMLxkYGBhLGBgdy1mgrMQKAE/5YkC4AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM8y9KSS1KTfHJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8sxqbikKDG5BJdhBeUcDAzMLxmARAkDo2M5C5DFCGQlFjLUMbDAuUlAHAXnJVcAAH6y8fzVAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHM8y9KSS1KTfHJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8sxqbikKDG5BJdhBeUcDAzMLxkYGJhKGBgdy1mALAYgKwnMYgSynCoAFEJEi8IAAAA=");
    }
}
