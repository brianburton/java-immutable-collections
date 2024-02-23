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
