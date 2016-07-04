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
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
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
        return new Editor().delta(value, 1).build();
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
            return new Editor().delta(value, count).build();
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
            if (!contains(other.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Cursorable<? extends T> other)
    {
        return containsAllOccurrences(other.cursor().iterator());
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
        final Counter counter = new Counter();
        while (other.hasNext()) {
            final T value = other.next();
            if ((value == null) || (counter.add(value, 1) > count(value))) {
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
    public boolean containsAny(@Nonnull Cursorable<? extends T> other)
    {
        return containsAny(other.cursor().iterator());
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
            if (contains(other.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> delete(@Nonnull T value)
    {
        return new Editor().remove(value).build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value)
    {
        return new Editor().delta(value, -1).build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value,
                                                  int subtractBy)
    {
        if (subtractBy < 0) {
            throw new IllegalArgumentException();
        } else {
            return new Editor().delta(value, -subtractBy).build();
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAll(other.cursor().iterator());
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
        Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                editor.remove(value);
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAllOccurrences(other.cursor().iterator());
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
        Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                editor.delta(value, -1);
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return deleteAllOccurrencesMultisetHelper(other);
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(values.cursor().iterator());
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
    public JImmutableMultiset<T> insertAll(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                editor.delta(value, 1);
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return insertAllMultisetHelper(values);
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Cursorable<? extends T> other)
    {
        return union(other.cursor().iterator());
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
        final Counter counter = new Counter();
        final Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                final int otherCount = counter.add(value, 1);
                editor.set(value, Math.max(otherCount, count(value)));
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return unionMultisetHelper(other);
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Cursorable<? extends T> other)
    {
        return intersection(other.cursor().iterator());
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
        if (isEmpty()) {
            return this;
        } else {
            final Counter counter = new Counter();
            final Editor editor = new Editor();
            while (other.hasNext()) {
                final T value = other.next();
                if (value != null) {
                    final int otherCount = counter.add(value, 1);
                    editor.set(value, Math.min(otherCount, count(value)));
                }
            }
            return editor.removeValuesNotInCounter(counter).build();
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableMultiset<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            return intersectionMultisetHelper(other);
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableSet<? extends T> other)
    {
        return intersection(other.getSet());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            Editor editor = new Editor();
            for (JImmutableMap.Entry<T, Integer> entry : map) {
                final T value = entry.getKey();
                final int oldCount = entry.getValue();
                if (other.contains(value)) {
                    editor.adjust(value, oldCount, 1);
                } else {
                    editor.adjust(value, oldCount, 0);
                }
            }
            return editor.build();
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
            return new Editor().set(value, count).build();
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

    /**
     * Implemented by derived classes to create a new empty mutable Map
     * that operates in the same way as this multiset's underlying immutable Map.
     *
     * @return new empty Counter
     */
    protected abstract Map<T, Integer> emptyMutableMap();

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
            final JImmutableMultiset that = (JImmutableMultiset)o;
            //noinspection unchecked
            return (occurrenceCount() == that.occurrenceCount()) && containsAllOccurrences(that);
        } else if (o instanceof JImmutableSet) {
            final JImmutableSet that = (JImmutableSet)o;
            return (size() == occurrences) && getSet().equals(that.getSet());
        } else {
            return (o instanceof Set) && (size() == occurrences) && getSet().equals(o);
        }
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(occurrenceCursor());
    }

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

    private <T1 extends T> boolean containsAllOccurrencesMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        final Counter counter = new Counter();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            if (count(value) < counter.add(value, otherCount)) {
                return false;
            }
        }
        return true;
    }

    private <T1 extends T> JImmutableMultiset<T> deleteAllOccurrencesMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        final Editor editor = new Editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            editor.delta(value, -otherCount);
        }
        return editor.build();
    }

    private <T1 extends T> JImmutableMultiset<T> insertAllMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        final Editor editor = new Editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            editor.delta(value, otherCount);
        }
        return editor.build();
    }

    @Nonnull
    private <T1 extends T> JImmutableMultiset<T> unionMultisetHelper(@Nonnull JImmutableMultiset<T1> other)
    {
        final Counter counter = new Counter();
        final Editor editor = new Editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = counter.add(value, entry.getValue());
            editor.set(value, Math.max(otherCount, count(value)));
        }
        return editor.build();
    }

    @Nonnull
    protected <T1 extends T> JImmutableMultiset<T> intersectionMultisetHelper(@Nonnull JImmutableMultiset<T1> other)
    {
        final Counter counter = new Counter();
        final Editor editor = new Editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = counter.add(value, entry.getValue());
            editor.set(value, Math.min(otherCount, count(value)));
        }
        return editor.removeValuesNotInCounter(counter).build();
    }

    private class Editor
    {
        private JImmutableMap<T, Integer> newMap;
        private int newOccurrences;

        private Editor()
        {
            this.newMap = map;
            this.newOccurrences = occurrences;
        }

        private Editor remove(T value)
        {
            final Integer oldCount = newMap.get(value);
            if (oldCount != null) {
                newMap = newMap.delete(value);
                newOccurrences -= oldCount;
            }
            return this;
        }

        private Editor delta(T value,
                             int delta)
        {
            if (delta != 0) {
                final int oldCount = newMap.find(value).getValueOr(0);
                adjust(value, oldCount, oldCount + delta);
            }
            return this;
        }

        private Editor set(T value,
                           int newCount)
        {
            final int oldCount = newMap.find(value).getValueOr(0);
            if (newCount != oldCount) {
                adjust(value, oldCount, newCount);
            }
            return this;
        }

        private void adjust(T value,
                            int oldCount,
                            int newCount)
        {
            if (newCount <= 0) {
                newMap = newMap.delete(value);
                newOccurrences -= oldCount;
            } else {
                newMap = newMap.assign(value, newCount);
                newOccurrences = newOccurrences - oldCount + newCount;
            }
        }

        private Editor removeValuesNotInCounter(Counter counter)
        {
            for (JImmutableMap.Entry<T, Integer> entry : newMap) {
                if (counter.get(entry.getKey()) == 0) {
                    newMap = newMap.delete(entry.getKey());
                    newOccurrences -= entry.getValue();
                }
            }
            return this;
        }

        private JImmutableMultiset<T> build()
        {
            return (map == newMap) ? AbstractJImmutableMultiset.this : create(newMap, newOccurrences);
        }
    }

    private class Counter
    {
        private final Map<T, Integer> counts;
        private int totalCount;

        private Counter()
        {
            counts = emptyMutableMap();
            assert counts.isEmpty();
        }

        private int add(T value,
                        int number)
        {
            assert value != null;
            assert number > 0;
            final int currentCount = get(value);
            final int newCount = currentCount + number;
            counts.put(value, newCount);
            totalCount = totalCount + newCount - currentCount;
            return newCount;
        }

        private int get(T value)
        {
            assert value != null;
            final Integer count = counts.get(value);
            assert (count == null) || (count > 0);
            return (count == null) ? 0 : count;
        }
    }
}