///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

import org.javimmutable.collections.IList;
import org.javimmutable.collections.ILists;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.ISets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class StressTestUtil
{
    static IList<String> loadTokens(Iterable<String> filenames)
        throws IOException
    {
        ISet<String> tokens = ISets.hashed();
        for (String filename : filenames) {
            tokens = addTokensFromFile(tokens, filename);
        }
        return ILists.allOf(tokens);
    }

    static IList<String> loadTokens(String filename)
        throws IOException
    {
        ISet<String> tokens = ISets.hashed();
        tokens = addTokensFromFile(tokens, filename);
        return ILists.allOf(tokens);
    }

    static ISet<String> addTokensFromFile(ISet<String> tokens,
                                          String filename)
        throws IOException
    {
        BufferedReader inp = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        try {
            for (String line = inp.readLine(); line != null; line = inp.readLine()) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                while (tokenizer.hasMoreTokens()) {
                    tokens = tokens.insert(tokenizer.nextToken());
                }
            }
        } finally {
            inp.close();
        }
        return tokens;
    }
}
