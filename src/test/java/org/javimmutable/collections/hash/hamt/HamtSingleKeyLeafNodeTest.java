package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.list.ListCollisionMap;

public class HamtSingleKeyLeafNodeTest
    extends TestCase
{
    private final int HASH_CODE = 12;
    private final ListCollisionMap<String, String> collisionMap = ListCollisionMap.instance();
    private final HamtSingleKeyLeafNode<String, String> ten = new HamtSingleKeyLeafNode<>(HASH_CODE, "10", "ten");

    public void testOperations()
    {
        assertEquals(Holders.of(), ten.find(collisionMap, 0, "10"));
        assertEquals(Holders.of(), ten.find(collisionMap, 0, "20"));
        assertEquals(Holders.of("ten"), ten.find(collisionMap, HASH_CODE, "10"));

        assertEquals("nope", ten.getValueOr(collisionMap, 0, "10", "nope"));
        assertEquals("nope", ten.getValueOr(collisionMap, 0, "20", "nope"));
        assertEquals("ten", ten.getValueOr(collisionMap, HASH_CODE, "10", "nope"));

        assertSame(ten, ten.assign(collisionMap, HASH_CODE, "10", "ten"));
        assertEquals("(0xc,10,ten-change)", ten.assign(collisionMap, HASH_CODE, "10", "ten-change").toString());
        assertEquals("(2,0x3000,2,[],[(0x0,10,ten),(0x0,20,twenty)])", ten.assign(collisionMap, HASH_CODE + 1, "20", "twenty").toString());
        assertEquals("(0xc,[10=ten,20=twenty])", ten.assign(collisionMap, HASH_CODE, "20", "twenty").toString());

        assertSame(ten, ten.update(collisionMap, HASH_CODE, "10", h -> h.getValueOr("new")));
        assertEquals("(0xc,10,ten-change)", ten.update(collisionMap, HASH_CODE, "10", h -> h.isFilled() ? h.getValue() + "-change" : "new").toString());
        assertEquals("(2,0x3000,2,[],[(0x0,10,ten),(0x0,20,twenty)])", ten.update(collisionMap, HASH_CODE + 1, "20", h -> h.getValueOr("twenty")).toString());
        assertEquals("(0xc,[10=ten,20=twenty])", ten.update(collisionMap, HASH_CODE, "20", h -> "twenty").toString());

        assertSame(ten, ten.delete(collisionMap, 0, "10"));
        assertSame(ten, ten.delete(collisionMap, HASH_CODE, "20"));
        assertSame(HamtEmptyNode.of(), ten.delete(collisionMap, HASH_CODE, "10"));

        assertEquals("(0x1e3,10,ten)", ten.liftNode(99).toString());
    }
}
