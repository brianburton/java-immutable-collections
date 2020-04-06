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

package org.javimmutable.collections.common;

import junit.framework.TestCase;

import java.util.concurrent.Callable;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.common.ArrayHelper.*;

public class ArrayHelperTest
    extends TestCase
{
    private final Allocator<Integer> alloc = allocator(Integer.class);

    public void testSubArray()
    {
        Integer[] orig = {1, 2, 3};

        verifyValues(subArray(alloc, orig, 0, 0));
        verifyValues(subArray(alloc, orig, 0, 1), 1);
        verifyValues(subArray(alloc, orig, 0, 2), 1, 2);
        verifyValues(subArray(alloc, orig, 0, 3), 1, 2, 3);

        verifyValues(subArray(alloc, orig, 1, 1));
        verifyValues(subArray(alloc, orig, 1, 2), 2);
        verifyValues(subArray(alloc, orig, 1, 3), 2, 3);

        verifyValues(subArray(alloc, orig, 2, 2));
        verifyValues(subArray(alloc, orig, 2, 3), 3);

        verifyValues(subArray(alloc, orig, 3, 3));
    }

    public void testSubArray2()
    {
        Integer[] a = {1, 2, 3};
        Integer[] b = {4, 5, 6};

        verifyValues(subArray(alloc, a, b, 0, 0));
        verifyValues(subArray(alloc, a, b, 0, 1), 1);
        verifyValues(subArray(alloc, a, b, 0, 2), 1, 2);
        verifyValues(subArray(alloc, a, b, 0, 3), 1, 2, 3);

        verifyValues(subArray(alloc, a, b, 1, 1));
        verifyValues(subArray(alloc, a, b, 1, 2), 2);
        verifyValues(subArray(alloc, a, b, 1, 3), 2, 3);

        verifyValues(subArray(alloc, a, b, 3, 3));
        verifyValues(subArray(alloc, a, b, 3, 4), 4);
        verifyValues(subArray(alloc, a, b, 3, 5), 4, 5);
        verifyValues(subArray(alloc, a, b, 3, 6), 4, 5, 6);

        verifyValues(subArray(alloc, a, b, 4, 4));
        verifyValues(subArray(alloc, a, b, 4, 5), 5);
        verifyValues(subArray(alloc, a, b, 4, 6), 5, 6);

        verifyValues(subArray(alloc, a, b, 2, 3), 3);
        verifyValues(subArray(alloc, a, b, 2, 4), 3, 4);
        verifyValues(subArray(alloc, a, b, 2, 5), 3, 4, 5);
        verifyValues(subArray(alloc, a, b, 2, 6), 3, 4, 5, 6);

        verifyValues(subArray(alloc, a, b, 1, 3), 2, 3);
        verifyValues(subArray(alloc, a, b, 1, 4), 2, 3, 4);
        verifyValues(subArray(alloc, a, b, 1, 5), 2, 3, 4, 5);
        verifyValues(subArray(alloc, a, b, 1, 6), 2, 3, 4, 5, 6);

    }

    public void testAssign()
    {
        Integer[] orig = {1, 2, 3};

        verifyValues(assign(orig, 0, 9), 9, 2, 3);
        verifyValues(assign(orig, 1, 9), 1, 9, 3);
        verifyValues(assign(orig, 2, 9), 1, 2, 9);

        try {
            assign(orig, 3, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public void testAppend()
    {
        verifyValues(append(alloc, new Integer[0], 9), 9);
        verifyValues(append(alloc, new Integer[]{1}, 9), 1, 9);
    }

    public void testInsert()
    {
        verifyValues(insert(alloc, new Integer[0], 0, 9), 9);

        verifyValues(insert(alloc, new Integer[]{1}, 0, 9), 9, 1);
        verifyValues(insert(alloc, new Integer[]{1}, 1, 9), 1, 9);

        verifyValues(insert(alloc, new Integer[]{1, 2}, 0, 9), 9, 1, 2);
        verifyValues(insert(alloc, new Integer[]{1, 2}, 1, 9), 1, 9, 2);
        verifyValues(insert(alloc, new Integer[]{1, 2}, 2, 9), 1, 2, 9);
    }

    public void testDelete()
    {
        // length 1
        Integer[] orig = {1};
        verifyValues(ArrayHelper.<Integer>delete(alloc, orig, 0));

        // length 2
        orig = new Integer[]{1, 2};
        verifyValues(delete(alloc, orig, 0), 2);
        verifyValues(delete(alloc, orig, 1), 1);

        // length 3
        orig = new Integer[]{1, 2, 3};
        verifyValues(delete(alloc, orig, 0), 2, 3);
        verifyValues(delete(alloc, orig, 1), 1, 3);
        verifyValues(delete(alloc, orig, 2), 1, 2);
    }

    public void testConcat()
    {
        verifyValues(concat(alloc, new Integer[]{}, new Integer[]{}));
        verifyValues(concat(alloc, new Integer[]{1}, new Integer[]{}), 1);
        verifyValues(concat(alloc, new Integer[]{}, new Integer[]{1}), 1);
        verifyValues(concat(alloc, new Integer[]{1}, new Integer[]{2}), 1, 2);
    }

    public void testAssignAppend()
    {
        try {
            assignAppend(alloc, new Integer[0], 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        verifyValues(assignAppend(alloc, new Integer[]{1}, 9, 10), 9, 10);
        verifyValues(assignAppend(alloc, new Integer[]{1, 2}, 9, 10), 1, 9, 10);
    }

    public void testAssignTwo()
    {
        try {
            assignTwo(new Integer[]{}, 0, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            assignTwo(new Integer[]{1}, 0, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            assignTwo(new Integer[]{1, 2}, 1, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        verifyValues(assignTwo(new Integer[]{1, 2}, 0, 9, 10), 9, 10);
        verifyValues(assignTwo(new Integer[]{
            1, 2, 3
        }, 0, 9, 10), 9, 10, 3);
        verifyValues(assignTwo(new Integer[]{
            1, 2, 3
        }, 1, 9, 10), 1, 9, 10);
    }

    public void testAssignInsertNode()
    {
        try {
            assignInsert(alloc, new Integer[]{}, 0, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            assignInsert(alloc, new Integer[]{1}, 1, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        verifyValues(assignInsert(alloc, new Integer[]{1}, 0, 9, 10), 9, 10);
        verifyValues(assignInsert(alloc, new Integer[]{
            1, 2
        }, 0, 9, 10), 9, 10, 2);
        verifyValues(assignInsert(alloc, new Integer[]{
            1, 2
        }, 1, 9, 10), 1, 9, 10);

        try {
            assignInsert(alloc, new Integer[]{1, 2}, 2, 9, 10);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public void testAssignDelete()
    {
        try {
            assignDelete(alloc, new Integer[]{1}, 0, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        verifyValues(assignDelete(alloc, new Integer[]{1, 2}, 0, 9), 9);
        try {
            assignDelete(alloc, new Integer[]{1, 2}, 1, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        verifyValues(assignDelete(alloc, new Integer[]{1, 2, 3}, 0, 9), 9, 3);
        verifyValues(assignDelete(alloc, new Integer[]{1, 2, 3}, 1, 9), 1, 9);
        try {
            assignDelete(alloc, new Integer[]{1, 2, 3}, 2, 9);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public void testPrefix()
    {
        verifyValues(a(), prefix(alloc, a(), 0));
        verifyValues(a(), prefix(alloc, a(1), 0));
        verifyValues(a(), prefix(alloc, a(1, 2), 0));
        verifyValues(a(1), prefix(alloc, a(1, 2), 1));
        verifyValues(a(1, 2), prefix(alloc, a(1, 2), 2));
        verifyOutOfBounds(() -> prefix(alloc, a(), 1));
        verifyOutOfBounds(() -> prefix(alloc, a(1), 2));
    }

    public void testPrefixInsert()
    {
        verifyValues(a(9), prefixInsert(alloc, a(), 0, 0, 9));
        verifyValues(a(9), prefixInsert(alloc, a(1), 0, 0, 9));
        verifyValues(a(9, 1), prefixInsert(alloc, a(1), 1, 0, 9));
        verifyValues(a(1, 9), prefixInsert(alloc, a(1), 1, 1, 9));
        verifyValues(a(9), prefixInsert(alloc, a(1, 2), 0, 0, 9));
        verifyValues(a(9, 1), prefixInsert(alloc, a(1, 2), 1, 0, 9));
        verifyValues(a(1, 9), prefixInsert(alloc, a(1, 2), 1, 1, 9));
        verifyValues(a(1, 2, 9), prefixInsert(alloc, a(1, 2), 2, 2, 9));
        verifyOutOfBounds(() -> prefixInsert(alloc, a(), 1, 0, 9));
        verifyOutOfBounds(() -> prefixInsert(alloc, a(1), 2, 0, 9));
        verifyOutOfBounds(() -> prefixInsert(alloc, a(1), 1, 2, 9));
    }

    public void testSuffix()
    {
        verifyValues(a(), suffix(alloc, a(), 0));
        verifyValues(a(), suffix(alloc, a(1), 1));
        verifyValues(a(), suffix(alloc, a(1, 2), 2));
        verifyValues(a(2), suffix(alloc, a(1, 2), 1));
        verifyValues(a(1, 2), suffix(alloc, a(1, 2), 0));
        verifyValues(a(1, 2, 3), suffix(alloc, a(1, 2, 3), 0));
        verifyValues(a(2, 3), suffix(alloc, a(1, 2, 3), 1));
        verifyValues(a(3), suffix(alloc, a(1, 2, 3), 2));
        verifyValues(a(), suffix(alloc, a(1, 2, 3), 3));
        verifyOutOfBounds(() -> suffix(alloc, a(), 1));
        verifyOutOfBounds(() -> suffix(alloc, a(1), 2));
        verifyOutOfBounds(() -> suffix(alloc, a(1, 2), 3));
    }

    public void testSuffixInsert()
    {
        verifyValues(a(9), suffixInsert(alloc, a(), 0, 0, 9));
        verifyValues(a(9), suffixInsert(alloc, a(1), 1, 1, 9));
        verifyValues(a(9, 1), suffixInsert(alloc, a(1), 0, 0, 9));
        verifyValues(a(1, 9), suffixInsert(alloc, a(1), 0, 1, 9));
        verifyValues(a(9), suffixInsert(alloc, a(1, 2), 2, 2, 9));
        verifyValues(a(2, 9), suffixInsert(alloc, a(1, 2), 1, 2, 9));
        verifyValues(a(9, 2), suffixInsert(alloc, a(1, 2), 1, 1, 9));
        verifyValues(a(9, 1, 2), suffixInsert(alloc, a(1, 2), 0, 0, 9));
        verifyValues(a(1, 9, 2), suffixInsert(alloc, a(1, 2), 0, 1, 9));
        verifyValues(a(1, 2, 9), suffixInsert(alloc, a(1, 2), 0, 2, 9));
        verifyOutOfBounds(() -> suffixInsert(alloc, a(), 1, 1, 9));
        verifyOutOfBounds(() -> suffixInsert(alloc, a(1), 2, 1, 9));
        verifyOutOfBounds(() -> suffixInsert(alloc, a(1, 2), 1, 0, 9));
    }

    public void testReverse()
    {
        verifyValues(reverse(alloc, new Integer[0]));
        verifyValues(reverse(alloc, new Integer[]{1}), 1);
        verifyValues(reverse(alloc, new Integer[]{1, 2}), 2, 1);
        verifyValues(reverse(alloc, new Integer[]{1, 2, 3}), 3, 2, 1);
        verifyValues(reverse(alloc, new Integer[]{1, 2, 3, 4}), 4, 3, 2, 1);
        verifyValues(reverse(alloc, new Integer[]{1, 2, 3, 4, 5}), 5, 4, 3, 2, 1);
        verifyValues(reverse(alloc, new Integer[]{1, 2, 3, 4, 5, 6}), 6, 5, 4, 3, 2, 1);
    }

    private void verifyOutOfBounds(Callable<Integer[]> op)
    {
        try {
            op.call();
            fail();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void verifyValues(Integer[] actual,
                              Integer... expected)
    {
        assertEquals(asList(expected), asList(actual));
    }

    private Integer[] a(Integer... values)
    {
        return values;
    }
}
