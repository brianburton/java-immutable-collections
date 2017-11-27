package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.hash.transforms.HashValueListNode;
import org.javimmutable.collections.hash.transforms.HashValueListTransforms;

public class HamtLeafNodeTest
    extends TestCase
{
    public void testDelete()
    {
        final Checked a = new Checked(1, 11);
        final Checked b = new Checked(1, 12);
        final Checked c = new Checked(1, 13);
        final Checked d = new Checked(1, 14);
        final MutableDelta size = new MutableDelta();
        final HashValueListTransforms<Checked, Integer> transforms = new HashValueListTransforms<>();
        HashValueListNode<Checked, Integer> values = transforms.update(null, a, 100, size);
        values = transforms.update(values, b, 200, size);
        values = transforms.update(values, c, 300, size);
        assertEquals(3, size.getValue());

        HamtNode<HashValueListNode<Checked, Integer>, Checked, Integer> node = new HamtLeafNode<>(1, values);
        assertSame(node, node.delete(transforms, 1, d, size));
        assertEquals(3, size.getValue());

        node = node.delete(transforms, 1, b, size);
        assertEquals(2, size.getValue());

        node = node.delete(transforms, 1, c, size);
        assertEquals(1, size.getValue());

        node = node.delete(transforms, 1, a, size);
        assertSame(HamtEmptyNode.of(), node);
    }
}
