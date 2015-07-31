///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.StressTestTool;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.tree_list.JImmutableTreeList;
import org.javimmutable.collections.util.JImmutables;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Test program to run an infinite loop feeding data to every implementation of every
 * JImmutable collection type, querying the data, and deleting the data to verify
 * the collection always contains what it should.
 */
public class StressTestLoop
{

    public static void main(String[] argv)
            throws Exception
    {
        new StressTestLoop().execute(argv);
    }

    public void execute(String[] args)
            throws Exception
    {
        JImmutableList<AbstractStressTestable> testers = JImmutables.<AbstractStressTestable>list()
                .insert(new JImmutableListStressTester(JImmutables.<String>list()))
                .insert(new JImmutableListStressTester(JImmutables.<String>ralist()))
                .insert(new JImmutableListStressTester(JImmutableTreeList.<String>of()))

                .insert(new JImmutableRandomAccessListStressTester(JImmutables.<String>ralist()))
                .insert(new JImmutableRandomAccessListStressTester(JImmutableTreeList.<String>of()))

                .insert(new JImmutableSetStressTester(JImmutables.<String>set(), HashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>insertOrderSet(), LinkedHashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>sortedSet(), TreeSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>multiset(), HashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>insertOrderMultiset(), LinkedHashSet.class))
                .insert(new JImmutableSetStressTester(JImmutables.<String>sortedMultiset(), TreeSet.class))

                .insert(new JImmutableMultisetStressTester(JImmutables.<String>multiset()))
                .insert(new JImmutableMultisetStressTester(JImmutables.<String>insertOrderMultiset()))
                .insert(new JImmutableMultisetStressTester(JImmutables.<String>sortedMultiset()))

                .insert(new JImmutableMapStressTester<RegularKey<String>>(JImmutableHashMap.<RegularKey<String>, String>usingList(), HashMap.class, new RegularKeyFactory()))
                .insert(new JImmutableMapStressTester<ComparableRegularKey<String>>(JImmutableHashMap.<ComparableRegularKey<String>, String>usingTree(), HashMap.class, new ComparableRegularKeyFactory()))
                .insert(new JImmutableMapStressTester<BadHashKey<String>>(JImmutableHashMap.<BadHashKey<String>, String>usingList(), HashMap.class, new BadHashKeyFactory()))
                .insert(new JImmutableMapStressTester<ComparableBadHashKey<String>>(JImmutableHashMap.<ComparableBadHashKey<String>, String>usingTree(), HashMap.class, new ComparableBadHashKeyFactory()))

                .insert(new JImmutableMapStressTester<ComparableRegularKey<String>>(JImmutables.<ComparableRegularKey<String>, String>insertOrderMap(), LinkedHashMap.class, new ComparableRegularKeyFactory()))
                .insert(new JImmutableMapStressTester<ComparableRegularKey<String>>(JImmutables.<ComparableRegularKey<String>, String>sortedMap(), TreeMap.class, new ComparableRegularKeyFactory()))

                .insert(new JImmutableSetMapStressTester(JImmutables.<String, String>setMap(), HashMap.class))
                .insert(new JImmutableSetMapStressTester(JImmutables.<String, String>insertOrderSetMap(), LinkedHashMap.class))
                .insert(new JImmutableSetMapStressTester(JImmutables.<String, String>sortedSetMap(), TreeMap.class))

                .insert(new JImmutableListMapStressTester(JImmutables.<String, String>listMap(), HashMap.class))
                .insert(new JImmutableListMapStressTester(JImmutables.<String, String>insertOrderListMap(), LinkedHashMap.class))
                .insert(new JImmutableListMapStressTester(JImmutables.<String, String>sortedListMap(), TreeMap.class))

                .insert(new JImmutableArrayStressTester(JImmutables.<String>array()))
                .insert(new JImmutableArrayStressTester(Bit32Array.<String>of()))

                .insert(new JImmutableStackStressTester(JImmutables.<String>stack()));


        OptionParser parser = makeTesterOptions(testers);
        OptionSpec<String> fileSpec = parser.accepts("file").withRequiredArg();
        OptionSpec<Long> seedSpec = parser.accepts("seed").withRequiredArg().ofType(Long.class);

        OptionSet options = parser.parse(args);

        Long seed = (options.has(seedSpec)) ? options.valueOf(seedSpec) : System.currentTimeMillis();
        Random random = new Random(seed);

        JImmutableList<String> tokens;
        if (options.has(fileSpec)) {
            List<String> filenames = options.valuesOf(fileSpec);
            tokens = loadTokens(filenames);
            System.out.printf("\nLoaded %d tokens from %d files%n", tokens.size(), filenames.size());
        } else {
            tokens = loadTokens("src/site/apt/index.apt");
            System.out.printf("\nLoaded %d tokens from index.apt%n", tokens.size());
        }
        boolean needsFilter = needsFilter(options, fileSpec, seedSpec);
        //noinspection InfiniteLoopStatement
        while (true) {
            for (AbstractStressTestable tester : testers) {
                if (!needsFilter || filter(options, tester)) {
                    System.out.printf("\nStarting with seed %d%n", seed);
                    tester.execute(random, tokens);
                    seed = System.currentTimeMillis();
                    random.setSeed(seed);
                }
            }
        }
    }

    private boolean needsFilter(OptionSet options,
                                OptionSpec<String> file,
                                OptionSpec<Long> seed)
    {
        List<OptionSpec<?>> usedOptions = options.specs();
        for (OptionSpec<?> spec : usedOptions) {
            if (!(spec.equals(seed) || spec.equals(file))) {
                return true;
            }
        }
        return false;
    }

    private boolean filter(OptionSet options,
                           AbstractStressTestable tester)
    {
        for (String option : tester.getOptions()) {
            if (options.has(option)) {
                if (options.hasArgument(option)) {
                    @SuppressWarnings("unchecked") List<String> arguments = (List<String>)options.valuesOf(option);
                    for (String argument : arguments) {
                        for (String argOption : tester.getOptions()) {
                            if (argument.equals(argOption)) {
                                return true;
                            }
                        }
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private OptionParser makeTesterOptions(JImmutableList<AbstractStressTestable> testers)
    {
        OptionParser parser = new OptionParser();
        for (AbstractStressTestable tester : testers) {
            for (String option : tester.getOptions()) {
                parser.accepts(option).withOptionalArg();
            }
        }
        return parser;
    }

    private JImmutableList<String> loadTokens(List<String> filenames)
            throws IOException
    {
        JImmutableSet<String> tokens = JImmutables.set();
        for (String filename : filenames) {
            tokens = addTokensFromFile(tokens, filename);
        }
        return JImmutables.list(tokens);
    }

    private JImmutableList<String> loadTokens(String filename)
            throws IOException
    {
        JImmutableSet<String> tokens = JImmutables.set();
        tokens = addTokensFromFile(tokens, filename);
        return JImmutables.list(tokens);
    }

    private JImmutableSet<String> addTokensFromFile(JImmutableSet<String> tokens,
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
