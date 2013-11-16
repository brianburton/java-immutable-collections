///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections.array.bit32;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.cursors.StandardCursorTest;

public class StandardBit32ArrayTest
        extends TestCase
{
    public void test()
    {
        Bit32Array<Integer> test = new StandardBit32Array<Integer>();
        for (int i = 0; i < 32; ++i) {
            test = test.assign(i, i);
            assertEquals(Holders.of(i), test.get(i));
            for (int k = 0; k < i; ++k) {
                assertEquals(Holders.of(k), test.get(k));
            }
            assertEquals(i + 1, test.size());
        }
        // verify no copy is made for value identity not changing
        for (int i = 0; i < 32; ++i) {
            assertSame(test, test.assign(i, i));
        }
        for (int i = 0; i < 32; ++i) {
            test = test.delete(i);
            assertEquals(Holders.<Integer>of(), test.get(i));
            for (int k = i + 1; k < 32; ++k) {
                assertEquals(Holders.of(k), test.get(k));
            }
            assertEquals(32 - i - 1, test.size());
        }
    }

    public void testAllPossibleGrowing1to2()
    {
        for (int first = 0; first < 32; ++first) {
            for (int second = 0; second < 32; ++second) {
                if (first != second) {
                    Bit32Array<Integer> array = new StandardBit32Array<Integer>();
                    array = array.assign(first, first);
                    assertEquals(1, array.size());
                    assertEquals(Holders.of(first), array.get(first));
                    array = array.assign(second, second);
                    assertEquals(2, array.size());
                    assertEquals(Holders.of(first), array.get(first));
                    assertEquals(Holders.of(second), array.get(second));

                    // test deleting first first
                    Bit32Array<Integer> delArray = array.delete(first);
                    assertTrue(delArray instanceof SingleBit32Array);
                    assertEquals(1, delArray.size());
                    assertEquals(Holders.<Integer>of(), delArray.get(first));
                    assertEquals(Holders.of(second), delArray.get(second));
                    delArray = delArray.delete(second);
                    assertTrue(delArray instanceof EmptyBit32Array);
                    assertEquals(0, delArray.size());
                    assertEquals(Holders.<Integer>of(), delArray.get(first));
                    assertEquals(Holders.<Integer>of(), delArray.get(second));

                    // test deleting second first
                    delArray = array.delete(second);
                    assertTrue(delArray instanceof SingleBit32Array);
                    assertEquals(1, delArray.size());
                    assertEquals(Holders.of(first), delArray.get(first));
                    assertEquals(Holders.<Integer>of(), delArray.get(second));
                    delArray = delArray.delete(first);
                    assertTrue(delArray instanceof EmptyBit32Array);
                    assertEquals(0, delArray.size());
                    assertEquals(Holders.<Integer>of(), delArray.get(first));
                    assertEquals(Holders.<Integer>of(), delArray.get(second));
                }
            }
        }
    }

    public void testAllPossibleGrowingTo3()
    {
        for (int first = 0; first < 32; ++first) {
            for (int second = 0; second < 32; ++second) {
                for (int third = 0; third < 32; ++third) {
                    if (first != second && first != third && second != third) {
                        Bit32Array<Integer> array = new StandardBit32Array<Integer>();
                        array = array.assign(first, first);
                        array = array.assign(second, second);
                        array = array.assign(third, third);
                        assertTrue(array instanceof StandardBit32Array);
                        assertEquals(3, array.size());
                        assertEquals(Holders.of(first), array.get(first));
                        assertEquals(Holders.of(second), array.get(second));
                        assertEquals(Holders.of(third), array.get(third));

                        Bit32Array<Integer> newArray = array.delete(first);
                        assertTrue(newArray instanceof StandardBit32Array);
                        assertEquals(2, newArray.size());
                        assertEquals(Holders.<Integer>of(), newArray.get(first));
                        assertEquals(Holders.of(second), newArray.get(second));
                        assertEquals(Holders.of(third), newArray.get(third));

                        newArray = newArray.delete(second);
                        assertTrue(newArray instanceof SingleBit32Array);
                        assertEquals(1, newArray.size());
                        assertEquals(Holders.<Integer>of(), newArray.get(first));
                        assertEquals(Holders.<Integer>of(), newArray.get(second));
                        assertEquals(Holders.of(third), newArray.get(third));

                        newArray = newArray.delete(third);
                        assertTrue(newArray instanceof EmptyBit32Array);
                        assertEquals(0, newArray.size());
                        assertEquals(Holders.<Integer>of(), newArray.get(first));
                        assertEquals(Holders.<Integer>of(), newArray.get(second));
                        assertEquals(Holders.<Integer>of(), newArray.get(third));
                    }
                }
            }
        }
    }

    public void testDownsizing2to1()
    {
        Bit32Array<Integer> test = new StandardBit32Array<Integer>();
        test = test.assign(10, 10);
        test = test.assign(20, 20);
        assertEquals(2, test.size());
        assertTrue(test instanceof StandardBit32Array);

        test = test.delete(10);
        assertEquals(1, test.size());
        assertTrue(test instanceof SingleBit32Array);
        assertEquals(Holders.of(20), test.get(20));
    }

    public void testDownsizing1to0()
    {
        Bit32Array<Integer> test = new StandardBit32Array<Integer>();
        test = test.assign(20, 20);
        assertEquals(1, test.size());
        assertTrue(test instanceof StandardBit32Array);

        test = test.delete(20);
        assertEquals(0, test.size());
        assertTrue(test instanceof EmptyBit32Array);
        assertEquals(Holders.<Integer>of(), test.get(20));
    }

    public void testBoundsCheck()
    {
        Bit32Array<Integer> array = new StandardBit32Array<Integer>();
        for (int i = 0; i < 32; ++i) {
            array.get(i);
            array.assign(i, i);
            array.delete(i);
        }
        try {
            array.get(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            array.get(32);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    public void testFirstIndex()
    {
        Bit32Array<Integer> array = new StandardBit32Array<Integer>();
        for (int index = 31; index > 0; --index) {
            array = array.assign(index, index);
            assertEquals(index, (int)array.get(index).getValueOrNull());
            assertEquals(index, array.firstIndex());
        }
    }

    public void testCursor()
    {
        Bit32Array<Integer> array = new StandardBit32Array<Integer>();
        assertEquals(false, array.cursor().next().hasValue());
        StandardCursorTest.emptyCursorTest(array.cursor());

        for (int i = 0; i < 32; ++i) {
            array = array.assign(i, i);
            StandardCursorTest.cursorTest(new Bit32ArrayTest.Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Bit32ArrayTest.Lookup<Integer>(array), array.size(), array.iterator());
        }

        array = new StandardBit32Array<Integer>();
        for (int i = 1; i < 32; i += 5) {
            array = array.assign(i, i);
            StandardCursorTest.cursorTest(new Bit32ArrayTest.Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Bit32ArrayTest.Lookup<Integer>(array), array.size(), array.iterator());
        }

        int index = 1;
        Cursor<JImmutableMap.Entry<Integer, Integer>> cursor = array.cursor();
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            assertEquals(index, (int)cursor.getValue().getKey());
            assertEquals(index, (int)cursor.getValue().getValue());
            index += 5;
        }
    }

    public void testIndexedConstructor()
    {
        Integer[] values = new Integer[32];
        for (int i = 0; i < values.length; ++i) {
            values[i] = i;
        }
        IndexedArray<Integer> source = IndexedArray.unsafe(values);

        for (int offset = 0; offset < values.length; ++offset) {
            for (int limit = offset; limit <= values.length; ++limit) {
                final int size = limit - offset;
                StandardBit32Array<Integer> barray = new StandardBit32Array<Integer>(source, offset, limit);
                assertEquals(size, barray.size());
                for (int i = 0; i < size; ++i) {
                    assertEquals(Holders.of(values[offset + i]), barray.get(i));
                }
                for (int i = size; i < 32; ++i) {
                    assertEquals(Holders.<Integer>of(), barray.get(i));
                }
            }
        }
    }
}
