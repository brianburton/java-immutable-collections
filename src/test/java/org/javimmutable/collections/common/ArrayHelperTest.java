///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import junit.framework.TestCase;

public class ArrayHelperTest
        extends TestCase
{
    public void testSubArray()
    {
        Integer[] orig = {1, 2, 3};
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        verifyValues(ArrayHelper.subArray(alloc, orig, 0, 0));
        verifyValues(ArrayHelper.subArray(alloc, orig, 0, 1), 1);
        verifyValues(ArrayHelper.subArray(alloc, orig, 0, 2), 1, 2);
        verifyValues(ArrayHelper.subArray(alloc, orig, 0, 3), 1, 2, 3);

        verifyValues(ArrayHelper.subArray(alloc, orig, 1, 1));
        verifyValues(ArrayHelper.subArray(alloc, orig, 1, 2), 2);
        verifyValues(ArrayHelper.subArray(alloc, orig, 1, 3), 2, 3);

        verifyValues(ArrayHelper.subArray(alloc, orig, 2, 2));
        verifyValues(ArrayHelper.subArray(alloc, orig, 2, 3), 3);

        verifyValues(ArrayHelper.subArray(alloc, orig, 3, 3));
    }

    public void testSubArray2()
    {
        Integer[] a = {1, 2, 3};
        Integer[] b = {4, 5, 6};
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        verifyValues(ArrayHelper.subArray(alloc, a, b, 0, 0));
        verifyValues(ArrayHelper.subArray(alloc, a, b, 0, 1), 1);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 0, 2), 1, 2);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 0, 3), 1, 2, 3);

        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 1));
        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 2), 2);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 3), 2, 3);

        verifyValues(ArrayHelper.subArray(alloc, a, b, 3, 3));
        verifyValues(ArrayHelper.subArray(alloc, a, b, 3, 4), 4);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 3, 5), 4, 5);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 3, 6), 4, 5, 6);

        verifyValues(ArrayHelper.subArray(alloc, a, b, 4, 4));
        verifyValues(ArrayHelper.subArray(alloc, a, b, 4, 5), 5);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 4, 6), 5, 6);

        verifyValues(ArrayHelper.subArray(alloc, a, b, 2, 3), 3);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 2, 4), 3, 4);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 2, 5), 3, 4, 5);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 2, 6), 3, 4, 5, 6);

        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 3), 2, 3);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 4), 2, 3, 4);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 5), 2, 3, 4, 5);
        verifyValues(ArrayHelper.subArray(alloc, a, b, 1, 6), 2, 3, 4, 5, 6);

    }

    public void testAssign()
    {
        Integer[] orig = {1, 2, 3};

        verifyValues(ArrayHelper.assign(orig, 0, 9), 9, 2, 3);
        verifyValues(ArrayHelper.assign(orig, 1, 9), 1, 9, 3);
        verifyValues(ArrayHelper.assign(orig, 2, 9), 1, 2, 9);

        try {
            ArrayHelper.assign(orig, 3, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public void testAppend()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        verifyValues(ArrayHelper.append(alloc, new Integer[0], 9), 9);
        verifyValues(ArrayHelper.append(alloc, new Integer[]{1}, 9), 1, 9);
    }

    public void testInsert()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        verifyValues(ArrayHelper.insert(alloc, new Integer[0], 0, 9), 9);

        verifyValues(ArrayHelper.insert(alloc, new Integer[]{1}, 0, 9), 9, 1);
        verifyValues(ArrayHelper.insert(alloc, new Integer[]{1}, 1, 9), 1, 9);

        verifyValues(ArrayHelper.insert(alloc, new Integer[]{1, 2}, 0, 9), 9, 1, 2);
        verifyValues(ArrayHelper.insert(alloc, new Integer[]{1, 2}, 1, 9), 1, 9, 2);
        verifyValues(ArrayHelper.insert(alloc, new Integer[]{1, 2}, 2, 9), 1, 2, 9);
    }

    public void testDelete()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        // length 1
        Integer[] orig = {1};
        verifyValues(ArrayHelper.delete(alloc, orig, 0));

        // length 2
        orig = new Integer[]{1, 2};
        verifyValues(ArrayHelper.delete(alloc, orig, 0), 2);
        verifyValues(ArrayHelper.delete(alloc, orig, 1), 1);

        // length 3
        orig = new Integer[]{1, 2, 3};
        verifyValues(ArrayHelper.delete(alloc, orig, 0), 2, 3);
        verifyValues(ArrayHelper.delete(alloc, orig, 1), 1, 3);
        verifyValues(ArrayHelper.delete(alloc, orig, 2), 1, 2);
    }

    public void testConcat()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        verifyValues(ArrayHelper.concat(alloc, new Integer[]{}, new Integer[]{}));
        verifyValues(ArrayHelper.concat(alloc, new Integer[]{1}, new Integer[]{}), 1);
        verifyValues(ArrayHelper.concat(alloc, new Integer[]{}, new Integer[]{1}), 1);
        verifyValues(ArrayHelper.concat(alloc, new Integer[]{1}, new Integer[]{2}), 1, 2);
    }

    public void testAssignAppend()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        try {
            ArrayHelper.assignAppend(alloc, new Integer[0], 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        verifyValues(ArrayHelper.assignAppend(alloc, new Integer[]{1}, 9, 10), 9, 10);
        verifyValues(ArrayHelper.assignAppend(alloc, new Integer[]{1, 2}, 9, 10), 1, 9, 10);
    }

    public void testAssignTwo()
    {
        try {
            ArrayHelper.assignTwo(new Integer[]{}, 0, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            ArrayHelper.assignTwo(new Integer[]{1}, 0, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            ArrayHelper.assignTwo(new Integer[]{1, 2}, 1, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        verifyValues(ArrayHelper.assignTwo(new Integer[]{1, 2}, 0, 9, 10), 9, 10);
        verifyValues(ArrayHelper.assignTwo(new Integer[]{
                1, 2, 3
        }, 0, 9, 10), 9, 10, 3);
        verifyValues(ArrayHelper.assignTwo(new Integer[]{
                1, 2, 3
        }, 1, 9, 10), 1, 9, 10);
    }

    public void testAssignInsertNode()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        try {
            ArrayHelper.assignInsert(alloc, new Integer[]{}, 0, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            ArrayHelper.assignInsert(alloc, new Integer[]{1}, 1, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        verifyValues(ArrayHelper.assignInsert(alloc, new Integer[]{1}, 0, 9, 10), 9, 10);
        verifyValues(ArrayHelper.assignInsert(alloc, new Integer[]{
                1, 2
        }, 0, 9, 10), 9, 10, 2);
        verifyValues(ArrayHelper.assignInsert(alloc, new Integer[]{
                1, 2
        }, 1, 9, 10), 1, 9, 10);

        try {
            ArrayHelper.assignInsert(alloc, new Integer[]{1, 2}, 2, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public void testAssignDelete()
    {
        ArrayHelper.Allocator<Integer> alloc = ArrayHelper.allocator(Integer.class);

        try {
            ArrayHelper.assignDelete(alloc, new Integer[]{1}, 0, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        verifyValues(ArrayHelper.assignDelete(alloc, new Integer[]{1, 2}, 0, 9), 9);
        try {
            ArrayHelper.assignDelete(alloc, new Integer[]{1, 2}, 1, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        verifyValues(ArrayHelper.assignDelete(alloc, new Integer[]{1, 2, 3}, 0, 9), 9, 3);
        verifyValues(ArrayHelper.assignDelete(alloc, new Integer[]{1, 2, 3}, 1, 9), 1, 9);
        try {
            ArrayHelper.assignDelete(alloc, new Integer[]{1, 2, 3}, 2, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    private void verifyValues(Integer[] actual,
                              Integer... expected)
    {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; ++i) {
            assertEquals(expected[i], actual[i]);
        }
    }
}
