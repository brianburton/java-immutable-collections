package org.javimmutable.collections.StressTestTool;

import junit.framework.TestCase;

import java.util.Random;

public class SizeStepCursorTest
        extends TestCase
{
    public void testSteps()
    {
        Random r = new Random();
        verifyTinySteps(6, 1, 1, r);
        verifyTinySteps(6, 3, 2, r);
        verifyTinySteps(6, 3, 3, r);
        verifySteps(6, 2, 4, r);
        verifySteps(6, 2, 5, r);
        verifySteps(6, 3, 6, r);
        verifySteps(6, 3, 7, r);
        verifySteps(6, 4, 8, r);
        verifySteps(6, 4, 9, r);
        verifySteps(6, 5, 10, r);
        verifySteps(6, 5, 11, r);
        verifySteps(6, 6, 12, r);
        for (int size = 13; size <= 32; ++size) {
            verifySteps(6, 6, size, r);
        }
        for (int size = 33; size <= 1200; size += 100) {
            verifySteps(6, 6, size, r);
        }
    }

    private void verifySteps(int requestedNumSteps,
                             int expectedNumSteps,
                             int maxSize,
                             Random r)
    {
        int stepCount = 0;
        SizeStepCursor.Step lastStep = null;
        for (SizeStepCursor.Step step : SizeStepCursor.steps(requestedNumSteps, maxSize, r)) {
            stepCount += 1;
            System.out.printf("step %d: grow %d  shrink %d%n", stepCount, step.growthSize(), step.shrinkSize());
            assertTrue(step.growthSize() <= maxSize);
            assertTrue(step.shrinkSize() <= maxSize);
            if (stepCount == expectedNumSteps) {
                assertTrue(step.growthSize() == step.shrinkSize());
            } else {
                assertTrue(step.growthSize() > step.shrinkSize());
            }
            if (lastStep != null) {
                assertTrue(lastStep.shrinkSize() < step.shrinkSize());
            }
            lastStep = step;
        }
        assertNotNull(lastStep);
        assertEquals(maxSize, lastStep.growthSize());
        assertEquals(expectedNumSteps, stepCount);
    }

    private void verifyTinySteps(int requestedNumSteps,
                             int expectedNumSteps,
                             int maxSize,
                             Random r)
    {
        int stepCount = 0;
        SizeStepCursor.Step lastStep = null;
        for (SizeStepCursor.Step step : SizeStepCursor.steps(requestedNumSteps, maxSize, r)) {
//            System.out.printf("grow %d  shrink %d%n", step.growthSize(), step.shrinkSize());
            assertTrue(step.growthSize() <= maxSize);
            assertTrue(step.shrinkSize() <= maxSize);
            assertTrue(step.growthSize() >= step.shrinkSize());
            if (lastStep != null) {
                assertTrue(lastStep.shrinkSize() <= step.shrinkSize());
            }
            lastStep = step;
            stepCount += 1;
        }
        assertNotNull(lastStep);
        assertEquals(maxSize, lastStep.growthSize());
        assertEquals(expectedNumSteps, stepCount);
    }
}
