package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TestUtil
{
    public static <T> List<T> makeList(@Nonnull Iterable<T> src)
    {
        final List<T> dst = new ArrayList<>();
        for (T value : src) {
            dst.add(value);
        }
        return dst;
    }
}
