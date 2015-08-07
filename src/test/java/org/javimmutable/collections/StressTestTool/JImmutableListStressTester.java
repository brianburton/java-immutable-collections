package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test program for all implementations of JImmutableList, including JImmutableRandomAccessList.
 * Divided into five sections: growing (adds new values to the beginning or end of the list),
 * updating (changes values at any index in the list), shrinking (removes values from either
 * end of the list), contains (tests methods that search for values in the list), and cleanup
 * (empties the list of all values).
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
        System.out.printf("JImmutableListStressTest on %s of size %d%n", getName(list), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", list.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(10)) {
                case 0: { //insert(T)
                    String value = makeValue(tokens, random);
                    list = list.insert(value);
                    expected.add(value);
                    break;
                }
                case 1: { //insertFirst(T)
                    String value = makeValue(tokens, random);
                    list = list.insertFirst(value);
                    expected.add(0, value);
                    break;
                }
                case 2: { //insertLast(T)
                    String value = makeValue(tokens, random);
                    list = list.insertLast(value);
                    expected.add(value);
                    break;
                }
                case 3: { //insert(Iterable)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insert(values);
                    expected.addAll(values);
                    break;
                }
                case 4: { //insertAll(Cursorable)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insertAll(IterableCursorable.of(values));
                    expected.addAll(values);
                    break;
                }
                case 5: { //insertAll(Collection)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insertAll(values);
                    expected.addAll(values);
                    break;
                }
                case 6: { //insertAllLast(Cursorable)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insertAllLast(IterableCursorable.of(values));
                    expected.addAll(values);
                    break;
                }
                case 7: { //insertAllLast(Collection)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insertAllLast(values);
                    expected.addAll(values);
                    break;
                }
                case 8: { //insertAllFirst(Cursorable)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insertAllFirst(IterableCursorable.of(values));
                    expected.addAll(0, values);
                    break;
                }
                case 9: { //insertAllFirst(Collection)
                    List<String> values = makeInsertList(tokens, random);
                    list = list.insertAllFirst(values);
                    expected.addAll(0, values);
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(list, expected);

            System.out.printf("updating %d%n", list.size());
            for (int i = 0; i < list.size() / 6; ++i) {
                switch (random.nextInt(2)) {
                case 0: { //assign(int, T)
                    int index = random.nextInt(list.size());
                    String value = (random.nextBoolean()) ? list.get(index) : makeValue(tokens, random);
                    list = list.assign(index, value);
                    expected.set(index, value);
                    break;
                }
                case 1: { //assign(int, T) - throw exception
                    int index = random.nextInt(list.size()) + list.size();
                    try {
                        list.assign(index, makeValue(tokens, random));
                        throw new RuntimeException(String.format("error in assign(index, value) method call - index %d was out of bounds, but method did not fail%n", index));
                    } catch (IndexOutOfBoundsException e) {
                        //ignored -- expected
                    }
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(list, expected);

            System.out.printf("shrinking %d%n", list.size());
            for (int i = 0; i < size / 6 && list.size() > 1; ++i) {
                switch (random.nextInt(2)) {
                case 0: //deleteLast()
                    list = list.deleteLast();
                    expected.remove(expected.size() - 1);
                    break;
                case 1: //deleteFirst()
                    list = list.deleteFirst();
                    expected.remove(0);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(list, expected);

            System.out.printf("contains %d%n", list.size());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(2)) {
                case 0: { //get(int)
                    int index = random.nextInt(list.size());
                    String value = list.get(index);
                    String expectedValue = expected.get(index);
                    if (!value.equals(expectedValue)) {
                        throw new RuntimeException(String.format("get(index) method call failed for %d - expected %s found %s%n", index, expectedValue, value));
                    }
                    break;
                }
                case 1: { //get(int) - throw exception
                    int index = random.nextInt(list.size()) + list.size();
                    try {
                        list.get(index);
                        throw new RuntimeException(String.format("error in get(index) method call - index %d was out of bounds, but method did not fail%n", index));
                    } catch (IndexOutOfBoundsException e) {
                        //ignored -- expected
                    }
                    break;
                }
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(list, expected);
        }

        System.out.printf("cleanup %d%n", expected.size());
        int threshold = random.nextInt(3);
        while (list.size() > threshold) {
            switch (random.nextInt(2)) {
            case 0: //deleteLast()
                list = list.deleteLast();
                expected.remove(expected.size() - 1);
                break;
            case 1: //deleteFirst()
                list = list.deleteFirst();
                expected.remove(0);
                break;
            default:
                throw new RuntimeException();
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
        System.out.printf("JImmutableListStressTest on %s completed without errors%n", getName(list));
    }
}