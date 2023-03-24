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

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.util.JImmutables;

/**
 * Uses a random number generator to create a series of size steps
 * containing a growth size target and a shrink size target.
 * Steps are randomly selected so that the final shrink size is
 * always the maxSize and the final growthSize is always somewhat larger.
 * Sizes are monotonically increasing.
 */
public class SizeStepListFactory
{
    public static Iterable<Step> steps(int numSteps,
                                       int maxSize,
                                       Random r)
    {
        IList<Step> steps;
        if (maxSize == 1) {
            steps = JImmutables.list(new Step(1, 1));
        } else if (maxSize == 2) {
            steps = JImmutables.list(new Step(1, 1),
                                     new Step(2, 1),
                                     new Step(2, 2));
        } else if (maxSize == 3) {
            steps = JImmutables.list(new Step(1, 1),
                                     new Step(3, 2),
                                     new Step(3, 3));
        } else if (maxSize < (2 * numSteps)) {
            steps = randomSteps(maxSize / 2, maxSize, r);
        } else {
            steps = randomSteps(numSteps, maxSize, r);
        }
        return steps;
    }

    private static IList<Step> randomSteps(int numSteps,
                                           int maxSize,
                                           Random r)
    {
        final int extra = Math.max(1, maxSize / (numSteps * 3));
        final int numSizes = 2 * (numSteps - 1);
        final Set<Integer> sizes = new TreeSet<>();
        while (sizes.size() < numSizes) {
            sizes.add(1 + r.nextInt(maxSize - extra));
        }
        IList<Step> steps = JImmutables.list();
        for (Iterator<Integer> i = sizes.iterator(); i.hasNext(); ) {
            final int shrinkSize = i.next();
            final int growthSize = i.next();
            steps = steps.insertLast(new Step(growthSize, shrinkSize));
        }
        steps = steps.insertLast(new Step(maxSize, maxSize));
        return steps;
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
