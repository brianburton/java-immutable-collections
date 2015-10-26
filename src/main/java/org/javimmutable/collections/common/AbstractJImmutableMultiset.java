///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Immutable
public abstract class AbstractJImmutableMultiset<T>
        implements JImmutableMultiset<T>
{
    private final JImmutableMap<T, Integer> map;
    private final int occurrences;

    protected AbstractJImmutableMultiset(JImmutableMap<T, Integer> map,
                                         int occurrences)
    {
        this.map = map;
        this.occurrences = occurrences;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insert(@Nonnull T value)
    {
        JImmutableMap<T, Integer> newMap = increaseCount(map, value, 1);
        return (newMap != map) ? create(newMap, 1 + occurrences) : this;
    }

    @Nonnull
    @Override
    public JImmutableMultiset<T> insert(@Nonnull T value,
                                        int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else if (count == 0) {
            return this;
        } else {
            JImmutableMap<T, Integer> newMap = increaseCount(map, value, count);
            return (newMap != map) ? create(newMap, count + occurrences) : this;
        }
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && (count(value) > 0);
    }

    @Override
    public boolean containsAtLeast(@Nullable T value,
                                   int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            return (value != null) && (count(value) >= count);
        }
    }

    @Override
    public boolean containsAll(@Nonnull Cursorable<? extends T> other)
    {
        return containsAll(other.cursor());
    }

    @Override
    public boolean containsAll(@Nonnull Collection<? extends T> other)
    {
        return containsAll(other.iterator());
    }

    @Override
    public boolean containsAll(@Nonnull Cursor<? extends T> other)
    {
        return containsAll(other.iterator());
    }

    @Override
    public boolean containsAll(@Nonnull Iterator<? extends T> other)
    {
        while (other.hasNext()) {
            if (!(contains(other.next()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Cursorable<? extends T> other)
    {
        return containsAllOccurrences(other.cursor());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Collection<? extends T> other)
    {
        return containsAllOccurrences(other.iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Cursor<? extends T> other)
    {
        return containsAllOccurrences(other.iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> checkMap = map;
        while (other.hasNext()) {
            final T value = other.next();
            final int count = getCount(checkMap, value);
            if ((value != null) && (count > 0)) {
                checkMap = checkMap.assign(value, count - 1);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return containsAllOccurrencesMultisetHelper(values);
    }


    @Override
    public boolean containsAllOccurrences(@Nonnull JImmutableSet<? extends T> values)
    {
        return containsAllOccurrencesSetHelper(values.cursor().iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Set<? extends T> values)
    {
        return containsAllOccurrencesSetHelper(values.iterator());
    }


    @Override
    public boolean containsAny(@Nonnull Cursorable<? extends T> other)
    {
        return containsAny(other.cursor());
    }

    @Override
    public boolean containsAny(@Nonnull Collection<? extends T> other)
    {
        return containsAny(other.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Cursor<? extends T> other)
    {
        return containsAny(other.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Iterator<? extends T> other)
    {
        while (other.hasNext()) {
            if (this.contains(other.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> delete(@Nonnull T value)
    {
        JImmutableMap<T, Integer> newMap = map.delete(value);
        return (newMap != map) ? create(newMap, occurrences - this.count(value)) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value)
    {
        JImmutableMap<T, Integer> newMap = decreaseCount(map, value, 1);
        return (newMap != map) ? create(newMap, occurrences - 1) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value,
                                                  int subtractBy)
    {
        if (subtractBy < 0) {
            throw new IllegalArgumentException();
        } else {
            JImmutableMap<T, Integer> newMap;
            int newOccurrences;
            int oldCount = this.count(value);
            if (subtractBy >= oldCount) {
                newMap = map.delete(value);
                newOccurrences = occurrences - oldCount;
            } else {
                newMap = decreaseCount(map, value, subtractBy);
                newOccurrences = occurrences - subtractBy;
            }
            return (newMap != map) ? create(newMap, newOccurrences) : this;
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAll(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Collection<? extends T> other)
    {
        return deleteAll(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Cursor<? extends T> other)
    {
        return deleteAll(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (getCount(newMap, value) > 0)) {
                newMap = newMap.delete(value);
                newOccurrences = newOccurrences - this.count(value);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAllOccurrences(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Collection<? extends T> other)
    {
        return deleteAllOccurrences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursor<? extends T> other)
    {
        return deleteAllOccurrences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (getCount(newMap, value) > 0)) {
                newMap = decreaseCount(newMap, value, 1);
                newOccurrences = newOccurrences - 1;
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return deleteAllOccurrencesHelper(other);
    }


    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(values.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Collection<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = increaseCount(newMap, value, 1);
                newOccurrences = newOccurrences + 1;
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return insertAllHelper(values);
    }


    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Cursorable<? extends T> other)
    {
        return union(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Collection<? extends T> other)
    {
        return union(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Cursor<? extends T> other)
    {
        return union(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        Map<T, Integer> otherMap = new HashMap<T, Integer>();
        int newOccurrences = occurrences;
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                int otherCount = (otherMap.containsKey(value)) ? otherMap.get(value) : 0;
                ++otherCount;
                otherMap.put(value, otherCount);
                Integer newCount = getCount(newMap, value);
                if (otherCount > newCount) {
                    newOccurrences = newOccurrences - newCount + otherCount;
                    newMap = newMap.assign(value, otherCount);
                }
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return unionMultisetHelper(other);
    }


    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull JImmutableSet<? extends T> other)
    {
        return unionSetHelper(other.cursor().iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Set<? extends T> other)
    {
        return unionSetHelper(other.iterator());
    }


    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Cursorable<? extends T> other)
    {
        return intersection(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Collection<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Cursor<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = emptyMap();
        int newOccurrences = 0;
        Map<T, Integer> otherMap = new HashMap<T, Integer>();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                int currentCount = this.count(value);
                if (currentCount > 0) {
                    Integer newCount = getCount(newMap, value);
                    if (currentCount > newCount) {
                        int otherCount = otherMap.containsKey(value) ? otherMap.get(value) : 0;
                        ++otherCount;
                        otherMap.put(value, otherCount);
                        newOccurrences -= newCount;
                        newOccurrences += (currentCount > otherCount) ? otherCount : currentCount;
                        newMap = (currentCount > otherCount) ? newMap.assign(value, otherCount) : newMap.assign(value, currentCount);
                    }
                }
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;

    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableMultiset<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else if (other.size() < size()) {
            return multisetIntersectionWithEmptyMap(other);
        } else {
            return multisetIntersectionWithFilledMap(other);
        }
    }


    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableSet<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else if (other.size() < size()) {
            return setIntersectionWithEmptyMap(other.cursor().iterator());
        } else {
            return setIntersectionWithFilledMap(other.getSet());
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else if (other.size() < size()) {
            return setIntersectionWithEmptyMap(other.iterator());
        } else {
            return setIntersectionWithFilledMap(other);
        }
    }


    @Override
    public int count(@Nonnull T value)
    {
        Conditions.stopNull(value);
        return map.getValueOr(value, 0);
    }

    @Nonnull
    @Override
    public JImmutableMultiset<T> setCount(@Nonnull T value,
                                          int count)
    {
        Conditions.stopNull(value, count);
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            int currentCount = this.count(value);
            if (count == currentCount) {
                return this;
            } else {
                int newOccurrences = occurrences - currentCount + count;
                return (count > 0) ? create(map.assign(value, count), newOccurrences) : delete(value);
            }
        }
    }


    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map         base map for new multiset
     * @param occurrences total occurrences in map
     * @return new multiset built from map
     */
    protected abstract JImmutableMultiset<T> create(JImmutableMap<T, Integer> map,
                                                    int occurrences);


    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public int occurrenceCount()
    {
        return occurrences;
    }

    @Override
    @Nonnull
    public Set<T> getSet()
    {
        return SetAdaptor.of(this);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    @Nonnull
    public Cursor<T> occurrenceCursor()
    {
        return MultiTransformCursor.of(entryCursor(), new Func1<JImmutableMap.Entry<T, Integer>, Cursor<T>>()
        {
            @Override
            public Cursor<T> apply(JImmutableMap.Entry<T, Integer> entry)
            {
                return StandardCursor.of(new StandardCursor.RepeatingValueCursorSource<T>(entry));
            }
        });

    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return map.keysCursor();
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<T, Integer>> entryCursor()
    {
        return map.cursor();
    }

    @Override
    public int hashCode()
    {
        return Cursors.computeHashCode(occurrenceCursor());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof JImmutableMultiset) {
            //noinspection unchecked
            return (occurrenceCount() == ((JImmutableMultiset)o).occurrenceCount()) && ((JImmutableMultiset)o).containsAllOccurrences(this);
        } else if (o instanceof JImmutableSet) {
            return (size() == occurrences) && getSet().equals(((JImmutableSet)o).getSet());
        } else {
            //noinspection EqualsBetweenInconvertibleTypes
            return (o instanceof Set) && (size() == occurrences) && getSet().equals(o);
        }
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(occurrenceCursor());
    }


    private JImmutableMap<T, Integer> increaseCount(JImmutableMap<T, Integer> map,
                                                    T value,
                                                    int addBy)
    {
        int newCount = getCount(map, value) + addBy;
        return map.assign(value, newCount);
    }

    protected abstract JImmutableMap<T, Integer> emptyMap();

    public void checkInvariants()
    {
        map.checkInvariants();
        if (occurrences < map.size()) {
            throw new IllegalStateException();
        }
        int checkOccurrences = 0;
        for (JImmutableMap.Entry<T, Integer> entry : entryCursor()) {
            int entryCount = entry.getValue();
            if (entryCount <= 0) {
                throw new IllegalStateException(String.format("illegal count of %d for value %s%n", entryCount, entry.getKey()));
            }
            checkOccurrences += entryCount;
        }
        if (occurrences != checkOccurrences) {
            throw new RuntimeException(String.format("occurrence size mismatch - expected %d found %d%n", checkOccurrences, occurrences));
        }
    }

    private JImmutableMap<T, Integer> decreaseCount(JImmutableMap<T, Integer> map,
                                                    T value,
                                                    int subtractBy)
    {
        Integer oldCount = getCount(map, value);
        if (subtractBy == 0 || oldCount == 0) {
            return map;
        } else {
            int newCount = oldCount - subtractBy;
            return (newCount > 0) ? map.assign(value, newCount) : map.delete(value);
        }
    }

    private Integer getCount(JImmutableMap<T, Integer> checkMap,
                             T value)
    {
        return checkMap.getValueOr(value, 0);
    }

    private <T1 extends T> boolean containsAllOccurrencesMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 entryValue = e.getValue().getKey();
            final int entryCount = e.getValue().getValue();
            if (!this.contains(entryValue) || this.count(entryValue) < entryCount) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAllOccurrencesSetHelper(@Nonnull Iterator<? extends T> i)
    {
        while (i.hasNext()) {
            final T value = i.next();
            if (!this.contains(value)) {
                return false;
            }
        }
        return true;
    }

    private <T1 extends T> JImmutableMultiset<T> deleteAllOccurrencesHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 value = e.getValue().getKey();
            final int entryCount = e.getValue().getValue();
            final int mapCount = this.count(value);
            newMap = (mapCount > entryCount) ? newMap.assign(value, mapCount - entryCount) : newMap.delete(value);
            newOccurrences = (mapCount > entryCount) ? newOccurrences - entryCount : newOccurrences - mapCount;
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    private <T1 extends T> JImmutableMultiset<T> insertAllHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 value = e.getValue().getKey();
            final int entryCount = e.getValue().getValue();
            newMap = newMap.assign(value, entryCount + count(value));
            newOccurrences += entryCount;
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Nonnull
    private <T1 extends T> JImmutableMultiset<T> unionMultisetHelper(@Nonnull JImmutableMultiset<T1> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 value = e.getValue().getKey();
            final int entryCount = e.getValue().getValue();
            final int mapCount = getCount(newMap, value);
            if (entryCount > mapCount) {
                newOccurrences = newOccurrences - mapCount + entryCount;
                newMap = newMap.assign(value, entryCount);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    //Precondition: Iterator i must come from a Set or JImmutableSet, so that there will only be one occurrence
    //of each value in it.
    @Nonnull
    private JImmutableMultiset<T> unionSetHelper(@Nonnull Iterator<? extends T> i)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        while (i.hasNext()) {
            final T value = i.next();
            if ((value != null) && (this.count(value) == 0)) {
                newMap = newMap.assign(value, 1);
                ++newOccurrences;
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Nonnull
    protected <T1 extends T> JImmutableMultiset<T> multisetIntersectionWithFilledMap(@Nonnull JImmutableMultiset<T1> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        Cursor<JImmutableMap.Entry<T, Integer>> e = entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T value = e.getValue().getKey();
            boolean inOther = other.getSet().contains(value);
            final int mapCount = this.count(value);
            newOccurrences -= mapCount;
            if (inOther) {
                final int otherCount = other.count((T1)value);
                newOccurrences += (mapCount > otherCount) ? otherCount : mapCount;
                newMap = (mapCount > otherCount) ? newMap.assign(value, otherCount) : newMap.assign(value, mapCount);
            } else {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Nonnull
    protected <T1 extends T> JImmutableMultiset<T> multisetIntersectionWithEmptyMap(@Nonnull JImmutableMultiset<T1> other)
    {
        JImmutableMap<T, Integer> newMap = emptyMap();
        int newOccurrences = 0;
        Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 value = e.getValue().getKey();
            final int mapCount = this.count(value);
            if ((mapCount > 0)) {
                final int entryCount = e.getValue().getValue();
                newOccurrences += (mapCount > entryCount) ? entryCount : mapCount;
                newMap = (mapCount > entryCount) ? newMap.assign(value, entryCount) : newMap.assign(value, mapCount);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Nonnull
    protected JImmutableMultiset<T> setIntersectionWithFilledMap(@Nonnull Set<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        Cursor<JImmutableMap.Entry<T, Integer>> e = entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T value = e.getValue().getKey();
            boolean inOther = other.contains(value);
            final int mapCount = this.count(value);
            newOccurrences -= mapCount;
            if (inOther) {
                final int otherCount = 1;
                newOccurrences += (mapCount > otherCount) ? otherCount : mapCount;
                newMap = (mapCount > otherCount) ? newMap.assign(value, otherCount) : newMap.assign(value, mapCount);
            } else {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;

        /*
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        for (T value: this.cursor()) {
            int oldOccurrences = count(value);
            newOccurrences -= oldOccurrences;
            if (other.contains(value)) {
                ++newOccurrences;
                newMap = newMap.assign(value, 1);
            } else {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
        */
    }

    //Precondition: Iterator i must come from a Set or JImmutableSet, so that there will only be one occurrence
    //of each value in it.
    @Nonnull
    protected JImmutableMultiset<T> setIntersectionWithEmptyMap(@Nonnull Iterator<? extends T> i)
    {
        JImmutableMap<T, Integer> newMap = emptyMap();
        int newOccurrences = 0;
        while (i.hasNext()) {
            final T value = i.next();
            if (this.count(value) > 0) {
                newOccurrences += 1;
                newMap = newMap.assign(value, 1);
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }


}