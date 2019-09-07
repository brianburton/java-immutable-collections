///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.setmap.JImmutableTemplateSetMap;
import org.javimmutable.collections.stress_test.KeyFactory.BadHashKeyFactory;
import org.javimmutable.collections.stress_test.KeyFactory.ComparableBadHashKeyFactory;
import org.javimmutable.collections.stress_test.KeyFactory.ComparableRegularKeyFactory;
import org.javimmutable.collections.stress_test.KeyFactory.RegularKeyFactory;
import org.javimmutable.collections.util.JImmutables;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Test program to run an infinite loop feeding data to every implementation of every
 * JImmutable collection type, querying the data, and deleting the data to verify
 * the collection always contains what it should.
 */
public class StressTester
{
    public static void main(String[] argv)
        throws Exception
    {
        new StressTester().execute(argv);
    }

    @SuppressWarnings("deprecation")
    public void execute(String[] args)
        throws Exception
    {
        JImmutableList<AbstractStressTestable> testers = JImmutables.<AbstractStressTestable>list()
            .insert(new JImmutableListStressTester(JImmutables.list(), JImmutables.listCollector()))

            .insert(new JImmutableSetStressTester(JImmutables.set(), HashSet.class, IterationOrder.UNORDERED))
            .insert(new JImmutableSetStressTester(JImmutables.insertOrderSet(), LinkedHashSet.class, IterationOrder.INSERT_ORDER))
            .insert(new JImmutableSetStressTester(JImmutables.sortedSet(), TreeSet.class, IterationOrder.ORDERED))
            .insert(new JImmutableSetStressTester(JImmutables.multiset(), HashSet.class, IterationOrder.UNORDERED))
            .insert(new JImmutableSetStressTester(JImmutables.insertOrderMultiset(), LinkedHashSet.class, IterationOrder.INSERT_ORDER))
            .insert(new JImmutableSetStressTester(JImmutables.sortedMultiset(), TreeSet.class, IterationOrder.ORDERED))

            .insert(new JImmutableMultisetStressTester(JImmutables.multiset()))
            .insert(new JImmutableMultisetStressTester(JImmutables.insertOrderMultiset()))
            .insert(new JImmutableMultisetStressTester(JImmutables.sortedMultiset()))

            .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingList(), HashMap.class, new RegularKeyFactory()))
            .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingTree(), HashMap.class, new ComparableRegularKeyFactory()))
            .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingList(), HashMap.class, new BadHashKeyFactory()))
            .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingTree(), HashMap.class, new ComparableBadHashKeyFactory()))

            .insert(new JImmutableMapStressTester<>(JImmutables.insertOrderMap(), LinkedHashMap.class, new ComparableRegularKeyFactory()))
            .insert(new JImmutableMapStressTester<>(JImmutables.sortedMap(), TreeMap.class, new ComparableRegularKeyFactory()))

            .insert(new JImmutableSetMapStressTester(JImmutables.setMap(), HashMap.class))
            .insert(new JImmutableSetMapStressTester(JImmutables.insertOrderSetMap(), LinkedHashMap.class))
            .insert(new JImmutableSetMapStressTester(JImmutables.sortedSetMap(), TreeMap.class))
            .insert(new JImmutableSetMapStressTester(JImmutables.setMap(JImmutables.<String, JImmutableSet<String>>sortedMap(), JImmutables.set()), TreeMap.class))

            .insert(new JImmutableListMapStressTester(JImmutables.listMap(), HashMap.class))
            .insert(new JImmutableListMapStressTester(JImmutables.insertOrderListMap(), LinkedHashMap.class))
            .insert(new JImmutableListMapStressTester(JImmutables.sortedListMap(), TreeMap.class))

            .insert(new JImmutableArrayStressTester(JImmutables.array(), ArrayIndexRange.INTEGER))

            .insert(new JImmutableStackStressTester(JImmutables.stack()));

        final OptionParser parser = new OptionParser();
        parser.accepts("help", "prints available options");
        final OptionSpec<String> fileSpec = parser.accepts("file", "specifies tokens file").withRequiredArg();
        final OptionSpec<Long> seedSpec = parser.accepts("seed", "specifies PRNG seed").withRequiredArg().ofType(Long.class);
        final OptionSpec<String> filterSpec = parser.accepts("filter", "specifies specific tests to run").withRequiredArg().ofType(String.class);
        final OptionSet options = parser.parse(args);
        final JImmutableSet<String> filters = JImmutables.sortedSet(filterSpec.values(options));
        if (options.has("help")) {
            printHelpMessage(testers, parser, filters);
            return;
        }

        Long seed = (options.has(seedSpec)) ? options.valueOf(seedSpec) : System.currentTimeMillis();
        Random random = new Random(seed);

        JImmutableList<String> tokens;
        if (options.has(fileSpec)) {
            List<String> filenames = options.valuesOf(fileSpec);
            tokens = StressTestUtil.loadTokens(filenames);
            System.out.printf("%nLoaded %d tokens from %d files%n", tokens.size(), filenames.size());
        } else {
            tokens = StressTestUtil.loadTokens("src/site/markdown/index.md");
            System.out.printf("%nLoaded %d tokens from index.md%n", tokens.size());
        }
        if (filters.size() > 0) {
            testers = testers.select(tester -> filters.containsAny(tester.getOptions()));
            if (testers.isEmpty()) {
                throw new RuntimeException("filter rejected all testers!!");
            }
        }
        System.out.printf("%nLoaded %d testers%n", testers.size());

        //noinspection InfiniteLoopStatement
        while (true) {
            for (AbstractStressTestable tester : testers) {
                System.out.printf("%nStarting with seed %d%n", seed);
                tester.execute(random, tokens);
                seed = System.currentTimeMillis();
                random.setSeed(seed);
                System.out.println("sleeping before next test");
                //noinspection BusyWait
                Thread.sleep(5000);
            }
        }
    }

    private String valuesString(Iterable<?> objects)
    {
        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(object);
        }
        return sb.toString();
    }

    public void printHelpMessage(JImmutableList<AbstractStressTestable> testers,
                                 OptionParser parser,
                                 JImmutableSet<String> selectedFilters)
        throws IOException
    {
        if (selectedFilters.size() > 0) {
            System.out.println("Filters: " + valuesString(selectedFilters));
            System.out.println();
        }

        System.out.println("Available Options:");
        System.out.println();
        parser.printHelpOn(System.out);

        JImmutableSetMap<String, String> filterMap = JImmutableTemplateSetMap.of(JImmutables.<String, JImmutableSet<String>>sortedMap(), JImmutables.sortedSet());
        for (AbstractStressTestable tester : testers) {
            for (String option : tester.getOptions()) {
                filterMap = filterMap.insert(tester.getTestName(), option);
            }
        }
        System.out.println();
        System.out.println("Available Filters By Class:");
        System.out.printf("%-40s  %s%n", "Tester Class", "Filters");
        for (JImmutableMap.Entry<String, JImmutableSet<String>> e : filterMap) {
            System.out.printf("%-40s  %s%n", e.getKey(), valuesString(e.getValue()));
        }

        filterMap = filterMap.deleteAll();
        for (AbstractStressTestable tester : testers) {
            for (String option : tester.getOptions()) {
                filterMap = filterMap.insert(option, tester.getTestName());
            }
        }
        System.out.println();
        System.out.println("Available Filters");
        System.out.printf("%-20s  %s%n", "Filter", "Tester Classes");
        for (JImmutableMap.Entry<String, JImmutableSet<String>> e : filterMap) {
            System.out.printf("%-20s  %s%n", e.getKey(), valuesString(e.getValue()));
        }

    }
}
