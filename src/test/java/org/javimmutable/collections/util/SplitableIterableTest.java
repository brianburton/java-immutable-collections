package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;

import java.io.IOException;

public class SplitableIterableTest
    extends TestCase
{
    public void testIndexedForEach()
    {
        JImmutableList.Builder<Integer> collected = JImmutables.listBuilder();
        JImmutables.list(1, 2, 3).indexedForEach((i, v) -> collected.add(i).add(v));
        assertEquals(JImmutables.list(0, 1, 1, 2, 2, 3), collected.build());
    }

    public void testIndexedForEachThrows()
    {
        JImmutableList.Builder<Integer> collected = JImmutables.listBuilder();
        try {
            JImmutables.list(1, 2, 3).indexedForEachThrows((i, v) -> {
                if (i == 2) {
                    throw new IOException();
                }
                collected.add(i).add(v);
            });
            fail();
        } catch (IOException ex) {
            assertEquals(JImmutables.list(0, 1, 1, 2), collected.build());
        }
    }
}
