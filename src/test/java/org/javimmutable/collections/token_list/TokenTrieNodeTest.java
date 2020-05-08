package org.javimmutable.collections.token_list;

import junit.framework.TestCase;

public class TokenTrieNodeTest
    extends TestCase
{
    public void testBasics()
    {
        TokenTrieNode<Integer> root = TokenTrieNode.empty();
        assertEquals("[]", root);
        root = root.assign(TokenImpl.token(0), 0);
        assertEquals("[[0,0]]", root);
        root = root.assign(TokenImpl.token(1), 1);
        assertEquals("[[0,0],[1,1]]", root);
        root = root.assign(TokenImpl.token(63), 63);
        assertEquals("[[0,0],[1,1],[63,63]]", root);
        root = root.assign(TokenImpl.token(63).next(), 100);
        assertEquals("[[0,0],[1,1],[63,63],[1.0,100]]", root);
        root = root.assign(TokenImpl.token(1, 63).next(), 200);
        assertEquals("[[0,0],[1,1],[63,63],[1.0,100],[2.0,200]]", root);

        root = root.delete(TokenImpl.token(63));
        assertEquals("[[0,0],[1,1],[1.0,100],[2.0,200]]", root);
        root = root.delete(TokenImpl.token(1, 0));
        assertEquals("[[0,0],[1,1],[2.0,200]]", root);
        root = root.delete(TokenImpl.token(0));
        assertEquals("[[1,1],[2.0,200]]", root);
        root = root.delete(TokenImpl.token(1));
        assertEquals("[[2.0,200]]", root);
        root = root.delete(TokenImpl.token(2, 0));
        assertEquals("[]", root);
        assertSame(TokenTrieNode.empty(), root);
    }

    private void assertEquals(String expected,
                              TokenTrieNode<Integer> actual)
    {
        assertEquals(expected, actual.toString());
    }
}
