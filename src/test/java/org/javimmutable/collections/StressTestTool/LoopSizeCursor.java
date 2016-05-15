package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.cursors.StandardCursor;

import java.util.Random;

/**
 * Uses a random number generator to create a series of size steps
 * containing a growth size target and a shrink size target.
 */
public class LoopSizeCursor
{
    public static Cursor<Step> steps(int numSteps,
                                     int maxSize,
                                     Random r)
    {
        final int stepSize = maxSize / numSteps;
        final int randomVariation = Math.max(1, stepSize / 4);
        final Step[] steps = new Step[numSteps];
        for (int i = 1; i < numSteps; ++i) {
            int shrinkSize = stepSize * i + variation(r, randomVariation);
            int growthSize = shrinkSize + stepSize + variation(r, randomVariation);
            steps[i - 1] = new Step(growthSize, shrinkSize);
        }
        steps[numSteps - 1] = new Step(maxSize + stepSize + variation(r, randomVariation), maxSize);
        return StandardCursor.of(IndexedArray.retained(steps));
    }

    private static int variation(Random r,
                                 int randomVariation)
    {
        return randomVariation - r.nextInt(randomVariation);
    }

    public static class Step
    {
        private final int growthSize;
        private final int shrinkSize;

        private Step(int growthSize,
                     int shrinkSize)
        {
            this.growthSize = growthSize;
            this.shrinkSize = shrinkSize;
        }

        public int growthSize()
        {
            return growthSize;
        }

        public int shrinkSize()
        {
            return shrinkSize;
        }
    }
}
