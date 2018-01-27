package org.javimmutable.collections.common;

import junit.framework.Assert;
import org.javimmutable.collections.JImmutableList;

public class StandardJImmutableListTests
{
    public static void standardTests(JImmutableList<Integer> empty)
    {
        verifyInsertAllFirst(empty);
        verifyInsertAllLast(empty);
    }

    public static void verifyInsertAllFirst(JImmutableList<Integer> empty)
    {
        JImmutableList<Integer> actual = appendAll(empty, 1, 10);
        JImmutableList<Integer> expected = actual;
        while (actual.size() < 250_000) {
            final int addSize = actual.size() / 5;
            final int first = actual.size() + 1;
            final int last = actual.size() + addSize;
            actual = actual.insertAllFirst(appendAll(empty, first, last));
            expected = prependAll(expected, first, last);
            Assert.assertEquals(expected, actual);
        }
    }

    public static void verifyInsertAllLast(JImmutableList<Integer> empty)
    {
        JImmutableList<Integer> actual = appendAll(empty, 1, 10);
        JImmutableList<Integer> expected = actual;
        while (actual.size() < 250_000) {
            final int addSize = actual.size() / 5;
            final int first = actual.size() + 1;
            final int last = actual.size() + addSize;
            actual = actual.insertAllLast(appendAll(empty, first, last));
            expected = appendAll(expected, first, last);
            Assert.assertEquals(expected, actual);
        }
    }

    private static JImmutableList<Integer> appendAll(JImmutableList<Integer> answer,
                                                     int first,
                                                     int last)
    {
        for (int i = first; i <= last; ++i) {
            answer = answer.insert(i);
        }
        return answer;
    }

    private static JImmutableList<Integer> prependAll(JImmutableList<Integer> answer,
                                                      int first,
                                                      int last)
    {
        for (int i = last; i >= first; --i) {
            answer = answer.insertFirst(i);
        }
        return answer;
    }
}
