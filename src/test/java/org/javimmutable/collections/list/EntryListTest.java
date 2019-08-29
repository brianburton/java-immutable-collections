package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.Arrays;

import static org.javimmutable.collections.Holders.holder;
import static org.javimmutable.collections.MapEntry.mapEntry;

public class EntryListTest
    extends TestCase
{
    final EntryList<Integer, Integer> empty = EntryList.empty();

    public void testAssign()
    {
        assertEquals(EntryList.instance(root(1, 1)), empty.assign(1, 1));
        assertEquals(EntryList.instance(root(0, 3)), empty.assign(0, 0).assign(1, 1).assign(2, 2).assign(3, 3));
        assertEquals(EntryList.instance(root(0, 3).assign(1, mapEntry(1, 9))), empty.assign(0, 0).assign(1, 1).assign(2, 2).assign(3, 3).assign(1, 9));

        EntryList<Integer, Integer> list = empty.assign(1, 1);
        assertSame(list, list.assign(1, 1));
    }

    public void testUpdate()
    {
        assertEquals(EntryList.instance(root(1, 1)), empty.update(1, h -> 1));
        assertEquals(EntryList.instance(root(0, 2).assign(1, mapEntry(1, 9))), empty.assign(0, 0).assign(1, 1).assign(2, 2).update(1, h -> 9));

        EntryList<Integer, Integer> list = empty.assign(1, 1);
        assertSame(list, list.update(1, h -> 1));
    }

    public void testDelete()
    {
        assertSame(empty, empty.delete(1));
        assertSame(empty, empty.assign(1, 1).delete(1));
        assertEquals(EntryList.instance(root(0, 1)), empty.assign(0, 0).assign(1, 1).assign(2, 2).delete(2));
    }

    public void testGetters()
    {
        Holder<Entry<Integer, Integer>> noEntry = holder();
        Holder<Integer> noValue = holder();
        assertEquals(Integer.valueOf(99), empty.getValueOr(0, 99));
        assertEquals(noValue, empty.findValue(0));
        assertEquals(noEntry, empty.findEntry(0));

        EntryList<Integer, Integer> node = empty.assign(0, 1);
        assertEquals(Integer.valueOf(1), node.getValueOr(0, 99));
        assertEquals(holder(1), node.findValue(0));
        assertEquals(holder(mapEntry(0, 1)), node.findEntry(0));
    }

    public void testIterator()
    {
        final EntryList<Integer, Integer> node = empty.assign(0, 0).assign(1, 1);
        Iterable<Entry<Integer, Integer>> iterable = () -> new GenericIterator<>(node, 0, node.size());
        StandardIteratorTests.verifyContents(Arrays.asList(mapEntry(0, 0), mapEntry(1, 1)), iterable);
    }

    static AbstractNode<Entry<Integer, Integer>> root(int first,
                                                      int last)
    {
        AbstractNode<Entry<Integer, Integer>> node = EmptyNode.instance();
        for (int i = first; i <= last; ++i) {
            node = node.append(mapEntry(i, i));
        }
        return node;
    }
}
