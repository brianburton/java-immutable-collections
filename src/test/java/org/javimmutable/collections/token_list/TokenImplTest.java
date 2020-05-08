package org.javimmutable.collections.token_list;

import junit.framework.TestCase;

import static org.javimmutable.collections.token_list.TokenImpl.token;

public class TokenImplTest
    extends TestCase
{
    public void testVarious()
    {
        assertEquals(2, token(1, 2, 3).maxShift());
        assertEquals(2, token(2).indexAt(0));
        assertEquals(0, token(5, 4, 3, 2, 1).indexAt(5));
        assertEquals(0, token(2, 1).indexAt(5));
        assertEquals(0, token(2, 1).indexAt(2));
        assertEquals(2, token(2, 1).indexAt(1));
        assertEquals(1, token(2, 1).indexAt(0));
        assertEquals("2", token(1).withIndexAt(0, 2));
        assertEquals("1.2", token(1, 1).withIndexAt(0, 2));
        assertEquals("2.1", token(1, 1).withIndexAt(1, 2));
    }

    public void testToString()
    {
        assertEquals("0", token(0));
        assertEquals("1.0", token(1, 0));
        assertEquals("2.1.0", token(2, 1, 0));
    }

    public void testBase()
    {
        TokenImpl t = token(1);
        assertEquals("0", t.base(0));
        assertEquals("0.0", t.base(1));
        assertEquals("0.0.0", t.base(2));

        t = token(3, 2, 1);
        assertEquals("3.2.0", t.base(0));
        assertEquals("3.0.0", t.base(1));
        assertEquals("0.0.0", t.base(2));
        assertEquals("0.0.0.0", t.base(3));
    }

    public void testCommonBase()
    {
        assertEquals("0.0", token(1).commonBaseWith(token(2)));
        assertEquals("0.0.0", token(2, 1).commonBaseWith(token(1)));
        assertEquals("0.0.0", token(1).commonBaseWith(token(2, 1)));
        assertEquals("0.0.0", token(1, 2).commonBaseWith(token(2, 1)));

        assertEquals("3.2.0", token(3, 2, 1).commonBaseWith(token(3, 2, 2)));
        assertEquals("0.3.2.0", token(0, 3, 2, 1).commonBaseWith(token(3, 2, 2)));
        assertEquals("0.3.2.0", token(3, 2, 1).commonBaseWith(token(0, 3, 2, 2)));

        assertEquals("3.0.0", token(3, 2, 1).commonBaseWith(token(3, 4, 2)));
        assertEquals("0.3.0.0", token(0, 3, 2, 1).commonBaseWith(token(3, 4, 2)));
        assertEquals("0.3.0.0", token(3, 2, 1).commonBaseWith(token(0, 3, 4, 2)));

        assertEquals("4.3.0.0", token(4, 3, 2, 1).commonBaseWith(token(4, 3, 6, 1)));
    }

    public void testNext()
    {
        assertEquals("1", token(0).next());
        assertEquals("1.0", token(63).next());
        assertEquals("1.1", token(1, 0).next());
        assertEquals("2.0", token(1, 63).next());
        assertEquals("2.0.0", token(1, 63, 63).next());
    }

    public void testSameBaseAt()
    {
        assertEquals(true, TokenImpl.sameBaseAt(0, token(0), token(1)));
        assertEquals(true, TokenImpl.sameBaseAt(0, token(0), token(0, 1)));

        assertEquals(true, TokenImpl.sameBaseAt(0, token(1, 3), token(1, 2)));
        assertEquals(true, TokenImpl.sameBaseAt(1, token(1, 3), token(1, 2)));

        assertEquals(true, TokenImpl.sameBaseAt(0, token(1, 2, 3, 1), token(1, 2, 3, 2)));
        assertEquals(true, TokenImpl.sameBaseAt(1, token(1, 2, 3, 1), token(1, 2, 3, 2)));

        assertEquals(false, TokenImpl.sameBaseAt(0, token(1, 2, 3, 1), token(1, 2, 4, 1)));
        assertEquals(true, TokenImpl.sameBaseAt(1, token(1, 2, 3, 1), token(1, 2, 4, 1)));
    }

    public void testEquivalentTo()
    {
        assertEquals(true, TokenImpl.equivalentTo(token(0), token(0)));
        assertEquals(true, TokenImpl.equivalentTo(token(0, 0), token(0)));
        assertEquals(true, TokenImpl.equivalentTo(token(0), token(0, 0)));

        assertEquals(true, TokenImpl.equivalentTo(token(1, 1), token(1, 1)));
        assertEquals(true, TokenImpl.equivalentTo(token(1, 1), token(0, 1, 1)));
        assertEquals(true, TokenImpl.equivalentTo(token(0, 0, 0, 1, 1), token(1, 1)));

        assertEquals(false, TokenImpl.equivalentTo(token(1, 1), token(2, 1)));
        assertEquals(false, TokenImpl.equivalentTo(token(2, 1), token(1, 1)));
        assertEquals(false, TokenImpl.equivalentTo(token(1, 2, 1), token(1, 1, 1)));
        assertEquals(false, TokenImpl.equivalentTo(token(1, 1, 1), token(1, 2, 1)));
        assertEquals(false, TokenImpl.equivalentTo(token(1, 1, 1), token(1, 1, 2)));
        assertEquals(false, TokenImpl.equivalentTo(token(1, 1, 2), token(1, 1, 1)));
    }

    public void testTrieDepth()
    {
        assertEquals(0, token(0).trieDepth());
        assertEquals(0, token(1).trieDepth());
        assertEquals(1, token(1, 0).trieDepth());

        assertEquals(2, token(2, 0, 0).trieDepth());
        assertEquals(1, token(2, 1, 0).trieDepth());
        assertEquals(0, token(3, 2, 1).trieDepth());

        assertEquals(0, token(0).trieDepth());
        assertEquals(0, token(0, 0).trieDepth());
        assertEquals(0, token(0, 0, 0).trieDepth());
    }

    private void assertEquals(String strValue,
                              TokenImpl token)
    {
        assertEquals(strValue, token.toString());
    }
}
