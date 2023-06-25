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

package org.javimmutable.collections.setmap;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.ISetMap;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;
import org.javimmutable.collections.tree.TreeMapTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

public class TreeSetMapTest
    extends AbstractSetMapTestCase
{
    public void testNormalOrder()
    {
        ISetMap<Integer, Integer> map = verifyOperations(TreeSetMap.of(), Ordering.HASH);
        verifyRandom(TreeSetMap.of(), new TreeMap<>());
        StandardIteratorTests.listIteratorTest(Arrays.asList(1, 2, 3), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(1, map.getSet(1)),
                                                             IMapEntry.of(2, map.getSet(2)),
                                                             IMapEntry.of(3, map.getSet(3))),
                                               map.iterator());
    }

    public void testReverseOrder()
    {
        ISetMap<Integer, Integer> map = verifyOperations(TreeSetMap.of(Comparator.<Integer>reverseOrder()), Ordering.HASH);
        StandardIteratorTests.listIteratorTest(Arrays.asList(3, 2, 1), map.keys().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(IMapEntry.of(3, map.getSet(3)),
                                                             IMapEntry.of(2, map.getSet(2)),
                                                             IMapEntry.of(1, map.getSet(1))),
                                               map.iterator());
    }

    public void testEquals()
    {
        ISetMap<Integer, Integer> a = TreeSetMap.of();
        ISetMap<Integer, Integer> b = TreeSetMap.of();
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
        final Func1<Object, Iterator> iteratorFactory = a -> ((TreeSetMap)a).iterator();
        ISetMap<String, String> empty = TreeSetMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4t8U0sCCjKr6j8DwL/VIx5GBgqihjsSDDIMam4pCgxuQSHYQXlLAwMzC+BrjPDa2gJ0D16zvm5BYlFIDkoqyS/CGYYE8wwIA0A/GkHEPAAAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4t8U0sCCjKr6j8DwL/VIx5GBgqihjsSDDIMam4pCgxuQSHYQXlLAwMzC+BrjPDa2gJ0D16zvm5BYlFIDkoqyS/CGYYE8wwBsYSBkZHOCuxAgCv6JFm/gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4t8U0sCCjKr6j8DwL/VIx5GBgqihjsSDDIMam4pCgxuQSHYQXlLAwMzC+BrjPDa2gJ0D16zvm5BYlFIDkoqyS/CGYYE8wwBuYSBkZHMIsRyEoE4ig4L7mQoY6BDc5NqgAAF2SlMBsBAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4t8U0sCCjKr6j8DwL/VIx5GBgqihjsSDDIMam4pCgxuQSHYQXlLAwMzC+BrjPDa2gJ0D16zvm5BYlFIDkoqyS/CGYYE8wwBqYSBkZHMIsByEoCsxiBLKcKAFgGlNoIAQAA");

        empty = TreeSetMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNULKUpNDU4t8U0sCCjKr6j8DwL/VIx5GBgqihjsSDDIMam4pCgxuQSHYQXlLAwMzC+BrtMCGpiol5OYl64XXFKUmZeu4pxYnOqZV5yaV5xZklmW6pyfW5BYlFiSX1TOHFMbE/D0HBPMACANAKosp/vkAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a"),
                                                     "H4sIAAAAAAAA/5XOMQ6CQBAF0EG08xhUFtvYWZgQKgsTEixpBjIha5ZdMjsCmngjz+ItLCy8ggqFdhb+6v/m5V8eMPMMK8eV2mOr6/ogWBhSpTOGStHOeuWJNRp9wnGqHRNlJFtsUnb98TXmGS3nAD3D+g8oLrwwlvIDa7opQHgf3i0GEJVBW6lMWNsqStDTxnqyXotuKXF1g4ziuAvzc57erpMPAIFAEH8b9m/H2hhl8gAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeSetMapTest::extraSerializationChecks, empty.insert("A", "a").insert("a", "b").insert("Z", "c"),
                                                     "H4sIAAAAAAAA/5WOMQrCQBREv0Y7j5HKYhs7C0GsLIRArMTmJ3zCymY3/v3GKOiJPIu3sLDwCuqqWFo4MDAzxWNON+h6hqHjQq2w1mW5EcwMqdwZQ7loZ73yxBqN3uOrqjkTpSQzrBJ2ze7x0j0e9AAahtEfoHHmhTGXH7Bq2wGIruFdPwBRGbSFSoW1LeIJeppaT9Zr0TVNXFkhozjeRsvDMrmc218AtAVa+ElrOEI31Cx48Z5aIeXNE4UH0BkFAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert("A", "a").insert("b", "B").delete("A", "a"),
                                                     "H4sIAAAAAAAA/5XOMQ6CQBCF4UG08xhUFtvYWZgglYUJiZY2A5mQNcsumR0BTbyRZ/EWFhZeQQUSSwun+qf58q5PmHiGheNCHbDWZXkUzAyp3BlDuWhnvfLEGo0+Y/+qHRNtSTZYpeza07u/VzSfArQMyz+gOPPCmMsPrGrGAOGjWzfrQFQGbaG2wtoWUYKe1taT9Vp0TYkrK2QUx024v+zT+230BWAkEMRDQVfZUEFXq/YD5C7/ZfwAAAA=");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeSetMap mapA = (TreeSetMap)a;
        TreeSetMap mapB = (TreeSetMap)b;
        assertEquals(mapA.getComparator(), mapB.getComparator());
        TreeMapTest.extraSerializationChecks(mapA.getMap(), mapB.getMap());
    }
}
