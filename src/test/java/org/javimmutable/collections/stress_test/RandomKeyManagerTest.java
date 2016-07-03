package org.javimmutable.collections.stress_test;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomKeyManagerTest
        extends TestCase
{
    private JImmutableList<String> allPossibleKeys;
    private RandomKeyManager keys;

    public void setUp()
            throws Exception
    {
        allPossibleKeys = JImmutables.list();
        for (int i = 0; i < 100; ++i) {
            allPossibleKeys = allPossibleKeys.insertLast(String.valueOf(i));
        }
        keys = new RandomKeyManager(new Random(1010101010101L), allPossibleKeys);
    }

    public void testNoneAllocated()
    {
        for (String key : allPossibleKeys) {
            assertEquals(false, keys.allocated(key));
            assertEquals(true, keys.unallocated(key));
        }
        for (int i = 1; i <= 30; ++i) {
            assertEquals(i, keys.randomUnallocatedKeysJList(i).size());
            assertEquals(i, keys.randomUnallocatedKeysList(i).size());
            assertEquals(true, keys.randomIntersectionKeysJList(0, 0, 0).isEmpty());
            JImmutableList<String> values = keys.randomIntersectionKeysJList(0, 0, i);
            assertEquals(i, values.size());
            for (String value : values) {
                assertEquals(false, keys.allocated(value));
                assertEquals(true, keys.unallocated(value));
            }
        }
        try {
            keys.randomAllocatedKeysJList(1);
            fail("");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            keys.randomAllocatedKeysList(1);
            fail("");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            keys.randomIntersectionKeysJList(1, 0, 0);
            fail("");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            keys.randomIntersectionKeysJList(0, 1, 0);
            fail("");
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void testAllocation()
    {
        for (int i = 1; i <= 250; ++i) {
            keys.clear();
            JImmutableList<String> values = keys.randomUnallocatedKeysJList(i);
            assertEquals(i, values.size());
            for (String value : values) {
                assertEquals(false, keys.allocated(value));
                assertEquals(true, keys.unallocated(value));
            }
            keys.allocate(values);
            assertEquals(countUniques(values), keys.size());
            for (String value : values) {
                assertEquals(true, keys.allocated(value));
                assertEquals(false, keys.unallocated(value));
            }
            for (String value : allPossibleKeys) {
                assertEquals(keys.allocated(value), !keys.unallocated(value));
            }
            for (String value : uniques(values)) {
                assertEquals(true, keys.allocated(value));
                assertEquals(false, keys.unallocated(value));
                keys.unallocate(value);
            }
            for (String value : values) {
                assertEquals(false, keys.allocated(value));
                assertEquals(true, keys.unallocated(value));
            }
            assertEquals(0, keys.size());
            keys.checkInvariants();
            keys.compact();
            keys.checkInvariants();
        }
    }

    public void testRandomIntersectionSetJList()
    {
        for (int i = 1; i <= 100; ++i) {
            keys.clear();
            while (keys.size() < i) {
                keys.allocate(keys.randomUnallocatedKey());
            }
            for (int uniqueCount = 1; uniqueCount <= i; ++uniqueCount) {
                for (int dupCount = 0; dupCount <= 5; ++dupCount) {
                    for (int unallocatedCount = 0; unallocatedCount <= 5; ++unallocatedCount) {
                        JImmutableList<String> list = keys.randomIntersectionKeysJList(uniqueCount, dupCount, unallocatedCount);
                        Set<String> visited = new HashSet<String>();
                        int qc = 0;
                        int dc = 0;
                        int uc = 0;
                        for (String value : list) {
                            if (keys.allocated(value)) {
                                if (visited.contains(value)) {
                                    dc += 1;
                                } else {
                                    visited.add(value);
                                    qc += 1;
                                }
                            } else {
                                uc += 1;
                            }
                        }
                        assertEquals(qc, uniqueCount);
                        assertEquals(dc, dupCount);
                        assertEquals(uc, unallocatedCount);
                    }
                }
            }
        }
    }

    public void testRandomIntersectionSetJListAllUnallocated()
    {
        for (int i = 1; i <= 100; ++i) {
            keys.clear();
            while (keys.size() < i) {
                keys.allocate(keys.randomUnallocatedKey());
            }
            for (int unallocatedCount = 0; unallocatedCount <= Math.min(5, allPossibleKeys.size() - keys.size()); ++unallocatedCount) {
                JImmutableList<String> list = keys.randomIntersectionKeysJList(0, 0, unallocatedCount);
                for (String value : list) {
                    assertEquals(false, keys.allocated(value));
                }
                assertEquals(unallocatedCount, list.size());
            }
        }
    }

    private int countUniques(Iterable<String> values)
    {
        return uniques(values).size();
    }

    private Set<String> uniques(Iterable<String> values)
    {
        Set<String> uniques = new HashSet<String>();
        for (String value : values) {
            uniques.add(value);
        }
        return uniques;
    }
}