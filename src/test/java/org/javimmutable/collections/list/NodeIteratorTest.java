package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterator;

import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.iterators.StandardIteratorTests.verifyOrderedIterable;

public class NodeIteratorTest
    extends TestCase
{
    public void testVarious()
    {
        List<Integer> list = new ArrayList<>();
        TreeBuilder<Integer> tree = new TreeBuilder<>();
        for (int i = 1; i <= 1024; ++i) {
            verifyOrderedIterable(list, tree.build());

            List<Integer> traversed = new ArrayList<>();
            traverse(traversed, tree.build().iterator());
            assertEquals(traversed, list);

            list.add(i);
            tree.add(i);
        }
    }

    private void traverse(List<Integer> values,
                          SplitableIterator<Integer> source)
    {
        if (source.isSplitAllowed()) {
            SplitIterator<Integer> split = source.splitIterator();
            traverse(values, split.getLeft());
            traverse(values, split.getRight());
        } else {
            while (source.hasNext()) {
                values.add(source.next());
            }
        }
    }
}
