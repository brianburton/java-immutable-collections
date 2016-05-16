package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Uses a random number generator to create a series of size steps
 * containing a growth size target and a shrink size target.
 * Steps are randomly selected so that the final shrink size is
 * always the maxSize and the final growthSize is always somewhat larger.
 * Sizes are monotonically increasing.
 */
public class SizeStepCursor
{
    public static Cursor<Step> steps(int numSteps,
                                     int maxSize,
                                     Random r)
    {
        final int extra = maxSize / (numSteps * 3);
        final int numSizes = 2 * numSteps;
        final Set<Integer> sizes = new TreeSet<Integer>();
        sizes.add(maxSize);
        sizes.add(maxSize + 1 + r.nextInt(extra));
        while (sizes.size() < numSizes) {
            sizes.add(1 + r.nextInt(maxSize - extra));
        }
        JImmutableList<Step> steps = JImmutables.list();
        for (Iterator<Integer> i = sizes.iterator(); i.hasNext(); ) {
            final int shrinkSize = i.next();
            final int growthSize = i.next();
            steps = steps.insertLast(new Step(growthSize, shrinkSize));
        }
        return steps.cursor();
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
