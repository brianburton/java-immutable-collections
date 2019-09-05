///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections.list;

import junit.framework.TestCase;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.*;

public class EmptyNodeTest
    extends TestCase
{
    private final AbstractNode<Integer> node = EmptyNode.instance();

    public void testVarious()
    {
        final OneValueNode<Integer> leaf = new OneValueNode<>(100);
        assertEquals(true, node.isEmpty());
        assertEquals(0, node.size());
        assertEquals(0, node.depth());
        verifyOutOfBounds(() -> node.get(0));
        assertEquals(leaf, node.append(100));
        assertSame(leaf, node.append(leaf));
        assertEquals(leaf, node.prepend(100));
        assertSame(leaf, node.prepend(leaf));
        verifyOutOfBounds(() -> node.assign(0, 100));
        assertEquals(leaf, node.insert(0, 100));
        verifyOutOfBounds(() -> node.insert(1, 100));
        verifyOutOfBounds(() -> node.deleteFirst());
        verifyOutOfBounds(() -> node.deleteLast());
        verifyOutOfBounds(() -> node.delete(0));
        assertSame(node, node.prefix(0));
        verifyOutOfBounds(() -> node.prefix(1));
        assertSame(node, node.suffix(0));
        verifyOutOfBounds(() -> node.suffix(1));
        Integer[] values = {1};
        node.copyTo(values, 0);
        assertThat(values).isEqualTo(new Integer[]{1});
        verifyUnsupported(() -> node.left());
        verifyUnsupported(() -> node.right());
        verifyUnsupported(() -> node.rotateLeft(leaf));
        verifyUnsupported(() -> node.rotateRight(leaf));
    }

    static void verifyOutOfBounds(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(IndexOutOfBoundsException.class);
    }

    static void verifyUnsupported(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(UnsupportedOperationException.class);
    }
}
