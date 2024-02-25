///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.common.TestUtil;
import static org.javimmutable.collections.list.MultiValueNode.MAX_SIZE;
import static org.javimmutable.collections.list.MultiValueNode.SPLIT_SIZE;

public class MultiValueNodeTest
    extends TestCase
{
    public void testSeek()
    {
        final AbstractNode<Integer> node = leaf(0, 1);
        assertThat(node.get(0)).isEqualTo(0);
        assertThatThrownBy(() -> node.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertEquals(Maybe.empty(), node.findImpl(-1, () -> Maybe.empty(), value2 -> Maybe.of(value2)));
        assertEquals(Maybe.of(0), node.findImpl(0, () -> Maybe.empty(), value1 -> Maybe.of(value1)));
        assertEquals(Maybe.empty(), node.findImpl(1, () -> Maybe.empty(), value -> Maybe.of(value)));
    }

    public void testVarious()
    {
        assertThat(leaf(0, 1).isEmpty()).isFalse();
        assertThat(leaf(0, 1).size()).isEqualTo(1);
        assertThat(leaf(0, 5).size()).isEqualTo(5);
        assertThat(leaf(0, 1).depth()).isEqualTo(0);
        assertThat(leaf(0, 5).get(0)).isEqualTo(0);
        assertThat(leaf(0, 5).get(4)).isEqualTo(4);
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).get(-1));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).get(5));

        assertThat(leaf(0, 5).append(5)).isEqualTo(leaf(0, 6));
        assertThat(leaf(0, 5).append(leaf(5, 10))).isEqualTo(leaf(0, 10));
        assertThat(leaf(0, MAX_SIZE - 3).append(leaf(MAX_SIZE - 3, MAX_SIZE))).isEqualTo(leaf(0, MAX_SIZE));
        assertThat(leaf(0, MAX_SIZE - 3).append(leaf(MAX_SIZE - 3, MAX_SIZE + 1))).isEqualTo(new BranchNode<>(leaf(0, MAX_SIZE - 3), leaf(MAX_SIZE - 3, MAX_SIZE + 1)));
        assertThat(leaf(0, MAX_SIZE).append(MAX_SIZE)).isEqualTo(new BranchNode<>(leaf(0, SPLIT_SIZE), leaf(SPLIT_SIZE, MAX_SIZE + 1)));

        assertThat(leaf(1, 5).prepend(0)).isEqualTo(leaf(0, 5));
        assertThat(leaf(5, 10).prepend(leaf(0, 5))).isEqualTo(leaf(0, 10));
        assertThat(leaf(MAX_SIZE - 3, MAX_SIZE).prepend(leaf(0, MAX_SIZE - 3))).isEqualTo(leaf(0, MAX_SIZE));
        assertThat(leaf(MAX_SIZE - 3, MAX_SIZE + 1).prepend(leaf(0, MAX_SIZE - 3))).isEqualTo(new BranchNode<>(leaf(0, MAX_SIZE - 3), leaf(MAX_SIZE - 3, MAX_SIZE + 1)));
        assertThat(leaf(1, MAX_SIZE + 1).prepend(0)).isEqualTo(new BranchNode<>(leaf(0, SPLIT_SIZE + 1), leaf(SPLIT_SIZE + 1, MAX_SIZE + 1)));

        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).assign(-1, 9));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).assign(5, 9));
        assertThat(leaf(0, 7).assign(3, 9)).isEqualTo(leaf(0, 7, 3, 9));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).assign(5, 9));

        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).insert(-1, 9));
        assertThat(leaf(0, 5).insert(3, 9)).isEqualTo(new MultiValueNode<>(new Integer[]{0, 1, 2, 9, 3, 4}, 6));
        assertThat(leaf(0, 5).insert(5, 9)).isEqualTo(new MultiValueNode<>(new Integer[]{0, 1, 2, 3, 4, 9}, 6));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).insert(6, 9));

        assertThat(leaf(0, 5).deleteFirst()).isEqualTo(leaf(1, 5));
        assertThat(leaf(0, 5).deleteLast()).isEqualTo(leaf(0, 4));

        assertThat(leaf(0, 1).delete(0)).isSameAs(EmptyNode.instance());
        TestUtil.verifyOutOfBounds(() -> leaf(0, 1).delete(-1));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 1).delete(1));

        assertThat(leaf(0, 5).delete(0)).isEqualTo(leaf(1, 5));
        assertThat(leaf(0, 5).delete(4)).isEqualTo(leaf(0, 4));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).delete(-1));
        TestUtil.verifyOutOfBounds(() -> leaf(0, 5).delete(5));

        final AbstractNode<Integer> self = leaf(0, 5);
        assertThat(self.prefix(0)).isSameAs(EmptyNode.instance());
        assertThat(self.prefix(5)).isSameAs(self);
        assertThat(self.prefix(3)).isEqualTo(leaf(0, 3));
        TestUtil.verifyOutOfBounds(() -> self.prefix(-1));
        TestUtil.verifyOutOfBounds(() -> self.prefix(6));

        assertThat(self.suffix(0)).isSameAs(self);
        assertThat(self.suffix(5)).isSameAs(EmptyNode.instance());
        assertThat(self.suffix(1)).isEqualTo(leaf(1, 5));
        assertThat(self.suffix(3)).isEqualTo(leaf(3, 5));
        TestUtil.verifyOutOfBounds(() -> self.suffix(-1));
        TestUtil.verifyOutOfBounds(() -> self.suffix(6));

        Integer[] values = new Integer[]{-1, -1, -1, -1, -1, -1, -1};
        self.copyTo(values, 1);
        assertThat(values).isEqualTo(new Integer[]{-1, 0, 1, 2, 3, 4, -1});

        TestUtil.verifyUnsupported(() -> self.left());
        TestUtil.verifyUnsupported(() -> self.right());
    }

    public void testReverse()
    {
        for (int len = 2; len <= MultiValueNode.MAX_SIZE; ++len) {
            AbstractNode<Integer> node = leaf(0, len);
            assertThat(node.reverse()).isEqualTo(reversed(0, len));
            assertThat(node.reverse().reverse()).isEqualTo(node);
        }
    }

    static AbstractNode<Integer> leaf(int start,
                                      int limit)
    {
        final int length = limit - start;
        if (length == 0) {
            return EmptyNode.instance();
        } else if (length == 1) {
            return new OneValueNode<>(start);
        } else {
            Integer[] values = new Integer[length];
            for (int i = 0; i < length; ++i) {
                values[i] = start + i;
            }
            return new MultiValueNode<>(values, length);
        }
    }

    static AbstractNode<Integer> reversed(int start,
                                          int limit)
    {
        final int length = limit - start;
        if (length == 0) {
            return EmptyNode.instance();
        } else if (length == 1) {
            return new OneValueNode<>(start);
        } else {
            final int max = start + length - 1;
            Integer[] values = new Integer[length];
            for (int i = 0; i < length; ++i) {
                values[i] = max - i;
            }
            return new MultiValueNode<>(values, length);
        }
    }

    static AbstractNode<Integer> leaf(int start,
                                      int limit,
                                      int index,
                                      int value)
    {
        final int length = limit - start;
        Integer[] values = new Integer[length];
        for (int i = 0; i < length; ++i) {
            values[i] = start + i;
        }
        values[index] = value;
        if (length == 0) {
            return EmptyNode.instance();
        } else if (length == 1) {
            return new OneValueNode<>(start);
        } else {
            return new MultiValueNode<>(values, length);
        }
    }
}
