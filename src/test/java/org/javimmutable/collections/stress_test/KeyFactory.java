///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableList;

import java.util.Random;

import static org.javimmutable.collections.stress_test.KeyWrapper.*;

public abstract class KeyFactory<T>
{
    abstract T newKey(JImmutableList<String> tokens,
                      Random random);

    abstract T makeKey(String value);

    protected String makeKey(JImmutableList<String> tokens,
                             Random random)
    {
        int length = 1 + random.nextInt(250);
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(tokens.get(random.nextInt(tokens.size())));
        }
        return sb.toString();
    }

    static class RegularKeyFactory
            extends KeyFactory<RegularKey<String>>
    {
        @Override
        public RegularKey<String> newKey(JImmutableList<String> tokens,
                                         Random random)
        {
            return new RegularKey<String>(makeKey(tokens, random));
        }

        @Override
        public RegularKey<String> makeKey(String value)
        {
            return new RegularKey<String>(value);
        }
    }

    static class ComparableRegularKeyFactory
            extends KeyFactory<ComparableRegularKey<String>>
    {
        @Override
        public ComparableRegularKey<String> newKey(JImmutableList<String> tokens,
                                                   Random random)
        {
            return new ComparableRegularKey<String>(makeKey(tokens, random));
        }

        @Override
        public ComparableRegularKey<String> makeKey(String value)
        {
            return new ComparableRegularKey<String>(value);
        }

    }

    static class BadHashKeyFactory
            extends KeyFactory<BadHashKey<String>>
    {
        @Override
        public BadHashKey<String> newKey(JImmutableList<String> tokens,
                                         Random random)
        {
            return new BadHashKey<String>(makeKey(tokens, random));
        }

        @Override
        public BadHashKey<String> makeKey(String value)
        {
            return new BadHashKey<String>(value);
        }
    }

    static class ComparableBadHashKeyFactory
            extends KeyFactory<ComparableBadHashKey<String>>
    {
        @Override
        public ComparableBadHashKey<String> newKey(JImmutableList<String> tokens,
                                                   Random random)
        {
            return new ComparableBadHashKey<String>(makeKey(tokens, random));
        }

        @Override
        public ComparableBadHashKey<String> makeKey(String value)
        {
            return new ComparableBadHashKey<String>(value);
        }
    }
}