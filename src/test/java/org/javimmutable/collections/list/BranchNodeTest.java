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
import org.javimmutable.collections.Maybe;
import static org.javimmutable.collections.common.TestUtil.verifyOutOfBounds;
import static org.javimmutable.collections.list.MultiValueNode.MAX_SIZE;
import static org.javimmutable.collections.list.MultiValueNode.SPLIT_SIZE;
import static org.javimmutable.collections.list.MultiValueNodeTest.leaf;
import static org.javimmutable.collections.list.MultiValueNodeTest.reversed;

import javax.annotation.Nonnull;

public class BranchNodeTest
    extends TestCase
{
    public void testSeek()
    {
        AbstractNode<Integer> root = branch(branch(leaf(0, MAX_SIZE),
                                                   leaf(MAX_SIZE, 2 * MAX_SIZE)),
                                            branch(leaf(2 * MAX_SIZE, 2 * MAX_SIZE + SPLIT_SIZE),
                                                   leaf(2 * MAX_SIZE + SPLIT_SIZE, 3 * MAX_SIZE + 1)));
        assertThat(root.get(0)).isEqualTo(0);
        assertThat(root.get(MAX_SIZE)).isEqualTo(MAX_SIZE);
        assertThat(root.get(2 * MAX_SIZE)).isEqualTo(2 * MAX_SIZE);
        assertEquals(Maybe.empty(), root.findImpl(-1, () -> Maybe.empty(), value4 -> Maybe.of(value4)));
        assertEquals(Maybe.of(0), root.findImpl(0, () -> Maybe.empty(), value3 -> Maybe.of(value3)));
        assertEquals(Maybe.of(MAX_SIZE), root.findImpl(MAX_SIZE, () -> Maybe.empty(), value2 -> Maybe.of(value2)));
        assertEquals(Maybe.of(2 * MAX_SIZE + 1), root.findImpl(2 * MAX_SIZE + 1, () -> Maybe.empty(), value1 -> Maybe.of(value1)));
        assertEquals(Maybe.empty(), root.findImpl(root.size(), () -> Maybe.empty(), value -> Maybe.of(value)));
    }

    public void testRotateLeft()
    {
        AbstractNode<Integer> start = branch(leaf(0, MAX_SIZE),
                                             branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                                    leaf(2 * MAX_SIZE, 3 * MAX_SIZE)));

        // append keeps the array full as it adds a value to the end
        assertThat(start.append(3 * MAX_SIZE))
            .isEqualTo(branch(branch(leaf(0, MAX_SIZE),
                                     leaf(MAX_SIZE, 2 * MAX_SIZE)),
                              branch(leaf(2 * MAX_SIZE, 3 * MAX_SIZE),
                                     leaf(3 * MAX_SIZE, 3 * MAX_SIZE + 1))));

        // insert splits the array as it adds a value to the end
        assertThat(start.insert(3 * MAX_SIZE, 3 * MAX_SIZE))
            .isEqualTo(branch(branch(leaf(0, MAX_SIZE),
                                     leaf(MAX_SIZE, 2 * MAX_SIZE)),
                              branch(leaf(2 * MAX_SIZE, 2 * MAX_SIZE + SPLIT_SIZE),
                                     leaf(2 * MAX_SIZE + SPLIT_SIZE, 3 * MAX_SIZE + 1))));
    }

    public void testRotateRight()
    {
        AbstractNode<Integer> start = branch(branch(leaf(0, MAX_SIZE),
                                                    leaf(MAX_SIZE, 2 * MAX_SIZE)),
                                             leaf(2 * MAX_SIZE, 3 * MAX_SIZE));

        // prepend keeps the array full as it adds a value to the beginning
        assertThat(start.prepend(-1))
            .isEqualTo(branch(branch(leaf(-1, 0),
                                     leaf(0, MAX_SIZE)),
                              branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                     leaf(2 * MAX_SIZE, 3 * MAX_SIZE))));

        // insert splits the array as it adds a value to the beginning
        assertThat(start.insert(0, -1))
            .isEqualTo(branch(branch(leaf(-1, SPLIT_SIZE),
                                     leaf(SPLIT_SIZE, MAX_SIZE)),
                              branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                     leaf(2 * MAX_SIZE, 3 * MAX_SIZE))));
    }

    public void testDelete()
    {
        AbstractNode<Integer> node = branch(leaf(0, MAX_SIZE), leaf(MAX_SIZE, MAX_SIZE + 2));
        node = node.delete(0);
        assertThat(node).isEqualTo(branch(leaf(1, MAX_SIZE), leaf(MAX_SIZE, MAX_SIZE + 2)));
        node = node.delete(MAX_SIZE);
        assertThat(node).isEqualTo(leaf(1, MAX_SIZE + 1));

        node = branch(leaf(0, 3), leaf(3, MAX_SIZE + 2));
        node = node.delete(2);
        assertThat(node).isEqualTo(branch(leaf(0, 2), leaf(3, MAX_SIZE + 2)));
        node = node.delete(0).delete(0);
        assertThat(node).isEqualTo(leaf(3, MAX_SIZE + 2));

        node = branch(leaf(0, 5), leaf(3, MAX_SIZE));
        node = node.delete(6).delete(5);
        assertThat(node).isEqualTo(leaf(0, MAX_SIZE));

        node = branch(leaf(0, 2), leaf(3, MAX_SIZE + 3));
        node = node.deleteFirst().deleteFirst();
        assertThat(node).isEqualTo(leaf(3, MAX_SIZE + 3));
    }

    public void testPrefix()
    {
        final AbstractNode<Integer> node = branch(branch(leaf(-1, SPLIT_SIZE),
                                                         leaf(SPLIT_SIZE, MAX_SIZE)),
                                                  branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                                         leaf(2 * MAX_SIZE, 3 * MAX_SIZE)));
        final int size = 3 * MAX_SIZE + 1;
        assertThat(node.size()).isEqualTo(size);
        assertThat(node.prefix(size)).isSameAs(node);
        assertThat(node.prefix(0)).isSameAs(EmptyNode.instance());
        assertThat(node.prefix(SPLIT_SIZE + 1)).isSameAs(node.left().left());
        assertThat(node.prefix(MAX_SIZE)).isEqualTo(leaf(-1, MAX_SIZE - 1));
        assertThat(node.prefix(2 * MAX_SIZE)).isEqualTo(branch(branch(leaf(-1, SPLIT_SIZE),
                                                                      leaf(SPLIT_SIZE, MAX_SIZE)),
                                                               leaf(MAX_SIZE, 2 * MAX_SIZE - 1)));
    }

    public void testSuffix()
    {
        final AbstractNode<Integer> node = branch(branch(leaf(-1, SPLIT_SIZE),
                                                         leaf(SPLIT_SIZE, MAX_SIZE)),
                                                  branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                                         leaf(2 * MAX_SIZE, 3 * MAX_SIZE)));
        final int size = 3 * MAX_SIZE + 1;
        assertThat(node.size()).isEqualTo(size);
        assertThat(node.suffix(size)).isSameAs(EmptyNode.instance());
        assertThat(node.suffix(0)).isSameAs(node);
        assertThat(node.suffix(2 * MAX_SIZE + 1)).isSameAs(node.right().right());
        assertThat(node.suffix(2 * MAX_SIZE + 5)).isEqualTo(leaf(2 * MAX_SIZE + 4, 3 * MAX_SIZE));
        assertThat(node.suffix(SPLIT_SIZE)).isEqualTo(branch(leaf(SPLIT_SIZE - 1, MAX_SIZE),
                                                             branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                                                    leaf(2 * MAX_SIZE, 3 * MAX_SIZE))));
    }

    public void testReverse()
    {
        final AbstractNode<Integer> node = branch(branch(leaf(-1, SPLIT_SIZE),
                                                         leaf(SPLIT_SIZE, MAX_SIZE)),
                                                  branch(leaf(MAX_SIZE, 2 * MAX_SIZE),
                                                         leaf(2 * MAX_SIZE, 3 * MAX_SIZE)));
        final AbstractNode<Integer> reverseNode = branch(branch(reversed(2 * MAX_SIZE, 3 * MAX_SIZE),
                                                                reversed(MAX_SIZE, 2 * MAX_SIZE)),
                                                         branch(reversed(SPLIT_SIZE, MAX_SIZE),
                                                                reversed(-1, SPLIT_SIZE)));
        assertThat(node.reverse()).isEqualTo(reverseNode);
        assertThat(reverseNode.reverse()).isEqualTo(node);
    }

    public void testIndexOutOfBounds()
    {
        final AbstractNode<Integer> node = branch(leaf(0, MAX_SIZE), leaf(MAX_SIZE, 2 * MAX_SIZE));
        final int size = 2 * MAX_SIZE;
        assertThat(node.size()).isEqualTo(size);

        verifyOutOfBounds(() -> node.get(-1));
        verifyOutOfBounds(() -> node.get(size));

        verifyOutOfBounds(() -> node.assign(-1, 100));
        verifyOutOfBounds(() -> node.assign(size, 100));

        verifyOutOfBounds(() -> node.insert(-1, 100));
        verifyOutOfBounds(() -> node.insert(size + 1, 100));

        verifyOutOfBounds(() -> node.delete(-1));
        verifyOutOfBounds(() -> node.delete(size));

        verifyOutOfBounds(() -> node.prefix(-1));
        verifyOutOfBounds(() -> node.prefix(size + 1));

        verifyOutOfBounds(() -> node.suffix(-1));
        verifyOutOfBounds(() -> node.suffix(size + 1));
    }

    @Nonnull
    static AbstractNode<Integer> branch(@Nonnull AbstractNode<Integer> left,
                                        @Nonnull AbstractNode<Integer> right)
    {
        return new BranchNode<>(left, right);
    }
}
