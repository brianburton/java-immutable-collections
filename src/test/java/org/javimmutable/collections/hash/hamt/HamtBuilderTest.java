package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;

import java.util.Random;

public class HamtBuilderTest
    extends TestCase
{
    public void testRandom()
    {
        final Random r = new Random(1032946);
        for (int i = 1; i <= 5000; ++i) {
            HamtBuilder<Integer, Integer> builder = new HamtBuilder<>();
            final int size = 1 + r.nextInt(1000);
            for (int k = 1; k <= size; ++k) {
                final Integer key = r.nextInt(5 * size);
                final Integer value = r.nextInt();
                builder.add(key, value);
            }
            builder.build().checkInvariants(builder.getCollisionMap());
        }
    }
}
