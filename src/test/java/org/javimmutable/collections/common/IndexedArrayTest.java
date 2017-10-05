package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;

import java.util.Arrays;

public class IndexedArrayTest
    extends TestCase
{
    public void test()
    {
        verifyEquals(IndexedArray.<Integer>of(), list());
        verifyEquals(IndexedArray.of(1), list(1));
        verifyEquals(IndexedArray.of(1, 2), list(1, 2));
        verifyEquals(IndexedArray.of(1, 2, 3), list(1, 2, 3));
        verifyEquals(IndexedArray.of(1, 2, 3, 4), list(1, 2, 3, 4));
        verifyEquals(IndexedArray.of(1, 2, 3, 4, 5), list(1, 2, 3, 4, 5));
        verifyEquals(IndexedArray.of(1, 2, 3, 4, 5, 6), list(1, 2, 3, 4, 5, 6));
        verifyEquals(IndexedArray.retained(new Integer[]{7, 8, 9}), list(7, 8, 9));
        verifyEquals(IndexedArray.copied(new Integer[]{7, 8, 9}), list(7, 8, 9));
    }

    private Indexed<Integer> list(Integer... values)
    {
        return IndexedList.retained(Arrays.asList(values));
    }

    private void verifyEquals(Indexed<Integer> expected,
                              Indexed<Integer> actual)
    {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
