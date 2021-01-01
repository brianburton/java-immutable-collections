///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.stress_test;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomKeyManager
{
    private final Random random;
    private final JImmutableList<String> allPossibleKeys;
    private final List<KeyInfo> allocated;
    private final Map<String, Integer> allocatedIndexes;

    public RandomKeyManager(Random random,
                            JImmutableList<String> allPossibleKeys)
    {
        this.random = random;
        this.allPossibleKeys = allPossibleKeys;
        allocated = new ArrayList<KeyInfo>();
        allocatedIndexes = new HashMap<String, Integer>();
    }

    public static String makeValue(JImmutableList<String> tokens,
                                   Random random)
    {
        int length = 1 + random.nextInt(250);
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(tokens.get(random.nextInt(tokens.size())));
        }
        return sb.toString();
    }

    public String randomKey()
    {
        if (size() == 0) {
            return randomUnallocatedKey();
        } else {
            return random.nextBoolean() ? randomAllocatedKey() : randomUnallocatedKey();
        }
    }

    public JImmutableList<String> randomKeys(int howMany)
    {
        JImmutableList<String> answer = JImmutables.list();
        while (answer.size() < howMany) {
            answer = answer.insertLast(randomKey());
        }
        return answer;
    }

    //on average, adds 1 value to set
    public JImmutableList<String> randomInsertJList()
    {
        String value;
        JImmutableList<String> list = JImmutables.list();
        switch (random.nextInt(8)) {
        case 0:  //adds 0 - empty
            break;
        case 1: //adds 0 - value already in set
            list = (size() == 0) ? list : list.insertLast(randomAllocatedKey());
            break;
        case 2: //adds 1 - unique value
            list = list.insertLast(randomUnallocatedKey());
            break;
        case 3: //adds 1 - unique value, value already in set
            list = (size() == 0) ? list : list.insertLast(randomAllocatedKey());
            list = list.insertLast(randomUnallocatedKey());
            break;
        case 4: //adds 1 - two copies of unique value
            value = randomUnallocatedKey();
            list = list.insert(value).insert(value);
            break;
        case 5: //adds 1 - two copies of unique value, value already in set
            list = (size() == 0) ? list : list.insertLast(randomAllocatedKey());
            value = randomUnallocatedKey();
            list = list.insert(value).insert(value);
            break;
        case 6: //adds 2 - two unique values
        case 7: //adds 2 - two unique values
            list = list.insertAll(randomUniqueUnallocatedKeysJList(2));
            break;
        default:
            throw new RuntimeException();
        }
        return list;
    }

    //on average, deletes 1 value from set
    public JImmutableList<String> randomDeleteJList(int minSize)
    {
        final int availableToDelete = Math.max(0, size() - minSize);
        final int commandMax = (availableToDelete == 0) ? 2 : ((availableToDelete == 1) ? 6 : 8);
        String value;
        JImmutableList<String> list = JImmutables.list();
        switch (random.nextInt(commandMax)) {
        case 0: //deletes 0 - empty
            break;
        case 1: //deletes 0 - value not in set
            list = list.insert(randomUnallocatedKey());
            break;
        case 2: //deletes 1 - value in set
            list = list.insertLast(randomAllocatedKey());
            break;
        case 3: //deletes 1 - two copies of value in set
            value = randomAllocatedKey();
            list = list.insert(value).insert(value);
            break;
        case 4: //deletes 1 - value in set, value not in set
            list = list.insertLast(randomAllocatedKey());
            list = list.insert(randomUnallocatedKey());
            break;
        case 5: //deletes 1 - two copies of value in set, value not in set
            value = randomAllocatedKey();
            list = list.insert(value).insert(value);
            list = list.insert(randomUnallocatedKey());
            break;
        case 6: //deletes 2 - two different values in set
        case 7: //deletes 2 - two different values in set
            list = list.insertAll(randomUniqueAllocatedKeysJList(2));
            break;
        default:
            throw new RuntimeException();
        }
        return list;
    }

    public JImmutableList<String> randomContainsJList(int maxSize)
    {
        if (size() == 0 || random.nextBoolean()) {
            return randomUnallocatedKeysJList(random.nextInt(maxSize));
        } else {
            return randomAllocatedKeysJList(random.nextInt(maxSize));
        }
    }

    public String randomAllocatedKey()
    {
        if (size() == 0) {
            throw new IllegalArgumentException("no allocated values available");
        }
        while (true) {
            final int index = random.nextInt(allocated.size());
            final KeyInfo key = allocated.get(index);
            if (key.present) {
                return key.text;
            }
        }
    }

    public String randomUnallocatedKey()
    {
        while (true) {
            final String text = makeValue(allPossibleKeys, random);
            if (unallocated(text)) {
                return text;
            }
        }
    }

    public JImmutableList<String> randomAllocatedKeysJList(int howMany)
    {
        if (size() == 0) {
            throw new IllegalArgumentException("no allocated values available");
        }
        compact();
        JImmutableList<String> answer = JImmutables.list();
        while (answer.size() < howMany) {
            final int index = random.nextInt(allocated.size());
            final KeyInfo key = allocated.get(index);
            if (key.present) {
                answer = answer.insertLast(key.text);
            }
        }
        return answer;
    }

    public JImmutableList<String> randomUniqueAllocatedKeysJList(int howMany)
    {
        if (size() < howMany) {
            throw new IllegalArgumentException("no allocated values available");
        }
        compact();
        Set<String> values = new HashSet<String>();
        JImmutableList<String> answer = JImmutables.list();
        while (answer.size() < howMany) {
            final int index = random.nextInt(allocated.size());
            final KeyInfo key = allocated.get(index);
            if (key.present && !values.contains(key.text)) {
                values.add(key.text);
                answer = answer.insertLast(key.text);
            }
        }
        return answer;
    }

    public List<String> randomAllocatedKeysList(int howMany)
    {
        return randomAllocatedKeysJList(howMany).getList();
    }

    public JImmutableList<String> randomUnallocatedKeysJList(int howMany)
    {
        compact();
        JImmutableList<String> answer = JImmutables.list();
        while (answer.size() < howMany) {
            answer = answer.insertLast(randomUnallocatedKey());
        }
        return answer;
    }

    public JImmutableList<String> randomUniqueUnallocatedKeysJList(int howMany)
    {
        compact();
        Set<String> values = new HashSet<String>();
        JImmutableList<String> answer = JImmutables.list();
        while (answer.size() < howMany) {
            final String text = randomUnallocatedKey();
            if (!values.contains(text)) {
                values.add(text);
                answer = answer.insertLast(text);
            }
        }
        return answer;
    }

    public List<String> randomUnallocatedKeysList(int howMany)
    {
        return randomUnallocatedKeysJList(howMany).getList();
    }

    public JImmutableList<String> randomIntersectionKeysJList(int howManyUnique,
                                                              int howManyDups,
                                                              int howManyUnallocated)
    {
        if (size() < howManyUnique) {
            throw new IllegalArgumentException("not enough allocated values available");
        }
        if (howManyDups > 0 && howManyUnique == 0) {
            throw new IllegalArgumentException("cannot return dups when no unique returned");
        }
        compact();
        Set<String> uniques = new HashSet<String>();
        JImmutableList<String> answer = JImmutables.list();
        int uniqueCount = 0;
        int dupCount = 0;
        int unallocatedCount = 0;
        while (uniqueCount < howManyUnique || dupCount < howManyDups || unallocatedCount < howManyUnallocated) {
            final String text = (unallocatedCount == howManyUnallocated) ? randomAllocatedKey() : randomKey();
            if (allocated(text)) {
                if (uniques.contains(text)) {
                    if (dupCount < howManyDups) {
                        answer = answer.insertLast(text);
                        dupCount += 1;
                    }
                } else if (uniqueCount < howManyUnique) {
                    uniques.add(text);
                    answer = answer.insertLast(text);
                    uniqueCount += 1;
                }
            } else if (unallocatedCount < howManyUnallocated) {
                answer = answer.insertLast(text);
                unallocatedCount += 1;
            }
        }
        final int expectedCount = howManyUnique + howManyDups + howManyUnallocated;
        if (answer.size() != expectedCount) {
            throw new RuntimeException(String.format("expected %d found %d", expectedCount, answer.size()));
        }
        return answer;
    }

    public List<String> randomIntersectionKeysList(int howManyUnique,
                                                   int howManyDups,
                                                   int howManyUnallocated)
    {
        return randomIntersectionKeysJList(howManyUnique, howManyDups, howManyUnallocated).getList();
    }

    public Set<String> randomIntersectionKeysSet(int howManyUnique,
                                                 int howManyUnallocated)
    {
        return new HashSet<String>(randomIntersectionKeysList(howManyUnique, 0, howManyUnallocated));
    }

    public void allocate(String key)
    {
        final Integer index = allocatedIndexes.get(key);
        if (index == null) {
            allocatedIndexes.put(key, allocated.size());
            allocated.add(new KeyInfo(key));
        } else {
            allocated.get(index).present = true;
        }
    }

    public void allocate(Iterable<String> keys)
    {
        compact();
        for (String key : keys) {
            allocate(key);
        }
    }

    public void unallocate(String key)
    {
        final Integer index = allocatedIndexes.remove(key);
        if (index != null) {
            allocated.get(index).present = false;
        }
    }

    public void unallocate(Iterable<String> keys)
    {
        for (String key : keys) {
            unallocate(key);
        }
        compact();
    }

    public boolean allocated(String key)
    {
        final Integer index = allocatedIndexes.get(key);
        if (index != null) {
            assert allocated.get(index).present;
        }
        return index != null;
    }

    public boolean unallocated(String key)
    {
        final Integer index = allocatedIndexes.get(key);
        if (index != null) {
            assert allocated.get(index).present;
        }
        return index == null;
    }

    public void retainAll(Iterable<String> keys)
    {
        Set<String> keysToRemove = new HashSet<String>(allocatedIndexes.keySet());
        for (String key : keys) {
            keysToRemove.remove(key);
        }
        for (String key : keysToRemove) {
            unallocate(key);
        }
    }

    public void checkInvariants()
    {
        int totalPresent = 0;
        for (int index = 0; index < allocated.size(); ++index) {
            KeyInfo keyInfo = allocated.get(index);
            if (keyInfo == null) {
                throw new RuntimeException(String.format("found null KeyInfo at index %d", index));
            } else if (keyInfo.present) {
                Integer mapIndex = allocatedIndexes.get(keyInfo.text);
                if (mapIndex == null) {
                    throw new RuntimeException(String.format("no mapIndex found for key %s at index %d", keyInfo.text, index));
                } else if (mapIndex != index) {
                    throw new RuntimeException(String.format("wrong mapIndex found for key %s at index %d found %d", keyInfo.text, index, mapIndex));
                }
                totalPresent += 1;
            }
        }
        for (Map.Entry<String, Integer> entry : allocatedIndexes.entrySet()) {
            String text = entry.getKey();
            Integer mapIndex = entry.getValue();
            if (mapIndex == null) {
                throw new RuntimeException(String.format("map has null index for %s", text));
            } else if (mapIndex < 0 || mapIndex >= allocated.size()) {
                throw new RuntimeException(String.format("map has invalid index %d for %s", mapIndex, text));
            } else if (!text.equals(allocated.get(mapIndex).text)) {
                throw new RuntimeException(String.format("map has text mismatch for %s found %s at index %d", text, allocated.get(mapIndex).text, mapIndex));
            } else if (!allocated.get(mapIndex).present) {
                throw new RuntimeException(String.format("map links to !present at index %d for %s", mapIndex, text));
            }
        }
        if (totalPresent != allocatedIndexes.size()) {
            throw new RuntimeException(String.format("map size has %d keys expected %d", allocatedIndexes.size(), totalPresent));
        }
    }

    public void clear()
    {
        allocated.clear();
        allocatedIndexes.clear();
    }

    public int size()
    {
        return allocatedIndexes.size();
    }

    public JImmutableList<String> allAllocatedJList()
    {
        JImmutableList<String> answer = JImmutables.list();
        for (KeyInfo key : allocated) {
            if (key.present) {
                answer = answer.insertLast(key.text);
            }
        }
        return answer;
    }

    void compact()
    {
        if (allocatedIndexes.size() < allocated.size() / 2) {
            allocatedIndexes.clear();
            int index = 0;
            for (Iterator<KeyInfo> keys = allocated.iterator(); keys.hasNext(); ) {
                KeyInfo key = keys.next();
                if (key.present) {
                    allocatedIndexes.put(key.text, index);
                    index += 1;
                } else {
                    keys.remove();
                }
            }
        }
    }

    private static class KeyInfo
    {
        private final String text;
        private boolean present;

        public KeyInfo(String text)
        {
            this.text = text;
            this.present = true;
        }
    }
}
