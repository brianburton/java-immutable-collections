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

package org.javimmutable.collections.token_list;

import junit.framework.TestCase;

import static org.javimmutable.collections.token_list.TokenImpl.*;

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
        assertEquals(true, TokenImpl.sameBaseAt(token(0), token(1), 0));
        assertEquals(true, TokenImpl.sameBaseAt(token(0), token(0, 1), 0));

        assertEquals(true, TokenImpl.sameBaseAt(token(1, 3), token(1, 2), 0));
        assertEquals(true, TokenImpl.sameBaseAt(token(1, 3), token(1, 2), 1));

        assertEquals(true, TokenImpl.sameBaseAt(token(1, 2, 3, 1), token(1, 2, 3, 2), 0));
        assertEquals(true, TokenImpl.sameBaseAt(token(1, 2, 3, 1), token(1, 2, 3, 2), 1));

        assertEquals(false, TokenImpl.sameBaseAt(token(1, 2, 3, 1), token(1, 2, 4, 1), 0));
        assertEquals(true, TokenImpl.sameBaseAt(token(1, 2, 3, 1), token(1, 2, 4, 1), 1));
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
        assertEquals(false, TokenImpl.equivalentTo(token(1, 2, 1), token(2, 1)));
        assertEquals(false, TokenImpl.equivalentTo(token(2, 1), token(1, 2, 1)));
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

    public void testCommonAncestorShift()
    {
        final TokenImpl root = token(0, 21, 0, 0, 0, 1);
        final TokenImpl leaf = token(0, 21, 61, 36, 13, 3);
        final TokenImpl assign = token(0, 21, 61, 36, 0, 0);
        assertEquals(0, leaf.trieDepth());
        assertEquals(2, assign.trieDepth());
        assertEquals(0, root.trieDepth());
        assertEquals(1, maxCommonShift(leaf, assign));
        assertEquals(2, commonAncestorShift(leaf, assign));
        assertEquals(3, commonAncestorShift(root, assign));
        assertEquals(0, commonAncestorShift(token(1), token(2)));
        assertEquals(5, commonAncestorShift(token(3, 0, 0, 0, 0, 0), token(1, 0, 0, 0, 0, 0)));
        assertEquals(4, commonAncestorShift(token(3, 1, 0, 0, 0, 0), token(3, 1, 1, 0, 0, 0)));
        assertEquals(3, commonAncestorShift(token(3, 1, 2, 0, 0, 0), token(3, 1, 1, 0, 0, 0)));
        assertEquals(1, token(3, 1, 3, 0, 1, 0).trieDepth());
        assertEquals(2, commonAncestorShift(token(3, 1, 3, 0, 0, 1), token(3, 1, 3, 1, 0, 0)));
        assertEquals(3, commonAncestorShift(root, leaf));
        assertEquals(2, commonAncestorShift(leaf, assign));
    }

    public void testCache()
    {
        TokenImpl token = ZERO;
        for (int i = 0; i <= 63; ++i) {
            assertSame(token.next(), token.next());
            token = token.next();
        }
        assertNotSame(token.next(), token.next());
        assertEquals(token.next(), token.next());
    }

    private void assertEquals(String strValue,
                              TokenImpl token)
    {
        assertEquals(strValue, token.toString());
    }
}
