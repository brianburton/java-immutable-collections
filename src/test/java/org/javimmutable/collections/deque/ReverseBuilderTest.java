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

package org.javimmutable.collections.deque;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;
import static org.javimmutable.collections.deque.ForwardBuilder.insertAtEnd;
import static org.javimmutable.collections.deque.ReverseBuilder.insertAtBeginning;
import org.javimmutable.collections.indexed.IndexedHelper;
import static org.javimmutable.collections.indexed.IndexedHelper.range;
import org.javimmutable.collections.iterators.IndexedIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReverseBuilderTest
    extends TestCase
{
    private static final Indexed<Integer> NoValues = IndexedHelper.empty();

    private final Map<String, Scenario> Scenarios =
        Stream.of(new Scenario("leaf",
                               NoValues,
                               range(1, 28),
                               NoValues,
                               range(-2000, 0)),
                  new Scenario("depth 2, no prefix, no suffix",
                               NoValues,
                               range(1, 512),
                               NoValues,
                               range(-2000, 0)),
                  new Scenario("depth 2, no prefix, leaf suffix",
                               NoValues,
                               range(1, 512),
                               range(513, 528),
                               range(-2000, 0)),
                  new Scenario("depth 2, leaf prefix, leaf suffix",
                               range(-2, 0),
                               range(1, 512),
                               range(513, 528),
                               range(-2000, -3)),
                  new Scenario("depth 3, no prefix, no suffix",
                               NoValues,
                               range(1, 1024),
                               NoValues,
                               range(-2000, 0)),
                  new Scenario("depth 3, full prefix, full suffix",
                               range(-67, 0),
                               range(1, 1094),
                               range(1094, 1097),
                               range(-3000, -68))
            )
            .collect(Collectors.toMap(Scenario::getName,
                                      Function.identity(),
                                      (x, y) -> y,
                                      LinkedHashMap::new));

    public void testEmpty()
    {
        ReverseBuilder<Integer> builder = insertAtBeginning(EmptyNode.of());
        builder.checkInvariants();
        verifyEquals(Collections.emptyList(), builder.build());
    }

    public void testAddToEmpty()
    {
        ReverseBuilder<Integer> builder = insertAtBeginning(EmptyNode.of());
        List<Integer> expected = new ArrayList<>();
        for (int i = 2500; i > 0; --i) {
            expected.add(0, i);
            builder.add(i);
            builder.checkInvariants();
            Node<Integer> built = builder.build();
            built.checkInvariants();
            verifyEquals(expected, built);
        }
    }

    public void testScenarios()
    {
//        runScenario(Scenarios.get("depth 3, full prefix, full suffix"));
        for (Scenario scenario : Scenarios.values()) {
            try {
                runScenario(scenario);
            } catch (AssertionFailedError failure) {
                AssertionFailedError extended = new AssertionFailedError("Scenario " + scenario.name + " failed.");
                extended.addSuppressed(failure);
                throw extended;
            }
        }
    }

    private void runScenario(Scenario scenario)
    {
        ForwardBuilder<Integer> startBuilder = insertAtEnd(EmptyNode.of());
        startBuilder.addAll(scenario.middle);
        Node<Integer> starter = startBuilder.build();
        starter.checkInvariants();

        for (int i = scenario.prefix.size() - 1; i >= 0; --i) {
            starter = starter.insertFirst(scenario.prefix.get(i));
        }
        starter.checkInvariants();

        for (int i = 0; i < scenario.suffix.size(); ++i) {
            starter = starter.insertLast(scenario.suffix.get(i));
        }
        starter.checkInvariants();

        ReverseBuilder<Integer> builder = insertAtBeginning(starter);
        builder.addAll(scenario.adds.reversed());
        builder.checkInvariants();
        Node<Integer> built = builder.build();
        built.checkInvariants();

        List<Integer> expected = new ArrayList<>();
        expected.addAll(IndexedHelper.asList(scenario.prefix));
        expected.addAll(IndexedHelper.asList(scenario.middle));
        expected.addAll(IndexedHelper.asList(scenario.suffix));
        for (Integer i : IndexedIterator.rev(scenario.adds)) {
            expected.add(0, i);
        }

        verifyEquals(expected, built);
    }

    private void verifyEquals(List<Integer> expectedList,
                              Node<Integer> actual)
    {
        List<Integer> actualList = new ArrayList<>(actual.size());
        actual.iterator().forEachRemaining(actualList::add);

        assertEquals(expectedList, actualList);
    }

    private void verifyEquals(Indexed<Integer> expected,
                              Node<Integer> actual)
    {
        List<Integer> actualList = new ArrayList<>(actual.size());
        actual.iterator().forEachRemaining(actualList::add);

        assertEquals(IndexedHelper.asList(expected), actualList);
    }

    private void verifyEquals(Node<Integer> expected,
                              Node<Integer> actual)
    {
        List<Integer> expectedList = new ArrayList<>(expected.size());
        expected.iterator().forEachRemaining(expectedList::add);

        verifyEquals(expectedList, actual);
    }

    private static class Scenario
    {
        private final String name;
        private final Indexed<Integer> prefix;
        private final Indexed<Integer> middle;
        private final Indexed<Integer> suffix;
        private final Indexed<Integer> adds;

        private Scenario(String name,
                         Indexed<Integer> prefix,
                         Indexed<Integer> middle,
                         Indexed<Integer> suffix,
                         Indexed<Integer> adds)
        {
            this.name = name;
            this.prefix = prefix;
            this.middle = middle;
            this.suffix = suffix;
            this.adds = adds;
        }

        private String getName()
        {
            return name;
        }
    }
}
