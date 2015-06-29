package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.cursors.IterableCursorable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JImmutableListStressTester
        extends AbstractListStressTestable
{
    private JImmutableList<String> list;

    public JImmutableListStressTester(JImmutableList<String> list)
    {
        this.list = list;
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
    {
        List<String> expected = new ArrayList<String>();
        int size = random.nextInt(100000);
        System.out.printf("JImmutableListStressTest on %s of size %d%n", list.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", list.size());
            for (int i = 0; i < size / 3; ++i) {
                if (random.nextBoolean()) {
                    String value = makeValue(tokens, random);
                    switch (random.nextInt(3) + 1) {
                    case 0:
                        if (list.isEmpty()) {
                            break;
                        } else {
                            int index = random.nextInt(list.size());
                            list = list.assign(index, value);
                            expected.set(index, value);
                            break;
                        }
                    case 1:
                        list = list.insert(value);
                        expected.add(value);
                        break;
                    case 2:
                        list = list.insertFirst(value);
                        expected.add(0, value);
                        break;
                    case 3:
                        list = list.insertLast(value);
                        expected.add(value);
                        break;
                    default:
                        throw new RuntimeException();
                    }
                } else {
                    List<String> values = new ArrayList<String>();
                    for (int n = 0; n < random.nextInt(3); ++n) {
                        values.add(makeValue(tokens, random));
                    }
                    switch (random.nextInt(7)) {
                    case 0:
                        list = list.insert(values);
                        expected.addAll(values);
                        break;
                    case 1:
                        list = list.insertAll(IterableCursorable.of(values));
                        expected.addAll(values);
                        break;
                    case 2:
                        list = list.insertAll(values);
                        expected.addAll(values);
                        break;
                    case 3:
                        list = list.insertAllLast(IterableCursorable.of(values));
                        expected.addAll(values);
                        break;
                    case 4:
                        list = list.insertAllLast(values);
                        expected.addAll(values);
                        break;
                    case 5:
                        list = list.insertAllFirst(IterableCursorable.of(values));
                        expected.addAll(0, values);
                        break;
                    case 6:
                        list = list.insertAllFirst(values);
                        expected.addAll(0, values);
                        break;
                    default:
                        throw new RuntimeException();
                    }
                }
            }
            verifyContents(expected, list);
            System.out.printf("shrinking %d%n", list.size());
            for (int i = 0; i < size / 6; ++i) {
                if (random.nextBoolean()) {
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                } else {
                    list = list.deleteFirst();
                    expected.remove(0);
                }
            }
            verifyContents(expected, list);
        }
        System.out.printf("cleanup %d%n", expected.size());
        while (list.size() > random.nextInt(3)) {
            if (random.nextBoolean()) {
                list = list.deleteLast();
                expected.remove(expected.size() - 1);
            } else {
                list = list.deleteFirst();
                expected.remove(0);
            }
        }
        verifyContents(expected, list);
        list = list.deleteAll();
        expected.clear();
        verifyContents(expected, list);
        System.out.printf("JImmutableListStressTest on %s completed without errors%n", list.getClass().getSimpleName());
    }
}
