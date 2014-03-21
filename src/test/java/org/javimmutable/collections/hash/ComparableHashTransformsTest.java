package org.javimmutable.collections.hash;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.tree.LeafNode;
import org.javimmutable.collections.tree.TwoNode;

import java.util.ArrayList;
import java.util.List;

public class ComparableHashTransformsTest
        extends TestCase
{
    public void testUpdateDelete()
    {
        ComparableHashTransforms<Integer, Integer> transforms = new ComparableHashTransforms<Integer, Integer>();
        MutableDelta delta = new MutableDelta();
        Object value = transforms.update(Holders.of(), 10, 100, delta);
        assertEquals(1, delta.getValue());
        assertEquals(new LeafNode<Integer, Integer>(10, 100), value);

        delta = new MutableDelta();
        value = transforms.update(Holders.of(value), 10, 1000, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new LeafNode<Integer, Integer>(10, 1000), value);

        delta = new MutableDelta();
        value = transforms.update(Holders.of(value), 12, 60, delta);
        assertEquals(1, delta.getValue());
        assertEquals(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 1000), new LeafNode<Integer, Integer>(12, 60), 10, 12), value);

        delta = new MutableDelta();
        value = transforms.update(Holders.of(value), 12, 90, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 1000), new LeafNode<Integer, Integer>(12, 90), 10, 12), value);

        delta = new MutableDelta();
        Holder<Object> deleted = transforms.delete(value, 87, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 1000), new LeafNode<Integer, Integer>(12, 90), 10, 12), deleted.getValue());

        delta = new MutableDelta();
        deleted = transforms.delete(deleted.getValue(), 10, delta);
        assertEquals(-1, delta.getValue());
        assertEquals(new LeafNode<Integer, Integer>(12, 90), deleted.getValue());

        delta = new MutableDelta();
        deleted = transforms.delete(deleted.getValue(), 40, delta);
        assertEquals(0, delta.getValue());
        assertEquals(new LeafNode<Integer, Integer>(12, 90), deleted.getValue());

        delta = new MutableDelta();
        deleted = transforms.delete(deleted.getValue(), 12, delta);
        assertEquals(-1, delta.getValue());
        assertEquals(true, deleted.isEmpty());
    }

    public void testFindGet()
    {
        ComparableHashTransforms<Integer, Integer> transforms = new ComparableHashTransforms<Integer, Integer>();
        MutableDelta delta = new MutableDelta();
        Object value = transforms.update(Holders.of(), 10, 100, delta);
        value = transforms.update(Holders.of(value), 18, 180, delta);
        value = transforms.update(Holders.of(value), 12, 60, delta);
        value = transforms.update(Holders.of(value), -6, -60, delta);
        value = transforms.update(Holders.of(value), 12, 90, delta);
        assertEquals(4, delta.getValue());

        assertEquals(Holders.of(100), transforms.findValue(value, 10));
        assertEquals(Holders.of(90), transforms.findValue(value, 12));
        assertEquals(Holders.of(180), transforms.findValue(value, 18));
        assertEquals(Holders.of(-60), transforms.findValue(value, -6));
        assertEquals(Holders.<Integer>of(), transforms.findValue(value, 11));

        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(10, 100)), transforms.findEntry(value, 10));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(12, 90)), transforms.findEntry(value, 12));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(18, 180)), transforms.findEntry(value, 18));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(-6, -60)), transforms.findEntry(value, -6));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), transforms.findEntry(value, 11));

        List<JImmutableMap.Entry<Integer, Integer>> expected = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        expected.add(MapEntry.of(-6, -60));
        expected.add(MapEntry.of(10, 100));
        expected.add(MapEntry.of(12, 90));
        expected.add(MapEntry.of(18, 180));
        StandardCursorTest.listCursorTest(expected, transforms.cursor(value));
    }
}
