package org.javimmutable.collections.list;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.*;

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
            TreeBuilder<Integer> second = new TreeBuilder<>();
            second.rebuild(before);
            assertThat(second.size()).isEqualTo(before.size());
            AbstractNode<Integer> after = second.build();
            assertEquals(JImmutableTreeList.create(after), JImmutableTreeList.create(before));
        }
    }
}
