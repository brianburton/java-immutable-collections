package org.javimmutable.collections.StressTestTool;

import java.util.Random;

public abstract class ArrayIndexRange
{
    public static final ArrayIndexRange BIT32 = new PositiveIndexRange(31);
    public static final ArrayIndexRange INTEGER = new AllIntIndexRange();

    private ArrayIndexRange()
    {
    }

    public abstract int maxSize();

    public abstract int randomIndex(Random random);

    private static class PositiveIndexRange
            extends ArrayIndexRange
    {
        private final int maxIndex;

        private PositiveIndexRange(int maxIndex)
        {
            this.maxIndex = maxIndex;
        }

        @Override
        public int maxSize()
        {
            return maxIndex + 1;
        }

        @Override
        public int randomIndex(Random random)
        {
            return random.nextInt(maxIndex + 1);
        }
    }

    private static class AllIntIndexRange
            extends ArrayIndexRange
    {
        private AllIntIndexRange()
        {
        }

        @Override
        public int maxSize()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public int randomIndex(Random random)
        {
            int base = random.nextInt(Integer.MAX_VALUE);
            return random.nextBoolean() ? base : -base;
        }
    }
}
