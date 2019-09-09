package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static Set<String> makeSet(String... args)
    {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, args);
        return set;
    }
}
