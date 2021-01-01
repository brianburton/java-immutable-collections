///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.stress_test;

import junit.framework.TestCase;

import java.util.Random;

public class SizeStepListFactoryTest
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
        SizeStepListFactory.Step lastStep = null;
        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(requestedNumSteps, maxSize, r)) {
            stepCount += 1;
//            System.out.printf("step %d: grow %d  shrink %d%n", stepCount, step.growthSize(), step.shrinkSize());
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
        SizeStepListFactory.Step lastStep = null;
        for (SizeStepListFactory.Step step : SizeStepListFactory.steps(requestedNumSteps, maxSize, r)) {
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
