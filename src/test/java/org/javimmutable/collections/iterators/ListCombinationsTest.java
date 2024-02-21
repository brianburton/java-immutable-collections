package org.javimmutable.collections.iterators;

import static org.javimmutable.collections.iterators.ListCombinations.combosOfListsOfLength;
import static org.javimmutable.collections.iterators.ListCombinations.listsOfLength;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;

public class ListCombinationsTest
{
    @Test
    public void testListsOfLength()
    {
        assertEquals(List.of(List.of()), listsOfLength(0, 0));
        assertEquals(List.of(List.of(), List.of(1)), listsOfLength(0, 1));
        assertEquals(List.of(List.of(), List.of(1), List.of(1, 2)), listsOfLength(0, 2));
        assertEquals(List.of(List.of(), List.of(1), List.of(1, 2), List.of(1, 2, 3)), listsOfLength(0, 3));
    }

    @Test
    public void testCombos()
    {
        assertEquals(List.of(), combosOfListsOfLength(0, 1, 2));
        assertEquals(List.of(List.of(List.of(1)),
                             List.of(List.of(1, 2))),
                     combosOfListsOfLength(1, 1, 2));
        assertEquals(List.of(List.of(List.of(1), List.of(1)),
                             List.of(List.of(1), List.of(1, 2)),
                             List.of(List.of(1, 2), List.of(1)),
                             List.of(List.of(1, 2), List.of(1, 2))),
                     combosOfListsOfLength(2, 1, 2));
        assertEquals(List.of(List.of(List.of(1), List.of(1), List.of(1)),
                             List.of(List.of(1), List.of(1), List.of(1, 2)),
                             List.of(List.of(1), List.of(1, 2), List.of(1)),
                             List.of(List.of(1), List.of(1, 2), List.of(1, 2)),

                             List.of(List.of(1, 2), List.of(1), List.of(1)),
                             List.of(List.of(1, 2), List.of(1), List.of(1, 2)),
                             List.of(List.of(1, 2), List.of(1, 2), List.of(1)),
                             List.of(List.of(1, 2), List.of(1, 2), List.of(1, 2))),
                     combosOfListsOfLength(3, 1, 2));
    }

    public void testRenumberCombos() {
        List<List<List<Integer>>> combos = combosOfListsOfLength(3, 1, 2);
        ListCombinations.renumberCombos(combos);
        assertEquals(List.of(List.of(List.of(1), List.of(2), List.of(3)),
                             List.of(List.of(1), List.of(2), List.of(3, 4)),
                             List.of(List.of(1), List.of(2, 3), List.of(4)),
                             List.of(List.of(1), List.of(2, 3), List.of(4, 5)),

                             List.of(List.of(1, 2), List.of(3), List.of(4)),
                             List.of(List.of(1, 2), List.of(3), List.of(4, 5)),
                             List.of(List.of(1, 2), List.of(3, 4), List.of(5)),
                             List.of(List.of(1, 2), List.of(3, 4), List.of(5, 6))),
                     combos);
    }
}
