package org.javimmutable.collections.tree_list;

import junit.framework.TestCase;

public class TreeBuilderTest
    extends TestCase
{
    public void testBuilderRebuild()
    {
        for (int size = 0; size < 1024; ++size) {
            TreeBuilder<Integer> builder = new TreeBuilder<>();
            for (int i = 1; i <= size; ++i) {
                builder.add(i);
            }
            AbstractNode<Integer> before = builder.build();
            builder.rebuild(builder.build());
            AbstractNode<Integer> after = builder.build();
            assertEquals(JImmutableTreeRAList.create(after), JImmutableTreeRAList.create(before));
        }
    }
}
