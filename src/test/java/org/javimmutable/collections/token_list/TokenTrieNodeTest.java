package org.javimmutable.collections.token_list;

import junit.framework.TestCase;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

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

    public void testRandom()
    {
        final Random r = new Random(1000);
        final Map<TokenImpl, Integer> map = new LinkedHashMap<>();
        TokenTrieNode<Integer> root = TokenTrieNode.empty();
        TokenImpl nextToken = TokenImpl.ZERO;
        int nextValue = 1;
        for (int loop = 1; loop <= 40000; ++loop) {
            final int command = r.nextInt(6);
            if (command <= 3) {
                map.put(nextToken, nextValue);
                root = root.assign(nextToken, nextValue);
                nextToken = nextToken.next();
                nextValue += 1;
            } else if (command <= 5 && map.size() > 0) {
                final TokenImpl token = randomKey(r, map.keySet());
                map.remove(token);
                root = root.delete(token);
            } else if (map.size() > 0) {
                final TokenImpl token = randomKey(r, map.keySet());
                assertEquals(map.get(token), root.getValueOr(token, null));
            }
            if (loop % 1000 == 0) {
                assertEquals(expected(map), root);
            }
        }
        while (map.size() > 0) {
            final TokenImpl token = randomKey(r, map.keySet());
            assertEquals(map.get(token), root.getValueOr(token, null));
            map.remove(token);
            root = root.delete(token);
            if (map.size() % 1000 == 0) {
                assertEquals(expected(map), root);
            }
        }
        assertEquals("[]", root);
        assertSame(TokenTrieNode.empty(), root);
    }

    private TokenImpl randomKey(Random r,
                                Collection<TokenImpl> keys)
    {
        final int size = keys.size();
        for (; ; ) {
            for (TokenImpl key : keys) {
                if (r.nextInt(size) == 0) {
                    return key;
                }
            }
        }
    }

    private void assertEquals(String expected,
                              TokenTrieNode<Integer> actual)
    {
        assertEquals(expected, actual.toString());
    }

    @Nonnull
    private String expected(@Nonnull Map<TokenImpl, Integer> map)
    {
        final StringBuilder sb = new StringBuilder("[");
        boolean subsequent = false;
        for (Map.Entry<TokenImpl, Integer> e : map.entrySet()) {
            if (subsequent) {
                sb.append(",");
            } else {
                subsequent = true;
            }
            sb.append("[");
            sb.append(e.getKey());
            sb.append(",");
            sb.append(e.getValue());
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }
}
