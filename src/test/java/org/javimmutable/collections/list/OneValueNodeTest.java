package org.javimmutable.collections.list;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.iterators.StandardIteratorTests.verifyContents;

public class OneValueNodeTest
    extends TestCase
{
    public void testVarious()
    {
        final EmptyNode<Integer> empty = EmptyNode.instance();
        final OneValueNode<Integer> node = new OneValueNode<>(100);
        final AbstractNode<Integer> large1 = MultiValueNodeTest.leaf(1, MultiValueNode.MAX_SIZE);
        final AbstractNode<Integer> maxed = MultiValueNodeTest.leaf(1, MultiValueNode.MAX_SIZE + 1);
        final AbstractNode<Integer> branch = maxed.append(large1);
        assertSame(empty, node.deleteFirst());
        assertSame(empty, node.deleteLast());
        assertSame(empty, node.delete(0));
        assertSame(empty, node.prefix(0));
        assertSame(node, node.prefix(1));
        assertSame(node, node.suffix(0));
        assertSame(empty, node.suffix(1));
        assertEquals(false, node.isEmpty());
        assertEquals(0, node.depth());
        assertEquals(1, node.size());
        assertSame(node, node.assign(0, 100));
        assertEquals(new OneValueNode<>(10), node.assign(0, 10));
        assertEquals(new MultiValueNode<>(100, 10), node.assign(1, 10));
        assertEquals(new MultiValueNode<>(10, 100), node.insert(0, 10));
        assertEquals(new MultiValueNode<>(100, 10), node.insert(1, 10));
        assertEquals(new MultiValueNode<>(10, 100), node.prepend(10));
        assertEquals(new MultiValueNode<>(100, 10), node.append(10));
        assertEquals(large1.append(100), node.prepend(large1));
        assertEquals(large1.prepend(100), node.append(large1));
        assertEquals(new BranchNode<>(maxed, node), node.prepend(maxed));
        assertEquals(new BranchNode<>(node, maxed), node.append(maxed));
        verifyContents(branch.append(100), node.prepend(branch));
        verifyContents(branch.prepend(100), node.append(branch));
        assertSame(node, node.append(empty));
        assertSame(node, node.prepend(empty));
        assertEquals(new Integer(100), node.get(0));
        assertSame(empty, node.delete(0));
        assertThatThrownBy(() -> node.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.assign(2, 100)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.insert(2, 100)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.delete(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.prefix(2)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.suffix(2)).isInstanceOf(IndexOutOfBoundsException.class);
    }
}
