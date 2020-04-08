package org.javimmutable.collections.tree;

import junit.framework.TestCase;

public class ValueNodeTest
    extends TestCase
{
    public void testToString()
    {
        assertEquals("[]", FringeNode.instance().toString());
        assertEquals("[(1,2)]", ValueNode.instance(1, 2).toString());
    }
}
