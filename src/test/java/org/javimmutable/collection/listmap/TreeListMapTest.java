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
import org.javimmutable.collection.tree.TreeMapTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

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
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8gxqbikKDG5BJdhBeUsDAzML4GuM8VnaAnQPXrO+bkFiUUgKSirJL8IZhYTzCwgDQB5SSTm7wAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8gxqbikKDG5BJdhBeUsDAzML4GuM8VnaAnQPXrO+bkFiUUgKSirJL8IZhYTzCwGxhIGRkc4K7ECAPKG5tz9AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8gxqbikKDG5BJdhBeUsDAzML4GuM8VnaAnQPXrO+bkFiUUgKSirJL8IZhYTzCwG5hIGRkcwixHISgTiKDgvuZChjoENzk2qAACl/nCsGgEAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8gxqbikKDG5BJdhBeUsDAzML4GuM8VnaAnQPXrO+bkFiUUgKSirJL8IZhYTzCwGphIGRkcwiwHISgKzGIEspwoA8dlPIAcBAAA=");

        empty = TreeListMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8gxqbikKDG5BJdhBeUsDAzML4Gu0wIamKiXk5iXrhdcUpSZl67inFic6plXnJpXnFmSWZbqnJ9bkFiUWJJfVM4cUxsT8PQcE8wAIA0Ab+/O3+QAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfXJLC7xTSwIKMqvqPwPAv9UjHkYGCqKGOyIN8gxqbikKDG5BJdhBeUsDAzML4Gu0wIamKiXk5iXrhdcUpSZl67inFic6plXnJpXnFmSWZbqnJ9bkFiUWJJfVM4cUxsT8PQcE8wABsYSBkZHOCuxAgAH3uIA8gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeListMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/42OMQrCQBREv4npPEYqi23sLASxEhQErSTNz7KElc1u/PvVKOiJPIu3sLDwCuoasRQcGJiZ4jHnOySeoO+oECvc6rLcMOZGCemMUZK1s8Ir0mj0AZu2IKUm2vMUqxm5ev9865H2OgA1weB/0DD3TCj5F6zatQHiW3jXDUAUBm0h5kzaFukIvRpbr6zXrLdq5MoKCdnRLs6O2ex6ib4AiBha+ElrOEESah68bKZWSLJ+AahubWoFAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").transform("A", l -> l.deleteLast()),
                                                     "H4sIAAAAAAAA/43OMQrCQBCF4UmincdIZbGNnYUQUwkKAS3TTMISVja7YXZMouCNPIu3sLDwCuoasBSc6p/m410eMHYEc0uV2GOr6vrAWGgpSqu1LFlZI5wkhVqdcPh2JOVaOd5gk5Htj6/PPePZBKAnWPwPJYVjwpJ/YU03Aojuft3Ugyg0mkpsmZSp4hSdXBknjVOsWpnaukFCttRF+TnPbtfwC0DIECRDga9iqMDXsn8DiRqJ9/wAAAA=");
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
