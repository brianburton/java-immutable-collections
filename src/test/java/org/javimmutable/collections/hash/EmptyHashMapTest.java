package org.javimmutable.collections.hash;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;

public class EmptyHashMapTest
        extends TestCase
{
    public void testAssign()
    {
        JImmutableMap<String, Integer> comparableMap = EmptyHashMap.of();
        comparableMap = comparableMap.assign("a", 100);
        assertTrue(comparableMap instanceof JImmutableHashMap);
        assertSame(JImmutableHashMap.COMPARABLE_TRANSFORMS, ((JImmutableHashMap)comparableMap).getTransforms());

        JImmutableMap<TimingLoop, Integer> otherMap = EmptyHashMap.of();
        otherMap = otherMap.assign(new TimingLoop(), 100);
        assertTrue(otherMap instanceof JImmutableHashMap);
        assertSame(JImmutableHashMap.TRANSFORMS, ((JImmutableHashMap)otherMap).getTransforms());

        try {
            otherMap = EmptyHashMap.of();
            otherMap.assign(null, 100);
            fail();
        } catch (NullPointerException ex) {
            // pass
        }
    }
}
