package org.javimmutable.collections.common;

import org.javimmutable.collections.JImmutableList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class StandardJImmutableListTests
{
    public static void verifyList(@Nonnull JImmutableList<Integer> template)
    {
        testTransform(template);
    }

    private static void testTransform(@Nonnull JImmutableList<Integer> template)
    {
        JImmutableList<Integer> ints = template;
        JImmutableList<String> strings = template.transform(String::valueOf);
        List<String> expected = new ArrayList<>();
        verifyContents(strings, expected);
        for (int i = 1; i <= 100; ++i) {
            ints = ints.insert(i);
            strings = ints.transform(String::valueOf);
            assertEquals(ints.getClass(), strings.getClass());
            expected.add(String.valueOf(i));
            verifyContents(strings, expected);
        }
    }

    private static <T> void verifyContents(@Nonnull JImmutableList<T> values,
                                           @Nonnull List<T> expected)
    {
        assertEquals(expected.size(), values.size());
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), values.get(i));
        }
    }

}
