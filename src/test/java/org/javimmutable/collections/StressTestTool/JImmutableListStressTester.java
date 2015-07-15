package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableList, including those that implement
 * JImmutableRandomAccessList. Divided into three sections: growing (adds new values to
 * either end of the list), shrinking (removes values from either end), and cleanup (empties
 * the list of all values)
 */
public class JImmutableListStressTester
        extends AbstractListStressTestable
{
    private JImmutableList<String> list;

    public JImmutableListStressTester(JImmutableList<String> list)
    {
        this.list = list;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("list").insert(makeClassOption(list));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
    {
        JImmutableList<String> list = this.list;
        List<String> expected = new ArrayList<String>();
        int size = random.nextInt(100000);
        System.out.printf("JImmutableListStressTest on %s of size %d%n", list.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", list.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(11)) {
                case 0: //assign(int, T)
                    if (!list.isEmpty()) {
                        String value = makeValue(tokens, random);
                        int index = random.nextInt(list.size());
                        list = list.assign(index, value);
                        expected.set(index, value);
                        break;
                    }
                case 1: //insert(T)
                    String value = makeValue(tokens, random);
                    list = list.insert(value);
                    expected.add(value);
                    break;
                case 2: //insertFirst(T)
                    value = makeValue(tokens, random);
                    list = list.insertFirst(value);
                    expected.add(0, value);
                    break;
                case 3: //insertLast(T)
                    value = makeValue(tokens, random);
                    list = list.insertLast(value);
                    expected.add(value);
                    break;
                case 4: //insert(Iterable)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insert(values);
                    expected.addAll(values);
                    break;
                case 5: //insertAll(Cursorable)
                    values = makeInsertList(tokens, random);
                    list = list.insertAll(IterableCursorable.of(values));
                    expected.addAll(values);
                    break;
                case 6: //insertAll(Collection)
                    values = makeInsertList(tokens, random);
                    list = list.insertAll(values);
                    expected.addAll(values);
                    break;
                case 7: //insertAllLast(Cursorable)
                    values = makeInsertList(tokens, random);
                    list = list.insertAllLast(IterableCursorable.of(values));
                    expected.addAll(values);
                    break;
                case 8: //insertAllLast(Collection)
                    values = makeInsertList(tokens, random);
                    list = list.insertAllLast(values);
                    expected.addAll(values);
                    break;
                case 9: //insertAllFirst(Cursorable)
                    values = makeInsertList(tokens, random);
                    list = list.insertAllFirst(IterableCursorable.of(values));
                    expected.addAll(0, values);
                    break;
                case 10: //insertAllFirst(Collection)
                    values = makeInsertList(tokens, random);
                    list = list.insertAllFirst(values);
                    expected.addAll(0, values);
                    break;
                default:
                    throw new RuntimeException();

                }
            }
            verifyContents(list, expected);
            System.out.printf("shrinking %d%n", list.size());
            for (int i = 0; i < size / 6; ++i) {
                switch (random.nextInt(2)) {
                case 0: //deleteLast()
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                    break;
                case 1: //deleteFirst()
                    list = list.deleteFirst();
                    expected.remove(0);
                    break;
                }
            }
            verifyContents(list, expected);
            verifyCursor(list, expected);
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
        if (list.size() != 0) {
            verifyContents(list, expected);
            list = list.deleteAll();
            expected.clear();
        }
        if (list.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", list.size()));
        }
        verifyContents(list, expected);
        System.out.printf("JImmutableListStressTest on %s completed without errors%n", list.getClass().getSimpleName());
    }
}

