package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.*;

public class EmptyNodeTest
    extends TestCase
{
    private final AbstractNode<Integer> node = EmptyNode.instance();

    public void testVarious()
    {
        final LeafNode<Integer> leaf = new LeafNode<>(100);
        assertEquals(true, node.isEmpty());
        assertEquals(0, node.size());
        assertEquals(0, node.depth());
        verifyOutOfBounds(() -> node.get(0));
        assertEquals(leaf, node.append(100));
        assertSame(leaf, node.append(leaf));
        assertEquals(leaf, node.prepend(100));
        assertSame(leaf, node.prepend(leaf));
        verifyOutOfBounds(() -> node.assign(0, 100));
        assertEquals(leaf, node.insert(0, 100));
        verifyOutOfBounds(() -> node.insert(1, 100));
        verifyOutOfBounds(() -> node.deleteFirst());
        verifyOutOfBounds(() -> node.deleteLast());
        verifyOutOfBounds(() -> node.delete(0));
        assertSame(node, node.prefix(0));
        verifyOutOfBounds(() -> node.prefix(1));
        assertSame(node, node.suffix(0));
        verifyOutOfBounds(() -> node.suffix(1));
        Integer[] values = {1};
        node.copyTo(values, 0);
        assertThat(values).isEqualTo(new Integer[]{1});
        verifyUnsupported(() -> node.left());
        verifyUnsupported(() -> node.right());
        verifyUnsupported(() -> node.rotateLeft(leaf));
        verifyUnsupported(() -> node.rotateRight(leaf));
    }

    static void verifyOutOfBounds(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(IndexOutOfBoundsException.class);
    }

    static void verifyUnsupported(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(UnsupportedOperationException.class);
    }
}
