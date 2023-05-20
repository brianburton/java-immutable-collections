///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection.list;

import junit.framework.TestCase;
import org.javimmutable.collection.Maybe;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collection.common.TestUtil.verifyContents;

public class OneValueNodeTest
    extends TestCase
{
    public void testSeek()
    {
        final AbstractNode<Integer> node = new OneValueNode<>(100);
        assertThat(node.get(0)).isEqualTo(100);
        assertThatThrownBy(() -> node.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertEquals(Maybe.empty(), node.seekImpl(-1, () -> Maybe.empty(), value2 -> Maybe.of(value2)));
        assertEquals(Maybe.of(100), node.seekImpl(0, () -> Maybe.empty(), value1 -> Maybe.of(value1)));
        assertEquals(Maybe.empty(), node.seekImpl(1, () -> Maybe.empty(), value -> Maybe.of(value)));
    }

    public void testVarious()
    {
        final EmptyNode<Integer> empty = EmptyNode.instance();
        final OneValueNode<Integer> node = new OneValueNode<>(100);
        final AbstractNode<Integer> large1 = MultiValueNodeTest.leaf(1, MultiValueNode.MAX_SIZE);
        final AbstractNode<Integer> maxed = MultiValueNodeTest.leaf(1, MultiValueNode.MAX_SIZE + 1);
        final AbstractNode<Integer> branch = maxed.append(large1);
        assertSame(empty, node.deleteFirst());
        assertSame(empty, node.deleteLast());
        assertSame(empty, node.delete(0));
        assertSame(empty, node.prefix(0));
        assertSame(node, node.prefix(1));
        assertSame(node, node.suffix(0));
        assertSame(node, node.reverse());
        assertSame(empty, node.suffix(1));
        assertEquals(false, node.isEmpty());
        assertEquals(0, node.depth());
        assertEquals(1, node.size());
        assertSame(node, node.assign(0, 100));
        assertEquals(new OneValueNode<>(10), node.assign(0, 10));
        assertEquals(new MultiValueNode<>(10, 100), node.insert(0, 10));
        assertEquals(new MultiValueNode<>(100, 10), node.insert(1, 10));
        assertEquals(new MultiValueNode<>(10, 100), node.prepend(10));
        assertEquals(new MultiValueNode<>(100, 10), node.append(10));
        assertEquals(large1.append(100), node.prepend(large1));
        assertEquals(large1.prepend(100), node.append(large1));
        assertEquals(new BranchNode<>(maxed, node), node.prepend(maxed));
        assertEquals(new BranchNode<>(node, maxed), node.append(maxed));
        verifyContents(branch.append(100), node.prepend(branch));
        verifyContents(branch.prepend(100), node.append(branch));
        assertSame(node, node.append(empty));
        assertSame(node, node.prepend(empty));
        assertEquals(new Integer(100), node.get(0));
        assertSame(empty, node.delete(0));
        assertThatThrownBy(() -> node.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.assign(1, 200)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.assign(2, 100)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.insert(2, 100)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.delete(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.prefix(2)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> node.suffix(2)).isInstanceOf(IndexOutOfBoundsException.class);
    }
}
