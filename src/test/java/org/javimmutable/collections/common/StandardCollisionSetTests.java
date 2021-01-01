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

package org.javimmutable.collections.common;

import junit.framework.AssertionFailedError;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.*;

public class StandardCollisionSetTests
{
    public static void randomTests(@Nonnull CollisionSet<Integer> set)
    {
        assertEquals(set.insert(set.empty(), -45), set.single(-45));
        Set<Integer> expected = new TreeSet<>();
        CollisionSet.Node node = set.empty();
        Random r = new Random(100);
        for (int loop = 1; loop <= 5000; ++loop) {
            int command = r.nextInt(3);
            switch (command) {
                case 0: {
                    Integer k = r.nextInt(250);
                    CollisionSet.Node oldNode = node;
                    boolean changed = expected.add(k);
                    node = set.insert(node, k);
                    if (changed == (node == oldNode)) {
                        fail(String.format("insert fail: k=%d changed=%s", k, changed));
                    }
                    break;
                }
                case 1: {
                    Integer k = r.nextInt(250);
                    CollisionSet.Node oldNode = node;
                    boolean changed = expected.remove(k);
                    node = set.delete(node, k);
                    if (changed == (node == oldNode)) {
                        fail(String.format("delete fail: k=%d changed=%s", k, changed));
                    }
                    break;
                }
                case 2: {
                    Integer k = r.nextInt(500);
                    boolean ev = expected.contains(k);
                    boolean av = set.contains(node, k);
                    if (!ev) {
                        if (av) {
                            fail(String.format("contains fail: k=%d ev=%s av=%s", k, ev, av));
                        }
                    } else if (!av) {
                        fail(String.format("contains fail: k=%d ev=%s av=%s", k, ev, av));
                    }
                    break;
                }
            }
            verifyValues(expected, set, node);
        }
    }

    private static void verifyValues(Set<Integer> expected,
                                     CollisionSet<Integer> set,
                                     CollisionSet.Node node)
    {
        StringBuilder sb = new StringBuilder();
        if (set.size(node) != expected.size()) {
            sb.append(String.format("size mismatch: expected=%d actual=%d\n", expected.size(), set.size(node)));
        }
        Set<Integer> checked = new HashSet<>();
        for (Integer v : expected) {
            checked.add(v);
            if (!set.contains(node, v)) {
                sb.append(String.format("missing: v=%d\n", v));
            }
        }
        for (Integer v : set.iterable(node)) {
            if (!checked.contains(v)) {
                sb.append(String.format("unexpected: v=%d\n", v));
            }
        }
        if (sb.length() > 0) {
            throw new AssertionFailedError(sb.toString());
        }
    }
}
