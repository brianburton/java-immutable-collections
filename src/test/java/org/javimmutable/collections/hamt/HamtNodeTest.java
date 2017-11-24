package org.javimmutable.collections.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.common.MutableDelta;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class HamtNodeTest
    extends TestCase
{
    public void testVarious()
    {
        HamtNode<String> empty = HamtNode.of();
        assertEquals(null, empty.getValueOr(1, null));
        verifyContents(empty);

        MutableDelta delta = new MutableDelta();
        HamtNode<String> node = empty.assign(1, "able", delta);
        assertEquals(1, delta.getValue());
        assertEquals("able", node.getValueOr(1, null));
        verifyContents(node, "able");

        assertSame(node, node.assign(1, "able", delta));
        assertEquals(1, delta.getValue());

        node = node.assign(1, "baker", delta);
        assertEquals(1, delta.getValue());
        assertEquals("baker", node.getValueOr(1, null));
        verifyContents(node, "baker");

        node = node.assign(-1, "charlie", delta);
        assertEquals(2, delta.getValue());
        assertEquals("charlie", node.getValueOr(-1, null));
        verifyContents(node, "baker", "charlie");

        assertSame(node, node.assign(-1, "charlie", delta));
        assertEquals(2, delta.getValue());

        node = node.assign(7, "delta", delta);
        assertEquals(3, delta.getValue());
        assertEquals("delta", node.getValueOr(7, null));
        verifyContents(node, "baker", "charlie", "delta");

        node = node.assign(4725297, "echo", delta);
        assertEquals(4, delta.getValue());
        assertEquals("echo", node.getValueOr(4725297, null));
        verifyContents(node, "baker", "charlie", "delta", "echo");

        assertSame(node, node.delete(-2, delta));
        assertEquals(4, delta.getValue());
        verifyContents(node, "baker", "charlie", "delta", "echo");

        node = node.assign(33, "foxtrot", delta);
        assertEquals(5, delta.getValue());
        assertEquals("foxtrot", node.getValueOr(33, null));
        verifyContents(node, "baker", "charlie", "delta", "echo", "foxtrot");

        node = node.delete(1, delta);
        assertEquals(4, delta.getValue());
        assertEquals(null, node.getValueOr(1, null));
        verifyContents(node, "charlie", "delta", "echo", "foxtrot");

        assertSame(node, node.delete(-2, delta));
        assertEquals(4, delta.getValue());

        node = node.delete(4725297, delta);
        assertEquals(3, delta.getValue());
        assertEquals(null, node.getValueOr(4725297, null));
        verifyContents(node, "charlie", "delta", "foxtrot");

        node = node.delete(-1, delta);
        assertEquals(2, delta.getValue());
        assertEquals(null, node.getValueOr(-1, null));
        verifyContents(node, "delta", "foxtrot");

        node = node.delete(7, delta);
        assertEquals(1, delta.getValue());
        assertEquals(null, node.getValueOr(7, null));
        verifyContents(node, "foxtrot");

        node = node.delete(33, delta);
        assertEquals(0, delta.getValue());
        assertSame(HamtNode.of(), node);
    }

    public void testRandom()
    {
        final Random r = new Random();
        final List<Integer> domain = IntStream.range(1, 1200)
            .boxed()
            .map(i -> r.nextInt())
            .collect(Collectors.toList());

        final MutableDelta size = new MutableDelta();
        HamtNode<Integer> node = HamtNode.of();
        for (Integer key : domain) {
            node = node.assign(key, key, size);
        }
        verifyIntContents(node, domain);

        final MutableDelta zero = new MutableDelta();
        Collections.shuffle(domain);
        for (Integer key : domain) {
            node = node.delete(key, size);
            assertSame(node, node.delete(key, zero));
        }
        assertSame(HamtNode.of(), node);
        assertEquals(0, size.getValue());
        assertEquals(0, zero.getValue());
    }

    private void verifyContents(HamtNode<String> node,
                                String... values)
    {
        Set<String> expected = new HashSet<>();
        expected.addAll(asList(values));
        Set<String> actual = node.stream().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    private void verifyIntContents(HamtNode<Integer> node,
                                   List<Integer> values)
    {
        Set<Integer> expected = new HashSet<>();
        expected.addAll(values);
        Set<Integer> actual = node.stream().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }
}
