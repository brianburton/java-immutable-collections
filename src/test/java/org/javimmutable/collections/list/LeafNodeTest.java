package org.javimmutable.collections.list;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.list.EmptyNodeTest.*;
import static org.javimmutable.collections.list.LeafNode.*;

public class LeafNodeTest
    extends TestCase
{
    public void testVarious()
    {
        assertThat(leaf(0, 1).isEmpty()).isFalse();
        assertThat(leaf(0, 1).size()).isEqualTo(1);
        assertThat(leaf(0, 5).size()).isEqualTo(5);
        assertThat(leaf(0, 1).depth()).isEqualTo(0);
        assertThat(leaf(0, 5).get(0)).isEqualTo(0);
        assertThat(leaf(0, 5).get(4)).isEqualTo(4);
        verifyOutOfBounds(() -> leaf(0, 5).get(-1));
        verifyOutOfBounds(() -> leaf(0, 5).get(5));

        assertThat(leaf(0, 5).append(5)).isEqualTo(leaf(0, 6));
        assertThat(leaf(0, 5).append(leaf(5, 10))).isEqualTo(leaf(0, 10));
        assertThat(leaf(0, MAX_SIZE - 3).append(leaf(MAX_SIZE - 3, MAX_SIZE))).isEqualTo(leaf(0, MAX_SIZE));
        assertThat(leaf(0, MAX_SIZE - 3).append(leaf(MAX_SIZE - 3, MAX_SIZE + 1))).isEqualTo(new BranchNode<>(leaf(0, MAX_SIZE - 3), leaf(MAX_SIZE - 3, MAX_SIZE + 1)));
        assertThat(leaf(0, MAX_SIZE).append(MAX_SIZE)).isEqualTo(new BranchNode<>(leaf(0, SPLIT_SIZE), leaf(SPLIT_SIZE, MAX_SIZE + 1)));

        assertThat(leaf(1, 5).prepend(0)).isEqualTo(leaf(0, 5));
        assertThat(leaf(5, 10).prepend(leaf(0, 5))).isEqualTo(leaf(0, 10));
        assertThat(leaf(MAX_SIZE - 3, MAX_SIZE).prepend(leaf(0, MAX_SIZE - 3))).isEqualTo(leaf(0, MAX_SIZE));
        assertThat(leaf(MAX_SIZE - 3, MAX_SIZE + 1).prepend(leaf(0, MAX_SIZE - 3))).isEqualTo(new BranchNode<>(leaf(0, MAX_SIZE - 3), leaf(MAX_SIZE - 3, MAX_SIZE + 1)));
        assertThat(leaf(1, MAX_SIZE + 1).prepend(0)).isEqualTo(new BranchNode<>(leaf(0, SPLIT_SIZE + 1), leaf(SPLIT_SIZE + 1, MAX_SIZE + 1)));

        verifyOutOfBounds(() -> leaf(0, 5).assign(-1, 9));
        assertThat(leaf(0, 7).assign(3, 9)).isEqualTo(leaf(0, 7, 3, 9));
        verifyOutOfBounds(() -> leaf(0, 5).assign(5, 9));

        verifyOutOfBounds(() -> leaf(0, 5).insert(-1, 9));
        assertThat(leaf(0, 5).insert(3, 9)).isEqualTo(new LeafNode<>(new Integer[]{0, 1, 2, 9, 3, 4}, 6));
        assertThat(leaf(0, 5).insert(5, 9)).isEqualTo(new LeafNode<>(new Integer[]{0, 1, 2, 3, 4, 9}, 6));
        verifyOutOfBounds(() -> leaf(0, 5).insert(6, 9));

        assertThat(leaf(0, 5).deleteFirst()).isEqualTo(leaf(1, 5));
        assertThat(leaf(0, 5).deleteLast()).isEqualTo(leaf(0, 4));

        assertThat(leaf(0, 5).delete(0)).isEqualTo(leaf(1, 5));
        assertThat(leaf(0, 5).delete(4)).isEqualTo(leaf(0, 4));
        verifyOutOfBounds(() -> leaf(0, 5).delete(-1));
        verifyOutOfBounds(() -> leaf(0, 5).delete(5));

        final LeafNode<Integer> self = leaf(0, 5);
        assertThat(self.prefix(0)).isSameAs(EmptyNode.instance());
        assertThat(self.prefix(5)).isSameAs(self);
        assertThat(self.prefix(3)).isEqualTo(leaf(0, 3));
        verifyOutOfBounds(() -> self.prefix(-1));
        verifyOutOfBounds(() -> self.prefix(6));

        assertThat(self.suffix(0)).isSameAs(self);
        assertThat(self.suffix(5)).isSameAs(EmptyNode.instance());
        assertThat(self.suffix(1)).isEqualTo(leaf(1, 5));
        assertThat(self.suffix(3)).isEqualTo(leaf(3, 5));
        verifyOutOfBounds(() -> self.suffix(-1));
        verifyOutOfBounds(() -> self.suffix(6));

        Integer[] values = new Integer[]{-1, -1, -1, -1, -1, -1, -1};
        self.copyTo(values, 1);
        assertThat(values).isEqualTo(new Integer[]{-1, 0, 1, 2, 3, 4, -1});

        verifyUnsupported(() -> self.left());
        verifyUnsupported(() -> self.right());
        verifyUnsupported(() -> self.rotateLeft(self));
        verifyUnsupported(() -> self.rotateRight(self));
    }

    private static LeafNode<Integer> leaf(int start,
                                          int limit)
    {
        final int length = limit - start;
        Integer[] values = new Integer[length];
        for (int i = 0; i < length; ++i) {
            values[i] = start + i;
        }
        return new LeafNode<>(values, length);
    }

    private static LeafNode<Integer> leaf(int start,
                                          int limit,
                                          int index,
                                          int value)
    {
        final int length = limit - start;
        Integer[] values = new Integer[length];
        for (int i = 0; i < length; ++i) {
            values[i] = start + i;
        }
        values[index] = value;
        return new LeafNode<>(values, length);
    }
}
