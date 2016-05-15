package org.javimmutable.collections.StressTestTool;

import junit.framework.TestCase;

import java.util.Random;

public class SizeStepCursorTest
        extends TestCase
{
    public void testSteps()
    {
        Random r = new Random();
        SizeStepCursor.Step lastStep = null;
        for (SizeStepCursor.Step step : SizeStepCursor.steps(6, 600, r)) {
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
