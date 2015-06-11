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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractJImmutableMultiset<T>
        implements JImmutableMultiset<T>
{
    private final JImmutableMap<T, Integer> map;
    private final int occurrences;

    protected AbstractJImmutableMultiset(JImmutableMap<T, Integer> map,
                                         int occurences)
    {
        this.map = map;
        this.occurrences = occurences;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insert(@Nonnull T value)
    {
        JImmutableMap<T, Integer> newMap = increaseCount(value, 1);
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
            JImmutableMap<T, Integer> newMap = increaseCount(value, count);
            return (newMap != map) ? create(newMap, count + occurrences) : this;
        }
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && (count(value) > 0);
    }

    @Override
    public boolean contains(@Nullable T value,
                            int count)
    {
        if (count <= 0) {
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
    public boolean containsAllOccurences(@Nonnull Cursorable<? extends T> other)
    {
        return containsAllOccurences(other.cursor());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Collection<? extends T> other)
    {
        return containsAllOccurences(other.iterator());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Cursor<? extends T> other)
    {
        return containsAllOccurences(other.iterator());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> checkMap = map;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (getCount(checkMap, value) > 0)) {
                //value cannot be null
                //value exists in checkMap with a count of 1 or greater
                checkMap = checkMap.assign(value, checkMap.get(value) - 1);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurences(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return containsAllOccurencesMultisetHelper(values);
    }

    private <T1 extends T> boolean containsAllOccurencesMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
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

    @Override
    public boolean containsAllOccurences(@Nonnull JImmutableSet<? extends T> values)
    {
        return containsAllOccurencesSetHelper(values.cursor().iterator());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Set<? extends T> values)
    {
        return containsAllOccurencesSetHelper(values.iterator());
    }

    private boolean containsAllOccurencesSetHelper(@Nonnull Iterator<? extends T> i)
    {
        while (i.hasNext()) {
            final T value = i.next();
            if (!this.contains(value)) {
                return false;
            }
        }
        return true;
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
        int newOccurences = occurrences - this.count(value);
        return (newMap != map) ? create(newMap, newOccurences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurence(@Nonnull T value)
    {
        if (this.count(value) == 0) {
            return this;
        } else {
            JImmutableMap<T, Integer> newMap = decreaseCount(value, 1);
            return (newMap != map) ? create(newMap, occurrences - 1) : this;
            //logic: if value is in map, then newMap will not equal map. Therefore, the total number of occurrences
            //should decrease by one. If newMap does equal map, then value was not in map. The number of occurrences
            //did not change, and this should be returned with no changes.
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value,
                                                  int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            JImmutableMap<T, Integer> newMap;
            int newOccurrences;
            if (count >= this.count(value)) {
                newMap = map.delete(value);
                newOccurrences = occurrences - this.count(value);
            } else {
                newMap = decreaseCount(value, count);
                newOccurrences = occurrences - count;
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
        int newOccurences = occurrences;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (getCount(newMap, value) > 0)) {
                newMap = newMap.delete(value);
                newOccurences = newOccurences - this.count(value);
            }
        }
        return (newMap != map) ? create(newMap, newOccurences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAllOccurences(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Collection<? extends T> other)
    {
        return deleteAllOccurences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Cursor<? extends T> other)
    {
        return deleteAllOccurences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurences = occurrences;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (getCount(newMap, value) > 0)) {
                newMap = decrementCount(newMap, value);
                newOccurences = newOccurences - 1;
            }
        }
        return (newMap != map) ? create(newMap, newOccurences) : this;
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
        int newOccurences = occurrences;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = incrementCount(newMap, value);
                newOccurences = newOccurences + 1;
            }
        }
        return (newMap != map) ? create(newMap, newOccurences) : this;
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
        JImmutableMap<T, Integer> otherMap = map.deleteAll();
        int newOccurrences = occurrences;
        while (other.hasNext()) {
            final T value = other.next();
            otherMap = incrementCount(otherMap, value);
            if (getCount(otherMap, value) > getCount(newMap, value)) {
                newOccurrences = newOccurrences - getCount(newMap, value) + getCount(otherMap, value);
                newMap = newMap.assign(value, getCount(otherMap, value));
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

    @Nonnull
    private JImmutableMultiset<T> unionSetHelper(@Nonnull Iterator<? extends T> i)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurrences = occurrences;
        while (i.hasNext()) {
            final T value = i.next();
            if ((value != null) && (this.count(value) == 0)) {
                newMap = newMap.assign(value, 1);
                newOccurrences = newOccurrences + 1;
            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
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
        JImmutableMap<T, Integer> newMap = map.deleteAll();
        int newOccurrences = 0;
        JImmutableMap<T, Integer> otherMap = map.deleteAll();
        while (other.hasNext()) {
            final T value = other.next();
            otherMap = incrementCount(otherMap, value);
            int currentCount = this.count(value);
            if ((currentCount > 0) && (currentCount != getCount(newMap, value))) {
                int otherCount = getCount(otherMap, value);
                newOccurrences -= getCount(newMap, value);
                newOccurrences += (currentCount > otherCount) ? otherCount : currentCount;
                newMap = (currentCount > otherCount) ? newMap.assign(value, otherCount) : newMap.assign(value, currentCount);

            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;

    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return intersectionMultisetHelper(other);
    }

    @Nonnull
    private <T1 extends T> JImmutableMultiset<T> intersectionMultisetHelper(@Nonnull JImmutableMultiset<T1> other)
    {
        JImmutableMap<T, Integer> newMap = map.deleteAll();
        int newOccurrences = 0;
        Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 value = e.getValue().getKey();
            final int mapCount = this.count(value);
            if ((mapCount > 0) && mapCount != getCount(newMap, value)) {
                final int entryCount = e.getValue().getValue();
                newOccurrences -= getCount(newMap, value);
                newOccurrences += (mapCount > entryCount) ? entryCount : mapCount;
                newMap = (mapCount > entryCount) ? newMap.assign(value, entryCount) : newMap.assign(value, mapCount);

            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableSet<T> other)
    {
        return intersectionSetHelper(other.cursor().iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Set<? extends T> other)
    {
        return intersectionSetHelper(other.iterator());
    }

    @Nonnull
    private JImmutableMultiset<T> intersectionSetHelper(@Nonnull Iterator<? extends T> i)
    {
        JImmutableMap<T, Integer> newMap = map.deleteAll();
        JImmutableMap<T, Integer> otherMap = map.deleteAll();
        int newOccurrences = 0;
        while (i.hasNext()) {
            final T value = i.next();
            otherMap = incrementCount(otherMap, value);
            int currentCount = this.count(value);
            if (currentCount > 0) {
                int otherCount = getCount(otherMap, value);
                newOccurrences += 1;
                newMap = newMap.assign(value, 1);

            }
        }
        return (newMap != map) ? create(newMap, newOccurrences) : this;
    }

    //would it work better to just do getValueOr?
    @Override
    public int count(T value)
    {
        Conditions.stopNull(value);
        Holder<Integer> current = map.find(value);
        return current.isFilled() ? current.getValue() : 0;
    }

    @Nonnull
    @Override
    public JImmutableMultiset<T> setCount(@Nonnull T value,
                                          int count)
    {
        Conditions.stopNull(value, count);
        if (count < 0) {
            throw new IllegalArgumentException();
        } else if (count == this.count(value)) {
            return this;
        } else {
            int newOccurrences = occurrences - this.count(value) + count;
            return (count > 0) ? create(map.assign(value, count), newOccurrences) : delete(value);
        }
    }


    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map
     * @return
     */
    protected abstract JImmutableMultiset<T> create(JImmutableMap<T, Integer> map,
                                                    int occurences);


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
    public int valueCount()
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
                return StandardCursor.of(new OccurrenceCursorSource(entry));
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

//    @Override
//    public boolean equals(Object o)
//    {
//        if (o == this) {
//            return true;
//        } else if (o == null) {
//            return false;
//        } else if (o instanceof JImmutableSet) {
//            return getSet().equals(((JImmutableSet)o).getSet());
//        } else {
//            return (o instanceof Set) && getSet().equals(o);
//        }
//    }
//
//    @Override
//    public String toString()
//    {
//        return Cursors.makeString(entryCursor());
//    }


    private JImmutableMap<T, Integer> increaseCount(T value,
                                                    int addBy)
    {
        return map.assign(value, addBy + this.count(value));
    }

    private JImmutableMap<T, Integer> incrementCount(JImmutableMap<T, Integer> newMap,
                                                     T value)
    {

        int newCount = getCount(newMap, value) + 1;
        return newMap.assign(value, newCount);

    }

    //removes subtractBy from map. Checks whether it needs to decrement or delete value.
    //preconditions: none. If value isn't in map, then this will be returned (see map.delete())
    //caution: calling method must know whether value is contained in this or not, so that it
    //can appropriately decrement occurrences.
    //logic: subtractBy = 0 or value not in this -- returns this.
    private JImmutableMap<T, Integer> decreaseCount(T value,
                                                    int subtractBy)
    {
        if (subtractBy == 0) {
            return map;
        } else {
            int newCount = this.count(value) - subtractBy;
            return (newCount > 0) ? map.assign(value, newCount) : map.delete(value);
        }
    }

    //decrements the count of value by 1 in newMap. For use in deleteAll type methods.
    //precondition: value exists at least once in newMap
    //caution: calling method must appropriately decrement occurrences.
    private JImmutableMap<T, Integer> decrementCount(JImmutableMap<T, Integer> newMap,
                                                     T value)
    {
        int newCount = newMap.get(value) - 1;
        return (newCount > 0) ? newMap.assign(value, newCount) : newMap.delete(value);
    }


    private Integer getCount(JImmutableMap<T, Integer> checkMap,
                             T value)
    {
        return checkMap.getValueOr(value, 0);
    }

    private class OccurrenceCursorSource
            implements StandardCursor.Source<T>
    {
        private int count;
        private final T value;

        private OccurrenceCursorSource(JImmutableMap.Entry<T, Integer> entry)
        {
            this.count = entry.getValue();
            this.value = entry.getKey();
        }

        private OccurrenceCursorSource(int count, T value)
        {
            this.count = count;
            this.value = value;
        }

        @Override
        public boolean atEnd()
        {
            return count <= 0;
        }

        @Override
        public T currentValue()
        {
            return value;
        }

        @Override
        public StandardCursor.Source<T> advance()
        {
            return new OccurrenceCursorSource(count - 1, value);
        }

    }
}