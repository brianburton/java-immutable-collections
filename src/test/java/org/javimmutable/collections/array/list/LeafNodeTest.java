package org.javimmutable.collections.array.list;

import junit.framework.TestCase;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeafNodeTest
        extends TestCase
{
    public void testRandomInserts()
    {
        Random r = new Random(10L);
        for (int loop = 1; loop <= 1000; ++loop) {
            List<Integer> expected = new ArrayList<Integer>();
            Node<Integer> node = EmptyNode.of();
            for (int i = 1; i <= 32; ++i) {
                if (r.nextBoolean()) {
                    expected.add(0, i);
                    node = node.insertFirst(i);
                } else {
                    expected.add(i);
                    node = node.insertLast(i);
                }
                assertTrue(node instanceof LeafNode);
                node.checkInvariants();
            }
            StandardCursorTest.listCursorTest(expected, node.cursor());
        }
    }

    public void testRandomDeletes()
    {
        Random r = new Random(10L);
        for (int loop = 1; loop <= 1000; ++loop) {
            List<Integer> expected = new ArrayList<Integer>();
            Node<Integer> node = EmptyNode.of();
            for (int i = 1; i <= 32; ++i) {
                expected.add(i);
                node = node.insertLast(i);
                assertTrue(node instanceof LeafNode);
                node.checkInvariants();
            }
            StandardCursorTest.listCursorTest(expected, node.cursor());
            for (int i = 1; i <= 32; ++i) {
                if (r.nextBoolean()) {
                    expected.remove(0);
                    node = node.deleteFirst();
                } else {
                    expected.remove(expected.size() - 1);
                    node = node.deleteLast();
                }
                if (node.isEmpty()) {
                    assertTrue(node instanceof EmptyNode);
                } else {
                    assertTrue(node instanceof LeafNode);
                }
                node.checkInvariants();
                StandardCursorTest.listCursorTest(expected, node.cursor());
            }
        }
    }

    public void testInsertFirstBranchCreation()
    {
        List<Integer> expected = new ArrayList<Integer>();
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32; ++i) {
            expected.add(0, i);
            node = node.insertFirst(i);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardCursorTest.listCursorTest(expected, node.cursor());
        }
        expected.add(0, 33);
        node = node.insertFirst(33);
        assertTrue(node instanceof BranchNode);
        node.checkInvariants();
        StandardCursorTest.listCursorTest(expected, node.cursor());
    }

    public void testInsertLastBranchCreation()
    {
        List<Integer> expected = new ArrayList<Integer>();
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32; ++i) {
            expected.add(i);
            node = node.insertLast(i);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardCursorTest.listCursorTest(expected, node.cursor());
        }
        expected.add(33);
        node = node.insertLast(33);
        assertTrue(node instanceof BranchNode);
        node.checkInvariants();
        StandardCursorTest.listCursorTest(expected, node.cursor());
    }

    public void testAssign()
    {
        List<Integer> expected = new ArrayList<Integer>();
        Node<Integer> node = EmptyNode.of();
        for (int i = 1; i <= 32; ++i) {
            expected.add(i);
            node = node.insertLast(i);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardCursorTest.listCursorTest(expected, node.cursor());
        }
        for (int x = 0; x < 32; ++x) {
            expected.set(x, 1000 - x);
            node = node.assign(x, 1000 - x);
            assertTrue(node instanceof LeafNode);
            node.checkInvariants();
            StandardCursorTest.listCursorTest(expected, node.cursor());
        }
    }
}
