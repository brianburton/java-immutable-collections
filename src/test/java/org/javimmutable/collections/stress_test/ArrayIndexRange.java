///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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
