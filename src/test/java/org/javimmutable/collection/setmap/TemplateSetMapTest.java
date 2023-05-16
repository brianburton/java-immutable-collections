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

package org.javimmutable.collection.setmap;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.ISet;
import org.javimmutable.collection.ISetMap;
import org.javimmutable.collection.common.StandardSerializableTests;
import org.javimmutable.collection.iterators.StandardIteratorTests;
import org.javimmutable.collection.tree.ComparableComparator;
import org.javimmutable.collection.tree.TreeMap;
import org.javimmutable.collection.tree.TreeMapTest;
import org.javimmutable.collection.tree.TreeSet;
import org.javimmutable.collection.tree.TreeSetTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class TemplateSetMapTest
    extends AbstractSetMapTestCase
{
    public void testVarious()
    {
        final Comparator<Integer> reverse = ComparableComparator.<Integer>of().reversed();
        final TreeMap<Integer, ISet<Integer>> emptyMap = TreeMap.of();
        final TreeSet<Integer> emptySet = TreeSet.of(reverse);
        final ISetMap<Integer, Integer> empty = TemplateSetMap.of(emptyMap.assign(1, emptySet.insert(10)),
                                                                  emptySet.insert(8).insert(25));
        assertEquals(true, empty.isEmpty());
        assertEquals(0, empty.stream().count());
        assertEquals(0, empty.keys().stream().count());
        assertNull(empty.get(1));
        ISetMap<Integer, Integer> map = verifyOperations(empty, Ordering.REVERSED);
        verifyRandom(TreeSetMap.of(), new java.util.TreeMap<>());
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getSet(1)),
                                                             IMapEntry.of(2, map.getSet(2)),
                                                             IMapEntry.of(3, map.getSet(3))),
                                               map.iterator());

        map = empty
            .insert(10, 100)
            .insert(10, 111)
            .insert(7, 5)
            .insert(3, 8)
            .insert(7, 12)
            .insert(3, 90);
        assertEquals(Arrays.asList(3, 7, 10), map.keys().stream().collect(toList()));
        assertEquals(Arrays.asList(90, 8), map.getSet(3).stream().collect(toList()));
        assertEquals(Arrays.asList(12, 5), map.getSet(7).stream().collect(toList()));
        assertEquals(Arrays.asList(111, 100), map.getSet(10).stream().collect(toList()));
    }

    public void testEquals()
    {
        final TreeMap<Integer, ISet<Integer>> emptyMap = TreeMap.of();
        final TreeSet<Integer> emptySet = TreeSet.of();
        final ISetMap<Integer, Integer> empty = TemplateSetMap.of(emptyMap.assign(1, emptySet.insert(10)),
                                                                  emptySet.insert(8).insert(25));
        ISetMap<Integer, Integer> a = empty;
        ISetMap<Integer, Integer> b = empty;
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

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((TemplateSetMap)a).iterator();
        ISetMap<String, String> empty = TemplateSetMap.of(TreeMap.<String, ISet<String>>of(), TreeSet.of());
        StandardSerializableTests.verifySerializable(iteratorFactory, TemplateSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMC0nNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIMssxqbikKDG5BIdZBeUsDAzML4HuMyPBfUWpqdgdZkW6w/C7yhSfgSVAd+g55+cWJBaBpKCskvwimFlMMLOANOleBAYZdbyIzSCYFwsZ6hjYoE6EUgB2vfgBMQIAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TemplateSetMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMC0nNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIMssxqbikKDG5BIdZBeUsDAzML4HuMyPBfUWpqdgdZkW6w/C7yhSfgSVAd+g55+cWJBaBpKCskvwimFlMMLOANOleBAYZdbyIzSCYFwsZ6hjYoE4EU4wlDIyOcFZiBQBIz+0HPwIAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TemplateSetMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMC0nNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIMssxqbikKDG5BIdZBeUsDAzML4HuMyPBfUWpqdgdZkW6w/C7yhSfgSVAd+g55+cWJBaBpKCskvwimFlMMLOANOleBAYZdbyIzSCYFwsZ6hjYoE4EU8wlDIyOYBYjkJUIxFFwXjJINS+cm1QBAEVcO4NcAgAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMC0nNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIMssxqbikKDG5BIdZBeUsDAzML4HuMyPBfUWpqdgdZkW6w/C7yhSfgSVAd+g55+cWJBaBpKCskvwimFlMMLOANOleBAYZdbyIzSCYFwsZ6hjYoE4EU0wlDIyOEAEgKwnMYgSynCoAtDwEP0kCAAA=");

        empty = TemplateSetMap.of(TreeMap.of(String.CASE_INSENSITIVE_ORDER), TreeSet.of(String.CASE_INSENSITIVE_ORDER));
        StandardSerializableTests.verifySerializable(iteratorFactory, TemplateSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/62QOQoCQRBFGxdMPMZEBp0IBoKBTGQgCBqalEMxtPRmdbmCnsizeAsDA6+gtjJmKgxYyecnj/freBX1QKLnKJdzWCljlgwzjTJzWmPGylkZkBRotYNXm6DxGhjHyEPwI3Kb7f15t6TdFGJTitWfBSbI+AvLr2tCVC/Rr1PCjxA/i3XLi/22akUYSA02l2MmZfMkhYADG9AGxWqFqTMeCNjRujrdT0fnU+UNiFl+V/zTf3Z9AhW7FuIgGoVhEQ/aBV4xJQIAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TemplateSetMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAA/62Qvw4BQRCHx79oPIZKsY1EIVGISiG5hFIzLpPLyt7umR3nSHgiz+ItFAqvgCOokFximt9M8+X7zf4MNc/QcxypOaY6jpeCM0MqdMZQKNpZ5Yk1Gr3BxzWhODEoNCYZYRKwy9bX+1ya7QZAVojVn3lhDOULK1lVASqn3K9TwI+JPot1i4v9tmrlMFQGbaTGwtpGzQF6GlpP1mvRKQ1cnCCjOF5VpttpcDyUX4A8i/fK//SfXp9Az14L2EH9afiIkkCp/94wuwHuiL4hMwIAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TemplateSetMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("a", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMC0nNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIMssxqbikKDG5BIdZBeUsDAzML4HuMyPBfUWpqdgdZkW6w/C7SgtoWKJeTmJeul5wSVFmXrqKc2JxqmdecWpecWZJZlmqc35uQWJRYkl+UTlzTG1MwNNzTDADgDTp/gKGE3X8hc0gqL8KGeoY2KEuBFNMJQyMiRAWSI4byE0C4iiwECOQlVwBALcUQaBGAgAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBNr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMC0nNLchJLEkNTi3xTSwIKMqvqPwPAv9UjHkYGCpIMssxqbikKDG5BIdZBeUsDAzML4HuMyPBfUWpqdgdZkW6w/C7SgtoWKJeTmJeul5wSVFmXrqKc2JxqmdecWpecWZJZlmqc35uQWJRYkl+UTlzTG1MwNNzTDADgDTp/gKGE3X8hc0gqL8KGeoY2KEuBFNMJQyMjhABICsJzGIEspwqAG/QdVY9AgAA");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TemplateSetMap mapA = (TemplateSetMap)a;
        TemplateSetMap mapB = (TemplateSetMap)b;
        assertEquals(mapA.getEmptyMap().getClass(), mapB.getEmptyMap().getClass());
        if (mapA.getEmptyMap() instanceof TreeMap) {
            TreeMapTest.extraSerializationChecks(mapA.getEmptyMap(), mapB.getEmptyMap());
        }
        assertEquals(mapA.getEmptySet().getClass(), mapB.getEmptySet().getClass());
        if (mapA.getEmptySet() instanceof TreeSet) {
            TreeSetTest.extraSerializationChecks(mapA.getEmptySet(), mapB.getEmptySet());
        }
    }
}
