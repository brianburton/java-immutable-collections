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

package org.javimmutable.collection.common;

import junit.framework.AssertionFailedError;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.Maybe;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import static junit.framework.Assert.*;

public class StandardCollisionMapTests
{
    public static void randomTests(@Nonnull CollisionMap<Integer, Integer> map)
    {
        assertEquals(map.update(map.empty(), -45, 1000), map.single(-45, 1000));
        Map<Integer, Integer> expected = new TreeMap<>();
        CollisionMap.Node node = map.empty();
        Random r = new Random(100);
        for (int loop = 1; loop <= 5000; ++loop) {
            int command = r.nextInt(6);
            switch (command) {
                case 0: {
                    Integer k = r.nextInt(250);
                    Integer v = randomValue(r, k, expected);
                    boolean noChange = (v == expected.get(k));
                    CollisionMap.Node oldNode = node;
                    expected.put(k, v);
                    node = map.update(node, k, v);
                    if (noChange && node != oldNode) {
                        fail(String.format("update value noChange fail: k=%d v=%s", k, v));
                    }
                    break;
                }
                case 1: {
                    Integer k = r.nextInt(250);
                    Integer v = randomValue(r, k, expected);
                    boolean noChange = (v == expected.get(k));
                    CollisionMap.Node oldNode = node;
                    expected.put(k, v);
                    node = map.update(node, k, ov -> v);
                    if (noChange && node != oldNode) {
                        fail(String.format("update generator noChange fail: k=%d v=%s", k, v));
                    }
                    break;
                }
                case 2: {
                    Integer k = r.nextInt(250);
                    expected.remove(k);
                    node = map.delete(node, k);
                    break;
                }
                case 3: {
                    Integer k = r.nextInt(500);
                    Integer ev = expected.get(k);
                    Integer av = map.getValueOr(node, k, null);
                    if (ev == null) {
                        if (av != null) {
                            fail(String.format("getValueOr fail: k=%d ev=%s av=%s", k, ev, av));
                        }
                    } else if (av == null) {
                        fail(String.format("getValueOr fail: k=%d ev=%s av=%s", k, ev, av));
                    }
                    break;
                }
                case 4: {
                    Integer k = r.nextInt(500);
                    Integer ev = expected.get(k);
                    Maybe<Integer> ah = map.findValue(node, k);
                    if (ev == null) {
                        if (ah.isPresent()) {
                            fail(String.format("findValue fail: k=%d ev=%s av=%s", k, ev, ah.unsafeGet()));
                        }
                    } else if (ah.isAbsent()) {
                        fail(String.format("findValue fail: k=%d ev=%s av=%s", k, ev, ah.getOrNull()));
                    }
                    break;
                }
                case 5: {
                    Integer k = r.nextInt(500);
                    Integer ev = expected.get(k);
                    Maybe<IMapEntry<Integer, Integer>> ah = map.findEntry(node, k);
                    if (ev == null) {
                        if (ah.isPresent()) {
                            fail(String.format("findEntry fail: k=%d ev=%s av=%s", k, ev, ah.unsafeGet()));
                        }
                    } else if (ah.isAbsent()) {
                        fail(String.format("findEntry fail: k=%d ev=%s av=%s", k, ev, ah.getOrNull()));
                    }
                    break;
                }
            }
            verifyValues(expected, map, node);
        }
    }

    private static Integer randomValue(Random r,
                                       Integer k,
                                       Map<Integer, Integer> expected)
    {
        if (expected.containsKey(k) && r.nextInt(100) <= 10) {
            return expected.get(k);
        }
        return r.nextInt(500);
    }

    private static void verifyValues(Map<Integer, Integer> expected,
                                     CollisionMap<Integer, Integer> map,
                                     CollisionMap.Node node)
    {
        StringBuilder sb = new StringBuilder();
        if (map.size(node) != expected.size()) {
            sb.append(String.format("size mismatch: expected=%d actual=%d\n", expected.size(), map.size(node)));
        }
        Set<Integer> checked = new HashSet<>();
        for (Integer k : expected.keySet()) {
            checked.add(k);
            Integer ev = expected.get(k);
            Integer av = map.getValueOr(node, k, null);
            if (!ev.equals(av)) {
                sb.append(String.format("mismatch: k=%d ev=%s av=%s\n", k, ev, av));
            }
            Maybe<Integer> hv = map.findValue(node, k);
            if (hv.isAbsent()) {
                sb.append(String.format("missing value for key: k=%d ev=%s\n", k, ev));
            } else if (!ev.equals(hv.unsafeGet())) {
                sb.append(String.format("mismatch: k=%d ev=%s hv=%s\n", k, ev, hv.unsafeGet()));
            }
            Maybe<IMapEntry<Integer, Integer>> he = map.findEntry(node, k);
            if (hv.isAbsent()) {
                sb.append(String.format("missing entry for key: k=%d ev=%s\n", k, ev));
            } else {
                if (!k.equals(he.unsafeGet().getKey())) {
                    sb.append(String.format("mismatch: k=%d he.k=%s\n", k, he.unsafeGet().getKey()));
                }
                if (!ev.equals(he.unsafeGet().getValue())) {
                    sb.append(String.format("mismatch: k=%d ev=%s he.v=%s\n", k, ev, he.unsafeGet().getValue()));
                }
            }
        }
        for (IMapEntry<Integer, Integer> e : map.iterable(node)) {
            if (!checked.contains(e.getKey())) {
                sb.append(String.format("unexpected: k=%d\n", e.getKey()));
            }
        }
        if (sb.length() > 0) {
            throw new AssertionFailedError(sb.toString());
        }
    }
}
