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

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.setmap.JImmutableSetMapFactory;
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
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.javimmutable.collections.util.JImmutables.*;

/**
 * Test program to run an infinite loop feeding data to every implementation of every
 * JImmutable collection type, querying the data, and deleting the data to verify
 * the collection always contains what it should.
 */
public class RunStressTests
{
    private static final JImmutableList<StressTester> AllTesters = JImmutables.<StressTester>list()
        .insert(new JImmutableListStressTester(list(), listCollector()))

        .insert(new JImmutableSetStressTester(set(), HashSet.class, IterationOrder.UNORDERED))
        .insert(new JImmutableSetStressTester(insertOrderSet(), LinkedHashSet.class, IterationOrder.INSERT_ORDER))
        .insert(new JImmutableSetStressTester(sortedSet(), TreeSet.class, IterationOrder.ORDERED))
        .insert(new JImmutableSetStressTester(multiset(), HashSet.class, IterationOrder.UNORDERED))
        .insert(new JImmutableSetStressTester(insertOrderMultiset(), LinkedHashSet.class, IterationOrder.INSERT_ORDER))
        .insert(new JImmutableSetStressTester(sortedMultiset(), TreeSet.class, IterationOrder.ORDERED))

        .insert(new JImmutableMultisetStressTester(multiset()))
        .insert(new JImmutableMultisetStressTester(insertOrderMultiset()))
        .insert(new JImmutableMultisetStressTester(sortedMultiset()))

        .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingList(), HashMap.class, new RegularKeyFactory()))
        .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingTree(), HashMap.class, new ComparableRegularKeyFactory()))
        .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingList(), HashMap.class, new BadHashKeyFactory()))
        .insert(new JImmutableMapStressTester<>(JImmutableHashMap.usingTree(), HashMap.class, new ComparableBadHashKeyFactory()))

        .insert(new JImmutableMapStressTester<>(insertOrderMap(), LinkedHashMap.class, new ComparableRegularKeyFactory()))
        .insert(new JImmutableMapStressTester<>(sortedMap(), TreeMap.class, new ComparableRegularKeyFactory()))

        .insert(new JImmutableSetMapStressTester(setMap(), HashMap.class))
        .insert(new JImmutableSetMapStressTester(insertOrderSetMap(), LinkedHashMap.class))
        .insert(new JImmutableSetMapStressTester(sortedSetMap(), TreeMap.class))
        .insert(new JImmutableSetMapStressTester(setMapFactory(String.class, String.class).withMap(sortedMap()).withSet(set()).create(), TreeMap.class))

        .insert(new JImmutableListMapStressTester(listMap(), HashMap.class))
        .insert(new JImmutableListMapStressTester(insertOrderListMap(), LinkedHashMap.class))
        .insert(new JImmutableListMapStressTester(sortedListMap(), TreeMap.class))

        .insert(new JImmutableArrayStressTester(array(), ArrayIndexRange.INTEGER))

        .insert(new JImmutableStackStressTester(stack()));

    public static void main(String[] argv)
        throws Exception
    {
        final OptionParser parser = new OptionParser();
        parser.accepts("help", "prints available options");
        final OptionSpec<String> fileSpec = parser.accepts("file", "specifies tokens file").withOptionalArg().ofType(String.class).defaultsTo("src/site/markdown/index.md");
        final OptionSpec<Long> seedSpec = parser.accepts("seed", "specifies random number seed").withOptionalArg().ofType(Long.class).defaultsTo(System.currentTimeMillis());
        final OptionSpec<String> testSpec = parser.accepts("test", "specifies tests to run").withRequiredArg().ofType(String.class);
        final OptionSet options;
        try {
            options = parser.parse(argv);
        } catch (OptionException ex) {
            System.out.printf("ERROR: %s%n%n", ex.getMessage());
            printHelpMessage(parser, set(), list());
            return;
        }
        final JImmutableSet<String> filters = sortedSet(testSpec.values(options));
        final JImmutableList<StressTester> selectedTests = filters.isEmpty() ? AllTesters : AllTesters.select(tester -> filters.containsAny(tester.getOptions()));
        if (options.has("help") || selectedTests.isEmpty()) {
            printHelpMessage(parser, filters, selectedTests);
            return;
        }

        final JImmutableList<String> filenames = list(options.valuesOf(fileSpec));
        final JImmutableList<String> tokens = StressTestUtil.loadTokens(filenames);
        System.out.printf("%nLoaded %d tokens from %d files%n", tokens.size(), filenames.size());

        long seed = options.valueOf(seedSpec);
        final Random random = new Random(seed);

        System.out.printf("%nRunning %d testers: %s%n", selectedTests.size(), valuesString(selectedTests.transform(StressTester::getTestName)));
        //noinspection InfiniteLoopStatement
        while (true) {
            for (StressTester tester : selectedTests) {
                System.out.printf("%nStarting %s with seed %d%n", tester.getTestName(), seed);
                tester.execute(random, tokens);
                seed = System.currentTimeMillis();
                random.setSeed(seed);
                System.out.println("sleeping before next test");
                //noinspection BusyWait
                Thread.sleep(5000);
            }
        }
    }

    private static void printHelpMessage(OptionParser parser,
                                         JImmutableSet<String> selectedTestArgs,
                                         JImmutableList<StressTester> selectedTests)
        throws IOException
    {
        if (selectedTests.size() > 0) {
            System.out.println("Selected Tests: " + valuesString(selectedTests.transform(StressTester::getTestName)));
            System.out.println();
        } else if (selectedTestArgs.size() > 0) {
            System.out.println("ERROR: invalid test args: " + valuesString(selectedTestArgs));
            System.out.println();
        }

        System.out.println("Available Options:");
        System.out.println();
        parser.printHelpOn(System.out);

        final JImmutableSetMapFactory<String, String> filterMapFactory = setMapFactory(String.class, String.class)
            .withMap(sortedMap())
            .withSet(sortedSet());
        final JImmutableSetMap<String, String> filterMap = AllTesters.stream()
            .flatMap(tester -> tester.getOptions()
                .stream()
                .map(option -> entry(tester.getTestName(), option)))
            .collect(filterMapFactory.collector());
        System.out.println();
        System.out.println("Available Filters By Class:");
        System.out.printf("%-40s  %s%n", "Tester Class", "Filters");
        filterMap.stream()
            .forEach(e -> System.out.printf("%-40s  %s%n", e.getKey(), valuesString(e.getValue())));

        System.out.println();
        System.out.println("Available Filters");
        System.out.printf("%-20s  %s%n", "Filter", "Tester Classes");
        filterMap.stream()
            .flatMap(e -> e.getValue().stream().map(s -> entry(s, e.getKey())))
            .collect(filterMapFactory.collector())
            .forEach(e -> System.out.printf("%-20s  %s%n", e.getKey(), valuesString(e.getValue())));
    }

    private static String valuesString(Iterable<?> objects)
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
}
