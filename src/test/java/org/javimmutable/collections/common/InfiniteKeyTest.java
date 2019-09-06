package org.javimmutable.collections.common;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.common.InfiniteKey.testKey;

public class InfiniteKeyTest
    extends TestCase
{
    private static final long MAX = 0x7fffffffffffffffL;

    @SuppressWarnings("NumericOverflow")
    public void testMax()
    {
        assertThat(MAX).isGreaterThan(0);
        assertThat(MAX + 1).isLessThan(0);
    }

    public void testCompareTo()
    {
        verifyNext(InfiniteKey.first());
        verifyNext(testKey(MAX - 500));
        verifyNext(testKey(0, MAX - 500));
        verifyNext(testKey(MAX, MAX - 500));
    }

    public void testToString()
    {
        assertThat(testKey(0).toString()).isEqualTo("0");
        assertThat(testKey(0, 1).toString()).isEqualTo("0.1");
        assertThat(testKey(0, 1, 2).toString()).isEqualTo("0.1.2");
    }

    public void testCache()
    {
        InfiniteKey key = InfiniteKey.first();
        for (int i = 0; i < InfiniteKey.CACHE_SIZE - 1; ++i) {
            InfiniteKey next = key.next();
            assertThat(next).isSameAs(key.next());
            key = next;
        }
    }

    private void verifyNext(InfiniteKey key)
    {
        for (int i = 1; i <= 1000; ++i) {
            InfiniteKey next = key.next();
            assertThat(key.compareTo(next)).isEqualTo(-1);
            assertThat(next.compareTo(key.next())).isEqualTo(0);
            assertThat(next.compareTo(key)).isEqualTo(1);
            assertThat(next.hashCode()).isEqualTo(key.next().hashCode());
            assertThat(key).isNotEqualTo(next);
            assertThat(next).isNotEqualTo(key);
            assertThat(key.next()).isEqualTo(next);
            key = next;
        }
    }
}
