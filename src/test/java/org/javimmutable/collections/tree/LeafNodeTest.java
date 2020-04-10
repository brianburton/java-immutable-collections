package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.MapEntry;

import java.util.Comparator;

public class LeafNodeTest
    extends TestCase
{
    private final Comparator<Integer> comparator = Comparator.naturalOrder();
    private final AbstractNode<Integer, Integer> empty = FringeNode.instance();

    public void testGetters()
    {
        final AbstractNode<Integer, Integer> node = new LeafNode<>(1, 5);
        assertEquals(true, node.containsKey(comparator, 1));
        assertEquals(false, node.containsKey(comparator, 3));
        assertEquals((Integer)1, node.key());
        assertEquals((Integer)5, node.value());
        assertEquals((Integer)5, node.get(comparator, 1, 20));
        assertEquals((Integer)20, node.get(comparator, 3, 20));
        assertEquals(Holders.of(5), node.find(comparator, 1));
        assertEquals(Holders.of(), node.find(comparator, 3));
        assertEquals(Holders.of(MapEntry.entry(1, 5)), node.findEntry(comparator, 1));
        assertEquals(Holders.of(), node.findEntry(comparator, 3));
    }

    public void testDelete()
    {
        final AbstractNode<Integer, Integer> node = new LeafNode<>(1, -1);
        assertSame(empty, node.delete(comparator, 1));
        assertSame(node, node.delete(comparator, 2));
    }

    public void testAssign()
    {
        AbstractNode<Integer, Integer> node = empty.assign(comparator, 1, 10);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,10)]", node.toString());

        assertSame(node, node.assign(comparator, 1, 10));

        node = node.assign(comparator, 1, 20);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,20)]", node.toString());

        AbstractNode<Integer, Integer> valueNode = node.assign(comparator, 0, 5);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(0,5),(1,20)]", valueNode.toString());

        valueNode = node.assign(comparator, 3, 5);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(1,20),(3,5)]", valueNode.toString());
    }

    public void testUpdate()
    {
        final Func1<Holder<Integer>, Integer> generator = h -> h.isFilled() ? h.getValue() + 1 : 1;

        AbstractNode<Integer, Integer> node = empty.update(comparator, 1, generator);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,1)]", node.toString());

        assertSame(node, node.update(comparator, 1, h -> h.getValueOr(-1)));

        node = node.update(comparator, 1, generator);
        assertTrue(node instanceof LeafNode);
        assertEquals("[(1,2)]", node.toString());

        AbstractNode<Integer, Integer> valueNode = node.update(comparator, 0, generator);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(0,1),(1,2)]", valueNode.toString());

        valueNode = node.update(comparator, 3, generator);
        assertTrue(valueNode instanceof ValueNode);
        assertEquals("[(1,2),(3,1)]", valueNode.toString());
    }

}
