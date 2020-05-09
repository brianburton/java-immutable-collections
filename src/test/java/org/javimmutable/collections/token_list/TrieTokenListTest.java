package org.javimmutable.collections.token_list;

import junit.framework.TestCase;

import java.util.stream.Collectors;

import static org.javimmutable.collections.token_list.TrieToken.ZERO;

public class TrieTokenListTest
    extends TestCase
{
    public void testVarious()
    {
        JImmutableTokenList<Integer> list = JImmutableTokenList.of();
        assertSame(EmptyTokenList.instance(), list);
        list = list.insertLast(1);
        assertEquals(TrieTokenList.class, list.getClass());
        assertEquals(1, list.size());
        assertEquals(ZERO, list.lastToken());
        assertSame(list, list.delete(ZERO.next()));
        assertSame(EmptyTokenList.instance(), list.delete(ZERO));
        list = list.insertLast(2);
        assertEquals(ZERO.next(), list.lastToken());
        list = list.insertLast(3);
        assertEquals(ZERO.next().next(), list.lastToken());
        list = list.insertLast(4);
        list = list.delete(list.lastToken());
        assertEquals("0,1,2", list.tokens().stream().map(Object::toString).collect(Collectors.joining(",")));
        assertEquals("1,2,3", list.values().stream().map(Object::toString).collect(Collectors.joining(",")));
        assertEquals("[0,1],[1,2],[2,3]", list.entries().stream().map(Object::toString).collect(Collectors.joining(",")));
    }
}
