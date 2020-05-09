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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class TrieNodeTest
    extends TestCase
{
    public void testBasics()
    {
        TrieNode<Integer> root = TrieNode.empty();
        assertEquals("[]", root);
        root = root.assign(TrieToken.token(0), 0);
        assertEquals("[[0,0]]", root);
        root = root.assign(TrieToken.token(1), 1);
        assertEquals("[[0,0],[1,1]]", root);
        root = root.assign(TrieToken.token(63), 63);
        assertEquals("[[0,0],[1,1],[63,63]]", root);
        root = root.assign(TrieToken.token(63).next(), 100);
        assertEquals("[[0,0],[1,1],[63,63],[1.0,100]]", root);
        root = root.assign(TrieToken.token(1, 63).next(), 200);
        assertEquals("[[0,0],[1,1],[63,63],[1.0,100],[2.0,200]]", root);

        root = root.delete(TrieToken.token(63));
        assertEquals("[[0,0],[1,1],[1.0,100],[2.0,200]]", root);
        root = root.delete(TrieToken.token(1, 0));
        assertEquals("[[0,0],[1,1],[2.0,200]]", root);
        root = root.delete(TrieToken.token(0));
        assertEquals("[[1,1],[2.0,200]]", root);
        root = root.delete(TrieToken.token(1));
        assertEquals("[[2.0,200]]", root);
        root = root.delete(TrieToken.token(2, 0));
        assertEquals("[]", root);
        assertSame(TrieNode.empty(), root);
    }

    public void testRandom()
    {
        final Random r = new Random(1000);
        final Map<TrieToken, Integer> map = new LinkedHashMap<>();
        TrieNode<Integer> root = TrieNode.empty();
        TrieToken nextToken = TrieToken.ZERO;
        int nextValue = 1;
        for (int loop = 1; loop <= 40000; ++loop) {
            final int command = r.nextInt(6);
            if (command <= 3) {
                map.put(nextToken, nextValue);
                root = root.assign(nextToken, nextValue);
                nextToken = nextToken.next();
                nextValue += 1;
            } else if (command <= 5 && map.size() > 0) {
                final TrieToken token = randomKey(r, map.keySet());
                map.remove(token);
                root = root.delete(token);
            } else if (map.size() > 0) {
                final TrieToken token = randomKey(r, map.keySet());
                assertEquals(map.get(token), root.getValueOr(token, null));
            }
            if (loop % 1000 == 0) {
                assertEquals(expected(map), root);
            }
        }
        while (map.size() > 0) {
            final TrieToken token = randomKey(r, map.keySet());
            assertEquals(map.get(token), root.getValueOr(token, null));
            map.remove(token);
            root = root.delete(token);
            if (map.size() % 1000 == 0) {
                assertEquals(expected(map), root);
            }
        }
        assertEquals("[]", root);
        assertSame(TrieNode.empty(), root);
    }

    private TrieToken randomKey(Random r,
                                Collection<TrieToken> keys)
    {
        final int size = keys.size();
        for (; ; ) {
            for (TrieToken key : keys) {
                if (r.nextInt(size) == 0) {
                    return key;
                }
            }
        }
    }

    private void assertEquals(String expected,
                              TrieNode<Integer> actual)
    {
        assertEquals(expected, actual.toString());
    }

    @Nonnull
    private String expected(@Nonnull Map<TrieToken, Integer> map)
    {
        final StringBuilder sb = new StringBuilder("[");
        boolean subsequent = false;
        for (Map.Entry<TrieToken, Integer> e : map.entrySet()) {
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
