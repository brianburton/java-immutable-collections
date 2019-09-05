package org.javimmutable.collections.common;

import junit.framework.AssertionFailedError;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import static junit.framework.Assert.fail;

public class StandardCollisionMapTests
{
    public static void randomTests(@Nonnull CollisionMap<Integer, Integer> map)
    {
        Map<Integer, Integer> expected = new TreeMap<>();
        CollisionMap.Node node = map.emptyNode();
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
                    Holder<Integer> ah = map.findValue(node, k);
                    if (ev == null) {
                        if (ah.isFilled()) {
                            fail(String.format("findValue fail: k=%d ev=%s av=%s", k, ev, ah.getValue()));
                        }
                    } else if (ah.isEmpty()) {
                        fail(String.format("findValue fail: k=%d ev=%s av=%s", k, ev, ah.getValueOrNull()));
                    }
                    break;
                }
                case 5: {
                    Integer k = r.nextInt(500);
                    Integer ev = expected.get(k);
                    Holder<JImmutableMap.Entry<Integer, Integer>> ah = map.findEntry(node, k);
                    if (ev == null) {
                        if (ah.isFilled()) {
                            fail(String.format("findEntry fail: k=%d ev=%s av=%s", k, ev, ah.getValue()));
                        }
                    } else if (ah.isEmpty()) {
                        fail(String.format("findEntry fail: k=%d ev=%s av=%s", k, ev, ah.getValueOrNull()));
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
            Holder<Integer> hv = map.findValue(node, k);
            if (hv.isEmpty()) {
                sb.append(String.format("missing value for key: k=%d ev=%s\n", k, ev));
            } else if (!ev.equals(hv.getValue())) {
                sb.append(String.format("mismatch: k=%d ev=%s hv=%s\n", k, ev, hv.getValue()));
            }
            Holder<JImmutableMap.Entry<Integer, Integer>> he = map.findEntry(node, k);
            if (hv.isEmpty()) {
                sb.append(String.format("missing entry for key: k=%d ev=%s\n", k, ev));
            } else {
                if (!k.equals(he.getValue().getKey())) {
                    sb.append(String.format("mismatch: k=%d he.k=%s\n", k, he.getValue().getKey()));
                }
                if (!ev.equals(he.getValue().getValue())) {
                    sb.append(String.format("mismatch: k=%d ev=%s he.v=%s\n", k, ev, he.getValue().getValue()));
                }
            }
        }
        for (JImmutableMap.Entry<Integer, Integer> e : map.iterable(node)) {
            if (!checked.contains(e.getKey())) {
                sb.append(String.format("unexpected: k=%d\n", e.getKey()));
            }
        }
        if (sb.length() > 0) {
            throw new AssertionFailedError(sb.toString());
        }
    }
}
