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

package org.javimmutable.collections.array.bit32;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bit32ArrayTest
        extends TestCase
{
    public void test()
    {
        Bit32Array<Integer> array = Bit32Array.of();
        assertEquals(0, array.size());
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.<Integer>of(), array.find(i));
        }
        assertEquals(0, array.size());
        StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
        StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        for (int i = 0; i < 32; ++i) {
            array = array.assign(i, i);
            assertEquals(i + 1, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.of(i), array.find(i));
            assertEquals(32, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
        for (int i = 0; i < 32; ++i) {
            array = array.assign(i, i + i);
            assertEquals(32, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.of(i + i), array.find(i));
            assertEquals(32, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
        for (int i = 0; i < 32; ++i) {
            array = array.delete(i);
            assertEquals(32 - i - 1, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.<Integer>of(), array.find(i));
            assertEquals(0, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
        StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
    }

    public void testRandomOrder()
    {
        Random random = new Random(100L);
        Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
        for (int loop = 1; loop < 10000; ++loop) {
            expected.clear();
            Bit32Array<Integer> array = Bit32Array.of();
            for (int i = 0; i < 200; ++i) {
                int command = random.nextInt(6);
                int index = random.nextInt(31);
                if (command < 3) {
                    int value = random.nextInt(1000);
                    array = array.assign(index, value);
                    expected.put(index, value);
                } else if (command < 6) {
                    array = array.delete(index);
                    expected.remove(index);
                } else {
                    assertEquals(Holders.fromNullable(expected.get(index)), array.find(index));
                }
                assertEquals(expected.size(), array.size());
            }
            for (int index = 0; index < 32; ++index) {
                assertEquals(Holders.fromNullable(expected.get(index)), array.find(index));
            }
            int count = 0;
            for (JImmutableMap.Entry<Integer, Integer> entry : array) {
                assertEquals(expected.get(entry.getKey()), entry.getValue());
                count += 1;
            }
            assertEquals(count, array.size());
            StandardCursorTest.cursorTest(new Lookup<Integer>(array), array.size(), array.cursor());
            StandardCursorTest.iteratorTest(new Lookup<Integer>(array), array.size(), array.iterator());
        }
    }

    public void testIndexedConstructor()
    {
        Integer[] values = new Integer[32];
        for (int i = 0; i < values.length; ++i) {
            values[i] = i;
        }
        IndexedArray<Integer> source = IndexedArray.retained(values);

        for (int offset = 0; offset < values.length; ++offset) {
            for (int limit = offset; limit <= values.length; ++limit) {
                final int size = limit - offset;
                Bit32Array<Integer> barray = Bit32Array.of(source, offset, limit);
                assertEquals(size, barray.size());
                for (int i = 0; i < size; ++i) {
                    assertEquals(Holders.of(values[offset + i]), barray.find(i));
                }
                for (int i = size; i < 32; ++i) {
                    assertEquals(Holders.<Integer>of(), barray.find(i));
                }
            }
        }
    }

    static class Lookup<T>
            implements Func1<Integer, JImmutableMap.Entry<Integer, T>>
    {
        private final Bit32Array<T> array;

        Lookup(Bit32Array<T> array)
        {
            this.array = array;
        }

        @Override
        public JImmutableMap.Entry<Integer, T> apply(Integer value)
        {
            for (int i = 0; i < 32; ++i) {
                Holder<T> holder = array.find(i);
                if (holder.isFilled()) {
                    if (value == 0) {
                        return MapEntry.of(i, holder.getValue());
                    } else {
                        value -= 1;
                    }
                }
            }
            throw new IndexOutOfBoundsException();
        }
    }
}
