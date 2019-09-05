package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.javimmutable.collections.common.StandardCollisionMapTests;

public class ListCollisionMapTest
    extends TestCase
{
    public void testStandard()
    {
        StandardCollisionMapTests.randomTests(ListCollisionMap.instance());
    }
}
