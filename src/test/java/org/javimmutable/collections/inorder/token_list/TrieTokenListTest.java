///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.inorder.token_list;

import junit.framework.TestCase;
import org.javimmutable.collections.common.StandardIterableStreamableTests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.javimmutable.collections.inorder.token_list.TrieToken.ZERO;

public class TrieTokenListTest
    extends TestCase
{
    public void testVarious()
    {
        TokenList<Integer> list = TokenList.of();
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

    public void testIteration()
    {
        List<TokenList.Token> tokens = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        List<TokenList.Entry<Integer>> entries = new ArrayList<>();
        TokenList<Integer> list = TokenList.of();
        for (int i = 0; i <= 10000; ++i) {
            list = list.insertLast(i);
            tokens.add(list.lastToken());
            values.add(i);
            entries.add(new TokenListEntry<>(list.lastToken(), i));
        }
        StandardIterableStreamableTests.verifyOrderedUsingCollection(tokens, list.tokens());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(values, list.values());
        StandardIterableStreamableTests.verifyOrderedUsingCollection(entries, list.entries());
    }
}
