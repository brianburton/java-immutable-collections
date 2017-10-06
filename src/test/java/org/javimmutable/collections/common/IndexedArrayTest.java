package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;

import java.util.Arrays;

public class IndexedArrayTest
    extends TestCase
{
    public void test()
    {
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
