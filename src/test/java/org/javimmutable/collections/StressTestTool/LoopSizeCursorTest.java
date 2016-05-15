package org.javimmutable.collections.StressTestTool;

import junit.framework.TestCase;

import java.util.Random;

public class LoopSizeCursorTest
        extends TestCase
{
    public void testSteps()
    {
        Random r = new Random();
        LoopSizeCursor.Step lastStep = null;
        for (LoopSizeCursor.Step step : LoopSizeCursor.steps(6, 600, r)) {
            System.out.printf("grow %d  shrink %d%n", step.growthSize(), step.shrinkSize());
            assertTrue(step.growthSize() > step.shrinkSize());
            if (lastStep != null) {
                assertTrue(lastStep.shrinkSize() < step.shrinkSize());
            }
            lastStep = step;
        }
        assertNotNull(lastStep);
        assertEquals(600, lastStep.shrinkSize());
    }
}
