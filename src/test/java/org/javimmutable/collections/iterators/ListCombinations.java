package org.javimmutable.collections.iterators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListCombinations
{
    public static List<List<Integer>> listsOfLength(int minLength,
                                                    int maxLength)
    {
        List<List<Integer>> answer = new ArrayList<>();
        for (int len = minLength; len <= maxLength; ++len) {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i <= len; ++i) {
                list.add(i);
            }
            answer.add(list);
        }
        return answer;
    }

    public static List<List<List<Integer>>> combosOfListsOfLength(int n,
                                                                  int minLength,
                                                                  int maxLength)
    {
        return combos(n, () -> listsOfLength(minLength, maxLength));
    }

    public static void renumberCombos(List<List<List<Integer>>> combos)
    {
        for (List<List<Integer>> combo : combos) {
            int value = 1;
            for (List<Integer> integers : combo) {
                for (int i = 0; i < integers.size(); ++i) {
                    integers.set(i, value++);
                }
            }
        }
    }

    public static List<Integer> valuesFrom(List<List<Integer>> combos) {
        List<Integer> values = new ArrayList<>();
        for (List<Integer> combo : combos) {
            values.addAll(combo);
        }
        return values;
    }

    public static <T> List<List<T>> combos(int n,
                                           Supplier<List<T>> things)
    {
        List<List<T>> combos = new ArrayList<>();
        if (n == 0) {
            // do nothing;
        } else if (n == 1) {
            for (T thing : things.get()) {
                List<T> combo = new ArrayList<>();
                combo.add(thing);
                combos.add(combo);
            }
        } else {
            for (T thing : things.get()) {
                for (List<T> combo : combos(n - 1, things)) {
                    combo.add(0, thing);
                    combos.add(combo);
                }
            }
        }
        return combos;
    }
}
