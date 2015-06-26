package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JImmutableListStressTester
    implements StressTestable
{
    private final JImmutableList<String> list;

    public JImmutableListStressTester(JImmutableList<String> list)
    {
        this.list = list;
    }

    @Override
    public void execute(Random random, JImmutableList<String> tokens)
    {
        JImmutableArrayList<Integer> list = JImmutableArrayList.of();
        ArrayList<Integer> expected = new ArrayList<Integer>();

        int size = random.nextInt(100000);
        System.out.printf("Testing PersistentList of size %d%n", size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", list.size());
            ArrayList<Integer> col = new ArrayList<Integer>();
            for (int i = 0; i < size / 3; ++i) {
                int value = random.nextInt(999999999);
                switch (random.nextInt(5)) {
                case 0:
                    list = list.insert(value);
                    expected.add(value);
                    break;
                case 1:
                    list = list.insertLast(value);
                    expected.add(value);
                    break;
                case 2:
                    list = list.insertFirst(value);
                    expected.add(0, value);
                    break;
                case 3:
                    col.clear();
                    int times = random.nextInt(3);
                    for (int n = 0; n < times; n++) {
                        col.add(random.nextInt(value));
                    }
                    expected.addAll(col);
                    list = list.insertAllLast(col.iterator());
                    break;
                case 4:
                    col.clear();
                    times = random.nextInt(3);
                    for (int n = 0; n < times; n++) {
                        col.add(random.nextInt(value));
                    }
                    expected.addAll(0, col);
                    list = list.insertAllFirst(col.iterator());
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            list.checkInvariants();
            verifyContents(expected, list);
            System.out.printf("shrinking %d%n", list.size());
            for (int i = 0; i < size / 6; ++i) {
                if (random.nextInt(2) == 0) {
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                } else {
                    list = list.deleteFirst();
                    expected.remove(0);
                }
            }
            verifyContents(expected, list);
            list.checkInvariants();
        }
        System.out.printf("cleanup %d%n", expected.size());
        while (list.size() > 0) {
            list = list.deleteLast();
            expected.remove(expected.size() - 1);
        }
        verifyContents(expected, list);
        System.out.println("PersistentList test completed without errors");
    }

    private void verifyContents(List<Integer> expected,
                                JImmutableList<Integer> list)
    {
        System.out.printf("checking contents with size %d%n", list.size());
        if (list.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), list.size()));
        }
        int index = 0;
        for (Integer expectedValue : expected) {
            Integer listValue = list.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %d found %d%n", expectedValue, listValue));
            }
            index += 1;
        }
        index = 0;
        for (Integer listValue : list) {
            Integer expectedValue = expected.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %d found %d%n", expectedValue, listValue));
            }
            index += 1;
        }
    }
}
